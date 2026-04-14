# COMP 182 Homework 6: Solutions and Rubric

## Directed Graphs, MSTs, and Infection Transmission

**Total: 100 points**

---

## Part 1: Algorithms and Proofs [20 pts]

### Problem 1: Minimizing Maximum Pair Sum [10 pts]

**(a) Brute-force algorithm [3 pts]**

**Solution:** Try every way of partitioning the n numbers into n/2 pairs, for each partition compute the maximum sum of a pair in that partition, and return the partition that has the minimum such sum.

**Running time:** O(n!n) (looking at all permutations and for each computing the maximum sum). A tighter bound: O(n!n/((n/2)!2^(n/2))).

**Rubric:**
- Brute-force algorithm is correct: 2 points
- Worst-case running time is correct (O(n!n) is good enough): 1 point

**(b) Mathematical result for polynomial-time algorithm [3 pts]**

**Solution:** Let x_1, x_2, ..., x_n be sorted in non-decreasing order. The partition that minimizes the maximum sum is (x_1, x_n), (x_2, x_{n-1}), ..., (x_{n/2}, x_{n/2+1}).

**Proof:** The pair (x_1, x_n) must be in an optimal solution. Assume it is not. Then the optimal solution has two pairs (x_1, x_j) and (x_k, x_n). Since x_1 is the smallest and x_n is the largest, we know x_1 <= x_j, x_k <= x_n. Replace these two pairs by (x_1, x_n) and (x_j, x_k). We know that x_1 + x_n <= x_k + x_n and x_j + x_k <= x_k + x_n. Therefore, the pair (x_1, x_n) must be in an optimal solution. Repeat the same exercise for (x_2, x_{n-1}) and so on.

**Rubric:**
- Relevant mathematical result with correct proof: 3 points
- Relevant mathematical result with almost correct proof: 2 points
- NOTE: No points for result with incorrect proof (result alone gives points for part c)

**(c) Polynomial-time algorithm [4 pts]**

**Solution:** (1) Sort the elements, and (2) pair them as given by the result in part (b). Running time: O(n log n) since sorting takes O(n log n) and pairing takes O(n).

**Rubric:**
- Correct algorithm: 2 points; almost correct: 1 point
- Correct worst-case running time: 2 points

---

### Problem 2: Unique MST with Distinct Weights [6 pts]

**Solution:** Proof by contradiction. Let g = (V, E, w) be a weighted graph with all distinct edge weights, and assume T_1 (with edges e_1, ..., e_k where k = |V| - 1) is the MST computed by Prim's algorithm. Assume T_2 is another MST. Let i be the smallest value such that e_i = (u, v) is not in T_2. Let (u, v') be an edge in T_2 connecting u to a node not in the subtree T_{i-1}. Replace (u, v') in T_2 by e_i = (u, v). This creates a spanning tree with smaller weight than T_2 (because w(u,v') must be strictly greater than w(e_i), since edge weights are distinct and Prim's chose e_i as the minimum). This contradicts T_2 being an MST.

**Rubric:**
- Correct proof: 6 points
- Correct proof with minor errors: 5 points
- Proof by contradiction with major logical error: 1 point

---

### Problem 3: Dijkstra's Algorithm with Negative Weights [4 pts]

**Solution:** Any directed graph with a negative-weight cycle causes Dijkstra's algorithm to fail, since the cycle can be taken repeatedly to shorten path lengths, leading to an infinite loop or incorrect results.

**Rubric:**
- Correct argument: 4 points
- Counterexample that demonstrates the issue: 4 points

---

## Part 2: Computing Rooted Directed MSTs (RDMSTs) [55 pts]

### Problem 2.1: Prove Lemma 1 [8 pts]

**Lemma 1:** Let E' = {me(u) : u in V\{r}}. Then either T = (V, E') is an RDMST or T contains a cycle.

