/**
 * Benchmark Runner
 *
 * High-level functions for running benchmarks with logging and result tracking.
 */

import * as fs from "fs"
import * as path from "path"
import { execSync } from "child_process"
import { runBenchmark, streamBenchmark, type BenchmarkResult } from "./index"
import { config } from "./config"
import { resolveWorkspaceDir } from "./workspacePaths"

/**
 * Options for the benchmark runner
 */
export interface RunnerOptions {
  /** Workspace ID (folder name under workspaces/) */
  workspaceId: string

  /** OpenRouter model ID */
  model?: string

  /** Maximum steps */
  maxSteps?: number

  /** Enable verbose logging */
  verbose?: boolean

  /** If true, re-prompt the agent when it stops without passing all tests */
  continueOnFail?: boolean

  /** Max number of continuation prompts (default: 3) */
  maxContinuations?: number

  /** Directory to write agent_output.json, agent_trace.jsonl, solution.diff, solution/ */
  resultsDir?: string
}

/**
 * Extended result with metadata
 */
export interface RunnerResult extends BenchmarkResult {
  workspaceId: string
  model: string
  startTime: Date
  endTime: Date
  durationMs: number
}

/**
 * Runs a benchmark with progress logging
 */
export async function run(options: RunnerOptions): Promise<RunnerResult> {
  const {
    workspaceId,
    model = config.defaultModel,
    maxSteps = config.defaultMaxSteps,
    verbose = false,
    continueOnFail = false,
    maxContinuations = 3,
    resultsDir,
  } = options

  const workspacesDir = path.resolve("./workspaces")
  let workspaceDir: string
  try {
    workspaceDir = resolveWorkspaceDir(workspacesDir, workspaceId)
  } catch {
    throw new Error(`Workspace not found: ${workspaceId}`)
  }

  const startTime = new Date()

  if (verbose) {
    console.log(`\n========================================`)
    console.log(`Benchmark: ${workspaceId}`)
    console.log(`Model: ${model}`)
    console.log(`Max Steps: ${maxSteps}`)
    console.log(`Continue on Fail: ${continueOnFail}`)
    console.log(`========================================\n`)
  }

  const result = await runBenchmark({
    workspaceDir,
    model,
    maxSteps,
    continueOnFail,
    maxContinuations,
    onStep: (stepNumber, toolCalls) => {
      if (verbose) {
        console.log(`Step ${stepNumber}: ${toolCalls.join(", ")}`)
      }
    },
  })

  const endTime = new Date()
  const durationMs = endTime.getTime() - startTime.getTime()

  if (verbose) {
    console.log(`\n========================================`)
    console.log(`Completed in ${result.steps} steps (${(durationMs / 1000).toFixed(1)}s)`)
    if (result.success) {
      console.log(`Status: Success (${result.gradeResult?.passed}/${result.gradeResult?.total} tests)`)
    } else {
      console.log(`Status: Failed - ${result.error}`)
    }
    console.log(`========================================\n`)
  }

  const runnerResult: RunnerResult = {
    ...result,
    workspaceId,
    model,
    startTime,
    endTime,
    durationMs,
  }

  // Write bench-cli compatible output files if resultsDir specified
  if (resultsDir) {
    writeResultFiles(resultsDir, runnerResult, workspaceDir)
    if (verbose) {
      console.log(`Results written to: ${resultsDir}`)
    }
  }

  return runnerResult
}

/**
 * Runs a benchmark with streaming output
 */
export async function runStreaming(options: RunnerOptions): Promise<RunnerResult> {
  const {
    workspaceId,
    model = config.defaultModel,
    maxSteps = config.defaultMaxSteps,
    verbose = false,
    continueOnFail = false,
    maxContinuations = 3,
  } = options

  const workspacesDir = path.resolve("./workspaces")
  let workspaceDir: string
  try {
    workspaceDir = resolveWorkspaceDir(workspacesDir, workspaceId)
  } catch {
    throw new Error(`Workspace not found: ${workspaceId}`)
  }

  const startTime = new Date()

  if (verbose) {
    console.log(`\n========================================`)
    console.log(`Benchmark: ${workspaceId} (streaming)`)
    console.log(`Model: ${model}`)
    console.log(`Continue on Fail: ${continueOnFail}`)
    console.log(`========================================\n`)
  }

  let totalSteps = 0
  let finalText = ""
  let error: string | undefined
  let success = false
  let gradeResult: { passed: number; total: number; percentage: number; performanceIndex?: number } | undefined
  let tokens: BenchmarkResult["tokens"]

  for await (const event of streamBenchmark({
    workspaceDir,
    model,
    maxSteps,
    continueOnFail,
    maxContinuations,
  })) {
    switch (event.type) {
      case "text":
        finalText += event.text
        if (verbose) {
          process.stdout.write(event.text)
        }
        break
      case "test":
        gradeResult = event.result
        if (verbose) {
          console.log(`\n[Test: ${event.result.passed}/${event.result.total} (${event.result.percentage}%)]`)
        }
        break
      case "tokens":
        tokens = {
          inputTokens: event.inputTokens,
          outputTokens: event.outputTokens,
          totalTokens: event.totalTokens,
          cost: event.cost,
          costIsComplete: event.costIsComplete,
        }
        break
      case "continue":
        if (verbose) {
          console.log(`\n[Continuing... attempt ${event.attempt}]`)
        }
        break
      case "done":
        totalSteps = event.totalSteps
        success = event.success
        gradeResult = event.gradeResult
        break
      case "error":
        error = event.error
        break
    }
  }

  const endTime = new Date()
  const durationMs = endTime.getTime() - startTime.getTime()

  if (verbose) {
    console.log(`\n\n========================================`)
    console.log(`Completed in ${totalSteps} steps (${(durationMs / 1000).toFixed(1)}s)`)
    if (success) {
      console.log(`Status: Success (${gradeResult?.passed}/${gradeResult?.total} tests)`)
    } else {
      console.log(`Status: Failed`)
    }
    console.log(`========================================\n`)
  }

  return {
    workspaceId,
    model,
    startTime,
    endTime,
    durationMs,
    steps: totalSteps,
    text: finalText,
    success,
    error,
    gradeResult: gradeResult as BenchmarkResult["gradeResult"],
    tokens,
  }
}

