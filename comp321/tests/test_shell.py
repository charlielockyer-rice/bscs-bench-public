"""
Tests for Project 4: Unix Shell (tsh)

Tests the following functionality:
- Built-in commands (quit, jobs, bg, fg)
- Foreground and background job execution
- Signal handling (SIGINT, SIGTSTP, SIGCHLD)
- Job control
- PATH searching

Test cases derived from grading feedback (trace files 01-24).
"""

import os
import re
import sys
import signal
import subprocess
from pathlib import Path
import tempfile

# Add tests dir to path for fixtures import
_tests_dir = Path(__file__).parent
if str(_tests_dir) not in sys.path:
    sys.path.insert(0, str(_tests_dir))

from fixtures import (
    make_test_result, run_program, compile_c_program, normalize_output,
    USE_DOCKER, DOCKER_IMAGE
)

# Point values for each test
TEST_POINTS = {
    # Basic tests
    "test_trace01": 2,  # Properly terminate on EOF
    "test_trace02": 2,  # Process built-in quit command
    "test_trace03": 2,  # Run a foreground job
    "test_trace04": 2,  # Run a background job
    "test_trace05": 2,  # Process jobs built-in command
    # Signal tests
    "test_trace06": 3,  # Forward SIGINT to foreground job
    "test_trace07": 3,  # Forward SIGINT only to foreground job
    "test_trace08": 3,  # Forward SIGTSTP only to foreground job
    "test_trace09": 3,  # Process bg built-in command
    "test_trace10": 3,  # Process fg built-in command
    # Job control tests
    "test_trace11": 3,  # Forward SIGINT to every process in fg job
    "test_trace12": 3,  # Forward SIGTSTP to every process in fg job
    "test_trace13": 3,  # Restart every stopped process in process group
    "test_trace14": 3,  # Simple error handling
    "test_trace15": 3,  # Putting it all together
    "test_trace16": 3,  # More complex job control
    # Additional job control tests
    "test_trace17": 3,  # fg command (one job)
    "test_trace18": 3,  # fg command (two jobs)
    "test_trace19": 3,  # SIGINT to process group
    # PATH handling tests
    "test_trace20": 3,  # NULL PATH - search current directory
    "test_trace21": 3,  # PATH starts with : - search current dir first
    "test_trace22": 3,  # PATH contains :: - search current dir
    "test_trace23": 3,  # PATH ends with : - search current dir
    "test_trace24": 3,  # Comprehensive job control
}


def _normalize_shell_output(output: str) -> str:
    """
    Normalize shell output for comparison.
    - Replace PIDs with placeholder
    - Normalize whitespace
    """
    # Remove blank lines at start/end
    output = output.strip()

    # Replace PIDs in job output like "[1] (12345)"
    output = re.sub(r'\(\d+\)', '(PID)', output)

    # Replace PIDs in signal messages
    output = re.sub(r'Job \[\d+\] \(\d+\)', 'Job [JID] (PID)', output)

    # Normalize multiple spaces to single
    output = re.sub(r' +', ' ', output)

    return output


