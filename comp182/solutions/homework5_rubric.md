# COMP 182: Algorithmic Thinking
## Homework 5 - Solution Key and Rubric

---

## Part 1

### Problem 1 [10 points] - Divide and Conquer Two-Sum Algorithm

**Problem:** Design an O(n log n) algorithm that returns true if set S contains two numbers that sum to x.

**Solution:**

Here are two possible algorithms (either one is acceptable):

#### Algorithm 1: Sort and Binary Search
Idea: Sort and do a binary search on x - y for every element y in S.

```
(a) MergeSort(S);
(b) For each y in S:
    i. If BinarySearch(S - {y}, x - y) != -1:
       Return True;
(c) Return False;
```

**Complexity:** Sorting takes O(n log n) and then each of n binary searches takes O(log n) for a total of O(n log n).

#### Algorithm 2: Sort and Two-Pointer Scan
Idea: Sort and scan the list forward and backward at the same time to see if two elements add up to x.

```
(a) MergeSort(S);
(b) i <- 0, j <- n - 1;
(c) While i <= j:
    i. If S[i] + S[j] = x:
       Return True;
    ii. ElseIf S[i] + S[j] < x:
        i <- i + 1;
    iii. Else:
         j <- j - 1;
(d) Return False;
```

**Complexity:** Sorting takes O(n log n) and the linear forward/backward scan takes O(n) for a total of O(n log n).

**Rubric:**
- Correct O(n log n) algorithm: 10 points
- Almost correct (only minor errors) O(n log n) algorithm: 5 points
- Correctly shows the algorithm is O(n log n): 4 points
- Correct algorithm but not O(n log n): 2 points

---

### Problem 2 [10 points] - Sum of Products Induction

**Problem:** Prove that for every positive integer n, sum_{i=1}^{n} i(i+1)(i+2) = n(n+1)(n+2)(n+3)/4.

**Solution:**

We prove the result using mathematical induction on n.

**Base case:** n = 1
- Left-hand side: 1 * 2 * 3 = 6
- Right-hand side: (1 * 2 * 3 * 4)/4 = 24/4 = 6
- Therefore, the result holds for n = 1.

**Inductive step:** Assume the result holds for an arbitrary positive integer k; that is:
sum_{i=1}^{k} i(i+1)(i+2) = k(k+1)(k+2)(k+3)/4  (Inductive Hypothesis)

We now show that the result is true for k+1. We want to show:
sum_{i=1}^{k+1} i(i+1)(i+2) = (k+1)(k+2)(k+3)(k+4)/4

Proof:
sum_{i=1}^{k+1} i(i+1)(i+2) = sum_{i=1}^{k} i(i+1)(i+2) + (k+1)(k+2)(k+3)
                            = k(k+1)(k+2)(k+3)/4 + (k+1)(k+2)(k+3)   [by I.H.]
                            = (k+1)(k+2)(k+3) * [k/4 + 1]
                            = (k+1)(k+2)(k+3) * [(k+4)/4]
                            = (k+1)(k+2)(k+3)(k+4)/4

Therefore, the result holds.

**Rubric:**
- Correct base case: 3 points
- Correct inductive step: 7 points
- Partial credit at grader's discretion

---

### Problem 3 [10 points] - Sum of Cubes Identity

**Problem:** Prove that for all n >= 0, sum_{i=0}^{n} i^3 = (sum_{i=0}^{n} i)^2.

**Solution:**

Define P(n) to be the proposition "sum_{i=0}^{n} i^3 = (sum_{i=0}^{n} i)^2" and prove that for all n >= 0, P(n).

**Base Case:** For n = 0, we have 0^3 = 0 = 0^2. Therefore, P(0) is true.

**Inductive step:** Assume P(n) (this is the Inductive Hypothesis) and prove P(n+1).

