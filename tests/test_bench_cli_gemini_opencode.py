"""Regression tests for Gemini and Opencode support in bench-cli."""

import importlib.machinery
import importlib.util
import json
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


def test_run_gemini_forces_headless_prompt_and_marks_missing_result(tmp_path, monkeypatch, bench_cli_module):
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
    monkeypatch.setattr(bench_cli_module, "_write_gemini_mcp_config", lambda _: None)
    monkeypatch.setattr(bench_cli_module, "_remove_gemini_mcp_config", lambda _: None)
    monkeypatch.setattr(bench_cli_module, "_build_gemini_metrics", lambda *_args, **_kwargs: False)

    def _fake_popen(cmd, stdin, stdout, stderr, text, cwd, env):  # noqa: ARG001
        seen["cmd"] = cmd
        seen["cwd"] = cwd
        return _FakeProc()

    monkeypatch.setattr(bench_cli_module.subprocess, "Popen", _fake_popen)

    rc = bench_cli_module.run_gemini(ws, result_dir, model="flash", timeout=12)

    assert rc == 0
    assert seen["cwd"] == str(tmp_path)
    assert seen["input"] == "prompt"
    assert seen["timeout"] == 12
    assert "--prompt" in seen["cmd"]
    assert seen["cmd"][seen["cmd"].index("--prompt") + 1] == ""

    output = json.loads((result_dir / "agent_output.json").read_text())
    assert output["is_error"] is True
    assert output["error"] == "gemini missing final result event"


def test_run_cli_grade_gemini_uses_prompt_flag(tmp_path, monkeypatch, bench_cli_module):
    ws_dir = tmp_path / "workspace"
    ws_dir.mkdir()
    (ws_dir / "writeup.md").write_text("This is a completed writeup with enough content to grade." * 4)

    result_dir = tmp_path / "results"
    result_dir.mkdir()
    ws = {
        "id": "comp140_module1",
        "dir": str(ws_dir),
        "course": "comp140",
        "name": "circles",
        "assignment_number": 1,
    }
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
        seen["input"] = kwargs.get("input")
        return _RunResult()

    monkeypatch.setattr(bench_cli_module.subprocess, "run", _fake_run)

    score, _elapsed = bench_cli_module.run_cli_grade(
        ws, result_dir, agent="gemini", model="flash", force=True)

    assert score is None
    assert seen["input"] == "grade this"
    assert seen["cmd"][0] == "gemini"
    assert "--prompt" in seen["cmd"]
    assert seen["cmd"][seen["cmd"].index("--prompt") + 1] == ""


def test_remove_gemini_mcp_config_deletes_session_artifacts(tmp_path, bench_cli_module):
    gemini_dir = tmp_path / ".gemini"
    gemini_dir.mkdir()
    (gemini_dir / "settings.json").write_text("{}")
    (gemini_dir / "tmp.txt").write_text("session data")

    bench_cli_module._remove_gemini_mcp_config(tmp_path)

    assert not gemini_dir.exists()


def test_build_opencode_metrics_marks_error_events_and_resume_retries(tmp_path, bench_cli_module):
    result_dir = tmp_path / "results" / "ws1"
    result_dir.mkdir(parents=True)
    trace_file = result_dir / "agent_trace.jsonl"
    output_file = result_dir / "agent_output.json"

    trace_file.write_text(
        '{"type":"error","timestamp":1,"sessionID":"ses_x",'
        '"error":{"name":"UnknownError","data":{"message":"Model not found"}}}\n'
    )

    bench_cli_module._build_opencode_metrics(
        trace_file, output_file, elapsed_ms=123, model="minimax/minimax-m2.5")

    output = json.loads(output_file.read_text())
    assert output["is_error"] is True
    assert output["session_id"] == "ses_x"
    assert "Model not found" in output["error"]
    assert bench_cli_module._workspace_completed(tmp_path / "results", "ws1") is False


def test_run_opencode_restores_existing_workspace_config(tmp_path, monkeypatch, bench_cli_module):
    ws = {"dir": str(tmp_path)}
    result_dir = tmp_path / "results"
    result_dir.mkdir()
    opencode_json = tmp_path / "opencode.json"
    original_config = '{"theme":"nord","mcp":{"existing":{"type":"local"}}}\n'
    opencode_json.write_text(original_config)

    class _FakeProc:
        returncode = 0

        def communicate(self, input=None, timeout=None):  # noqa: A002
            return "", ""

    monkeypatch.setattr(bench_cli_module, "_build_solver_prompt", lambda _: "prompt")
    monkeypatch.setattr(bench_cli_module, "_build_opencode_metrics", lambda *_args, **_kwargs: None)
    monkeypatch.setattr(bench_cli_module, "_opencode_is_configured", lambda env=None: True)
    monkeypatch.setattr(bench_cli_module.shutil, "which", lambda _name: "/usr/local/bin/opencode")
    monkeypatch.setattr(bench_cli_module.subprocess, "Popen", lambda *args, **kwargs: _FakeProc())

    rc = bench_cli_module.run_opencode(ws, result_dir, model="openrouter/anthropic/claude-sonnet-4-5")

    assert rc == 0
    assert opencode_json.read_text() == original_config


def test_require_opencode_accepts_auth_json_without_openrouter_env(tmp_path, monkeypatch, bench_cli_module):
    auth_root = tmp_path / "xdg"
    auth_path = auth_root / "opencode" / "auth.json"
    auth_path.parent.mkdir(parents=True)
    auth_path.write_text('{"provider":"openrouter"}')

    monkeypatch.delenv("OPENCODE_CONFIG_CONTENT", raising=False)
    monkeypatch.setenv("XDG_DATA_HOME", str(auth_root))
    monkeypatch.setattr(bench_cli_module.shutil, "which", lambda _name: "/usr/local/bin/opencode")

    bench_cli_module._require_opencode("openrouter/anthropic/claude-sonnet-4-5")


def test_require_opencode_rejects_missing_auth(tmp_path, monkeypatch, bench_cli_module):
    monkeypatch.delenv("OPENCODE_CONFIG_CONTENT", raising=False)
    monkeypatch.setenv("XDG_DATA_HOME", str(tmp_path / "xdg"))
    monkeypatch.setattr(bench_cli_module.shutil, "which", lambda _name: "/usr/local/bin/opencode")

    with pytest.raises(FileNotFoundError, match="opencode auth not configured"):
        bench_cli_module._require_opencode("openrouter/anthropic/claude-sonnet-4-5")
