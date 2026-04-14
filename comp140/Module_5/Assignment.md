# Module 5: QR Codes

The goal of this assignment is to use the computational thinking process to help create a real world product. We will follow the process of:
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

In this assignment, you will generate error-correction information for QR codes. You will need to implement a method to generate a Reed-Solomon error-correction code for a block of text.

We have broken down the problem into manageable pieces. You should carefully focus on each subproblem, as they will combine nicely to solve the overall problem. This is one good example of where the computational thinking process we have been teaching throughout the course really starts to shine!

## 2. A Solution Strategy

First, make sure you understand the problem we are trying to solve. Once you do, we need to develop a solution strategy. We are going to use polynomial arithmetic to solve this problem.

Reed-Solomon encoding consists of a number of error correction bytes, derived from the data that's being encoded. These error correction bytes come from dividing a polynomial representing your data, by a polynomial known to both the encoder and decoder (the "generator" polynomial).

To make sure that each of the coefficients of this remainder fit in a byte, each of the coefficients of these polynomials are numbers in $\mathbb{Z}_{256}$; this means that we'll need to be careful to use $\mathbb{Z}_{256}$ arithmetic instead of normal arithmetic in our recipe.

The message polynomial, which is our numerator, will be constructed as follows:

$message = \sum\limits_{i = 0}^{n-1} m_ix^{k+n-i-1}$ where $M = m_0m_1\cdots m_{n-2}m_{n-1}$ are the $n$ bytes in our message, and $k$ is the number of error correction bytes required.

The generator polynomial, which is our denominator, will constructed as follows:

$generator = \prod\limits_{i = 0}^{k-1} (x - 2^i)$ where $k$ is the number of error correction bytes required.

Note that both $message$ and $generator$ have coefficients in $\mathbb{Z}_{256}$. In particular, we must compute $2^{i}$ in $\mathbb{Z}_{256}$ to make sure this holds.

Once we have these polynomials, we will use polynomial long division to compute $remainder$ from dividing the $message$ by the $generator$ ($\frac{message}{generator}$). From here, we can pull the coefficients out of $remainder$ to get our Reed-Solomon error correction data.

### Reed-Solomon Example

Here's a brief example of Reed-Solomon encoding:

For a message of bytes [32, 91, 11, 120, 209, 114, 220, 77] with k=2 error correction bytes:

1. Create message polynomial: $32x^9 + 91x^8 + 11x^7 + 120x^6 + 209x^5 + 114x^4 + 220x^3 + 77x^2$
2. Create generator polynomial: $(x - 1)(x - 2) = x^2 - 3x + 2$ (in $\mathbb{Z}_{256}$, note $-3 = 253$)
3. Divide and get remainder: The remainder coefficients are the error correction bytes

## 3. Breaking Down the Problem: Manipulating Individual Terms

The first sub-problem you will need to solve is to represent polynomials and polynomial math over $\mathbb{Z}_{256}$ in Python. We have designed a Polynomial API for this purpose, which is the API you will be implementing with your `Polynomial` class on this assignment.

We have provided a partial implementation of `Polynomial` for you to start with in the template (`solution.py` in your workspace). We have chosen to represent polynomials internally as a dictionary of terms. A simplified polynomial only has one coefficient for each term, $ax^n$, with coefficient $a$ and power $n$. So, the `terms` dictionary maps powers ($n$) to coefficients ($a$). For example, the polynomial $x^5 + 4x^2 + 5$ would be represented by the dictionary `{5: 1, 2: 4, 0: 5}`.

Further, we have provided you with implementations of several general "utility" methods (`get_coefficient`, `get_degree`, `get_terms`, the constructor method `__init__`, the string method `__str__`, and equality/inequality methods `__eq__`, `__ne__`).

You will need to implement several methods that operate on a single `Polynomial` object and a single term: `add_term`, `subtract_term`, and `multiply_by_term`. Implementing these methods will help you to later implement methods that operate on two `Polynomial` objects.

