# COMP 182: Algorithmic Thinking
## Homework 2: Proofs, Sets, and Functions

---

## 1 Sets

### Problem 1 [10 pts]

The symmetric difference of sets A and B, denoted by A triangle B, is the set containing those elements in A or B, but not in both A and B. For example, for A = {1, 2, 3} and B = {2, 3, 4, 5}, A triangle B = {1, 4, 5}.

**(a)** Prove that (A triangle B) triangle B = A.

**(b)** Is the following true: A triangle (B triangle C) = (A triangle B) triangle C? Prove your answer.

---

### Problem 2 [5 pts]

Prove that if A and B are two finite sets, then |A intersection B| <= |A union B|. Determine when this relationship is an equality.

---

### Problem 3

An ordered pair (a, b) differs from a set of two elements {a, b} since the elements of a set are unordered. But we can represent an ordered pair in terms of sets. Introduce a set-based formulation of ordered pairs and prove that (a, b) = (c, d) under your set-based formulation if and only if a = c and b = d.

---

## 2 Functions

### Problem 1 [5 pts]

Let f(x) = x^2 + 1 and g(x) = x + 2. Compute f compose g and g compose f.

---

### Problem 2 [5 pts]

If f and f compose g are one-to-one, does it follow that g is one-to-one? Justify your answer.

---

### Problem 3 [10 pts]

Let f be a function from A to B and let S and T be subsets of A (i.e., S, T subset of A). Prove that:

**(a)** f(S union T) = f(S) union f(T).

**(b)** f(S intersection T) subset of f(S) intersection f(T).

---

### Problem 4

Let A, B, and C be three sets such that there exist bijections f : A -> B and g : A -> C. Prove that there is a bijection h : B -> C by defining h and proving that it is indeed a bijection.

---

### Problem 5

Let A and B be two finite sets of the same size. Prove that if f : A -> B is injective, then it is also surjective.

---

### Problem 6

Let A and B be two infinite sets such that there exists a bijection between them. Consider an injective function f : A -> B. Is f necessarily surjective? Prove your answer.

---

## 3 Relations [25 points]

### Problem 1

Let R1 be the relation defined on the set of ordered pairs of positive integers such that (a, b) R1 (c, d) if and only if ad = bc. Is R1 an equivalence relation? Prove your answer.

---

### Problem 2

Let R1 and R2 be two equivalence relations. Is R1 intersection R2 an equivalence relation? Prove your answer.

---

### Problem 3

A relation R on a set A is called *circular* if (a, b) in R and (b, c) in R implies (c, a) in R for all a, b, c in A. Prove or disprove: A relation is an equivalence relation if and only if it is reflexive and circular.

---

### Problem 4

Give a partially ordered set, or poset, that has:

**(a)** a minimal element but no maximal element.

**(b)** neither a maximal nor a minimal element.

---

### Problem 5 [8 pts]

Let (A, <=) be a partially ordered set that has no minimal element and A is not empty. Can A be finite?

---
