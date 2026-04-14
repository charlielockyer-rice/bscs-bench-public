# COMP 182 Homework 4: Solutions and Grading Rubric

## Problem 1: Growth of Functions, Algorithms, and Summations [45 pts]

### Growth of Functions [25 pts]

#### A. Show that 1^k + 2^k + ... + n^k = O(n^(k+1)) [4 pts]

**Solution:**

1^k + 2^k + ... + n^k <= n^k + n^k + ... + n^k = n * n^k = n^(k+1) for every n_0 >= 0.

Therefore, if we take n_0 = 0 and C = 1, we obtain 1^k + 2^k + ... + n^k <= C*n^(k+1) for every n >= n_0.

Equivalently, 1^k + 2^k + ... + n^k = O(n^(k+1)).

**Rubric:**
- Does the answer give specific values for constants C and n_0?
  - Yes: 2 pts
  - No: 0 pts
- Does the answer show that 1^k + 2^k + ... + n^k <= C*n^(k+1) for every n >= n_0?
  - Yes: 2 pts
  - No: 0 pts

---

#### B. Show that for a > 1, b > 1, if f(x) = O(log_b x), then f(x) = O(log_a x) [4 pts]

**Solution:**

Let a > 1 and b > 1 and assume f(x) = O(log_b x). Then, there exist constants C and k such that f(x) <= C log_b x for every x >= k.

We make use of the change-of-base formula for logarithms: log_b x = log_a x / log_a b

We obtain f(x) <= C log_a x / log_a b for every x >= k.

Since log_a b is a constant, then C' = C / log_a b is a constant.

Therefore, f(x) <= C' log_a x for every x >= k.

In other words, f(x) = O(log_a x).

**Rubric:**
- Does the answer make use of the fact that there exist constants C and k such that f(x) <= C log_b x for every x >= k?
  - Yes: 1 pt
  - No: 0 pts
- Does the answer make use of the change-of-base formula?
  - Yes: 1 pt
  - No: 0 pts
- Does the answer get the correct constant C' by making use of the fact that log_a b is a constant?
  - Yes: 1 pt
  - No: 0 pts
- Does the answer reach the final conclusion from the other parts?
  - Yes: 1 pt
  - No: 0 pts

---

#### C. Show that log(x^2 + 1) and log(x) are of the same order [4 pts]

**Solution:**

Clearly, for this to be defined, we have x > 0.

**Direction 1: log(x) = O(log(x^2 + 1))**

We know that for x >= 1, x^2 >= x. Furthermore, whenever x < 1, x^2 + 1 >= x.
Therefore, x^2 + 1 >= x for every x > 0.
It follows that log(x^2 + 1) >= log x for every x > 0.

So, if we take k = 1 and C = 1, we obtain log x <= C log(x^2 + 1) for every x > k.
Then we have shown that log x = O(log(x^2 + 1)).

**Direction 2: log(x^2 + 1) = O(log x)**

We know that log(x^2 + 1) <= log(x^2 + x^2) = log(2x^2) = log 2 + 2 log x = 1 + 2 log x for every x > 1.

Therefore, if we take k = 2 and C = 3, we obtain log(x^2 + 1) <= C log x for every x > k.

It follows from both results that log(x^2 + 1) and log(x) are of the same order.

**Rubric:**
- +2 points for each of the two directions, for a total maximum of 4 points
- Give 0 points if a direction is completely wrong, or just constants are given but no explanation
- Give +1 point to a direction if it is partially correct

---

#### D. Prove transitivity of Theta: f(x) = Theta(g(x)) and g(x) = Theta(h(x)) implies f(x) = Theta(h(x)) [5 pts]

**Solution:**

- From the assumption f(x) = Theta(g(x)), we have constants k_1, C_1, C_2 such that C_1*g <= f <= C_2*g for all x > k_1.
- From the assumption g(x) = Theta(h(x)), we have constants k_2, C_3, C_4 such that C_3*h <= g <= C_4*h for all x > k_2.

From these two results it follows that:
C_1*C_3*h <= f <= C_2*C_4*h

for all x > max(k_1, k_2).

