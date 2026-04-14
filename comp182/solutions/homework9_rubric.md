# COMP 182: Algorithmic Thinking
## Homework 9: Discrete Probability - Solutions and Rubric

---

## 1. Events and Their Probabilities [55 pts]

### Problem 1 [24 pts] - Permutation Probabilities

**(a)** The first 13 letters of the permutation are in alphabetical order.

**Solution:**
The way to think about it is that exactly one of each 13-letter permutation is in alphabetical order. There are P(26, 13) 13-letter permutations that can be placed at the beginning of a permutation of the 26 letters. There are C(26, 13) unique combinations of 13 letters. Therefore, the probability that the first 13 letters of the permutation are in alphabetical order is:

**C(26, 13) / P(26, 13) = 1/13!**

---

**(b)** *a* is the first letter of the permutation and *z* is the last letter.

**Solution:**
There are (26 - 2)! = 24! permutations of the 26 letters where *a* is the first letter and *z* is the last. There is a total of 26! permutations. Therefore, the probability of the desired event is:

**24!/26! = 1/(25 * 26)**

---

**(c)** *a* and *z* are next to each other in the permutation.

**Solution:**
The number of permutations in which *a* and *z* are next to each other (think about it as treating *a* and *z* as a single letter that can be written in two different ways, *az* and *za*) is 2 * 25!. There is a total of 26! permutations. Therefore, the probability of the desired event is:

**(2 * 25!)/26! = 2/26 = 1/13**

---

**(d)** *a* and *b* are not next to each other in the permutation.

**Solution:**
As we showed in the previous part, the probability that *a* and *b* are next to each other is 1/13. Therefore, the probability that they are *not* next to each other is:

**1 - (1/13) = 12/13**

---

**(e)** *a* and *z* are separated by at least 23 letters in the permutation.

**Solution:**
If *a* and *z* are separated by 23 letters, then we have the following possibilities: *a/z* is the first letter and *z/a* is the 25th letter; *a/z* is the second letter, and *z/a* is the 26th letter (that is, four ways). For *a* and *z* to be separated by 24 letters, then *a/z* is the first letter and *z/a* is the last letter (that is, two ways). So, we have 6 * 24! permutations in which *a* and *z* are separated by at least 23 letters. Therefore, the probability of the desired event is:

**(6 * 24!)/26! = 6/(25 * 26)**

---

**(f)** *z* precedes both *a* and *b* in the permutation.

**Solution:**
The simplest way to think about this is that in one third of the permutations *z* is before *a* and *b*, in one third it is between them, and in one third it is after them. Therefore, the probability of the desired event is:

**1/3**

---

### Problem 2 [9 pts] - Birthday Problem (Days of Week)

**(a)** What is the probability that two people chosen at random were born on the same day of the week?

**Solution:**
The probability that two people are born on day *i* is (1/7) * (1/7). Since there are 7 possible values for *i*, the answer is:

**7 * (1/7) * (1/7) = 1/7**

---

**(b)** What is the probability that in a group of *n* people chosen at random, there are at least two born on the same day of the week?

**Solution:**
Clearly, for n = 1, this probability is 0, and for n >= 8, this probability is 1 (by the pigeonhole principle).

For 2 <= n <= 7, we denote by p_n the probability that n people chosen at random are born on different days. This probability is:

p_n = (6/7) * (5/7) * ... * ((8-n)/7)

The probability that at least two are born on the same day of the week is:

**1 - p_n**

---

**(c)** How many people chosen at random are needed to make the probability greater than 1/2 that there are at least two people born on the same day of the week?

**Solution:**
We want n such that 1 - p_n > 1/2.

- For n = 3: 1 - p_n = 19/49 <= 1/2
- For n = 4: 1 - p_n = 223/343 > 1/2

**Therefore, n = 4**

---

### Problem 3 [4 pts] - Independence of Complements

**Solution:**
p(E_bar intersection F_bar) = 1 - p(E union F) = 1 - (p(E) + p(F) - p(E intersection F)) = 1 - p(E) - p(F) + p(E)p(F)

where the last equality follows from the assumption of independence of E and F.

But, 1 - p(E) - p(F) + p(E)p(F) = (1 - p(E))(1 - p(F)) = p(E_bar)p(F_bar).

**Therefore, E_bar and F_bar are independent.**

**Rubric:** 4 points if the proof is correct, 2 for partly correct, and 0 for wrong proof.

---

### Problem 4 [6 pts] - Intersection and Union Bounds

**(a)** Bounds for p(A intersection B)

**Solution:**
We use p(A union B) = p(A) + p(B) - p(A intersection B) = 2/3 + 1/2 - p(A intersection B) = 7/6 - p(A intersection B).

Therefore, p(A intersection B) = 7/6 - p(A union B).

