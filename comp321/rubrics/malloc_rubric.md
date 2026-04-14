---
total_points: 20
---

# Project 5: Malloc - Written Questions Rubric

## Section: Design Description (10 points)

**Question:** "Replace with an English description of your submission's design."

**Expected Answer:**
A complete design description should cover the following components:

1. **Block Structure (2 pts):**
   - Header format: size, allocation status bit(s)
   - Footer (if used): what information, when used
   - Minimum block size and why
   - Alignment requirements (typically 16 bytes)

   Example: "Each block has a 8-byte header containing the block size and allocated bit in the low-order bit. Free blocks also have an 8-byte footer with the same information to enable coalescing."

2. **Free List Organization (2 pts):**
   - Implicit list, explicit list, or segregated lists
   - If explicit: singly or doubly linked
   - If segregated: size class boundaries
   - Where free list pointers are stored

   Example: "I use segregated free lists with 10 size classes: {16-32}, {33-64}, {65-128}, ..., {4097+}. Each free block contains prev/next pointers in the payload area."

3. **Allocation Policy (2 pts):**
   - Search strategy: first fit, next fit, best fit
   - How size class is determined (if segregated)
   - When to search larger size classes

   Example: "For allocation, I use first-fit within each size class, starting from the smallest class that could fit the request and moving to larger classes if no block is found."

4. **Splitting Strategy (1.5 pts):**
   - When to split blocks
   - Minimum remainder size to split
   - What happens to remainder

   Example: "Blocks are split if the remainder would be at least the minimum block size (32 bytes). The remainder is added to the appropriate free list."

5. **Coalescing Strategy (1.5 pts):**
   - Immediate vs. deferred coalescing
   - How adjacent free blocks are detected
   - Boundary tag usage for previous block

   Example: "I use immediate coalescing on free. The footer of the previous block (if free) and header of the next block are checked to merge adjacent free blocks."

6. **Heap Extension (1 pt):**
   - When sbrk is called
   - How much to extend
   - How new space is integrated

   Example: "When no suitable block is found, I extend the heap by the requested size rounded up to PAGE_SIZE. The new space is coalesced with any free block at the end of the heap."

**Rubric:**
- Full credit (10 pts): All 6 components clearly described with specific details about data structures and algorithms.
- 8-9 pts: Most components covered well, minor gaps in 1-2 areas.
- 6-7 pts: Covers block structure and free list but missing details on allocation/coalescing strategy.
- 4-5 pts: Basic description present but lacks detail on multiple components.
- 2-3 pts: Very brief, missing most structural details.
- 0-1 pt: Missing or only states "I implemented malloc".

---

## Section: checkheap() Description (10 points)

**Question:** "Replace with an English description of your submission's heap consistency checker."

**Expected Answer:**
A complete checkheap description should explain what invariants are checked:

1. **Block-Level Checks (3 pts):**
   - Header/footer consistency (size and alloc bit match)
   - Block size >= minimum block size
   - Block size is properly aligned
   - Block lies within heap boundaries (between heap_start and heap_end)
   - Payload pointer is aligned

   Example: "For each block, I verify that the header size matches the footer size, the block size is at least 32 bytes, and the block doesn't extend past the heap end."

2. **Free List Checks (3 pts):**
   - Every block in free list is marked as free
   - No allocated blocks in free list
   - Free list pointers point to valid heap addresses
   - Prev/next pointers are consistent (doubly-linked list)
   - Free list covers all free blocks (no orphaned free blocks)

   Example: "I traverse each segregated free list and verify: (1) each block's allocated bit is 0, (2) next->prev == current, and (3) each block falls within its size class."

3. **Coalescing Check (2 pts):**
   - No two consecutive free blocks (if immediate coalescing)
   - Every free block followed by allocated block or heap end

   Example: "I scan the entire heap and verify that no free block is immediately followed by another free block, which would indicate a coalescing failure."

4. **Count Consistency (1 pt):**
   - Number of free blocks in heap matches number in free lists
   - (For segregated) blocks are in correct size classes

   Example: "I count free blocks during a linear heap scan and compare against the count from traversing all free lists. These must match."

5. **Additional Checks (1 pt):**
   - Prologue/epilogue blocks present and correctly formatted
   - All blocks accounted for (no gaps between blocks)
   - Heap size matches sum of all block sizes

**Rubric:**
- Full credit (10 pts): Describes checks for block consistency, free list integrity, coalescing, and count verification. Shows understanding of what could go wrong.
- 8-9 pts: Covers most categories with good detail, minor gaps.
- 6-7 pts: Covers block and free list checks but missing coalescing or count checks.
- 4-5 pts: Basic checks described but lacks systematic coverage.
- 2-3 pts: Very brief, only mentions 1-2 checks.
- 0-1 pt: Missing or only states "I check the heap".

**Note:** The description should be detailed enough that someone could implement the checker from the description. Vague statements like "I check that everything is correct" receive minimal credit.
