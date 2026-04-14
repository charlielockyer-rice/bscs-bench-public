# HW3: Stroke Prediction (Classification)

## Overview

CVA (stroke) prediction using Decision Trees, Logistic Regression, and LDA on health/demographic data. 13 tests, 100 points, 15 written questions.

## Data

- **File**: `CVA.csv` (~5,108 rows)
- **Target**: `CVA` column (binary: 0/1)
- **Class imbalance**: ~95% negative (no stroke), ~5% positive
- **Missing values**: BMI column uses `"N/A"` strings — must use `na_values=["N/A"]` in `pd.read_csv`
- **Column names**: `sex` (not `gender`), `residence_type` (not `Residence_type`), `id` (exclude from features)

## Key Parameters

- Train/test split: `test_size=0.40, random_state=6`
- Logistic regression: `max_iter=1000`
- No regularization: `penalty=None` (Python None, NOT string `'none'`) — sklearn 1.4.0 requirement
- max_depth tuning: range 1-20 inclusive

## Gotchas

- **Column name mismatch**: The original Colab notebook and rubric referenced `gender` and `Residence_type`, but the actual CSV uses `sex` and `residence_type`. All files (instructions, tests, rubric) were corrected.
- **BMI "N/A" strings**: Not standard NaN — requires explicit `na_values` parameter in read_csv.
- **Imbalanced accuracy**: A naive all-negative classifier gets ~95% accuracy. Test thresholds are set at 0.85 for decision trees and 0.7 for logistic regression/LDA to be meaningful but achievable.
- **One-hot encoding alignment**: `pd.get_dummies` on train and test separately produces different columns if a category only appears in one set. Must use `.align(join="outer", fill_value=0)` or equivalent.
- **Scaling doesn't affect decision trees**: Tests verify this by checking accuracy stays within a small tolerance.

## Functions (11 total)

| Function | Returns | Notes |
|----------|---------|-------|
| `load_cva_data` | DataFrame | Use `na_values=["N/A"]` |
| `prepare_features_labels` | (X, y) | Drop `id` and `CVA` |
| `impute_missing_values` | (X_train, X_test) | Fit means on train only |
| `train_decision_tree` | (model, train_acc, test_acc) | 3-tuple |
| `one_hot_encode` | (X_train_enc, X_test_enc) | Consistent columns |
| `scale_features` | (X_train_sc, X_test_sc) | Fit on train only |
| `find_optimal_depth` | dict | Keys: train_accuracies, test_accuracies, best_depth |
| `get_feature_importances` | DataFrame | Columns: feature, importance (sorted desc) |
| `train_logistic_regression` | (model, train_acc, test_acc) | 3-tuple |
| `train_lda` | (model, train_acc, test_acc) | 3-tuple |
| `compare_regularization` | dict | Keys: 'L1', 'L2', 'none' → (model, train_acc, test_acc) |

## Agent Validation

An Opus agent solved this from scratch in ~3.5 minutes (18 tool calls), achieving 100/100 on the first grading run. The instructions, tests, and Docker environment are validated.
