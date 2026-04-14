# Homework 4: Houston Home Price Prediction (Regression)

## Overview

Predict the `list_price` of Houston homes using four regression methods: Linear Regression, Lasso (with alpha tuning), Support Vector Regression (with kernel comparison), and Decision Tree Regressor (with cross-validation). You will explore the dataset, build a preprocessing pipeline, train and evaluate models, and generate predictions on the test set.

**Data Files:**
- `houston_homes.csv` - Training data (5,575 rows x 16 columns)
- `houston_homes_test.csv` - Test data for prediction (1,858 rows x 15 columns, no `list_price`)
- Location: `data/hw4/` (mounted at `/data/hw4` in Docker)

**Key Columns:**

| Column | Type | Missing | Notes |
|--------|------|---------|-------|
| `houseid` | int | 0 | Unique ID -- exclude from features |
| `property_type` | str | 0 | 3 values: Single Family Residential, Townhouse, Condo/Co-op |
| `city` | str | 0 | 37 unique -- exclude (too many categories, redundant with lat/long) |
| `state` | str | 0 | All "TX" -- exclude (constant) |
| `zipcode` | int | 0 | 136 unique -- exclude (too many for one-hot, use lat/long instead) |
| `list_price` | int | 0 | **TARGET** -- mean ~$550K, range $45K-$20M |
| `beds` | float | 2 | |
| `baths` | float | 11 | |
| `location` | str | 4 | Neighborhood name -- exclude (too many categories) |
| `sqft` | float | 20 | |
| `lot_size` | float | 647 (12%) | |
| `year_built` | int | 0 | |
| `days_on_market` | float | 2 | |
| `hoa` | float | 2,030 (36%) | NaN likely means no HOA -- fill with 0 |
| `latitude` | float | 0 | |
| `longitude` | float | 0 | |

## Tasks

### Part 0: Setup and Data Loading

Load the training data and inspect its structure.

1. Load `houston_homes.csv` into a DataFrame using `load_housing_data(data_dir)`
2. Inspect the shape, dtypes, and first few rows

### Part 1: Data Exploration (30 pts)

Explore the dataset to understand missing values, feature distributions, and relationships.

#### Step 1: Missing Values

Check for missing values using `check_missing_values(df)`. Identify which columns have missing data and how many rows are affected.

**Written Question:** Q1: Do you think that the missing values are going to be problematic for predicting list price? Why or why not?

#### Step 2: Zipcode Distribution

Examine the distribution of homes across zipcodes. Consider how many unique zipcodes exist and whether some are overrepresented.

**Written Question:** Q2: Based on the plots you generated above, do you think differences in zipcode distribution will affect models that predict list prices? Explain.

#### Step 3: Geographic Analysis

Examine the relationship between geographic coordinates (latitude, longitude) and `list_price`.

**Written Question:** Q3: Where are the most expensive homes located?

**Written Question:** Q4: Is latitude and longitude more / less / similarly informative than zipcode for determining list price? Explain.

#### Step 4: Feature Correlations

Compute the correlation matrix of the numeric features. Identify which features are most correlated with `list_price` and whether any features are highly correlated with each other.

**Written Question:** Q5: Based on the correlations, should we remove any features before running linear regression? Explain.

#### Step 5: Baseline Heuristic

Implement `baseline_heuristic(y_train, n_predictions)` to generate a simple baseline: predict the mean of the training target for every sample.

**Written Question:** Q6: If our goal is to reduce the error in our price predictions, design a baseline heuristic for this problem. Explain your rationale.

### Data Preparation

Before training any models, prepare the data using this exact pipeline:

1. **Separate target:** `y = df['list_price']`
2. **Drop columns from features:** Remove `houseid`, `list_price`, `city`, `state`, `location`, `zipcode`
3. **Keep these features:** `property_type`, `beds`, `baths`, `sqft`, `lot_size`, `year_built`, `days_on_market`, `hoa`, `latitude`, `longitude`
4. **Train/validation split:** `train_test_split(X, y, test_size=0.20, random_state=42)`
5. **Fill HOA missing values:** Fill `hoa` NaN with 0 (NaN means no HOA). Do this before other imputation.
6. **Impute remaining missing values:** For `beds`, `baths`, `sqft`, `lot_size`, `days_on_market`, compute the mean on the training set and apply to both train and validation sets (to avoid data leakage).
7. **One-hot encode `property_type`:** Use `pd.get_dummies`. Ensure train and validation have consistent columns using `.align(join='outer', fill_value=0)`.
8. **Scale features with `StandardScaler`:** Fit on the training set, transform both. Scaling is required for SVR and recommended for linear models. It is not required for decision trees but will not hurt.

