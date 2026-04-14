"""
COMP 341 Homework 4: Houston Home Price Prediction - Test Suite

Tests organized by functional area:
- Data loading and missing values
- Error metrics (RMSE, RMSLE)
- Baseline heuristic
- Linear Regression
- Lasso Regression and alpha tuning
- SVR (linear and RBF kernels)
- Decision Tree Regressor and cross-validation
- Kaggle submission format
"""

import pytest
import pandas as pd
import numpy as np
import os
import sys
import tempfile
from pathlib import Path

# =============================================================================
# Test Configuration
# =============================================================================

# Get paths from environment or use defaults
DATA_DIR = Path(os.environ.get('COMP341_DATA_DIR',
    Path(__file__).parent.parent / 'data')) / 'hw4'
WORKSPACE = Path(os.environ.get('COMP341_WORKSPACE', '.'))

# Add workspace to path for importing solution
sys.path.insert(0, str(WORKSPACE))

# Import will fail if solution.py doesn't exist - that's expected for template
try:
    import solution
    SOLUTION_AVAILABLE = True
except ImportError:
    SOLUTION_AVAILABLE = False


# Skip all tests if solution not available
pytestmark = pytest.mark.skipif(
    not SOLUTION_AVAILABLE,
    reason="solution.py not found in workspace"
)

# Expected columns in the raw housing data
EXPECTED_COLS = {
    'houseid', 'property_type', 'city', 'state', 'zipcode',
    'list_price', 'beds', 'baths', 'location', 'sqft',
    'lot_size', 'year_built', 'days_on_market', 'hoa',
    'latitude', 'longitude'
}


# =============================================================================
# Fixtures
# =============================================================================

@pytest.fixture(scope="module")
def housing_df():
    """Load raw housing data."""
    if not SOLUTION_AVAILABLE:
        pytest.skip("solution.py not available")
    return solution.load_housing_data(DATA_DIR)


@pytest.fixture(scope="module")
def features_target(housing_df):
    """Separate features and target, drop non-predictive columns."""
    y = housing_df['list_price']
    drop_cols = [c for c in ['houseid', 'list_price', 'city', 'state',
                             'location', 'zipcode'] if c in housing_df.columns]
    X = housing_df.drop(columns=drop_cols)
    return X, y


@pytest.fixture(scope="module")
def train_val_split(features_target):
    """80/20 train/val split."""
    from sklearn.model_selection import train_test_split
    X, y = features_target
    X_train, X_val, y_train, y_val = train_test_split(
        X, y, test_size=0.20, random_state=42
    )
    return X_train, X_val, y_train, y_val


@pytest.fixture(scope="module")
def preprocessed_data(train_val_split):
    """Handle missing values and encode categoricals."""
    X_train, X_val, y_train, y_val = train_val_split
    X_train = X_train.copy()
    X_val = X_val.copy()

    # Fill hoa NaN with 0 (NaN means no HOA)
    X_train['hoa'] = X_train['hoa'].fillna(0)
    X_val['hoa'] = X_val['hoa'].fillna(0)

    # Impute other numeric NaN with training mean
    numeric_cols = X_train.select_dtypes(include=[np.number]).columns
    train_means = X_train[numeric_cols].mean()
    X_train[numeric_cols] = X_train[numeric_cols].fillna(train_means)
    X_val[numeric_cols] = X_val[numeric_cols].fillna(train_means)

    # One-hot encode property_type
    X_train = pd.get_dummies(X_train, columns=['property_type'])
    X_val = pd.get_dummies(X_val, columns=['property_type'])
    X_train, X_val = X_train.align(X_val, join='outer', axis=1, fill_value=0)

    return X_train, X_val, y_train, y_val


@pytest.fixture(scope="module")
def scaled_data(preprocessed_data):
    """Scale features with StandardScaler."""
    from sklearn.preprocessing import StandardScaler
    X_train, X_val, y_train, y_val = preprocessed_data
    scaler = StandardScaler()
    X_train_sc = pd.DataFrame(
        scaler.fit_transform(X_train),
        columns=X_train.columns,
        index=X_train.index
    )
    X_val_sc = pd.DataFrame(
        scaler.transform(X_val),
        columns=X_val.columns,
        index=X_val.index
    )
    return X_train_sc, X_val_sc, y_train, y_val


