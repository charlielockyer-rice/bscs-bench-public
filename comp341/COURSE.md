# COMP 341: Practical Machine Learning

## What This Course Is About

Machine learning tutorials make it look easy: load a dataset, call `model.fit()`, done. But real ML work is messier. Data comes in inconsistent formats. Values are missing. Features need transformation. Models need tuning. Results need interpretation.

COMP 341 focuses on these practical challenges—the parts of applied ML that take the most time in industry, research, and hackathons. While many courses teach ML algorithms as mathematical abstractions, this course takes a **data-focused perspective**, teaching students to work with the messy reality of real-world datasets.

The course philosophy: ML algorithms shouldn't be magical black boxes. Understanding how data quality affects model performance, how to validate results properly, and how to choose appropriate methods for specific problems—these skills matter more than memorizing algorithm details.

## Core Concepts

### Data Exploration & Cleaning
Before any modeling, you need to understand your data. What are the attributes? What's missing? What's inconsistent? The first assignments focus entirely on exploration and cleaning—skills that often determine whether an ML project succeeds or fails.

### Feature Engineering
Raw data rarely feeds directly into models. Converting categorical variables, normalizing scales, handling missing values, reducing dimensionality—these transformations shape what models can learn.

### Model Selection & Validation
No single algorithm wins everywhere. The course covers classification (decision trees, logistic regression, LDA), regression (linear, Lasso, SVR), unsupervised learning (clustering, topic modeling), and deep learning (CNNs). More importantly, it teaches how to compare algorithms fairly using cross-validation and appropriate metrics.

### The Full Pipeline
Each assignment works through a complete ML pipeline: load data → explore → clean → transform → model → evaluate. This end-to-end practice builds intuition for how choices at each stage affect final results.

## The Seven Assignments

The assignments progress from pure data work to deep learning, each introducing new techniques while reinforcing the data-first mindset.

### HW1: Baby Names — Exploratory Data Analysis

**The Task:** Analyze 140 years of US baby name records to find trends and predict ages from names.

**What You Learn:**
- Loading and combining multiple data files
- Aggregating and filtering large datasets
- Calculating weighted statistics
- The importance of vectorized operations for performance

**Key Insight:** Even "simple" analysis requires careful thought. How do you define "oldest" names? Raw counts mislead—you need weighted medians that account for birth rates across decades.

**Data:** 2 million rows of name/sex/count/year records (27MB)

---

### HW2: Movie Critics — Data Cleaning & Imputation

**The Task:** Find movie critics whose ratings best predict audience scores.

**What You Learn:**
- Cleaning non-uniform data (scores come as fractions, letter grades, percentages)
- Handling sparse matrices with different imputation strategies
- Correlation analysis
- PCA for visualization

**Key Insight:** Real data is messy. Critics rate movies on different scales ("3/5", "B+", "87/100"). Missing values dominate—most critics review only a fraction of movies. How you handle these issues fundamentally changes your conclusions.

**Data:** 1.1 million critic reviews, 17K movies (243MB total)

---

### HW3: Stroke Prediction — Classification

**The Task:** Predict stroke risk from patient health data.

**What You Learn:**
- Decision trees and the effect of max_depth on overfitting
- Logistic regression with L1/L2 regularization
- Linear Discriminant Analysis
- Interpreting feature importance

**Key Insight:** Simpler models are often better. A shallow decision tree might outperform a deep one on new data. Regularization prevents overfitting by penalizing complexity.

**Data:** Patient health records (<1MB)

---

### HW4: House Prices — Regression

**The Task:** Predict house prices from tabular features.

**What You Learn:**
- Linear regression as a baseline
- Lasso regression and alpha tuning
- Support Vector Regression with different kernels
- K-fold cross-validation for fair comparison

**Key Insight:** Cross-validation reveals how models generalize. A model that memorizes training data (low training error) may fail on new data (high validation error).

**Data:** Houston home sales (<1MB)

---

### HW5: Text Classification — Unsupervised & Supervised

**The Task:** Classify documents using topic modeling and traditional classifiers.

**What You Learn:**
- Topic modeling with Latent Dirichlet Allocation (LDA)
- Dimensionality reduction (PCA, t-SNE) for visualization
- K-means clustering and silhouette analysis
- Multiclass classification with ROC/PR curves

