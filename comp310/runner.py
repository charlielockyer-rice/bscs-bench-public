#!/usr/bin/env python3
"""
COMP 310 Java Test Runner (Docker-only)

Compiles and runs tests for COMP 310 Ballworld assignments using
javac + JUnit standalone console launcher inside a Docker container.

Usage:
    python runner.py <project> <workspace> [options]

Examples:
    python runner.py hw01 ./workspaces/comp310_hw01
    python runner.py hw03 ./workspaces/comp310_hw03 --verbose
    python runner.py hw02 ./workspaces/comp310_hw02 --json
"""

import os
import subprocess
import sys
import uuid
from pathlib import Path

# Add parent directory to path for imports
sys.path.insert(0, str(Path(__file__).parent.parent))
from framework.docker_utils import ensure_docker_image, is_in_container, run_local
from framework.config_compat import get_timeout_seconds, DEFAULT_TIMEOUT_JAVA
from framework.runner_utils import (
    load_course_config,
    parse_junit_xml,
    make_grade_result,
    check_java_compilation_error,
    runner_main,
)

# Docker image name
DOCKER_IMAGE = "comp310-runner"

# Docker build timeout (seconds)
BUILD_TIMEOUT = 300

# Compilation buffer (seconds) added to test timeout for javac + JUnit startup
COMPILATION_BUFFER = 60


class JavaRunner:
    """Compiles and runs Java project tests using javac + JUnit in Docker."""

    def __init__(self, comp310_dir: Path):
        self.comp310_dir = comp310_dir
        self.configs_dir = comp310_dir / "configs"
        self.in_container = is_in_container()

    def load_config(self, project_name: str) -> dict:
        return load_course_config(self.configs_dir, project_name)

    def ensure_docker_image(self, force_rebuild: bool = False) -> tuple:
        return ensure_docker_image(DOCKER_IMAGE, self.comp310_dir, BUILD_TIMEOUT, force_rebuild)

    def run_tests_in_container(self, workspace: Path, config: dict, verbose: bool = False) -> tuple:
        """Run build.sh inside Docker container (or locally if in container) to compile and test."""
        test_timeout = get_timeout_seconds(config, DEFAULT_TIMEOUT_JAVA)
        total_timeout = test_timeout + COMPILATION_BUFFER

        hw_name = config.get("assignment_name", "hw01")

        if self.in_container:
            # Already inside a pre-provisioned benchmark container — run build.sh directly
            cmd = ["/opt/comp310/build.sh", hw_name, str(workspace.resolve())]
            env = os.environ.copy()
            suite_override = os.environ.get("BSCS_TEST_SUITE_DIR", "").strip()
            if suite_override:
                env["COMP310_TESTS_DIR"] = suite_override
            returncode, stdout, stderr = run_local(
                cmd, timeout=total_timeout, cwd=workspace, env=env,
            )
            if verbose:
                if stdout:
                    print(stdout, file=sys.stderr)
                if stderr:
                    print(stderr, file=sys.stderr)
            return returncode == 0, stdout, stderr

        cmd = [
            "docker", "run", "--rm",
            "--memory", "4g",
            "--cpus", "2",
            "--network", "none",
            "-v", f"{workspace.resolve()}:/workspace",
            "-w", "/workspace",
        ]

        suite_override = os.environ.get("BSCS_TEST_SUITE_DIR", "").strip()
        if suite_override:
            suite_path = Path(suite_override).resolve()
            cmd.extend([
                "-v", f"{suite_path}:/suite_tests:ro",
                "-e", "COMP310_TESTS_DIR=/suite_tests",
            ])

        container_name = f"bscs-{uuid.uuid4().hex[:12]}"
        cmd.extend(["--name", container_name])
        cmd.extend([
            DOCKER_IMAGE, "/opt/comp310/build.sh", hw_name, "/workspace"
        ])

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

            if verbose:
                if stdout:
                    print(stdout, file=sys.stderr)
                if stderr:
                    print(stderr, file=sys.stderr)

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
        result = make_grade_result(config, "comp310", project_name)

        success, error = self.ensure_docker_image()
        if not success:
            result.compilation_error = error
            return result

        success, stdout, stderr = self.run_tests_in_container(workspace_path, config, verbose)

        comp_error = check_java_compilation_error(success, stdout, stderr)
        if comp_error:
            result.compilation_error = comp_error
            return result

        result.test_results = parse_junit_xml(workspace_path / "test-reports")

        if not result.test_results and not success:
            result.compilation_error = stderr if stderr else "Build/tests failed with no results"

        return result


def main():
    runner_main(
        description="COMP 310 Java Autograder (Docker)",
        course_code="comp310",
        runner_factory=JavaRunner,
    )


if __name__ == "__main__":
    main()
