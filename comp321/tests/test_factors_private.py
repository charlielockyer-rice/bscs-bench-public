"""
Private tests for Project 1: Factors

Comprehensive coverage including:
- All small numbers not in public suite
- Medium numbers (3-4 digit composites and primes)
- Large numbers: primorials, Mersenne primes, 32-bit boundaries
- Full recursive coverage for small/medium numbers
- Large recursive numbers (stack overflow edge cases)
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
    # Small numbers (iterative) not in public suite
    "test_factors_3": 1,
    "test_factors_4": 1,
    "test_factors_5": 1,
    "test_factors_7": 1,
    "test_factors_8": 1,
    "test_factors_9": 1,
    "test_factors_11": 1,
    "test_factors_13": 1,
    "test_factors_14": 1,
    "test_factors_16": 1,
    # Medium numbers
    "test_factors_1068": 1,
    "test_factors_4261": 1,
    "test_factors_7919": 1,
    # Large numbers (2 points each)
    "test_factors_510510": 2,
    "test_factors_524287": 2,
    "test_factors_104469862": 2,
    "test_factors_223092870": 2,
    "test_factors_1234567890": 2,
    "test_factors_2147483647": 2,
    "test_factors_2147483648": 2,
    "test_factors_3234846615": 2,
    "test_factors_4294967291": 2,
    "test_factors_4294967295": 2,
    # Recursive tests not in public suite
    "test_factors_3_r": 1,
    "test_factors_4_r": 1,
    "test_factors_5_r": 1,
    "test_factors_7_r": 1,
    "test_factors_8_r": 1,
    "test_factors_9_r": 1,
    "test_factors_11_r": 1,
    "test_factors_12_r": 1,
    "test_factors_13_r": 1,
    "test_factors_14_r": 1,
    "test_factors_16_r": 1,
    "test_factors_17_r": 1,
    "test_factors_1068_r": 1,
    "test_factors_4261_r": 1,
    "test_factors_7919_r": 1,
    "test_factors_510510_r": 2,
    "test_factors_524287_r": 2,
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
# Iterative Tests (not in public suite)
# ============================================================================

def test_factors_3(workspace):
    """Test factorization of 3 (prime)."""
    return _run_factors_test(workspace, 3)

test_factors_3.input_description = "n=3"


def test_factors_4(workspace):
    """Test factorization of 4 = 2^2."""
    return _run_factors_test(workspace, 4)

test_factors_4.input_description = "n=4"


def test_factors_5(workspace):
    """Test factorization of 5 (prime)."""
    return _run_factors_test(workspace, 5)

test_factors_5.input_description = "n=5"


def test_factors_7(workspace):
    """Test factorization of 7 (prime)."""
    return _run_factors_test(workspace, 7)

test_factors_7.input_description = "n=7"


def test_factors_8(workspace):
    """Test factorization of 8 = 2^3."""
    return _run_factors_test(workspace, 8)

test_factors_8.input_description = "n=8"


def test_factors_9(workspace):
    """Test factorization of 9 = 3^2."""
    return _run_factors_test(workspace, 9)

test_factors_9.input_description = "n=9"


def test_factors_11(workspace):
    """Test factorization of 11 (prime)."""
    return _run_factors_test(workspace, 11)

test_factors_11.input_description = "n=11"


def test_factors_13(workspace):
    """Test factorization of 13 (prime)."""
    return _run_factors_test(workspace, 13)

test_factors_13.input_description = "n=13"


def test_factors_14(workspace):
    """Test factorization of 14 = 2 * 7."""
    return _run_factors_test(workspace, 14)

test_factors_14.input_description = "n=14"


def test_factors_16(workspace):
    """Test factorization of 16 = 2^4."""
    return _run_factors_test(workspace, 16)

test_factors_16.input_description = "n=16"


# ============================================================================
# Medium Numbers (iterative)
# ============================================================================

def test_factors_1068(workspace):
    """Test factorization of 1068 = 2^2 * 3 * 89."""
    return _run_factors_test(workspace, 1068)

test_factors_1068.input_description = "n=1068"


def test_factors_4261(workspace):
    """Test factorization of 4261 = 7 * 607."""
    return _run_factors_test(workspace, 4261)

test_factors_4261.input_description = "n=4261"


def test_factors_7919(workspace):
    """Test factorization of 7919 (prime)."""
    return _run_factors_test(workspace, 7919)

test_factors_7919.input_description = "n=7919"


# ============================================================================
# Large Numbers (iterative, 2 points each)
# ============================================================================

def test_factors_510510(workspace):
    """Test factorization of 510510 = 2 * 3 * 5 * 7 * 11 * 13 * 17 (primorial)."""
    return _run_factors_test(workspace, 510510)

test_factors_510510.input_description = "n=510510"


def test_factors_524287(workspace):
    """Test factorization of 524287 (Mersenne prime 2^19 - 1)."""
    return _run_factors_test(workspace, 524287)

test_factors_524287.input_description = "n=524287"


def test_factors_104469862(workspace):
    """Test factorization of 104469862."""
    return _run_factors_test(workspace, 104469862)

test_factors_104469862.input_description = "n=104469862"


def test_factors_223092870(workspace):
    """Test factorization of 223092870 = 2 * 3 * 5 * 7 * 11 * 13 * 17 * 19 * 23."""
    return _run_factors_test(workspace, 223092870)

test_factors_223092870.input_description = "n=223092870"


def test_factors_1234567890(workspace):
    """Test factorization of 1234567890."""
    return _run_factors_test(workspace, 1234567890)

test_factors_1234567890.input_description = "n=1234567890"


def test_factors_2147483647(workspace):
    """Test factorization of 2147483647 (max signed 32-bit, Mersenne prime 2^31-1)."""
    return _run_factors_test(workspace, 2147483647)

test_factors_2147483647.input_description = "n=2147483647"


def test_factors_2147483648(workspace):
    """Test factorization of 2147483648 = 2^31."""
    return _run_factors_test(workspace, 2147483648)

test_factors_2147483648.input_description = "n=2147483648"


def test_factors_3234846615(workspace):
    """Test factorization of 3234846615 = 3 * 5 * 7 * 11 * 13 * 17 * 19 * 23 * 29."""
    return _run_factors_test(workspace, 3234846615)

test_factors_3234846615.input_description = "n=3234846615"


def test_factors_4294967291(workspace):
    """Test factorization of 4294967291 (large prime near 2^32)."""
    return _run_factors_test(workspace, 4294967291)

test_factors_4294967291.input_description = "n=4294967291"


def test_factors_4294967295(workspace):
    """Test factorization of 4294967295 = 2^32 - 1 = 3 * 5 * 17 * 257 * 65537."""
    return _run_factors_test(workspace, 4294967295)

test_factors_4294967295.input_description = "n=4294967295"


# ============================================================================
# Recursive Tests (not in public suite)
# ============================================================================

def test_factors_3_r(workspace):
    """Test recursive factorization of 3."""
    return _run_factors_test(workspace, 3, recursive=True)

test_factors_3_r.input_description = "n=3, -r flag"


def test_factors_4_r(workspace):
    """Test recursive factorization of 4."""
    return _run_factors_test(workspace, 4, recursive=True)

test_factors_4_r.input_description = "n=4, -r flag"


def test_factors_5_r(workspace):
    """Test recursive factorization of 5."""
    return _run_factors_test(workspace, 5, recursive=True)

test_factors_5_r.input_description = "n=5, -r flag"


def test_factors_7_r(workspace):
    """Test recursive factorization of 7."""
    return _run_factors_test(workspace, 7, recursive=True)

test_factors_7_r.input_description = "n=7, -r flag"


def test_factors_8_r(workspace):
    """Test recursive factorization of 8."""
    return _run_factors_test(workspace, 8, recursive=True)

test_factors_8_r.input_description = "n=8, -r flag"


def test_factors_9_r(workspace):
    """Test recursive factorization of 9."""
    return _run_factors_test(workspace, 9, recursive=True)

test_factors_9_r.input_description = "n=9, -r flag"


def test_factors_11_r(workspace):
    """Test recursive factorization of 11."""
    return _run_factors_test(workspace, 11, recursive=True)

test_factors_11_r.input_description = "n=11, -r flag"


def test_factors_12_r(workspace):
    """Test recursive factorization of 12."""
    return _run_factors_test(workspace, 12, recursive=True)

test_factors_12_r.input_description = "n=12, -r flag"


def test_factors_13_r(workspace):
    """Test recursive factorization of 13."""
    return _run_factors_test(workspace, 13, recursive=True)

test_factors_13_r.input_description = "n=13, -r flag"


def test_factors_14_r(workspace):
    """Test recursive factorization of 14."""
    return _run_factors_test(workspace, 14, recursive=True)

test_factors_14_r.input_description = "n=14, -r flag"


def test_factors_16_r(workspace):
    """Test recursive factorization of 16."""
    return _run_factors_test(workspace, 16, recursive=True)

test_factors_16_r.input_description = "n=16, -r flag"


def test_factors_17_r(workspace):
    """Test recursive factorization of 17."""
    return _run_factors_test(workspace, 17, recursive=True)

test_factors_17_r.input_description = "n=17, -r flag"


def test_factors_1068_r(workspace):
    """Test recursive factorization of 1068."""
    return _run_factors_test(workspace, 1068, recursive=True)

test_factors_1068_r.input_description = "n=1068, -r flag"


def test_factors_4261_r(workspace):
    """Test recursive factorization of 4261."""
    return _run_factors_test(workspace, 4261, recursive=True)

test_factors_4261_r.input_description = "n=4261, -r flag"


def test_factors_7919_r(workspace):
    """Test recursive factorization of 7919."""
    return _run_factors_test(workspace, 7919, recursive=True)

test_factors_7919_r.input_description = "n=7919, -r flag"


def test_factors_510510_r(workspace):
    """Test recursive factorization of 510510."""
    return _run_factors_test(workspace, 510510, recursive=True)

test_factors_510510_r.input_description = "n=510510, -r flag"


def test_factors_524287_r(workspace):
    """Test recursive factorization of 524287."""
    return _run_factors_test(workspace, 524287, recursive=True)

test_factors_524287_r.input_description = "n=524287, -r flag"
