# Homework 3: Stroke Prediction (Classification)

## Overview

Predict whether an individual has had a cerebrovascular accident (CVA/stroke) given a snapshot of their health and demographic information. You will train and compare three classification methods -- Decision Trees, Logistic Regression, and Linear Discriminant Analysis -- exploring how feature encoding, scaling, and regularization affect performance.

**Data File:**
- `CVA.csv` - Health and demographic data for stroke prediction
- Location: `data/hw3/` (mounted at `/data/hw3` in Docker)

**Key Columns:**
- `id` - Patient identifier (non-predictive, exclude from features)
- `sex` - Male, Female, Other (categorical)
- `age` - Age of patient (numeric)
- `hypertension` - 0 or 1 (binary)
- `heart_disease` - 0 or 1 (binary)
- `ever_married` - Yes or No (categorical)
- `work_type` - children, Govt_job, Never_worked, Private, Self-employed (categorical)
- `residence_type` - Rural or Urban (categorical)
- `avg_glucose_level` - Average glucose level (numeric)
- `bmi` - Body mass index (numeric, has missing values)
- `smoking_status` - formerly smoked, never smoked, smokes, Unknown (categorical)
- `CVA` - 1 if the patient had a stroke, 0 otherwise (**target variable**)

## Tasks

### Part 0: Getting Familiar with the Data (13 pts)

Explore the dataset to understand its structure, feature types, missing values, and class distribution.

1. Load `CVA.csv` into a DataFrame
2. Determine the number of individuals in the dataset
3. Identify the feature types: nominal, ordinal, and numeric
4. Check for missing values -- how many rows and columns are affected?
5. Count how many individuals have had a CVA vs. not
6. Visualize the distribution of features (use appropriate plots for each data type)

**Written Questions (answered in writeup.md):**
- Q1: How many individuals in this dataset?
- Q2: List the nominal features, ordinal features, and numeric features
- Q3: Without additional coding -- do you think the missing values will be problematic during classification? Why or why not?
- Q4: Without additional coding -- which features do you think will be the most informative? Explain your rationale.

### Data Preparation

Before training any models, prepare the data:

1. **Separate features and labels:** Create feature matrix `X` (exclude `CVA` and non-predictive columns like `id`) and label vector `y` (the `CVA` column)
2. **Train/test split:** Use `train_test_split(X, y, test_size=0.40, random_state=6)`
3. **Impute missing values:** After the split, fill missing values using mean imputation. Compute the mean on the training set and apply to both train and test sets separately (to avoid data leakage).

### Part 1: Decision Trees (30 pts)

#### Step 1: Numeric Features Only
Train a `DecisionTreeClassifier` with default parameters using only the numeric (continuous-valued) features. Evaluate accuracy on the test set.

#### Step 2: One-Hot Encode Categorical Features
Convert categorical features using one-hot encoding (e.g., `pd.get_dummies`). Train a new decision tree with default parameters on all features (numeric + one-hot encoded categorical). Evaluate accuracy on the test set.

**Written Question:** Q5: Did you notice any accuracy improvements after adding the categorical features? Comment on why this did or did not help.

#### Step 3: Scale Numeric Features
Scale all numeric features using `StandardScaler`. Fit the scaler on the training set and transform both train and test sets. Retrain and re-test the decision tree.

**Written Question:** Q6: Did you notice any accuracy improvements after scaling? Comment on why scaling did or did not help.

#### Step 4: Tune max_depth
Vary the `max_depth` parameter from 1 to 20 (inclusive). For each depth, train a decision tree and record accuracy on both the training and test sets. Plot max_depth vs. accuracy for both sets on a single figure.

**Written Question:** Q7: Which maximum depth parameter is the best choice?

#### Step 5: Feature Importances
Using the decision tree with the optimal `max_depth` from Step 4, extract the feature importances (`model.feature_importances_`). Create a DataFrame showing each feature and its importance.

**Written Question:** Q8: Looking at the feature importances, does this match your initial expectations? Why or why not?

### Part 2: Logistic Regression (30 pts)

#### Step 1: Unscaled Logistic Regression
Train `LogisticRegression` with default parameters on the one-hot encoded (but unscaled) feature set. Evaluate accuracy on the test set.

#### Step 2: Scaled Logistic Regression
Train `LogisticRegression` on the scaled and one-hot encoded features. Evaluate accuracy on the test set.

