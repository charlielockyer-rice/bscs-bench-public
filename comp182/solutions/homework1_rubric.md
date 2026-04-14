# COMP 182: Algorithmic Thinking

## Homework 1: Algorithmic Thinking and Logic - Solutions and Rubric

---

## 1. Algorithmic Thinking [20 pts]

**Grading:** Give +4 pts to each of the five steps. Please be "generous" in this problem. The main point is to make sure the student bothered to read the paper and understood the five steps. In particular, the first and fifth steps are the most problematic for students, so don't take points off if the students are in the right direction.

### Understanding the problem

The main points here are:
1. A dispute during the course of the study of a Karate club caused the club to split into two clubs;
2. Zachary wanted to understand the process that led to this fission;
3. Zachary observed the club for a period of three years;
4. In addition to direct observations, Zachary collected information on the history of the club prior to the period of study;
5. He collected data on the friendship relationships among the club members.

### Formulating the problem

Zachary formulated the problem as a graph-theoretic problem where the input consists of a weighted graph (V, E, C), where V is the set of individuals, E is the set of pairwise relationships among the individuals, and C is the weights (he referred to them as capacities) that quantify the relative strengths/weaknesses of the relationships. The output consists of computing the flow in this capacitated network.

### Algorithm design

The algorithm used is a network flow algorithm known as the Ford-Fulkerson algorithm.

### Algorithm implementation

Zachary used an existing program, NETFLOW, written in APL for implementing Ford-Fulkerson's max flow / min cut algorithm.

### Solving the original problem

Zachary found that the flow of political information through the club interacts with the political strategy of the factions to pull apart the network at the factional boundary. This boundary corresponds to an ideological as well as organizational division in the club. In the karate club, both factions shared an ideological position toward the club in the early stages of the conflict, but followed increasingly divergent positions.

---

## 2. Propositional Logic [30 pts]

### Problem 2.1

