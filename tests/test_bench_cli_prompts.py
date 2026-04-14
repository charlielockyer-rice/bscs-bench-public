"""Regression tests for shared CLI prompt construction."""

import importlib.machinery
import importlib.util
from pathlib import Path
import sys

import pytest


PROJECT_ROOT = Path(__file__).resolve().parent.parent
BIN_DIR = PROJECT_ROOT / "bin"


@pytest.fixture(scope="module")
def bench_cli_module():
    bench_cli_path = str(PROJECT_ROOT / "bin" / "bench-cli")
    loader = importlib.machinery.SourceFileLoader("bench_cli", bench_cli_path)
    spec = importlib.util.spec_from_loader("bench_cli", loader)
    mod = importlib.util.module_from_spec(spec)
    loader.exec_module(mod)
    return mod


@pytest.fixture(scope="module")
def prompts_module():
    if str(BIN_DIR) not in sys.path:
        sys.path.insert(0, str(BIN_DIR))
    prompts_path = PROJECT_ROOT / "bin" / "_prompts.py"
    spec = importlib.util.spec_from_file_location("bench_prompts", prompts_path)
    mod = importlib.util.module_from_spec(spec)
    assert spec.loader is not None
    spec.loader.exec_module(mod)
    return mod


def test_hybrid_prompt_uses_config_metadata_without_writeup_file(tmp_path, prompts_module):
    ws = {
        "dir": str(tmp_path),
        "course": "comp140",
        "language": "python",
        "name": "circles",
        "assignment_number": 1,
        "display_name": "Circles",
    }

    prompt = prompts_module.build_solver_prompt(ws)

    assert "Written component required in writeup.md." in prompt
    assert "Recipe for make_circle" in prompt
    assert "Config metadata points to these primary files" in prompt
    assert "- solution.py" in prompt
    assert "Complete the required written component in writeup.md." in prompt


def test_code_only_prompt_omits_writeup_step(tmp_path, prompts_module):
    ws = {
        "dir": str(tmp_path),
        "course": "comp411",
        "language": "java",
        "name": "assignment1",
        "assignment_number": 1,
        "display_name": "Parsing and Abstract Syntax",
    }

    prompt = prompts_module.build_solver_prompt(ws)

    assert "Complete the required written component in writeup.md." not in prompt
    assert "Configured student-owned files" in prompt
    assert "- Parser.java" in prompt


def test_prompt_mentions_previous_solution_when_present(tmp_path, prompts_module):
    prev_dir = tmp_path / "previous_solution"
    prev_dir.mkdir()
    (prev_dir / "solution.py").write_text("pass\n")

    ws = {
        "dir": str(tmp_path),
        "course": "comp140",
        "language": "python",
        "name": "stock_prediction",
        "assignment_number": 3,
        "display_name": "Stock Prediction",
    }

    prompt = prompts_module.build_solver_prompt(ws)

    assert "IMPORTANT: This assignment builds on your previous work." in prompt
    assert "previous_solution/" in prompt


def test_workspace_needs_llm_grading_is_metadata_driven(prompts_module):
    assert prompts_module.workspace_needs_llm_grading({
        "course": "comp140",
        "language": "python",
        "name": "circles",
        "assignment_number": 1,
    }) is True

    assert prompts_module.workspace_needs_llm_grading({
        "course": "comp411",
        "language": "java",
        "name": "assignment1",
        "assignment_number": 1,
    }) is False

    assert prompts_module.workspace_needs_llm_grading({
        "course": "comp382",
        "language": "proof",
        "name": "homework1",
        "assignment_number": 1,
    }) is True


def test_explicit_assignment_mode_overrides_inference(tmp_path, prompts_module):
    ws = {
        "dir": str(tmp_path),
        "course": "comp140",
        "language": "python",
        "name": "circles",
        "assignment_number": 1,
        "assignment_mode": "code",
    }

    prompt = prompts_module.build_solver_prompt(ws)

    assert "Complete the required written component in writeup.md." not in prompt
    assert prompts_module.workspace_needs_llm_grading(ws) is False


