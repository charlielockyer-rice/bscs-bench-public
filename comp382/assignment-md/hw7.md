Reasoning about Algorithms &emsp; November 27, 2022

Rice University &emsp; COMP 382, Fall 2022

Michael Burke &emsp; Konstantinos Mamouras &emsp; Homework 7

---

# Homework 7 – Due 11:59pm, December 2, 2022

We will use $\preceq_P$ to denote a polynomial time mapping reduction.

1. (25 points) **(Path Selection)** (30 points) Suppose you are managing a communication network, modeled by a directed graph $G = (V, E)$. There are $c$ users who want to use the network. User $i$ (we have $1 \le i \le c$) issues a request to reserve a specific path $P_i$ in $G$ on which to transmit data.

   You want to accept as many path requests as possible, except if you accept $P_i$ and $P_j$, then $P_i$ and $P_j$ cannot share any nodes.

   The *Path Selection* problem formalizes this intent. It asks: Given a directed graph $G = (V, E)$, a set of path requests $P_1, \ldots, P_c$, and a number $k$, is it possible to select at least $k$ of the paths so that no two of the selected paths share any nodes?

   Show that Independent Set $\preceq_P$ Path Selection. Prove that your reduction is correct.

2. (25 points) **(Half 3-CNF Satisfiability)** Given a 3-CNF formula $\varphi$ with $n$ variables and $m$ clauses, where $m$ is even. We wish to determine whether there is a truth assignment to the variables of $\varphi$ such that exactly half the clauses evaluate to true and half the clauses evaluate to false.

   Show that 3-SAT $\preceq_P$ Half 3-CNF Satisfiability. Prove that your reduction is correct.
