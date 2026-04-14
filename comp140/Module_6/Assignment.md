# Module 6: Sports Analytics

The goal of this assignment is to use the computational thinking process to solve a real world problem. We will follow the process of:
1. Reading and understanding the problem description.
2. Determining the inputs and outputs.
3. Decomposing the problem into subproblems, as appropriate.
4. Designing a computational recipe (algorithm) to solve the subproblems/problem.
5. Implementing your solution.

Be sure to read the entire assignment before beginning.

## Testing Your Solution

Use the `grade` tool to test your implementation:
```bash
bin/grade ./workspaces/<your_workspace>
```

Or if working in a workspace, simply use the grade tool provided by the agent harness.

---

## 1. The Problem

In this assignment, you will predict the outcome of baseball seasons.

We will provide you with 122 different statistics for each season. These statistics fall into 4 categories:
1. The number of games a team won in a given season.
2. Aggregate player stats of all players on the team that season.
3. Aggregate player stats of all players leaving the team before the next season.
4. Aggregate player stats of all players joining the team before the next season.

Given all of this data about one season, can we predict how many games the same baseball team will win next season?

If you are interested in baseball, can you guess which statistics will be most important?

## 2. A Solution Strategy

First, make sure you understand the problem we are trying to solve. Once you do, we need to develop a solution strategy. We are going to use matrices to solve this problem.

We will be using a Linear model to characterize the relationship between the statistics and the number of wins. This means that we will attempt to express our measured variable, the number of wins, as a weighted sum of our explanatory variables. Given a number of statistics $x_1, \cdots, x_{122}$ for a single team for a single year, we will model the number of wins next year $y$ using:

$x_1w_1 + x_2w_2 + \cdots + x_{122}w_{122} = y$

We can capture this equation more simply using matrices:

$\left( \begin{array}{ccc} x_1 & x_2 & \cdots & x_{122}\end{array} \right) \left( \begin{array}{ccc} w_1\\ w_2 \\ \cdots \\ w_{122} \end{array} \right) = \left( y \right)$

Once our model is in this form, it becomes easy to predict many teams and many years at once. Each row of the $X$ matrix will represent the statistics for a single team for a single year, and the corresponding row in the $y$ matrix will be the number of games that team won in the *next* year.

$\left( \begin{array}{ccc} x_{1,1} & x_{1,2} & \cdots & x_{1,122} \\ x_{2,1} & x_{2,2} & \cdots & x_{2,122} \\ \vdots & \vdots & \ddots & \vdots \\ x_{n,1} & x_{n,2} & \cdots & x_{n,122} \end{array} \right) \left( \begin{array}{ccc} w_1\\ w_2 \\ \cdots \\ w_{122} \end{array} \right) = \left( \begin{array}{ccc} y_1 \\ y_2 \\ \cdots \\ y_n \end{array} \right)$

For example, the first row might be statistics for the Houston Astros in 1954. The second row might be statistics for the Astros in 1955. The $537^{th}$ row might be statistics for the Boston Red Sox in 1998. And so on.

More generally, call the $n \times m$ matrix of statistics $X$, the $m \times 1$ matrix of weights $w$, and the $n \times 1$ matrix of wins $y$. Then our linear model can be summarized as $Xw = y$.

Note that in this discussion we have not said anything about what the weights $w$ of our weighted sum actually are. These weights will be the parameters of our model; to fit the linear model to real world historical baseball data, we must develop an algorithm to choose these weights.

We will use two different methods for determining the weights. Recall that the Mean Squared Error is defined as the sum of the squares of the differences between the values predicted by the model and the actual results. In order to deal with this mathematically, we must find a formula for the Mean Squared Error $MSE(w)$ directly in terms of the weights $w$ and the training data.

The first method, the Least Squares Estimate, finds the $w$ which minimizes $MSE(w)$ for the training data.

The second approach is known as the LASSO Estimate (LASSO stands for "Least Absolute Shrinkage and Selection Operator"). In this, we attempt to find a set of weights which best matches the training data while **also** minimizing the complexity of the weights. Complexity is defined as the sum of the absolute value of all weights (sometimes called the 1-norm):

$\left\lVert w \right\rVert_1 = \displaystyle\sum_{j=0}^{m-1} \lvert w_j \rvert$

We'll introduce a new parameter for our model, $\lambda$, and attempt to find a value for $w$ which minimizes the quantity $MSE(w) + \lambda \left\lVert w \right\rVert_1$.

## 3. Breaking Down the Problem

You should start your implementation with the provided template (`solution.py` in your workspace).

### A. Reading in Data

#### i. Recipe