**Written Question:** Q9: Does scaling have an effect on logistic regression performance? Why or why not?

#### Step 3: Extract Coefficients
Extract the feature coefficients from the scaled logistic regression model (`model.coef_`). Display them alongside feature names.

**Written Question:** Q10: Based on the coefficients, are the following features associated with an increased or decreased chance of CVA: living in an urban area, being married, BMI?

#### Step 4: Compare Regularization
Train three logistic regression models on the scaled data and extract coefficients from each:
1. **L1 regularization:** `LogisticRegression(solver='liblinear', penalty='l1')`
2. **L2 regularization:** `LogisticRegression(solver='liblinear', penalty='l2')`
3. **No regularization:** `LogisticRegression(solver='lbfgs', penalty=None)`

Compare the coefficient values across all three.

**Written Questions:**
- Q11: Do the different regularization methods change the feature weights? If so, which features are affected?
- Q12: Based on the feature weights across the different regularization schemes, would you say that regularization helps prevent overfitting in this case? Explain.

### Part 3: Linear Discriminant Analysis (12 pts)

#### Step 1: Train LDA
Train `LinearDiscriminantAnalysis` with default settings on the scaled and one-hot encoded features. Evaluate accuracy on the test set.

#### Step 2: Extract Coefficients
Extract the LDA coefficients (`model.coef_`). Display them alongside feature names.

**Written Questions:**
- Q13: Can the coefficients extracted from LDA be interpreted as feature importances? Why or why not?
- Q14: Now that you have run three classification methods (decision trees, logistic regression, LDA) with various parameters, which method is best suited for the CVA prediction problem? Explain.
- Q15: Which method would you use for identifying risk factors for CVAs? Explain.

### General Coding Style (15 pts)

- **10 points** for coding style: logical variable names, comments as needed, clean code
- **5 points** for code flow: accurate results when functions are called sequentially

## Functions to Implement

