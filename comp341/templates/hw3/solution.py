"""
COMP 341 Homework 3: Stroke Prediction

Predict cerebrovascular accidents (CVAs) using classification methods including
Decision Trees, Logistic Regression, and Linear Discriminant Analysis.

Functions to implement:
- load_cva_data: Load CVA.csv into a DataFrame
- prepare_features_labels: Separate features X and labels y
- impute_missing_values: Mean imputation on numeric columns with missing values
- train_decision_tree: Train a DecisionTreeClassifier and evaluate accuracy
- one_hot_encode: One-hot encode categorical features
- scale_features: StandardScaler on numeric features
- find_optimal_depth: Tune max_depth from 1-20 and find best depth
- get_feature_importances: Extract and rank feature importances from a decision tree
- train_logistic_regression: Train LogisticRegression and evaluate accuracy
- train_lda: Train LinearDiscriminantAnalysis and evaluate accuracy
- compare_regularization: Compare L1, L2, and no regularization for logistic regression
"""

import pandas as pd
import numpy as np
from pathlib import Path
from typing import Union, Dict, List, Tuple, Optional
from sklearn.model_selection import train_test_split
from sklearn.tree import DecisionTreeClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.discriminant_analysis import LinearDiscriminantAnalysis
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.impute import SimpleImputer


# =============================================================================
# Data Loading and Preparation
# =============================================================================

def load_cva_data(data_dir: Union[str, Path]) -> pd.DataFrame:
    """Load CVA.csv into a pandas DataFrame.

    Args:
        data_dir: Path to directory containing CVA.csv

    Returns:
        DataFrame with all columns from the CSV file, including health
        and demographic features along with the CVA label column
    """
    # TODO: Implement this function
    # Hint: Use pd.read_csv() to load data_dir/CVA.csv
    pass


def prepare_features_labels(
    df: pd.DataFrame
) -> Tuple[pd.DataFrame, pd.Series]:
    """Separate features (X) and labels (y) from the DataFrame.

    The label column is 'CVA' (cerebrovascular accident indicator).
    Non-predictive columns (e.g., 'id') should be excluded from X.

    Args:
        df: Full CVA DataFrame with all columns

    Returns:
        Tuple of:
        - X: DataFrame of features (excluding CVA and non-predictive columns)
        - y: Series of labels (the CVA column)
    """
    # TODO: Implement this function
    # Hint: Drop 'CVA' and any non-predictive columns like 'id' from X
    # Hint: y should be the 'CVA' column
    pass


def impute_missing_values(
    X_train: pd.DataFrame,
    X_test: pd.DataFrame
) -> Tuple[pd.DataFrame, pd.DataFrame]:
    """Impute missing values using mean imputation on numeric columns.

    Important: Fit the imputer on X_train only, then transform both
    X_train and X_test. This prevents data leakage from test to train.

    Args:
        X_train: Training features with potential missing values
        X_test: Test features with potential missing values

    Returns:
        Tuple of:
        - X_train with missing numeric values filled by training set means
        - X_test with missing numeric values filled by training set means
    """
    # TODO: Implement this function
    # Hint: Identify numeric columns with missing values
    # Hint: Use SimpleImputer(strategy='mean') or manual fillna
    # Hint: Fit on X_train only, transform both X_train and X_test
    pass


# =============================================================================
# Decision Tree Functions
# =============================================================================

def train_decision_tree(
    X_train: pd.DataFrame,
    y_train: pd.Series,
    X_test: pd.DataFrame,
    y_test: pd.Series,
    max_depth: Optional[int] = None
) -> Tuple[DecisionTreeClassifier, float, float]:
    """Train a DecisionTreeClassifier and evaluate accuracy.

    Args:
        X_train: Training features
        y_train: Training labels
        X_test: Test features
        y_test: Test labels
        max_depth: Maximum depth of the tree (None for unlimited)

    Returns:
        Tuple of:
        - Fitted DecisionTreeClassifier model
        - Training accuracy (float between 0 and 1)
        - Test accuracy (float between 0 and 1)
    """
    # TODO: Implement this function
    # Hint: Create DecisionTreeClassifier with max_depth parameter
    # Hint: Fit on training data, then score on both train and test
    pass


def one_hot_encode(
    X_train: pd.DataFrame,
    X_test: pd.DataFrame
) -> Tuple[pd.DataFrame, pd.DataFrame]:
    """One-hot encode categorical features in the dataset.

    Categorical columns (object/string dtype) should be one-hot encoded.
    Numeric columns should be kept as-is.

    Args:
        X_train: Training features with categorical columns
        X_test: Test features with categorical columns

    Returns:
        Tuple of:
        - X_train with categorical columns replaced by one-hot encoded columns
        - X_test with categorical columns replaced by one-hot encoded columns
    """
    # TODO: Implement this function
    # Hint: Identify categorical columns (object dtype)
    # Hint: Use pd.get_dummies() or OneHotEncoder
    # Hint: Ensure X_train and X_test have the same columns after encoding
    pass