So, if we take k' = max(k_1, k_2), C'_1 = C_1*C_3 and C'_2 = C_2*C_4, we get:
C'_1*h <= f <= C'_2*h

for all x > k'. In other words, f = Theta(h).

**Rubric:**
- Give +2 pts for the first two bullets (stating what we get from the assumptions)
- Give +3 pts for the three constants C'_1, C'_2, k'
- Maximum: 5 points

---

#### E. Order the functions by big-O [8 pts]

**Solution:**

The order is:
```
n log n log log n << n(log n)^(3/2) << n^(4/3)(log n)^2 << n^(3/2) << n^(log n) << 2^(100n) << 2^(n^2) << 2^(2^n) << 2^(n!)
```

**Justifications:**

We focus on sufficiently large values of n, and we make use of two results:
1. log n <= n^(1/k) (for any given natural number k)
2. n = 2^(log n)

- **n log n log log n <= n(log n)^(3/2):** Dividing by n log n, need log(log n) <= (log n)^(1/2), which follows from result (1).

- **n(log n)^(3/2) <= n^(3/2):** Need (log n)^(3/2) <= n^(1/2). Based on (1), (log n)^3 <= n, so done.

- **n^(3/2) >= n^(4/3)(log n)^2:** n^(3/2) = n^(9/6), n^(4/3)(log n)^2 = n^(8/6)(log n)^2. Dividing by n^(8/6), sufficient to show n^(1/6) >= (log n)^2. Since n^(1/12) >= log n by (1), we have (n^(1/12))^2 >= (log n)^2.

- **n^(3/2) <= n^(log n):** Since 3/2 <= log n for large n, n^(3/2) <= n^(log n).

- **n^(log n) <= 2^(100n):** n^(log n) = (2^(log n))^(log n) = 2^((log n)^2) <= 2^n <= 2^(100n).

- **2^(100n) <= 2^(n^2):** Since 100n <= n^2 for large n.

- **2^(n^2) <= 2^(2^n):** Since n^2 <= 2^n for large n.

- **2^(2^n) <= 2^(n!):** Since 2^n <= n! for large n.

**Rubric:**
- There are 8 pairwise relationships the students need to establish
- Give +1 pt for every correct one with correct justification
- Give 0 points to missing justification, incorrect justification, or incorrect pair
- Maximum: 8 points

---

### Algorithms and Their Complexity [10 pts]

#### A. Algorithm ComputeNumberOfShortestPaths [5 pts]

**Solution:**

The idea is simple:
1. Find the smallest d such that A^d[i, j] != 0; this d is the length of a shortest path between i and j. The value in A^d[i, j] is the number of paths of length d between these two nodes.
2. Remove the edge e from the graph by setting A[u, v] to 0; call this matrix B.
3. Compute B^d[i, j]. This is the number of paths of length d between i and j that do not use edge e.
4. Return A^d[i, j] - B^d[i, j].

**Pseudo-code:**

```
Algorithm ComputeNumberOfShortestPaths
Input: The adjacency matrix A of graph g = (V, E) with V = {0, 1, ..., n-1},
       i, j in V, and an edge e = {u, v} in E.
Output: The number of shortest paths between i and j that go through e.

1  d <- 1                          // d is the length of a shortest path
2  C <- A                          // matrix copying
3  while C[i, j] = 0 AND d <= (n - 1) do
4      C <- MatrixMultiplication(C, A)
5      d <- d + 1
6  if C[i, j] = 0 then
7      return 0
8  B <- A
9  B[u, v] <- 0
10 D <- B
11 for i <- 1 to d - 1 do
12     D <- MatrixMultiplication(D, B)
13 return C[i, j] - D[i, j]
```

**Rubric:**
- Does the algorithm have a name and have the correct input/output?
  - Yes to all: 1 pt
  - No to at least one: 0 pts
- Does the algorithm compute the length of shortest paths by finding when A^d[i,j] != 0?
  - Yes: 1 pt
  - No: 0 pts
- Does the algorithm create a copy of the adjacency matrix with entry [u,v] set to 0?
  - Yes: 1 pt
  - No: 0 pts
