"""
Tests for Module 6: Sports Analytics

Tests the following:
- LinearModel class (__init__, __str__, get_weights, generate_predictions, prediction_error)
- read_matrix(filename)
- fit_least_squares(input_data, output_data)
- fit_lasso(param, iterations, input_data, output_data)
"""

import sys
from pathlib import Path

_tests_dir = Path(__file__).parent
if str(_tests_dir) not in sys.path:
    sys.path.insert(0, str(_tests_dir))

from fixtures import make_test_result, approx_equal

# Add lib to path for numeric module
lib_path = Path(__file__).parent.parent / "lib"
if str(lib_path) not in sys.path:
    sys.path.insert(0, str(lib_path))

import numeric

# Point values for each test
TEST_POINTS = {
    # LinearModel tests
    "test_linear_model_init": 1,
    "test_linear_model_str": 1,
    "test_linear_model_get_weights": 1,
    "test_linear_model_generate_predictions_simple": 1,
    "test_linear_model_generate_predictions_multiple": 1,
    "test_linear_model_prediction_error_perfect": 1,
    "test_linear_model_prediction_error_nonzero": 1,
    "test_linear_model_prediction_error_large": 1,
    # read_matrix tests
    "test_read_matrix_returns_matrix": 1,
    "test_read_matrix_shape": 1,
    "test_read_matrix_values": 1,
    "test_read_matrix_single_row": 1,
    "test_read_matrix_single_column": 1,
    "test_read_matrix_negative_values": 1,
    # fit_least_squares tests
    "test_fit_least_squares_returns_model": 1,
    "test_fit_least_squares_perfect_fit": 1,
    "test_fit_least_squares_simple": 1,
    "test_fit_least_squares_multivariate": 1,
    "test_fit_least_squares_weights_shape": 1,
    # fit_lasso tests
    "test_fit_lasso_returns_model": 1,
    "test_fit_lasso_simple": 1,
    "test_fit_lasso_weights_shape": 1,
    "test_fit_lasso_high_lambda": 1,
    "test_fit_lasso_iterations": 1,
    "test_fit_lasso_effect": 1,
}


# ============================================================================
# LinearModel Tests
# ============================================================================

def test_linear_model_init(module):
    """Test LinearModel initialization with weights matrix."""
    try:
        weights = numeric.Matrix([[1.0], [2.0], [3.0]])
        model = module.LinearModel(weights)
        return make_test_result(True, "creates model", "creates model")
    except Exception as e:
        return make_test_result(False, "creates model", str(e))

test_linear_model_init.input_description = "weights=[[1],[2],[3]] (3x1 matrix)"


def test_linear_model_str(module):
    """Test LinearModel __str__ method."""
    weights = numeric.Matrix([[1.0], [2.0]])
    model = module.LinearModel(weights)
    result = str(model)

    if isinstance(result, str) and len(result) > 0:
        return make_test_result(True, "non-empty string", result[:50])
    return make_test_result(False, "non-empty string", result)

test_linear_model_str.input_description = "str(LinearModel)"


def test_linear_model_get_weights(module):
    """Test LinearModel get_weights method."""
    weights = numeric.Matrix([[1.5], [2.5], [3.5]])
    model = module.LinearModel(weights)

    result = model.get_weights()

    # Check shape
    if result.shape() != (3, 1):
        return make_test_result(False, "(3, 1)", result.shape())

    # Check values
    if approx_equal(result[0, 0], 1.5) and approx_equal(result[1, 0], 2.5) and approx_equal(result[2, 0], 3.5):
        return make_test_result(True, "weights returned", "weights returned")
    return make_test_result(False, "[[1.5],[2.5],[3.5]]", str(result))

test_linear_model_get_weights.input_description = "get_weights() returns weights"


