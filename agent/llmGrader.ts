/**
 * LLM Grader Core
 *
 * Course-agnostic LLM-based grading using AI SDK 6.
 * Calls the LLM to generate a JSON response, then parses it with Zod.
 */

import { generateText } from "ai"
import * as path from "path"
import { config, getOpenRouter } from "./config"
import {
  type LLMGradeConfig,
  type LLMGradeResult,
  type LLMGradeResponse,
  LLMGradeResponseSchema,
} from "./llmGraderTypes"
import { buildGradingPrompt } from "./promptBridge"

// ============================================================================
// Main Grading Function
// ============================================================================

/**
 * Grade a student submission using an LLM.
 *
 * This function:
 * 1. Builds a grading prompt via the Python prompt bridge
 * 2. Calls the LLM to generate JSON output
 * 3. Parses and validates the response with Zod
 * 4. Converts to the standard GradeResult format
 */
export async function gradeSubmission(cfg: LLMGradeConfig): Promise<LLMGradeResult> {
  // Build the grading prompt via the Python prompt bridge (single source of truth).
  // The Python builder discovers instructions, rubric, reference solution, and
  // submission from standard file paths within the workspace and course directories.
  const prompt = buildGradingPrompt({
    dir: path.resolve(cfg.workspaceDir),
    course: cfg.course,
    name: cfg.assignmentName || "",
    assignment_number: cfg.assignmentNumber,
    display_name: cfg.assignmentName || "",
  })

  // Get OpenRouter client
  const openrouter = getOpenRouter()

  // Call the LLM
  const modelId = cfg.model || config.defaultModel

  const result = await generateText({
    model: openrouter.chat(modelId),
    prompt,
  })

  // Extract JSON from the response text
  const text = result.text.trim()
  const jsonMatch = text.match(/\{[\s\S]*\}/)
  if (!jsonMatch) {
    throw new Error("LLM did not return valid JSON. Response: " + text.slice(0, 500))
  }

  // Parse and validate with Zod
  let parsedJson: unknown
  try {
    parsedJson = JSON.parse(jsonMatch[0])
  } catch (e) {
    throw new Error("Failed to parse JSON from LLM response: " + (e instanceof Error ? e.message : String(e)))
  }

  const validatedResponse = LLMGradeResponseSchema.parse(parsedJson)

  // Convert to standard result format
  return convertToGradeResult(validatedResponse)
}

// ============================================================================
// Result Conversion
// ============================================================================

/**
 * Convert LLM response to standard GradeResult format
 */
function convertToGradeResult(response: LLMGradeResponse): LLMGradeResult {
  const tests = response.problems.map(problem => ({
    name: `Problem ${problem.problemNumber}: ${problem.problemName}`,
    passed: problem.pointsEarned >= problem.pointsMax * config.llmGradingPassThreshold,
    points: problem.pointsEarned,
    maxPoints: problem.pointsMax,
    feedback: problem.feedback,
    error: problem.errors.length > 0 ? problem.errors.join("; ") : undefined,
  }))

  const passed = tests.filter(t => t.passed).length
  const total = tests.length

  return {
    tests,
    summary: {
      passed,
      total,
      percentage: total > 0 ? Math.round((response.totalPointsEarned / response.totalPointsMax) * 100) : 0,
      pointsEarned: response.totalPointsEarned,
      pointsMax: response.totalPointsMax,
    },
    overallComments: response.overallComments,
    rawResponse: response,
  }
}
