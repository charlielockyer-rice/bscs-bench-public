# COMP 182: Algorithmic Thinking
## Homework 9: Discrete Probability

The goal of this homework is for you to practice discrete probability.

---

## 1. Events and Their Probabilities [55 pts]

### Problem 1 [24 pts]
What is the probability of the following events when we randomly select a permutation of the 26 lowercase letters of the English alphabet? Show your work.

**(a)** The first 13 letters of the permutation are in alphabetical order.

**(b)** *a* is the first letter of the permutation and *z* is the last letter.

**(c)** *a* and *z* are next to each other in the permutation.

**(d)** *a* and *b* are not next to each other in the permutation.

**(e)** *a* and *z* are separated by at least 23 letters in the permutation.

**(f)** *z* precedes both *a* and *b* in the permutation.

---

### Problem 2 [9 pts]
Assume that all days of the week are equally likely. Show your work in each of the following parts.

**(a)** What is the probability that two people chosen at random were born on the same day of the week?

**(b)** What is the probability that in a group of *n* people chosen at random, there are at least two born on the same day of the week?

**(c)** How many people chosen at random are needed to make the probability greater than 1/2 that there are at least two people born on the same day of the week?

---

### Problem 3 [4 pts]
Show that if *E* and *F* are independent events, then *E* (complement) and *F* (complement) are independent events.

---

### Problem 4 [6 pts]
Suppose that *A* and *B* are two events with probabilities p(A) = 2/3 and p(B) = 1/2.

**(a)** What is the largest p(A intersection B) can be? What is the smallest it can be? Give examples to show that both extremes for p(A intersection B) are possible. Show your work.

**(b)** What is the largest p(A union B) can be? What is the smallest it can be? Give examples to show that both extremes for p(A union B) are possible. Show your work.

---

### Problem 5 [12 pts]
Find each of the following probabilities when *n* independent Bernoulli trials are carried out with probability of success *p*. Show your work.

**(a)** The probability of no success.

**(b)** The probability of at least one success.

**(c)** The probability of at most one success.

**(d)** The probability of at least two successes.

---

## 2. Random Variables [45 pts]

### Problem 1 [6 pts]
Suppose that we roll a fair die until a 6 comes up.

**(a)** What is the probability that we roll the die *n* times?

**(b)** What is the expected number of times we roll the die?

---

### Problem 2 [6 pts]
A space probe near Neptune communicates with Earth using bit strings. Suppose that in its transmissions it sends a 1 one-third of the time and a 0 two-thirds of the time. When a 0 is sent, the probability that it is received correctly is 0.9, and the probability that it is received incorrectly (as a 1) is 0.1. When a 1 is sent, the probability that it is received correctly is 0.8, and the probability that it is received incorrectly (as a 0) is 0.2.

Given information:
- P(S = 1) = 1/3 and P(S = 0) = 2/3
- P(R = 0|S = 0) = 0.9 and P(R = 1|S = 0) = 0.1
- P(R = 1|S = 1) = 0.8 and P(R = 0|S = 1) = 0.2

**(a)** [3 pts] Find the probability that a 0 is received.

**(b)** [3 pts] Find the probability that a 0 was transmitted, given that a 0 was received.

---

### Problem 3 [6 pts]
Suppose that *X* and *Y* are random variables and that *X* and *Y* are nonnegative for all points in a sample space *S*. Let *Z* be the random variable defined by Z(s) = max(X(s), Y(s)) for all elements s in S. Show that E(Z) <= E(X) + E(Y).

---

### Problem 4 [3 pts]
Let *X* and *Y* be the random variables that count the number of heads and the number of tails that come up when two fair coins are flipped. Are *X* and *Y* independent? Prove your answer.

---

### Problem 5 [5 pts]
What is the variance of the number of times a 6 appears when a fair die is rolled 10 times? Show your derivation.

---

### Problem 6 [6 pts]
Give an example that shows that the variance of the sum of two random variables is not necessarily equal to the sum of their variances when the random variables are not independent.

---

### Problem 7 [4 pts]
Use Chebyshev's inequality to find an upper bound on the probability that the number of tails that come up when a biased coin with probability of heads equal to 0.6 is tossed *n* times deviates from the mean by more than sqrt(n).

---

### Problem 8 [9 pts]
Suppose the probability that *x* is the *i*-th element in a list of *n* distinct integers is i / (n(n+1)). Find the average number of comparisons used by the linear search algorithm (Algorithm 2 in Section 3.1 in the textbook) to find *x* or to determine that it is not in the list.

---

**Total Points: 100**