There are several things that you should keep in mind when implementing the following three methods:
- First, you should make sure to satisfy the Polynomial API (see the template docstrings), which specifies the exact inputs and outputs for each method.
- Second, remember that all of the coefficient arithmetic should use the provided z256 module (from `lib`). As an example of how to use this module, you can look at the implementation of the provided `divide_terms` function. We chose not to include this function within the `Polynomial` class, because rather than operating on a single `Polynomial` and a single term, it operates on two single terms.
- Last, recall that immutability has many benefits; thus, when implementing these methods, you should **not** mutate the `Polynomial` on which the method is called; rather, each of the following three methods should return a new `Polynomial` object reflecting the results of the operation.

### A. Add Term

Write a method on the `Polynomial` class, `add_term(self, coefficient, power)`, which adds a single input term $b_mx^m$ to `self`, a polynomial of the form $p = a_nx^n + \ldots + a_1x + a_0$. Your method should produce the polynomial $r = p + b_mx^m$, where the coefficients are added in $\mathbb{Z}_{256}$.

### B. Subtract Term

Write a method on the `Polynomial` class, `subtract_term(self, coefficient, power)`, which subtracts a single input term $b_mx^m$ from `self`, a polynomial of the form $p = a_nx^n + \ldots + a_1x + a_0$. This should produce the polynomial $r = p - b_mx^m$, where the coefficients are subtracted in $\mathbb{Z}_{256}$. *Before writing this method, think carefully about the properties of $\mathbb{Z}_{256}$ math! You should not be duplicating code that you have already written.*

### C. Multiply by Term

#### i. Recipe

Clearly describe a recipe for multiplying a polynomial $p = a_nx^n + \ldots + a_1x + a_0$ by a single term $b_mx^m$. Your recipe should produce the polynomial $r = p \times b_mx^m$, where the coefficients have been multiplied in $\mathbb{Z}_{256}$.

#### ii. Code

Write a method on the `Polynomial` class, `multiply_by_term(self, coefficient, power)`, which implements your recipe.

## 4. Breaking Down the Problem: Manipulating Polynomials

Now that we have some basic polynomial methods, including methods that deal with single terms, let's build upon the recipes and functions we've already created to manipulate whole polynomials at a time.

*Everything in this section will benefit from using the methods and recipes that have already been implemented for polynomials! If you don't find you are using what you've already developed, you should step back and rethink what you are doing.* If you choose to reuse any of the methods or functions introduced in part 3 (and you should!), you may simply refer to them by name rather than writing separate recipes for them. This applies to the provided ones (e.g. `get_coefficient` or `divide_terms`), as well as any that you implemented -- including those for which you were not required to write a recipe. As usual, however, if you choose to create any additional helper functions outside of the previously defined methods of the polynomial class, you must write separate recipes for these helper functions. Further, each recipe in this section builds upon the previous one; thus, you can also refer to these by name in subsequent parts.

You should follow these guidelines when implementing the following three methods:
- Make sure to satisfy the Polynomial API we have given you, which specifies the exact inputs and outputs for each method.
- Perform all of the coefficient arithmetic in $\mathbb{Z}_{256}$.
- Do not mutate the `Polynomial` on which the method is called; instead, return a new `Polynomial` object reflecting the results of the operation.

### A. Polynomial Addition

#### i. Recipe

Clearly describe a recipe for adding two polynomials $p = a_nx^n + \ldots + a_1x + a_0$ and $q = b_mx^m + \ldots + b_1x + b_0$ together. Your recipe should produce the polynomial $r = p + q$, where the coefficients are added in $\mathbb{Z}_{256}$. You should make use of the fact that you have already created a method for adding a single term to a polynomial.

#### ii. Code

Write a method on the `Polynomial` class, `add_polynomial(self, other_polynomial)`, which implements your recipe.

### B. Polynomial Subtraction

Write a method on the `Polynomial` class, `subtract_polynomial(self, other_polynomial)`, which subtracts an input polynomial `other_polynomial`, of the form $q = b_mx^m + \ldots + b_1x + b_0$, from `self`, a polynomial of the form $p = a_nx^n + \ldots + a_1x + a_0$. Your method should return the polynomial $r = p - q$.

This should look *very* similar to polynomial addition. You therefore do not need to write an additional recipe.

### C. Polynomial Multiplication

#### i. Recipe

