"""
COMP 341 Homework 1: Exploring Baby Names - Test Suite

Tests organized by tier:
- Tier 1: Core data operations (must pass)
- Tier 2: Statistical calculations (main logic)
- Tier 3: Age prediction (application)
- Tier 4: Visualization validation
"""

import pytest
import pandas as pd
import numpy as np
import os
import sys
from pathlib import Path
from datetime import datetime

# =============================================================================
# Test Configuration
# =============================================================================

# Use current year for age calculations (avoids hardcoded values)
CURRENT_YEAR = datetime.now().year

# Get paths from environment or use defaults
DATA_DIR = Path(os.environ.get('COMP341_DATA_DIR',
    Path(__file__).parent.parent / 'data')) / 'hw1'
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
def baby_names_df():
    """Load baby names data once for all tests."""
    if not SOLUTION_AVAILABLE:
        pytest.skip("solution.py not available")
    df = solution.load_baby_names(DATA_DIR)
    return df


@pytest.fixture(scope="module")
def filtered_df(baby_names_df):
    """Get filtered DataFrame (names with >= 20000 occurrences)."""
    return solution.filter_by_count(baby_names_df, threshold=20000)


# =============================================================================
# Tier 1: Core Data Operations
# =============================================================================

class TestDataLoading:
    """Tier 1: Verify data is loaded correctly."""

    def test_filter_threshold_applied(self, baby_names_df):
        """Verify all remaining names have >= threshold total occurrences."""
        threshold = 20000
        filtered = solution.filter_by_count(baby_names_df, threshold=threshold)

        # Check each (name, sex) pair
        totals = filtered.groupby(['name', 'sex'])['count'].sum()
        assert (totals >= threshold).all(), \
            "Some names have fewer than threshold occurrences"

    def test_filter_reduces_data(self, baby_names_df):
        """Verify filtering significantly reduces the dataset."""
        filtered = solution.filter_by_count(baby_names_df, threshold=20000)

        original_pairs = baby_names_df.groupby(['name', 'sex']).ngroups
        filtered_pairs = filtered.groupby(['name', 'sex']).ngroups

        # Should keep roughly 1-5% of unique name-sex pairs
        reduction_ratio = filtered_pairs / original_pairs
        assert reduction_ratio < 0.10, \
            f"Expected significant reduction, but kept {reduction_ratio:.1%}"

    def test_filter_preserves_columns(self, baby_names_df):
        """Verify filtered DataFrame has same columns."""
        filtered = solution.filter_by_count(baby_names_df, threshold=20000)
        assert set(filtered.columns) == set(baby_names_df.columns)

    def test_filter_common_names_present(self, filtered_df):
        """Verify common names are in filtered data."""
        # These names definitely have > 20000 occurrences
        common_names = [
            ('Mary', 'F'), ('John', 'M'), ('James', 'M'),
            ('Elizabeth', 'F'), ('William', 'M'), ('Jennifer', 'F')
        ]

        for name, sex in common_names:
            matches = filtered_df[(filtered_df['name'] == name) &
                                  (filtered_df['sex'] == sex)]
            assert len(matches) > 0, f"Common name {name} ({sex}) should be in filtered data"


# =============================================================================
# Tier 2: Statistical Calculations
# =============================================================================

