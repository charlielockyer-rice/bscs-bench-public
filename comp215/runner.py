#!/usr/bin/env python3
"""
COMP 215 Java Test Runner (Docker-enabled)

This module provides functionality to compile and run JUnit tests
for COMP 215 homework assignments, parsing results and calculating scores.

By default, tests run inside a Docker container for isolation and consistency.
Use --no-docker flag to run tests directly on the host.

Usage:
    python runner.py <homework> <workspace> [options]

Examples:
    python runner.py 1 ./workspaces/agent_hw1
    python runner.py 2 ./workspaces/agent_hw2 --verbose
    python runner.py 3 ./workspaces/agent_hw3 --no-docker
"""

import argparse
import json
import os
import re
import shutil
import subprocess
import sys
from pathlib import Path
from typing import List, Tuple

# Add framework to path for imports
sys.path.insert(0, str(Path(__file__).parent.parent))
from framework.yaml_utils import parse_simple_yaml
from framework.docker_utils import (
    docker_image_exists,
    ensure_docker_image as _ensure_docker_image,
    is_in_container,
    run_bash_in_container,
    run_local,
)
from framework.config_compat import (
    normalize_config,
    get_assignment_number,
    get_display_name,
    get_timeout_seconds,
    get_total_tests,
    get_test_classes,
    DEFAULT_TIMEOUT_JAVA,
)
from framework.runner_utils import parse_junit_xml
from shared.results import TestResult, GradeResult

# Docker image name
DOCKER_IMAGE = "comp215-runner"

# Default timeouts
# Per-test timeout uses the centralized DEFAULT_TIMEOUT_JAVA constant
# from framework.config_compat (30 seconds for standard Java).
BUILD_TIMEOUT = 180  # 3 minutes for Docker build
COMPILATION_BUFFER = 60  # Extra time for compilation

# Test estimation
ESTIMATED_TESTS_PER_CLASS = 20  # Average tests per JUnit class
JUNIT_REPORT_DIR = "test-reports"


def _normalize_test(test) -> tuple:
    """Extract (name, passed, error_message, source) from TestResult or legacy dict."""
    if isinstance(test, TestResult):
        return test.name, test.passed, test.error_message, test.source
    return test['name'], test['passed'], test.get('error'), "public"


