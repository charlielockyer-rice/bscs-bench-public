# Solutions for Midterm Exam

**COMP 421/ELEC 421, COMP 521/ELEC 552**
**Operating Systems and Concurrent Programming**
**Spring 2024**

For some of the questions on the midterm exam, there is more than one way to solve them correctly. I give one possible correct solution for each question below.

---

## Question 1: Short Definitions

The one-sentence definitions below, I believe, essentially contain all key aspects of the definition of each term, although some of this detail may not strictly be required for an acceptable answer on this question.

Note that a definition that says something like "A system [or method or strategy, etc.] for [accomplishing something]" without saying *how* this is accomplished is not a good definition. Simply saying what is accomplished without indicating *how* it is accomplished does not really define the term.

**(a)** ***general semaphore:*** Also known as a counting semaphore, a type of semaphore, a special type of integer-valued variable used for synchronization and on which only operations P( ) and V( ), both atomic, can be performed after the semaphore's value has been initialized, that is used in a way in which the semaphore can take on any arbitrary non-negative integer value.

**(b)** ***best fit:*** A strategy for memory allocation in which, for some requested new allocation of memory, the smallest existing free chunk of memory that is large enough for the new request is chosen to satisfy this request, thus leaving behind the smallest leftover remaining free chunk after this allocation.

**(c)** ***supervisor mode:*** Also known as kernel mode, a hardware mode of CPU execution in which the CPU places essentially no restrictions on instruction execution, including allowing the execution of certain instructions (either the instruction itself or the instruction with certain operands), known as privileged instructions.

**(d)** ***race condition:*** A condition in which multiple processes (or threads) are using shared data and the outcome of this sharing depends on the order of events during this execution, yet the order is not determined by the program. [Note that the outcome of ***any*** concurrent sharing of data will depend on the order of the accesses to that data; what makes it a ***race*** condition is that this order is not controlled or in effect somehow determined by the program.]

**(e)** ***multiprogramming:*** A technique in which the operating system keeps multiple processes in memory at the same time, such that if the currently running process must be blocked for some reason (such as starting some I/O operation), the operating system can then context switch immediately to one of the other processes in memory in order to keep the CPU busy, thus improving CPU utilization. [Note that multiprogramming and time sharing are not same thing.]

---

## Question 2: Synchronization Using a Monitor

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

---

## Question 3: Synchronization Using Semaphores

Below are two very different solutions for this question. The first solution is very simple and essentially lets the semaphores do all the work, including avoiding starvation. The second solution is much more complex, as it explicitly controls the starvation itself, using the semaphores essentially only for mutual exclusion.

### Solution #1:

```
semaphore mutex = 1;            // semaphore for mutual exclusion
semaphore avail = N;            // counts the number of whiteboards currently available

entry EnterRoom(int count)
{
    // Make everyone wait the same way to get their whiteboards, and make sure that we can get
    // the full "count" of whiteboards we need before anyone else can take the last one, which could
    // otherwise lead to a deadlock.
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

### Solution #2:

```
#define BYPASS_LIMIT            (some big number, e.g., 100000, used to avoid starvation)

int whiteboards = N;            // current number of whiteboards that are available

semaphore mutex = 1;                        // semaphore for mutual exclusion
semaphore queue[3] = { 0, 0, 0 };          // semaphore for each whiteboard count (1, 2, 3) to wait on

int waiting[3] = { 0, 0, 0 };              // number of groups for each count (1, 2, 3) currently waiting
int bypassed[3] = { 0, 0, 0 };             // count of times each count (1, 2, 3) has been bypassed while waiting
// we really only need this for count 2 or 3, but it's easier to keep this parallel to the other arrays

