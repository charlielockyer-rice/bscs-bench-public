# COMP 182: Algorithmic Thinking
## Homework 4: Graphs and Their Exploration

---

## Problem 1: Growth of Functions, Algorithms, and Summations [45 pts]

### Growth of Functions [25 pts]

**A.** Show that 1^k + 2^k + ... + n^k = O(n^(k+1)).

**B.** Show that for all real numbers a and b with a > 1 and b > 1, if f(x) = O(log_b x), then f(x) = O(log_a x).

**C.** Show that the two functions log(x^2 + 1) and log(x) are of the same order (recall that "same order" is established using Theta).

**D.** Let f(x), g(x), and h(x) be three functions such that f(x) = Theta(g(x)) and g(x) = Theta(h(x)). Prove that f(x) = Theta(h(x)).

**E.** Arrange the functions 2^(100n), 2^(n^2), 2^(n!), 2^(2^n), n^(log n), n log n log log n, n^(3/2), n(log n)^(3/2), and n^(4/3)(log n)^2 in a list so that each function is big-O of the next function. Briefly justify your answer.

### Algorithms and Their Complexity [10 pts]

In class, we saw Algorithm **MatrixMultiplication** for multiplying two matrices and discussed its running time. Section 10.4 of your textbook establishes a theorem about the number of paths of any given length between a pair of nodes in a graph using the adjacency matrix of the graph and raising it to the appropriate power.

**A.** Using this theorem, write the pseudo-code of Algorithm **ComputeNumberOfShortestPaths** that takes as input the adjacency matrix A of a graph g (assume its nodes are numbered 0, 1, ..., n-1), two nodes i, j in the graph, and an edge e in the graph, and computes/returns the number of shortest paths (recall: the length of a path is the number of edges on it) between nodes i and j that go through e in g. *Do not use Algorithm BFS here. Your algorithm must make use of Algorithm MatrixMultiplication and be based on the Theorem in Section 10.4 of the textbook.*

**B.** Using big-O notation, what is the worst-case running time of your Algorithm **ComputeNumberOfShortestPaths** on a graph with n nodes? Clearly state what the worst case corresponds to in this case, assuming the distance between pairs of nodes could be as large as n - 1.

### Sequences and Summations [10 pts]

Several algorithms have a loop structure that iterates n times and for the k-th iteration, they perform on the order of k^2 operations. In this case, the running time of the loop can be obtained by the sum from k=1 to n of k^2. In this problem, we will derive a closed formula for this sum and show that the sum is O(n^3).

**A.** A *telescoping sum* is a sum of the form sum from j=1 to n of (a_j - a_(j-1)). It is easy to see that this sum equals a_n - a_0. In class, we stated that the sum from k=1 to n of k^2 = n(n + 1)(2n + 1)/6. Derive this formula using the telescoping sum. *Hint: Take a_k = k^3 in the telescoping sum.*

**B.** Show that the sum from k=1 to n of k^2 = O(n^3) by explicitly finding the values of the constants k and C and demonstrating the result.

---

## Problem 2: Breadth-First Search and Its Applications [30 pts]

The pseudo-code of Algorithm **BFS** is given below. This algorithm explores a graph g = (V, E) by visiting its nodes starting from a given source node i. Upon completion, the algorithm returns a list v that has an entry for each node j in V such that:

- v_j = True if node j was visited
- v_j = False if node j was not visited

As we discussed in class, if the input graph has n nodes and m edges, Algorithm BFS performs on the order of m + n operations.

### Algorithm BFS

```
Input: Graph g = (V, E); source node i in V.
Output: v_j in {True, False} for all j in V.

1  foreach j in V do
2      v_j <- False;                    // Node j has not been visited yet
3  v_i <- True;                         // Start by visiting the source node i
4  Initialize Q to an empty queue;
5  enqueue(Q, i);
6  while Q is not empty do
7      j <- dequeue(Q);
8      foreach neighbor h of j do
9          if v_h = False then
10             v_h <- True;
11             enqueue(Q, h);
12 return v;
```

**A. [10 pts]** Give the pseudo-code of Algorithm **ComputeDistances** that, given a graph g = (V, E) and a source node i in V, computes the distance d_j from node i to every other node j in V and performs on the order of m + n operations on a graph with n nodes and m edges. In particular, d_i = 0 and d_j = infinity if node j is not reachable from node i. Discuss that your algorithm indeed performs on the order of m + n operations. *Hint: Think about replacing v_j by d_j throughout the algorithm.*

**B. [10 pts]** Give the pseudo-code of Algorithm **IsBipartite** that, given a graph g = (V, E), determines whether the graph is bipartite and takes on the order of m + n operations. You may assume in this question that the input graph g is connected. *Hint: Think of slightly modifying Algorithm BFS to color the nodes of the graph.*

