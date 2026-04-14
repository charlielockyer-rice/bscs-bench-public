#!/usr/bin/env python3
"""
COMP 341 Python ML Test Runner (Docker-based)

Runs pytest against student solutions for Machine Learning assignments
inside a Docker container for consistent environment.

Usage:
    python runner.py <homework> <workspace> [options]

Examples:
    python runner.py hw1 ./workspaces/agent_hw1
    python runner.py hw1 ./workspaces/agent_hw1 --verbose
    python runner.py hw1 ./workspaces/agent_hw1 --json
    python runner.py hw1 ./workspaces/agent_hw1 --rebuild
"""

import argparse
import json
import os
import re
import subprocess
import sys
import xml.etree.ElementTree as ET
from pathlib import Path
from typing import List, Optional, Tuple

# Add parent directory to path for imports
sys.path.insert(0, str(Path(__file__).parent.parent))
from framework.yaml_utils import parse_simple_yaml
from framework.docker_utils import ensure_docker_image, is_in_container, run_local
from framework.suite_manifest import (
    load_suite_manifest,
    source_from_manifest_classname,
    source_from_manifest_path,
)
from framework.config_compat import (
    normalize_config,
    get_assignment_number,
    get_display_name,
    get_timeout_seconds,
    DEFAULT_TIMEOUT_PYTHON,
)
from shared.results import TestResult, GradeResult

# Docker configuration
DOCKER_IMAGE = "comp341-runner"

# Timeout configuration
# Docker build timeout (seconds) - first build downloads torch (~100MB CPU version)
BUILD_TIMEOUT = 600

# Buffer time for pytest startup/teardown and data loading
PYTEST_BUFFER_SECONDS = 60
PYTEST_REPORT_NAME = "pytest-report.xml"


