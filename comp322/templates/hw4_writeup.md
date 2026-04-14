# COMP 322 Homework 4: Minimum Spanning Tree (Boruvka's Algorithm)

## Part 1: Written Questions (40 points)

### 1.1 HJ isolated vs Java Atomic Variables (20 points)

Compare the following two PRNG implementations:

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

---

#### 1.1.1 Same Semantics? (10 points)

> Do they have the same semantics when called in parallel? Explain.

**Your Answer:**

[Yes/No, then explain:
- How does IsolatedPRNG achieve atomicity?
- How does AtomicPRNG achieve atomicity?
- Are the guarantees (unique seeds, sequential consistency) the same?]

---

#### 1.1.2 Why While Loop? (10 points)

> Why is the while (true) loop needed? What if it only ran once?

**Your Answer:**

[Provide a concrete execution scenario showing:
1. Two threads calling nextSeed() concurrently
2. What happens with the loop
3. What would happen without the loop (single iteration)
4. The consequences (duplicate seeds, lost updates)]

---

### 1.2 Dining Philosophers Problem (20 points)

Consider 5 philosophers where:
- Philosophers 0-3: Acquire left fork first, then right fork
- Philosopher 4: Acquire right fork first, then left fork

---

#### 1.2.1 Deadlock with Synchronized (10 points)

> Is deadlock possible with synchronized statements? Show execution or explain why not.

**Your Answer:**

[Yes/No, then:
- If Yes: Show a specific execution sequence leading to deadlock
- If No: Explain which deadlock condition is broken and why]

---

#### 1.2.2 Livelock with tryLock (10 points)

> Is livelock possible with ReentrantLock.tryLock()? Show execution or explain why not.

**Your Answer:**

[Yes/No, then:
- If Yes: Show a specific execution sequence demonstrating livelock
- If No: Explain why progress is guaranteed]

---

## Part 2: Report (15 points)

### A. Design and Parallel Constructs

[Describe your parallel Boruvka implementation:
- What data structures did you use for the work-list?
- How are components processed in parallel?
- What locking/atomic strategy did you use for component merging?]

---

### B. Correctness and Data-Race-Free Justification

[Explain why your implementation:
1. Produces a valid MST (same weight as sequential)
2. Is free from data races during component merging
3. Correctly handles the "isDead" flag and edge updates]

---

### C. Deadlock-Free Justification

[Explain why your implementation cannot deadlock:
- What lock ordering strategy did you use?
- Or if using tryLock: how do you handle lock acquisition failures?
- Why is circular wait impossible?]

---

### D. Livelock-Free Justification

[Explain why your implementation cannot livelock:
- How do you guarantee progress?
- If using tryLock: why won't threads spin forever?
- What ensures termination?]

---

### E. Performance Measurements

**Machine Specifications:**
- CPU: [Model]
- Cores: [Number]
- Memory: [Size]

**Test Graph:** USA-road-d.NE.gr (Northeast road network)
- Nodes: [Number]
- Edges: [Number]

**Performance Results:**

| Threads | Time (ms) | Speedup | Target |
|---------|-----------|---------|--------|
| Sequential | | 1.0x | - |
| 1 | | | <= 2.0x |
| 2 | | | >= 1.25x |
| 4 | | | >= 1.6x |
| 8 | | | >= 1.5x |
| 16 | | | >= 1.4x |

**Analysis:**

[Discuss:
- Did you meet the speedup targets?
- How does performance scale with thread count?
- What limits scalability (contention, work imbalance, etc.)?
- Why might speedup decrease at higher thread counts?]

---
