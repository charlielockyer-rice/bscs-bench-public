#!/usr/bin/env python3
"""
COMP 321 C Test Runner

This module provides functionality to compile and run tests for
COMP 321 systems programming assignments. Supports both Docker-based
execution (recommended) and host execution (fallback).

Usage:
    python runner.py <project> <workspace> [options]

Examples:
    python runner.py factors ./workspaces/student1
    python runner.py count ./workspaces/student1 --verbose
    python runner.py shell ./workspaces/student1 --json
    python runner.py proxy ./workspaces/student1 --no-docker
"""

import argparse
import json
import os
import re
import subprocess
import sys
from pathlib import Path
from typing import List, Optional, Tuple

# Add parent directory to path for imports
sys.path.insert(0, str(Path(__file__).parent.parent))
from framework.docker_utils import (
    ensure_docker_image as _ensure_docker_image,
    compile_in_docker,
    is_in_container,
    run_local,
)
from framework.config_compat import DEFAULT_TIMEOUT_C
from framework.runner_utils import load_course_config, make_grade_result
from framework.test_loader import load_python_module_from_file, load_python_test_modules
from framework.suite_manifest import load_suite_manifest, source_from_manifest_path
from shared.results import TestResult, GradeResult

# Docker image name
DOCKER_IMAGE = "comp321-runner"

# Default timeouts
# Per-test timeout uses the centralized DEFAULT_TIMEOUT_C constant
# from framework.config_compat (60 seconds for C projects).
BUILD_TIMEOUT = 300  # 5 minutes for Docker build
COMPILATION_BUFFER = 60  # Extra time for make compilation

# Projects that require network access
NETWORK_PROJECTS = {"proxy"}


