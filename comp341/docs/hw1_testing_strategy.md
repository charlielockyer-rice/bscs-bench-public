# HW1 Testing Strategy: Exploring Baby Names

## Assignment Overview

**Goal:** Analyze SSA baby name data (1880-2024) to explore naming trends and predict ages from names.

**Spirit of the Assignment:**
- Learn data wrangling fundamentals (loading, cleaning, aggregating)
- Practice exploratory data analysis with real-world messy data
- Understand how simple statistics (mean, median) can be used for prediction
- Develop intuition about data-driven insights vs. domain knowledge

**Point Allocation:**
| Part | Points | Description |
|------|--------|-------------|
| Style/Flow | 15 | Code quality and sequential execution |
| Part 0 | 5 | Data loading |
| Part 1 | 3 | Sanity checks (dimensions) |
| Part 2 | 10 | General patterns (plotting births over time) |
| Part 3 | 6 | Ruth analysis (gender patterns) |
| Part 4 | 50 | Oldest/youngest name identification |
| Part 5 | 11 | Age predictions |
| Extra Credit | 5 | Alternative prediction metric |

## Data Characteristics

- **Files:** 145 yearly files (`yob1880.txt` through `yob2024.txt`)
- **Format:** CSV without header: `name,sex,count`
- **Total rows:** ~2.15 million (varies by data version)
- **Note:** Original assignment used 1880-2021 data (~2.05M rows); current data extends to 2024

## Testable Components

### Tier 1: Core Data Operations (Must Pass)

These tests verify fundamental correctness. An agent cannot proceed without these.

#### T1.1: Data Loading
```python
def test_load_data_structure():
    """Verify DataFrame has required columns."""
    df = solution.load_baby_names(DATA_DIR)
    assert 'name' in df.columns
    assert 'sex' in df.columns
    assert 'count' in df.columns
    assert 'year' in df.columns

def test_load_data_row_count():
    """Verify DataFrame has reasonable row count."""
    df = solution.load_baby_names(DATA_DIR)
    # Allow range for different data versions
    assert 2_000_000 < len(df) < 2_500_000

def test_load_data_types():
    """Verify correct data types."""
    df = solution.load_baby_names(DATA_DIR)
    assert df['name'].dtype == 'object'  # string
    assert df['sex'].dtype == 'object'
    assert df['count'].dtype in ['int64', 'int32']
    assert df['year'].dtype in ['int64', 'int32']

def test_load_data_year_range():
    """Verify all years loaded."""
    df = solution.load_baby_names(DATA_DIR)
    assert df['year'].min() == 1880
    assert df['year'].max() >= 2021  # At least through 2021
```

#### T1.2: Filtering
```python
def test_filter_threshold():
    """Verify 20,000 occurrence filter works correctly."""
    df = solution.load_baby_names(DATA_DIR)
    filtered = solution.filter_by_count(df, threshold=20000)

    # Each remaining (name, sex) pair should have >= 20000 total
    totals = filtered.groupby(['name', 'sex'])['count'].sum()
    assert (totals >= 20000).all()

def test_filter_reduces_data():
    """Verify filtering reduces dataset significantly."""
    df = solution.load_baby_names(DATA_DIR)
    filtered = solution.filter_by_count(df, threshold=20000)

    # Should be much smaller (roughly 2000-3000 unique name-sex pairs)
    original_pairs = df.groupby(['name', 'sex']).ngroups
    filtered_pairs = filtered.groupby(['name', 'sex']).ngroups
    assert filtered_pairs < original_pairs * 0.05  # <5% remain
```

### Tier 2: Statistical Calculations (Core Logic)

These tests verify the main analytical work is correct.

#### T2.1: Median/Mean Year Calculations
```python
def test_median_year_known_name():
    """Test median year for a well-known name with predictable pattern."""
    df = solution.load_baby_names(DATA_DIR)

    # "Jennifer" peaked in late 1970s-early 1980s
    median_year = solution.calculate_median_year(df, 'Jennifer', 'F')
    assert 1970 <= median_year <= 1985

def test_mean_year_known_name():
    """Test mean year calculation."""
    df = solution.load_baby_names(DATA_DIR)

    # "Mary" was very popular early, so mean should be earlier than recent names
    mean_year_mary = solution.calculate_mean_year(df, 'Mary', 'F')
    mean_year_madison = solution.calculate_mean_year(df, 'Madison', 'F')
    assert mean_year_mary < mean_year_madison

def test_median_vs_mean_relationship():
    """Verify median and mean are reasonably close for most names."""
    df = solution.load_baby_names(DATA_DIR)
    filtered = solution.filter_by_count(df, threshold=20000)

    # For each name, |median - mean| should typically be < 30 years
    stats = solution.calculate_name_stats(filtered)
    diff = abs(stats['median_year'] - stats['mean_year'])
    assert diff.median() < 20  # Most names have small difference
```

