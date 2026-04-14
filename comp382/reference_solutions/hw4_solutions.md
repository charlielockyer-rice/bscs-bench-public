# COMP 382 Homework 4 - Solutions

---

## Problem 1: Hashing with Chaining [20 Points]

**Problem:** Select a key uniformly at random from n keys stored in a hash table of size m with chaining, in expected time O(L · (1 + 1/α)), where L is the longest chain length and α = n/m.

**Solution:**

**Algorithm:**
1. Choose one of the m slots in the hash table uniformly at random
2. Let n_j denote the number of elements at slot j
3. Pick a number x from 1 to L uniformly at random
4. If x ≤ n_j, return the x-th element on the list
5. Otherwise, repeat from step 1

**Correctness:** Any element in the hash table will be selected with probability 1/(mL), so we return any key with equal probability.

**Analysis:**

Let X be the random variable counting the number of attempts before we stop, and let p be the probability of returning on a given attempt.

Since we'd expect to traverse 1 + α steps to reach an element on the list (and we know chain lengths):
$$E[X] = p(1+\alpha) + (1-p)(1+E[X])$$

Solving: E[X] = α + 1/p

The probability of picking any particular element is n/(mL) = α/L, so:
$$E[X] = \alpha + \frac{L}{\alpha} = L\left(\frac{\alpha}{L} + \frac{1}{\alpha}\right) = O(L(1 + 1/\alpha))$$

since α ≤ L.

---

## Problem 2: Longest-Probe Bound for Hashing [40 Points]

### Part (a)

**Claim:** For i = 1, 2, ..., n, the probability that the i-th insertion requires strictly more than k probes is at most 2^(-k).

**Proof:**

The index for each probe is computed uniformly from among all possible indices. Since n ≤ m/2, at least half of the indices are empty at any stage.

For more than k probes to be required, each of the first k probes must hit an occupied slot. The probability of hitting an occupied slot on any probe is at most 1/2.

Therefore, the probability of requiring more than k probes is:
$$< \left(\frac{1}{2}\right)^k = 2^{-k}$$

### Part (b)

**Claim:** For i = 1, 2, ..., n, the probability that the i-th insertion requires more than 2log₂n probes is O(1/n²).

**Proof:**

Using part (a) with k = 2log₂n:
$$\Pr[X_i > 2\log_2 n] < 2^{-2\log_2 n} = 2^{\log_2 n^{-2}} = n^{-2} = \frac{1}{n^2}$$

### Part (c)

**Claim:** Pr[X > 2log₂n] = O(1/n), where X = max_{1≤i≤n} X_i.

**Proof:**

By union bound:
$$\Pr[X > 2\log_2 n] = \Pr[\exists i : X_i > 2\log_2 n]$$
$$\leq \sum_{i=1}^{n} \Pr[X_i > 2\log_2 n]$$
$$\leq \sum_{i=1}^{n} \frac{1}{n^2} = \frac{n}{n^2} = \frac{1}{n}$$

### Part (d)

**Claim:** E[X] = O(log₂n).

**Proof:**

The longest possible probe sequence is n (trying every entry). We know from part (c) that Pr[X > 2log₂n] ≤ 1/n.

$$E[X] \leq \Pr[X \leq 2\log_2 n] \cdot 2\log_2 n + \Pr[X > 2\log_2 n] \cdot n$$
$$= \left(1 - \frac{1}{n}\right) \cdot 2\log_2 n + \frac{1}{n} \cdot n$$
$$= 2\log_2 n + 1 - \frac{2\log_2 n}{n}$$
$$= O(\log_2 n)$$

---

## Problem 3: Randomized Coloring [15 Points]

**Problem:** Given graph G = (V, E), find a 3-coloring that satisfies at least (2/3)c* edges in expectation, where c* is the maximum number of satisfiable edges.

**Solution:**

**Upper bound:** c* ≤ m, where m = |E|.

**Algorithm:** Color every node independently with one of three colors, each with probability 1/3.

**Analysis:**

Define random variable for each edge e:
$$X_e = \begin{cases} 1, & \text{if edge } e \text{ is satisfied} \\ 0, & \text{otherwise} \end{cases}$$

For any edge e, there are 9 ways to color its two endpoints (3 × 3). Each has probability 1/9. Of these, 3 are not satisfying (both endpoints same color).

Therefore:
$$E[X_e] = \Pr[\text{e is satisfied}] = \frac{6}{9} = \frac{2}{3}$$

Let Y be the total number of satisfied edges. By linearity of expectation:
$$E[Y] = E\left[\sum_{e \in E} X_e\right] = \sum_{e \in E} E[X_e] = \frac{2}{3}m \geq \frac{2}{3}c^*$$

---

## Problem 4: Analysis of Selection Using Recurrence [25 Points]

**Problem:** Find the smallest integer constant c for which T(n) ≤ cn, where T(n) is the expected number of comparisons in randomized SELECT.

**Solution:**

**Recurrence:**
$$T(n) = (n-1) + \frac{1}{n}\left(\sum_{i=1}^{k-1} T(n-i) + \sum_{i=k+1}^{n} T(i-1)\right)$$

$$= (n-1) + \frac{1}{n}\left(\sum_{i=n-k+1}^{n-1} T(i) + \sum_{i=k}^{n-1} T(i)\right)$$

**Claim:** T(n) ≤ cn for some constant c.

**Proof by induction:**

**Base case:** T(1) = 0 (no comparisons needed) ≤ c·1 ✓

**Inductive step:** For n > 1, assuming T(i) ≤ ci for all i < n:

$$T(n) \leq (n-1) + \frac{1}{n}\left(\sum_{i=n-k+1}^{n-1} ci + \sum_{i=k}^{n-1} ci\right)$$

$$= (n-1) + \frac{c}{n}\left(\sum_{i=n-k+1}^{n-1} i + \sum_{i=k}^{n-1} i\right)$$

$$= (n-1) + \frac{c}{n}\left(\frac{(k-1)(2n-k)}{2} + \frac{(n-k)(n+k-1)}{2}\right)$$

$$= (n-1) + \frac{c}{2n}((k-1)(2n-k) + (n-k)(n+k-1))$$

$$\leq (n-1) + \frac{c}{2n}(k(2n-k) + (n-k)(n+k))$$

The expression in the second parenthesis is:
$$-2k^2 + 2kn + n^2$$

This is maximized when k = n/2 (taking derivative and setting to 0).

Substituting k = n/2:
$$T(n) \leq (n-1) + \frac{c}{2n}\left(\frac{n}{2} \cdot \frac{3n}{2} + \frac{n}{2} \cdot \frac{3n}{2}\right)$$

$$= (n-1) + \frac{c}{2n} \cdot 2 \cdot \frac{n}{2} \cdot \frac{3n}{2}$$

$$= (n-1) + c \cdot \frac{3n}{4}$$

For T(n) ≤ cn, we need:
$$(n-1) + \frac{3cn}{4} \leq cn$$

$$n - 1 \leq \frac{cn}{4}$$

$$4(n-1) \leq cn$$

Choosing **c = 4** works:
$$T(n) \leq (n-1) + 3n = 4n - 1 < 4n = cn$$

**Answer: c = 4**