Clearly describe a recipe for multiplying two polynomials $p = a_nx^n + \ldots + a_1x + a_0$ and $q = b_mx^m + \ldots + b_1x + b_0$ together. Your recipe should produce the polynomial $r = p \times q$, where the coefficients have been multiplied in $\mathbb{Z}_{256}$. You should make use of the fact that you have already written recipes to multiply a polynomial by a single term and to add two polynomials.

#### ii. Code

Write a method on the `Polynomial` class, `multiply_by_polynomial(self, other_polynomial)`, which implements your recipe.

### D. Polynomial Remainders

#### i. Recipe

Clearly describe a recipe for finding the remainder when dividing two polynomials $p = a_nx^n + \ldots + a_1x + a_0$ and $q = b_mx^m + \ldots + b_1x + b_0$, where $p$ is the numerator and $q$ is the denominator. Your recipe should produce the remainder polynomial, where the coefficients have been computed in $\mathbb{Z}_{256}$. You should make use of the fact that you were provided with methods for dividing terms, getting a polynomial's degree, and getting a polynomial's coefficient; further, you should make use of the fact that you have already written a recipe for multiplying two polynomials.

#### ii. Code

Write a method on the `Polynomial` class, `remainder(self, denominator)`, which implements your recipe.

## 5. Solving the Problem

At this point, you have a representation of polynomials with coefficients in $\mathbb{Z}_{256}$, and ways to manipulate them. You should test your code to confirm that everything from Parts 3 and 4 succeeds before continuing.

To finish our Reed-Solomon encoding, we're going to need to create the polynomials and divide them, as explained in section 2.

**As your work on these problems, you should refer to the hand-calculated example from section 2 for guidance. Note that you need to manipulate polynomials for this function: make sure you're using the `Polynomial` class you created above, and not solving the same problems all over again!**

### A. Creating the Message Polynomial

Write a function `create_message_polynomial(message, num_correction_bytes)` which implements the mathematical specification given in section 2 to create the message polynomial. This function should take $M$ and $k$, described above, as input. The message, $M$, is represented as a list of numbers (each between 0 and 255 inclusive). The number of error correction bytes to be generated, $k$, is an integer. The function should produce as output an instance of your `Polynomial` class representing the message polynomial described in section 2.

Note that the mathematical formula given in section 2 effectively *is* the recipe for this function!

### B. Creating the Generator Polynomial

Write a function `create_generator_polynomial(num_correction_bytes)` which implements the mathematical specification given in section 2 to create the generator polynomial. This function should take $k$, described above, as input. Again, the number of error correction bytes to be generated, $k$, is an integer. The function should produce as output an instance of your `Polynomial` class representing the generator polynomial described in section 2.

Again, note that the mathematical formula given in section 2 is the recipe for this function!

Make sure that you remember to do all of your coefficient arithmetic in $\mathbb{Z}_{256}$, making use of the z256 module from `lib`.

### C. Reed-Solomon Encoding

Write a function `reed_solomon_correction(message_data, num_correction_bytes)` that returns the remainder polynomial for the Reed-Solomon encoding for the input data `message_data` when we need `num_correction_bytes` error correction bytes. Given what you've already done, you should find that this function is fairly simple. If you are writing anything complex, step back and think about what you have already written in parts 4 and 5!

## 6. Discussion (Written Response)

1. If you are given a message that you want to encode and a value of $k$, which indicates how many error correction bytes you need, is it possible to guarantee that you will not have any coefficients that are equal to zero in the remainder from dividing the message polynomial by the generator polynomial? If there were coefficients that are equal to zero in the encoded data, would it be a problem? Why or why not?
2. We have discussed the importance of modularity and writing your recipes/code in such a way that you can reuse them. If you needed a `Polynomial` class to represent polynomials with regular, real-number coefficients (as opposed to coefficients that are elements of $\mathbb{Z}_{256}$), how could you minimally change the code you have already written in order to reuse it for this purpose?

Answer these questions by expressing your ideas in a few sentences of English. You do not need to prove anything mathematically or show any Python.

Write your response in `writeup.md`.

---
*COMP 140: Computational Thinking, Rice University*
