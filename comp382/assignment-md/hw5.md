# COMP 382: Reasoning about Algorithms
# Homework 5

Konstantinos Mamouras &emsp; Michael Burke

Due on November 6, 2022

(released on October 23, 2022)

## 1 Hashing with Chaining [20 Points]

Suppose that we have stored $n$ keys in a hash table of size $m$, with collisions resolved by chaining. We know the length of each chain, including the length $L$ of the longest chain. Describe a procedure that selects a key uniformly at random from among the keys in the hash table and returns it in expected time $O(L \cdot (1 + 1/\alpha))$.

## 2 Longest-Probe Bound for Hashing [40 Points]

Suppose you are using an open-addressed hash table of size $m$ to store $n \le m/2$ items.

(a) Assuming independent uniform permutation hashing, show that for $i = 1, 2, \ldots, n$, the probability is at most $2^{-k}$ that the $i$-th insertion requires strictly more than $k$ probes.

(b) Show that for $i = 1, 2, \ldots, n$, the probability is $O(1/n^2)$ that the $i$-th insertion requires more than $2 \log_2 n$ probes.

Let the random variable $X_i$ denote the number of probes required by the $i$-th insertion. You have shown in part (b) that $\Pr[X_i > 2 \log_2 n] = O(1/n^2)$. Let the random variable $X = \max_{1 \le i \le n} X_i$ denote the maximum number of probes required by any of the $n$ insertions.

(c) Show that $\Pr[X > 2 \log_2 n] = O(1/n)$.

(d) Show that the expected length $\mathbb{E}[X]$ of the longest probe sequence is $O(\log_2 n)$.

## 3 Randomized Coloring [15 Points]

We are given an undirected graph $G = (V, E)$ and we want to color each node with one of three colors, even if we aren't necessarily able to give different colors to every pair of adjacent nodes. Instead, we say that an edge $(u, v)$ is *satisfied* if the colors assigned to $u$ and $v$ are different, and seek to maximize the number of satisfied edges.

Consider a 3-coloring that maximizes the number of satisfied edges, and let $c^*$ denote this number. Give a polynomial-time randomized algorithm for our problem where the *expected* number of satisfied edges is at least $\frac{2}{3}c^*$.

## 4 Analysis of Selection Using Recurrence [25 Points]

Consider the randomized variant of selection shown in Algorithm 1, which selects the element with rank $k$ in an unsorted array $A[1..n]$. Assume that the array elements have distinct values. The function Partition($A[1..n]$, $i$) partitions an array $A$ into three parts by comparing the pivot element $A[i]$ to every other element, using $(n - 1)$ comparisons, and returns the new index of the pivot element. The elements that are smaller (resp., larger) than the pivot are placed to the left (resp., right) of the pivot. The function Random($1..n$) selects a number from the set $\{1, \ldots, n\}$ uniformly at random.

---

**Algorithm 1:** Select($A[1..n]$, $k$)

**Input:** array of integers $A[1..n]$ and integer $k \in \{1, \ldots, n\}$

**Output:** Element of rank $k$

1. $i \leftarrow$ Random($1..n$) &emsp; // select $i$ from $\{1, \ldots, n\}$ uniformly at random
2. $r \leftarrow$ Partition($A[1..n]$, $i$) &emsp; // re-arrange elements using $A[i]$ as pivot
3. **if** $k < r$ **then**
4. &emsp; **return** Select($A[1..(r-1)]$, $k$)
5. **else if** $k > r$ **then**
6. &emsp; **return** Select($A[(r+1)..n]$, $k - r$)
7. **else**
8. &emsp; **return** $A[k]$

---

Let $T(n)$ be the expected number of comparisons that Select performs. Find the smallest integer constant $c$ you can for which $T(n) \le cn$ for every $n$.

*Hint:* You can follow the recurrence approach that was discussed in class.