**Maximum:** To maximize p(A intersection B), we need to minimize p(A union B). That is, we want A and B to overlap as much as possible. Since p(A) > p(B), we can take B subset of A, in which case p(A union B) = 2/3 and **p(A intersection B) = 1/2**.

*Example:* Sample space S = {1, 2, 3, 4, 5, 6}, A = {1, 2, 3, 4} and B = {1, 2, 3}.

**Minimum:** To minimize p(A intersection B), we need to maximize p(A union B), which can be achieved by making p(A union B) = 1. In this case, **p(A intersection B) = 1/6**.

*Example:* Sample space S = {1, 2, 3, 4, 5, 6}, A = {1, 2, 3, 4} and B = {4, 5, 6}.

**Rubric:** 1 point for each correct answer for p(A intersection B) (both underlined values), and 2 points for the two correct examples (if one of the examples is incorrect or one or both are missing, give 0 points for the examples).

---

**(b)** Bounds for p(A union B)

**Solution:**

**Maximum:** The largest that p(A union B) can be is **1**.

*Example:* Sample space S = {1, 2, 3, 4, 5, 6}, A = {1, 2, 3, 4} and B = {4, 5, 6}.

**Minimum:** The smallest that p(A union B) can be is when B subset of A (as we discussed above). In this case, **p(A union B) = 2/3**.

*Example:* Sample space S = {1, 2, 3, 4, 5, 6}, A = {1, 2, 3, 4} and B = {1, 2, 3}.

**Rubric:** 1 point for each correct answer for p(A union B) (both underlined values), and 2 points for the two correct examples (if one of the examples is incorrect or one or both are missing, give 0 points for the examples).

---

### Problem 5 [12 pts] - Bernoulli Trials

**Rubric:** For each part, give 3 points if the answer is correct and the submitted solution gives the details; 0 points otherwise.

**(a)** The probability of no success.

**Solution:**
We want each of the trials to result in failure. Due to independence, the probability of this event is:

**(1-p)^n**

---

**(b)** The probability of at least one success.

**Solution:**
This is 1 minus the probability of no success. So:

**1 - (1-p)^n**

---

**(c)** The probability of at most one success.

**Solution:**
This is the probability of no success ((1-p)^n) or exactly one success (which is np(1-p)^(n-1)). So, the probability is:

**(1-p)^(n-1) * ((1-p) + np)**

---

**(d)** The probability of at least two successes.

**Solution:**
This is 1 minus the probability of at most one success. So, it's:

**1 - (1-p)^(n-1) * ((1-p) + np)**

---

## 2. Random Variables [45 pts]

### Problem 1 [6 pts] - Rolling a Die

**(a)** What is the probability that we roll the die n times?

**Solution:**
As discussed in class (also given by Example 10 and Definition 2 in Section 7.4 in the textbook), this probability is given by the Geometric distribution. The probability is:

**(5/6)^(n-1) * (1/6) = 5^(n-1) / 6^n**

---

**(b)** What is the expected number of times we roll the die?

**Solution:**
By Theorem 4 in Section 7.4, the expected number is 1/p where p = 1/6. Therefore, the expected number is:

**6**

---

### Problem 2 [6 pts] - Space Probe Communication

**(a)** [3 pts] Find the probability that a 0 is received.

**Solution:**
Introduce two random variables S (for Sent) and R (for Received). Each variable takes a value from the set {0, 1}. Based on the problem description, we know the following:
- P(S = 1) = 1/3 and P(S = 0) = 2/3
- P(R = 0|S = 0) = 0.9 and P(R = 1|S = 0) = 0.1
- P(R = 1|S = 1) = 0.8 and P(R = 0|S = 1) = 0.2

We are interested in P(R = 0). We compute this value using the law of total probability as follows:

P(R = 0) = P(R = 0|S = 0)P(S = 0) + P(R = 0|S = 1)P(S = 1) = 0.9 * (2/3) + 0.2 * (1/3) = **(2/3)**

**Rubric:** Give the points only if the student's answer uses the correct formula and the correct numbers. 0 points otherwise.

---

**(b)** [3 pts] Find the probability that a 0 was transmitted, given that a 0 was received.

**Solution:**
The question asks for the value of P(S = 0|R = 0). To compute this, we make use of Bayes' Theorem, as follows:

P(S = 0|R = 0) = P(R = 0|S = 0)P(S = 0) / P(R = 0) = (0.9 * (2/3)) / (2/3) = **0.9**

**Rubric:** Give the points only if the student's answer uses the correct formula and the correct numbers. 0 points otherwise.

---

### Problem 3 [6 pts] - Expected Value of Max

**Solution:**

(a) By definition of the expected value, E(Z) = sum over s of Z(s)p(s).

(b) By definition of Z, it follows that E(Z) = sum over s of max(X(s), Y(s))p(s).

(c) Since X and Y are nonnegative for all points in S, it follows that max(X(s), Y(s)) <= X(s) + Y(s).
(Notice this the assumption that X and Y are nonnegative is crucial for this step.) From (2) and (3), it follows that E(Z) <= sum over s of (X(s) + Y(s))p(s).

