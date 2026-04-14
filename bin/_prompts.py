"""Shared prompt builders for CLI benchmark agents and grading.

Centralizes prompt construction so Claude Code CLI and Codex CLI stay in
lockstep and prompt behavior is driven by assignment metadata instead of
ad-hoc heuristics.
"""

from __future__ import annotations

import json
import re
import subprocess
import textwrap
from pathlib import Path

from _lib import PROJECT_ROOT, THEORETICAL_COURSES, _yaml

# Template placeholders — skip writeups that are still templates.
# Keep in sync with agent/tools.ts TEMPLATE_PLACEHOLDERS.
TEMPLATE_PLACEHOLDERS = [
    "[Your solution",
    "[Your solutions",
    "[WRITE YOUR ANSWER",
    "[Write your answer",
]


def validate_writeup_text(text):
    """Return the text if it has real content, or None if empty/template.

    Checks minimum length (100 chars) and template placeholder detection
    (allows partially filled writeups).
    """
    if not text:
        return None
    text = text.strip()
    if len(text) < 100:
        return None
    if any(p in text for p in TEMPLATE_PLACEHOLDERS):
        # Check if ALL content is just placeholders (allow partially filled)
        lines = [l.strip() for l in text.splitlines() if l.strip() and not l.startswith("#")]
        non_template = [l for l in lines if not any(p in l for p in TEMPLATE_PLACEHOLDERS)]
        if len(non_template) < 3:
            return None
    return text


def read_writeup(ws_dir):
    """Read the student writeup, returning None if empty/template.

    NOTE: agent/tools.ts hasWriteupContent() has similar logic but uses a
    200-char minimum and no partial-fill tolerance. Consider aligning if
    the divergence causes grading inconsistencies.
    """
    writeup_path = Path(ws_dir) / "writeup.md"
    if not writeup_path.exists():
        return None
    return validate_writeup_text(writeup_path.read_text())


def read_attempt_text(attempt_dir, snapshot_name, fallback_path=None):
    """Read a frozen attempt snapshot, falling back to a live workspace file."""
    if attempt_dir:
        snapshot_path = Path(attempt_dir) / snapshot_name
        if snapshot_path.exists():
            return snapshot_path.read_text()
    if fallback_path:
        path = Path(fallback_path)
        if path.exists():
            return path.read_text()
    return None


try:
    from framework.config_compat import (
        normalize_config as _normalize_config,
        get_assignment_name as _assignment_name_from_config,
        get_assignment_number as _assignment_number_from_config,
    )
except ImportError:
    def _normalize_config(config):
        return config

    def _assignment_name_from_config(data):
        for field in ("assignment_name", "module_name", "homework_name", "project_name"):
            if field in data:
                return data[field]
        return None

    def _assignment_number_from_config(data):
        for field in ("assignment_number", "module_number", "homework_number", "project_number"):
            if field in data:
                try:
                    return int(data[field])
                except (TypeError, ValueError):
                    return None
        return None


def _read_yaml_file(path: Path):
    if not path.exists() or not _yaml:
        return None
    with open(path) as f:
        data = _yaml.safe_load(f) or {}
    if isinstance(data, dict):
        return _normalize_config(data)
    return data


def load_assignment_config(course, assignment_number, assignment_name=""):
    """Load the config YAML matching a workspace assignment."""
    configs_dir = PROJECT_ROOT / course / "configs"
    if not configs_dir.is_dir() or not _yaml:
        return None

    for cfg_path in sorted(configs_dir.glob("*.yaml")):
        data = _read_yaml_file(cfg_path)
        if not isinstance(data, dict):
            continue

        cfg_number = _assignment_number_from_config(data)
        cfg_name = _assignment_name_from_config(data)

        if cfg_number == assignment_number:
            return data
        if assignment_name and cfg_name == assignment_name:
            return data

    return None


def _first_existing(candidates):
    """Return first existing path from candidates, or None."""
    for path in candidates:
        if path.exists():
            return path
    return None


def find_rubric_file(course, assignment_number, assignment_name=""):
    """Find rubric file for a course assignment."""
    course_dir = PROJECT_ROOT / course
    n = assignment_number
    name = assignment_name

    # rubrics/ (most courses)
    d = course_dir / "rubrics"
    if d.is_dir():
        candidates = []
        if name:
            candidates.append(d / f"{name}_rubric.md")
        candidates.extend([
            d / f"module{n}_rubric.md",
            d / f"hw{n}_rubric.md",
            d / f"homework{n}_rubric.md",
            d / f"assignment{n}_rubric.md",
        ])
        if found := _first_existing(candidates):
            return found

    # solutions/ (comp182 stores rubrics here)
    d = course_dir / "solutions"
    if d.is_dir():
        candidates = [d / f"homework{n}_rubric.md"]
        if name:
            candidates.append(d / f"{name}_rubric.md")
        if found := _first_existing(candidates):
            return found

    # reference_solutions/ (comp382 stores rubrics as hw{N}_solutions.md)
    d = course_dir / "reference_solutions"
    if d.is_dir():
        candidates = [d / f"hw{n}_solutions.md", d / f"homework{n}_solutions.md"]
        if name:
            candidates.append(d / f"{name}_solutions.md")
        if found := _first_existing(candidates):
            return found

    return None


