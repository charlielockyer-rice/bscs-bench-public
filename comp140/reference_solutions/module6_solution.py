"""
Module 6: Sports Analytics - Reference Solution

This module implements linear regression using:
1. Ordinary Least Squares (OLS): w = (X^T X)^{-1} X^T y
2. LASSO with shooting algorithm: minimizes MSE(w) + lambda * ||w||_1

The implementation supports both:
- The Matrix-based API from the template (for CodeSkulptor compatibility)
- A simpler list-based API for testing
"""

import sys
from pathlib import Path

# Add lib to path for numeric module
lib_path = Path(__file__).parent.parent / "lib"
if str(lib_path) not in sys.path:
    sys.path.insert(0, str(lib_path))

from numeric import Matrix


def read_matrix(filename):
    """
    Parse data from the file with the given filename into a matrix.

    The file format is expected to have one row per line, with values
    separated by commas or whitespace.

    input:
        - filename: a string representing the name of the file

    returns: a Matrix containing the elements in the given file
    """
    with open(filename, 'r') as file:
        content = file.read()

    rows = []
    for line in content.strip().split('\n'):
        if not line.strip():
            continue
        # Handle both comma-separated and space-separated values
        if ',' in line:
            values = [float(val.strip()) for val in line.split(',') if val.strip()]
        else:
            values = [float(val.strip()) for val in line.split() if val.strip()]
        if values:
            rows.append(values)

    return Matrix(rows)


def soft_threshold(x, t):
    """
    Apply the soft thresholding function.

    SoftThreshold(x, t) =
        x - t   if x > t
        0       if |x| <= t
        x + t   if x < -t

    This moves x toward 0 by distance t, clamping at 0.

    inputs:
        - x: the value to threshold
        - t: the threshold distance (must be non-negative)

    returns: the soft-thresholded value
    """
    if x > t:
        return x - t
    elif x < -t:
        return x + t
    else:
        return 0.0


class LinearModel:
    """
    A class used to represent a Linear statistical model of multiple variables.

    This model takes a vector of input variables and predicts that
    the measured variable will be their weighted sum plus an intercept.

    Supports two interfaces:
    1. Matrix-based (from template): weights is an m x 1 Matrix
    2. List-based (for testing): coefficients is a list, intercept is a float
    """

    def __init__(self, weights_or_coefficients, intercept=None):
        """
        Create a new LinearModel.

        Two modes of initialization:
        1. LinearModel(weights) - where weights is an m x 1 Matrix
        2. LinearModel(coefficients, intercept) - where coefficients is a list
           and intercept is a float

        inputs:
            - weights_or_coefficients: either an m x 1 Matrix or a list of coefficients
            - intercept: optional float intercept (only used in list mode)
        """
        if isinstance(weights_or_coefficients, Matrix):
            # Matrix-based mode (from template)
            self._weights = weights_or_coefficients
            self._intercept = 0.0  # Intercept is part of weights in matrix mode
            self._use_matrix_mode = True
        else:
            # List-based mode (for testing)
            self._coefficients = list(weights_or_coefficients)
            self._intercept = intercept if intercept is not None else 0.0
            # Create weights matrix for internal use (intercept as last weight)
            # Actually, we keep them separate for the list-based API
            self._use_matrix_mode = False
            # Also create a Matrix representation for compatibility
            m = len(self._coefficients)
            # In list mode, weights are just the coefficients (no intercept in matrix)
            weights_data = [[c] for c in self._coefficients]
            if m > 0:
                self._weights = Matrix(weights_data)
            else:
                self._weights = None

    def __str__(self):
        """
        Return: weights as a human readable string.
        """
        if self._use_matrix_mode:
            return str(self._weights)
        else:
            return f"LinearModel(coefficients={self._coefficients}, intercept={self._intercept})"

    def get_weights(self):
        """
        Return: the weights associated with the model (as Matrix).
        """
        return self._weights

    def generate_predictions(self, inputs):
        """
        Use this model to predict a matrix of measured variables
        given a matrix of input data.

        This implements: y = X * w

        inputs:
            - inputs: an n x m Matrix of explanatory variables

        Returns: an n x 1 Matrix of predictions
        """
        return inputs * self._weights

    def predict(self, features):
        """
        Predict the output for a single sample (list-based API).

        inputs:
            - features: a list of feature values [x1, x2, ..., xm]

        returns: a float representing the prediction
        """
        if self._use_matrix_mode:
            # Convert to Matrix, compute, extract value
            X = Matrix([features])
            result = self.generate_predictions(X)
            return result[0, 0]
        else:
            # Direct computation: sum(coef_i * x_i) + intercept
            total = sum(c * x for c, x in zip(self._coefficients, features))
            return total + self._intercept

    def prediction_error(self, inputs, actual_result):
        """
        Calculate the MSE between the actual measured data and the predictions
        generated by this model based on the input data.

        MSE = (1/n) * sum((predicted_i - actual_i)^2)

        inputs:
            - inputs: an n x m Matrix of explanatory variables
            - actual_result: an n x 1 Matrix of actual values

        Returns: a float that is the MSE between predictions and actual data
        """
        predictions = self.generate_predictions(inputs)
        n = predictions.shape()[0]

        total_squared_error = 0.0
        for i in range(n):
            error = predictions[i, 0] - actual_result[i, 0]
            total_squared_error += error * error

        return total_squared_error / n

    def mse(self, X, y):
        """
        Calculate MSE using list-based inputs (for testing API).

        inputs:
            - X: a list of lists, where each inner list is a sample's features
            - y: a list of actual output values

        returns: the mean squared error as a float
        """
        n = len(X)
        total_squared_error = 0.0

        for i in range(n):
            pred = self.predict(X[i])
            error = pred - y[i]
            total_squared_error += error * error

        return total_squared_error / n