**Written Question:** Q7: Which columns did you choose to omit from your feature set? Why did you exclude these columns?

### Part 2: Regression Models + Evaluation (25 pts)

#### Step 1: Linear Regression

Train a `LinearRegression` with default parameters using `train_linear_regression`. Evaluate using RMSE on both training and validation sets.

**Written Question:** Q8: Does the linear regression model outperform the baseline heuristic you chose earlier? What does this comparison tell you?

#### Step 2: Lasso Regression with Alpha Tuning

Train a `Lasso` model with default `alpha=1.0` using `train_lasso_regression`. Evaluate using RMSLE.

Then tune the alpha hyperparameter using `tune_lasso_alpha`. Test the following alpha range:

```python
alphas = np.concatenate([
    np.arange(0, 100, 5),
    np.arange(100, 1000, 100),
    np.arange(1000, 20000, 1000)
])
```

For each alpha, train a Lasso model and record training and validation RMSLE. Identify the alpha with the lowest validation RMSLE.

**Written Question:** Q9: Using the plot above as a guide, determine if there is an optimal alpha (or small range of alphas) for this task. Explain.

#### Step 3: SVR (Support Vector Regression)

Train SVR models with `C=100` and three different kernels using `train_svr`:
1. `kernel='linear'`
2. `kernel='poly'`
3. `kernel='rbf'`

Evaluate each using RMSLE on training and validation sets. Compare kernel performance.

**Written Question:** Q10: Which SVR kernel has the best performance? Give some intuition as to why you think it outperformed the other kernel choices.

### Part 3: Cross-validation (25 pts)

#### Step 1: Decision Tree Regressor

Train a `DecisionTreeRegressor` with default parameters using `train_decision_tree_regressor`. Evaluate using RMSLE.

#### Step 2: Cross-validate Max Depth

Use `cross_validate_model` to perform 5-fold cross-validation on `DecisionTreeRegressor` across `max_depth` values from 1 to 50 (inclusive).

For each depth, use `sklearn.model_selection.cross_val_score` with `scoring='neg_mean_squared_log_error'`. The function returns negative MSLE values -- negate them before taking the square root to get RMSLE:

```python
scores = cross_val_score(model, X, y, cv=5, scoring='neg_mean_squared_log_error')
rmsle = np.sqrt(np.mean(-scores))
```

Note: Pass the training data (`X_train`, `y_train`) from your 80/20 split -- do not include the validation set. Cross-validation handles its own internal fold splitting within the training data. Do not pass scaled data to cross-validation for decision trees -- decision trees are invariant to feature scaling. Pass the one-hot encoded but unscaled features.

Identify the max_depth with the lowest mean CV RMSLE.

**Written Questions:**
- Q11: Based on these RMSLEs, which max_depth parameter is optimal? Explain.
- Q12: Was cross-validation helpful in choosing the optimal max depth parameter? Why or why not?

### Part 4: Test Set Predictions (5 pts)

Use `prepare_kaggle_submission` to generate predictions on the test set.

1. Load `houston_homes_test.csv`
2. Apply the exact same preprocessing as the training data: drop the same columns, fill HOA NaN with 0, impute missing values using the training set means, one-hot encode `property_type` (align columns with training set), and scale using the same fitted scaler
3. Choose your best model and generate predictions
4. Save a CSV with columns `['houseid', 'list_price']`

### General Coding Style (15 pts)

- **10 points** for coding style: logical variable names, comments as needed, clean code
- **5 points** for code flow: accurate results when functions are called sequentially

## Functions to Implement

