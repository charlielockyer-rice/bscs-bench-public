---
total_points: 100
written_questions_points: 60
---

# COMP 341 Homework 3: Stroke Prediction - Written Questions Rubric

## Part 0: Getting Familiar with the Data (16 points)

### Question 1: "How many individuals in this dataset?" (2 pts)

**Expected Answer:**
- Approximately 5,000 individuals (exact count from data)

**Rubric:**
- **Full credit (2 pts)**: Provides correct count
- **No credit (0 pts)**: Missing or incorrect

### Question 2: "List the nominal features, ordinal features, and numeric features in this data." (4 pts)

**Expected Insights:**
- **Nominal (categorical, no order):** sex, ever_married, work_type, residence_type, smoking_status
- **Ordinal (categorical, ordered):** None obvious, though some may argue hypertension/heart_disease could be ordinal
- **Numeric (continuous):** age, avg_glucose_level, bmi
- **Binary (could be nominal or separate):** hypertension, heart_disease, CVA

**Rubric:**
- **Full credit (4 pts)**: Correctly categorizes most features with reasonable justification
- **Partial credit (2-3 pts)**: Gets categories mostly right but misses some
- **Minimal credit (1 pt)**: Attempts categorization but many errors
- **No credit (0 pts)**: Missing

### Question 3: "Do you think the missing values determined above will be problematic during our classification task? Why or why not?" (5 pts)

**Expected Insights:**
- Missing values are primarily in BMI column
- ~200-300 missing BMI values out of ~5000 (about 4-5%)
- This is a small percentage, so impact may be limited
- However, BMI could be predictive of CVA
- Imputation approach matters (mean, median, model-based)
- Could potentially drop rows, but loses data

**Rubric:**
- **Full credit (5 pts)**: Identifies which feature has missing values, percentage, and discusses potential impact with reasoning
- **Partial credit (3-4 pts)**: Acknowledges missing values but analysis is incomplete
- **Minimal credit (1-2 pts)**: Generic statement without specifics
- **No credit (0 pts)**: Missing

### Question 4: "Which features do you think will be the most informative? Explain." (5 pts)

**Expected Insights:**
- Domain knowledge suggests: age, hypertension, heart_disease, smoking_status, bmi, glucose
- Age is often the strongest predictor of stroke
- Medical conditions (hypertension, heart disease) are known risk factors
- Lifestyle factors (smoking, BMI) also relevant
- May note that some features seem less relevant (work_type, residence_type)

**Rubric:**
- **Full credit (5 pts)**: Identifies multiple plausible features with medical/logical reasoning
- **Partial credit (3-4 pts)**: Identifies features but reasoning is weak or limited
- **Minimal credit (1-2 pts)**: Lists features without reasoning
- **No credit (0 pts)**: Missing

---

## Part 1: Decision Trees (16 points)

### Question 5: "Did you notice any performance (accuracy) improvements after adding the categorical features? Comment on why this did or did not help." (4 pts)

**Expected Insights:**
- Compare accuracy with numeric-only vs numeric+categorical
- Categorical features may help by providing additional splits
- Or may not help if categories don't separate classes well
- One-hot encoding increases dimensionality
- Results may vary based on data and random seed

**Rubric:**
- **Full credit (4 pts)**: Reports specific accuracy values and explains why categorical features did/didn't help based on decision tree mechanics
- **Partial credit (2-3 pts)**: Reports results but explanation is weak
- **Minimal credit (1 pt)**: Just says "yes" or "no" without data
- **No credit (0 pts)**: Missing

### Question 6: "Did you notice any performance (accuracy) improvements after scaling? Comment on why scaling did or did not help." (4 pts)

**Expected Insights:**
- Decision trees should NOT be affected by scaling
- Trees use threshold comparisons, not distances
- Any change in accuracy is likely due to random variation
- This contrasts with distance-based methods like SVM, KNN

**Rubric:**
- **Full credit (4 pts)**: Correctly explains that scaling shouldn't affect decision trees because of threshold-based splitting
- **Partial credit (2-3 pts)**: Notes little/no change but doesn't explain why
- **Minimal credit (1 pt)**: Incorrect reasoning (e.g., claims scaling helps trees)
- **No credit (0 pts)**: Missing

### Question 7: "Which maximum depth parameter is the best choice?" (4 pts)

**Expected Insights:**
- Should identify depth where test accuracy peaks or plateaus
- Typically depth 3-10 range for this dataset
- Training accuracy keeps increasing with depth (overfitting)
- Test accuracy peaks then may decrease
- Should reference the plot of depth vs accuracy

**Rubric:**
- **Full credit (4 pts)**: Identifies specific depth with reasoning based on test accuracy, discusses train/test gap
- **Partial credit (2-3 pts)**: States a depth but reasoning is incomplete
- **Minimal credit (1 pt)**: Random guess without justification
- **No credit (0 pts)**: Missing

### Question 8: "Looking at the feature importances determined above, does this match your initial expectations? Why or why not?" (4 pts)

**Expected Insights:**
- Compare to predictions from Question 4
- Age is typically most important (matches medical knowledge)
- Some surprises may occur (e.g., sex might be unimportant)
- Feature importance reflects what tree used for splits
- May differ from medical literature due to data characteristics

**Rubric:**
- **Full credit (4 pts)**: Compares actual importances to initial predictions with specific discussion of matches/surprises
- **Partial credit (2-3 pts)**: Notes some comparison but lacks depth
- **Minimal credit (1 pt)**: Generic statement without specifics
- **No credit (0 pts)**: Missing

---

## Part 2: Logistic Regression (16 points)

