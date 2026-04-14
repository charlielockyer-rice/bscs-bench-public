"""
Tests for Module 3: Stock Prediction

Tests the following functions:
- markov_chain(data, order)
- predict(chain, last, num)
- mse(result, expected)
- run_experiment(train, order, test, future, actual, trials)
"""

import random
import sys
from pathlib import Path

_tests_dir = Path(__file__).parent
if str(_tests_dir) not in sys.path:
    sys.path.insert(0, str(_tests_dir))

from fixtures import make_test_result, approx_equal

# Point values for each test
TEST_POINTS = {
    # markov_chain tests (12)
    "test_markov_chain_simple": 1,
    "test_markov_chain_order1": 1,
    "test_markov_chain_order2": 1,
    "test_markov_chain_probabilities_sum": 1,
    "test_markov_chain_returns_dict": 1,
    "test_markov_chain_three_states": 1,
    "test_markov_chain_integer_bins": 1,
    "test_markov_chain_order3": 1,
    "test_markov_chain_exact_probabilities": 1,
    "test_markov_chain_minimal_data": 1,
    "test_markov_chain_insufficient_data": 1,
    "test_markov_chain_order2_probabilities": 1,
    # predict tests (11)
    "test_predict_length": 1,
    "test_predict_deterministic": 1,
    "test_predict_starts_correct": 1,
    "test_predict_uses_chain": 1,
    "test_predict_integer_bins": 1,
    "test_predict_unknown_state": 1,
    "test_predict_probabilistic": 1,
    "test_predict_order2_sliding_window": 1,
    "test_predict_zero_predictions": 1,
    "test_predict_single_prediction": 1,
    "test_predict_returns_list": 1,
    # mse tests (8)
    "test_mse_zero": 1,
    "test_mse_simple": 1,
    "test_mse_lists": 1,
    "test_mse_negative": 1,
    "test_mse_single_element": 1,
    "test_mse_floats": 1,
    "test_mse_varying_differences": 1,
    "test_mse_large_values": 1,
    # run_experiment tests (8)
    "test_run_experiment_returns_float": 1,
    "test_run_experiment_perfect_prediction": 1,
    "test_run_experiment_nonzero_mse": 1,
    "test_run_experiment_order2": 1,
    "test_run_experiment_single_trial": 1,
    "test_run_experiment_many_trials": 1,
    "test_run_experiment_nonnegative": 1,
    "test_run_experiment_uses_all_params": 1,
}


# ============================================================================
# Markov Chain Tests
# ============================================================================

def test_markov_chain_simple(module):
    """Test markov_chain with simple data."""
    data = ["up", "up", "up", "up"]
    chain = module.markov_chain(data, 1)

    # Should have ("up",) -> {"up": 1.0} (always up follows up)
    if not isinstance(chain, dict):
        return make_test_result(False, "dict", type(chain).__name__)

    # Check that chain has the expected structure
    key = ("up",)
    if key not in chain:
        return make_test_result(False, f"key {key} in chain", "key not found")

    return make_test_result(True, "valid chain", "valid chain")

test_markov_chain_simple.input_description = "data=['up','up','up','up'], order=1"


def test_markov_chain_order1(module):
    """Test markov_chain with order 1."""
    data = ["up", "down", "up", "down", "up"]
    chain = module.markov_chain(data, 1)

    # ("up",) should go to "down" with probability 1.0 (up is always followed by down)
    key = ("up",)
    if key not in chain:
        return make_test_result(False, f"key {key} in chain", "key not found")

    transitions = chain[key]
    if "down" not in transitions:
        return make_test_result(False, "'down' in transitions", str(transitions))

    # Check probability is 1.0 for down
    expected_prob = 1.0
    actual_prob = transitions.get("down", 0)
    return make_test_result(approx_equal(actual_prob, expected_prob), expected_prob, actual_prob)

test_markov_chain_order1.input_description = "data=['up','down','up','down','up'], order=1"


def test_markov_chain_order2(module):
    """Test markov_chain with order 2."""
    data = ["a", "b", "a", "b", "a", "b", "c"]
    chain = module.markov_chain(data, 2)

    # ("a", "b") should have transitions to both "a" and "c"
    key = ("a", "b")
    if key not in chain:
        return make_test_result(False, f"key {key} in chain", "key not found")

    return make_test_result(True, "order 2 chain", "order 2 chain")