def test_linear_model_generate_predictions_simple(module):
    """Test LinearModel.generate_predictions with single feature."""
    # weights = [[2.0]], so prediction = 2*x
    weights = numeric.Matrix([[2.0]])
    model = module.LinearModel(weights)

    # inputs = [[3.0]] (1 sample, 1 feature)
    inputs = numeric.Matrix([[3.0]])
    result = model.generate_predictions(inputs)

    # Expected: [[6.0]] (2*3 = 6)
    if result.shape() != (1, 1):
        return make_test_result(False, "(1, 1)", result.shape())

    expected = 6.0
    if approx_equal(result[0, 0], expected):
        return make_test_result(True, expected, result[0, 0])
    return make_test_result(False, expected, result[0, 0])

test_linear_model_generate_predictions_simple.input_description = "weights=[[2]], inputs=[[3]]"


def test_linear_model_generate_predictions_multiple(module):
    """Test LinearModel.generate_predictions with multiple samples."""
    # weights = [[1.0], [2.0]], so prediction = x1 + 2*x2
    weights = numeric.Matrix([[1.0], [2.0]])
    model = module.LinearModel(weights)

    # inputs: 2 samples, 2 features each
    inputs = numeric.Matrix([[1.0, 1.0], [2.0, 3.0]])
    result = model.generate_predictions(inputs)

    # Expected: [[3.0], [8.0]] (1+2=3, 2+6=8)
    if result.shape() != (2, 1):
        return make_test_result(False, "(2, 1)", result.shape())

    if approx_equal(result[0, 0], 3.0) and approx_equal(result[1, 0], 8.0):
        return make_test_result(True, "[[3],[8]]", f"[[{result[0,0]}],[{result[1,0]}]]")
    return make_test_result(False, "[[3],[8]]", f"[[{result[0,0]}],[{result[1,0]}]]")

test_linear_model_generate_predictions_multiple.input_description = "2 samples, 2 features"


# ============================================================================
# LinearModel Tests
# ============================================================================

def test_linear_model_prediction_error_perfect(module):
    """Test LinearModel.prediction_error with perfect predictions."""
    # weights = [[2.0]], so prediction = 2*x
    weights = numeric.Matrix([[2.0]])
    model = module.LinearModel(weights)

    inputs = numeric.Matrix([[1.0], [2.0], [3.0]])
    actual = numeric.Matrix([[2.0], [4.0], [6.0]])  # Perfect match

    mse = model.prediction_error(inputs, actual)

    if approx_equal(mse, 0.0):
        return make_test_result(True, 0.0, mse)
    return make_test_result(False, 0.0, mse)

test_linear_model_prediction_error_perfect.input_description = "perfect predictions, MSE=0"


def test_linear_model_prediction_error_nonzero(module):
    """Test LinearModel.prediction_error with imperfect predictions."""
    # weights = [[1.0]], so prediction = x (but actual = x + 1)
    weights = numeric.Matrix([[1.0]])
    model = module.LinearModel(weights)

    inputs = numeric.Matrix([[1.0], [2.0], [3.0]])
    actual = numeric.Matrix([[2.0], [3.0], [4.0]])  # Each off by 1

    mse = model.prediction_error(inputs, actual)

    # MSE = (1^2 + 1^2 + 1^2) / 3 = 1.0
    expected = 1.0
    if approx_equal(mse, expected, tolerance=0.01):
        return make_test_result(True, expected, mse)
    return make_test_result(False, expected, mse)

test_linear_model_prediction_error_nonzero.input_description = "predictions off by 1"


def test_linear_model_prediction_error_large(module):
    """Test LinearModel.prediction_error with large errors."""
    # weights = [[0.0]], so prediction = 0 for all inputs
    weights = numeric.Matrix([[0.0]])
    model = module.LinearModel(weights)

    inputs = numeric.Matrix([[1.0], [2.0]])
    actual = numeric.Matrix([[10.0], [20.0]])

    mse = model.prediction_error(inputs, actual)

    # MSE = (10^2 + 20^2) / 2 = (100 + 400) / 2 = 250
    expected = 250.0
    if approx_equal(mse, expected, tolerance=0.1):
        return make_test_result(True, expected, mse)
    return make_test_result(False, expected, mse)

test_linear_model_prediction_error_large.input_description = "large prediction errors"


# ============================================================================
# Read Matrix Tests
# ============================================================================

