# COMP 382 Homework 6 - Solutions

---

## Problem 1: True or False

### Part (a): Any flow graph G has a maximum flow that does not include a cycle in which each edge has positive flow.

**TRUE**

By conservation of flow, a cycle contributes zero net flow. If we have a maximum flow with a positive-flow cycle, we can reduce the flow on all edges of the cycle by the minimum flow value in that cycle, obtaining a flow of equal value without the cycle. Therefore, there always exists a maximum flow without positive-flow cycles.

### Part (b): If all edges in a graph have distinct capacities, there is a unique maximum flow.

**FALSE**

Counterexample:
```
        2/0  →  4/0
    s → ○ -----→ ○ → t
        ↓  3/1  ↑
        ○ -----→ ○
        2/1     4/1
```

Two different maximum flows can achieve the same value by routing flow through different paths.

### Part (c): If we multiply all edge capacities by a positive number λ, then the minimum cut stays unchanged.

**TRUE**

The value of each cut gets multiplied by λ. Thus, the relative order of cuts does not change. Whatever cut was minimum before remains minimum after scaling.

### Part (d): If we add a positive number λ to all edge capacities, then the minimum cut stays unchanged.

**FALSE**

Counterexample with λ = 2:

Original graph:
```
        2/1     b     1/1
    s ------→ ○ --------→ t
      \3/2    ↓ 1/1      ↗
       ↘     ○ --------→
         a       2/1
```
Min-cut is ({s, a, b}, {t}).

After adding λ = 2:
```
        4/2     b     3/2
    s ------→ ○ --------→ t
      \5/5    ↓ 3/3      ↗
       ↘     ○ --------→
         a       4/2
```
Min-cut is now ({s}, {t, a, b}).

---

## Problem 2: Flow Reduction

**Solution:**

If the minimum s−t cut has size ≤ k, then we can reduce the flow to zero by removing all edges in the min-cut.

Otherwise, let f > k be the value of the maximum s−t flow. We identify a minimum s−t cut (A, B), and delete k of the edges out of A. The resulting subgraph has a maximum flow value of f − k.

**Proof of optimality:**

We claim that for any set of edges F of size k, the subgraph G' = (V, E \ F) has an s−t flow of value at least f − k.

Indeed, consider any cut (A, B) of G'. There are at least f edges out of A in G, and at most k have been deleted, so there are at least f − k edges out of A in G'. Thus, the minimum cut in G' has value at least f − k, and so there is a flow of at least this value.

**Algorithm complexity:** O(VE²) using Edmonds-Karp for max-flow, plus O(E) to identify and remove cut edges.

---

## Problem 3: Node Capacities

**Solution:**

Transform the node-capacitated network into a standard edge-capacitated network:

For each node v other than the source and sink, replace it with two nodes: vᵢₙ and vₒᵤₜ.
- All edges that used to come into v now go into vᵢₙ
- All edges that used to go out of v now go out of vₒᵤₜ
- All these edges have infinite capacity (or a number larger than the sum of all node capacities)
- Add an edge (vᵢₙ, vₒᵤₜ) of capacity cᵥ

**Correctness:**

If there is a flow of value ν in this new graph, then there is a flow of value ν in the original graph that respects all node capacities: we simply use the flow obtained by contracting all edges of the form (vᵢₙ, vₒᵤₜ).

Conversely, if there is a flow of value ν in the original graph, then we can use it to construct a flow of value ν in this new graph; the flow using v in the original graph will now pass through the edge (vᵢₙ, vₒᵤₜ), and it will not exceed this edge capacity due to the node capacity condition in the original graph.

**Complexity:** The new graph has |V'| = 2|V| = O(|V|) nodes and |E'| = |V| + 2|E| = O(|V| + |E|) edges. Running time is the time required to solve a max-flow problem on this graph.

---

## Problem 4: Station Closing

**Solution:**

We assume that there is no rail between the two main stations A and B. We need to generalize the problem to deal with vertex capacities.

Let G(V, E) be the original graph where A, B ∈ V and (A, B) ∉ E. Construct a corresponding flow-graph G'(V', E') as follows:

1. For each v ∈ V, insert into V' vertices vᵢₙ and vₒᵤₜ
2. Insert into E' a directed "in-vertex" edge (vᵢₙ, vₒᵤₜ) of capacity 1
3. For each (u, v) ∈ E, insert into E' directed edges (uₒᵤₜ, vᵢₙ) and (vₒᵤₜ, uᵢₙ) of capacity ∞

Let s = Aₒᵤₜ, t = Bᵢₙ, and (S, T) be a minimum s−t cut in G'.

Since A and B are not directly connected, there doesn't exist an edge (s, t). Therefore, c(S, T) is at most |V| − 2, meaning that we cut only in-vertex edges, each of cost 1.

For any A−B path p in G, there is a unique corresponding s−t path p' in G'. Since (S, T) is an s−t cut, p' must use some in-vertex edge (vᵢₙ, vₒᵤₜ) from S to T, meaning that we cut (close down) v in G. Therefore, p must go through some cut-vertex in G.

It follows that the stations A and B are cut apart with the minimum number of stations closed down.

**Complexity:** O(|V| + |E|) for graph construction, plus max-flow time.

---

## Problem 5: Exam Scheduling

**Solution:**

Create a flow network G:

**Nodes:**
- n class nodes
- r room nodes
- t time nodes
- source s and sink t

**Edges:**
- Edge with capacity 1 from s to every class node
- Edge with capacity 1 from class node i to room node j for which E[i] < S[j]
- Edge of capacity 1 from every room node to every time node
- Edge of capacity r from every time node to t

**Algorithm:**
1. Compute maximum s-t flow
2. There is a valid assignment if and only if the flow value equals n
3. The assignment is determined by which class→room edges have flow

**Complexity:**
- |V| = n + r + t + 2
- |E| = at most n + nr + rt + t

Using Edmonds-Karp for max flow: O(VE²)

So complexity is O((n + r + t)(nr + rt)²)