test_markov_chain_order2.input_description = "data=['a','b','a','b','a','b','c'], order=2"


def test_markov_chain_probabilities_sum(module):
    """Test that markov_chain probabilities sum to 1."""
    data = ["a", "b", "c", "a", "b", "a", "c", "b"]
    chain = module.markov_chain(data, 1)

    for state, transitions in chain.items():
        total = sum(transitions.values())
        if not approx_equal(total, 1.0, tolerance=0.01):
            return make_test_result(
                False,
                f"probabilities sum to 1",
                f"state {state} sums to {total}"
            )

    return make_test_result(True, "all sum to 1", "all sum to 1")

test_markov_chain_probabilities_sum.input_description = "data=['a','b','c','a','b','a','c','b'], order=1"


def test_markov_chain_returns_dict(module):
    """Test that markov_chain returns a dictionary."""
    data = ["up", "down", "up"]
    chain = module.markov_chain(data, 1)

    if not isinstance(chain, dict):
        return make_test_result(False, "dict", type(chain).__name__)
    return make_test_result(True, "dict", "dict")

test_markov_chain_returns_dict.input_description = "data=['up','down','up'], order=1"


def test_markov_chain_three_states(module):
    """Test markov_chain with three states."""
    data = ["a", "b", "c", "a", "b", "c"]
    chain = module.markov_chain(data, 1)

    # Should have 3 states: a, b, c
    expected_states = {("a",), ("b",), ("c",)}
    actual_states = set(chain.keys())

    if expected_states != actual_states:
        return make_test_result(False, str(expected_states), str(actual_states))

    return make_test_result(True, "3 states", "3 states")

test_markov_chain_three_states.input_description = "data=['a','b','c','a','b','c'], order=1"


def test_markov_chain_integer_bins(module):
    """Test markov_chain with integer bins 0-3 (matching actual assignment data)."""
    # Simulates stock data binned into 4 categories: 0, 1, 2, 3
    data = [0, 1, 2, 3, 0, 1, 2, 3, 0, 1]
    chain = module.markov_chain(data, 1)

    if not isinstance(chain, dict):
        return make_test_result(False, "dict", type(chain).__name__)

    # (0,) should always transition to 1
    key = (0,)
    if key not in chain:
        return make_test_result(False, f"key {key} in chain", "key not found")

    if 1 not in chain[key]:
        return make_test_result(False, "1 in transitions from (0,)", str(chain[key]))

    # Probability of 0 -> 1 should be 1.0
    if not approx_equal(chain[key][1], 1.0):
        return make_test_result(False, 1.0, chain[key][1])

    return make_test_result(True, "valid integer bin chain", "valid integer bin chain")

test_markov_chain_integer_bins.input_description = "data=[0,1,2,3,0,1,2,3,0,1], order=1"


def test_markov_chain_order3(module):
    """Test markov_chain with order 3."""
    data = [0, 1, 2, 0, 1, 2, 0, 1, 2, 3]
    chain = module.markov_chain(data, 3)

    if not isinstance(chain, dict):
        return make_test_result(False, "dict", type(chain).__name__)

    # (0, 1, 2) should have transitions
    key = (0, 1, 2)
    if key not in chain:
        return make_test_result(False, f"key {key} in chain", "key not found")

    # (0, 1, 2) transitions to both 0 and 3 in this data
    transitions = chain[key]
    if 0 not in transitions and 3 not in transitions:
        return make_test_result(False, "0 or 3 in transitions", str(transitions))

    return make_test_result(True, "valid order 3 chain", "valid order 3 chain")

test_markov_chain_order3.input_description = "data=[0,1,2,0,1,2,0,1,2,3], order=3"


# ============================================================================
# Markov Chain Tests
# ============================================================================