**Solution (proof by contradiction):** The set E' contains one incoming edge per node, except for the root node r. If E' has no cycle, then E' is a tree rooted at r. If the total weight of E' is not minimum among all trees rooted at r, then there exists another tree T' with smaller weight. In that case, there exists a node u such that the edge incoming into u in T' has weight smaller than the edge incoming into u in E'. But this contradicts the property that each edge in E' has minimum weight among all edges incoming into a node. Therefore, if E' has no cycle, it is an RDMST. If E' has a cycle, then the result holds trivially.

**Rubric:**
- Missing proof or completely wrong: 0 points
- Missing the no-cycle case, has the cycle case: 1 point
- Has both cases but with issues: 2 points
- Missing the cycle case, has the no-cycle case: 4 points
- Correct proof: 8 points

---

### Problem 2.2: Prove Lemma 2 [6 pts]

**Lemma 2:** w'(e) = w(e) - m(v). Then T is an RDMST under w iff T is an RDMST under w'.

**Solution:** Let T be an RDST, and let w(T) and w'(T) be the total edge weights under w and w' respectively. Then w_hat = w(T) - w'(T) = sum_{u != r} m(u). Since w_hat is independent of which RDST we consider (it's a constant), any RDST has minimum weight under w if and only if it has minimum weight under w'.

**Rubric:**
- Missing proof or completely wrong: 0 points
- Neither completely wrong nor correct: 1-3 points (use discretion)
- Correct proof: 6 points

---

### Problem 2.3: `reverse_digraph_representation(graph)` [4 pts]

**Solution:**
```python
def reverse_digraph_representation(graph):
    rev_graph = {}
    for node in graph:
        rev_graph[node] = {}
    for node in graph:
        for nbr in graph[node]:
            rev_graph[nbr][node] = graph[node][nbr]
    return rev_graph
```

**Rubric:**
- Proper docstring and helpful comments: 1 point (YES) / 0 points (NO)
- Test cases passed: None=0, >0% and <=50%=1, >50% and <100%=2, All=3 points

---

### Problem 2.4: `modify_edge_weights(rgraph, root)` [4 pts]

**Solution:**
```python
def modify_edge_weights(graph, root):
    for node in graph:
        if (node != root and bool(graph[node])):
            min_weight = min(graph[node].values())
            for nbr in list(graph[node].keys()):
                graph[node][nbr] -= min_weight
```

**Rubric:**
- Proper docstring and helpful comments: 1 point (YES) / 0 points (NO)
- Test cases passed: None=0, >0% and <=50%=1, >50% and <100%=2, All=3 points

---

### Problem 2.5: `compute_rdst_candidate(rgraph, root)` [4 pts]

**Solution:**
```python
def compute_rdst_candidate(graph, root):
    rdst = {}
    for node in graph:
        rdst[node] = {}
    for node in graph:
        if not (node == root):
            for nbr in graph[node]:
                if graph[node][nbr] == 0:
                    rdst[node][nbr] = 0
                    break
    return rdst
```

**Rubric:**
- Proper docstring and helpful comments: 1 point (YES) / 0 points (NO)
- Test cases passed: None=0, >0% and <=50%=1, >50% and <100%=2, All=3 points

---

### Problem 2.6: `compute_cycle(graph)` [8 pts]

**Solution:**
```python
def compute_cycle(graph):
    visited = set()
    for node in graph:
        path = []
        while (node not in path) & (node not in visited):
            visited.add(node)
            if node not in graph:
                break
            if not graph[node]:
                break
            path.append(node)
            node = list(graph[node].keys())[0]
        if node in path:
            ind = path.index(node)
            cycle = path[ind:]
            return tuple(cycle)
    return None
```

**Rubric:**
- Proper docstring and helpful comments: 1 point (YES) / 0 points (NO)
- Test cases passed: None=0, >0% and <=25%=1, >25% and <50%=2, >50% and <75%=3, >75% and <100%=4, All=7 points

---

### Problem 2.7: `contract_cycle(graph, cycle)` [9 pts]

