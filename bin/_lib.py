"""
Shared utility functions for bin/ scripts.

Extracted from bench-cli, reset-workspaces, archive-run, and grade
to eliminate duplication. All functions preserve their original behavior.
"""

import json
import os
import re
from pathlib import Path

try:
    import yaml as _yaml
except ImportError:
    _yaml = None

PROJECT_ROOT = Path(__file__).resolve().parent.parent
WORKSPACES_DIR = PROJECT_ROOT / "workspaces"

# Courses that are theoretical (proof-based, LLM-graded, no code tests).
# Keep in sync with agent/workspaceConfig.ts THEORETICAL_COURSES.
THEORETICAL_COURSES = {"comp182", "comp382"}


# ── Environment ──────────────────────────────────────────────────────────────

def make_agent_env():
    """Build environment for agent subprocess (remove CLAUDECODE to avoid nested-session error)."""
    return {k: v for k, v in os.environ.items() if k != "CLAUDECODE"}


def load_dotenv():
    """Load .env file from project root into os.environ (setdefault).

    Strips quotes from values so KEY="value" and KEY=value both work.
    """
    env_file = PROJECT_ROOT / ".env"
    if env_file.is_file():
        with open(env_file) as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith("#") and "=" in line:
                    key, _, value = line.partition("=")
                    os.environ.setdefault(key.strip(), value.strip().strip('"').strip("'"))


# ── Model slug ───────────────────────────────────────────────────────────────

def model_slug(model_name):
    """Convert a model name to a filesystem-safe directory slug.

    Rules: lowercase, dots→dashes, spaces→dashes, slashes→dashes,
    collapse runs of dashes, strip leading/trailing dashes.

    Examples:
        claude-opus-4-6       -> claude-opus-4-6
        claude-opus-4.6       -> claude-opus-4-6
        gpt-5.4               -> gpt-5-4
        gemini-3-flash-preview -> gemini-3-flash-preview
        openrouter/anthropic/claude-sonnet-4-5  -> openrouter-anthropic-claude-sonnet-4-5
        minimax/minimax-m2.5  -> minimax-minimax-m2-5
    """
    if not model_name:
        return ""
    slug = model_name.lower()
    slug = slug.replace(".", "-").replace("/", "-").replace(" ", "-").replace("_", "-").replace(":", "-")
    # Collapse runs of dashes
    slug = re.sub(r"-{2,}", "-", slug)
    slug = slug.strip("-")
    return slug


# ── Workspace discovery ──────────────────────────────────────────────────────

def read_workspace_yaml(ws_dir):
    """Read workspace.yaml from a directory. Returns dict or None."""
    ws_yaml = ws_dir / "workspace.yaml"
    try:
        if _yaml:
            with open(ws_yaml) as f:
                return _yaml.safe_load(f) or {}
        else:
            # Fallback: simple key: value parser
            info = {}
            with open(ws_yaml) as f:
                for line in f:
                    line = line.strip()
                    if ":" in line and not line.startswith("#"):
                        key, _, val = line.partition(":")
                        val = val.strip().strip('"').strip("'")
                        info[key.strip()] = val
            return info
    except FileNotFoundError:
        return None


def workspace_entry(ws_dir, info):
    """Build a workspace dict from a directory and its parsed YAML info."""
    course = info.get("course", "")
    language = info.get("language", "")
    module_name = info.get("module_name", info.get("assignment_name", ""))
    display_name = info.get("display_name", "")
    if not display_name and module_name:
        display_name = module_name.replace("_", " ").title()
    assignment_number = info.get("assignment_number",
                       info.get("module_number",
                       info.get("homework_number",
                       info.get("project_number", 0))))
    try:
        assign_num = int(assignment_number)
    except (ValueError, TypeError):
        assign_num = 0
    return {
        "id": ws_dir.name,
        "dir": str(ws_dir),
        "course": course,
        "language": language,
        "assignment_mode": info.get("assignment_mode", ""),
        "name": module_name,
        "display_name": display_name,
        "assignment_number": assign_num,
        "depends_on": info.get("depends_on", ""),
    }


