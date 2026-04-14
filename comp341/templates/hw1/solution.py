"""
COMP 341 Homework 1: Exploring Baby Names

Analyze SSA baby name data to explore naming trends and predict ages.

Functions to implement:
- load_baby_names: Load all yearly files into a single DataFrame
- filter_by_count: Filter to names with >= threshold total occurrences
- calculate_median_year: Weighted median birth year for a name
- calculate_mean_year: Weighted mean birth year for a name
- get_oldest_names: Names popular longest ago (lowest median year)
- get_youngest_names: Names most recently popular (highest median year)
- predict_age: Predict age based on median birth year
"""

import pandas as pd
import numpy as np
import glob
from pathlib import Path
from typing import Union


def load_baby_names(data_dir: Union[str, Path]) -> pd.DataFrame:
    """Load all SSA baby name files into a single DataFrame.

    The SSA provides yearly files named yobYYYY.txt with format:
    name,sex,count (no header)

    Args:
        data_dir: Path to directory containing yobYYYY.txt files

    Returns:
        DataFrame with columns: name, sex, count, year
        - name: Baby name (str)
        - sex: 'M' or 'F' (str)
        - count: Number of babies with this name that year (int)
        - year: Year of birth (int)
    """
    # TODO: Implement this function
    # Hint: Use glob to find all yob*.txt files
    # Hint: Extract year from filename (e.g., yob1880.txt -> 1880)
    # Hint: pd.concat() to combine all DataFrames
    pass


def filter_by_count(df: pd.DataFrame, threshold: int = 20000) -> pd.DataFrame:
    """Filter to (name, sex) pairs with total count >= threshold.

    This removes uncommon names that don't have enough data for
    reliable statistical analysis.

    Args:
        df: Baby names DataFrame with columns [name, sex, count, year]
        threshold: Minimum total occurrences across all years

    Returns:
        Filtered DataFrame containing only rows for (name, sex) pairs
        that have at least `threshold` total occurrences
    """
    # TODO: Implement this function
    # Hint: Group by (name, sex), sum counts, filter, then merge back
    pass


def calculate_median_year(df: pd.DataFrame, name: str, sex: str) -> int:
    """Calculate the weighted median birth year for a specific name.

    The median is weighted by count - each baby counts as one observation.
    For example, if 100 babies named "Alice" were born in 1990 and
    50 in 2000, the median would be closer to 1990.

    Args:
        df: Baby names DataFrame
        name: Name to analyze
        sex: Sex ('M' or 'F')

    Returns:
        Median birth year as integer (rounded)
    """
    # TODO: Implement this function
    # Hint: np.repeat() can expand years by their counts
    # Hint: np.median() on the expanded array
    pass


def calculate_mean_year(df: pd.DataFrame, name: str, sex: str) -> float:
    """Calculate the weighted mean birth year for a specific name.

    Args:
        df: Baby names DataFrame
        name: Name to analyze
        sex: Sex ('M' or 'F')

    Returns:
        Mean birth year as float
    """
    # TODO: Implement this function
    # Hint: Weighted mean = sum(year * count) / sum(count)
    pass


def calculate_name_stats(df: pd.DataFrame) -> pd.DataFrame:
    """Calculate median year, mean year, and total count for all names.

    Args:
        df: Baby names DataFrame (ideally already filtered)

    Returns:
        DataFrame with columns: name, sex, median_year, mean_year, total_count
        Sorted by median_year (ascending), then mean_year, then total_count
    """
    # TODO: Implement this function
    # This combines the above calculations for all (name, sex) pairs
    pass


def get_oldest_names(df: pd.DataFrame, sex: str, n: int = 10) -> pd.DataFrame:
    """Get the n oldest names for a given sex.

    "Oldest" means lowest median year - names that were popular longest ago.

    Args:
        df: Baby names DataFrame
        sex: 'M' or 'F'
        n: Number of names to return

    Returns:
        DataFrame with top n oldest names, including median_year
    """
    # TODO: Implement this function
    pass


def get_youngest_names(df: pd.DataFrame, sex: str, n: int = 10) -> pd.DataFrame:
    """Get the n youngest names for a given sex.

    "Youngest" means highest median year - names most recently popular.

    Args:
        df: Baby names DataFrame
        sex: 'M' or 'F'
        n: Number of names to return

    Returns:
        DataFrame with top n youngest names, including median_year
    """
    # TODO: Implement this function
    pass


def predict_age(df: pd.DataFrame, name: str, sex: str,
                current_year: int = 2024) -> int:
    """Predict a person's age based on their name's median birth year.

    This is a simple heuristic: age = current_year - median_birth_year

    Args:
        df: Baby names DataFrame
        name: Person's name
        sex: 'M' or 'F'
        current_year: Year to calculate age from (default 2024)

    Returns:
        Predicted age as integer
    """
    # TODO: Implement this function
    pass


# =============================================================================
# Helper functions for visualization (optional to implement)
# =============================================================================

def get_births_per_year(df: pd.DataFrame) -> pd.DataFrame:
    """Aggregate total births per year by sex.

    Args:
        df: Baby names DataFrame

    Returns:
        DataFrame with columns: year, sex, total_births
    """
    # TODO: Implement if needed for plotting
    pass


def get_name_trend(df: pd.DataFrame, name: str, sex: str) -> pd.DataFrame:
    """Get yearly birth counts for a specific name.

    Args:
        df: Baby names DataFrame
        name: Name to track
        sex: Sex ('M' or 'F')

    Returns:
        DataFrame with columns: year, count
    """
    # TODO: Implement if needed for plotting
    pass