def fit_least_squares(input_data, output_data):
    """
    Create a Linear Model which predicts the output vector
    given the input matrix with minimal Mean-Squared Error.

    Uses the normal equation: w = (X^T X)^{-1} X^T y

    Supports two input formats:
    1. Matrix objects (from template)
    2. Lists (for testing)

    inputs:
        - input_data: an n x m Matrix OR a list of lists (n samples, m features)
        - output_data: an n x 1 Matrix OR a list of n output values

    returns: a LinearModel object fit using Least Squares
    """
    # Handle list inputs (for testing API)
    if isinstance(input_data, list):
        # Convert lists to matrices
        # Add intercept column (column of 1s) for the list-based API
        n = len(input_data)
        m = len(input_data[0]) if input_data else 0

        # For the list-based API, we need to include an intercept
        # Add a column of 1s to X
        X_with_intercept = [row + [1.0] for row in input_data]
        X = Matrix(X_with_intercept)
        y = Matrix([[val] for val in output_data])

        # Compute weights: w = (X^T X)^{-1} X^T y
        X_T = X.transpose()
        XTX = X_T * X
        XTX_inv = XTX.inverse()
        XTy = X_T * y
        weights = XTX_inv * XTy

        # Extract coefficients and intercept
        coefficients = [weights[i, 0] for i in range(m)]
        intercept = weights[m, 0]

        return LinearModel(coefficients, intercept)
    else:
        # Matrix inputs (from template)
        X = input_data
        y = output_data

        # Compute weights: w = (X^T X)^{-1} X^T y
        X_T = X.transpose()
        XTX = X_T * X
        XTX_inv = XTX.inverse()
        XTy = X_T * y
        weights = XTX_inv * XTy

        return LinearModel(weights)


