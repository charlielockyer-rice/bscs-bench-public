---
total_points: 5
---

# Project 1: Factors - Written Questions Rubric

## Section: Testing Strategy (3 points)

**Question:** "Replace with a detailed description of your testing strategy."

**Expected Answer:**
A good testing strategy should cover:

1. **Normal cases:**
   - Small positive integers with multiple factors (e.g., 12 = 2^2 * 3)
   - Prime numbers (e.g., 7, 13, 23)
   - Composite numbers with many factors (e.g., 60 = 2^2 * 3 * 5)
   - Perfect powers (e.g., 8 = 2^3, 27 = 3^3)

2. **Edge cases:**
   - n = 1 (no prime factors)
   - n = 2 (smallest prime)
   - Large primes (testing efficiency)
   - Large numbers with many small factors (e.g., 2^20)

3. **Boundary cases:**
   - Maximum integer values (testing overflow)
   - Powers of 2 (single prime factor, multiple occurrences)

4. **Comparison testing:**
   - Verifying iterative and recursive implementations produce same results
   - Verifying count_factors and count_distinct agree on primes

**Rubric:**
- Full credit (3 pts): Describes systematic approach with specific examples covering normal cases, edge cases, and boundary conditions. Mentions testing both iterative and recursive implementations.
- Partial credit (2 pts): Lists several test categories but lacks specific examples or misses major categories (e.g., no edge cases).
- Partial credit (1 pt): Very brief or vague description (e.g., "tested with various numbers").
- No credit (0 pts): Missing or completely inadequate.

---

## Section: Something I Learned (2 points)

**Question:** "Replace with a description of one feature of C that you learned while completing this assignment that wasn't taught in lecture or lab."

**Expected Answer:**
Students should describe a specific C feature they discovered. Acceptable answers include (but are not limited to):

- Integer overflow behavior and how to detect/prevent it
- Division/modulo behavior with different integer types
- Function pointers (if used for comparing implementations)
- Static variables for iteration state
- Unsigned vs. signed integer semantics
- printf format specifiers for different integer types
- Compiler warnings and how to address them
- Make and Makefiles
- Debugging with gdb
- Memory layout of local variables on the stack

**Rubric:**
- Full credit (2 pts): Clearly identifies a specific C feature and demonstrates understanding of how it works or why it matters. Shows genuine learning beyond the assignment requirements.
- Partial credit (1 pt): Mentions a valid C feature but explanation is superficial or unclear.
- No credit (0 pts): Missing, or describes something that was explicitly taught in lecture/lab, or is not a C feature.
