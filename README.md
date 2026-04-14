# BSCS Bench

An agent evaluation framework for testing AI coding agents on programming assignments. The active architecture is assignment-attempt based: each assignment run writes immutable attempt data plus a full git workspace snapshot, and aggregate views are derived from that state.

## Features

- Multi-language support: Python, Java, C, theoretical (proof-based)
- Docker-first runners for reproducible grading
- SSH-based runner for COMP 421 (Rice CLEAR servers)
- LLM-based grading for theoretical and hybrid assignments
- LLM-based code review with multi-reviewer support (quality, style, assignment spirit)
- Workspace-scoped tools, allowlisted edits, and resettable workspaces
- Performance benchmarking (malloc lab)

## Supported Courses

| Course | Language | Assignments | Runner | Notes |
|--------|----------|-------------|--------|-------|
| COMP 140 | Python | 7 modules | Docker | Computational Thinking |
| COMP 182 | Proof + Python | 9 homeworks | LLM + Docker | Algorithmic Thinking (HW4/HW6 hybrid) |
| COMP 215 | Java | 6 homeworks | Docker | Software Development |
| COMP 310 | Java | 7 assignments | Docker | Advanced OOP (Ballworld chain + ChatApp design) |
| COMP 321 | C | 6 projects | Docker | Computer Systems + perf |
| COMP 322 | Java | 4 homeworks | Docker | HJlib parallel programming |
| COMP 341 | Python | 7 homeworks | Docker | ML stack (sklearn, PyTorch) |
| COMP 382 | Proof | 7 homeworks | LLM | Theoretical, LLM-graded |
| COMP 411 | Java | 6 assignments | Docker | Programming Languages |
| COMP 421 | C + Proof | 3 labs + 2 exams | SSH + LLM | Operating Systems (CLEAR) |

## Quick Start

See **[docs/QUICKSTART.md](docs/QUICKSTART.md)** for the full setup guide (prerequisites, install, configuration, validation).

The short version:

```bash
bun install
cp .env.example .env       # fill in OPENROUTER_API_KEY
bin/doctor                  # validate your setup
bin/setup_course ./comp140
bin/bench-cli run course comp140 --model-key gpt-5-3-codex
```

## Architecture

```
bscs-bench/
├── agent/              # TypeScript agent harness (AI SDK + OpenRouter)
├── framework/          # Python evaluation engine (reusable)
├── bin/                # CLI entry points
├── comp140/            # Course content (example)
├── comp421/            # SSH-based course (CLEAR servers)
└── workspaces/         # Agent workspaces (gitignored)
```

CLI-driven: use `bin/bench-cli` for assignment/course/corpus runs and derived exports, and `bin/grade` for the underlying submit-grade phase.

## Canonical State

Canonical benchmark state lives under:

```text
state/models/<model_key>/assignments/<assignment_id>/
  manifest.json
  attempts/<attempt_id>/
```

Each attempt stores the raw trace, raw agent metrics, grading outputs, review outputs,
`solution.diff`, copied solution files, and `workspace.bundle` for the full git workspace snapshot.

Derived export packages live under:

```text
exports/models/<model_key>/
  latest.json
  latest.tar.gz
```

## Workspace Structure

Each workspace is a single assignment with reset support. Metadata uses the unified schema (`assignment_*`) and maps to a stable `assignment_id` of the form `<course>_<assignment_name>`:

```
workspaces/comp140_module1_circles/
├── workspace.yaml
├── instructions.md
├── solution.py
├── template.py
├── writeup.md
├── attempts.json
└── lib/ -> symlink
```

Example `workspace.yaml` (abbreviated):
```yaml
assignment_number: 1
assignment_name: "circles"
display_name: "Circles"
course: "comp140"
language: "python"
```

## COMP 421: SSH-Based Runner (Rice CLEAR)

COMP 421 (Operating Systems) is unique — it uses SSH to Rice University's CLEAR servers instead of Docker. The Yalnix OS simulator, kernel linker, and hardware libraries only exist on CLEAR at `/clear/courses/comp421/pub/` and cannot be redistributed.

### Who Can Use This

**COMP 421 is only usable by current Rice University students** with an active Net ID and access to CLEAR. If you are not a Rice student, you cannot run COMP 421 labs. The other courses work for anyone with Docker.

### Setup

See [docs/QUICKSTART.md](docs/QUICKSTART.md#comp-421-rice-clear-ssh-access) for SSH key setup and configuration.

### What Runs Where

| Assignment | Type | Runner | Details |
|-----------|------|--------|---------|
| Lab 1: MonTTY | C | SSH | Terminal driver, needs Xvfb on CLEAR |
| Lab 2: Yalnix Kernel | C | SSH | Process management, VM, traps |
| Lab 3: Yalnix FS | C | SSH | File system on Yalnix simulator |
| Midterm Exam | Theoretical | LLM | LLM-graded, no SSH needed |
| Final Exam | Theoretical | LLM | LLM-graded, no SSH needed |

The two exams are theoretical (proof-based) and graded by LLM — they do not require CLEAR access.

## Bench CLI V2

Active commands:

```bash
bin/bench-cli run assignment comp140_circles --model-key gpt-5-3-codex
bin/bench-cli run course comp140 --model-key gpt-5-3-codex
bin/bench-cli run corpus --model-key gpt-5-3-codex
bin/bench-cli phase submit-grade comp140_circles --model-key gpt-5-3-codex
bin/bench-cli phase llm-grade comp140_circles --model-key gpt-5-3-codex
bin/bench-cli phase code-review comp140_circles --model-key gpt-5-3-codex
bin/bench-cli summarize model gpt-5-3-codex
bin/bench-cli export model gpt-5-3-codex
```

Notes:
- `run` creates immutable per-assignment attempts under `state/`
- `phase ...` operates on the active attempt for one assignment
- `summarize` reads manifests only
- `export` builds the archive/website package from active attempts only

## LLM Grading

The `submit` tool supports written grading for theoretical and hybrid courses:
- Theoretical: written only (COMP 182, COMP 382, COMP 421 exams)
- Hybrid: code tests + written component (COMP 140, COMP 321, COMP 322, COMP 341)

Written submissions are **one attempt only** by design.
Submissions record git state (`HEAD` and `git status --short`) in `submission.json`.

## Code Review

LLM-based code review evaluates agent solutions across correctness, code quality, style, assignment spirit, and robustness. Supports multiple reviewers per active attempt and stores raw reviewer outputs in the attempt directory.

## Security / Trust Model

This repo is intended for local usage today. File tools are scoped to the workspace and enforce allowlisted edit paths. Most grading runs in Docker per-course (including Python and malloc perf), and there is no general host shell tool exposed to agents.

COMP 421 uses SSH to an external server (CLEAR) — the runner uses `BatchMode=yes` so it will fail immediately rather than prompt for a password. No credentials are stored in the repository.

## Adding a New Course

See `docs/ADDING_COURSES.md` and `docs/CONFIG_SCHEMA.md` for the unified config schema and integration checklist.

## Development

```bash
bun run typecheck
```

## Environment Variables

See `.env.example` for a copyable template and [docs/QUICKSTART.md](docs/QUICKSTART.md#3-configure-environment) for the full reference.

## License

MIT