- Does the algorithm raise matrix B to the power d correctly?
  - Yes: 1 pt
  - No: 0 pts
- Does the algorithm return the correct answer (A^d[i,j] - B^d[i,j])?
  - Yes: 1 pt
  - No: 0 pts

---

#### B. Running time analysis [5 pts]

**Solution:**

The worst case corresponds to a scenario where the distance between i and j is n - 1 or i and j are not connected by a path.

In this case:
- The matrix multiplication on Line 4 is executed n-2 times, each taking O(n^3) time
- This means the Loop on Line 3 takes O(n^4) time
- Similarly, the Loop on Line 11 takes O(n^4) time
- The matrix copying on Lines 2, 8, and 10 take O(n^2) each
- All other lines take O(1) each

Therefore, the running time of this algorithm is **O(n^4)**.

**Rubric:**
- Does the answer state that matrix multiplication is O(n^3)?
  - Yes: 1 pt
  - No: 0 pts
- Does the answer state that in the worst case the distance is n - 1 or i and j are not connected?
  - Yes: 1 pt
  - No: 0 pts
- Does the answer state that each of the two loops take on the order of n^4 operations?
  - Yes: 1 pt
  - No: 0 pts
- Does the answer state that matrix copying takes O(n^2) time?
  - Yes: 1 pt
  - No: 0 pts
- Does the answer give O(n^4) as the final running time?
  - Yes: 1 pt
  - No: 0 pts

---

### Sequences and Summations [10 pts]

#### A. Derive the formula for sum of k^2 using telescoping sum [7 pts]

**Solution:**

Let S_n = sum from k=1 to n of k^2.

i. If we take a_k = k^3 in the telescoping sum, we get:
   sum from k=1 to n of (k^3 - (k-1)^3) = n^3 - 0^3 = n^3

ii. However, k^3 - (k-1)^3 = k^3 - (k^3 - 3k^2 + 3k - 1) = 3k^2 - 3k + 1
    Since we want sum of k^2, we have:
    S_n = sum from k=1 to n of [(k^3 - (k-1)^3) - 2k^2 + 3k - 1]

iii. This is the same as:
     S_n = sum(k^3 - (k-1)^3) - 2*sum(k^2) + 3*sum(k) - sum(1)

iv. We know:
    - sum(k^3 - (k-1)^3) = n^3
    - 2*sum(k^2) = 2*S_n
    - 3*sum(k) = 3*n(n+1)/2
    - sum(1) = n

v. Using these results:
   S_n = n^3 - 2*S_n + 3n(n+1)/2 - n

vi. Equivalently, 3*S_n = n^3 + 3n(n+1)/2 - n
    3*S_n = (2n^3 + 3n(n+1) - 2n) / 2 = n(n+1)(2n+1) / 2

    Therefore: **S_n = n(n+1)(2n+1) / 6**

**Rubric:**
- 1 pt for making use of the telescoping sum formula (Part i)
- 1 pt for Part ii
- 1 pt for Part iii
- 2 pts for Part iv
- 1 pt for Part v
- 1 pt for Part vi

---

#### B. Show sum of k^2 = O(n^3) [3 pts]

**Solution:**

We have seen that sum from k=1 to n of k^2 = n(n+1)(2n+1)/6.

We need to show that n(n+1)(2n+1)/6 = O(n^3).

We know that (2n^3 + 3n^2 + n)/6 <= (2n^3 + 3n^3 + n^3)/6 = n^3 for every n >= 0.

Therefore, if we take **C = 1** and **k = 0**, we have (2n^3 + 3n^2 + n)/6 <= C*n^3 for every n >= k.

In other words, sum of k^2 = O(n^3).

**Alternative solution:** sum from k=1 to n of k^2 <= sum of n^2 = n^3, with C = 1 and k = 1.

**Rubric:**
- Does the answer give specific values for constants C and k?
  - Yes: 1 pt
  - No: 0 pts
- Does the answer show that sum(k^2) <= C*n^3 for every n >= k?
  - Yes: 2 pts
  - No: 0 pts

---

## Problem 2: Breadth-First Search and Its Applications [30 pts]

### A. Algorithm ComputeDistances [10 pts]

**Solution:**