**Key Insight:** Unsupervised methods reveal structure without labels. Topics emerge from word co-occurrence; clusters emerge from feature similarity. These patterns inform supervised approaches.

**Data:** Document term matrices (~5MB)

---

### HW6: Fashion-MNIST — Convolutional Neural Networks

**The Task:** Build a CNN to classify fashion items (shirts, shoes, bags, etc.).

**What You Learn:**
- CNN architecture: convolution, pooling, fully-connected layers
- PyTorch model definition and training loops
- Batch processing and optimization

**Key Insight:** CNNs learn hierarchical features—edges combine into textures, textures into patterns, patterns into objects. Architecture choices (filter sizes, layer depth) affect what the network can learn.

**Data:** 70K grayscale images (auto-downloaded)

---

### HW7: House Prices + Images — Hybrid Deep Learning

**The Task:** Predict house prices using both tabular data AND house photos.

**What You Learn:**
- Custom PyTorch datasets for mixed data types
- Combining CNN (image) and MLP (tabular) feature extractors
- End-to-end training of hybrid architectures

**Key Insight:** Different data types require different architectures. Images need convolutions; tabular data needs dense layers. Combining them requires careful feature fusion.

**Data:** Home sales with photos (45MB)

---

## Why These Skills Matter

The course description puts it well: tutorials and wrapper packages have made ML "accessible to the masses," but algorithms get used as "magical black boxes" without understanding their limitations.

These assignments build the skills that matter in practice:
- **Data intuition:** Knowing what to check before modeling
- **Debugging instinct:** When results look wrong, where to investigate
- **Method selection:** Matching algorithms to problem characteristics
- **Honest evaluation:** Avoiding the trap of overfitting to test data

An agent (or human) who completes these assignments can take a new dataset and work through the full pipeline—not just call library functions, but make informed decisions at each step.

## Technical Environment

### Language & Libraries

All assignments use **Python** with the standard data science stack:

| Category | Libraries |
|----------|-----------|
| Data manipulation | pandas, NumPy |
| Machine learning | scikit-learn |
| Deep learning | PyTorch, torchvision |
| Visualization | plotnine, matplotlib |

### Execution Environment

Tests run in **Docker containers** to ensure reproducibility:

- **Base image:** Python 3.11
- **Memory limit:** 4GB
- **CPU limit:** 2 cores
- **Network:** Disabled (no external API calls)

This isolated environment ensures solutions work consistently regardless of the host machine.

### Data Files

Datasets total ~300MB and are not stored in the repository. They range from small CSV files to large image collections. Some assignments (HW2) involve genuinely large data (1M+ rows) that takes time to process—this is intentional, as real ML work involves real-sized data.

## How We Evaluate Solutions

### Automated Testing

Each assignment has a pytest test suite that validates functional correctness. Tests check:

- **Data loading:** Correct structure, expected columns, reasonable row counts
- **Transformations:** Cleaning produces valid ranges, imputation removes NaN
- **Model outputs:** Predictions in expected format, reasonable accuracy
- **Analysis results:** Correlations in valid ranges, top-N results contain expected items

### Testing Philosophy

Tests verify that solutions **work correctly**, not that they match a specific implementation:

- **Ranges over exact values:** Accept accuracy > 0.7, not accuracy == 0.732
- **Flexible rankings:** Check that expected items appear in top-N, not exact order
- **No style requirements:** Any working code is accepted
- **Data-version tolerance:** Slight variations between dataset versions are okay

### What We Don't Test

- Visualization aesthetics (only data preparation for plots)
- Exact model weights (training is non-deterministic)
- Code style or documentation quality
- Execution speed (within generous timeouts)

### Timeouts

Most assignments complete in under 2 minutes. HW2 is an exception—KNN imputation on 1M+ rows takes ~11 minutes, so its timeout is set to 20 minutes.

## Relationship to CLAUDE.md

This file (COURSE.md) explains what the course teaches and why.

The companion file **CLAUDE.md** contains operational details for working with the codebase: directory structure, how to run tests, Docker configuration, known issues, and gotchas discovered during development.

Read COURSE.md to understand the course. Read CLAUDE.md to work with the code.
