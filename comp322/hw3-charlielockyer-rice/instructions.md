# COMP 322 Homework 3: Pairwise Sequence Alignment

**Total: 100 points**

## Overview

Implement parallel algorithms for optimal scoring of pairwise DNA sequence alignment using the Smith-Waterman algorithm.

## Testing Your Solution

```bash
# Run all tests
bin/grade workspaces/agent_hw3

# Or run specific test classes via Maven
mvn test -Dtest=Homework3Checkpoint1CorrectnessTest
mvn test -Dtest=Homework3Checkpoint2CorrectnessTest
mvn test -Dtest=Homework3Checkpoint3CorrectnessTest
mvn test -Dtest=Homework3PerformanceTest
```

---

## Part 1: Written Assignment (30 points)

### 1.1 Amdahl's Law (15 points)

Consider a generalization of Amdahl's Law:
- q1 = fraction of WORK that must be sequential
- q2 = fraction that can use at most 2 processors
- (1 - q1 - q2) = fraction that can use unbounded processors

1. (10 points) Provide the best possible upper bound on Speedup as a function of q1 and q2.
2. (5 points) Justify your answer using cases when q1=0, q2=0, q1=1, or q2=1.

### 1.2 Finish Accumulators (15 points)

Analyze the Parallel Search algorithm that computes Q (occurrences of pattern in text):

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

What possible values can `count0`, `count1`, and `count2` contain? Write answers in terms of M, N, and Q.

---

## Part 2: Programming Assignment (70 points)

### Background: Smith-Waterman Algorithm

For DNA sequences (alphabet: A, C, T, G), the Smith-Waterman algorithm computes optimal alignment scores.

**Scoring scheme:**
- Match (p=q): +5
- Mismatch (p!=q): +2
- Gap in X (p,-): -4
- Gap in Y (-,q): -2

**Recurrence:**
```
S[i,j] = max{
    S[i-1, j-1] + M[X[i],Y[j]],  // diagonal
    S[i-1, j] + M[X[i],-],        // gap in Y
    S[i, j-1] + M[-,Y[j]]         // gap in X
}
```

**Boundary conditions:** S[i,0] = i * M[p,-] and S[0,j] = j * M[-,q]

### Checkpoint 1: IdealParScoring.java (15 points)

Maximize ideal parallelism using HJlib abstract metrics:
- Insert `doWork(1)` for each S[i,j] computation
- Do NOT use phasers
- Create tasks to exploit the diagonal dependence structure

### Checkpoint 2: UsefulParScoring.java (20 points)

Achieve smallest execution time on 16 cores:
- Target: >= 8x speedup over sequential
- Test with O(10^4) length strings
- Can use phasers for synchronization

### Final: SparseParScoring.java (25 points)

Sparse memory version:
- Use less than O(n^2) space
- Handle O(10^5) length strings
- Target: >= 3x speedup

---

## Key Insight

The dependence structure of S[i,j] computations forms a wavefront pattern:
- Each cell depends on its left, top, and diagonal neighbors
- Cells on the same anti-diagonal can be computed in parallel
- This is the key to parallelization

---

## Scoring

- 15 points: Checkpoint 1 (IdealParScoring)
- 20 points: Checkpoint 2 (UsefulParScoring)
- 25 points: Final (SparseParScoring)
- 10 points: Report with design, correctness justification, and performance measurements

---

## Files to Implement

- `src/main/java/edu/rice/comp322/IdealParScoring.java`
- `src/main/java/edu/rice/comp322/UsefulParScoring.java`
- `src/main/java/edu/rice/comp322/SparseParScoring.java`

---
*COMP 322: Fundamentals of Parallel Programming, Rice University*
