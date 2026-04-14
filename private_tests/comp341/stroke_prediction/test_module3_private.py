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

    def test_decision_tree_numeric(self, numeric_only_data):
        """Verify decision tree on numeric features only."""
        from sklearn.tree import DecisionTreeClassifier

        X_train, X_test, y_train, y_test = numeric_only_data
        result = solution.train_decision_tree(X_train, y_train, X_test, y_test)

        # Should return (model, train_acc, test_acc)
        assert len(result) == 3, \
            f"Should return (model, train_acc, test_acc), got {len(result)} values"

        model, train_acc, test_acc = result

        # Model should be a DecisionTreeClassifier
        assert isinstance(model, DecisionTreeClassifier), \
            f"Model should be DecisionTreeClassifier, got {type(model).__name__}"

        # Accuracies should be reasonable floats in [0, 1]
        assert isinstance(train_acc, (int, float, np.floating)), \
            "train_acc should be a number"
        assert isinstance(test_acc, (int, float, np.floating)), \
            "test_acc should be a number"
        assert 0.85 <= train_acc <= 1.0, \
            f"Training accuracy {train_acc:.3f} should be in [0.85, 1.0]"
        assert 0.85 <= test_acc <= 1.0, \
            f"Test accuracy {test_acc:.3f} should be in [0.85, 1.0]"


class TestOneHotEncoding:
    """Tier 2: Verify one-hot encoding of categorical features."""

    def test_one_hot_encoding(self, encoded_data, imputed_data):
        """Verify categorical columns are one-hot encoded."""
        X_train_enc, X_test_enc, _, _ = encoded_data
        X_train_imp, X_test_imp, _, _ = imputed_data

        # Should return DataFrames (or arrays with more columns)
        assert hasattr(X_train_enc, 'shape'), "Encoded train data should have shape"
        assert hasattr(X_test_enc, 'shape'), "Encoded test data should have shape"

        # Row count should be preserved
        assert X_train_enc.shape[0] == X_train_imp.shape[0], \
            "Encoding should not change number of training rows"
        assert X_test_enc.shape[0] == X_test_imp.shape[0], \
            "Encoding should not change number of test rows"

        # Column count should increase (categorical expanded)
        assert X_train_enc.shape[1] > X_train_imp.shape[1], \
            "One-hot encoding should increase the number of columns"

        # If returned as DataFrame, original categorical column names should be gone
        if isinstance(X_train_enc, pd.DataFrame):
            remaining_cat = CATEGORICAL_COLS & set(X_train_enc.columns)
            assert len(remaining_cat) == 0, \
                f"Original categorical columns should be replaced: {remaining_cat}"

        # All values should be numeric after encoding
        if isinstance(X_train_enc, pd.DataFrame):
            for col in X_train_enc.columns:
                assert pd.api.types.is_numeric_dtype(X_train_enc[col]), \
                    f"Column '{col}' should be numeric after encoding"


class TestDecisionTreeAllFeatures:
    """Tier 2: Verify decision tree with all features."""

    def test_decision_tree_all_features(self, encoded_data):
        """Verify decision tree with encoded (all) features."""
        from sklearn.tree import DecisionTreeClassifier

        X_train_enc, X_test_enc, y_train, y_test = encoded_data
        result = solution.train_decision_tree(X_train_enc, y_train, X_test_enc, y_test)

        assert len(result) == 3, \
            f"Should return (model, train_acc, test_acc), got {len(result)} values"

        model, train_acc, test_acc = result

        # Model should be a DecisionTreeClassifier
        assert isinstance(model, DecisionTreeClassifier), \
            f"Model should be DecisionTreeClassifier, got {type(model).__name__}"

        # Accuracies should be reasonable (CVA dataset is imbalanced, ~95% negative)
        assert 0.85 <= train_acc <= 1.0, \
            f"Training accuracy {train_acc:.3f} should be in [0.85, 1.0]"
        assert 0.85 <= test_acc <= 1.0, \
            f"Test accuracy {test_acc:.3f} should be in [0.85, 1.0]"


class TestScaling:
    """Tier 2: Verify feature scaling."""

    def test_scaling_effect(self, scaled_data, encoded_data):
        """Verify scaled features have mean ~0 and std ~1."""
        X_train_sc, X_test_sc, _, _ = scaled_data
        X_train_enc, X_test_enc, _, _ = encoded_data

        # Shape should be preserved
        assert X_train_sc.shape == X_train_enc.shape, \
            "Scaling should not change shape"

        # Convert to numpy for consistent checking
        if isinstance(X_train_sc, pd.DataFrame):
            train_values = X_train_sc.values
        else:
            train_values = np.asarray(X_train_sc)

        # Scaled training features should have mean ~0 and std ~1
        col_means = np.nanmean(train_values, axis=0)
        col_stds = np.nanstd(train_values, axis=0)

        # Allow tolerance for binary/one-hot columns which won't perfectly center
        mean_close = np.abs(col_means) < 0.5
        assert mean_close.mean() >= 0.5, \
            "At least half of scaled feature means should be close to 0"

        # At least some columns should have std near 1
        std_near_one = np.abs(col_stds - 1.0) < 0.5
        assert std_near_one.sum() >= 2, \
            "At least 2 scaled features should have std near 1.0"