def find_reference_solution(course, assignment_number):
    """Find a reference solution file for written grading."""
    course_dir = PROJECT_ROOT / course
    candidates = [
        course_dir / "reference_solutions" / f"hw{assignment_number}_solutions.md",
        course_dir / "solutions" / f"assignment{assignment_number}_solution.md",
        course_dir / "solutions" / f"hw{assignment_number}_solution.md",
    ]
    for path in candidates:
        if path.exists():
            return path
    return None


def find_grading_prompt_file(course):
    path = PROJECT_ROOT / course / "grading_prompt.md"
    return path if path.exists() else None


def find_code_review_prompt_file(course):
    """Find a course-specific code review prompt file."""
    path = PROJECT_ROOT / course / "code_review_prompt.md"
    return path if path.exists() else None


def find_style_guide(course):
    """Find a style guide for the course."""
    course_dir = PROJECT_ROOT / course
    candidates = [
        course_dir / "Shared" / "Coding_Style_and_Standards.md",
        course_dir / "style_guide.md",
    ]
    for path in candidates:
        if path.exists():
            return path
    return None


def find_reference_solution_code(course, assignment_number, assignment_name=""):
    """Find a reference solution code file for code review."""
    course_dir = PROJECT_ROOT / course
    ref_dir = course_dir / "reference_solutions"
    if not ref_dir.is_dir():
        return None

    extensions = [".py", ".java", ".c", ".go", ".ts"]
    name_patterns = [
        f"module{assignment_number}_solution",
        f"hw{assignment_number}_solution",
    ]
    if assignment_name:
        name_patterns.extend([
            f"{assignment_name}_solution",
            assignment_name,
        ])

    for pattern in name_patterns:
        for ext in extensions:
            path = ref_dir / f"{pattern}{ext}"
            if path.exists():
                return path

    return None


def _get_initial_commit(ws_dir):
    """Get the initial commit SHA for a workspace. Returns None on failure."""
    try:
        result = subprocess.run(
            ["git", "rev-list", "--max-parents=0", "HEAD"],
            cwd=ws_dir, capture_output=True, text=True, timeout=10,
        )
        if result.returncode != 0 or not result.stdout.strip():
            return None
        return result.stdout.strip().splitlines()[0]
    except (subprocess.TimeoutExpired, OSError):
        return None


def read_solution_diff(ws_dir):
    """Run git diff against initial commit in workspace, return the diff string."""
    ws_dir = Path(ws_dir)
    if not (ws_dir / ".git").exists():
        return ""

    initial_commit = _get_initial_commit(ws_dir)
    if not initial_commit:
        return ""

    try:
        result = subprocess.run(
            ["git", "diff", initial_commit, "HEAD"],
            cwd=ws_dir, capture_output=True, text=True, timeout=30,
        )
        return result.stdout if result.returncode == 0 else ""
    except (subprocess.TimeoutExpired, OSError):
        return ""


# Source code extensions to include when reading solution files.
_SOURCE_EXTENSIONS = {".py", ".java", ".c", ".h", ".go", ".ts", ".tsx", ".js", ".jsx"}

# Maximum total characters to read across all solution files.
_SOURCE_CONTENT_CAP = 100_000


def _collect_source_files(file_iter):
    """Collect source files from an iterator of (relative_name, absolute_path) tuples.

    Filters by _SOURCE_EXTENSIONS and caps total content at _SOURCE_CONTENT_CAP.
    Returns a dict of {filename: content}.
    """
    files = {}
    total_chars = 0
    for rel_name, fpath in file_iter:
        ext = Path(rel_name).suffix.lower()
        if ext not in _SOURCE_EXTENSIONS:
            continue
        if not fpath.exists():
            continue
        try:
            content = fpath.read_text()
        except (OSError, UnicodeDecodeError):
            continue
        if total_chars + len(content) > _SOURCE_CONTENT_CAP:
            remaining = _SOURCE_CONTENT_CAP - total_chars
            if remaining > 500:
                files[rel_name] = content[:remaining] + "\n... (truncated)"
            break
        files[rel_name] = content
        total_chars += len(content)
    return files


def read_solution_files(ws_dir):
    """Read source files modified by the agent (relative to initial commit).

    Returns a dict of {filename: content}. Only includes source code files
    and caps total content at _SOURCE_CONTENT_CAP characters.
    """
    ws_dir = Path(ws_dir)
    if not (ws_dir / ".git").exists():
        return {}

    initial_commit = _get_initial_commit(ws_dir)
    if not initial_commit:
        return {}

    try:
        result = subprocess.run(
            ["git", "diff", "--name-only", initial_commit, "HEAD"],
            cwd=ws_dir, capture_output=True, text=True, timeout=10,
        )
        if result.returncode != 0:
            return {}
    except (subprocess.TimeoutExpired, OSError):
        return {}

    changed = [f.strip() for f in result.stdout.strip().splitlines() if f.strip()]
    return _collect_source_files((fname, ws_dir / fname) for fname in changed)


def read_attempt_solution_files(attempt_dir, ws_dir=None):
    """Read frozen solution files from an attempt, falling back to the workspace."""
    if attempt_dir:
        solution_dir = Path(attempt_dir) / "solution"
        if solution_dir.is_dir():
            file_iter = (
                (str(fpath.relative_to(solution_dir)), fpath)
                for fpath in sorted(solution_dir.rglob("*"))
                if fpath.is_file()
            )
            files = _collect_source_files(file_iter)
            if files:
                return files
    return read_solution_files(ws_dir) if ws_dir else {}


