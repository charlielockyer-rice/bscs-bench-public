# COMP 382: Reasoning about Algorithms
## Homework 3

**Total Points: 100**

---

## Problem 1: String Merging [15 points]

Let X, Y, and Z be three strings, where |X| = m, |Y| = n, and |Z| = m + n. We say that Z *merges* X and Y iff Z can be formed by interleaving the characters from X and Y in a way that maintains the left-to-right ordering of the characters from each string. For example, both `abucvwx` and `uavwbxc` merge the strings `abc` and `uvwx`.

### Part 1 [5 points]
Give an efficient dynamic programming algorithm that determines whether Z merges X and Y. What is the time complexity of your algorithm?

### Part 2 [10 points]
We will now generalize the problem so that the input includes a number K (in addition to the strings X, Y, Z). K is the maximum number of errors (substitutions) that we can allow during the merging. That is, the computational problem is to determine if there exists a string Z' (of length m + n) such that:
(i) Z' merges X and Y, and
(ii) Z, Z' differ at a maximum of K positions (i.e., the Hamming distance between Z and Z' is at most K).

Give an efficient dynamic programming algorithm for this problem. What is the time complexity of your algorithm?

---

## Problem 2: Inventory Planning [20 Points]

The Rinky Dink Company makes machines that resurface ice rinks. The demand for such products varies from month to month, and so the company needs to develop a strategy to plan its manufacturing given the fluctuating, but predictable, demand.

The company wishes to design a plan for the next n months. For each month i, the company knows the demand dᵢ, that is, the number of machines that it will sell. Let D = Σᵢ₌₁ⁿ dᵢ be the total demand over the next n months.

The company keeps a full-time staff who provide labor to manufacture up to m machines per month. If the company needs to make more than m machines in a given month, it can hire additional, part-time labor, at a cost that works out to c dollars per machine.

Furthermore, if, at the end of a month, the company is holding any unsold machines, it must pay inventory costs. The cost for holding j machines is given as a function h(j) for j = 1, 2, ..., D, where h(j) ≥ 0 for 1 ≤ j ≤ D and h(j) ≤ h(j + 1) for 1 ≤ j ≤ D − 1.

Give an algorithm that calculates a plan for the company that minimizes its costs while fulfilling all the demand. The running time should be polynomial in n and D.

---

## Problem 3: Optimal Matching of Sequences [30 Points]

For an integer n ≥ 1, we define [n] = {1, ..., n}. Suppose we are given as input sequences (i.e., arrays) of integers X = [x₁, ..., xₘ] and Y = [y₁, ..., yₙ]. A *matching* for (X, Y) is a subset M ⊆ [m] × [n] (containing pairs of indexes) that satisfies the following properties:

(i) **(Left covered)** For every index i ∈ [m] there is some j ∈ [n] such that (i, j) ∈ M.

(ii) **(Right covered)** For every index j ∈ [n] there is some i ∈ [m] such that (i, j) ∈ M.

(iii) **(No crossing)** There are no indexes i, i' ∈ [m] and j, j' ∈ [n] with i < i' and j < j' such that (i, j') ∈ M and (i', j) ∈ M.

The *cost* of a matching M for (X, Y) is defined as follows:

$$\text{cost}(M) = \sum_{(i,j) \in M} (x_i - y_j)^2$$

We are interested in finding an *optimal matching*, that is, a matching with minimal cost.

### Part 1 [3 points]
Prove that every matching contains the pairs (1, 1) and (m, n).

### Part 2 [7 points]
Let f(m, n) be the number of all possible matchings for (X, Y), where X = [x₁, ..., xₘ] and Y = [y₁, ..., yₙ]. Give a recursive definition for the function f and carefully explain how you obtained it.

### Part 3 [20 points]
Design an algorithm that computes a matching with minimal cost. Explain why it is correct and discuss its time and space complexity.

---

## Problem 4: Edit Distance & LCS [35 Points]

We will consider a generalization of the concept of edit distance, where the cost of an insertion, deletion and substitution is cᵢ, c_d and c_s respectively. Each parameter can be chosen to be any positive extended real number, i.e., an element of {x ∈ ℝ | x > 0} ∪ {∞}.

Keep in mind the following properties of ∞:
- ∞ + ∞ = ∞
- x + ∞ = ∞
- x < ∞

for every x ∈ ℝ.

### Part 1 [3 points]
Provide pseudocode for the procedure `EDITDISTANCE(X, Y, cᵢ, c_d, c_s)`, where X[1..m] and Y[1..n] are strings and cᵢ, c_d, c_s are the edit operation costs. This procedure should return the edit distance from X to Y, which we denote by D(X, Y). Recall that:

D(X, Y) = min{cost(W) | edit sequence W for (X, Y)}

as we defined in the lectures.

### Part 2 [12 points]
Prove that D(xa, ya) = D(x, y) for all strings x, y and every letter a.

**Note:** This is not a trivial claim. You should justify it with a careful proof using the definition of edit distance.

### Part 3 [20 points]
Let L(x, y) be the length of the longest common subsequence of strings x, y. We know from CLRS (page 396) that L is given by the following recursive definition:

- L(x, ε) = 0
- L(ε, y) = 0
- L(xa, ya) = L(x, y) + 1
- L(xa, yb) = max(L(xa, y), L(x, yb)) when a ≠ b

for all strings x, y and letters a, b.

For strings X and Y, show how L(X, Y) can be computed in one line of code using only **one invocation** of the procedure `EDITDISTANCE(X, Y, cᵢ, c_d, c_s)`. Justify the correctness of your approach with a careful proof.