entry EnterRoom(count)
{
    P(mutex);
    // if not enough whiteboards are available or if we've bypassed another count too much
    if (whiteboards < count || !bypass_ok(count)) {
        // then wait until we can get our whiteboards, making us visible in the waiting count
        waiting[count-1]++;
        V(mutex);
        P(queue[count-1]);      // we "inherit" mutex still "locked" and so do not do P(mutex) here
        waiting[count-1]--;
    }
    whiteboards -= count;
    bypassed[count-1] = 0;      // we got our whiteboards, so reset our bypassed back to 0
    // if any with a higher count is waiting, we just bypassed them to use whiteboards before them
    for (int i = count + 1; i <= 3; i++)
        if (waiting[i-1] > 0) bypassed[i-1]++;
    // if any other waiting group can also now get their whiteboards, let one of them do so
    // (this is needed when, for example a count 3 leaves, so they let in a 2 (us) and we can let in a 1)
    for (i = count; i >= 1; i--)    // check in decreasing difficulty of getting that many whiteboards
        if (waiting[i-1] > 0 && bypass_ok(i)) break;
    if (i >= 1)
        V(queue[i-1]);          // the group we wake up "inherits" control of the mutex
    else
        V(mutex);               // only unlock mutex if we didn't V a new group
}

entry LeaveRoom(count)
{
    P(mutex);
    whiteboards += count;       // we're no longer using those whiteboards
    // if any other waiting group can now get their whiteboards, let one of them do so
    for (i = count; i >= 1; i--)    // check in decreasing difficulty of getting that many whiteboards
        if (waiting[i-1] > 0 && bypass_ok(i)) break;
    if (i >= 1)
        V(queue[i-1]);          // the group we wake up "inherits" control of the mutex
    else
        V(mutex);               // only unlock mutex if we didn't V a new group
}

bool bypass_ok(count)       // check if it's still OK for us to bypass everyone in more difficult counts
{
    for (int i = count + 1; i <= 3; i++)
        if (bypassed[i-1] >= BYPASS_LIMIT) return (false);  // not OK to bypass again
    return (true);                                           // OK for us to bypass this time
}
```

---

## Question 4: The Critical Section Problem

**(a)** ***Mutual exclusion:*** Yes, this proposed solution guarantees mutual exclusion.

At the top of the "while" loop, a process checks the *flag* variable for the other process and can only leave the loop if *flag[1-i]* is not equal to 1. And anytime the process is evaluating this expression at the top of the "while" loop, its own *flag* variable is always equal to 1: a process sets *flag[i] = 1* before entering the "while" loop, and, if it changes its *flag* inside the loop, it always sets *flag[i] = 1* again before returning to the top of the loop to reevaluate this expression. Thus, a process can only exit the "while" loop if its own *flag* is 1 and the other process's *flag* is not 1.

**(b)** ***Progress:*** Yes, the proposed solution does guarantee progress.

Suppose that, for some value of *i*, only process *i* now wants to enter the critical section and process 1-*i* is not currently executing in the critical section. If process 1-*i* is executing in the remainder section, then it has no effect on the selection of the next process to enter the critical section; *flag[1-i] == 0*, so process *i* will exit either (or both) of the "while" loops in the entry section that it is executing in. If, instead, process 1-*i* is currently executing in the exit section, then within finite time it will set *flag[1-i] = 0* and enter the remainder section.

Suppose, instead, that both processes currently want to enter the critical section, and that both have set their own *turn[i] = 1* and have entered the outer "while" loop. Before entering that "while" loop, they both executed *turn = i*, but exactly one of them executed that line of code *last*. Thus, the other of the two processes (the one that executed that line of code *first*) will enter the body of the "if", whereas the other process (the one that executed that line of code *last*) will not. The one that enters the body of the "if" will set its *flag[i]* to a value other than 1 (specifically, the value 2), allowing the other process (in finite time) to exit the outer "while" loop, complete the entry section, and enter the critical section. The process executing the inner "while" loop remains in that loop until the process now in the critical section (again in finite time) finishes the entry section and executes the exit section, setting its *flag[i] = 0*.

Thus, in all cases, only processes in the entry and exit section affect the selection of the next process to enter the critical section, and this selection cannot be postponed indefinitely.

**(c)** ***Bounded waiting:*** No, the proposed solution does not guarantee bounded waiting.

Suppose processes 0 and 1 both begin the entry section at about the same time, and suppose that process 1 executes the line of code *turn = i* (thus, *turn = 1*) *after* process 0 did so. Thus, inside the outer "while" loop, process 0 will enter the inner "while" loop, whereas process 1 will not (since *turn* is currently equal to 1). Thus, process 1 will be able to enter the critical section.

Now suppose that process 1 completes the critical section, executes the exit section, and begins the entry section again. Even though process 1 set *flag[1] = 0* in the exit section, it is possible that it could set *flag[1] = 1* again in the entry section *before* process 0 gets a chance to see (at the top of the inner "while" loop) that *flag[1]* was equal to 0 for some time. Since process 1 just began the entry section again, process 1 will also have set *turn = 1*. Process 0 is still executing in the inner "while" loop, and thus *flag[0] == 2* now, allowing process 1 to immediately exit the outer "while" loop and enter the critical section again.

This same pattern could repeat infinitely often, over and over again entering the critical section while process 0 has been waiting the whole time. Thus, this solution does not achieve bounded waiting.

---

## Question 5: Compaction

There is a total of 3000 + 2000 + 4000 + 2000 + 5000 = 16,000 bytes of memory in use.
The question states that the computer has a total of 30,000 bytes of memory.
So there are 30,000 - 16,000 = 14,000 bytes of memory currently free.

After compaction, these 14,000 bytes of memory must appear in a single contiguous chunk of memory beginning at address 0.

Each of the 5 process's memory should then appear consecutively above that, with process 4's memory finally ending at the top of memory, at address 30,000.

|           | BASE   | LIMIT |
|-----------|--------|-------|
| Process 0 | 14,000 | 3000  |
| Process 1 | 17,000 | 2000  |
| Process 2 | 19,000 | 4000  |
| Process 3 | 23,000 | 2000  |
| Process 4 | 25,000 | 5000  |

---

## Question 6: Hierarchical Page Tables

A tree-structured (hierarchical) page table consists of a "tree" of tables, with each entry in each table giving the address of the corresponding table at the next level down in the tree; the entries at the bottom level give the actual (i.e., conventional) page table information, such as protection bits and the PFN of the physical page mapped by that entry.

The question states that this hierarchical page table has two levels, so each entry in the top-level table (i.e., the level-1 table) simply gives the address of the corresponding level-2 table. There is always only a single level-1 table. The question also states that there are 10 bits in the level-1 index, so there are 2^10 = 1024 entries in the level-1 table. And there are 10 bits in the level-2 index, so there are also 2^10 = 1024 entries in each of the 1024 level-2 tables. But, in a hierarchical page table, any given level-2 table does not exist (i.e., is not allocated) if the corresponding entry in the level-1 table is a "null" pointer.

```
#define LEVEL1_WIDTH    10          // number of bits in a level-1 index
#define LEVEL2_WIDTH    10          // number of bits in a level-2 index
#define OFFSET_WIDTH    12          // number of bits in a page offset

