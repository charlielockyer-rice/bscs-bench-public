# COMP 382 Homework 3 - Solutions

---

## Problem 1: String Merging [15 points]

### Part 1 [5 points]

**Subproblem Definition:** Let M[i,j] be a Boolean indicating whether Z[1..i+j] merges X[1..i] and Y[1..j].

**Recurrence:**
```
M[0,0] = true

M[i,0] = (Z[i] = X[i]) ∧ M[i-1, 0]

M[0,j] = (Z[j] = Y[j]) ∧ M[0, j-1]

M[i,j] = ((Z[i+j] = X[i]) ∧ M[i-1, j]) ∨ ((Z[i+j] = Y[j]) ∧ M[i, j-1])
```

**Output:** M[m,n]

**Time Complexity:** O(m·n) - We fill an (m+1) × (n+1) table, with O(1) work per cell.

### Part 2 [10 points]

**Subproblem Definition:** Let T[i,j] = minimum Hamming distance between Z[1..i+j] and any string Z' that merges X[1..i] and Y[1..j].

**Helper function:** Define ν(a,b) = 1 if a ≠ b, and ν(a,a) = 0.

**Recurrence:**
```
T[0,0] = 0

T[i,0] = T[i-1, 0] + ν(Z[i], X[i])

T[0,j] = T[0, j-1] + ν(Z[j], Y[j])

T[i,j] = min{
    T[i-1, j] + ν(Z[i+j], X[i]),
    T[i, j-1] + ν(Z[i+j], Y[j])
}
```

**Output:** Return TRUE if and only if T[m,n] ≤ K.

**Time Complexity:** O(m·n)

---

## Problem 2: Inventory Planning [20 Points]

**Subproblem Definition:** Let OPT(i, K) denote the minimal cost for months i through n, given K machines in stock at the start of month i.

**Base Case (final month i = n):**
```
OPT(n, K) =
    h(K - d_n),           if d_n ≤ K           (surplus, no production needed)
    0,                    if K < d_n ≤ K + m   (full-time staff sufficient)
    c · (d_n - K - m),    if K + m < d_n       (part-time labor needed)
```

**Recursive Case (i < n):**
```
OPT(i, K) = min over k ∈ {0,1,...,D} such that 0 ≤ K + k - d_i ≤ D of:
    c · max(0, k - m) + h(K + k - d_i) + OPT(i+1, K + k - d_i)
```

Where:
- k = number of machines produced in month i
- max(0, k - m) = machines requiring part-time labor
- K + k - d_i = unsold machines at end of month

**Algorithm:** Use dynamic programming, solving subproblems in order of increasing n - i. Store choices in an additional table s[1..n, 0..D].

**Time Complexity:** O(n · D²)
- Number of subproblems: O(n · D)
- Choices per subproblem: O(D)

---

## Problem 3: Optimal Matching of Sequences [30 Points]

### Part 1 [3 points]

**Claim:** Every matching contains (1,1) and (m,n).

**Proof:** By contradiction.
- If (1,1) ∉ M, then index 1 in X must be matched to some j > 1, and index 1 in Y must be matched to some i > 1. But then (i,1) and (1,j) would cross (since 1 < i and 1 < j), violating the no-crossing property.
- Similarly for (m,n): If not in M, then m must match some j < n, and n must match some i < m. Then (i,n) and (m,j) would cross.

### Part 2 [7 points]

Let M(X,Y) denote the set of all matchings for (X,Y).

**Base Cases:**
- f(1, n) = 1 (only matching: {(1,1), (1,2), ..., (1,n)})
- f(m, 1) = 1 (only matching: {(1,1), (2,1), ..., (m,1)})

