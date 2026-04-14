# Quickstart

Everything you need to go from a fresh clone to running benchmarks.

## Quick Setup

For an interactive setup that handles everything below automatically:

```bash
bin/setup
```

This installs dependencies, configures `.env`, creates workspaces for your chosen courses, and optionally pre-builds Docker images.

---

## Manual Setup

If you prefer to set things up step by step:

## 1. Prerequisites

**Required:**

| Tool | Version | Why |
|------|---------|-----|
| Python | 3.10+ | Grading framework and runners (3.9 will not work) |
| Bun | any | TypeScript agent harness |
| Docker Desktop | any | Reproducible test runners for most courses |
| Git | any | Workspace reset (each workspace is its own git repo) |
| rsync | any | Workspace file setup |

**Recommended:**

| Package | Install | Why |
|---------|---------|-----|
| PyYAML | `pip3 install pyyaml` | `bin/grade` reads workspace.yaml |

## 2. Install Dependencies

```bash
git clone <repo-url> && cd bscs-bench
bun install
```

## 3. Configure Environment

```bash
cp .env.example .env
```

Edit `.env` and fill in at minimum:

```
OPENROUTER_API_KEY=sk-or-v1-...    # https://openrouter.ai/keys
```

If you plan to use `--agent opencode`, also configure Opencode itself:

```bash
opencode auth login
```

Or set `OPENCODE_CONFIG_CONTENT` for inline provider config. `bench-cli` does
not rely on `OPENROUTER_API_KEY` alone for Opencode auth.

All variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `OPENROUTER_API_KEY` | OpenRouter API key | (none — required for benchmarks) |
| `CLEAR_NETID` | Rice Net ID for COMP 421 labs | (none — only for COMP 421) |
| `MOCK_GITHUB` | Skip real GitHub API for COMP 322 HW2 | `true` recommended |
| `GITHUB_USERNAME` | GitHub username (COMP 322 HW2, if not mocking) | (none) |
| `GITHUB_TOKEN` | GitHub PAT (COMP 322 HW2, if not mocking) | (none) |

## 4. Validate Setup

```bash
bin/doctor
```

This checks all prerequisites, dependencies, configuration, Docker images, and course directories. Fix any failures before continuing — warnings are informational.

## 5. Create Workspaces

Pick the courses you want to benchmark:

```bash
bin/setup_course ./comp140             # 7 Python workspaces
bin/setup_course ./comp321             # 6 C workspaces
bin/setup_course ./comp182             # 9 theoretical workspaces (LLM-graded)
# ... see CLAUDE.md for full list
```

Workspaces land in `workspaces/`. Each is its own git repo for reset support.

To reset workspaces to clean state (initial commit):

```bash
bin/reset-workspaces              # All workspaces (with confirmation)
bin/reset-workspaces comp140      # Only COMP 140 workspaces
bin/reset-workspaces --force      # Skip confirmation
```

To recreate from scratch: `bin/setup_course ./comp140 --force`

## 6. Run a Benchmark

### Option A: Claude Code / Codex CLI (recommended for cost efficiency)

```bash
bin/bench-cli run assignment comp140_circles --model-key gpt-5-3-codex
bin/bench-cli run course comp140 --model-key gpt-5-3-codex
bin/bench-cli run corpus --model-key gpt-5-3-codex
bin/bench-cli phase llm-grade comp140_circles --model-key gpt-5-3-codex
bin/bench-cli phase code-review comp140_circles --model-key gpt-5-3-codex
bin/bench-cli summarize model gpt-5-3-codex
bin/bench-cli export model gpt-5-3-codex
```

State goes to `state/models/<model_key>/assignments/<assignment_id>/...` with immutable attempts, full agent traces, grading outputs, review outputs, and `workspace.bundle`. Derived export packages go to `exports/models/<model_key>/`.

### Option B: Built-in agent harness (OpenRouter)

```bash
bin/bench comp140_circles
bin/bench comp140_circles --model "openai/gpt-4o"
```

## 7. Grade Manually

```bash
bin/grade ./workspaces/comp140_circles
bin/grade ./workspaces/comp140_circles -v --history
```

Docker images build automatically on first grading run (~1-2 min per course, ~5-6 GB total across all courses).

---

## Course-Specific Setup

### COMP 341: Machine Learning Data

Download datasets before running COMP 341 benchmarks:

```bash
bin/setup_comp341_data           # Downloads all 7 datasets (~114 MB compressed)
bin/setup_comp341_data --check   # Just show what's missing
```

Data is hosted as GitHub Release assets — no authentication needed.
`bin/setup_course ./comp341` auto-triggers this if the data directory is missing.

### COMP 322 HW2: GitHub API

HW2 makes GitHub API calls. Use mock mode (recommended) or provide real credentials:

```bash
# Mock mode (set in .env or per-command)
MOCK_GITHUB=true bin/grade ./workspaces/comp322_hw2

# Real mode (requires GITHUB_USERNAME + GITHUB_TOKEN in .env)
bin/grade ./workspaces/comp322_hw2
```

### COMP 421: Rice CLEAR SSH Access

COMP 421 labs run on Rice University's CLEAR servers via SSH. **This only works for Rice students with an active Net ID.**

1. Set up passwordless SSH:
   ```bash
   ssh-keygen -t ed25519                              # if you don't have a key
   ssh-copy-id your_netid@ssh.clear.rice.edu
   ssh your_netid@ssh.clear.rice.edu echo "OK"        # verify
   ```

2. Add to `.env`:
   ```
   CLEAR_NETID=your_netid
   ```

3. Create workspaces and grade:
   ```bash
   bin/setup_course ./comp421
   bin/grade ./workspaces/comp421_lab1 -v
   ```

The midterm and final exams are LLM-graded and do not require CLEAR access.

---

## Troubleshooting

**`bin/doctor` shows failures** — Fix the required tools first. Warnings (Docker images, optional env vars) are fine to skip initially.

**Docker build fails** — Rebuild the course image:
```bash
python3 comp321/runner.py factors ./workspaces/comp321_factors --rebuild
```

**Workspace missing `.git`** — Delete and recreate:
```bash
bin/setup_course ./comp140 --force
```

**PyYAML not found** — Install it:
```bash
pip3 install pyyaml
```