def discover_workspaces(course_filter=None, suffix=None, model_dir=None):
    """Find all workspaces, optionally filtered by course, suffix, and/or model directory.

    When model_dir is given, look inside WORKSPACES_DIR / model_dir/ for workspaces.
    Otherwise, fall back to flat workspace discovery (backward compatible).

    Returns list of dicts with keys: id, dir, course, language,
    assignment_mode, name, display_name, assignment_number.
    """
    if not WORKSPACES_DIR.exists():
        return []

    # Determine the root directory to scan for workspaces
    if model_dir:
        scan_root = WORKSPACES_DIR / model_dir
        if not scan_root.is_dir():
            return []
    else:
        scan_root = WORKSPACES_DIR

    # Short-circuit for exact workspace name match
    if course_filter and suffix is None and model_dir is None:
        exact = scan_root / course_filter
        if exact.is_dir():
            info = read_workspace_yaml(exact)
            if info is not None:
                return [workspace_entry(exact, info)]
    elif course_filter and model_dir:
        exact = scan_root / course_filter
        if exact.is_dir():
            info = read_workspace_yaml(exact)
            if info is not None:
                return [workspace_entry(exact, info)]

    workspaces = []
    for ws_dir in sorted(scan_root.iterdir()):
        if not ws_dir.is_dir():
            continue

        # Skip the runs/ subdirectory when scanning a model directory
        if model_dir and ws_dir.name == "runs":
            continue

        # When scanning flat (no model_dir), skip directories that look like
        # model directories (they contain workspaces, not are workspaces).
        # A model dir has no workspace.yaml but contains subdirs that do.
        if not model_dir and not (ws_dir / "workspace.yaml").exists() and not (ws_dir / "workspace.json").exists():
            continue

        # Skip early by directory name prefix when filtering by course
        if course_filter and not ws_dir.name.startswith(course_filter):
            continue

        info = read_workspace_yaml(ws_dir)
        if info is None:
            continue

        entry = workspace_entry(ws_dir, info)

        if course_filter and entry["course"] != course_filter and ws_dir.name != course_filter:
            continue

        if suffix is not None:
            if suffix == "":
                # No suffix: exclude workspaces that have a suffix after the base name
                expected_base = f"{entry['course']}_{entry['name']}"
                if ws_dir.name != expected_base:
                    continue
            else:
                # Match workspaces ending with _<suffix>
                if not ws_dir.name.endswith(f"_{suffix}"):
                    continue

        workspaces.append(entry)

    return workspaces

# ── Grade data extraction ────────────────────────────────────────────────────

def _normalize_test_results(raw):
    """Normalize per-test results into a unified schema.

    Unified test result schema:
        name: str           - Test name
        passed: bool        - Whether the test passed
        points: float       - Points earned
        max_points: float   - Points possible
        error: str|null     - Error message if any
        expected: str|null  - Expected output
        actual: str|null    - Actual output

    Both formats now use "tests" with unified field names. Fallbacks
    for legacy "test_results" / old field names are kept for reading
    old result files.

    Returns list of dicts, or empty list if no test data found.
    """
    tests = []

    # Primary: unified "tests" key; fallback: legacy "test_results" key
    raw_tests = raw.get("tests", raw.get("test_results", []))

    for t in raw_tests:
        tests.append({
            "name": t.get("name", t.get("test_name", "")),
            "passed": bool(t["passed"]) if "passed" in t else t.get("status", "").lower() == "pass",
            "points": float(t.get("points", t.get("points_earned", 0))),
            "max_points": float(t.get("max_points", t.get("points_possible", 1))),
            "error": t.get("error") or t.get("error_message") or None,
            "expected": str(t["expected"]) if t.get("expected") is not None else None,
            "actual": str(t["actual"]) if t.get("actual") is not None else None,
        })

    return tests


def extract_grade_data(raw):
    """Extract structured grade fields from JSON grade output.

    Both formats now produce the unified schema with "passed"/"total"/
    "points"/"max_points"/"tests".  Fallbacks for legacy field names
    (tests_passed, tests_total, total_points_earned, total_points_possible)
    are kept for reading old result files.
    """
    # Primary path: unified schema (passed/total/points/max_points)
    # Fallback path: legacy framework format (tests_passed/tests_total/total_points_earned)
    passed = raw.get("passed", raw.get("tests_passed"))
    total = raw.get("total", raw.get("tests_total"))

    if passed is None or total is None:
        return None

    passed = int(passed)
    total = int(total)
    points = float(raw.get("points", raw.get("total_points_earned", passed)))
    max_points = float(raw.get("max_points", raw.get("total_points_possible", total)))
    pct = (points / max_points * 100) if max_points > 0 else 0

    result = {
        "tests_passed": passed,
        "tests_total": total,
        "points_earned": points,
        "points_possible": max_points,
        "score_percentage": round(pct, 1),
        "test_results": _normalize_test_results(raw),
    }
    if "performance_index" in raw:
        result["performance_index"] = float(raw["performance_index"])
        result["utilization_score"] = float(raw.get("utilization_score", 0))
        result["throughput_score"] = float(raw.get("throughput_score", 0))
    return result


