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

    def test_load_data(self, text_data):
        """Verify load_text_data returns dict with expected structure."""
        # Check dict has expected keys
        assert set(text_data.keys()) == {'bag_of_words', 'tfidf', 'labels'}, \
            f"Expected keys {{'bag_of_words', 'tfidf', 'labels'}}, " \
            f"got {set(text_data.keys())}"

        # Check shapes
        assert text_data['bag_of_words'].shape == (560, 1119), \
            f"bag_of_words shape should be (560, 1119), " \
            f"got {text_data['bag_of_words'].shape}"
        assert text_data['tfidf'].shape == (560, 1119), \
            f"tfidf shape should be (560, 1119), " \
            f"got {text_data['tfidf'].shape}"
        assert text_data['labels'].shape == (560, 3), \
            f"labels shape should be (560, 3), " \
            f"got {text_data['labels'].shape}"

        # Check textid column exists in bow and tfidf
        assert 'textid' in text_data['bag_of_words'].columns, \
            "bag_of_words should have 'textid' column"
        assert 'textid' in text_data['tfidf'].columns, \
            "tfidf should have 'textid' column"

        # Check labels columns
        assert 'category' in text_data['labels'].columns, \
            "labels should have 'category' column"
        assert 'textid' in text_data['labels'].columns, \
            "labels should have 'textid' column"


# =============================================================================
# LDA Topic Modeling
# =============================================================================

class TestLDATopics:
    """Verify LDA topic modeling."""

    def test_lda_topics(self, lda_model):
        """Verify LDA model is fitted with correct number of topics."""
        from sklearn.decomposition import LatentDirichletAllocation

        # Check type
        assert isinstance(lda_model, LatentDirichletAllocation), \
            f"Should return LatentDirichletAllocation, " \
            f"got {type(lda_model).__name__}"

        # Check n_components
        assert lda_model.n_components == 10, \
            f"Should have 10 topics, got {lda_model.n_components}"

        # Check it's fitted (has components_)
        assert hasattr(lda_model, 'components_'), \
            "LDA model should be fitted (have components_ attribute)"

        # Check shape: (n_topics, n_features) = (10, 1118)
        assert lda_model.components_.shape[0] == 10, \
            f"components_ should have 10 rows (topics), " \
            f"got {lda_model.components_.shape[0]}"
        assert lda_model.components_.shape[1] == 1118, \
            f"components_ should have 1118 columns (features), " \
            f"got {lda_model.components_.shape[1]}"


# =============================================================================
# Top Words Per Topic
# =============================================================================

class TestTopWordsPerTopic:
    """Verify extraction of top words per topic."""

    def test_top_words_per_topic(self, lda_model, feature_names):
        """Verify get_top_topic_words returns correct structure."""
        top_words = solution.get_top_topic_words(lda_model, feature_names, n_words=15)

        # Should be list of 10 lists
        assert len(top_words) == 10, \
            f"Should return 10 topic word lists, got {len(top_words)}"

        # Each inner list should have 15 words
        for i, topic_words in enumerate(top_words):
            assert len(topic_words) == 15, \
                f"Topic {i} should have 15 words, got {len(topic_words)}"

            # Each word should be a string
            for word in topic_words:
                assert isinstance(word, str), \
                    f"Each word should be a string, got {type(word).__name__}"

            # Words should be unique within a topic
            assert len(set(topic_words)) == 15, \
                f"Topic {i} should have 15 unique words, " \
                f"got {len(set(topic_words))} unique"


# =============================================================================
# PCA Reduction
# =============================================================================

class TestPCAReduction:
    """Verify PCA dimensionality reduction."""

