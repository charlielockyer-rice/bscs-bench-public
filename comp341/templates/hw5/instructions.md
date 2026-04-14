# Homework 5: Who Said It? (Text Classification)

## Overview

Classify passages of text into four categories (president, actor, academic, other) using unsupervised exploration and supervised classification. You will use LDA topic modeling, dimensionality reduction (PCA, t-SNE), K-means clustering to investigate the "other" category, then train a Random Forest classifier with multiclass evaluation metrics (ROC, precision-recall, confusion matrix), and generate predictions on the test set.

**Data Files:**
- `bag_of_words.csv` - Bag-of-words features (560 rows x 1119 columns)
- `tfidf.csv` - TF-IDF features (560 rows x 1119 columns)
- `document_labels.csv` - Labels (560 rows x 3 columns)
- `test-bag_of_words.csv` - Test bag-of-words (181 rows x 1119 columns)
- `test-tfidf.csv` - Test TF-IDF (181 rows x 1119 columns)
- Location: `data/hw5/` (mounted at `/data/hw5` in Docker)

**Key Columns:**

| File | Columns | Notes |
|------|---------|-------|
| `bag_of_words.csv` | `textid` + 1118 word count columns | Raw word counts for LDA |
| `tfidf.csv` | `textid` + 1118 TF-IDF columns | Normalized features for classification |
| `document_labels.csv` | `textid`, `category`, `person` | 4 categories, 12 unique persons |

**Category Distribution:**

| Category | Count | Notes |
|----------|-------|-------|
| president | 188 | Political speeches/text |
| actor | 152 | Actor-related text |
| academic | 115 | Academic text |
| other | 105 | Mystery category -- `person` is NaN |

- **12 unique persons** across non-other categories (anonymized as numbers 0-14, with gaps)
- **No missing values** in bag_of_words or tfidf -- clean numerical matrices
- **Train and test columns match exactly** -- no alignment issues needed

## Tasks

### Part 1: Data Exploration (50 pts)

#### Step 1: Data Loading

Load all three training data files using `load_text_data(data_dir)`. The function reads `bag_of_words.csv`, `tfidf.csv`, and `document_labels.csv` from the given directory and returns a dict with keys `'bag_of_words'`, `'tfidf'`, and `'labels'`.

#### Step 2: LDA Topic Modeling

Run Latent Dirichlet Allocation with 10 topics on the bag-of-words features (excluding the `textid` column) using `run_lda_topics(bow_features, n_topics=10)`. LDA expects raw word counts, not TF-IDF values.

#### Step 3: Top Words Per Topic

Extract the top 15 words for each topic using `get_top_topic_words(lda_model, feature_names, n_words=15)`. Pass the column names from the bag-of-words DataFrame (after dropping `textid`) as `feature_names`.

#### Step 4: PCA Dimensionality Reduction

Reduce the TF-IDF features (excluding `textid`) to 2 dimensions using `reduce_dimensions_pca(features, n_components=2)`. Returns both the transformed data and the fitted PCA model.

#### Step 5: t-SNE Dimensionality Reduction

Reduce the TF-IDF features (excluding `textid`) to 2 dimensions using `reduce_dimensions_tsne(features, n_components=2, random_state=42)`. Returns the transformed array.

**Written Question:** Q1: Given the topics you identified with LDA and the dimensionality reduction plot, do you think there is enough information in the data (specifically, the bag of words) for the classification task of predicting the category for a passage of text? Explain.

#### Step 6: K-Means Clustering on "other" Category

Filter the TF-IDF data to only rows belonging to the "other" category. Reduce those features to 2D with PCA, then cluster with K-means for k=2 to 10 using `cluster_kmeans(features_2d, k)`.