class JavaRunner:
    """Compiles and runs Java JUnit tests, with optional Docker support."""

    def __init__(self, comp215_dir: Path):
        self.comp215_dir = comp215_dir
        self.lib_dir = comp215_dir / "lib"
        self.configs_dir = comp215_dir / "configs"
        self.junit_jar = self.lib_dir / "junit-platform-console-standalone-1.9.0.jar"
        self.in_container = is_in_container()

    def load_config(self, homework_number: int) -> dict:
        """Load homework configuration from YAML.

        Normalizes the config to support both legacy and unified field names.
        """
        config_path = self.configs_dir / f"homework{homework_number}.yaml"
        if not config_path.exists():
            raise FileNotFoundError(f"Config not found: {config_path}")
        with open(config_path) as f:
            raw_config = parse_simple_yaml(f.read())
        return normalize_config(raw_config)

    # -------------------------------------------------------------------------
    # Docker Methods
    # -------------------------------------------------------------------------

    def ensure_docker_image(self, force_rebuild: bool = False) -> Tuple[bool, str]:
        """Build Docker image if not already built."""
        return _ensure_docker_image(DOCKER_IMAGE, self.comp215_dir, BUILD_TIMEOUT, force_rebuild)

    def run_tests_in_container(
        self,
        workspace: Path,
        config: dict,
        homework_number: int,
        verbose: bool = False
    ) -> Tuple[bool, str, str]:
        """Run compilation and tests inside Docker container (or locally if already in container)."""
        timeout_per_test = get_timeout_seconds(config, DEFAULT_TIMEOUT_JAVA)
        num_tests = get_total_tests(config, len(get_test_classes(config)) * ESTIMATED_TESTS_PER_CLASS)
        total_timeout = (timeout_per_test * max(num_tests, 5)) + COMPILATION_BUFFER

        # Build the compilation and test command for inside container
        script = self._build_container_script(workspace, config, homework_number, verbose)

        if self.in_container:
            # Already inside a pre-provisioned benchmark container — run script directly
            returncode, stdout, stderr = run_local(
                ["bash", "-c", script],
                timeout=total_timeout,
                cwd=workspace,
            )
            if verbose:
                if stdout:
                    print(stdout, file=sys.stderr)
                if stderr:
                    print(stderr, file=sys.stderr)
            return returncode == 0, stdout, stderr

        suite_override = os.environ.get("BSCS_TEST_SUITE_DIR", "").strip()
        extra_volumes = {
            # Mount the course root read-only so custom tests are available
            str(self.comp215_dir.resolve()): "/course:ro",
        }
        env = None
        if suite_override:
            suite_path = Path(suite_override).resolve()
            extra_volumes[str(suite_path)] = "/suite_tests:ro"
            env = {"BSCS_TEST_SUITE_DIR": "/suite_tests"}

        returncode, stdout, stderr = run_bash_in_container(
            image=DOCKER_IMAGE,
            script=script,
            workspace=workspace,
            timeout=total_timeout,
            memory="2g",
            cpus="2",
            network="none",
            extra_volumes=extra_volumes,
            env=env,
        )

        return returncode == 0, stdout, stderr

    def _build_container_script(
        self,
        workspace: Path,
        config: dict,
        homework_number: int,
        verbose: bool
    ) -> str:
        """Build bash script to compile and run tests inside container."""
        # Paths inside container (workspace is mounted at /workspace)
        # The skeleton_code directory structure is:
        #   /workspace/Homework_N/skeleton_code/src/main/
        #   /workspace/Homework_N/skeleton_code/src/test/
        #   /workspace/Homework_N/skeleton_code/lib/
        #
        # For workspaces created by setup_course, the structure may be:
        #   /workspace/skeleton_code/src/main/
        # or just /workspace/src/main/ depending on how workspace is set up

        lines = [
            "set -e",  # Exit on error
            "",
            "# Find the skeleton_code directory",
            f"if [ -d /workspace/Homework_{homework_number}/skeleton_code ]; then",
            f"    SKELETON=/workspace/Homework_{homework_number}/skeleton_code",
            "elif [ -d /workspace/skeleton_code ]; then",
            "    SKELETON=/workspace/skeleton_code",
            "else",
            "    SKELETON=/workspace",
            "fi",
            "",
            "# Set up directories",
            "SRC_MAIN=$SKELETON/src/main",
            "SRC_TEST=$SKELETON/src/test",
            "BUILD_DIR=$SKELETON/build",
            "LIB_DIR=$SKELETON/lib",
            "",
            "# Clean build directory",
            "rm -rf $BUILD_DIR",
            "mkdir -p $BUILD_DIR",
            "",
            "# Build classpath from homework lib + container JARs",
            "if [ -d $LIB_DIR ]; then",
            "    HW_JARS=$(find $LIB_DIR -name '*.jar' | tr '\\n' ':')",
            "else",
            "    HW_JARS=''",
            "fi",
            "CP=\"${HW_JARS}${JUNIT_CLASSPATH}\"",
            "",
            "# Compile main sources",
            "echo 'Compiling main sources...'",
            "find $SRC_MAIN -name '*.java' > /tmp/main_sources.txt",
            "if [ -s /tmp/main_sources.txt ]; then",
            "    javac -d $BUILD_DIR -cp \"$CP\" @/tmp/main_sources.txt",
            "fi",
            "",
            "# Compile test sources",
            "echo 'Compiling test sources...'",
            "TEST_CP=\"${BUILD_DIR}:${CP}\"",
            "",
            "# Check for explicit suite tests first, then workspace, then course root",
            "SUITE_TESTS=${BSCS_TEST_SUITE_DIR:-}",
            f"CUSTOM_TESTS=/workspace/tests/homework{homework_number}",
            f"COURSE_TESTS=/course/tests/homework{homework_number}",
            "if [ -n \"$SUITE_TESTS\" ] && [ -d $SUITE_TESTS ]; then",
            "    echo 'Using selected suite tests'",
            "    find $SUITE_TESTS -name '*.java' > /tmp/test_sources.txt",
            "elif [ -d $CUSTOM_TESTS ]; then",
            "    echo 'Using custom tests from workspace tests/'",
            "    find $CUSTOM_TESTS -name '*.java' > /tmp/test_sources.txt",
            "elif [ -d $COURSE_TESTS ]; then",
            "    echo 'Using custom tests from course tests/'",
            "    find $COURSE_TESTS -name '*.java' > /tmp/test_sources.txt",
            "elif [ -d $SRC_TEST ]; then",
            "    echo 'Using skeleton tests'",
            "    find $SRC_TEST -name '*.java' > /tmp/test_sources.txt",
            "else",
            "    echo 'No test sources found'",
            "    exit 1",
            "fi",
            "",
            "if [ -s /tmp/test_sources.txt ]; then",
            "    javac -d $BUILD_DIR -cp \"$TEST_CP\" @/tmp/test_sources.txt",
            "fi",
            "",
            "# Run JUnit tests (per-test timeout prevents infinite loops)",
            "echo 'Running tests...'",
            "java -jar /opt/junit/junit-platform-console-standalone-1.9.0.jar \\",
            "    --class-path \"$BUILD_DIR:$CP\" \\",
            "    --scan-class-path \\",
            "    --include-classname='.*Test.*' \\",
            "    --config junit.jupiter.execution.timeout.default=30s \\",
            f"    --reports-dir /workspace/{JUNIT_REPORT_DIR} \\",
            "    --details=verbose",
        ]

        return "\n".join(lines)

    # -------------------------------------------------------------------------
    # Host (non-Docker) Methods
    # -------------------------------------------------------------------------

    def find_jars(self, hw_lib_dir: Path) -> str:
        """Build classpath from JAR files."""
        jars = list(hw_lib_dir.glob("*.jar"))
        return ":".join(str(j) for j in jars)

    def compile_sources(self, src_dir: Path, build_dir: Path, classpath: str) -> Tuple[bool, str]:
        """Compile Java source files."""
        java_files = list(src_dir.rglob("*.java"))
        if not java_files:
            return False, f"No Java files found in {src_dir}"

        build_dir.mkdir(parents=True, exist_ok=True)

        cmd = ["javac", "-d", str(build_dir), "-cp", classpath] + [str(f) for f in java_files]
        result = subprocess.run(cmd, capture_output=True, text=True)

        if result.returncode != 0:
            return False, result.stderr
        return True, ""

    def run_tests_host(self, build_dir: Path, test_classes: List[str], hw_lib: Path) -> Tuple[List[dict], str]:
        """Run JUnit tests on host and parse results."""
        reports_dir = build_dir.parent / JUNIT_REPORT_DIR
        shutil.rmtree(reports_dir, ignore_errors=True)

        # Look for JUnit JAR in multiple locations
        junit_jar = None
        for jar_path in [
            hw_lib / "junit-platform-console-standalone-1.9.0.jar",
            self.junit_jar,
        ]:
            if jar_path.exists():
                junit_jar = jar_path
                break

        if not junit_jar:
            return [], f"JUnit JAR not found in {hw_lib} or {self.lib_dir}"

        cmd = [
            "java", "-jar", str(junit_jar),
            "--class-path", str(build_dir),
            "--scan-class-path",
            "--include-classname=.*Test.*",
            "--config", "junit.jupiter.execution.timeout.default=30s",
            "--reports-dir", str(reports_dir),
            "--details=verbose"
        ]

        result = subprocess.run(cmd, capture_output=True, text=True, timeout=300)
        output = result.stdout + result.stderr

        return self._parse_junit_output(output), output

    # -------------------------------------------------------------------------
    # JUnit Output Parsing
    # -------------------------------------------------------------------------

    def _parse_junit_output(self, output: str) -> List[dict]:
        """Parse JUnit console output into test results."""
        # Strip ANSI escape codes
        ansi_escape = re.compile(r'\x1B(?:[@-Z\\-_]|\[[0-?]*[ -/]*[@-~])')
        output_clean = ansi_escape.sub('', output)

        tests = []
        lines = output_clean.split('\n')
        current_test = None

        for line in lines:
            # Match lines like "-- testSmallMaxFactorize30()" with unicode box chars
            test_match = re.search(r'[├└│─\-]+\s*(\w+)\(\)\s*$', line)
            if test_match:
                current_test = test_match.group(1)
                continue

            # Match status lines
            if current_test:
                if 'SUCCESSFUL' in line or '\u2714' in line:  # checkmark
                    tests.append({'name': current_test, 'passed': True, 'error': None})
                    current_test = None
                elif 'FAILED' in line or '\u2718' in line:  # X mark
                    tests.append({'name': current_test, 'passed': False, 'error': ''})
                    # Keep current_test to potentially capture error
                elif 'caught:' in line and tests and not tests[-1].get('passed', True):
                    error_match = re.search(r'caught:\s*(.+)', line)
                    if error_match:
                        tests[-1]['error'] = error_match.group(1).strip()
                elif line.strip().startswith('status:'):
                    if 'SUCCESSFUL' in line:
                        tests.append({'name': current_test, 'passed': True, 'error': None})
                    else:
                        tests.append({'name': current_test, 'passed': False, 'error': ''})
                    current_test = None

        return tests

    # -------------------------------------------------------------------------
    # Main Grade Method
    # -------------------------------------------------------------------------

    def grade(
        self,
        homework_number: int,
        workspace_path: Path,
        verbose: bool = False,
        use_docker: bool = True
    ) -> GradeResult:
        """Grade a homework submission."""
        config = self.load_config(homework_number)

        result = GradeResult(
            project_name=f"homework{homework_number}",
            display_name=get_display_name(config) or f'Homework {homework_number}',
            course_code="comp215",
            project_number=homework_number
        )

        if use_docker:
            return self._grade_docker(homework_number, workspace_path, config, result, verbose)
        else:
            return self._grade_host(homework_number, workspace_path, config, result, verbose)

    def _grade_docker(
        self,
        homework_number: int,
        workspace_path: Path,
        config: dict,
        result: GradeResult,
        verbose: bool
    ) -> GradeResult:
        """Grade using Docker container."""
        # Ensure Docker image is ready
        success, error = self.ensure_docker_image()
        if not success:
            result.compilation_error = error
            return result

        # Run tests in container
        success, stdout, stderr = self.run_tests_in_container(
            workspace_path, config, homework_number, verbose
        )

        combined_output = stdout + stderr

        if not success:
            # Distinguish compilation errors from test failures.
            # The container script prints "Running tests..." only after
            # successful compilation (set -e ensures javac failure aborts
            # before that point).  If that marker is present, tests ran
            # and any "error:" strings are from JUnit output (e.g.
            # AssertionFailedError), not javac — so fall through to the
            # test parser instead of returning early.
            tests_started = "running tests" in combined_output.lower()

            if not tests_started:
                if "error:" in combined_output.lower() and "compiling" in combined_output.lower():
                    result.compilation_error = combined_output[:2000]
                    return result
                if "error:" in stderr and ".java:" in stderr:
                    result.compilation_error = stderr[:2000]
                    return result

        tests = parse_junit_xml(workspace_path / JUNIT_REPORT_DIR)
        if not tests:
            tests = self._parse_junit_output(combined_output)
        test_points = config.get('test_points', {})

        for test in tests:
            name, passed, error_message, source = _normalize_test(test)
            points = test_points.get(name, 1.0)

            result.test_results.append(TestResult(
                name=name,
                passed=passed,
                points=points if passed else 0,
                max_points=points,
                error_message=error_message,
                source=source,
            ))

        # If no results found but execution failed, report the error
        if not result.test_results and not success:
            result.compilation_error = combined_output[:2000] if combined_output else "Tests failed with no output"

        return result

    def _grade_host(
        self,
        homework_number: int,
        workspace_path: Path,
        config: dict,
        result: GradeResult,
        verbose: bool
    ) -> GradeResult:
        """Grade using host Java installation (legacy mode)."""
        # Find the homework directory
        hw_dir = workspace_path / f"Homework_{homework_number}"
        if not hw_dir.exists():
            hw_dir = workspace_path

        skeleton_dir = hw_dir / "skeleton_code"
        if not skeleton_dir.exists():
            skeleton_dir = hw_dir

        src_main = skeleton_dir / "src" / "main"
        src_test = skeleton_dir / "src" / "test"
        build_dir = skeleton_dir / "build"

        # Find JARs in homework's lib directory
        hw_lib = skeleton_dir / "lib"
        if not hw_lib.exists():
            hw_lib = self.comp215_dir / f"Homework_{homework_number}" / "skeleton_code" / "lib"

        classpath = self.find_jars(hw_lib)
        if not classpath:
            result.compilation_error = f"No JAR files found in {hw_lib}"
            return result

        # Compile main sources
        success, error = self.compile_sources(src_main, build_dir, classpath)
        if not success:
            result.compilation_error = f"Main compilation failed:\n{error}"
            return result

        # Compile test sources
        test_classpath = f"{build_dir}:{classpath}"

        # Check for custom tests (preferred over skeleton tests)
        suite_override = os.environ.get("BSCS_TEST_SUITE_DIR", "").strip()
        custom_tests = Path(suite_override).resolve() if suite_override else (self.comp215_dir / "tests" / f"homework{homework_number}")
        if custom_tests.exists():
            success, error = self.compile_sources(custom_tests, build_dir, test_classpath)
            if not success:
                result.compilation_error = f"Custom test compilation failed:\n{error}"
                return result
        elif src_test.exists():
            success, error = self.compile_sources(src_test, build_dir, test_classpath)
            if not success:
                result.compilation_error = f"Test compilation failed:\n{error}"
                return result

        # Run tests
        test_classes = get_test_classes(config)
        tests, raw_output = self.run_tests_host(build_dir, test_classes, hw_lib)
        xml_tests = parse_junit_xml(skeleton_dir / JUNIT_REPORT_DIR)
        if xml_tests:
            tests = xml_tests

        # Map test results to points
        test_points = config.get('test_points', {})

        for test in tests:
            name, passed, error_message, source = _normalize_test(test)
            points = test_points.get(name, 1.0)

            result.test_results.append(TestResult(
                name=name,
                passed=passed,
                points=points if passed else 0,
                max_points=points,
                error_message=error_message,
                source=source,
            ))

        return result


