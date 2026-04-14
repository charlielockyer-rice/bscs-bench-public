# COMP 322 Homework 4: Minimum Spanning Tree (Boruvka's Algorithm)

**Total: 100 points**

## Overview

Implement a parallel algorithm for finding the minimum spanning tree of an undirected graph using Boruvka's algorithm.

## Testing Your Solution

```bash
# Run all tests
bin/grade workspaces/agent_hw4

# Run specific tests
mvn test -Dtest=BoruvkaCorrectnessTest
mvn test -Dtest=BoruvkaPerformanceTest
```

---

## Part 1: Written Assignment (40 points)

### 1.1 HJ isolated vs Java atomic variables (20 points)

Compare IsolatedPRNG (using HJlib isolated) and AtomicPRNG (using AtomicInteger):

**IsolatedPRNG:**
```java
class IsolatedPRNG {
    private int seed;
    public int nextSeed() {
        final int retVal = isolatedWithReturn(() -> {
            final int curSeed = seed;
            final int newSeed = nextInt(curSeed);
            seed = newSeed;
            return curSeed;
        });
        return retVal;
    }
}
```

**AtomicPRNG:**
```java
class AtomicPRNG {
    private AtomicInteger seed;
    public int nextSeed() {
        int retVal;
        while (true) {
            retVal = seed.get();
            int nextSeedVal = nextInt(retVal);
            if (seed.compareAndSet(retVal, nextSeedVal)) break;
        }
        return retVal;
    }
}
```

1. (10 points) Do they have the same semantics when called in parallel? Explain.
2. (10 points) Why is the `while (true)` loop needed? What if it only ran once?

### 1.2 Dining Philosophers Problem (20 points)

Consider 5 philosophers where 4 acquire left fork first, then right, but 1 philosopher acquires right fork first, then left.

1. (10 points) Is deadlock possible with synchronized statements? Show execution or explain why not.
2. (10 points) Is livelock possible with ReentrantLock.tryLock()? Show execution or explain why not.

---

## Part 2: Programming Assignment (60 points)

### Background: Boruvka's Algorithm

Boruvka's algorithm computes MST through successive edge-contraction operations:

```java
while (!nodesLoaded.isEmpty()) {
    n = nodesLoaded.poll();
    if (n.isDead) continue;
    Edge e = n.getMinEdge();
    if (e == null) break;
    Component other = e.getOther(n);
    other.isDead = true;
    n.merge(other, e.weight());
    nodesLoaded.add(n);
}
```

**Key insights for parallelization:**
- Work-list order doesn't matter (unordered collection)
- Disjoint (n, other) pairs can execute in parallel

### Checkpoint 1: Correctness (20 points)

Implement parallel Boruvka in:
- `ParBoruvka.java` - main algorithm
- `ParComponent.java` - component with mutual exclusion
- `ParEdge.java` - edge representation

Must pass `BoruvkaCorrectnessTest` with at least 2 threads.

### Final: Performance (25 points)

Performance evaluation with USA-road-d.NE.gr.gz:

| Threads | Speedup Target | Points |
|---------|---------------|--------|
| 1 | <= 2x sequential | 5 |
| 2 | >= 1.25x | 5 |
| 4 | >= 1.6x | 5 |
| 8 | >= 1.5x | 5 |
| 16 | >= 1.4x | 5 |

### Implementation Tips

- Create limited threads (one per core)
- Reduce parallelism as graph contracts
- Use `ConcurrentLinkedQueue` or `ConcurrentHashMap` for work-list
- Consider `ReentrantLock.tryLock()` for collision handling

---

## Report (15 points)

Include:
- Design and parallel constructs used
- Correctness and data-race-free justification
- Deadlock-free justification
- Livelock-free justification
- Test outputs and performance measurements

---

## Files to Implement

- `src/main/java/edu/rice/comp322/boruvka/parallel/ParBoruvka.java`
- `src/main/java/edu/rice/comp322/boruvka/parallel/ParComponent.java`
- `src/main/java/edu/rice/comp322/boruvka/parallel/ParEdge.java`

---
*COMP 322: Fundamentals of Parallel Programming, Rice University*
