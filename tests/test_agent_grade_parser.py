"""Regression tests for the agent-side grade output parser."""

import json
import subprocess
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parent.parent


def test_parse_grade_output_uses_final_detailed_results_section():
    transcript = """============================================================
COMP 421 Lab 1: Reader/Writer - Autograder Results
============================================================
Attempt: 1 of 10

  [PASS] test_simple_write
  [FAIL] test_concurrent_long_write
  [PASS] test_basic_read

RESULTS SUMMARY
---------------
Passed: 2/3 tests (66.7%)
Points: 20/30 (66.7%)

DETAILED RESULTS
----------------
[PASS] test_simple_write (10/10 pts)
[FAIL] test_concurrent_long_write (0/10 pts)
       Input: threads=2
       Expected: completed=2
       Actual: completed=1
[PASS] test_basic_read (10/10 pts)
============================================================
"""

    script = """
import { parseGradeOutput } from "./agent/gradeParser";

const output = process.env.GRADE_OUTPUT;
const parsed = parseGradeOutput(output);
console.log(JSON.stringify(parsed));
"""

    env = {**__import__("os").environ, "GRADE_OUTPUT": transcript}
    result = subprocess.run(
        ["bun", "--eval", script],
        cwd=PROJECT_ROOT,
        capture_output=True,
        text=True,
        check=True,
        env=env,
    )

    parsed = json.loads(result.stdout)
    assert parsed["passed"] == 2
    assert parsed["total"] == 3
    assert len(parsed["tests"]) == 3
    assert [test["name"] for test in parsed["tests"]] == [
        "test_simple_write",
        "test_concurrent_long_write",
        "test_basic_read",
    ]

