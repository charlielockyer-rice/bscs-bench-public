# COMP 182: Algorithmic Thinking

## Homework 3: Graphs - Solutions

---

## Problem 1: Understanding Graph-theoretic Definitions and Terminology [29 pts]

### 1.1 Degree Sequences [7 pts]

**K_4:** Every node in K_4 has degree 3; therefore, the degree sequence of K_4 is **3, 3, 3, 3**.

**C_5:** Every node in C_5 has degree 2; therefore, the degree sequence of C_5 is **2, 2, 2, 2, 2**.

**K_{m,n}:** K_{m,n} has m+n nodes u_1, u_2, ..., u_m, v_1, v_2, ..., v_n. Assume without loss of generality that m <= n. Each of the m nodes u_1, ..., u_m has degree n, and each of the n nodes v_1, ..., v_n has degree m. Since m <= n, the degree sequence is **a sequence of m instances of the value n followed by n instances of the value m**.

For example, if m = 2 and n = 4, then the degree sequence is 4, 4, 2, 2, 2, 2.

**Grading:**
- 2 pts for K_4
- 2 pts for C_5
- 3 pts for K_{m,n} (must handle the general case correctly)

---

### 1.2 Planar Graphs [4 pts]

**K_2, K_3, and K_4 are planar, but K_5 is not.**

The planar drawings:
- K_2: Two nodes connected by a single edge
- K_3: Triangle
- K_4: Can be drawn as a triangle with one node in the center connected to all three vertices

**Grading:**
- 1 pt for correctly identifying which are planar
- 3 pts for correct drawings (1 pt each for K_2, K_3, K_4)

---

### 1.3 Course Prerequisites [9 pts]

**(a) Precedence Graph [3 pts]:**

```
M111 --> M112
              \
               --> S310 --> C382
              /              ^
M211 --------+               |
  \                          |
   --> C182 --> C215 --> C310 --> C410
         \         \              ^
          \         --> C321 ----/
           \              ^
            --> C311 ----/

E220 --> C321
C140 --> C182
```

The graph should show:
- MATH 111 -> MATH 112
- MATH 112 -> STAT 310
- MATH 211 -> COMP 182, STAT 310
- COMP 140 -> COMP 182
- COMP 182 -> COMP 215, COMP 311, COMP 382
- COMP 215 -> COMP 310, COMP 321
- STAT 310 -> COMP 382
- COMP 310 -> COMP 410
- COMP 311 -> COMP 410
- COMP 321 -> COMP 410
- ELEC 220 -> COMP 321

**Grading:** 3 pts for correct graph with all edges

**(b) Acyclic? [3 pts]:**

The graph is **acyclic**. It makes no sense for this graph to have cycles because that would cause a situation where at least one course cannot be taken (circular dependency).

**Grading:**
- 1 pt for identifying acyclic
- 2 pts for explanation

**(c) Minimum Semesters [3 pts]:**

**Five semesters** is the minimum.

More generally, the minimum number of semesters equals **the number of nodes on a longest path in the graph**.

One such longest path: MATH 111 -> MATH 112 -> STAT 310 -> COMP 382 (but this is only 4)
Actually: COMP 140 -> COMP 182 -> COMP 215 -> COMP 310 -> COMP 410 (5 nodes)
Or: COMP 140 -> COMP 182 -> COMP 215 -> COMP 321 -> COMP 410 (5 nodes)

**Grading:**
- 1 pt for correct answer (5 semesters)
- 2 pts for general algorithm (longest path)

---

### 1.4 Matchings [9 pts]

**(a) Graph with matching of size 2 [3 pts]:**

A complete graph with 5 nodes (K_5), for example, has a matching of size 2. Any two non-adjacent edges form a matching of size 2.

**Grading:** 3 pts for valid example

**(b) Graph with no matching of size 2 [3 pts]:**

A graph with 5 nodes and no edges, for example, has no matching of size 2.

Alternative: A star graph K_{1,4} (one center node connected to 4 leaf nodes) has no matching of size 2.

**Grading:** 3 pts for valid example

**(c) Size of perfect matching [3 pts]:**

In a perfect matching, every node has to be the endpoint of an edge in the matching, and, by definition of matching, no node can be the endpoint of two edges in the matching. Therefore, it follows that a perfect matching has **n/2 edges**.

**Grading:** 3 pts for correct answer with reasoning

---

## Problem 2: Graph-theoretic Problem Formulation [21 pts]

### 2.1 Jury Selection [7 pts]

