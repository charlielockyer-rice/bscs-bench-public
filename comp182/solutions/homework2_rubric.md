# COMP 182: Algorithmic Thinking
## Homework 2: Proofs, Sets, and Functions - Solutions and Rubric

---

## 1 Sets

### Problem 1 [10 pts]

The symmetric difference of sets A and B, denoted by A triangle B, is the set containing those elements in A or B, but not in both A and B. For example, for A = {1, 2, 3} and B = {2, 3, 4, 5}, A triangle B = {1, 4, 5}.

#### Part (a) [5 pts]: Prove that (A triangle B) triangle B = A.

**Solution:**

One way to prove the result is to prove set containment in both directions. A "shorter" proof is to make use of the theorem:

A triangle B = (A union B) - (A intersection B)

By this theorem we have:

(A triangle B) triangle B = ((A triangle B) union B) - ((A triangle B) intersection B)
                         = ((A triangle B) union B) intersection complement((A triangle B) intersection B)
                         = ((A triangle B) union B) intersection ((A triangle B) union complement(B))
                         = ((A triangle B) intersection complement(A triangle B)) union ((A triangle B) intersection B) union (B intersection complement(A triangle B)) union (B intersection complement(B))
                         = empty union ((A triangle B) intersection B) union (B intersection complement(A triangle B)) union empty
                         = ((A triangle B) intersection complement(B)) union (B intersection complement(A triangle B))
                         = ((A \ B) intersection complement(B)) union ((B \ A) intersection complement(B)) union (B intersection A - B intersection B - A)
                         = (A intersection complement(B) intersection complement(B)) union (B intersection complement(A) intersection complement(B)) union (A intersection B)
                         = (A intersection complement(B)) union empty union (A intersection B)
                         = (A intersection complement(B)) union (A intersection B)
                         = A.

**Rubric:**
- 5 points if proof is correct and sufficiently detailed
- 3 points if proof is correct but missing details (too many shortcuts)
- 1 point if proof has serious problems but some correct parts
- 0 if the proof has major problems / completely wrong

---

#### Part (b) [5 pts]: Is the following true: A triangle (B triangle C) = (A triangle B) triangle C? Prove your answer.

**Solution:**

Yes, it is true. The symmetric difference operation is associative.

**Rubric:**
- 5 points if proof is correct and sufficiently detailed
- 3 points if proof is correct but missing details (too many shortcuts)
- 1 point if proof has serious problems but some correct parts
- 0 if the proof has major problems / completely wrong

---

### Problem 2 [5 pts]

Prove that if A and B are two finite sets, then |A intersection B| <= |A union B|. Determine when this relationship is an equality.

**Solution:**

We prove that for all x, (x in A intersection B) -> (x in A union B). Let a be an arbitrary element of A intersection B. By definition, (a in A and a in B). This implies that (a in A or a in B), equivalently that a in A union B. By universal generalization, for all x, (x in A intersection B) -> (x in A union B). Therefore, |A intersection B| <= |A union B|.

The relationship is an equality if and only if A = B.

**Rubric:**

The proof (3 points):
- The proof is correct: +3 pts
- The proof is close to correct: +2 pts
- The proof is not close to correct: +0 pts

The equality question (2 points):
- The answer is correct: +2 pts
- The answer is incorrect: +0 pts

---

### Problem 3

An ordered pair (a, b) differs from a set of two elements {a, b} since the elements of a set are unordered. But we can represent an ordered pair in terms of sets.

**Solution:**

We represent pair (a, b) as set {a, {a, b}}. We now show that {a, {a, b}} = {c, {c, d}} if and only if a = c and b = d.

If {a, {a, b}} = {c, {c, d}}, it must be that a = c and b = d. Otherwise, we have a = {c, d} and c = {a, b}, which means a = {{a, b}, d}, a contradiction.

If a = c and b = d, then {a, b} = {c, d} which implies that {a, {a, b}} = {c, {c, d}}.

---

## 2 Functions

### Problem 1 [5 pts]

Let f(x) = x^2 + 1 and g(x) = x + 2.

**Solution:**

f compose g = (x + 2)^2 + 1 = x^2 + 4x + 5

g compose f = (x^2 + 1) + 2 = x^2 + 3

**Rubric:**
- 2.5 points for each correct answer
- 0 points for each incorrect answer

---

### Problem 2 [5 pts]

If f and f compose g are one-to-one, does it follow that g is one-to-one? Justify your answer.

**Solution:**

Yes. Assume f and f compose g are one-to-one but g is not one-to-one. This means that there are at least two elements a, b such that a != b and g(a) = g(b) (this follows from the assumption that g is not one-to-one). Since g(a) = g(b), it follows that f(g(a)) = f(g(b)), since f is a function. In other words, we have a != b and f(g(a)) = f(g(b)), which violates the assumption that f compose g is one-to-one. Therefore, g must be one-to-one.

