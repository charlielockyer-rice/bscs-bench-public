/**
 * Tool Factory - Creates tools bound to a specific workspace directory
 *
 * All file operations are sandboxed to the workspace root.
 * Path traversal attempts (../) are blocked.
 */

import { tool } from "ai"
import { z } from "zod"
import * as fs from "fs"
import * as path from "path"
import { spawn } from "child_process"
import { createTwoFilesPatch } from "diff"
import * as yaml from "js-yaml"
import { getAugmentedEnv } from "./config"
import { loadWorkspaceConfigFromFile, isTheoreticalCourse, findRubric, type WorkspaceConfig } from "./workspaceConfig"
import { createTestExecutionBackend } from "./testExecutionBackend"
import { parseTestSummary } from "./gradeParser"


/**
 * Template placeholder patterns used in writeup files.
 * Shared constant to ensure consistency across detection and validation.
 * Keep in sync with bin/_prompts.py TEMPLATE_PLACEHOLDERS.
 */
const TEMPLATE_PLACEHOLDERS = [
  "[Your solution",
  "[Your solutions",
  "[WRITE YOUR ANSWER",
  "[Write your answer",
]

/**
 * Check if content contains any template placeholder patterns.
 */
export function containsTemplatePlaceholder(content: string): boolean {
  return TEMPLATE_PLACEHOLDERS.some(p => content.includes(p))
}

/**
 * Submission record stored in submission.json
 */
interface SubmissionRecord {
  timestamp: string
  /** True once submit is invoked (used for strict one-attempt enforcement). */
  submitAttempted?: boolean
  submitted: boolean
  /** True if a written submission was attempted (regardless of success) */
  writtenSubmissionAttempted?: boolean
  git?: {
    head?: string | null
    status?: string
    clean?: boolean
    error?: string
  }
  codeResults?: {
    passed: number
    total: number
    percentage: number
  }
  writtenResults?: {
    pointsEarned: number
    pointsMax: number
    percentage: number
    problems: unknown[]
  }
  llmGraded: boolean
  combinedScore?: {
    earned: number
    max: number
    percentage: number
  }
}

/**
 * Check if a workspace config represents a theoretical (proof-based) course with no code tests.
 * Checks assignment_mode first (explicit per-assignment override), then falls back to
 * language field and course name for backwards compatibility.
 */
function isTheoreticalWorkspace(config: WorkspaceConfig | null): boolean {
  if (!config) return false
  // Explicit assignment_mode takes precedence over course-level detection
  if (config.assignment_mode === "hybrid" || config.assignment_mode === "code") return false
  if (config.assignment_mode === "written") return true
  return config.language === "proof" || isTheoreticalCourse(config.course)
}


/**
 * Check if a workspace has substantive writeup content (not just template placeholders).
 * Supports both writeup.md and writeup.txt files.
 *
 * @param workspaceRoot - Absolute path to workspace directory
 * @returns True if writeup has meaningful content
 */
function hasWriteupContent(workspaceRoot: string): boolean {
  const writeupFiles = ["writeup.md", "writeup.txt"]

  for (const filename of writeupFiles) {
    const writeupPath = path.join(workspaceRoot, filename)
    if (fs.existsSync(writeupPath)) {
      const content = fs.readFileSync(writeupPath, "utf-8").trim()
      // Check for minimum content length and absence of template placeholders
      if (content.length > 200 && !containsTemplatePlaceholder(content)) {
        return true
      }
    }
  }
  return false
}

/**
 * Check if a workspace has code tests (based on config).
 * Returns false for proof-based (theoretical) courses unless assignment_mode is hybrid/code.
 *
 * @param config - Workspace configuration
 * @returns True if the workspace has code tests
 */
function hasCodeTests(config: WorkspaceConfig | null): boolean {
  if (!config) return false
  return !isTheoreticalWorkspace(config)
}

/**
 * Validates and resolves a path, ensuring it stays within the workspace
 */
function createPathValidator(workspaceRoot: string) {
  const resolvedRoot = path.resolve(workspaceRoot)

  return (inputPath: string): { valid: true; resolved: string } | { valid: false; error: string } => {
    const resolved = path.isAbsolute(inputPath)
      ? path.resolve(inputPath)
      : path.resolve(resolvedRoot, inputPath)

    const normalized = path.normalize(resolved)

    if (!normalized.startsWith(resolvedRoot + path.sep) && normalized !== resolvedRoot) {
      return {
        valid: false,
        error: `Access denied: Path "${inputPath}" resolves outside workspace. Workspace root: ${resolvedRoot}`,
      }
    }

    return { valid: true, resolved: normalized }
  }
}