**Solution:**
```python
def contract_cycle(graph, cycle):
    orig_nodes = list(graph.keys())
    cstar = max(orig_nodes) + 1
    graph[cstar] = {}

    for node in orig_nodes:
        if node not in cycle:
            min_weight = float("inf")
            for nbr in graph[node]:
                if nbr in cycle:
                    if graph[node][nbr] < min_weight:
                        min_weight = graph[node][nbr]
            if not (min_weight == float("inf")):
                graph[node][cstar] = min_weight
        else:
            for nbr in graph[node]:
                if nbr not in cycle:
                    if nbr not in graph[cstar]:
                        graph[cstar][nbr] = graph[node][nbr]
                    else:
                        if graph[node][nbr] < graph[cstar][nbr]:
                            graph[cstar][nbr] = graph[node][nbr]

    for node in cycle:
        del graph[node]
        for nbr in graph:
            if node in graph[nbr]:
                del graph[nbr][node]
    return (graph, cstar)
```

**Rubric:**
- Proper docstring and helpful comments: 1 point (YES) / 0 points (NO)
- Test cases passed: None=0, >0% and <=20%=1, >20% and <=40%=2, >40% and <60%=3, >60% and <80%=4, >75% and <100%=5, All=8 points

---

### Problem 2.8: `expand_graph(original_graph, rdst_candidate, cycle, cstar)` [8 pts]

**Solution:**
```python
def expand_graph(original_graph, rdst_candidate, cycle, cstar):
    expanded_graph = {}
    enter_cycle_weight = float("inf")

    for node in rdst_candidate:
        if node in original_graph:
            expanded_graph[node] = {}
            for nbr in rdst_candidate[node]:
                if nbr in original_graph:
                    expanded_graph[node][nbr] = original_graph[node][nbr]
            if cstar in rdst_candidate[node]:
                for nbr in cycle:
                    if (nbr in original_graph[node]) and (original_graph[node][nbr] < enter_cycle_weight):
                        enter_cycle_weight = original_graph[node][nbr]
                        enter_cycle_tail = node
                        enter_cycle_head = nbr

    for i in range(0, len(cycle)):
        expanded_graph[cycle[i]] = {}
    for i in range(-1, len(cycle)-1):
        if not cycle[i] == enter_cycle_head:
            expanded_graph[cycle[i+1]] = {cycle[i]: original_graph[cycle[i+1]][cycle[i]]}
    expanded_graph[enter_cycle_tail][enter_cycle_head] = enter_cycle_weight

    for node in original_graph:
        if node not in cycle:
            if node in rdst_candidate[cstar]:
                for nbr in cycle:
                    if (node in original_graph[nbr]):
                        if (original_graph[nbr][node] == rdst_candidate[cstar][node]):
                            expanded_graph[nbr][node] = original_graph[nbr][node]
                            break
    return expanded_graph
```

**Rubric:**
- Proper docstring and helpful comments: 1 point (YES) / 0 points (NO)
- Test cases passed: None=0, >0% and <=20%=1, >20% and <=40%=2, >40% and <60%=3, >60% and <80%=4, >75% and <100%=5, All=7 points

---

## Part 3: Bacterial Infection Transmission [25 pts]

### Problem 3.1: `compute_genetic_distance(seq1, seq2)` [6 pts]

**Solution:**
```python
def compute_genetic_distance(seq1, seq2):
    if len(seq1) != len(seq2):
        print("The two sequences are not the same length")
        return
    score = 0
    for i in range(len(seq1)):
        if seq1[i] != seq2[i]:
            score += 1
    return score
```

**Rubric:**
- Proper docstring: 1 point (YES) / 0 points (NO)
- Handles sequences of different lengths properly: YES=2, checks but no proper message=1, NO=0 points
- Correctness: Works correctly=3, mostly correct with boundary issues=1, very few cases=1, doesn't work=0 points

---

