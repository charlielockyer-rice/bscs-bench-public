/**
 * TypeScript API Server for BSCS Bench
 *
 * Exposes streamBenchmark via SSE with structured events.
 * Run with: bun run agent/server.ts
 */

import type { GradeResult } from "./gradeParser"
import type { ToolCallbacks } from "./tools"
import { getAssignmentNumber } from "./workspaceConfig"
import { runBenchmarkEngine, type BenchmarkCallbacks, type BenchmarkPerfResult, type BenchmarkTokenUsage } from "./benchmarkEngine"
import type { ModelMessage } from "ai"
import { handleWorkspaceRoutes } from "./serverWorkspaceRoutes"
import { handleCatalogRoutes } from "./serverCatalogRoutes"
import type {
  AttemptsData,
  EventCallback,
  EventEmitter,
  ModuleConfig,
  PersistedMessages,
  WorkspaceState,
  WorkspaceValidationResult,
} from "./serverTypes"
import { tryResolveWorkspaceDir } from "./workspacePaths"
import * as path from "path"
import * as fs from "fs"
import yaml from "js-yaml"
import { execSync } from "child_process"
import { MAX_TOOL_OUTPUT_LENGTH, type SSEEvent } from "../shared/benchContracts"

const PORT = process.env.BENCH_API_PORT ? parseInt(process.env.BENCH_API_PORT) : 3001

function createEventEmitter(state: WorkspaceState): EventEmitter {
  const listeners = new Set<EventCallback>()

  return {
    emit(event: SSEEvent) {
      state.events.push(event)
      // Limit buffer size with batched cleanup (avoids O(n) shift operations)
      if (state.events.length > 6000) {
        state.events = state.events.slice(-5000)
      }
      for (const listener of listeners) {
        try {
          listener(event)
        } catch {
          // Listener may have been removed
        }
      }
    },
    subscribe(callback: EventCallback) {
      listeners.add(callback)
      return () => listeners.delete(callback)
    },
  }
}

const workspaceStates = new Map<string, WorkspaceState>()

/**
 * Schedule cleanup of workspace state after completion.
 * Prevents memory leaks by removing completed/failed workspace states after 1 hour.
 */
function scheduleWorkspaceCleanup(workspaceId: string): void {
  setTimeout(() => {
    const state = workspaceStates.get(workspaceId)
    if (state && state.status !== "running") {
      workspaceStates.delete(workspaceId)
    }
  }, 60 * 60 * 1000) // 1 hour
}

/**
 * Get or create workspace state.
 * Returns existing state if available, otherwise creates a fresh idle state.
 */
function getOrCreateWorkspaceState(workspaceId: string): WorkspaceState {
  if (!workspaceStates.has(workspaceId)) {
    workspaceStates.set(workspaceId, {
      status: "idle",
      events: [],
      steps: 0,
      totalInputTokens: 0,
      totalOutputTokens: 0,
      totalCost: 0,
    })
  }
  return workspaceStates.get(workspaceId)!
}

/**
 * Reset workspace state for a fresh run.
 * Clears events and metrics but preserves the state object.
 */
function resetWorkspaceState(state: WorkspaceState): void {
  state.status = "idle"
  state.controller = undefined
  state.emitter = undefined
  state.events = []
  state.model = undefined
  state.startedAt = undefined
  state.steps = 0
  state.totalInputTokens = 0
  state.totalOutputTokens = 0
  state.totalCost = 0
  state.lastGradeResult = undefined
}

/**
 * Save stream events to workspace for persistence across navigation.
 * Called when benchmark completes or fails.
 */
