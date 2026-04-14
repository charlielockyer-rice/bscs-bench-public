"""
COMP 421 Lab 1 (MonTTY Terminal Driver) Test Definitions

Test definitions map test program names to expected output/exit-code patterns.
Lab 1 tests are regular executables (not Yalnix programs): they link against
montty.o + liblab1.a + the hardware emulator, and write output to stdout/stderr.

The check functions take (output, exit_code) instead of just (output) because
assert-based tests communicate pass/fail through their exit code.
"""

from typing import Tuple


# ---------------------------------------------------------------------------
# Check functions
# Each returns (passed: bool, error_msg: str)
# ---------------------------------------------------------------------------

def _check_exit_ok(output: str, exit_code: int) -> Tuple[bool, str]:
    """Smoke test: pass if exit code is 0 or 124 (timeout killed a sleep).

    Only use for tests that end with sleep() and expect to be killed by timeout.
    """
    if exit_code in (0, 124):
        return (True, "")
    return (False, f"Exit code {exit_code} (expected 0); output: {output[:200]}")


def _check_exit_zero(output: str, exit_code: int) -> Tuple[bool, str]:
    """Assert-based: pass only if exit code is 0.

    Exit 124 (timeout kill) means the test hung and should be a failure.
    """
    if exit_code == 0:
        return (True, "")
    return (False, f"Exit code {exit_code} (expected 0); output: {output[:200]}")


def _check_test2(output: str, exit_code: int) -> Tuple[bool, str]:
    """test2: Single writer writes 26 chars, expects 'Done: status = 26'."""
    if exit_code not in (0, 124):
        return (False, f"Exit code {exit_code}")
    if "Done: status = 26" not in output:
        return (False, "Missing 'Done: status = 26'")
    return (True, "")


def _check_test3(output: str, exit_code: int) -> Tuple[bool, str]:
    """test3: Writer loop with 50 numbered entries, no errors expected.

    The numbered output goes through WriteTerminal to the X11 terminal,
    not stdout.  We can only check that no error was reported on stderr
    and the process exited cleanly.
    """
    if exit_code not in (0, 124):
        return (False, f"Exit code {exit_code}")
    if "Error:" in output:
        return (False, "Writer error found in output")
    return (True, "")


def _check_test4(output: str, exit_code: int) -> Tuple[bool, str]:
    """test4: Two concurrent writers, no errors expected from either."""
    if exit_code not in (0, 124):
        return (False, f"Exit code {exit_code}")
    if "Error:" in output:
        return (False, "Writer error found in output")
    return (True, "")


def _check_test8(output: str, exit_code: int) -> Tuple[bool, str]:
    """test8: Two concurrent long writes with statistics output.

    string1 is 692 bytes, string2 is 754 bytes (no newlines in either).
    Expected: user_in=1446, tty_out=1446 (no \r\n expansion needed).
    """
    if exit_code not in (0, 124):
        return (False, f"Exit code {exit_code}")
    done_count = output.count("Done: status =")
    if done_count < 2:
        return (False, f"Expected 2 'Done: status =' lines, found {done_count}")
    if "TERMINAL" not in output or "tty_in" not in output:
        return (False, "Missing statistics output")
    # Verify stats: user_in should be 692+754=1446, tty_out should equal user_in
    import re
    user_in_matches = re.findall(r"User characters written \(user_in\): (\d+)", output)
    tty_out_matches = re.findall(r"Characters out \(tty_out\): (\d+)", output)
    # Terminal 1 is the one used (index 1 in the stats array)
    if len(user_in_matches) >= 2:
        user_in_val = int(user_in_matches[1])  # terminal 1 stats
        if user_in_val != 1446:
            return (False, f"user_in for terminal 1 = {user_in_val}, expected 1446")
    if len(tty_out_matches) >= 2:
        tty_out_val = int(tty_out_matches[1])
        if tty_out_val != 1446:
            return (False, f"tty_out for terminal 1 = {tty_out_val}, expected 1446")
    return (True, "")


# ---------------------------------------------------------------------------
# Test definitions
# ---------------------------------------------------------------------------

TEST_DEFINITIONS = {
    "test_smoke": {
        "program": "test1",
        "points": 5,
        "check": _check_exit_ok,
        "description": "Init driver and terminal, verify clean startup",
    },
    "test_write_simple": {
        "program": "test2",
        "points": 10,
        "check": _check_test2,
        "description": "Single writer: write 26 chars, verify status",
    },
    "test_write_loop": {
        "program": "test3",
        "points": 10,
        "check": _check_test3,
        "timeout": 30,
        "description": "Single writer: write 50 numbered entries in loop",
    },
    "test_concurrent_write": {
        "program": "test4",
        "points": 10,
        "check": _check_test4,
        "description": "Two concurrent writers on same terminal",
    },
    "test_concurrent_long_write": {
        "program": "test8",
        "points": 10,
        "check": _check_test8,
        "timeout": 60,
        "description": "Two concurrent long writes with statistics",
    },
    "test_stats": {
        "program": "stats",
        "points": 10,
        "check": _check_exit_zero,
        "description": "TerminalDriverStatistics correctness (assert-based)",
    },
    "test_invalid_write": {
        "program": "invalid-write-terminal",
        "points": 10,
        "check": _check_exit_zero,
        "description": "WriteTerminal error handling: bad args (assert-based)",
    },
    "test_use_before_init": {
        "program": "use-before-init",
        "points": 5,
        "check": _check_exit_zero,
        "description": "API calls before init return ERROR (assert-based)",
    },
}