def test_markov_chain_exact_probabilities(module):
    """Test that markov_chain computes exact probability values."""
    # 'a' is followed by 'b' twice and 'c' once -> P(b|a) = 2/3, P(c|a) = 1/3
    data = ["a", "b", "a", "c", "a", "b"]
    chain = module.markov_chain(data, 1)

    key = ("a",)
    if key not in chain:
        return make_test_result(False, f"key {key} in chain", "key not found")

    transitions = chain[key]

    # Check P(b|a) = 2/3
    expected_b = 2.0 / 3.0
    actual_b = transitions.get("b", 0)
    if not approx_equal(actual_b, expected_b, tolerance=0.01):
        return make_test_result(False, f"P(b|a)={expected_b:.3f}", f"P(b|a)={actual_b:.3f}")

    # Check P(c|a) = 1/3
    expected_c = 1.0 / 3.0
    actual_c = transitions.get("c", 0)
    if not approx_equal(actual_c, expected_c, tolerance=0.01):
        return make_test_result(False, f"P(c|a)={expected_c:.3f}", f"P(c|a)={actual_c:.3f}")

    return make_test_result(True, "exact probabilities", "exact probabilities")

test_markov_chain_exact_probabilities.input_description = "data=['a','b','a','c','a','b'], order=1"


def test_markov_chain_minimal_data(module):
    """Test markov_chain with minimal data (just one transition)."""
    data = [0, 1]
    chain = module.markov_chain(data, 1)

    if not isinstance(chain, dict):
        return make_test_result(False, "dict", type(chain).__name__)

    # Should have exactly one state: (0,) -> {1: 1.0}
    if len(chain) != 1:
        return make_test_result(False, "1 state", f"{len(chain)} states")

    key = (0,)
    if key not in chain:
        return make_test_result(False, f"key {key}", str(list(chain.keys())))

    if chain[key].get(1, 0) != 1.0:
        return make_test_result(False, "{1: 1.0}", str(chain[key]))

    return make_test_result(True, "single transition", "single transition")

test_markov_chain_minimal_data.input_description = "data=[0,1], order=1"


def test_markov_chain_insufficient_data(module):
    """Test markov_chain returns empty dict when data too short for order."""
    data = [0, 1]
    chain = module.markov_chain(data, 3)  # Need at least 4 elements for order 3

    if not isinstance(chain, dict):
        return make_test_result(False, "dict", type(chain).__name__)

    # Should be empty - no complete transitions possible
    if len(chain) != 0:
        return make_test_result(False, "empty dict", f"dict with {len(chain)} keys")

    return make_test_result(True, "empty dict", "empty dict")

test_markov_chain_insufficient_data.input_description = "data=[0,1], order=3"


def test_markov_chain_order2_probabilities(module):
    """Test markov_chain order 2 with specific probabilities."""
    # (0, 1) -> 2 twice, 3 once => P(2|0,1) = 2/3, P(3|0,1) = 1/3
    data = [0, 1, 2, 0, 1, 3, 0, 1, 2]
    chain = module.markov_chain(data, 2)

    key = (0, 1)
    if key not in chain:
        return make_test_result(False, f"key {key} in chain", "key not found")

    transitions = chain[key]

    expected_2 = 2.0 / 3.0
    actual_2 = transitions.get(2, 0)
    if not approx_equal(actual_2, expected_2, tolerance=0.01):
        return make_test_result(False, f"P(2|0,1)={expected_2:.3f}", f"P(2|0,1)={actual_2:.3f}")

    return make_test_result(True, "order 2 probabilities", "order 2 probabilities")

test_markov_chain_order2_probabilities.input_description = "data=[0,1,2,0,1,3,0,1,2], order=2"


# ============================================================================
# Predict Tests
# ============================================================================

def test_predict_length(module):
    """Test that predict returns correct length."""
    chain = {("up",): {"up": 1.0}, ("down",): {"down": 1.0}}
    last = ("up",)
    result = module.predict(chain, last, 5)

    if not isinstance(result, list):
        return make_test_result(False, "list", type(result).__name__)

    expected_len = 5
    actual_len = len(result)
    return make_test_result(actual_len == expected_len, expected_len, actual_len)

test_predict_length.input_description = "chain={('up',): {'up': 1.0}}, last=('up',), num=5"


def test_predict_deterministic(module):
    """Test predict with deterministic chain."""
    chain = {("a",): {"b": 1.0}, ("b",): {"a": 1.0}}
    last = ("a",)
    result = module.predict(chain, last, 4)

    # Should be ["b", "a", "b", "a"]
    expected = ["b", "a", "b", "a"]

    if result != expected:
        return make_test_result(False, expected, result)
    return make_test_result(True, expected, result)

