"""
COMP 421 Lab 2 (Yalnix Kernel) Test Definitions

Test definitions map test program names to expected output patterns.
The runner executes each test under the Yalnix simulator and checks output.

Unlike Lab 3 (where TTYLOG.0 is the FS server and TTYLOG.1 is the test
program), Lab 2 output comes from a combination of:
  - Yalnix stdout: printf/fprintf output from test programs
  - TRACE file: TracePrintf output from kernel and user (enabled with -lk 0 -lu 0)
  - TTYLOG files: TtyWrite/TtyPrintf output (often empty)

The runner combines all three sources before passing to check functions.
Each check function takes (output,) and returns (bool, str).

25 tests, 100 points total.
"""

from typing import Tuple

# Programs that are compiled but not scored — they're targets for Exec tests.
EXTRA_PROGRAMS = ["exec_target", "list_args"]


# ---------------------------------------------------------------------------
# Boot & Init (3 tests, 10 points)
# ---------------------------------------------------------------------------

def check_boot(output: str) -> Tuple[bool, str]:
    """init_exit: Prints 'Entering init_exit' and 'Exiting' via printf."""
    if "Entering init_exit" not in output:
        return (False, "Missing 'Entering init_exit'")
    if "Exiting" not in output:
        return (False, "Missing 'Exiting'")
    return (True, "")


def check_getpid(output: str) -> Tuple[bool, str]:
    """init_getpid: Prints 'init: pid:' via printf."""
    if "init: pid:" not in output:
        return (False, "Missing 'init: pid:' — GetPid may not work")
    return (True, "")


def check_print(output: str) -> Tuple[bool, str]:
    """init_print: Prints 'hello world' via printf."""
    if "hello world" not in output:
        return (False, "Missing 'hello world'")
    return (True, "")


# ---------------------------------------------------------------------------
# Fork (4 tests, 17 points)
# ---------------------------------------------------------------------------

def check_fork_basic(output: str) -> Tuple[bool, str]:
    """fork_basic: TracePrintf prints 'I'm the parent' and 'I'm the child'."""
    if "I'm the parent" not in output and "parent" not in output.lower():
        return (False, "Missing parent message")
    if "I'm the child" not in output and "child" not in output.lower():
        return (False, "Missing child message")
    return (True, "")


def check_fork_print(output: str) -> Tuple[bool, str]:
    """fork_print: Prints 'FORK2>' lines and 'FORK2 done.' via printf."""
    if "FORK2>" not in output:
        return (False, "Missing 'FORK2>' output lines")
    if "FORK2 done." not in output:
        return (False, "Missing 'FORK2 done.' — fork/recursion may have failed")
    return (True, "")


def check_fork_recurse(output: str) -> Tuple[bool, str]:
    """fork_recurse: Prints 'CHILD' and 'PARENT' messages, uses Delay."""
    if "CHILD" not in output:
        return (False, "Missing 'CHILD' message")
    if "PARENT" not in output:
        return (False, "Missing 'PARENT' message")
    return (True, "")


def check_fork_multi(output: str) -> Tuple[bool, str]:
    """fork_multi: TracePrintf prints 'done, forked' for each process."""
    if "done, forked" not in output:
        return (False, "Missing 'done, forked' — fork loop may have failed")
    return (True, "")


# ---------------------------------------------------------------------------
# Wait & Exit (5 tests, 23 points)
# ---------------------------------------------------------------------------

def check_wait_status(output: str) -> Tuple[bool, str]:
    """wait_status: TracePrintf prints 'exited with status: 15'."""
    if "exited with status: 15" not in output:
        return (False, "Missing 'exited with status: 15' — Wait/Exit status may be wrong")
    return (True, "")


def check_wait_zombie(output: str) -> Tuple[bool, str]:
    """wait_zombie: Prints 'FORKWAIT> GOOD!' via printf."""
    if "FORKWAIT> GOOD!" not in output:
        return (False, "Missing 'FORKWAIT> GOOD!' — zombie collection failed")
    return (True, "")


