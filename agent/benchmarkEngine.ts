import { ToolLoopAgent, stepCountIs } from "ai"
import type { LanguageModel, ModelMessage } from "ai"
import { createTools, createToolsWithCallbacks, type ToolCallbacks } from "./tools"
import { loadAllowedEditPaths } from "./allowedPaths"
import {
  createUnifiedTaskPrompt,
  getSystemPrompt,
  CONTINUE_PROMPT_CORRECTNESS,
  createContinuePromptPerformance,
} from "./prompts"
import { config, getOpenRouter } from "./config"
import { parseGradeOutput, type GradeResult } from "./gradeParser"
import { resolveWorkspaceContext } from "./workspaceContext"

export interface BenchmarkTokenUsage {
  inputTokens: number
  outputTokens: number
  totalTokens: number
  cost: number
  costIsComplete?: boolean
}

export interface BenchmarkPerfResult {
  performanceIndex: number
  utilizationScore: number
  throughputScore: number
}

export interface BenchmarkCallbacks {
  onText?: (text: string) => void
  onStep?: (step: number, tools: string[]) => void
  onTokens?: (usage: BenchmarkTokenUsage) => void
  onGrade?: (gradeResult: GradeResult) => void
  onPerf?: (perfResult: BenchmarkPerfResult) => void
  onContinue?: (attempt: number) => void
  onMessages?: (messages: ModelMessage[]) => void
  onError?: (error: string) => void
}

export interface BenchmarkEngineOptions {
  workspaceDir: string
  model?: string
  maxSteps?: number
  continueOnFail?: boolean
  maxContinuations?: number
  performanceThreshold?: number
  prompt?: string
  previousMessages?: ModelMessage[]
  toolCallbacks?: ToolCallbacks
  callbacks?: BenchmarkCallbacks
  abortSignal?: AbortSignal
  throwOnError?: boolean
}

export interface TraceEvent {
  type: "system" | "assistant" | "tool_call" | "tool_result"
  timestamp: number
  name?: string
  content?: string
  args?: unknown
  result?: string
}

export interface BenchmarkResult {
  /** Number of steps taken */
  steps: number

  /** Final text response from agent */
  text: string

  /** Whether the agent completed successfully (all tests passed or performance target met) */
  success: boolean

  /** Error message if failed */
  error?: string

  /** Last grade result if available */
  gradeResult?: GradeResult

  /** For performance benchmarks, the target that was set */
  performanceThreshold?: number

  /** Token usage totals (if available) */
  tokens?: BenchmarkTokenUsage

  /** Trace events captured during the run (for agent_trace.jsonl) */
  traceEvents?: TraceEvent[]

  /** Model ID used for the run */
  modelId?: string

  /** Total duration in milliseconds */
  durationMs?: number
}

interface BenchmarkRefs {
  gradeRef: { current: GradeResult | null }
  perfRef: { current: BenchmarkPerfResult | null }
}

function parsePerfOutput(output: string): BenchmarkPerfResult | null {
  const perfMatch = output.match(/Perf index = (\d+)\/\d+ \(util\) \+ (\d+)\/\d+ \(thru\) = (\d+)\/100/)
  if (!perfMatch) {
    return null
  }

  return {
    utilizationScore: parseInt(perfMatch[1], 10),
    throughputScore: parseInt(perfMatch[2], 10),
    performanceIndex: parseInt(perfMatch[3], 10),
  }
}

function wrapToolsForBenchmark(
  tools: ReturnType<typeof createTools>,
  refs: BenchmarkRefs,
  callbacks?: BenchmarkCallbacks
): ReturnType<typeof createTools> {
  const { gradeRef, perfRef } = refs

  const originalTest = tools.test
  const originalTestExecute = originalTest.execute
  const wrappedTest = {
    ...originalTest,
    execute: async (params: Record<string, unknown>, options: unknown) => {
      const result = await originalTestExecute!.call(originalTest, params, options as any)
      const output = typeof result === "string" ? result : String(result)
      const parsed = parseGradeOutput(output)
      if (parsed) {
        gradeRef.current = parsed
        callbacks?.onGrade?.(parsed)
      }
      return result
    },
  }

  const originalMallocPerf = tools.malloc_perf
  const originalMallocPerfExecute = originalMallocPerf.execute
  const wrappedMallocPerf = {
    ...originalMallocPerf,
    execute: async (params: Record<string, unknown>, options: unknown) => {
      const result = await originalMallocPerfExecute!.call(originalMallocPerf, params, options as any)
      const output = typeof result === "string" ? result : String(result)
      const perfResult = parsePerfOutput(output)
      if (perfResult) {
        perfRef.current = perfResult
        if (gradeRef.current) {
          gradeRef.current.performanceIndex = perfResult.performanceIndex
          gradeRef.current.utilizationScore = perfResult.utilizationScore
          gradeRef.current.throughputScore = perfResult.throughputScore
        }
        callbacks?.onPerf?.(perfResult)
      }
      return result
    },
  }

  return {
    ...tools,
    test: wrappedTest as typeof tools.test,
    malloc_perf: wrappedMallocPerf as typeof tools.malloc_perf,
  }
}

