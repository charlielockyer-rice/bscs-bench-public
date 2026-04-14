# COMP 421 Midterm Exam — Grading Rubric

**Spring 2024 — Total: 100 points**

---

## Question 1: Short Definitions (10 points)

Each term is worth 2 points.

Terms: **(a)** general semaphore, **(b)** best fit, **(c)** supervisor mode, **(d)** race condition, **(e)** multiprogramming

**Per-term rubric:**

- **0 pts** — Full credit
- **−1 pt** — Partially correct: missing some important detail, or some incorrect statement as part of the answer
- **−2 pts** — Many important errors or omissions, or no answer was given

---

## Question 2: Synchronization Using a Monitor (25 points)

### Basic operation of the shared buffer

- **−2 pts** — Upon a producer adding 2 items, only allows ONE CONSUMER to proceed

### Concurrency problems

- **−5 pts** — Allows STARVATION of a producer trying to add 2 items by producers each adding only 1 item
- **−2 pts** — Allows STARVATION due to the behavior of a Mesa monitor, with no protection added to the solution to control this (new processes may enter and take "your" turn, over and over again)

---

## Question 3: Synchronization Using Semaphores (25 points)

### Concurrency problems

- **−6 pts** — Allows STARVATION

---

## Question 4: The Critical Section Problem (15 points)

### Mutual exclusion

- **0 pts** — Full credit
- **−5 pts** — Incorrect, or no answer submitted

### Progress

- **0 pts** — Full credit
- **−5 pts** — Incorrect, or no answer submitted

### Bounded waiting

- **0 pts** — Full credit
- **−5 pts** — Incorrect, or no answer submitted

---

## Question 5: Compaction (10 points)

- **−2 pts** — Incorrectly coalesces so that ALLOCATED memory begins at address 0, rather than what the question asks for with FREE memory beginning at address 0

---

## Question 6: Hierarchical Page Tables (15 points)

### Page table structure

- **−9 pts** — Does not correctly treat either or both of the given page tables as a 2-level hierarchical (tree-structured) page table

### Comparing a PTE from the two page tables

- **−4 pts** — Does NOT compare corresponding level-2 PTEs from the two page tables