def check_wait_before_exit(output: str) -> Tuple[bool, str]:
    """wait_before_exit: Parent waits, child delays then exits with 123."""
    if "wait returned" not in output:
        return (False, "Missing 'wait returned' — Wait may not have returned")
    if "123" not in output:
        return (False, "Missing status 123 in wait output")
    return (True, "")


def check_wait_after_exit(output: str) -> Tuple[bool, str]:
    """wait_after_exit: Child exits immediately with 123, parent delays then waits."""
    if "wait returned" not in output:
        return (False, "Missing 'wait returned' — Wait may not have returned")
    if "123" not in output:
        return (False, "Missing status 123 in wait output")
    return (True, "")


def check_wait_loop(output: str) -> Tuple[bool, str]:
    """wait_loop: 10 iterations of fork-wait, prints 'FORKWAIT' and 'GOOD!' each time."""
    good_count = output.count("GOOD!")
    if good_count < 5:
        return (False, f"Expected multiple 'GOOD!' entries, found {good_count}")
    return (True, "")


# ---------------------------------------------------------------------------
# Exec (2 tests, 10 points)
# ---------------------------------------------------------------------------

def check_exec(output: str) -> Tuple[bool, str]:
    """exec_wrapper: Calls Exec('exec_target',...) which prints 'argv[0]:' via TracePrintf."""
    if "argv[" not in output and "Calling exec" not in output:
        return (False, "Missing exec target output — Exec may have failed")
    if "Calling exec" in output and "argv[" not in output:
        return (False, "Exec was called but target didn't execute — Exec failed")
    return (True, "")


def check_fork_exec(output: str) -> Tuple[bool, str]:
    """fork_exec: Forks then both parent and child Exec('list_args',...).
    list_args prints 'arguments passed in' via both printf and TracePrintf."""
    if "arguments passed in" not in output:
        if "Exec failed" in output or "Fork failed" in output:
            return (False, "Exec or Fork reported failure")
        return (False, "Missing 'arguments passed in' — Exec to list_args may have failed")
    return (True, "")


# ---------------------------------------------------------------------------
# Memory (5 tests, 20 points)
# ---------------------------------------------------------------------------

def check_brk(output: str) -> Tuple[bool, str]:
    """brktest: Does sbrk, Brk, malloc — should NOT print 'returned error' or 'failed'."""
    if "returned error" in output:
        return (False, "Brk returned error")
    if "malloc failed" in output:
        return (False, "malloc failed after Brk")
    return (True, "")


def check_bigstack(output: str) -> Tuple[bool, str]:
    """bigstack: Prints 'foo = 42' via printf."""
    if "foo = 42" not in output:
        return (False, "Missing 'foo = 42' — stack growth may have failed")
    return (True, "")


def check_malloc_exhaust(output: str) -> Tuple[bool, str]:
    """malloc_exhaust: Malloc loop until NULL, then prints 'Couldn't malloc' via printf."""
    if "Couldn't malloc" not in output:
        return (False, "Missing 'Couldn't malloc' — memory exhaustion not detected")
    return (True, "")


def check_stack_growth(output: str) -> Tuple[bool, str]:
    """stack_growth: Recursive counter via TracePrintf. Should see numbers counting up.
    Will eventually crash when stack can't grow anymore — that's expected."""
    found_numbers = False
    for line in output.split("\n"):
        stripped = line.strip()
        try:
            n = int(stripped)
            if n >= 0:
                found_numbers = True
                break
        except ValueError:
            continue
    if not found_numbers:
        return (False, "No counting output found — recursive stack growth may not work")
    return (True, "")


def check_brk_delay(output: str) -> Tuple[bool, str]:
    """brk_delay: TracePrintf prints 'Pid:' and 'str:' lines."""
    if "Pid:" not in output:
        return (False, "Missing 'Pid:' — Brk/Delay may have failed")
    if "str:" not in output:
        return (False, "Missing 'str:' — malloc/string building may have failed")
    return (True, "")


# ---------------------------------------------------------------------------
# Traps (3 tests, 9 points)
# ---------------------------------------------------------------------------

def _check_no_crash(output: str) -> Tuple[bool, str]:
    """Generic checker — pass if no Yalnix-level crash."""
    crash_patterns = ["Yalnix ABORT", "KERNEL PANIC", "yalnix: internal error"]
    for pattern in crash_patterns:
        if pattern in output:
            return (False, f"Yalnix crashed: {pattern}")
    return (True, "")