def read_attempt_solution_diff(attempt_dir, ws_dir=None):
    """Read a frozen solution diff from an attempt, falling back to the workspace."""
    if attempt_dir:
        diff_path = Path(attempt_dir) / "solution.diff"
        if diff_path.exists():
            return diff_path.read_text()
    return read_solution_diff(ws_dir) if ws_dir else ""


def _extract_source_paths(config):
    source_files = config.get("source_files") or []
    if not isinstance(source_files, list):
        return [], []

    preferred = []
    fallback = []
    for entry in source_files:
        if isinstance(entry, str):
            fallback.append(entry)
            continue
        if not isinstance(entry, dict):
            continue
        path = entry.get("path")
        if not isinstance(path, str):
            continue
        fallback.append(path)
        if entry.get("student_implements") is True:
            preferred.append(path)

    return preferred, fallback


def _extract_writeup_sections(config):
    sections = config.get("writeup_sections")
    if isinstance(sections, list):
        return [s for s in sections if isinstance(s, str)]

    docs = config.get("docs") or {}
    sections = docs.get("writeup_sections") if isinstance(docs, dict) else None
    if not isinstance(sections, list):
        return []
    return [s for s in sections if isinstance(s, str)]


def _has_written_source_file(config):
    source_files = config.get("source_files") or []
    if not isinstance(source_files, list):
        return False

    for entry in source_files:
        if isinstance(entry, str):
            path = entry
        elif isinstance(entry, dict):
            path = entry.get("path")
        else:
            continue
        if isinstance(path, str) and Path(path).name in {"writeup.md", "writeup.txt"}:
            return True
    return False


def detect_assignment_mode(ws):
    """Classify a workspace as code-only, hybrid, or written-only."""
    course = ws.get("course", "")
    assignment_number = ws.get("assignment_number", 0)
    assignment_name = ws.get("name", "")
    language = ws.get("language", "")
    config = load_assignment_config(course, assignment_number, assignment_name) or {}
    explicit_mode = ws.get("assignment_mode")

    rubric_path = find_rubric_file(course, assignment_number, assignment_name)
    writeup_sections = _extract_writeup_sections(config)
    preferred_sources, fallback_sources = _extract_source_paths(config)

    if explicit_mode in {"code", "hybrid", "written"}:
        mode = explicit_mode
    else:
        is_proof = (
            language == "proof"
            or course in THEORETICAL_COURSES
            or config.get("type") == "theoretical"
        )
        has_written_source = _has_written_source_file(config)
        has_written_questions = bool(config.get("written_questions"))

        if is_proof:
            mode = "written"
        elif rubric_path or writeup_sections or has_written_source or has_written_questions:
            mode = "hybrid"
        else:
            mode = "code"

    return {
        "mode": mode,
        "config": config,
        "rubric_path": rubric_path,
        "writeup_sections": writeup_sections,
        "writeup_required": mode in {"written", "hybrid"},
        "preferred_source_paths": preferred_sources,
        "source_paths": preferred_sources or fallback_sources,
    }


def workspace_needs_llm_grading(ws):
    meta = detect_assignment_mode(ws)
    return meta["mode"] in {"written", "hybrid"}


def _format_source_targets(meta):
    paths = meta["source_paths"][:6]
    if not paths:
        return ""

    label = "Configured student-owned files" if meta["preferred_source_paths"] else "Config metadata points to these primary files"
    lines = "\n".join(f"- {path}" for path in paths)
    suffix = "\n- ..." if len(meta["source_paths"]) > 6 else ""
    return f"{label}:\n{lines}{suffix}"


def _format_writeup_targets(meta):
    if not meta["writeup_required"]:
        return ""

    sections = meta["writeup_sections"][:6]
    if not sections:
        return "Written component:\n- Complete every question or placeholder in writeup.md before you finish."

    lines = "\n".join(f"- {section}" for section in sections)
    suffix = "\n- ..." if len(meta["writeup_sections"]) > 6 else ""
    return f"Written component required in writeup.md. Target sections:\n{lines}{suffix}"