#### T2.2: Oldest/Youngest Identification
```python
def test_oldest_female_names_reasonable():
    """Verify oldest female names are from early 1900s era."""
    df = solution.load_baby_names(DATA_DIR)
    oldest = solution.get_oldest_names(df, sex='F', n=10)

    # Oldest names should have median year before 1920
    assert all(oldest['median_year'] < 1920)

    # Should include classic old names like Mildred, Ethel, Bertha
    old_name_set = set(oldest['name'].str.lower())
    expected_old = {'mildred', 'ethel', 'bertha', 'minnie', 'bessie'}
    assert len(old_name_set & expected_old) >= 2

def test_youngest_female_names_reasonable():
    """Verify youngest female names are modern."""
    df = solution.load_baby_names(DATA_DIR)
    youngest = solution.get_youngest_names(df, sex='F', n=10)

    # Youngest names should have median year after 2005
    assert all(youngest['median_year'] > 2005)

def test_oldest_male_names_reasonable():
    """Verify oldest male names are from early 1900s era."""
    df = solution.load_baby_names(DATA_DIR)
    oldest = solution.get_oldest_names(df, sex='M', n=10)

    # Should include classic old names
    old_name_set = set(oldest['name'].str.lower())
    expected_old = {'clarence', 'earl', 'willie', 'harold', 'herman'}
    assert len(old_name_set & expected_old) >= 2

def test_youngest_male_names_reasonable():
    """Verify youngest male names are modern."""
    df = solution.load_baby_names(DATA_DIR)
    youngest = solution.get_youngest_names(df, sex='M', n=10)

    # Youngest names should have median year after 2005
    assert all(youngest['median_year'] > 2005)
```

### Tier 3: Age Prediction (Application)

#### T3.1: Prediction Logic
```python
def test_predict_age_logic():
    """Verify age prediction uses median year correctly."""
    df = solution.load_baby_names(DATA_DIR)
    current_year = 2024  # Or datetime.now().year

    # Age = current_year - median_year
    age = solution.predict_age(df, 'Jennifer', 'F', current_year)
    median = solution.calculate_median_year(df, 'Jennifer', 'F')
    assert age == current_year - median

def test_predict_age_specific_names():
    """Test specific name predictions are reasonable."""
    df = solution.load_baby_names(DATA_DIR)

    # Gerald is an older name (popular 1930s-1950s)
    gerald_age = solution.predict_age(df, 'Gerald', 'M')
    assert 60 < gerald_age < 100

    # Kai is a newer name (popular 2010s)
    kai_age = solution.predict_age(df, 'Kai', 'M')
    assert 5 < kai_age < 30

    # Madison became popular in 1990s
    madison_age = solution.predict_age(df, 'Madison', 'F')
    assert 20 < madison_age < 40
```

### Tier 4: Visualization (Validation Only)

Plots are hard to test automatically. We validate they execute without error and contain expected data.

```python
def test_births_per_year_plot_data():
    """Verify plot data is reasonable."""
    df = solution.load_baby_names(DATA_DIR)
    plot_data = solution.prepare_births_per_year_data(df)

    # Should have data for each year and sex
    assert len(plot_data) > 280  # ~145 years * 2 sexes
    assert 'year' in plot_data.columns
    assert 'total' in plot_data.columns
    assert 'sex' in plot_data.columns

def test_ruth_plot_data():
    """Verify Ruth data for plotting."""
    df = solution.load_baby_names(DATA_DIR)
    ruth_data = df[(df['name'] == 'Ruth') & (df['sex'] == 'M')]

    # Should find boys named Ruth
    assert len(ruth_data) > 0

    # Peak should be around 1900-1930
    peak_year = ruth_data.loc[ruth_data['count'].idxmax(), 'year']
    assert 1890 < peak_year < 1940
```

## Written Response Evaluation (Future)

The assignment includes 5 short-answer questions that require thoughtful responses:

### Q1: Historical Patterns in Birth Data
**Question:** Do you notice any interesting patterns across years? Do they relate to historical events?

**Expected themes:**
- Baby boom after WWII (1946-1964)
- Decline during Great Depression (1930s)
- Lower births during WWI/WWII
- General upward trend in registration

### Q2: Babe Ruth Influence
**Question:** Do you think Babe Ruth had influence on boys named Ruth?

**Expected reasoning:**
- Babe Ruth's career: 1914-1935
- Check if peak in boys named Ruth coincides
- Consider cultural context (naming after famous athletes)

### Q3: Birth Rate and Name Popularity
**Question:** Can birth rate influence oldest/youngest name identification?