def test_read_matrix_returns_matrix(module):
    """Test that read_matrix returns a Matrix object."""
    import tempfile
    import os

    with tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False) as f:
        f.write("1.0 2.0\n3.0 4.0\n")
        temp_file = f.name

    try:
        result = module.read_matrix(temp_file)
        # Check if it's a Matrix-like object
        if hasattr(result, 'shape') or hasattr(result, '__getitem__'):
            return make_test_result(True, "Matrix", type(result).__name__)
        return make_test_result(False, "Matrix", type(result).__name__)
    except Exception as e:
        return make_test_result(False, "Matrix", str(e))
    finally:
        os.unlink(temp_file)

test_read_matrix_returns_matrix.input_description = "2x2 matrix file"


def test_read_matrix_shape(module):
    """Test that read_matrix reads correct dimensions."""
    import tempfile
    import os

    with tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False) as f:
        f.write("1.0 2.0 3.0\n4.0 5.0 6.0\n")
        temp_file = f.name

    try:
        result = module.read_matrix(temp_file)
        shape = result.shape()

        if shape == (2, 3):
            return make_test_result(True, (2, 3), shape)
        return make_test_result(False, (2, 3), shape)
    except Exception as e:
        return make_test_result(False, "(2, 3)", str(e))
    finally:
        os.unlink(temp_file)

test_read_matrix_shape.input_description = "2x3 matrix file"


def test_read_matrix_values(module):
    """Test that read_matrix reads correct values."""
    import tempfile
    import os

    with tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False) as f:
        f.write("1.5 2.5\n3.5 4.5\n")
        temp_file = f.name

    try:
        result = module.read_matrix(temp_file)

        # Check values
        v00 = result[0, 0]
        v01 = result[0, 1]
        v10 = result[1, 0]
        v11 = result[1, 1]

        if approx_equal(v00, 1.5) and approx_equal(v01, 2.5) and \
           approx_equal(v10, 3.5) and approx_equal(v11, 4.5):
            return make_test_result(True, "[[1.5,2.5],[3.5,4.5]]", "correct")
        return make_test_result(False, "[[1.5,2.5],[3.5,4.5]]", f"[[{v00},{v01}],[{v10},{v11}]]")
    except Exception as e:
        return make_test_result(False, "correct values", str(e))
    finally:
        os.unlink(temp_file)

test_read_matrix_values.input_description = "matrix with floats"


def test_read_matrix_single_row(module):
    """Test read_matrix with single row."""
    import tempfile
    import os

    with tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False) as f:
        f.write("1.0 2.0 3.0 4.0 5.0\n")
        temp_file = f.name

    try:
        result = module.read_matrix(temp_file)
        shape = result.shape()

        if shape == (1, 5):
            return make_test_result(True, (1, 5), shape)
        return make_test_result(False, (1, 5), shape)
    except Exception as e:
        return make_test_result(False, "(1, 5)", str(e))
    finally:
        os.unlink(temp_file)

test_read_matrix_single_row.input_description = "1x5 row matrix"


def test_read_matrix_single_column(module):
    """Test read_matrix with single column."""
    import tempfile
    import os

    with tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False) as f:
        f.write("1.0\n2.0\n3.0\n4.0\n")
        temp_file = f.name

    try:
        result = module.read_matrix(temp_file)
        shape = result.shape()

        if shape == (4, 1):
            return make_test_result(True, (4, 1), shape)
        return make_test_result(False, (4, 1), shape)
    except Exception as e:
        return make_test_result(False, "(4, 1)", str(e))
    finally:
        os.unlink(temp_file)

test_read_matrix_single_column.input_description = "4x1 column matrix"


def test_read_matrix_negative_values(module):
    """Test read_matrix with negative numbers."""
    import tempfile
    import os

    with tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False) as f:
        f.write("-1.5 2.5\n3.5 -4.5\n")
        temp_file = f.name

    try:
        result = module.read_matrix(temp_file)

        v00 = result[0, 0]
        v11 = result[1, 1]

        if approx_equal(v00, -1.5) and approx_equal(v11, -4.5):
            return make_test_result(True, "correct negatives", f"[{v00}, {v11}]")
        return make_test_result(False, "correct negatives", f"[{v00}, {v11}]")
    except Exception as e:
        return make_test_result(False, "correct negatives", str(e))
    finally:
        os.unlink(temp_file)