class CRunner:
    """Compiles and runs C program tests."""

    def __init__(self, comp321_dir: Path, use_docker: bool = True):
        self.comp321_dir = comp321_dir
        suite_override = os.environ.get("BSCS_TEST_SUITE_DIR", "").strip()
        self.tests_dir = Path(suite_override).resolve() if suite_override else (comp321_dir / "tests")
        self.configs_dir = comp321_dir / "configs"
        # When running inside a pre-provisioned benchmark container, disable Docker
        # and run tests directly (the runtime tools are already available).
        if is_in_container():
            self.use_docker = False
        else:
            self.use_docker = use_docker

    def load_config(self, project_name: str) -> dict:
        return load_course_config(self.configs_dir, project_name)

    # -------------------------------------------------------------------------
    # Docker Methods
    # -------------------------------------------------------------------------

    def ensure_docker_image(self, force_rebuild: bool = False) -> Tuple[bool, str]:
        """Build Docker image if not already built."""
        return _ensure_docker_image(DOCKER_IMAGE, self.comp321_dir, BUILD_TIMEOUT, force_rebuild)

    def compile_in_container(self, workspace: Path, project_name: str, config: dict) -> Tuple[bool, str]:
        """Compile the C project inside Docker container."""
        # Get executable from nested tests dict or fallback to top-level
        executable = config.get("tests", {}).get("executable") or config.get("executable", project_name)
        needs_network = project_name in NETWORK_PROJECTS

        # Determine build directory - may be a subdirectory (e.g., readjcf/readjcf)
        if "/" in executable:
            subdir = executable.rsplit("/", 1)[0]
            build_cmd = f"cd {subdir} && make clean && make"
        else:
            build_cmd = "make clean && make"

        # Fix workspace lib symlink inside Docker. The symlink points to an
        # absolute host path that doesn't exist in the container. Replace it
        # with a link to /opt/csapp (where the Docker image has csapp installed).
        lib_dir = self.comp321_dir / "lib"
        if lib_dir.is_dir():
            build_cmd = f"rm -f /workspace/lib && ln -sf /opt/csapp /workspace/lib && {build_cmd}"

        return compile_in_docker(
            image=DOCKER_IMAGE,
            workspace=workspace,
            build_cmd=build_cmd,
            timeout=COMPILATION_BUFFER,
            memory="4g",
            cpus="2",
            network=needs_network,
        )

    # -------------------------------------------------------------------------
    # Host Execution Methods (fallback / legacy)
    # -------------------------------------------------------------------------

    def compile_project_host(self, workspace: Path, project_name: str) -> Tuple[bool, str]:
        """Compile the C project on host."""
        config = self.load_config(project_name)
        # Get executable from nested tests dict or fallback to top-level
        executable = config.get("tests", {}).get("executable") or config.get("executable", project_name)

        # Determine build directory - may be a subdirectory
        if "/" in executable:
            # Executable is in a subdirectory (e.g., "readjcf/readjcf")
            subdir = executable.rsplit("/", 1)[0]
            build_dir = workspace / subdir
        else:
            build_dir = workspace

        # Check for Makefile
        makefile = build_dir / "Makefile"
        if makefile.exists():
            result = subprocess.run(
                ["make", "-C", str(build_dir)],
                capture_output=True,
                text=True,
                timeout=60
            )
            if result.returncode != 0:
                return False, result.stderr
            return True, ""

        # Try direct gcc compilation
        c_files = list(build_dir.glob("*.c"))
        if not c_files:
            return False, f"No C files found in {build_dir}"

        cmd = ["gcc", "-Wall", "-Wextra", "-o", str(workspace / executable)]
        cmd.extend([str(f) for f in c_files])

        result = subprocess.run(
            cmd,
            capture_output=True,
            text=True,
            timeout=60
        )
        if result.returncode != 0:
            return False, result.stderr
        return True, ""

    # -------------------------------------------------------------------------
    # Test Execution
    # -------------------------------------------------------------------------

    def load_test_module(self, project_name: str):
        """Load the test module for a project."""
        test_file = self.tests_dir / f"test_{project_name}.py"
        return load_python_module_from_file(test_file, f"test_{project_name}")

    def load_test_modules(self, project_name: str) -> List[object]:
        """Load one or more test modules for a project.

        Default behavior loads the canonical project module.
        Suite override behavior loads all test_*.py modules so agent-authored
        tests run alongside public tests.
        """
        suite_override = os.environ.get("BSCS_TEST_SUITE_DIR", "").strip()
        suite_dir = self.tests_dir if suite_override else None
        return load_python_test_modules(
            canonical_test_file=self.tests_dir / f"test_{project_name}.py",
            canonical_module_name=f"test_{project_name}",
            suite_dir=suite_dir,
            suite_namespace="bscs_suite",
        )

    def run_tests(self, project_name: str, workspace_path: Path, verbose: bool = False) -> GradeResult:
        """Run all tests for a project."""
        config = self.load_config(project_name)

        result = make_grade_result(config, "comp321", project_name)

        # Compile project
        if self.use_docker:
            # Ensure Docker image is ready
            success, error = self.ensure_docker_image()
            if not success:
                result.compilation_error = error
                return result

            # Compile in container
            success, error = self.compile_in_container(workspace_path, project_name, config)
            if not success:
                result.compilation_error = error
                return result

            # Set environment variable for fixtures to use Docker
            os.environ["USE_DOCKER"] = "true"
        else:
            # Host compilation (fallback)
            success, error = self.compile_project_host(workspace_path, project_name)
            if not success:
                result.compilation_error = error
                return result

            # Set environment variable for fixtures to use host execution
            os.environ["USE_DOCKER"] = "false"

        # Load test module(s)
        try:
            test_modules = self.load_test_modules(project_name)
        except FileNotFoundError as e:
            result.compilation_error = str(e)
            return result

        # Find and run test functions
        suite_manifest = load_suite_manifest(suite_dir=self.tests_dir if os.environ.get("BSCS_TEST_SUITE_DIR", "").strip() else None)
        for test_module in test_modules:
            module_full = getattr(test_module, "__name__", "test_module")
            module_label = module_full.split(".")[-1]
            test_points = getattr(test_module, "TEST_POINTS", {})
            module_file = getattr(test_module, "__file__", "")

            # Detect test source from module name
            rel_path = ""
            if module_file:
                try:
                    rel_path = str(Path(module_file).resolve().relative_to(self.tests_dir.resolve()).as_posix())
                except Exception:
                    rel_path = Path(module_file).name
            manifest_source = source_from_manifest_path(suite_manifest, rel_path) if rel_path else None
            if manifest_source:
                test_source = manifest_source
            elif "_private" in module_full:
                test_source = "private"
            elif "_agent" in module_full or "test_my_" in module_full:
                test_source = "agent"
            else:
                test_source = "public"

            for name in dir(test_module):
                if name.startswith("test_"):
                    test_func = getattr(test_module, name)
                    if callable(test_func):
                        points = test_points.get(name, 1.0)
                        input_desc = getattr(test_func, "input_description", "")
                        case_name = f"{module_label}.{name}"

                        try:
                            # Run test
                            test_result = test_func(workspace_path)

                            if hasattr(test_result, 'passed'):
                                # TestResult object from fixtures
                                actual_str = str(test_result.actual) if hasattr(test_result, 'actual') else ""

                                result.test_results.append(TestResult(
                                    name=case_name,
                                    passed=test_result.passed,
                                    points=points if test_result.passed else 0,
                                    max_points=points,
                                    expected=str(test_result.expected) if hasattr(test_result, 'expected') else "",
                                    actual=actual_str,
                                    error_message=test_result.error_message if hasattr(test_result, 'error_message') else None,
                                    input_description=input_desc,
                                    source=test_source,
                                ))

                                # Extract performance index from test_performance result
                                if name == "test_performance" and test_result.passed:
                                    perf_match = re.search(
                                        r'Perf index = (\d+)/40 \(util\) \+ (\d+)/60 \(thru\) = (\d+)/100',
                                        actual_str
                                    )
                                    if perf_match:
                                        result.utilization_score = int(perf_match.group(1))
                                        result.throughput_score = int(perf_match.group(2))
                                        result.performance_index = int(perf_match.group(3))
                            else:
                                # Legacy format - tuple (passed, expected, actual)
                                passed, expected, actual = test_result if isinstance(test_result, tuple) else (test_result, "", "")
                                result.test_results.append(TestResult(
                                    name=case_name,
                                    passed=bool(passed),
                                    points=points if passed else 0,
                                    max_points=points,
                                    expected=str(expected),
                                    actual=str(actual),
                                    input_description=input_desc,
                                    source=test_source,
                                ))

                        except Exception as e:
                            result.test_results.append(TestResult(
                                name=case_name,
                                passed=False,
                                points=0,
                                max_points=points,
                                error_message=str(e),
                                input_description=input_desc,
                                source=test_source,
                            ))

        return result

    def run_malloc_perf(self, workspace_path: Path) -> Tuple[bool, str]:
        """Run malloc performance benchmark in Docker (mdriver -v)."""
        config = self.load_config("malloc")

        if is_in_container():
            success, error = self.compile_project_host(workspace_path, "malloc")
            if not success:
                return False, error
            returncode, stdout, stderr = run_local(
                ["./mdriver", "-v"],
                cwd=workspace_path,
                timeout=120,
            )
            output = (stdout or "") + (stderr or "")
            if returncode != 0:
                return False, output or f"mdriver exited with code {returncode}"
            return True, output

        # Ensure Docker image is ready
        success, error = self.ensure_docker_image()
        if not success:
            return False, error

        # Compile in container
        success, error = self.compile_in_container(workspace_path, "malloc", config)
        if not success:
            return False, error

        # Run mdriver in container
        from framework.docker_utils import run_program_in_docker

        returncode, stdout, stderr = run_program_in_docker(
            workspace=workspace_path,
            executable="mdriver",
            image=DOCKER_IMAGE,
            args=["-v"],
            timeout=120,
            network=False,
            memory="4g",
            cpus="2"
        )

        output = (stdout or "") + (stderr or "")
        if returncode != 0:
            return False, output or f"mdriver exited with code {returncode}"

        return True, output


