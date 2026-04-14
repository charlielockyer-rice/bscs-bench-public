"""Command-line interface for flat workspace grading."""

import sys
import argparse
from pathlib import Path

from .config import load_workspace_config
from .runner import TestRunner
from .tracker import AttemptTracker


def create_parser() -> argparse.ArgumentParser:
    """Create the argument parser."""
    parser = argparse.ArgumentParser(
        prog="grade",
        description="Agent Evaluation Framework - Autograder",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  grade ./workspaces/agent_001_circles    Grade a single-task workspace
  grade ./workspaces/agent_001_circles -v Grade with verbose output
  grade ./workspaces/agent_001_circles --history  Show grading history
""",
    )

    parser.add_argument(
        "workspace",
        type=str,
        help="Path to the workspace directory",
    )

    parser.add_argument(
        "-v", "--verbose",
        action="store_true",
        help="Show detailed test output",
    )

    parser.add_argument(
        "--history",
        action="store_true",
        help="Show attempt history instead of running tests",
    )

    parser.add_argument(
        "--json",
        action="store_true",
        help="Output results as JSON",
    )

    parser.add_argument(
        "--check",
        action="store_true",
        help="Only check if submission is valid (don't run tests)",
    )

    return parser


def format_output(grade_result, verbose: bool = False, as_json: bool = False) -> str:
    """Format the grading output."""
    if as_json:
        import json
        return json.dumps(grade_result.to_dict(), indent=2)

    if verbose:
        return grade_result.format_full_report()
    else:
        return grade_result.format_summary()


def main(args=None) -> int:
    """
    Main entry point for the CLI.

    Args:
        args: Command line arguments (uses sys.argv if None)

    Returns:
        Exit code (0 for success, non-zero for errors)
    """
    parser = create_parser()
    parsed = parser.parse_args(args)

    workspace_path = Path(parsed.workspace).resolve()

    if not workspace_path.exists():
        print(f"Error: Workspace not found: {workspace_path}", file=sys.stderr)
        return 1

    has_yaml = (workspace_path / "workspace.yaml").exists()
    has_json = (workspace_path / "workspace.json").exists()
    if not has_yaml and not has_json:
        print(f"Error: Not a valid workspace (missing workspace.yaml or workspace.json): {workspace_path}", file=sys.stderr)
        return 1

    try:
        load_workspace_config(workspace_path)
    except FileNotFoundError as e:
        print(f"Error: {e}", file=sys.stderr)
        return 1

    if parsed.history:
        tracker = AttemptTracker.from_workspace(workspace_path)
        print(tracker.get_summary())
        return 0

    runner = TestRunner.from_workspace(workspace_path)

    # Handle check-only mode
    if parsed.check:
        is_valid, message = runner.check_submission()
        print(message)
        return 0 if is_valid else 1

    # Run the grading
    try:
        result = runner.run_tests()
    except FileNotFoundError as e:
        print(f"Error: {e}", file=sys.stderr)
        return 1
    except RuntimeError as e:
        print(f"Error: {e}", file=sys.stderr)
        return 1
    except Exception as e:
        print(f"Unexpected error: {e}", file=sys.stderr)
        import traceback
        traceback.print_exc()
        return 1

    # Output results
    output = format_output(result, parsed.verbose, parsed.json)
    print(output)

    # Return 0 if all tests passed, 1 otherwise
    return 0 if result.all_passed else 1


if __name__ == "__main__":
    sys.exit(main())
