# COMP 341: Practical Machine Learning - Grading Guidelines

## Course Context

COMP 341 is a hands-on machine learning course that emphasizes practical skills over theoretical proofs. Students work with real datasets, building intuition for when and why different ML techniques work.

**Key Learning Objectives:**
- Data exploration and cleaning (handling messy real-world data)
- Feature engineering and selection
- Model selection and hyperparameter tuning
- Interpreting results and communicating insights
- Understanding tradeoffs between different approaches

## Evaluation Philosophy

### What Makes a Good Answer

Written responses in COMP 341 should demonstrate:

1. **Data-Driven Reasoning**: Answers should reference specific observations from the data, plots, or model outputs. Generic statements without grounding in actual results are insufficient.

2. **Pattern Recognition**: Students should identify meaningful patterns in data and explain their significance. Simply describing what's visible without interpretation is not enough.

3. **Critical Thinking**: Good answers consider limitations, alternative explanations, and tradeoffs. Oversimplified or absolute statements miss nuance.

4. **ML Intuition**: Responses should show understanding of why ML methods behave certain ways (e.g., why scaling matters for some models but not others).

### Grading Scale

For each question, evaluate on this scale:

- **Full Credit**: Demonstrates clear understanding with specific, data-driven insights. Addresses the question directly with appropriate reasoning.

- **Partial Credit (75%)**: Shows understanding but lacks specificity or depth. May be correct but generic, or specific but incomplete.

- **Partial Credit (50%)**: Partially addresses the question with some correct elements but significant gaps in reasoning or accuracy.

- **Minimal Credit (25%)**: Attempts to answer but misses key points or shows fundamental misunderstanding.

- **No Credit**: Missing, irrelevant, or completely incorrect.

## Question Type Guidelines

### Data Exploration Questions

These ask students to describe patterns in data.

**Look for:**
- Specific observations (numbers, trends, outliers)
- Plausible explanations for observed patterns
- Connection to domain knowledge when relevant

**Example:**
> Q: "Do you notice any interesting patterns across years?"
>
> Good: "The birth rate shows a clear spike during 1946-1964 (baby boom), drops in the 1970s, and has a smaller increase in the 1980s-90s as baby boomers had children."
>
> Weak: "The birth rate goes up and down over time."

### Model Comparison Questions

These ask students to compare different approaches.

**Look for:**
- Performance metrics comparison (accuracy, RMSE, etc.)
- Reasoning about WHY one method performs differently
- Consideration of practical tradeoffs (speed, interpretability)

**Example:**
> Q: "Did scaling improve performance?"
>
> Good: "Scaling improved logistic regression accuracy from 72% to 78% because logistic regression is sensitive to feature magnitudes. Decision trees were unaffected since they only use threshold comparisons."
>
> Weak: "Yes, it improved performance."

### Interpretation Questions

These ask students to explain model behavior or results.

**Look for:**
- Correct interpretation of coefficients/weights/importances
- Understanding of what features mean in context
- Appropriate caveats about interpretation limits

**Example:**
> Q: "Based on the weights, is BMI associated with increased or decreased CVA risk?"
>
> Good: "The positive coefficient (0.23) for BMI indicates higher BMI is associated with increased CVA risk, holding other features constant. However, this is correlation not causation."
>
> Weak: "BMI increases risk because it's positive."

### Justification Questions

These ask students to defend a choice or design decision.

**Look for:**
- Clear rationale tied to the specific problem
- Consideration of alternatives
- Acknowledgment of limitations

**Example:**
> Q: "Design a baseline heuristic for predicting house prices."
>
> Good: "Predict the mean price for each zipcode. This accounts for neighborhood variation, which the data shows is substantial (prices range from $150K to $2M across zipcodes). This should outperform a global mean baseline."
>
> Weak: "Use the average price."

### Confusion Matrix / Classification Analysis

**Look for:**
- Identification of commonly confused classes
- Plausible explanations for confusion
- Connection to domain knowledge

**Example:**
> Q: "Which clothing items get confused?"
>
> Good: "Shirts and T-shirts are most confused (8% misclassification rate), which makes sense as they have similar silhouettes. Sandals and Ankle Boots are rarely confused as they have distinct shapes."
>
> Weak: "Some items are confused sometimes."

## Common Issues

### Vague or Generic Responses
Many students give answers that could apply to any dataset. Push for specificity by looking for:
- Actual numbers from their analysis
- References to specific features or patterns
- Concrete examples

### Correlation vs Causation
ML students often conflate correlation with causation. Look for appropriate hedging language:
- "associated with" vs "causes"
- "predicts" vs "determines"
- Acknowledgment that observational data has limits

### Visualization Without Insight
Students sometimes describe what a plot shows without saying what it means. A good answer extracts actionable insights, not just visual descriptions.

## Assignment-Specific Notes

### HW1: Baby Names
- Birth rate patterns should mention baby boom, historical events
- Ruth question should connect to Babe Ruth's career timeline (1914-1935)
- Median vs mean discussion should address skewed distributions

### HW2: Movie Critics
- Imputation method comparison is open-ended; accept reasoned arguments for any position
- PCA interpretation should note clustering or lack thereof
- Top critics may differ by method; this is expected and should be discussed

### HW3: Stroke Prediction
- Feature importance should align with medical knowledge (age, hypertension)
- Model comparison should consider both accuracy and interpretability
- Regularization effects on coefficients should be specific

### HW4: House Prices
- Baseline heuristic should be reasonable (mean, median by zipcode, etc.)
- Cross-validation analysis should identify optimal hyperparameters

### HW5: Text Classification
- LDA topics should be coherent and labeled
- Clustering analysis should use both elbow and silhouette

### HW6: Fashion-MNIST
- Convergence analysis should reference training/validation curves
- Confusion patterns should identify specific item pairs
- CNN + classical ML discussion should be thoughtful

### HW7: Hybrid Deep Learning
- Drawing detection estimate should be reasonable (order of magnitude)
- Image vs tabular comparison should reference their specific results
