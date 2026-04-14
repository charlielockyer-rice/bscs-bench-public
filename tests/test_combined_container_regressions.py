"""Regression tests for sandbox migration and in-container runner fallbacks."""

import importlib.machinery
import importlib.util
import sys
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parent.parent
BIN_DIR = PROJECT_ROOT / "bin"


def _load_module(name: str, path: Path):
    loader = importlib.machinery.SourceFileLoader(name, str(path))
    spec = importlib.util.spec_from_loader(name, loader)
    module = importlib.util.module_from_spec(spec)
    loader.exec_module(module)
    return module


def test_sandbox_name_uses_results_dir_and_sanitizes():
    if str(BIN_DIR) not in sys.path:
        sys.path.insert(0, str(BIN_DIR))
    bench_cli = _load_module("bench_cli_combined", PROJECT_ROOT / "bin" / "bench-cli")

    results_dir = PROJECT_ROOT / "results" / "bench cli:2026"
    assert bench_cli._sandbox_name("claude", results_dir) == "bscs-claude-bench-cli-2026"


def test_build_sandbox_exec_cmd_sets_workdir_and_env():
    if str(BIN_DIR) not in sys.path:
        sys.path.insert(0, str(BIN_DIR))
    bench_cli = _load_module("bench_cli_sandbox", PROJECT_ROOT / "bin" / "bench-cli")

    cmd = bench_cli._build_sandbox_exec_cmd(
        "bscs-claude-run",
        "/tmp/workspace",
        ["claude", "-p"],
        env_vars={"BSCS_TEST_EXECUTION_MODE": "host", "CLEAR_NETID": "abc123"},
    )

    assert cmd == [
        "docker", "sandbox", "exec",
        "-i",
        "-w", "/tmp/workspace",
        "-e", "BSCS_TEST_EXECUTION_MODE=host",
        "-e", "CLEAR_NETID=abc123",
        "bscs-claude-run",
        "claude", "-p",
    ]


def test_run_agent_in_sandbox_claude_uses_docker_sandbox_exec(tmp_path, monkeypatch):
    if str(BIN_DIR) not in sys.path:
        sys.path.insert(0, str(BIN_DIR))
    bench_cli = _load_module("bench_cli_sandbox_run", PROJECT_ROOT / "bin" / "bench-cli")

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

    monkeypatch.setattr(bench_cli, "_build_solver_prompt", lambda _ws: "prompt")
    monkeypatch.setattr(bench_cli, "_write_mcp_config", lambda _ws_dir: None)
    monkeypatch.setattr(bench_cli, "_remove_mcp_config", lambda _ws_dir: None)
    monkeypatch.setattr(bench_cli, "_extract_claude_result", lambda _trace, output: output.write_text("{}"))

    def _fake_popen(cmd, stdin, stdout, stderr, text, env):  # noqa: ARG001
        seen["cmd"] = cmd
        return _FakeProc()

    monkeypatch.setattr(bench_cli.subprocess, "Popen", _fake_popen)

    rc = bench_cli.run_agent_in_sandbox(
        ws,
        result_dir,
        agent="claude",
        model="sonnet",
        sandbox_name="bscs-claude-run",
        timeout=15,
    )

    assert rc == 0
    assert seen["input"] == "prompt"
    assert seen["timeout"] == 15
    assert seen["cmd"][:5] == ["docker", "sandbox", "exec", "-i", "-w"]
    assert seen["cmd"][5] == str(tmp_path)
    assert "-e" in seen["cmd"]
    assert "BSCS_TEST_EXECUTION_MODE=host" in seen["cmd"]
    assert "bscs-claude-run" in seen["cmd"]
    assert "claude" in seen["cmd"]


def test_comp321_malloc_perf_uses_local_runner_in_combined_mode(monkeypatch, tmp_path):
    comp321 = _load_module("comp321_runner_test", PROJECT_ROOT / "comp321" / "runner.py")
    runner = comp321.CRunner(PROJECT_ROOT / "comp321")

    monkeypatch.setattr(comp321, "is_in_container", lambda: True)
    monkeypatch.setattr(runner, "load_config", lambda _name: {})
    monkeypatch.setattr(runner, "compile_project_host", lambda _ws, _name: (True, ""))

    seen = {}

    def _fake_run_local(cmd, cwd=None, timeout=None, env=None):
        seen["cmd"] = cmd
        seen["cwd"] = cwd
        seen["timeout"] = timeout
        return 0, "ok", ""

    monkeypatch.setattr(comp321, "run_local", _fake_run_local)

    ok, output = runner.run_malloc_perf(tmp_path)

    assert ok is True
    assert output == "ok"
    assert seen["cmd"] == ["./mdriver", "-v"]
    assert seen["cwd"] == tmp_path
