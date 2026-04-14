# COMP 341 HW2: Movie Critics - Implementation Plan

## Assignment Overview

**Topic:** Movie Critics Analysis - Finding critics correlated with audience ratings using data cleaning, imputation, PCA, and correlation analysis.

**Data Files:**
- `rotten_tomatoes_critic_reviews.csv` - 1,130,017 rows, 8 columns (226 MB)
- `rotten_tomatoes_movies.csv` - 17,712 rows, 22 columns (17 MB)

**Key Columns:**
- Critics: `rotten_tomatoes_link`, `critic_name`, `top_critic`, `publisher_name`, `review_type`, `review_score`, `review_date`, `review_content`
- Movies: `rotten_tomatoes_link`, `movie_title`, `audience_rating`, `audience_count`, `tomatometer_rating`, `genres`, etc.

---

## Functions to Implement

### 1. Data Loading Functions

```python
def load_critic_data(data_dir: Union[str, Path]) -> pd.DataFrame:
    """Load rotten_tomatoes_critic_reviews.csv."""

def load_movie_data(data_dir: Union[str, Path]) -> pd.DataFrame:
    """Load rotten_tomatoes_movies.csv."""
```

### 2. Data Cleaning Functions

```python
def remove_unnamed_critics(df: pd.DataFrame) -> pd.DataFrame:
    """Remove critics with no name (empty or NaN critic_name)."""

def filter_critics_by_review_count(df: pd.DataFrame, threshold: int = 500) -> pd.DataFrame:
    """Filter to keep only critics who have scored at least `threshold` movies.
    Note: Must check for actual scores (non-NaN review_score), not just reviews."""

def clean_review_scores(df: pd.DataFrame) -> pd.DataFrame:
    """Normalize all review_score values to 0-100 scale.
    Must handle:
    - Fractions: 3/5, 3/4, 4/10, 87/100, etc.
    - Letter grades: A, A-, B+, B, B-, C+, C, C-, D+, D, D-, F
    - Empty/NaN values (preserve as NaN)
    - Direct numbers: 3, 4, 5, etc."""
```

### 3. Imputation Functions

```python
def prepare_critic_movie_matrix(critic_df: pd.DataFrame, movie_df: pd.DataFrame) -> pd.DataFrame:
    """Create pivot table: movies (rows) x critics (columns) + audience_score."""

def impute_missing_zeros(df: pd.DataFrame) -> pd.DataFrame:
    """Fill missing values with 0."""

def impute_missing_mean(df: pd.DataFrame, exclude_cols: List[str] = ['audience_score']) -> pd.DataFrame:
    """Fill missing values with mean across movies (per critic column)."""

def impute_missing_knn(df: pd.DataFrame, k: int = 5, exclude_cols: List[str] = ['audience_score']) -> pd.DataFrame:
    """Fill missing values using KNN imputation with k neighbors."""
```

### 4. Analysis Functions

```python
def calculate_critic_correlation(df: pd.DataFrame, target_col: str = 'audience_score') -> pd.Series:
    """Calculate Pearson correlation between each critic and the target."""

def get_top_correlated_critics(correlations: pd.Series, n: int = 5) -> List[str]:
    """Return the top n critic names most correlated with audience score."""

def run_pca(df: pd.DataFrame, n_components: int = 2, exclude_cols: List[str] = ['audience_score']) -> Tuple[np.ndarray, PCA]:
    """Run PCA on the imputed data."""
```

### 5. Bias Analysis Functions

```python
def calculate_critic_bias(df: pd.DataFrame) -> pd.DataFrame:
    """Calculate fresh/rotten percentages per critic."""
```

---

## Testing Strategy

### Test Tiers

**Tier 1: Data Loading (10 points)**
- `test_load_critic_data` - DataFrame loaded with correct columns (5 pts)
- `test_load_movie_data` - DataFrame loaded with correct columns (5 pts)

**Tier 2: Filtering (10 points)**
- `test_filter_critics_by_count` - Filter works correctly (threshold = 500) (5 pts)
- `test_filter_removes_unnamed` - Unnamed critics removed (5 pts)

**Tier 3: Score Cleaning (20 points)**
- `test_clean_scores_fractions` - Correctly converts X/5, X/4, X/10 formats (5 pts)
- `test_clean_scores_letters` - Correctly converts A-F letter grades (5 pts)
- `test_clean_scores_range` - All values fall in [0, 100] (5 pts)
- `test_clean_scores_preserves_nan` - NaN values preserved, not converted (5 pts)

