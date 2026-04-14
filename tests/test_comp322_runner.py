"""Regression tests for comp322 runner build-failure handling."""

from framework.runner_utils import check_java_compilation_error

from comp322.runner import JavaRunner


def test_build_maven_script_clears_workspace_test_reports_before_run(tmp_path):
    """The comp322 runner should remove stale reports before invoking Maven."""
    runner = JavaRunner(tmp_path)

    script = runner._build_maven_script("/workspace", "/suite_tests", 123, verbose=False)

    assert "rm -rf /workspace/test-reports" in script
    assert script.index("rm -rf /workspace/test-reports") < script.index("mvn clean test")


def test_check_java_compilation_error_treats_checkstyle_build_failure_as_compilation_error():
    """Pre-test Maven build failures should not fall through to stale JUnit XML."""
    stdout = "\n".join(
        [
            "[INFO] --- checkstyle:2.17:check (validate) @ hw3 ---",
            "[INFO] BUILD FAILURE",
            "[ERROR] Failed to execute goal org.apache.maven.plugins:maven-checkstyle-plugin:2.17:check",
        ]
    )

    error = check_java_compilation_error(False, stdout, "")

    assert error == stdout
