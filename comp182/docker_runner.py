#!/usr/bin/env python3
"""
COMP 182 Docker Runner - thin wrapper around framework runner.

Runs Python tests inside a Docker container for isolation and consistency.
For non-Docker execution, use framework.cli directly.
"""

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))
from framework.docker_utils import ensure_docker_image, run_python_tests_in_docker

DOCKER_IMAGE = "comp182-runner"
BUILD_TIMEOUT = 120
TEST_TIMEOUT = 300


def run_tests_in_docker(
    workspace: Path,
    comp182_dir: Path,
    verbose: bool = False,
    cli_args: list[str] | None = None,
) -> dict:
    """
    Run framework tests inside Docker container (or locally if in container).

    Returns dict with keys: success, stdout, stderr, error
    """
    return run_python_tests_in_docker(
        workspace=workspace,
        course_dir=comp182_dir,
        docker_image=DOCKER_IMAGE,
        build_timeout=BUILD_TIMEOUT,
        test_timeout=TEST_TIMEOUT,
        verbose=verbose,
        cli_args=cli_args,
    )


def main():
    """CLI entry point."""
    import argparse

    parser = argparse.ArgumentParser(description="COMP 182 Docker Runner")
    parser.add_argument("workspace", type=str, help="Path to workspace")
    parser.add_argument("-v", "--verbose", action="store_true")
    parser.add_argument("--json", action="store_true")
    parser.add_argument("--history", action="store_true")
    parser.add_argument("--check", action="store_true")
    parser.add_argument("--rebuild", action="store_true", help="Force rebuild Docker image")

    args = parser.parse_args()

    comp182_dir = Path(__file__).parent
    workspace = Path(args.workspace).resolve()

    if not workspace.exists():
        print(f"Error: Workspace not found: {workspace}", file=sys.stderr)
        sys.exit(1)

    # Handle rebuild
    if args.rebuild:
        success, error = ensure_docker_image(DOCKER_IMAGE, comp182_dir, BUILD_TIMEOUT, force_rebuild=True)
        if not success:
            print(f"Error rebuilding Docker image: {error}", file=sys.stderr)
            sys.exit(1)

    cli_args: list[str] = []
    if args.verbose:
        cli_args.append("--verbose")
    if args.json:
        cli_args.append("--json")
    if args.history:
        cli_args.append("--history")
    if args.check:
        cli_args.append("--check")

    result = run_tests_in_docker(workspace, comp182_dir, args.verbose, cli_args)

    if result["error"] and not result["stdout"]:
        print(f"Error: {result['error']}", file=sys.stderr)
        if result["stderr"]:
            print(result["stderr"], file=sys.stderr)
        sys.exit(1)

    # Output results (framework CLI outputs JSON when --json flag is used)
    print(result["stdout"])

    sys.exit(0 if result["success"] else 1)


if __name__ == "__main__":
    main()
