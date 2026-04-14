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

    def test_load_data(self, housing_df):
        """Verify housing data loads as DataFrame with expected structure."""
        # Must be a DataFrame
        assert isinstance(housing_df, pd.DataFrame), \
            "Should return a pandas DataFrame"

        # Row count: ~5,575 homes
        row_count = len(housing_df)
        assert 5000 < row_count < 6000, \
            f"Expected ~5,575 rows, got {row_count:,}"

        # Required columns
        actual_cols = set(housing_df.columns)
        missing = EXPECTED_COLS - actual_cols
        assert not missing, f"Missing columns: {missing}"

        # list_price should be numeric and positive
        assert pd.api.types.is_numeric_dtype(housing_df['list_price']), \
            "list_price should be numeric"
        assert (housing_df['list_price'] > 0).all(), \
            "All list_price values should be positive"

        # property_type should have expected values
        expected_types = {'Single Family Residential', 'Townhouse', 'Condo/Co-op'}
        actual_types = set(housing_df['property_type'].dropna().unique())
        assert actual_types.issubset(expected_types), \
            f"Unexpected property_type values: {actual_types - expected_types}"
        assert len(actual_types) >= 2, \
            f"Expected at least 2 property types, got {len(actual_types)}"


# =============================================================================
# Missing Values
# =============================================================================

class TestMissingValues:
    """Verify missing values check."""

    def test_missing_values_check(self, housing_df):
        """Verify check_missing_values returns correctly structured result."""
        result = solution.check_missing_values(housing_df)

        # Should return a Series
        assert isinstance(result, pd.Series), \
            f"Should return a pd.Series, got {type(result).__name__}"

        # All values should be > 0 (filtered to only missing columns)
        assert (result > 0).all(), \
            "All values should be > 0 (only columns with missing values)"

        # Should include known high-missing columns
        assert 'hoa' in result.index, \
            "Should include 'hoa' (known high-missing column, ~36%)"
        assert 'lot_size' in result.index, \
            "Should include 'lot_size' (known missing column, ~12%)"

        # 'hoa' should have the most missing values (~2030)
        assert result.iloc[0] == result['hoa'] or result['hoa'] >= 1500, \
            f"'hoa' should have many missing values (~2030), got {result['hoa']}"

        # Should be sorted descending
        values = result.values
        assert all(values[i] >= values[i + 1] for i in range(len(values) - 1)), \
            "Missing value counts should be sorted in descending order"

        # Should have at least 4 columns with missing values
        assert len(result) >= 4, \
            f"Expected at least 4 columns with missing values, got {len(result)}"


# =============================================================================
# Error Metrics
# =============================================================================

class TestMetrics:
    """Verify RMSE and RMSLE calculations."""

    def test_rmse_calculation(self):
        """Verify RMSE calculation with known values."""
        # Test 1: Known error
        y_true = np.array([100, 200, 300])
        y_pred = np.array([110, 190, 310])
        rmse = solution.calculate_rmse(y_true, y_pred)

        assert isinstance(rmse, (int, float, np.floating)), \
            f"RMSE should be a number, got {type(rmse).__name__}"
        # sqrt(mean([100, 100, 100])) = sqrt(100) = 10.0
        assert abs(rmse - 10.0) < 0.01, \
            f"RMSE of [100,200,300] vs [110,190,310] should be 10.0, got {rmse:.4f}"

        # Test 2: Identical arrays -> RMSE = 0
        y_same = np.array([1.0, 2.0, 3.0])
        rmse_zero = solution.calculate_rmse(y_same, y_same)
        assert abs(rmse_zero) < 1e-10, \
            f"RMSE of identical arrays should be 0.0, got {rmse_zero}"

        # Test 3: RMSE should be non-negative
        assert rmse >= 0, "RMSE should be non-negative"

