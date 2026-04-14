"""Regression tests for Codex CLI command construction in bench-cli."""

import importlib.machinery
import importlib.util
from pathlib import Path

import pytest


PROJECT_ROOT = Path(__file__).resolve().parent.parent


@pytest.fixture(scope="module")
def bench_cli_module():
    bench_cli_path = str(PROJECT_ROOT / "bin" / "bench-cli")
    loader = importlib.machinery.SourceFileLoader("bench_cli", bench_cli_path)
    spec = importlib.util.spec_from_loader("bench_cli", loader)
    mod = importlib.util.module_from_spec(spec)
    loader.exec_module(mod)
    return mod


def test_build_codex_exec_cmd_enforces_workspace_sandbox_and_no_prompts(bench_cli_module):
    cmd = bench_cli_module._build_codex_exec_cmd()

    assert cmd[0] == "codex"
    assert "exec" in cmd
    assert cmd.index("-a") < cmd.index("exec")
    assert "--json" in cmd
    assert "--sandbox" in cmd
    assert cmd[cmd.index("--sandbox") + 1] == "workspace-write"
    assert "-a" in cmd
    assert cmd[cmd.index("-a") + 1] == "never"
    assert "--full-auto" not in cmd
    assert "--add-dir" not in cmd
    assert cmd[-1] == "-"


def test_build_codex_exec_cmd_applies_model_and_reasoning(bench_cli_module):
    cmd = bench_cli_module._build_codex_exec_cmd(model="gpt-5.3-codex", reasoning="high")

    model_idx = cmd.index("-m")
    assert cmd[model_idx + 1] == "gpt-5.3-codex"

    config_idx = cmd.index("-c")
    assert cmd[config_idx + 1] == 'model_reasoning_effort="high"'


def test_run_codex_uses_strict_command_with_reasoning(tmp_path, monkeypatch, bench_cli_module):
    ws = {"dir": str(tmp_path)}
    result_dir = tmp_path / "results"
    result_dir.mkdir()
    seen = {}

    class _FakeProc:
        returncode = 0

        def communicate(self, input=None, timeout=None):  # noqa: A002
            seen["input"] = input
            seen["timeout"] = timeout
            return "", ""

    monkeypatch.setattr(bench_cli_module, "_build_solver_prompt", lambda _: "prompt")
    monkeypatch.setattr(bench_cli_module, "_write_codex_mcp_config", lambda _: None)
    monkeypatch.setattr(bench_cli_module, "_remove_codex_mcp_config", lambda _: None)
    monkeypatch.setattr(bench_cli_module, "_build_codex_metrics", lambda *_args, **_kwargs: None)

    def _fake_popen(cmd, stdin, stdout, stderr, text, cwd, env):  # noqa: ARG001
        seen["cmd"] = cmd
        seen["cwd"] = cwd
        return _FakeProc()

    monkeypatch.setattr(bench_cli_module.subprocess, "Popen", _fake_popen)

    rc = bench_cli_module.run_codex(
        ws,
        result_dir,
        model="gpt-5.3-codex",
        timeout=123,
        reasoning="high",
    )

    assert rc == 0
    assert seen["cwd"] == str(tmp_path)
    assert seen["input"] == "prompt"
    assert seen["timeout"] == 123
    assert "--sandbox" in seen["cmd"]
    assert seen["cmd"][seen["cmd"].index("--sandbox") + 1] == "workspace-write"
    assert "-a" in seen["cmd"]
    assert seen["cmd"][seen["cmd"].index("-a") + 1] == "never"
    assert "-c" in seen["cmd"]
    assert seen["cmd"][seen["cmd"].index("-c") + 1] == 'model_reasoning_effort="high"'


def test_run_cli_grade_codex_uses_read_only_and_never_approval(tmp_path, monkeypatch, bench_cli_module):
    ws = {"dir": str(tmp_path), "course": "comp140", "name": "circles", "assignment_number": 1}
    result_dir = tmp_path / "results"
    result_dir.mkdir()
    (tmp_path / "writeup.md").write_text("This is a completed writeup with enough content to grade." * 4)
    seen = {}

    monkeypatch.setattr(bench_cli_module, "find_rubric_file", lambda *_args, **_kwargs: Path("/fake/rubric.md"))
    monkeypatch.setattr(bench_cli_module, "build_grading_prompt", lambda _ws, **_kw: "grade this")
    monkeypatch.setattr(bench_cli_module, "format_grade_output", lambda _raw, _ws: "PASS")
    monkeypatch.setattr(bench_cli_module, "parse_grade_score", lambda _raw: None)

    class _RunResult:
        returncode = 0
        stdout = "PASS"
        stderr = ""

    def _fake_run(cmd, **kwargs):
        seen["cmd"] = cmd
        return _RunResult()

    monkeypatch.setattr(bench_cli_module.subprocess, "run", _fake_run)

    score, elapsed = bench_cli_module.run_cli_grade(ws, result_dir, agent="codex", model=None, reasoning=None, force=True)

    assert "--sandbox" in seen["cmd"]
    assert seen["cmd"][seen["cmd"].index("--sandbox") + 1] == "read-only"
    assert "-a" in seen["cmd"]
    assert seen["cmd"][seen["cmd"].index("-a") + 1] == "never"
