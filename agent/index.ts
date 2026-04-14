/**
 * BSCS Bench Agent Harness
 *
 * A minimal agent harness for running benchmark evaluations.
 * Built on AI SDK 6's ToolLoopAgent with OpenRouter for model access.
 */

import { runBenchmarkEngine, type BenchmarkResult as EngineBenchmarkResult, type TraceEvent } from "./benchmarkEngine"
import {
  resolveWorkspaceContext,
  readWorkspaceInfo,
  readProjectConfig,
  detectCourseLanguage,
  type WorkspaceInfo,
  type ProjectConfig,
  type WorkspaceContext,
} from "./workspaceContext"

export { createTools, createToolsWithCallbacks, type ToolCallbacks } from "./tools"
export {
  createUnifiedTaskPrompt,
  getSystemPrompt,
  CONTINUE_PROMPT_CORRECTNESS,
  createContinuePromptPerformance,
} from "./prompts"
export { config } from "./config"
export { parseGradeOutput, type GradeResult, type TestResult } from "./gradeParser"
export {
  resolveWorkspaceContext,
  readWorkspaceInfo,
  readProjectConfig,
  detectCourseLanguage,
}
export type { WorkspaceInfo, ProjectConfig, WorkspaceContext, TraceEvent }

// LLM Grader exports
export { gradeSubmission } from "./llmGrader"
export type { LLMGradeConfig, LLMGradeResult, LLMGradeResponse } from "./llmGraderTypes"

// Workspace context helpers are now in ./workspaceContext

/**
 * Result from a benchmark run
 */
export type BenchmarkResult = EngineBenchmarkResult

/**
 * Runs a benchmark and returns the result
 */
export async function runBenchmark(options: {
  workspaceDir: string
  model?: string
  maxSteps?: number
  /** If true, re-prompt the agent when it stops without passing all tests */
  continueOnFail?: boolean
  /** Max number of continuation prompts (default: 3) */
  maxContinuations?: number
  /** Override performance threshold (for performance benchmarks like malloc) */
  performanceThreshold?: number
  onStep?: (stepNumber: number, toolCalls: string[]) => void
}): Promise<BenchmarkResult> {
  const { onStep, ...rest } = options
  return runBenchmarkEngine({
    ...rest,
    callbacks: onStep ? { onStep } : undefined,
  })
}

/**
 * Streams a benchmark run with real-time updates
 */
export async function* streamBenchmark(options: {
  workspaceDir: string
  model?: string
  maxSteps?: number
  continueOnFail?: boolean
  maxContinuations?: number
  /** Override performance threshold (for performance benchmarks like malloc) */
  performanceThreshold?: number
}): AsyncGenerator<
  | { type: "text"; text: string }
  | { type: "step"; stepNumber: number; toolCalls: string[] }
  | { type: "tokens"; inputTokens: number; outputTokens: number; totalTokens: number; cost: number; costIsComplete?: boolean }
  | { type: "test"; result: { passed: number; total: number; percentage: number; performanceIndex?: number } }
  | { type: "continue"; attempt: number }
  | { type: "done"; totalSteps: number; success: boolean; gradeResult?: { passed: number; total: number; percentage: number; performanceIndex?: number }; performanceThreshold?: number }
  | { type: "error"; error: string }
> {
  const queue: Array<
    | { type: "text"; text: string }
    | { type: "step"; stepNumber: number; toolCalls: string[] }
    | { type: "tokens"; inputTokens: number; outputTokens: number; totalTokens: number; cost: number; costIsComplete?: boolean }
    | { type: "test"; result: { passed: number; total: number; percentage: number; performanceIndex?: number } }
    | { type: "continue"; attempt: number }
    | { type: "done"; totalSteps: number; success: boolean; gradeResult?: { passed: number; total: number; percentage: number; performanceIndex?: number }; performanceThreshold?: number }
    | { type: "error"; error: string }
  > = []

  let done = false
  let notify: (() => void) | null = null

  const push = (event: (typeof queue)[number]) => {
    queue.push(event)
    if (notify) {
      notify()
      notify = null
    }
  }

  runBenchmarkEngine({
    ...options,
    throwOnError: true,
    callbacks: {
      onText: (text) => push({ type: "text", text }),
      onStep: (stepNumber, toolCalls) => push({ type: "step", stepNumber, toolCalls }),
      onTokens: (usage) => {
        push({
          type: "tokens",
          inputTokens: usage.inputTokens,
          outputTokens: usage.outputTokens,
          totalTokens: usage.totalTokens,
          cost: usage.cost,
          costIsComplete: usage.costIsComplete,
        })
      },
      onGrade: (gradeResult) => {
        push({
          type: "test",
          result: {
            passed: gradeResult.passed,
            total: gradeResult.total,
            percentage: gradeResult.percentage,
            performanceIndex: gradeResult.performanceIndex,
          },
        })
      },
      onContinue: (attempt) => push({ type: "continue", attempt }),
    },
  })
    .then((result) => {
      push({
        type: "done",
        totalSteps: result.steps,
        success: result.success,
        gradeResult: result.gradeResult
          ? {
              passed: result.gradeResult.passed,
              total: result.gradeResult.total,
              percentage: result.gradeResult.percentage,
              performanceIndex: result.gradeResult.performanceIndex,
            }
          : undefined,
        performanceThreshold: result.performanceThreshold,
      })
    })
    .catch((error) => {
      push({
        type: "error",
        error: error instanceof Error ? error.message : String(error),
      })
    })
    .finally(() => {
      done = true
      if (notify) {
        notify()
        notify = null
      }
    })

  while (true) {
    if (queue.length > 0) {
      yield queue.shift()!
      continue
    }
    if (done) {
      return
    }
    await new Promise<void>((resolve) => {
      notify = resolve
    })
  }
}