class TestMedianYear:
    """Tier 2: Verify median year calculations."""

    def test_median_year_returns_int(self, baby_names_df):
        """Verify calculate_median_year returns an integer."""
        result = solution.calculate_median_year(baby_names_df, 'Mary', 'F')
        assert isinstance(result, (int, np.integer)), "Should return integer"

    def test_median_year_reasonable_range(self, baby_names_df):
        """Verify median year is within data range."""
        result = solution.calculate_median_year(baby_names_df, 'Mary', 'F')
        assert 1880 <= result <= CURRENT_YEAR, f"Median year {result} out of range"

    def test_median_year_jennifer(self, baby_names_df):
        """Test median year for Jennifer (peaked late 1970s-early 1980s)."""
        median = solution.calculate_median_year(baby_names_df, 'Jennifer', 'F')
        # Jennifer was hugely popular 1970-1985
        assert 1970 <= median <= 1990, \
            f"Jennifer median should be ~1975-1985, got {median}"

    def test_median_year_old_vs_new_name(self, baby_names_df):
        """Verify old names have earlier median than new names."""
        # Mildred: popular early 1900s
        # Madison: popular starting 1990s
        median_mildred = solution.calculate_median_year(baby_names_df, 'Mildred', 'F')
        median_madison = solution.calculate_median_year(baby_names_df, 'Madison', 'F')

        assert median_mildred < median_madison, \
            f"Mildred ({median_mildred}) should be older than Madison ({median_madison})"


class TestMeanYear:
    """Tier 2: Verify mean year calculations."""

    def test_mean_year_returns_number(self, baby_names_df):
        """Verify calculate_mean_year returns a number."""
        result = solution.calculate_mean_year(baby_names_df, 'Mary', 'F')
        assert isinstance(result, (int, float, np.number)), "Should return number"

    def test_mean_year_reasonable_range(self, baby_names_df):
        """Verify mean year is within data range."""
        result = solution.calculate_mean_year(baby_names_df, 'Mary', 'F')
        assert 1880 <= result <= CURRENT_YEAR, f"Mean year {result} out of range"

    def test_mean_year_old_vs_new_name(self, baby_names_df):
        """Verify old names have earlier mean than new names."""
        mean_ethel = solution.calculate_mean_year(baby_names_df, 'Ethel', 'F')
        mean_sophia = solution.calculate_mean_year(baby_names_df, 'Sophia', 'F')

        assert mean_ethel < mean_sophia, \
            f"Ethel ({mean_ethel}) should be older than Sophia ({mean_sophia})"

    def test_mean_vs_median_close(self, baby_names_df):
        """For most names, mean and median should be within 30 years."""
        # Test a few common names
        test_names = [('Mary', 'F'), ('John', 'M'), ('Jennifer', 'F')]

        for name, sex in test_names:
            median = solution.calculate_median_year(baby_names_df, name, sex)
            mean = solution.calculate_mean_year(baby_names_df, name, sex)
            diff = abs(median - mean)
            assert diff < 30, \
                f"{name} ({sex}): median={median}, mean={mean:.1f}, diff={diff:.1f}"