class PythonMLRunner:
    """Runs pytest against Python ML solutions in Docker."""

    def __init__(self, comp341_dir: Path):
        self.comp341_dir = comp341_dir
        suite_override = os.environ.get("BSCS_TEST_SUITE_DIR", "").strip()
        self.suite_override = Path(suite_override).resolve() if suite_override else None
        self.tests_dir = self.suite_override if self.suite_override else (comp341_dir / "tests")
        self.configs_dir = comp341_dir / "configs"
        self.data_dir = comp341_dir / "data"
        self.in_container = is_in_container()

    def load_config(self, hw_name: str) -> dict:
        """Load homework configuration from YAML."""
        config_path = self.configs_dir / f"{hw_name}.yaml"
        if not config_path.exists():
            raise FileNotFoundError(f"Config not found: {config_path}")
        with open(config_path) as f:
            raw_config = parse_simple_yaml(f.read())
        return normalize_config(raw_config)

    def ensure_docker_image(self, force_rebuild: bool = False) -> Tuple[bool, str]:
        """Build Docker image if not already built."""
        return ensure_docker_image(
            DOCKER_IMAGE, self.comp341_dir, BUILD_TIMEOUT, force_rebuild
        )

    def run_tests_in_container(
        self,
        workspace: Path,
        config: dict,
        verbose: bool = False,
    ) -> Tuple[bool, str, str]:
        """Run pytest inside Docker container (or locally if in container).

        Mounts:
        - /workspace: The student workspace (solution.py)
        - /comp341: The course directory (tests, framework)
        - /data: The data directory for the homework

        Returns:
            Tuple of (success, stdout, stderr)
        """
        hw_number = get_assignment_number(config)
        timeout_seconds = get_timeout_seconds(config, DEFAULT_TIMEOUT_PYTHON)
        total_timeout = timeout_seconds + PYTEST_BUFFER_SECONDS

        if self.in_container:
            # Already inside a pre-provisioned benchmark container — run pytest directly.
            # Paths inside the container match the Docker mounts:
            # /workspace, /comp341, /data are already at their expected locations.
            data_dir = Path("/data")
            if not data_dir.exists():
                return False, "", "Data directory not mounted at /data"
            test_target = (
                str(self.suite_override) if self.suite_override
                else f"{self.comp341_dir}/tests/test_module{hw_number}.py"
            )
            pytest_cmd = f"pytest {test_target} -v --tb=short --junitxml={PYTEST_REPORT_NAME}"
            if verbose:
                pytest_cmd += " -s"
            else:
                pytest_cmd += " --no-header"

            env = os.environ.copy()
            env["COMP341_WORKSPACE"] = "/workspace"
            env["COMP341_DATA_DIR"] = "/data"
            env["PYTHONPATH"] = f"{workspace.resolve()}:{self.comp341_dir.resolve()}"

            returncode, stdout, stderr = run_local(
                ["bash", "-c", pytest_cmd], timeout=total_timeout, cwd=workspace, env=env,
            )
            return returncode == 0, stdout, stderr

        # Build pytest command.
        # With suite override, run the whole suite directory so agent-added
        # tests are included alongside public tests.
        test_target = "/suite_tests" if self.suite_override else f"/comp341/tests/test_module{hw_number}.py"
        pytest_cmd = f"pytest {test_target} -v --tb=short --junitxml=/workspace/{PYTEST_REPORT_NAME}"
        if verbose:
            pytest_cmd += " -s"
        else:
            pytest_cmd += " --no-header"

        # Docker command with proper mounts
        cmd = [
            "docker", "run", "--rm",
            "--memory", "4g",
            "--cpus", "2",
            "--network", "none",
            # Mount workspace (read-write for any temp files)
            "-v", f"{workspace.resolve()}:/workspace",
            # Mount course directory (read-only)
            "-v", f"{self.comp341_dir.resolve()}:/comp341:ro",
            # Mount data directory (read-only)
            "-v", f"{self.data_dir.resolve()}:/data:ro",
            # Set environment variables
            "-e", "COMP341_WORKSPACE=/workspace",
            "-e", "COMP341_DATA_DIR=/data",
            "-e", "PYTHONPATH=/workspace:/comp341",
            # Working directory
            "-w", "/workspace",
            # Image and command
            DOCKER_IMAGE,
            "bash", "-c", pytest_cmd,
        ]
        if self.suite_override:
            cmd = cmd[:-4] + ["-v", f"{self.suite_override.resolve()}:/suite_tests:ro"] + cmd[-4:]

        try:
            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=total_timeout,
            )
            return result.returncode == 0, result.stdout, result.stderr
        except subprocess.TimeoutExpired:
            return False, "", f"Tests timed out after {total_timeout}s"
        except Exception as e:
            return False, "", f"Docker error: {str(e)}"

    def run_tests(
        self,
        hw_name: str,
        workspace: Path,
        verbose: bool = False,
    ) -> GradeResult:
        """Run pytest against the workspace solution in Docker.

        Args:
            hw_name: Homework identifier (e.g., "hw1")
            workspace: Path to the workspace directory
            verbose: Show detailed test output

        Returns:
            GradeResult with test outcomes
        """
        config = self.load_config(hw_name)
        hw_number = get_assignment_number(config)
        display_name = get_display_name(config)

        # Get test points mapping from config
        test_points = config.get('test_points', {})

        # Create result object
        result = GradeResult(
            project_name=config.get('assignment_name', hw_name),
            display_name=display_name,
            course_code="comp341",
            project_number=hw_number,
        )

        # Ensure Docker image is ready
        success, error = self.ensure_docker_image()
        if not success:
            result.compilation_error = f"Docker build failed: {error}"
            return result

        # Verify test file exists
        test_file = self.tests_dir / f"test_module{hw_number}.py"
        if not test_file.exists():
            result.compilation_error = f"Test file not found: {test_file}"
            return result

        # Run tests in container
        success, stdout, stderr = self.run_tests_in_container(
            workspace, config, verbose
        )

        report_path = workspace / PYTEST_REPORT_NAME
        result.test_results = self._parse_pytest_junit_xml(report_path, test_points)
        if not result.test_results:
            result.test_results = self._parse_pytest_output(stdout, stderr, test_points)

        # If no results and not successful, record the error
        if not result.test_results and not success:
            result.compilation_error = stderr or stdout or "Tests failed with no output"

        return result

    def _parse_pytest_output(
        self,
        stdout: str,
        stderr: str,
        test_points: dict = None,
    ) -> List[TestResult]:
        """Parse pytest output into TestResult objects.

        Args:
            stdout: pytest stdout
            stderr: pytest stderr
            test_points: Optional mapping of test names to point values
        """
        results = []
        test_points = test_points or {}

        # Pattern for test results: test_name PASSED/FAILED/ERROR
        # pytest -v output format: tests/test_module1.py::TestClass::test_name PASSED
        suite_manifest = load_suite_manifest(suite_dir=self.suite_override)
        test_pattern = re.compile(
            r'(?:(.+?)::)?(?:(\w+)::)?(test_\w+)\s+(PASSED|FAILED|ERROR|SKIPPED)'
        )

        for line in stdout.split('\n'):
            match = test_pattern.search(line)
            if match:
                rel_path = (match.group(1) or "").strip()
                class_name = match.group(2) or ""
                test_name = match.group(3)
                status_str = match.group(4)

                # Combine class and test name for clarity
                full_name = f"{class_name}::{test_name}" if class_name else test_name
                passed = (status_str == 'PASSED')

                # Get point value from config (default 1.0)
                max_pts = float(test_points.get(test_name, 1.0))

                # Detect test source from name
                manifest_source = source_from_manifest_path(suite_manifest, rel_path)
                if not manifest_source:
                    manifest_source = source_from_manifest_classname(suite_manifest, class_name)
                if manifest_source:
                    source = manifest_source
                elif "private" in full_name.lower() or "private" in rel_path.lower():
                    source = "private"
                elif "agent" in full_name.lower() or "test_my_" in full_name.lower():
                    source = "agent"
                else:
                    source = "public"

                results.append(TestResult(
                    name=full_name,
                    passed=passed,
                    points=max_pts if passed else 0.0,
                    max_points=max_pts,
                    source=source,
                ))

        # Check for various failure conditions when no tests found
        if not results:
            error_msg = None

            # Check for collection errors
            if 'error' in stderr.lower():
                error_msg = stderr[:500]
            # Check for import errors
            elif 'ImportError' in stdout or 'ModuleNotFoundError' in stdout:
                error_msg = "Import error in solution.py"
            # Check for no tests collected (solution not available)
            elif 'skipped' in stdout.lower() and 'no tests ran' not in stdout.lower():
                # Tests were skipped - likely no solution.py
                pass  # Don't add error, let skipped tests be reported
            elif 'no tests ran' in stdout.lower() or 'collected 0 items' in stdout.lower():
                error_msg = "No tests were collected"
            # Generic fallback
            elif stderr:
                error_msg = stderr[:500]

            if error_msg:
                results.append(TestResult(
                    name="test_collection",
                    passed=False,
                    points=0.0,
                    max_points=1.0,
                    error_message=error_msg,
                ))

        return results

    def _parse_pytest_junit_xml(
        self,
        report_path: Path,
        test_points: dict | None = None,
    ) -> List[TestResult]:
        """Parse pytest JUnit XML output into TestResult objects."""
        if not report_path.exists():
            return []

        suite_manifest = load_suite_manifest(suite_dir=self.suite_override)
        test_points = test_points or {}
        results: List[TestResult] = []

        try:
            tree = ET.parse(report_path)
        except ET.ParseError:
            return []

        root = tree.getroot()
        for testcase in root.findall(".//testcase"):
            test_name = testcase.get("name", "unknown")
            class_name = testcase.get("classname", "")
            file_name = testcase.get("file", "")
            time_str = testcase.get("time", "0")
            skipped = testcase.find("skipped")
            failure = testcase.find("failure")
            error = testcase.find("error")

            try:
                time_seconds = float(time_str)
            except ValueError:
                time_seconds = 0.0

            display_name = f"{class_name}::{test_name}" if class_name else test_name
            manifest_source = source_from_manifest_path(suite_manifest, file_name)
            if not manifest_source:
                manifest_source = source_from_manifest_classname(suite_manifest, class_name)

            if manifest_source:
                source = manifest_source
            elif "private" in class_name.lower() or "private" in file_name.lower():
                source = "private"
            elif "agent" in class_name.lower() or "agent" in file_name.lower():
                source = "agent"
            else:
                source = "public"

            if skipped is not None:
                status = "skip"
                passed = False
                error_message = skipped.get("message") or None
            else:
                passed = failure is None and error is None
                status = "pass" if passed else "fail"
                if failure is not None:
                    error_message = failure.get("message") or (failure.text[:500] if failure.text else None)
                elif error is not None:
                    error_message = error.get("message") or (error.text[:500] if error.text else None)
                else:
                    error_message = None

            max_pts = float(test_points.get(test_name, 1.0))
            results.append(TestResult(
                name=display_name,
                passed=passed,
                points=max_pts if passed else 0.0,
                max_points=max_pts,
                time_seconds=time_seconds,
                error_message=error_message,
                status=status,
                source=source,
            ))

        return results


