---
total_points: 30
---

# COMP 322 Homework 1 - Written Questions Rubric

## Part 1: Written Questions (15 points)

### 1.1 Recursive Fibonacci Analysis

#### Question 1.1.1: Recursive Fibonacci Complexity (5 points)

**Question:** "What is the formula (exact answer) for the total work performed by a call to fib(n)? Assume that a single call to fib() (without any recursive calls inside) has a total WORK of 1. Include an explanation of the analysis, and state what expression you get for WORK(n) as a function of n. The exact answer should not be recursive."

**Expected Answer:**

The recurrence relation is:
- WORK(0) = 1
- WORK(1) = 1
- WORK(n) = WORK(n-1) + WORK(n-2) + 1

This is the Fibonacci recurrence plus 1 for each call. The solution is:

WORK(n) = 2 * F(n+1) - 1

Where F(n) is the nth Fibonacci number. Alternatively:
WORK(n) = 2 * fib(n+1) - 1

Using the closed form (Binet's formula):
WORK(n) = 2 * ((phi^(n+1) - psi^(n+1)) / sqrt(5)) - 1

Where phi = (1 + sqrt(5))/2 (golden ratio) and psi = (1 - sqrt(5))/2.

Asymptotically: WORK(n) = Theta(phi^n) = Theta(1.618^n)

**Rubric:**
- 5 points: Correct exact formula (2*F(n+1) - 1 or equivalent) with clear derivation
- 4 points: Correct formula with minor errors in derivation or missing justification
- 3 points: Correct asymptotic bound (Theta(phi^n) or Theta(1.618^n)) without exact formula
- 2 points: Correct recurrence identified but not solved
- 1 point: Shows understanding that work grows exponentially
- 0 points: Incorrect or missing answer

---

#### Question 1.1.2a: Memoized Fibonacci - First Call (5 points)

**Question:** "What is the big-O for WORK performed by fib(n) when called for the very first time?"

**Expected Answer:**

WORK = O(n)

**Explanation:**
- On the first call to fib(n), the memoization array is empty (all Lazy values are unevaluated)
- The call triggers evaluation of memoized[n-1] and memoized[n-2]
- This cascades down, but each memoized[k] is evaluated at most once
- Total: n-1 lazy evaluations, each doing constant work
- Therefore: O(n) total work

**Rubric:**
- 5 points: Correct answer (O(n)) with clear explanation of lazy evaluation
- 4 points: Correct answer with incomplete explanation
- 3 points: Correct answer without explanation
- 2 points: Incorrect answer but shows understanding of memoization concept
- 1 point: Mentions memoization reduces work but wrong complexity
- 0 points: Incorrect or missing answer

---

#### Question 1.1.2b: Memoized Fibonacci - Subsequent Calls (5 points)

**Question:** "After many random calls to fib(k), what is the big-O for expected WORK of a subsequent fib(n) call?"

**Expected Answer:**

WORK = O(1)

**Explanation:**
- After many random calls, most or all values in the memoization table are already computed
- A call to fib(n) simply retrieves memoized[n-1].get() and memoized[n-2].get()
- Both are already evaluated, so .get() returns immediately (O(1))
- The addition is O(1)
- Total: O(1) expected work

Note: In the worst case (if memoized[n-1] or [n-2] hasn't been computed), it could be O(n), but expected value after many random calls is O(1).

**Rubric:**
- 5 points: Correct answer (O(1)) with clear explanation of cached lookup
- 4 points: Correct answer with incomplete explanation
- 3 points: Correct answer without explanation
- 2 points: O(n) with explanation that some values may not be cached (partial understanding)
- 1 point: Mentions caching but wrong complexity
- 0 points: Incorrect or missing answer

---

## Part 2: Performance Report (15 points)

### Report: Sequential vs Parallel Streams Comparison

**Requirements:**
1. Machine specifications (CPU model, cores, memory)
2. Performance measurements for sequential and parallel stream operations
3. Speedup calculations
4. Analysis and discussion of results

**Rubric:**

#### Section A: Machine Description (3 points)
- 3 points: Complete specs including CPU model, number of cores, and memory
- 2 points: CPU and cores listed but missing memory
- 1 point: Minimal specs (just "laptop" or similar)
- 0 points: No machine description

#### Section B: Performance Measurements (6 points)
- 6 points: Clear timing data for all stream operations, both sequential and parallel
- 4-5 points: Most operations measured, some missing
- 2-3 points: Limited measurements (only a few operations)
- 1 point: Attempted measurements but incomplete or clearly incorrect
- 0 points: No measurements

#### Section C: Speedup Analysis (3 points)
- 3 points: Correct speedup calculations (T_seq / T_par) with interpretation
- 2 points: Speedup calculated but minimal analysis
- 1 point: No speedup calculation but some comparison attempted
- 0 points: No speedup analysis

#### Section D: Discussion (3 points)
- 3 points: Insightful analysis explaining observed behavior (e.g., overhead for small data, scalability patterns)
- 2 points: Basic discussion of results without deep analysis
- 1 point: Minimal discussion
- 0 points: No discussion
