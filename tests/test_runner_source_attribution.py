"""Regression tests for manifest-backed test source attribution."""

from pathlib import Path

from comp321.runner import CRunner
from comp341.runner import PythonMLRunner
from framework.runner_utils import parse_junit_xml
from framework.suite_manifest import write_suite_manifest


def test_parse_junit_xml_uses_suite_manifest_for_private_java_tests(tmp_path, monkeypatch):
    suite_dir = tmp_path / "suite"
    suite_dir.mkdir()
    write_suite_manifest(suite_dir, {
        "version": 1,
        "run_type": "submit",
        "entries": [
            {
                "relative_path": "hw01/ShapeTests.java",
                "source": "private",
                "class_name": "ShapeTests",
            },
        ],
    })

    reports_dir = tmp_path / "reports"
    reports_dir.mkdir()
    (reports_dir / "TEST-ShapeTests.xml").write_text(
        "<testsuite>"
        "<testcase classname='comp310.tests.hw01.ShapeTests' name='testHidden' time='0.01' />"
        "</testsuite>"
    )

    monkeypatch.setenv("BSCS_TEST_SUITE_DIR", str(suite_dir))

    results = parse_junit_xml(reports_dir)

    assert len(results) == 1
    assert results[0].source == "private"


def test_comp341_junit_parser_uses_manifest_path_for_private_tests(tmp_path):
    suite_dir = tmp_path / "suite"
    suite_dir.mkdir()
    write_suite_manifest(suite_dir, {
        "version": 1,
        "run_type": "submit",
        "entries": [
            {
                "relative_path": "nested/test_hidden.py",
                "source": "private",
            },
        ],
    })

    report_path = tmp_path / "pytest-report.xml"
    report_path.write_text(
        "<testsuite>"
        "<testcase classname='TestVisibility' name='test_secret' file='nested/test_hidden.py' time='0.02' />"
        "</testsuite>"
    )

    runner = PythonMLRunner(tmp_path)
    runner.suite_override = suite_dir

    results = runner._parse_pytest_junit_xml(report_path, {"test_secret": 2.0})

    assert len(results) == 1
    assert results[0].source == "private"
    assert results[0].max_points == 2.0


def test_comp321_runner_uses_manifest_for_python_harness_sources(tmp_path, monkeypatch):
    suite_dir = tmp_path / "suite"
    suite_dir.mkdir()
    write_suite_manifest(suite_dir, {
        "version": 1,
        "run_type": "submit",
        "entries": [
            {
                "relative_path": "test_hidden.py",
                "source": "private",
            },
        ],
    })
    (suite_dir / "test_hidden.py").write_text(
        "def test_sample(workspace_path):\n"
        "    return True\n"
    )

    monkeypatch.setenv("BSCS_TEST_SUITE_DIR", str(suite_dir))
    runner = CRunner(tmp_path, use_docker=False)
    monkeypatch.setattr(runner, "load_config", lambda project_name: {"assignment_number": 1, "display_name": project_name})
    monkeypatch.setattr(runner, "compile_project_host", lambda workspace, project_name: (True, ""))

    workspace = tmp_path / "workspace"
    workspace.mkdir()

    result = runner.run_tests("hidden", workspace)

    assert len(result.test_results) == 1
    assert result.test_results[0].source == "private"