```
Algorithm ComputeDistances
Input: Graph g = (V, E); source node i in V.
Output: d_j in (N union {infinity}) for all j in V.

1  foreach j in V do
2      d_j <- infinity              // All nodes initially at distance infinity
3  d_i <- 0                         // Initialize distance to source node
4  Initialize Q to an empty queue
5  enqueue(Q, i)
6  while Q is not empty do
7      j <- dequeue(Q)
8      foreach neighbor h of j do
9          if d_h = infinity then
10             d_h <- d_j + 1       // Update distance to node h
11             enqueue(Q, h)
12 return d
```

The only differences from BFS are on Lines 2, 3, 9, and 10, where simple assignments and conditions were replaced by other simple assignments and conditions. Therefore, the algorithm performs a similar number of operations like BFS; that is, on the order of **m + n operations**.

**Rubric:**
- **Style [3 pts]:** Is the algorithm properly named, clearly and correctly specifies input/output?
  - All three items properly specified: +3 pts
  - One item not properly specified: +2 pts
  - Two or three items not properly specified: +0 pts
- **Correctness [5 pts]:**
  - +1 pt for correct initialization of d_j on Line 2
  - +1 pt for correct setting of d_i on Line 3
  - +1 pt for correct test condition of d_h on Line 9
  - +2 pts for correct update to d_h on Line 10
- **Running time [2 pts]:**
  - Trivial modification replaces O(1) operations by O(1) operations: +2 pts
  - No justification or wrong: +0 pts

---

### B. Algorithm IsBipartite [10 pts]

**Solution:**

A bipartite graph is 2-colorable. The idea is to start with a node, color it 0, then color its neighbors 1, their neighbors 0, and so on. If at any point, a node is already colored and the algorithm attempts to color it with a different color, the coloring fails.

```
Algorithm IsBipartite
Input: Graph g = (V, E) with |V| >= 2.
Output: True if g is bipartite, False otherwise.

1  foreach j in V do
2      c_j <- -1                    // Node j has not been colored yet
3  foreach i in V do
4      if c_i = -1 then
5          c_i <- 1                 // Assign color 1 to node i
6          Initialize Q to an empty queue
7          enqueue(Q, i)
8          while Q is not empty do
9              j <- dequeue(Q)
10             foreach neighbor h of j do
11                 if c_h = -1 then
12                     c_h <- 1 - c_j   // Assign the "other" color
13                     enqueue(Q, h)
14                 else if c_h = c_j then
15                     return False     // Conflict in coloring
16 return True
```

Similar to BFS, this algorithm traverses each edge exactly twice. The only difference is that this algorithm visits all nodes and edges even if the graph is disconnected. Running time: **O(m + n)**.

**Rubric:**
- **Style [2 pts]:** Is the algorithm properly named, clearly and correctly specifies input/output?
  - All items properly specified: +2 pts
  - One item not properly specified: +1 pt
  - Two or more items not properly specified: +0 pts
- **Correctness [4 pts]:**
  - +3 pts for correctly performing the BFS style exploration (Lines 6-13)
  - +0.5 pts for correctly initializing the c values (Line 2)
  - +0.5 pts for correctly testing a conflict in coloring (Lines 14 and 15)
- **Running time [4 pts]:**
  - Correct analysis making analogy to BFS: +4 pts
  - Partially correct analysis: +2 pts
  - Missing, completely wrong, or not O(m+n): +0 pts

---

### C. Algorithm ComputeLargestCCSize [10 pts]

**Solution:**

```
Algorithm ComputeLargestCCSize
Input: Graph g = (V, E).
Output: LargestSize in Z+, the size of the largest CC in g.

1  LargestSize <- -infinity         // Initialize size of largest CC
2  foreach j in V do
3      v_j <- False                 // Node j has not been visited yet
4  foreach i in V do
5      if v_i = False then
6          ccsize <- 1              // Initialize size of current CC
7          v_i <- True              // Start visiting source node i
8          Initialize Q to an empty queue
9          enqueue(Q, i)
10         while Q is not empty do
11             j <- dequeue(Q)
12             foreach neighbor h of j do
13                 if v_h = False then
14                     v_h <- True
15                     ccsize <- ccsize + 1   // Update size of current CC
16                     enqueue(Q, h)
17         LargestSize <- max{LargestSize, ccsize}  // Update largest CC
18 return LargestSize
```