test_read_matrix_negative_values.input_description = "matrix with negatives"


# ============================================================================
# Fit Least Squares Tests
# ============================================================================

def test_fit_least_squares_returns_model(module):
    """Test that fit_least_squares returns a LinearModel."""
    X = numeric.Matrix([[1.0], [2.0], [3.0]])
    y = numeric.Matrix([[2.0], [4.0], [6.0]])

    result = module.fit_least_squares(X, y)

    if isinstance(result, module.LinearModel):
        return make_test_result(True, "LinearModel", "LinearModel")
    return make_test_result(False, "LinearModel", type(result).__name__)

test_fit_least_squares_returns_model.input_description = "simple linear data"


def test_fit_least_squares_perfect_fit(module):
    """Test fit_least_squares on perfectly linear data."""
    # y = 2*x
    X = numeric.Matrix([[1.0], [2.0], [3.0], [4.0]])
    y = numeric.Matrix([[2.0], [4.0], [6.0], [8.0]])

    model = module.fit_least_squares(X, y)
    mse = model.prediction_error(X, y)

    if approx_equal(mse, 0.0, tolerance=0.01):
        return make_test_result(True, "MSE ~0", mse)
    return make_test_result(False, "MSE ~0", mse)

test_fit_least_squares_perfect_fit.input_description = "y = 2x"


def test_fit_least_squares_simple(module):
    """Test fit_least_squares learns correct pattern."""
    # y = 3*x
    X = numeric.Matrix([[1.0], [2.0], [3.0], [4.0], [5.0]])
    y = numeric.Matrix([[3.0], [6.0], [9.0], [12.0], [15.0]])

    model = module.fit_least_squares(X, y)

    # Test on new data: x=10 should give ~30
    test_input = numeric.Matrix([[10.0]])
    pred = model.generate_predictions(test_input)

    if approx_equal(pred[0, 0], 30.0, tolerance=0.5):
        return make_test_result(True, 30.0, pred[0, 0])
    return make_test_result(False, 30.0, pred[0, 0])

test_fit_least_squares_simple.input_description = "y = 3x"


def test_fit_least_squares_multivariate(module):
    """Test fit_least_squares with multiple features."""
    # y = x1 + 2*x2
    X = numeric.Matrix([[1.0, 1.0], [2.0, 1.0], [1.0, 2.0], [2.0, 2.0]])
    y = numeric.Matrix([[3.0], [4.0], [5.0], [6.0]])

    model = module.fit_least_squares(X, y)
    mse = model.prediction_error(X, y)

    if approx_equal(mse, 0.0, tolerance=0.1):
        return make_test_result(True, "MSE ~0", mse)
    return make_test_result(False, "MSE ~0", mse)

test_fit_least_squares_multivariate.input_description = "y = x1 + 2x2"


def test_fit_least_squares_weights_shape(module):
    """Test that fit_least_squares returns model with correct weights shape."""
    # 3 features - use linearly independent columns
    X = numeric.Matrix([[1.0, 0.0, 0.0], [0.0, 1.0, 0.0], [0.0, 0.0, 1.0], [1.0, 1.0, 1.0]])
    y = numeric.Matrix([[2.0], [3.0], [4.0], [9.0]])  # weights are [2, 3, 4]

    model = module.fit_least_squares(X, y)
    weights = model.get_weights()

    # Should be 3x1 (one weight per feature)
    expected_shape = (3, 1)
    if weights.shape() == expected_shape:
        return make_test_result(True, expected_shape, weights.shape())
    return make_test_result(False, expected_shape, weights.shape())

test_fit_least_squares_weights_shape.input_description = "3 features -> 3x1 weights"


# ============================================================================
# Fit LASSO Tests
# ============================================================================

