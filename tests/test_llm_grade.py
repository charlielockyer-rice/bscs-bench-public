"""Regression tests for LLM grading (run_cli_grade in bench-cli)."""

import importlib.machinery
import importlib.util
import json
import subprocess
import sys
from pathlib import Path

import pytest


PROJECT_ROOT = Path(__file__).resolve().parent.parent
BIN_DIR = PROJECT_ROOT / "bin"


@pytest.fixture(scope="module")
def bench_cli_module():
    if str(BIN_DIR) not in sys.path:
        sys.path.insert(0, str(BIN_DIR))
    bench_cli_path = str(PROJECT_ROOT / "bin" / "bench-cli")
    loader = importlib.machinery.SourceFileLoader("bench_cli", bench_cli_path)
    spec = importlib.util.spec_from_loader("bench_cli", loader)
    mod = importlib.util.module_from_spec(spec)
    loader.exec_module(mod)
    return mod


def test_run_cli_grade_uses_shared_prompt_and_formats_output(tmp_path, monkeypatch, bench_cli_module):
    ws_dir = tmp_path / "workspace"
    ws_dir.mkdir()
    (ws_dir / "instructions.md").write_text("Solve the assignment.")
    (ws_dir / "writeup.md").write_text("This is a completed writeup with enough content to grade." * 4)

    result_dir = tmp_path / "results" / "comp140_module1"
    result_dir.mkdir(parents=True)
    ws = {
        "id": "comp140_module1",
        "dir": str(ws_dir),
        "course": "comp140",
        "name": "circles",
        "display_name": "Circles",
        "assignment_number": 1,
        "assignment_mode": "hybrid",
    }
    seen = {}

    monkeypatch.setattr(bench_cli_module, "find_rubric_file", lambda *_args, **_kwargs: PROJECT_ROOT / "comp140" / "rubrics" / "circles_rubric.md")
    monkeypatch.setattr(bench_cli_module, "build_grading_prompt", lambda _ws, **_kw: "shared-grade-prompt")

    class _RunResult:
        stdout = """{
  "problems": [
    {
      "problemNumber": 1,
      "problemName": "Problem",
      "pointsEarned": 8,
      "pointsMax": 10,
      "approach": "Approach",
      "strengths": ["Good"],
      "errors": ["Minor"],
      "feedback": "Solid work",
      "rubricBreakdown": []
    }
  ],
  "overallComments": "Nice job",
  "totalPointsEarned": 8,
  "totalPointsMax": 10
}"""
        stderr = ""
        returncode = 0

    def _fake_run(cmd, **kwargs):
        seen["cmd"] = cmd
        seen["input"] = kwargs.get("input")
        return _RunResult()

    monkeypatch.setattr(bench_cli_module.subprocess, "run", _fake_run)

    score, elapsed = bench_cli_module.run_cli_grade(ws, result_dir, agent="claude", model="opus", force=True)

    assert score == "8/10"
    assert elapsed >= 0
    assert seen["input"] == "shared-grade-prompt"
    assert seen["cmd"][0] == "claude"
    assert (result_dir / "llm_grade_result_opus.md").exists()
    formatted = (result_dir / "llm_grade_result_opus.md").read_text()
    assert "LLM Grading Results" in formatted
    assert "**Points:** 8/10" in formatted


def test_run_cli_grade_skips_template_writeup(tmp_path, monkeypatch, bench_cli_module):
    ws_dir = tmp_path / "workspace"
    ws_dir.mkdir()
    (ws_dir / "writeup.md").write_text("[Your solution here]\n[Your solution here]\n")

    result_dir = tmp_path / "results"
    ws = {
        "id": "test_ws",
        "dir": str(ws_dir),
        "course": "comp140",
        "name": "circles",
        "assignment_number": 1,
    }

    score, info = bench_cli_module.run_cli_grade(ws, result_dir)
    assert score is None
    assert info == "no writeup"


def test_run_cli_grade_skips_already_graded(tmp_path, monkeypatch, bench_cli_module):
    ws_dir = tmp_path / "workspace"
    ws_dir.mkdir()
    (ws_dir / "writeup.md").write_text("This is a completed writeup with enough content to grade." * 4)

    result_dir = tmp_path / "results"
    result_dir.mkdir(parents=True)
    # Create an existing grade file with enough content
    llm_file = result_dir / "llm_grade_result_claude.md"
    llm_file.write_text("x" * 600)

    monkeypatch.setattr(bench_cli_module, "find_rubric_file", lambda *_args, **_kwargs: PROJECT_ROOT / "comp140" / "rubrics" / "circles_rubric.md")

    ws = {
        "id": "test_ws",
        "dir": str(ws_dir),
        "course": "comp140",
        "name": "circles",
        "assignment_number": 1,
    }

    score, info = bench_cli_module.run_cli_grade(ws, result_dir)
    assert score is None
    assert info == "already graded"


def test_run_grade_parses_json_output_via_shared_helpers(tmp_path, monkeypatch, bench_cli_module):
    ws_dir = tmp_path / "workspace"
    ws_dir.mkdir()
    result_dir = tmp_path / "results"
    result_dir.mkdir()

    ws = {
        "id": "comp140_module1",
        "dir": str(ws_dir),
        "course": "comp140",
        "name": "circles",
        "assignment_number": 1,
        "language": "python",
    }

    class _RunResult:
        returncode = 0
        stdout = json.dumps({
            "passed": 2,
            "total": 2,
            "points": 2,
            "max_points": 2,
            "tests": [
                {"name": "t1", "passed": True, "points": 1, "max_points": 1},
                {"name": "t2", "passed": True, "points": 1, "max_points": 1},
            ],
        })
        stderr = ""

    monkeypatch.setattr(bench_cli_module.subprocess, "run", lambda *args, **kwargs: _RunResult())

    summary, grade_data = bench_cli_module.run_grade(ws, result_dir)

    assert summary == "Passed: 2/2 tests (100.0%)"
    assert grade_data["tests_passed"] == 2
    assert grade_data["points_earned"] == 2.0
    assert (result_dir / "grade_result.json").exists()