@pytest.fixture(scope="module")
def holdout_df():
    """Load test housing data (Kaggle holdout)."""
    return pd.read_csv(DATA_DIR / 'houston_homes_test.csv')


@pytest.fixture(scope="module")
def preprocessed_test_data(preprocessed_data, holdout_df):
    """Preprocess test data the same way as training data for submission test."""
    X_train, _, _, _ = preprocessed_data
    test_df = holdout_df.copy()

    # Drop non-predictive columns (same as training)
    drop_cols = [c for c in ['houseid', 'city', 'state', 'location', 'zipcode']
                 if c in test_df.columns]
    X_test = test_df.drop(columns=drop_cols)

    # Fill hoa NaN with 0
    X_test['hoa'] = X_test['hoa'].fillna(0)

    # Impute numeric NaN with column mean (from test set itself for simplicity)
    numeric_cols = X_test.select_dtypes(include=[np.number]).columns
    X_test[numeric_cols] = X_test[numeric_cols].fillna(X_test[numeric_cols].mean())

    # One-hot encode property_type
    X_test = pd.get_dummies(X_test, columns=['property_type'])

    # Align columns with training data
    X_test, _ = X_test.align(X_train, join='right', axis=1, fill_value=0)

    return X_test


# =============================================================================
# Data Loading
# =============================================================================

class TestDataLoading:
    """Verify housing data loads correctly."""

    def test_rmsle_calculation(self):
        """Verify RMSLE calculation with known values."""
        # Test 1: Identical arrays -> RMSLE = 0
        y_true = np.array([100, 200, 300])
        y_pred = np.array([100, 200, 300])
        rmsle = solution.calculate_rmsle(y_true, y_pred)

        assert isinstance(rmsle, (int, float, np.floating)), \
            f"RMSLE should be a number, got {type(rmsle).__name__}"
        assert abs(rmsle) < 1e-10, \
            f"RMSLE of identical arrays should be 0.0, got {rmsle}"

        # Test 2: Different values -> RMSLE > 0
        y_pred_diff = np.array([110, 190, 310])
        rmsle_diff = solution.calculate_rmsle(y_true, y_pred_diff)
        assert rmsle_diff > 0, \
            f"RMSLE of different arrays should be > 0, got {rmsle_diff}"

        # Test 3: Known computation
        # RMSLE = sqrt(mean((log(1+y_true) - log(1+y_pred))^2))
        y_a = np.array([1, 10, 100])
        y_b = np.array([1, 10, 100])
        rmsle_same = solution.calculate_rmsle(y_a, y_b)
        assert abs(rmsle_same) < 1e-10, \
            f"RMSLE of identical arrays should be 0.0, got {rmsle_same}"

        # Test 4: RMSLE should be non-negative
        assert rmsle_diff >= 0, "RMSLE should be non-negative"


# =============================================================================
# Baseline
# =============================================================================

class TestBaseline:
    """Verify baseline heuristic predictions."""

    def test_baseline_rmse(self, train_val_split):
        """Verify baseline heuristic returns mean-based predictions."""
        _, X_val, y_train, y_val = train_val_split
        n_val = len(y_val)

        baseline_preds = solution.baseline_heuristic(y_train, n_val)

        # Should return ndarray of correct length
        assert isinstance(baseline_preds, np.ndarray), \
            f"Should return np.ndarray, got {type(baseline_preds).__name__}"
        assert len(baseline_preds) == n_val, \
            f"Should return {n_val} predictions, got {len(baseline_preds)}"

        # All predictions should be the same value (the training mean)
        assert np.all(baseline_preds == baseline_preds[0]), \
            "All baseline predictions should be the same value (the mean)"

        # The predicted value should be close to the mean of y_train
        train_mean = y_train.mean()
        assert abs(baseline_preds[0] - train_mean) < 1.0, \
            f"Baseline prediction {baseline_preds[0]:.2f} should be close to " \
            f"training mean {train_mean:.2f}"

        # Compute RMSE with validation set - should be in reasonable range
        rmse = np.sqrt(np.mean((y_val.values - baseline_preds) ** 2))
        assert rmse > 200_000, \
            f"Baseline RMSE ${rmse:,.0f} is suspiciously low for a mean-only model"
        assert rmse < 1_500_000, \
            f"Baseline RMSE ${rmse:,.0f} is unreasonably high"


# =============================================================================
# Linear Regression
# =============================================================================