def test_fit_lasso_returns_model(module):
    """Test that fit_lasso returns a LinearModel."""
    X = numeric.Matrix([[1.0], [2.0], [3.0]])
    y = numeric.Matrix([[2.0], [4.0], [6.0]])

    result = module.fit_lasso(0.1, 100, X, y)

    if isinstance(result, module.LinearModel):
        return make_test_result(True, "LinearModel", "LinearModel")
    return make_test_result(False, "LinearModel", type(result).__name__)

test_fit_lasso_returns_model.input_description = "param=0.1, iterations=100"


def test_fit_lasso_simple(module):
    """Test fit_lasso on simple data."""
    X = numeric.Matrix([[1.0], [2.0], [3.0], [4.0]])
    y = numeric.Matrix([[2.0], [4.0], [6.0], [8.0]])

    model = module.fit_lasso(0.01, 100, X, y)  # Small lambda

    # Test prediction
    test_input = numeric.Matrix([[5.0]])
    pred = model.generate_predictions(test_input)

    if approx_equal(pred[0, 0], 10.0, tolerance=1.0):
        return make_test_result(True, "~10", pred[0, 0])
    return make_test_result(False, "~10", pred[0, 0])

test_fit_lasso_simple.input_description = "y = 2x, lambda=0.01"


def test_fit_lasso_weights_shape(module):
    """Test that fit_lasso returns model with correct weights shape."""
    # 2 features
    X = numeric.Matrix([[1.0, 1.0], [2.0, 2.0], [3.0, 3.0]])
    y = numeric.Matrix([[2.0], [4.0], [6.0]])

    model = module.fit_lasso(0.1, 50, X, y)
    weights = model.get_weights()

    # Should be 2x1
    expected_shape = (2, 1)
    if weights.shape() == expected_shape:
        return make_test_result(True, expected_shape, weights.shape())
    return make_test_result(False, expected_shape, weights.shape())

test_fit_lasso_weights_shape.input_description = "2 features -> 2x1 weights"


def test_fit_lasso_high_lambda(module):
    """Test LASSO with high lambda (high regularization)."""
    X = numeric.Matrix([[1.0], [2.0], [3.0], [4.0]])
    y = numeric.Matrix([[2.0], [4.0], [6.0], [8.0]])

    model = module.fit_lasso(10000.0, 100, X, y)  # Very high lambda

    # With very high lambda, weights should be shrunk significantly
    # Just check it produces a result
    try:
        test_input = numeric.Matrix([[5.0]])
        pred = model.generate_predictions(test_input)
        return make_test_result(True, "produces result", pred[0, 0])
    except Exception as e:
        return make_test_result(False, "produces result", str(e))

test_fit_lasso_high_lambda.input_description = "lambda=10000"


def test_fit_lasso_iterations(module):
    """Test that fit_lasso accepts iterations parameter."""
    X = numeric.Matrix([[1.0], [2.0], [3.0]])
    y = numeric.Matrix([[2.0], [4.0], [6.0]])

    try:
        # Should accept iterations as second parameter
        model = module.fit_lasso(0.1, 10, X, y)
        return make_test_result(True, "accepts iterations", "accepts iterations")
    except Exception as e:
        return make_test_result(False, "accepts iterations", str(e))

test_fit_lasso_iterations.input_description = "iterations=10"


def test_fit_lasso_effect(module):
    """Test that increasing lambda changes the model."""
    X = numeric.Matrix([[1.0], [2.0], [3.0], [4.0]])
    y = numeric.Matrix([[2.0], [4.0], [6.0], [8.0]])

    model_low = module.fit_lasso(0.01, 100, X, y)
    model_high = module.fit_lasso(1000.0, 100, X, y)

    test_input = numeric.Matrix([[10.0]])
    pred_low = model_low.generate_predictions(test_input)
    pred_high = model_high.generate_predictions(test_input)

    # Predictions should be different
    if not approx_equal(pred_low[0, 0], pred_high[0, 0], tolerance=0.5):
        return make_test_result(True, "different predictions", f"low: {pred_low[0,0]}, high: {pred_high[0,0]}")
    return make_test_result(False, "different predictions", f"both: {pred_low[0,0]}")

test_fit_lasso_effect.input_description = "compare lambda=0.01 vs 1000"
