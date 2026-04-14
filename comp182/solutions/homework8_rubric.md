# COMP 182: Algorithmic Thinking
## Homework 8: Recurrences and Dynamic Programming
### Solutions and Grading Rubric

---

## 1. Modeling with Recurrences [50 pts]

### Problem 1.1 [20 pts] - Tile Covering Recurrences

#### Part (1): 1 x n area [6 pts]

**Solution:**

Consider a tile that covers the rightmost area:
- If it's a 1 x 1 tile, we are left with an area of n - 1 to cover
- If it's a 1 x 2 tile, we are left with an area of n - 2 to cover

**Recurrence:** a_n = a_{n-1} + a_{n-2}

**Boundary conditions:** a_1 = 1 and a_2 = 2

**Grading:**
- 3 pts: Correct recurrence
- 3 pts: Correct boundary conditions

---

#### Part (2): 2 x n area [14 pts]

**Solution:**

This is more involved. We introduce:
- a_n: the number of ways to cover a 2 x n area (the quantity we're interested in)
- b_n: the number of ways to cover a 2 x n area where one row has an area of (n-1) and the second row has an area of n

**Case Analysis:**

**Cases 1 and 2:** If we cover the rightmost square in both rows (either by two 1 x 1 tiles or one 1 x 2 tile placed vertically), then we are left with a_{n-1} ways to cover the remaining area. So, in this case we have 2a_{n-1}.

**Cases 3 and 4:** If we cover the rightmost square in one row with a 1 x 1 tile and the two rightmost squares in the other row with a 1 x 2 tile, then we are left with b_{n-1} ways to cover the remaining area. So, in this case we have 2b_{n-1}.

**Case 5:** If we cover the two rightmost squares in both rows with 1 x 2 tiles, then we are left with a_{n-2} ways to cover the rest.

**First recurrence:** a_n = 2a_{n-1} + 2b_{n-1} + a_{n-2}

**Recurrence for b_n:** b_n = a_{n-1} + b_{n-1}

**Deriving a single recurrence:**

From the first recurrence:
b_{n-1} = (1/2)a_n - a_{n-1} - (1/2)a_{n-2}

Replacing n-1 by n:
b_n = (1/2)a_{n+1} - a_n - (1/2)a_{n-1}

Plugging into the recurrence for b_n:
(1/2)a_{n+1} - a_n - (1/2)a_{n-1} = a_{n-1} + (1/2)a_n - a_{n-1} - (1/2)a_{n-2}

This simplifies to:
**a_{n+1} = 3a_n + a_{n-1} - a_{n-2}**

Alternatively:
**a_n = 3a_{n-1} + a_{n-2} - a_{n-3}**

**Boundary conditions:** a_1 = 2, a_2 = 7, a_3 = 22

**Grading:**
- 4 pts: Identifying the need for auxiliary sequence b_n
- 4 pts: Correct case analysis
- 3 pts: Correct combined recurrence
- 3 pts: Correct boundary conditions

---

### Problem 1.2 [10 pts] - Graph Paths Recurrence

**Solution:**

Let a_n be the number of paths of length n that start at node 1.

Let b_n^{(i)}, for i = 2, 3, 4, 5, be the number of paths of length n that start at node i.

**Key observation:** Due to graph symmetry, b_n^{(2)} = b_n^{(3)} = b_n^{(4)} = b_n^{(5)}. Therefore, we assume b_n = b_n^{(i)}.

Since from node 1 we can go to any of the other four nodes:
**a_n = 4b_{n-1}**

Furthermore:
**b_n = 2b_{n-1} + a_{n-1}**

(This is because from any node i in {2,3,4,5}, we can go to node 1 (contributing a_{n-1}) and to exactly 2 other nodes in {2,3,4,5} (contributing 2b_{n-1}))

**Deriving a single recurrence:**

From a_n = 4b_{n-1}, we get b_{n-1} = a_n/4

Substituting into b_n = 2b_{n-1} + a_{n-1}:
(1/4)a_{n+1} = (1/2)a_n + a_{n-1}

Equivalently:
**a_n = 2a_{n-1} + 4a_{n-2}**

**Boundary conditions:** a_1 = 4 and a_2 = 12

**Grading:**
- 3 pts: Recognizing symmetry and setting up auxiliary sequence
- 3 pts: Correct system of recurrences
- 2 pts: Correct combined recurrence
- 2 pts: Correct boundary conditions

---

### Problem 1.3 [20 pts] - Counting Functions

#### Part (a) [14 pts] - Recurrence and Boundary Conditions

**Solution:**

This problem is equivalent to counting the number of sequences of length n over {0, 1, 2} with no two adjacent 2's.

**Reasoning:**
- If the last digit is 0 or 1, then we have a_{n-1} ways to complete the sequence (2 choices for last digit x a_{n-1})
- If the last digit is 2, then the second from last has to be either 0 or 1 (to avoid f(n-1) + f(n) = 4), giving us 2a_{n-2}

**Recurrence:** a_n = 2a_{n-1} + 2a_{n-2}

**Boundary conditions:** a_0 = 1 and a_1 = 3

**Grading:**
- 5 pts: Correct interpretation of the problem
- 5 pts: Correct recurrence with justification
- 4 pts: Correct boundary conditions

---

#### Part (b) [6 pts] - Explicit Formula

**Solution:**

**Characteristic equation:** x^2 - 2x - 2 = 0

**Characteristic roots:** 1 - sqrt(3) and 1 + sqrt(3)

Using the boundary conditions and the standard technique for solving linear recurrences:

**a_n = (1/sqrt(3) + 1/sqrt(2))(1 + sqrt(3))^n + (1/sqrt(2) - 1/sqrt(3))(1 - sqrt(3))^n**

**Grading:**
- 2 pts: Correct characteristic equation
- 2 pts: Correct characteristic roots
- 2 pts: Correct explicit formula using boundary conditions

---

## 2. Recurrences and Dynamic Programming [50 pts]

### Problem 2.1 [25 pts] - Seam Carving

#### Part (a) - Exponential Growth

**Solution:**

Once a pixel is chosen in row i, then there are either:
- 2 choices (if the pixel in row i is in the first or last column), or
- 3 choices (for any other column) for the pixel in row i+1

Therefore, there are **at least 2^m possible seams**.

**Grading:**
- 5 pts: Correct argument showing exponential growth (at least 2^m)

---

#### Part (b) - Dynamic Programming Algorithm

**Solution:**

**Definition:** Define matrix M[1..m, 1..n], where M[i,j] is the disruption of an optimal seam ending in position [i,j] (the seam starts somewhere in row 1).

**Recurrence:**

M[i,j] = M[i-1, k*] + d[i,j]

where k* is defined as:
- k* = argmin_{k in {j, j+1}} M[i-1, k]  if j = 1 (leftmost column)
- k* = argmin_{k in {j-1, j}} M[i-1, k]  if j = n (rightmost column)
- k* = argmin_{k in {j-1, j, j+1}} M[i-1, k]  otherwise

**Boundary conditions (first row):**
For all 1 <= j <= n: M[1,j] = d[1,j]

**Optimal value:**
j* = argmin_{1 <= j <= n} M[m,j]

**Pseudo-code:**
1. Fill out M[1,j] for all 1 <= j <= n according to boundary conditions
2. For all 2 <= i <= m, for all 1 <= j <= n, compute M[i,j] according to recurrence
3. For finding the seam, start the traceback from j* as computed above

**Time Complexity:**
- Step 1 takes O(n) time
- Step 2 takes O(mn) time
- Step 3 takes O(m) time

**Total: O(mn) time**

**Grading:**
- 5 pts: Correct definition of M[i,j] (subproblem structure)
- 5 pts: Correct recurrence handling all three cases (left edge, right edge, middle)
- 3 pts: Correct boundary conditions
- 4 pts: Correct pseudo-code
- 3 pts: Correct time complexity analysis

---

### Problem 2.2 [25 pts] - Optimal String Breaking

**Solution:**

The DP approach here has a similar structure to that of RNA secondary structure.

**Setup:**
- String S is indexed from 1 to n
- L is indexed from 1 to m (break points)
- S[a,b] denotes the substring of S from position a to position b, inclusive

**Extended notation:** We extend L so that it's L[0..m+1], where:
- L[0] = 0
- L[m+1] = n

This way:
- S[L[0]+1, L[1]] is the leftmost substring resulting from breaking at position L[1]
- S[L[m]+1, L[m+1]] is the rightmost substring resulting from breaking at position L[m]

**Definition:** M[i,j] is the lowest cost for a sequence of breaks of the substring S[L[i]+1, L[j]].

**Recurrence:**

M[i,j] = min_{k in {i+1, ..., j-1}} (L[j] - L[i] + M[i,k] + M[k,j])

where 0 <= i < j <= m+1.

**Explanation for a given k:**
- L[j] - L[i] is the cost of breaking the substring S[L[i]+1, L[j]], since its length is L[j] - L[i]
- M[i,k] is the cost of ("recursively") breaking the left substring S[L[i]+1, L[k]]
- M[k,j] is the cost of ("recursively") breaking the right substring S[L[k]+1, L[j]]

**Boundary condition:**
M[i, i+1] = 0 for 0 <= i <= m

(since the string S[L[i]+1, L[i+1]] will not be broken any further, as there are no breakpoints in L between L[i] and L[i+1])

**Quantity of interest:** M[0, m+1]

**Pseudo-code:**
```
(a) Set M[i, i+1] = 0 for all 0 <= i <= m
(b) l = 2
(c) While (l <= m+1):
    i. For i = 0 to (m+1) - l:
       A. j = i + l
       B. Compute M[i,j] according to the recurrence above
    ii. l = l + 1
(d) Return M[0, m+1]
```

**Time Complexity:**
- Each of the For and While loops takes O(m) time
- The recurrence takes O(m) time in the worst case

**Total: O(m^3) algorithm**

**Traceback:** Starts from M[0, m+1] for computing the actual sequence of breaks.

**Grading:**
- 5 pts: Correct definition of M[i,j] and extended L array
- 5 pts: Correct recurrence with proper justification
- 3 pts: Correct boundary conditions
- 5 pts: Correct pseudo-code showing order of computation
- 4 pts: Correct time complexity analysis
- 3 pts: Mentioning traceback for recovering the actual sequence

---

## Summary of Point Distribution

| Problem | Points |
|---------|--------|
| 1.1 (1 x n) | 6 |
| 1.1 (2 x n) | 14 |
| 1.2 | 10 |
| 1.3 (a) | 14 |
| 1.3 (b) | 6 |
| 2.1 (a) | 5 |
| 2.1 (b) | 20 |
| 2.2 | 25 |
| **Total** | **100** |