**Tier 4: Imputation (25 points)**
- `test_impute_zeros_no_nan` - NaN replaced with 0, no NaN remaining (5 pts)
- `test_impute_mean_values` - NaN replaced with column mean (10 pts)
- `test_impute_knn_reasonable` - KNN imputation produces reasonable values (10 pts)

**Tier 5: Correlation (20 points)**
- `test_correlation_range` - All correlations in [-1, 1] (5 pts)
- `test_correlation_zeros_method` - Correlation calculated correctly (5 pts)
- `test_correlation_knn_method` - Correlation after KNN imputation (5 pts)
- `test_top_critics_count` - Top 5 critics returned in correct order (5 pts)

**Tier 6: PCA (10 points)**
- `test_pca_dimensions` - Output has correct shape (n_samples, n_components) (5 pts)
- `test_pca_explained_variance` - PCA object has explained variance ratio (5 pts)

**Tier 7: Bias Analysis (5 points - optional/bonus)**
- `test_critic_bias` - Fresh percentage calculated correctly (5 pts)

### Testing Considerations

1. **Score Cleaning Validation:**
   - Verify specific conversions: `3/5` -> 60, `3/4` -> 75, `B+` -> ~88
   - Use `pytest.approx()` for floating point comparisons

2. **Imputation Tolerances:**
   - Mean imputation: verify no NaN remains, values within data range
   - KNN imputation: use larger tolerance (values should be reasonable, not exact)

3. **Correlation Testing:**
   - Cannot test exact critic rankings (data-dependent)
   - Verify top critics have positive correlation > 0.3
   - Verify correlation values are in [-1, 1]

4. **PCA Testing:**
   - Verify output shape
   - Verify explained variance ratio sums < 1
   - Do not test exact loadings (data-dependent)

5. **Data Subset for Speed:**
   - Consider creating a small test fixture with ~100 critics, ~500 movies
   - Use this for unit tests, full data for integration tests

---

## Template Structure

### `instructions.md` Outline

```markdown
# Homework 2: Movie Critics

## Overview
Find critics whose ratings best predict audience scores using data cleaning,
imputation, and correlation analysis.

## Tasks

### Part 0: Getting to Know the Data (11 pts)
- Load both CSV files
- Remove critics with no name
- Determine number of unique critics
- Plot distribution of movies reviewed per critic

### Part 1: Reviewer Bias (9 pts)
- Calculate fresh vs rotten review percentages per critic
- Visualize bias distribution

### Part 2: Cleaning Scores (15 pts)
- Convert all review_score values to 0-100 scale
- Handle: fractions (X/5, X/4, X/10, X/100), letters (A-F), empty

### Part 3: Handling Missing Values & PCA (50 pts)
- Create critic x movie matrix with audience scores
- Three imputation methods: zeros, mean, KNN
- Calculate correlation with audience score for each method
- Identify top 5 correlated critics
- Run PCA and visualize
```

### `solution.py` Skeleton

See `templates/hw2/solution.py` for the full skeleton with function stubs.

---

## Data Dependencies and Gotchas

1. **Large Data Size:**
   - Critic reviews: 1.1M rows, 226MB
   - May need to use data subsets for faster testing

2. **Score Cleaning Complexity:**
   - 300K+ empty scores - these should remain NaN
   - Multiple fraction formats: /5, /4, /10, /100, /6, /8
   - Letter grades need consistent mapping
   - Edge cases: "Fresh" and "Rotten" text values

3. **Missing Critic Names:**
   - Some reviews have empty `critic_name` - must filter these out first

4. **Review Count Filter:**
   - The notebook uses 500 movie threshold
   - Must count movies with actual scores (not just reviews)

5. **Matrix Sparsity:**
   - Most critics review a small fraction of movies
   - KNN imputation on sparse data can be slow

6. **Audience Score Location:**
   - `audience_rating` is in movies table, must join on `rotten_tomatoes_link`

---

## Updated hw2.yaml Configuration

See `configs/hw2.yaml` for the updated configuration with test points and written questions.

---

## Implementation Order

1. Create `templates/hw2/solution.py` with function stubs
2. Create `templates/hw2/instructions.md` with assignment description
3. Create `tests/test_module2.py` with pytest test suite
4. Update `configs/hw2.yaml` with test points
5. Test with agent to validate framework
