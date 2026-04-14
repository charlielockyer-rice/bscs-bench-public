import type { ModelMessage } from "ai"
import * as fs from "fs"
import * as path from "path"
import { spawn } from "child_process"

import { parseGradeOutput } from "./gradeParser"
import { config, getAugmentedEnv } from "./config"
import { loadWorkspaceConfigFromFile, resolveDependency } from "./workspaceConfig"
import type { EventEmitter, WorkspaceState, WorkspaceValidationResult } from "./serverTypes"

interface WorkspaceRouteDeps {
  WORKSPACES_DIR: string
  workspaceStates: Map<string, WorkspaceState>
  validateWorkspaceId: (workspaceId: string) => WorkspaceValidationResult
  getOrCreateWorkspaceState: (workspaceId: string) => WorkspaceState
  resetWorkspaceState: (state: WorkspaceState) => void
  createEventEmitter: (state: WorkspaceState) => EventEmitter
  saveStreamToWorkspace: (workspaceId: string, state: WorkspaceState) => void
  scheduleWorkspaceCleanup: (workspaceId: string) => void
  runBenchmarkAgent: (
    workspaceId: string,
    state: WorkspaceState,
    workspaceDir: string,
    maxSteps: number
  ) => Promise<unknown>
  runContinueBenchmarkAgent: (
    workspaceId: string,
    state: WorkspaceState,
    workspaceDir: string,
    maxSteps: number,
    contextMessage: string,
    previousMessages?: ModelMessage[]
  ) => Promise<unknown>
  loadMessagesFromWorkspace: (workspaceDir: string) => ModelMessage[] | null
  resetWorkspace: (workspaceDir: string) => void
  clearWorkspaceCaches: (workspaceDir: string) => void
  jsonResponse: (data: unknown, status?: number) => Response
}

