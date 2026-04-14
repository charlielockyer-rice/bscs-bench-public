---
total_points: 5
---

# Module 2: Spot It! - Written Questions Rubric

## Question 1 (5 points)

**Question:** "If you use a prime modulus of 5, you will generate a valid deck of 31 'Spot it!' cards, each with 6 images on them. If you use a prime modulus of 7, you will generate a valid deck of 57 'Spot it!' cards, each with 8 images on them. Suppose you wanted to create a valid deck of 40 'Spot it!' cards. Is this possible? If not, why not? If so, how would you go about doing so?"

**Expected Answer:**

A complete answer should explain that creating exactly 40 valid Spot It! cards is **not possible** using projective geometry.

**Key Points:**

1. **The constraint**: Using projective geometry over a finite field Z_m (where m must be prime), the number of lines (cards) is always m^2 + m + 1.

2. **Checking possible primes**:
   - m = 5: 5^2 + 5 + 1 = 31 cards
   - m = 6: Not valid (6 is not prime)
   - m = 7: 7^2 + 7 + 1 = 57 cards

3. **No prime between 5 and 7**: Since 6 is not prime, there's no projective plane that would give us a number of cards between 31 and 57.

4. **40 is not of the form m^2 + m + 1** for any prime m:
   - Solving m^2 + m + 1 = 40 gives m^2 + m - 39 = 0
   - Using quadratic formula: m = (-1 + sqrt(157))/2 ≈ 5.77
   - This is not an integer, so no projective plane gives exactly 40 cards

**Alternative Partial Solutions** (should mention but explain limitations):
- Could take a subset of 40 cards from the 57-card deck, but would lose the property that every pair shares exactly one image
- Could create a non-standard deck using different mathematical structures, but this would be complex and beyond the scope of the assignment

**Rubric:**

- **Full credit (5 pts)**: Correctly explains that 40 cards is not possible using projective geometry. Demonstrates understanding that the number of cards is constrained by the formula m^2 + m + 1 where m must be prime. Shows that no prime m gives exactly 40 cards (either by checking primes 5 and 7, or by solving the equation).

- **Partial credit (3-4 pts)**: Correctly identifies that 40 is not possible but explanation is incomplete. May mention that the modulus must be prime without fully explaining the formula constraint. May correctly state that we'd need a value between 5 and 7 which doesn't exist as a prime.

- **Minimal credit (1-2 pts)**: Shows some understanding of the constraints but reasoning is unclear or contains errors. May incorrectly conclude it's possible or impossible without proper justification. May mention "need a prime" without connecting to the card count.

- **No credit (0 pts)**: No answer, or answer shows no understanding of the projective geometry constraints. Claims it's easily possible without addressing the mathematical limitations. Completely incorrect reasoning.