**Workflow:**
1. Filter `document_labels.csv` to rows where `category == 'other'` to get the "other" textids
2. Filter the TF-IDF DataFrame to rows matching those textids
3. Drop the `textid` column from the filtered TF-IDF data
4. Reduce to 2D using `reduce_dimensions_pca(other_features, n_components=2)`
5. For each k from 2 to 10, run `cluster_kmeans(features_2d, k)`

#### Step 7: Silhouette Analysis

Calculate silhouette scores for k=2 to 10 using `calculate_silhouette_scores(features_2d, k_range=range(2, 11))`. This function runs K-means for each k and returns a dict with `'k_values'`, `'inertias'` (for the elbow plot), and `'silhouette_scores'`.

**Written Question:** Q2: Using the elbow plot and silhouette plots, how many authors/speakers do you think there are for the "other" category of documents? (We are assuming that the number of clusters corresponds to the number of unique authors/speakers.) Explain your choice.

### Part 2: Text Classification (45 pts)

#### Step 1: Train Classifier

Train a `RandomForestClassifier(n_estimators=100, random_state=42)` on the TF-IDF features using `train_classifier(X_train, y_train, X_val, y_val)`.

**Data preparation:**
1. Use the TF-IDF DataFrame, drop the `textid` column to get `X`
2. Use the `category` column from `document_labels.csv` as `y`
3. Split: `train_test_split(X, y, test_size=0.20, random_state=42)`
4. Train the model and compute training and validation accuracy

#### Step 2: ROC Curve

Compute per-class ROC curves using `calculate_roc_curve(model, X_val, y_val)`. Use one-vs-rest approach:
1. Binarize labels with `label_binarize(y, classes=sorted_classes)`
2. Get class probabilities with `model.predict_proba()`
3. Compute `roc_curve` and `auc` for each class separately

Returns a dict mapping each category name to `{'fpr', 'tpr', 'auc'}`.

#### Step 3: Precision-Recall Curve

Compute per-class precision-recall curves using `calculate_precision_recall(model, X_val, y_val)`. Same binarization approach as ROC:
1. Binarize labels with `label_binarize(y, classes=sorted_classes)`
2. Get class probabilities with `model.predict_proba()`
3. Compute `precision_recall_curve` and `average_precision_score` for each class

Returns a dict mapping each category name to `{'precision', 'recall', 'avg_precision'}`.

#### Step 4: Confusion Matrix

Compute the confusion matrix using `create_confusion_matrix(y_true, y_pred)` where `y_pred = model.predict(X_val)`. Returns a tuple of (confusion matrix as np.ndarray, sorted list of class labels).

**Written Question:** Q3: Looking at the confusion matrix, are there any categories that tend to get mixed up more than others? Does this make sense to you?

### Part 3: Test Set Predictions (5 pts)

Use `prepare_kaggle_submission(model, X_test, test_df, output_path)` to generate predictions on the test set.

1. Load `test-tfidf.csv`
2. Drop the `textid` column from the test TF-IDF data to get `X_test` features
3. Use your trained model to predict categories
4. Save a CSV with columns `['textid', 'category']` -- use the `textid` column from the original test DataFrame

### General Coding Style (15 pts)

- **10 points** for coding style: logical variable names, comments as needed, clean code
- **5 points** for code flow: accurate results when functions are called sequentially

## Functions to Implement