### Problem 3.2: `construct_complete_weighted_digraph(gen_data, epi_data)` [8 pts]

**Solution:**
```python
def construct_complete_weighted_digraph(gen_data, epi_data):
    sequences = provided.read_patient_sequences(gen_data)
    gen_g = provided.compute_pairwise_gen_distances(sequences, compute_genetic_distance)
    epi_g = provided.read_patient_traces(epi_data)

    # Get the maximum epidemiological weight
    max_e = -1 * float("inf")
    for i in epi_g:
        for j in epi_g[i]:
            if epi_g[i][j] > max_e:
                max_e = epi_g[i][j]

    # Combine the two graphs
    tot_g = {}
    for patient_u in gen_g:
        tot_g[patient_u] = {}
        for patient_v in gen_g[patient_u]:
            tot_g[patient_u][patient_v] = gen_g[patient_u][patient_v] + (
                (999.0 * (epi_g[patient_u][patient_v] / max_e)) / 10**5
            )
    return tot_g
```

**Rubric:**
- Proper docstring: 1 point (YES) / 0 points (NO)
- Correctly calls `read_patient_sequences`, `compute_pairwise_gen_distances`, and `read_patient_traces`: All three=2, one or two=1, none=0 points
- Correctly computes max(E): Yes=2, minor issues=1, no/major issues=0 points
- Correctly combines distances using formula and generates complete weighted digraph: Yes=3, mostly correct=2, major problems=1, doesn't work=0 points

---

### Problem 3.3: Data Analysis [10 pts]

**Solution:**

The RDMST rooted at Patient 1 has the following structure:
```
                    1
                 /  |  \
                8   3   4
               /   / \    \
              2   5   9   10
                        /    \
                       6     12
                      / \    / \
                    17   7  18  13
                         |
                        14
                      / | \
                    16  11  15
```

**RDMST weight:** 43.04995 (accept any value between 43.03 and 43.08 due to floating point issues)

**Interpretation:** Three independent transmission chains initiated from Patient 1:
- Patient 1 -> Patient 8 -> Patient 2
- Patient 1 -> Patient 3 -> Patients 5, 9
- Patient 1 -> Patient 4 -> Patient 10 -> cascading to most other patients

**Uniqueness:** The RDMST is **not unique** -- a quick inspection of the original graph's edge weights shows that alternative RDMSTs with the same total weight exist.

**Rubric:**
- Code for analyzing data included: 1 point (YES) / 0 points (NO)
- RDMST figure: Properly drawn and correct=3, minor issues=2, major issues=1, missing or completely wrong=0 points
- RDMST weight (43.03-43.08): Correct=2, makes sense for student's RDST=1, missing or completely wrong=0 points
- Reasonable explanation of transmission spread: Yes=2, minimal explanation=1, No=0 points
- States that RDMST is not unique: Yes=2, No=0 points

---

## Point Summary

| Section | Problem | Points |
|---------|---------|--------|
| Part 1 | 1. Min-max pair sum | 10 |
| Part 1 | 2. Unique MST proof | 6 |
| Part 1 | 3. Dijkstra negative weights | 4 |
| Part 2 | 1. Prove Lemma 1 | 8 |
| Part 2 | 2. Prove Lemma 2 | 6 |
| Part 2 | 3. reverse_digraph_representation | 4 |
| Part 2 | 4. modify_edge_weights | 4 |
| Part 2 | 5. compute_rdst_candidate | 4 |
| Part 2 | 6. compute_cycle | 8 |
| Part 2 | 7. contract_cycle | 9 |
| Part 2 | 8. expand_graph | 8 |
| Part 2 | (Subtotal) | (55) |
| Part 3 | 1. compute_genetic_distance | 6 |
| Part 3 | 2. construct_complete_weighted_digraph | 8 |
| Part 3 | 3. Data analysis | 10 |
| Part 3 | (Subtotal) | (25) |
| **Total** | | **100** |
