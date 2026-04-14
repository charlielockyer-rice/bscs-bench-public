"""
COMP 341 Homework 4: Houston Home Price Prediction

Predict list prices of Houston homes using regression methods including
Linear Regression, Lasso, SVR, and Decision Tree Regressor with cross-validation.

Functions to implement:
- load_housing_data: Load houston_homes.csv into a DataFrame
- check_missing_values: Check for missing values in the DataFrame
- calculate_rmse: Calculate Root Mean Squared Error
- calculate_rmsle: Calculate Root Mean Squared Logarithmic Error
- baseline_heuristic: Generate baseline predictions using mean of training targets
- train_linear_regression: Train LinearRegression and evaluate using RMSE
- train_lasso_regression: Train Lasso regression and evaluate using RMSLE
- tune_lasso_alpha: Tune Lasso alpha hyperparameter
- train_svr: Train SVR with given kernel and evaluate using RMSLE
- train_decision_tree_regressor: Train DecisionTreeRegressor and evaluate using RMSLE
- cross_validate_model: 5-fold cross-validation for DecisionTreeRegressor across max_depths
- prepare_kaggle_submission: Generate Kaggle submission predictions
"""

import pandas as pd
import numpy as np
from pathlib import Path
from typing import Union, Dict, List, Tuple, Optional
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.linear_model import LinearRegression, Lasso
from sklearn.svm import SVR
from sklearn.tree import DecisionTreeRegressor
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import mean_squared_error, mean_squared_log_error


# =============================================================================
# Data Loading and Exploration
# =============================================================================

def load_housing_data(data_dir: Union[str, Path]) -> pd.DataFrame:
    """Load houston_homes.csv into a pandas DataFrame.

    Args:
        data_dir: Path to directory containing houston_homes.csv

    Returns:
        DataFrame with all columns from the CSV file, including home
        features and the list_price target column
    """
    # TODO: Implement this function
    # Hint: Use pd.read_csv() to load data_dir/houston_homes.csv
    pass


def check_missing_values(df: pd.DataFrame) -> pd.Series:
    """Check for missing values in the DataFrame.

    Args:
        df: DataFrame to check for missing values

    Returns:
        Series with feature names as index and NaN counts as values,
        filtered to features with > 0 missing values, sorted descending
        by count
    """
    # TODO: Implement this function
    # Hint: Use df.isnull().sum(), filter to > 0, sort descending
    pass


# =============================================================================
# Error Metrics
# =============================================================================

def calculate_rmse(y_true: np.ndarray, y_pred: np.ndarray) -> float:
    """Calculate Root Mean Squared Error.

    RMSE = sqrt(mean((y_true - y_pred)^2))

    Args:
        y_true: Array of true target values
        y_pred: Array of predicted values

    Returns:
        RMSE as a float
    """
    # TODO: Implement this function
    # Hint: Use sklearn.metrics.mean_squared_error with squared=False,
    # Hint: or compute manually with np.sqrt(np.mean((y_true - y_pred)**2))
    pass


def calculate_rmsle(y_true: np.ndarray, y_pred: np.ndarray) -> float:
    """Calculate Root Mean Squared Logarithmic Error.

    RMSLE = sqrt(mean((log(1 + y_true) - log(1 + y_pred))^2))

    Note: Both y_true and y_pred must be non-negative.

    Args:
        y_true: Array of true target values (non-negative)
        y_pred: Array of predicted values (non-negative)

    Returns:
        RMSLE as a float
    """
    # TODO: Implement this function
    # Hint: Use np.log1p for log(1+x), or sklearn.metrics.mean_squared_log_error
    # Hint: then take the square root
    pass


# =============================================================================
# Baseline
# =============================================================================

def baseline_heuristic(
    y_train: pd.Series,
    n_predictions: int
) -> np.ndarray:
    """Generate baseline predictions using mean of training targets.

    Args:
        y_train: Training target values
        n_predictions: Number of predictions to generate

    Returns:
        Array of length n_predictions, all equal to mean(y_train)
    """
    # TODO: Implement this function
    # Hint: np.full(n_predictions, y_train.mean())
    pass


# =============================================================================
# Regression Models
# =============================================================================

def train_linear_regression(
    X_train: pd.DataFrame,
    y_train: pd.Series,
    X_val: pd.DataFrame,
    y_val: pd.Series
) -> Tuple[LinearRegression, float, float]:
    """Train a LinearRegression model and evaluate using RMSE.

    Args:
        X_train: Training features
        y_train: Training target values
        X_val: Validation features
        y_val: Validation target values

    Returns:
        Tuple of:
        - Fitted LinearRegression model
        - Training RMSE (float)
        - Validation RMSE (float)
    """
    # TODO: Implement this function
    # Hint: Use LinearRegression().fit(), then predict and calculate_rmse
    pass