class TestOptimalDepth:
    """Tier 2: Verify optimal depth search."""

    def test_optimal_depth_range(self, scaled_data):
        """Verify find_optimal_depth returns correct structure."""
        X_train_sc, X_test_sc, y_train, y_test = scaled_data
        max_depths = list(range(1, 21))

        result = solution.find_optimal_depth(
            X_train_sc, y_train, X_test_sc, y_test, max_depths
        )

        # Should return a dict
        assert isinstance(result, dict), \
            f"Should return a dict, got {type(result).__name__}"

        # Should have train_accuracies and test_accuracies
        assert 'train_accuracies' in result, \
            "Result should contain 'train_accuracies'"
        assert 'test_accuracies' in result, \
            "Result should contain 'test_accuracies'"
        assert 'best_depth' in result, \
            "Result should contain 'best_depth'"

        train_accs = result['train_accuracies']
        test_accs = result['test_accuracies']
        best_depth = result['best_depth']

        # Should have 20 accuracy values (depths 1-20)
        assert len(train_accs) == 20, \
            f"Expected 20 train accuracies, got {len(train_accs)}"
        assert len(test_accs) == 20, \
            f"Expected 20 test accuracies, got {len(test_accs)}"

        # All accuracies should be in [0, 1]
        for acc in train_accs:
            assert 0.0 <= acc <= 1.0, \
                f"Train accuracy {acc} out of range [0, 1]"
        for acc in test_accs:
            assert 0.0 <= acc <= 1.0, \
                f"Test accuracy {acc} out of range [0, 1]"

        # Training accuracy should generally increase with depth
        assert train_accs[-1] >= train_accs[0], \
            "Training accuracy at max depth should be >= accuracy at depth 1"

        # best_depth should be an integer in the valid range
        assert isinstance(best_depth, (int, np.integer)), \
            f"best_depth should be int, got {type(best_depth).__name__}"
        assert 1 <= best_depth <= 20, \
            f"best_depth {best_depth} should be in [1, 20]"


class TestFeatureImportances:
    """Tier 2: Verify feature importance extraction."""

    def test_feature_importances(self, scaled_data):
        """Verify feature importances from a decision tree model."""
        X_train_sc, X_test_sc, y_train, y_test = scaled_data

        # First, train a model to get importances from
        # Use the find_optimal_depth to get a tuned model, or train directly
        from sklearn.tree import DecisionTreeClassifier

        # Get feature names
        if isinstance(X_train_sc, pd.DataFrame):
            feature_names = list(X_train_sc.columns)
        else:
            feature_names = [f"feature_{i}" for i in range(X_train_sc.shape[1])]

        # Train a tree with a reasonable depth to extract importances
        model, _, _ = solution.train_decision_tree(
            X_train_sc, y_train, X_test_sc, y_test, max_depth=5
        )

        result = solution.get_feature_importances(model, feature_names)

        # Should return a DataFrame
        assert isinstance(result, pd.DataFrame), \
            f"Should return a DataFrame, got {type(result).__name__}"

        # Should have feature and importance columns
        col_names_lower = [c.lower() for c in result.columns]
        assert any('feature' in c for c in col_names_lower), \
            f"Should have a 'feature' column, got columns: {list(result.columns)}"
        assert any('importance' in c for c in col_names_lower), \
            f"Should have an 'importance' column, got columns: {list(result.columns)}"

        # Identify the importance column
        importance_col = None
        for c in result.columns:
            if 'importance' in c.lower():
                importance_col = c
                break

        importances = result[importance_col].values

        # Importances should sum to approximately 1.0
        total = np.sum(importances)
        assert abs(total - 1.0) < 0.05, \
            f"Feature importances should sum to ~1.0, got {total:.4f}"

        # All importances should be non-negative
        assert (importances >= 0).all(), \
            "All feature importances should be non-negative"

        # Should have the right number of features
        assert len(result) == len(feature_names), \
            f"Expected {len(feature_names)} features, got {len(result)}"

        # Should be sorted descending by importance
        assert all(importances[i] >= importances[i + 1]
                    for i in range(len(importances) - 1)), \
            "Feature importances should be sorted in descending order"


# =============================================================================
# Tier 3: Logistic Regression and Regularization
# =============================================================================

