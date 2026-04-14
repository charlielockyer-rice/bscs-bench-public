# COMP 182: Algorithmic Thinking

## Homework 1: Algorithmic Thinking and Logic

---

## 1. Algorithmic Thinking [20 pts]

In 1977, Anthropologist Wayne Zachary published a seminal paper (now cited more than 4,000 times) titled "An information flow model for conflict and fission in small groups". This paper is an excellent example of the use of the 5-step Algorithmic Thinking process that I defined in class.

The goal of this problem is for you to read the paper and describe each of the five steps as they relate to this paper. In particular:

- **Understanding the problem:** What is the question that Zachary wanted to answer? What was the motivation for the question? What data did he collect? How did he think about solving the problem?

- **Formulating the problem:** What was the mathematical formulation of the problem that Zachary arrived at after understanding the problem?

- **Algorithm design:** What algorithmic technique did Zachary use to solve the problem? You do not need to describe the algorithm itself, but rather the algorithmic technique he employed (1-2 sentences).

- **Algorithm implementation:** Comment on the algorithm implementation (1-2 sentences).

- **Solving the original problem:** What were Zachary's findings in terms of the original problem he set out to solve?

---

## 2. Propositional Logic [30 pts]

### Problem 2.1

Write each of the following sentences using propositional logic. Clearly introduce propositions (using p, q, r, etc.) and give the compound proposition that corresponds to the English sentence.

(a) Neither the wind nor the rain affected the house.

(b) If the pandemic is not controlled, more people will die.

(c) Pedestrians should neither jaywalk nor cross a red light, or they will get a ticket.

(d) A plant is not poisonous unless it is tropical.

(e) Growing in the tropic is necessary but not sufficient for a plant to be poisonous.

### Problem 2.2

Recall that XOR denotes "exclusive or." For each of the following compound propositions, determine if it is satisfiable or not. If it is satisfiable, determine also if it is a tautology. Prove your answer.

(a) p XOR p

(b) p XOR (NOT p)

(c) (p XOR q) OR (p XOR (NOT q))

### Problem 2.3

Two compound propositions p and q are logically equivalent if p <-> q is a tautology (see Section 1.3 in your textbook). Which of the following pairs of compound propositions are logically equivalent? Prove your answer.

(a) (p AND q) -> r and (p -> r) AND (q -> r)

(b) (p OR q) -> r and (p -> r) OR (q -> r)

(c) p -> (q <-> r) and (p -> q) <-> (p -> r)

---

## 3. Predicate Logic [30 pts]

### Problem 3.1

Determine the truth value of each of these statements if the domain is R (the real numbers). Explain your answer.

(a) There exists x such that x^3 = -1

(b) For all x, (-x)^2 = x^2

(c) For all x, 2x > x

(d) For all x, there exists y such that x * y = y * x

(e) For all x, for all y, there exists z such that z = (x + y)/2

(f) There exists x such that for all y, x <= y^2

(g) There exists x such that for all y, x <= y

### Problem 3.2

Which of the following pairs of expressions are logically equivalent? Prove your answer.

(a) For all x (P(x) OR Q(x)) and (For all x P(x)) OR (For all x Q(x))

(b) For all x (P(x) AND Q(x)) and (For all x P(x)) AND (For all x Q(x))

(c) (For all x P(x)) OR (For all x Q(x)) and For all x, For all y (P(x) OR Q(y))

(Here all quantifiers have the same nonempty domain.)

---

## 4. Proofs [20 pts]

(a) **[5 pts]** Prove that given a nonnegative integer n, there is a unique nonnegative integer m such that m^2 <= n < (m + 1)^2.

(b) **[5 pts]** Prove that for any positive integer n, sqrt(n) is either an integer or irrational.

(c) **[5 pts]** Let a, b, and c be three odd integers. Prove that ax^2 + bx + c = 0 does not have a rational solution.

(d) **[5 pts]** Prove that between every rational number and every irrational number there is an irrational number.
