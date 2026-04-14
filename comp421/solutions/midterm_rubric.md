# COMP 421 Midterm Exam — Grading Rubric

**Spring 2024 — Total: 100 points**

---

## Question 1: Short Definitions (10 points)

Each term is worth 2 points.

Terms: **(a)** general semaphore, **(b)** best fit, **(c)** supervisor mode, **(d)** race condition, **(e)** multiprogramming

### Reference Solutions

**(a)** ***general semaphore:*** Also known as a counting semaphore, a type of semaphore, a special type of integer-valued variable used for synchronization and on which only operations P( ) and V( ), both atomic, can be performed after the semaphore's value has been initialized, that is used in a way in which the semaphore can take on any arbitrary non-negative integer value.

**(b)** ***best fit:*** A strategy for memory allocation in which, for some requested new allocation of memory, the smallest existing free chunk of memory that is large enough for the new request is chosen to satisfy this request, thus leaving behind the smallest leftover remaining free chunk after this allocation.

**(c)** ***supervisor mode:*** Also known as kernel mode, a hardware mode of CPU execution in which the CPU places essentially no restrictions on instruction execution, including allowing the execution of certain instructions (either the instruction itself or the instruction with certain operands), known as privileged instructions.

**(d)** ***race condition:*** A condition in which multiple processes (or threads) are using shared data and the outcome of this sharing depends on the order of events during this execution, yet the order is not determined by the program. [Note that the outcome of *any* concurrent sharing of data will depend on the order of the accesses to that data; what makes it a *race* condition is that this order is not controlled or in effect somehow determined by the program.]

**(e)** ***multiprogramming:*** A technique in which the operating system keeps multiple processes in memory at the same time, such that if the currently running process must be blocked for some reason (such as starting some I/O operation), the operating system can then context switch immediately to one of the other processes in memory in order to keep the CPU busy, thus improving CPU utilization. [Note that multiprogramming and time sharing are not same thing.]

### Deductions

Note: A definition that says something like "A system [or method or strategy, etc.] for [accomplishing something]" without saying *how* this is accomplished is not a good definition.

**Per-term rubric:**

- **0 pts** — Full credit
- **−1 pt** — Partially correct: missing some important detail, or some incorrect statement as part of the answer
- **−2 pts** — Many important errors or omissions, or no answer was given

---

## Question 2: Synchronization Using a Monitor (25 points)

### Reference Solution

The primary danger of starvation to be concerned about is producers adding only 1 item starving producers trying to add 2 items. Consumers always take out only 1 item, immediately making it possible for a producer trying to add 1 item to proceed but not making it possible for a producer trying to add 2 items to proceed. If this happens infinitely often (i.e., forever), the producer trying to add 2 items starves.

This solution is intended to be a Hoare monitor.

```
#define BYPASS_LIMIT    (some big number, e.g., 10000)

monitor buffer {
    struct item buffer[N];          // the buffer itself
    int present = 0;                // current number of items in the buffer
    int in = 0, out = 0;           // index at which next to add to or remove from the buffer
    int waiting[2] = { 0, 0 };     // number of producers for each count that are currently waiting
    int bypassed = 0;               // count of times count == 2 has been bypassed while waiting
    condition full[2];              // condition variable to wait on for each count if buffer is full
    condition empty;                // condition variable to wait on if the buffer is empty

    entry AddToBuffer(struct item *data, int count)
    {
        if (present + count > N || (count == 1 && bypassed > BYPASS_LIMIT)) {
            waiting[count-1]++;
            full[count-1].wait;
            waiting[count-1]--;
        }
        if (count == 2) bypassed = 0;
        else if (count == 1 && waiting[2-1] > 0) bypassed++;
        buffer[in] = data[0];
        in = (in + 1) % N;
        if (count == 2) {
            buffer[in] = data[1];
            in = (in + 1) % N;
        }
        present += count;
        if (present > 1) empty.signal;
        if (count == 2 && present > 1) empty.signal;
    }

    entry RemoveFromBuffer(struct item *data)
    {
        if (present == 0) empty.wait;
        data[0] = buffer[out];
        present--;
        out = (out + 1) % N;
        if (present < N-1 && waiting[2-1] > 0)
            full[2-1].signal;
        if (present < N && waiting[1-1] && bypassed < BYPASS_LIMIT)
            full[1-1].signal;
    }
}
```

