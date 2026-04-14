Reasoning about Algorithms &emsp; November 19, 2022

Rice University &emsp; COMP 382, Fall 2022

Michael Burke &emsp; Konstantinos Mamouras &emsp; Homework 6

---

# Homework 6 – Due 11:59pm, Wednesday, November 23, 2022

1. **(Flow with upper and lower limits)** Suppose that each edge $e = (u, v)$ in a flow network $G = (V, E)$ has not only an upper bound $c_e$ on the net flow from $u$ to $v$, but also a lower bound $b_e$. That is, any flow $f$ in the network must satisfy $b_e \le f(e) \le c_e$, for all edges $e$. It may be the case that for a network no such feasible flow exists.

   For a cut $(A, B)$, let us define $c(A, B) = \sum_{e=(u,v), u \in A, v \in B} c_e$, and $b(A, B) = \sum_{e=(u,v), u \in A, v \in B} b_e$.

   (a) Prove that if $f$ is a flow in the network, then the value $v(f)$ of the flow satisfies $v(f) \le c(A, B) - b(B, A)$ for any cut $(A, B)$ of $G$.

   (b) Prove that the value of a maximum flow in the network, if it exists, is the minimum value of $c(A, B) - b(B, A)$ over all cuts $(A, B)$ of the network.

2. **(Base stations)** Consider a set of mobile computing clients in a certain town who each need to be connected to one of several possible base stations. We'll suppose there are $n$ clients, with the position of each client specified by its $(x, y)$ coordinates in the plane. There are also $k$ base stations; the position of each of these is specified by $(x, y)$ coordinates as well. For each client, we wish to connect it to exactly one of the base stations. Our choice of connections is constrained in the following ways.

   There is a range parameter $r$ — a client can only be connected to a base station that is within distance $r$. There is also a load parameter $L$ — no more than $L$ clients can be connected to any single base station. Your goal is to design a polynomial-time algorithm for the following problem. Given the positions of a set of clients and a set of base stations, as well as the range and load parameters, decide whether every client can be connected simultaneously to a base station, subject to the range and load conditions in the previous paragraph.

3. **(Exam scheduling)** Consider the following problem of scheduling exams at a university. The university offers $n$ different classes. There are $r$ different rooms on campus and $t$ different time slots in which exams can be offered. You are given two arrays $E[1 \ldots n]$ and $S[1 \ldots r]$, where $E[i]$ is the number of students enrolled in the $i$-th class, and $S[j]$ is the number of seats in the $j$-th room. At most one final exam can be held in each room during each time slot. Class $i$ can hold its final exam in room $j$ only if $E[i] \le S[j]$.

   Give a max-flow or min-cut based algorithm to assign a room and a time slot to each class (or report correctly that no such assignment is possible). Perform a complexity analysis of the algorithm.

4. **(Flow reduction)** You are given a flow network with unit-capacity edges: It consists of a directed graph $G = (V, E)$, a source $s \in V$, and a sink $t \in V$; and capacity $c_e = 1$ for every $e \in E$. You are also given a parameter $k$.

   The goal is to delete $k$ edges so as to reduce the maximum $s$-$t$ flow in $G$ by as much as possible. In other words, you should find a set of edges $F \subseteq E$ so that $|F| = k$ and the maximum $s$-$t$ flow in $G' = (V, E \setminus F)$ is as small as possible.

   Give a polynomial-time algorithm ($k$ cannot occur in the exponent of the complexity term) to solve this problem.