We model the input as a graph where the individuals are nodes and there's an edge between two nodes if their corresponding individuals know each other.

**Input:** Graph g = (V, E) and m in N.

**Output:** V' subset of V such that (|V'| = m) AND (for all u, v in V', {u, v} not in E).

This is the **Independent Set** problem.

**Grading:**
- 3 pts for correct graph model
- 2 pts for correct input specification
- 2 pts for correct output specification

---

### 2.2 Student Groups [7 pts]

We model the input as a graph where the students are nodes and there's an edge between two nodes if their corresponding students like each other.

**Input:** Graph g = (V, E).

**Output:** Partition V_1, V_2, ..., V_k where:
1. For all 1 <= i <= k, for all u, v in V_i, (u != v) implies ({u, v} in E)
2. k is the smallest size of a partition that satisfies (i)

This is the **Clique Cover** problem (partitioning into cliques).

**Grading:**
- 3 pts for correct graph model
- 2 pts for correct input specification
- 2 pts for correct output specification (partition into cliques, minimize k)

---

### 2.3 Course Scheduling [7 pts]

This is an instance of the **Graph Coloring** problem.

We model the input as a graph where the courses are nodes and there's an edge between two nodes if their corresponding courses have at least one student taking both of them.

**Input:** Graph g = (V, E).

**Output:** Smallest l such that (l <= k) AND (there exists f : V -> {1, ..., l}, for all {u, v} in E, f(u) != f(v)).

We can modify it to define the construction problem where we get the actual time slot assignment for the courses.

**Grading:**
- 3 pts for correct graph model (courses as nodes, edges for shared students)
- 2 pts for correct input specification
- 2 pts for correct output specification (valid coloring with minimum colors <= k)

---

## Problem 3: Proving Graph-theoretic Results [50 pts]

### 3.1 Bipartite Bit Vector Graph [10 pts]

**Proof:**

Let V_e be the set of bit vectors of size n with an **even number of zeros**. Let V_o be the set of all bit vectors of size n with an **odd number of zeros**.

Clearly, V_e intersection V_o = empty set and V_e union V_o = V. So, V_e and V_o form a bipartition of V.

We now show that no edge has both of its endpoints in either V_e or in V_o.

Let e = {u, v} be an arbitrary edge. If u has an even number of zeros, then u in V_e. By definition of the graph, v differs from u by one bit, so v has an odd number of zeros; that is, v in V_o. Similarly if u has an odd number of zeros.

**Therefore, the graph is bipartite.**

**Alternative characterization:** V_e contains bit vectors with even parity (even number of 1s), V_o contains odd parity vectors. Flipping one bit changes parity.

**Grading:**
- 3 pts for defining the bipartition
- 4 pts for proving no edge has both endpoints in same partition
- 3 pts for clear logical structure and conclusion

---

### 3.2 Bridges and Even Degrees [10 pts]

**Proof by contradiction:**

Assume the graph is connected. (If not connected, we can do the proof on any of its connected components.)

Assume all degrees in g are even and the graph has a bridge e = {u, v}.

Then, g' = (V, E \ {e}) has two connected components, one that contains the node u and another that contains the node v.

Since the edge e was deleted and node u had an even degree before e's deletion, then u has an **odd degree** in g'.

So, in the connected component that has node u, all nodes have even degrees (by the assumption) except for u which has odd degree.

Therefore, the sum of the degrees of all nodes in the connected component is **odd**.

This contradicts the **Handshaking Lemma** (which is applicable to connected components as well; the sum of degrees of all nodes in a connected component must be even since each edge contributes 2 to the sum).

**Therefore, g doesn't have a bridge.**

**Grading:**
- 2 pts for setting up proof by contradiction
- 3 pts for analyzing degree of u after edge removal
- 3 pts for applying Handshaking Lemma
- 2 pts for correct conclusion

---

### 3.3 Edge Coloring and Hamiltonian Circuits [10 pts]

**Proof:**

**Step 1:** The Handshaking Lemma tells us that the sum of the degrees of a graph's nodes is even. Since the degree of each node in g is odd (3), then the number of nodes in g must be **even** (otherwise the sum of its degrees would be odd).

**Step 2:** Since g has a Hamiltonian circuit, that circuit must be of **even length** (same number of edges as nodes).

**Step 3:** Let the set of edges on the circuit be denoted by C. Color the edges in C with **two colors** while alternating colors as you walk along the circuit. Since the circuit has even length, this 2-coloring is consistent (no conflicts). So, 2 colors are sufficient for the circuit edges.

**Step 4:** Now, consider the graph g' = (V, E \ C). Since the degree of each node in g is 3, and the degree of each node in the Hamiltonian circuit subgraph g'' = (V, C) is 2, it follows that the degree of each node in g' is **1**.

In other words, g' is a collection of edges, no two of which share an endpoint (a perfect matching).

**Step 5:** Color all these edges with the same, **third color**. Since no two of these edges share an endpoint, there are no conflicts.

**Conclusion:** The edges of g are colored with 3 colors, and no two adjacent edges share a color.

**Grading:**
- 2 pts for proving n is even
- 2 pts for 2-coloring the Hamiltonian circuit
- 3 pts for analyzing remaining edges (degree 1 each)
- 3 pts for completing the 3-coloring argument

---

### 3.4 Cartesian Product Graphs [20 pts]

#### (a) Hamiltonian Circuits [8 pts]

**Statement is TRUE.**

**Proof:**

Assume that g_1 and g_2 both contain Hamiltonian circuits.

Let (u_1, u_2, ..., u_k, u_1) be a Hamiltonian cycle in g_1, and (v_1, v_2, ..., v_l, v_1) be a Hamiltonian cycle in g_2.

We construct a Hamiltonian cycle in C as follows:

```
(u_1, v_1) -> (u_1, v_2) -> ... -> (u_1, v_l) ->
(u_2, v_1) -> (u_2, v_2) -> ... -> (u_2, v_l) ->
...
(u_k, v_1) -> (u_k, v_2) -> ... -> (u_k, v_l) ->
(u_1, v_1)
```

**Verification:**
- Each transition within a row uses edges from g_2 (same u_i, adjacent v's)
- Each transition between rows uses edges from g_1 (adjacent u's, same v_1)
- This path visits all |V_1| * |V_2| nodes exactly once
- It starts and ends at the same node (u_1, v_1)

Therefore, it is a Hamiltonian cycle.

**Grading:**
- 2 pts for stating the claim is true
- 4 pts for constructing the Hamiltonian cycle
- 2 pts for verifying the construction is valid

#### (b) Euler Circuits [12 pts]

**Statement is TRUE.**

**Proof:**

Assume that g_1 and g_2 both contain Euler circuits.

By Theorem 1 in Section 10.5: A connected multigraph has an Euler circuit if and only if every vertex has even degree.

Since g_1 and g_2 have Euler circuits, every node in each graph has an even degree.

To show that C has an Euler circuit, we prove that:
1. C is connected
2. The degree of each node in C is even

**Part 1: C is connected**

Let (u, x) and (v, y) be two arbitrary nodes in C.

Since g_1 is connected, there is a path (u, u_1, ..., u_k, v) in g_1. Then, in C we have the path:
((u, x), (u_1, x), (u_2, x), ..., (u_k, x), (v, x))

Since g_2 is connected, there is a path (x, v_1, v_2, ..., v_l, y) in g_2. Then, in C we have the path:
((v, x), (v, v_1), ..., (v, v_l), (v, y))

Thus, we have a path between every two nodes in C. **C is connected.**

**Part 2: Every node in C has even degree**

Let (u, x) be an arbitrary node in C. Let A be the set of all neighbors of u in g_1, and B be the set of all neighbors of x in g_2.

We partition the set D of neighbors of node (u, x) in C into three sets:

- **D_1 = A x B:** The set of all neighbors (v, y) of (u, x) such that v is a neighbor of u AND y is a neighbor of x.

- **D_2 = A x (V_2 \ B):** The set of all neighbors (v, y) of (u, x) such that v is a neighbor of u AND y is NOT a neighbor of x.

- **D_3 = (V_1 \ A) x B:** The set of all neighbors (v, y) of (u, x) such that v is NOT a neighbor of u AND y is a neighbor of x.

The three sets are non-empty, disjoint, and their union gives D, therefore they are a partition of D.

Since |A| and |B| each has an even number of elements (all nodes have even degrees in g_1 and g_2), it follows that:
- |D_1| = |A| * |B| is even (even * even)
- |D_2| = |A| * |V_2 \ B| is even (even * anything)
- |D_3| = |V_1 \ A| * |B| is even (anything * even)

Since |D| = |D_1| + |D_2| + |D_3|, it follows that |D| is even.

In other words, the degree of the arbitrary node (u, x) is even.

**Therefore, C has an Euler circuit.**

**Grading:**
- 2 pts for stating the claim is true
- 3 pts for proving C is connected
- 5 pts for proving all nodes have even degree (partitioning argument)
- 2 pts for clear logical structure and conclusion
