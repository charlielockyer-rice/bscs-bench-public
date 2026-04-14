---
total_points: 100
written_questions_points: 46
---

# COMP 341 Homework 4: Houston House Prices - Written Questions Rubric

## Part 1: Data Exploration (24 points)

### Question 1: "Do you think that the missing values are going to be problematic for predicting list price? Why or why not?" (4 pts)

**Expected Insights:**
- Identify which features have missing values (HOA, parking, etc.)
- Assess whether these features are likely predictive of price
- Consider the percentage of missing values
- Discuss imputation strategies or whether to drop features
- Some missing values may be informative (no HOA = NaN for HOA fee)

**Rubric:**
- **Full credit (4 pts)**: Identifies specific features with missing values, percentage, and assesses their importance for price prediction
- **Partial credit (2-3 pts)**: Acknowledges missing values but analysis is incomplete
- **Minimal credit (1 pt)**: Generic statement without specifics
- **No credit (0 pts)**: Missing

### Question 2: "Based on the plots you generated above, do you think differences in zipcode distribution will affect models that predict list prices? Explain." (4 pts)

**Expected Insights:**
- Zipcodes have very different price distributions
- Some zipcodes have few listings (sparse data)
- Models need to handle categorical zipcode appropriately
- One-hot encoding creates many features
- Location is clearly important for price prediction

**Rubric:**
- **Full credit (4 pts)**: Explains how zipcode variation affects modeling, discusses challenges (sparsity, encoding)
- **Partial credit (2-3 pts)**: Notes differences but doesn't connect to modeling implications
- **Minimal credit (1 pt)**: Generic statement
- **No credit (0 pts)**: Missing

### Question 3: "Where are the most expensive homes located?" (4 pts)

**Expected Insights:**
- Identify specific zipcodes with highest median/mean prices
- Reference the violin plot or geographic plot
- May mention specific Houston neighborhoods (River Oaks, Memorial, etc.)
- Should give specific zipcode numbers

**Rubric:**
- **Full credit (4 pts)**: Names specific zipcodes with highest prices, references the data
- **Partial credit (2-3 pts)**: Gives general area but not specific zipcodes
- **Minimal credit (1 pt)**: Vague answer
- **No credit (0 pts)**: Missing

### Question 4: "Is latitude and longitude more / less / similarly informative than zipcode for determining list price? Explain." (4 pts)

**Expected Insights:**
- Lat/long is continuous vs zipcode categorical
- Lat/long captures fine-grained location (within-zipcode variation)
- Zipcode is more interpretable, groups similar areas
- May note that lat/long can show patterns zipcode misses
- Correlation with price may be non-linear for lat/long

**Rubric:**
- **Full credit (4 pts)**: Compares both with specific observations from plots, discusses tradeoffs
- **Partial credit (2-3 pts)**: Makes comparison but lacks depth
- **Minimal credit (1 pt)**: Just says "more" or "less" without reasoning
- **No credit (0 pts)**: Missing

### Question 5: "Based on the correlations, should we remove any features before running linear regression? Explain." (4 pts)

**Expected Insights:**
- Highly correlated features (multicollinearity) can be problematic
- Look for feature pairs with |r| > 0.8 or so
- Examples: beds and baths often correlated with sq_ft
- Removing correlated features can help interpretability
- Or may argue regularization handles this

**Rubric:**
- **Full credit (4 pts)**: Identifies specific correlated feature pairs with reasoning about whether/why to remove
- **Partial credit (2-3 pts)**: Notes correlations but recommendation is unclear
- **Minimal credit (1 pt)**: Generic statement about correlation
- **No credit (0 pts)**: Missing

### Question 6: "If our goal is to reduce the error in our price predictions, design a baseline heuristic for this problem. Explain your rationale." (4 pts)

**Expected Insights:**
- Simple heuristics: global mean/median, mean by zipcode
- Price per square foot * sq_ft
- Consider what information is available
- Should be simple but reasonable
- Justification should connect to data patterns

**Rubric:**
- **Full credit (4 pts)**: Proposes reasonable heuristic with clear rationale tied to data characteristics
- **Partial credit (2-3 pts)**: Reasonable heuristic but weak rationale
- **Minimal credit (1 pt)**: Just says "use the mean" without explanation
- **No credit (0 pts)**: Missing

---

## Part 2: Regression Models (14 points)

### Question 7: "Which columns did you choose to omit from your feature set? Why did you exclude these columns?" (4 pts)

