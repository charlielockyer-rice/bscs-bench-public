# COMP 182: Algorithmic Thinking
## Homework 7: Counting - Solutions and Grading Rubric
### Spring 2023

> **Note:** There are many correct ways for arriving at each answer. Do not penalize a student just because their solution is different from the one provided below. However, there could be many wrong solutions where the mistakes are subtle. It's important to give correct feedback; otherwise, students could be misled to think that a correct solution is incorrect or vice versa.

---

## Part 1

### Problem 1 [7 pts]
**How many bit strings of length 10 either start with 000 or end with 111?**

#### Solution

Let $A$ be the set of all bit strings of length 10 that start with 000, and $B$ be the set of all bit strings of length 10 that end with 111. We are interested in $|A \cup B|$, which can be computed using the formula:

$$|A \cup B| = |A| + |B| - |A \cap B|$$

- $|A| = 2^7 = 128$ since only the last 7 bits could take on either value.
- $|B| = 2^7 = 128$ since only the first 7 bits could take on either value.
- $|A \cap B| = 2^4 = 16$ since the first three and last three bits are fixed, leaving only 4 bits that could take on either value.

Therefore,
$$|A \cup B| = 128 + 128 - 16 = \boxed{240}$$

#### Grading Rubric (7 points)
- The logic (how the student thought about the problem and the formula they ended up using) behind the answer is correct: **4 points**
- The final answer is correct: **2 points**
- The submitted solution shows the work (not just gives a formula and an answer): **1 point**

---

### Problem 2 [7 pts]
**How many positive integers less than 1,000,000 have exactly one digit equal to 9 and have a sum of digits equal to 13?**

#### Solution

Since each positive integer is smaller than 1,000,000, it has at most 6 digits. Since one digit is 9 and the sum of digits equals 13, each positive integer must have at least 2 digits. So, we're looking at positive integers with 2, 3, 4, 5, or 6 digits, one of which is a 9 and the sum of the digits is 13.

