#!/usr/bin/env python3
"""
COMP 322 Java/Maven Test Runner (Docker-only)

Compiles and runs tests for COMP 322 parallel programming assignments
using Maven and HJlib inside a Docker container.

Usage:
    python runner.py <project> <workspace> [options]

Examples:
    python runner.py hw1 ./workspaces/agent_hw1
    python runner.py hw3 ./workspaces/agent_hw3 --verbose
    python runner.py hw4 ./workspaces/agent_hw4 --json
"""

import os
import subprocess
import sys
import uuid
from pathlib import Path

# Add parent directory to path for imports
sys.path.insert(0, str(Path(__file__).parent.parent))
from framework.docker_utils import ensure_docker_image, is_in_container, run_local
from framework.config_compat import get_timeout_seconds, DEFAULT_TIMEOUT_JAVA_HJLIB
from framework.runner_utils import (
    load_course_config,
    parse_junit_xml,
    make_grade_result,
    check_java_compilation_error,
    runner_main,
)

# Docker image name
DOCKER_IMAGE = "comp322-runner"

# Docker build timeout (seconds)
BUILD_TIMEOUT = 300

# Maven compilation buffer (seconds) added to test timeout
COMPILATION_BUFFER = 120


class JavaRunner:
    """Compiles and runs Java/Maven project tests with HJlib support in Docker."""

    def __init__(self, comp322_dir: Path):
        self.comp322_dir = comp322_dir
        self.configs_dir = comp322_dir / "configs"
        self.in_container = is_in_container()

    def load_config(self, project_name: str) -> dict:
        return load_course_config(self.configs_dir, project_name)

    def ensure_docker_image(self, force_rebuild: bool = False) -> tuple:
        return ensure_docker_image(DOCKER_IMAGE, self.comp322_dir, BUILD_TIMEOUT, force_rebuild)

    def _build_maven_script(
        self, ws_path: str, suite_path: str, test_timeout: int, verbose: bool
    ) -> str:
        """Build the Maven test script using the given workspace and suite paths.

        Args:
            ws_path: Path to workspace (e.g. "/workspace" in Docker, actual path locally)
            suite_path: Path to suite tests (e.g. "/suite_tests" in Docker, actual path locally),
                        or empty string if no suite override
            test_timeout: Surefire timeout in seconds
            verbose: Whether to show verbose Maven output

        Returns:
            Bash script string ready to execute
        """
        quiet_flag = "" if verbose else " -q"

        if not suite_path:
            return (
                "set -e; "
                f"rm -rf {ws_path}/test-reports && "
                f"mvn clean test -Dsurefire.timeout={test_timeout}{quiet_flag}"
            )

        return (
            "set -e; "
            f"rm -rf {ws_path}/test-reports && "
            f"rm -rf /tmp/bscs_ws && mkdir -p /tmp/bscs_ws && cp -a {ws_path}/. /tmp/bscs_ws && "
            f"rm -rf /tmp/bscs_ws/src/test && mkdir -p /tmp/bscs_ws/src/test && cp -a {suite_path}/. /tmp/bscs_ws/src/test/ && "
            f"cd /tmp/bscs_ws && mvn clean test -Dsurefire.timeout={test_timeout}{quiet_flag}"
            f" && rm -rf {ws_path}/test-reports && mkdir -p {ws_path}/test-reports && "
            f"if [ -d /tmp/bscs_ws/target/surefire-reports ]; then cp -a /tmp/bscs_ws/target/surefire-reports/. {ws_path}/test-reports/; fi"
        )

    def _resolve_credentials(self, config: dict, env: dict) -> tuple:
        """Resolve MOCK_GITHUB / credential settings.

        Returns (ok, error_message, mock_github_flag) where mock_github_flag
        is True when mock mode should be used, False for live API mode.
        """
        if not config.get("requires_credentials", False):
            return True, "", False

        mock_env = env.get("MOCK_GITHUB")
        mock_github = True if mock_env is None else mock_env.lower() == "true"

        if mock_github:
            return True, "", True

        if not env.get("GITHUB_USERNAME") or not env.get("GITHUB_TOKEN"):
            return False, "HW2 requires GITHUB_USERNAME and GITHUB_TOKEN environment variables (or set MOCK_GITHUB=true)", False

        return True, "", False

    def run_tests_in_container(self, workspace: Path, config: dict, verbose: bool = False) -> tuple:
        """Run Maven tests inside Docker container (or locally if in container)."""
        test_timeout = get_timeout_seconds(config, DEFAULT_TIMEOUT_JAVA_HJLIB)
        total_timeout = test_timeout + COMPILATION_BUFFER

        suite_override = os.environ.get("BSCS_TEST_SUITE_DIR", "").strip()

        if self.in_container:
            # Already inside a pre-provisioned benchmark container — run Maven directly.
            # Build the script with actual local paths from the start.
            ws_path = str(workspace.resolve())
            suite_path = suite_override if suite_override else ""
            maven_cmd = self._build_maven_script(ws_path, suite_path, test_timeout, verbose)

            env = os.environ.copy()
            ok, err, mock_github = self._resolve_credentials(config, env)
            if not ok:
                return False, "", err
            if mock_github:
                env["MOCK_GITHUB"] = "true"

            returncode, stdout, stderr = run_local(
                ["bash", "-c", maven_cmd], timeout=total_timeout, cwd=workspace, env=env,
            )
            return returncode == 0, stdout, stderr

        # Docker path — use container-internal paths
        maven_cmd = self._build_maven_script(
            "/workspace",
            "/suite_tests" if suite_override else "",
            test_timeout,
            verbose,
        )

        cmd = [
            "docker", "run", "--rm",
            "--memory", "10g",
            "--cpus", "2",
            "-v", f"{workspace.resolve()}:/workspace",
            "-v", "comp322-maven-cache:/root/.m2/repository",
            "-w", "/workspace",
        ]
        if suite_override:
            cmd.extend(["-v", f"{Path(suite_override).resolve()}:/suite_tests:ro"])

        ok, err, mock_github = self._resolve_credentials(config, os.environ)
        if not ok:
            return False, "", err

        if config.get("requires_credentials", False):
            if mock_github:
                cmd.extend(["-e", "MOCK_GITHUB=true"])
                cmd.extend(["--network", "none"])
            else:
                cmd.extend(["-e", f"GITHUB_USERNAME={os.environ['GITHUB_USERNAME']}"])
                cmd.extend(["-e", f"GITHUB_TOKEN={os.environ['GITHUB_TOKEN']}"])
        else:
            cmd.extend(["--network", "none"])

        container_name = f"bscs-{uuid.uuid4().hex[:12]}"
        cmd.extend(["--name", container_name])
        cmd.extend([DOCKER_IMAGE, "bash", "-c", maven_cmd])

        proc = subprocess.Popen(
            cmd,
            stdin=subprocess.DEVNULL,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            cwd=workspace,
        )
        try:
            stdout, stderr = proc.communicate(timeout=total_timeout)
            return proc.returncode == 0, stdout, stderr
        except subprocess.TimeoutExpired:
            subprocess.run(
                ["docker", "stop", "--time=5", container_name],
                capture_output=True, timeout=15
            )
            proc.kill()
            proc.communicate()
            return False, "", f"Test execution timed out after {total_timeout}s"
        except Exception as e:
            proc.kill()
            proc.communicate()
            return False, "", f"Docker error: {str(e)}"

    def run_tests(self, project_name: str, workspace_path: Path, verbose: bool = False) -> 'GradeResult':
        """Run all tests for a project."""
        config = self.load_config(project_name)
        result = make_grade_result(config, "comp322", project_name)

        success, error = self.ensure_docker_image()
        if not success:
            result.compilation_error = error
            return result

        success, stdout, stderr = self.run_tests_in_container(workspace_path, config, verbose)

        comp_error = check_java_compilation_error(success, stdout, stderr)
        if comp_error:
            result.compilation_error = comp_error
            return result

        result.test_results = parse_junit_xml(
            workspace_path / "test-reports",
            fallback_dirs=[workspace_path / "target" / "surefire-reports"],
        )

        if not result.test_results and not success:
            result.compilation_error = stderr if stderr else "Maven tests failed with no results"

        return result


def main():
    runner_main(
        description="COMP 322 Java/Maven Autograder (Docker)",
        course_code="comp322",
        runner_factory=JavaRunner,
    )


if __name__ == "__main__":
    main()