function saveStreamToWorkspace(workspaceId: string, state: WorkspaceState): void {
  try {
    const validation = validateWorkspaceId(workspaceId)
    if (!validation.valid) return

    const workspaceDir = validation.workspaceDir
    if (!fs.existsSync(workspaceDir)) return

    const streamData = {
      workspaceId,
      model: state.model,
      status: state.status,
      startedAt: state.startedAt?.toISOString(),
      completedAt: new Date().toISOString(),
      steps: state.steps,
      tokens: {
        input: state.totalInputTokens,
        output: state.totalOutputTokens,
        total: state.totalInputTokens + state.totalOutputTokens,
        cost: state.totalCost,
      },
      events: state.events,
    }

    const streamPath = path.join(workspaceDir, "last_stream.json")
    fs.writeFileSync(streamPath, JSON.stringify(streamData, null, 2))
  } catch {
    // Silently fail - stream caching is non-critical
  }
}

/**
 * Save conversation messages to workspace for context persistence across continues.
 * Truncates messages if they exceed MAX_CHARS to avoid context overflow.
 */
function saveMessagesToWorkspace(
  workspaceId: string,
  state: WorkspaceState,
  messages: ModelMessage[]
): void {
  const validation = validateWorkspaceId(workspaceId)
  if (!validation.valid) return

  const MAX_CHARS = 200000 // ~50K tokens
  let truncated = false
  let finalMessages = messages

  const serialized = JSON.stringify(messages)
  if (serialized.length > MAX_CHARS) {
    truncated = true
    finalMessages = truncateMessages(messages, MAX_CHARS)
  }

  const data: PersistedMessages = {
    workspaceId,
    model: state.model,
    timestamp: new Date().toISOString(),
    messageCount: messages.length,
    truncated,
    messages: finalMessages,
  }

  const messagesPath = path.join(validation.workspaceDir, "last_messages.json")
  fs.writeFileSync(messagesPath, JSON.stringify(data, null, 2))
}

/**
 * Truncate messages to fit within maxChars, keeping first message and as many
 * recent messages as possible.
 */
function truncateMessages(messages: ModelMessage[], maxChars: number): ModelMessage[] {
  if (messages.length === 0) return []

  let currentSize = JSON.stringify(messages[0]).length
  const kept: ModelMessage[] = []

  // Add messages from the end (most recent) until we hit the limit
  for (let i = messages.length - 1; i > 0; i--) {
    const msgSize = JSON.stringify(messages[i]).length
    if (currentSize + msgSize < maxChars) {
      kept.push(messages[i])
      currentSize += msgSize
    } else {
      break
    }
  }

  kept.reverse()
  return [messages[0], ...kept]
}

/**
 * Load previously saved conversation messages from workspace.
 */
function loadMessagesFromWorkspace(workspaceDir: string): ModelMessage[] | null {
  const messagesPath = path.join(workspaceDir, "last_messages.json")
  if (!fs.existsSync(messagesPath)) return null

  try {
    const data = JSON.parse(fs.readFileSync(messagesPath, "utf-8")) as PersistedMessages
    return data.messages
  } catch {
    return null
  }
}

function corsHeaders() {
  return {
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "GET, POST, DELETE, OPTIONS",
    "Access-Control-Allow-Headers": "Content-Type",
  }
}

function jsonResponse(data: unknown, status = 200) {
  return new Response(JSON.stringify(data), {
    status,
    headers: {
      "Content-Type": "application/json",
      ...corsHeaders(),
    },
  })
}

// ============================================================================
// Directory paths
// ============================================================================

const WORKSPACES_DIR = path.resolve(process.cwd(), "workspaces")

/**
 * Validates a workspaceId to prevent path traversal attacks.
 *
 * Accepts:
 * - Flat IDs: "comp140_module1_circles"
 * - Nested IDs with one slash: "model-dir/comp140_module1_circles"
 *
 * Rejects IDs containing:
 * - ".." (parent directory traversal)
 * - "\" (backslash path separator)
 * - More than one "/" (too many levels of nesting)
 *
 * Also tries to resolve the workspace across both flat and model-directory
 * layouts, and verifies the resolved path is still under WORKSPACES_DIR.
 */
