# COMP 215 Homework 1: Prime Factorization

## File to Edit

`skeleton_code/src/main/rice/PrimeFactorizer.java` - Your implementation (EDIT THIS FILE)

## Task

Implement a `PrimeFactorizer` class that computes prime factorizations of integers.

1. Read the file above to see the method stubs
2. Implement the constructor and `computePrimeFactorization` method
3. Run the grade tool to test
4. Fix failures until all tests pass

## Requirements

### Constructor: `PrimeFactorizer(int maxNumToFactorize)`

Initialize the factorizer to handle numbers up to `maxNumToFactorize`.
Pre-compute primes up to this maximum for efficiency.

### Method: `int[] computePrimeFactorization(int numToFactorize)`

Return an array containing the prime factors in ascending order.
Include repeated factors (e.g., 12 = [2, 2, 3]).

**Edge Cases:**
- Negative numbers, 0, 1: Return `null`
- Numbers exceeding maxNumToFactorize: Return `null`

## Grading

28 points across 14 tests

---
*COMP 215: Program Design, Rice University*