# COMP 322 Homework 1: Functional Programming & Java Streams

## Part 1: Written Questions (15 points)

### 1.1 Recursive Fibonacci Analysis

#### 1.1.1 Recursive Fibonacci Complexity (5 points)

> What is the formula (exact answer) for the total work performed by a call to fib(n)? Assume that a single call to fib() (without any recursive calls inside) has a total WORK of 1. Include an explanation of the analysis, and state what expression you get for WORK(n) as a function of n. The exact answer should not be recursive.

**Your Answer:**

[Provide the recurrence relation, solve it, and give the exact closed-form formula for WORK(n)]

---

#### 1.1.2 Memoized Fibonacci Complexity (10 points)

Consider the memoized version using Lazy evaluation:

```java
public class MemoizationFib {
    private static final int MaxMemo = 1000000;
    private static final Lazy<Integer>[] memoized =
        IntStream.range(0, MaxMemo)
        .mapToObj(e->Lazy.of(()->fib(e)))
        .toArray(Lazy[]::new);

    public static int fib(int n) {
        if (n <= 0) return 0;
        else if (n == 1) return 1;
        else if (n >= MaxMemo) return fib(n - 1) + fib(n - 2);
        else return memoized[n-1].get() + memoized[n-2].get();
    }
}
```

**a) First Call (5 points)**

> What is the big-O for WORK performed by fib(n) when called for the very first time?

**Your Answer:**

[Provide the big-O complexity and explain why]

---

**b) Subsequent Calls (5 points)**

> After many random calls to fib(k), what is the big-O for expected WORK of a subsequent fib(n) call?

**Your Answer:**

[Provide the big-O complexity and explain why]

---

## Part 2: Performance Report (15 points)

### A. Machine Description

**System Specifications:**
- CPU: [Model name]
- Cores: [Number of cores]
- Memory: [RAM size]
- OS: [Operating system]

---

### B. Performance Measurements

**Stream Operations Timing (milliseconds):**

| Operation | Sequential | Parallel | Speedup |
|-----------|------------|----------|---------|
| Max February Revenue | | | |
| Recent Order IDs | | | |
| Distinct Customers | | | |
| March Discounts | | | |
| Customer Spend Map | | | |
| Category Avg Cost | | | |
| Tech Product Orders | | | |
| Tier-0 Utilization | | | |

**Test Configuration:**
- Data size: [Number of records]
- Warm-up runs: [Number]
- Measured runs: [Number]

---

### C. Speedup Analysis

[Calculate and discuss the speedup ratios. Which operations benefit most from parallelization? Which show minimal improvement?]

---

### D. Discussion

[Analyze your results. Consider:
- Why do some operations show better speedup than others?
- What is the overhead of parallel stream creation?
- At what data size does parallel processing become beneficial?
- How does the nature of the operation (aggregation vs. collection) affect parallelism?]

---
