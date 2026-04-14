import * as fs from "fs"
import * as path from "path"
import * as yaml from "js-yaml"
import { loadWorkspaceConfigFromFile, isTheoreticalCourse } from "./workspaceConfig"

/**
 * Workspace metadata from workspace.yaml/workspace.json
 */
export interface WorkspaceInfo {
  module_number: number
  module_name: string
  assignment_number: number
  assignment_name: string
  display_name: string
  course: string
  language?: string
  assignment_mode?: "code" | "hybrid" | "written"
  /** Optional workspace name that this workspace depends on (for sequential assignments) */
  depends_on?: string
}

/**
 * Reads workspace.yaml to get course and module info
 */
export function readWorkspaceInfo(workspaceDir: string): WorkspaceInfo | null {
  const config = loadWorkspaceConfigFromFile(workspaceDir)
  if (!config) return null

  return {
    module_number: config.module_number,
    module_name: config.module_name,
    assignment_number: config.assignment_number,
    assignment_name: config.assignment_name,
    display_name: config.display_name,
    course: config.course,
    language: config.language,
    assignment_mode: config.assignment_mode,
    depends_on: config.depends_on,
  }
}

/**
 * Project config with benchmarking settings
 */
export interface ProjectConfig {
  project_name: string
  language: "python" | "java" | "c" | "go" | "typescript"
  benchmarking?: {
    mode: "correctness" | "performance"
    performance_threshold?: number
  }
}

/**
 * Reads project config from course configs directory
 */
export function readProjectConfig(courseDir: string, moduleName: string): ProjectConfig | null {
  const coursePath = path.resolve(courseDir)
  const configPath = path.join(coursePath, "configs", `${moduleName}.yaml`)

  if (fs.existsSync(configPath)) {
    try {
      const content = fs.readFileSync(configPath, "utf-8")
      const parsed = yaml.load(content, { schema: yaml.JSON_SCHEMA }) as Record<string, unknown>
      return {
        project_name: (parsed.project_name as string) || moduleName,
        language: (parsed.language as "python" | "java" | "c" | "go" | "typescript") || "python",
        benchmarking: parsed.benchmarking as ProjectConfig["benchmarking"],
      }
    } catch {
      return null
    }
  }
  return null
}

/**
 * Safely parses a YAML config file and extracts the language field.
 * Returns null if parsing fails or language is not present.
 */
function parseConfigLanguage(configPath: string): "python" | "java" | "c" | "go" | "typescript" | null {
  try {
    const content = fs.readFileSync(configPath, "utf-8")
    const parsed = yaml.load(content, { schema: yaml.JSON_SCHEMA }) as Record<string, unknown>
    if (parsed && typeof parsed.language === "string") {
      const lang = parsed.language.toLowerCase()
      if (lang === "c" || lang === "java" || lang === "python" || lang === "go" || lang === "typescript") {
        return lang
      }
    }
  } catch {
    // Ignore parsing errors, return null
  }
  return null
}

/**
 * Detects the language of a course based on config files
 */
export function detectCourseLanguage(
  courseDir: string,
  moduleNumber: number,
  moduleName?: string
): "python" | "java" | "c" | "go" | "typescript" | "proof" {
  const coursePath = path.resolve(courseDir)

  // Check if this is a theoretical course based on directory name,
  // but first check per-assignment config for hybrid/code overrides
  const courseName = path.basename(coursePath)
  if (isTheoreticalCourse(courseName)) {
    // Check if this specific assignment overrides the course-level language
    const configName = moduleName ? `${moduleName}.yaml` : `homework${moduleNumber}.yaml`
    const assignmentConfig = path.join(coursePath, "configs", configName)
    if (fs.existsSync(assignmentConfig)) {
      const lang = parseConfigLanguage(assignmentConfig)
      if (lang) return lang
    }
    return "proof"
  }

  // If module name provided, check for project config (C courses like COMP321)
  if (moduleName) {
    const projectConfig = path.join(coursePath, "configs", `${moduleName}.yaml`)
    if (fs.existsSync(projectConfig)) {
      const lang = parseConfigLanguage(projectConfig)
      if (lang) {
        return lang
      }
    }
  }

  // Check for homework config (Java courses like COMP215)
  const hwConfig = path.join(coursePath, "configs", `homework${moduleNumber}.yaml`)
  if (fs.existsSync(hwConfig)) {
    const lang = parseConfigLanguage(hwConfig)
    if (lang) {
      return lang
    }
  }

  // Check for module config (Python courses like COMP140)
  const modConfig = path.join(coursePath, "configs", `module${moduleNumber}.yaml`)
  if (fs.existsSync(modConfig)) {
    return "python"
  }

  // Fallback: check if course has its own runner.py (Java courses)
  const runnerPath = path.join(coursePath, "runner.py")
  if (fs.existsSync(runnerPath)) {
    // Double-check by looking for any homework config with java
    const configsDir = path.join(coursePath, "configs")
    if (fs.existsSync(configsDir)) {
      const files = fs.readdirSync(configsDir)
      for (const file of files) {
        if (file.startsWith("homework") && file.endsWith(".yaml")) {
          const lang = parseConfigLanguage(path.join(configsDir, file))
          if (lang === "java") {
            return "java"
          }
        }
      }
    }
  }

  return "python"
}

/**
 * Resolved workspace context with all relevant info
 */
export interface WorkspaceContext {
  language: "python" | "java" | "c" | "go" | "typescript" | "proof"
  moduleNumber: number
  moduleName: string
  course: string
  assignmentMode?: "code" | "hybrid" | "written"
  projectConfig: ProjectConfig | null
  /** For performance-based benchmarks, the target score (default 90) */
  performanceThreshold?: number
  /** Whether this is a performance-based benchmark */
  isPerformanceBenchmark: boolean
}

/**
 * Resolves workspace context (language and module number) from workspace.yaml
 */
export function resolveWorkspaceContext(workspaceDir: string): WorkspaceContext {
  const workspaceInfo = readWorkspaceInfo(workspaceDir)
  let language: "python" | "java" | "c" | "go" | "typescript" | "proof" = "python"
  let moduleNumber = 1
  let moduleName = ""
  let course = ""
  let projectConfig: ProjectConfig | null = null

  if (workspaceInfo) {
    moduleNumber = workspaceInfo.module_number
    moduleName = workspaceInfo.module_name
    course = workspaceInfo.course
    // Walk up to find the project root (contains the course directory).
    // Flat layout: workspaces/<ws>/  → parent.parent
    // Model-dir layout: workspaces/<model>/<ws>/ → parent.parent.parent
    let projectRoot = path.dirname(path.dirname(workspaceDir))
    let courseDir = path.join(projectRoot, workspaceInfo.course)
    if (!fs.existsSync(courseDir)) {
      projectRoot = path.dirname(projectRoot)
      courseDir = path.join(projectRoot, workspaceInfo.course)
    }
    if (fs.existsSync(courseDir)) {
      language = detectCourseLanguage(courseDir, moduleNumber, moduleName)
      projectConfig = readProjectConfig(courseDir, moduleName)
    }
  }

  const isPerformanceBenchmark = projectConfig?.benchmarking?.mode === "performance"
  const performanceThreshold = projectConfig?.benchmarking?.performance_threshold ?? 90

  return {
    language,
    moduleNumber,
    moduleName,
    course,
    assignmentMode: workspaceInfo?.assignment_mode as WorkspaceContext["assignmentMode"],
    projectConfig,
    performanceThreshold,
    isPerformanceBenchmark,
  }
}
