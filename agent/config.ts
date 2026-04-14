/**
 * Agent configuration
 *
 * Central config for agent harness settings.
 * Change defaults here instead of in multiple files.
 */

import { existsSync } from "fs"
import { join } from "path"
import { DEFAULT_MAX_STEPS } from "../shared/benchContracts"
import { createOpenRouter } from "@openrouter/ai-sdk-provider"

export const config = {
  /** Default OpenRouter model ID */
  defaultModel: "minimax/minimax-m2.5",

  /** Maximum steps before stopping */
  defaultMaxSteps: DEFAULT_MAX_STEPS,

  /** Default course directory */
  defaultCourseDir: "./comp140",

  /** Maximum compilation error message length to display in UI */
  maxCompilationErrorLength: 500,

  /** Minimum percentage of points for a problem to be considered "passed" in LLM grading */
  llmGradingPassThreshold: 0.6,
}

/**
 * Build an augmented PATH string with additional tool paths.
 *
 * Priority order:
 * 1. JAVA_HOME/bin (if JAVA_HOME is set and the bin directory exists)
 * 2. EXTRA_PATH (custom paths, colon-separated)
 * 3. Homebrew paths (only if they exist on the filesystem)
 * 4. Original PATH
 *
 * This makes the function work cross-platform:
 * - On Linux, set JAVA_HOME or EXTRA_PATH as needed
 * - On macOS with Homebrew, paths are auto-detected
 * - On macOS without Homebrew, use JAVA_HOME or EXTRA_PATH
 */
function buildAugmentedPath(): string {
  const pathParts: string[] = []

  // 1. Add JAVA_HOME/bin if JAVA_HOME is set and valid
  const javaHome = process.env.JAVA_HOME
  if (javaHome) {
    const javaBin = join(javaHome, "bin")
    if (existsSync(javaBin)) {
      pathParts.push(javaBin)
    }
  }

  // 2. Add EXTRA_PATH if set (allows custom paths without modifying code)
  const extraPath = process.env.EXTRA_PATH
  if (extraPath) {
    pathParts.push(extraPath)
  }

  // 3. Add Homebrew paths only if they exist on the filesystem
  const homebrewPaths = [
    "/opt/homebrew/opt/openjdk@21/bin",
    "/opt/homebrew/bin",
    // Intel Mac Homebrew location
    "/usr/local/opt/openjdk@21/bin",
    "/usr/local/bin",
  ]

  for (const brewPath of homebrewPaths) {
    if (existsSync(brewPath)) {
      pathParts.push(brewPath)
    }
  }

  // 4. Append original PATH
  const originalPath = process.env.PATH || ""
  if (originalPath) {
    pathParts.push(originalPath)
  }

  return pathParts.join(":")
}

/**
 * Get environment with augmented PATH for tool availability (Java, etc.)
 * Used by spawned processes that need access to installed tools.
 *
 * Cross-platform support:
 * - Set JAVA_HOME to specify Java installation location
 * - Set EXTRA_PATH to add custom paths (colon-separated)
 * - Homebrew paths are auto-detected if present
 */
export function getAugmentedEnv(): Record<string, string | undefined> {
  return {
    ...process.env,
    PATH: buildAugmentedPath(),
  }
}

/**
 * Create an OpenRouter client, requiring OPENROUTER_API_KEY to be set.
 * Centralised here so that all callers share the same validation logic.
 * Cached after first call (API key doesn't change during process lifetime).
 */
let _openRouterClient: ReturnType<typeof createOpenRouter> | null = null
export function getOpenRouter() {
  if (_openRouterClient) return _openRouterClient
  const apiKey = process.env.OPENROUTER_API_KEY
  if (!apiKey) {
    throw new Error("OPENROUTER_API_KEY environment variable is required")
  }
  _openRouterClient = createOpenRouter({ apiKey })
  return _openRouterClient
}