def test_legacy_top_level_writeup_sections_are_respected(tmp_path, monkeypatch, prompts_module):
    course_dir = tmp_path / "legacy101"
    configs_dir = course_dir / "configs"
    configs_dir.mkdir(parents=True)
    (configs_dir / "module1.yaml").write_text(
        """\
assignment_number: 1
assignment_name: "legacy_assignment"
language: python
source_files:
  - path: "solution.py"
writeup_sections:
  - "Explain the approach"
  - "Analyze the tradeoffs"
"""
    )

    monkeypatch.setattr(prompts_module, "PROJECT_ROOT", tmp_path)

    ws = {
        "dir": str(tmp_path / "workspace"),
        "course": "legacy101",
        "language": "python",
        "name": "legacy_assignment",
        "assignment_number": 1,
    }

    prompt = prompts_module.build_solver_prompt(ws)

    assert "Written component required in writeup.md." in prompt
    assert "Explain the approach" in prompt
    assert "Analyze the tradeoffs" in prompt
    assert prompts_module.workspace_needs_llm_grading(ws) is True


def test_hybrid_metadata_without_rubric_still_needs_llm_grading(tmp_path, monkeypatch, prompts_module):
    course_dir = tmp_path / "metadata201"
    configs_dir = course_dir / "configs"
    configs_dir.mkdir(parents=True)
    (configs_dir / "assignment2.yaml").write_text(
        """\
assignment_number: 2
assignment_name: "reflection"
language: python
source_files:
  - path: "solution.py"
written_questions:
  - id: q1
    prompt: "What did you learn?"
"""
    )

    monkeypatch.setattr(prompts_module, "PROJECT_ROOT", tmp_path)

    ws = {
        "dir": str(tmp_path / "workspace"),
        "course": "metadata201",
        "language": "python",
        "name": "reflection",
        "assignment_number": 2,
    }

    prompt = prompts_module.build_solver_prompt(ws)

    assert "Written component:" in prompt
    assert "Complete every question or placeholder in writeup.md before you finish." in prompt
    assert prompts_module.workspace_needs_llm_grading(ws) is True


def test_grading_prompt_includes_rubric_and_course_guidance(tmp_path, prompts_module):
    (tmp_path / "instructions.md").write_text("Solve the assignment.")
    (tmp_path / "writeup.md").write_text("My solution.")

    ws = {
        "dir": str(tmp_path),
        "course": "comp140",
        "language": "python",
        "name": "circles",
        "assignment_number": 1,
        "display_name": "Circles",
    }

    prompt = prompts_module.build_grading_prompt(ws)

    assert "## Course-Specific Grading Instructions" in prompt
    assert "## Grading Rubric" in prompt
    assert "## Student Submission" in prompt
    assert "Return ONLY valid JSON" in prompt


def test_bench_cli_uses_shared_prompt_functions(bench_cli_module, prompts_module):
    """Verify bench-cli imports prompt functions from _prompts (no wrappers)."""
    # Both modules load _prompts independently so identity check won't work,
    # but we can verify the functions exist and have the right names.
    assert bench_cli_module.build_grading_prompt.__name__ == "build_grading_prompt"
    assert bench_cli_module.format_grade_output.__name__ == "format_grade_output"
    assert bench_cli_module.read_writeup.__name__ == "read_writeup"
    # Verify no leftover wrapper functions
    assert not hasattr(bench_cli_module, "_build_grading_prompt")
    assert not hasattr(bench_cli_module, "_format_grade_output")


def test_run_claude_pipes_generated_prompt_to_cli(tmp_path, monkeypatch, bench_cli_module):
    ws = {"dir": str(tmp_path)}
    result_dir = tmp_path / "results"
    result_dir.mkdir()
    trace_file = result_dir / "agent_trace.jsonl"
    seen = {}

    class _FakeProc:
        returncode = 0

        def communicate(self, input=None, timeout=None):  # noqa: A002
            seen["input"] = input
            seen["timeout"] = timeout
            trace_file.write_text('{"type":"result","subtype":"success"}\n')
            return "", ""

    monkeypatch.setattr(bench_cli_module, "_build_solver_prompt", lambda _: "prompt-from-builder")
    monkeypatch.setattr(bench_cli_module, "_write_mcp_config", lambda _: None)
    monkeypatch.setattr(bench_cli_module, "_remove_mcp_config", lambda _: None)

    def _fake_popen(cmd, stdin, stdout, stderr, text, cwd, env):  # noqa: ARG001
        seen["cmd"] = cmd
        seen["cwd"] = cwd
        return _FakeProc()

    monkeypatch.setattr(bench_cli_module.subprocess, "Popen", _fake_popen)

    rc = bench_cli_module.run_claude(ws, result_dir, model="sonnet", timeout=45)

    assert rc == 0
    assert seen["cwd"] == str(tmp_path)
    assert seen["input"] == "prompt-from-builder"
    assert seen["timeout"] == 45
    assert seen["cmd"][0] == "claude"
    assert "--allowedTools" in seen["cmd"]