```python
import pandas as pd
import numpy as np
from pathlib import Path
from typing import Union, Dict, List, Tuple
from sklearn.decomposition import LatentDirichletAllocation, PCA
from sklearn.manifold import TSNE
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_score, roc_curve, auc, precision_recall_curve, average_precision_score, confusion_matrix, accuracy_score
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import label_binarize


def load_text_data(data_dir: Union[str, Path]) -> Dict[str, pd.DataFrame]:
    """Load bag_of_words.csv, tfidf.csv, and document_labels.csv.

    Args:
        data_dir: Path to directory containing the three CSV files

    Returns:
        Dict with keys 'bag_of_words', 'tfidf', 'labels',
        each mapping to a DataFrame
    """


def run_lda_topics(bow_features: pd.DataFrame, n_topics: int = 10) -> LatentDirichletAllocation:
    """Run LDA topic modeling on bag-of-words features.

    The input should NOT contain the textid column -- only word count columns.
    Use random_state=42 for reproducibility.

    Args:
        bow_features: DataFrame of word counts (no textid column)
        n_topics: Number of topics to extract (default 10)

    Returns:
        Fitted LatentDirichletAllocation model
    """


def get_top_topic_words(lda_model: LatentDirichletAllocation, feature_names: List[str], n_words: int = 15) -> List[List[str]]:
    """Extract the top N words for each topic from a fitted LDA model.

    Use lda_model.components_ to get the topic-word distribution matrix.
    For each topic, find the indices of the top n_words by weight and
    map them back to word strings using feature_names.

    Args:
        lda_model: Fitted LDA model
        feature_names: List of word strings matching the columns used to fit LDA
        n_words: Number of top words per topic (default 15)

    Returns:
        List of lists, where each inner list contains n_words word strings
        for that topic, ordered by descending weight
    """


def reduce_dimensions_pca(features: pd.DataFrame, n_components: int = 2) -> Tuple[np.ndarray, PCA]:
    """Reduce dimensionality using PCA.

    Args:
        features: DataFrame of numeric features (no textid column)
        n_components: Number of components (default 2)

    Returns:
        Tuple of (transformed_data as np.ndarray, fitted PCA model)
    """


def reduce_dimensions_tsne(features: pd.DataFrame, n_components: int = 2, random_state: int = 42) -> np.ndarray:
    """Reduce dimensionality using t-SNE.

    Args:
        features: DataFrame of numeric features (no textid column)
        n_components: Number of components (default 2)
        random_state: Random seed for reproducibility (default 42)

    Returns:
        Transformed data as np.ndarray
    """


def cluster_kmeans(features_2d: np.ndarray, k: int) -> KMeans:
    """Run K-Means clustering with k clusters.

    Use random_state=42 and n_init=10 for reproducibility.

    Args:
        features_2d: 2D array of features (e.g., from PCA)
        k: Number of clusters

    Returns:
        Fitted KMeans model
    """


def calculate_silhouette_scores(features_2d: np.ndarray, k_range: range = range(2, 11)) -> Dict:
    """Run K-means for each k in k_range and compute silhouette scores.

    For each k, fit a KMeans model (random_state=42) and compute:
    - The silhouette score using sklearn.metrics.silhouette_score
    - The inertia (model.inertia_) for the elbow plot

    Args:
        features_2d: 2D array of features (e.g., from PCA)
        k_range: Range of k values to test (default 2 to 10)

    Returns:
        Dict with keys:
        - 'k_values': list of int, the k values tested
        - 'inertias': list of float, inertia for each k (for elbow plot)
        - 'silhouette_scores': list of float, silhouette score for each k
    """


def train_classifier(X_train, y_train, X_val, y_val) -> Tuple:
    """Train a RandomForestClassifier and evaluate accuracy.

    Use RandomForestClassifier(n_estimators=100, random_state=42).

    Args:
        X_train: Training features (TF-IDF, no textid)
        y_train: Training category labels
        X_val: Validation features (TF-IDF, no textid)
        y_val: Validation category labels

    Returns:
        Tuple of (fitted model, training accuracy, validation accuracy)
    """


def calculate_roc_curve(model, X_val, y_val) -> Dict:
    """Compute per-class ROC curves using one-vs-rest approach.

    Steps:
    1. Get sorted unique classes from y_val
    2. Binarize labels: label_binarize(y_val, classes=sorted_classes)
    3. Get probabilities: model.predict_proba(X_val)
    4. For each class, compute roc_curve(y_bin[:, i], probs[:, i])
       and auc(fpr, tpr)

    Args:
        model: Fitted classifier with predict_proba method
        X_val: Validation features
        y_val: Validation labels

    Returns:
        Dict mapping category name (str) to dict with keys:
        - 'fpr': np.ndarray of false positive rates
        - 'tpr': np.ndarray of true positive rates
        - 'auc': float, area under the ROC curve
    """


def calculate_precision_recall(model, X_val, y_val) -> Dict:
    """Compute per-class precision-recall curves using one-vs-rest approach.

    Steps:
    1. Get sorted unique classes from y_val
    2. Binarize labels: label_binarize(y_val, classes=sorted_classes)
    3. Get probabilities: model.predict_proba(X_val)
    4. For each class, compute precision_recall_curve(y_bin[:, i], probs[:, i])
       and average_precision_score(y_bin[:, i], probs[:, i])

    Args:
        model: Fitted classifier with predict_proba method
        X_val: Validation features
        y_val: Validation labels

    Returns:
        Dict mapping category name (str) to dict with keys:
        - 'precision': np.ndarray of precision values
        - 'recall': np.ndarray of recall values
        - 'avg_precision': float, average precision score
    """


def create_confusion_matrix(y_true, y_pred) -> Tuple[np.ndarray, List[str]]:
    """Compute the confusion matrix.

    Args:
        y_true: True category labels
        y_pred: Predicted category labels

    Returns:
        Tuple of:
        - Confusion matrix as np.ndarray
        - Sorted list of class label strings
    """


def prepare_kaggle_submission(model, X_test, test_df, output_path) -> pd.DataFrame:
    """Generate Kaggle submission CSV.

    Use the trained model to predict categories for the test features.
    Save a CSV with columns ['textid', 'category'] to output_path.

    Args:
        model: Trained classifier with .predict() method
        X_test: Preprocessed test features (TF-IDF, no textid)
        test_df: Original test DataFrame (for the textid column)
        output_path: Path to save the submission CSV

    Returns:
        DataFrame with columns ['textid', 'category']
    """
```