```python
import pandas as pd
import numpy as np
from pathlib import Path
from typing import Union, Tuple, Dict, List, Optional
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.linear_model import LinearRegression, Lasso
from sklearn.svm import SVR
from sklearn.tree import DecisionTreeRegressor
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import mean_squared_error, mean_squared_log_error


def load_housing_data(data_dir: Union[str, Path]) -> pd.DataFrame:
    """Load houston_homes.csv into a DataFrame.

    Args:
        data_dir: Path to directory containing houston_homes.csv

    Returns:
        DataFrame with all columns from houston_homes.csv
    """


def check_missing_values(df: pd.DataFrame) -> pd.Series:
    """Check for missing values in the DataFrame.

    Args:
        df: DataFrame to check

    Returns:
        Series with feature names as index and NaN counts as values,
        filtered to only features with > 0 missing values,
        sorted descending by count
    """


def calculate_rmse(y_true: np.ndarray, y_pred: np.ndarray) -> float:
    """Calculate Root Mean Squared Error.

    RMSE = sqrt(mean((y_true - y_pred)^2))

    Args:
        y_true: True target values
        y_pred: Predicted values

    Returns:
        RMSE as a float
    """


def calculate_rmsle(y_true: np.ndarray, y_pred: np.ndarray) -> float:
    """Calculate Root Mean Squared Logarithmic Error.

    RMSLE = sqrt(mean((log(1 + y_true) - log(1 + y_pred))^2))

    Note: y_true and y_pred must be non-negative.

    Args:
        y_true: True target values (non-negative)
        y_pred: Predicted values (non-negative)

    Returns:
        RMSLE as a float
    """


def baseline_heuristic(y_train: pd.Series, n_predictions: int) -> np.ndarray:
    """Generate baseline predictions using the mean of training targets.

    Args:
        y_train: Training target values
        n_predictions: Number of predictions to generate

    Returns:
        Array of length n_predictions, all equal to mean(y_train)
    """


def train_linear_regression(
    X_train: pd.DataFrame, y_train: pd.Series,
    X_val: pd.DataFrame, y_val: pd.Series
) -> Tuple[LinearRegression, float, float]:
    """Train LinearRegression and evaluate using RMSE.

    Args:
        X_train: Training features (preprocessed, numeric)
        y_train: Training target values
        X_val: Validation features (preprocessed, numeric)
        y_val: Validation target values

    Returns:
        Tuple of (fitted model, training RMSE, validation RMSE)
    """


def train_lasso_regression(
    X_train: pd.DataFrame, y_train: pd.Series,
    X_val: pd.DataFrame, y_val: pd.Series,
    alpha: float = 1.0
) -> Tuple[Lasso, float, float]:
    """Train Lasso regression and evaluate using RMSLE.

    Args:
        X_train: Training features (preprocessed, numeric)
        y_train: Training target values
        X_val: Validation features (preprocessed, numeric)
        y_val: Validation target values
        alpha: Regularization strength (default 1.0)

    Returns:
        Tuple of (fitted model, training RMSLE, validation RMSLE)
    """


def tune_lasso_alpha(
    X_train: pd.DataFrame, y_train: pd.Series,
    X_val: pd.DataFrame, y_val: pd.Series,
    alphas: np.ndarray = None
) -> Dict:
    """Tune Lasso alpha hyperparameter.

    If alphas is None, use the default range:
        np.concatenate([
            np.arange(0, 100, 5),
            np.arange(100, 1000, 100),
            np.arange(1000, 20000, 1000)
        ])

    For each alpha, train a Lasso model and record training and
    validation RMSLE.

    Args:
        X_train: Training features (preprocessed, numeric)
        y_train: Training target values
        X_val: Validation features (preprocessed, numeric)
        y_val: Validation target values
        alphas: Array of alpha values to test (optional)

    Returns:
        Dict with keys:
        - 'train_rmsles': list of training RMSLE for each alpha
        - 'val_rmsles': list of validation RMSLE for each alpha
        - 'best_alpha': float, alpha with lowest validation RMSLE
    """


def train_svr(
    X_train: pd.DataFrame, y_train: pd.Series,
    X_val: pd.DataFrame, y_val: pd.Series,
    kernel: str = 'rbf', C: float = 100
) -> Tuple[SVR, float, float]:
    """Train SVR with given kernel and evaluate using RMSLE.

    Args:
        X_train: Training features (scaled)
        y_train: Training target values
        X_val: Validation features (scaled)
        y_val: Validation target values
        kernel: Kernel type ('linear', 'poly', or 'rbf')
        C: Regularization parameter (default 100)

    Returns:
        Tuple of (fitted model, training RMSLE, validation RMSLE)
    """


def train_decision_tree_regressor(
    X_train: pd.DataFrame, y_train: pd.Series,
    X_val: pd.DataFrame, y_val: pd.Series,
    max_depth: Optional[int] = None
) -> Tuple[DecisionTreeRegressor, float, float]:
    """Train DecisionTreeRegressor and evaluate using RMSLE.

    Args:
        X_train: Training features
        y_train: Training target values
        X_val: Validation features
        y_val: Validation target values
        max_depth: Maximum depth of tree (None for unlimited)

    Returns:
        Tuple of (fitted model, training RMSLE, validation RMSLE)
    """


def cross_validate_model(
    X: pd.DataFrame, y: pd.Series,
    max_depths: List[int] = list(range(1, 51)),
    cv: int = 5
) -> Dict:
    """5-fold cross-validation for DecisionTreeRegressor across max_depths.

    For each depth, train a DecisionTreeRegressor and compute mean RMSLE
    across folds. Use sklearn cross_val_score with
    scoring='neg_mean_squared_log_error', then convert:
        rmsle = sqrt(mean(-scores))

    Note: Pass the training data (X_train, y_train from the 80/20 split).
    Cross-validation handles its own internal fold splitting within this
    training data. Use one-hot encoded but unscaled features since decision
    trees are invariant to scaling.

    Args:
        X: Training feature DataFrame (one-hot encoded, unscaled)
        y: Training target Series
        max_depths: List of max_depth values to test (default 1-50)
        cv: Number of cross-validation folds (default 5)

    Returns:
        Dict with keys:
        - 'mean_cv_rmsle': list of mean CV RMSLE for each depth
        - 'best_depth': int, depth with lowest mean CV RMSLE
    """


def prepare_kaggle_submission(
    model, X_test: pd.DataFrame,
    test_df: pd.DataFrame, output_path: str
) -> pd.DataFrame:
    """Generate Kaggle submission CSV.

    Args:
        model: Trained model with .predict() method
        X_test: Preprocessed test features (same pipeline as training)
        test_df: Original test DataFrame (for houseid column)
        output_path: Path to save the submission CSV

    Returns:
        DataFrame with columns ['houseid', 'list_price']
    """
```

