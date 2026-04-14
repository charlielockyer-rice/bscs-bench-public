---
total_points: 20
---

# Module 6: Sports Analytics - Written Questions Rubric

## Question 1 (5 points)

**Question:** "In the computation of the Least-Squares estimate, we used the fact that y^T X w = w^T X^T y. However, this is not true for any arbitrary matrices w, X, and y. Why is this expression true for this computation?"

**Expected Answer:**

The key insight is that **both expressions result in a scalar (1x1 matrix)**, and for scalars, the transpose equals itself.

Detailed explanation:
1. y is n x 1, X is n x m, w is m x 1
2. y^T is 1 x n
3. y^T X is 1 x m
4. y^T X w is 1 x 1 (a scalar)
5. Similarly, w^T is 1 x m, X^T is m x n, y is n x 1
6. w^T X^T y is also 1 x 1 (a scalar)

Since both expressions produce scalars, and the transpose of a scalar is itself, we have:
- (y^T X w)^T = w^T X^T y (using the rule (ABC)^T = C^T B^T A^T)
- But since y^T X w is a scalar, (y^T X w)^T = y^T X w
- Therefore y^T X w = w^T X^T y

Alternative explanation:
- A scalar can be viewed as a 1x1 matrix
- The transpose of a 1x1 matrix is itself
- Therefore (y^T X w) = (y^T X w)^T = w^T X^T y

**Rubric:**

- **Full credit (5 pts)**: Correctly identifies that both expressions are scalars (1x1 matrices) and that the transpose of a scalar equals itself. May also correctly apply the matrix transpose rule (ABC)^T = C^T B^T A^T.

- **Partial credit (3-4 pts)**: Shows understanding that scalars are involved but explanation is incomplete. May correctly state the transpose property without fully explaining why both expressions are scalars.

- **Minimal credit (1-2 pts)**: Shows some relevant understanding but explanation is confused or incomplete. May mention transposes or scalars without connecting them correctly.

- **No credit (0 pts)**: No answer or explanation shows fundamental misunderstanding of matrix operations.

---

## Question 2 (5 points)

**Question:** "The LASSO algorithm finds the weights which minimize MSE(w) + lambda ||w||_1. How does increasing lambda change the value to be minimized? How does the weight vector output by the LASSO algorithm change as lambda increases?"

**Expected Answer:**

**Effect on the value being minimized:**
- Increasing lambda gives more importance to the regularization term ||w||_1
- The algorithm balances fitting the data (MSE) against keeping weights small (||w||_1)
- With higher lambda, the algorithm "cares more" about having small weights than about fitting the training data perfectly

**Effect on the weight vector:**
- As lambda increases, the weight vector tends toward zero
- More and more weights become exactly zero (sparsity)
- The SoftThreshold operation pushes small weights to exactly zero
- At very high lambda, almost all weights become zero
- At lambda = 0, LASSO gives the same result as least squares

**Why this is useful:**
- Helps prevent overfitting by limiting model complexity
- Performs "feature selection" by zeroing out unimportant features
- Creates simpler, more interpretable models

**Rubric:**

- **Full credit (5 pts)**: Correctly explains that increasing lambda emphasizes the regularization term, causing weights to shrink toward zero. Mentions that weights can become exactly zero (sparsity). Shows understanding of the trade-off between fitting data and keeping weights small.

- **Partial credit (3-4 pts)**: Explains one aspect well but not both. May correctly describe the effect on the optimization without explaining the sparsity effect, or vice versa.

- **Minimal credit (1-2 pts)**: Shows some understanding but explanation is vague or partially incorrect. May incorrectly describe the effect of increasing lambda.

- **No credit (0 pts)**: No answer or completely incorrect understanding.

---

## Question 3 (5 points)

**Question:** "Which method of fitting the weights produced the lowest MSE on the training data? Did these weights also best predict the number of wins on the test 2001-2012 data? What conclusions can you draw from this?"

**Expected Answer:**

**Expected experimental results:**
- Least squares typically produces the lowest MSE on training data
- LASSO with higher lambda produces higher MSE on training data
- However, least squares often does NOT perform best on test data
- LASSO (with appropriate lambda) often performs better on test data

**Conclusions:**
1. **Overfitting**: Low training error doesn't guarantee low test error. Least squares may overfit by learning noise in the training data.

2. **Regularization helps generalization**: LASSO's penalty prevents overfitting, leading to better generalization to new data.

3. **Trade-off**: There's a trade-off between fitting training data perfectly and generalizing to new data.

4. **Model complexity**: Simpler models (fewer non-zero weights) may generalize better than complex models that fit every detail of training data.

**Rubric:**

- **Full credit (5 pts)**: Reports experimental results (which methods performed best on training vs. test data). Correctly draws conclusions about overfitting and the value of regularization. Explains why best training performance doesn't equal best test performance.

- **Partial credit (3-4 pts)**: Reports results but conclusions are incomplete or partially correct. May recognize overfitting without fully explaining it. May give conclusions without reporting specific results.

- **Minimal credit (1-2 pts)**: Shows some understanding but answer is vague or missing key components. May report results without drawing meaningful conclusions.

- **No credit (0 pts)**: No answer or incorrect results with incorrect conclusions.

---

## Question 4 (5 points)

**Question:** "It is often useful to determine which statistics are more or less important in predicting the dependent variable. For example, this might help a Baseball Manager decide which players to draft or what to focus on at practice. Can you use the fitted weights generated by the LASSO algorithm to help figure out which statistics are not important?"

**Expected Answer:**

**Yes, LASSO is excellent for this purpose!**

Key insights:
1. **Zero weights indicate unimportant features**: LASSO tends to set weights to exactly zero for statistics that are not important for prediction. If a weight is zero, that statistic can be ignored.

2. **Sparsity = feature selection**: LASSO effectively performs automatic feature selection. The statistics with non-zero weights are the important ones.

3. **Magnitude matters**: Among non-zero weights, larger absolute values indicate more important statistics (though normalization of features should be considered).

4. **Practical application**:
   - Statistics with zero weights can be ignored in decision-making
   - Focus resources on improving the statistics with large non-zero weights
   - Simplifies the model and makes it more interpretable

**Rubric:**

- **Full credit (5 pts)**: Correctly identifies that LASSO sets unimportant feature weights to zero. Explains that looking at which weights are zero (or very small) identifies unimportant statistics. May mention that this is called feature selection or discuss practical applications.

- **Partial credit (3-4 pts)**: Shows understanding that weights relate to importance but explanation is incomplete. May correctly identify the approach without fully explaining how LASSO creates sparsity.

- **Minimal credit (1-2 pts)**: Shows some relevant understanding but answer is vague. May suggest looking at weights without specifically explaining LASSO's sparsity property.

- **No credit (0 pts)**: No answer or fundamentally incorrect understanding of how weights relate to feature importance.
