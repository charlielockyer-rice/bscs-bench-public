"""
COMP 341 Homework 2: Movie Critics

Find critics whose ratings best predict audience scores using data cleaning,
imputation, PCA, and correlation analysis.

Functions to implement:
- load_critic_data: Load critic reviews CSV
- load_movie_data: Load movies CSV
- remove_unnamed_critics: Filter out critics with no name
- filter_critics_by_review_count: Keep critics with >= threshold scored movies
- clean_review_scores: Normalize all scores to 0-100 scale
- prepare_critic_movie_matrix: Create pivot table of critics x movies
- impute_missing_zeros: Fill NaN with 0
- impute_missing_mean: Fill NaN with column mean
- impute_missing_knn: Fill NaN using KNN imputation
- calculate_critic_correlation: Pearson correlation with audience score
- get_top_correlated_critics: Top n critics by correlation
- run_pca: Principal Component Analysis
"""

import pandas as pd
import numpy as np
from pathlib import Path
from typing import Union, List, Tuple
from sklearn.impute import KNNImputer
from sklearn.decomposition import PCA


# =============================================================================
# Data Loading Functions
# =============================================================================

def load_critic_data(data_dir: Union[str, Path]) -> pd.DataFrame:
    """Load rotten_tomatoes_critic_reviews.csv.

    Args:
        data_dir: Path to directory containing the CSV file

    Returns:
        DataFrame with columns: rotten_tomatoes_link, critic_name, top_critic,
        publisher_name, review_type, review_score, review_date, review_content
    """
    # TODO: Implement this function
    # Hint: Use pd.read_csv()
    # Hint: File is ~226MB with 1.1M rows
    pass


def load_movie_data(data_dir: Union[str, Path]) -> pd.DataFrame:
    """Load rotten_tomatoes_movies.csv.

    Args:
        data_dir: Path to directory containing the CSV file

    Returns:
        DataFrame with movie information including audience_rating
    """
    # TODO: Implement this function
    pass


# =============================================================================
# Data Cleaning Functions
# =============================================================================

def remove_unnamed_critics(df: pd.DataFrame) -> pd.DataFrame:
    """Remove critics with no name (empty string or NaN critic_name).

    Args:
        df: Critic reviews DataFrame

    Returns:
        DataFrame with unnamed critics removed
    """
    # TODO: Implement this function
    # Hint: Check for both NaN and empty strings
    pass


def filter_critics_by_review_count(
    df: pd.DataFrame,
    threshold: int = 500
) -> pd.DataFrame:
    """Filter to keep only critics who have scored at least `threshold` movies.

    Important: Count movies with actual scores (non-NaN review_score),
    not just total reviews.

    Args:
        df: Critic reviews DataFrame
        threshold: Minimum number of scored movies

    Returns:
        DataFrame containing only reviews from critics who meet the threshold
    """
    # TODO: Implement this function
    # Hint: Group by critic_name, count non-null review_score
    # Hint: Filter to critics meeting threshold, then filter original df
    pass


def clean_review_scores(df: pd.DataFrame) -> pd.DataFrame:
    """Normalize all review_score values to 0-100 scale.

    Must handle these formats:
    - Fractions: "3/5" -> 60, "3/4" -> 75, "87/100" -> 87, "4/10" -> 40
    - Letter grades: "A" -> 95, "A-" -> 92, "B+" -> 88, "B" -> 85,
                     "B-" -> 82, "C+" -> 78, "C" -> 75, "C-" -> 72,
                     "D+" -> 68, "D" -> 65, "D-" -> 62, "F" -> 50
    - Direct numbers: Already in 0-100 range, keep as-is
    - Empty/NaN: Preserve as NaN (do not convert to 0)

    Edge cases to handle:
    - Fractions where numerator > denominator (e.g., "92/10"):
      These are likely already on a 0-100 scale, so use numerator directly
      (92/10 -> 92, not 9.2*100=920)
    - Values > 100: cap at 100 (likely data entry errors)

    Args:
        df: Critic reviews DataFrame with review_score column

    Returns:
        DataFrame with normalized review_score column (float, 0-100 or NaN)
    """
    # TODO: Implement this function
    # Hint: Create a helper function to convert a single score
    # Hint: Use df['review_score'].apply() with the helper
    # Hint: Handle fractions by splitting on '/' and dividing
    # Hint: Use a dictionary for letter grade mappings
    # Hint: Be careful with edge cases like "92/10" -> should be 92, not 920
    pass


# =============================================================================
# Matrix Preparation and Imputation Functions
# =============================================================================