**Rubric:**
- If it is correct and properly justified (doesn't have to be as formal; illustration through a figure is fine): +5 pts
- If it is correct, and the justification is good but not perfect: +3 pts
- If it is correct and the justification is not good or missing: +1 pts
- If it is incorrect: +0 pts

---

### Problem 3 [10 pts]

Let f be a function from A to B and let S and T be subsets of A (i.e., S, T subset of A).

#### Part (a) [5 pts]: Prove that f(S union T) = f(S) union f(T).

**Solution:**

We need to prove two directions:

**Direction 1:** f(S union T) subset of f(S) union f(T)

Let y be an arbitrary element of f(S union T). Then, there exists x in S union T such that y = f(x). Then, either x in S or x in T. If x in S, since y = f(x), we have y in f(S). Similarly, if x in T, since y = f(x), we have y in f(T). Therefore, y in f(S) or y in f(T). Equivalently, y in f(S) union f(T).

**Direction 2:** f(S) union f(T) subset of f(S union T)

Let y be an arbitrary element of f(S) union f(T). Assume without loss of generality that y in f(S). Then, there exists x in S such that f(x) = y. Since x in S, it follows that x in (S union T). And since f(x) in f(S), it follows that f(x) in f(S) union f(T).

---

#### Part (b) [5 pts]: Prove that f(S intersection T) subset of f(S) intersection f(T).

**Solution:**

Let y be an arbitrary element of f(S intersection T). Then, there exists x in S intersection T such that y = f(x). Since x in S intersection T, then x in S and x in T. Since x in S and y = f(x), we conclude that y in f(S). Similarly, since x in T and y = f(x), we conclude that y in f(T). Therefore, y in f(S) intersection f(T). Therefore, f(S intersection T) subset of f(S) intersection f(T).

**Rubric:**

Consider the two directions of the proof separately. For each direction:
- The proof is correct: +5 pts
- The proof is close to correct but with a minor error: +4 pts
- The proof is close to correct but with more than one minor error: +3 pts
- The proof is not close to correct: +0 pts

---

### Problem 4

Let A, B, and C be three sets such that there exist bijections f : A -> B and g : A -> C. Prove that there is a bijection h : B -> C.

**Solution:**

Let h = g compose f^(-1). Since f is a bijection, f^(-1) : B -> A is well defined and also a bijection. Thus, h is a well-defined function, and we need to show it's a bijection.

**(Injectivity)** Let x, x' in B such that x != x' and assume that h(x) = h(x') for the sake of contradiction. Hence, we have g(f^(-1)(x)) = g(f^(-1)(x')). Since g is a bijection it follows that f^(-1)(x) = f^(-1)(x'). Furthermore, since f^(-1) is a bijection as was noted above, it follows that x = x' contradicting our assumption.

**(Surjectivity)** Let y in C be an arbitrary element. Since g : A -> C is a bijection there exists some z in A such that g(z) = y. Furthermore, since f^(-1) : B -> A is also a bijection it follows that there exists some x in B such that f^(-1)(x) = z. Putting these together we get h(x) = g(f^(-1)(x)) = g(z) = y, and hence the result follows.

---

### Problem 5

Let A and B be two finite sets of the same size. Prove that if f : A -> B is injective, then it is also surjective.

**Solution:**

Assume |A| = |B| = n. If f is not surjective, then at least one element in B does not have an element in A that maps to it. This means the n elements in A map to at most n-1 elements in B, which means at least two elements in A map to the same element in B, contradicting the assumption that f is injective (this is a use of the pigeonhole principle).

---

### Problem 6

Let A and B be two infinite sets such that there exists a bijection between them. Consider an injective function f : A -> B. Is f necessarily surjective? Prove your answer.

**Solution:**

No. Take for example f : N -> N where f(n) = n + 10. This function is injective, but it's not surjective (the elements 1 through 10 have no preimage).

---

## 3 Relations [25 points]

### Problem 1 [9 pts]

Let R1 be the relation defined on the set of ordered pairs of positive integers such that (a, b) R1 (c, d) if and only if ad = bc. Is R1 an equivalence relation? Prove your answer.

**Solution:**

Yes. We need to show that it is reflexive, symmetric, and transitive.

**Reflexivity:** Let (a, b) be a pair of positive integers. Then (a, b) R1 (a, b) since ab = ba.

**Symmetry:** Assume (a, b) R1 (c, d) for two pairs of positive integers. Then, ad = bc. By commutativity of multiplication, we have da = cb, and by symmetry of =, we have cb = da. Therefore, (c, d) R1 (a, b).

**Transitivity:** Let (a, b), (c, d), and (e, f) be three pairs of positive integers such that (a, b) R1 (c, d) and (c, d) R1 (e, f). Then, (i) ad = bc and (ii) cf = de. We want to show that af = be. From equality (ii), we have f = de/c. Then, (iii) af = a(de/c) = ade/c. Substituting bc for ad in (iii), we obtain af = bce/c = be. Therefore, (a, b) R1 (e, f).

**Rubric:**

For reflexivity (3 points):
- No or no discussion of this property: 0 points
- Yes, but no justification: 1 point
- Yes, with proper justification: 3 points

For symmetry (3 points):
- No or no discussion of this property: 0 points
- Yes, but no justification: 1 point
- Yes, with proper justification: 3 points

For transitivity (3 points):
- No or no discussion of this property: 0 points
- Yes, but no justification: 1 point
- Yes, with proper justification: 3 points

---

### Problem 2

Let R1 and R2 be two equivalence relations. Is R1 intersection R2 an equivalence relation? Prove your answer.

**Solution:**

Yes. We know that given two relations their intersection is a relation, hence we are left to prove the three properties of an equivalence relation.

**(Reflexivity)** Let x in X be an arbitrary element. Since R1 and R2 are equivalence relations and hence reflexive it follows that (x, x) in R1 and (x, x) in R2 implying (x, x) in R1 intersection R2. Hence, the intersection is reflexive.

**(Symmetry)** Let x, y in X be a pair of elements such that (x, y) in R1 intersection R2. Then we have (x, y) in R1 and (x, y) in R2. Furthermore, since R1, R2 are both symmetric it follows that (y, x) in R1 and (y, x) in R2 respectively. Therefore, we obtain (y, x) in R1 intersection R2 and subsequently R1 intersection R2 is symmetric.

**(Transitivity)** Let x, y, z in X be such that (x, y) in R1 intersection R2 and (y, z) in R1 intersection R2. Then we have (x, y) in R1 and (y, z) in R1 implying by transitivity of R1 that (x, z) in R1. Analogous argument for R2 gives us (x, z) in R2, and hence we conclude that (x, z) in R1 intersection R2.

---

### Problem 3

A relation R on a set A is called *circular* if (a, b) in R and (b, c) in R implies (c, a) in R for all a, b, c in A.

**Solution:**

The statement is true.

**Direction 1:** If R is an equivalence relation, then it's reflexive, symmetric, and transitive. We need to show that it's circular. If (a, b) in R and (b, c) in R, then by symmetry of R, we have (b, a) in R and (c, b) in R, and by transitivity we have (c, a) in R. Therefore, R is circular.

**Direction 2:** Assume R is circular and reflexive. We need to show that it is symmetric and transitive.

*Symmetry:* If (a, b) in R, and since (b, b) in R, then by circularity we have (b, a) in R. So, R is symmetric.

*Transitivity:* If (a, b) in R and (b, c) in R, then (c, a) in R by circularity. But because (a, a) in R, then (a, c) in R by circularity. So, R is transitive.

---

### Problem 4 [8 pts]

Give a partially ordered set, or poset, that has:

#### Part (a) [4 pts]: a minimal element but no maximal element.

**Solution:**

(Z^+, <=). Since Z^+ = {1, 2, 3, 4, ...}, it has a minimal element, which is 1, and no maximal element, under the partial order <=.

**Rubric:**
- 4 points for a correct answer (the answer is not unique)
- 0 points for an incorrect answer

---

#### Part (b) [4 pts]: neither a maximal nor a minimal element.

**Solution:**

(Z, <=). Since Z = {..., -4, -3, -2, -1, 0, 1, 2, 3, 4, ...}, it has neither a minimal element nor a maximal element under the partial order <=.

**Rubric:**
- 4 points for a correct answer (the answer is not unique)
- 0 points for an incorrect answer

---

### Problem 5 [8 pts]

Let (A, <=) be a partially ordered set that has no minimal element and A is not empty. Can A be finite?

**Solution:**

No. Assume A is finite and (A, <=) has no minimal elements. This means that for every a in A, there exists a b in A such that b != a and b <= a. Since the set is finite, we must have a sequence of elements in A: a_1, a_2, ..., a_k, where k >= 2, and a_i != a_{i+1} for each 1 <= i < k, such that:

a_1 <= a_2 <= ... <= a_k <= a_1

Therefore, we have a_1 <= a_2 and by transitivity a_2 <= a_1. Since <= is antisymmetric, it follows that a_1 = a_2, which contradicts the assumption that the k chosen elements are different.

**Rubric:**
- If the submitted answer is YES: 0 points
- If the submitted answer is NO, but no justification is provided: 2 points
- If the submitted answer is NO, and a proper justification is provided: 8 points

---