We don't want to allow leading 0's (otherwise, we'd over-count: 299, 0299, 00299, 000299 would be counted as different numbers). Therefore we need the constraint that the first (leftmost) digit is not 0.

For a $k$-digit number, $k \geq 2$, we want to count the number of solutions to the equation:
$$x_1 + \cdots + x_k = 13$$

with the constraints:
- (a) the $x_i$'s are nonnegative integers,
- (b) $x_1 \geq 1$ (since we don't want a leading 0), and
- (c) $\exists 1 \leq i \leq k, x_i = 9$.

For a $k$-digit number, we have $\binom{k}{1} = k$ ways of selecting the digit that equals 9. We consider two cases:

**Case 1:** When the leftmost digit is chosen to be 9, then we have $k-1$ digits, $x_2, \ldots, x_k$ that satisfy $x_2 + \cdots + x_k = 4$ with the constraint that $x_i \geq 0$ for each $2 \leq i \leq k$. The number of solutions is:
$$\binom{k-1+4-1}{4} = \binom{k+2}{4}$$

**Case 2:** When $x_j$ is chosen to be 9, where $j \neq 1$, then we have $k-1$ digits that satisfy $x_1 + \cdots + x_k = 4$ (with $x_j$ missing from the sum) with the constraint that $x_1 \geq 1$ and $x_i \geq 0$ for each $2 \leq i \leq k$ and $i \neq j$. This is equivalent to solving $x_1 + \cdots + x_k = 3$ (with $x_j$ missing) with all $x_i \geq 0$. The number of solutions is:
$$\binom{k-1+3-1}{3} = \binom{k+1}{3}$$

Therefore, we have:
$$\sum_{k=2}^{6} \left(\binom{k+2}{4} + (k-1)\binom{k+1}{3}\right)$$

$$= \left(\binom{4}{4} + 1\binom{3}{3}\right) + \left(\binom{5}{4} + 2\binom{4}{3}\right) + \left(\binom{6}{4} + 3\binom{5}{3}\right) + \left(\binom{7}{4} + 4\binom{6}{3}\right) + \left(\binom{8}{4} + 5\binom{7}{3}\right)$$

$$= (1 + 1) + (5 + 8) + (15 + 30) + (35 + 80) + (70 + 175)$$

$$= 2 + 13 + 45 + 115 + 245 = \boxed{420}$$

#### Grading Rubric (7 points)
- The logic (how the student thought about the problem and the formula they ended up using) behind the answer is correct: **4 points**
- The final answer is correct: **2 points**
- The submitted solution shows the work (not just gives a formula and an answer): **1 point**

---

### Problem 3 [7 pts]
**How many solutions are there to the inequality $x_1 + x_2 + x_3 \leq 11$, where $x_1$, $x_2$, and $x_3$ are nonnegative integers?**

#### Solution

This question says that the sum of the three variables could be 0, 1, ..., 11. Therefore, the number of solutions is:

$$\sum_{r=0}^{11} \binom{2+r}{2} = \binom{2}{2} + \binom{3}{2} + \binom{4}{2} + \cdots + \binom{13}{2}$$

$$= 1 + 3 + 6 + 10 + 15 + 21 + 28 + 36 + 45 + 55 + 66 + 78 = \boxed{364}$$

**Alternative approach:** Introduce a slack variable $x_4 \geq 0$ to convert the inequality to an equality:
$$x_1 + x_2 + x_3 + x_4 = 11$$

The number of nonnegative integer solutions is $\binom{11+4-1}{4-1} = \binom{14}{3} = 364$.

#### Grading Rubric (7 points)
- The logic (how the student thought about the problem and the formula they ended up using) behind the answer is correct: **4 points**
- The final answer is correct: **2 points**
- The submitted solution shows the work (not just gives a formula and an answer): **1 point**

---

### Problem 4 [8 pts]
**Prove, without using induction, that in any set of $n + 1$ positive integers not exceeding $2n$ there must be two that are relatively prime.**

#### Solution

**Key Lemma:** Every two consecutive positive integers are relatively prime.

*Proof of Lemma:* Assume this is not the case and that $k$ and $k + 1$ are not relatively prime. Then, some integer $q \geq 2$ divides both $k$ and $k + 1$. This implies that $q$ divides $(k + 1) - k = 1$, which contradicts the fact that $q \geq 2$.

**Main Proof:** We now show that any set of $n + 1$ positive integers not exceeding $2n$ must have two consecutive numbers.

Let $x_1, x_2, \ldots, x_{n+1}$ be $n + 1$ positive integers not exceeding $2n$ and assume, without loss of generality, that they are sorted in increasing order.

Assume that no two of them are consecutive; that is, assume that for each $1 \leq i \leq n$, we have $x_{i+1} - x_i \geq 2$.

Then:
$$\sum_{i=1}^{n}(x_{i+1} - x_i) \geq 2n$$

However, the left-hand side of the inequality is $x_{n+1} - x_1$. In other words, we have $x_{n+1} - x_1 \geq 2n$.

Since $x_1 \geq 1$, it follows that $x_{n+1} \geq 2n + 1$, which contradicts the assumption that none of the integers exceeds $2n$.

Therefore, any set of $n+1$ positive integers not exceeding $2n$ must contain two consecutive integers, and by our lemma, these two integers are relatively prime.

#### Grading Rubric (8 points)
- Give full credit to a correct, detailed proof: **8 points**
- 0 points to a completely wrong proof
- Use the full scale to give partial credit to partially correct proofs

---

## Part 2

### Problem 1 [8 pts]
**Suppose that $p$ and $q$ are prime numbers, that $p \neq q$, and that $n = pq$. What is the number of positive integers not exceeding $n$ that are relatively prime to $n$?**

#### Solution

The number of positive integers not exceeding $n$ is $pq$. Of those, the number of integers that are not relatively prime to $pq$ is the number of integers that are divisible by $p$ or $q$.

- Number of integers $\leq pq$ divisible by $p$: $pq/p = q$
- Number of integers $\leq pq$ divisible by $q$: $pq/q = p$
- Number of integers divisible by both $p$ and $q$ (i.e., by $pq$): 1

By the inclusion-exclusion principle, the number of integers that are not relatively prime to $pq$ is:
$$p + q - 1$$

Therefore, the number of positive integers not exceeding $n$ that are relatively prime to $n$ is:
$$pq - (p + q - 1) = \boxed{pq - p - q + 1} = (p-1)(q-1)$$

**Note:** This is Euler's totient function $\phi(pq) = (p-1)(q-1)$ when $p$ and $q$ are distinct primes.

**Edge case:** If a student considers $p = q$, then the number would be $pq - p = p^2 - p = p(p-1)$.

#### Grading Rubric (8 points)
- Give full 8 points for a correct solution
- 0 points for an incorrect one
- Use discretion for partial credit

---

### Problem 2 [21 pts]
**In celebration of the end of the semester, all the students and instructors in COMP 182 were invited to take a group picture. Assume that there are $n$ students and $m$ instructors. Exactly one instructor is designated as the lead instructor.**

#### Part (a) [7 pts]
**How many ways are there to arrange all the students and instructors in a line such that all students are standing next to each other (no instructors between them)?**

##### Solution

Treat the $n$ students as a single letter. The problem is one of counting the number of permutations of $m + 1$ letters (the student-block plus $m$ instructors), which is $(m + 1)!$.

However, the $n$ students can be arranged (consecutively) in any order, and the number of such arrangements is $n!$.

Therefore, the number of ways is:
$$\boxed{(m + 1)! \cdot n!}$$

##### Grading Rubric (7 points)
- The logic behind the answer is correct: **3 points**
- The final answer is correct: **2 points**
- The submitted solution shows the work: **2 points**

---

#### Part (b) [7 pts]
**How many ways are there to arrange all the students and instructors in a line such that no two instructors are standing next to each other?**

##### Solution

Assume that students are arranged in a line and instructors are placed in the gaps. Let:
- $x_0$ = number of students before the first instructor
- $x_1$ = number of students between the 1st and 2nd instructor
- ...
- $x_{m-1}$ = number of students between the $(m-1)$th and $m$th instructor
- $x_m$ = number of students after the $m$th instructor

We have:
$$x_0 + x_1 + x_2 + \ldots + x_{m-1} + x_m = n$$

with the constraints:
- $x_i \geq 1$ for each $1 \leq i \leq (m-1)$ (at least one student between every two instructors)
- $x_0, x_m \geq 0$

This is equivalent to:
$$x_0 + x_1 + x_2 + \ldots + x_m = n - (m - 1)$$

with $x_i \geq 0$ for each $0 \leq i \leq m$.

The number of solutions is the number of $n - (m-1)$ combinations of a set of $(m+1)$ objects with repetition:
$$\binom{(m+1) - 1 + (n - (m-1))}{n - (m-1)} = \binom{n+1}{n-(m-1)} = \binom{n+1}{m}$$

We haven't accounted for the fact that:
- The $m$ instructors can be arranged in $m!$ ways
- The $n$ students can be arranged in $n!$ ways

Therefore, the number of ways is:
$$\boxed{n! \cdot m! \cdot \binom{n+1}{m}}$$

##### Grading Rubric (7 points)
- The logic behind the answer is correct: **3 points**
- The final answer is correct: **2 points**
- The submitted solution shows the work: **2 points**

---

#### Part (c) [7 pts]
**How many ways are there to arrange all the students and instructors in a line such that no student is standing next to the lead instructor?**

##### Solution

Let the lead instructor be called "Luay." We consider two cases:

**Case 1:** Luay is at one of the two ends of the line.
- 2 positions (left end or right end)
- There are $m - 1$ instructors to choose to have next to him
- The remaining $n$ students and $m - 2$ instructors can be arranged in $(n + m - 2)!$ ways

Subtotal: $2(m-1)(n+m-2)!$

**Case 2:** Luay is not at one of the two ends of the line.
- There are $n + m - 2$ positions for him (not at either end)
- We must choose two instructors to stand to his left and right: $P(m-1, 2) = (m-1)(m-2)$ ways
- The remaining $n$ students and $m - 3$ instructors have $(n + m - 3)!$ arrangements

Subtotal: $(n+m-2) \cdot P(m-1, 2) \cdot (n+m-3)!$

Therefore, the total number of ways is:
$$\boxed{2(m-1)(n+m-2)! + (n+m-2) \cdot P(m-1,2) \cdot (n+m-3)!}$$

This can be simplified to:
$$2(m-1)(n+m-2)! + (n+m-2)(m-1)(m-2)(n+m-3)!$$

##### Grading Rubric (7 points)
- The logic behind the answer is correct: **3 points**
- The final answer is correct: **2 points**
- The submitted solution shows the work: **2 points**

---

## Part 3

### Problem 1 [14 pts]
**How many ways are there to place 80 identical balls in 5 distinct bins such that no bin has more than 24 balls?**

#### Solution

This is equivalent to counting the number of solutions to:
$$x_1 + x_2 + x_3 + x_4 + x_5 = 80$$

with constraints $x_i \geq 0$ and $x_i \leq 24$ for $i = 1, \ldots, 5$.

**Without the upper bound constraints:**
$$\binom{80+5-1}{5-1} = \binom{84}{4}$$

**Using Inclusion-Exclusion:**

Let $A_i$ be the set of solutions where $x_i > 24$ (i.e., $x_i \geq 25$).

The answer we seek is:
$$\binom{84}{4} - |A_1 \cup A_2 \cup A_3 \cup A_4 \cup A_5|$$

**Computing the inclusion-exclusion terms:**

$|A_i|$ is the number of solutions to $x_1 + x_2 + x_3 + x_4 + x_5 = 55$ with $x_i \geq 0$:
$$|A_i| = \binom{55+5-1}{5-1} = \binom{59}{4}$$

$|A_i \cap A_j|$ is the number of solutions to $x_1 + x_2 + x_3 + x_4 + x_5 = 30$ with $x_i \geq 0$:
$$|A_i \cap A_j| = \binom{30+5-1}{5-1} = \binom{34}{4}$$

$|A_i \cap A_j \cap A_k|$ is the number of solutions to $x_1 + x_2 + x_3 + x_4 + x_5 = 5$ with $x_i \geq 0$:
$$|A_i \cap A_j \cap A_k| = \binom{5+5-1}{5-1} = \binom{9}{4}$$

$|A_i \cap A_j \cap A_k \cap A_l| = 0$ (since $80 - 4 \times 25 = -20 < 0$)

$|A_1 \cap A_2 \cap A_3 \cap A_4 \cap A_5| = 0$

**Final Answer:**
$$\boxed{\binom{84}{4} - \binom{5}{1}\binom{59}{4} + \binom{5}{2}\binom{34}{4} - \binom{5}{3}\binom{9}{4}}$$

Computing numerically:
- $\binom{84}{4} = 1,929,501$
- $\binom{5}{1}\binom{59}{4} = 5 \times 455,126 = 2,275,630$
- $\binom{5}{2}\binom{34}{4} = 10 \times 46,376 = 463,760$
- $\binom{5}{3}\binom{9}{4} = 10 \times 126 = 1,260$

Answer: $1,929,501 - 2,275,630 + 463,760 - 1,260 = 116,371$

#### Grading Rubric (14 points)
- Correctly identifying this as an inclusion-exclusion problem: **3 points**
- Correctly computing $|A_i|$: **2 points**
- Correctly computing $|A_i \cap A_j|$: **2 points**
- Correctly computing $|A_i \cap A_j \cap A_k|$: **2 points**
- Correctly identifying that higher-order intersections are empty: **2 points**
- Final formula/answer is correct: **2 points**
- Work is shown clearly: **1 point**

---

### Problem 2 [14 pts]
**What is the number of permutations of the letters in the word INDISTINGUISHABLE such that the strings GIT, TIN, and NAB do not appear in the permutation?**

#### Solution

The word INDISTINGUISHABLE has 17 letters with the following frequencies:
- I: 4 times
- N: 2 times
- S: 2 times
- D, T, G, U, H, A, B, L, E: 1 time each

The total number of permutations without restrictions is:
$$\frac{17!}{4! \cdot 2! \cdot 2!}$$

**Using Inclusion-Exclusion:**

Let:
- $A$ = set of permutations containing "GIT"
- $B$ = set of permutations containing "TIN"
- $C$ = set of permutations containing "NAB"

We need: Total $- |A \cup B \cup C|$

**Computing individual sets:**

When we treat a substring as one letter, we reduce the letter count by 2 (3 letters become 1).

- $|A|$: GIT is one letter. Remaining: I appears 3 times, N appears 2 times, S appears 2 times.
$$|A| = \frac{15!}{3! \cdot 2! \cdot 2!}$$

- $|B|$: TIN is one letter. Remaining: I appears 3 times, S appears 2 times.
$$|B| = \frac{15!}{3! \cdot 2!}$$

- $|C|$: NAB is one letter. Remaining: I appears 4 times, S appears 2 times.
$$|C| = \frac{15!}{4! \cdot 2!}$$

**Computing pairwise intersections:**

- $|A \cap B|$: GITIN must appear (GIT and TIN overlap). Remaining: 13 letters with I appearing 2 times, S appearing 2 times.
$$|A \cap B| = \frac{13!}{2! \cdot 2!}$$

- $|A \cap C|$: GIT and NAB don't overlap. Remaining: 13 letters with I appearing 3 times, S appearing 2 times.
$$|A \cap C| = \frac{13!}{3! \cdot 2!}$$

- $|B \cap C|$: TIN and NAB can appear separately OR as TINAB.
  - Non-overlapping: $\frac{13!}{3! \cdot 2!}$
  - Overlapping (TINAB): $\frac{13!}{3! \cdot 2!}$
$$|B \cap C| = \frac{13!}{3! \cdot 2!} + \frac{13!}{3! \cdot 2!}$$

**Computing three-way intersection:**

$|A \cap B \cap C|$: Either GITIN and NAB are non-overlapping, or GITINAB appears.
- Non-overlapping: $\frac{11!}{2! \cdot 2!}$
- GITINAB: $\frac{11!}{2! \cdot 2!}$
$$|A \cap B \cap C| = \frac{11!}{2! \cdot 2!} + \frac{11!}{2! \cdot 2!}$$

**Final Answer:**
$$\boxed{\frac{17!}{4! \cdot 2! \cdot 2!} - \left(\frac{15!}{3! \cdot 2! \cdot 2!} + \frac{15!}{3! \cdot 2!} + \frac{15!}{4! \cdot 2!}\right) + \left(\frac{13!}{2! \cdot 2!} + \frac{13!}{3! \cdot 2!} + \frac{13!}{3! \cdot 2!} + \frac{13!}{3! \cdot 2!}\right) - \left(\frac{11!}{2! \cdot 2!} + \frac{11!}{2! \cdot 2!}\right)}$$

#### Grading Rubric (14 points)
- Correctly counting total permutations: **2 points**
- Correctly computing $|A|$, $|B|$, $|C|$: **3 points**
- Correctly identifying overlaps for pairwise intersections: **3 points**
- Correctly computing pairwise intersections: **3 points**
- Correctly computing three-way intersection: **2 points**
- Work is shown clearly: **1 point**

---

### Problem 3 [14 pts]
**How many 1-1 functions $f : \{1, \ldots, 20\} \to \{1, \ldots, 80\}$ without a fixed point are there?**

#### Solution

**Total 1-1 functions:**
The number of 1-1 functions from a 20-element set to an 80-element set is:
$$\frac{80!}{60!} = 80 \times 79 \times 78 \times \cdots \times 61$$

**Using Inclusion-Exclusion:**

Let $A_i$ ($1 \leq i \leq 20$) denote the set of 1-1 functions where $f(i) = i$ (i.e., $i$ is a fixed point).

$$|A_i| = \frac{79!}{60!}$$

(Once $f(i) = i$ is fixed, we have a 1-1 function from $\{1, \ldots, 20\} \setminus \{i\}$ to $\{1, \ldots, 80\} \setminus \{i\}$.)

The cardinality of the intersection of $j$ different $A_i$ sets is:
$$\frac{(80-j)!}{60!}$$

By the inclusion-exclusion principle, the number of 1-1 functions without a fixed point is:

$$\boxed{\sum_{j=0}^{20} (-1)^j \binom{20}{j} \frac{(80-j)!}{60!}}$$

Expanding:
$$= \frac{80!}{60!} - \binom{20}{1}\frac{79!}{60!} + \binom{20}{2}\frac{78!}{60!} - \binom{20}{3}\frac{77!}{60!} + \cdots + (-1)^{20}\binom{20}{20}\frac{60!}{60!}$$

$$= \frac{80!}{60!} - 20 \cdot \frac{79!}{60!} + 190 \cdot \frac{78!}{60!} - 1140 \cdot \frac{77!}{60!} + \cdots + 1$$

#### Grading Rubric (14 points)
- Correctly counting total 1-1 functions: **3 points**
- Setting up inclusion-exclusion correctly: **3 points**
- Correctly computing $|A_i|$: **2 points**
- Correctly computing intersection of $j$ sets: **3 points**
- Final formula is correct: **2 points**
- Work is shown clearly: **1 point**

---

## Summary

| Part | Problem | Points | Topic |
|------|---------|--------|-------|
| 1 | 1 | 7 | Inclusion-Exclusion (bit strings) |
| 1 | 2 | 7 | Combinations with constraints |
| 1 | 3 | 7 | Stars and bars inequality |
| 1 | 4 | 8 | Proof (consecutive integers) |
| 2 | 1 | 8 | Euler's totient function |
| 2 | 2a | 7 | Permutations (grouped elements) |
| 2 | 2b | 7 | Permutations (no adjacent elements) |
| 2 | 2c | 7 | Permutations (restricted positions) |
| 3 | 1 | 14 | Stars and bars with upper bounds |
| 3 | 2 | 14 | Permutations avoiding substrings |
| 3 | 3 | 14 | 1-1 functions without fixed points |
| **Total** | | **100** | |