**Recursive Case (m,n > 1):**
The key observation is that every matching must include (m,n). Removing (m,n) from a matching M for (X,Y) gives us matchings from one of three disjoint sets:
- M(X', Y) where X' = [x₁,...,x_{m-1}]
- M(X, Y') where Y' = [y₁,...,y_{n-1}]
- M(X', Y')

Therefore:
```
f(m, n) = f(m-1, n) + f(m, n-1) + f(m-1, n-1)   for m,n > 1
```

### Part 3 [20 points]

**Subproblem:** Let opt(X[1..i], Y[1..j]) = minimum cost matching for prefixes X[1..i] and Y[1..j].

**Base Cases:**
```
opt([x₁], [y₁,...,y_n]) = Σⱼ₌₁ⁿ (x₁ - yⱼ)²

opt([x₁,...,x_m], [y₁]) = Σᵢ₌₁ᵐ (xᵢ - y₁)²
```

**Recursive Case:**
```
opt([x₁,...,x_m], [y₁,...,y_n]) =
    min{
        opt([x₁,...,x_{m-1}], [y₁,...,y_n]),
        opt([x₁,...,x_m], [y₁,...,y_{n-1}]),
        opt([x₁,...,x_{m-1}], [y₁,...,y_{n-1}])
    } + (x_m - y_n)²
```

**Algorithm:** Fill table c[1..m, 1..n] with minimal costs, solving subproblems in order of increasing i + j.

**Time Complexity:** O(m·n) - m·n subproblems, O(1) choices each

**Space Complexity:** O(m·n) for the table

---

## Problem 4: Edit Distance & LCS [35 Points]

### Part 1 [3 points]

```
EDITDISTANCE(X, Y, c_i, c_d, c_s):
    m ← |X|
    n ← |Y|
    Let c[0..m, 0..n] be a new table

    c[0,0] ← 0

    for i = 1 to m:
        c[i,0] ← c[i-1,0] + c_d      // c[i,0] = i · c_d

    for j = 1 to n:
        c[0,j] ← c[0,j-1] + c_i      // c[0,j] = j · c_i

    for i = 1 to m:
        for j = 1 to n:
            if X[i] = Y[j]:
                x ← c[i-1,j-1]       // copy (free)
            else:
                x ← c[i-1,j-1] + c_s // substitution

            y ← c[i,j-1] + c_i       // insertion
            z ← c[i-1,j] + c_d       // deletion

            c[i,j] ← min(x, y, z)

    return c[m,n]
```

### Part 2 [12 points]

**Claim:** D(xa, ya) = D(x, y) for all strings x, y and every letter a.

**Proof:**

**(≤ direction):** From D(x,y), there exists an optimal edit sequence W₀ for (x,y). Then W₁ = W₀ followed by copy(a→a) is an edit sequence for (xa,ya) with the same cost. Thus D(xa,ya) ≤ D(x,y).

**(≥ direction, by contradiction):** Assume D(xa,ya) < D(x,y). Let W' = W·(u→v) be an optimal edit sequence for (xa,ya).

**Case (a): (u→v) = (a→a) (copy)**
Then W is an edit sequence for (x,y), so D(x,y) ≤ cost(W) = cost(W') = D(xa,ya). Contradiction.

**Case (b): (u→v) = (a→ε) (deletion)**
The edit sequence must have a suffix where 'a' appears in the output at some point. We can always rearrange to end with (a→a) without increasing cost, reducing to Case (a).

**Case (c): (u→v) = (ε→a) (insertion)**
Symmetric to Case (b).

In all cases we reach a contradiction. Therefore D(xa,ya) = D(x,y).

### Part 3 [20 points]

**Claim:** L(X,Y) = (|X| + |Y| - EDITDISTANCE(X, Y, 1, 1, ∞)) / 2

**Proof:**

Setting c_s = ∞ disallows substitutions. The edit distance becomes:
```
D(x, ε) = |x|
D(ε, y) = |y|
D(xa, ya) = D(x, y)                              [from Part 2]
D(xa, yb) = min(D(xa,y) + 1, D(x,yb) + 1)       [when a ≠ b]
```

**Lemma:** D(x,y) + 2L(x,y) = |x| + |y| for all strings x, y.

**Proof by induction on |x| + |y|:**

- **Case (x, ε):** D(x,ε) + 2L(x,ε) = |x| + 0 = |x| + |ε| ✓

- **Case (ε, y):** Similar.

- **Case (xa, ya):**
  D(xa,ya) + 2L(xa,ya) = D(x,y) + 2(L(x,y) + 1) = D(x,y) + 2L(x,y) + 2 = |x| + |y| + 2 = |xa| + |ya| ✓

- **Case (xa, yb) with a ≠ b:**
  WLOG assume L(xa,y) ≤ L(x,yb). Then D(xa,y) ≥ D(x,yb) by IH.

  D(xa,yb) + 2L(xa,yb) = D(x,yb) + 1 + 2L(x,yb) = |x| + |yb| + 1 = |xa| + |yb| ✓

**Solution:**
```
LCS-LENGTH(X, Y):
    return (|X| + |Y| - EDITDISTANCE(X, Y, 1, 1, ∞)) / 2
```