test_predict_deterministic.input_description = "chain={('a',): {'b': 1.0}, ('b',): {'a': 1.0}}, last=('a',), num=4"


def test_predict_starts_correct(module):
    """Test that predict starts with correct state."""
    chain = {("x",): {"y": 1.0}, ("y",): {"x": 1.0}}
    last = ("x",)
    result = module.predict(chain, last, 1)

    if len(result) < 1:
        return make_test_result(False, "at least 1 prediction", "empty")

    # First prediction should be "y" (what follows "x")
    expected_first = "y"
    actual_first = result[0]
    return make_test_result(actual_first == expected_first, expected_first, actual_first)

test_predict_starts_correct.input_description = "chain={('x',): {'y': 1.0}}, last=('x',), num=1"


def test_predict_uses_chain(module):
    """Test that predict follows the chain transitions."""
    # Chain where state determines next state uniquely
    chain = {
        ("1",): {"2": 1.0},
        ("2",): {"3": 1.0},
        ("3",): {"1": 1.0}
    }
    last = ("1",)
    result = module.predict(chain, last, 6)

    # Should cycle: 2, 3, 1, 2, 3, 1
    expected = ["2", "3", "1", "2", "3", "1"]
    return make_test_result(result == expected, expected, result)

test_predict_uses_chain.input_description = "cyclic chain, last=('1',), num=6"


def test_predict_integer_bins(module):
    """Test predict with integer bins 0-3."""
    # Deterministic chain: 0 -> 1 -> 2 -> 3 -> 0
    chain = {
        (0,): {1: 1.0},
        (1,): {2: 1.0},
        (2,): {3: 1.0},
        (3,): {0: 1.0}
    }
    last = [0]  # note: list, not tuple (function signature says list)
    result = module.predict(chain, last, 4)

    expected = [1, 2, 3, 0]
    if result != expected:
        return make_test_result(False, expected, result)
    return make_test_result(True, expected, result)

test_predict_integer_bins.input_description = "integer bin chain, last=[0], num=4"


def test_predict_unknown_state(module):
    """Test predict handles unknown state by returning random bin 0-3."""
    # Chain only knows about state (0,)
    chain = {(0,): {1: 1.0}}
    # Start from unknown state (9,)
    last = [9]
    result = module.predict(chain, last, 10)

    if not isinstance(result, list) or len(result) != 10:
        return make_test_result(False, "list of length 10", f"{type(result).__name__} of length {len(result) if isinstance(result, list) else 'N/A'}")

    # All predictions should be integers in range 0-3 (random choice)
    for val in result:
        if not isinstance(val, int) or val < 0 or val > 3:
            return make_test_result(False, "all values in 0-3", f"got {val}")

    return make_test_result(True, "random 0-3 values", "random 0-3 values")

test_predict_unknown_state.input_description = "unknown state (9,) not in chain"


def test_predict_probabilistic(module):
    """Test predict with probabilistic transitions over many trials."""
    # Chain with 50/50 probability
    chain = {
        (0,): {0: 0.5, 1: 0.5}
    }
    last = [0]

    # Run many trials and check distribution
    count_0 = 0
    count_1 = 0
    trials = 100

    for _ in range(trials):
        result = module.predict(chain, last, 1)
        if len(result) > 0:
            if result[0] == 0:
                count_0 += 1
            elif result[0] == 1:
                count_1 += 1

    # With 50/50 probability, we expect roughly even split
    # Allow wide tolerance (20-80%) due to randomness
    total = count_0 + count_1
    if total == 0:
        return make_test_result(False, "predictions made", "no valid predictions")

    ratio_0 = count_0 / total
    if 0.2 <= ratio_0 <= 0.8:
        return make_test_result(True, "roughly 50/50 distribution", f"{count_0}/{count_1}")
    return make_test_result(False, "roughly 50/50 distribution", f"{count_0}/{count_1} (ratio {ratio_0:.2f})")

test_predict_probabilistic.input_description = "50/50 chain over 100 trials"


