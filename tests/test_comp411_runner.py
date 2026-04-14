"""Regression tests for comp411 structured JUnit reporting."""

from comp411.runner import JUNIT_REPORT_DIR, JavaRunner
from framework.suite_manifest import write_suite_manifest


def test_build_test_script_includes_structured_junit_harness(tmp_path):
    runner = JavaRunner(tmp_path)
    script = runner._build_test_script(
        ws_path="/workspace",
        suite_dir="",
        test_classes=["Assign1Test"],
        compile_test_files=["Assign1Test.java"],
        source_files=["Interpreter.java"],
        report_dir="test-reports",
    )

    assert "BSCSJUnitXmlRunner.java" in script
    assert "TEST-bscs-suite.xml" in script
    assert "mkdir -p test-reports" in script


def test_run_tests_prefers_junit_xml_output(tmp_path, monkeypatch):
    runner = JavaRunner(tmp_path)
    workspace = tmp_path / "workspace"
    workspace.mkdir()

    suite_dir = tmp_path / "suite"
    suite_dir.mkdir()
    write_suite_manifest(suite_dir, {
        "version": 1,
        "run_type": "submit",
        "entries": [
            {
                "relative_path": "Assign1TestPrivate.java",
                "source": "private",
                "class_name": "Assign1TestPrivate",
            },
        ],
    })
    monkeypatch.setenv("BSCS_TEST_SUITE_DIR", str(suite_dir))

    reports_dir = workspace / JUNIT_REPORT_DIR
    reports_dir.mkdir()
    (reports_dir / "TEST-bscs-suite.xml").write_text(
        "<testsuite>"
        "<testcase classname='Assign1TestPrivate' name='testSecret' time='0.01' />"
        "</testsuite>"
    )

    monkeypatch.setattr(runner, "load_config", lambda assignment_name: {
        "assignment_number": 1,
        "display_name": assignment_name,
        "tests": {"classes": ["Assign1Test"]},
        "source_files": [],
    })
    monkeypatch.setattr(runner, "ensure_docker_image", lambda force_rebuild=False: (True, ""))
    monkeypatch.setattr(runner, "run_tests_in_docker", lambda *args, **kwargs: (False, "", "console fallback should not be used"))

    result = runner.run_tests("assignment1", workspace)

    assert len(result.test_results) == 1
    assert result.test_results[0].source == "private"