**C. [10 pts]** Give the pseudo-code of Algorithm **ComputeLargestCCSize** that, given a graph g = (V, E), computes the size (in terms of the number of nodes) of the largest connected component of g. Your algorithm must perform on the order of m + n operations in the worst case on a graph with n nodes and m edges and you need to discuss why that is the case.

---

## Problem 3: Network Resilience [25 pts]

For this problem, you must write the Python function `compute_largest_cc_size`, for the Algorithm **ComputeLargestCCSize** from Problem 2. You must write it in `autograder.py`, a file provided to you. It will be autograded.

You must also write analysis code for random attacks and target attacks on networks. All analysis code should be in a separate file, `analysis.py`. For this file, you will need `comp182.py`, which includes code for plotting the results of your experiments, and `provided.py`, which includes code for generating graphs.

### Background

Network resilience is a desired property of networks, man-made and natural alike. For example, we'd want the Internet to be resilient to attacks on its nodes. One way to measure, or quantify, resilience is by analyzing the effect of random and targeted attacks on disrupting the network's topology (i.e., graph).

- A **random attack** simulates the case where a node in the Internet physical network fails or malfunctions.
- A **targeted attack** simulates a case of a terrorist attack that targets special nodes in the network, e.g., those with high connectivity.

The way we will quantify the effect in this problem is by inspecting the change to the size of the largest connected component in a graph.

### Task

We will analyze the graph of a real network, and compare the analysis to that of randomly generated graphs under two different models of random graphs.

We have provided a graph of the measured network topology of an Internet Service Provider, `rf7.repr`. The nodes are network routers and the edges are network links. This file can be read using `comp182.read_graph`.

You should build two random graphs:
1. One using **Erdos-Renyi** (`erdos_renyi` in `provided.py`)
2. One using **Undirected Preferential Attachment** (`upa` in `provided.py`)

Both should have the same number of nodes and approximate edge characteristics as the real network topology graph. Make sure you describe how you decided on the parameter values to use with the functions for generating random graphs.

### Experiments

For each of the three graphs, run the following two experiments:

1. **Random attack:** Remove nodes randomly, one by one, and compute the size of the largest connected component for each resulting graph.

2. **Targeted attack:** Remove nodes in decreasing order of degree, one by one, and compute the size of the largest connected component for each resulting graph.

Plot the results of all six experiments as a function of the number of nodes removed using `comp182.plot_lines`. Note that you need to use the same random graphs for the two experiments. Therefore, you should copy the graphs before modifying them (`comp182.copy_graph` can be used for this purpose).

Analyze the results as the first 20% of the nodes are removed from each graph.

### Discussion Questions

Discuss the resilience of each type of graph to random and targeted attacks, and then compare the two random graph topologies. Are the results expected? Why? In particular, base your discussion on your understanding of how the two random graph models differ.

- How would you design your network topology, if your main concern was targeted attacks?
- How well was the real-world topology (given in `rf7.repr`) designed?

Keep in mind that other factors (such as geography, distance, and cost) influence real-world network designs.

---

## Provided Code

### autograder.py (Template)

```python
## Firstname Lastname
## NetID
## COMP 182 Homework 4 Problem 3

from collections import *

def compute_largest_cc_size(g: dict) -> int:
    """
    Compute the size of the largest connected component in graph g.

    Arguments:
    g -- undirected graph represented as a dictionary where keys are nodes
         and values are sets of neighbors

    Returns:
    The size (number of nodes) of the largest connected component in g.
    """
    return 0  # Your code here.
```

### Helper Functions (in provided.py)

- `erdos_renyi(n, p)` - Generate a random Erdos-Renyi graph with n nodes and edge probability p
- `upa(n, m)` - Generate an undirected graph with n nodes and m edges per node using preferential attachment
- `make_complete_graph(num_nodes)` - Returns a complete graph with num_nodes nodes
- `total_degree(g)` - Compute total degree of an undirected graph

### Utility Functions (in comp182.py)

- `read_graph(filename)` - Read a graph from a file
- `copy_graph(g)` - Return a deep copy of graph g
- `plot_lines(data, title, xlabel, ylabel, labels, filename)` - Plot line graphs

---

## Graph Representation

Graphs are represented as dictionaries where:
- Keys are node identifiers
- Values are sets containing the neighbors of each node

Example:
```python
# A simple graph with 3 nodes: a, b, c
# Edges: a-b, b-c
g = {
    "a": {"b"},
    "b": {"a", "c"},
    "c": {"b"}
}
```

---

## Submission Requirements

1. `autograder.py` - Contains the `compute_largest_cc_size` function (autograded)
2. `analysis.py` - Contains all analysis code for the network resilience experiments
3. A PDF/DOC file containing:
   - Solutions to Problem 1 (Growth of Functions, Algorithms, Summations)
   - Solutions to Problem 2 (BFS algorithms pseudo-code)
   - Discussion and plots for Problem 3