def parse_homework_arg(value: str) -> int:
    """Parse homework argument - accepts int or 'homeworkN' format."""
    # Try direct integer first
    try:
        return int(value)
    except ValueError:
        pass
    # Try 'homeworkN' format
    match = re.match(r'homework(\d+)', value, re.IGNORECASE)
    if match:
        return int(match.group(1))
    raise argparse.ArgumentTypeError(f"Invalid homework: {value}. Use number (1-6) or 'homework1' format.")


def main():
    """Main entry point."""
    parser = argparse.ArgumentParser(description="COMP 215 Java Autograder (Docker-enabled)")
    parser.add_argument("homework", type=parse_homework_arg, help="Homework number (1-6) or 'homeworkN'")
    parser.add_argument("workspace", type=str, help="Path to workspace")
    parser.add_argument("-v", "--verbose", action="store_true", help="Show detailed output")
    parser.add_argument("--json", action="store_true", help="Output as JSON")
    parser.add_argument("--no-docker", action="store_true", help="Run on host without Docker")
    parser.add_argument("--rebuild", action="store_true", help="Force rebuild Docker image")

    args = parser.parse_args()

    # Find comp215 directory
    comp215_dir = Path(__file__).parent

    runner = JavaRunner(comp215_dir)

    # Force rebuild if requested
    if args.rebuild and not args.no_docker:
        success, error = runner.ensure_docker_image(force_rebuild=True)
        if not success:
            print(f"Error rebuilding Docker image: {error}", file=sys.stderr)
            sys.exit(1)

    workspace_path = Path(args.workspace).resolve()
    if not workspace_path.exists():
        print(f"Error: Workspace not found: {workspace_path}")
        sys.exit(1)

    use_docker = not args.no_docker
    result = runner.grade(args.homework, workspace_path, args.verbose, use_docker)

    if args.json:
        print(json.dumps(result.to_dict(), indent=2))
    else:
        print(result.format_output(args.verbose))

    # Exit with error code if tests failed
    if result.compilation_error or result.passed_count < result.total_count:
        sys.exit(1)


if __name__ == "__main__":
    main()