1^3 + 2^3 + 3^3 + ... + n^3 + (n+1)^3
= (1 + 2 + 3 + ... + n)^2 + (n+1)^3           [by I.H.]
= (n(n+1)/2)^2 + (n+1)^3                       [sum formula from class]
= n^2(n+1)^2/4 + (n+1)^3
= [n^2(n+1)^2 + 4(n+1)(n+1)^2] / 4
= [n^2(n+1)^2 + (4n+4)(n+1)^2] / 4
= [(n^2 + 4n + 4)(n+1)^2] / 4
= [(n+2)^2(n+1)^2] / 4
= [(n+1)(n+2)/2]^2
= (1 + 2 + ... + n + (n+1))^2                  [sum formula from class]

By mathematical induction, for all n >= 1, P(n).

**Rubric:**
- Correct base case: 3 points
- Correct inductive step: 7 points
- Partial credit at grader's discretion

---

## Part 2

### Problem 4 [10 points] - Lines and Regions in a Plane

**Problem:** Prove that n lines separate the plane into (n^2 + n + 2)/2 regions if no two lines are parallel and no three pass through a common point.

**Solution:**

We prove the result using strong mathematical induction on n.

**Base case:** n = 1
A single line separates the plane into two regions, which is (1^2 + 1 + 2)/2 = 4/2 = 2 regions.
So, the result is true for n = 1.

**Inductive step:** Assume the result is true for every positive integer between 1 and k; that is, l (1 <= l <= k) lines separate the plane into (l^2 + l + 2)/2 regions if no two of these lines are parallel and no three pass through a common point (this is the I.H.).

We now show that the result is true for k+1. That is, we want to show that k+1 lines separate the plane into ((k+1)^2 + (k+1) + 2)/2 regions if no two of these lines are parallel and no three pass through a common point.

Let S be an arbitrary set of k+1 lines drawn in the plane so that no two of these lines are parallel and no three pass through a common point.

Choose an arbitrary line L in the set S and remove it. The resulting set S' = S - {L} contains k lines such that no two of these lines in S' are parallel and no three lines in S' pass through a common point. By the I.H., the lines in S' separate the plane into (k^2 + k + 2)/2 regions.

Now consider adding the line L back to the set S'. Imagine drawing the line from -infinity to +infinity. As you draw it, it will intersect a line, say L'. Before line L intersected line L', it was in a region that is bounded by line L' (and some other lines). The line L will divide this region into two regions, thus increasing the number of regions by 1.

Since no two lines are parallel and no three lines pass through a common point, line L will intersect each of the existing k lines exactly once, thus adding a total of k to the number of regions created by the lines in set S'.

Furthermore, as line L intersects the very last line, it divides the region "on the other side" of that last line into two, thus adding 1 to the count of regions. In other words, adding line L increases the number of regions by k+1.

Since the number of regions created by the lines in S' is (k^2 + k + 2)/2 (by the I.H.), we now have:
(k^2 + k + 2)/2 + (k + 1) = (k^2 + k + 2 + 2k + 2)/2 = (k^2 + 3k + 4)/2

It is easy to show that:
(k^2 + k + 2)/2 + (k + 1) = ((k+1)^2 + (k+1) + 2)/2

Verification:
((k+1)^2 + (k+1) + 2)/2 = (k^2 + 2k + 1 + k + 1 + 2)/2 = (k^2 + 3k + 4)/2  [checkmark]

Therefore, the result holds.

**Rubric:**
- Correct base case: 3 points
- Correct inductive step: 7 points
- Partial credit at grader's discretion
- The figure/illustration is not required

---

### Problem 5 [10 points] - Postage Stamps

**Problem:** Prove that a postage of n cents, for n >= 18, can be formed using just 4-cent stamps and 7-cent stamps.

**Solution:**

Proof by strong induction on n.

**Base case:**
- n = 18: A postage of 18 cents can be formed by two 7-cent stamps and one 4-cent stamp (7 + 7 + 4 = 18). Therefore, the result holds for n = 18.
- n = 19: A postage of 19 cents can be formed by one 7-cent stamp and three 4-cent stamps (7 + 4 + 4 + 4 = 19). Therefore, the result holds for n = 19.
- n = 20: A postage of 20 cents can be formed by five 4-cent stamps (4 + 4 + 4 + 4 + 4 = 20). Therefore, the result holds for n = 20.
- n = 21: A postage of 21 cents can be formed by three 7-cent stamps (7 + 7 + 7 = 21). Therefore, the result holds for n = 21.

