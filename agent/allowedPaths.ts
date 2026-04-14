import * as fs from "fs"
import * as path from "path"
import * as yaml from "js-yaml"
import { getAssignmentNumber, findRubric } from "./workspaceConfig"

interface AllowedPathsOptions {
  workspaceDir?: string
  course: string
  assignmentNumber: number
  assignmentName?: string
  language?: string
}

function getString(value: unknown, fallback = ""): string {
  return typeof value === "string" ? value : fallback
}

function extractPaths(value: unknown): string[] {
  if (!Array.isArray(value)) return []

  const paths: string[] = []
  for (const item of value) {
    if (typeof item === "string") {
      if (item.trim()) paths.push(item)
      continue
    }

    if (item && typeof item === "object") {
      const obj = item as Record<string, unknown>
      const candidate = getString(obj.path || obj.file || obj.name, "")
      if (candidate) paths.push(candidate)
    }
  }

  return paths
}

function normalizePaths(paths: string[]): string[] {
  const seen = new Set<string>()
  const result: string[] = []
  for (const p of paths) {
    const trimmed = p.trim()
    if (!trimmed || seen.has(trimmed)) continue
    seen.add(trimmed)
    result.push(trimmed)
  }
  return result
}

function shouldAllowWriteup(options: AllowedPathsOptions): boolean {
  if (options.language === "proof") return true
  if (options.course) {
    const rubric = findRubric(options.course, options.assignmentNumber, options.assignmentName)
    if (rubric) return true
  }

  if (options.workspaceDir) {
    const writeupMd = path.join(options.workspaceDir, "writeup.md")
    const writeupTxt = path.join(options.workspaceDir, "writeup.txt")
    if (fs.existsSync(writeupMd) || fs.existsSync(writeupTxt)) return true
  }

  return false
}

function addWriteupPaths(paths: string[], workspaceDir?: string): string[] {
  const extras: string[] = []
  let found = false

  if (workspaceDir) {
    const writeupMd = path.join(workspaceDir, "writeup.md")
    const writeupTxt = path.join(workspaceDir, "writeup.txt")
    if (fs.existsSync(writeupMd)) {
      extras.push("writeup.md")
      found = true
    }
    if (fs.existsSync(writeupTxt)) {
      extras.push("writeup.txt")
      found = true
    }
  }

  if (!found) {
    extras.push("writeup.md")
  }

  return normalizePaths([...paths, ...extras])
}

export function loadAllowedEditPaths(options: AllowedPathsOptions): string[] {
  const { course, assignmentNumber, language } = options

  if (!course || !assignmentNumber) return []

  const projectRoot = process.cwd()
  const configsDir = path.join(projectRoot, course, "configs")

  let allowed: string[] = []

  if (fs.existsSync(configsDir)) {
    const files = fs.readdirSync(configsDir).filter((f) => f.endsWith(".yaml"))
    for (const file of files) {
      try {
        const filePath = path.join(configsDir, file)
        const content = fs.readFileSync(filePath, "utf-8")
        const data = yaml.load(content, { schema: yaml.JSON_SCHEMA }) as Record<string, unknown>

        const configNum = getAssignmentNumber(data)
        if (configNum !== assignmentNumber) continue

        const tooling = data.tooling as Record<string, unknown> | undefined
        const toolingAllowed = tooling ? extractPaths(tooling.allowed_paths) : []
        if (toolingAllowed.length > 0) {
          allowed = toolingAllowed
          break
        }

        const rawPaths = [
          ...extractPaths(data.solution_files),
          ...extractPaths(data.implementation_files),
          ...extractPaths(data.source_files),
        ]

        // Convert glob patterns (e.g., "**/*.go") to directory prefixes (e.g., "./")
        // since the path validator does exact/prefix matching, not glob matching.
        allowed = rawPaths.map((p) => {
          if (p.includes("*") || p.includes("?")) {
            // Extract the non-glob prefix, or use "./" for patterns like "**/*.go"
            const prefix = p.replace(/[*?].*$/, "")
            return prefix || "./"
          }
          return p
        })

        // Allow entire source directories so agents can create new files
        // (e.g., Java classes not in the starter code for cumulative homeworks).
        // Extract unique directory prefixes from source_files and allow them.
        const dirPrefixes = new Set<string>()
        for (const p of allowed) {
          // skeleton_code/src/main/rice/obj/APyObj.java -> skeleton_code/src/main/
          if (p.includes("skeleton_code/src/main/")) {
            dirPrefixes.add("skeleton_code/src/main/")
          }
          // src/main/java/edu/rice/... -> src/main/
          if (p.startsWith("src/main/") && !p.includes("skeleton_code")) {
            dirPrefixes.add("src/main/")
          }
          // src/hw01/shape/AShape.java -> src/ (COMP 310 pattern)
          const srcMatch = p.match(/^(src\/[^/]+\/)/)
          if (srcMatch) {
            dirPrefixes.add(srcMatch[1])
          }
        }
        for (const prefix of dirPrefixes) {
          allowed.push(prefix)
        }

        break
      } catch {
        // Skip invalid YAML files
      }
    }
  }

  if (allowed.length === 0) {
    if (language === "python") {
      allowed = ["solution.py"]
    } else {
      // No source_files configured — agent creates all files from scratch
      // (e.g., COMP 421 C labs). Allow edits anywhere in the workspace.
      allowed = ["./"]
    }
  }

  allowed = normalizePaths(allowed)

  // Always allow agent-authored tests under tests/agent.
  // These are included in `test` runs but excluded from final `submit` grading.
  allowed = normalizePaths([...allowed, "tests/agent/"])

  if (shouldAllowWriteup(options)) {
    allowed = addWriteupPaths(allowed, options.workspaceDir)
  }

  return allowed
}
