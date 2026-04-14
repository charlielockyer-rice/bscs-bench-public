# COMP 421 Midterm Exam

**Spring 2024**

---

## Question 1: Short Definitions

*10 Points*

Give a *one-sentence* definition of each of the following five terms as they apply to the material in this course. For each, please give a general definition that covers the use of the term in the course, rather than only describing the specific example or examples where we have used that term. Type your answer in the space below each term.

**(a)** **general semaphore** *(2 points)*

**(b)** **best fit** *(2 points)*

**(c)** **supervisor mode** *(2 points)*

**(d)** **race condition** *(2 points)*

**(e)** **multiprogramming** *(2 points)*

---

## Question 2: Synchronization Using a Monitor

*25 Points*

Consider a synchronization problem similar to the standard Bounded-Buffer (i.e., Producer-Consumer) problem but with the following changes in the problem definition:

- There can be **any number** of producer processes and **any number** of consumer processes, **not** necessarily just a single one of each.

- Each time a producer process produces new items (i.e., each time around the `while` loop), the producer may produce **either 1 or 2** items, **rather than** always producing only a single item each time. If a producer produces two items like this, those two items must be added to the shared buffer *without any other items (e.g., from other producers) interleaved* between those two items.

Show a solution to this synchronization problem *using a single monitor, together with condition variables,* for **all** synchronization required. Specifically, your monitor must have the following two *entry procedures*:

- `AddToBuffer(struct item *data, int count)`: This entry procedure, called by a producer process, adds 1 or 2 items to the shared buffer. The value of `count` gives the number of items to add, and the values of those items come from `data[0]` and possibly also `data[1]`.

- `RemoveFromBuffer(struct item *data)`: This entry procedure, called by a consumer process, removes a single item from the shared buffer, putting that item into `data[0]`.

*Your monitor must have no other entry procedures*, and your two entry procedures must use only the parameters defined above. In addition, *you may not assume any form of unique process id for each process*.

Show your solution using pseudo-code similar to that used in the book or in the lectures to clearly describe your monitor's operation. Be sure to declare and initialize any variables you use in your solution. Also, be sure to clearly state *what type of monitor* you are using: Brinch Hansen, Hoare, or Mesa. Again, *you may use only a single monitor*, together with condition variables, for all synchronization needed in your solution for this question.

**Your solution must avoid all *deadlock* and *starvation***, but it should otherwise allow the **maximum** amount of concurrency you can.

---

## Question 3: Synchronization Using Semaphores

*25 Points*

Suppose that a room with a number of mobile, wheeled whiteboards is available for groups of students to make use of while working as a group in this room on some problem for some course they are taking. Each group of students occasionally gets together in this room to work together, works there on their group problem for a while, and then the group leaves the room.

There is a very large (unlimited) number of such groups of students, and each group, while working in this room, works independently of every other group. The problems being worked on by each group may be different, and at different times, any group may be working on an easier or a more complex part of that group's overall problem.

While a group is working together in this room, to help them work out various aspects of their problem while working as a group there, the group utilizes a number of the mobile, wheeled whiteboards, such as to sketch out and share ideas with other members of their group. Any whiteboard may be used by no more than a single group at a time.

Specifically, there are *N* total whiteboards in the room, and **you may assume** that *N* >= 3. When a group arrives at the room to work on their group's problem, the group (as a whole) calls the procedure

- `EnterRoom(count)`, where `count`, is a count of the number of whiteboards that group needs for this period of working on their group's problem in this room. The value of `count` must be > 0 and <= 3. A call to `EnterRoom` must not return until the needed number of whiteboards are available for that group's use while working in the room for this period. As long as sufficient whiteboards are available, any number of different groups may be working in this room at the same time.

When a group ends this period of working on their group's problem, the group (as a whole) calls the procedure

- `LeaveRoom(count)`, where `count` is the count of the number of whiteboards that group has been using during this period of working on the group's problem in this room.

You may assume that each group correctly calls each of these two procedures as described above, including that the `count` value passed to `LeaveRoom` by a group is the same the `count` value passed by that same group on its call to `EnterRoom` immediately proceeding this call to `LeaveRoom`. However, each time a group wants to enter the room to begin working again, the `count` value passed on this call to `EnterRoom` may be different than the `count` value passed by that group on its previous `EnterRoom` call.

**Design a solution to the scheduling problem for use of this room and the whiteboards in the room, satisfying the requirements described above.** Your solution must consist of your implementation of the two procedures `EnterRoom` and `LeaveRoom`.

