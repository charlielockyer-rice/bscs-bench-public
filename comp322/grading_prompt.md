# COMP 322 Grading Guidelines

## Course Overview

COMP 322 is a parallel programming course that teaches fundamental concepts using Java and the HJlib (Habanero-Java) library. The course covers:

- Functional programming and Java Streams
- Task parallelism (async/finish)
- Futures and data-driven tasks
- Phasers and synchronization
- Locks, atomic variables, and concurrent data structures

## Key Concepts to Evaluate

### 1. Work and Span Analysis

When evaluating complexity analysis:

- **Work**: Total number of operations across all parallel tasks
- **Span** (Critical Path Length): Longest chain of sequential dependencies
- **Parallelism**: Work / Span ratio

**Expected notation:**
- Use Big-O notation (e.g., O(n), O(n log n), O(n^2))
- For exact formulas, use recurrence relations when appropriate
- Clearly distinguish between Work and Span

**Common mistakes to watch for:**
- Confusing Work with Span
- Forgetting to account for overhead in parallel algorithms
- Not considering the impact of sequential portions (Amdahl's Law)

### 2. Parallel Algorithm Design

When evaluating parallel algorithm descriptions:

- **Correctness**: Does the algorithm produce the same result as sequential?
- **Safety**: Is it free from data races, deadlocks, and livelocks?
- **Efficiency**: Does parallelization reduce Span while keeping Work bounded?

**Look for:**
- Clear identification of parallelizable vs. sequential portions
- Proper use of HJlib constructs (async, finish, future, isolated)
- Synchronization strategy when shared state exists

### 3. Data Race Analysis

A data race occurs when:
1. Two or more tasks access the same memory location
2. At least one access is a write
3. There is no synchronization between the accesses

**Evaluation criteria:**
- Identify all shared variables
- Determine which accesses are reads vs. writes
- Check for proper synchronization (finish, isolated, locks, atomics)
- If a data race exists, determine if it is benign (doesn't affect correctness)

### 4. Deadlock Analysis

Deadlock conditions (all four must hold):
1. Mutual exclusion
2. Hold and wait
3. No preemption
4. Circular wait

**Evaluation criteria:**
- Identify the resources (locks, components)
- Trace possible execution orders
- Check if circular wait can occur
- Verify if any condition is broken

### 5. Livelock Analysis

Livelock occurs when tasks are active but make no progress (e.g., repeated tryLock failures).

**Evaluation criteria:**
- Identify retry mechanisms
- Check for progress guarantees
- Consider fairness of scheduling
- Look for back-off strategies

### 6. Performance Reports

When evaluating performance reports:

**Required elements:**
- Machine specifications (CPU, cores, memory)
- Clear timing methodology (warm-up runs, averaging)
- Speedup calculations: T_sequential / T_parallel(P)
- Analysis of results (scaling behavior, overhead)

**Quality indicators:**
- Multiple data points (varying input sizes or thread counts)
- Statistical rigor (multiple runs, standard deviation)
- Explanation of observed behavior
- Comparison against theoretical predictions

## HJlib Constructs Reference

### Task Parallelism
```java
finish {
    async { /* parallel task 1 */ }
    async { /* parallel task 2 */ }
}
// both tasks complete before continuing
```

### Futures
```java
HjFuture<T> f = future(() -> { return computation(); });
T result = f.get(); // blocks until future completes
```

### Isolated Sections
```java
isolated(() -> {
    // atomic section - no concurrent executions
});
```

### Atomic Variables
```java
AtomicInteger ai = new AtomicInteger(0);
ai.incrementAndGet();
ai.compareAndSet(expected, newValue);
```

### Phasers
```java
HjPhaser ph = newPhaser(PhaserMode.SIG_WAIT);
async phased(ph, SIG_WAIT) {
    // signal and wait at barrier
    next();
}
```

## Rubric Application Guidelines

1. **Award partial credit** for demonstrating understanding even with minor errors
2. **Penalize heavily** for fundamental misconceptions (e.g., confusing Work with Span)
3. **Give credit for approach** when mathematical details are incorrect
4. **Require precision** in data race and deadlock analysis - vague answers get minimal credit
5. **Value clarity** - well-explained simple answers beat poorly-explained complex ones

## Common Point Deductions

- Missing units or asymptotic notation: -1 point
- Incomplete justification: -2 points
- Incorrect formula with correct approach: -2 to -3 points
- Fundamental conceptual error: -4 to -5 points
- Missing or incomplete answer: full deduction

## Report Grading

Reports should demonstrate:
1. **Completeness**: All required measurements present
2. **Correctness**: Accurate calculations and valid methodology
3. **Analysis**: Meaningful interpretation of results
4. **Clarity**: Well-organized presentation

Typical deductions:
- Missing machine specs: -2 points
- No performance measurements: -5 points
- No analysis/discussion: -3 points
- Incomplete speedup calculations: -2 points