Clearly describe the recipe which takes a string as input and produces a matrix filled with the data from the string. The string may contain multiple lines; each line contains comma-separated decimal values. All values in the string should be treated as real numbers. When run on a simple CSV file with content:
```
2.5,6,3
1,2,3
```
your recipe should produce the matrix:

$\left( \begin{array}{ccc} 2.5 & 6 & 3 \\ 1 & 2 & 3 \end{array} \right)$

You should describe this recipe in clear, concise English.

#### ii. Code

Write a function, `read_matrix(filename)`, which reads a matrix from a file. Instead of accepting a string of contents as input, the `filename` parameter should be a string representing the name of a file. The function should return a matrix; the contents of the matrix should match the contents of the file.

### B. Understanding the Model: Generating Predictions

To understand the linear model we are using, we must understand how the model generates predictions when faced with new data.

#### i. Recipe

Clearly describe the recipe which takes as input:
1. A linear model using the $m \times 1$ matrix of weights $w$.
2. $X$: $n \times m$ matrix of explanatory variables

The recipe should produce an $n \times 1$ matrix, the values predicted by the model when provided with the data $X$.

You should describe this recipe in clear concise English.

#### ii. Code

Write a method on the `LinearModel` class, `generate_predictions(self, inputs)`, which implements your recipe. The input `inputs` should be an $n \times m$ matrix of statistics. The output should be the corresponding $n \times 1$ matrix of predicted wins predicted by the model.

### C. Understanding the Model: Calculating Prediction Error

In the analysis of different methods for assigning weights, it will be useful to have a function which explicitly computes the Mean-Squared error $MSE(w)$ between the values predicted by a model and the actual values.

**Note:** You have already written a function `mse` on Module 3 which computes the mean-squared error between two sequences. It might be helpful to copy this function into your file. You may also make use of this function in your recipe.

#### i. Recipe

Clearly describe the recipe which takes as input:
1. A linear model using the $m \times 1$ matrix of weights $w$.
2. $X$: $n \times m$ matrix of explanatory variables
3. $y$: $n \times 1$ matrix of the corresponding actual values for the measured variables

The recipe should produce $MSE(w)$, the Mean-Squared error between the values predicted by the model and the actual results.

You should describe this recipe in clear, concise English.

#### ii. Code

Write a method on the `LinearModel` class, `prediction_error(self, inputs, actual_result)`, which implements your recipe. The input `inputs` should be a $n \times m$ matrix of statistics, while the input `actual_result` should be the corresponding $n \times 1$ matrix of wins.

### D. Fitting the Model: Least Squares Estimate

#### i. Derivation

The optimal weights can be computed by minimizing the MSE as discussed in class. The derivation involves:
1. Express $MSE(w)$ in matrix form
2. Take the derivative with respect to $w$
3. Set the derivative equal to 0 and solve for $w$

The result is the Least Squares Estimate formula:

$w = (X^TX)^{-1}X^Ty$

**Note:** Be careful! $X$ is *not* a square matrix and so it does *not* have an inverse. The formula uses $(X^TX)^{-1}$ which is the inverse of the square matrix $X^TX$.

Useful matrix properties:
- $(AB)^T = B^TA^T$
- Matrix multiplication is not commutative: $AB \neq BA$ in general
- $(A^T)^T = A$

#### ii. Code

Write a function `fit_least_squares(input_data, output_data)` which computes the Least-Squares Estimate for the weights. The `input_data` parameter should be a $n \times m$ matrix and the `output_data` parameter should be a $n \times 1$ matrix.

This function should return a `LinearModel` object which has been fit using Least Squares to approximately match the data.

### E. Fitting the Model: LASSO Estimate

There are many different approaches for minimizing $MSE(w) + \lambda \left\lVert w \right\rVert_1$, but 'Shooting' is the simplest to implement. In order to more easily describe this algorithm, we'll first describe the mathematical function $SoftThreshold(x, t)$ as follows:

$SoftThreshold(x, t) = \begin{cases} x - t & \text{if } & x > t\\\ 0 & \text{if } & \lvert x \rvert \leq t\\ x + t & \text{if } & x < -t \end{cases}$

Intuitively, $SoftThreshold$ moves $x$ closer to 0 by the distance $t$. If this would move $x$ past 0, the value is simply 0.

Using this, we can describe the algorithm for LASSO Shooting. In this algorithm, we begin with an initial guess for the minimal weights $w$. We will use the $w$ which minimizes $MSE(w)$ as our initial guess; this is the value we calculated in part D.

