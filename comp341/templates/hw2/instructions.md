# Homework 2: Movie Critics

## Overview

Find critics whose ratings best predict audience scores. You'll clean messy review data, handle missing values with different imputation methods, and use correlation analysis to identify the most predictive critics.

**Data Files:**
- `rotten_tomatoes_critic_reviews.csv` - 1.1M reviews from critics (226 MB)
- `rotten_tomatoes_movies.csv` - 17,712 movies with audience ratings
- Location: `data/hw2/` (mounted at `/data/hw2` in Docker)

**Key Columns:**
- Critics: `critic_name`, `review_score`, `review_type` (Fresh/Rotten), `rotten_tomatoes_link`
- Movies: `rotten_tomatoes_link`, `audience_rating`, `movie_title`

## Tasks

### Part 0: Getting to Know the Data (11 pts)

1. Load both CSV files into DataFrames
2. Remove critics with no name (empty or NaN `critic_name`)
3. Determine the number of unique critics
4. Plot the distribution of movies reviewed per critic

**Written Question:** What do you notice about the distribution? Are most critics prolific or occasional reviewers?

### Part 1: Reviewer Bias (9 pts)

Analyze whether critics tend toward positive or negative reviews:
1. Calculate fresh vs rotten review percentages per critic
2. Visualize the distribution of critic bias

**Written Question:** Do critics tend to be harsh or generous? Are there outliers?

### Part 2: Cleaning Scores (15 pts)

The `review_score` column is messy - critics use different rating scales:
- Fractions: `3/5`, `3/4`, `87/100`, `4/10`
- Letter grades: `A`, `A-`, `B+`, `B`, `C`, `F`
- Numbers: `75`, `3.5`
- Empty values

Normalize all scores to a 0-100 scale:

| Original | Normalized |
|----------|------------|
| `3/5` | 60 |
| `3/4` | 75 |
| `87/100` | 87 |
| `A` | 95 |
| `B+` | 88 |
| `C` | 75 |
| `F` | 50 |
| Empty/NaN | NaN (preserve) |

**Letter Grade Scale:**
- A: 95, A-: 92
- B+: 88, B: 85, B-: 82
- C+: 78, C: 75, C-: 72
- D+: 68, D: 65, D-: 62
- F: 50

### Part 3: Handling Missing Values & Correlation (50 pts)

Most critics review only a small subset of movies, creating a sparse matrix. You'll try three imputation strategies:

#### Step 1: Create Critic-Movie Matrix
Build a pivot table where:
- Rows = movies
- Columns = critics (their normalized scores)
- Include `audience_score` from movies table

#### Step 2: Filter to Active Critics
Keep only critics who have scored at least 500 movies. This reduces sparsity and focuses on prolific reviewers.

#### Step 3: Three Imputation Methods

**Method A - Zeros:** Fill missing scores with 0
- Simple but biased (treats "not reviewed" as "terrible")

**Method B - Mean:** Fill with each critic's mean score
- Assumes critic would give their average rating

**Method C - KNN (k=5):** Use similar movies to estimate missing scores
- Most sophisticated, uses patterns in the data

#### Step 4: Calculate Correlations
For each imputation method:
1. Calculate Pearson correlation between each critic and `audience_score`
2. Identify the top 5 most correlated critics

#### Step 5: PCA Visualization
Run PCA on the imputed matrix to visualize the data in 2D.

**Written Questions:**
- Which imputation method produces the highest correlations?
- Do the same critics appear in the top 5 across methods?
- What does the PCA plot reveal about critic clusters?

### Part 4: Reflection (15 pts)

**Written Questions:**
1. If you were building a movie recommendation system, which critics would you weight most heavily?
2. What are the limitations of using correlation to identify "good" critics?
3. How might the 500-movie threshold bias your results?

## Functions to Implement

```python
# Data Loading
def load_critic_data(data_dir: str) -> pd.DataFrame:
    """Load rotten_tomatoes_critic_reviews.csv."""

def load_movie_data(data_dir: str) -> pd.DataFrame:
    """Load rotten_tomatoes_movies.csv."""

# Data Cleaning
def remove_unnamed_critics(df: pd.DataFrame) -> pd.DataFrame:
    """Remove critics with no name."""

def filter_critics_by_review_count(df: pd.DataFrame, threshold: int = 500) -> pd.DataFrame:
    """Keep critics with >= threshold scored movies."""

def clean_review_scores(df: pd.DataFrame) -> pd.DataFrame:
    """Normalize review_score to 0-100 scale."""

# Imputation
def prepare_critic_movie_matrix(critic_df, movie_df) -> pd.DataFrame:
    """Create movies x critics pivot table with audience_score."""

def impute_missing_zeros(df: pd.DataFrame) -> pd.DataFrame:
    """Fill NaN with 0."""

def impute_missing_mean(df: pd.DataFrame) -> pd.DataFrame:
    """Fill NaN with column mean."""

def impute_missing_knn(df: pd.DataFrame, k: int = 5) -> pd.DataFrame:
    """Fill NaN using KNN imputation."""

# Analysis
def calculate_critic_correlation(df: pd.DataFrame) -> pd.Series:
    """Pearson correlation between each critic and audience_score."""

def get_top_correlated_critics(correlations: pd.Series, n: int = 5) -> List[str]:
    """Top n critics by correlation."""

def run_pca(df: pd.DataFrame, n_components: int = 2) -> Tuple[np.ndarray, PCA]:
    """PCA on the imputed matrix."""
```

## Hints

- **Score cleaning:** Use regex or string methods to detect fractions (`'/' in score`)
- **Large data:** The critic reviews file has 1.1M rows - operations may take a few seconds
- **Sparse matrix:** After filtering to 500+ reviews, you'll have ~50-100 critics
- **KNN is slow:** KNN imputation on sparse data can take 30+ seconds
- **Correlation:** Use `df.corrwith(df['audience_score'])` for efficient calculation

## Grading

| Part | Points |
|------|--------|
| Part 0: Data Exploration | 11 |
| Part 1: Reviewer Bias | 9 |
| Part 2: Score Cleaning | 15 |
| Part 3: Imputation & Correlation | 50 |
| Part 4: Written Reflection | 15 |
| **Total** | **100** |

## Extra Credit (up to 5 pts)

Propose and implement an alternative approach to identify predictive critics. Ideas:
- Use only critics who reviewed the same movies as the test set
- Weight by critic expertise (top_critic flag)
- Time-weighted correlation (recent reviews matter more)