#define LEVEL1_ENTRIES  (1 << LEVEL1_WIDTH)     // number of entries at level 1
#define LEVEL2_ENTRIES  (1 << LEVEL2_WIDTH)     // number of entries in each level 2

MakeSharedSubtrees(struct pte **table1, struct pte **table2)
                                    // the address of the two level-1 tables
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

// return true (nonzero) if these two level-2 tables can be shared; and false (zero) otherwise
CanShareLevel2(struct pte *table1, struct pte *table2)
{
    // if either (or both) level-2 tables don't exist, then can't share them (or nothing to share)
    if (table1 == NULL || table2 == NULL) return (0);
    // both level-2 tables exist, so now check all of the individual entries in them
    for (int level2 = 0; level2 < LEVEL2_ENTRIES; level2++) {
        // if both are invalid, then these two PTEs could be shared regardless of the other fields
        if (table1[level2].valid == 0 && table2[level2].valid == 0) continue;
        // otherwise, if any of the fields do not match, then these level-2s can't be shared
        if (table1[level2].valid != table2[level2].valid) return (0);
        if (table1[level2].kprot != table2[level2].kprot) return (0);
        if (table1[level2].uprot != table2[level2].uprot) return (0);
        if (table1[level2].pfn   != table2[level2].pfn)   return (0);
    }
    // if not already returned, then these two level-2 tables can be shared
    return (1);
}
```