### Deductions

**Basic operation of the shared buffer:**

- **−2 pts** — Upon a producer adding 2 items, only allows ONE CONSUMER to proceed

**Concurrency problems:**

- **−5 pts** — Allows STARVATION of a producer trying to add 2 items by producers each adding only 1 item
- **−2 pts** — Allows STARVATION due to the behavior of a Mesa monitor, with no protection added to the solution to control this (new processes may enter and take "your" turn, over and over again)

---

## Question 3: Synchronization Using Semaphores (25 points)

### Reference Solutions

Two very different solutions are presented. The first is very simple and lets the semaphores do all the work, including avoiding starvation. The second is much more complex, explicitly controlling starvation using the semaphores essentially only for mutual exclusion.

**Solution #1 (Simple):**

```
semaphore mutex = 1;            // semaphore for mutual exclusion
semaphore avail = N;            // counts the number of whiteboards currently available

entry EnterRoom(int count)
{
    P(mutex);
    for (int i = 0; i < count; i++)
        P(avail);
    V(mutex);
}

entry LeaveRoom(int count)
{
    for (int i = 0; i < count; i++)
        V(avail);
}
```

**Solution #2 (Complex, explicit starvation control):**

```
#define BYPASS_LIMIT            (some big number, e.g., 100000, used to avoid starvation)

int whiteboards = N;            // current number of whiteboards that are available

semaphore mutex = 1;                        // semaphore for mutual exclusion
semaphore queue[3] = { 0, 0, 0 };          // semaphore for each whiteboard count (1, 2, 3) to wait on

int waiting[3] = { 0, 0, 0 };              // number of groups for each count (1, 2, 3) currently waiting
int bypassed[3] = { 0, 0, 0 };             // count of times each count (1, 2, 3) has been bypassed

entry EnterRoom(count)
{
    P(mutex);
    if (whiteboards < count || !bypass_ok(count)) {
        waiting[count-1]++;
        V(mutex);
        P(queue[count-1]);      // we "inherit" mutex still "locked"
        waiting[count-1]--;
    }
    whiteboards -= count;
    bypassed[count-1] = 0;
    for (int i = count + 1; i <= 3; i++)
        if (waiting[i-1] > 0) bypassed[i-1]++;
    for (i = count; i >= 1; i--)
        if (waiting[i-1] > 0 && bypass_ok(i)) break;
    if (i >= 1)
        V(queue[i-1]);          // the group we wake up "inherits" control of the mutex
    else
        V(mutex);
}

entry LeaveRoom(count)
{
    P(mutex);
    whiteboards += count;
    for (i = count; i >= 1; i--)
        if (waiting[i-1] > 0 && bypass_ok(i)) break;
    if (i >= 1)
        V(queue[i-1]);
    else
        V(mutex);
}

bool bypass_ok(count)
{
    for (int i = count + 1; i <= 3; i++)
        if (bypassed[i-1] >= BYPASS_LIMIT) return (false);
    return (true);
}
```

### Deductions

**Concurrency problems:**

- **−6 pts** — Allows STARVATION

---

## Question 4: The Critical Section Problem (15 points)

### Reference Solution

**(a)** ***Mutual exclusion:*** **Yes.** At the top of the "while" loop, a process checks the flag variable for the other process and can only leave the loop if flag[1-i] is not equal to 1. Anytime the process is evaluating this expression at the top of the "while" loop, its own flag variable is always equal to 1: a process sets flag[i] = 1 before entering the "while" loop, and if it changes its flag inside the loop, it always sets flag[i] = 1 again before returning to the top of the loop. Thus, a process can only exit the "while" loop if its own flag is 1 and the other process's flag is not 1.