## Hints

- **LDA on bag_of_words**: LDA expects raw word counts, not TF-IDF. Drop the `textid` column before fitting.
- **Feature names for LDA**: Use `bow_df.columns.tolist()` after dropping `textid` to get word names for `get_top_topic_words`.
- **t-SNE random_state**: Always use `random_state=42` for reproducibility.
- **Clustering on "other"**: Filter labels to `category == 'other'`, get those textids, filter TF-IDF to matching rows, drop `textid`, PCA to 2D, then K-means.
- **KMeans n_init**: Use `n_init=10` with KMeans for deterministic results (sklearn >= 1.4 changed the default to `'auto'`).
- **Silhouette range**: k=2 to 10 (cannot compute silhouette for k=1).
- **RandomForestClassifier**: Use `n_estimators=100, random_state=42`. Good default for multiclass text classification.
- **No scaling needed**: TF-IDF is already normalized. Bag of words is used as-is for LDA. No StandardScaler required.
- **Train/val split**: Use `train_test_split(X, y, test_size=0.20, random_state=42)` on the TF-IDF features and category labels.
- **ROC curves multiclass**: Use `label_binarize(y, classes=sorted_classes)` and `model.predict_proba()` to get per-class probabilities. Compute `roc_curve` and `auc` for each class separately.
- **Precision-recall multiclass**: Same binarization approach. Use `precision_recall_curve` and `average_precision_score` per class.
- **Confusion matrix labels**: Pass `labels=sorted(unique_categories)` to `sklearn.metrics.confusion_matrix` to ensure consistent ordering.
- **Submission format**: CSV with columns `['textid', 'category']`. Use `test-tfidf.csv` features (drop `textid` for prediction, keep `textid` for output).

## Grading

| Part | Points |
|------|--------|
| Part 1: Data Exploration | 50 |
| Part 2: Text Classification | 45 |
| Part 3: Test Set Predictions | 5 |
| **Total** | **100** |