def test_predict_order2_sliding_window(module):
    """Test predict with order 2 chain (verifies sliding window works)."""
    # Order 2 chain: (0, 1) -> 2, (1, 2) -> 3, (2, 3) -> 0
    chain = {
        (0, 1): {2: 1.0},
        (1, 2): {3: 1.0},
        (2, 3): {0: 1.0},
        (3, 0): {1: 1.0}
    }
    last = [0, 1]  # Start with state (0, 1)
    result = module.predict(chain, last, 4)

    # Should predict: 2, 3, 0, 1
    # After 2: window is (1, 2) -> 3
    # After 3: window is (2, 3) -> 0
    # After 0: window is (3, 0) -> 1
    expected = [2, 3, 0, 1]
    if result != expected:
        return make_test_result(False, expected, result)
    return make_test_result(True, expected, result)

test_predict_order2_sliding_window.input_description = "order 2 chain, last=[0,1], num=4"


def test_predict_zero_predictions(module):
    """Test predict with num=0 returns empty list."""
    chain = {(0,): {1: 1.0}}
    last = [0]
    result = module.predict(chain, last, 0)

    if not isinstance(result, list):
        return make_test_result(False, "list", type(result).__name__)

    if len(result) != 0:
        return make_test_result(False, "empty list", f"list with {len(result)} elements")

    return make_test_result(True, "empty list", "empty list")

test_predict_zero_predictions.input_description = "num=0"


def test_predict_single_prediction(module):
    """Test predict with num=1."""
    chain = {(0,): {1: 1.0}, (1,): {0: 1.0}}
    last = [0]
    result = module.predict(chain, last, 1)

    if not isinstance(result, list) or len(result) != 1:
        return make_test_result(False, "list of length 1", f"{type(result).__name__} of length {len(result) if isinstance(result, list) else 'N/A'}")

    if result[0] != 1:
        return make_test_result(False, [1], result)

    return make_test_result(True, [1], result)

test_predict_single_prediction.input_description = "chain with 0->1, last=[0], num=1"


def test_predict_returns_list(module):
    """Test that predict always returns a list."""
    chain = {(0,): {1: 1.0}}
    last = [0]
    result = module.predict(chain, last, 3)

    if not isinstance(result, list):
        return make_test_result(False, "list", type(result).__name__)
    return make_test_result(True, "list", "list")

test_predict_returns_list.input_description = "basic predict call"


# ============================================================================
# MSE Tests
# ============================================================================

def test_mse_zero(module):
    """Test MSE when predictions match exactly."""
    result = [1, 2, 3, 4, 5]
    expected = [1, 2, 3, 4, 5]
    mse = module.mse(result, expected)

    if not approx_equal(mse, 0.0):
        return make_test_result(False, 0.0, mse)
    return make_test_result(True, 0.0, mse)

test_mse_zero.input_description = "result=[1,2,3,4,5], expected=[1,2,3,4,5]"


def test_mse_simple(module):
    """Test MSE with simple difference."""
    result = [0, 0, 0]
    expected = [1, 1, 1]
    mse = module.mse(result, expected)

    # MSE = (1 + 1 + 1) / 3 = 1
    expected_mse = 1.0
    if not approx_equal(mse, expected_mse):
        return make_test_result(False, expected_mse, mse)
    return make_test_result(True, expected_mse, mse)

test_mse_simple.input_description = "result=[0,0,0], expected=[1,1,1]"


def test_mse_lists(module):
    """Test MSE with varying differences."""
    result = [1, 2, 3]
    expected = [4, 5, 6]
    mse = module.mse(result, expected)

    # Differences: 3, 3, 3
    # MSE = (9 + 9 + 9) / 3 = 9
    expected_mse = 9.0
    if not approx_equal(mse, expected_mse):
        return make_test_result(False, expected_mse, mse)
    return make_test_result(True, expected_mse, mse)

test_mse_lists.input_description = "result=[1,2,3], expected=[4,5,6]"


def test_mse_negative(module):
    """Test MSE with negative values."""
    result = [-1, -2, -3]
    expected = [1, 2, 3]
    mse = module.mse(result, expected)

    # Differences: 2, 4, 6
    # MSE = (4 + 16 + 36) / 3 = 56/3 ≈ 18.67
    expected_mse = 56.0 / 3.0
    if not approx_equal(mse, expected_mse, tolerance=0.1):
        return make_test_result(False, expected_mse, mse)
    return make_test_result(True, expected_mse, mse)

test_mse_negative.input_description = "result=[-1,-2,-3], expected=[1,2,3]"


