# COMP 382: Reasoning about Algorithms
# Homework 3

Konstantinos Mamouras &emsp; Michael Burke

Due on October 12, 2022

(released on September 26, 2022)

## 1 String Merging [15 points]

Let $X$, $Y$, and $Z$ be three strings, where $|X| = m$, $|Y| = n$, and $|Z| = m + n$. We say that $Z$ *merges* $X$ and $Y$ iff $Z$ can be formed by interleaving the characters from $X$ and $Y$ in a way that maintains the left-to-right ordering of the characters from each string. For example, both *abucvwx* and *uavwbxc* merge the strings *abc* and *uvwx*.

(1) [5 points] Give an efficient dynamic programming algorithm that determines whether $Z$ merges $X$ and $Y$. What is the time complexity of your algorithm?

(2) [10 points] We will now generalize the problem so that the input includes a number $K$ (in addition to the strings $X$, $Y$, $Z$). $K$ is the maximum number of errors (substitutions) that we can allow during the merging. That is, the computational problem is to determine if there exists a string $Z'$ (of length $m + n$) such that (i) $Z'$ merges $X$ and $Y$, and (ii) $Z$, $Z'$ differ at a maximum of $K$ positions$^1$. Give an efficient dynamic programming algorithm for this problem. What is the time complexity of your algorithm?

## 2 Edit Distance & LCS [35 Points]

We will consider a generalization of the concept of edit distance, where the cost of an insertion, deletion and substitution is $c_i$, $c_d$ and $c_s$ respectively. Each parameter can be chosen to be any positive extended real number, i.e., an element of $\{x \in \mathbb{R} \mid x > 0\} \cup \{\infty\}$. Keep in mind the following ***properties of*** $\infty$:

$$\infty + \infty = \infty, \quad x + \infty = \infty, \quad \text{and} \quad x < \infty$$

for every $x \in \mathbb{R}$.

(1) [3 points] Provide pseudocode for the procedure EditDistance($X$, $Y$, $c_i$, $c_d$, $c_s$), where $X[1..m]$ and $Y[1..n]$ are strings and $c_i$, $c_d$, $c_s$ are the edit operation costs. This procedure should return the edit distance from $X$ to $Y$, which we denote by $D(X, Y)$. Recall that

$$D(X, Y) = \min\{\text{cost}(W) \mid \text{edit sequence } W \text{ for } (X, Y)\},$$

as we defined in the lectures.

(2) [12 points] Prove that $D(xa, ya) = D(x, y)$ for all strings $x$, $y$ and every letter $a$. [*Note:* This is not a trivial claim. You should justify it with a careful proof using the definition of edit distance.]

(3) [20 points] Let $L(x, y)$ be the length of the longest common subsequence of strings $x$, $y$. We know from CLRS (page 396) that $L$ is given by the following recursive definition:

$$L(x, \varepsilon) = 0$$
$$L(\varepsilon, y) = 0$$
$$L(xa, ya) = L(x, y) + 1$$
$$L(xa, yb) = \max(L(xa, y), L(x, yb)) \text{ when } a \ne b$$

for all strings $x$, $y$ and letters $a$, $b$.

For strings $X$ and $Y$, show how $L(X, Y)$ can be computed in one line of code using only ***one invocation*** of the procedure EditDistance($X$, $Y$, $c_i$, $c_d$, $c_s$). Justify the correctness of your approach with a careful proof.

## 3 Inventory Planning [20 Points]

The Rinky Dink Company makes machines that resurface ice rinks. The demand for such products varies from month to month, and so the company needs to develop a strategy to plan its manufacturing given the fluctuating, but predictable, demand. The company wishes to design a plan for the next $n$ months. For each month $i$, the company knows the demand $d_i$, that is, the number of machines that it will sell. Let $D = \sum_{i=1}^{n} d_i$ be the total demand over the next $n$ months. The company keeps a full-time staff who provide labor to manufacture up to $m$ machines per month. If the company needs to make more than $m$ machines in a given month, it can hire additional, part-time labor, at a cost that works out to $c$ dollars per machine. Furthermore, if, at the end of a month, the company is holding any unsold machines, it must pay inventory costs. The cost for holding $j$ machines is given as a function $h(j)$ for $j = 1, 2, \ldots, D$, where $h(j) \ge 0$ for $1 \le j \le D$ and $h(j) \le h(j + 1)$ for $1 \le j \le D - 1$.

Give an algorithm that calculates a plan for the company that minimizes its costs while fulfilling all the demand. The running time should be polynomial in $n$ and $D$.

## 4 Optimal Matching of Sequences [30 Points]

For an integer $n \ge 1$, we define $[n] = \{1, \ldots, n\}$. Suppose we are given as input sequences (i.e., arrays) of integers $X = [x_1, \ldots, x_m]$ and $Y = [y_1, \ldots, y_n]$. A *matching* for $(X, Y)$ is a subset $M \subseteq [m] \times [n]$ (containing pairs of indexes) that satisfies the following properties:

(i) (**Left covered**) For every index $i \in [m]$ there is some $j \in [n]$ such that $(i, j) \in M$.

(ii) (**Right covered**) For every index $j \in [n]$ there is some $i \in [m]$ such that $(i, j) \in M$.

(iii) (**No crossing**) There are no indexes $i, i' \in [m]$ and $j, j' \in [n]$ with $i < i'$ and $j < j'$ such that $(i, j') \in M$ and $(i', j) \in M$.

The *cost* of a matching $M$ for $(X, Y)$ is defined as follows:

$$\text{cost}(M) = \sum_{(i,j) \in M} (x_i - y_j)^2.$$

We are interested in finding an optimal matching, that is, a matching with minimal cost.

(1) [3 points] Prove that every matching contains the pairs $(1, 1)$ and $(m, n)$.

(2) [7 points] Let $f(m, n)$ be the number of all possible matchings for $(X, Y)$, where $X = [x_1, \ldots, x_m]$ and $Y = [y_1, \ldots, y_n]$. Give a recursive definition for the function $f$ and carefully explain how you obtained it.

(3) [20 points] Design an algorithm that computes a matching with minimal cost. Explain why it is correct and discuss its time and space complexity.

---

$^1$This is the same as saying the the Hamming distance between $Z$ and $Z'$ is at most $K$. The Hamming distance between two strings $A$ and $B$ of the same length $n$ is defined as

$$\text{Ham}(A, B) = |\{i \in \{1, \ldots, n\} \mid A[i] \ne B[i]\}|.$$

In other words, $\text{Ham}(A, B)$ is the number of positions at which $A$ and $B$ differ.
