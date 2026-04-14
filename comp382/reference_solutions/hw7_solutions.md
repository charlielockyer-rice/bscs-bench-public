# COMP 382 Homework 7 - Solutions

---

## Problem 1: Efficient Recruiting

**Goal:** Show that Vertex Cover ≤ₚ Efficient Recruiting

**Reduction:**

Given a graph G = (V, E) and an integer k:
- Define a sport Sₑ for each edge e ∈ E
- Define a counselor Cᵥ for each vertex v ∈ V
- Cᵥ is qualified in sport Sₑ if and only if e has an endpoint equal to v

**Proof of correctness:**

(⇒) If there are k counselors that, together, are qualified in all sports, the corresponding vertices in G have the property that each edge has an endpoint in at least one of them; so they define a vertex cover of size k.

(⇐) Conversely, if there is a vertex cover of size k, then this set of counselors has the property that each sport is contained in the list of qualifications of at least one of them.

Thus, G has a vertex cover of size at most k if and only if the instance of EFFICIENT RECRUITING can be solved with at most k counselors.

The instance of EFFICIENT RECRUITING has size polynomial in the size of G (|sports| = |E|, |counselors| = |V|).

---

## Problem 2: Plot Fulfillment

**Goal:** Show that Hitting Set ≤ₚ Plot Fulfillment

**Reduction:**

Consider an instance of HITTING-SET with a set A = {a₁, ..., aₙ}, subsets S₁, ..., Sₘ, and a bound k.

Construct the following instance of PLOT-FULFILLMENT:

**Graph nodes:**
- s (start), t (end)
- {vᵢⱼ : 1 ≤ i ≤ k, 1 ≤ j ≤ n}

**Edges:**
- Edge from s to each v₁ⱼ (1 ≤ j ≤ n)
- Edge from each vₖⱼ to t (1 ≤ j ≤ n)
- Edge from vᵢⱼ to vᵢ₊₁,ₗ for each 1 ≤ i ≤ k − 1 and 1 ≤ j, l ≤ n

This creates a layered graph where nodes vᵢⱼ belong to layer i, and edges go between consecutive layers. Intuitively, the nodes vᵢⱼ, for fixed j, represent the element aⱼ ∈ A.

**Thematic element sets:**
Tₗ = {vₗⱼ : aⱼ ∈ Sₗ, 1 ≤ i ≤ k}

**Proof of correctness:**

(⇒) Suppose there is a valid path P in PLOT-FULFILLMENT. Let H = {aⱼ : vᵢⱼ ∈ P for some i}. Note that H has at most k elements. For each l, there is some vᵢⱼ ∈ P that belongs to Tₗ, and the corresponding aⱼ belongs to Sₗ. Thus, H is a hitting set.

(⇐) Suppose there is a hitting set H = {aⱼ₁, ..., aⱼₖ}. Define the path {s, v₁,ⱼ₁, v₂,ⱼ₂, ..., vₖ,ⱼₖ, t}. For each l, some aⱼᵧ lies in Sₗ, and the corresponding node vᵧ,ⱼᵧ meets the set Tₗ. Thus P is a valid solution.

---

## Problem 3: Path Selection

**Goal:** Show that Independent Set ≤ₚ Path Selection

**Reduction:**

Convert an arbitrary instance (G, k) of INDEPENDENT SET into an instance of PATH SELECTION:

**New graph G':**
- One vertex uₑ corresponding to each edge e in G
- G' is the complete directed graph (directed edge from every node to every other node)

**Paths:**
For each vertex v in G with incident edges e₁, ..., eₖ (in arbitrary order), create path:
Pᵥ = e₁.e₂....eₖ

Note: This is a valid path because G' is complete.

**Proof of correctness:**

For vertices v₁ and v₂ in G:
- (v₁, v₂) is an edge in G ⟺ Pᵥ₁ and Pᵥ₂ have a common vertex

This is because if (v₁, v₂) is an edge e, then both paths Pᵥ₁ and Pᵥ₂ must pass through the node uₑ in G'.

Therefore: One can select k non-overlapping paths in the new instance ⟺ G has an independent set of size ≥ k.

**Complexity:** Construction of G' and paths Pᵥ is polynomial time.

---

## Problem 4: Half 3-SAT Satisfiability

**Goal:** Show that 3-SAT ≤ₚ Half 3-SAT Satisfiability

**Reduction:**

Let φ be any 3-SAT formula with m clauses and input variables x₁, x₂, ..., xₙ.

Define:
- T = (y ∨ y ∨ ¬y) — a clause that is TRUE when y = 0
- F = (y ∨ y ∨ y) — a clause that is TRUE when y = 1

Construct:
φ' = φ ∧ T ∧ ... ∧ T ∧ F ∧ ... ∧ F

where there are m copies of T and 2m copies of F.

Then φ' has 4m clauses total and can be constructed from φ in polynomial time.

**Proof of correctness:**

(⇒) Suppose φ has a satisfying assignment. Set y = 0 and the xᵢ's to the satisfying assignment:
- All m clauses of φ are satisfied
- All m T clauses are satisfied (since y = 0)
- None of the 2m F clauses are satisfied (since y = 0)
- Total: exactly 2m = half of 4m clauses satisfied ✓

(⇐) Suppose there is no satisfying assignment to φ.

**Case y = 0:**
- The m T clauses are satisfied
- The 2m F clauses are all false
- At least one clause of φ is false
- Total satisfied: strictly less than 2m ✗

**Case y = 1:**
- The m T clauses are false
- The 2m F clauses are all satisfied
- At most m clauses of φ are satisfied
- Total satisfied: at most 3m, which is more than 2m ✗

Thus φ has a satisfying assignment if and only if φ' has an assignment which satisfies exactly half of its clauses.
