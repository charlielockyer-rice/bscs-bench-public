# Circles Writeup Solutions

## 4.A. Recipe

The recipe is almost identical to the provided "solution strategy":

**Inputs:** `point0`, `point1`, `point2`, each of which is an (x, y) coordinate pair in the two-dimensional (x-y) plane.

1. Find the slope of the line between `point0` and `point1` by invoking the `slope` function with the arguments (x coordinate of `point0`, y coordinate of `point0`, x coordinate of `point1`, y coordinate of `point1`); assign this value to `slope01`.

2. Find the midpoint of the line between `point0` and `point1` by using the `midpoint` function with the arguments (x coordinate of `point0`, y coordinate of `point0`, x coordinate of `point1`, y coordinate of `point1`); assign this value to `midpoint01`.

3. Find the slope of the line perpendicular to the line between `point0` and `point1` by calling the `perp` function with `slope01` as its argument; assign this value to `perp01`.

4. Repeat steps 1-3 for `point1` and `point2` (assigning the results to `slope12`, `midpoint12`, and `perp12`).

5. Find the intersection of the two lines through the midpoints found in step 2 with the perpendicular slopes found in step 3 by invoking the `intersect` function with the arguments (`perp01`, x coordinate of `midpoint01`, y coordinate of `midpoint01`, `perp12`, x coordinate of `midpoint12`, y coordinate of `midpoint12`). Assign this value to `center`.

6. Find the distance between `center` and `point0` by calling the `distance` function with the arguments (x coordinate of `point0`, y coordinate of `point0`, x coordinate of `center`, y coordinate of `center`); assign this value to `radius`.

7. Return `center` and `radius`.

*Note that this is just one possible way of expressing this recipe.*

---

## 5. Discussion

Given the equation for a circle and three points, you can create a system of equations that will allow you to find the center and radius. So, you can directly compute the center and the radius of the circle as a function of the three points, without following the process you would use to find the center and radius on a piece of paper. While more mathematically complex, this is a more direct and efficient method of finding the answer to this problem.

Note that the point of this assignment was not to get you to find the most computationally efficient solution, but rather to introduce you to Python and the computational thinking process.