**Inductive step:** Assume the result is true for every positive integer between 18 and some arbitrary k (k >= 21).

We want to show that the result holds for a postage of k+1 cents.

Since k+1 = (k-3) + 4 and since k >= 21, we know that (k-3) >= 18.

By the I.H., k-3 postage can be formed by m 7-cent stamps and l 4-cent stamps, for some m, l >= 0.

Therefore, a postage of k+1 can be formed by m 7-cent stamps and (l+1) 4-cent stamps.

Therefore, the result holds.

**Rubric:**
- Correct base case (all four values n = 18, 19, 20, 21): 3 points
- Correct inductive step: 7 points
- Partial credit at grader's discretion
- Note: Based on the given proof, the base case requires all four n values

---

### Problem 6 [10 points] - Integer Powers

**Problem:** Let x be a real number such that x + 1/x is an integer. Prove that x^n + 1/x^n is an integer for all integers n >= 0.

**Solution:**

Proof by strong mathematical induction.

**Base case:** n = 0
x^0 + 1/x^0 = 1 + 1 = 2, which is an integer.

**Inductive step:** Assume the result is true for every integer between 0 and n, and let's prove it for n+1, that is, x^{n+1} + 1/x^{n+1} is an integer.

Observe that:
x^{n+1} + 1/x^{n+1} = (x + 1/x) * (x^n + 1/x^n) - (x^{n-1} + 1/x^{n-1})

We know that:
- x + 1/x is an integer (by the definition of the problem)
- x^n + 1/x^n is an integer (by the I.H.)
- x^{n-1} + 1/x^{n-1} is an integer (by the I.H.)

Since integers are closed under multiplication and subtraction, we have that x^{n+1} + 1/x^{n+1} is an integer.

**Note:** The key algebraic identity can be verified:
(x + 1/x)(x^n + 1/x^n) = x^{n+1} + x^{n-1} + 1/x^{n-1} + 1/x^{n+1}
                       = (x^{n+1} + 1/x^{n+1}) + (x^{n-1} + 1/x^{n-1})

Therefore:
x^{n+1} + 1/x^{n+1} = (x + 1/x)(x^n + 1/x^n) - (x^{n-1} + 1/x^{n-1})

**Rubric:**
- Correct base case: 3 points
- Correct inductive step: 7 points
- Partial credit at grader's discretion

---

## Part 3

### Problem 7 [10 points] - Tribonacci Sequence Bound

**Problem:** Consider the sequence a_1, a_2, a_3, ... where a_1 = 1, a_2 = 2, a_3 = 3, and a_n = a_{n-1} + a_{n-2} + a_{n-3} for all n >= 4. Prove that a_n < 2^n for all positive integers n.

**Solution:**

Proof by strong mathematical induction.

**Base case:** We have to show the result for n = 1, n = 2, and n = 3, as the inductive step applies only to n >= 4.
- a_1 = 1 and 2^1 = 2, so a_1 < 2^1. [checkmark]
- a_2 = 2 and 2^2 = 4, so a_2 < 2^2. [checkmark]
- a_3 = 3 and 2^3 = 8, so a_3 < 2^3. [checkmark]

**Inductive step:** Assume the result is true for every integer between 1 and n, and let's prove it for n+1, that is, a_{n+1} < 2^{n+1}.

a_{n+1} = a_n + a_{n-1} + a_{n-2}              [by definition]
        < 2^n + 2^{n-1} + 2^{n-2}              [by I.H.]
        < 2^n + 2^{n-1} + 2^{n-1}              [since 2^{n-2} < 2^{n-1}]
        = 2^n + 2 * 2^{n-1}
        = 2^n + 2^n
        = 2 * 2^n
        = 2^{n+1}

Therefore, a_{n+1} < 2^{n+1}.

**Rubric:**
- Correct base case (all three values): 3 points
- Correct inductive step: 7 points
- Partial credit at grader's discretion

---