The algorithm visits each edge exactly twice, similarly to BFS. Running time: **O(m + n)**.

**Rubric:**
- **Style [2 pts]:** Is the algorithm properly named, clearly and correctly specifies input/output?
  - All items properly specified: +2 pts
  - One item not properly specified: +1 pt
  - Two or more items not properly specified: +0 pts
- **Correctness [4 pts]:**
  - +1 pt for correctly looping to cover all components (Line 4)
  - +1 pt for correctly performing the BFS style exploration (Lines 7-16)
  - +1 pt for correctly initializing the v values (Line 3)
  - +1 pt for correctly computing LargestSize (Lines 1, 6, 15, 17, 18)
- **Running time [4 pts]:**
  - Correct analysis making analogy to BFS: +4 pts
  - Partially correct analysis: +2 pts
  - Missing, completely wrong, or not O(m+n): +0 pts

---

## Problem 3: Network Resilience [25 pts]

### Python Solution for compute_largest_cc_size

```python
import collections

def compute_largest_cc_size(g):
    """
    Computes the size of the largest connected component of graph g.

    Arguments:
    g -- undirected graph represented as a dictionary

    Returns:
    The size of the largest CC of g
    """
    largest_size = 0
    visited = {}

    # Initialize all nodes as not visited
    for node in g:
        visited[node] = False

    # Explore each unvisited node
    for node in g:
        if visited[node] == False:
            cc_size = 1
            visited[node] = True

            # Initialize a queue with current node
            queue = collections.deque([node])

            # BFS to explore connected component
            while queue:
                j = queue.popleft()
                for h in g[j]:
                    if visited[h] == False:
                        visited[h] = True
                        cc_size = cc_size + 1
                        queue.append(h)

            largest_size = max(largest_size, cc_size)

    return largest_size
```

### Helper Functions for Analysis

```python
def edge_count(g):
    """
    Return the number of edges in g.
    """
    edges = 0
    for n in g:
        edges += len(g[n])
    return edges // 2


def remove_node(g, n):
    """
    Remove a node from a graph.
    """
    g.pop(n, None)
    for u in g:
        g[u].discard(n)


def remove_random_node(g):
    """
    Remove a random node from a graph.
    """
    import random
    nodes = list(g.keys())
    if nodes:
        remove_node(g, random.choice(nodes))


def remove_high_degree_node(g):
    """
    Remove the highest degree node from a graph.
    """
    if not g:
        return
    # Find highest degree
    highest = max(len(nbrs) for nbrs in g.values())
    # Find and remove one node with highest degree
    for n in g:
        if len(g[n]) == highest:
            remove_node(g, n)
            return


def resilience(g, remove_node_fn):
    """
    Compute the resilience of a graph using the given node removal function.

    Arguments:
    g -- undirected graph
    remove_node_fn -- a function that takes a graph and removes a node from it

    Returns:
    A dictionary mapping the number of removed nodes to the largest CC size
    """
    result = {}
    n = len(g)

    for removed in range(n):
        result[removed] = compute_largest_cc_size(g)
        if g:
            remove_node_fn(g)

    return result
```

### Analysis Code

