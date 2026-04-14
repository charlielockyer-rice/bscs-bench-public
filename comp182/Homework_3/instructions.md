# COMP 182: Algorithmic Thinking

## Homework 3: Graphs

---

## Problem 1: Understanding Graph-theoretic Definitions and Terminology [29 pts]

### 1.1 Degree Sequences [7 pts]

The *degree sequence* of a graph is the sequence of the degrees of the nodes of the graph in non-increasing order.

What is the degree sequence of:
- K_4?
- C_5?
- K_{m,n}?

### 1.2 Planar Graphs [4 pts]

Graph g is *planar* if it can be drawn on the plane such that no two edges intersect other than at their endpoints.

Which of the graphs K_2, K_3, K_4, and K_5 are planar? (Reminder: K_n is the complete graph on n nodes.)

Draw the planar ones to demonstrate your answer.

### 1.3 Course Prerequisites [9 pts]

Consider the following set of CS course prerequisites (COMP 140, MATH 111, MATH 211, and ELEC 220 have no prerequisites):

| Course   | Prerequisites            |
|----------|--------------------------|
| MATH 112 | MATH 111                 |
| COMP 182 | COMP 140, MATH 211       |
| COMP 215 | COMP 182                 |
| COMP 310 | COMP 215                 |
| STAT 310 | MATH 112, MATH 211       |
| COMP 311 | COMP 182                 |
| COMP 382 | COMP 182, STAT 310       |
| COMP 321 | ELEC 220, COMP 215       |
| COMP 410 | COMP 321, COMP 310, COMP 311 |

**(a)** Draw a precedence graph that models the courses and their prerequisites. Here, add edges for direct prerequisites only.

**(b)** Is the graph acyclic? Does it make sense for it to be cyclic? Explain.

**(c)** For the courses above, what is the smallest number of semesters it would take a student to take all of the courses in the table? Assume that all courses are offered in every semester. More generally, for an arbitrary precedence graph that captures course prerequisites, how would you compute the minimum number of semesters it would take a student to take all of the courses?

### 1.4 Matchings [9 pts]

A *matching* on graph g = (V, E) is a subset of g's edges such that no two edges have a node in common. A *perfect matching* is a matching where every node is incident with at least one edge in the matching. The *size* of a matching is the number of edges in it.

**(a)** Give an example of a graph with at least 5 nodes that has a matching of size 2.

**(b)** Give an example of a graph with at least 5 nodes that has no matching of size 2.

**(c)** What is the size of a perfect matching (if one exists) for a graph that has n nodes (assume n is even)?

---

## Problem 2: Graph-theoretic Problem Formulation [21 pts]

Formulate each of the problems as a graph-theoretic problem. Explicitly describe the input and output.

### 2.1 Jury Selection [7 pts]

Given a city with n individuals, we need to select m of them for jury duty such that no two of them know each other. Assume we have knowledge about the city and who knows whom.

### 2.2 Student Groups [7 pts]

Luay wants to divide the students in COMP 182 into as few groups as possible such that in each group every two students like each other (every student must belong to exactly one group). Assume that Luay knows who likes whom in the class.

### 2.3 Course Scheduling [7 pts]

The computer science department wants to schedule (i.e., assign time slots) the Fall 2023 courses so that no two courses that have at least one student in common are scheduled at overlapping times.

Assumptions:
1. The department knows which students will be taking each of the courses
2. There is a fixed set of k time slots available to schedule courses
3. The department wants to use as few slots as possible (to avoid conflict with other departments and to help with classroom scheduling)

---

## Problem 3: Proving Graph-theoretic Results [50 pts]

### 3.1 Bipartite Bit Vector Graph [10 pts]

We define B_n = (V, E) as the graph whose nodes correspond to all bit vectors of size n such that there is an edge between two nodes if their bit vectors differ in one bit.

For example, B_2 contains four nodes {00, 01, 10, 11} and 4 edges {{00, 01}, {00, 10}, {01, 11}, {10, 11}}.

**Prove that B_n is bipartite for an arbitrary n >= 2.**

### 3.2 Bridges and Even Degrees [10 pts]

Let g = (V, E) be a graph. Edge e in E is called a *bridge* if the graph g' = (V, E \ {e}) has more connected components than g.

**Prove that if the degrees of all nodes in g are even, then g does not contain a bridge.**

### 3.3 Edge Coloring and Hamiltonian Circuits [10 pts]

Let g = (V, E) be a graph. We say that f : E -> {1, 2, ..., k} is a *k-coloring of the edges* of g if for every two edges e_1 and e_2 that share a node in common we have f(e_1) != f(e_2).

A simple circuit in a graph g that passes through every node of the graph exactly once is called a *Hamiltonian circuit* (Definition 2 in Section 10.5 in the textbook).

**Prove that if the degree of each node in g is 3 and g has a Hamiltonian circuit, then there exists a 3-coloring of g's edges.**

### 3.4 Cartesian Product Graphs [20 pts]

An *Euler circuit* in a graph g is a simple circuit that contains every edge of g (Definition 1 in Section 10.5 in the textbook).

Let g_1 = (V_1, E_1) and g_2 = (V_2, E_2) be two connected graphs (reminder: no parallel edges or self loops). We define the *Cartesian Product graph* C = (V_C, E_C) of the two graphs as follows:

- V_C = V_1 x V_2
- E_C = {{(u, x), (v, y)} : {u, v} in E_1 OR {x, y} in E_2}

**Prove or disprove each of the following:**

**(a)** If g_1 and g_2 both contain Hamiltonian circuits, then C contains a Hamiltonian circuit.

**(b)** If g_1 and g_2 both contain Euler circuits, then C contains an Euler circuit.

*Hint: You might find Theorems 1 and 2 in Section 10.5 in the textbook helpful.*