**Expected insight:**
- Higher birth rates in certain eras mean more total babies with any name
- Normalization (percentage vs. raw count) might be more robust
- Median year weighted by count vs. unweighted

### Q4: Prediction Accuracy
**Question:** Do predicted ages match experience? Would you rely on these predictions?

**Expected reasoning:**
- Predictions are population averages, individuals vary
- Some names span multiple generations (e.g., William, Elizabeth)
- Recent names have less uncertainty
- Cultural/regional variations

### Q5: Mean vs. Median vs. Mode
**Question:** How do different metrics affect results?

**Expected analysis:**
- Median: robust to outliers, represents "middle" person
- Mean: sensitive to skew, influenced by early/late popularity
- Mode: peak year only, ignores distribution shape

## Test File Structure

```python
# tests/test_hw1.py

import pytest
import pandas as pd
from pathlib import Path
import os

# Get data directory from environment or default
DATA_DIR = Path(os.environ.get('COMP341_DATA_DIR', 'comp341/data')) / 'hw1'
WORKSPACE = Path(os.environ.get('COMP341_WORKSPACE', '.'))

# Import student solution
import sys
sys.path.insert(0, str(WORKSPACE))
import solution


class TestDataLoading:
    """Tier 1: Core data loading tests."""

    def test_load_data_structure(self):
        # ... implementation

    def test_load_data_row_count(self):
        # ... implementation


class TestFiltering:
    """Tier 1: Data filtering tests."""

    def test_filter_threshold(self):
        # ... implementation


class TestStatistics:
    """Tier 2: Statistical calculation tests."""

    def test_median_year_calculation(self):
        # ... implementation


class TestOldestYoungest:
    """Tier 2: Name ranking tests."""

    def test_oldest_female_names(self):
        # ... implementation


class TestPrediction:
    """Tier 3: Age prediction tests."""

    def test_predict_age_specific_names(self):
        # ... implementation


class TestVisualization:
    """Tier 4: Visualization validation."""

    def test_plot_data_prepared(self):
        # ... implementation
```

## Template Solution Interface

The `solution.py` template should expose these functions:

```python
def load_baby_names(data_dir: str) -> pd.DataFrame:
    """Load all SSA baby name files into a single DataFrame.

    Args:
        data_dir: Path to directory containing yobYYYY.txt files

    Returns:
        DataFrame with columns: name, sex, count, year
    """
    pass

def filter_by_count(df: pd.DataFrame, threshold: int = 20000) -> pd.DataFrame:
    """Filter to (name, sex) pairs with total count >= threshold.

    Args:
        df: Baby names DataFrame
        threshold: Minimum total occurrences

    Returns:
        Filtered DataFrame
    """
    pass

def calculate_median_year(df: pd.DataFrame, name: str, sex: str) -> int:
    """Calculate median birth year for a specific name and sex.

    Uses weighted median where each occurrence counts as one observation.
    """
    pass

def calculate_mean_year(df: pd.DataFrame, name: str, sex: str) -> float:
    """Calculate mean birth year for a specific name and sex."""
    pass

def get_oldest_names(df: pd.DataFrame, sex: str, n: int = 10) -> pd.DataFrame:
    """Get the n oldest names for a given sex.

    "Oldest" = lowest median year (were popular longest ago).
    """
    pass

def get_youngest_names(df: pd.DataFrame, sex: str, n: int = 10) -> pd.DataFrame:
    """Get the n youngest names for a given sex.

    "Youngest" = highest median year (most recently popular).
    """
    pass

def predict_age(df: pd.DataFrame, name: str, sex: str,
                current_year: int = 2024) -> int:
    """Predict age based on median birth year."""
    pass
```

## Grading Rubric

| Test Category | Points | Pass Criteria |
|---------------|--------|---------------|
| Data Loading | 10 | All Tier 1 tests pass |
| Filtering | 10 | Filter correctly removes uncommon names |
| Median/Mean Calculations | 20 | Calculations match expected values |
| Oldest/Youngest Names | 30 | Reasonable names identified, correct ranking |
| Age Predictions | 15 | Predictions within expected ranges |
| Visualization Data | 10 | Plot data correctly prepared |
| Written Responses | 5 | (Future: LLM evaluation) |

## Notes for Test Implementation

1. **Data Version Tolerance:** Tests should accept a range of row counts since data updates yearly.

2. **Name Validation:** Use names with well-known patterns (Jennifer, Madison, Mildred) for verification.

3. **Approximate Matching:** For rankings, check that expected names appear in top-N rather than exact positions.

4. **Current Year Handling:** Age predictions should use a configurable or detected current year.

5. **Plotting:** Don't test visual output, just that the data preparation is correct.
