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

    def test_clean_scores_fractions(self):
        """Verify fraction scores are converted correctly."""
        test_df = pd.DataFrame({
            'review_score': ['3/5', '3/4', '87/100', '4/10', '92/10']
        })
        cleaned = solution.clean_review_scores(test_df)
        scores = cleaned['review_score'].tolist()

        # 3/5 = 60, 3/4 = 75, 87/100 = 87, 4/10 = 40
        # 92/10: numerator > denominator, should be treated as 92
        assert abs(scores[0] - 60.0) < 1, f"3/5 should be ~60, got {scores[0]}"
        assert abs(scores[1] - 75.0) < 1, f"3/4 should be ~75, got {scores[1]}"
        assert abs(scores[2] - 87.0) < 1, f"87/100 should be ~87, got {scores[2]}"
        assert abs(scores[3] - 40.0) < 1, f"4/10 should be ~40, got {scores[3]}"
        assert abs(scores[4] - 92.0) < 1, f"92/10 should be ~92, got {scores[4]}"

    def test_clean_scores_letters(self):
        """Verify letter grade scores are converted correctly."""
        test_df = pd.DataFrame({
            'review_score': ['A', 'A-', 'B+', 'B', 'C', 'F']
        })
        cleaned = solution.clean_review_scores(test_df)
        scores = cleaned['review_score'].tolist()

        # Check each is in expected range
        assert 93 <= scores[0] <= 100, f"A should be 93-100, got {scores[0]}"
        assert 90 <= scores[1] <= 94, f"A- should be 90-94, got {scores[1]}"
        assert 86 <= scores[2] <= 90, f"B+ should be 86-90, got {scores[2]}"
        assert 83 <= scores[3] <= 87, f"B should be 83-87, got {scores[3]}"
        assert 73 <= scores[4] <= 77, f"C should be 73-77, got {scores[4]}"
        assert 45 <= scores[5] <= 55, f"F should be 45-55, got {scores[5]}"

    def test_clean_scores_preserves_nan(self):
        """Verify NaN values are preserved, not converted."""
        test_df = pd.DataFrame({
            'review_score': ['A', None, np.nan, '', 'B']
        })
        cleaned = solution.clean_review_scores(test_df)
        scores = cleaned['review_score']

        # First and last should be valid numbers
        assert pd.notna(scores.iloc[0]), "A should be converted to a number"
        assert pd.notna(scores.iloc[4]), "B should be converted to a number"

        # Middle three should be NaN
        assert pd.isna(scores.iloc[1]), "None should remain NaN"
        assert pd.isna(scores.iloc[2]), "np.nan should remain NaN"
        assert pd.isna(scores.iloc[3]), "Empty string should become NaN"

    def test_clean_scores_range(self, cleaned_critic_df):
        """Verify cleaned scores are in valid range [0, 100] or NaN."""
        scores = cleaned_critic_df['review_score']

        # Non-null scores should be in [0, 100]
        valid_scores = scores.dropna()

        if len(valid_scores) > 0:
            min_score = valid_scores.min()
            max_score = valid_scores.max()

            assert min_score >= 0, f"Min score {min_score} is below 0"
            assert max_score <= 100, f"Max score {max_score} is above 100"

        # Should have converted many scores (not all NaN)
        non_null_pct = scores.notna().mean()
        assert non_null_pct > 0.5, \
            f"Expected >50% non-null scores after cleaning, got {non_null_pct:.1%}"


# =============================================================================
# Tier 3: Imputation Tests
# =============================================================================

class TestImputation:
    """Tier 3: Verify imputation methods work correctly."""

    def test_impute_zeros(self, critic_movie_matrix):
        """Verify zero imputation removes all NaN values."""
        imputed = solution.impute_missing_zeros(critic_movie_matrix.copy())

        # Should return DataFrame
        assert isinstance(imputed, pd.DataFrame)

        # Should have no NaN values
        nan_count = imputed.isna().sum().sum()
        assert nan_count == 0, f"Expected no NaN after zero imputation, got {nan_count}"

        # All values should be >= 0 (zeros and original scores)
        assert (imputed.values >= 0).all(), "All values should be >= 0"

    def test_impute_mean(self, critic_movie_matrix):
        """Verify mean imputation removes all NaN and uses reasonable values."""
        imputed = solution.impute_missing_mean(critic_movie_matrix.copy())

        # Should return DataFrame
        assert isinstance(imputed, pd.DataFrame)

        # Exclude audience_score column from NaN check if it exists
        critic_cols = [c for c in imputed.columns if c != 'audience_score']
        critic_data = imputed[critic_cols]

        # Should have no NaN in critic columns
        nan_count = critic_data.isna().sum().sum()
        assert nan_count == 0, \
            f"Expected no NaN in critic columns after mean imputation, got {nan_count}"

        # Values should be in reasonable range [0, 100]
        values = critic_data.values.flatten()
        valid_values = values[~np.isnan(values)]
        if len(valid_values) > 0:
            assert valid_values.min() >= 0, "Imputed values should be >= 0"
            assert valid_values.max() <= 100, "Imputed values should be <= 100"

    def test_impute_knn(self, critic_movie_matrix):
        """Verify KNN imputation produces reasonable values."""
        imputed = solution.impute_missing_knn(critic_movie_matrix.copy(), k=5)

        # Should return DataFrame
        assert isinstance(imputed, pd.DataFrame)

        # Exclude audience_score column from checks
        critic_cols = [c for c in imputed.columns if c != 'audience_score']
        critic_data = imputed[critic_cols]

        # Should have no NaN in critic columns
        nan_count = critic_data.isna().sum().sum()
        assert nan_count == 0, \
            f"Expected no NaN in critic columns after KNN imputation, got {nan_count}"

        # Values should be in reasonable range [0, 100]
        # KNN might produce slight out-of-range values, allow small tolerance
        values = critic_data.values.flatten()
        valid_values = values[~np.isnan(values)]
        if len(valid_values) > 0:
            assert valid_values.min() >= -5, "KNN values should be >= -5"
            assert valid_values.max() <= 105, "KNN values should be <= 105"