export async function handleWorkspaceRoutes(
  req: Request,
  url: URL,
  pathname: string,
  method: string,
  deps: WorkspaceRouteDeps
): Promise<Response | null> {
  let match: RegExpMatchArray | null

  match = pathname.match(/^\/api\/workspace\/([^/]+)\/start$/)
  if (match && method === "POST") {
    const workspaceId = match[1]

    try {
      const body = await req.json()
      const { model = config.defaultModel, maxSteps = config.defaultMaxSteps } = body

      const validation = deps.validateWorkspaceId(workspaceId)
      if (!validation.valid) {
        return deps.jsonResponse({ error: validation.error }, 400)
      }

      const workspaceDir = validation.workspaceDir
      if (!fs.existsSync(workspaceDir)) {
        return deps.jsonResponse({ error: `Workspace not found: ${workspaceId}` }, 404)
      }

      const state = deps.getOrCreateWorkspaceState(workspaceId)
      if (state.status === "running") {
        return deps.jsonResponse({
          workspaceId,
          status: "running",
          reconnected: true,
          steps: state.steps,
          tokens: {
            input: state.totalInputTokens,
            output: state.totalOutputTokens,
            total: state.totalInputTokens + state.totalOutputTokens,
            cost: state.totalCost,
          },
        })
      }

      const depResult = resolveDependency(workspaceDir, deps.WORKSPACES_DIR)
      if (depResult.resolved && depResult.filesCopied > 0) {
        console.log(`[${workspaceId}] ${depResult.message}`)
      }

      deps.resetWorkspaceState(state)
      state.status = "running"
      state.model = model
      state.startedAt = new Date()
      state.controller = new AbortController()
      state.emitter = deps.createEventEmitter(state)

      deps.runBenchmarkAgent(workspaceId, state, workspaceDir, maxSteps).catch((error) => {
        console.error(`Benchmark for ${workspaceId} failed:`, error)
        state.status = "failed"
        state.emitter?.emit({
          type: "error",
          error: error instanceof Error ? error.message : "Unknown error",
        })
        deps.scheduleWorkspaceCleanup(workspaceId)
      })

      return deps.jsonResponse({ workspaceId, status: "running" })
    } catch (error) {
      return deps.jsonResponse(
        { error: error instanceof Error ? error.message : "Unknown error" },
        500
      )
    }
  }

  match = pathname.match(/^\/api\/workspace\/([^/]+)\/stop$/)
  if (match && method === "POST") {
    const workspaceId = match[1]

    const state = deps.workspaceStates.get(workspaceId)
    if (!state || state.status !== "running") {
      return deps.jsonResponse({ error: "No running benchmark for this workspace" }, 404)
    }

    state.controller?.abort()
    state.status = "failed"
    state.emitter?.emit({ type: "done", success: false, steps: state.steps })
    deps.saveStreamToWorkspace(workspaceId, state)
    deps.scheduleWorkspaceCleanup(workspaceId)

    return deps.jsonResponse({ workspaceId, status: "stopped" })
  }

  match = pathname.match(/^\/api\/workspace\/([^/]+)\/events$/)
  if (match && method === "GET") {
    const workspaceId = match[1]

    const validation = deps.validateWorkspaceId(workspaceId)
    if (!validation.valid) {
      return deps.jsonResponse({ error: validation.error }, 400)
    }

    const state = deps.workspaceStates.get(workspaceId)
    if (state) {
      return deps.jsonResponse({
        events: state.events,
        status: state.status,
        steps: state.steps,
        tokens: {
          input: state.totalInputTokens,
          output: state.totalOutputTokens,
          total: state.totalInputTokens + state.totalOutputTokens,
          cost: state.totalCost,
        },
      })
    }

    const streamPath = path.join(validation.workspaceDir, "last_stream.json")
    if (fs.existsSync(streamPath)) {
      try {
        const cached = JSON.parse(fs.readFileSync(streamPath, "utf-8"))
        return deps.jsonResponse({
          events: cached.events || [],
          status: cached.status || "completed",
          steps: cached.steps || 0,
          tokens: cached.tokens || { input: 0, output: 0, total: 0, cost: 0 },
          cached: true,
        })
      } catch {
        // Fall through to empty response
      }
    }

    return deps.jsonResponse({
      events: [],
      status: "idle",
      steps: 0,
      tokens: { input: 0, output: 0, total: 0, cost: 0 },
    })
  }

  match = pathname.match(/^\/api\/workspace\/([^/]+)\/stream$/)
  if (match && method === "GET") {
    const workspaceId = match[1]

    const state = deps.workspaceStates.get(workspaceId)
    if (!state?.emitter || state.status !== "running") {
      const stream = new ReadableStream({
        start(controller) {
          controller.close()
        },
      })
      return new Response(stream, {
        headers: {
          "Content-Type": "text/event-stream",
          "Cache-Control": "no-cache",
          Connection: "keep-alive",
          "Access-Control-Allow-Origin": "*",
          "Access-Control-Allow-Methods": "GET, POST, DELETE, OPTIONS",
          "Access-Control-Allow-Headers": "Content-Type",
        },
      })
    }

    let unsubscribe: (() => void) | null = null
    const encoder = new TextEncoder()
    const stream = new ReadableStream({
      start(controller) {
        unsubscribe = state.emitter!.subscribe((event) => {
          try {
            controller.enqueue(encoder.encode(`data: ${JSON.stringify(event)}\n\n`))
            if (event.type === "done" || event.type === "error") {
              unsubscribe?.()
              unsubscribe = null
              controller.close()
            }
          } catch {
            unsubscribe?.()
            unsubscribe = null
            try {
              controller.close()
            } catch {
              // Ignore already-closed stream
            }
          }
        })
      },
      cancel() {
        unsubscribe?.()
        unsubscribe = null
      },
    })

    return new Response(stream, {
      headers: {
        "Content-Type": "text/event-stream",
        "Cache-Control": "no-cache",
        Connection: "keep-alive",
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Methods": "GET, POST, DELETE, OPTIONS",
        "Access-Control-Allow-Headers": "Content-Type",
      },
    })
  }

  match = pathname.match(/^\/api\/workspace\/([^/]+)\/status$/)
  if (match && method === "GET") {
    const workspaceId = match[1]

    const validation = deps.validateWorkspaceId(workspaceId)
    if (!validation.valid) {
      return deps.jsonResponse({ error: validation.error }, 400)
    }

    const state = deps.workspaceStates.get(workspaceId)
    if (state) {
      return deps.jsonResponse({
        workspaceId,
        status: state.status,
        model: state.model,
        steps: state.steps,
        startedAt: state.startedAt?.toISOString(),
        tokens: {
          input: state.totalInputTokens,
          output: state.totalOutputTokens,
          total: state.totalInputTokens + state.totalOutputTokens,
          cost: state.totalCost,
        },
        gradeResult: state.lastGradeResult
          ? {
              passed: state.lastGradeResult.passed,
              total: state.lastGradeResult.total,
              percentage: state.lastGradeResult.percentage,
            }
          : undefined,
      })
    }

    const streamPath = path.join(validation.workspaceDir, "last_stream.json")
    if (fs.existsSync(streamPath)) {
      try {
        const cached = JSON.parse(fs.readFileSync(streamPath, "utf-8"))
        return deps.jsonResponse({
          workspaceId,
          status: cached.status || "idle",
          model: cached.model,
          steps: cached.steps || 0,
          startedAt: cached.startedAt,
          tokens: cached.tokens || { input: 0, output: 0, total: 0, cost: 0 },
          cached: true,
        })
      } catch {
        // Fall through
      }
    }

    return deps.jsonResponse({
      workspaceId,
      status: "idle",
      steps: 0,
      tokens: { input: 0, output: 0, total: 0, cost: 0 },
    })
  }

  match = pathname.match(/^\/api\/workspace\/([^/]+)\/file$/)
  if (match && method === "GET") {
    const workspaceId = match[1]
    const filePath = url.searchParams.get("path")

    if (!filePath) {
      return deps.jsonResponse({ error: "path query parameter is required" }, 400)
    }

    const validation = deps.validateWorkspaceId(workspaceId)
    if (!validation.valid) {
      return deps.jsonResponse({ error: validation.error }, 400)
    }

    const workspaceDir = validation.workspaceDir
    const fullPath = path.resolve(workspaceDir, filePath)
    if (!fullPath.startsWith(workspaceDir + path.sep) && fullPath !== workspaceDir) {
      return deps.jsonResponse({ error: "Access denied: path outside workspace" }, 403)
    }
    if (!fs.existsSync(fullPath)) {
      return deps.jsonResponse({ error: "File not found" }, 404)
    }

    const stat = fs.statSync(fullPath)
    if (stat.isDirectory()) {
      return deps.jsonResponse({ error: "Path is a directory" }, 400)
    }

    return deps.jsonResponse({ path: filePath, content: fs.readFileSync(fullPath, "utf-8") })
  }

  match = pathname.match(/^\/api\/workspace\/([^/]+)\/files$/)
  if (match && method === "GET") {
    const workspaceId = match[1]

    const validation = deps.validateWorkspaceId(workspaceId)
    if (!validation.valid) {
      return deps.jsonResponse({ error: validation.error }, 400)
    }

    const workspaceDir = validation.workspaceDir
    if (!fs.existsSync(workspaceDir)) {
      return deps.jsonResponse({ error: "Workspace not found" }, 404)
    }

    const files: string[] = []
    function walk(dir: string, prefix = "") {
      const entries = fs.readdirSync(dir, { withFileTypes: true })
      for (const entry of entries) {
        if (entry.name.startsWith(".") || entry.name === "__pycache__") continue
        const relPath = prefix ? `${prefix}/${entry.name}` : entry.name
        if (entry.isDirectory()) {
          walk(path.join(dir, entry.name), relPath)
        } else {
          files.push(relPath)
        }
      }
    }

    walk(workspaceDir)
    return deps.jsonResponse({ files })
  }

  match = pathname.match(/^\/api\/workspace\/([^/]+)\/test$/)
  if (match && method === "POST") {
    const workspaceId = match[1]

    const validation = deps.validateWorkspaceId(workspaceId)
    if (!validation.valid) {
      return deps.jsonResponse({ error: validation.error }, 400)
    }

    const workspaceDir = validation.workspaceDir
    if (!fs.existsSync(workspaceDir)) {
      return deps.jsonResponse({ error: "Workspace not found" }, 404)
    }

    try {
      const testScript = path.resolve(process.cwd(), "bin/test")
      const proc = spawn(testScript, [workspaceDir, "--run-type", "test", "-v"], {
        cwd: process.cwd(),
        env: getAugmentedEnv(),
        stdio: ["ignore", "pipe", "pipe"],
      })

      let output = ""
      proc.stdout?.on("data", (d: Buffer) => (output += d.toString()))
      proc.stderr?.on("data", (d: Buffer) => (output += d.toString()))

      await new Promise<void>((resolve) => {
        proc.once("exit", resolve)
        proc.once("error", resolve)
      })

      const gradeResult = parseGradeOutput(output)
      if (!gradeResult) {
        return deps.jsonResponse({
          tests: [],
          summary: { passed: 0, total: 0, percentage: 0 },
          raw: output,
          error: "Failed to parse grade output",
        })
      }

      const response: Record<string, unknown> = {
        tests: gradeResult.tests,
        summary: {
          passed: gradeResult.passed,
          total: gradeResult.total,
          percentage: gradeResult.percentage,
        },
        timestamp: new Date().toISOString(),
      }
      if (gradeResult.compilationError) {
        response.compilationError = gradeResult.compilationError
      }

      const cachePath = path.join(workspaceDir, "last_test.json")
      fs.writeFileSync(cachePath, JSON.stringify(response, null, 2))
      const legacyCachePath = path.join(workspaceDir, "last_grade.json")
      fs.writeFileSync(legacyCachePath, JSON.stringify(response, null, 2))
      return deps.jsonResponse({ ...response, raw: output })
    } catch (error) {
      return deps.jsonResponse(
        { error: error instanceof Error ? error.message : "Failed to run grader" },
        500
      )
    }
  }

  match = pathname.match(/^\/api\/workspace\/([^/]+)\/malloc_perf$/)
  if (match && method === "POST") {
    const workspaceId = match[1]

    const validation = deps.validateWorkspaceId(workspaceId)
    if (!validation.valid) {
      return deps.jsonResponse({ error: validation.error }, 400)
    }

    const workspaceDir = validation.workspaceDir
    if (!fs.existsSync(workspaceDir)) {
      return deps.jsonResponse({ error: "Workspace not found" }, 404)
    }
    if (!fs.existsSync(path.join(workspaceDir, "mm.c"))) {
      return deps.jsonResponse({ error: "This endpoint is only for malloc workspaces" }, 400)
    }

    try {
      const runnerPath = path.join(process.cwd(), "comp321", "runner.py")
      if (!fs.existsSync(runnerPath)) {
        return deps.jsonResponse({ error: `comp321 runner not found: ${runnerPath}` }, 500)
      }

      const proc = spawn("python3", [runnerPath, "malloc", workspaceDir, "--perf", "--json"], {
        cwd: process.cwd(),
        env: getAugmentedEnv(),
        stdio: ["ignore", "pipe", "pipe"],
      })

      let output = ""
      proc.stdout?.on("data", (d: Buffer) => (output += d.toString()))
      proc.stderr?.on("data", (d: Buffer) => (output += d.toString()))

      const timer = setTimeout(() => {
        proc.kill()
      }, 120000)

      await new Promise<void>((resolve) => {
        proc.once("exit", resolve)
        proc.once("error", resolve)
      })
      clearTimeout(timer)

      let parsed: {
        performanceIndex?: number | null
        utilizationScore?: number | null
        throughputScore?: number | null
        correctness?: { passed: number; total: number }
        raw?: string
        error?: string
      } | null = null

      try {
        parsed = JSON.parse(output)
      } catch {
        // Fall back to error below
      }

      if (!parsed) {
        return deps.jsonResponse({ error: "Failed to parse perf output", raw: output }, 500)
      }
      if (parsed.error) {
        return deps.jsonResponse({ error: parsed.error, raw: parsed.raw }, 500)
      }

      const response = {
        performanceIndex: parsed.performanceIndex ?? null,
        utilizationScore: parsed.utilizationScore ?? null,
        throughputScore: parsed.throughputScore ?? null,
        correctness: parsed.correctness ?? { passed: 0, total: 0 },
        raw: parsed.raw ?? "",
        timestamp: new Date().toISOString(),
      }

      fs.writeFileSync(path.join(workspaceDir, "last_perf.json"), JSON.stringify(response, null, 2))
      return deps.jsonResponse(response)
    } catch (error) {
      return deps.jsonResponse(
        { error: error instanceof Error ? error.message : "Failed to run performance test" },
        500
      )
    }
  }

  match = pathname.match(/^\/api\/workspace\/([^/]+)\/test$/)
  if (match && method === "GET") {
    const workspaceId = match[1]

    const validation = deps.validateWorkspaceId(workspaceId)
    if (!validation.valid) {
      return deps.jsonResponse({ error: validation.error }, 400)
    }

    const workspaceDir = validation.workspaceDir
    const cachePath = path.join(workspaceDir, "last_test.json")
    const legacyCachePath = path.join(workspaceDir, "last_grade.json")
    const fileToRead = fs.existsSync(cachePath) ? cachePath : legacyCachePath

    if (!fs.existsSync(fileToRead)) {
      return deps.jsonResponse({
        tests: [],
        summary: { passed: 0, total: 0, percentage: 0 },
        cached: false,
      })
    }

    try {
      const cached = JSON.parse(fs.readFileSync(fileToRead, "utf-8"))
      return deps.jsonResponse({ ...cached, cached: true })
    } catch {
      return deps.jsonResponse({
        tests: [],
        summary: { passed: 0, total: 0, percentage: 0 },
        cached: false,
      })
    }
  }

  match = pathname.match(/^\/api\/workspace\/([^/]+)\/submission$/)
  if (match && method === "GET") {
    const workspaceId = match[1]

    const validation = deps.validateWorkspaceId(workspaceId)
    if (!validation.valid) {
      return deps.jsonResponse({ error: validation.error }, 400)
    }

    const submissionPath = path.join(validation.workspaceDir, "submission.json")
    if (!fs.existsSync(submissionPath)) {
      return deps.jsonResponse(null)
    }

    try {
      return deps.jsonResponse(JSON.parse(fs.readFileSync(submissionPath, "utf-8")))
    } catch {
      return deps.jsonResponse({ error: "Failed to read submission" }, 500)
    }
  }

  match = pathname.match(/^\/api\/workspace\/([^/]+)\/llm_grade$/)
  if (match && method === "POST") {
    const workspaceId = match[1]

    const validation = deps.validateWorkspaceId(workspaceId)
    if (!validation.valid) {
      return deps.jsonResponse({ error: validation.error }, 400)
    }

    const workspaceDir = validation.workspaceDir
    if (!fs.existsSync(workspaceDir)) {
      return deps.jsonResponse({ error: "Workspace not found" }, 404)
    }

    try {
      const { gradeSubmission } = await import("./llmGrader")
      const wsConfig = loadWorkspaceConfigFromFile(workspaceDir)
      if (!wsConfig) {
        return deps.jsonResponse({ error: "Invalid workspace: missing workspace.yaml" }, 400)
      }

      let body: {
        model?: string
        gradingPrompt?: string
        submission?: string
        rubric?: string
        referenceSolution?: string
      } = {}
      try {
        body = await req.json()
      } catch {
        // Empty body is OK
      }

      const result = await gradeSubmission({
        workspaceDir,
        course: wsConfig.course,
        assignmentNumber: wsConfig.module_number,
        assignmentName: wsConfig.module_name,
        model: body.model,
        gradingPrompt: body.gradingPrompt,
        submission: body.submission,
        rubric: body.rubric,
        referenceSolution: body.referenceSolution,
      })

      const cacheData = {
        ...result,
        timestamp: new Date().toISOString(),
        llmGraded: true,
      }
      fs.writeFileSync(path.join(workspaceDir, "last_test.json"), JSON.stringify(cacheData, null, 2))
      fs.writeFileSync(path.join(workspaceDir, "last_grade.json"), JSON.stringify(cacheData, null, 2))
      return deps.jsonResponse(cacheData)
    } catch (error) {
      return deps.jsonResponse(
        { error: error instanceof Error ? error.message : "LLM grading failed" },
        500
      )
    }
  }

  match = pathname.match(/^\/api\/workspace\/([^/]+)\/continue$/)
  if (match && method === "POST") {
    const workspaceId = match[1]

    const validation = deps.validateWorkspaceId(workspaceId)
    if (!validation.valid) {
      return deps.jsonResponse({ error: validation.error }, 400)
    }

    const workspaceDir = validation.workspaceDir
    if (!fs.existsSync(workspaceDir)) {
      return deps.jsonResponse({ error: "Workspace not found" }, 404)
    }

    try {
      const body = await req.json()
      const { model = config.defaultModel, maxSteps = config.defaultMaxSteps, message } = body

      const state = deps.getOrCreateWorkspaceState(workspaceId)
      if (state.status === "running") {
        return deps.jsonResponse({ error: "Benchmark already running for this workspace" }, 400)
      }

      if (state.events.length === 0) {
        const streamPath = path.join(workspaceDir, "last_stream.json")
        if (fs.existsSync(streamPath)) {
          try {
            const cached = JSON.parse(fs.readFileSync(streamPath, "utf-8")) as { events?: unknown[] }
            if (cached.events && cached.events.length > 0) {
              state.events = cached.events as WorkspaceState["events"]
            }
          } catch {
            // Ignore parse errors
          }
        }
      }

      let contextMessage = message || ""
      if (!contextMessage) {
        const testPath = path.join(workspaceDir, "last_test.json")
        const gradePath = path.join(workspaceDir, "last_grade.json")
        const fileToRead = fs.existsSync(testPath) ? testPath : gradePath
        if (fs.existsSync(fileToRead)) {
          try {
            const gradeData = JSON.parse(fs.readFileSync(fileToRead, "utf-8")) as {
              summary?: { passed: number; total: number; percentage: number }
            }
            if (gradeData.summary) {
              const { passed, total, percentage } = gradeData.summary
              contextMessage =
                percentage < 100
                  ? `Your previous attempt passed ${passed}/${total} tests (${percentage}%). Continue working to pass the remaining tests. Review the failing tests and fix the issues.`
                  : "All tests are passing. Review your implementation for any improvements."
            }
          } catch {
            // Ignore parse errors
          }
        }
        if (!contextMessage) {
          contextMessage =
            "Continue working on the assignment. Review your progress and complete any remaining tasks."
        }
      }

      const previousMessages = deps.loadMessagesFromWorkspace(workspaceDir)

      state.status = "running"
      state.model = model
      state.startedAt = new Date()
      state.steps = 0
      state.totalInputTokens = 0
      state.totalOutputTokens = 0
      state.totalCost = 0
      state.controller = new AbortController()
      state.emitter = deps.createEventEmitter(state)
      state.emitter.emit({
        type: "text",
        text: "\n--- Continued from previous run ---\n\n",
      })

      deps
        .runContinueBenchmarkAgent(
          workspaceId,
          state,
          workspaceDir,
          maxSteps,
          contextMessage,
          previousMessages ?? undefined
        )
        .catch((error) => {
          console.error(`Continue benchmark for ${workspaceId} failed:`, error)
          state.status = "failed"
          state.emitter?.emit({
            type: "error",
            error: error instanceof Error ? error.message : "Unknown error",
          })
          deps.scheduleWorkspaceCleanup(workspaceId)
        })

      return deps.jsonResponse({ workspaceId, status: "running" })
    } catch (error) {
      return deps.jsonResponse(
        { error: error instanceof Error ? error.message : "Unknown error" },
        500
      )
    }
  }

  match = pathname.match(/^\/api\/workspace\/([^/]+)\/reset$/)
  if (match && method === "POST") {
    const workspaceId = match[1]

    const validation = deps.validateWorkspaceId(workspaceId)
    if (!validation.valid) {
      return deps.jsonResponse({ error: validation.error }, 400)
    }

    const workspaceDir = validation.workspaceDir
    if (!fs.existsSync(workspaceDir)) {
      return deps.jsonResponse({ error: "Workspace not found" }, 404)
    }

    try {
      const state = deps.workspaceStates.get(workspaceId)
      if (state) {
        state.controller?.abort()
        deps.resetWorkspaceState(state)
      }

      deps.resetWorkspace(workspaceDir)
      deps.clearWorkspaceCaches(workspaceDir)
      return deps.jsonResponse({ status: "reset", workspaceId })
    } catch (error) {
      return deps.jsonResponse(
        { error: error instanceof Error ? error.message : "Failed to reset" },
        500
      )
    }
  }

  return null
}