def build_base_system_prompt():
    """Build the agent-agnostic base system prompt.

    This provides behavioral guidance, tool usage rules, and the student
    identity framing.  It is prepended to every solver prompt so that all
    CLI agents (Claude Code, Codex, Gemini, Opencode) share identical
    foundational instructions regardless of delivery mechanism.
    """
    return textwrap.dedent("""\
        # Identity

        You are a computer science student working on a class assignment.
        Your sole objective is to produce a correct, complete, and well-crafted
        solution. You are being evaluated — treat this like a real class
        submission where your grade depends on the quality of your work.

        # Workspace Rules

        - Stay inside your workspace directory at all times. Do NOT cd out of
          it or access files outside it (except the project root for reference).
        - Only edit the source files you are told to edit. Never modify provided
          tests or template files. Do not modify build files or Makefiles unless
          the assignment section below explicitly allows it.

        # Mandatory Tools

        You have two critical tools provided via MCP:

        1. **test** — Compiles (if needed) and runs the test suite against your
           code. You MUST use this tool to test. Do NOT run compilers, test
           runners, or build commands manually via the shell (no `python`,
           `pytest`, `javac`, `java`, `make`, `gcc`, `go test`, etc.).
           The test tool handles all of that in the correct environment.

        2. **submit** — Runs a final evaluation (may include private tests or
           LLM grading for written work). Only call this when you believe your
           solution is complete. For written assignments, submission is
           one-attempt-only.

        Always use these tools. Skipping them means your work cannot be graded.

        # Using Your Tools Effectively

        You have access to dedicated tools for file operations. Use them instead
        of shell commands — they are faster and more reliable:
        - **Read** to read files (not `cat`, `head`, or `tail`)
        - **Write** to create new files (not `echo >` or heredocs)
        - **Edit** to modify existing files (not `sed` or `awk`)
        - **Glob** to find files by pattern (not `find` or `ls`)
        - **Grep** to search file contents (not `grep` or `rg`)
        - **Bash** only for git commands or operations that genuinely require
          a shell. Reserve it for when no dedicated tool fits.

        Read a file before editing it. Understand existing code before changing
        it. If you need to call multiple tools and the calls are independent of
        each other, make them in parallel to save time.

        # Working Smart

        - **Start by reading.** Read the assignment instructions and existing
          source code before writing anything.
        - **Test early and often.** Run the test tool after your first compiling
          version, not after you think everything is done. Small feedback loops
          catch mistakes before they compound.
        - **If stuck, step back.** When tests keep failing on the same issue,
          re-read the instructions and the failing test output carefully.
          Consider a different approach rather than repeatedly tweaking the same
          code. Do not brute-force your way through — think, then act.
        - **Evaluate tradeoffs carefully.** Beware of over-engineering that
          distracts you from the core problem. But don't shy away from a good
          abstraction when it genuinely simplifies your solution. The right
          amount of complexity is the minimum needed to solve the problem
          correctly and clearly.
        - **Don't give up.** If you can't get everything passing, partial credit
          is better than nothing. Keep working on what you can solve.

        # Code Quality

        - Write clean, correct code. Prioritize correctness over cleverness.
        - Don't introduce security vulnerabilities (injection, path traversal,
          etc.) even in assignment code — it's a bad habit.
        - If the test output says your code is wrong, the problem is in your
          code, not the test environment. Fix your code.
    """)


def build_solver_prompt(ws):
    """Build the shared solver prompt for CLI agents.

    The returned prompt is the complete system prompt: base behavioural
    guidance (from build_base_system_prompt) + task-specific instructions.
    """
    base = build_base_system_prompt()
    ws_dir = ws["dir"]
    meta = detect_assignment_mode(ws)

    if meta["mode"] == "written":
        task = textwrap.dedent(f"""\
            # Assignment

            You are completing a written exam/homework assignment.

            Your workspace is: {ws_dir}

            Workflow:
            1. Read instructions.md to understand all problems
            2. Work through each problem systematically
            3. Write ALL your solutions to writeup.md using proper formatting
            4. Review every answer for completeness before finishing
            5. Call the `submit` tool for final evaluation

            CRITICAL:
            - You MUST write your answers to writeup.md
            - Do not just think through problems — actually write solutions to the file
            - Show your work clearly for partial credit
            - Use proper mathematical notation where needed
            - Address ALL parts of multi-part questions
            - You MUST call `submit` when finished — it is the only way your work gets graded
        """)
        return base.rstrip() + "\n\n" + task.lstrip()

    if ws["language"] == "python":
        file_guidance = textwrap.dedent("""\
            File structure:
            - instructions.md — Assignment description and requirements
            - template.py — Clean template (reference only)
            - solution.py — Your implementation
            - lib/ — Helper modules (already available, just import them)

            Do NOT manually run python, pytest, or unittest — use the test tool.
        """)
    elif ws["language"] == "java":
        file_guidance = textwrap.dedent("""\
            File structure:
            - instructions.md — Read this first; it names the exact file to edit
            - skeleton_code/src/main/... or src/main/... — Java source files to implement
            - skeleton_code/src/test/... or src/test/... — JUnit test files (reference only)

            Do NOT run Maven, javac, or java commands directly — use the test tool.
        """)
    elif ws["language"] == "c":
        if meta["source_paths"]:
            file_guidance = textwrap.dedent("""\
                File structure:
                - instructions.md — Assignment description and requirements
                - *.c — C source files to implement
                - Makefile — Build configuration (reference only)

                Do NOT run make, gcc, or any compilation commands directly — use the test tool.
                The test tool compiles and tests inside Docker when needed.
            """)
        else:
            file_guidance = textwrap.dedent("""\
                File structure:
                - instructions.md — Assignment description and requirements
                - Makefile — Build configuration (you may modify this if needed)

                You must create all .c and .h source files from scratch.
                You may also modify the Makefile if your design requires it.
                Do NOT run make, gcc, or any compilation commands directly — use the test tool.
                The test tool compiles and tests on the remote server.
            """)
    elif ws["language"] == "typescript":
        file_guidance = textwrap.dedent("""\
            File structure:
            - instructions.md — Assignment requirements
            - src/main.ts — Entry point (keep this thin — use it for wiring/bootstrap only)
            - src/components/... — Web components and UI modules
            - src/... — Additional app modules (state, api, types, utils)

            You may create new files/folders under src/ as needed.
            Prefer modular code: split components, services, and types into separate files.
        """)
    else:
        file_guidance = textwrap.dedent("""\
            File structure:
            - instructions.md — Assignment description and requirements
        """)

    metadata_guidance = "\n\n".join(
        block for block in (
            _format_source_targets(meta),
            _format_writeup_targets(meta),
        ) if block
    )
    if metadata_guidance:
        metadata_guidance = "\n\n" + metadata_guidance

    malloc_note = ""
    if ws["name"] == "malloc":
        malloc_note = textwrap.dedent("""\

            MALLOC LAB — The test tool runs ALL trace files and reports both
            correctness AND your aggregate performance index.
            Perf index = X/40 (utilization) + Y/60 (throughput) = Z/100.
            Your goal: get all traces passing, then maximize perf index (target: 90+/100).
            Use segregated free lists and best-fit for high scores.
            NOTE: A perfect 100/100 is extremely difficult and not expected. Keep optimizing
            as long as you're making meaningful progress, but stop when you've hit diminishing
            returns and don't see a clear path to improvement.
        """)

    prev_solution_dir = Path(ws_dir) / "previous_solution"
    prev_solution_note = ""
    if prev_solution_dir.is_dir() and any(prev_solution_dir.iterdir()):
        prev_solution_note = textwrap.dedent("""\

        IMPORTANT: This assignment builds on your previous work.
        The previous_solution/ directory contains your solution from the prior assignment.
        Review it to understand the code you already wrote — this assignment extends it.
        """)

    writeup_step = ""
    if meta["writeup_required"]:
        writeup_step = textwrap.dedent("""\
            7. Complete the required written component in writeup.md.
               Replace every placeholder and answer every required section before you finish.
               Then try the `submit` tool. If submit fails, that's OK — make sure your
               answers are saved in writeup.md. You MUST complete this step.
        """)

    if meta["source_paths"]:
        rules_edit_line = "Only edit the source files listed above (and tests/agent/ for your own tests)."
    else:
        rules_edit_line = "You may create and edit any source files needed. You may also write tests under tests/agent/."

    task = textwrap.dedent(f"""\
        # Assignment

        You are solving a programming assignment.

        Your workspace is: {ws_dir}

        {file_guidance}{metadata_guidance}
        {prev_solution_note}
        Workflow:
        1. Read instructions.md to understand the assignment
        2. Read the source file(s) to see what needs implementing
        3. Implement incrementally — one function or method at a time.
           Do NOT try to write an entire large file in a single response.
           Start with a compiling skeleton (method signatures + stubs),
           then fill in logic piece by piece.
        4. Run the `test` tool (no arguments needed) EARLY and OFTEN.
           Do not wait until you think everything is done — test after
           your first compiling version, then iterate.
        5. Read the test output — fix any failures
        6. Repeat steps 3-5 until all tests pass (or you've done your best)
        {writeup_step}

        Testing:
        - Public tests are visible but NOT comprehensive — passing them all does not
          guarantee correctness. You MUST write your own tests under tests/agent/ to cover
          edge cases and build confidence before submitting.
        - `test` runs public + agent tests (unlimited). `submit` runs public + private (once).
        - Only submit when the entire assignment is complete, including any required writeup.

        Rules:
        - {rules_edit_line}
        {malloc_note}
    """)
    return base.rstrip() + "\n\n" + task.lstrip()