Your solution must use only ***semaphores*** to control all of the necessary synchronization, with a separate process executing the behavior of each student group (i.e., one process per group, not one process per student). In addition, *you may not assume any form of unique process id for each process*.

Show your solution using pseudo-code similar to that used in the book or in the lectures, to clearly describe its operation. Be sure to declare and initialize any variables you use in your solution. Again, *you may use only semaphores*, for any synchronization needed in your solution for this question.

**Your solution must avoid all *deadlock* and *starvation***, but it should otherwise allow the **maximum** amount of concurrency you can.

---

## Question 4: The Critical Section Problem

*15 Points*

Consider the following proposed possible solution to the critical section problem for 2 processes. The code shown here is the same for both processes, with *i*, being either 0 or 1, giving the current process's process id:

```
int flag[2] = { 0, 0 };        /* shared variable */
int turn = 0;                   /* shared variable */

Entry section:

        flag[i] = 1;
        turn = i;

        while (flag[1-i] == 1) {

                if (turn != i) {
                        flag[i] = 2;
                        while (turn != i && flag[1-i] == 1)
                                ;       /* busy wait */
                        flag[i] = 1;
                }
        }

Exit section:

        flag[i] = 0;
```

**Mutual Exclusion:**
Does the proposed solution above guarantee *mutual exclusion*? *Clearly state "Yes" or "No"*; and if "Yes", give an informal proof of this, or if "no", give a counterexample showing that it does not.

**Progress:**
Does the proposed solution above guarantee *progress*? *Clearly state "Yes" or "No"*; and if "Yes", give an informal proof of this, or if "no", give a counterexample showing that it does not.

**Bounded Waiting:**
Does the proposed solution above guarantee *bounded waiting*? *Clearly state "Yes" or "No"*; and if "Yes", give an informal proof of this, or if "no", give a counterexample showing that it does not.

---

## Question 5: Compaction

*10 Points*

Consider some operating system running on hardware that uses only a BASE register and a LIMIT register for memory management; this system thus requires ***contiguous memory allocation*** for each process's address space. This computer has a total of 30,000 bytes of memory (all numbers in this question are given in decimal).

Suppose the operating system is currently running the following five processes:

- Process 0 is using 3000 bytes of memory beginning at address 1000.
- Process 1 is using 2000 bytes of memory beginning at address 6000.
- Process 2 is using 4000 bytes of memory beginning at address 10,000.
- Process 3 is using 2000 bytes of memory beginning at address 14,000.
- Process 4 is using 5000 bytes of memory beginning at address 20,000.

Suppose that the operating system now *performs compaction* to coalesce all ***free*** memory together ***to begin at address 0***. Clearly show the **BASE register** value and the **LIMIT register** value that the operating system should then use *after this compaction* for *each of these five processes*. For this question, you may ignore anything else that may be using memory, such as the operating system kernel, and may assume that only these 5 processes are using memory.

---

## Question 6: Hierarchical Page Tables

*15 Points*

Consider some operating system running on hardware that uses a tree-structured (hierarchical) page table format. And suppose you are given the address of the tree-structured (hierarchical) page table ***for each of two processes*** (i.e., you are given these two addresses).

Due to the nature of tree-structured (hierarchical) page tables, it may be possible to represent these two page tables using less physical memory in sum across the two page tables, due to the ability to have shared sub-trees in the page tables.

For simplicity in this question, you can assume that you can access memory in the same way using either a virtual address or a physical address. In other words, for this question, you may ignore the difference between a virtual address and a physical address when accessing memory in your solution.

***Design a procedure*** that, given the pointer to each of these two page tables, examines the two page tables and modifies the two page tables so that the two page tables each function the same as they each did before, but they take advantage of the ability to represent shared sub-trees to take up less total physical memory for the page tables, where possible. In particular, for each of the resulting two modified page tables, while the process for which this is the page table is executing, that page table must still function exactly the same as the corresponding original unmodified page table did.

For this question, assume that each virtual address has a 10-bit first-level index field and a 10-bit second-level index field. The size of each virtual address is 32 bits, and thus the size of the page offset field within each virtual address is 12 bits. And each of the individual PTEs at level 2 have the same format as a PTEs do on the RCS 421 hardware on which Yalnix runs for Lab 2.

***In your solution, you must show in detail how you access any parts of these two page tables, and must show in detail any modifications that you make to them.***

Show your solution using pseudo-code similar to that used in the book or in the lectures. Again, your procedure should take as input the address of the two page tables, and it should modify those two page tables (in place) to make use of shared sub-trees where possible.
