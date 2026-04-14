"""
Shared utilities for course test runners.

Provides common operations duplicated across comp310, comp321, comp322, comp411
runners: config loading, JUnit XML parsing, GradeResult construction, and CLI
boilerplate.
"""

import argparse
import json
import sys
import xml.etree.ElementTree as ET
from pathlib import Path
from typing import List, Optional, Tuple

from framework.yaml_utils import parse_simple_yaml
from framework.config_compat import normalize_config, get_assignment_number, get_display_name
from framework.suite_manifest import load_suite_manifest, source_from_manifest_classname
from shared.results import TestResult, GradeResult


def load_course_config(configs_dir: Path, name: str) -> dict:
    """Load and normalize a course config YAML by assignment name.

    Args:
        configs_dir: Path to the configs/ directory
        name: Config filename stem (e.g. "hw01", "assignment3", "factors")

    Returns:
        Normalized config dict

    Raises:
        FileNotFoundError: If config file doesn't exist
    """
    config_path = configs_dir / f"{name}.yaml"
    if not config_path.exists():
        raise FileNotFoundError(f"Config not found: {config_path}")
    with open(config_path) as f:
        raw_config = parse_simple_yaml(f.read())
    return normalize_config(raw_config)


def parse_junit_xml(reports_dir: Path, fallback_dirs: Optional[List[Path]] = None) -> List[TestResult]:
    """Parse JUnit XML test reports into TestResult objects.

    Searches reports_dir first, then any fallback_dirs in order.

    Args:
        reports_dir: Primary directory containing TEST-*.xml files
        fallback_dirs: Additional directories to check if primary is empty

    Returns:
        List of TestResult objects
    """
    if not reports_dir.exists() and fallback_dirs:
        for d in fallback_dirs:
            if d.exists():
                reports_dir = d
                break

    if not reports_dir.exists():
        return []

    suite_manifest = load_suite_manifest()
    results = []
    for xml_file in reports_dir.glob("TEST-*.xml"):
        try:
            tree = ET.parse(xml_file)
            root = tree.getroot()

            for testcase in root.findall(".//testcase"):
                name = testcase.get("name", "unknown")
                classname = testcase.get("classname", "")
                time_str = testcase.get("time", "0")

                try:
                    time_seconds = float(time_str)
                except ValueError:
                    time_seconds = 0.0

                failure = testcase.find("failure")
                error = testcase.find("error")
                skipped = testcase.find("skipped")

                if skipped is not None:
                    continue

                passed = failure is None and error is None
                error_message = None

                if failure is not None:
                    error_message = failure.get("message", "")
                    if failure.text:
                        error_message = failure.text[:2000]
                elif error is not None:
                    error_message = error.get("message", "")
                    if error.text:
                        error_message = error.text[:2000]

                full_name = f"{classname.split('.')[-1]}.{name}" if classname else name

                # Detect test source from classname
                manifest_source = source_from_manifest_classname(suite_manifest, classname)
                if manifest_source:
                    source = manifest_source
                elif "Private" in classname or "private" in classname:
                    source = "private"
                elif "agent" in classname.lower() or "test_my_" in classname.lower():
                    source = "agent"
                else:
                    source = "public"

                results.append(TestResult(
                    name=full_name,
                    passed=passed,
                    points=1.0 if passed else 0.0,
                    max_points=1.0,
                    error_message=error_message,
                    time_seconds=time_seconds,
                    source=source,
                ))

        except ET.ParseError as e:
            print(f"Warning: Could not parse {xml_file}: {e}", file=sys.stderr)

    return results


def make_grade_result(config: dict, course_code: str, project_name: str) -> GradeResult:
    """Construct a GradeResult from a normalized config.

    Args:
        config: Normalized config dict
        course_code: e.g. "comp310", "comp322"
        project_name: e.g. "hw01", "assignment3"

    Returns:
        GradeResult with metadata populated, test_results empty
    """
    return GradeResult(
        project_name=project_name,
        display_name=get_display_name(config) or project_name.replace("_", " ").title(),
        course_code=course_code,
        project_number=get_assignment_number(config) or 0,
    )


def check_java_compilation_error(
    success: bool, stdout: str, stderr: str
) -> Optional[str]:
    """Check Docker Java build output for compilation errors.

    Returns the error string if a compilation error is detected, else None.
    """
    if success:
        return None
    combined = stdout + stderr
    if "BUILD FAILURE" in combined:
        build_failure_markers = (
            "COMPILATION ERROR",
            "cannot find symbol",
            "checkstyle",
            "Failed to execute goal",
        )
        if any(marker in combined for marker in build_failure_markers):
            return stderr if stderr else stdout
    return None


def runner_main(
    description: str,
    course_code: str,
    runner_factory,
    extra_args_fn=None,
    post_parse_fn=None,
):
    """Shared CLI entry point for course runners.

    Args:
        description: argparse description string
        course_code: e.g. "comp310"
        runner_factory: callable(course_dir: Path) -> runner instance
            Runner must have run_tests(project, workspace, verbose) -> GradeResult
            and optionally ensure_docker_image(force_rebuild).
        extra_args_fn: optional callable(parser) to add extra CLI arguments
        post_parse_fn: optional callable(args, runner) -> bool that runs after
            parsing. Return True to exit early (e.g. for --perf handling).
    """
    parser = argparse.ArgumentParser(description=description)
    parser.add_argument("project", type=str, help="Project/assignment name")
    parser.add_argument("workspace", type=str, help="Path to workspace")
    parser.add_argument("-v", "--verbose", action="store_true", help="Show detailed output")
    parser.add_argument("--json", action="store_true", help="Output as JSON")
    parser.add_argument("--course", type=str, default=None, help="Path to course directory")
    parser.add_argument("--rebuild", action="store_true", help="Force rebuild Docker image")

    if extra_args_fn:
        extra_args_fn(parser)

    args = parser.parse_args()

    course_dir = Path(args.course).resolve() if args.course else Path(__file__).parent.parent / course_code
    runner = runner_factory(course_dir)

    if args.rebuild and hasattr(runner, "ensure_docker_image"):
        success, error = runner.ensure_docker_image(force_rebuild=True)
        if not success:
            print(f"Error rebuilding Docker image: {error}", file=sys.stderr)
            sys.exit(1)

    workspace_path = Path(args.workspace).resolve()
    if not workspace_path.exists():
        print(f"Error: Workspace not found: {workspace_path}", file=sys.stderr)
        sys.exit(1)

    if post_parse_fn and post_parse_fn(args, runner):
        return

    try:
        result = runner.run_tests(args.project, workspace_path, args.verbose)
    except FileNotFoundError as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)

    if args.json:
        print(json.dumps(result.to_dict(), indent=2))
    else:
        print(result.format_output(args.verbose))

    if result.compilation_error or result.passed_count < result.total_count:
        sys.exit(1)