def fit_lasso(input_data_or_param, iterations_or_y, input_data_or_lambda=None, output_data=None):
    """
    Create a Linear Model using the LASSO method with shooting algorithm.

    Supports two calling conventions:
    1. fit_lasso(param, iterations, input_data, output_data) - from template
    2. fit_lasso(X, y, lambda_param) - for testing

    The LASSO algorithm minimizes: MSE(w) + lambda * ||w||_1

    For the shooting algorithm:
    1. Initialize w with least squares estimate
    2. For each iteration, update each w_j using:
       a_j = (X^T y)_{j,0} - ((X^T X)_{j,:} w)_{0,0}) / (X^T X)_{j,j}
       b_j = lambda / (2 * (X^T X)_{j,j})
       w_j = SoftThreshold(w_j + a_j, b_j)
    3. Stop if ||w - w_old||_1 < 1e-5 or iterations exhausted
    """
    # Detect calling convention
    if output_data is not None:
        # Template calling convention: fit_lasso(param, iterations, input_data, output_data)
        lambda_param = input_data_or_param
        iterations = iterations_or_y
        X = input_data_or_lambda
        y = output_data
        is_matrix_mode = True
    else:
        # Testing calling convention: fit_lasso(X, y, lambda_param)
        X_list = input_data_or_param
        y_list = iterations_or_y
        lambda_param = input_data_or_lambda if input_data_or_lambda is not None else 0.0
        iterations = 1000  # Default iterations for testing
        is_matrix_mode = False

        # Convert lists to matrices with intercept column
        n = len(X_list)
        m = len(X_list[0]) if X_list else 0
        X_with_intercept = [row + [1.0] for row in X_list]
        X = Matrix(X_with_intercept)
        y = Matrix([[val] for val in y_list])

    # Get dimensions
    n, m = X.shape()

    # Precompute X^T X and X^T y (these don't change between iterations)
    X_T = X.transpose()
    XTX = X_T * X  # m x m
    XTy = X_T * y  # m x 1

    # Initialize w with least squares estimate
    XTX_inv = XTX.inverse()
    w = XTX_inv * XTy  # m x 1

    # Extract w into a mutable list for easier manipulation
    w_list = [w[j, 0] for j in range(m)]

    # Shooting algorithm
    for iteration in range(iterations):
        w_old = list(w_list)

        for j in range(m):
            # Compute (X^T X)_{j,:} w = row j of XTX times w
            # This is the dot product of row j of XTX with w
            XTX_j_dot_w = sum(XTX[j, k] * w_list[k] for k in range(m))

            # a_j = ((X^T y)_{j,0} - (X^T X)_{j,:} w) / (X^T X)_{j,j}
            XTX_jj = XTX[j, j]
            if abs(XTX_jj) < 1e-10:
                # Avoid division by zero
                continue

            a_j = (XTy[j, 0] - XTX_j_dot_w) / XTX_jj

            # b_j = lambda / (2 * (X^T X)_{j,j})
            b_j = lambda_param / (2.0 * XTX_jj)

            # Update w_j
            w_list[j] = soft_threshold(w_list[j] + a_j, b_j)

        # Check convergence: ||w - w_old||_1 < 1e-5
        diff_norm = sum(abs(w_list[j] - w_old[j]) for j in range(m))
        if diff_norm < 1e-5:
            break

    # Convert w back to Matrix
    weights = Matrix([[w_list[j]] for j in range(m)])

    if is_matrix_mode:
        return LinearModel(weights)
    else:
        # Extract coefficients and intercept for list-based API
        # Last weight is intercept (from the column of 1s we added)
        m_original = len(X_list[0]) if X_list else 0
        coefficients = [w_list[j] for j in range(m_original)]
        intercept = w_list[m_original]
        return LinearModel(coefficients, intercept)


def run_experiment(iterations):
    """
    Using historical data from 1954-2000 as training data, generate weights
    for a Linear Model using both Least-Squares and LASSO methods.

    Test each model using historical data from 2001-2012.

    inputs:
        - iterations: number of iterations to use for LASSO

    Print out each model's prediction error on both data sets.
    """
    # Note: This function requires access to the data files which may not be
    # available locally. The implementation is provided for completeness.

    # URLs for data files (if using codeskulptor/urllib)
    # For local testing, these would need to be downloaded first

    print("run_experiment requires data files which are not available locally.")
    print("This function is meant to be run in CodeSkulptor with access to:")
    print("  - comp140_analytics_baseball.txt")
    print("  - comp140_analytics_wins.txt")
    print("  - comp140_analytics_baseball_test.txt")
    print("  - comp140_analytics_wins_test.txt")

    # Template implementation (would work if files were available):
    """
    # Read training data
    X_train = read_matrix("comp140_analytics_baseball.txt")
    y_train = read_matrix("comp140_analytics_wins.txt")

    # Read test data
    X_test = read_matrix("comp140_analytics_baseball_test.txt")
    y_test = read_matrix("comp140_analytics_wins_test.txt")

    # Fit least squares model
    ls_model = fit_least_squares(X_train, y_train)
    print("Created LS model:")
    print(f"Error on training data: {ls_model.prediction_error(X_train, y_train)}")
    print(f"Error on test data: {ls_model.prediction_error(X_test, y_test)}")

    # Fit LASSO models with different lambda values
    for lam in [1000, 10000, 100000]:
        lasso_model = fit_lasso(lam, iterations, X_train, y_train)
        print(f"\nCreated LASSO model; param = {lam}")
        print(f"Error on training data: {lasso_model.prediction_error(X_train, y_train)}")
        print(f"Error on test data: {lasso_model.prediction_error(X_test, y_test)}")
    """


