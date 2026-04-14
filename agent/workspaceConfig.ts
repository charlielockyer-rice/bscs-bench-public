import * as fs from "fs"
import * as path from "path"
import * as yaml from "js-yaml"
import { execSync } from "child_process"

export interface WorkspaceConfig {
  module_number: number
  module_name: string
  assignment_number: number
  assignment_name: string
  display_name: string
  course: string
  language?: string
  assignment_mode?: "code" | "hybrid" | "written"
  created_at?: string
  /** Optional workspace name that this workspace depends on (for sequential assignments) */
  depends_on?: string
}

function getNumber(value: unknown): number | null {
  return typeof value === "number" ? value : null
}

function getString(value: unknown): string | null {
  return typeof value === "string" ? value : null
}

/**
 * Extract the assignment number from config data using the unified schema.
 * Supports: assignment_number (unified), module_number, homework_number, project_number (legacy)
 */
export function getAssignmentNumber(data: Record<string, unknown>): number | null {
  return (
    getNumber(data.assignment_number) ??
    getNumber(data.module_number) ??
    getNumber(data.homework_number) ??
    getNumber(data.project_number)
  )
}

function resolveAssignmentName(data: Record<string, unknown>): string | null {
  return (
    getString(data.assignment_name) ??
    getString(data.module_name) ??
    getString(data.homework_name) ??
    getString(data.project_name)
  )
}

export function normalizeWorkspaceConfig(data: Record<string, unknown>): WorkspaceConfig | null {
  const assignment_number = getAssignmentNumber(data)
  const assignment_name = resolveAssignmentName(data)
  const course = getString(data.course)

  if (!assignment_number || !assignment_name || !course) {
    return null
  }

  const display_name =
    getString(data.display_name) ??
    assignment_name

  return {
    module_number: assignment_number,
    module_name: assignment_name,
    assignment_number,
    assignment_name,
    display_name,
    course,
    language: getString(data.language) ?? undefined,
    assignment_mode: getString(data.assignment_mode) as WorkspaceConfig["assignment_mode"] | undefined,
    created_at: getString(data.created_at) ?? undefined,
    depends_on: getString(data.depends_on) ?? undefined,
  }
}

/**
 * Load workspace config from a workspace directory.
 * Tries workspace.yaml first, then workspace.json as fallback.
 */
export function loadWorkspaceConfigFromFile(workspaceRoot: string): WorkspaceConfig | null {
  const yamlPath = path.join(workspaceRoot, "workspace.yaml")
  const jsonPath = path.join(workspaceRoot, "workspace.json")

  try {
    const content = fs.readFileSync(yamlPath, "utf-8")
    const parsed = yaml.load(content, { schema: yaml.JSON_SCHEMA }) as Record<string, unknown>
    return normalizeWorkspaceConfig(parsed)
  } catch {
    // Fall through to JSON
  }

  try {
    const content = fs.readFileSync(jsonPath, "utf-8")
    const parsed = JSON.parse(content) as Record<string, unknown>
    return normalizeWorkspaceConfig(parsed)
  } catch {
    return null
  }
}

/**
 * Courses that are theoretical (proof-based with no code tests).
 * These rely on LLM grading rather than unit tests.
 */
export const THEORETICAL_COURSES = new Set(["comp182", "comp382"])

export function isTheoreticalCourse(course: string): boolean {
  return THEORETICAL_COURSES.has(course)
}

/**
 * Find a rubric file for an assignment in the course directory.
 * Checks multiple naming conventions used by different courses.
 *
 * @param course - Course identifier (e.g., "comp140", "comp182")
 * @param assignmentNumber - Assignment/module/homework number
 * @param assignmentName - Optional assignment name for name-based rubrics
 * @returns Absolute path to rubric file, or null if not found
 */
export function findRubric(course: string, assignmentNumber: number, assignmentName?: string): string | null {
  const courseDir = path.join(process.cwd(), course)
  const rubricsDir = path.join(courseDir, "rubrics")

  const candidates: string[] = []

  if (assignmentName) {
    candidates.push(`${assignmentName}_rubric.md`)
  }
  candidates.push(
    `module${assignmentNumber}_rubric.md`,
    `hw${assignmentNumber}_rubric.md`,
    `homework${assignmentNumber}_rubric.md`,
    `assignment${assignmentNumber}_rubric.md`
  )

  for (const candidate of candidates) {
    const rubricPath = path.join(rubricsDir, candidate)
    if (fs.existsSync(rubricPath)) return rubricPath
  }

  return null
}

