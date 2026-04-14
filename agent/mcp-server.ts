/**
 * MCP Server for bscs-bench grading
 *
 * Exposes test/submit as MCP tools so that CLI agents
 * like Claude Code use a first-class tool interface instead of arbitrary
 * shell commands.  This prevents agents from discovering and passing
 * flags like --no-docker to bypass containerised grading.
 *
 * Usage:
 *   bun run agent/mcp-server.ts --workspace <path>
 *
 * Stdout is reserved for the MCP stdio transport — all logging goes to stderr.
 */

import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js"
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js"
import * as path from "path"
import * as fs from "fs"

import { execSync } from "child_process"
import { createTestExecutionBackend, type TestExecutionMode } from "./testExecutionBackend"
import { resolveWorkspaceContext } from "./workspaceContext"
import { deriveProjectRoot } from "./workspacePaths"

// ── CLI args ─────────────────────────────────────────────────────────────────

function parseArgs(): { workspaceDir: string } {
  const args = process.argv.slice(2)
  const idx = args.indexOf("--workspace")
  if (idx === -1 || idx + 1 >= args.length) {
    console.error("Usage: bscs-mcp --workspace <path>")
    process.exit(1)
  }
  const raw = args[idx + 1]
  const workspaceDir = path.resolve(raw)
  if (!fs.existsSync(workspaceDir)) {
    console.error(`Workspace not found: ${workspaceDir}`)
    process.exit(1)
  }
  return { workspaceDir }
}

// ── Helpers ──────────────────────────────────────────────────────────────────
// deriveProjectRoot is imported from ./workspacePaths

// ── Main ─────────────────────────────────────────────────────────────────────

async function main() {
  const { workspaceDir } = parseArgs()
  const projectRoot = deriveProjectRoot(workspaceDir)
  const ctx = resolveWorkspaceContext(workspaceDir)
  const executionMode = (process.env.BSCS_TEST_EXECUTION_MODE || "host") as TestExecutionMode
  const testBackend = createTestExecutionBackend(projectRoot, executionMode)

  const log = (...args: unknown[]) => console.error("[bscs-mcp]", ...args)
  log(`workspace: ${workspaceDir}`)
  log(`course: ${ctx.course}, language: ${ctx.language}, module: ${ctx.moduleName}`)
  log(`test backend: ${testBackend.mode}`)

  const server = new McpServer({
    name: "bscs-bench",
    version: "1.0.0",
  })

  // ── test tool ───────────────────────────────────────────────────────────

  // For malloc, run mdriver -v (all traces) which reports both correctness
  // and the aggregate performance index in a single run.
  const isMalloc = ctx.course === "comp321" && ctx.moduleName === "malloc"

  let testAttempt = 0
  const MAX_TEST_ATTEMPTS = 100

  const runTestAttempt = async () => {
    testAttempt++
    if (testAttempt > MAX_TEST_ATTEMPTS) {
      return { content: [{ type: "text" as const, text: `ERROR: Safety limit reached (${MAX_TEST_ATTEMPTS} test attempts). This likely indicates a problem with the approach — stop and re-evaluate.` }] }
    }
    log(`test: attempt ${testAttempt}, committing workspace...`)

    // Commit the agent's current work before grading
    try {
      execSync("git add -A", { cwd: workspaceDir, stdio: "ignore" })
      execSync(`git commit -m "Test attempt ${testAttempt}" --allow-empty`, { cwd: workspaceDir, stdio: "ignore" })
    } catch {
      // Commit may fail if nothing changed — that's fine
    }

    log("test: running...")
    let output: string
    if (isMalloc) {
      output = await testBackend.runMallocPerf(workspaceDir)
    } else {
      output = await testBackend.runTest(workspaceDir, "test")
    }
    log(`test: done (${output.length} chars)`)
    return { content: [{ type: "text" as const, text: `[Attempt ${testAttempt}]\n\n${output}` }] }
  }

  server.registerTool(
    "test",
    {
      description: isMalloc
        ? "Run the basic test suite on your malloc implementation (public + agent tests). Call this EARLY and OFTEN."
        : "Run the basic test suite on your solution (public + agent tests). Returns detailed pass/fail results. Call this EARLY and OFTEN after making changes.",
    },
    runTestAttempt,
  )

  // ── submit tool (always available, one attempt total) ─────────────────────

  server.registerTool(
    "submit",
    {
      description:
        "Mark your work as submitted (one attempt total). Final grading happens after submission.",
    },
    async () => {
      const submissionPath = path.join(workspaceDir, "submission.json")
      try {
        if (fs.existsSync(submissionPath)) {
          const existing = JSON.parse(fs.readFileSync(submissionPath, "utf-8")) as {
            submitted?: boolean
          }
          if (existing.submitted) {
            return { content: [{ type: "text" as const, text: "ERROR: This workspace has already been submitted. You cannot resubmit." }] }
          }
        }
      } catch {
        // Ignore malformed submission files and continue.
      }

      log("submit: marking as submitted")

      fs.writeFileSync(
        submissionPath,
        JSON.stringify({
          timestamp: new Date().toISOString(),
          submitted: true,
        }, null, 2),
      )

      return { content: [{ type: "text" as const, text: "Submission recorded. Your work has been marked as final." }] }
    },
  )

  // ── Start server ─────────────────────────────────────────────────────────

  const transport = new StdioServerTransport()
  await server.connect(transport)
  log("MCP server running on stdio")
}

main().catch((err) => {
  console.error("Fatal:", err)
  process.exit(1)
})
