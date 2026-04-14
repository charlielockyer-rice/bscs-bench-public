"""
COMP 341 Homework 3: Stroke Prediction - Test Suite

Tests organized by tier:
- Tier 1: Data loading and preparation (must pass)
- Tier 2: Decision tree classification
- Tier 3: Logistic regression and regularization
- Tier 4: Linear Discriminant Analysis
"""

import pytest
import pandas as pd
import numpy as np
import os
import sys
from pathlib import Path

# =============================================================================
# Test Configuration
# =============================================================================

# Get paths from environment or use defaults
DATA_DIR = Path(os.environ.get('COMP341_DATA_DIR',
    Path(__file__).parent.parent / 'data')) / 'hw3'
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

# Columns that should NOT be used as predictive features
NON_PREDICTIVE_COLS = {'id', 'CVA'}

# Known categorical columns in the CVA dataset
CATEGORICAL_COLS = {'sex', 'ever_married', 'work_type', 'residence_type',
                    'smoking_status'}

# Known numeric columns (continuous-valued)
NUMERIC_COLS = {'age', 'avg_glucose_level', 'bmi'}


# =============================================================================
# Fixtures
# =============================================================================

@pytest.fixture(scope="module")
def cva_df():
    """Load CVA data once for all tests."""
    if not SOLUTION_AVAILABLE:
        pytest.skip("solution.py not available")
    df = solution.load_cva_data(DATA_DIR)
    return df


@pytest.fixture(scope="module")
def features_labels(cva_df):
    """Split into features (X) and labels (y)."""
    X, y = solution.prepare_features_labels(cva_df)
    return X, y


@pytest.fixture(scope="module")
def train_test_data(features_labels):
    """Create train/test split matching the assignment specification."""
    from sklearn.model_selection import train_test_split
    X, y = features_labels
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.40, random_state=6
    )
    return X_train, X_test, y_train, y_test


@pytest.fixture(scope="module")
def imputed_data(train_test_data):
    """Impute missing values in training and test sets."""
    X_train, X_test, y_train, y_test = train_test_data
    X_train_imp, X_test_imp = solution.impute_missing_values(
        X_train.copy(), X_test.copy()
    )
    return X_train_imp, X_test_imp, y_train, y_test


@pytest.fixture(scope="module")
def encoded_data(imputed_data):
    """One-hot encode categorical features."""
    X_train_imp, X_test_imp, y_train, y_test = imputed_data
    X_train_enc, X_test_enc = solution.one_hot_encode(
        X_train_imp.copy(), X_test_imp.copy()
    )
    return X_train_enc, X_test_enc, y_train, y_test


@pytest.fixture(scope="module")
def scaled_data(encoded_data):
    """Scale features after encoding."""
    X_train_enc, X_test_enc, y_train, y_test = encoded_data
    X_train_sc, X_test_sc = solution.scale_features(
        X_train_enc.copy(), X_test_enc.copy()
    )
    return X_train_sc, X_test_sc, y_train, y_test


@pytest.fixture(scope="module")
def numeric_only_data(imputed_data):
    """Extract only numeric columns from imputed data for numeric-only tests."""
    X_train_imp, X_test_imp, y_train, y_test = imputed_data
    # Select only numeric dtype columns
    numeric_train = X_train_imp.select_dtypes(include=[np.number])
    numeric_test = X_test_imp.select_dtypes(include=[np.number])
    return numeric_train, numeric_test, y_train, y_test


# =============================================================================
# Tier 1: Data Loading and Preparation
# =============================================================================

class TestDataLoading:
    """Tier 1: Verify CVA data loads correctly."""

    def test_load_data(self, cva_df):
        """Verify CVA data loads as DataFrame with expected structure."""
        # Must be a DataFrame
        assert isinstance(cva_df, pd.DataFrame), "Should return a pandas DataFrame"

        # Row count: ~5000 individuals
        row_count = len(cva_df)
        assert 4000 < row_count < 6000, \
            f"Expected ~5000 rows, got {row_count:,}"

        # Required columns
        expected_cols = {
            'id', 'sex', 'age', 'hypertension', 'heart_disease',
            'ever_married', 'work_type', 'residence_type',
            'avg_glucose_level', 'bmi', 'smoking_status', 'CVA'
        }
        actual_cols = set(cva_df.columns)
        missing = expected_cols - actual_cols
        assert not missing, f"Missing columns: {missing}"

        # CVA column should be binary (0 and 1)
        cva_values = set(cva_df['CVA'].dropna().unique())
        assert cva_values.issubset({0, 1}), \
            f"CVA should be binary (0/1), got unique values: {cva_values}"

        # Numeric columns should have correct types
        for col in ['age', 'avg_glucose_level']:
            assert pd.api.types.is_numeric_dtype(cva_df[col]), \
                f"{col} should be numeric"


class TestFeatureLabelSplit:
    """Tier 1: Verify feature/label separation."""

    def test_feature_label_split(self, features_labels, cva_df):
        """Verify X excludes CVA and non-predictive columns, y is CVA."""
        X, y = features_labels

        # Both should be pandas objects
        assert isinstance(X, pd.DataFrame), "X should be a DataFrame"
        assert isinstance(y, (pd.Series, pd.DataFrame, np.ndarray)), \
            "y should be a Series, DataFrame, or array"

        # X should not contain CVA
        assert 'CVA' not in X.columns, "X should not contain the target column 'CVA'"

        # X should not contain id (non-predictive)
        assert 'id' not in X.columns, "X should not contain 'id' (non-predictive)"

        # y should correspond to CVA values
        y_array = np.asarray(y).flatten()
        assert set(np.unique(y_array)).issubset({0, 1}), \
            "y should contain only 0 and 1 (CVA labels)"

        # Row counts should match
        assert len(X) == len(cva_df), \
            f"X row count ({len(X)}) should match original ({len(cva_df)})"
        assert len(y_array) == len(cva_df), \
            f"y length ({len(y_array)}) should match original ({len(cva_df)})"

        # X should have multiple feature columns
        assert X.shape[1] >= 5, \
            f"X should have multiple feature columns, got {X.shape[1]}"


# =============================================================================
# Tier 2: Decision Tree Classification
# =============================================================================

class TestImputation:
    """Tier 2: Verify missing value imputation."""

    def test_imputation(self, imputed_data, train_test_data):
        """Verify imputation removes NaN and preserves shape."""
        X_train_imp, X_test_imp, _, _ = imputed_data
        X_train_orig, X_test_orig, _, _ = train_test_data

        # Select only numeric columns for NaN check (categorical may still have NaN)
        numeric_train = X_train_imp.select_dtypes(include=[np.number])
        numeric_test = X_test_imp.select_dtypes(include=[np.number])

        # No NaN values in numeric columns after imputation
        train_nans = numeric_train.isna().sum().sum()
        test_nans = numeric_test.isna().sum().sum()
        assert train_nans == 0, \
            f"Training numeric features should have no NaN after imputation, got {train_nans}"
        assert test_nans == 0, \
            f"Test numeric features should have no NaN after imputation, got {test_nans}"

        # Shape should be preserved
        assert X_train_imp.shape[0] == X_train_orig.shape[0], \
            "Imputation should not change number of training rows"
        assert X_test_imp.shape[0] == X_test_orig.shape[0], \
            "Imputation should not change number of test rows"

        # Columns should be preserved
        assert set(X_train_imp.columns) == set(X_train_orig.columns), \
            "Imputation should not change column names"


class TestDecisionTree:
    """Tier 2: Verify decision tree training."""