class TestLinearRegression:
    """Verify linear regression training."""

    def test_linear_regression(self, preprocessed_data):
        """Verify linear regression with preprocessed features."""
        from sklearn.linear_model import LinearRegression

        X_train, X_val, y_train, y_val = preprocessed_data
        result = solution.train_linear_regression(X_train, y_train, X_val, y_val)

        # Should return (model, train_rmse, val_rmse)
        assert len(result) == 3, \
            f"Should return (model, train_rmse, val_rmse), got {len(result)} values"

        model, train_rmse, val_rmse = result

        # Model should be a LinearRegression
        assert isinstance(model, LinearRegression), \
            f"Model should be LinearRegression, got {type(model).__name__}"

        # Model should have coef_ attribute (i.e., it was fitted)
        assert hasattr(model, 'coef_'), \
            "Model should have coef_ attribute (must be fitted)"

        # RMSE values should be positive floats
        assert isinstance(train_rmse, (int, float, np.floating)), \
            f"train_rmse should be a number, got {type(train_rmse).__name__}"
        assert isinstance(val_rmse, (int, float, np.floating)), \
            f"val_rmse should be a number, got {type(val_rmse).__name__}"
        assert train_rmse > 0, "train_rmse should be positive"
        assert val_rmse > 0, "val_rmse should be positive"

        # Validation RMSE should be reasonable (any decent model < $1M)
        assert val_rmse < 1_000_000, \
            f"Validation RMSE ${val_rmse:,.0f} should be < $1,000,000"

        # Training and validation RMSE should not be wildly different
        assert train_rmse <= val_rmse * 2, \
            f"Training RMSE ${train_rmse:,.0f} should not be > 2x " \
            f"validation RMSE ${val_rmse:,.0f}"


# =============================================================================
# Lasso Regression
# =============================================================================

class TestLassoRegression:
    """Verify Lasso regression and alpha tuning."""

    def test_lasso_regression(self, preprocessed_data):
        """Verify Lasso regression with default alpha."""
        from sklearn.linear_model import Lasso

        X_train, X_val, y_train, y_val = preprocessed_data
        result = solution.train_lasso_regression(X_train, y_train, X_val, y_val)

        # Should return (model, train_rmsle, val_rmsle)
        assert len(result) == 3, \
            f"Should return (model, train_rmsle, val_rmsle), got {len(result)} values"

        model, train_rmsle, val_rmsle = result

        # Model should be a Lasso
        assert isinstance(model, Lasso), \
            f"Model should be Lasso, got {type(model).__name__}"

        # RMSLE values should be positive floats
        assert isinstance(train_rmsle, (int, float, np.floating)), \
            f"train_rmsle should be a number, got {type(train_rmsle).__name__}"
        assert isinstance(val_rmsle, (int, float, np.floating)), \
            f"val_rmsle should be a number, got {type(val_rmsle).__name__}"
        assert train_rmsle > 0, "train_rmsle should be positive"
        assert val_rmsle > 0, "val_rmsle should be positive"

        # RMSLE should be within reasonable range
        assert val_rmsle < 1.5, \
            f"Validation RMSLE {val_rmsle:.4f} should be < 1.5"

    def test_lasso_alpha_tuning(self, preprocessed_data):
        """Verify Lasso alpha tuning returns correct structure."""
        X_train, X_val, y_train, y_val = preprocessed_data
        result = solution.tune_lasso_alpha(X_train, y_train, X_val, y_val)

        # Should return a dict
        assert isinstance(result, dict), \
            f"Should return a dict, got {type(result).__name__}"

        # Should have required keys
        assert 'train_rmsles' in result, \
            "Result should contain 'train_rmsles'"
        assert 'val_rmsles' in result, \
            "Result should contain 'val_rmsles'"
        assert 'best_alpha' in result, \
            "Result should contain 'best_alpha'"

        train_rmsles = result['train_rmsles']
        val_rmsles = result['val_rmsles']
        best_alpha = result['best_alpha']

        # Should have tried multiple alpha values (default range has >10)
        assert len(train_rmsles) > 10, \
            f"Should try >10 alpha values, got {len(train_rmsles)}"
        assert len(val_rmsles) > 10, \
            f"Should try >10 alpha values, got {len(val_rmsles)}"

        # Train and val lists should have the same length
        assert len(train_rmsles) == len(val_rmsles), \
            f"train_rmsles ({len(train_rmsles)}) and val_rmsles ({len(val_rmsles)}) " \
            "should have the same length"

        # All RMSLE values should be positive
        for i, (tr, va) in enumerate(zip(train_rmsles, val_rmsles)):
            assert tr > 0, f"train_rmsle[{i}] should be positive, got {tr}"
            assert va > 0, f"val_rmsle[{i}] should be positive, got {va}"

        # best_alpha should be a non-negative number
        assert isinstance(best_alpha, (int, float, np.integer, np.floating)), \
            f"best_alpha should be a number, got {type(best_alpha).__name__}"
        assert best_alpha >= 0, \
            f"best_alpha should be non-negative, got {best_alpha}"