function validateWorkspaceId(workspaceId: string): WorkspaceValidationResult {
  // Reject IDs with path traversal characters
  if (workspaceId.includes("..") || workspaceId.includes("\\")) {
    return {
      valid: false,
      error: `Invalid workspace ID: contains forbidden characters (.. \\)`,
    }
  }

  // Allow at most one slash (for model-dir/workspace-name)
  const slashCount = (workspaceId.match(/\//g) || []).length
  if (slashCount > 1) {
    return {
      valid: false,
      error: `Invalid workspace ID: too many path separators`,
    }
  }

  // Try to resolve workspace using the shared helper
  const resolved = tryResolveWorkspaceDir(WORKSPACES_DIR, workspaceId)
  if (resolved) {
    // Verify the resolved path is still under WORKSPACES_DIR
    if (!resolved.startsWith(WORKSPACES_DIR + path.sep) && resolved !== WORKSPACES_DIR) {
      return {
        valid: false,
        error: `Invalid workspace ID: resolves outside workspaces directory`,
      }
    }
    return { valid: true, workspaceDir: resolved }
  }

  // Fallback: resolve the literal path (for workspace creation, etc.)
  const workspaceDir = path.resolve(WORKSPACES_DIR, workspaceId)
  if (!workspaceDir.startsWith(WORKSPACES_DIR + path.sep) && workspaceDir !== WORKSPACES_DIR) {
    return {
      valid: false,
      error: `Invalid workspace ID: resolves outside workspaces directory`,
    }
  }

  return { valid: true, workspaceDir }
}

// Shared config parsing helpers
function getStringArray(data: Record<string, unknown>, key: string): string[] {
  const val = data[key]
  return Array.isArray(val) ? val.filter((v): v is string => typeof v === "string") : []
}

function getNumber(data: Record<string, unknown>, key: string, fallback: number): number {
  const val = data[key]
  return typeof val === "number" ? val : fallback
}

function getString(data: Record<string, unknown>, key: string, fallback: string): string {
  const val = data[key]
  return typeof val === "string" ? val : fallback
}

function getStringFirstOf(data: Record<string, unknown>, keys: string[], fallback: string): string {
  for (const key of keys) {
    const val = data[key]
    if (typeof val === "string") return val
  }
  return fallback
}


function isValidWorkspace(wsDir: string): boolean {
  return (
    fs.existsSync(path.join(wsDir, "workspace.yaml")) ||
    fs.existsSync(path.join(wsDir, "workspace.json"))
  )
}

/**
 * Reset workspace to initial commit using git.
 * This unified function replaces language-specific reset functions.
 * IMPORTANT: Only works if workspace has its own .git directory.
 */
function resetWorkspace(workspaceDir: string): void {
  // CRITICAL: Verify workspace has its own .git directory
  // Without this check, git commands would operate on the parent repo!
  const gitDir = path.join(workspaceDir, ".git")
  if (!fs.existsSync(gitDir)) {
    throw new Error(`Workspace is not a git repository: ${workspaceDir}`)
  }

  try {
    // Find initial commit (first commit in history)
    const initialCommit = execSync("git rev-list --max-parents=0 HEAD", {
      cwd: workspaceDir,
      encoding: "utf-8",
    }).trim()

    // Validate commit hash format (40 hex characters) to prevent command injection
    if (!/^[0-9a-f]{40}$/i.test(initialCommit)) {
      throw new Error(`Invalid commit hash format: ${initialCommit}`)
    }

    // Hard reset to initial commit
    execSync(`git reset --hard ${initialCommit}`, {
      cwd: workspaceDir,
      encoding: "utf-8",
    })

    // Clean untracked AND ignored files (like target/, *.class, __pycache__)
    // The -x flag is critical: without it, gitignored build artifacts persist
    execSync("git clean -fdx", {
      cwd: workspaceDir,
      encoding: "utf-8",
    })
  } catch (error) {
    const msg = error instanceof Error ? error.message : String(error)
    throw new Error(`Git reset failed: ${msg}`)
  }
}

/**
 * Clear workspace cache files
 */
function clearWorkspaceCaches(workspaceDir: string): void {
  const cacheFiles = ["attempts.json", "last_grade.json", "last_test.json", "last_test_meta.json", "last_stream.json", "last_messages.json", "last_perf.json", "submission.json"]
  for (const file of cacheFiles) {
    const filePath = path.join(workspaceDir, file)
    if (fs.existsSync(filePath)) {
      fs.unlinkSync(filePath)
    }
  }
}

/**
 * Check if a config has performance benchmarking enabled.
 * Looks for benchmarking.mode === "performance" in the config data.
 */
function hasPerformanceBenchmarking(data: Record<string, unknown>): boolean {
  const benchmarking = data.benchmarking as Record<string, unknown> | undefined
  return benchmarking?.mode === "performance"
}


/**
 * Build ModuleConfig from parsed config data using the unified schema.
 * Handles both unified and legacy field names.
 */
function buildModuleConfigFromData(data: Record<string, unknown>, moduleNum: number): ModuleConfig {
  // Get grading info - could be nested under "grading" or at top level
  const grading = data.grading as Record<string, unknown> | undefined

  // Get display name using unified schema with fallbacks
  const displayName = getStringFirstOf(data,
    ["display_name", "module_name", "homework_name", "project_name", "assignment_name"],
    `Module ${moduleNum}`)

  // Get grading values from either nested grading object or top level
  const maxAttempts = grading
    ? getNumber(grading, "max_attempts", 10)
    : getNumber(data, "max_attempts", 10)
  const totalPoints = grading
    ? getNumber(grading, "total_points", 0)
    : getNumber(data, "total_points", 0)
  const totalTests = grading
    ? getNumber(grading, "total_tests", 0)
    : getNumber(data, "total_tests", 0)

  // Get functions/source files - try multiple field names
  let functions = getStringArray(data, "functions")
  if (functions.length === 0) functions = getStringArray(data, "source_files")
  if (functions.length === 0) functions = getStringArray(data, "implementation_files")
  if (functions.length === 0) functions = getStringArray(data, "solution_files")

  return {
    module_number: moduleNum,
    module_name: displayName,
    max_attempts: maxAttempts,
    total_points: totalPoints,
    total_tests: totalTests,
    functions,
    supports_performance: hasPerformanceBenchmarking(data),
  }
}

function loadModuleConfig(moduleNum: number, course: string): ModuleConfig {
  const projectRoot = process.cwd()
  const configsDir = path.join(projectRoot, course, "configs")

  if (!fs.existsSync(configsDir)) {
    return {
      module_number: moduleNum,
      module_name: `Module ${moduleNum}`,
      max_attempts: 10,
      total_points: 0,
      total_tests: 0,
      functions: [],
      supports_performance: false,
    }
  }

  // Scan all YAML files and find the one matching our assignment number
  // This works regardless of the file naming convention (module1.yaml, hw1.yaml, assignment1.yaml, etc.)
  const files = fs.readdirSync(configsDir).filter((f) => f.endsWith(".yaml"))
  for (const file of files) {
    try {
      const filePath = path.join(configsDir, file)
      const content = fs.readFileSync(filePath, "utf-8")
      const data = yaml.load(content, { schema: yaml.JSON_SCHEMA }) as Record<string, unknown>

      const configNum = getAssignmentNumber(data)
      if (configNum === moduleNum) {
        return buildModuleConfigFromData(data, moduleNum)
      }
    } catch (e) {
      // Skip invalid YAML files
      console.error(`Warning: Failed to parse config ${file}:`, e)
    }
  }

  // Default fallback
  return {
    module_number: moduleNum,
    module_name: `Module ${moduleNum}`,
    max_attempts: 10,
    total_points: 0,
    total_tests: 0,
    functions: [],
    supports_performance: false,
  }
}

/**
 * Load allowed editable files from the course config.
 * Returns file paths relative to workspace that the agent can edit.
 * Uses unified config schema - scans all YAML files and matches by assignment_number.
 */

function getModuleAttempts(wsDir: string): AttemptsData | null {
  const attemptsFile = path.join(wsDir, "attempts.json")
  if (fs.existsSync(attemptsFile)) {
    const content = fs.readFileSync(attemptsFile, "utf-8")
    return JSON.parse(content) as AttemptsData
  }

  // Fallback: check last_test.json / last_grade.json (used by "Run Tests" button for Java/C)
  const lastTestFile = path.join(wsDir, "last_test.json")
  const lastGradeFile = path.join(wsDir, "last_grade.json")
  const fallbackFile = fs.existsSync(lastTestFile) ? lastTestFile : lastGradeFile
  if (fs.existsSync(fallbackFile)) {
    try {
      const content = fs.readFileSync(fallbackFile, "utf-8")
      const gradeData = JSON.parse(content) as {
        tests?: { name: string; passed: boolean }[]
        summary?: { passed: number; total: number; percentage: number }
        timestamp?: string
        compilationError?: string
      }

      // Convert last_grade.json format to AttemptsData format
      if (gradeData.summary) {
        return {
          module_number: 0,
          attempts: [{
            attempt_number: 1,
            timestamp: gradeData.timestamp || new Date().toISOString(),
            code_hash: "",
            tests_passed: gradeData.summary.passed,
            tests_total: gradeData.summary.total,
            points_earned: 0,
            points_possible: 0,
            pass_rate: gradeData.summary.total > 0 ? gradeData.summary.passed / gradeData.summary.total : 0,
            score_percentage: gradeData.summary.percentage,
            errors: [],
          }]
        }
      }
    } catch {
      // Ignore parse errors
    }
  }

  return null
}

function buildModuleInfoFromAttempts(
  moduleNum: number,
  moduleName: string,
  attemptsData: AttemptsData | null
): Record<string, unknown> {
  const moduleInfo: Record<string, unknown> = {
    module: moduleNum,
    name: moduleName,
    attempts: 0,
    best_score: 0,
    latest_score: 0,
    tests_passed: 0,
    tests_total: 0,
    status: "pending",
  }

  if (attemptsData && attemptsData.attempts && attemptsData.attempts.length > 0) {
    const attempts = attemptsData.attempts
    const latest = attempts[attempts.length - 1]

    moduleInfo.attempts = attempts.length
    moduleInfo.best_score = Math.max(...attempts.map((a) => a.score_percentage))
    moduleInfo.latest_score = latest.score_percentage
    moduleInfo.tests_passed = latest.tests_passed
    moduleInfo.tests_total = latest.tests_total

    if (latest.score_percentage === 100) {
      moduleInfo.status = "pass"
    } else if (latest.score_percentage > 0) {
      moduleInfo.status = "in_progress"
    }
  }

  return moduleInfo
}

// Shared helper to create tool callbacks for benchmark instrumentation
function createBenchmarkCallbacks(
  state: WorkspaceState,
  emitter: EventEmitter,
  workspaceDir: string
): { callbacks: ToolCallbacks; getLastToolArgs: () => Record<string, unknown> | null } {
  let lastToolArgs: Record<string, unknown> | null = null

  const callbacks: ToolCallbacks = {
    onToolStart: (tool, args) => {
      lastToolArgs = args
      emitter.emit({ type: "tool_start", tool, args })
    },
    onToolEnd: (tool, result) => {
      emitter.emit({ type: "tool_end", tool, result: result.slice(0, MAX_TOOL_OUTPUT_LENGTH) })

      // Parse submit output
      if (tool === "submit") {
        // Read submission.json to get structured results
        const submissionPath = path.join(workspaceDir, "submission.json")
        if (fs.existsSync(submissionPath)) {
          try {
            const submission = JSON.parse(fs.readFileSync(submissionPath, "utf-8")) as {
              codeResults?: { passed: number; total: number; percentage: number }
              writtenResults?: { pointsEarned: number; pointsMax: number; percentage: number }
              llmGraded: boolean
            }
            emitter.emit({
              type: "submit",
              codeResults: submission.codeResults,
              writtenResults: submission.writtenResults,
              llmGraded: submission.llmGraded,
            })
          } catch {
            // Ignore parse errors
          }
        }
      }

      // Track file changes
      if (tool === "write" || tool === "edit") {
        const filePath = lastToolArgs?.filePath as string | undefined
        if (filePath) {
          emitter.emit({ type: "file_change", path: filePath, action: tool as "write" | "edit" })
        }
      }

      // Clear args after processing
      lastToolArgs = null
    },
  }

  return { callbacks, getLastToolArgs: () => lastToolArgs }
}

function createEngineCallbacks(
  workspaceId: string,
  state: WorkspaceState,
  emitter: EventEmitter,
  workspaceDir: string
): BenchmarkCallbacks {
  return {
    onText: (text) => {
      emitter.emit({ type: "text", text })
    },
    onStep: (step, tools) => {
      state.steps = step
      emitter.emit({ type: "step", step, tools })
    },
    onTokens: (usage: BenchmarkTokenUsage) => {
      state.totalInputTokens = usage.inputTokens
      state.totalOutputTokens = usage.outputTokens
      state.totalCost = usage.cost
      emitter.emit({
        type: "tokens",
        inputTokens: usage.inputTokens,
        outputTokens: usage.outputTokens,
        totalTokens: usage.totalTokens,
        cost: usage.cost,
      })
    },
    onGrade: (gradeResult) => {
      state.lastGradeResult = gradeResult
      emitter.emit({
        type: "test",
        tests: gradeResult.tests,
        summary: {
          passed: gradeResult.passed,
          total: gradeResult.total,
          percentage: gradeResult.percentage,
        },
        compilationError: gradeResult.compilationError,
      })

      const cacheData = {
        tests: gradeResult.tests,
        summary: {
          passed: gradeResult.passed,
          total: gradeResult.total,
          percentage: gradeResult.percentage,
        },
        timestamp: new Date().toISOString(),
        compilationError: gradeResult.compilationError,
      }
      const cachePath = path.join(workspaceDir, "last_test.json")
      fs.writeFileSync(cachePath, JSON.stringify(cacheData, null, 2))
      const legacyCachePath = path.join(workspaceDir, "last_grade.json")
      fs.writeFileSync(legacyCachePath, JSON.stringify(cacheData, null, 2))
    },
    onPerf: (perfResult: BenchmarkPerfResult) => {
      emitter.emit({
        type: "malloc_perf",
        ...perfResult,
      })

      const cachePath = path.join(workspaceDir, "last_perf.json")
      fs.writeFileSync(
        cachePath,
        JSON.stringify(
          {
            ...perfResult,
            timestamp: new Date().toISOString(),
          },
          null,
          2
        )
      )
    },
    onMessages: (messages: ModelMessage[]) => {
      saveMessagesToWorkspace(workspaceId, state, messages)
    },
  }
}

async function runBenchmarkWithEngine(options: {
  workspaceId: string
  state: WorkspaceState
  workspaceDir: string
  maxSteps: number
  prompt?: string
  previousMessages?: ModelMessage[]
}) {
  const { workspaceId, state, workspaceDir, maxSteps, prompt, previousMessages } = options
  const { emitter } = state
  if (!emitter) {
    throw new Error("Workspace state not properly initialized")
  }

  try {
    const { callbacks: toolCallbacks } = createBenchmarkCallbacks(state, emitter, workspaceDir)
    const engineCallbacks = createEngineCallbacks(workspaceId, state, emitter, workspaceDir)

    const result = await runBenchmarkEngine({
      workspaceDir,
      model: state.model,
      maxSteps,
      prompt,
      previousMessages,
      toolCallbacks,
      callbacks: engineCallbacks,
      abortSignal: state.controller?.signal,
      throwOnError: true,
    })

    if (state.controller?.signal.aborted || state.status === "failed") {
      return
    }

    state.status = "completed"
    state.steps = result.steps

    if (result.tokens) {
      state.totalInputTokens = result.tokens.inputTokens
      state.totalOutputTokens = result.tokens.outputTokens
      state.totalCost = result.tokens.cost
    }

    const finalGrade = result.gradeResult ?? state.lastGradeResult

    emitter.emit({
      type: "done",
      success: result.success,
      steps: result.steps,
      gradeResult: finalGrade
        ? {
            passed: finalGrade.passed,
            total: finalGrade.total,
            percentage: finalGrade.percentage,
          }
        : undefined,
    })
    saveStreamToWorkspace(workspaceId, state)
    scheduleWorkspaceCleanup(workspaceId)
  } catch (error) {
    state.status = "failed"
    emitter.emit({
      type: "error",
      error: error instanceof Error ? error.message : "Unknown error",
    })
    saveStreamToWorkspace(workspaceId, state)
    scheduleWorkspaceCleanup(workspaceId)
  }
}

// Run the benchmark agent in the background
async function runBenchmarkAgent(workspaceId: string, state: WorkspaceState, workspaceDir: string, maxSteps: number) {
  return runBenchmarkWithEngine({ workspaceId, state, workspaceDir, maxSteps })
}

// Run the continue benchmark agent with a continuation prompt
async function runContinueBenchmarkAgent(
  workspaceId: string,
  state: WorkspaceState,
  workspaceDir: string,
  maxSteps: number,
  contextMessage: string,
  previousMessages?: ModelMessage[]
) {
  return runBenchmarkWithEngine({
    workspaceId,
    state,
    workspaceDir,
    maxSteps,
    prompt: contextMessage,
    previousMessages,
  })
}

// Main HTTP server
const server = Bun.serve({
  port: PORT,
  // Increase idle timeout for long-running operations (Docker-based grading can take 2+ minutes)
  idleTimeout: 255, // Maximum allowed (255 seconds = ~4 minutes)
  async fetch(req) {
    const url = new URL(req.url)
    const pathname = url.pathname
    const method = req.method

    if (method === "OPTIONS") {
      return new Response(null, {
        status: 204,
        headers: corsHeaders(),
      })
    }

    if (pathname === "/health" || pathname === "/api/health") {
      return jsonResponse({ status: "ok", port: PORT })
    }

    const workspaceResponse = await handleWorkspaceRoutes(req, url, pathname, method, {
      WORKSPACES_DIR,
      workspaceStates,
      validateWorkspaceId,
      getOrCreateWorkspaceState,
      resetWorkspaceState,
      createEventEmitter,
      saveStreamToWorkspace,
      scheduleWorkspaceCleanup,
      runBenchmarkAgent,
      runContinueBenchmarkAgent,
      loadMessagesFromWorkspace,
      resetWorkspace,
      clearWorkspaceCaches,
      jsonResponse,
    })
    if (workspaceResponse) {
      return workspaceResponse
    }

    const catalogResponse = await handleCatalogRoutes(req, pathname, method, {
      WORKSPACES_DIR,
      validateWorkspaceId,
      isValidWorkspace,
      getModuleAttempts,
      buildModuleInfoFromAttempts,
      loadModuleConfig,
      jsonResponse,
    })
    if (catalogResponse) {
      return catalogResponse
    }

    return jsonResponse({ error: "Not found" }, 404)
  },
})

console.log(`BSCS Bench API Server running on http://localhost:${PORT}`)
