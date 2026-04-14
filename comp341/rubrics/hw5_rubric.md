---
total_points: 100
written_questions_points: 22
---

# COMP 341 Homework 5: Text Classification - Written Questions Rubric

## Part 1: Data Exploration (14 points)

### Question 1: "Given the topics you identified with LDA and the dimensionality reduction plot above, do you think that there is enough information in our data (specifically, the bag of words) for the classification task of predicting the category for a passage of text? Explain." (6 pts)

**Expected Insights:**
- LDA topics should show some coherent themes
- Dimensionality reduction plot may show clusters by category
- If categories cluster well = good signal for classification
- If overlapping/mixed = harder classification task
- Bag of words captures content but loses context/order
- May note that some categories are easier to distinguish than others

**Rubric:**
- **Full credit (6 pts)**: Analyzes both LDA topics and DR plot, makes reasoned assessment of classification feasibility with specific observations
- **Partial credit (4-5 pts)**: Analyzes one or the other well, or both superficially
- **Partial credit (2-3 pts)**: Generic statement without reference to results
- **No credit (0 pts)**: Missing

### Question 2: "Using the elbow plot and the silhouette plots above, how many authors/speakers do you think there are for the 'other' category of documents? Explain your choice." (8 pts)

**Expected Insights:**
- Elbow plot: look for "elbow" where adding clusters stops helping much
- Silhouette: higher is better, look for peak
- May get conflicting signals from the two methods
- Should pick a number and justify with both plots
- Typical range: 3-6 clusters is reasonable
- Acknowledge uncertainty in the estimate

**Rubric:**
- **Full credit (8 pts)**: References both plots specifically, identifies where signals point, makes reasoned estimate with uncertainty acknowledgment
- **Partial credit (5-7 pts)**: Uses one method well or both superficially
- **Partial credit (2-4 pts)**: Just states a number without referencing plots
- **No credit (0 pts)**: Missing

---

## Part 2: Text Classification (8 points)

### Question 3: "Looking at the confusion matrix, are there any categories that tend to get mixed up more than others? Does this make sense to you?" (8 pts)

**Expected Insights:**
- Identify specific category pairs with high confusion
- Explain WHY those categories might be confused:
  - Similar vocabulary (e.g., academics vs writers)
  - Overlapping topics
  - Similar speaking/writing styles
- Note categories that are well-separated
- May mention class imbalance effects

**Rubric:**
- **Full credit (8 pts)**: Identifies specific confused pairs with plausible explanations for why confusion occurs
- **Partial credit (5-7 pts)**: Identifies pairs but explanations are weak
- **Partial credit (2-4 pts)**: Generic statement about confusion without specifics
- **No credit (0 pts)**: Missing

---

## Grading Summary

| Question | Topic | Points |
|----------|-------|--------|
| Q1 | LDA + DR classification assessment | 6 |
| Q2 | Number of 'other' authors estimate | 8 |
| Q3 | Confusion matrix interpretation | 8 |
| **Total Written** | | **22** |

## Extra Credit: Which Presidents? (up to 10 pts)

### Extra Credit Question: "Which presidents do you think are in the training data?"

Students attempt to identify 4 anonymized presidents from a set of 20 possibilities using outlier detection, clustering, or classification methods.

**Rubric:**
- **Participation (2 pts)**: Good faith attempt with predictions for any of the 20 presidents
- **Correct identification (2 pts each, up to 8 pts)**: Each correctly identified president