# =============================================================================
# SVR
# =============================================================================

class TestSVR:
    """Verify SVR with linear and RBF kernels (requires scaled data)."""

    def test_svr_linear(self, scaled_data):
        """Verify SVR with linear kernel on scaled features."""
        from sklearn.svm import SVR as SVRModel

        X_train_sc, X_val_sc, y_train, y_val = scaled_data
        result = solution.train_svr(
            X_train_sc, y_train, X_val_sc, y_val,
            kernel='linear', C=100
        )

        # Should return (model, train_rmsle, val_rmsle)
        assert len(result) == 3, \
            f"Should return (model, train_rmsle, val_rmsle), got {len(result)} values"

        model, train_rmsle, val_rmsle = result

        # Model should be an SVR instance
        assert isinstance(model, SVRModel), \
            f"Model should be SVR, got {type(model).__name__}"

        # RMSLE values should be positive floats
        assert isinstance(train_rmsle, (int, float, np.floating)), \
            f"train_rmsle should be a number, got {type(train_rmsle).__name__}"
        assert isinstance(val_rmsle, (int, float, np.floating)), \
            f"val_rmsle should be a number, got {type(val_rmsle).__name__}"
        assert train_rmsle > 0, "train_rmsle should be positive"
        assert val_rmsle > 0, "val_rmsle should be positive"

        # RMSLE should be within generous range
        assert val_rmsle < 2.0, \
            f"Validation RMSLE {val_rmsle:.4f} should be < 2.0"

    def test_svr_rbf(self, scaled_data):
        """Verify SVR with RBF kernel on scaled features."""
        from sklearn.svm import SVR as SVRModel

        X_train_sc, X_val_sc, y_train, y_val = scaled_data
        result = solution.train_svr(
            X_train_sc, y_train, X_val_sc, y_val,
            kernel='rbf', C=100
        )

        # Should return (model, train_rmsle, val_rmsle)
        assert len(result) == 3, \
            f"Should return (model, train_rmsle, val_rmsle), got {len(result)} values"

        model, train_rmsle, val_rmsle = result

        # Model should be an SVR instance
        assert isinstance(model, SVRModel), \
            f"Model should be SVR, got {type(model).__name__}"

        # RMSLE values should be positive floats
        assert isinstance(train_rmsle, (int, float, np.floating)), \
            f"train_rmsle should be a number, got {type(train_rmsle).__name__}"
        assert isinstance(val_rmsle, (int, float, np.floating)), \
            f"val_rmsle should be a number, got {type(val_rmsle).__name__}"
        assert train_rmsle > 0, "train_rmsle should be positive"
        assert val_rmsle > 0, "val_rmsle should be positive"

        # RMSLE should be within generous range
        assert val_rmsle < 2.0, \
            f"Validation RMSLE {val_rmsle:.4f} should be < 2.0"


# =============================================================================
# Decision Tree Regressor
# =============================================================================