/**
 * Options for creating tools
 */
export interface CreateToolsOptions {
  /**
   * List of file paths (relative to workspace) that can be edited.
   * If provided, write/edit tools will only allow modifications to these files.
   * If not provided or empty, all files in workspace can be edited.
   */
  allowedEditPaths?: string[]
}

/**
 * Creates a complete toolset bound to a workspace directory
 */
export function createTools(workspaceRoot: string, options: CreateToolsOptions = {}) {
  const validatePath = createPathValidator(workspaceRoot)
  const resolvedRoot = path.resolve(workspaceRoot)
  const testBackend = createTestExecutionBackend(process.cwd())

  // Resolve allowed edit paths to absolute paths for comparison.
  // Preserve trailing "/" on directory prefixes (path.resolve strips them).
  const allowedEditPaths = options.allowedEditPaths?.map(p =>
    p.endsWith("/") ? path.resolve(resolvedRoot, p) + path.sep : path.resolve(resolvedRoot, p)
  ) ?? []

  /**
   * Check if a file path is allowed to be edited
   */
  function isEditAllowed(filepath: string): { allowed: true } | { allowed: false; error: string } {
    // If no restrictions, allow all
    if (allowedEditPaths.length === 0) {
      return { allowed: true }
    }

    const resolved = path.resolve(filepath)
    for (const allowed of allowedEditPaths) {
      if (allowed.endsWith(path.sep) ? resolved.startsWith(allowed) : resolved === allowed) {
        return { allowed: true }
      }
    }

    // Build helpful error message showing allowed files
    const allowedRelative = allowedEditPaths.map(p => path.relative(resolvedRoot, p))
    return {
      allowed: false,
      error: `Cannot edit "${path.relative(resolvedRoot, resolved)}". Only these files can be edited:\n${allowedRelative.map(f => `  - ${f}`).join('\n')}`
    }
  }

  if (!fs.existsSync(resolvedRoot)) {
    fs.mkdirSync(resolvedRoot, { recursive: true })
  }

  /**
   * Verify the workspace has its own .git directory.
   * Without this check, git commands would operate on the parent repository.
   */
  function verifyWorkspaceGit(): { valid: true } | { valid: false; error: string } {
    const gitDir = path.join(resolvedRoot, ".git")
    if (!fs.existsSync(gitDir)) {
      return {
        valid: false,
        error: `Workspace is not a git repository: ${resolvedRoot}. Git commands cannot run without a .git directory.`,
      }
    }
    return { valid: true }
  }

  const gitEnv = {
    ...getAugmentedEnv(),
    GIT_CONFIG_GLOBAL: "/dev/null",
    GIT_CONFIG_SYSTEM: "/dev/null",
    GIT_CONFIG_NOSYSTEM: "1",
    GIT_TERMINAL_PROMPT: "0",
  }

  const gitBaseArgs = ["-c", "core.hooksPath=/dev/null"]

  // ============ READ TOOL ============
  const read = tool({
    description: `Read a file from the workspace.
Paths are relative to workspace root.
Cannot read files outside workspace.`,

    inputSchema: z.object({
      filePath: z.string().describe("Path to file (relative to workspace)"),
      offset: z.number().optional().describe("Line number to start from (0-based)"),
      limit: z.number().optional().describe("Number of lines to read (default 2000)"),
    }),

    execute: async (params) => {
      const result = validatePath(params.filePath)
      if (!result.valid) return `Error: ${result.error}`

      const filepath = result.resolved

      if (!fs.existsSync(filepath)) {
        return `Error: File not found: ${filepath}`
      }

      const stat = fs.statSync(filepath)
      if (stat.isDirectory()) {
        return `Error: Path is a directory: ${filepath}`
      }

      const content = fs.readFileSync(filepath, "utf-8")
      const lines = content.split("\n")

      const offset = params.offset ?? 0
      const limit = params.limit ?? 2000

      const selected = lines.slice(offset, offset + limit).map((line) =>
        line.length > 2000 ? line.slice(0, 2000) + "..." : line
      )

      const formatted = selected.map((line, i) => {
        const num = (i + offset + 1).toString().padStart(5, "0")
        return `${num}| ${line}`
      })

      let output = formatted.join("\n")

      if (lines.length > offset + selected.length) {
        output += `\n\n(${lines.length - offset - selected.length} more lines. Use offset to continue.)`
      }

      return output || "(empty file)"
    },
  })

  // ============ WRITE TOOL ============
  const write = tool({
    description: `Write content to a file in the workspace.
Creates parent directories if needed.
Cannot write outside workspace.
Note: Only allowlisted files can be written/edited.`,

    inputSchema: z.object({
      filePath: z.string().describe("Path to file (relative to workspace)"),
      content: z.string().describe("Content to write"),
    }),

    execute: async (params) => {
      const result = validatePath(params.filePath)
      if (!result.valid) return `Error: ${result.error}`

      const filepath = result.resolved

      // Check if edit is allowed
      const editCheck = isEditAllowed(filepath)
      if (!editCheck.allowed) return `Error: ${editCheck.error}`

      const dir = path.dirname(filepath)

      let oldContent = ""
      const exists = fs.existsSync(filepath)
      if (exists) {
        oldContent = fs.readFileSync(filepath, "utf-8")
      }

      if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true })
      }

      fs.writeFileSync(filepath, params.content)

      const diff = createTwoFilesPatch(filepath, filepath, oldContent, params.content)
      return exists ? `File updated:\n${diff}` : `File created: ${filepath}`
    },
  })

  // ============ EDIT TOOL ============
  const edit = tool({
    description: `Perform string replacement in a file.
Read the file first before editing.
Fails if oldString not found or found multiple times (use replaceAll for multiple).
Note: Only allowlisted files can be written/edited.`,

    inputSchema: z.object({
      filePath: z.string().describe("Path to file"),
      oldString: z.string().describe("Text to replace"),
      newString: z.string().describe("Replacement text"),
      replaceAll: z.boolean().optional().describe("Replace all occurrences"),
    }),

    execute: async (params) => {
      if (params.oldString === params.newString) {
        return "Error: oldString and newString must be different"
      }

      const result = validatePath(params.filePath)
      if (!result.valid) return `Error: ${result.error}`

      const filepath = result.resolved

      // Check if edit is allowed
      const editCheck = isEditAllowed(filepath)
      if (!editCheck.allowed) return `Error: ${editCheck.error}`

      if (!fs.existsSync(filepath)) {
        return `Error: File not found: ${filepath}`
      }

      const oldContent = fs.readFileSync(filepath, "utf-8")

      if (!oldContent.includes(params.oldString)) {
        return "Error: oldString not found in file"
      }

      const count = oldContent.split(params.oldString).length - 1
      if (count > 1 && !params.replaceAll) {
        return `Error: Found ${count} matches. Provide more context or use replaceAll.`
      }

      const newContent = params.replaceAll
        ? oldContent.replaceAll(params.oldString, params.newString)
        : oldContent.replace(params.oldString, params.newString)

      fs.writeFileSync(filepath, newContent)

      const diff = createTwoFilesPatch(filepath, filepath, oldContent, newContent)
      return diff
    },
  })

  // ============ GLOB TOOL ============
  const glob = tool({
    description: `Find files by pattern in the workspace.
Supports patterns like "**/*.ts", "src/**/*.js".
Returns paths sorted by modification time.`,

    inputSchema: z.object({
      pattern: z.string().describe("Glob pattern to match"),
      subpath: z.string().optional().describe("Subdirectory to search in"),
    }),

    execute: async (params) => {
      let searchPath = resolvedRoot

      if (params.subpath) {
        const result = validatePath(params.subpath)
        if (!result.valid) return `Error: ${result.error}`
        searchPath = result.resolved
      }

      const files: { path: string; mtime: number }[] = []
      const limit = 100

      const g = new Bun.Glob(params.pattern)

      for await (const file of g.scan({ cwd: searchPath, absolute: true, onlyFiles: true })) {
        if (files.length >= limit) break
        try {
          const stat = fs.statSync(file)
          files.push({ path: file, mtime: stat.mtime.getTime() })
        } catch {
          // skip
        }
      }

      files.sort((a, b) => b.mtime - a.mtime)

      if (files.length === 0) return "No files found"

      let output = files.map((f) => f.path).join("\n")
      if (files.length >= limit) {
        output += "\n\n(Results limited to 100. Use a more specific pattern.)"
      }

      return output
    },
  })

  // ============ GREP TOOL ============
  const grep = tool({
    description: `Search file contents in the workspace.
Uses regex patterns.
Filter by file pattern with include parameter.`,

    inputSchema: z.object({
      pattern: z.string().describe("Regex pattern to search for"),
      subpath: z.string().optional().describe("Subdirectory to search in"),
      include: z.string().optional().describe("File pattern to include (e.g. '*.ts')"),
    }),

    execute: async (params) => {
      let searchPath = resolvedRoot

      if (params.subpath) {
        const result = validatePath(params.subpath)
        if (!result.valid) return `Error: ${result.error}`
        searchPath = result.resolved
      }

      // Try ripgrep first
      try {
        const args = ["-nH", "--regexp", params.pattern]
        if (params.include) args.push("--glob", params.include)
        args.push(searchPath)

        const proc = Bun.spawn(["rg", ...args], { stdout: "pipe", stderr: "pipe" })
        const output = await new Response(proc.stdout).text()
        const exitCode = await proc.exited

        if (exitCode === 1) return "No matches found"
        if (exitCode === 0 && output) {
          const lines = output.trim().split("\n").slice(0, 100)
          return lines.join("\n") + (lines.length >= 100 ? "\n\n(truncated)" : "")
        }
      } catch {
        // ripgrep not available, fall through
      }

      // Fallback: simple search
      const regex = new RegExp(params.pattern)
      const matches: string[] = []

      function walk(dir: string) {
        const entries = fs.readdirSync(dir, { withFileTypes: true })
        for (const entry of entries) {
          if (entry.name.startsWith(".") || entry.name === "node_modules") continue

          const full = path.join(dir, entry.name)
          if (entry.isDirectory()) {
            walk(full)
          } else if (entry.isFile()) {
            if (params.include && !new Bun.Glob(params.include).match(entry.name)) continue
            try {
              const content = fs.readFileSync(full, "utf-8")
              const lines = content.split("\n")
              for (let i = 0; i < lines.length && matches.length < 100; i++) {
                if (regex.test(lines[i])) {
                  matches.push(`${full}:${i + 1}:${lines[i].slice(0, 200)}`)
                }
              }
            } catch {
              // skip unreadable
            }
          }
        }
      }

      walk(searchPath)

      if (matches.length === 0) return "No matches found"
      return matches.join("\n") + (matches.length >= 100 ? "\n\n(truncated)" : "")
    },
  })

  // ============ TEST TOOL ============
  const test = tool({
    description: `Run the basic test suite on your solution. This is THE way to test your code during development.

What it does:
- Automatically compiles your code (Java/C) or loads your module (Python)
- Runs the PUBLIC suite plus any tests you add under tests/agent
- Returns detailed results showing which tests pass/fail with error messages

How to use: Just call this tool with no arguments. That's it.

DO NOT manually compile or run tests yourself - this tool handles everything.
After implementing code, call test to see results, then fix failures and repeat.`,

    inputSchema: z.object({}),

    execute: async () => {
      return testBackend.runTest(resolvedRoot, "test")
    },
  })

  // ============ GIT_COMMIT TOOL ============
  const git_commit = tool({
    description: `Commit your current changes to save progress.
Use this after making significant progress or passing tests.
Changes are staged automatically before commit.`,

    inputSchema: z.object({
      message: z.string().describe("Commit message describing what you accomplished"),
    }),

    execute: async (params) => {
      // Verify workspace has its own .git directory
      const gitCheck = verifyWorkspaceGit()
      if (!gitCheck.valid) return `Error: ${gitCheck.error}`

      // Stage all changes first
      const addProc = spawn("git", ["add", "-A"], {
        cwd: resolvedRoot,
        env: gitEnv,
        stdio: ["ignore", "pipe", "pipe"],
      })

      let addError = ""
      await new Promise<void>((resolve) => {
        addProc.once("exit", resolve)
        addProc.once("error", (err) => {
          addError = err.message
          resolve()
        })
      })

      if (addError) {
        return `Failed to stage changes: ${addError}`
      }

      // Then commit (using array args to avoid shell injection)
      const commitProc = spawn("git", [...gitBaseArgs, "commit", "-m", params.message], {
        cwd: resolvedRoot,
        env: gitEnv,
        stdio: ["ignore", "pipe", "pipe"],
      })

      let output = ""
      commitProc.stdout?.on("data", (chunk) => (output += chunk.toString()))
      commitProc.stderr?.on("data", (chunk) => (output += chunk.toString()))

      const exitCode = await new Promise<number>((resolve) => {
        commitProc.once("exit", (code) => resolve(code ?? 1))
        commitProc.once("error", () => resolve(1))
      })

      if (exitCode === 0) {
        return `Committed: ${params.message}`
      } else if (output.includes("nothing to commit")) {
        return "Nothing to commit - working tree is clean"
      } else {
        return `Commit failed: ${output}`
      }
    },
  })

  // ============ GIT_STATUS TOOL ============
  const git_status = tool({
    description: `Show the current git status - which files are modified, staged, or untracked.`,

    inputSchema: z.object({}),

    execute: async () => {
      // Verify workspace has its own .git directory
      const gitCheck = verifyWorkspaceGit()
      if (!gitCheck.valid) return `Error: ${gitCheck.error}`

      const proc = spawn("git", [...gitBaseArgs, "status", "--short"], {
        cwd: resolvedRoot,
        env: gitEnv,
        stdio: ["ignore", "pipe", "pipe"],
      })

      let output = ""
      proc.stdout?.on("data", (chunk) => (output += chunk.toString()))
      proc.stderr?.on("data", (chunk) => (output += chunk.toString()))

      await new Promise<void>((resolve) => {
        proc.once("exit", resolve)
        proc.once("error", (err) => {
          output += `\nProcess error: ${err.message}`
          resolve()
        })
      })

      return output.trim() || "No changes - working tree is clean"
    },
  })

  // ============ GIT_DIFF TOOL ============
  const git_diff = tool({
    description: `Show the diff of uncommitted changes. Use to review what you've modified.`,

    inputSchema: z.object({
      file: z.string().optional().describe("Specific file to diff (optional, defaults to all)"),
    }),

    execute: async (params) => {
      // Verify workspace has its own .git directory
      const gitCheck = verifyWorkspaceGit()
      if (!gitCheck.valid) return `Error: ${gitCheck.error}`

      // Validate file path if provided
      if (params.file) {
        const result = validatePath(params.file)
        if (!result.valid) return `Error: ${result.error}`
      }

      // Build args array (avoids shell injection)
      const args = [...gitBaseArgs, "diff"]
      if (params.file) args.push("--", params.file)

      const proc = spawn("git", args, {
        cwd: resolvedRoot,
        env: gitEnv,
        stdio: ["ignore", "pipe", "pipe"],
      })

      let output = ""
      proc.stdout?.on("data", (chunk) => (output += chunk.toString()))
      proc.stderr?.on("data", (chunk) => (output += chunk.toString()))

      await new Promise<void>((resolve) => {
        proc.once("exit", resolve)
        proc.once("error", (err) => {
          output += `\nProcess error: ${err.message}`
          resolve()
        })
      })

      // Truncate if too long
      const MAX_OUTPUT = 10000
      if (output.length > MAX_OUTPUT) {
        output = output.slice(0, MAX_OUTPUT) + "\n\n(output truncated)"
      }

      return output.trim() || "No changes"
    },
  })

  // ============ SUBMIT TOOL ============
  const submit = tool({
    description: `Mark your work as submitted (one attempt total). Final grading happens after submission.

⚠️ IMPORTANT - READ CAREFULLY:
- You get only ONE submission attempt total
- Make sure ALL problems are answered before submitting
- For code, verify tests pass with the test tool first

Call this tool only when you are confident your work is complete.`,

    inputSchema: z.object({}),

    execute: async () => {
      const submissionPath = path.join(resolvedRoot, "submission.json")

      // One attempt only (global submit lock)
      if (fs.existsSync(submissionPath)) {
        try {
          const existing = JSON.parse(fs.readFileSync(submissionPath, "utf-8")) as {
            submitted?: boolean
          }
          if (existing.submitted) {
            return "ERROR: This workspace has already been submitted. You cannot resubmit."
          }
        } catch {
          // Ignore parse errors, continue with fresh submission.
        }
      }

      fs.writeFileSync(
        submissionPath,
        JSON.stringify({
          timestamp: new Date().toISOString(),
          submitted: true,
        }, null, 2),
      )

      return "Submission recorded. Your work has been marked as final."
    },
  })

  // ============ MALLOC_PERF TOOL ============
  const malloc_perf = tool({
    description: `Run the malloc performance benchmark. FOR MALLOC LAB ONLY.

This tool runs mdriver with ALL trace files and reports your aggregate performance index.
Use this AFTER all correctness tests pass via the test tool.

The performance index (0-100) is calculated as:
- 40 points: Memory utilization (how efficiently you use heap space)
- 60 points: Throughput (operations per second, capped at libc performance)

Output includes:
- Per-trace correctness verification
- Per-trace utilization and throughput
- Aggregate performance index

Target: 90/100 or higher for a well-optimized implementation.

DO NOT use this tool until the test tool shows all tests passing.`,

    inputSchema: z.object({}),

    execute: async () => {
      // Check if this is a malloc workspace
      const mmcPath = path.join(resolvedRoot, "mm.c")

      if (!fs.existsSync(mmcPath)) {
        return "Error: This tool is only for the malloc lab. No mm.c found in workspace."
      }

      const runnerPath = path.resolve(process.cwd(), "comp321", "runner.py")
      if (!fs.existsSync(runnerPath)) {
        return `Error: comp321 runner not found at ${runnerPath}`
      }

      const proc = spawn("python3", [runnerPath, "malloc", resolvedRoot, "--perf", "--json"], {
        cwd: process.cwd(),
        env: getAugmentedEnv(),
        stdio: ["ignore", "pipe", "pipe"],
      })

      let output = ""
      proc.stdout?.on("data", (d) => output += d.toString())
      proc.stderr?.on("data", (d) => output += d.toString())

      const timer = setTimeout(() => {
        proc.kill()
      }, 120000) // 2 minute timeout

      await new Promise<void>((resolve) => {
        proc.once("exit", resolve)
        proc.once("error", (err) => {
          output += `\nProcess error: ${err.message}`
          resolve()
        })
      })

      clearTimeout(timer)

      if (!output) return "(no output)"

      try {
        const parsed = JSON.parse(output) as {
          raw?: string
        }
        if (parsed && typeof parsed.raw === "string") {
          return parsed.raw
        }
      } catch {
        // Fall through to raw output
      }

      return output
    },
  })

  return {
    read,
    write,
    edit,
    glob,
    grep,
    test,
    submit,
    malloc_perf,
    git_commit,
    git_status,
    git_diff,
  }
}

