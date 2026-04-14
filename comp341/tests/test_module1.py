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

    def test_load_data_returns_dataframe(self):
        """Verify load_baby_names returns a DataFrame."""
        df = solution.load_baby_names(DATA_DIR)
        assert isinstance(df, pd.DataFrame), "Should return a pandas DataFrame"

    def test_load_data_structure(self, baby_names_df):
        """Verify DataFrame has required columns."""
        required_cols = {'name', 'sex', 'count', 'year'}
        actual_cols = set(baby_names_df.columns)
        assert required_cols.issubset(actual_cols), \
            f"Missing columns: {required_cols - actual_cols}"

    def test_load_data_row_count(self, baby_names_df):
        """Verify DataFrame has reasonable row count (~2M rows)."""
        # Data varies by version (1880-2021 vs 1880-2024)
        # Accept range of 2M to 2.5M rows
        row_count = len(baby_names_df)
        assert 2_000_000 < row_count < 2_500_000, \
            f"Expected ~2M rows, got {row_count:,}"

    def test_load_data_types(self, baby_names_df):
        """Verify correct data types."""
        # Accept both 'object' and StringDtype (newer pandas)
        assert pd.api.types.is_string_dtype(baby_names_df['name']), "name should be string"
        assert pd.api.types.is_string_dtype(baby_names_df['sex']), "sex should be string"
        assert pd.api.types.is_integer_dtype(baby_names_df['count']), \
            "count should be integer"
        assert pd.api.types.is_integer_dtype(baby_names_df['year']), \
            "year should be integer"

    def test_load_data_sex_values(self, baby_names_df):
        """Verify sex column contains only M and F."""
        unique_sex = set(baby_names_df['sex'].unique())
        assert unique_sex == {'M', 'F'}, f"Unexpected sex values: {unique_sex}"

    def test_load_data_year_range(self, baby_names_df):
        """Verify year range starts at 1880."""
        min_year = baby_names_df['year'].min()
        max_year = baby_names_df['year'].max()
        assert min_year == 1880, f"Expected min year 1880, got {min_year}"
        assert max_year >= 2021, f"Expected max year >= 2021, got {max_year}"

    def test_load_data_positive_counts(self, baby_names_df):
        """Verify all counts are positive."""
        assert (baby_names_df['count'] > 0).all(), "All counts should be positive"


class TestFiltering:
    """Tier 1: Verify filtering works correctly."""

    def test_filter_returns_dataframe(self, baby_names_df):
        """Verify filter_by_count returns a DataFrame."""
        filtered = solution.filter_by_count(baby_names_df, threshold=20000)
        assert isinstance(filtered, pd.DataFrame)

