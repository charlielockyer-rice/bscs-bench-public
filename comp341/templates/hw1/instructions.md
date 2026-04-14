# Homework 1: Exploring Baby Names

## Overview

Analyze baby name data from the US Social Security Agency (SSA) to explore naming trends and see if we can predict a person's age given only their first name.

**Data:** SSA provides yearly files (1880-2024) with baby names, sex, and counts.
- Format: `name,sex,count` (no header)
- Files: `yob1880.txt` through `yob2024.txt`
- Location: `data/hw1/` (symlinked to workspace)

## Tasks

### Part 0: Read in Data (5 pts)
Load all yearly files into a single pandas DataFrame with columns:
- `name`: Baby name (string)
- `sex`: 'M' or 'F' (string)
- `count`: Number of babies with this name that year (int)
- `year`: Year of birth (int)

**Expected:** ~2 million rows total

### Part 1: Sanity Checks (3 pts)
Verify your DataFrame:
- Check dimensions (should have ~2,052,781 rows)
- Look at top and bottom rows

### Part 2: Search for General Patterns (10 pts)
Plot total births per year with different colors for M/F.
- Clear axis labels
- Appropriately sized figure
- Readable tick labels

**Written Question:** Do you notice any interesting patterns across years? Do they relate to historical events?

### Part 3: Disentangling the Sexes (6 pts)
Investigate boys named Ruth:
- Plot boys named Ruth over time
- Find which year had the most boys named Ruth

**Written Question:** Do you think Babe Ruth (baseball player) had any influence on boys named Ruth?

### Part 4: Oldest and Youngest Names (50 pts)
1. Filter to (name, sex) pairs with ≥20,000 total occurrences
2. Calculate how many names were filtered out
3. Calculate median and mean birth year for each name
4. Find top 10 oldest and youngest names for each sex
5. Plot trends for top 5 oldest and youngest names

"Oldest" = lowest median year (popular longest ago)
"Youngest" = highest median year (most recently popular)

**Written Question:** Can birth rate influence which names appear oldest/youngest? Is there a more robust approach?

### Part 5: Making Predictions (11 pts)
Predict ages using: `age = current_year - median_birth_year`

Predict ages for:
- Men: Gerald, Kai, Spencer, Jeffrey
- Women: Madison, Katherine, Anna, Simone

**Written Questions:**
- Do predictions match your experience meeting people with these names?
- What difference does median vs mean vs mode make?

## Functions to Implement

```python
def load_baby_names(data_dir: str) -> pd.DataFrame:
    """Load all SSA baby name files into a single DataFrame."""

def filter_by_count(df: pd.DataFrame, threshold: int = 20000) -> pd.DataFrame:
    """Filter to (name, sex) pairs with total count >= threshold."""

def calculate_median_year(df: pd.DataFrame, name: str, sex: str) -> int:
    """Calculate weighted median birth year for a name."""

def calculate_mean_year(df: pd.DataFrame, name: str, sex: str) -> float:
    """Calculate weighted mean birth year for a name."""

def get_oldest_names(df: pd.DataFrame, sex: str, n: int = 10) -> pd.DataFrame:
    """Get n oldest names (lowest median year) for a sex."""

def get_youngest_names(df: pd.DataFrame, sex: str, n: int = 10) -> pd.DataFrame:
    """Get n youngest names (highest median year) for a sex."""

def predict_age(df: pd.DataFrame, name: str, sex: str, current_year: int = 2024) -> int:
    """Predict age from median birth year."""
```

## Hints

- Use `glob.glob()` to find all `yob*.txt` files
- Extract year from filename: `yob1880.txt` → `1880`
- Use `pd.concat()` to combine DataFrames
- For weighted median: `np.repeat(years, counts)` then `np.median()`
- For weighted mean: `sum(year * count) / sum(count)`

## Grading

| Part | Points |
|------|--------|
| Style & Flow | 15 |
| Part 0: Load Data | 5 |
| Part 1: Sanity Checks | 3 |
| Part 2: Patterns | 10 |
| Part 3: Ruth | 6 |
| Part 4: Oldest/Youngest | 50 |
| Part 5: Predictions | 11 |
| **Total** | **100** |

## Extra Credit (up to 5 pts)

Propose and demonstrate a better metric for predicting age from names.
