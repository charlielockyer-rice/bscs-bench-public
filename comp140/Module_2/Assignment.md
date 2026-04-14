# Module 2: Spot It!

The goal of this assignment is to practice with more complex computational problems and to become comfortable with more of the Python programming language. We will follow the process of:
1. Reading and understanding the problem description.
2. Determining the inputs and outputs.
3. Decomposing the problem into subproblems, as appropriate.
4. Designing a computational recipe (algorithm) to solve the subproblems/problem.
5. Implementing your solution.

Be sure to read the entire assignment before beginning.

## Testing Your Solution

Use the `grade` tool to test your implementation:
```bash
bin/grade ./workspaces/<your_workspace>
```

Or if working in a workspace, simply use the grade tool provided by the agent harness.

---

## 1. The Problem

In this assignment, you will create a deck of cards for the [Spot it!](http://www.blueorangegames.com/spotit/) game. You will be able to use this deck to play a single-person variant of the game.

In a valid deck of cards for the game, every card must have the same number of images and every card must have one image, and only one image, in common with *every* other card in the deck.

As with the last module, hopefully, the problem is clear from the English language description. However, the solution strategy for this problem is likely to be much less obvious.

## 2. A Solution Strategy

First, make sure you understand the problem we are trying to solve. Once you do, we need to develop a solution strategy. We are going to use projective geometry to solve this problem.

Projective geometry has the following four properties:
1. A point is a triple of numbers $(x, y, z)$ that are not all zero. (The use of parentheses is just to distinguish between points and lines.)
2. A line is a triple of numbers, $[a, b, c]$ that are not all zero. (The use of brackets is just to distinguish between points and lines.)
3. If $k \ne 0$, then $(kx, ky, kz) = (x, y, z)$ and $[ka, kb, kc] = [a, b, c]$.
4. The point $(x, y, z)$ is on the line $[a, b, c]$ if and only if $ax + by + cz = 0$.

To actually use projective geometry to create a deck of cards for "Spot it!", we have to further restrict our numbers to be in a finite field, $\mathbb{Z}_m$. In order to construct a valid projective geometric plane, $m$ must be a prime number so that we can add, subtract, multiply, and divide numbers. Remember in a finite field, $\mathbb{Z}_m$, all mathematical operations must be performed modulo $m$.

Projective geometry also has an elegant symmetry in which there is a duality between points and lines. This means that you can interchange points and lines in any true theorem or statement about a projective geometric plane. See, for example, the duality of properties (1) and (2) above. Further, note that properties (3) and (4) remain valid if you interchange the points and lines.

## 3. Breaking Down the Problem

The ultimate objective is to create a projective plane in $\mathbb{Z}_m$ and convert that to a deck of cards. It is unreasonable to try to do this in one step.

The four mathematical conditions for projective geometry give us a clue as to the underlying operations we will need:
1. We need to be able to generate all points in a projective plane.
2. We need to be able to generate all lines in a projective plane. (Note that due to the duality of projective geometry, this is *exactly* the same as generating all points, so we can just use the same function to generate either points or lines!)
3. We need to be able to check if two points/lines are the same.
4. We need to be able to check if a point is on a line.

There are primarily two types of data in this problem: points and lines. We need to choose how to represent these types. As points and lines cannot be modified, it makes sense to represent them both using tuples with three elements. As they are the dual of each other, representing them both in the same way makes sense.

Given these representations, write the following functions. You should start your implementation with the provided template (`solution.py` in your workspace).

### A. Incidence

Write a function, named `incident`, to test if a point lies on a line. The function should take a point, a line, and a modulus and return `True` or `False`.

$incident(p, l, m) = \begin{cases}True & l_a p_x + l_b p_y + l_c p_z = 0 \mod{m}\\False & otherwise\end{cases}$

### B. Equivalence

Write a function, named `equivalent`, to test if two points (lines) are equivalent. The function should take two points and a modulus and return `True` or `False`.

Mathematically, this can be expressed as follows:

$equivalent(p, q, m) = \begin{cases}True & (p_x, p_y, p_z) = (k q_x, k q_y, k q_z) \mod{m}\\False & otherwise\end{cases}$

This is the simplest way of expressing this function, as it directly follows the definition of equivalence in projective geometry. However, it is actually deceptively difficult to implement it this way because some of the coordinates of both points can be 0. That makes it difficult to divide the elements of the two points (using multiplicative inverses) and see if the results are all the same ($k$).

Instead, we can use the *cross product*. The cross product can be interpreted as the line that connects two points. The cross product is defined as follows:

$p \times q = [p_y q_z - p_z q_y,~~ p_z q_x - p_x q_z,~~ p_x q_y - p_y q_x]$

This yields the line $[a, b, c]$. If the result is all zero, then there is no line, so they must be the same point!

Given the definition of the cross product, the `equivalent` function can be expressed mathematically as follows:

$equivalent(p, q, m) = \begin{cases}True & p \times q = [0, 0, 0] \mod{m}\\False & otherwise\end{cases}$

Write the function `equivalent`. The function should take two points and a modulus and return `True` or `False`.

### C. Generating all points

#### i. Recipe

Generating all of the points in a projective plane is a multi-step process. Clearly describe the recipe for computing all of the unique points in the projective plane given a prime modulus.

You should describe this recipe in clear, concise *English*. Do not use any Python! You can use bulleted (or enumerated) lists for clarity, if you desire.

If you need to break down the recipe into multiple functions, clearly describe the recipe for each one. Don't forget that you've already been asked to write two functions that might be useful, as well! If you need to use `incident` or `equivalent`, you can just use them; you do not need to further describe the recipe for either.

#### ii. Code

Implement your recipe in Python in a function named `generate_all_points`. This function should take a modulus as input and return a list of points. Each point should be a tuple with 3 elements. You may write and use any helper functions that are appropriate. Your implementation should follow the recipe you gave in part (i).

## 4. Solving the Problem

At this point, you have a working implementation of a projective geometric plane. Test your implementation before proceeding. If you do not pass all tests, you should go back to the previous problem and continue until you successfully pass the tests.

The key insight here is that each card in the deck is simply a line in the projective geometric plane and each image on the card is a point on that line. We do not want to label images on the "Spot it!" cards with their $(x, y, z)$ points. Instead, we just want to use consecutive integers starting at 0. It does not matter which point is assigned to which integer, so you can just use the index of the point in the list generated by `generate_all_points`.

### A. Recipe

Clearly describe the recipe for converting points in a projective geometric plane into cards for a "Spot it!" game. You will need both the points and the prime modulus. Keep in mind the functions you wrote in part 3. You can make use of those functions here (and do not need to describe how they work, as you have already done so)!

### B. Code

Write a function `create_cards`. This function should take a list of points (as created by `generate_all_points`), a list of lines, and a modulus. It should output a list of cards. Each card should be a list of numbers representing the images that will be on that card. Your implementation should follow the recipe you gave in part (A).

## 5. Discussion (Written Response)

If you use a prime modulus of 5, you will generate a valid deck of 31 "Spot it!" cards, each with 6 images on them. If you use a prime modulus of 7, you will generate a valid deck of 57 "Spot it!" cards, each with 8 images on them.

Suppose you wanted to create a valid deck of 40 "Spot it!" cards. Is this possible? If not, why not? If so, how would you go about doing so?

Answer this question by expressing your ideas in a few sentences of *English*. You do not need to prove anything mathematically or show any Python.

Write your response in `writeup.md`.

---
*COMP 140: Computational Thinking, Rice University*
