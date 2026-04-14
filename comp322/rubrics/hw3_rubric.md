---
total_points: 40
---

# COMP 322 Homework 3 - Written Questions Rubric

## Part 1: Written Questions (30 points)

### 1.1 Amdahl's Law Generalization

**Setup:**
- q1 = fraction of WORK that must be sequential (1 processor only)
- q2 = fraction of WORK that can use at most 2 processors
- (1 - q1 - q2) = fraction that can use unbounded processors

---

#### Question 1.1.1: Speedup Upper Bound (10 points)

**Question:** "Provide the best possible upper bound on Speedup as a function of q1 and q2."

**Expected Answer:**

Speedup(P) <= 1 / (q1 + q2/2)

**Derivation:**

With P processors (P -> infinity):
- Sequential portion (q1): Takes time q1 * T_seq
- 2-processor portion (q2): Takes time q2 * T_seq / min(P, 2) = q2 * T_seq / 2
- Unbounded portion (1 - q1 - q2): Takes time (1 - q1 - q2) * T_seq / P -> 0 as P -> infinity

Total parallel time (as P -> infinity):
T_par = q1 * T_seq + (q2 * T_seq / 2) + 0

Speedup = T_seq / T_par = T_seq / (q1 * T_seq + q2 * T_seq / 2)
       = 1 / (q1 + q2/2)

**Rubric:**
- 10 points: Correct formula 1/(q1 + q2/2) with clear derivation
- 8-9 points: Correct formula with minor errors in derivation
- 6-7 points: Correct approach but algebraic errors
- 4-5 points: Understands components but wrong formula
- 2-3 points: Mentions Amdahl's Law but doesn't generalize correctly
- 0-1 points: Incorrect or missing answer

---

#### Question 1.1.2: Justification with Special Cases (5 points)

**Question:** "Justify your answer using cases when q1=0, q2=0, q1=1, or q2=1."

**Expected Answer:**

**Case 1: q1 = 1 (fully sequential)**
- q2 = 0, (1-q1-q2) = 0
- Speedup = 1/(1 + 0) = 1
- Correct: no speedup possible when everything is sequential

**Case 2: q2 = 1 (fully 2-parallelizable)**
- q1 = 0, (1-q1-q2) = 0
- Speedup = 1/(0 + 1/2) = 2
- Correct: maximum speedup is 2 when limited to 2 processors

**Case 3: q1 = 0 (no sequential portion)**
- Speedup = 1/(0 + q2/2) = 2/q2
- As q2 -> 0, Speedup -> infinity
- Correct: with no bottlenecks, speedup is unbounded

**Case 4: q2 = 0 (standard Amdahl's Law)**
- Speedup = 1/q1
- Matches original Amdahl's Law formula

**Rubric:**
- 5 points: All 4 cases analyzed correctly with interpretations
- 4 points: 3 cases correct with good explanations
- 3 points: 2 cases correct or 4 cases with minimal explanation
- 2 points: 1 case correct or attempt at multiple cases
- 1 point: Attempt at case analysis but incorrect
- 0 points: No case analysis

---

### 1.2 Finish Accumulators Analysis (15 points)

**Code Reference:**
```java
count0 = 0;
accumulator a = new accumulator(SUM, int.class);
finish (a) {
    for (int i = 0; i <= N - M; i++)
    async {
        int j;
        for (j = 0; j < M; j++) if (text[i+j] != pattern[j]) break;
        if (j == M) { count0++; a.put(1); }
        count1 = a.get();
    }
}
count2 = a.get();
```

**Context:**
- N = length of text
- M = length of pattern
- Q = number of times pattern occurs in text

---

#### Question 1.2.1: Possible Values of count0, count1, count2 (15 points)

**Question:** "What possible values can count0, count1, and count2 contain? Write answers in terms of M, N, and Q."

**Expected Answer:**

**count0:**
- **Possible values: 0 to Q** (any integer in this range)
- **Explanation:**
  - count0++ is a data race (no synchronization)
  - Multiple asyncs may read the same value and increment
  - Lost updates possible
  - Best case: All Q increments succeed -> count0 = Q
  - Worst case: All increments lost -> count0 = 0
  - Any value in between is possible

**count1:**
- **Possible values: 0 to Q-1** (or 0 to Q depending on timing)
- **Explanation:**
  - a.get() inside async returns the "local view" of the accumulator
  - Per HJlib semantics, a.get() inside finish returns accumulated value up to last phase
  - Since asyncs run concurrently, count1 sees partial sums
  - Could be 0 (before any matches) to Q-1 (just before last match)
  - Note: The exact semantics depend on HJlib accumulator implementation
  - Conservative answer: 0 to Q

**count2:**
- **Possible value: Exactly Q**
- **Explanation:**
  - a.get() after finish completes returns the final accumulated sum
  - Accumulators guarantee correct aggregation at finish boundary
  - Exactly Q patterns matched, exactly Q calls to a.put(1)
  - After finish, count2 = Q

**Summary:**
| Variable | Possible Values | Reason |
|----------|-----------------|--------|
| count0 | 0 to Q | Data race on count0++ |
| count1 | 0 to Q | Partial accumulator view inside async |
| count2 | Q | Final accumulator value after finish |

**Rubric:**

**count0 (5 points):**
- 5 points: 0 to Q with data race explanation
- 4 points: Correct range with partial explanation
- 3 points: Identifies data race but wrong range
- 2 points: Mentions race without analysis
- 1 point: Attempt but incorrect
- 0 points: Missing

**count1 (5 points):**
- 5 points: 0 to Q (or 0 to Q-1) with accumulator semantics explanation
- 4 points: Correct range with partial explanation
- 3 points: Mentions partial values without precise range
- 2 points: Confuses with count0 or count2
- 1 point: Attempt but incorrect
- 0 points: Missing

**count2 (5 points):**
- 5 points: Exactly Q with finish semantics explanation
- 4 points: Correct value with partial explanation
- 3 points: Correct value without explanation
- 2 points: Range instead of exact value
- 1 point: Attempt but incorrect
- 0 points: Missing

---

## Part 2: Report (10 points)

### Report: Parallel Sequence Alignment

**Requirements:**
1. Design summary for parallel scoring algorithms (Ideal, Useful, Sparse)
2. Correctness justification
3. Performance measurements

**Rubric:**

#### Section A: Design Summary (4 points)
- 4 points: Clear description of:
  - Parallelization strategy (wavefront/diagonal)
  - Difference between Ideal, Useful, and Sparse versions
  - Synchronization approach (phasers for Useful)
- 3 points: Good description with minor omissions
- 2 points: Basic description, missing key details
- 1 point: Minimal description
- 0 points: No summary

#### Section B: Performance Measurements (4 points)
- 4 points: Timing data for:
  - Sequential baseline
  - Ideal parallelism metrics (Work, Span, Parallelism)
  - Useful parallel speedup at various thread counts
  - Sparse version scalability
- 3 points: Most measurements present
- 2 points: Limited measurements
- 1 point: Minimal measurements
- 0 points: No measurements

#### Section C: Analysis (2 points)
- 2 points: Insightful analysis of:
  - Why Useful achieves target speedup (or not)
  - Memory vs. parallelism tradeoffs in Sparse
  - Scaling behavior
- 1 point: Basic analysis
- 0 points: No analysis
