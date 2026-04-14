"""Regression tests for comp215 runner host-mode behavior."""

from types import SimpleNamespace

from comp215.runner import JavaRunner


def test_run_tests_host_includes_test_private_class_pattern(tmp_path, monkeypatch):
    """Host-mode JUnit invocation should discover classes containing 'Test'."""
    runner = JavaRunner(tmp_path)

    build_dir = tmp_path / "build"
    build_dir.mkdir()
    hw_lib = tmp_path / "lib"
    hw_lib.mkdir()
    (hw_lib / "junit-platform-console-standalone-1.9.0.jar").write_text("")

    seen = {}

    def fake_run(cmd, capture_output, text, timeout):
        seen["cmd"] = cmd
        return SimpleNamespace(returncode=0, stdout="", stderr="")

    monkeypatch.setattr("comp215.runner.subprocess.run", fake_run)

    tests, output = runner.run_tests_host(build_dir, test_classes=[], hw_lib=hw_lib)

    assert tests == []
    assert output == ""
    assert "--include-classname=.*Test.*" in seen["cmd"]