def format_grade_summary(gd):
    """Format a grade data dict into a human-readable summary string."""
    if not gd or gd.get("tests_total", 0) == 0:
        return None
    tp, tt = gd["tests_passed"], gd["tests_total"]
    pe, pp = gd["points_earned"], gd["points_possible"]
    pct = gd["score_percentage"]
    if pp != tt:
        return f"Points: {pe:.0f}/{pp:.0f} ({pct:.1f}%)"
    return f"Passed: {tp}/{tt} tests ({pct:.1f}%)"


def accumulate_by_course(by_course, course, metrics, gd):
    """Accumulate per-course aggregates into by_course dict."""
    if not course:
        return
    if course not in by_course:
        by_course[course] = {
            "workspaces": 0, "cost_usd": 0, "duration_ms": 0,
            "tests_passed": 0, "tests_total": 0,
            "points_earned": 0.0, "points_possible": 0.0,
        }
    bc = by_course[course]
    bc["workspaces"] += 1
    bc["cost_usd"] += metrics.get("cost_usd", 0)
    bc["duration_ms"] += metrics.get("duration_ms", 0)
    if gd and gd.get("tests_total", 0) > 0:
        bc["tests_passed"] += gd["tests_passed"]
        bc["tests_total"] += gd["tests_total"]
        bc["points_earned"] += gd["points_earned"]
        bc["points_possible"] += gd["points_possible"]


DEFAULT_GRADE = {
    "tests_passed": 0, "tests_total": 0,
    "points_earned": 0.0, "points_possible": 0.0,
    "score_percentage": 0.0, "test_results": [],
}


# ── Model ID normalization ───────────────────────────────────────────────────

# Claude CLI outputs dash-separated model IDs (e.g. claude-opus-4-6) while the
# Anthropic API and OpenRouter use dots (claude-opus-4.6).  We normalize to dots
# so that model IDs are consistent across summary.json, reviewer tags, and
# filename-derived keys.

_MODEL_DOT_FIXUPS = re.compile(
    r"(?<=-)"           # preceded by a dash
    r"(\d+)"            # major version
    r"-"                # dash that should be a dot
    r"(\d+)"            # minor version number
    r"(?=-|$)"          # followed by dash or end of string
)


def normalize_model_id(model_id):
    """Normalize a model ID to use dots for version separators.

    Examples:
        claude-opus-4-6         -> claude-opus-4.6
        claude-sonnet-4-6       -> claude-sonnet-4.6
        claude-haiku-4-5-20251001 -> claude-haiku-4.5-20251001
        gpt-5-3-codex           -> gpt-5.3-codex
        gpt-4-1                 -> gpt-4.1
        already-dotted-4.6      -> already-dotted-4.6  (no change)
    """
    if not model_id or "." in model_id:
        return model_id
    return _MODEL_DOT_FIXUPS.sub(r"\1.\2", model_id)


# ── Metrics extraction ───────────────────────────────────────────────────────

