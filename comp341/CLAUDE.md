# COMP 341: Practical Machine Learning

## Course Overview

An applied machine learning course covering the full ML pipeline from data exploration through deep learning. Assignments progress from classical statistics to PyTorch neural networks.

**Language:** Python
**Framework:** scikit-learn, PyTorch
**Homeworks:** 7 assignments

## Homework Summary

| HW | Topic | Key Methods | Data Size |
|----|-------|-------------|-----------|
| 1 | Baby Names EDA | pandas, aggregation, statistics | 27MB |
| 2 | Movie Critics | Imputation, PCA, Correlation | 232MB |
| 3 | Stroke Prediction | Decision Trees, Logistic Regression, LDA | <1MB |
| 4 | House Prices (Tabular) | Linear/Lasso/SVR, Cross-validation | <1MB |
| 5 | Text Classification | LDA, t-SNE, K-means, Multiclass | ~5MB |
| 6 | Fashion-MNIST | PyTorch CNN | Auto-download |
| 7 | House Prices (Images) | Hybrid CNN+MLP | 45MB |

## Data Setup

Data files are **not stored in git** due to size (~300MB total).

**Quick setup (if you have the data locally):**
```bash
# Data should be in comp341/data/ with structure:
# data/hw1/  - SSA baby name txt files (yob1880.txt - yob2023.txt)
# data/hw2/  - rotten_tomatoes_*.csv files
# data/hw3/  - CVA.csv
# data/hw4/  - houston_homes*.csv
# data/hw5/  - bag_of_words.csv, tfidf.csv, document_labels.csv, test-*.csv
# data/hw7/  - home_data_*.csv, house_imgs/
```

**Download all data (from GitHub Release, no auth needed):**
```bash
bin/setup_comp341_data           # Downloads all missing data (~114 MB compressed)
bin/setup_comp341_data --check   # Just show what's missing
bin/setup_comp341_data --hw 3    # Download a specific homework
```

Data is hosted as GitHub Release assets at the `comp341-data` tag. `bin/setup_course ./comp341` auto-triggers this if the data directory is missing.

## Directory Structure

```
comp341/
├── CLAUDE.md           # This file
├── runner.py           # Docker-based pytest runner
├── Dockerfile          # ML environment (Python 3.11 + sklearn + torch)
├── docker-compose.yml  # Docker config
├── configs/            # Assignment YAML configs (hw1-hw7)
├── data/               # GITIGNORED - large datasets
├── sources/            # GITIGNORED - original zip files
├── notebooks/          # Original Colab notebooks (reference)
├── templates/          # Solution templates per homework
│   ├── hw1/            # Full template (instructions.md, solution.py, writeup.md)
│   ├── hw2/            # Full template
│   ├── hw3/            # Full template
│   ├── hw4/            # Full template
│   ├── hw5/            # Full template
│   ├── hw6/            # Full template
│   └── hw7/            # Full template
└── tests/              # Test suites (test_module{N}.py)
```

## Docker Architecture

**All tests run in Docker** for consistent environment across machines.

```bash
# Run tests (Docker image builds automatically on first run)
python3 comp341/runner.py hw1 workspaces/agent341_hw1

# Force rebuild Docker image
python3 comp341/runner.py hw1 workspaces/agent341_hw1 --rebuild

# Or use bin/grade (auto-routes to runner.py)
bin/grade workspaces/agent341_hw1
```

**Docker configuration:**
- Image: `comp341-runner` (Python 3.11-slim + ML stack)
- Memory: 4GB limit
- CPUs: 2 cores
- Network: Disabled (no external access)

**Mounts:**
- `/workspace` - Student workspace (solution.py)
- `/comp341` - Course directory (tests, configs) - read-only
- `/data` - Data directory - read-only

## Dependencies (in Docker)

**Core ML:**
- pandas 2.2.0, numpy 1.26.4
- scikit-learn 1.4.0
- plotnine 0.13.3 (ggplot-style visualization)

**Deep Learning (HW6-7):**
- torch 2.2.0 (CPU), torchvision 0.17.0
- torchmetrics 1.6.0
- PIL (Pillow 10.2.0)

## Assignment Details

### HW1: Baby Names (EDA)
- Load SSA data (1880-2023, ~2M rows)
- Filter names by occurrence count
- Calculate median/mean birth years
- Identify oldest/youngest names
- Predict ages from names

### HW2: Movie Critics (Data Cleaning + PCA)
- **Data:** 1.1M critic reviews (226MB), 17K movies
- **Functions:** 12 total (load, clean, filter, impute, analyze)
- Clean non-uniform review scores to 0-100 scale:
  - Fractions: "3/5" → 60, "87/100" → 87
  - Letters: "A" → 95, "B+" → 88, "F" → 50
  - Edge case: "92/10" → 92 (numerator > denominator)
- Three imputation methods: zeros, mean, KNN (k=5)
- Correlation analysis with audience scores
- PCA visualization
- **Tests:** 16 tests, 100 points
- **Timeout:** 900s (KNN imputation takes ~11 min on full dataset)

### HW3: Stroke Prediction (Classification)
- Decision Trees with max_depth tuning (1-20)
- Logistic Regression with L1/L2 regularization
- Linear Discriminant Analysis
- Feature importance extraction

