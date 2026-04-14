# COMP 322 Homework 2: GitHub Contributions (Parallel)

## Part 1: Written Questions (30 points)

### 1.1 Parallelizing the IsKColorable Algorithm

Consider the pseudo-code of the IsKColorable algorithm:

```
Input: Graph g = (V, E) and k in N.
Output: Does there exist f : V -> [k] such that for all {u, v} in E, f(u) != f(v)?

1  foreach Assignment f of a value in [k] to each node in V do
2      colorable <- True;
3      foreach {u, v} in E do
4          if f(u) = f(v) then
5              colorable <- False;
6              break;
7      if colorable = True then
8          return True;
9  return False;
```

---

#### 1.1.1 Parallel Version (10 points)

> Create a parallel version using finish and async statements to maximize parallelism while ensuring correctness.

**Your Answer:**

```java
// Write your parallel pseudocode here
```

[Explain your parallelization strategy and synchronization approach]

---

#### 1.1.2 Total Work Analysis (5 points)

> What is the big-O for total WORK?

**Your Answer:**

[Provide the big-O formula in terms of k, |V|, and |E|, with explanation]

---

#### 1.1.3 Larger Work Than Sequential? (5 points)

> Can your parallel algorithm have LARGER WORK than sequential? Explain.

**Your Answer:**

[Yes/No with detailed explanation]

---

#### 1.1.4 Smaller Work Than Sequential? (5 points)

> Can your parallel algorithm have SMALLER WORK than sequential? Explain.

**Your Answer:**

[Yes/No with detailed explanation. Distinguish between Work and Span.]

---

#### 1.1.5 Data Race Analysis (5 points)

> Is a data race possible? If so, is it benign?

**Your Answer:**

[Identify shared variables, analyze concurrent access patterns, and determine if any race is benign]

---

## Part 2: Report (15 points)

### A. Algorithm Summary

[Describe your parallel implementation of loadContributorsPar:
- What parallel constructs did you use (futures, streams)?
- How are API calls parallelized?
- How is data aggregation performed?]

---

### B. Correctness and Data-Race-Free Justification

[Explain why your implementation:
1. Produces the same results as the sequential version
2. Is free from data races
3. Correctly handles aggregation (e.g., summing contributions)]

---

### C. Work Analysis

Let N = number of repositories and M = total number of contributors across all repos.

[Provide the Work formula as a function of N and M, and explain:
- Work for API calls
- Work for data aggregation
- Total expected Work]

---
