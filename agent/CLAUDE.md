# Agent Harness

TypeScript agent harness using AI SDK 6 with OpenRouter for model-agnostic benchmarking.

## Structure

```
agent/
  index.ts            # Main exports: runBenchmark, streamBenchmark
  benchmarkEngine.ts  # Core benchmark loop (ToolLoopAgent, token tracking, continuations)
  workspaceContext.ts # Resolve language, course, and performance settings from workspace
  workspaceConfig.ts  # Load and normalize workspace.yaml/workspace.json
  allowedPaths.ts     # Derive allowed edit paths from course config
  tools.ts            # Workspace-sandboxed tools + callback instrumentation
  prompts.ts          # System prompts for benchmarking (Python, Java, C, theoretical)
  config.ts           # Default model, max steps configuration
  runner.ts           # High-level CLI runner with logging
  gradeParser.ts      # Parses grade output into structured TestResult[]
  llmGrader.ts        # LLM-based grading for theoretical courses (comp182, comp382)
  llmGraderTypes.ts   # Zod schemas and types for LLM grading
  promptBridge.ts     # Bridge to Python prompt builder (bin/_prompts.py)
```

## Key Concepts

### Tool Sandboxing

All file operations restricted to workspace directory:
```typescript
const validatePath = createPathValidator(workspaceRoot)
// Blocks: ../../../etc/passwd, /absolute/paths, symlink escapes
```

### Grade Parsing

`gradeParser.ts` extracts structured test results from CLI output:
```
[PASS] test_distance_basic (1/1 pts)
[FAIL] test_midpoint_basic (0/1 pts)
       Input: ...
       Expected: ...
       Actual: ...
```

### Malloc Performance Tool

`malloc_perf` is a separate tool for malloc lab performance benchmarking:
- Only available for malloc workspaces (checks for `mm.c`)
- Runs via `comp321/runner.py --perf` inside Docker
- Compiles and runs `mdriver -v` in the container to get per-trace and aggregate performance
- Two-phase workflow: use `grade` for correctness first, then `malloc_perf` for optimization
- Output format: `Perf index = 31/40 (util) + 60/60 (thru) = 91/100`

### Submit Tool (Hybrid & Theoretical Courses)

The `submit` tool handles final submission with **detection-based grading**:

**Course Types:**
- **Theoretical** (comp182, comp382): Written only, no code tests
- **Hybrid** (comp140, comp321, comp322, comp341): Code tests + written component
- **Code-only** (comp215, comp411): Code tests only, no written grading

**Detection-based approach** (no explicit type configuration needed):
- `hasCodeTests(config)` - Returns false for `language === "proof"`, true otherwise
- `findRubric(course, num, name)` - Discovers rubric in `{course}/rubrics/` directory
- `hasWriteupContent(workspace)` - Checks writeup has content (not template placeholders)

**Rubric discovery convention** (`findRubric()` checks in order):
1. `{assignmentName}_rubric.md` (e.g., `circles_rubric.md`)
2. `module{N}_rubric.md`
3. `hw{N}_rubric.md`
4. `homework{N}_rubric.md`
5. `assignment{N}_rubric.md`

**Submission flow:**
1. Check for existing written submission (one-attempt enforcement)
2. Run code tests if `hasCodeTests()` is true
3. Run LLM grading if rubric exists AND writeup has content
4. Combine scores and save to `submission.json`

**Key features:**
- **One attempt enforcement**: Written submissions can only be submitted once
- Writes `submission.json` with `writtenSubmissionAttempted: true` **before** LLM grading starts
- Combined scores show code points + written points = total
- Submission records git state (`HEAD` and `git status --short`) for auditability

**Template placeholder detection** uses shared `TEMPLATE_PLACEHOLDERS` constant (prefix-matched):
```typescript
// Keep in sync with bin/_prompts.py TEMPLATE_PLACEHOLDERS.
const TEMPLATE_PLACEHOLDERS = [
  "[Your solution",
  "[Your solutions",
  "[WRITE YOUR ANSWER",
  "[Write your answer",
]
```

### LLM Grading (llmGrader.ts)

LLM-based grading for theoretical courses that have no unit tests:

**Files:**
- `llmGrader.ts` - Main `gradeSubmission()` function
- `llmGraderTypes.ts` - Zod schemas for structured JSON output
- `promptBridge.ts` - Bridge to Python prompt builder (grading + solver prompts)

**Context discovery:**
- Instructions: `workspace/instructions.md`
- Submission: `workspace/writeup.md`
- Rubrics: `course/solutions/homework{N}_rubric.md` (comp182) or `course/rubrics/`
- Reference solutions: `course/reference_solutions/hw{N}_solutions.md`

**Output format:**
```typescript
interface LLMGradeResult {
  tests: Array<{ name, passed, points, maxPoints, feedback }>
  summary: { passed, total, percentage, pointsEarned, pointsMax }
  overallComments: string
}
```

### Course-Specific Prompts

`prompts.ts` selects prompts based on course type detection:

