# CLI Entry Points

Executable scripts for workspace management, grading, and benchmarking. All scripts are designed to be run from the project root.

## Scripts

| Script | Runtime | Purpose |
|--------|---------|---------|
| `bench` | Bun | Run AI agent benchmark on a workspace |
| `bench-cli` | Python | Run Claude Code / Codex CLI / Gemini CLI / Opencode CLI / Cursor CLI agents on workspaces |
| `bench-course` | Bun | Run AI agent benchmark across all workspaces for a course |
| `reset-workspaces` | Python | Reset workspaces to initial git state (by course or all) |
| `build-image` | Bash | Build combined Docker images for courses |
| `grade` | Python | Unified autograder - detects language and dispatches |
| `setup_workspace` | Bash | Create single-task workspace for any course |
| `setup_course` | Bash | Create all workspaces for a course at once |
| `setup` | Bash | Interactive setup (deps, .env, workspaces, Docker) |
| `doctor` | Bash | Validate environment setup (tools, deps, config) |

## Key Commands

### Workspace Setup

```bash
# Single workspace
bin/setup_workspace ./comp140 1              # Creates comp140_module1_circles
bin/setup_workspace ./comp215 1              # Creates comp215_homework1
bin/setup_workspace ./comp321 2              # Creates comp321_hw2_count
bin/setup_workspace ./comp140 1 myagent      # Creates comp140_module1_circles_myagent (with suffix)

# All workspaces for a course (recommended)
bin/setup_course ./comp140                   # Creates 7 workspaces
bin/setup_course ./comp321 --force           # Recreate all
```

### Grading

```bash
# New format (reads workspace.yaml)
bin/grade ./workspaces/comp140_module1_circles
bin/grade ./workspaces/comp140_module1_circles -v --json

```

### Benchmarking

```bash
export OPENROUTER_API_KEY=sk-or-v1-...

bin/bench comp140_circles
bin/bench comp140_circles --model "openai/gpt-4o" --verbose
bin/bench comp140_circles --continue-on-fail --max-continuations 5
bin/bench comp140_circles --save  # Save to .benchmark/

# bench-cli v2
bin/bench-cli run assignment comp140_circles --model-key gpt-5-3-codex
bin/bench-cli run course comp140 --model-key gpt-5-3-codex
bin/bench-cli run corpus --model-key gpt-5-3-codex
bin/bench-cli phase submit-grade comp140_circles --model-key gpt-5-3-codex
bin/bench-cli phase llm-grade comp140_circles --model-key gpt-5-3-codex
bin/bench-cli phase code-review comp140_circles --model-key gpt-5-3-codex
bin/bench-cli summarize model gpt-5-3-codex
bin/bench-cli export model gpt-5-3-codex
```

## Course Type Detection

`setup_workspace` auto-detects course type from config files:

| Pattern | Type | Example |
|---------|------|---------|
| `configs/module*.yaml` | module | COMP 140 (Python) |
| `configs/homework*.yaml` | homework | COMP 215 (Java) |
| `configs/hw*.yaml` | python-hw / java-hw | COMP 341 (Python/ML), COMP 322 (Java) |
| `configs/assign*.yaml` | java-assignment | COMP 411 (Java) |
| `project_number:` in yaml | project | COMP 321 (C) |

## Gotchas & Learnings

- **grade auto-dispatches**: The unified `grade` script detects language from config files and calls the appropriate runner (Python Docker runner if present, Java runner.py, or C runner.py). No need to specify language.

- **workspace.yaml is source of truth**: Workspaces use unified fields (`assignment_number`, `assignment_name`, `display_name`). The grade script reads this to determine how to run tests.

- **setup_workspace creates both yaml and json**: For systems without PyYAML, `workspace.json` is created as fallback.

- **Python grading uses Docker by default**: If `docker_runner.py` exists, Python grading runs in Docker. Use `--no-docker` to force host execution (not recommended).

- **bench requires OPENROUTER_API_KEY**: Exits immediately if not set. Get key from https://openrouter.ai/keys

- **--continue-on-fail re-prompts**: If agent stops without passing all tests, it receives a "tests still failing" message and continues. Useful for stubborn failures.

- **bench-cli v2**: Canonical state lives under `state/models/<model_key>/...` as manifests plus immutable attempts. `export model` produces the archive package from active attempts only.

- **bench-cli runs on host by default**: Use `--sandbox` to run Claude, Codex, or Gemini inside Docker Sandbox.

- **bench-cli --tmux for long runs**: Wraps the entire run in a detached tmux session so it persists across SSH disconnects. Session auto-named `bench-{agent}-{filter}-{HHMMSS}`. Reattach with `tmux attach -t <name>`. Pane stays open after completion so you can read the summary.

- **setup_course skips existing**: By default, existing workspaces are skipped. Use `--force` to recreate.

- **Java tests copied to workspace**: `setup_workspace` copies custom tests from `course/tests/homeworkN/` into the workspace's skeleton_code, replacing skeleton tests.

## Decisions

- **Unified grade script**: Single entry point handles all languages. Simplifies agent tooling - just call `grade` without knowing course type.

- **Flat workspace structure**: Each workspace is one task. Simpler than nested Module_N directories. Agent has clear context.

- **Bun for TypeScript scripts**: `bench` uses Bun shebang for native TypeScript execution without compilation step.
