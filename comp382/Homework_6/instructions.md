# COMP 382: Reasoning about Algorithms
## Homework 6

**Total Points: 100** (Each problem is worth 20 points)

---

## Problem 1: True or False [20 points]

Say whether the following claims are true or false. **Justify** each of your answers with either a proof or a counterexample.

### Part (a)
Any flow graph G has a maximum flow that does not include a cycle in which each edge has positive flow.

### Part (b)
If all edges in a graph have distinct capacities, there is a unique maximum flow.

### Part (c)
If we multiply all edge capacities in a flow network by a positive number λ, then the minimum cut stays unchanged.

### Part (d)
If we add a positive number λ to all edge capacities, then the minimum cut stays unchanged.

---

## Problem 2: Flow Reduction [20 points]

You are given a flow network with unit-capacity edges: It consists of a directed graph G = (V, E), a source s ∈ V, and a sink t ∈ V; and capacity cₑ = 1 for every e ∈ E. You are also given a parameter k.

The goal is to delete k edges so as to reduce the maximum s-t flow in G by as much as possible. In other words, you should find a set of edges F ⊆ E so that |F| = k and the maximum s-t flow in G' = (V, E \ F) is as small as possible.

Give a polynomial-time algorithm (k cannot occur in the exponent of the complexity term) to solve this problem.

---

## Problem 3: Node Capacities [20 points]

In a standard s-t Maximum Flow problem, we assume edges have capacities, and there is no limit on how much flow is allowed to pass through a node. In this problem, we consider the variant of the MaxFlow problem with **node capacities**.

Let G = (V, E) be a directed graph, with source s ∈ V, sink t ∈ V, and a non-negative node capacity cᵥ for each node. Given a flow f in this graph, the flow through a node v is defined as f^in(v). We say that a flow is feasible if it satisfies the usual flow-conservation constraints and the node-capacity constraints f^in(v) ≤ cᵥ for all nodes.

Give a polynomial-time algorithm to find an s − t maximum flow in such a node-capacitated network.

---

## Problem 4: Station Closing [20 points]

Suppose you are in charge of a rail network connecting a large number of towns. Recently, several cases of a contagious disease have been reported in a town A. Your goal is to close down certain railway stations to prevent the disease from spreading to your hometown B. No trains can pass through a closed station.

To minimize expense, you want to close down as few stations as possible. However, you cannot close the station A, as this would expose you to the disease, and you cannot close the station B, because then you couldn't visit your favorite pub.

Describe and analyze an efficient algorithm, based either on max-flow or min-cut, to find the minimum number of stations that must be closed to block all rail travel from A to B.

The rail network is represented by an undirected graph, with a vertex for each station and an edge for each rail connection between two stations. Two special vertices represent the stations in A and B.

---

## Problem 5: Exam Scheduling [20 points]

Consider the following problem of scheduling exams at a university. The university offers n different classes. There are r different rooms on campus and t different time slots in which exams can be offered.

You are given two arrays E[1...n] and S[1...r], where:
- E[i] is the number of students enrolled in the i-th class
- S[j] is the number of seats in the j-th room

At most one final exam can be held in each room during each time slot. Class i can hold its final exam in room j only if E[i] < S[j].

Give an efficient max-flow or min-cut based algorithm to assign a room and a time slot to each class (or report correctly that no such assignment is possible).

State and briefly explain the complexity of your algorithm.
