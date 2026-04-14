# COMP 322 Homework 3: Pairwise Sequence Alignment

## Part 1: Written Questions (30 points)

### 1.1 Amdahl's Law Generalization (15 points)

Consider a generalization of Amdahl's Law:
- q1 = fraction of WORK that must be sequential (1 processor only)
- q2 = fraction that can use at most 2 processors
- (1 - q1 - q2) = fraction that can use unbounded processors

---

#### 1.1.1 Speedup Upper Bound (10 points)

> Provide the best possible upper bound on Speedup as a function of q1 and q2.

**Your Answer:**

[Derive the formula step by step, showing:
1. Time for each component with P processors
2. Total parallel time as P -> infinity
3. Final Speedup formula]

---

#### 1.1.2 Justification with Special Cases (5 points)

> Justify your answer using cases when q1=0, q2=0, q1=1, or q2=1.

**Your Answer:**

| Case | q1 | q2 | Speedup | Interpretation |
|------|----|----|---------|----------------|
| Fully Sequential | 1 | 0 | | |
| Fully 2-Parallel | 0 | 1 | | |
| No Sequential | 0 | varies | | |
| Standard Amdahl | varies | 0 | | |

[Provide interpretations for each case]

---

### 1.2 Finish Accumulators (15 points)

Analyze the following Parallel Search algorithm:

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

Where:
- N = length of text
- M = length of pattern
- Q = number of times pattern occurs in text

> What possible values can count0, count1, and count2 contain? Write answers in terms of M, N, and Q.

**Your Answer:**

**count0:**
- Possible values: [range]
- Explanation: [why this range is possible]

**count1:**
- Possible values: [range]
- Explanation: [why this range is possible]

**count2:**
- Possible values: [exact value or range]
- Explanation: [why this value is guaranteed]

---

## Part 2: Report (10 points)

### A. Design Summary

**IdealParScoring:**
[Describe your approach to maximize ideal parallelism using HJlib abstract metrics]

**UsefulParScoring:**
[Describe your approach to achieve real speedup on 16 cores, including phaser usage]

**SparseParScoring:**
[Describe your memory-efficient approach for O(10^5) length strings]

---

### B. Performance Measurements

**Machine Specifications:**
- CPU: [Model]
- Cores: [Number]
- Memory: [Size]

**Ideal Parallelism Metrics:**

| Version | Work | Span | Parallelism |
|---------|------|------|-------------|
| IdealParScoring | | | |

**Useful Parallel Speedup (string length = 10,000):**

| Threads | Time (ms) | Speedup |
|---------|-----------|---------|
| Sequential | | 1.0x |
| 2 | | |
| 4 | | |
| 8 | | |
| 16 | | |

**Sparse Parallel Speedup (string length = 100,000):**

| Threads | Time (ms) | Speedup |
|---------|-----------|---------|
| Sequential | | 1.0x |
| 2 | | |
| 4 | | |
| 8 | | |
| 16 | | |

---

### C. Discussion

[Analyze your results:
- Did UsefulParScoring achieve the target 8x speedup? Why or why not?
- What were the memory vs. parallelism tradeoffs in SparseParScoring?
- How does the wavefront parallelism pattern affect scaling?]

---