def main():
    """Main entry point."""
    parser = argparse.ArgumentParser(description="COMP 321 C Autograder")
    parser.add_argument("project", type=str, help="Project name (factors, count, linking, shell, malloc, proxy)")
    parser.add_argument("workspace", type=str, help="Path to workspace")
    parser.add_argument("-v", "--verbose", action="store_true", help="Show detailed output")
    parser.add_argument("--json", action="store_true", help="Output as JSON")
    parser.add_argument("--course", type=str, default=None, help="Path to course directory")
    parser.add_argument("--no-docker", action="store_true", help="Run without Docker (host execution)")
    parser.add_argument("--rebuild", action="store_true", help="Force rebuild Docker image")
    parser.add_argument("--perf", action="store_true", help="Run malloc performance benchmark (Docker only)")

    args = parser.parse_args()

    # Find comp321 directory
    if args.course:
        comp321_dir = Path(args.course).resolve()
    else:
        comp321_dir = Path(__file__).parent

    use_docker = not args.no_docker
    runner = CRunner(comp321_dir, use_docker=use_docker)

    # Force rebuild if requested
    if args.rebuild and use_docker:
        success, error = runner.ensure_docker_image(force_rebuild=True)
        if not success:
            print(f"Error rebuilding Docker image: {error}", file=sys.stderr)
            sys.exit(1)

    workspace_path = Path(args.workspace).resolve()
    if not workspace_path.exists():
        print(f"Error: Workspace not found: {workspace_path}")
        sys.exit(1)

    if args.perf:
        if args.project != "malloc":
            print("Error: --perf is only supported for the malloc project", file=sys.stderr)
            sys.exit(1)
        if args.no_docker:
            print("Error: --perf requires Docker (remove --no-docker)", file=sys.stderr)
            sys.exit(1)

        success, output = runner.run_malloc_perf(workspace_path)
        if success:
            if args.json:
                perf_match = re.search(
                    r'Perf index = (\d+)/40 \(util\) \+ (\d+)/60 \(thru\) = (\d+)/100',
                    output
                )
                util = int(perf_match.group(1)) if perf_match else None
                thru = int(perf_match.group(2)) if perf_match else None
                perf = int(perf_match.group(3)) if perf_match else None

                trace_results = re.finditer(r'trace\s+(\S+)\s+(yes|no)\s+\d+%\s+\d+', output)
                passed = 0
                total = 0
                for match in trace_results:
                    total += 1
                    if match.group(2) == "yes":
                        passed += 1

                payload = {
                    "performanceIndex": perf,
                    "utilizationScore": util,
                    "throughputScore": thru,
                    "correctness": {"passed": passed, "total": total},
                    "raw": output,
                }
                print(json.dumps(payload, indent=2))
            else:
                print(output)
            sys.exit(0)
        if args.json:
            print(json.dumps({"error": output}), file=sys.stderr)
        else:
            print(output, file=sys.stderr)
        sys.exit(1)

    result = runner.run_tests(args.project, workspace_path, args.verbose)

    if args.json:
        print(json.dumps(result.to_dict(), indent=2))
    else:
        print(result.format_output(args.verbose))

    # Exit with error code if tests failed
    if result.compilation_error or result.passed_count < result.total_count:
        sys.exit(1)


if __name__ == "__main__":
    main()