export async function runBenchmarkEngine(options: BenchmarkEngineOptions): Promise<BenchmarkResult> {
  const {
    workspaceDir,
    model = config.defaultModel,
    maxSteps = config.defaultMaxSteps,
    continueOnFail = false,
    maxContinuations = 3,
    performanceThreshold,
    prompt,
    previousMessages,
    toolCallbacks,
    callbacks,
    abortSignal,
    throwOnError = false,
  } = options

  const context = resolveWorkspaceContext(workspaceDir)
  const { language, moduleNumber, moduleName, course, assignmentMode, isPerformanceBenchmark } = context
  const perfThreshold = performanceThreshold ?? context.performanceThreshold ?? 90

  const systemPrompt = getSystemPrompt({
    language,
    course,
    assignmentNumber: moduleNumber,
    assignmentName: moduleName,
    assignmentMode,
    workspaceDir,
  })

  const gradeRef = { current: null as GradeResult | null }
  const perfRef = { current: null as BenchmarkPerfResult | null }

  const openrouter = getOpenRouter()

  const allowedEditPaths = loadAllowedEditPaths({
    workspaceDir,
    course,
    assignmentNumber: moduleNumber,
    assignmentName: moduleName,
    language,
  })

  const baseTools = toolCallbacks
    ? createToolsWithCallbacks(workspaceDir, toolCallbacks, allowedEditPaths.length > 0 ? { allowedEditPaths } : {})
    : createTools(workspaceDir, allowedEditPaths.length > 0 ? { allowedEditPaths } : {})

  const tools = wrapToolsForBenchmark(baseTools, { gradeRef, perfRef }, callbacks)

  const initialPrompt = prompt ?? createUnifiedTaskPrompt(moduleNumber, language, {
    projectName: moduleName,
    performanceThreshold: perfThreshold,
    course,
    assignmentName: moduleName,
  })

  const continuePrompt = isPerformanceBenchmark
    ? createContinuePromptPerformance(perfThreshold)
    : CONTINUE_PROMPT_CORRECTNESS

  let totalSteps = 0
  let continuations = 0
  let allText = ""

  const traceEvents: TraceEvent[] = []
  const startTime = Date.now()

  // Record system prompt as first trace event
  traceEvents.push({
    type: "system",
    timestamp: Date.now(),
    content: systemPrompt,
  })

  const tokenTotals: BenchmarkTokenUsage = {
    inputTokens: 0,
    outputTokens: 0,
    totalTokens: 0,
    cost: 0,
  }
  let costIsComplete = true

  function buildResult(overrides: {
    success: boolean
    error?: string
    gradeResult?: GradeResult
  }): BenchmarkResult {
    return {
      steps: totalSteps,
      text: allText.trim(),
      success: overrides.success,
      error: overrides.error,
      gradeResult: overrides.gradeResult,
      performanceThreshold: isPerformanceBenchmark ? perfThreshold : undefined,
      tokens: tokenTotals.totalTokens > 0 ? { ...tokenTotals, costIsComplete } : undefined,
      traceEvents,
      modelId: model,
      durationMs: Date.now() - startTime,
    }
  }

  function isGoalMet(result: GradeResult): boolean {
    if (isPerformanceBenchmark) {
      const correctnessOk = result.percentage === 100
      const perfOk = (perfRef.current?.performanceIndex ?? 0) >= perfThreshold
      return correctnessOk && perfOk
    }
    return result.percentage === 100
  }

  let currentPrompt = initialPrompt
  let isFirstRun = true
  let submitCalled = false

  while (totalSteps < maxSteps) {
    if (abortSignal?.aborted) {
      return buildResult({
        success: false,
        error: "Aborted",
        gradeResult: gradeRef.current || undefined,
      })
    }
    const remainingSteps = maxSteps - totalSteps

    const modelClient = openrouter.chat(model, { usage: { include: true } }) as LanguageModel

    const agent = new ToolLoopAgent({
      model: modelClient,
      instructions: systemPrompt,
      tools,
      stopWhen: stepCountIs(remainingSteps),
      onStepFinish: (stepResult) => {
        // AI SDK 6 step results: toolCalls have {toolName, input}, toolResults have {toolName, output}
        const { toolCalls, toolResults, text: stepText, usage, providerMetadata } = stepResult

        totalSteps++
        const toolNames = (toolCalls as Array<{ toolName: string }>).map((tc) => tc.toolName)
        callbacks?.onStep?.(totalSteps, toolNames)

        // Capture assistant text if present
        if (stepText) {
          traceEvents.push({
            type: "assistant",
            timestamp: Date.now(),
            content: stepText,
          })
        }

        // Capture tool calls and results
        for (const tc of toolCalls as Array<{ toolName: string; input: unknown }>) {
          if (tc.toolName === "submit") {
            submitCalled = true
          }
          traceEvents.push({
            type: "tool_call",
            timestamp: Date.now(),
            name: tc.toolName,
            args: tc.input,
          })
        }
        for (const tr of toolResults as Array<{ toolName: string; output: unknown }>) {
          const resultStr = typeof tr.output === "string" ? tr.output : JSON.stringify(tr.output)
          traceEvents.push({
            type: "tool_result",
            timestamp: Date.now(),
            name: tr.toolName,
            result: resultStr.length > 5000 ? resultStr.slice(0, 5000) + "...[truncated]" : resultStr,
          })
        }

        if (usage) {
          const stepUsage = usage as { inputTokens?: number; outputTokens?: number }
          tokenTotals.inputTokens += stepUsage.inputTokens || 0
          tokenTotals.outputTokens += stepUsage.outputTokens || 0

          const openrouterMeta = providerMetadata?.openrouter as { usage?: { cost?: number } } | undefined
          const stepCost = openrouterMeta?.usage?.cost
          if (stepCost === undefined) {
            costIsComplete = false
          } else {
            tokenTotals.cost += stepCost
          }

          tokenTotals.totalTokens = tokenTotals.inputTokens + tokenTotals.outputTokens

          callbacks?.onTokens?.({ ...tokenTotals, costIsComplete })
        }
      },
    })

    try {
      const streamInput = isFirstRun && previousMessages
        ? { messages: [...previousMessages, { role: "user" as const, content: currentPrompt }] }
        : { prompt: currentPrompt }

      const result = await agent.stream(streamInput)

      for await (const chunk of result.textStream) {
        if (abortSignal?.aborted) {
          break
        }
        callbacks?.onText?.(chunk)
        allText += chunk
      }

      await result.steps

      if (abortSignal?.aborted) {
        return buildResult({
          success: false,
          error: "Aborted",
          gradeResult: gradeRef.current || undefined,
        })
      }

      if (isFirstRun) {
        isFirstRun = false
        try {
          const response = await result.response
          if (response?.messages) {
            callbacks?.onMessages?.(response.messages)
          }
        } catch {
          // Non-critical, continue without messages
        }
      }

      const lastResult = gradeRef.current
      if (lastResult && isGoalMet(lastResult)) {
        return buildResult({
          success: true,
          gradeResult: lastResult,
        })
      }

      // For theoretical/proof courses: submit = done (no tests to pass)
      if (submitCalled && !lastResult) {
        return buildResult({
          success: true,
        })
      }

      if (!continueOnFail || continuations >= maxContinuations) {
        break
      }

      continuations++
      callbacks?.onContinue?.(continuations)
      currentPrompt = continuePrompt
      isFirstRun = false
    } catch (error) {
      const lastResult = gradeRef.current
      const errorMessage = error instanceof Error ? error.message : String(error)
      callbacks?.onError?.(errorMessage)
      if (throwOnError) {
        throw new Error(errorMessage)
      }
      return buildResult({
        success: false,
        error: errorMessage,
        gradeResult: lastResult || undefined,
      })
    }
  }

  const lastResult = gradeRef.current
  let errorMsg: string
  if (!lastResult) {
    errorMsg = "Agent stopped without running test tool"
  } else if (isPerformanceBenchmark) {
    const perfIdx = perfRef.current?.performanceIndex ?? 0
    errorMsg = `Performance index ${perfIdx}/100 (target: ${perfThreshold})`
  } else {
    errorMsg = `Completed with ${lastResult.passed}/${lastResult.total} tests passing (${lastResult.percentage}%)`
  }

  return buildResult({
    success: false,
    error: errorMsg,
    gradeResult: lastResult || undefined,
  })
}
