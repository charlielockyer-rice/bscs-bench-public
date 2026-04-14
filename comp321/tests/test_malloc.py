"""
Tests for Project 5: Dynamic Memory Allocator

Tests the following functionality:
- Basic malloc/free operations
- Coalescing of free blocks
- Memory utilization
- Throughput
- Realloc operations

Test cases based on CS:APP malloc lab trace files.
"""

import sys
from pathlib import Path

# Add tests dir to path for fixtures import
_tests_dir = Path(__file__).parent
if str(_tests_dir) not in sys.path:
    sys.path.insert(0, str(_tests_dir))

from fixtures import make_test_result, run_program, compile_c_program

# Point values for each test
# Note: Performance benchmarking is done via the malloc_perf tool, not these tests
TEST_POINTS = {
    "test_compile": 2,
    # Short traces (for quick testing)
    "test_short1": 3,
    "test_short2": 3,
    # Standard traces
    "test_amptjp": 5,
    "test_binary": 5,
    "test_binary2": 5,
    "test_cccp": 5,
    "test_coalescing": 5,
    "test_cp_decl": 5,
    "test_expr": 5,
    "test_random": 5,
    "test_random2": 5,
    # Realloc traces
    "test_realloc": 5,
    "test_realloc2": 5,
}


def _run_trace_test(workspace, trace_name):
    """Run mdriver with a specific trace file."""
    executable = workspace / "mdriver"

    # Compile if needed
    if not executable.exists():
        success, error = compile_c_program(workspace)
        if not success:
            return make_test_result(False, "compiled", "compilation failed", error)

    # Check if trace file exists
    trace_file = workspace / f"{trace_name}.rep"
    if not trace_file.exists():
        return make_test_result(False, "trace exists", f"trace file missing: {trace_name}.rep", None)

    # Run mdriver with trace file from workspace directory
    # Note: -f expects filename only (mdriver prepends "./" internally)
    ret_code, stdout, stderr = run_program(
        executable, ["-f", trace_file.name, "-v"], "", timeout=60, cwd=workspace
    )

    # Parse output for correctness
    output = stdout + stderr

    # Check for errors (use word boundary to avoid matching "incorrect")
    if "Segmentation fault" in output:
        return make_test_result(False, "no segfault", output[:300], output)
    if output.upper().startswith("ERROR") or "\nERROR" in output.upper():
        return make_test_result(False, "no errors", output[:300], output)

    # Check for successful completion - mdriver reports results with "Perf index"
    if ret_code == 0 and "Perf index" in output:
        # Extract performance index for display
        import re
        perf_match = re.search(r'Perf index = (\d+)/100', output)
        perf_score = perf_match.group(1) if perf_match else "?"
        return make_test_result(True, "passed", f"Perf index: {perf_score}/100", None)

    return make_test_result(False, "exit code 0", f"exit code {ret_code}\n{output[:200]}", output)


def test_compile(workspace):
    """Test that mdriver compiles successfully."""
    executable = workspace / "mdriver"

    if not executable.exists():
        success, error = compile_c_program(workspace)
        if not success:
            return make_test_result(False, "compiled", "compilation failed", error)

    return make_test_result(True, "compiled", "compiled", None)

test_compile.input_description = "compilation"


def test_short1(workspace):
    """Test with short1-bal.rep - simple allocation pattern."""
    return _run_trace_test(workspace, "short1-bal")

test_short1.input_description = "short1-bal.rep (simple pattern)"


def test_short2(workspace):
    """Test with short2-bal.rep - simple allocation pattern."""
    return _run_trace_test(workspace, "short2-bal")

test_short2.input_description = "short2-bal.rep (simple pattern)"


def test_amptjp(workspace):
    """Test with amptjp-bal.rep - from amptjp trace."""
    return _run_trace_test(workspace, "amptjp-bal")

test_amptjp.input_description = "amptjp-bal.rep"


def test_binary(workspace):
    """Test with binary-bal.rep - binary allocation pattern."""
    return _run_trace_test(workspace, "binary-bal")

test_binary.input_description = "binary-bal.rep (binary pattern)"


def test_binary2(workspace):
    """Test with binary2-bal.rep - binary allocation pattern variant."""
    return _run_trace_test(workspace, "binary2-bal")

test_binary2.input_description = "binary2-bal.rep (binary pattern 2)"


def test_cccp(workspace):
    """Test with cccp-bal.rep - from cccp trace."""
    return _run_trace_test(workspace, "cccp-bal")

test_cccp.input_description = "cccp-bal.rep"


def test_coalescing(workspace):
    """Test with coalescing-bal.rep - tests free block coalescing."""
    return _run_trace_test(workspace, "coalescing-bal")

test_coalescing.input_description = "coalescing-bal.rep (coalescing)"


def test_cp_decl(workspace):
    """Test with cp-decl-bal.rep - from cp-decl trace."""
    return _run_trace_test(workspace, "cp-decl-bal")

test_cp_decl.input_description = "cp-decl-bal.rep"


def test_expr(workspace):
    """Test with expr-bal.rep - from expr trace."""
    return _run_trace_test(workspace, "expr-bal")

test_expr.input_description = "expr-bal.rep"


def test_random(workspace):
    """Test with random-bal.rep - random allocation pattern."""
    return _run_trace_test(workspace, "random-bal")

test_random.input_description = "random-bal.rep (random)"


def test_random2(workspace):
    """Test with random2-bal.rep - random allocation pattern variant."""
    return _run_trace_test(workspace, "random2-bal")

test_random2.input_description = "random2-bal.rep (random 2)"


def test_realloc(workspace):
    """Test with realloc-bal.rep - tests realloc operations."""
    return _run_trace_test(workspace, "realloc-bal")

test_realloc.input_description = "realloc-bal.rep (realloc)"


def test_realloc2(workspace):
    """Test with realloc2-bal.rep - tests realloc operations variant."""
    return _run_trace_test(workspace, "realloc2-bal")

test_realloc2.input_description = "realloc2-bal.rep (realloc 2)"