class TestLogisticRegression:
    """Tier 3: Verify logistic regression training."""

    def test_logistic_regression(self, scaled_data):
        """Verify logistic regression with default parameters."""
        from sklearn.linear_model import LogisticRegression

        X_train_sc, X_test_sc, y_train, y_test = scaled_data
        result = solution.train_logistic_regression(
            X_train_sc, y_train, X_test_sc, y_test
        )

        # Should return (model, train_acc, test_acc)
        assert len(result) == 3, \
            f"Should return (model, train_acc, test_acc), got {len(result)} values"

        model, train_acc, test_acc = result

        # Model should be a LogisticRegression
        assert isinstance(model, LogisticRegression), \
            f"Model should be LogisticRegression, got {type(model).__name__}"

        # Accuracies should be reasonable
        assert isinstance(train_acc, (int, float, np.floating)), \
            "train_acc should be a number"
        assert isinstance(test_acc, (int, float, np.floating)), \
            "test_acc should be a number"
        assert 0.7 <= train_acc <= 1.0, \
            f"Training accuracy {train_acc:.3f} should be in [0.7, 1.0]"
        assert 0.7 <= test_acc <= 1.0, \
            f"Test accuracy {test_acc:.3f} should be in [0.7, 1.0]"

        # Model should have coefficients
        assert hasattr(model, 'coef_'), "Model should have coef_ attribute"


class TestLogisticRegularization:
    """Tier 3: Verify regularization comparison."""

    def test_logistic_regularization(self, scaled_data):
        """Verify compare_regularization returns results for L1, L2, and none."""
        X_train_sc, X_test_sc, y_train, y_test = scaled_data

        result = solution.compare_regularization(
            X_train_sc, y_train, X_test_sc, y_test
        )

        # Should return a dict
        assert isinstance(result, dict), \
            f"Should return a dict, got {type(result).__name__}"

        # Should have at least 3 entries (L1, L2, and no regularization)
        assert len(result) >= 3, \
            f"Should have at least 3 regularization methods, got {len(result)}"

        # Check that keys suggest L1, L2, and no regularization
        keys_lower = [k.lower() for k in result.keys()]
        has_l1 = any('l1' in k for k in keys_lower)
        has_l2 = any('l2' in k for k in keys_lower)
        has_none = any('none' in k or 'no' in k for k in keys_lower)

        assert has_l1, \
            f"Should include L1 regularization, got keys: {list(result.keys())}"
        assert has_l2, \
            f"Should include L2 regularization, got keys: {list(result.keys())}"
        assert has_none, \
            f"Should include no regularization, got keys: {list(result.keys())}"

        # Each entry should have (model, train_acc, test_acc)
        for name, entry in result.items():
            assert len(entry) == 3, \
                f"Entry '{name}' should have (model, train_acc, test_acc), got {len(entry)} values"

            model, train_acc, test_acc = entry

            # Model should have coef_ attribute
            assert hasattr(model, 'coef_'), \
                f"Model for '{name}' should have coef_ attribute"

            # Accuracies should be reasonable
            assert 0.7 <= train_acc <= 1.0, \
                f"'{name}' train accuracy {train_acc:.3f} should be in [0.7, 1.0]"
            assert 0.7 <= test_acc <= 1.0, \
                f"'{name}' test accuracy {test_acc:.3f} should be in [0.7, 1.0]"


# =============================================================================
# Tier 4: Linear Discriminant Analysis
# =============================================================================

class TestLDA:
    """Tier 4: Verify Linear Discriminant Analysis."""

    def test_lda_accuracy(self, scaled_data):
        """Verify LDA returns model with reasonable accuracy."""
        from sklearn.discriminant_analysis import LinearDiscriminantAnalysis

        X_train_sc, X_test_sc, y_train, y_test = scaled_data
        result = solution.train_lda(X_train_sc, y_train, X_test_sc, y_test)

        # Should return (model, train_acc, test_acc)
        assert len(result) == 3, \
            f"Should return (model, train_acc, test_acc), got {len(result)} values"

        model, train_acc, test_acc = result

        # Model should be LDA
        assert isinstance(model, LinearDiscriminantAnalysis), \
            f"Model should be LinearDiscriminantAnalysis, got {type(model).__name__}"

        # Accuracies should be reasonable
        assert isinstance(train_acc, (int, float, np.floating)), \
            "train_acc should be a number"
        assert isinstance(test_acc, (int, float, np.floating)), \
            "test_acc should be a number"
        assert 0.7 <= train_acc <= 1.0, \
            f"Training accuracy {train_acc:.3f} should be in [0.7, 1.0]"
        assert 0.7 <= test_acc <= 1.0, \
            f"Test accuracy {test_acc:.3f} should be in [0.7, 1.0]"

    def test_lda_coefficients(self, scaled_data):
        """Verify LDA model has coefficients with correct shape."""
        X_train_sc, X_test_sc, y_train, y_test = scaled_data
        result = solution.train_lda(X_train_sc, y_train, X_test_sc, y_test)

        model, _, _ = result

        # Model should have coef_ attribute
        assert hasattr(model, 'coef_'), \
            "LDA model should have coef_ attribute"

        coef = model.coef_

        # Coefficients shape: (n_classes - 1, n_features) for binary = (1, n_features)
        n_features = X_train_sc.shape[1]
        assert coef.shape[-1] == n_features, \
            f"Coefficients should have {n_features} features, got shape {coef.shape}"

        # For binary classification, should have 1 row of coefficients
        assert coef.shape[0] == 1, \
            f"Binary LDA should have 1 set of coefficients, got {coef.shape[0]}"

        # Coefficients should not all be zero
        assert not np.allclose(coef, 0), \
            "LDA coefficients should not all be zero"
