"""
COMP 341 Homework 5: Who Said It? (Text Classification) - Test Suite

Tests organized by functional area:
- Data loading (bag-of-words, tfidf, labels)
- LDA topic modeling
- Dimensionality reduction (PCA, t-SNE)
- K-means clustering and silhouette analysis
- Multiclass classification (Random Forest)
- Evaluation metrics (ROC, Precision-Recall, Confusion Matrix)
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
    Path(__file__).parent.parent / 'data')) / 'hw5'
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


# =============================================================================
# Fixtures
# =============================================================================

@pytest.fixture(scope="module")
def text_data():
    """Load all three CSVs using load_text_data."""
    if not SOLUTION_AVAILABLE:
        pytest.skip("solution.py not available")
    return solution.load_text_data(DATA_DIR)


@pytest.fixture(scope="module")
def bow_features(text_data):
    """Bag-of-words features with textid dropped."""
    bow = text_data['bag_of_words'].drop(columns=['textid'])
    return bow


@pytest.fixture(scope="module")
def tfidf_features(text_data):
    """Tfidf features with textid dropped."""
    tfidf = text_data['tfidf'].drop(columns=['textid'])
    return tfidf


@pytest.fixture(scope="module")
def labels(text_data):
    """Category labels series."""
    return text_data['labels']['category']


@pytest.fixture(scope="module")
def feature_names(text_data):
    """Word column names from bag_of_words."""
    return text_data['bag_of_words'].drop(columns=['textid']).columns.tolist()


@pytest.fixture(scope="module")
def train_val_split(tfidf_features, labels):
    """80/20 train/val split."""
    from sklearn.model_selection import train_test_split
    X_train, X_val, y_train, y_val = train_test_split(
        tfidf_features, labels, test_size=0.20, random_state=42
    )
    return X_train, X_val, y_train, y_val


@pytest.fixture(scope="module")
def lda_model(bow_features):
    """Fitted LDA model."""
    return solution.run_lda_topics(bow_features, n_topics=10)


@pytest.fixture(scope="module")
def trained_classifier(train_val_split):
    """Trained RF classifier and metrics."""
    X_train, X_val, y_train, y_val = train_val_split
    return solution.train_classifier(X_train, y_train, X_val, y_val)


@pytest.fixture(scope="module")
def other_features_2d(tfidf_features, text_data):
    """PCA-reduced 2D features for 'other' category only."""
    other_mask = text_data['labels']['category'] == 'other'
    other_tfidf = tfidf_features[other_mask]
    transformed, _ = solution.reduce_dimensions_pca(other_tfidf, n_components=2)
    return transformed


@pytest.fixture(scope="module")
def holdout_df():
    """Load test tfidf for Kaggle submission."""
    return pd.read_csv(DATA_DIR / 'test-tfidf.csv')


# =============================================================================
# Data Loading
# =============================================================================

class TestLoadData:
    """Verify text data loads correctly."""

    def test_pca_reduction(self, tfidf_features):
        """Verify PCA reduces to correct dimensions."""
        from sklearn.decomposition import PCA

        transformed, pca_model = solution.reduce_dimensions_pca(
            tfidf_features, n_components=2
        )

        # Check output types
        assert isinstance(transformed, np.ndarray), \
            f"Transformed data should be np.ndarray, " \
            f"got {type(transformed).__name__}"
        assert isinstance(pca_model, PCA), \
            f"Model should be PCA, got {type(pca_model).__name__}"

        # Check shape
        assert transformed.shape == (560, 2), \
            f"Transformed shape should be (560, 2), got {transformed.shape}"

        # Check PCA is fitted
        assert hasattr(pca_model, 'explained_variance_ratio_'), \
            "PCA model should be fitted (have explained_variance_ratio_)"
        assert len(pca_model.explained_variance_ratio_) == 2, \
            f"Should have 2 explained variance ratios, " \
            f"got {len(pca_model.explained_variance_ratio_)}"


# =============================================================================
# t-SNE Reduction
# =============================================================================

class TestTSNEReduction:
    """Verify t-SNE dimensionality reduction."""

    def test_tsne_reduction(self, tfidf_features):
        """Verify t-SNE reduces to correct dimensions."""
        transformed = solution.reduce_dimensions_tsne(
            tfidf_features, n_components=2, random_state=42
        )

        # Check type and shape
        assert isinstance(transformed, np.ndarray), \
            f"Transformed data should be np.ndarray, " \
            f"got {type(transformed).__name__}"
        assert transformed.shape == (560, 2), \
            f"Transformed shape should be (560, 2), got {transformed.shape}"

        # Check values are finite
        assert np.all(np.isfinite(transformed)), \
            "All transformed values should be finite (no NaN or Inf)"


# =============================================================================
# K-Means Clustering
# =============================================================================

class TestKMeansClustering:
    """Verify K-means clustering on 'other' category."""

    def test_kmeans_clustering(self, other_features_2d):
        """Verify K-means with k=3 on PCA-reduced 'other' features."""
        from sklearn.cluster import KMeans

        # Test with k=3
        model = solution.cluster_kmeans(other_features_2d, k=3)

        assert isinstance(model, KMeans), \
            f"Should return KMeans, got {type(model).__name__}"
        assert model.n_clusters == 3, \
            f"Should have 3 clusters, got {model.n_clusters}"
        assert hasattr(model, 'labels_'), \
            "KMeans model should be fitted (have labels_ attribute)"
        assert len(model.labels_) == 105, \
            f"Should have 105 labels (105 'other' samples), " \
            f"got {len(model.labels_)}"

        # Labels should be 0, 1, or 2
        assert set(model.labels_).issubset({0, 1, 2}), \
            f"Labels should be in {{0, 1, 2}}, got {set(model.labels_)}"


# =============================================================================
# Silhouette Analysis
# =============================================================================

class TestSilhouetteAnalysis:
    """Verify silhouette score computation across k values."""

    def test_silhouette_analysis(self, other_features_2d):
        """Verify silhouette scores for k=2 to k=10."""
        result = solution.calculate_silhouette_scores(
            other_features_2d, k_range=range(2, 11)
        )

        # Check keys
        assert 'k_values' in result, \
            "Result should contain 'k_values'"
        assert 'inertias' in result, \
            "Result should contain 'inertias'"
        assert 'silhouette_scores' in result, \
            "Result should contain 'silhouette_scores'"

        # Check lengths (k=2 to 10 = 9 values)
        assert len(result['k_values']) == 9, \
            f"Should have 9 k_values (2-10), got {len(result['k_values'])}"
        assert len(result['inertias']) == 9, \
            f"Should have 9 inertias, got {len(result['inertias'])}"
        assert len(result['silhouette_scores']) == 9, \
            f"Should have 9 silhouette_scores, got {len(result['silhouette_scores'])}"

        # k_values should be 2-10
        assert list(result['k_values']) == list(range(2, 11)), \
            f"k_values should be [2, 3, ..., 10], got {list(result['k_values'])}"

        # Silhouette scores should be between -1 and 1
        for i, s in enumerate(result['silhouette_scores']):
            assert -1 <= s <= 1, \
                f"Silhouette score at k={i+2} should be in [-1, 1], got {s}"

        # Inertias should be positive and generally decreasing
        for i, inertia in enumerate(result['inertias']):
            assert inertia > 0, \
                f"Inertia at k={i+2} should be positive, got {inertia}"


# =============================================================================
# Classifier Accuracy
# =============================================================================

class TestClassifierAccuracy:
    """Verify Random Forest classifier training and accuracy."""

    def test_classifier_accuracy(self, trained_classifier):
        """Verify classifier returns model with reasonable accuracy."""
        from sklearn.ensemble import RandomForestClassifier

        model, train_acc, val_acc = trained_classifier

        # Check model type
        assert isinstance(model, RandomForestClassifier), \
            f"Should return RandomForestClassifier, " \
            f"got {type(model).__name__}"

        # Check accuracies are reasonable (RF on text should get >60%)
        assert 0.60 <= train_acc <= 1.0, \
            f"Training accuracy {train_acc:.4f} should be in [0.60, 1.0]"
        assert 0.50 <= val_acc <= 1.0, \
            f"Validation accuracy {val_acc:.4f} should be in [0.50, 1.0]"

        # Training accuracy should be >= validation accuracy
        # (RF typically overfits somewhat)
        assert train_acc >= val_acc * 0.9, \
            f"Training accuracy {train_acc:.4f} is surprisingly low " \
            f"compared to validation {val_acc:.4f}"


# =============================================================================
# ROC Curve
# =============================================================================

class TestROCCurve:
    """Verify ROC curve computation for multiclass classification."""

    def test_roc_curve(self, trained_classifier, train_val_split):
        """Verify ROC data for each category."""
        model = trained_classifier[0]
        _, X_val, _, y_val = train_val_split

        roc_data = solution.calculate_roc_curve(model, X_val, y_val)

        # Should have entry for each of 4 categories
        expected_cats = sorted(['academic', 'actor', 'other', 'president'])
        assert sorted(roc_data.keys()) == expected_cats, \
            f"Should have keys {expected_cats}, " \
            f"got {sorted(roc_data.keys())}"

        # Each entry should have fpr, tpr, auc
        for cat, data in roc_data.items():
            assert 'fpr' in data, \
                f"ROC data for '{cat}' should contain 'fpr'"
            assert 'tpr' in data, \
                f"ROC data for '{cat}' should contain 'tpr'"
            assert 'auc' in data, \
                f"ROC data for '{cat}' should contain 'auc'"

            # AUC should be between 0.3 and 1.0 for a decent classifier
            assert 0.3 <= data['auc'] <= 1.0, \
                f"AUC for '{cat}' = {data['auc']:.4f} should be in [0.3, 1.0]"

            # fpr and tpr should be arrays of same length
            assert len(data['fpr']) == len(data['tpr']), \
                f"fpr and tpr for '{cat}' should have same length, " \
                f"got {len(data['fpr'])} and {len(data['tpr'])}"
            assert len(data['fpr']) > 2, \
                f"fpr for '{cat}' should have more than 2 points, " \
                f"got {len(data['fpr'])}"


# =============================================================================
# Precision-Recall
# =============================================================================

class TestPrecisionRecall:
    """Verify Precision-Recall curve computation."""

    def test_precision_recall(self, trained_classifier, train_val_split):
        """Verify precision-recall data for each category."""
        model = trained_classifier[0]
        _, X_val, _, y_val = train_val_split

        pr_data = solution.calculate_precision_recall(model, X_val, y_val)

        # Should have entry for each of 4 categories
        expected_cats = sorted(['academic', 'actor', 'other', 'president'])
        assert sorted(pr_data.keys()) == expected_cats, \
            f"Should have keys {expected_cats}, " \
            f"got {sorted(pr_data.keys())}"

        # Each entry should have precision, recall, avg_precision
        for cat, data in pr_data.items():
            assert 'precision' in data, \
                f"PR data for '{cat}' should contain 'precision'"
            assert 'recall' in data, \
                f"PR data for '{cat}' should contain 'recall'"
            assert 'avg_precision' in data, \
                f"PR data for '{cat}' should contain 'avg_precision'"

            # avg_precision should be between 0 and 1
            assert 0.0 <= data['avg_precision'] <= 1.0, \
                f"avg_precision for '{cat}' = {data['avg_precision']:.4f} " \
                f"should be in [0.0, 1.0]"

            # precision and recall should be arrays of same length
            assert len(data['precision']) == len(data['recall']), \
                f"precision and recall for '{cat}' should have same length, " \
                f"got {len(data['precision'])} and {len(data['recall'])}"


# =============================================================================
# Confusion Matrix
# =============================================================================

class TestConfusionMatrix:
    """Verify confusion matrix computation."""

    def test_confusion_matrix(self, trained_classifier, train_val_split):
        """Verify confusion matrix shape and contents."""
        model = trained_classifier[0]
        _, X_val, _, y_val = train_val_split

        y_pred = model.predict(X_val)
        cm, class_labels = solution.create_confusion_matrix(y_val, y_pred)

        # Check shape: 4x4 for 4 categories
        assert cm.shape == (4, 4), \
            f"Confusion matrix shape should be (4, 4), got {cm.shape}"

        # Check class labels
        assert sorted(class_labels) == sorted(['academic', 'actor', 'other', 'president']), \
            f"Class labels should be the 4 categories, " \
            f"got {sorted(class_labels)}"

        # Diagonal should be non-negative (correct predictions)
        for i in range(4):
            assert cm[i, i] >= 0, \
                f"Diagonal element cm[{i},{i}] should be non-negative, " \
                f"got {cm[i, i]}"

        # Sum should equal number of validation samples
        assert cm.sum() == len(y_val), \
            f"Confusion matrix sum ({cm.sum()}) should equal " \
            f"number of validation samples ({len(y_val)})"


# =============================================================================
# Kaggle Submission
# =============================================================================

class TestSubmissionFormat:
    """Verify Kaggle submission format."""

    def test_submission_format(self, trained_classifier, holdout_df):
        """Verify submission has correct format with textid and category."""
        model = trained_classifier[0]
        X_test = holdout_df.drop(columns=['textid'])

        with tempfile.NamedTemporaryFile(suffix='.csv', delete=False) as f:
            output_path = f.name

        try:
            submission = solution.prepare_kaggle_submission(
                model, X_test, holdout_df, output_path
            )

            # Check columns
            assert list(submission.columns) == ['textid', 'category'], \
                f"Should have columns ['textid', 'category'], " \
                f"got {list(submission.columns)}"

            # Check row count matches test data
            assert len(submission) == 181, \
                f"Should have 181 rows, got {len(submission)}"

            # Check textid values match
            assert list(submission['textid']) == list(holdout_df['textid']), \
                "textid values should match holdout data"

            # Check categories are valid
            valid_cats = {'academic', 'actor', 'other', 'president'}
            assert set(submission['category']).issubset(valid_cats), \
                f"All predicted categories should be in {valid_cats}, " \
                f"got {set(submission['category'])}"

            # Check file was saved
            assert os.path.exists(output_path), \
                f"Submission file should be saved at {output_path}"
            saved = pd.read_csv(output_path)
            assert list(saved.columns) == ['textid', 'category'], \
                f"Saved file should have columns ['textid', 'category'], " \
                f"got {list(saved.columns)}"
            assert len(saved) == 181, \
                f"Saved file should have 181 rows, got {len(saved)}"

        finally:
            if os.path.exists(output_path):
                os.unlink(output_path)
