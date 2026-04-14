---
total_points: 20
---

# Module 3: Stock Prediction - Written Questions Rubric

## Question 1 (5 points)

**Question:** "What is the order of the model that works best for each stock/index? If the orders are not the same, discuss why that might be the case."

**Expected Answer:**

The answer should:
1. Report the experimental results showing which order (1, 2, 3, etc.) gave the lowest MSE for each stock (DJIA, GOOG, FSLR)
2. If orders differ, explain possible reasons such as:
   - Different stocks have different patterns of volatility
   - Some stocks may have more random day-to-day behavior (lower order better)
   - Some stocks may have more predictable patterns based on recent history (higher order better)
   - The amount of training data limits how high an order can be effective

**Rubric:**

- **Full credit (5 pts)**: Reports specific results for each stock/index with the best-performing order. Provides thoughtful explanation for why different stocks might have different optimal orders, relating to the nature of Markov chains and stock behavior.

- **Partial credit (3-4 pts)**: Reports results but explanation is vague or incomplete. May correctly identify best orders without explaining why they differ. May give a reasonable explanation without specific experimental results.

- **Minimal credit (1-2 pts)**: Provides some results or explanation but with significant gaps or errors. May guess at orders without running experiments. Explanation shows limited understanding.

- **No credit (0 pts)**: No answer or completely incorrect results with no valid reasoning.

---

## Question 2 (5 points)

**Question:** "Which stock/index can you predict with the lowest error? Based on the plots of the day-to-day change in stocks and the histogram of bins, can you guess why that stock/index is easiest to predict?"

**Expected Answer:**

The answer should:
1. Identify which stock had the lowest prediction error (typically DJIA)
2. Explain why based on the data characteristics:
   - DJIA tends to have smaller, more consistent changes (most values in bins 1 and 2)
   - More volatile stocks (like FSLR) have changes spread across all bins
   - A stock with most changes in the middle bins is easier to predict because the model learns that "middle" states are most common
   - Less volatility means fewer extreme values to predict incorrectly

**Rubric:**

- **Full credit (5 pts)**: Correctly identifies the most predictable stock and provides a clear explanation based on the distribution of price changes. Connects the histogram/bin distribution to prediction difficulty.

- **Partial credit (3-4 pts)**: Identifies the correct stock but explanation is incomplete or doesn't fully connect to the data characteristics. May give a reasonable but unsupported explanation.

- **Minimal credit (1-2 pts)**: Makes an attempt but the answer is unclear or the explanation doesn't relate to the actual data. May identify wrong stock with some valid reasoning.

- **No credit (0 pts)**: No answer or completely incorrect with no valid reasoning.

---

## Question 3 (5 points)

**Question:** "Given that we have divided the day-to-day price change into 4 bins, how many possible states are there in an n-th order Markov chain for predicting the change in stock price?"

**Expected Answer:**

The answer is **4^n** (4 to the power of n).

Explanation:
- Each state in an n-th order Markov chain represents the last n observations
- Each observation can be in one of 4 bins (0, 1, 2, or 3)
- Therefore, there are 4 choices for each of the n positions
- Total states = 4 * 4 * ... * 4 (n times) = 4^n

Examples:
- Order 1: 4 states (0, 1, 2, 3)
- Order 2: 16 states ((0,0), (0,1), ..., (3,3))
- Order 3: 64 states

**Rubric:**

- **Full credit (5 pts)**: Correctly states 4^n with clear reasoning about why (n positions, 4 choices each). May include examples for specific values of n.

- **Partial credit (3-4 pts)**: Gives correct formula but explanation is weak or unclear. May show understanding through examples without stating the general formula.

- **Minimal credit (1-2 pts)**: Shows some understanding of combinatorics but gets the formula wrong. May confuse with 4*n or other incorrect formulas.

- **No credit (0 pts)**: No answer or completely incorrect with no relevant reasoning.

---

## Question 4 (5 points)

**Question:** "The training data we gave you covers two years of data, with 502 data points per stock/index. With that data, is it possible to see all of the possible states in an n-th order Markov chain? What are the constraints on n? How do you think it would affect the accuracy of the model if there were not enough data?"

**Expected Answer:**

The answer should address:

1. **Constraint on n**: With 502 data points, we can observe at most 502 - n + 1 state transitions. To reliably observe all 4^n states, we need significantly more transitions than states.
   - For n=1: 4 states, plenty of data
   - For n=2: 16 states, still manageable
   - For n=3: 64 states, getting sparse
   - For n=4: 256 states, likely many unseen
   - For n=5: 1024 states, more states than data points!

2. **Practical constraint**: n should be small enough that 4^n << 502. Roughly n ≤ 4 or so.

3. **Effect of insufficient data**:
   - Many states will never be observed (no transition probabilities)
   - Observed states may have unreliable probabilities (based on few samples)
   - Model must fall back to random guessing for unseen states
   - Higher-order models become less accurate, not more
   - Overfitting: model memorizes training data rather than learning patterns

**Rubric:**

- **Full credit (5 pts)**: Correctly explains that we need data >> 4^n states to see all states. Gives a reasonable constraint on n (approximately n ≤ 4-5). Explains how sparse data leads to unreliable probabilities and the need for fallback behavior.

- **Partial credit (3-4 pts)**: Addresses most aspects but explanation is incomplete. May correctly identify the constraint without explaining the accuracy implications, or vice versa.

- **Minimal credit (1-2 pts)**: Shows some understanding but answer is vague or contains significant errors. May recognize that "more data is better" without explaining why in terms of state coverage.

- **No credit (0 pts)**: No answer or completely incorrect reasoning.