def prepare_critic_movie_matrix(
    critic_df: pd.DataFrame,
    movie_df: pd.DataFrame
) -> pd.DataFrame:
    """Create pivot table: movies (rows) x critics (columns) with audience_score.

    Args:
        critic_df: Cleaned critic reviews DataFrame (with normalized scores)
        movie_df: Movies DataFrame with audience_rating

    Returns:
        DataFrame where:
        - Each row is a movie (indexed by rotten_tomatoes_link)
        - Each column is a critic (their normalized score for that movie)
        - Last column is 'audience_score' from movie_df
        - Missing values are NaN (critic didn't review that movie)
    """
    # TODO: Implement this function
    # Hint: Use pd.pivot_table() with critic_df
    # Hint: Join with movie_df on rotten_tomatoes_link to get audience_rating
    # Hint: Rename audience_rating to audience_score for clarity
    pass


def impute_missing_zeros(df: pd.DataFrame) -> pd.DataFrame:
    """Fill missing values with 0.

    Args:
        df: Critic-movie matrix with NaN values

    Returns:
        DataFrame with NaN replaced by 0
    """
    # TODO: Implement this function
    # Hint: Use df.fillna(0)
    pass


def impute_missing_mean(
    df: pd.DataFrame,
    exclude_cols: List[str] = None
) -> pd.DataFrame:
    """Fill missing values with column mean (per critic).

    Args:
        df: Critic-movie matrix with NaN values
        exclude_cols: Columns to exclude from imputation (e.g., ['audience_score'])

    Returns:
        DataFrame with NaN replaced by column means
    """
    # TODO: Implement this function
    # Hint: For each column (except excluded), fill NaN with that column's mean
    # Hint: df[col].fillna(df[col].mean())
    if exclude_cols is None:
        exclude_cols = ['audience_score']
    pass


def impute_missing_knn(
    df: pd.DataFrame,
    k: int = 5,
    exclude_cols: List[str] = None
) -> pd.DataFrame:
    """Fill missing values using KNN imputation.

    Uses k nearest neighbors to estimate missing values based on
    similar movies' scores.

    Args:
        df: Critic-movie matrix with NaN values
        k: Number of neighbors for KNN

    Returns:
        DataFrame with NaN replaced by KNN-imputed values
    """
    # TODO: Implement this function
    # Hint: Use sklearn.impute.KNNImputer
    # Hint: Exclude audience_score from imputation, add back after
    # Hint: KNNImputer returns numpy array, convert back to DataFrame
    if exclude_cols is None:
        exclude_cols = ['audience_score']
    pass


# =============================================================================
# Analysis Functions
# =============================================================================

def calculate_critic_correlation(
    df: pd.DataFrame,
    target_col: str = 'audience_score'
) -> pd.Series:
    """Calculate Pearson correlation between each critic and the target.

    Args:
        df: Imputed critic-movie matrix (no NaN values)
        target_col: Column to correlate against (default: audience_score)

    Returns:
        Series with critic names as index and correlation coefficients as values
    """
    # TODO: Implement this function
    # Hint: Use df.corrwith() or calculate manually with df.corr()
    # Hint: Exclude target_col from the result
    pass


def get_top_correlated_critics(
    correlations: pd.Series,
    n: int = 5
) -> List[str]:
    """Return the top n critic names most positively correlated with audience score.

    Args:
        correlations: Series of correlation values (critic name -> correlation)
        n: Number of top critics to return

    Returns:
        List of critic names, sorted by correlation (highest first)
    """
    # TODO: Implement this function
    # Hint: Use .nlargest() or .sort_values(ascending=False)
    pass


def run_pca(
    df: pd.DataFrame,
    n_components: int = 2,
    exclude_cols: List[str] = None
) -> Tuple[np.ndarray, PCA]:
    """Run PCA on the imputed critic-movie matrix.

    Args:
        df: Imputed critic-movie matrix
        n_components: Number of principal components
        exclude_cols: Columns to exclude (e.g., ['audience_score'])

    Returns:
        Tuple of:
        - Transformed data (n_samples, n_components)
        - Fitted PCA object (for explained_variance_ratio_)
    """
    # TODO: Implement this function
    # Hint: Use sklearn.decomposition.PCA
    # Hint: Exclude audience_score before fitting
    # Hint: Return both transformed data and PCA object
    if exclude_cols is None:
        exclude_cols = ['audience_score']
    pass


# =============================================================================
# Optional: Bias Analysis (Bonus)
# =============================================================================

def calculate_critic_bias(df: pd.DataFrame) -> pd.DataFrame:
    """Calculate fresh/rotten percentages per critic.

    Args:
        df: Critic reviews DataFrame with review_type column

    Returns:
        DataFrame with columns: critic_name, fresh_pct, rotten_pct, total_reviews
    """
    # TODO: Implement if attempting bonus points
    # Hint: Group by critic_name, count review_type values
    # Hint: Calculate percentages
    pass