_GENERIC_GRADING_PRINCIPLES = """\
## Grading Principles

### 1. Correctness is Primary
- The core question is: **Is the solution correct?**
- A correct but inelegant solution should receive full marks
- A beautifully written but incorrect solution should receive few marks
- Partial credit is awarded for demonstrating understanding even when the final answer is wrong

### 2. For Proofs
- **Logical validity**: Does each step follow from the previous? Are there gaps?
- **Completeness**: Are all cases handled? Are base cases and inductive steps present?
- **Correct use of definitions**: Are formal definitions applied accurately?

### 3. For Algorithms
- **Correctness**: Does the algorithm produce the right output for all valid inputs?
- **Complexity analysis**: Is the time/space analysis correct and justified?
- **Pseudocode quality**: Is the algorithm clearly specified?

### 4. Partial Credit Guidelines
| Fraction | Criteria |
|----------|----------|
| 100% | Correct, complete, well-justified solution |
| 80-90% | Correct approach with minor gaps or small errors |
| 60-70% | Right general idea, but significant gaps |
| 40-50% | Demonstrates understanding, partially correct |
| 20-30% | Some relevant observations but fundamentally incomplete |
| 0-10% | No meaningful progress |

### 5. Important
- **Be generous with partial credit** for students who demonstrate understanding
- **Be strict about logical correctness** - an incorrect solution is incorrect
- **Accept equivalent correct solutions** - there may be multiple valid approaches"""


_GENERIC_CODE_REVIEW_RUBRIC = """\
## Code Review Rubric

Evaluate the code across five dimensions, scoring each 0-20 for a total of 0-100.

### 1. Correctness & Completeness (0-20)
- Does the code solve the problem as specified?
- Are edge cases handled appropriately?
- Are all required functions/methods implemented?

### 2. Code Quality (0-20)
- Is the code readable and well-structured?
- Are names descriptive and consistent?
- Is the code properly decomposed into functions/methods?
- Is there unnecessary duplication?

### 3. Style & Conventions (0-20)
- Does the code follow the language's standard conventions?
- Is formatting consistent?
- Are comments useful (not excessive or absent)?

### 4. Assignment Spirit (0-20)
- Does the solution use the techniques/concepts the assignment is teaching?
- Does it avoid bypassing the problem (e.g., hardcoding, using prohibited libraries)?
- Does it demonstrate understanding of the underlying concepts?

### 5. Robustness (0-20)
- Does the code handle unexpected input gracefully?
- Are there hardcoded test values or magic numbers?
- Would the code work on inputs beyond the provided test cases?"""