class TestDecisionTree:
    """Verify decision tree regressor and cross-validation."""

    def test_decision_tree_regressor(self, preprocessed_data):
        """Verify decision tree regressor with default (unlimited) depth."""
        from sklearn.tree import DecisionTreeRegressor

        X_train, X_val, y_train, y_val = preprocessed_data
        result = solution.train_decision_tree_regressor(
            X_train, y_train, X_val, y_val
        )

        # Should return (model, train_rmsle, val_rmsle)
        assert len(result) == 3, \
            f"Should return (model, train_rmsle, val_rmsle), got {len(result)} values"

        model, train_rmsle, val_rmsle = result

        # Model should be a DecisionTreeRegressor
        assert isinstance(model, DecisionTreeRegressor), \
            f"Model should be DecisionTreeRegressor, got {type(model).__name__}"

        # RMSLE values should be positive floats
        assert isinstance(train_rmsle, (int, float, np.floating)), \
            f"train_rmsle should be a number, got {type(train_rmsle).__name__}"
        assert isinstance(val_rmsle, (int, float, np.floating)), \
            f"val_rmsle should be a number, got {type(val_rmsle).__name__}"

        # Training RMSLE should be very low (tree overfits with unlimited depth)
        assert train_rmsle < 0.5, \
            f"Training RMSLE {train_rmsle:.4f} should be < 0.5 " \
            "(tree should overfit training data)"

        # Validation RMSLE should be reasonable
        assert val_rmsle < 1.5, \
            f"Validation RMSLE {val_rmsle:.4f} should be < 1.5"

    def test_cross_validation(self, preprocessed_data):
        """Verify cross-validation across max_depth values."""
        X_train, _, y_train, _ = preprocessed_data
        max_depths = list(range(1, 51))

        result = solution.cross_validate_model(
            X_train, y_train, max_depths, cv=5
        )

        # Should return a dict
        assert isinstance(result, dict), \
            f"Should return a dict, got {type(result).__name__}"

        # Should have required keys
        assert 'mean_cv_rmsle' in result, \
            "Result should contain 'mean_cv_rmsle'"
        assert 'best_depth' in result, \
            "Result should contain 'best_depth'"

        mean_cv_rmsle = result['mean_cv_rmsle']
        best_depth = result['best_depth']

        # Should have 50 RMSLE values (depths 1-50)
        assert len(mean_cv_rmsle) == 50, \
            f"Expected 50 mean RMSLE values, got {len(mean_cv_rmsle)}"

        # All RMSLE values should be positive
        for i, val in enumerate(mean_cv_rmsle):
            assert val > 0, \
                f"mean_cv_rmsle[{i}] should be positive, got {val}"

        # best_depth should be an integer in the valid range
        assert isinstance(best_depth, (int, np.integer)), \
            f"best_depth should be int, got {type(best_depth).__name__}"
        assert 1 <= best_depth <= 50, \
            f"best_depth {best_depth} should be in [1, 50]"

        # Depth 1 should underfit compared to best depth
        # (RMSLE at depth=1 should be > RMSLE at best_depth)
        rmsle_depth1 = mean_cv_rmsle[0]
        rmsle_best = mean_cv_rmsle[best_depth - 1]
        assert rmsle_depth1 > rmsle_best, \
            f"RMSLE at depth=1 ({rmsle_depth1:.4f}) should be > " \
            f"RMSLE at best_depth={best_depth} ({rmsle_best:.4f})"


# =============================================================================
# Kaggle Submission
# =============================================================================

class TestKaggle:
    """Verify Kaggle submission format."""

    def test_submission_format(self, preprocessed_data, preprocessed_test_data,
                               holdout_df):
        """Verify submission has correct format with houseid and list_price."""
        from sklearn.linear_model import LinearRegression

        X_train, _, y_train, _ = preprocessed_data
        X_test = preprocessed_test_data
        test_df = holdout_df

        # Train a simple model for prediction
        model = LinearRegression()
        model.fit(X_train, y_train)

        # Generate submission
        with tempfile.NamedTemporaryFile(suffix='.csv', delete=False) as f:
            output_path = f.name

        try:
            result = solution.prepare_kaggle_submission(
                model, X_test, test_df, output_path
            )

            # Should return a DataFrame
            assert isinstance(result, pd.DataFrame), \
                f"Should return a DataFrame, got {type(result).__name__}"

            # Should have exactly 2 columns: houseid and list_price
            assert set(result.columns) == {'houseid', 'list_price'}, \
                f"Should have columns ['houseid', 'list_price'], " \
                f"got {list(result.columns)}"

            # Number of rows should match test data (1,858)
            assert len(result) == len(test_df), \
                f"Should have {len(test_df)} rows, got {len(result)}"

            # All list_price values should be positive
            assert (result['list_price'] > 0).all(), \
                "All predicted list_price values should be positive"

            # houseid values should match test data
            expected_ids = set(test_df['houseid'].values)
            actual_ids = set(result['houseid'].values)
            assert actual_ids == expected_ids, \
                f"houseid values should match test data. " \
                f"Missing: {expected_ids - actual_ids}, " \
                f"Extra: {actual_ids - expected_ids}"

        finally:
            # Clean up temp file
            if os.path.exists(output_path):
                os.unlink(output_path)