```python
import pandas as pd
import numpy as np
from pathlib import Path
from typing import Union, Optional, List, Tuple, Dict
from sklearn.model_selection import train_test_split
from sklearn.tree import DecisionTreeClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.discriminant_analysis import LinearDiscriminantAnalysis
from sklearn.preprocessing import StandardScaler


def load_cva_data(data_dir: Union[str, Path]) -> pd.DataFrame:
    """Load CVA.csv into a DataFrame.

    Args:
        data_dir: Path to directory containing CVA.csv

    Returns:
        DataFrame with all columns from CVA.csv
    """


def prepare_features_labels(df: pd.DataFrame) -> Tuple[pd.DataFrame, pd.Series]:
    """Separate features (X) and labels (y) from the dataset.

    Exclude the target column 'CVA' and any non-predictive columns (e.g., 'id')
    from the feature matrix.

    Args:
        df: Full CVA DataFrame

    Returns:
        Tuple of (X, y) where X is feature DataFrame and y is CVA Series
    """


def impute_missing_values(
    X_train: pd.DataFrame,
    X_test: pd.DataFrame
) -> Tuple[pd.DataFrame, pd.DataFrame]:
    """Impute missing values using mean imputation.

    Compute the mean on X_train and apply to both X_train and X_test
    to avoid data leakage.

    Args:
        X_train: Training feature DataFrame
        X_test: Test feature DataFrame

    Returns:
        Tuple of (X_train_imputed, X_test_imputed)
    """


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
        max_depth: Maximum depth of tree (None for unlimited)

    Returns:
        Tuple of (fitted model, training accuracy, test accuracy)
    """


def one_hot_encode(
    X_train: pd.DataFrame,
    X_test: pd.DataFrame
) -> Tuple[pd.DataFrame, pd.DataFrame]:
    """One-hot encode categorical features in both train and test sets.

    Must produce consistent columns across train and test. Use pd.get_dummies
    or similar, ensuring both DataFrames end up with the same columns.

    Args:
        X_train: Training features with categorical columns
        X_test: Test features with categorical columns

    Returns:
        Tuple of (X_train_encoded, X_test_encoded) with consistent columns
    """


def scale_features(
    X_train: pd.DataFrame,
    X_test: pd.DataFrame
) -> Tuple[pd.DataFrame, pd.DataFrame]:
    """Scale numeric features using StandardScaler.

    Fit the scaler on X_train and transform both train and test.

    Args:
        X_train: Training features
        X_test: Test features

    Returns:
        Tuple of (X_train_scaled, X_test_scaled)
    """


def find_optimal_depth(
    X_train: pd.DataFrame,
    y_train: pd.Series,
    X_test: pd.DataFrame,
    y_test: pd.Series,
    max_depths: List[int] = list(range(1, 21))
) -> Dict:
    """Find the optimal max_depth by training trees at each depth.

    Args:
        X_train: Training features (scaled, one-hot encoded)
        y_train: Training labels
        X_test: Test features (scaled, one-hot encoded)
        y_test: Test labels
        max_depths: Range of max_depth values to try (default 1-20)

    Returns:
        Dict with keys:
        - 'train_accuracies': list of training accuracies for each depth
        - 'test_accuracies': list of test accuracies for each depth
        - 'best_depth': int, the max_depth with highest test accuracy
    """


def get_feature_importances(
    model: DecisionTreeClassifier,
    feature_names: List[str]
) -> pd.DataFrame:
    """Extract feature importances from a fitted decision tree.

    Args:
        model: Fitted DecisionTreeClassifier
        feature_names: List of feature names matching model's features

    Returns:
        DataFrame with columns ['feature', 'importance'],
        sorted by importance descending
    """


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
        solver: Solver algorithm (default 'lbfgs')
        max_iter: Maximum iterations (default 1000)

    Returns:
        Tuple of (fitted model, training accuracy, test accuracy)
    """


def train_lda(
    X_train: pd.DataFrame,
    y_train: pd.Series,
    X_test: pd.DataFrame,
    y_test: pd.Series
) -> Tuple[LinearDiscriminantAnalysis, float, float]:
    """Train an LDA model and evaluate accuracy.

    Args:
        X_train: Training features (scaled, one-hot encoded)
        y_train: Training labels
        X_test: Test features (scaled, one-hot encoded)
        y_test: Test labels

    Returns:
        Tuple of (fitted model, training accuracy, test accuracy)
    """


def compare_regularization(
    X_train: pd.DataFrame,
    y_train: pd.Series,
    X_test: pd.DataFrame,
    y_test: pd.Series
) -> Dict[str, Tuple[LogisticRegression, float, float]]:
    """Compare L1, L2, and no regularization for logistic regression.

    Trains three models on the scaled features:
    1. L1: LogisticRegression(solver='liblinear', penalty='l1')
    2. L2: LogisticRegression(solver='liblinear', penalty='l2')
    3. None: LogisticRegression(solver='lbfgs', penalty=None)

    Args:
        X_train: Scaled training features
        y_train: Training labels
        X_test: Scaled test features
        y_test: Test labels

    Returns:
        Dict mapping regularization name to (model, train_accuracy, test_accuracy).
        Keys: 'L1', 'L2', 'none'
    """
```

## Hints

- **Non-predictive columns:** The `id` column is a patient identifier and should be excluded from features
- **Train/test split:** Always use `test_size=0.40, random_state=6` for reproducibility
- **Imputation order:** Impute missing values *after* train/test split, computing means on training data only
- **One-hot encoding consistency:** Use `pd.get_dummies()` on combined train+test, then re-split, or use `handle_unknown='ignore'` with `OneHotEncoder` to ensure consistent columns
- **Scaling:** Fit `StandardScaler` on training data only, then transform both sets
- **Decision tree and scaling:** Decision trees split on thresholds, so scaling should not affect their performance
- **max_depth tuning:** The best depth balances train and test accuracy (look for where test accuracy peaks before overfitting begins)
- **Logistic regression convergence:** Increase `max_iter` if you see convergence warnings (1000 is usually sufficient)
- **No regularization:** Use `penalty=None` (Python None, not the string `'none'`)

## Grading

| Part | Points |
|------|--------|
| Part 0: Data Exploration | 13 |
| Part 1: Decision Trees | 30 |
| Part 2: Logistic Regression | 30 |
| Part 3: LDA & Comparison | 12 |
| General Coding Style | 15 |
| **Total** | **100** |

## Extra Credit (up to 10 pts)

Using plotnine (not matplotlib or seaborn), write visualization functions:
- Plot a sigmoid curve for each feature from logistic regression (up to 5 pts)
- Plot the LDA classification boundary with training points (up to 5 pts)
