"""
COMP 341 Homework 2: Movie Critics - Test Suite

Tests organized by tier:
- Tier 1: Data loading (must pass)
- Tier 2: Filtering and cleaning
- Tier 3: Imputation methods
- Tier 4: Correlation analysis
- Tier 5: PCA and top critics
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
    Path(__file__).parent.parent / 'data')) / 'hw2'
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
def critic_df():
    """Load critic reviews data once for all tests."""
    if not SOLUTION_AVAILABLE:
        pytest.skip("solution.py not available")
    df = solution.load_critic_data(DATA_DIR)
    return df


@pytest.fixture(scope="module")
def movie_df():
    """Load movie data once for all tests."""
    if not SOLUTION_AVAILABLE:
        pytest.skip("solution.py not available")
    df = solution.load_movie_data(DATA_DIR)
    return df


@pytest.fixture(scope="module")
def cleaned_critic_df(critic_df):
    """Get cleaned critic DataFrame (unnamed removed, scores normalized)."""
    df = solution.remove_unnamed_critics(critic_df)
    df = solution.clean_review_scores(df)
    return df


@pytest.fixture(scope="module")
def filtered_critic_df(cleaned_critic_df):
    """Get filtered critic DataFrame (500+ reviews)."""
    return solution.filter_critics_by_review_count(cleaned_critic_df, threshold=500)


@pytest.fixture(scope="module")
def critic_movie_matrix(filtered_critic_df, movie_df):
    """Get the critic-movie pivot matrix."""
    return solution.prepare_critic_movie_matrix(filtered_critic_df, movie_df)


@pytest.fixture(scope="module")
def imputed_zeros(critic_movie_matrix):
    """Matrix imputed with zeros."""
    return solution.impute_missing_zeros(critic_movie_matrix.copy())


@pytest.fixture(scope="module")
def imputed_mean(critic_movie_matrix):
    """Matrix imputed with column means."""
    return solution.impute_missing_mean(critic_movie_matrix.copy())


@pytest.fixture(scope="module")
def imputed_knn(critic_movie_matrix):
    """Matrix imputed with KNN."""
    return solution.impute_missing_knn(critic_movie_matrix.copy(), k=5)


# =============================================================================
# Tier 1: Data Loading Tests
# =============================================================================

class TestDataLoading:
    """Tier 1: Verify data is loaded correctly."""

    def test_load_critic_data(self):
        """Verify load_critic_data returns a DataFrame with correct structure."""
        df = solution.load_critic_data(DATA_DIR)

        # Should return DataFrame
        assert isinstance(df, pd.DataFrame), "Should return a pandas DataFrame"

        # Required columns
        required_cols = {'rotten_tomatoes_link', 'critic_name', 'review_score'}
        actual_cols = set(df.columns)
        assert required_cols.issubset(actual_cols), \
            f"Missing columns: {required_cols - actual_cols}"

        # Should have ~1.1M rows (accept range for different data versions)
        row_count = len(df)
        assert 1_000_000 < row_count < 1_500_000, \
            f"Expected ~1.1M rows, got {row_count:,}"

    def test_load_movie_data(self):
        """Verify load_movie_data returns a DataFrame with correct structure."""
        df = solution.load_movie_data(DATA_DIR)

        # Should return DataFrame
        assert isinstance(df, pd.DataFrame), "Should return a pandas DataFrame"

        # Required columns
        required_cols = {'rotten_tomatoes_link', 'audience_rating'}
        actual_cols = set(df.columns)
        assert required_cols.issubset(actual_cols), \
            f"Missing columns: {required_cols - actual_cols}"

        # Should have ~17K movies
        row_count = len(df)
        assert 15_000 < row_count < 20_000, \
            f"Expected ~17K rows, got {row_count:,}"


# =============================================================================
# Tier 2: Filtering and Cleaning Tests
# =============================================================================

class TestFilteringAndCleaning:
    """Tier 2: Verify filtering and score cleaning."""

    def test_remove_unnamed_critics(self, critic_df):
        """Verify unnamed critics are removed."""
        cleaned = solution.remove_unnamed_critics(critic_df)

        # Should return DataFrame
        assert isinstance(cleaned, pd.DataFrame)

        # No NaN in critic_name
        assert cleaned['critic_name'].notna().all(), \
            "Should have no NaN critic names"

        # No empty strings in critic_name
        empty_names = (cleaned['critic_name'].str.strip() == '').sum()
        assert empty_names == 0, \
            f"Should have no empty critic names, got {empty_names}"

        # Should have reduced the dataset
        assert len(cleaned) < len(critic_df), \
            "Should remove some rows with unnamed critics"

    def test_filter_critics(self, cleaned_critic_df):
        """Verify filter_critics_by_review_count works correctly."""
        threshold = 500
        filtered = solution.filter_critics_by_review_count(
            cleaned_critic_df, threshold=threshold
        )

        # Should return DataFrame
        assert isinstance(filtered, pd.DataFrame)

        # Should significantly reduce the dataset
        assert len(filtered) < len(cleaned_critic_df), \
            "Filtering should reduce the dataset"

        # All remaining critics should have >= threshold scored reviews
        scores_per_critic = filtered.groupby('critic_name')['review_score'].apply(
            lambda x: x.notna().sum()
        )

        # All critics must meet threshold (strict check)
        assert (scores_per_critic >= threshold).all(), \
            f"All critics should have >= {threshold} scored reviews"

        # Should have reasonable number of critics (varies by data version)
        unique_critics = filtered['critic_name'].nunique()
        assert 20 < unique_critics < 600, \
            f"Expected 20-600 critics after filtering, got {unique_critics}"

