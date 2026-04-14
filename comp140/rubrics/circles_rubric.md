---
total_points: 5
---

# Module 1: Circles - Written Questions Rubric

## Question 1 (5 points)

**Question:** "The strategy we followed to calculate the center and radius of the circle is not the most efficient. We used a strategy that you would use on paper in order to introduce you to Python and computational problem solving. However, there are better ways for a computer to solve this problem. Describe (in English) a method for doing so. Your description can be brief (2 or 3 sentences), just give the idea, you do not need to work out all of the math and Python."

**Expected Answer:**

A good answer should describe one of the following alternative approaches:

1. **Direct Formula Method**: Use the general equation of a circle (x - h)^2 + (y - k)^2 = r^2, substitute the three points to get three equations, and solve the system of equations directly for h, k, and r. This is more efficient because it requires fewer intermediate calculations.

2. **Matrix/Linear Algebra Method**: Set up the circle equation in expanded form (x^2 + y^2 + Dx + Ey + F = 0), substitute the three points to create a system of linear equations, solve using matrix operations (or Cramer's rule), then derive center and radius from D, E, F.

3. **Circumcenter Formula**: Use the determinant-based formula for the circumcenter of a triangle, which computes the center directly from the three points' coordinates in one formula.

**Key Elements for Full Credit:**
- Recognizes that a direct mathematical approach exists
- Describes solving a system of equations OR using a direct formula
- Explains why this is more efficient (fewer steps, no intermediate constructions)

**Rubric:**

- **Full credit (5 pts)**: Describes a valid alternative method (direct equations, matrix method, or circumcenter formula) with a clear explanation of why it's more efficient for a computer. Shows understanding that we can skip the geometric construction entirely.

- **Partial credit (3-4 pts)**: Describes a reasonable alternative approach but explanation is incomplete or unclear. May correctly identify "solve equations directly" without fully explaining how. May describe a valid method but not explain the efficiency advantage.

- **Minimal credit (1-2 pts)**: Shows some understanding that alternatives exist. May mention "equations" or "formulas" without a clear method. May suggest a different but still inefficient approach.

- **No credit (0 pts)**: No answer, or answer describes the same paper-based approach, or describes an incorrect/impossible method, or completely misunderstands the question.