def main():
    parser = argparse.ArgumentParser(
        description="COMP 341 Python ML Test Runner (Docker)"
    )
    parser.add_argument(
        "homework",
        help="Homework identifier (e.g., hw1, 1)"
    )
    parser.add_argument(
        "workspace",
        help="Path to the workspace directory"
    )
    parser.add_argument(
        "-v", "--verbose",
        action="store_true",
        help="Show detailed test output"
    )
    parser.add_argument(
        "--json",
        action="store_true",
        help="Output results as JSON"
    )
    parser.add_argument(
        "--rebuild",
        action="store_true",
        help="Force rebuild Docker image"
    )
    parser.add_argument(
        "--course",
        type=str,
        default=None,
        help="Path to course directory (auto-detected)"
    )

    args = parser.parse_args()

    # Normalize homework identifier
    hw_name = args.homework
    if hw_name.isdigit():
        hw_name = f"hw{hw_name}"

    workspace = Path(args.workspace).resolve()

    # Find comp341 directory
    if args.course:
        comp341_dir = Path(args.course).resolve()
    else:
        comp341_dir = Path(__file__).parent

    runner = PythonMLRunner(comp341_dir)

    # Force rebuild if requested
    if args.rebuild:
        success, error = runner.ensure_docker_image(force_rebuild=True)
        if not success:
            print(f"Error rebuilding Docker image: {error}", file=sys.stderr)
            sys.exit(1)

    if not workspace.exists():
        print(f"Error: Workspace not found: {workspace}", file=sys.stderr)
        sys.exit(1)

    try:
        result = runner.run_tests(hw_name, workspace, args.verbose)

        if args.json:
            print(json.dumps(result.to_dict(), indent=2))
        else:
            print(result.format_output(verbose=args.verbose))

        # Exit with error code if tests failed
        if result.compilation_error or result.passed_count < result.total_count:
            sys.exit(1)

    except FileNotFoundError as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        import traceback
        traceback.print_exc()
        sys.exit(1)


if __name__ == "__main__":
    main()