_CODE_REVIEW_OUTPUT_FORMAT = """\
## Output Format

You MUST return a JSON object with EXACTLY this structure:

{
  "dimensions": [
    {
      "name": "Correctness & Completeness",
      "score": 18,
      "maxScore": 20,
      "strengths": ["Handles all edge cases", "Complete implementation"],
      "issues": ["Minor: could validate input types"],
      "feedback": "Detailed feedback in Markdown (use **bold**, `code`, lists, etc.)"
    }
  ],
  "overallScore": 82,
  "overallMaxScore": 100,
  "overallComments": "Summary of the code review with key observations (Markdown formatted)"
}

CRITICAL REQUIREMENTS:
1. Return ONLY valid JSON - no markdown, no explanation outside the JSON
2. There must be exactly 5 dimensions in the order specified in the rubric
3. Scores must be integers 0-20
4. overallScore must equal the sum of all dimension scores
5. overallMaxScore must always be 100
6. Each dimension must have at least one entry in strengths OR issues
7. Use Markdown formatting inside feedback and overallComments (bold, code spans, lists, etc.)"""


_OUTPUT_FORMAT_INSTRUCTIONS = """\
## Output Format

You MUST return a JSON object with EXACTLY this structure:

{
  "problems": [
    {
      "problemNumber": 1,
      "problemName": "Problem title",
      "pointsEarned": 15,
      "pointsMax": 20,
      "approach": "Brief description of student's approach",
      "strengths": ["What they did well", "Another strength"],
      "errors": ["Specific error 1", "Error 2"],
      "feedback": "Detailed feedback in Markdown (use **bold**, `code`, lists, etc.)",
      "rubricBreakdown": [
        {
          "criterion": "Correctness",
          "points": 10,
          "maxPoints": 12,
          "reason": "Why this score"
        }
      ]
    }
  ],
  "overallComments": "Summary of patterns across all problems (Markdown formatted)",
  "totalPointsEarned": 75,
  "totalPointsMax": 100
}

CRITICAL REQUIREMENTS:
1. Return ONLY valid JSON - no markdown, no explanation outside the JSON
2. Every problem in the submission MUST have a corresponding entry in "problems"
3. Points must be numbers (not strings)
4. totalPointsEarned must equal the sum of all pointsEarned
5. totalPointsMax must equal the sum of all pointsMax
6. Use Markdown formatting inside feedback and overallComments (bold, code spans, lists, etc.)"""


def build_grading_prompt(ws, instructions_text=None, submission_text=None):
    """Build the shared written grading prompt."""
    ws_dir = Path(ws["dir"])
    course = ws["course"]
    assignment_number = ws["assignment_number"]
    assignment_name = ws.get("name", "")
    display_name = ws.get("display_name", "")

    if instructions_text is None:
        instructions_path = ws_dir / "instructions.md"
        if not instructions_path.exists():
            raise FileNotFoundError(f"Instructions not found at {instructions_path}")
        instructions = instructions_path.read_text()
    else:
        instructions = instructions_text

    if submission_text is None:
        writeup_path = ws_dir / "writeup.md"
        if not writeup_path.exists():
            raise FileNotFoundError(f"Submission not found at {writeup_path}")
        submission = writeup_path.read_text()
    else:
        submission = submission_text

    rubric_path = find_rubric_file(course, assignment_number, assignment_name)
    rubric = rubric_path.read_text() if rubric_path else None

    ref_path = find_reference_solution(course, assignment_number)
    reference = ref_path.read_text() if ref_path else None

    gp_path = find_grading_prompt_file(course)
    grading_prompt = gp_path.read_text() if gp_path else None

    sections = []

    aref = f'"{display_name or assignment_name}"' if (display_name or assignment_name) else f"Assignment {assignment_number}"
    cref = f" for {course.upper()}" if course else ""
    intro = (
        f"You are an expert grading assistant evaluating a student submission for {aref}{cref}.\n\n"
        "Your task is to:\n"
        "1. Carefully read the assignment instructions\n"
        "2. Evaluate the student's submission against the requirements"
    )
    if reference:
        intro += "\n3. Compare the student's work to the reference solution"
    if rubric:
        intro += f"\n{4 if reference else 3}. Apply the grading rubric to assign points"
    intro += "\n\nProvide a detailed, fair grade with constructive feedback."
    sections.append(intro)

    if grading_prompt:
        sections.append("## Course-Specific Grading Instructions\n\n" + grading_prompt)
    else:
        sections.append(_GENERIC_GRADING_PRINCIPLES)

    sections.append(f"## Assignment Instructions\n\nThe student was given the following assignment:\n\n```\n{instructions}\n```")

    if reference:
        sections.append(
            "## Reference Solution\n\n"
            "Use this reference solution to evaluate the student's work. "
            "Note that equivalent correct solutions that differ from this reference should receive full credit.\n\n"
            f"```\n{reference}\n```"
        )

    if rubric:
        sections.append(f"## Grading Rubric\n\nApply this rubric when assigning points:\n\n```\n{rubric}\n```")

    sections.append(f"## Student Submission\n\nGrade the following student submission:\n\n```\n{submission}\n```")
    sections.append(_OUTPUT_FORMAT_INSTRUCTIONS)

    final = "## Your Task\n\nNow grade the student submission above."
    if rubric:
        final += " Apply the rubric criteria carefully."
    if reference:
        final += " Compare against the reference solution but accept equivalent correct approaches."
    final += (
        "\n\nRemember:\n"
        "- Award partial credit for demonstrated understanding\n"
        "- Be specific about errors and how to fix them\n"
        "- Ensure your point totals are mathematically correct\n"
        "- Return ONLY the JSON object, no other text"
    )
    sections.append(final)

    return "\n\n---\n\n".join(sections)


