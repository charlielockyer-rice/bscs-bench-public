/**
 * Bridge to the Python prompt builder (bin/_prompts.py).
 *
 * This module delegates prompt generation to the single Python source of truth,
 * eliminating duplicated prompt logic across TypeScript and Python.
 *
 * All functions are synchronous (execFileSync) since prompts are generated
 * during setup, not on the hot path.
 */

import { execFileSync } from "child_process"
import * as path from "path"

/** Safety timeout for the Python subprocess (10 seconds). */
const SUBPROCESS_TIMEOUT_MS = 10_000

export interface WorkspacePromptInput {
  dir: string
  course: string
  language?: string
  name?: string
  assignment_number: number
  display_name?: string
  assignment_mode?: string
}

/**
 * Call the Python prompt CLI with a command and workspace descriptor.
 *
 * Uses execFileSync (not execSync) to avoid shell injection.
 * stderr is inherited so Python tracebacks surface during debugging.
 * Computes project root at call time (not module load time) for robustness.
 */
function callPromptsCli(command: string, ws: WorkspacePromptInput): string {
  const projectRoot = process.cwd()
  const script = path.join(projectRoot, "bin", "_prompts.py")
  return execFileSync(
    "python3",
    [script, command, JSON.stringify(ws)],
    {
      cwd: projectRoot,
      encoding: "utf-8",
      stdio: ["pipe", "pipe", "inherit"],
      timeout: SUBPROCESS_TIMEOUT_MS,
    },
  )
}

/**
 * Generate solver prompt via Python prompt builder (single source of truth).
 */
export function buildSolverPrompt(ws: WorkspacePromptInput): string {
  return callPromptsCli("solver", ws)
}

/**
 * Generate grading prompt via Python prompt builder.
 */
export function buildGradingPrompt(ws: WorkspacePromptInput): string {
  return callPromptsCli("grading", ws)
}