# =============================================================================
# Tier 4: Correlation Tests
# =============================================================================

class TestCorrelation:
    """Tier 4: Verify correlation calculations."""

    def test_correlation_zeros(self, imputed_zeros):
        """Verify correlation calculation with zero imputation."""
        correlations = solution.calculate_critic_correlation(imputed_zeros)

        # Should return Series
        assert isinstance(correlations, pd.Series), \
            "Should return a pandas Series"

        # All correlations should be in [-1, 1]
        assert (correlations >= -1).all() and (correlations <= 1).all(), \
            "Correlations should be in [-1, 1]"

        # Should have correlations for multiple critics
        assert len(correlations) > 10, \
            f"Expected correlations for >10 critics, got {len(correlations)}"

        # Should not include audience_score in the result
        assert 'audience_score' not in correlations.index, \
            "Should not include audience_score in correlations"

    def test_correlation_knn(self, imputed_knn):
        """Verify correlation calculation with KNN imputation."""
        correlations = solution.calculate_critic_correlation(imputed_knn)

        # Should return Series
        assert isinstance(correlations, pd.Series)

        # All correlations should be in [-1, 1]
        assert (correlations >= -1).all() and (correlations <= 1).all(), \
            "Correlations should be in [-1, 1]"

        # Top correlations should be positive (good critics predict audience)
        top_corr = correlations.nlargest(5)
        assert (top_corr > 0).all(), \
            "Top 5 correlations should be positive"

        # Best correlation should be reasonably strong (> 0.1)
        assert correlations.max() > 0.1, \
            f"Best correlation {correlations.max():.3f} seems too weak"


# =============================================================================
# Tier 5: PCA and Top Critics Tests
# =============================================================================

class TestPCAAndTopCritics:
    """Tier 5: Verify PCA and top critic identification."""

    def test_pca_dimensions(self, imputed_mean):
        """Verify PCA returns correct dimensions."""
        n_components = 2
        transformed, pca = solution.run_pca(imputed_mean, n_components=n_components)

        # Transformed should be numpy array
        assert isinstance(transformed, np.ndarray), \
            "Transformed data should be numpy array"

        # Should have correct shape (n_samples, n_components)
        n_samples = len(imputed_mean)
        assert transformed.shape == (n_samples, n_components), \
            f"Expected shape ({n_samples}, {n_components}), got {transformed.shape}"

        # PCA object should have explained_variance_ratio_
        assert hasattr(pca, 'explained_variance_ratio_'), \
            "PCA object should have explained_variance_ratio_ attribute"

        # Explained variance should sum to <= 1
        total_variance = pca.explained_variance_ratio_.sum()
        assert total_variance <= 1.0, \
            f"Explained variance ratio sum {total_variance} should be <= 1"

        # Should explain some variance (> 1%)
        assert total_variance > 0.01, \
            f"PCA should explain >1% variance, got {total_variance:.1%}"

    def test_top_critics_identified(self, imputed_mean):
        """Verify top correlated critics are identified correctly."""
        correlations = solution.calculate_critic_correlation(imputed_mean)
        top_critics = solution.get_top_correlated_critics(correlations, n=5)

        # Should return list
        assert isinstance(top_critics, list), "Should return a list"

        # Should have exactly n critics
        assert len(top_critics) == 5, f"Expected 5 critics, got {len(top_critics)}"

        # All items should be strings (critic names)
        assert all(isinstance(c, str) for c in top_critics), \
            "All items should be strings"

        # Top critics should be in the correlations index
        for critic in top_critics:
            assert critic in correlations.index, \
                f"Critic '{critic}' not found in correlations"

        # Top critics should have positive correlation
        for critic in top_critics:
            assert correlations[critic] > 0, \
                f"Top critic '{critic}' should have positive correlation"

        # Should be sorted by correlation (descending)
        critic_corrs = [correlations[c] for c in top_critics]
        assert critic_corrs == sorted(critic_corrs, reverse=True), \
            "Top critics should be sorted by correlation (highest first)"


# =============================================================================
# Integration Tests
# =============================================================================

class TestIntegration:
    """End-to-end workflow tests."""

    def test_full_workflow(self, critic_df, movie_df):
        """Test complete analysis workflow."""
        # 1. Load data (via fixtures)
        assert len(critic_df) > 1_000_000
        assert len(movie_df) > 15_000

        # 2. Clean and filter
        cleaned = solution.remove_unnamed_critics(critic_df)
        cleaned = solution.clean_review_scores(cleaned)
        filtered = solution.filter_critics_by_review_count(cleaned, threshold=500)

        assert len(filtered) < len(critic_df)

        # 3. Create matrix
        matrix = solution.prepare_critic_movie_matrix(filtered, movie_df)
        assert isinstance(matrix, pd.DataFrame)
        assert 'audience_score' in matrix.columns, \
            "Matrix must include audience_score column"
        assert len(matrix.columns) > 10, \
            "Matrix should have multiple critic columns"

        # 4. Impute (use mean for speed)
        imputed = solution.impute_missing_mean(matrix.copy())

        # 5. Calculate correlations
        correlations = solution.calculate_critic_correlation(imputed)
        assert len(correlations) > 10

        # 6. Get top critics
        top = solution.get_top_correlated_critics(correlations, n=5)
        assert len(top) == 5

        # 7. Run PCA
        transformed, pca = solution.run_pca(imputed, n_components=2)
        assert transformed.shape[1] == 2