def build_code_review_prompt(ws, solution_files=None, diff=None, instructions_text=None):
    """Build the code review prompt for LLM-based code evaluation.

    Accepts optional pre-computed solution_files and diff to avoid redundant
    git subprocess calls when the caller has already read them.
    """
    ws_dir = Path(ws["dir"])
    course = ws["course"]
    assignment_number = ws["assignment_number"]
    assignment_name = ws.get("name", "")
    display_name = ws.get("display_name", "")
    language = ws.get("language", "python")

    # Read assignment instructions
    if instructions_text is None:
        instructions_path = ws_dir / "instructions.md"
        if not instructions_path.exists():
            raise FileNotFoundError(f"Instructions not found at {instructions_path}")
        instructions = instructions_path.read_text()
    else:
        instructions = instructions_text

    # Read agent's solution files (use pre-computed if provided)
    if solution_files is None:
        solution_files = read_solution_files(ws_dir)
    if not solution_files:
        raise FileNotFoundError(f"No solution files found in {ws_dir}")

    # Read the diff (use pre-computed if provided)
    if diff is None:
        diff = read_solution_diff(ws_dir)

    # Optional context
    style_guide_path = find_style_guide(course)
    style_guide = style_guide_path.read_text() if style_guide_path else None

    ref_path = find_reference_solution_code(course, assignment_number, assignment_name)
    reference = ref_path.read_text() if ref_path else None

    review_prompt_path = find_code_review_prompt_file(course)
    review_prompt = review_prompt_path.read_text() if review_prompt_path else None

    sections = []

    # Intro
    aref = f'"{display_name or assignment_name}"' if (display_name or assignment_name) else f"Assignment {assignment_number}"
    cref = f" for {course.upper()}" if course else ""
    lang_name = {"python": "Python", "java": "Java", "c": "C", "go": "Go", "typescript": "TypeScript"}.get(language, language)

    intro = (
        f"You are an expert code reviewer evaluating a {lang_name} solution for {aref}{cref}.\n\n"
        "Your task is to:\n"
        "1. Read the assignment instructions to understand what was required\n"
        "2. Review the student's code for quality, correctness, and style\n"
        "3. Evaluate whether the solution follows the spirit of the assignment\n"
        "4. Score each dimension of the review rubric\n\n"
        "Provide a thorough, fair code review with constructive feedback."
    )
    sections.append(intro)

    # Review criteria (course-specific or generic)
    if review_prompt:
        sections.append("## Course-Specific Review Criteria\n\n" + review_prompt)

    sections.append(_GENERIC_CODE_REVIEW_RUBRIC)

    if style_guide:
        sections.append(f"## Style Guide\n\nThe course uses this style guide. Evaluate adherence:\n\n```\n{style_guide}\n```")

    # Assignment context
    sections.append(f"## Assignment Instructions\n\nThe student was given the following assignment:\n\n```\n{instructions}\n```")

    if reference:
        sections.append(
            "## Reference Solution\n\n"
            "This is a reference implementation. The student's approach may differ — "
            "alternative correct approaches are valid. Use this to understand the expected solution strategy.\n\n"
            f"```{language}\n{reference}\n```"
        )

    # Student's code
    file_sections = []
    for fname, content in solution_files.items():
        file_sections.append(f"### {fname}\n```{language}\n{content}\n```")
    sections.append("## Student's Solution\n\n" + "\n\n".join(file_sections))

    if diff:
        sections.append(f"## Changes from Template\n\nThis diff shows what the student changed from the starter code:\n\n```diff\n{diff}\n```")

    sections.append(_CODE_REVIEW_OUTPUT_FORMAT)

    final = "## Your Task\n\nNow review the student's code above."
    if style_guide:
        final += " Evaluate adherence to the style guide."
    if reference:
        final += " Compare against the reference solution but accept valid alternative approaches."
    final += (
        "\n\nRemember:\n"
        "- Focus on substantive issues, not nitpicks\n"
        "- Acknowledge good practices, not just problems\n"
        "- Score each dimension independently based on the rubric\n"
        "- Return ONLY the JSON object, no other text"
    )
    sections.append(final)

    return "\n\n---\n\n".join(sections)


def extract_grade_json(raw_text):
    """Extract a JSON grade object from LLM output."""
    json_match = re.search(r"\{[\s\S]*\}", raw_text)
    if not json_match:
        return None

    try:
        return json.loads(json_match.group())
    except json.JSONDecodeError:
        return None


