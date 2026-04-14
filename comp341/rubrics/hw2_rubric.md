---
total_points: 100
written_questions_points: 40
---

# COMP 341 Homework 2: Movie Critics - Written Questions Rubric

## Part 0: Getting to Know the Data (6 points)

### Question 1: "How many unique critics are there in this data?"

**Expected Answer:**
- After removing critics with no name: approximately 6,000-8,000 unique critics
- Exact number depends on data version and cleaning approach

**Rubric:**
- **Full credit (2 pts)**: Provides a specific number that's plausible (~6K-8K range)
- **No credit (0 pts)**: Missing, wildly incorrect, or just says "many"

### Question 2: "Give an explanation for the cutoff you chose above." (4 pts)

**Expected Insights:**
- Distribution is highly skewed (most critics review few movies)
- Need enough data points per critic for reliable correlation
- Tradeoff: higher cutoff = fewer critics but more reliable estimates
- Common choices: 50, 100, 200, 500 movies
- Should reference the distribution plot

**Rubric:**
- **Full credit (4 pts)**: Justifies cutoff with reference to distribution shape and statistical reasoning
- **Partial credit (2-3 pts)**: Reasonable cutoff but weak justification
- **Minimal credit (1 pt)**: States a number without any reasoning
- **No credit (0 pts)**: Missing

---

## Part 1: Reviewer Bias (4 points)

### Question 3: "Using the plot, would you say that critics are more likely to write a negative or positive movie review?"

**Expected Insights:**
- Distribution of fresh% is roughly centered or slightly positive-skewed
- Most critics have >50% fresh reviews (critics are generally positive)
- Some outliers exist (extremely harsh or generous critics)
- May note bimodal tendencies or specific percentages from the plot

**Rubric:**
- **Full credit (4 pts)**: States a clear position (positive/negative bias) with specific evidence from the plot (e.g., "Most critics have 60-80% fresh reviews")
- **Partial credit (2-3 pts)**: Correct conclusion but lacks specificity
- **Minimal credit (1 pt)**: Says "positive" or "negative" without evidence
- **No credit (0 pts)**: Missing or contradicts the data

---

## Part 3: Handling Missing Values (24 points)

### Question 4: "List the top 5 critic names that are the most correlated with the audience score." (Zero imputation) (4 pts)

**Expected Format:**
- List of 5 critic names
- These should be real critics from the dataset
- Order matters (most correlated first)

**Rubric:**
- **Full credit (4 pts)**: Lists 5 plausible critic names in order
- **Partial credit (2-3 pts)**: Lists critics but incomplete or wrong format
- **No credit (0 pts)**: Missing or clearly fabricated names

### Question 5: "List the top 5 critic names that are the most correlated with the audience score." (Mean imputation) (4 pts)

**Same rubric as Question 4**

### Question 6: "List the top 5 critic names that are the most correlated with the audience score." (KNN imputation) (4 pts)

**Same rubric as Question 4**

### Question 7: "Compare the top 5 critics identified using the 3 different imputation methods. Did you expect them to be the same or all different? What does this say about the choice of imputation method?" (6 pts)

**Expected Insights:**
- Results may differ across methods (some critics appear in multiple, some don't)
- Zero imputation penalizes critics who reviewed fewer movies
- Mean imputation assumes critics are "average" when they don't review
- KNN uses movie similarity, may be more realistic
- Imputation choice affects downstream conclusions
- No single "correct" method - depends on assumptions

**Rubric:**
- **Full credit (6 pts)**: Compares specific results, explains why differences occur, discusses implications for imputation choice
- **Partial credit (4-5 pts)**: Notes differences but shallow analysis of why
- **Partial credit (2-3 pts)**: Generic discussion without specific comparison
- **No credit (0 pts)**: Missing

### Question 8: "The PCA plots look different depending on how NaNs are handled (with 0s or KNN). Does this imply that one missing value imputation method is better than the other?" (6 pts)

**Expected Insights:**
- Different visual patterns don't mean one is "better"
- Zero imputation creates artificial separation (non-reviewers clustered)
- KNN may preserve more natural relationships
- "Better" depends on the goal:
  - For visualization: may prefer KNN (less artificial clustering)
  - For prediction: depends on downstream task performance
  - For interpretability: may prefer simpler methods
- Both have assumptions that may or may not be valid

**Rubric:**
- **Full credit (6 pts)**: Argues that "better" is context-dependent with specific reasoning about what each method assumes
- **Partial credit (4-5 pts)**: Acknowledges no clear winner but lacks depth on why
- **Partial credit (2-3 pts)**: Claims one is definitively better without nuance
- **No credit (0 pts)**: Missing

---

## Final Recommendations (6 points)

### Question 9: "Based on your analysis, which 3 critics would you recommend for predicting the general audience score?"

**Expected Insights:**
- Should select critics that appear consistently across imputation methods
- OR justify why a specific imputation method is most appropriate
- Consider both correlation and reviewer bias
- May consider number of reviews (more reviews = more reliable)
- Names should be from actual top critics identified

**Rubric:**
- **Full credit (6 pts)**: Names 3 specific critics with clear justification based on the analysis (correlation, consistency across methods, bias consideration)
- **Partial credit (4-5 pts)**: Names critics but weak justification
- **Partial credit (2-3 pts)**: Generic answer without specific names
- **No credit (0 pts)**: Missing

---

## Grading Summary

| Question | Topic | Points |
|----------|-------|--------|
| Q1 | Number of unique critics | 2 |
| Q2 | Cutoff justification | 4 |
| Q3 | Reviewer bias direction | 4 |
| Q4 | Top 5 (zero imputation) | 4 |
| Q5 | Top 5 (mean imputation) | 4 |
| Q6 | Top 5 (KNN imputation) | 4 |
| Q7 | Imputation method comparison | 6 |
| Q8 | PCA interpretation | 6 |
| Q9 | Critic recommendations | 6 |
| **Total Written** | | **40** |