### HW4: House Prices (Regression)
- Linear Regression, Lasso (alpha tuning)
- SVR (linear, poly, rbf kernels)
- Decision Tree Regressor
- 5-fold cross-validation
- Kaggle competition

### HW5: Text Classification (NLP)
- LDA topic modeling (10 topics)
- PCA/t-SNE dimensionality reduction
- K-means clustering (k=2-10, silhouette analysis)
- Multiclass classification with ROC/PR curves
- Kaggle competition

### HW6: Fashion-MNIST (PyTorch CNN)
- CNN architecture:
  - Conv1: 12 filters, 5x5, padding=2, MaxPool 2x2
  - Conv2: 32 filters, 5x5, padding=1, MaxPool 2x2, ReLU
  - FC1: 600 neurons, ReLU
  - FC2: 120 neurons, ReLU
  - FC3: 10 neurons, LogSoftmax
- SGD optimizer, NLLLoss (pairs with LogSoftmax output)
- 15 epochs training

### HW7: House Images (Hybrid DL)
- Custom PyTorch Dataset for images + tabular features
- Three model variants:
  - HybridHouseNN: CNN + MLP concatenated
  - HouseImageOnly: CNN only
  - HouseFeatsOnly: MLP only
- RMSE loss, 30 epochs
- Kaggle competition

## Testing Approach

### Philosophy
Tests focus on **functional correctness** rather than implementation details. The goal is to verify that the agent's solution produces valid results that match the spirit of the assignment, not to enforce a specific coding style.

### Test Tiers
Each homework has tests organized by criticality:

1. **Tier 1 (Core):** Data loading, basic operations - must pass
2. **Tier 2 (Logic):** Statistical calculations, ML model training
3. **Tier 3 (Application):** Predictions, model evaluation
4. **Tier 4 (Validation):** Visualization data, format checks

### Written Response Evaluation
Assignments include short-answer questions marked with `[WRITE YOUR ANSWER HERE]`. These require thoughtful analysis beyond code. A future evaluation framework will assess:
- Presence of expected themes/concepts
- Logical reasoning
- Connection to data/results

See `docs/hw*_testing_strategy.md` for detailed test plans per assignment.

### General Notes
- Tests accept ranges for data-dependent values (row counts vary by data version)
- Approximate matching for rankings (expected items in top-N, not exact positions)
- Visualization tests validate data preparation, not visual output
- Deep learning tests use small subsets for speed
- Kaggle submissions tested for format only

## Gotchas & Learnings

- **[2026-03] bench-cli sandboxing is opt-in**: `bin/bench-cli` runs on the host by default. Use `--sandbox` if you want the agent inside Docker Sandbox. Direct `runner.py` calls still work unchanged.

- **[2026-01] Docker is required**: All tests run in Docker for consistent environment. The runner automatically builds the image on first use (takes ~5 min to download torch).

- **[2026-01] Data symlinks don't work in Docker**: Workspaces have symlinks to `comp341/data/hw{N}/` but Docker can't follow them. The runner mounts the data directory separately at `/data`.

- **[2025-01] Test file naming**: Tests must be named `test_module{N}.py` to match the framework's expectations, even though configs use `hw{N}.yaml`.

- **[2025-01] Inline YAML comments**: The simple YAML parser (`yaml_utils.py`) doesn't strip inline comments. Put comments on separate lines.

- **[2025-01] Filter before oldest/youngest**: The `get_oldest_names()` and `get_youngest_names()` functions must filter to names with >= 20,000 occurrences internally. Otherwise rare names from 1880s dominate.

- **[2025-01] Vectorize for speed**: Using `iterrows()` with `np.repeat()` for weighted median is O(n²) and times out. Use vectorized operations: `np.repeat(df['year'].values, df['count'].values)`.

- **[2025-01] Pandas dtype changes**: Python 3.14+ pandas uses `StringDtype` instead of `object` for strings. Use `pd.api.types.is_string_dtype()` instead of `dtype == 'object'`.

- **[2025-01] Data symlinks**: Workspaces get a symlink to `comp341/data/hw{N}/` rather than copying data. Keeps workspaces small.

- **[2026-02] HW2 score cleaning edge cases**: Fractions like "92/10" should be treated as 92 (on 0-100 scale), not 920 (which would be 92/10*100). When numerator > denominator and numerator ≤ 100, use numerator directly.

- **[2026-02] HW2 KNN imputation is slow**: KNNImputer on the full 1.1M row critic dataset takes ~11 minutes. The fixture uses module scope to avoid re-running, but test_impute_knn and test_correlation_knn will be slow.

- **[2026-02] HW2 filter threshold**: The 500-movie threshold should count movies with actual scores (non-NaN review_score), not total reviews.

## Creating New HW Test Suites

See **[docs/CREATING_HW_TESTS.md](docs/CREATING_HW_TESTS.md)** for the full playbook used to create HW1-7 test suites.

## Known Issues

- HW2 KNN imputation takes ~11 minutes on full 1.1M row dataset - timeout set to 900s
- GPU recommended for HW6-7 but CPU works (slower)

## Status

All 7 homework test suites, instructions.md files, and solution.py templates are implemented and synchronized.
