/**
 * Test Executor — shared subprocess helpers for autograder/test runs.
 *
 * Extracted from tools.ts so both the OpenRouter agent harness and
 * the MCP server can run the test runner without duplicating spawn logic.
 */

import { spawn } from "child_process"
import * as path from "path"
import { getAugmentedEnv } from "./config"

/**
 * Spawn a command, collect stdout+stderr, and return combined output.
 *
 * @param cmd     - Executable path
 * @param args    - Arguments array
 * @param cwd     - Working directory
 * @param timeoutMs - Kill after this many ms (0 = no timeout)
 * @returns Combined stdout+stderr output
 */
export function spawnAndCollect(
  cmd: string,
  args: string[],
  cwd: string,
  timeoutMs = 0,
): Promise<string> {
  return new Promise((resolve) => {
    const proc = spawn(cmd, args, {
      cwd,
      env: getAugmentedEnv(),
      stdio: ["ignore", "pipe", "pipe"],
    })

    let output = ""
    proc.stdout?.on("data", (d) => (output += d.toString()))
    proc.stderr?.on("data", (d) => (output += d.toString()))

    let timer: ReturnType<typeof setTimeout> | undefined
    if (timeoutMs > 0) {
      timer = setTimeout(() => {
        proc.kill()
        output += `\nProcess killed after ${timeoutMs}ms timeout`
      }, timeoutMs)
    }

    proc.once("exit", () => {
      if (timer) clearTimeout(timer)
      resolve(output || "(no output)")
    })
    proc.once("error", (err) => {
      if (timer) clearTimeout(timer)
      output += `\nProcess error: ${err.message}`
      resolve(output || "(no output)")
    })
  })
}

/**
 * Run `bin/test <workspace> --run-type <type> -v` and return combined output.
 *
 * @param workspaceRoot - Absolute path to the workspace directory
 * @param projectRoot   - Absolute path to the project root (where bin/test lives)
 * @param runType       - "test" (public + agent) or "submit" (public + private)
 * @returns Grade output text
 */
export async function executeTest(
  workspaceRoot: string,
  projectRoot: string,
  runType: "test" | "submit" = "test",
): Promise<string> {
  const testScript = path.resolve(projectRoot, "bin/test")
  return spawnAndCollect(testScript, [workspaceRoot, "--run-type", runType, "-v"], projectRoot)
}