(d) The right-hand side of (4) equals sum over s of X(s)p(s) + sum over s of Y(s)p(s) which equals E(X) + E(Y).

(e) It follows, then, that **E(Z) <= E(X) + E(Y)**.

**Rubric:** 6 points for a correct proof; 0 points for completely wrong; use your discretion for partially correct proof.

---

### Problem 4 [3 pts] - Independence of Head/Tail Counts

**Solution:**
**No**, since, for example, p(X = 0, Y = 0) = 0 != p(X = 0)p(Y = 0) where p(X = 0)p(Y = 0) = 0.25 * 0.25.

**Rubric:** 1 point if the answer is "No" (0 points otherwise), and 2 points for the example to demonstrate it.

---

### Problem 5 [5 pts] - Variance of Die Rolls

**Solution:**
We define the random variable X to denote the number of times 6 appears when a fair die is rolled 10 times. In this case, X takes values in {0, 1, 2, ..., 10}. The probability that 6 appears i times is given by the binomial distribution B(10, 1/6); that is:

p(X = i) = C(10, i) * (1/6)^i * (5/6)^(10-i)

As we showed in class (also in your textbook), the expected value of a random variable that is distributed according to B(n, p) is np. Therefore, in our case, we have E(X) = 10 * (1/6) = 10/6.

We have V(X) = sum over i in {0,1,2,...,10} of (i - E(X))^2 * p(X = i)
             = sum over i in {0,1,2,...,10} of (i - (10/6))^2 * C(10, i) * (1/6)^i * (5/6)^(10-i)

The variance of a binomial distribution is np(1-p), so the answer can also be obtained by plugging values in this formula:

**V(X) = 10 * (1/6) * (5/6) = 50/36**

**Rubric:** 5 points for a correct solution, whether they derive the formula for the variance or make use of the formula np(1-p).

---

### Problem 6 [6 pts] - Variance of Non-Independent Variables

**Solution:**

(a) Let X be the number of heads in 5 tosses of a fair coin, and let Y be the number of tails in 5 tosses of a fair coin.

(b) Clearly, X and Y are not independent, since X(s) = 5 - Y(s) for every s in the sample space. Further, it's always the case that X(s) + Y(s) = 5 for every s in the sample space. So, the variance of X + Y is 0; i.e., V(X + Y) = 0. The variances of X and Y are positive values (not zeros); you can verify this. Therefore, **V(X + Y) != V(X) + V(Y)**.

**Rubric:** 3 points for a correct example (part (a) in the solution), and 3 points for discussing properly that the example works (part (b) in the solution).

---

### Problem 7 [4 pts] - Chebyshev's Inequality

**Solution:**
From the variance of a binomially distributed random variable (Example 18 in Section 7.4), we have:

V(X) = 0.4 * 0.6 * n = 0.24n

By Chebyshev's inequality, the upper bound is:

V(X) / (sqrt(n))^2 = 0.24n / n = **0.24**

**Rubric:** 4 points for a correct solution; 0 points otherwise.

---

### Problem 8 [9 pts] - Linear Search Average Comparisons

**Solution:**
This is similar to Example 8 in Section 7.4.

The number of comparisons used if x is the i-th element is 2i + 1. The number of comparisons used if x is not in the list is 2n + 2.

The probability that x is not in the list is:

1 - sum(i=1 to n) of i/(n(n+1)) = 1 - (1/(n(n+1))) * sum(i=1 to n) of i = 1 - (1/(n(n+1))) * (n(n+1)/2) = **1/2**

The average number of comparisons, then, is:

sum(i=1 to n) of ((2i + 1) * i/(n(n+1))) + (1/2)(2n + 2)

= (n + 1) + (1/(n(n+1))) * sum(i=1 to n) of (2i^2 + i)

= (n + 1) + (1/(n(n+1))) * (2 * sum(i=1 to n) of i^2 + sum(i=1 to n) of i)

= (n + 1) + (1/(n(n+1))) * (n(n+1)(2n+1)/3 + n(n+1)/2)

= (n + 1) + (2n+1)/3 + 1/2

= **(5n+4)/3 + 1/2**

---

## Summary

| Section | Problem | Points |
|---------|---------|--------|
| 1 | 1(a-f) | 24 |
| 1 | 2(a-c) | 9 |
| 1 | 3 | 4 |
| 1 | 4(a-b) | 6 |
| 1 | 5(a-d) | 12 |
| 2 | 1(a-b) | 6 |
| 2 | 2(a-b) | 6 |
| 2 | 3 | 6 |
| 2 | 4 | 3 |
| 2 | 5 | 5 |
| 2 | 6 | 6 |
| 2 | 7 | 4 |
| 2 | 8 | 9 |
| **Total** | | **100** |