def extract_metrics(result_dir):
    """Extract cost, token, and duration info from CC JSON output."""
    json_file = result_dir / "agent_output.json"
    empty = {
        "cost_usd": 0, "input_tokens": 0, "output_tokens": 0,
        "cache_creation_tokens": 0, "cache_read_tokens": 0,
        "duration_ms": 0, "duration_api_ms": 0, "num_turns": 0,
        "session_id": "", "model_id": "",
    }
    if not json_file.exists():
        return empty

    try:
        data = json.loads(json_file.read_text())
        cost = data.get("total_cost_usd") or 0
        usage = data.get("usage") or {}

        # Resolve full model ID from modelUsage (primary = highest cost)
        model_id = ""
        model_usage = data.get("modelUsage") or {}
        if model_usage:
            model_id = max(model_usage, key=lambda k: model_usage[k].get("costUSD", 0))

        return {
            "cost_usd": float(cost),
            "input_tokens": int(usage.get("input_tokens", 0)),
            "output_tokens": int(usage.get("output_tokens", 0)),
            "cache_creation_tokens": int(usage.get("cache_creation_input_tokens", 0)),
            "cache_read_tokens": int(usage.get("cache_read_input_tokens", 0)),
            "duration_ms": int(data.get("duration_ms", 0)),
            "duration_api_ms": int(data.get("duration_api_ms", 0)),
            "num_turns": int(data.get("num_turns", 0)),
            "session_id": data.get("session_id", ""),
            "model_id": normalize_model_id(model_id),
        }
    except (json.JSONDecodeError, TypeError, ValueError, AttributeError):
        return empty


# ── LLM grade parsing ────────────────────────────────────────────────────────

# Keywords that indicate a line contains an LLM grade score (e.g. "Points: 18/20")
LLM_SCORE_KEYWORDS = ["Score", "Total", "Points", "points", "Passed"]


def _parse_llm_file(llm_file):
    """Parse a single LLM grade file into structured data (best-effort).

    Returns {"status", "points_earned", "points_possible", "score_percentage", "feedback"}
    or None if file is empty.
    """
    text = llm_file.read_text().strip()
    if not text:
        return None

    if text.startswith("Error:") or text.startswith("Traceback"):
        return {"status": "error", "feedback": text}

    result = {"status": "graded", "feedback": text}

    for line in reversed(text.splitlines()):
        m = re.search(r'(\d+(?:\.\d+)?)\s*/\s*(\d+(?:\.\d+)?)', line)
        if m and any(kw in line for kw in LLM_SCORE_KEYWORDS):
            earned = float(m.group(1))
            possible = float(m.group(2))
            pct = (earned / possible * 100) if possible > 0 else 0
            result["points_earned"] = earned
            result["points_possible"] = possible
            result["score_percentage"] = round(pct, 1)
            break

    return result


def _tag_from_filename(filename, prefix):
    """Extract model tag from a tagged result filename (e.g. 'llm_grade_result_opus.md' -> 'opus')."""
    stem = Path(filename).stem
    if stem.startswith(prefix) and len(stem) > len(prefix):
        return stem[len(prefix):]
    return "default"


def _parse_all_tagged_summaries(result_dir, glob_prefix, filename_prefix):
    """Parse all model-tagged result files matching a glob prefix.

    Returns dict keyed by model tag: {"opus": {...}, "gpt-5.4": {...}}
    """
    result_dir = Path(result_dir)
    results = {}
    candidates = sorted(result_dir.glob(f"{glob_prefix}*.md"))
    if not candidates:
        candidates = sorted(result_dir.glob(f"{glob_prefix}*.txt"))
    for f in candidates:
        tag = _tag_from_filename(f.name, filename_prefix)
        parsed = _parse_llm_file(f)
        if parsed:
            results[tag] = parsed
    return results


def parse_all_llm_grade_summaries(result_dir):
    """Parse ALL LLM grade files in a result directory."""
    return _parse_all_tagged_summaries(result_dir, "llm_grade_result", "llm_grade_result_")


def parse_all_code_review_summaries(result_dir):
    """Parse ALL code review files in a result directory."""
    return _parse_all_tagged_summaries(result_dir, "code_review_result", "code_review_result_")


def parse_llm_grade_summary(result_dir, grading_model=None):
    """Parse LLM grade text for structured data (best-effort).

    Returns {"points_earned", "points_possible", "score_percentage", "feedback"}
    or None if no LLM grade file exists.

    For multi-grader support, prefer parse_all_llm_grade_summaries().
    """
    result_dir = Path(result_dir)

    if grading_model:
        model_tag = grading_model.split("/")[-1]
        llm_file = result_dir / f"llm_grade_result_{model_tag}.md"
        if not llm_file.exists():
            llm_file = result_dir / f"llm_grade_result_{model_tag}.txt"  # legacy
        if not llm_file.exists():
            return None
        return _parse_llm_file(llm_file)

    # No specific model: find any llm_grade_result file
    all_grades = parse_all_llm_grade_summaries(result_dir)
    if not all_grades:
        return None
    # Return the first one found
    return next(iter(all_grades.values()))