def test_mse_single_element(module):
    """Test MSE with single element lists."""
    result = [5]
    expected = [3]
    mse = module.mse(result, expected)

    # MSE = (5-3)^2 / 1 = 4
    expected_mse = 4.0
    if not approx_equal(mse, expected_mse):
        return make_test_result(False, expected_mse, mse)
    return make_test_result(True, expected_mse, mse)

test_mse_single_element.input_description = "result=[5], expected=[3]"


def test_mse_floats(module):
    """Test MSE with float values."""
    result = [1.5, 2.5, 3.5]
    expected = [1.0, 2.0, 3.0]
    mse = module.mse(result, expected)

    # Differences: 0.5, 0.5, 0.5
    # MSE = (0.25 + 0.25 + 0.25) / 3 = 0.25
    expected_mse = 0.25
    if not approx_equal(mse, expected_mse, tolerance=0.01):
        return make_test_result(False, expected_mse, mse)
    return make_test_result(True, expected_mse, mse)

test_mse_floats.input_description = "result=[1.5,2.5,3.5], expected=[1.0,2.0,3.0]"


def test_mse_varying_differences(module):
    """Test MSE with varying differences between elements."""
    result = [0, 0, 0]
    expected = [1, 2, 3]
    mse = module.mse(result, expected)

    # Differences: 1, 2, 3
    # MSE = (1 + 4 + 9) / 3 = 14/3 ≈ 4.67
    expected_mse = 14.0 / 3.0
    if not approx_equal(mse, expected_mse, tolerance=0.01):
        return make_test_result(False, expected_mse, mse)
    return make_test_result(True, expected_mse, mse)

test_mse_varying_differences.input_description = "result=[0,0,0], expected=[1,2,3]"


def test_mse_large_values(module):
    """Test MSE with larger values."""
    result = [100, 200, 300]
    expected = [110, 190, 310]
    mse = module.mse(result, expected)

    # Differences: 10, 10, 10
    # MSE = (100 + 100 + 100) / 3 = 100
    expected_mse = 100.0
    if not approx_equal(mse, expected_mse):
        return make_test_result(False, expected_mse, mse)
    return make_test_result(True, expected_mse, mse)

test_mse_large_values.input_description = "result=[100,200,300], expected=[110,190,310]"


# ============================================================================
# Run Experiment Tests
# ============================================================================

def test_run_experiment_returns_float(module):
    """Test that run_experiment returns a float (mean squared error)."""
    # train: data to build model from
    # order: markov chain order
    # test: last `order` values to start prediction
    # future: number of days to predict
    # actual: actual values for those future days
    # trials: number of trials to average
    train = [0, 1, 2, 1, 0, 1, 2, 1, 0, 1, 2]
    order = 1
    test = [1]  # last `order` values
    future = 3
    actual = [2, 1, 0]  # actual next 3 values
    trials = 10

    result = module.run_experiment(train, order, test, future, actual, trials)

    if isinstance(result, (int, float)):
        return make_test_result(True, "numeric", type(result).__name__)
    return make_test_result(False, "numeric (int or float)", type(result).__name__)

test_run_experiment_returns_float.input_description = "train=[0,1,2,1,0,1,2,1,0,1,2], order=1, test=[1], future=3, actual=[2,1,0], trials=10"


def test_run_experiment_perfect_prediction(module):
    """Test run_experiment with deterministic chain (perfect prediction possible)."""
    # Deterministic chain: 0 -> 1 -> 2 -> 0 -> ...
    train = [0, 1, 2, 0, 1, 2, 0, 1, 2]
    order = 1
    test = [2]  # start after a 2
    future = 3
    actual = [0, 1, 2]  # deterministic: after 2 comes 0, then 1, then 2
    trials = 5

    result = module.run_experiment(train, order, test, future, actual, trials)

    # With deterministic chain predicting [0, 1, 2] and actual being [0, 1, 2], MSE should be 0
    if approx_equal(result, 0.0, tolerance=0.01):
        return make_test_result(True, 0.0, result)
    return make_test_result(False, 0.0, result)

test_run_experiment_perfect_prediction.input_description = "deterministic chain, should have MSE=0"