def train_lasso_regression(
    X_train: pd.DataFrame,
    y_train: pd.Series,
    X_val: pd.DataFrame,
    y_val: pd.Series,
    alpha: float = 1.0
) -> Tuple[Lasso, float, float]:
    """Train a Lasso regression model and evaluate using RMSLE.

    Args:
        X_train: Training features
        y_train: Training target values
        X_val: Validation features
        y_val: Validation target values
        alpha: Regularization strength (default: 1.0)

    Returns:
        Tuple of:
        - Fitted Lasso model
        - Training RMSLE (float)
        - Validation RMSLE (float)
    """
    # TODO: Implement this function
    # Hint: Use Lasso(alpha=alpha).fit()
    pass


def tune_lasso_alpha(
    X_train: pd.DataFrame,
    y_train: pd.Series,
    X_val: pd.DataFrame,
    y_val: pd.Series,
    alphas: np.ndarray = None
) -> Dict:
    """Tune Lasso alpha hyperparameter.

    Trains a Lasso model for each alpha value and records training and
    validation RMSLE. The best alpha is the one with lowest validation RMSLE.

    Args:
        X_train: Training features
        y_train: Training target values
        X_val: Validation features
        y_val: Validation target values
        alphas: Array of alpha values to try. If None, uses default range:
            np.concatenate([np.arange(0,100,5), np.arange(100,1000,100),
            np.arange(1000,20000,1000)])

    Returns:
        Dict with keys:
        - 'train_rmsles': list of training RMSLE for each alpha
        - 'val_rmsles': list of validation RMSLE for each alpha
        - 'best_alpha': float, the alpha with lowest validation RMSLE
    """
    # TODO: Implement this function
    # Hint: Loop over alphas, call train_lasso_regression for each
    # Hint: best_alpha = alphas[argmin(val_rmsles)]
    pass


def train_svr(
    X_train: pd.DataFrame,
    y_train: pd.Series,
    X_val: pd.DataFrame,
    y_val: pd.Series,
    kernel: str = 'rbf',
    C: float = 100
) -> Tuple[SVR, float, float]:
    """Train an SVR model with given kernel and evaluate using RMSLE.

    Important: X_train and X_val should already be scaled (e.g., with
    StandardScaler) before being passed to this function. SVR performs
    poorly on unscaled features.

    Args:
        X_train: Training features (should be pre-scaled)
        y_train: Training target values
        X_val: Validation features (should be pre-scaled)
        y_val: Validation target values
        kernel: SVR kernel type ('rbf', 'linear', 'poly')
        C: Regularization parameter (default: 100)

    Returns:
        Tuple of:
        - Fitted SVR model
        - Training RMSLE (float)
        - Validation RMSLE (float)
    """
    # TODO: Implement this function
    # Hint: Use SVR(kernel=kernel, C=C).fit()
    # Hint: X_train and X_val should already be scaled before calling this function
    pass


def train_decision_tree_regressor(
    X_train: pd.DataFrame,
    y_train: pd.Series,
    X_val: pd.DataFrame,
    y_val: pd.Series,
    max_depth: Optional[int] = None
) -> Tuple[DecisionTreeRegressor, float, float]:
    """Train a DecisionTreeRegressor and evaluate using RMSLE.

    Args:
        X_train: Training features
        y_train: Training target values
        X_val: Validation features
        y_val: Validation target values
        max_depth: Maximum depth of the tree (None for unlimited)

    Returns:
        Tuple of:
        - Fitted DecisionTreeRegressor model
        - Training RMSLE (float)
        - Validation RMSLE (float)
    """
    # TODO: Implement this function
    # Hint: Use DecisionTreeRegressor(max_depth=max_depth).fit()
    pass


# =============================================================================
# Cross-Validation
# =============================================================================

def cross_validate_model(
    X: pd.DataFrame,
    y: pd.Series,
    max_depths: List[int] = list(range(1, 51)),
    cv: int = 5
) -> Dict:
    """5-fold cross-validation for DecisionTreeRegressor across max_depths.

    For each max_depth value, performs k-fold cross-validation and records
    the mean RMSLE. The best depth is the one with lowest mean RMSLE.

    Args:
        X: Training feature DataFrame (one-hot encoded, unscaled)
        y: Training target Series
        max_depths: List of max_depth values to try (default: 1 to 50)
        cv: Number of cross-validation folds (default: 5)

    Returns:
        Dict with keys:
        - 'mean_cv_rmsle': list of mean RMSLE for each depth
        - 'best_depth': int, the max_depth with lowest mean RMSLE
    """
    # TODO: Implement this function
    # Hint: For each depth, use cross_val_score with
    # Hint: scoring='neg_mean_squared_log_error', then
    # Hint: rmsle = sqrt(mean(-scores))
    pass


# =============================================================================
# Kaggle Submission
# =============================================================================

def prepare_kaggle_submission(
    model,
    X_test: pd.DataFrame,
    test_df: pd.DataFrame,
    output_path: str
) -> pd.DataFrame:
    """Generate Kaggle submission predictions.

    Args:
        model: Trained model with .predict() method
        X_test: Preprocessed test features
        test_df: Original test DataFrame (for houseid column)
        output_path: Path to save the submission CSV

    Returns:
        DataFrame with columns ['houseid', 'list_price']
    """
    # TODO: Implement this function
    # Hint: model.predict(X_test), then build DataFrame with
    # Hint: test_df['houseid'] and predictions, save to CSV
    pass
