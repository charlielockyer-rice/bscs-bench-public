/**
 * System prompts for the benchmark agent
 *
 * This module delegates prompt generation to the Python prompt builder
 * via promptBridge.ts. Only TS-specific concerns (continue prompts)
 * remain here.
 */

import { buildSolverPrompt, type WorkspacePromptInput } from "./promptBridge"

// ============================================================================
// SYSTEM PROMPT (delegates to Python prompt builder via bridge)
// ============================================================================

interface SystemPromptOptions {
  language: string
  course?: string
  assignmentNumber?: number
  assignmentName?: string
  assignmentMode?: string
  workspaceDir?: string
}

/**
 * Gets the appropriate system prompt for a workspace.
 *
 * Delegates to the Python prompt builder via promptBridge.ts for the full
 * solver prompt content. The Python builder is the single source of truth
 * for prompt text, eliminating duplication between TS and Python.
 */
export function getSystemPrompt(opts: SystemPromptOptions): string {
  const ws: WorkspacePromptInput = {
    dir: opts.workspaceDir || "",
    course: opts.course || "",
    language: opts.language || "python",
    name: opts.assignmentName || "",
    assignment_number: opts.assignmentNumber || 1,
    assignment_mode: opts.assignmentMode,
  }
  return buildSolverPrompt(ws)
}

// ============================================================================
// TASK PROMPT
// ============================================================================

/**
 * Creates a task prompt for any course type.
 *
 * The solver prompt from the Python builder already contains the full
 * workflow, so the task prompt just needs to direct the agent to start.
 */
export function createUnifiedTaskPrompt(
  number: number,
  language: string,
  options?: { projectName?: string; performanceThreshold?: number; course?: string; assignmentName?: string }
): string {
  const ref = options?.projectName || options?.assignmentName || `Assignment ${number}`
  return `Complete ${ref} in this workspace. Follow the system prompt instructions.`
}

// ============================================================================
// CONTINUE PROMPTS (TS-specific, used by benchmarkEngine.ts)
// ============================================================================

/**
 * Continue prompt for correctness benchmarks (tests not all passing)
 */
export const CONTINUE_PROMPT_CORRECTNESS = `You haven't passed all tests yet. Continue working on the implementation.
Run the test tool again after making changes to check your progress.`

/**
 * Creates a continue prompt for performance benchmarks (performance index below target)
 */
export function createContinuePromptPerformance(performanceThreshold: number): string {
  return `Your performance index is below ${performanceThreshold}. Continue optimizing.
Focus on:
1. Segregated free lists if not implemented
2. Better fit policies (best-fit or better-fit)
3. Optimized coalescing
4. In-place realloc expansion
Run malloc_perf again after each optimization to check your performance index.`
}