```python
import math

def compute_resilience_analysis(filename):
    """
    Compute and plot the resilience of a graph stored in "filename"
    using both random and targeted attack models. Compare to similar
    random graphs using Erdos-Renyi and UPA models.
    """
    # Read graph and compute parameters
    g = comp182.read_graph(filename)
    n = len(g)
    edges = edge_count(g)
    p = float(edges) / ((n * (n-1)) / 2.0)
    m = int(math.ceil(float(edges) / n))

    print(f"nodes: {n} edges: {edges} m: {m} p: {p}")

    # Generate random graphs
    erg = erdos_renyi(n, p)
    ername = f"G({n}, {p:.3f})"

    upag = upa(n, m)
    upaname = f"UPAG({n}, {m})"

    # Compute resilience of input graph
    drandom = resilience(comp182.copy_graph(g), remove_random_node)
    dtarget = resilience(comp182.copy_graph(g), remove_high_degree_node)

    # Compute resilience of Erdos-Renyi graph
    errandom = resilience(comp182.copy_graph(erg), remove_random_node)
    ertarget = resilience(comp182.copy_graph(erg), remove_high_degree_node)

    # Compute resilience of UPA graph
    uparandom = resilience(comp182.copy_graph(upag), remove_random_node)
    upatarget = resilience(comp182.copy_graph(upag), remove_high_degree_node)

    # Collate data and plot
    data = [drandom, errandom, uparandom, dtarget, ertarget, upatarget]
    labels = [
        f"{filename} Random", f"{ername} Random", f"{upaname} Random",
        f"{filename} Target", f"{ername} Target", f"{upaname} Target"
    ]

    comp182.plot_lines(data, "Largest Connected Component Size",
                       "Nodes Removed", "Component Size", labels)
```

### Expected Parameter Values

- **p (for Erdos-Renyi):** approximately 0.0034 (values between 0.003 and 0.004 are correct)
- **m (for UPA):** either 2 or 3

### Expected Results and Discussion

The results show that:

1. **ISP Network (rf7.repr):** Resilient to random attacks, but very sensitive to targeted attacks. If attacked by targeting high-degree nodes, connectivity gets damaged very rapidly.

2. **Erdos-Renyi graphs:** Have similar resilience to random attacks as UPA graphs, but are more resilient to targeted attacks than UPA graphs.

3. **UPA graphs:** Less resilient to targeted attacks because they have hubs (nodes with high degrees) whose removal disrupts connectivity.

**Key Discussion Points:**

- UPA graphs are less resilient to targeted attacks than ER graphs because UPA graphs have hubs whose removal is expected to disrupt connectivity
- Both types of random graphs have similar resilience to random attacks because (1) the presence of hubs in UPA graphs does not matter in this case, and (2) the choice of p and m ensured that most nodes in both graphs have similar degree
- For resilience to targeted attacks, design the network like an ER graph
- However, the size of the largest connected component does not account for factors like geography, distance, and cost that influence real-world network designs

---

### Grading Rubric for Problem 3

#### The compute_largest_cc_size function [11 pts]

**A.** Does the submitted function contain a docstring and useful comments?
- No: 0 pts
- Yes: 1 pt

**B.** Is there clear indication that the student tested the code on at least two of their own examples?
- No: 0 pts
- Yes: 1 pt

**C.** Does the algorithm return the correct value?
- 0 to 9 points, as determined by the autograder

#### The rest of the Python code [2 pts]

**A.** Does the Python code have appropriate docstrings and comments?
- No docstrings/comments or not proper: 0 pts
- All functions properly documented: 1 pt

**B.** Is the Python code modular (uses erdos_renyi, upa, compute_largest_cc_size, and a resilience function)?
- Not modular or repeats code: 0 pts
- Code is modular and not repeated: 1 pt

#### Correctness and formatting of the plots [5 pts]

**A.** Are there six curves, properly formatted?
- Fewer than 4 curves or improper format: 0 pts
- 4 or 5 curves: 1 pt
- All 6 curves, properly formatted: 2 pts

**B.** Do the curves have correct trends?
- At least one curve disagrees: 0 pts
- All curves agree: 2 pt

#### The chosen values of p and m [2 pts]

- Values not provided or wrong: 0 pts
- Both values correct: 2 pts

#### Discussion details [7 pts]

**A.** ISP network is resilient to random attacks and sensitive to targeted attacks?
- No: 0 pts
- Yes: 1 pt

**B.** UPA graphs are less resilient to targeted attacks than ER graphs because of hubs?
- No: 0 pts
- States fact but no reason: 1 pt
- Full explanation: 2 pts

**C.** Both random graphs have same resilience to random attacks (with both reasons)?
- No: 0 pts
- One reason: 1 pt
- Both reasons: 2 pts

**D.** ER graph topology best for targeted attack resilience, but real-world factors matter?
- No: 0 pts
- Yes: 2 pts
