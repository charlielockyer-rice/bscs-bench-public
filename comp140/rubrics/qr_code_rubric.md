---
total_points: 10
---

# Module 5: QR Codes - Written Questions Rubric

## Question 1 (5 points)

**Question:** "If you are given a message that you want to encode and a value of k, which indicates how many error correction bytes you need, is it possible to guarantee that you will not have any coefficients that are equal to zero in the remainder from dividing the message polynomial by the generator polynomial? If there were coefficients that are equal to zero in the encoded data, would it be a problem? Why or why not?"

**Expected Answer:**

**Part 1 - Can we guarantee no zero coefficients?**
No, we cannot guarantee that there will be no zero coefficients in the remainder.

Reasoning:
- The remainder depends on both the message content and the generator polynomial
- Different messages will produce different remainders
- Some messages will inevitably produce remainders with zero coefficients
- We have no control over what the division produces

**Part 2 - Is it a problem?**
No, zero coefficients are NOT a problem.

Reasoning:
- A zero coefficient simply means that power of x doesn't appear in the polynomial
- The Reed-Solomon decoder knows the positions of all coefficients (including zeros)
- During decoding, the algorithm uses the structure of the code, not just non-zero values
- Zero is a valid element of Z_256, just like any other value 0-255
- The error correction still works because the mathematical relationship between message and remainder is preserved

Alternative considerations:
- In the actual QR code encoding, zeros might need special handling for visual patterns
- But mathematically, zero coefficients in the remainder don't affect error correction capability

**Rubric:**

- **Full credit (5 pts)**: Correctly states that we cannot guarantee no zeros in the remainder. Explains that zeros are not a problem because they're valid elements of Z_256 and the decoding process handles them correctly. Shows understanding of how Reed-Solomon works.

- **Partial credit (3-4 pts)**: Gets one part correct but not both. May correctly state that we can't guarantee no zeros but give an incomplete explanation of why zeros aren't a problem. Or may correctly explain why zeros aren't problematic without addressing the guarantee question.

- **Minimal credit (1-2 pts)**: Shows some understanding but answer is confused or partially incorrect. May incorrectly claim we can guarantee no zeros, or incorrectly claim zeros would be a problem.

- **No credit (0 pts)**: No answer or both parts are incorrect.

---

## Question 2 (5 points)

**Question:** "We have discussed the importance of modularity and writing your recipes/code in such a way that you can reuse them. If you needed a Polynomial class to represent polynomials with regular, real-number coefficients (as opposed to coefficients that are elements of Z_256), how could you minimally change the code you have already written in order to reuse it for this purpose?"

**Expected Answer:**

The key insight is that the Polynomial class should be modified to **parameterize the arithmetic operations**.

Good approaches include:

1. **Replace z256 module calls with standard arithmetic**: Change all uses of `z256.add()`, `z256.sub()`, `z256.mul()`, `z256.div()` to standard Python `+`, `-`, `*`, `/` operations. This could be done by:
   - Creating a parameter or configuration option that selects which arithmetic to use
   - Creating a separate "arithmetic module" that can be swapped (dependency injection)
   - Using a base class with abstract arithmetic methods, then subclassing for Z_256 and real numbers

2. **Abstract the arithmetic**: Create an interface or module that defines `add`, `sub`, `mul`, `div` operations. Then create two implementations - one for Z_256 (the existing z256 module) and one for real numbers (using standard operations).

3. **Pass arithmetic functions as parameters**: The Polynomial class constructor could accept functions for addition, subtraction, multiplication, and division. Different functions would be passed for different number systems.

**Key points for full credit:**
- Recognizes that the arithmetic operations are what differs
- Proposes abstracting or parameterizing those operations
- Mentions that the polynomial logic (adding terms, polynomial multiplication, etc.) can stay the same
- Understands this is a case for code reuse through abstraction

**Rubric:**

- **Full credit (5 pts)**: Identifies that the z256 module calls need to be replaced or abstracted. Proposes a clean solution involving parameterization, dependency injection, or abstraction. Shows understanding that the polynomial logic itself doesn't change, only the underlying arithmetic.

- **Partial credit (3-4 pts)**: Identifies that z256 operations need to change but solution is incomplete or less elegant. May suggest simply replacing z256 calls with standard operations without a reusable approach. May understand the concept but explain it unclearly.

- **Minimal credit (1-2 pts)**: Shows some understanding that changes are needed but doesn't identify the arithmetic operations as the key difference. May suggest rewriting much of the class unnecessarily.

- **No credit (0 pts)**: No answer or suggests changes that don't address the actual difference between Z_256 and real number arithmetic. Suggests rewriting the entire class from scratch.
