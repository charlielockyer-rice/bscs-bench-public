---
total_points: 55
---

# COMP 322 Homework 4 - Written Questions Rubric

## Part 1: Written Questions (40 points)

### 1.1 HJ isolated vs Java Atomic Variables

**IsolatedPRNG Reference:**
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

**AtomicPRNG Reference:**
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

#### Question 1.1.1: Same Semantics? (10 points)

**Question:** "Do they have the same semantics when called in parallel? Explain."

**Expected Answer:**

**Yes, they have equivalent semantics** (both are correct PRNG implementations).

**Explanation:**

Both implementations guarantee:
1. **Atomicity:** Each call atomically reads the current seed and updates it
2. **Unique return values:** Each call returns a unique seed value (no duplicates)
3. **Sequential consistency:** The sequence of seeds follows the PRNG recurrence

**IsolatedPRNG:**
- Uses HJlib's `isolatedWithReturn` for mutual exclusion
- Only one task can execute the isolated block at a time
- Guarantees sequential execution of read-modify-write

**AtomicPRNG:**
- Uses compare-and-swap (CAS) for lock-free synchronization
- The while loop retries if another thread modified seed between get() and compareAndSet()
- Also guarantees each task gets a unique seed

**Key difference in execution:**
- IsolatedPRNG: Blocking - tasks wait for isolated section
- AtomicPRNG: Non-blocking - tasks spin on CAS failure
- Both produce the same distribution of seeds across threads

**Rubric:**
- 10 points: Yes, with clear explanation of both mechanisms and why they're equivalent
- 8-9 points: Correct answer with good explanation of one mechanism
- 6-7 points: Correct answer with partial explanation
- 4-5 points: Says "same" but misses key details of why
- 2-3 points: Wrong answer (says different) but shows understanding of mechanisms
- 0-1 points: Incorrect or missing answer

---

#### Question 1.1.2: Why While Loop? (10 points)

**Question:** "Why is the while (true) loop needed? What if it only ran once?"

**Expected Answer:**

**Why the loop is needed:**

The loop handles **contention** - when multiple threads try to update the seed simultaneously.

**Execution scenario without loop (running once):**

1. Thread A: `retVal = seed.get()` -> gets 42
2. Thread B: `retVal = seed.get()` -> gets 42 (same value!)
3. Thread A: `nextSeedVal = nextInt(42)` -> computes 57
4. Thread B: `nextSeedVal = nextInt(42)` -> computes 57 (same!)
5. Thread A: `compareAndSet(42, 57)` -> succeeds, seed = 57
6. Thread B: `compareAndSet(42, 57)` -> **FAILS** (seed is now 57, not 42)

**Without the loop:**
- Thread B would return 42 (duplicate!)
- Both threads return the same seed value
- PRNG contract violated: seeds should be unique

**With the loop:**
- Thread B retries: reads new seed (57), computes next (73), CAS succeeds
- Each thread gets a unique seed

**Consequences of single iteration:**
1. **Duplicate seeds:** Multiple threads may return the same value
2. **Lost updates:** Some PRNG state transitions are skipped
3. **Broken randomness:** Statistical properties of PRNG are violated

**Rubric:**
- 10 points: Clear explanation of:
  - CAS failure scenario with example
  - Why duplicates would occur without loop
  - Consequences (lost updates, broken PRNG)
- 8-9 points: Good explanation with minor gaps
- 6-7 points: Explains CAS failure but misses duplicate/lost update issue
- 4-5 points: Says "for contention" without detailed scenario
- 2-3 points: Mentions retrying but doesn't explain why
- 0-1 points: Incorrect or missing answer

---

### 1.2 Dining Philosophers Problem

**Setup:**
- 5 philosophers, 5 forks
- Philosophers 0-3: Acquire left fork first, then right
- Philosopher 4: Acquire right fork first, then left

---

#### Question 1.2.1: Deadlock with Synchronized (10 points)

**Question:** "Is deadlock possible with synchronized statements? Show execution or explain why not."

**Expected Answer:**

**No, deadlock is NOT possible** with this configuration.

**Explanation:**

The classic dining philosophers deadlock requires **circular wait**. Let's analyze:

Fork assignment (assuming clockwise seating):
- Philosopher 0: left=fork0, right=fork1
- Philosopher 1: left=fork1, right=fork2
- Philosopher 2: left=fork2, right=fork3
- Philosopher 3: left=fork3, right=fork4
- Philosopher 4: left=fork4, right=fork0

Acquisition order:
- P0: fork0 -> fork1
- P1: fork1 -> fork2
- P2: fork2 -> fork3
- P3: fork3 -> fork4
- P4: **fork0 -> fork4** (right first, then left)

**Why no circular wait:**

For deadlock, we need: P0 holds fork0, waits for fork1; P1 holds fork1, waits for fork2; ... ; P4 holds fork4, waits for fork0.