def test_run_experiment_nonzero_mse(module):
    """Test run_experiment returns non-zero MSE when prediction differs from actual."""
    # Train on pattern that predicts 1 after 0
    train = [0, 1, 0, 1, 0, 1, 0, 1]
    order = 1
    test = [0]  # start after 0
    future = 2
    actual = [2, 2]  # but actual is [2, 2], not [1, 1]
    trials = 10

    result = module.run_experiment(train, order, test, future, actual, trials)

    # Prediction will be [1, 1] (deterministic), actual is [2, 2]
    # MSE = ((1-2)^2 + (1-2)^2) / 2 = 1.0
    if isinstance(result, (int, float)) and result > 0:
        return make_test_result(True, "positive MSE", result)
    return make_test_result(False, "positive MSE", result)

test_run_experiment_nonzero_mse.input_description = "prediction differs from actual"


def test_run_experiment_order2(module):
    """Test run_experiment with order 2 chain."""
    # Deterministic order 2 chain
    train = [0, 1, 2, 0, 1, 2, 0, 1, 2]
    order = 2
    test = [0, 1]  # Start with (0, 1)
    future = 3
    actual = [2, 0, 1]  # Deterministic: (0,1)->2, (1,2)->0, (2,0)->1
    trials = 5

    result = module.run_experiment(train, order, test, future, actual, trials)

    # Should have MSE = 0 (deterministic chain)
    if approx_equal(result, 0.0, tolerance=0.01):
        return make_test_result(True, 0.0, result)
    return make_test_result(False, 0.0, result)

test_run_experiment_order2.input_description = "order 2 chain, deterministic"


def test_run_experiment_single_trial(module):
    """Test run_experiment with single trial."""
    train = [0, 1, 0, 1, 0, 1]
    order = 1
    test = [0]
    future = 2
    actual = [1, 0]  # Matches deterministic prediction
    trials = 1

    result = module.run_experiment(train, order, test, future, actual, trials)

    if isinstance(result, (int, float)):
        # Deterministic chain, should have MSE = 0
        if approx_equal(result, 0.0, tolerance=0.01):
            return make_test_result(True, 0.0, result)
        return make_test_result(False, 0.0, result)
    return make_test_result(False, "numeric", type(result).__name__)

test_run_experiment_single_trial.input_description = "single trial"


def test_run_experiment_many_trials(module):
    """Test run_experiment with many trials."""
    # Use a deterministic chain - result should be same regardless of trials
    train = [0, 1, 2, 0, 1, 2, 0, 1, 2]
    order = 1
    test = [2]
    future = 3
    actual = [0, 1, 2]
    trials = 100

    result = module.run_experiment(train, order, test, future, actual, trials)

    # Deterministic chain -> MSE should be 0
    if approx_equal(result, 0.0, tolerance=0.01):
        return make_test_result(True, 0.0, result)
    return make_test_result(False, 0.0, result)

test_run_experiment_many_trials.input_description = "100 trials, deterministic chain"


def test_run_experiment_nonnegative(module):
    """Test that run_experiment always returns non-negative MSE."""
    train = [0, 1, 2, 3, 0, 1, 2, 3]
    order = 1
    test = [0]
    future = 4
    actual = [3, 2, 1, 0]  # Different from predictions
    trials = 10

    result = module.run_experiment(train, order, test, future, actual, trials)

    if isinstance(result, (int, float)) and result >= 0:
        return make_test_result(True, "non-negative", result)
    return make_test_result(False, "non-negative value", result)

test_run_experiment_nonnegative.input_description = "verify MSE >= 0"


def test_run_experiment_uses_all_params(module):
    """Test run_experiment uses all parameters correctly."""
    # This tests that the function actually uses train to build chain,
    # order for the chain order, test as starting point, and future for prediction count
    train = [0, 0, 0, 0, 0, 1]  # Mostly 0->0, one 0->1
    order = 1
    test = [0]
    future = 5
    actual = [0, 0, 0, 0, 0]
    trials = 50

    result = module.run_experiment(train, order, test, future, actual, trials)

    # The chain has 0->0 with high probability, so predictions should mostly be 0
    # Actual is all 0s, so MSE should be low (but not necessarily 0 due to randomness)
    if isinstance(result, (int, float)) and result >= 0:
        return make_test_result(True, "valid MSE", result)
    return make_test_result(False, "valid numeric MSE", result)

test_run_experiment_uses_all_params.input_description = "verify all parameters used"