We then 'shoot' this initial guess towards the minimum by iteratively making small changes to $w$ which each slightly reduce the quantity $MSE(w) + \lambda \left\lVert w \right\rVert_1$. We can do this computation for as many iterations as we desire to get arbitrarily close to the minimum; in practice, it is enough to stop the process once $w$ is barely changing over each iteration.

During each iteration, we cycle through each coordinate $w_j$ of $w$ and adjust only that coordinate to reduce $MSE(w) + \lambda \left\lVert w \right\rVert_1$. The derivation of this minimization is far outside the scope of this course; we only need the result. We can define two quantities:

$a_j = \displaystyle\frac{(X^Ty)_{j,0} - ((X^TX)_{j,.}w)_{0,0}}{(X^TX)_{j,j}}$ $b_j = \displaystyle\frac{\lambda}{2(X^TX)_{j,j}}$

Then the updated value for $w_j$ is $SoftThreshold(w_j + a_j, b_j)$. Note that $a_j$ and $b_j$ are not matrix elements. For each $w_j$, you need to calculate two values, $a_j$ and $b_j$, for use with $SoftThreshold$. Once you have updated $w_j$, you will no longer need $a_j$ and $b_j$. They have the subscript $j$ only to clearly indicate that you need to compute different values as you update each $w_j$.

Note that $(X^TX)_{j,.}$ refers to the $j$-th row of $X^TX$.

#### i. Recipe

The LASSO Shooting algorithm:
1. Initialize $w$ using the Least Squares Estimate
2. Compute $X^TX$ and $X^Ty$ once (these don't change)
3. For each iteration:
   a. For each coordinate $j$ from 0 to m-1:
      - Compute $a_j$ and $b_j$ as defined above
      - Update $w_j = SoftThreshold(w_j + a_j, b_j)$
4. Return the final $w$

#### ii. Code

Write a function `fit_lasso(param, iterations, input_data, output_data)` which implements this recipe. The input `param` represents the parameter $\lambda$ for the LASSO algorithm.

This function should return a `LinearModel` object which has been fit using the LASSO algorithm to approximately match the data.

**Note:** When working with such large data sets, it is easy for the code to take much longer than required by doing a lot of extra work. When implementing this function, be careful to avoid recomputing any values which do not change between iterations.

## 4. Predicting Baseball Performance

Now that we have several ways to develop weights, we should try and determine which one results in a more accurate statistical model. Just as on Module 3, we will split up our data set into training data and test data. We can then fit the model to the training data and see how well it predicts the test data. For training data, we will use historical data between 1954 and 2000. For test data, we will use data between 2001 and 2012.

The data files are available in the `data/` directory:
- Statistics between 1954 and 2000: `comp140_analytics_baseball.txt`
- Wins between 1954 and 2000: `comp140_analytics_wins.txt`
- Statistics between 2001 and 2012: `comp140_analytics_baseball_test.txt`
- Wins between 2001 and 2012: `comp140_analytics_wins_test.txt`

Write a function `run_experiment(iterations)` which will:
1. Create and fit a model to the 1954-2000 data in each of the following ways:
   1. The Least-Squares Estimation
   2. LASSO estimation with 3 different $\lambda$ values between 1000 and 100,000. You should compute these estimations for the number of iterations specified in the input.

2. Print out each model's prediction error on the 1954-2000 data.
3. Print out each model's prediction error on the 2001-2012 data.

Include the results of this code in the written portion.

**Note:** We're working with a lot of data, so this function may take a long time to run. While developing this function, you should make sure to use a low number of iterations (<50). Once the function is complete and correct, you can run it *once* with a high number of iterations (~500-1000) to get your results.

## 5. Discussion (Written Response)

1. In the computation of the Least-Squares estimate, we used the fact that $y^TXw = w^TX^Ty$. However, this is not true for any arbitrary matrices $w$, $X$, and $y$. Why is this expression true for this computation?
2. The LASSO algorithm finds the weights which minimize $MSE(w)+\lambda \left\lVert w \right\rVert_1$. How does increasing $\lambda$ change the value to be minimized? How does the weight vector output by the LASSO algorithm change as $\lambda$ increases?
3. Which method of fitting the weights produced the lowest MSE on the training data? Did these weights also best predict the number of wins on the test 2001-2012 data? What conclusions can you draw from this?
4. It is often useful to determine which statistics are more or less important in predicting the dependent variable. For example, this might help a Baseball Manager decide which players to draft or what to focus on at practice. Can you use the fitted weights generated by the LASSO algorithm to help figure out which statistics are not important?

Answer these questions in clear, precise English. Do not write more than a few sentences for each question.

Write your response in `writeup.md`.

---
*COMP 140: Computational Thinking, Rice University*