class TestOldestYoungestNames:
    """Tier 2: Verify oldest/youngest name identification."""

    def test_get_oldest_names_returns_dataframe(self, baby_names_df):
        """Verify get_oldest_names returns a DataFrame."""
        result = solution.get_oldest_names(baby_names_df, sex='F', n=10)
        assert isinstance(result, pd.DataFrame)

    def test_get_oldest_names_correct_count(self, baby_names_df):
        """Verify correct number of names returned."""
        result = solution.get_oldest_names(baby_names_df, sex='F', n=10)
        assert len(result) == 10

    def test_oldest_female_names_reasonable(self, baby_names_df):
        """Verify oldest female names are from early 1900s."""
        oldest = solution.get_oldest_names(baby_names_df, sex='F', n=10)

        # Should have 'name' and 'median_year' columns
        assert 'name' in oldest.columns
        assert 'median_year' in oldest.columns

        # All should have median year before 1925
        assert (oldest['median_year'] < 1925).all(), \
            "Oldest female names should have median year < 1925"

        # Should include some expected old names
        old_names = set(oldest['name'].str.lower())
        expected = {'mildred', 'ethel', 'bertha', 'minnie', 'bessie',
                    'gertrude', 'hazel', 'edna', 'thelma', 'myrtle'}
        overlap = len(old_names & expected)
        assert overlap >= 3, \
            f"Expected at least 3 classic old names, got {old_names & expected}"

    def test_youngest_female_names_reasonable(self, baby_names_df):
        """Verify youngest female names are modern."""
        youngest = solution.get_youngest_names(baby_names_df, sex='F', n=10)

        # All should have median year after 2005
        assert (youngest['median_year'] > 2005).all(), \
            "Youngest female names should have median year > 2005"

    def test_oldest_male_names_reasonable(self, baby_names_df):
        """Verify oldest male names are from early 1900s."""
        oldest = solution.get_oldest_names(baby_names_df, sex='M', n=10)

        # All should have median year before 1930
        assert (oldest['median_year'] < 1930).all(), \
            "Oldest male names should have median year < 1930"

        # Should include some expected old names (names with median_year < 1930)
        old_names = set(oldest['name'].str.lower())
        expected = {'woodrow', 'elmer', 'homer', 'irving', 'orville',
                    'grover', 'wilbur', 'emil', 'charley', 'chester'}
        overlap = len(old_names & expected)
        assert overlap >= 3, \
            f"Expected at least 3 classic old names, got {old_names & expected}"

    def test_youngest_male_names_reasonable(self, baby_names_df):
        """Verify youngest male names are modern."""
        youngest = solution.get_youngest_names(baby_names_df, sex='M', n=10)

        # All should have median year after 2005
        assert (youngest['median_year'] > 2005).all(), \
            "Youngest male names should have median year > 2005"

    def test_oldest_sorted_correctly(self, baby_names_df):
        """Verify oldest names are sorted by median_year ascending."""
        oldest = solution.get_oldest_names(baby_names_df, sex='F', n=10)
        years = oldest['median_year'].tolist()
        assert years == sorted(years), "Oldest names should be sorted ascending by median_year"

    def test_youngest_sorted_correctly(self, baby_names_df):
        """Verify youngest names are sorted by median_year descending."""
        youngest = solution.get_youngest_names(baby_names_df, sex='F', n=10)
        years = youngest['median_year'].tolist()
        assert years == sorted(years, reverse=True), \
            "Youngest names should be sorted descending by median_year"


# =============================================================================
# Tier 3: Age Prediction
# =============================================================================

class TestAgePrediction:
    """Tier 3: Verify age prediction logic."""

    def test_predict_age_returns_int(self, baby_names_df):
        """Verify predict_age returns an integer."""
        result = solution.predict_age(baby_names_df, 'Mary', 'F')
        assert isinstance(result, (int, np.integer)), "Should return integer"

    def test_predict_age_uses_median(self, baby_names_df):
        """Verify prediction uses median year correctly."""
        current_year = CURRENT_YEAR
        age = solution.predict_age(baby_names_df, 'Jennifer', 'F', current_year)
        median = solution.calculate_median_year(baby_names_df, 'Jennifer', 'F')
        expected = current_year - median

        assert age == expected, \
            f"Age should be {current_year} - {median} = {expected}, got {age}"

    def test_predict_age_gerald(self, baby_names_df):
        """Test prediction for Gerald (older name, popular 1930s-1950s)."""
        age = solution.predict_age(baby_names_df, 'Gerald', 'M', CURRENT_YEAR)
        # Gerald peaked around 1945, so age should be ~75-85
        assert 60 < age < 100, f"Gerald age should be 60-100, got {age}"

    def test_predict_age_kai(self, baby_names_df):
        """Test prediction for Kai (newer name, popular 2010s)."""
        age = solution.predict_age(baby_names_df, 'Kai', 'M', CURRENT_YEAR)
        # Kai became popular recently, age should be < 25
        assert 0 < age < 30, f"Kai age should be < 30, got {age}"

    def test_predict_age_madison(self, baby_names_df):
        """Test prediction for Madison (became popular 1990s)."""
        age = solution.predict_age(baby_names_df, 'Madison', 'F', CURRENT_YEAR)
        # Madison peaked around 2000, so age should be ~20-30
        assert 15 < age < 40, f"Madison age should be 15-40, got {age}"

    def test_predict_age_spencer(self, baby_names_df):
        """Test prediction for Spencer."""
        age = solution.predict_age(baby_names_df, 'Spencer', 'M', CURRENT_YEAR)
        # Spencer has been moderately popular for a while
        assert 10 < age < 60, f"Spencer age should be reasonable, got {age}"

    def test_predict_age_ordering(self, baby_names_df):
        """Verify age ordering matches name popularity eras."""
        gerald_age = solution.predict_age(baby_names_df, 'Gerald', 'M', CURRENT_YEAR)
        spencer_age = solution.predict_age(baby_names_df, 'Spencer', 'M', CURRENT_YEAR)
        kai_age = solution.predict_age(baby_names_df, 'Kai', 'M', CURRENT_YEAR)

        # Gerald should be oldest, Kai youngest
        assert gerald_age > spencer_age > kai_age, \
            f"Expected Gerald > Spencer > Kai, got {gerald_age}, {spencer_age}, {kai_age}"