### Problem 8 [10 points] - Recursive Definition of Binary Strings

**Problem:** Give a recursive definition of the set of all binary strings (strings over the alphabet {0, 1}) that have more 0's than 1's.

**Solution:**

Let S denote the set of all binary strings that have more 0's than 1's. We define S recursively as follows:

**Base case:** 0 in S.
This case says that the smallest possible string with more 0's than 1's is the string "0". Notice that the base case cannot be the empty string epsilon, as this string has an equal number of 0's and 1's.

**Recursive step:** If x, y in S, then so are xy, 1xy, x1y, and xy1.

The recursive step says that if x and y are two strings and each has more 0's than 1's, then their concatenation xy also has more 0's than 1's.

Furthermore, since their concatenation has at least two more 0's than 1's (since x has at least one more 0's than 1's and y has at least one more 0's than 1's), adding a 1 to xy, either before x, between x and y, or after y, still results in a string that has more 0's than 1's.

**Rubric:**
- Correct base case: 2 points
- Correct recursive step: 8 points
- Partial credit at grader's discretion

---

### Problem 9 [20 points] - Balanced Parentheses

**Problem:**
(a) Give a recursive definition of the set of all balanced strings of parentheses.
(b) Use induction to prove that if x is a balanced string of parentheses, then the number of left parentheses equals the number of right parentheses in x.

**Solution:**

#### Part (a) - Recursive Definition [10 points]

Let B denote the set of balanced parentheses. Then we have the following recursive definition of B:

**Base case:** epsilon in B (the empty string).
Since the empty string has 0 left and 0 right parentheses, it is clearly balanced.

**Recursive step:** Let x, y in B, then we have:
- (x) in B
- xy in B (where xy means concatenation)

**Rubric for Part (a):**
- Correct base case: 3 points
- Correct recursive step: 7 points
- Partial credit at grader's discretion

#### Part (b) - Inductive Proof [10 points]

**Proof:** Let us denote by L(x) and R(x) the numbers of left and right parentheses in x, respectively.

If x not in B, then (x in B) -> (L(x) = R(x)) is true (vacuously).

Now, assume x in B. We use structural induction to prove the result.

**Base case:** x = epsilon (empty string)
In this case, L(x) = R(x) = 0. [checkmark]

**Inductive step:** Assume that for every string w, where |w| < |x| and x != epsilon, we have:
(w in B) -> (L(w) = R(w))

This assumption is the inductive hypothesis (I.H.).

Since x in B, then either x = (z) for some z in B, or x = uv for some u, v in B where u != epsilon and v != epsilon.

Let us consider each case separately:

**Case I:** x = (z) for some z in B.
Since z in B and |z| < |x|, then by the I.H., we have L(z) = R(z).
We also know that L(x) = 1 + L(z) and R(x) = 1 + R(z).
Therefore, L(x) = R(x). [checkmark]

**Case II:** x = uv for some u, v in B.
Since u, v in B and |u| < |x| and |v| < |x|, it follows from the I.H. that L(u) = R(u) and L(v) = R(v).
Since L(x) = L(u) + L(v) and R(x) = R(u) + R(v), it follows that L(x) = R(x). [checkmark]

QED

**Rubric for Part (b):**
- Correct base case: 3 points
- Correct inductive step: 7 points
- Partial credit at grader's discretion

---

## Total Points Summary

| Problem | Points | Topic |
|---------|--------|-------|
| 1 | 10 | Divide and Conquer Algorithm |
| 2 | 10 | Induction (Sum of Products) |
| 3 | 10 | Induction (Sum of Cubes) |
| 4 | 10 | Induction (Lines and Regions) |
| 5 | 10 | Strong Induction (Postage Stamps) |
| 6 | 10 | Strong Induction (Integer Powers) |
| 7 | 10 | Strong Induction (Tribonacci Bound) |
| 8 | 10 | Recursive Definition (Binary Strings) |
| 9a | 10 | Recursive Definition (Balanced Parentheses) |
| 9b | 10 | Structural Induction (Balanced Parentheses) |
| **Total** | **100** | |