**(b)** ***Progress:*** **Yes.** If only process i wants to enter the critical section and process 1-i is not in the critical section, then flag[1-i] == 0, so process i will exit the while loop. If both processes want to enter, both have set turn = i, but exactly one executed that line last. The other process will enter the body of the "if", set its flag to 2, allowing the process that set turn last to exit the outer while loop and enter the critical section.

**(c)** ***Bounded waiting:*** **No.** Counterexample: Suppose processes 0 and 1 both begin the entry section, and process 1 executes turn = 1 after process 0. Process 1 enters the critical section. When process 1 completes and begins the entry section again, it could set flag[1] = 1 and turn = 1 before process 0 sees that flag[1] was briefly 0. Since process 0 still has flag[0] == 2 in the inner while loop, process 1 immediately exits the outer while loop and enters the critical section again. This pattern can repeat infinitely, starving process 0.

### Deductions

**Mutual exclusion (5 points):**

- **0 pts** — Full credit
- **−5 pts** — Incorrect, or no answer submitted

**Progress (5 points):**

- **0 pts** — Full credit
- **−5 pts** — Incorrect, or no answer submitted

**Bounded waiting (5 points):**

- **0 pts** — Full credit
- **−5 pts** — Incorrect, or no answer submitted

---

## Question 5: Compaction (10 points)

### Reference Solution

Total memory in use: 3000 + 2000 + 4000 + 2000 + 5000 = 16,000 bytes.
Total memory: 30,000 bytes.
Free memory: 30,000 - 16,000 = 14,000 bytes.

After compaction, the 14,000 bytes of free memory begin at address 0. The five processes are placed consecutively above that:

| Process | BASE | LIMIT |
|---------|------|-------|
| Process 0 | 14,000 | 3000 |
| Process 1 | 17,000 | 2000 |
| Process 2 | 19,000 | 4000 |
| Process 3 | 23,000 | 2000 |
| Process 4 | 25,000 | 5000 |

### Deductions

- **−2 pts** — Incorrectly coalesces so that ALLOCATED memory begins at address 0, rather than what the question asks for with FREE memory beginning at address 0

---

## Question 6: Hierarchical Page Tables (15 points)

### Reference Solution

A two-level hierarchical page table with 10-bit level-1 index (1024 entries) and 10-bit level-2 index (1024 entries per table). Each level-1 entry points to a level-2 table. Level-2 entries have the same PTE format as the RCS 421 hardware (valid, kprot, uprot, pfn).

```
#define LEVEL1_WIDTH    10
#define LEVEL2_WIDTH    10
#define OFFSET_WIDTH    12

#define LEVEL1_ENTRIES  (1 << LEVEL1_WIDTH)     // 1024
#define LEVEL2_ENTRIES  (1 << LEVEL2_WIDTH)     // 1024

MakeSharedSubtrees(struct pte **table1, struct pte **table2)
{
    for (int level1a = 0; level1a < LEVEL1_ENTRIES; level1a++) {
        for (int level1b = 0; level1b < LEVEL1_ENTRIES; level1b++) {
            if (CanShareLevel2(table1[level1a], table2[level1b])) {
                free the memory for the level-2 table at address table2[level1b];
                table2[level1b] = table1[level1a];
            }
        }
    }
}

CanShareLevel2(struct pte *table1, struct pte *table2)
{
    if (table1 == NULL || table2 == NULL) return (0);
    for (int level2 = 0; level2 < LEVEL2_ENTRIES; level2++) {
        if (table1[level2].valid == 0 && table2[level2].valid == 0) continue;
        if (table1[level2].valid != table2[level2].valid) return (0);
        if (table1[level2].kprot != table2[level2].kprot) return (0);
        if (table1[level2].uprot != table2[level2].uprot) return (0);
        if (table1[level2].pfn   != table2[level2].pfn)   return (0);
    }
    return (1);
}
```

### Deductions

**Page table structure:**

- **−9 pts** — Does not correctly treat either or both of the given page tables as a 2-level hierarchical (tree-structured) page table

**Comparing PTEs from the two page tables:**

- **−4 pts** — Does NOT compare corresponding level-2 PTEs from the two page tables
