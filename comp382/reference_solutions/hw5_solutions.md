# COMP 382 Homework 5 - Solutions

---

## Problem 1: Greedy Trucking

**Solution:**

We prove the greedy algorithm uses the fewest possible trucks by showing that it "stays ahead" of any other solution. Specifically, we consider any other solution and show the following: if the greedy algorithm fits boxes b₁, ..., bⱼ into the first k trucks, and the other solution fits b₁, ..., bᵢ into the first k trucks, then i ≤ j.

Note that this implies the optimality of the greedy algorithm, by setting k to be the number of trucks used by the greedy algorithm.

We show this by induction over k:

**Base case (k = 1):** The greedy algorithm fits as many boxes as possible into the first truck, so this is clearly optimal.

**Inductive step:** Assume it holds for (k − 1): the greedy algorithm fits j' boxes into the first (k − 1), and the other solution fits i' ≤ j' boxes.

Now for the k-th truck, the alternate solution packs boxes bᵢ'₊₁, ..., bᵢ. Since j' ≥ i', the greedy algorithm is able at least to fit all the boxes bⱼ'₊₁, ..., bᵢ into the k-th truck, and it can potentially fit more.

This completes the induction step and the proof of the claim.

---

## Problem 2: Smallest Set of Intervals Containing Points

**Solution:**

**Algorithm:**
1. Sort the points in ascending order
2. Consider the leftmost uncovered point xₘᵢₙ
3. Create an interval [xₘᵢₙ, xₘᵢₙ + 1]
4. Remove all points covered by this interval
5. Repeat until all points are covered

**Proof of correctness:**

The leftmost interval must contain the leftmost point. It will do no good if it extends any further left than the leftmost point. So we know that its left-hand side is exactly the leftmost point.

We then remove any point that is within a unit distance of the leftmost point, since they are contained in this single interval. Then we repeat until all points are covered.

Since at each step there is a clearly optimal choice for where to put the leftmost interval (starting at the leftmost uncovered point), the final solution is optimal.

---

## Problem 3: All Spanning Trees

**Solution:**

Label the edges arbitrarily as e₁, ..., eₘ with the property that eₘ₋ₙ₊₁, ..., eₘ belong to T (the target MST).

Let δ be the minimum difference between any two non-equal edge weights. Subtract δ(i/m + 1) from the weight of edge eᵢ.

**Key observations:**
1. All edge weights are now distinct
2. The sorted order of the new weights is the same as some valid ordering of the original weights
3. Over all spanning trees of G, T is the one whose total weight has been reduced by the most

Thus, T is now the **unique** minimum spanning tree of G and will be returned by Kruskal's algorithm on this valid ordering.

---

## Problem 4: Oscillating Subsequence

**Solution:**

**Algorithm:**

We call a consecutive subarray a "run" if it is monotonically increasing or decreasing and any subarray containing this subarray is not monotonically increasing or decreasing.

1. Use linear time to find all the runs for this array
2. The start and end elements of each run form the longest alternating subsequence

**Proof:**

This can be proven by induction. At each local maximum or minimum (transition between runs), we must include that element to maximize the oscillation count. The greedy choice of taking all local extrema is optimal because:
- Skipping a local extremum cannot help us find a longer subsequence
- Including all extrema gives us the maximum number of direction changes

---

## Problem 5: Coin Changing

### Part (a)

**Algorithm:** Given denominations c⁰, c¹, c², ..., cᵏ, repeatedly select the largest coin that doesn't exceed the remaining amount.

**Proof of optimality:**

Given an optimal solution {x₀, x₁, ..., xₖ} where xᵢ indicates the number of coins of denomination cⁱ, we first show that we must have xᵢ < c for every i < k.

Suppose that we had some xᵢ ≥ c. Then we could decrease xᵢ by c and increase xᵢ₊₁ by 1. This collection of coins has the same value and has c − 1 fewer coins, so the original solution must have been non-optimal.

This configuration of coins is exactly the same as you would get if you kept greedily picking the largest coin possible. This is because to get a total value of V:
- xₖ = ⌊V·c⁻ᵏ⌋
- For i < k, xᵢ = ⌊(V mod cⁱ⁺¹)·c⁻ⁱ⌋

This is the only solution that satisfies the property that there aren't more than c of any denomination except possibly the largest, because the coin amounts are a base-c representation of V mod cᵏ.

### Part (b)

**Counterexample:** Let the coin denominations be {1, 3, 4}, and the value to make change for be 6.

- **Greedy solution:** {4, 1, 1} = 3 coins
- **Optimal solution:** {3, 3} = 2 coins

The greedy algorithm fails because 4 does not divide 6 evenly, and using 4 forces us to use inefficient small coins.