def scale_features(
    X_train: pd.DataFrame,
    X_test: pd.DataFrame
) -> Tuple[pd.DataFrame, pd.DataFrame]:
    """Scale all features using StandardScaler.

    Fit the scaler on X_train only, then transform both X_train and X_test.

    Args:
        X_train: Training features (may include one-hot encoded columns)
        X_test: Test features (may include one-hot encoded columns)

    Returns:
        Tuple of:
        - X_train with numeric features scaled (zero mean, unit variance)
        - X_test with numeric features scaled using training set statistics
    """
    # TODO: Implement this function
    # Hint: Use StandardScaler from sklearn.preprocessing
    # Hint: Fit on X_train numeric columns only, transform both
    # Hint: Preserve DataFrame structure and column names
    pass


def find_optimal_depth(
    X_train: pd.DataFrame,
    y_train: pd.Series,
    X_test: pd.DataFrame,
    y_test: pd.Series,
    max_depths: List[int] = list(range(1, 21))
) -> Dict:
    """Find the optimal max_depth for a DecisionTreeClassifier.

    Train a decision tree for each max_depth value and record train/test
    accuracy. The best depth is the one with highest test accuracy.

    Args:
        X_train: Training features
        y_train: Training labels
        X_test: Test features
        y_test: Test labels
        max_depths: Range of max_depth values to try (default: 1 to 20)

    Returns:
        Dict with keys:
        - 'train_accuracies': list of training accuracies for each depth
        - 'test_accuracies': list of test accuracies for each depth
        - 'best_depth': int, the max_depth with highest test accuracy
    """
    # TODO: Implement this function
    # Hint: Loop over max_depths, train a tree for each, record accuracies
    # Hint: Use train_decision_tree() or train directly
    # Hint: best_depth = max_depths[argmax(test_accuracies)]
    pass


def get_feature_importances(
    model: DecisionTreeClassifier,
    feature_names: List[str]
) -> pd.DataFrame:
    """Extract feature importances from a fitted decision tree.

    Args:
        model: Fitted DecisionTreeClassifier
        feature_names: List of feature names corresponding to model's features

    Returns:
        DataFrame with columns 'feature' and 'importance',
        sorted by importance in descending order
    """
    # TODO: Implement this function
    # Hint: Use model.feature_importances_ attribute
    # Hint: Create DataFrame and sort by importance descending
    pass


# =============================================================================
# Logistic Regression Functions
# =============================================================================

def train_logistic_regression(
    X_train: pd.DataFrame,
    y_train: pd.Series,
    X_test: pd.DataFrame,
    y_test: pd.Series,
    penalty: str = 'l2',
    solver: str = 'lbfgs',
    max_iter: int = 1000
) -> Tuple[LogisticRegression, float, float]:
    """Train a LogisticRegression model and evaluate accuracy.

    Args:
        X_train: Training features
        y_train: Training labels
        X_test: Test features
        y_test: Test labels
        penalty: Regularization penalty ('l1', 'l2', or None)
        solver: Optimization solver ('lbfgs', 'liblinear', etc.)
        max_iter: Maximum number of iterations for convergence

    Returns:
        Tuple of:
        - Fitted LogisticRegression model
        - Training accuracy (float between 0 and 1)
        - Test accuracy (float between 0 and 1)
    """
    # TODO: Implement this function
    # Hint: Create LogisticRegression with given penalty, solver, max_iter
    # Hint: Fit on training data, score on both train and test
    pass


def compare_regularization(
    X_train: pd.DataFrame,
    y_train: pd.Series,
    X_test: pd.DataFrame,
    y_test: pd.Series
) -> Dict[str, Tuple[LogisticRegression, float, float]]:
    """Compare L1, L2, and no regularization for logistic regression.

    Trains three logistic regression models:
    - 'L1': liblinear solver with L1 penalty
    - 'L2': liblinear solver with L2 penalty
    - 'none': lbfgs solver with no penalty

    Args:
        X_train: Training features (should be scaled)
        y_train: Training labels
        X_test: Test features (should be scaled)
        y_test: Test labels

    Returns:
        Dict mapping regularization name to tuple of:
        - Fitted LogisticRegression model
        - Training accuracy
        - Test accuracy

        Keys are 'L1', 'L2', 'none'
    """
    # TODO: Implement this function
    # Hint: Use train_logistic_regression() with different parameters
    # Hint: L1 -> solver='liblinear', penalty='l1'
    # Hint: L2 -> solver='liblinear', penalty='l2'
    # Hint: none -> solver='lbfgs', penalty=None
    pass


# =============================================================================
# Linear Discriminant Analysis Functions
# =============================================================================

def train_lda(
    X_train: pd.DataFrame,
    y_train: pd.Series,
    X_test: pd.DataFrame,
    y_test: pd.Series
) -> Tuple[LinearDiscriminantAnalysis, float, float]:
    """Train a Linear Discriminant Analysis model and evaluate accuracy.

    Uses default LDA settings.

    Args:
        X_train: Training features (should be scaled, one-hot encoded)
        y_train: Training labels
        X_test: Test features (should be scaled, one-hot encoded)
        y_test: Test labels

    Returns:
        Tuple of:
        - Fitted LinearDiscriminantAnalysis model
        - Training accuracy (float between 0 and 1)
        - Test accuracy (float between 0 and 1)
    """
    # TODO: Implement this function
    # Hint: Create LinearDiscriminantAnalysis with default settings
    # Hint: Fit on training data, score on both train and test
    pass
