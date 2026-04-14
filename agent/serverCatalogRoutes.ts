import * as fs from "fs"
import * as path from "path"
import { spawn } from "child_process"

import { config, getAugmentedEnv } from "./config"
import { loadWorkspaceConfigFromFile } from "./workspaceConfig"
import { iterateWorkspaces } from "./workspacePaths"
import type { AttemptsData, ModuleConfig, WorkspaceValidationResult } from "./serverTypes"

interface CatalogRouteDeps {
  WORKSPACES_DIR: string
  validateWorkspaceId: (workspaceId: string) => WorkspaceValidationResult
  isValidWorkspace: (wsDir: string) => boolean
  getModuleAttempts: (wsDir: string) => AttemptsData | null
  buildModuleInfoFromAttempts: (
    moduleNum: number,
    moduleName: string,
    attemptsData: AttemptsData | null
  ) => Record<string, unknown>
  loadModuleConfig: (moduleNum: number, course: string) => ModuleConfig
  jsonResponse: (data: unknown, status?: number) => Response
}

export async function handleCatalogRoutes(
  req: Request,
  pathname: string,
  method: string,
  deps: CatalogRouteDeps
): Promise<Response | null> {
  let match: RegExpMatchArray | null

  if (pathname === "/api/workspaces" && method === "GET") {
    if (!fs.existsSync(deps.WORKSPACES_DIR)) {
      return deps.jsonResponse([])
    }

    const workspaces: Record<string, unknown>[] = []

    const allWorkspaces = [...iterateWorkspaces(deps.WORKSPACES_DIR, deps.isValidWorkspace)]
    allWorkspaces.sort((a, b) => a.id.localeCompare(b.id))

    for (const { id, dir: wsDir, modelDir } of allWorkspaces) {
      const wsConfig = loadWorkspaceConfigFromFile(wsDir)
      if (!wsConfig) continue

      const moduleNum = wsConfig.module_number || 0
      const moduleName = wsConfig.module_name || `Module ${moduleNum}`
      const course = wsConfig.course || "unknown"
      const attemptsData = deps.getModuleAttempts(wsDir)
      const moduleInfo = deps.buildModuleInfoFromAttempts(moduleNum, moduleName, attemptsData)
      const moduleConfig = deps.loadModuleConfig(moduleNum, course)

      moduleInfo.total_tests = moduleConfig.total_tests || 0
      moduleInfo.supports_performance = moduleConfig.supports_performance

      let submission: Record<string, unknown> | undefined
      const submissionPath = path.join(wsDir, "submission.json")
      if (fs.existsSync(submissionPath)) {
        try {
          const subData = JSON.parse(fs.readFileSync(submissionPath, "utf-8")) as {
            submitted?: boolean
            llmGraded?: boolean
            codeResults?: { passed: number; total: number; percentage: number }
            writtenResults?: { pointsEarned: number; pointsMax: number; percentage: number }
            combinedScore?: { earned: number; max: number; percentage: number }
          }
          submission = {
            submitted: subData.submitted ?? false,
            llmGraded: subData.llmGraded ?? false,
            codeResults: subData.codeResults,
            writtenResults: subData.writtenResults,
            combinedScore: subData.combinedScore,
          }
        } catch {
          // Ignore parse errors
        }
      }

      workspaces.push({
        id,
        type: modelDir ? "nested" : "flat",
        modelDir,
        course,
        module_number: moduleNum,
        module_name: moduleName,
        supports_performance: moduleConfig.supports_performance,
        submission,
        modules: [moduleInfo],
      })
    }

    return deps.jsonResponse(workspaces)
  }

  match = pathname.match(/^\/api\/workspaces\/([^/]+)$/)
  if (match && method === "GET") {
    const workspaceId = match[1]

    const validation = deps.validateWorkspaceId(workspaceId)
    if (!validation.valid) {
      return deps.jsonResponse({ error: validation.error }, 400)
    }

    const wsDir = validation.workspaceDir
    if (!fs.existsSync(wsDir)) {
      return deps.jsonResponse({ error: "Workspace not found" }, 404)
    }

    const wsConfig = loadWorkspaceConfigFromFile(wsDir)
    if (!wsConfig) {
      return deps.jsonResponse(
        { error: "Invalid workspace: missing workspace.yaml or workspace.json" },
        500
      )
    }

    const moduleNum = wsConfig.module_number || 0
    const moduleName = wsConfig.module_name || `Module ${moduleNum}`
    const course = wsConfig.course || "unknown"
    const moduleConfig = deps.loadModuleConfig(moduleNum, course)
    const attemptsData = deps.getModuleAttempts(wsDir)

    const moduleDetail: Record<string, unknown> = {
      module: moduleNum,
      name: moduleName,
      max_attempts: moduleConfig.max_attempts || 10,
      total_points: moduleConfig.total_points || 0,
      total_tests: moduleConfig.total_tests || 0,
      functions: moduleConfig.functions || [],
      supports_performance: moduleConfig.supports_performance,
      attempts: [],
      status: "pending",
    }

    if (attemptsData?.attempts?.length) {
      moduleDetail.attempts = attemptsData.attempts
      const latest = attemptsData.attempts[attemptsData.attempts.length - 1]
      if (latest.score_percentage === 100) {
        moduleDetail.status = "pass"
      } else if (latest.score_percentage > 0) {
        moduleDetail.status = "in_progress"
      }
    }

    return deps.jsonResponse({
      id: workspaceId,
      type: "flat",
      course,
      module_number: moduleNum,
      module_name: moduleName,
      supports_performance: moduleConfig.supports_performance,
      modules: [moduleDetail],
    })
  }

  if (pathname === "/api/models" && method === "GET") {
    return deps.jsonResponse({
      default: config.defaultModel,
      models: [
        { id: "minimax/minimax-m2.5", name: "MiniMax M2.5", provider: "MiniMax" },
        { id: "moonshotai/kimi-k2.5", name: "Kimi K2.5", provider: "Moonshot" },
        { id: "z-ai/glm-5", name: "GLM 5", provider: "Z-AI" },
        { id: "qwen/qwen3-coder:free", name: "Qwen3 Coder", provider: "Qwen" },
        { id: "anthropic/claude-opus-4.6", name: "Claude Opus 4.6", provider: "Anthropic" },
        { id: "anthropic/claude-sonnet-4.6", name: "Claude Sonnet 4.6", provider: "Anthropic" },
        { id: "anthropic/claude-haiku-4.5", name: "Claude Haiku 4.5", provider: "Anthropic" },
        { id: "openai/gpt-5.2-codex", name: "GPT-5.2 Codex", provider: "OpenAI" },
        { id: "openai/gpt-oss-120b:free", name: "GPT-OSS 120B", provider: "OpenAI" },
      ],
    })
  }

  if (pathname === "/api/courses" && method === "GET") {
    const projectRoot = process.cwd()
    const courses: Array<{ id: string; name: string; moduleCount: number }> = []

    const entries = fs.readdirSync(projectRoot, { withFileTypes: true })
    for (const entry of entries) {
      if (!entry.isDirectory()) continue
      if (entry.name.startsWith(".") || entry.name === "node_modules") continue

      const configsDir = path.join(projectRoot, entry.name, "configs")
      if (!fs.existsSync(configsDir)) continue

      const configFiles = fs.readdirSync(configsDir).filter((f) => f.endsWith(".yaml"))
      if (configFiles.length > 0) {
        courses.push({
          id: entry.name,
          name: entry.name.toUpperCase(),
          moduleCount: configFiles.length,
        })
      }
    }

    return deps.jsonResponse({ courses })
  }

  if (pathname === "/api/workspaces/setup" && method === "POST") {
    try {
      const body = (await req.json()) as { courseId: string; agentId: string; force?: boolean }
      const { courseId, agentId, force } = body

      if (!courseId || !agentId) {
        return deps.jsonResponse({ error: "courseId and agentId are required" }, 400)
      }
      if (
        courseId.includes("..") ||
        courseId.includes("/") ||
        courseId.includes("\\") ||
        agentId.includes("..") ||
        agentId.includes("/") ||
        agentId.includes("\\")
      ) {
        return deps.jsonResponse({ error: "Invalid courseId or agentId" }, 400)
      }

      const courseDir = path.join(process.cwd(), courseId)
      if (!fs.existsSync(courseDir)) {
        return deps.jsonResponse({ error: `Course not found: ${courseId}` }, 404)
      }

      const setupScript = path.join(process.cwd(), "bin", "setup_course")
      const args = [courseDir, agentId]
      if (force) args.push("--force")

      const proc = spawn(setupScript, args, {
        cwd: process.cwd(),
        env: getAugmentedEnv(),
        stdio: ["ignore", "pipe", "pipe"],
      })

      let output = ""
      proc.stdout?.on("data", (d: Buffer) => (output += d.toString()))
      proc.stderr?.on("data", (d: Buffer) => (output += d.toString()))

      const exitCode = await new Promise<number>((resolve) => {
        proc.once("exit", (code) => resolve(code ?? 1))
        proc.once("error", () => resolve(1))
      })

      if (exitCode !== 0) {
        return deps.jsonResponse({ error: "Setup failed", output }, 500)
      }

      const createdMatch = output.match(/Created: (\d+)/)
      const skippedMatch = output.match(/Skipped: (\d+)/)
      return deps.jsonResponse({
        success: true,
        created: createdMatch ? parseInt(createdMatch[1], 10) : 0,
        skipped: skippedMatch ? parseInt(skippedMatch[1], 10) : 0,
        output,
      })
    } catch (error) {
      return deps.jsonResponse(
        { error: error instanceof Error ? error.message : "Setup failed" },
        500
      )
    }
  }

  if (pathname === "/api/workspaces/clear" && method === "POST") {
    try {
      const body = (await req.json()) as { agentId?: string; courseId?: string; all?: boolean }
      const { agentId, courseId, all } = body

      if (agentId && !/^[a-zA-Z0-9_-]+$/.test(agentId)) {
        return deps.jsonResponse(
          {
            error:
              "Invalid agentId: must contain only alphanumeric characters, hyphens, and underscores",
          },
          400
        )
      }
      if (courseId && (courseId.includes("..") || courseId.includes("/") || courseId.includes("\\"))) {
        return deps.jsonResponse({ error: "Invalid courseId" }, 400)
      }

      if (!fs.existsSync(deps.WORKSPACES_DIR)) {
        return deps.jsonResponse({ success: true, cleared: 0 })
      }

      let cleared = 0

      if (all) {
        // Clear everything under workspaces/
        const entries = fs.readdirSync(deps.WORKSPACES_DIR, { withFileTypes: true })
        for (const entry of entries) {
          if (!entry.isDirectory()) continue
          fs.rmSync(path.join(deps.WORKSPACES_DIR, entry.name), { recursive: true })
          cleared++
        }
      } else {
        // Iterate all workspaces (flat and nested) and filter
        for (const { id, dir: wsPath } of iterateWorkspaces(deps.WORKSPACES_DIR)) {
          if (agentId && !id.startsWith(`${agentId}_`)) continue

          if (courseId) {
            const wsYaml = path.join(wsPath, "workspace.yaml")
            if (fs.existsSync(wsYaml)) {
              const content = fs.readFileSync(wsYaml, "utf-8")
              const courseMatch = content.match(/^course:\s*"?([^"\n]+)"?/m)
              if (courseMatch && courseMatch[1] !== courseId) {
                continue
              }
            }
          }

          fs.rmSync(wsPath, { recursive: true })
          cleared++
        }
      }

      return deps.jsonResponse({ success: true, cleared })
    } catch (error) {
      return deps.jsonResponse(
        { error: error instanceof Error ? error.message : "Clear failed" },
        500
      )
    }
  }

  return null
}
