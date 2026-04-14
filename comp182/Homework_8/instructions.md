# COMP 182: Algorithmic Thinking
## Homework 8: Recurrences and Dynamic Programming

---

## 1. Modeling with Recurrences [50 pts]

### Problem 1.1 [20 pts]

We would like to cover a rectangular area by two kinds of tiles: 1 x 1 and 1 x 2 tiles (the 1 x 2 tiles can be rotated). Give a recurrence and boundary conditions for the number of ways to cover the area with these two tiles if the area is:

1. 1 x n
2. 2 x n

Assume you have enough tiles of each kind. Show your work.

---

### Problem 1.2 [10 pts]

Consider graph G = (V, E) where V = {1, 2, 3, 4, 5} and E = {{1,2}, {1,3}, {1,4}, {1,5}, {2,3}, {3,4}, {4,5}, {5,2}}.

Give a recurrence and the boundary conditions for the number of paths of length n that start at node 1 in this graph. Show your work.

---

### Problem 1.3 [20 pts]

Consider the following set for n in N (natural numbers):

F = {f : {1, ..., n} -> {0, 1, 2} : for all 1 <= i <= n-1, f(i) + f(i+1) != 4}

(a) Give a recurrence and boundary conditions for |F|.

(b) Solve the recurrence and give an explicit formula for |F|.

Show your work in both parts.

---

## 2. Recurrences and Dynamic Programming [50 pts]

### Problem 2.1 [25 pts]

We are given a color picture consisting of an m x n matrix A[1..m, 1..n] of pixels, where each pixel specifies a triple of red, green, and blue (RGB) intensities. Suppose that we wish to compress this picture slightly. Specifically, we wish to remove one pixel from each of the m rows, so that the whole picture becomes one pixel narrower. To avoid disturbing visual effects, however, we require that the pixels removed in two adjacent rows be in the same or adjacent columns; the pixels removed form a "seam" from the top row to the bottom row where successive pixels in the seam are adjacent vertically or diagonally.

(a) Show that the number of such possible seams grows at least exponentially in m, assuming that n > 1.

(b) Suppose now that along with each pixel A[i,j], we have calculated a real-valued disruption measure d[i,j], indicating how disruptive it would be to remove pixel A[i,j]. Intuitively, the lower a pixel's disruption measure, the more similar the pixel is to its neighbors. Suppose further that we define the disruption measure of a seam to be the sum of the disruption measures of its pixels. Give a polynomial-time dynamic programming algorithm to find a seam with the lowest disruption measure. First derive and show the recurrence and boundary conditions. How efficient is your algorithm?

---

### Problem 2.2 [25 pts]

A certain string-processing language allows a programmer to break a string into two pieces. Because this operation copies the string, it costs n time units to break a string of n characters into two pieces. Suppose a programmer wants to break a string into many pieces. The order in which the breaks occur can affect the total amount of time used.

For example, suppose that the programmer wants to break a 20-character string after characters 2, 8, and 10 (numbering the characters in ascending order from the left-hand end, starting from 1).

- If she programs the breaks to occur in left-to-right order, then the first break costs 20 time units, the second break costs 18 time units (breaking the string from characters 3 to 20 at character 8), and the third break costs 12 time units, totaling 50 time units.
- If she programs the breaks to occur in right-to-left order, however, then the first break costs 20 time units, the second break costs 10 time units, and the third break costs 8 time units, totaling 38 time units.
- In yet another order, she could break first at 8 (costing 20), then break the left piece at 2 (costing 8), and finally the right piece at 10 (costing 12), for a total cost of 40.

Give a polynomial-time dynamic programming algorithm that, given the numbers of characters after which to break, determines a least-cost way to sequence those breaks. More formally, given a string S with n characters and an array L[1..m] containing the break points, compute the lowest cost for a sequence of breaks, along with a sequence of breaks that achieves this cost.

First derive and show the recurrence and boundary conditions. How efficient is your algorithm?
