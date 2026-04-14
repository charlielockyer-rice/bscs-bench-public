---
total_points: 45
---

# COMP 322 Homework 2 - Written Questions Rubric

## Part 1: Written Questions (30 points)

### 1.1 Parallelizing the IsKColorable Algorithm

**Algorithm Reference:**
```
Input: Graph g = (V, E) and k in N.
Output: Does there exist f : V -> [k] such that for all {u, v} in E, f(u) != f(v)?

1  foreach Assignment f of a value in [k] to each node in V do
2      colorable <- True;
3      foreach {u, v} in E do
4          if f(u) = f(v) then
5              colorable <- False;
6              break;
7      if colorable = True then
8          return True;
9  return False;
```

---

#### Question 1.1.1: Parallel Version (10 points)

**Question:** "Create a parallel version using finish and async statements to maximize parallelism while ensuring correctness."

**Expected Answer:**

```java
// V = number of vertices, E = number of edges
// k^V total assignments to check
boolean isKColorable(Graph g, int k) {
    AtomicBoolean result = new AtomicBoolean(false);

    finish {
        for each Assignment f of a value in [k] to each node in V {
            async {
                if (result.get()) return; // early exit if solution found

                boolean colorable = true;
                for each {u, v} in E {
                    if (f(u) == f(v)) {
                        colorable = false;
                        break;
                    }
                }

                if (colorable) {
                    result.set(true);
                }
            }
        }
    }
    return result.get();
}
```

**Key points:**
1. Outer loop over assignments can be parallelized (async for each assignment)
2. Inner loop over edges can be sequential (break optimization preserved)
3. Need atomic/isolated access to shared result variable
4. Early termination possible but not required for correctness

**Alternative (nested parallelism for edge checking):**
The inner edge loop could also be parallelized, but this adds overhead and loses the break optimization.

**Rubric:**
- 10 points: Correct parallel version with:
  - async for each assignment
  - Proper synchronization for shared result (isolated/atomic)
  - finish to wait for all asyncs
- 8-9 points: Correct structure with minor issues (missing isolated, incomplete pseudocode)
- 6-7 points: Parallelization attempt with correctness issues
- 4-5 points: Uses async/finish but incorrect parallelization strategy
- 2-3 points: Shows understanding of async/finish but wrong application
- 0-1 points: Incorrect or missing answer

---

#### Question 1.1.2: Total Work Analysis (5 points)

**Question:** "What is the big-O for total WORK?"

**Expected Answer:**

WORK = O(k^|V| * |E|)

**Explanation:**
- Number of assignments: k^|V| (k colors for each of |V| vertices)
- Work per assignment: O(|E|) to check all edges
- Total: k^|V| * |E|

Note: In the best case (early termination), work could be less, but worst-case work is O(k^|V| * |E|).

**Rubric:**
- 5 points: Correct answer O(k^|V| * |E|) with clear explanation
- 4 points: Correct answer with minimal explanation
- 3 points: Correct factors but wrong combination (e.g., O(k^V + E))
- 2 points: Identifies k^V or |E| but not both
- 1 point: Attempt at analysis but incorrect
- 0 points: Incorrect or missing answer

---

#### Question 1.1.3: Larger Work Than Sequential? (5 points)

**Question:** "Can your parallel algorithm have LARGER WORK than sequential? Explain."

**Expected Answer:**

**Yes**, the parallel algorithm can have larger work than the sequential version.

**Explanation:**
- The sequential algorithm can terminate early when a valid coloring is found (line 8)
- Once the sequential algorithm returns True, it stops checking remaining assignments
- The parallel algorithm spawns all k^|V| asyncs at the start (or in batches)
- Even if one async finds a solution, other asyncs may continue running
- Worst case: Parallel does full k^|V| * |E| work while sequential might do much less

However, with proper early termination (checking AtomicBoolean), the parallel version can reduce extra work, but spawned asyncs that have already started will complete.

