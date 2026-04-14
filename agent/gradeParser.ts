/**
 * Grade Output Parser
 *
 * Parses autograder output into structured test results.
 */

import { config } from "./config"
import type { TestResult as SharedTestResult } from "../shared/benchContracts"

export type TestResult = SharedTestResult

export interface GradeResult {
  module: number
  passed: number
  total: number
  percentage: number
  pointsEarned: number
  pointsPossible: number
  tests: TestResult[]
  compilationError?: string
  /** Performance index for malloc lab (0-100), null for other projects */
  performanceIndex?: number
  /** Utilization component (0-40) */
  utilizationScore?: number
  /** Throughput component (0-60) */
  throughputScore?: number
}

/**
 * Parses test tool output into structured test results.
 *
 * Example output format:
 * ```
 * ============================================================
 * COMP 140 Module 1: Circles - Autograder Results
 * ============================================================
 * Attempt: 1 of 10
 *
 * RESULTS SUMMARY
 * ---------------
 * Passed: 18/24 tests (75.0%)
 * Points: 15/20 (75.0%)
 *
 * DETAILED RESULTS
 * ----------------
 * [PASS] test_distance_basic (1/1 pts)
 * [FAIL] test_midpoint_basic (0/1 pts)
 *        Input: point0=(0, 0), point1=(4, 6)
 *        Expected: (2.0, 3.0)
 *        Actual: (0.0, 0.0)
 * ============================================================
 * ```
 */
/**
 * Parses test runner output into a summary of passed/total/percentage.
 * Tries JSON first ({passed, total}), then falls back to regex on the text output.
 */
export function parseTestSummary(output: string): { passed: number; total: number; percentage: number } | null {
  try {
    const parsed = JSON.parse(output) as Record<string, unknown>
    // Support both { passed, total } and { tests_passed, tests_total } field names
    const passedVal = parsed.passed ?? parsed.tests_passed
    const totalVal = parsed.total ?? parsed.tests_total
    if (typeof passedVal === "number" && typeof totalVal === "number") {
      const percentage = totalVal > 0 ? (passedVal / totalVal) * 100 : 0
      return {
        passed: passedVal,
        total: totalVal,
        percentage: Math.round(percentage * 10) / 10,
      }
    }
  } catch {
    // fall through to regex parsing
  }

  const match = output.match(/Passed:\s*(\d+)\/(\d+)\s*tests\s*\((\d+(?:\.\d+)?)%\)/)
  if (match) {
    return {
      passed: parseInt(match[1], 10),
      total: parseInt(match[2], 10),
      percentage: parseFloat(match[3]),
    }
  }

  return null
}