/**
 * Write bench-cli compatible output files to a results directory.
 *
 * Produces:
 *   agent_output.json   - Metrics summary (tokens, cost, duration, model)
 *   agent_trace.jsonl    - NDJSON trace of system/assistant/tool_call/tool_result events
 *   grade_result.json    - Structured grade result (if grading ran)
 *   solution.diff        - Unified diff of agent changes vs initial commit
 *   solution/            - Copies of files the agent changed
 */
function writeResultFiles(
  resultsDir: string,
  result: RunnerResult,
  workspaceDir: string,
): void {
  if (!fs.existsSync(resultsDir)) {
    fs.mkdirSync(resultsDir, { recursive: true })
  }

  // 1. agent_output.json
  const agentOutput: Record<string, unknown> = {
    is_error: !result.success && !!result.error,
    error: result.error || null,
    session_id: null,
    model_id: result.modelId || result.model,
    usage: {
      input_tokens: result.tokens?.inputTokens ?? 0,
      output_tokens: result.tokens?.outputTokens ?? 0,
      cache_creation_input_tokens: 0,
      cache_read_input_tokens: 0,
    },
    duration_ms: result.durationMs,
    num_turns: result.steps,
    cost_usd: result.tokens?.cost ?? 0,
  }
  fs.writeFileSync(
    path.join(resultsDir, "agent_output.json"),
    JSON.stringify(agentOutput, null, 2),
  )

  // 2. agent_trace.jsonl
  const traceEvents = result.traceEvents || []
  const traceLines = traceEvents.map((ev) => JSON.stringify(ev))
  fs.writeFileSync(
    path.join(resultsDir, "agent_trace.jsonl"),
    traceLines.join("\n") + (traceLines.length > 0 ? "\n" : ""),
  )

  // 3. grade_result.json
  if (result.gradeResult) {
    fs.writeFileSync(
      path.join(resultsDir, "grade_result.json"),
      JSON.stringify(result.gradeResult, null, 2),
    )
  }

  // 4. solution.diff - diff against initial commit
  try {
    const initialCommit = execSync("git rev-list --max-parents=0 HEAD", {
      cwd: workspaceDir,
      encoding: "utf-8",
      timeout: 10000,
    }).trim()

    if (/^[0-9a-f]{40}$/i.test(initialCommit)) {
      // Stage uncommitted changes so diff captures everything
      execSync("git add -A", { cwd: workspaceDir, timeout: 10000 })
      execSync('git commit -m "Agent solution" --allow-empty', {
        cwd: workspaceDir,
        timeout: 10000,
        stdio: "ignore",
      })

      const diff = execSync(`git diff ${initialCommit} HEAD`, {
        cwd: workspaceDir,
        encoding: "utf-8",
        timeout: 10000,
        maxBuffer: 10 * 1024 * 1024,
      })
      if (diff.trim()) {
        fs.writeFileSync(path.join(resultsDir, "solution.diff"), diff)
      }

      // 5. solution/ - copy changed files
      const changed = execSync(`git diff --name-only ${initialCommit} HEAD`, {
        cwd: workspaceDir,
        encoding: "utf-8",
        timeout: 10000,
      })
      if (changed.trim()) {
        const solutionDir = path.join(resultsDir, "solution")
        if (!fs.existsSync(solutionDir)) {
          fs.mkdirSync(solutionDir, { recursive: true })
        }
        for (const fname of changed.trim().split("\n")) {
          const src = path.join(workspaceDir, fname)
          if (fs.existsSync(src) && fs.statSync(src).isFile()) {
            const dest = path.join(solutionDir, fname)
            const destDir = path.dirname(dest)
            if (!fs.existsSync(destDir)) {
              fs.mkdirSync(destDir, { recursive: true })
            }
            fs.copyFileSync(src, dest)
          }
        }
      }
    }
  } catch {
    // Non-critical: workspace may not be a git repo or may have no commits
  }
}

/**
 * Saves a benchmark result to the workspace
 */
export function saveResult(result: RunnerResult): string {
  const workspacesDir = path.resolve("./workspaces")
  let workspaceDir: string
  try {
    workspaceDir = resolveWorkspaceDir(workspacesDir, result.workspaceId)
  } catch {
    // Fallback to flat path if resolution fails
    workspaceDir = path.resolve("./workspaces", result.workspaceId)
  }
  const resultsDir = path.join(workspaceDir, ".benchmark")
  if (!fs.existsSync(resultsDir)) {
    fs.mkdirSync(resultsDir, { recursive: true })
  }

  const filename = `benchmark_${result.startTime.toISOString().replace(/[:.]/g, "-")}.json`
  const filepath = path.join(resultsDir, filename)

  fs.writeFileSync(filepath, JSON.stringify(result, null, 2))
  return filepath
}