## Hints

- **HOA fill order:** Fill `hoa` NaN with 0 before computing means for other columns. NaN in `hoa` means no HOA, not a missing measurement.
- **Train/val split:** Always use `test_size=0.20, random_state=42` for reproducibility
- **Imputation order:** Impute missing values *after* train/val split, computing means on training data only
- **One-hot encoding consistency:** Use `pd.get_dummies()` on train and validation separately, then use `.align(join='outer', fill_value=0)` to ensure consistent columns
- **Scaling:** Fit `StandardScaler` on training data only, then transform both sets
- **SVR is slow:** Feature scaling is essential for SVR performance. Keep the feature count reasonable.
- **RMSLE requires non-negative values:** House price predictions are naturally non-negative, but verify this holds for all models
- **cross_val_score sign convention:** `scoring='neg_mean_squared_log_error'` returns negative MSLE values. Negate before taking the square root.
- **Test set preprocessing:** Apply the identical pipeline to test data: drop the same columns, fill HOA with 0, impute using training set means, one-hot encode and align columns, scale using the same fitted scaler
- **Columns to exclude:** `houseid` (identifier), `city` (37 categories, redundant with lat/long), `state` (constant "TX"), `location` (too many categories), `zipcode` (136 unique, use lat/long instead)
- **Lasso alpha=0:** `alpha=0` in the default range is equivalent to ordinary least squares. Lasso with very large alpha shrinks all coefficients toward zero.

## Grading

| Part | Points |
|------|--------|
| Part 1: Data Exploration | 30 |
| Part 2: Regression Models | 25 |
| Part 3: Cross-validation | 25 |
| Part 4: Test Set Predictions | 5 |
| General Coding Style | 15 |
| **Total** | **100** |
