# COMP 182: Algorithmic Thinking
## Homework 5

This homework is divided into three parts. When you submit, you will need a file for each part.

---

## Part 1

### Problem 1 [10 points]

Solve the problem using a **divide and conquer** strategy:

**Input:** A set S of n real numbers and a single real number x

**Output:** Returns true if S contains two numbers that sum to x, and false otherwise

Design an O(n log n) algorithm for the problem. Give pseudo-code for the algorithm and justify why the algorithm has the specified complexity.

---

### Problem 2 [10 points]

Use induction to prove that, for every positive integer n:

$$\sum_{i=1}^{n} i(i + 1)(i + 2) = \frac{n(n+1)(n+2)(n+3)}{4}$$

---

### Problem 3 [10 points]

Prove that for all n >= 0:

$$\sum_{i=0}^{n} i^3 = \left(\sum_{i=0}^{n} i\right)^2$$

---

## Part 2

### Problem 4 [10 points]

Prove that n lines separate the plane into (n^2 + n + 2)/2 regions if no two of these lines are parallel and no three pass through a common point.

---

### Problem 5 [10 points]

Prove that a postage of n cents, for n >= 18, can be formed using just 4-cent stamps and 7-cent stamps.

---

### Problem 6 [10 points]

Let x be a real number such that x + 1/x is an integer. Prove that x^n + 1/x^n is an integer for all integers n >= 0.

---

## Part 3

### Problem 7 [10 points]

Consider the sequence a_1, a_2, a_3, ... where a_1 = 1, a_2 = 2, a_3 = 3, and a_n = a_{n-1} + a_{n-2} + a_{n-3} for all n >= 4.

Prove that a_n < 2^n for all positive integers n.

---

### Problem 8 [10 points]

Give a recursive definition of the set of all binary strings (strings over the alphabet {0, 1}) that have more 0's than 1's.

---

### Problem 9 [20 points]

When using parentheses it is important that they are balanced. For example, (()) and ()()() are balanced strings of parentheses, whereas (())) and ()()( are not.

**(a)** Give a recursive definition of the set of all balanced strings of parentheses.

**(b)** Use induction to prove that if x is a balanced string of parentheses, then the number of left parentheses equals the number of right parentheses in x.
