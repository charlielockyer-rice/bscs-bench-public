# Module 1: Circle Through Three Points

The goal of this assignment is to practice with the computational thinking process:
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

In this assignment, you will solve the following problem: given three points, calculate the center and radius of a circle that passes through all three points.

## 2. A Solution Strategy

Given that we understand the problem that we are trying to solve, we need to develop a solution strategy. In this assignment, we will use a strategy that follows how you would solve this problem on a piece of paper:
1. Connect a pair of points with a line.
2. Find the midpoint of that line.
3. Draw a new line that is perpendicular to the original line and passes through the midpoint.
4. Repeat steps 1 through 3 for another pair of points.
5. The point at which the two perpendicular lines intersect is the center of the circle.
6. The distance from the center to any of the three original points is the radius.

Graphically, this process looks as follows:

Here, the given points are the three points in black. The lines from step 1 are drawn in black. The lines from step 3 are drawn in blue. The center of the circle is the point in red, with a red line indicating its radius.

## 3. Breaking Down the Problem

There are three types of data in this problem: points, lines, and circles. We need to choose how to represent these types. As our knowledge of Python is limited at this point, we have very few options, so we will do the following:
1. A point is two numbers: an x coordinate and a y coordinate.
2. A line is three numbers (representing the line in point-slope form): a slope, an x coordinate and a y coordinate.
3. A circle is three numbers: an x and y coordinate for the center and a radius.

Given these representations, write the following five functions. You should start with the provided template (`solution.py` in your workspace).

Note that to return multiple values from a function, you can have multiple values separated by commas, such as: `return 1, 2, 3`

Similarly, when you call a function that returns multiple values, you would separate the variables you want to assign those values to with commas: `var1, var2, var3 = func_returns_3()`

### A. distance

This function should take two points as input and return the distance between them.

Mathematically, this can be expressed as follows:

$d = \sqrt{(x_1 - x_2)^2 + (y_1 - y_2)^2}$

Where the input points are $(x_1, y_1)$ and $(x_2, y_2)$ and $d$ is the distance.

### B. midpoint

This function should take two points as input and return the point that is the midpoint on a line between them.

Mathematically, this can be expressed as follows:

$x = x_1 + \frac{x_2 - x_1}{2}$

$y = y_1 + \frac{y_2 - y_1}{2}$

Where the input points are $(x_1, y_1)$ and $(x_2, y_2)$ and $(x, y)$ is the midpoint.

### C. slope

This function should take two points and return the slope of the line connecting them.

Mathematically, this can be expressed as follows:

$s = \frac{y_2 - y_1}{x_2 - x_1}$

Where the input points are $(x_1, y_1)$ and $(x_2, y_2)$ and $s$ is the slope.

### D. perp

This function should take the slope of a line and return the slope of a perpendicular line.

Mathematically, this can be expressed as follows:

$p = \frac{-1}{s}$

Where $s$ is the input slope and $p$ is the slope of a perpendicular line.

### E. intersect

This function should take two lines and return the point where they intersect.

Mathematically, this can be expressed as follows:

$x = \frac{(s_1 \times x_1) - (s_2 \times x_2) + (y_2 - y_1)}{s_1 - s_2}$

$y = s_1 \times (x - x_1) + y_1$

Where $(x_1, y_1)$ and $s_1$ are one line in point-slope form, $(x_2, y_2)$ and $s_2$ are another line in point-slope form, and $(x, y)$ is the intersection point.

You can (and should) derive these equations (or similar ones) for yourself.

## 4. Finding the Circle

Before you start this problem, you should test your code for the above functions. Do not begin this part until your code from part 3 passes all of the tests.

### A. Recipe

Describe the recipe for computing the center and radius of a circle given three points. Keep in mind the five functions you wrote in part 3. You must refer (by name) to these functions here!

### B. Code

Implement your recipe in Python. Implement the `make_circle` function which takes the coordinates of three points and returns the center (x, y) and radius of the circle passing through them.

## 5. Discussion (Written Response)

The strategy we followed to calculate the center and radius of the circle is not the most efficient. We used a strategy that you would use on paper in order to introduce you to Python and computational problem solving. However, there are better ways for a computer to solve this problem. Describe (*in English*) a method for doing so. Your description can be brief (2 or 3 sentences), just give the idea, you do not need to work out all of the math and Python.

Write your response in `writeup.md`.

---
*COMP 140: Computational Thinking, Rice University*