**Rubric:**
- 5 points: Yes with clear explanation of early termination difference
- 4 points: Yes with partial explanation
- 3 points: Correct answer but reasoning focuses on wrong aspect (e.g., overhead only)
- 2 points: Incorrect answer (No) but shows understanding of parallelism
- 1 point: Attempt at reasoning but incorrect
- 0 points: Incorrect or missing answer

---

#### Question 1.1.4: Smaller Work Than Sequential? (5 points)

**Question:** "Can your parallel algorithm have SMALLER WORK than sequential? Explain."

**Expected Answer:**

**No**, the parallel algorithm cannot have smaller total WORK than the sequential version (for the same input).

**Explanation:**
- WORK is the total number of operations across all tasks
- Both algorithms must check the same assignments
- The parallel version does at least as much work as sequential
- Parallelism reduces SPAN (time), not WORK
- The only way to reduce work is to skip checking assignments, which both algorithms can do with early termination

Note: If we consider "work" as wall-clock time, then parallel can finish faster (smaller span), but total WORK (operations) is never smaller.

**Rubric:**
- 5 points: No with clear explanation that Work is total operations, not time
- 4 points: No with partial explanation
- 3 points: Confused work with span but shows understanding
- 2 points: Incorrect answer (Yes) but discusses relevant concepts
- 1 point: Attempt at reasoning but incorrect
- 0 points: Incorrect or missing answer

---

#### Question 1.1.5: Data Race Analysis (5 points)

**Question:** "Is a data race possible? If so, is it benign?"

**Expected Answer:**

**Yes**, a data race is possible on the shared result variable.

**Analysis:**
- Multiple asyncs can simultaneously:
  - Read `result` (to check for early exit)
  - Write `result` (to set it to True)
- This is a data race: concurrent access with at least one write, no synchronization

**Is it benign?**

**Yes, it is benign** (with boolean result):
- The only value ever written is True
- Multiple writes of True to the same location don't affect correctness
- Reading True or False during a concurrent write doesn't affect correctness
  - If we read False but another async is writing True, we just do extra work
  - If we read True, we can exit early
- The final result is always correct

**To eliminate the race:**
Use `AtomicBoolean` or `isolated` blocks, but the race is benign without them.

**Rubric:**
- 5 points: Yes, race on result variable; Yes, benign because only writing True
- 4 points: Correct identification of race location with partial benign analysis
- 3 points: Identifies data race but wrong about benign/not benign
- 2 points: Says no data race but discusses synchronization needs
- 1 point: Mentions race without specific analysis
- 0 points: Incorrect or missing answer

---

## Part 2: Report (15 points)

### Report: Parallel GitHub Contributors

**Requirements:**
1. Summary of parallel algorithm design
2. Correctness and data-race-free justification
3. Expected work analysis as function of N (number of repos or contributors)

**Rubric:**

#### Section A: Algorithm Summary (5 points)
- 5 points: Clear description of:
  - How futures are used for parallel API calls
  - How data aggregation is performed
  - How UI blocking is avoided
- 4 points: Good description with minor omissions
- 3 points: Basic description, missing key details
- 2 points: Incomplete description
- 1 point: Minimal description
- 0 points: No summary

#### Section B: Correctness Justification (5 points)
- 5 points: Explains:
  - Why result is equivalent to sequential version
  - How data races are avoided (futures, immutable data)
  - Why aggregation is correct (associative operations)
- 4 points: Good justification with minor gaps
- 3 points: Basic justification
- 2 points: Incomplete justification
- 1 point: Minimal justification
- 0 points: No justification

#### Section C: Work Analysis (5 points)
- 5 points: Correct analysis:
  - N = number of repos
  - M = number of contributors
  - Work = O(N) API calls + O(M) aggregation
  - Clear explanation of parallelism benefits
- 4 points: Correct analysis with minor errors
- 3 points: Partial analysis
- 2 points: Attempt at analysis but incorrect
- 1 point: Minimal analysis
- 0 points: No analysis