**Grading:** +3 pts for each correct part (Yes, a student can get a maximum of 33 points for this problem, and that's fine.). +1.5 pts for partially correct. 0 pts for wrong answer.

**(a) Neither the wind nor the rain affected the house.**

- p: the wind affected the house
- q: the rain affected the house
- Proposition: NOT(p OR q) (which is equivalent to (NOT p) AND (NOT q))

**(b) If the pandemic is not controlled, more people will die.**

- p: the pandemic is controlled
- q: more people will die
- Proposition: (NOT p) -> q (if the student defined p to be "the pandemic is not controlled," then there's no need for the negation in the compound proposition)

**(c) Pedestrians should neither jaywalk nor cross a red light, or they will get a ticket.**

- p: pedestrians should jaywalk
- q: pedestrians should cross a red light
- r: pedestrians will get a ticket
- Proposition: NOT(p OR q) OR r (which is equivalent to (p OR q) -> r)

**(d) A plant is not poisonous unless it is tropical.**

- p: a plant is poisonous
- q: a plant is tropical
- Proposition: p -> q

**(e) Growing in the tropic is necessary but not sufficient for a plant to be poisonous.**

- p: a plant is poisonous
- q: a plant is tropical (or, grows in the tropic)
- Proposition: p -> q

### Problem 2.2

**Grading:** +3 pts for each correct part with the correct explanation. +1 pt for correct answer but wrong/missing explanation. 0 pts for wrong answer.

**(a) p XOR p**

Contradiction, as it's always False. One proof is by a two-row truth table. Another is just for the student to say that T XOR T is F and F XOR F is F.

**(b) p XOR (NOT p)**

Satisfiable and tautology, as it's always True. Proof similar style to the first part.

**(c) (p XOR q) OR (p XOR (NOT q))**

Satisfiable and tautology. Proof by a four-row truth table, showing that it evaluates to True under all four assignments.

### Problem 2.3

**Grading:** +3 pts for each correct part with the correct explanation. +1 pt for correct answer but wrong/missing explanation. 0 pts for wrong answer.

**(a) (p AND q) -> r and (p -> r) AND (q -> r)**

Not equivalent. For example p = T, q = F, and r = F.

**(b) (p OR q) -> r and (p -> r) OR (q -> r)**

Not equivalent. For example p = T, q = F, and r = F.

**(c) p -> (q <-> r) and (p -> q) <-> (p -> r)**

Equivalent. Proof by showing tautology (e.g., an 8-row truth table).

---

## 3. Predicate Logic [30 pts]

### Problem 3.1

**Grading:** +2 pts for each correct part with the correct explanation. +1 pt for correct answer but wrong/missing explanation. 0 pts for wrong answer.

**(a) There exists x such that x^3 = -1**

True. Take x = -1.

**(b) For all x, (-x)^2 = x^2**

True. As the square of a number is always positive, (-x)^2 = x^2 for every number.

**(c) For all x, 2x > x**

False. Take x = -1. 2x = -2, but -2 < -1.

**(d) For all x, there exists y such that x * y = y * x**

True. As multiplication of reals is commutative, this is true. For example, for every x, let y = x. We have x^2 = x^2.

**(e) For all x, for all y, there exists z such that z = (x + y)/2**

True. For any two reals x and y, calculate (x+y)/2 and assign the value to z. As (x+y)/2 is defined for any two reals, z exists for any given x and y.

**(f) There exists x such that for all y, x <= y^2**

True. Since y^2 >= 0 for every real y, take x = 0 and the statement is true.

**(g) There exists x such that for all y, x <= y**

False. This statement says that there exists a smallest real number. Since the reals are not bounded from below, this is false.

### Problem 3.2

**(a) For all x (P(x) OR Q(x)) and (For all x P(x)) OR (For all x Q(x))**

**Grading:** +4 pts for correct answer and correct proof. +2 pts for correct answer but wrong/missing proof. 0 pts for wrong answer.

False (Not equivalent). Let P(x) be "x is odd" and Q(x) be "x is even" (assuming the domain is Z). Then, "For all x (P(x) OR Q(x))" says that every integer is either odd or even, which is true. However, "(For all x P(x)) OR (For all x Q(x))" says that either all integers are odd or all integers are even, which is False.

**(b) For all x (P(x) AND Q(x)) and (For all x P(x)) AND (For all x Q(x))**

**Grading:** +6 pts for correct answer and correct proof (1 point for correct answer and 2.5 points for each direction). +2 pts for correct answer but wrong/missing proof. 0 pts for wrong answer.

True (Equivalent). We prove two directions:

**Direction 1:** For all x (P(x) AND Q(x)) -> (For all x P(x)) AND (For all x Q(x))

Assume "For all x (P(x) AND Q(x))" is true. Then, for an arbitrary a, P(a) AND Q(a) is true. By the simplification rule, we have: for an arbitrary a, P(a). By universal generalization, we have "For all x P(x)". We establish "For all x Q(x)" similarly, since by simplification we also have for an arbitrary a, Q(a). We conclude "(For all x P(x)) AND (For all x Q(x))".

**Direction 2:** (For all x P(x)) AND (For all x Q(x)) -> For all x (P(x) AND Q(x))

Assume "(For all x P(x)) AND (For all x Q(x))". By simplification, "For all x P(x)". That is, for an arbitrary a, P(a). Similarly, by simplification, "For all x Q(x)". That is, for an arbitrary a, Q(a). In particular, for an arbitrary a, we have P(a) AND Q(a). By universal generalization, we have "For all x (P(x) AND Q(x))".

**(c) (For all x P(x)) OR (For all x Q(x)) and For all x, For all y (P(x) OR Q(y))**

**Grading:** +6 pts for correct answer and correct proof (1 point for correct answer and 2.5 points for each direction). +2 pts for correct answer but wrong/missing proof. 0 pts for wrong answer.

True (Equivalent). We prove two directions:

**Direction 1:** (For all x P(x)) OR (For all x Q(x)) -> For all x, For all y (P(x) OR Q(y))

This is equivalent to proving NOT(For all x, For all y (P(x) OR Q(y))) -> NOT((For all x P(x)) OR (For all x Q(x))) (proof by contrapositive).

1. NOT(For all x, For all y (P(x) OR Q(y))) — Given
2. Exists x, Exists y NOT(P(x) OR Q(y)) — De Morgan's law for quantifiers
3. NOT(P(a) OR Q(b)) for some a and b — Existential instantiation
4. NOT P(a) AND NOT Q(b) — De Morgan's law
5. NOT P(a) — Simplification on (4)
6. Exists x NOT P(x) — Existential generalization
7. NOT(For all x P(x)) — De Morgan's law for quantifiers
8. NOT(For all x Q(x)) — (like lines 5, 6, 7 but for Q(x))
9. NOT(For all x P(x)) AND NOT(For all x Q(x)) — Conjunction on 7 and 8
10. NOT((For all x P(x)) OR (For all x Q(x))) — De Morgan's law

**Direction 2:** For all x, For all y (P(x) OR Q(y)) -> (For all x P(x)) OR (For all x Q(x))

This is equivalent to proving NOT((For all x P(x)) OR (For all x Q(x))) -> NOT(For all x, For all y (P(x) OR Q(y))).

1. NOT((For all x P(x)) OR (For all x Q(x))) — Given
2. NOT(For all x P(x)) AND NOT(For all x Q(x)) — De Morgan's law
3. NOT(For all x P(x)) — Simplification on 2
4. Exists x NOT P(x) — De Morgan's law for quantifiers
5. NOT P(a) for some a — Existential instantiation
6. NOT(For all x Q(x)) — Simplification on 2
7. Exists x NOT Q(x) — De Morgan's law for quantifiers
8. NOT Q(b) for some b — Existential instantiation
9. NOT P(a) AND NOT Q(b) for some a and some b — Conjunction on 5 and 8
10. NOT(P(a) OR Q(b)) for some a and some b — De Morgan's law
11. Exists y NOT(P(a) OR Q(y)) for some a — Existential generalization
12. Exists x, Exists y NOT(P(x) OR Q(y)) — Existential generalization
13. NOT(For all x, For all y (P(x) OR Q(y))) — De Morgan's law for quantifiers

---

## 4. Proofs [20 pts]

### (a) [5 pts] Prove that given a nonnegative integer n, there is a unique nonnegative integer m such that m^2 <= n < (m + 1)^2.

**Solution:**

First, observe that between m^2 and (m + 1)^2 there are no other perfect squares, since these two are consecutive perfect squares.

Let us now take m = floor(sqrt(n)). By construction of m, we have m^2 <= n < (m + 1)^2. Assume now that there exists another nonnegative integer, q, such that q^2 <= n < (q+1)^2. It must be that q < m since m^2 <= n. It follows that (q + 1) < (m + 1), and (q + 1)^2 < (m + 1)^2. In other words, we have q^2 < m^2 <= n < (q + 1)^2 < (m + 1)^2. This means that we have a perfect square ((q + 1)^2) between the two consecutive perfect squares m^2 and (m + 1)^2; a contradiction. Therefore, m is unique.

**Grading:**
- The proof is correct: +5 pts
- The proof is close to correct but with a minor error: +4 pts
- The proof is close to correct but with more than one minor error: +2 pts
- The proof is not close to correct: +0 pts

### (b) [5 pts] Prove that for any positive integer n, sqrt(n) is either an integer or irrational.

**Solution:**

Proof by contradiction. Assume sqrt(n) is a rational number that is not an integer. Let a and b be two integers whose greatest common divisor is 1 such that sqrt(n) = a/b (we cannot have a = b = 1 and we cannot have b = 1; otherwise, a/b is an integer). Then, n = a^2/b^2, which means nb = a^2/b. Since nb is an integer, it must be that a^2/b is an integer. Since gcd(a, b) = 1, we have gcd(a^2, b) = 1. Therefore, for a^2/b to be an integer, b must be 1, which is a contradiction.

**Grading:**
- The proof is correct: +5 pts
- The proof is close to correct but with a minor error: +5 pts
- The proof is close to correct but with more than one minor error: +2 pts
- The proof is not close to correct: +0 pts

### (c) [5 pts] Let a, b, and c be three odd integers. Prove that ax^2 + bx + c = 0 does not have a rational solution.

**Solution:**

Assume the equation does have a rational solution m/n where gcd(m, n) = 1. Then, a(m/n)^2 + b(m/n) + c = 0, equivalently, am^2 + bmn + cn^2 = 0. Observe that at most one of m and n can be even (otherwise, it can't be that gcd(m, n) = 1).

If m is even and n is odd, then am^2 is even, bmn is even, and cn^2 is odd (c is odd by the problem assumption). But the sum of an even number (am^2 + bmn) and an odd number (cn^2) cannot be 0. Similarly if m is odd and n is even.

Assume both m and n are odd. Then, all three terms am^2, bmn and cn^2 are odd, and their sum is odd, which cannot be 0.

Therefore, x cannot be m/n with gcd(m, n) = 1.

**Grading:**
- The proof is correct: +5 pts
- The proof is close to correct but with a minor error: +4 pts
- The proof is close to correct but with more than one minor error: +2 pts
- The proof is not close to correct: +0 pts

### (d) [5 pts] Prove that between every rational number and every irrational number there is an irrational number.

**Solution:**

Let a and b be a rational and irrational number, respectively. Consider number c = (a + b)/2. We prove that c is irrational.

Assume c is rational and equals p/q. Then, (p/q) = (a + b)/2 which implies that b = 2(p/q) - a = (2p - aq)/q. Since a is rational and p, q are integers, then (2p - aq)/q is rational. A contradiction to the assumption that b is irrational. Therefore, c cannot be rational.

**Grading:**
- The proof is correct: +5 pts
- The proof is close to correct but with a minor error: +4 pts
- The proof is close to correct but with more than one minor error: +2 pts
- The proof is not close to correct: +0 pts