But P4 acquires fork0 FIRST (right fork), not fork4.

Consider the worst case:
1. P0 holds fork0, waits for fork1
2. P1 holds fork1, waits for fork2
3. P2 holds fork2, waits for fork3
4. P3 holds fork3, waits for fork4
5. P4 cannot hold fork4 (tries fork0 first, blocked by P0)

So P3 gets fork4 and proceeds! No circular wait forms.

**General principle:** Breaking the circular ordering for even ONE philosopher prevents deadlock (this is the "resource ordering" solution).

**Rubric:**
- 10 points: No, with clear analysis showing circular wait is broken
- 8-9 points: Correct answer with good explanation
- 6-7 points: Correct answer but incomplete analysis
- 4-5 points: Wrong answer (Yes) but shows understanding of deadlock conditions
- 2-3 points: Mentions circular wait without proper analysis
- 0-1 points: Incorrect or missing answer

---

#### Question 1.2.2: Livelock with tryLock (10 points)

**Question:** "Is livelock possible with ReentrantLock.tryLock()? Show execution or explain why not."

**Expected Answer:**

**Yes, livelock IS possible** with tryLock().

**Livelock scenario:**

With tryLock(), philosophers release the first fork if they can't get the second. This prevents deadlock but can cause livelock:

```
Time 0: All philosophers try to acquire first fork
  P0 gets fork0, P1 gets fork1, P2 gets fork2, P3 gets fork3, P4 gets fork0 (fails)

Time 1: Each tries second fork
  P0 tries fork1 (held by P1) - FAILS, releases fork0
  P1 tries fork2 (held by P2) - FAILS, releases fork1
  P2 tries fork3 (held by P3) - FAILS, releases fork2
  P3 tries fork4 (available) - SUCCESS

Time 2: P3 eats, others retry from start
  P0 gets fork0, P1 gets fork1, P2 gets fork2
  P4 tries fork0 (held by P0) - FAILS

Time 3: Same pattern repeats
  P0 fails on fork1, releases fork0
  P1 fails on fork2, releases fork1
  P2 fails on fork3 (P3 eating), releases fork2
  ...
```

**Pathological scenario (worst case):**

If timing is such that all philosophers repeatedly:
1. Acquire first fork
2. Fail to acquire second fork
3. Release first fork
4. Retry

Then no philosopher ever eats, but all are actively running (livelock).

**Why it happens:**
- tryLock() is non-blocking: returns false immediately if lock unavailable
- Without backoff, all philosophers retry in lockstep
- "Courtesy" (releasing first fork) prevents deadlock but enables livelock

**Prevention:**
- Random backoff before retrying
- Different wait times per philosopher
- Adaptive algorithms

**Rubric:**
- 10 points: Yes, with clear execution trace showing livelock
- 8-9 points: Yes, with good explanation but informal scenario
- 6-7 points: Yes, but incomplete scenario
- 4-5 points: Wrong answer (No) but shows understanding of tryLock behavior
- 2-3 points: Mentions livelock without proper analysis
- 0-1 points: Incorrect or missing answer

---

## Part 2: Report (15 points)

### Report: Parallel Boruvka's Algorithm

**Requirements:**
1. Design and parallel constructs used
2. Correctness and data-race-free justification
3. Deadlock-free justification
4. Livelock-free justification
5. Test outputs and performance measurements

**Rubric:**

#### Section A: Design Summary (3 points)
- 3 points: Clear description of:
  - Parallel work-list processing
  - Lock/atomic strategy for component merging
  - How components are contracted
- 2 points: Good description with minor omissions
- 1 point: Basic description
- 0 points: No design summary

#### Section B: Correctness & Data-Race-Free (3 points)
- 3 points: Explains:
  - How mutual exclusion on components prevents races
  - Why merged components are correctly marked dead
  - How MST weight is correctly accumulated
- 2 points: Partial justification
- 1 point: Minimal justification
- 0 points: No justification

#### Section C: Deadlock-Free (3 points)
- 3 points: Clear argument:
  - Lock ordering strategy (e.g., by component ID)
  - Or tryLock with release-on-failure
  - Why circular wait cannot occur
- 2 points: Partial argument
- 1 point: Claims deadlock-free without justification
- 0 points: No justification

#### Section D: Livelock-Free (3 points)
- 3 points: Clear argument:
  - Progress guarantee (at least one component merges per round)
  - Why tryLock contention doesn't cause permanent livelock
  - Termination guarantee
- 2 points: Partial argument
- 1 point: Claims livelock-free without justification
- 0 points: No justification

#### Section E: Performance Measurements (3 points)
- 3 points: Complete data:
  - Sequential baseline
  - Speedup at 1, 2, 4, 8, 16 threads
  - Analysis of scaling behavior
- 2 points: Most measurements present
- 1 point: Limited measurements
- 0 points: No measurements