**Theoretical courses** (`BENCHMARK_PROMPT_THEORETICAL`):
- Instructs agent to write answers to writeup.md
- Emphasizes ONE submission attempt rule
- No `grade` tool (only `submit`)

**Hybrid courses** (`BENCHMARK_PROMPT_HYBRID`):
- Instructs agent to complete BOTH code AND written components
- Use `grade` tool for code iteration
- Use `submit` for final combined grading
- One attempt for written work

**Code-only courses** (default prompts):
- Standard code implementation workflow
- `grade` tool for testing, no written component

**Detection logic** in `getSystemPrompt()`:
```typescript
if (language === "proof") return BENCHMARK_PROMPT_THEORETICAL
if (rubricExists(course, num, name)) return BENCHMARK_PROMPT_HYBRID
return BENCHMARK_PROMPT_CODE  // default
```

**Helper:** `rubricExists()` delegates to `findRubric()` from tools.ts (DRY)

## Gotchas

- **OPENROUTER_API_KEY Required**: Benchmarks require this key to be set.

- **Malloc Perf Docker Runner**: `malloc_perf` calls `comp321/runner.py --perf` to compile and run `mdriver` inside Docker.

- **Submit Tool One-Attempt**: For theoretical courses, `submission.json` is written with `writtenSubmissionAttempted: true` BEFORE LLM grading runs. This ensures the attempt is tracked even if grading fails.

## Security Considerations

### YAML Loading

All YAML parsing uses `yaml.JSON_SCHEMA` to prevent code execution:
```typescript
yaml.load(content, { schema: yaml.JSON_SCHEMA }) as WorkspaceConfig
```

The default `yaml.load()` can execute arbitrary JavaScript code through YAML tags like `!!js/function`. Always use `JSON_SCHEMA` which only allows safe JSON-compatible types.

**Files with YAML loading:**
- `index.ts` - 2 locations (workspace info, project config)

### Command Injection Prevention

Git commit hashes are validated before use in shell commands:
```typescript
const initialCommit = execSync("git rev-list --max-parents=0 HEAD", ...)
if (!/^[0-9a-f]{40}$/i.test(initialCommit)) {
  throw new Error(`Invalid commit hash format: ${initialCommit}`)
}
execSync(`git reset --hard ${initialCommit}`, ...)
```

This prevents an attacker from injecting shell commands through a malicious git repository.

## Tool Sandboxing Notes

### What's Protected
- All file operations (read, write, edit, glob, grep) validate paths stay within workspace
- Path traversal via `../` is blocked
- Absolute paths outside workspace are rejected
- Symlinks that escape workspace are blocked

### What's Not Protected
- CPU/memory limits are not enforced by the tools themselves

### Edit Restrictions
Certain workspaces restrict which files can be edited via `allowedEditPaths` (derived from course config `tooling.allowed_paths` or `source_files`):
```typescript
const toolOptions: CreateToolsOptions = {
  allowedEditPaths: ["solution.py", "mm.c"]
}
```

## Code Organization

### Shared Helpers in prompts.ts

Continue prompts are centralized in `prompts.ts`:
```typescript
// For correctness benchmarks
export const CONTINUE_PROMPT_CORRECTNESS = "..."

// For performance benchmarks (takes threshold as parameter)
export function createContinuePromptPerformance(threshold: number): string
```

### Tool Wrapping in index.ts

The `wrapToolsForBenchmark()` helper wraps grade and malloc_perf tools to capture results:
```typescript
const refs = { gradeRef: { current: null }, perfRef: { current: null } }
const wrappedTools = wrapToolsForBenchmark(tools, refs)
```

This avoids duplicating the wrapping logic between `runBenchmark()` and `streamBenchmark()`.

## Spawn Error Handling

All spawn calls now properly capture process errors:
```typescript
await new Promise<void>((resolve) => {
  proc.once("exit", resolve)
  proc.once("error", (err) => {
    output += `\nProcess error: ${err.message}`
    resolve()
  })
})
```

**Why this matters:** Without the error handler, spawn failures (e.g., command not found) would cause the promise to never resolve, hanging the request.

**Locations updated:**
- `tools.ts` - grade, git_commit, git_status, git_diff, malloc_perf (make and mdriver)

## Grade Parser Notes

The grade parser (`gradeParser.ts`) uses a state machine to parse test results:

1. Look for test result lines: `[PASS|FAIL|...] test_name (pts)`
2. For failed tests, capture detail lines: `Input:`, `Expected:`, `Actual:`, `Error:`
3. Lines that don't match patterns are silently ignored (intentional for robustness)

If parsing seems incomplete, check that the autograder output format matches the expected patterns documented in the function's JSDoc.

## Decisions

- **OpenRouter**: Single provider for 300+ models
- **Bun Runtime**: Required for `Bun.Glob`, `Bun.spawn`
- **AI SDK 6 ToolLoopAgent**: `stepCountIs(maxSteps)` for termination
- **Safe YAML Loading**: Always use `JSON_SCHEMA` to prevent code execution
- **Validated Command Inputs**: Git hashes and paths are validated before shell use