def _run_trace_test(workspace: Path, trace_num: int) -> 'TestResult':
    """
    Run a shell trace test.

    Args:
        workspace: Path to workspace containing tsh
        trace_num: Trace file number (1-24)

    Returns:
        TestResult
    """
    tsh = workspace / "tsh"
    sdriver = workspace / "sdriver.pl"

    # All traces are in the test data directory (grader's copies)
    trace_file = _tests_dir / "data" / "shell" / f"trace{trace_num:02d}.txt"

    # Compile if needed
    if not tsh.exists():
        success, error = compile_c_program(workspace)
        if not success:
            return make_test_result(False, "compiled", f"compilation failed: {error}", None)

    # Check for required files
    if not trace_file.exists():
        return make_test_result(False, str(trace_file), "trace file not found", None)

    if not sdriver.exists():
        return make_test_result(False, str(sdriver), "sdriver.pl not found", None)

    # Set up environment for PATH tests
    path_env = {}
    if trace_num == 20:
        # NULL PATH - should search current directory
        path_env["PATH"] = ""
    elif trace_num == 21:
        # PATH starts with : - should search current directory first
        path_env["PATH"] = ":/usr/bin"
    elif trace_num == 22:
        # PATH contains :: - should search current directory
        path_env["PATH"] = "/usr/bin::/bin"
    elif trace_num == 23:
        # PATH ends with : - should search current directory
        path_env["PATH"] = "/usr/bin:"
    elif trace_num == 24:
        # Normal PATH with current directory
        path_env["PATH"] = ".:/usr/bin:/bin"

    # Run student shell with trace (shorter timeout for potentially hanging tests)
    test_timeout = 10 if trace_num >= 17 else 30

    if USE_DOCKER:
        # Run sdriver.pl inside Docker where the ELF binaries can execute
        from framework.docker_utils import run_bash_in_container

        trace_data_dir = str((_tests_dir / "data" / "shell").resolve())
        base_cmd = f"/usr/bin/perl ./sdriver.pl -t /traces/trace{trace_num:02d}.txt -s ./tsh -a -p"

        if path_env:
            # Embed PATH modification into the script rather than passing as
            # container-level env.  Docker's runc needs a valid PATH to start
            # the container (resolve 'bash', etc.), but the *test process* and
            # its child tsh must see the modified PATH.  We export PATH inside
            # the bash script and use the absolute path to perl so that even an
            # empty PATH doesn't prevent perl from running.
            path_value = path_env["PATH"]
            script = f'export PATH="{path_value}"; {base_cmd}'
        else:
            script = base_cmd

        try:
            returncode, stdout, stderr = run_bash_in_container(
                image=DOCKER_IMAGE,
                script=script,
                workspace=workspace,
                timeout=test_timeout,
                memory="4g",
                cpus="2",
                network="none",
                extra_volumes={trace_data_dir: "/traces:ro"},
            )
            student_output = (stdout or "") + (stderr or "")
        except Exception as e:
            if "timed out" in str(e).lower():
                return make_test_result(False, "completed in time", "timed out (likely hung)", None)
            return make_test_result(False, "ran", str(e), None)

        if returncode == -1 and "timed out" in student_output.lower():
            return make_test_result(False, "completed in time", "timed out (likely hung)", None)
    else:
        # Host execution
        env = os.environ.copy()
        env.update(path_env)

        # Use absolute path to perl for PATH tests (since PATH may be modified)
        perl_path = "/usr/bin/perl"

        try:
            student_result = subprocess.run(
                [perl_path, str(sdriver), "-t", str(trace_file), "-s", str(tsh), "-a", "-p"],
                capture_output=True,
                text=True,
                timeout=test_timeout,
                cwd=str(workspace),
                env=env
            )
            student_output = student_result.stdout + student_result.stderr
            returncode = student_result.returncode
        except subprocess.TimeoutExpired:
            return make_test_result(False, "completed in time", "timed out (likely hung)", None)
        except Exception as e:
            return make_test_result(False, "ran", str(e), None)

    # Check for crashes
    if returncode < 0:
        signal_num = -returncode
        return make_test_result(False, "no crash", f"crashed with signal {signal_num}", None)

    # Check if sdriver reported errors
    if "ERROR" in student_output or "timed out" in student_output.lower():
        return make_test_result(False, "no errors", student_output[:200], None)

    # Normalize output for comparison
    student_norm = _normalize_shell_output(student_output)

    # Define expected output patterns for each trace
    # These are the key behaviors that should be present
    expected = {
        1: [],  # EOF - just exit cleanly
        2: [],  # quit command
        3: ["tsh>"],  # foreground job runs
        4: [r"\[\d+\].*&"],  # background job shows [N] (PID) ... &
        5: ["Running", "jobs"],  # jobs command shows running jobs
        6: ["terminated by signal"],  # SIGINT terminates fg job
        7: ["terminated by signal"],  # SIGINT only to fg
        8: ["stopped by signal"],  # SIGTSTP stops fg job
        9: ["bg"],  # bg command
        10: ["fg"],  # fg command
        11: ["terminated by signal"],  # SIGINT to process group
        12: ["stopped by signal"],  # SIGTSTP to process group
        13: [],  # restart stopped group
        14: ["No such file", "command not found"],  # error handling (either is ok)
        15: [],  # comprehensive
        16: [],  # complex job control
        17: ["stopped", "fg"],  # fg with one stopped job
        18: ["stopped", "fg"],  # fg with two jobs
        19: ["terminated", "stopped"],  # comprehensive job control
        20: ["tsh>"],  # NULL PATH - must NOT have "not found"
        21: ["tsh>"],  # PATH starts with : - must NOT have "not found"
        22: ["tsh>"],  # PATH contains :: - must NOT have "not found"
        23: ["tsh>"],  # PATH ends with : - must NOT have "not found"
        24: ["tsh>"],  # Normal PATH test - must NOT have "not found"
    }

    patterns = expected.get(trace_num, [])

    # For traces with specific patterns, check them
    if patterns:
        found_any = False
        for pattern in patterns:
            if re.search(pattern, student_output, re.IGNORECASE):
                found_any = True
                break
        if not found_any and patterns:
            return make_test_result(
                False,
                f"one of: {patterns}",
                student_norm[:200],
                None
            )

    # PATH handling tests (20-23) should NOT have "Command not found"
    if trace_num in [20, 21, 22, 23]:
        if "not found" in student_output.lower() or "command not found" in student_output.lower():
            return make_test_result(
                False,
                "myspin found in PATH",
                "myspin: Command not found",
                "Shell should search current directory for PATH edge cases"
            )

    return make_test_result(True, "passed", student_norm[:100] if student_norm else "ok", None)


# ============================================================================
# Basic Tests (trace01-05)
# ============================================================================

def test_trace01(workspace):
    """Test proper termination on EOF."""
    return _run_trace_test(workspace, 1)

test_trace01.input_description = "trace01.txt - EOF handling"


def test_trace02(workspace):
    """Test built-in quit command."""
    return _run_trace_test(workspace, 2)

test_trace02.input_description = "trace02.txt - quit command"


def test_trace03(workspace):
    """Test running a foreground job."""
    return _run_trace_test(workspace, 3)

test_trace03.input_description = "trace03.txt - foreground job"


def test_trace04(workspace):
    """Test running a background job."""
    return _run_trace_test(workspace, 4)

test_trace04.input_description = "trace04.txt - background job"


def test_trace05(workspace):
    """Test jobs built-in command."""
    return _run_trace_test(workspace, 5)

test_trace05.input_description = "trace05.txt - jobs command"


# ============================================================================
# Signal Tests (trace06-10)
# ============================================================================

def test_trace06(workspace):
    """Test forwarding SIGINT to foreground job."""
    return _run_trace_test(workspace, 6)

test_trace06.input_description = "trace06.txt - SIGINT to foreground"