# =============================================================================
# Tier 4: Visualization Data Validation
# =============================================================================

class TestVisualizationData:
    """Tier 4: Verify data for visualizations is correct."""

    def test_births_per_year_available(self, baby_names_df):
        """Test that we can aggregate births per year."""
        # This tests the data needed for Part 2 visualization
        births_by_year = baby_names_df.groupby(['year', 'sex'])['count'].sum().reset_index()

        assert len(births_by_year) > 280  # ~145 years * 2 sexes
        assert 'year' in births_by_year.columns
        assert 'count' in births_by_year.columns

    def test_ruth_data_exists(self, baby_names_df):
        """Test Ruth data for Part 3 visualization."""
        ruth_boys = baby_names_df[
            (baby_names_df['name'] == 'Ruth') &
            (baby_names_df['sex'] == 'M')
        ]

        # Should have some boys named Ruth
        assert len(ruth_boys) > 0, "Should find boys named Ruth"

        # Peak should be in early 1900s
        peak_year = ruth_boys.loc[ruth_boys['count'].idxmax(), 'year']
        assert 1890 < peak_year < 1940, \
            f"Ruth (M) peak should be early 1900s, got {peak_year}"

    def test_name_trend_data(self, baby_names_df):
        """Test that we can get trend data for any name."""
        jennifer_trend = baby_names_df[
            (baby_names_df['name'] == 'Jennifer') &
            (baby_names_df['sex'] == 'F')
        ]

        # Jennifer should have data across many years
        years_with_data = jennifer_trend['year'].nunique()
        assert years_with_data > 50, "Jennifer should have data across many years"

        # Peak should be around 1970-1985
        peak_year = jennifer_trend.loc[jennifer_trend['count'].idxmax(), 'year']
        assert 1970 <= peak_year <= 1985, \
            f"Jennifer peak should be 1970-1985, got {peak_year}"


# =============================================================================
# Integration Tests
# =============================================================================

class TestIntegration:
    """End-to-end workflow tests."""

    def test_full_workflow(self, baby_names_df):
        """Test complete analysis workflow."""
        # 1. Load data (already done via fixture)
        assert len(baby_names_df) > 2_000_000

        # 2. Filter
        filtered = solution.filter_by_count(baby_names_df, threshold=20000)
        assert len(filtered) < len(baby_names_df)

        # 3. Get statistics
        oldest_f = solution.get_oldest_names(baby_names_df, sex='F', n=5)
        youngest_f = solution.get_youngest_names(baby_names_df, sex='F', n=5)

        # 4. Verify separation
        oldest_median = oldest_f['median_year'].max()
        youngest_median = youngest_f['median_year'].min()
        assert oldest_median < youngest_median, \
            "Oldest names should have earlier median than youngest"

        # 5. Make predictions
        for name in oldest_f['name'].head(3):
            age = solution.predict_age(baby_names_df, name, 'F', CURRENT_YEAR)
            assert age > 80, f"Oldest name {name} should predict age > 80"
