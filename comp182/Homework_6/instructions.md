# COMP 182 Homework 6: Rooted Directed Minimum Spanning Trees

## Overview

In this homework, you will learn about minimum spanning trees for weighted, directed graphs and apply this to analyze disease transmission patterns.

---

## Problem 1: Proofs (Written)

### Part A: Prove Lemma 1

**Lemma 1:** Let g = (V, E, w) be a weighted, directed graph with designated root r ∈ V. Let

E' = {me(u) : u ∈ (V \ {r})}

where me(u) is an edge whose head is node u and whose weight is m(u) (the minimum weight of an edge whose head is u).

Then, either T = (V, E') is an RDMST of g rooted at r or T contains a cycle.

**Task:** Complete the proof of this lemma.

### Part B: Prove Lemma 2

**Lemma 2:** Let g = (V, E, w) be a weighted, directed graph with designated root r ∈ V. Consider the weight function w' : E → R+ defined as follows for each edge e = (u, v):

w'(e) = w(e) - m(v)

Then, T = (V, E') is an RDMST of g = (V, E, w) rooted at r if and only if T is an RDMST of g = (V, E, w') rooted at r.

**Task:** Complete the proof of this lemma.

---

## Problem 2: Implementing the RDMST Algorithm (Python)

Implement the following functions that are used by the `compute_rdmst` algorithm. The main algorithm is provided to you.

### Definitions

- **RDST (Rooted Directed Spanning Tree):** A subgraph T = (V, E') of a directed graph g such that:
  1. If we ignore edge directions, T is a spanning tree of g
  2. There is a directed path from root r to every node v ∈ V \ {r}

- **RDMST (Rooted Directed Minimum Spanning Tree):** An RDST with the smallest total edge weight

### Graph Representation

**Standard representation:** Dictionary where each key i maps to a dictionary of nodes reachable from i with edge weights.
```python
# Example: g = {0: {1: 20, 2: 4, 3: 20}, 1: {2: 2, 5: 16}, ...}
# Edge from 0 to 1 has weight 20
```

**Reversed representation:** Dictionary where each key i maps to a dictionary of nodes that have edges TO i.
```python
# Example: g = {0: {}, 1: {0: 20, 4: 4}, 2: {0: 4, 1: 2}, ...}
# Edges to node 1 come from nodes 0 (weight 20) and 4 (weight 4)
```

### Functions to Implement

#### 1. `reverse_digraph_representation(graph)`

Convert between standard and reversed representations.

**Arguments:**
- `graph` -- a weighted digraph in dictionary form

**Returns:**
- An identical digraph with the opposite representation

#### 2. `modify_edge_weights(rgraph, root)`

Modify edge weights according to Lemma 2 (Step 1 of algorithm). For each node (except root), subtract the minimum incoming edge weight from all incoming edges.

**Arguments:**
- `rgraph` -- a weighted digraph in reversed representation
- `root` -- a node in the graph

**Returns:**
- The modified graph (in-place modification)

#### 3. `compute_rdst_candidate(rgraph, root)`

Compute the set E' of edges based on Lemma 1 (Step 2 of algorithm). For each non-root node, select one edge with weight 0.

**Arguments:**
- `rgraph` -- a weighted digraph in reversed representation (after weight modification)
- `root` -- a node in the graph

**Returns:**
- A weighted digraph (reversed representation) containing the selected edges

#### 4. `compute_cycle(rgraph)`

Find a cycle in the RDST candidate graph.

**Arguments:**
- `rgraph` -- a weighted digraph in reversed representation where every node has in-degree 0 or 1

**Returns:**
- A tuple of nodes forming a cycle, or None if no cycle exists

#### 5. `contract_cycle(graph, cycle)`

Contract a cycle into a single node (Step 4a of algorithm).

**Arguments:**
- `graph` -- a weighted digraph in standard representation
- `cycle` -- a tuple of nodes on the cycle

**Returns:**
- A tuple (graph, cstar) where graph is the contracted graph and cstar is the new node label

#### 6. `expand_graph(rgraph, rdst_candidate, cycle, cstar)`

Expand a contracted cycle back to the original nodes (Step 4c of algorithm).

**Arguments:**
- `rgraph` -- the original digraph (reversed representation) before contraction
- `rdst_candidate` -- the RDMST computed on the contracted graph
- `cycle` -- the cycle that was contracted
- `cstar` -- the node label used for the contracted cycle

**Returns:**
- A weighted digraph with the cycle expanded back

---

## Problem 3: Bacterial Infection Transmission Analysis

### Background

In 2011, an outbreak of antibiotic-resistant *Klebsiella pneumoniae* infected 18 patients at a hospital, causing 6 deaths. Researchers used genetic and epidemiological data to trace the transmission.

### Task

Implement functions to:
1. Compute genetic (Hamming) distance between bacterial genomes
2. Construct a complete weighted digraph combining genetic and epidemiological data
3. Infer the transmission map using the RDMST algorithm

### Functions to Implement

#### 1. `compute_genetic_distance(seq1, seq2)`

Compute the Hamming distance between two sequences.

**Arguments:**
- `seq1` -- a DNA sequence (encoded as 0's and 1's)
- `seq2` -- a DNA sequence (same length as seq1)

**Returns:**
- The number of positions where the sequences differ

**Example:**
```python
compute_genetic_distance('00101', '10100')  # Returns 2
```

#### 2. `construct_complete_weighted_digraph(gen_data, epi_data)`

Build a complete weighted digraph using genetic and epidemiological data.

The weight of edge (A, B) is:
```
D_AB = G_AB + (999 * (E_AB / max(E))) / 10^5
```

where G_AB is genetic distance and E_AB is epidemiological distance.

**Arguments:**
- `gen_data` -- filename for genetic data
- `epi_data` -- filename for epidemiological data

**Returns:**
- A complete weighted digraph

#### 3. `infer_transmap(gen_data, epi_data)`

Infer the transmission map by computing the RDMST of the weighted digraph.

**Arguments:**
- `gen_data` -- filename for genetic data
- `epi_data` -- filename for epidemiological data

**Returns:**
- The RDMST representing the most likely transmission map

### Data Files

You are provided with:
- `patient_sequences.txt` -- genetic sequences for each patient
- `patient_traces.txt` -- epidemiological data

### Provided Functions (in `provided.py`)

- `read_patient_sequences(filename)` -- reads genetic data
- `read_patient_traces(filename)` -- reads epidemiological data
- `compute_pairwise_gen_distances(sequences, dist_func)` -- computes pairwise genetic distances

### Analysis Questions

After implementing the functions and inferring the transmission map:

1. Describe the transmission map you obtained
2. Who was Patient Zero (the source)?
3. Were there any surprising transmission paths?
4. How does this analysis help understand disease outbreaks?

---

## Submission Requirements

1. **Python code:**
   - `autograder.py` containing all required functions
   - Properly documented with docstrings and comments
   - Tested on your own examples

2. **Written solutions:**
   - Proofs for Lemma 1 and Lemma 2
   - Analysis and discussion of the transmission map

---

## Algorithm Reference: ComputeRDMST

```
Algorithm ComputeRDMST
Input: Weighted digraph g = (V, E, w) and node r ∈ V
Output: An RDMST T of g rooted at r

1. Modify edge weights according to Lemma 2
2. Compute RDST candidate T = (V, E') using Lemma 1
3. If T is an RDST, return it
4. Else (T contains a cycle C):
   (a) Contract cycle C into single node c*
   (b) Recursively compute RDMST of contracted graph
   (c) Expand c* back to original cycle nodes
   (d) Return expanded RDMST
```
