# COMP 382: Reasoning about Algorithms
## Homework 5

**Total Points: 100** (Each problem is worth 20 points)

---

## Problem 1: Greedy Trucking [20 points]

Consider a shipping company that sends a number of trucks each day between Houston and Austin. Trucks have a fixed weight W on the maximum amount of weight they are allowed to carry. Boxes arrive at the Houston station one by one, and each package has a weight wᵢ. The trucking station is quite small, so only one truck can be at the station at a given time. Company policy requires that the boxes are shipped in the order in which they are received.

Consider the greedy algorithm that packs the boxes in the order that they arrive; whenever the next box does not fit, the algorithm sends the truck on its way.

**Prove** that, for a given set of boxes with specified weights, the greedy algorithm minimizes the number of trucks that are needed.

---

## Problem 2: Smallest Set of Intervals Containing Points [20 points]

Describe a greedy algorithm that, given a set {x₁, x₂, ..., xₙ} of points on the real line, determines the smallest set of unit-length closed intervals that contains all of the given points.

**Prove** that your algorithm is correct.

---

## Problem 3: All Spanning Trees [20 points]

Kruskal's algorithm can return different spanning trees for the same input graph G, depending on how it breaks ties in edge costs when the edges are sorted.

**Prove** that for each minimum spanning tree T of G, there is a way to sort the edges of G in Kruskal's algorithm so that the algorithm returns T.

---

## Problem 4: Oscillating Subsequence [20 points]

Call a sequence X[1...n] of numbers *oscillating* if X[i] < X[i + 1] for all even i, and X[i] > X[i + 1] for all odd i.

Describe a greedy algorithm to compute the length of the longest oscillating subsequence of an arbitrary array A of integers.

**Note:** A subsequence of an array A is a sequence ⟨a₁, ..., aₖ⟩ of elements of A that appear in the same order as in A. A subsequence need not be a contiguous subarray. For example, ⟨1, 7, 3⟩ is a subsequence of the array [5, 1, 9, 7, 23, 30, 3].

---

## Problem 5: Coin Changing [20 points]

Consider the problem of making change for n cents using the smallest number of coins. Assume that each coin's value is an integer.

### Part (a)
Describe a greedy algorithm to make change when available coins are in denominations that are powers of c: the denominations are c⁰, c¹, c², ..., cᵏ for some integers c > 1 and k ≥ 1.

**Show** that the greedy algorithm always yields an optimal solution.

### Part (b)
Give a set of coin denominations for which the greedy algorithm does not yield an optimal solution. Your set should include a penny so that there is a solution for every value of n.
