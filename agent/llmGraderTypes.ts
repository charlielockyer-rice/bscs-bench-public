/**
 * LLM Grader Type Definitions
 *
 * Course-agnostic types for LLM-based grading of theoretical assignments.
 * Uses Zod schemas for structured output validation with AI SDK 6.
 */

import { z } from "zod"

// ============================================================================
// Zod Schemas for Structured Output
// ============================================================================

/**
 * Rubric breakdown item - how points were awarded for a specific criterion
 */
export const RubricItemSchema = z.object({
  criterion: z.string().describe("The rubric criterion being evaluated"),
  points: z.number().describe("Points earned for this criterion"),
  maxPoints: z.number().describe("Maximum points possible for this criterion"),
  reason: z.string().describe("Brief explanation for the score"),
})

/**
 * Grade for a single problem
 */
export const ProblemGradeSchema = z.object({
  problemNumber: z.number().describe("Problem number (1, 2, 3, etc.)"),
  problemName: z.string().describe("Problem name or title"),
  pointsEarned: z.number().describe("Total points earned for this problem"),
  pointsMax: z.number().describe("Maximum points possible for this problem"),
  approach: z.string().describe("Brief description of the student's approach"),
  strengths: z.array(z.string()).describe("What the student did well"),
  errors: z.array(z.string()).describe("Specific errors or gaps in the solution"),
  feedback: z.string().describe("Detailed feedback paragraph for the student"),
  rubricBreakdown: z
    .array(RubricItemSchema)
    .describe("Point-by-point breakdown of the grade"),
})

/**
 * Complete LLM grading response
 */
export const LLMGradeResponseSchema = z.object({
  problems: z.array(ProblemGradeSchema).describe("Grades for each problem"),
  overallComments: z
    .string()
    .describe("Overall feedback summarizing patterns of strength or weakness"),
  totalPointsEarned: z.number().describe("Sum of all points earned"),
  totalPointsMax: z.number().describe("Sum of all maximum points"),
})

// ============================================================================
// TypeScript Types (inferred from Zod schemas)
// ============================================================================

export type RubricItem = z.infer<typeof RubricItemSchema>
export type ProblemGrade = z.infer<typeof ProblemGradeSchema>
export type LLMGradeResponse = z.infer<typeof LLMGradeResponseSchema>

// ============================================================================
// Configuration Types
// ============================================================================

/**
 * Configuration for LLM grading.
 * All paths are either absolute or relative to workspace/course directory.
 */
export interface LLMGradeConfig {
  /** Path to the workspace directory */
  workspaceDir: string

  /** Course identifier (comp140, comp182, comp321, comp322, comp341, comp382, comp411) */
  course: string

  /** Assignment number (1, 2, 3, etc.) */
  assignmentNumber: number

  /** Assignment name (used for file discovery) */
  assignmentName?: string

  /** OpenRouter model ID (uses default from config if not specified) */
  model?: string

  // Optional overrides for grading context
  /** Custom grading prompt (inline or path to file) */
  gradingPrompt?: string

  /** Custom rubric content (inline or path to file) */
  rubric?: string

  /** Custom reference solution content (inline or path to file) */
  referenceSolution?: string

  /** Assignment instructions content (inline or path to file) */
  instructions?: string

  /** Student submission content (inline, otherwise read from writeup.md) */
  submission?: string

  /** Explicit path to rubric file (from detection, takes precedence over auto-discovery) */
  rubricPath?: string
}

/**
 * Result from LLM grading.
 * Maps to existing TestResult[] pattern for UI compatibility.
 */
export interface LLMGradeResult {
  /** Individual test/problem results */
  tests: Array<{
    name: string
    passed: boolean
    points: number
    maxPoints: number
    error?: string
    feedback?: string
  }>

  /** Summary statistics */
  summary: {
    passed: number
    total: number
    percentage: number
    pointsEarned: number
    pointsMax: number
  }

  /** Overall comments from the grader */
  overallComments: string

  /** Raw LLM response for debugging */
  rawResponse?: LLMGradeResponse
}

// ============================================================================
// Grading Context Types
// ============================================================================

/**
 * Context for building a grading prompt.
 * All fields are optional - the prompt builder adapts based on what's available.
 */
export interface GradingContext {
  /** Assignment instructions */
  instructions: string

  /** Student submission to grade */
  submission: string

  /** Point-by-point grading rubric (optional) */
  rubric?: string

  /** Reference/model solution for comparison (optional) */
  referenceSolution?: string

  /** Course-specific grading instructions (optional) */
  gradingPrompt?: string

  /** Assignment type hint for prompt customization */
  assignmentType?: "theoretical" | "programming" | "mixed"

  /** Course name for context */
  course?: string

  /** Assignment number */
  assignmentNumber?: number

  /** Assignment name */
  assignmentName?: string
}

// ============================================================================
// Course Config Types (for YAML parsing)
// ============================================================================

/**
 * Problem definition from course config
 */
export interface ProblemConfig {
  number: number
  name: string
  points: number
  parts?: Array<{
    part: string
    points: number
    description: string
    rubric?: string[]
  }>
}

/**
 * LLM grading section in course config
 */
export interface LLMGradingConfig {
  /** Path to rubric file (relative to course directory) */
  rubric?: string

  /** Path to reference solution file (relative to course directory) */
  referenceSolution?: string

  /** Path to grading prompt file (relative to course directory) */
  gradingPrompt?: string
}

/**
 * Course assignment config (parsed from YAML)
 */
export interface AssignmentConfig {
  /** Assignment number (unified schema) */
  assignment_number?: number
  /** Legacy: homework_number */
  homework_number?: number
  /** Legacy: module_number */
  module_number?: number

  /** Assignment name (unified schema) */
  assignment_name?: string
  /** Legacy: homework_name */
  homework_name?: string
  /** Legacy: module_name */
  module_name?: string

  /** Display name */
  display_name?: string

  /** Course identifier */
  course?: string

  /** Total points */
  total_points?: number

  /** Assignment type */
  type?: "theoretical" | "programming" | "mixed"

  /** Language (python, java, c, proof) */
  language?: string

  /** LLM grading configuration */
  llm_grading?: LLMGradingConfig

  /** Problem definitions */
  problems?: ProblemConfig[]
}