export function parseGradeOutput(output: string): GradeResult | null {
  const tests: TestResult[] = []

  // Extract module number from header (handles "Module X", "Homework X", and "Project X")
  const moduleMatch = output.match(/(?:Module|Homework|Project) (\d+):/)
  const module = moduleMatch ? parseInt(moduleMatch[1], 10) : 0

  // Check for compilation failure
  if (output.includes("COMPILATION FAILED")) {
    // Extract the error message (everything after "COMPILATION FAILED" until the next section or end)
    const errorMatch = output.match(/COMPILATION FAILED\s*[-]+\s*([\s\S]*?)(?:={10,}|$)/)
    const errorMessage = errorMatch
      ? errorMatch[1].trim().slice(0, config.maxCompilationErrorLength)
      : "Compilation failed"

    // Parse total from summary line (e.g., "Passed: 0/99 tests (0.0%)")
    const summaryMatch = output.match(/Passed:\s*(\d+)\/(\d+)\s*tests/)
    const total = summaryMatch ? parseInt(summaryMatch[2], 10) : 0

    return {
      module,
      passed: 0,
      total,
      percentage: 0,
      pointsEarned: 0,
      pointsPossible: 0,
      tests: [],
      compilationError: errorMessage,
    }
  }

  // Extract summary stats
  const passedMatch = output.match(/Passed:\s*(\d+)\/(\d+)\s*tests\s*\((\d+(?:\.\d+)?)%\)/)
  if (!passedMatch) {
    return null
  }

  const passed = parseInt(passedMatch[1], 10)
  const total = parseInt(passedMatch[2], 10)
  const percentage = parseFloat(passedMatch[3])

  // Extract points
  const pointsMatch = output.match(/Points:\s*(\d+(?:\.\d+)?)\/(\d+)/)
  const pointsEarned = pointsMatch ? parseFloat(pointsMatch[1]) : 0
  const pointsPossible = pointsMatch ? parseFloat(pointsMatch[2]) : 0

  // ==========================================================================
  // Parse individual test results using a state machine approach
  // ==========================================================================
  //
  // Expected format for each test:
  //   [STATUS] test_name (points/max pts)   -- COMP 140 style with points
  //   [STATUS] test_name                     -- COMP 215/321 style without points
  //
  // For failed tests, detail lines follow immediately after the test line:
  //   [FAIL] test_name (0/1 pts)
  //          Input: ...
  //          Expected: ...
  //          Actual: ...
  //
  // State machine:
  //   - currentTest = null: Looking for a new test line
  //   - currentTest != null: We have a test, looking for detail lines or next test
  //   - When we find a new test line, we push the previous test (if any)
  //   - Detail lines are only captured for failed tests
  //
  // Note: If the output format changes, patterns below may silently fail to match.
  // Consider adding debug logging if test parsing seems incomplete.
  // ==========================================================================

  // Parse only the final detailed-results section when present. Verbose course
  // runners often print per-test progress lines like "[PASS] test_name" during
  // execution and then print the full detailed report again at the end. If we
  // scan the entire output, those progress lines get double-counted.
  const detailedMarker = output.lastIndexOf("DETAILED RESULTS")
  const parseRegion = detailedMarker >= 0 ? output.slice(detailedMarker) : output
  const lines = parseRegion.split("\n")
  let currentTest: TestResult | null = null

  for (let i = 0; i < lines.length; i++) {
    const line = lines[i]

    // Try to match a test result line
    // Pattern 1 (with points): [PASS] test_name (1/1 pts) or (1.5/2 pts)
    const testMatchWithPoints = line.match(/\[(PASS|FAIL|ERROR|TIMEOUT|SKIP)\]\s+(\S+)\s+\((\d+(?:\.\d+)?)\/(\d+(?:\.\d+)?)\s*pts?\)/)
    // Pattern 2 (with timing): [PASS] test_name (0.12s) - COMP 322 style
    const testMatchWithTiming = line.match(/\[(PASS|FAIL|ERROR|TIMEOUT|SKIP)\]\s+(\S+)\s+\(\d+(?:\.\d+)?s\)/)
    // Pattern 3 (without points or timing): [PASS] test_name
    const testMatchWithoutPoints = line.match(/\[(PASS|FAIL|ERROR|TIMEOUT|SKIP)\]\s+(\S+)\s*$/)

    const testMatch = testMatchWithPoints || testMatchWithTiming || testMatchWithoutPoints
    if (testMatch) {
      // Save previous test if we have one
      if (currentTest) {
        tests.push(currentTest)
      }

      const status = testMatch[1]
      const isPassed = status === "PASS"

      // If points are provided (COMP 140 format), use them
      // Otherwise (COMP 215/321 format), use default: 1/1 for pass, 0/1 for fail
      const points = testMatchWithPoints
        ? parseFloat(testMatchWithPoints[3])
        : (isPassed ? 1 : 0)
      const pointsMax = testMatchWithPoints
        ? parseFloat(testMatchWithPoints[4])
        : 1

      currentTest = {
        name: testMatch[2],
        passed: isPassed,
        points,
        pointsMax,
      }
      continue
    }

    // Check for detail lines (Input, Expected, Actual, Error) for failed tests
    // These lines are indented and follow the test result line
    if (currentTest && !currentTest.passed) {
      const inputMatch = line.match(/^\s+Input:\s*(.+)$/)
      if (inputMatch) {
        currentTest.input = inputMatch[1]
        continue
      }

      const expectedMatch = line.match(/^\s+Expected:\s*(.+)$/)
      if (expectedMatch) {
        currentTest.expected = expectedMatch[1]
        continue
      }

      const actualMatch = line.match(/^\s+Actual:\s*(.+)$/)
      if (actualMatch) {
        currentTest.actual = actualMatch[1]
        continue
      }

      const errorMatch = line.match(/^\s+Error:\s*(.+)$/)
      if (errorMatch) {
        currentTest.error = errorMatch[1]
        continue
      }

      // Note: Lines that don't match any pattern are silently ignored.
      // This is intentional - the output may contain decorative lines,
      // section headers, or other content we don't need to parse.
    }
  }

  // Push the final test (state machine exits with currentTest still holding last test)
  if (currentTest) {
    tests.push(currentTest)
  }

  // Parse malloc performance index if present
  // Format: "Perf index = 39/40 (util) + 2/60 (thru) = 41/100"
  // Note: util and thru max values may vary, so we use flexible pattern matching
  let performanceIndex: number | undefined
  let utilizationScore: number | undefined
  let throughputScore: number | undefined

  const perfMatch = output.match(/Perf index = (\d+)\/\d+ \(util\) \+ (\d+)\/\d+ \(thru\) = (\d+)\/100/)
  if (perfMatch) {
    utilizationScore = parseInt(perfMatch[1], 10)
    throughputScore = parseInt(perfMatch[2], 10)
    performanceIndex = parseInt(perfMatch[3], 10)
  }

  return {
    module,
    passed,
    total,
    percentage,
    pointsEarned,
    pointsPossible,
    tests,
    performanceIndex,
    utilizationScore,
    throughputScore,
  }
}