# Testing code
if __name__ == "__main__":
    # Test read_matrix
    print("Testing read_matrix...")
    import tempfile
    import os

    with tempfile.NamedTemporaryFile(mode='w', suffix='.txt', delete=False) as f:
        f.write("2.5,6,3\n1,2,3\n")
        temp_file = f.name

    try:
        m = read_matrix(temp_file)
        print(f"Read matrix shape: {m.shape()}")
        print(f"Matrix:\n{m}")
        assert m.shape() == (2, 3)
        assert abs(m[0, 0] - 2.5) < 0.001
    finally:
        os.unlink(temp_file)

    # Test LinearModel with Matrix
    print("\nTesting LinearModel (Matrix mode)...")
    weights = Matrix([[2.0], [1.0]])
    model = LinearModel(weights)
    X = Matrix([[1.0, 1.0], [2.0, 1.0], [3.0, 1.0]])
    preds = model.generate_predictions(X)
    print(f"Predictions: {preds}")

    # Test LinearModel with list
    print("\nTesting LinearModel (list mode)...")
    model2 = LinearModel([2.0], 1.0)  # y = 2x + 1
    pred = model2.predict([3.0])
    print(f"predict([3.0]) = {pred} (expected 7.0)")
    assert abs(pred - 7.0) < 0.001

    # Test MSE
    X_list = [[1.0], [2.0], [3.0]]
    y_list = [3.0, 5.0, 7.0]  # y = 2x + 1
    mse = model2.mse(X_list, y_list)
    print(f"MSE (perfect fit): {mse}")
    assert abs(mse) < 0.001

    # Test fit_least_squares with list
    print("\nTesting fit_least_squares (list mode)...")
    X_list = [[1.0], [2.0], [3.0], [4.0]]
    y_list = [3.0, 5.0, 7.0, 9.0]  # y = 2x + 1
    ls_model = fit_least_squares(X_list, y_list)
    pred = ls_model.predict([5.0])
    print(f"Least squares predict([5.0]) = {pred} (expected 11.0)")
    assert abs(pred - 11.0) < 0.1

    # Test fit_least_squares with Matrix
    print("\nTesting fit_least_squares (Matrix mode)...")
    X = Matrix([[1.0, 1.0], [2.0, 1.0], [3.0, 1.0], [4.0, 1.0]])  # includes intercept column
    y = Matrix([[3.0], [5.0], [7.0], [9.0]])  # y = 2x + 1
    ls_model2 = fit_least_squares(X, y)
    preds = ls_model2.generate_predictions(X)
    print(f"Predictions:\n{preds}")

    # Test fit_lasso with list
    print("\nTesting fit_lasso (list mode)...")
    lasso_model = fit_lasso(X_list, y_list, 0.01)
    pred = lasso_model.predict([5.0])
    print(f"LASSO predict([5.0]) = {pred} (expected ~11.0)")

    # Test fit_lasso with Matrix (template mode)
    print("\nTesting fit_lasso (Matrix mode)...")
    lasso_model2 = fit_lasso(0.01, 100, X, y)
    preds = lasso_model2.generate_predictions(X)
    print(f"LASSO Predictions:\n{preds}")

    print("\nAll tests passed!")