### Question 9: "Does scaling have an effect on the performance for logistic regression? Why or why not?" (4 pts)

**Expected Insights:**
- Scaling typically DOES affect logistic regression
- Gradient descent converges faster with scaled features
- Features on same scale contribute more equally
- Without scaling, large-scale features dominate
- May see accuracy improvement or faster convergence

**Rubric:**
- **Full credit (4 pts)**: Correctly explains how scaling affects gradient-based optimization and feature contribution
- **Partial credit (2-3 pts)**: Notes effect but explanation is incomplete
- **Minimal credit (1 pt)**: Incorrect reasoning
- **No credit (0 pts)**: Missing

### Question 10: "Based on the above weights, are the following features associated with an increased or a decreased chance of a CVA: living in an urban area, being married, bmi?" (4 pts)

**Expected Insights:**
- Look at coefficient signs
- Positive coefficient = increased CVA risk
- Negative coefficient = decreased CVA risk
- Must account for one-hot encoding (reference category)
- BMI typically positive (higher BMI, higher risk)
- Urban/married effects depend on data

**Rubric:**
- **Full credit (4 pts)**: Correctly interprets coefficient signs for all three features
- **Partial credit (2-3 pts)**: Gets some correct but not all
- **Minimal credit (1 pt)**: Attempts interpretation but mostly incorrect
- **No credit (0 pts)**: Missing

### Question 11: "Do the different regularization methods change the feature weights? If so, which features are affected?" (4 pts)

**Expected Insights:**
- L1 (Lasso) tends to zero out coefficients (feature selection)
- L2 (Ridge) shrinks coefficients but rarely to zero
- No regularization gives largest coefficients
- Compare specific features across methods
- Less important features affected more by L1

**Rubric:**
- **Full credit (4 pts)**: Explains how L1 vs L2 affect weights differently with specific examples from the data
- **Partial credit (2-3 pts)**: Notes differences but explanation is incomplete
- **Minimal credit (1 pt)**: Generic statement about regularization
- **No credit (0 pts)**: Missing

### Question 12: "Based on the feature weights across the different regularization schemes above, would you say that regularization helps prevent overfitting in this case? Explain." (4 pts)

**Expected Insights:**
- Compare train/test accuracy with different regularization
- If gap is smaller with regularization, it helps
- May note coefficient magnitudes are more reasonable
- For small datasets, regularization often helps
- Could argue either way with evidence

**Rubric:**
- **Full credit (4 pts)**: Uses evidence from results (accuracy, coefficients) to argue whether regularization helps
- **Partial credit (2-3 pts)**: Makes claim but evidence is weak
- **Minimal credit (1 pt)**: Generic statement without evidence
- **No credit (0 pts)**: Missing

---

## Part 3: LDA and Final Evaluation (12 points)

### Question 13: "Can the coefficients that are extracted from LDA be interpreted as feature importances? Why or why not?" (4 pts)

**Expected Insights:**
- LDA coefficients are discriminant coefficients
- They indicate direction of maximum class separation
- Not the same as "importance" in regression sense
- Magnitude depends on feature scaling
- Can indicate which features separate classes, but interpretation differs from logistic regression

**Rubric:**
- **Full credit (4 pts)**: Correctly explains what LDA coefficients represent and how interpretation differs from regression
- **Partial credit (2-3 pts)**: Partial understanding of LDA coefficient meaning
- **Minimal credit (1 pt)**: Incorrect or confused explanation
- **No credit (0 pts)**: Missing

### Question 14: "Now that you have run three different classification methods, which method do you think is best suited for the CVA prediction problem? Explain." (4 pts)

**Expected Insights:**
- Compare accuracy across methods
- Consider interpretability (logistic regression interpretable)
- Consider clinical context (need to explain to doctors/patients)
- Decision trees are easily visualized
- LDA assumes Gaussian distributions
- No single "right" answer, but needs justification

**Rubric:**
- **Full credit (4 pts)**: Recommends method with multiple criteria (accuracy, interpretability, assumptions)
- **Partial credit (2-3 pts)**: Recommends but reasoning is limited to accuracy only
- **Minimal credit (1 pt)**: No justification
- **No credit (0 pts)**: Missing

### Question 15: "Which method would you use for identifying risk factors for CVAs? Explain." (4 pts)

**Expected Insights:**
- For risk factors, interpretability is key
- Logistic regression coefficients = odds ratios (meaningful)
- Decision tree can show important splits but not magnitude
- LDA coefficients less intuitive
- May prefer logistic regression for clinical communication

**Rubric:**
- **Full credit (4 pts)**: Recommends method based on interpretability for clinical use, explains why
- **Partial credit (2-3 pts)**: Reasonable recommendation but weak reasoning
- **Minimal credit (1 pt)**: No reasoning
- **No credit (0 pts)**: Missing

---

## Grading Summary

| Question | Topic | Points |
|----------|-------|--------|
| Q1 | Number of individuals | 2 |
| Q2 | Feature types | 4 |
| Q3 | Missing values impact | 5 |
| Q4 | Informative features prediction | 5 |
| Q5 | Categorical features effect | 4 |
| Q6 | Scaling effect on trees | 4 |
| Q7 | Optimal max depth | 4 |
| Q8 | Feature importance comparison | 4 |
| Q9 | Scaling effect on logistic regression | 4 |
| Q10 | Coefficient interpretation | 4 |
| Q11 | Regularization effects | 4 |
| Q12 | Regularization and overfitting | 4 |
| Q13 | LDA coefficient interpretation | 4 |
| Q14 | Best method for prediction | 4 |
| Q15 | Best method for risk factors | 4 |
| **Total Written** | | **60** |