/**
 * Callbacks for tool instrumentation
 */
export interface ToolCallbacks {
  onToolStart?: (toolName: string, args: Record<string, unknown>) => void
  onToolEnd?: (toolName: string, result: string) => void
}

/**
 * Creates tools with callback instrumentation for the streaming server.
 * Wraps each tool to emit start/end events.
 */
export function createToolsWithCallbacks(
  workspaceRoot: string,
  callbacks: ToolCallbacks,
  options: CreateToolsOptions = {}
) {
  const baseTools = createTools(workspaceRoot, options)

  /**
   * Wraps a tool to add callback instrumentation.
   * Note: Uses Record<string, unknown> and cast back to T because AI SDK tool types
   * are complex and we only need to intercept the execute function.
   */
  function wrapTool<T extends Record<string, unknown>>(
    name: string,
    baseTool: T
  ): T {
    const originalExecute = (baseTool as any).execute
    if (!originalExecute) {
      return baseTool
    }
    return {
      ...baseTool,
      execute: async (params: Record<string, unknown>, options: unknown) => {
        callbacks.onToolStart?.(name, params)
        try {
          const result = await originalExecute.call(baseTool, params, options)
          const resultStr = typeof result === "string" ? result : String(result)
          callbacks.onToolEnd?.(name, resultStr)
          return result
        } catch (error) {
          const errorMsg = error instanceof Error ? error.message : String(error)
          callbacks.onToolEnd?.(name, `Error: ${errorMsg}`)
          throw error
        }
      },
    } as T
  }

  return {
    read: wrapTool("read", baseTools.read),
    write: wrapTool("write", baseTools.write),
    edit: wrapTool("edit", baseTools.edit),
    glob: wrapTool("glob", baseTools.glob),
    grep: wrapTool("grep", baseTools.grep),
    test: wrapTool("test", baseTools.test),
    submit: wrapTool("submit", baseTools.submit),
    malloc_perf: wrapTool("malloc_perf", baseTools.malloc_perf),
    git_commit: wrapTool("git_commit", baseTools.git_commit),
    git_status: wrapTool("git_status", baseTools.git_status),
    git_diff: wrapTool("git_diff", baseTools.git_diff),
  }
}