**Expected Insights:**
- ID columns (house_id) - not predictive
- Address/street - too granular, hard to encode
- URL - not predictive
- Highly correlated features (if removed)
- Features with too many missing values
- Rationale should be clear

**Rubric:**
- **Full credit (4 pts)**: Lists specific excluded columns with clear reasoning for each
- **Partial credit (2-3 pts)**: Lists columns but reasoning is incomplete
- **Minimal credit (1 pt)**: Vague answer
- **No credit (0 pts)**: Missing

### Question 8: "Does the linear regression model outperform the baseline heuristic you chose earlier? What does this comparison tell you?" (4 pts)

**Expected Insights:**
- Compare RMSE/RMSLE of linear regression vs baseline
- If model wins: regression captures additional patterns
- If baseline wins: model may be underfitting or features poor
- Baseline provides important reference point
- May discuss what features contributed to improvement

**Rubric:**
- **Full credit (4 pts)**: Reports specific metrics, explains what comparison reveals about the data/model
- **Partial credit (2-3 pts)**: Reports comparison but interpretation is weak
- **Minimal credit (1 pt)**: Just says "yes" or "no"
- **No credit (0 pts)**: Missing

### Question 9: "Using the plot above as a guide, determine if there is an optimal alpha (or small range of alphas) for this task. Explain." (3 pts)

**Expected Insights:**
- Look for where validation error is minimized
- Note if there's a clear elbow/minimum
- Discuss bias-variance tradeoff
- Very small alpha = overfitting, large alpha = underfitting
- May note if optimal alpha is at boundary of search range

**Rubric:**
- **Full credit (3 pts)**: Identifies optimal alpha range with reference to plot, discusses tradeoffs
- **Partial credit (1-2 pts)**: Identifies alpha but reasoning is incomplete
- **No credit (0 pts)**: Missing

### Question 10: "Which SVR kernel has the best performance? Give some intuition as to why you think it outperformed the other kernel choices." (3 pts)

**Expected Insights:**
- Report which kernel won (likely rbf or poly)
- Linear may underfit if relationship is non-linear
- RBF is flexible, can fit complex patterns
- Poly depends on degree
- Connect to the nature of the data (non-linear price relationships)

**Rubric:**
- **Full credit (3 pts)**: Names winning kernel with reasoning about why it fits the data better
- **Partial credit (1-2 pts)**: Names kernel but reasoning is weak
- **No credit (0 pts)**: Missing

---

## Part 3: Cross-validation (8 points)

### Question 11: "Based on these RMSLEs, which max_depth parameter is optimal? Explain." (4 pts)

**Expected Insights:**
- Identify depth where CV RMSLE is minimized
- May note that very deep trees overfit
- Shallow trees underfit
- Optimal is typically in moderate range (5-15)
- Should reference the CV results

**Rubric:**
- **Full credit (4 pts)**: Identifies optimal depth with reasoning based on CV results
- **Partial credit (2-3 pts)**: States depth but reasoning is incomplete
- **Minimal credit (1 pt)**: Random guess
- **No credit (0 pts)**: Missing

### Question 12: "Was cross-validation helpful in choosing the optimal max depth parameter? Why or why not?" (4 pts)

**Expected Insights:**
- Compare CV estimate to single train/validation split
- CV provides more robust estimate (averages over folds)
- Single split may be sensitive to which samples in validation
- CV may show different optimal depth than single split
- Discuss variance in estimates

**Rubric:**
- **Full credit (4 pts)**: Compares CV to single split, explains why CV is more/less helpful in this case
- **Partial credit (2-3 pts)**: Notes CV is useful but doesn't compare to alternative
- **Minimal credit (1 pt)**: Generic statement about CV
- **No credit (0 pts)**: Missing

---

## Grading Summary

| Question | Topic | Points |
|----------|-------|--------|
| Q1 | Missing values impact | 4 |
| Q2 | Zipcode distribution effects | 4 |
| Q3 | Most expensive locations | 4 |
| Q4 | Lat/long vs zipcode | 4 |
| Q5 | Feature correlations | 4 |
| Q6 | Baseline heuristic design | 4 |
| Q7 | Excluded columns | 4 |
| Q8 | Model vs baseline | 4 |
| Q9 | Optimal Lasso alpha | 3 |
| Q10 | SVR kernel comparison | 3 |
| Q11 | Optimal tree depth (CV) | 4 |
| Q12 | CV helpfulness | 4 |
| **Total Written** | | **46** |
