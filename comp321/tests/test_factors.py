"""
Public tests for Project 1: Factors

Basic smoke tests covering:
- A prime number (iterative + recursive)
- A two-factor composite (iterative + recursive)
- A multi-factor composite (iterative + recursive)
- A power of 2 (iterative)
- A larger prime (iterative)

These are intentionally minimal. More comprehensive tests run at submit time.
"""

import sys
from pathlib import Path

# Add tests dir to path for fixtures import
_tests_dir = Path(__file__).parent
if str(_tests_dir) not in sys.path:
    sys.path.insert(0, str(_tests_dir))

from fixtures import (
    make_test_result, run_program, compile_c_program,
    count_prime_factors, count_distinct_prime_factors
)

# Point values for each test
TEST_POINTS = {
    # Iterative
    "test_factors_2": 1,
    "test_factors_6": 1,
    "test_factors_10": 1,
    "test_factors_12": 1,
    "test_factors_17": 1,
    # Recursive
    "test_factors_2_r": 1,
    "test_factors_6_r": 1,
    "test_factors_10_r": 1,
}


def _run_factors_test(workspace_path: Path, n: int, recursive: bool = False):
    """Run factors program and check output."""
    executable = workspace_path / "factors"

    # Compile if needed
    if not executable.exists():
        success, error = compile_c_program(workspace_path)
        if not success:
            return make_test_result(False, "compiled", "compilation failed", error)

    # Run program
    args = ["-r"] if recursive else []
    stdin_input = f"{n}\n"

    ret_code, stdout, stderr = run_program(
        executable, args, stdin_input, timeout=60
    )

    if ret_code != 0 and "Segmentation fault" not in stderr:
        return make_test_result(
            False,
            f"exit code 0",
            f"exit code {ret_code}",
            stderr
        )

    # For recursive tests, segfault on large numbers is acceptable
    if recursive and "Segmentation fault" in stderr:
        if n > 1000000000:
            return make_test_result(True, "segfault (expected for large recursive)", "segfault", None)

    # Calculate expected values
    total_factors = count_prime_factors(n)
    distinct_factors = count_distinct_prime_factors(n)

    if recursive:
        expected_output = f"{n} has {total_factors} prime factors."
    else:
        expected_output = f"{n} has {total_factors} prime factors, {distinct_factors} of them distinct."

    if expected_output in stdout:
        return make_test_result(True, expected_output, stdout.strip(), None)
    else:
        return make_test_result(False, expected_output, stdout.strip(), None)


# ============================================================================
# Iterative Tests
# ============================================================================

def test_factors_2(workspace):
    """Test factorization of 2 (prime)."""
    return _run_factors_test(workspace, 2)

test_factors_2.input_description = "n=2"


def test_factors_6(workspace):
    """Test factorization of 6 = 2 * 3."""
    return _run_factors_test(workspace, 6)

test_factors_6.input_description = "n=6"


def test_factors_10(workspace):
    """Test factorization of 10 = 2 * 5."""
    return _run_factors_test(workspace, 10)

test_factors_10.input_description = "n=10"


def test_factors_12(workspace):
    """Test factorization of 12 = 2^2 * 3."""
    return _run_factors_test(workspace, 12)

test_factors_12.input_description = "n=12"


def test_factors_17(workspace):
    """Test factorization of 17 (prime)."""
    return _run_factors_test(workspace, 17)

test_factors_17.input_description = "n=17"


# ============================================================================
# Recursive Tests (with -r flag)
# ============================================================================

def test_factors_2_r(workspace):
    """Test recursive factorization of 2."""
    return _run_factors_test(workspace, 2, recursive=True)

test_factors_2_r.input_description = "n=2, -r flag"


def test_factors_6_r(workspace):
    """Test recursive factorization of 6."""
    return _run_factors_test(workspace, 6, recursive=True)

test_factors_6_r.input_description = "n=6, -r flag"


def test_factors_10_r(workspace):
    """Test recursive factorization of 10."""
    return _run_factors_test(workspace, 10, recursive=True)

test_factors_10_r.input_description = "n=10, -r flag"