/** Files/directories to skip when copying from a dependency workspace */
const DEPENDENCY_SKIP_FILES = new Set([
  "workspace.yaml",
  "workspace.json",
  "last_grade.json",
  "last_test.json",
  "last_test_meta.json",
  "last_stream.json",
  "last_perf.json",
  "attempts.json",
  "submission.json",
  ".gitignore",
  ".mcp.json",
  ".codex/",
])

/**
 * Result of resolving a workspace dependency.
 */
export interface DependencyResult {
  /** Whether the dependency was resolved and files were copied */
  resolved: boolean
  /** Number of files copied */
  filesCopied: number
  /** Human-readable message about the resolution */
  message: string
}

/**
 * Resolve a workspace's depends_on dependency by copying the prior workspace's
 * solution files into a previous_solution/ subdirectory.
 *
 * This is idempotent: if previous_solution/ already exists with content, it's a no-op.
 * Files are committed into the workspace's git repo so the agent can see them.
 *
 * @param workspaceDir - Absolute path to the current workspace
 * @param workspacesDir - Absolute path to the workspaces/ directory
 * @returns Result describing what happened
 */
export function resolveDependency(workspaceDir: string, workspacesDir: string): DependencyResult {
  const config = loadWorkspaceConfigFromFile(workspaceDir)
  if (!config?.depends_on) {
    return { resolved: false, filesCopied: 0, message: "No depends_on field" }
  }

  const depName = config.depends_on
  const depDir = path.join(workspacesDir, depName)

  if (!fs.existsSync(depDir)) {
    return {
      resolved: false,
      filesCopied: 0,
      message: `Dependency workspace not found: ${depName}`,
    }
  }

  // Idempotent: skip if previous_solution/ already has content
  const prevDir = path.join(workspaceDir, "previous_solution")
  if (fs.existsSync(prevDir)) {
    const entries = fs.readdirSync(prevDir)
    if (entries.length > 0) {
      return { resolved: true, filesCopied: 0, message: "previous_solution/ already populated" }
    }
  }

  // Verify dependency workspace is a git repo
  const depGit = path.join(depDir, ".git")
  if (!fs.existsSync(depGit)) {
    return {
      resolved: false,
      filesCopied: 0,
      message: `Dependency workspace ${depName} is not a git repository`,
    }
  }

  // Find the initial commit
  let initialCommit: string
  try {
    initialCommit = execSync("git rev-list --max-parents=0 HEAD", {
      cwd: depDir,
      encoding: "utf-8",
    }).trim().split("\n")[0]
  } catch {
    return {
      resolved: false,
      filesCopied: 0,
      message: `Failed to find initial commit in ${depName}`,
    }
  }

  if (!/^[0-9a-f]{40}$/i.test(initialCommit)) {
    return {
      resolved: false,
      filesCopied: 0,
      message: `Invalid initial commit hash in ${depName}: ${initialCommit}`,
    }
  }

  // Get files changed since initial commit
  let changedFiles: string[]
  try {
    const output = execSync(`git diff --name-only ${initialCommit} HEAD`, {
      cwd: depDir,
      encoding: "utf-8",
    })
    changedFiles = output.trim().split("\n").filter((f) => f.trim())
  } catch {
    return {
      resolved: false,
      filesCopied: 0,
      message: `Failed to diff against initial commit in ${depName}`,
    }
  }

  if (changedFiles.length === 0) {
    return {
      resolved: false,
      filesCopied: 0,
      message: `No agent changes found in dependency ${depName}`,
    }
  }

  // Copy changed files into previous_solution/
  fs.mkdirSync(prevDir, { recursive: true })
  let copied = 0

  for (const fname of changedFiles) {
    if (DEPENDENCY_SKIP_FILES.has(fname) || fname.startsWith(".codex/") || fname.startsWith("previous_solution/")) continue

    const src = path.join(depDir, fname)
    if (!fs.existsSync(src) || fs.statSync(src).isDirectory()) continue

    const dest = path.join(prevDir, fname)
    fs.mkdirSync(path.dirname(dest), { recursive: true })
    fs.copyFileSync(src, dest)
    copied++
  }

  if (copied === 0) {
    return {
      resolved: false,
      filesCopied: 0,
      message: `No solution files to copy from ${depName}`,
    }
  }

  // Commit into workspace git repo so agent can see the files
  const wsGit = path.join(workspaceDir, ".git")
  if (fs.existsSync(wsGit)) {
    try {
      execSync("git add previous_solution/", { cwd: workspaceDir, encoding: "utf-8" })
      execSync(`git commit -m "Add previous solution from ${depName}"`, {
        cwd: workspaceDir,
        encoding: "utf-8",
      })
    } catch {
      // Commit may fail if nothing to commit (e.g., files are identical)
    }
  }

  return {
    resolved: true,
    filesCopied: copied,
    message: `Copied ${copied} file(s) from ${depName} into previous_solution/`,
  }
}