def format_grade_output(raw_text, ws):
    """Format structured JSON grading output into human-readable text."""
    data = extract_grade_json(raw_text)
    if data is None:
        return raw_text

    course_name = ws["course"].upper()
    hw_name = ws.get("display_name") or ws.get("name") or f"Assignment {ws['assignment_number']}"

    total_earned = data.get("totalPointsEarned", 0)
    total_max = data.get("totalPointsMax", 0)
    problems = data.get("problems", [])
    passed = sum(
        1
        for problem in problems
        if problem.get("pointsEarned", 0) >= problem.get("pointsMax", 0) * 0.6
    )
    pct = round(total_earned / total_max * 100) if total_max > 0 else 0

    lines = [
        f"# {course_name} {hw_name} — LLM Grading Results",
        "",
        "## Results Summary",
        "",
        f"**Passed:** {passed}/{len(problems)} problems ({pct}%)  ",
        f"**Points:** {total_earned}/{total_max}",
        "",
        "## Detailed Results",
        "",
    ]

    for problem in problems:
        status = "✅" if problem.get("pointsEarned", 0) >= problem.get("pointsMax", 0) * 0.6 else "❌"
        name = f"Problem {problem.get('problemNumber', '?')}: {problem.get('problemName', 'Unknown')}"
        lines.append(f"### {status} {name} ({problem.get('pointsEarned', 0)}/{problem.get('pointsMax', 0)} pts)")
        lines.append("")
        if problem.get("feedback"):
            lines.append(problem["feedback"])
            lines.append("")
        if problem.get("errors"):
            lines.append(f"> **Errors:** {'; '.join(problem['errors'])}")
            lines.append("")

    if data.get("overallComments"):
        lines.extend(["## Overall Comments", "", data["overallComments"], ""])

    lines.append("---")
    return "\n".join(lines)


def parse_grade_score(raw_text):
    """Return a simple earned/max score string when JSON grading output is available."""
    data = extract_grade_json(raw_text)
    if data is None:
        return None

    earned = data.get("totalPointsEarned")
    total = data.get("totalPointsMax")
    if earned is None or total is None:
        return None
    return f"{earned}/{total}"



def format_code_review_output(raw_text, ws):
    """Format code review JSON output into a human-readable report."""
    data = extract_grade_json(raw_text)
    if data is None:
        return raw_text

    course_name = ws["course"].upper()
    hw_name = ws.get("display_name") or ws.get("name") or f"Assignment {ws['assignment_number']}"

    overall_score = data.get("overallScore", 0)
    overall_max = data.get("overallMaxScore", 100)
    dimensions = data.get("dimensions", [])

    lines = [
        f"# {course_name} {hw_name} — Code Review Results",
        "",
        "## Results Summary",
        "",
        f"**Score:** {overall_score}/{overall_max}",
        "",
        "## Dimension Scores",
        "",
    ]

    for dim in dimensions:
        name = dim.get("name", "Unknown")
        score = dim.get("score", 0)
        max_score = dim.get("maxScore", 20)
        lines.append(f"### {name}: {score}/{max_score}")
        lines.append("")

        strengths = dim.get("strengths", [])
        if strengths:
            lines.append("**Strengths:**")
            for s in strengths:
                lines.append(f"- {s}")
            lines.append("")

        issues = dim.get("issues", [])
        if issues:
            lines.append("**Issues:**")
            for issue in issues:
                lines.append(f"- {issue}")
            lines.append("")

        feedback = dim.get("feedback", "")
        if feedback:
            lines.append(feedback)
            lines.append("")

    if data.get("overallComments"):
        lines.extend(["## Overall Comments", "", data["overallComments"], ""])

    lines.append("---")
    lines.append("")
    lines.append("<details>")
    lines.append("<summary>Raw JSON</summary>")
    lines.append("")
    lines.append("```json")
    lines.append(json.dumps(data, indent=2))
    lines.append("```")
    lines.append("")
    lines.append("</details>")

    return "\n".join(lines)


def parse_code_review_score(raw_text):
    """Extract overall score from code review JSON output.

    Returns a string like "82/100" or None.
    """
    data = extract_grade_json(raw_text)
    if data is None:
        return None

    score = data.get("overallScore")
    max_score = data.get("overallMaxScore")
    if score is None or max_score is None:
        return None
    return f"{score}/{max_score}"


if __name__ == "__main__":
    import sys

    def _fail(msg):
        print(msg, file=sys.stderr)
        sys.exit(1)

    if len(sys.argv) < 2:
        _fail("Usage: python3 bin/_prompts.py <command> <json>")

    command = sys.argv[1]

    if command in ("solver", "grading", "code-review", "detect-mode", "needs-llm-grading"):
        if len(sys.argv) < 3:
            _fail(f"Usage: python3 bin/_prompts.py {command} '<json>'")
        try:
            ws = json.loads(sys.argv[2])
        except json.JSONDecodeError as exc:
            _fail(f"Invalid JSON: {exc}")
    else:
        _fail(f"Unknown command: {command}")

    try:
        if command == "solver":
            print(build_solver_prompt(ws), end="")
        elif command == "grading":
            print(build_grading_prompt(ws), end="")
        elif command == "code-review":
            print(build_code_review_prompt(ws), end="")
        elif command == "detect-mode":
            result = detect_assignment_mode(ws)
            print(json.dumps({
                "mode": result["mode"],
                "writeup_required": result["writeup_required"],
            }))
        elif command == "needs-llm-grading":
            print("true" if workspace_needs_llm_grading(ws) else "false")
    except Exception as exc:
        _fail(f"Error: {exc}")