def check_trap_math(output: str) -> Tuple[bool, str]:
    """trapmath: Division by zero — kernel should kill the faulting process."""
    return _check_no_crash(output)


def check_trap_memory(output: str) -> Tuple[bool, str]:
    """trapmemory: Invalid memory access — kernel should kill the faulting process."""
    return _check_no_crash(output)


def check_trap_illegal(output: str) -> Tuple[bool, str]:
    """trapillegal: Illegal instruction — kernel should kill the faulting process."""
    return _check_no_crash(output)


# ---------------------------------------------------------------------------
# TTY Write (3 tests, 11 points)
# ---------------------------------------------------------------------------

def check_ttywrite(output: str) -> Tuple[bool, str]:
    """ttywrite: Single TtyWrite + Exit."""
    passed, msg = _check_no_crash(output)
    if not passed:
        return (passed, msg)
    if "Hello world" not in output:
        return (False, "Expected 'Hello world' in TtyWrite output")
    return (True, "")


def check_ttywrite_loop(output: str) -> Tuple[bool, str]:
    """ttywrite_loop: TtyWrite x10 loop."""
    passed, msg = _check_no_crash(output)
    if not passed:
        return (passed, msg)
    count = output.count("Hello world")
    if count < 5:
        return (False, f"Expected multiple 'Hello world' lines, found {count}")
    return (True, "")


def check_ttywrite_concurrent(output: str) -> Tuple[bool, str]:
    """ttywrite_concurrent: Fork + concurrent TtyWrite."""
    passed, msg = _check_no_crash(output)
    if not passed:
        return (passed, msg)
    has_parent = "Parent line" in output
    has_child = "Child line" in output
    if not has_parent and not has_child:
        return (False, "Expected 'Parent line' or 'Child line' in TtyWrite output")
    return (True, "")


# ---------------------------------------------------------------------------
# Test definitions (25 tests, 100 points)
# ---------------------------------------------------------------------------

TEST_DEFINITIONS = {
    "test_fork_recurse": {
        "program": "fork_recurse",
        "points": 4,
        "check": check_fork_recurse,
        "needs_timeout": True,
        "timeout": 45,
        "description": "Fork + recursion + Delay",
    },
    "test_wait_zombie": {
        "program": "wait_zombie",
        "points": 5,
        "check": check_wait_zombie,
        "needs_timeout": True,
        "timeout": 45,
        "description": "Zombie collection, status 1234567",
    },
    "test_wait_after_exit": {
        "program": "wait_after_exit",
        "points": 4,
        "check": check_wait_after_exit,
        "needs_timeout": True,
        "timeout": 45,
        "description": "Child Exit before parent Wait",
    },
    "test_wait_loop": {
        "program": "wait_loop",
        "points": 4,
        "check": check_wait_loop,
        "needs_timeout": False,
        "description": "10x synchronous fork-wait-exit",
    },
    "test_malloc_exhaust": {
        "program": "malloc_exhaust",
        "points": 3,
        "check": check_malloc_exhaust,
        "needs_timeout": True,
        "timeout": 45,
        "description": "Malloc until NULL, Exit",
    },
    "test_brk_delay": {
        "program": "brk_delay",
        "points": 3,
        "check": check_brk_delay,
        "needs_timeout": True,
        "timeout": 60,
        "description": "Brk + malloc + Delay loop",
    },
    "test_trap_illegal": {
        "program": "trapillegal",
        "points": 3,
        "check": check_trap_illegal,
        "needs_timeout": False,
        "description": "Illegal instruction — kill process",
    },
    "test_ttywrite_loop": {
        "program": "ttywrite_loop",
        "points": 4,
        "check": check_ttywrite_loop,
        "needs_timeout": False,
        "description": "TtyWrite x10 loop",
    },
    "test_ttywrite_concurrent": {
        "program": "ttywrite_concurrent",
        "points": 4,
        "check": check_ttywrite_concurrent,
        "needs_timeout": False,
        "description": "Fork + concurrent TtyWrite",
    },
}
