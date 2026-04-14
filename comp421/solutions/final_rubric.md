# COMP 421 Final Exam — Grading Rubric

**Spring 2024 — Total: 100 points**

---

## Question 1: Short Definitions (10 points)

Each term is worth 2 points.

Terms: **(a)** capability, **(b)** cylinder group, **(c)** symbolic link, **(d)** message digest, **(e)** extent

### Reference Solutions

**(a)** ***capability:*** A protected reference to some object, such that possession of the capability by some process allows that process to perform the operations indicated in the capability on that object, and such that the process cannot modify or forge the capability. (*Note that* a capability is not just this information, such as written down somewhere, but *possession* of the capability is what *gives* the process the ability to do those operations on that object. The process is *capable* of performing those operations on that object specifically if it possesses that *capability*.)

**(b)** ***cylinder group:*** A contiguous subset of the cylinders on a hard disk on which a file system is stored, containing a redundant copy of the file system's superblock, a subset of the data blocks of the file system, a subset of the inodes of the file system, and a list (e.g., a bitmap) of the free blocks and of the free inodes within that cylinder group.

**(c)** ***symbolic link:*** A form of name in a file system that, in contrast to a hard link, which represents the name as a link directly to the inode of the target file, instead represents it by the symbolic pathname of the file linked to, such that when looking up a pathname containing a symbolic link, results in traversing to the target of that symbolic link before completing the original pathname lookup.

**(d)** ***message digest:*** The output of a secure cryptographic one-way hash function, such as SHA-1 or SHA-2 or SHA-3, applied to some message, such that it is computationally infeasible to find the input or any other input that produced this output value. (*Note that* the message digest, that is, the output of the hash function, is *not* and need not be *unique*.)

**(e)** ***extent:*** A contiguous range of disk blocks (or similar for other forms of storage), representing an allocated unit or a free unit of storage, generally identified by the block number of the first block of the extent and the count of contiguous blocks in the extent.

### Deductions

**Per-term rubric:**

- **0 pts** — Full credit
- **−1 pt** — Partially correct: missing some important detail, or some incorrect statement as part of the answer
- **−2 pts** — Many important errors or omissions, or no answer was given

Note: A definition that says something like "A system [or method or strategy, etc.] for [accomplishing something]" without saying *how* this is accomplished is not a good definition.

---

## Question 2: LoadProgram and Yalnix Demand Paging (25 points)

### Reference Solution

This solution requires changes in two different parts of the Yalnix kernel: in LoadProgram and in the TRAP_MEMORY handler function.

**In LoadProgram, make the following changes:**

- For each *text* or *data* PTE, initialize `valid = 0` in that PTE, and do *not* allocate a physical page for that virtual page.

- For each *data* PTE, initialize both the `uprot` and `kprot` fields in that PTE to `PROT_READ | PROT_WRITE`.

- For each *text* PTE, initialize the `uprot` field in that PTE to `PROT_READ | PROT_EXEC`, and initialize the `kprot` field in it to `PROT_READ | PROT_WRITE`. By initializing the kernel protection for the text pages this way, the kernel will be able later to do the `read()` into that virtual page if a page fault occurs for it; in that case, the `kprot` field in that PTE will then be changed to `PROT_READ | PROT_EXEC` after the `read()`, matching its `uprot` value.

- For each invalid PTE, initialize that entire PTE (all bits) to 0, not just the `valid` bit. This allows the kernel to determine why the `valid` bit is 0 in any type of PTE: if the *entire* PTE is all 0, then this virtual page is truly invalid, whereas if the protection bits are *not* all 0, then this virtual page has only been marked as invalid to support a demand paging load of that page.

- Do not do the `read(fd, MEM_INVALID_SIZE, li.text_size+li.data_size)` call; do not read any of the text or data pages during LoadProgram.

- Save the value of the file descriptor number `fd` in `pcb->exec_fd`.

- Do not do the `close(fd)` call at the bottom of LoadProgram; leave the file descriptor open.

**In the TRAP_MEMORY handler, add the following initial code:**

```c
addr = DOWN_TO_PAGE(info->addr);    // the address of the beginning of the page
vpn = addr >> PAGESHIFT;
pcb = address of the PCB for the current process;
pt = virtual address of current Region 0 page table;

if (pt[vpn].valid == 0 && pt[vpn].kprot != 0) {

    // This is a page fault for the page at virtual address addr
    pt[vpn].pfn = AllocateFreePage();
    pt[vpn].valid = 1;
    WriteRegister(REG_TLB_FLUSH, addr);

    lseek(pcb->exec_fd, addr – MEM_INVALID_SIZE, SEEK_SET);
    read(pcb->exec_fd, addr, PAGESIZE);
    if (pt[vpn].uprot == PROT_READ | PROT_EXEC) {
        pt[vpn].kprot = PROT_READ | PROT_EXEC;
        WriteRegister(REG_TLB_FLUSH, addr);
    }

    return;     // Done with handling this TRAP_MEMORY
}

// ... continue with other TRAP_MEMORY-handling code ....
```

**Additional changes for completeness (not strictly required):**

- In Exit handler: `close(pcb->exec_fd)` since no more page faults.
- At top of LoadProgram: if `pcb->exec_fd != -1`, close the old fd (for Exec into same process).
- In Fork handler: `child_pcb->exec_fd = dup(parent_pcb->exec_fd)` for independent file descriptors.

### Deductions

**Changes during LoadProgram:**

- **−2 pts** — Doesn't save a way of accessing the file contents later during page fault (file descriptor number, inode number, or pathname)
- **−3 pts** — Doesn't initialize ALL text and data PTEs to have valid = 0
- **−3 pts** — Doesn't clearly and correctly initialize something (e.g., in PTE or in PCB) to be able to determine during a TRAP_MEMORY the reason for which valid == 0 is that this is a page fault

**Changes in TRAP_MEMORY handler:**

- **−3 pts** — Does not clearly and correctly determine if this TRAP_MEMORY is a page fault
- **−1 pt** — If this is NOT a page fault, does something OTHER THAN the NORMAL existing handling of that TRAP_MEMORY
- **−2 pts** — On a page fault, does not get a physical page for this virtual page AND/OR does not update the pfn field in the virtual page's PTE
- **−3 pts** — Does not turn on valid = 1 in the virtual page's PTE
- **−2 pts** — Does not clearly and correctly show the location in the file to read the page image FROM
- **−2 pts** — Does not clearly and correctly show where in memory to read the page image INTO (must be a virtual address)
- **−2 pts** — Does not clearly show how to access the correct file to read the page from
- **−6 pts** — Does not actually READ the page from the file into memory

**General use of PTEs:**

- **−3 pts** — Uses fields that are not defined in Lab 2 PTE format, without explaining that this uses bit(s) ignored by the hardware AND/OR that do not fit within the bits ignored by the hardware, AND/OR adds additional bits AND/OR changes the meaning of existing hardware-understood PTE bits
- **−3 pts** — Does not clearly explain how one or more PTEs in the solution are found

**Additional errors:**

- **−1 pt** — One additional error
- **−1 pt** — Another additional error
- **−1 pt** — Another additional error
- **−2 pts** — A larger error
- **−2 pts** — Another large error

**Major design flaws:**

- **−6 pts** — Requires specific HARDWARE modification to trigger a page fault
- **−7 pts** — Sets up and/or reads ALL text and data pages after a SINGLE page fault on only ONE of them
- **−10 pts** — Requires SOFTWARE to check and trigger ALL "page faults"
- **−25 pts** — No submission

---

## Question 3: Page Replacement Algorithms (15 points)

Each sub-question is worth 5 points.

Page reference string: `3, 5, 0, 1, 0, 2, 1, 0, 3, 2, 4, 2, 0, 5` with 4 physical page frames, initially empty.

### Reference Solutions

**3.1 Optimal replacement**

**Total number of page faults = 4 initial faults + 3 additional faults = 7 total page faults**

Faults on: `3*, 5*, 0*, 1*, 0, 2*, 1, 0, 3, 2, 4*, 2, 0, 5*`

**3.2 FIFO replacement**

**Total number of page faults = 4 initial faults + 5 additional faults = 9 total page faults**

Faults on: `3*, 5*, 0*, 1*, 0, 2*, 1, 0, 3*, 2, 4*, 2, 0*, 5*`

**3.3 LRU replacement**

**Total number of page faults = 4 initial faults + 4 additional faults = 8 total page faults**

Faults on: `3*, 5*, 0*, 1*, 0, 2*, 1, 0, 3*, 2, 4*, 2, 0, 5*`

### Deductions

**3.1 Optimal replacement (5 points):**

- **0 pts** — Correct
- **−1 pt** — Incorrect by 1 page fault
- **−2 pts** — Omitted the initial page faults due to physical memory starting empty
- **−2 pts** — Incorrect by 2 page faults
- **−1 pt** — Did NOT "clearly state the total number of page faults that will occur during the execution of this program"

**3.2 FIFO replacement (5 points):**

- **0 pts** — Correct
- **−1 pt** — Incorrect by 1 page fault
- **−2 pts** — Omitted the initial page faults due to physical memory starting empty
- **−2 pts** — Incorrect by 2 page faults
- **−3 pts** — Incorrect by 3 page faults

**3.3 LRU replacement (5 points):**

- **0 pts** — Correct
- **−1 pt** — Incorrect by 1 page fault
- **−2 pts** — Omitted the initial page faults due to physical memory starting empty
- **−2 pts** — Incorrect by 2 page faults
- **−3 pts** — Incorrect by 3 page faults
- **−4 pts** — Does not clearly state the total number of page faults

---

## Question 4: Unix File System Extension (25 points)

### Reference Solution

Use a second inode for the second independent collection of data bytes. The "main" collection uses a standard inode exactly as in the original file system. By using a second inode, each inode represents and supports its respective collection of data bytes entirely independently, essentially as two independent files. The second inode is allocated from the normal pool of inodes.

**Tying the two inodes together:** Create a list that maps the first inode number to the second. Since an inode number is 16-bits, this list can be represented as an "array of `short`" stored on disk, indexed by the first inode number, with the value being the second inode number. Since there are a fixed number of inodes (determined at format time), this list is constant size.

**Alternative representation:** An array where each entry consists of two `short` values (first inode number, second inode number). If fewer than half of files use a second collection, this takes less space, but requires searching rather than direct indexing.

**Storage on disk:** Store the list in a "file" using some reserved inode number to describe the disk blocks containing the list. The contents can be accessed internally by the file system in exactly the same way as accessing any other file, and space can be allocated in the same way. A variant: use any available inode number and store that inode number in the file system's superblock.

**Key design properties:**
- Does NOT modify the format of directory entries or inodes
- Does NOT reserve a second inode for every file (only files using the alternate collection get a second inode)
- Space efficient — the mapping list is compact and constant-size
- Supports hard links (tied to inode, not filename)

### Deductions

**On-disk data structure design:**

- **−4 pts** — For some new/modified on-disk data structure, does not clearly or correctly describe the LOCATION on disk of it
- **−4 pts** — For some new/modified on-disk data structure, does not clearly or correctly describe the STORAGE ALLOCATION on disk for it
- **−5 pts** — For some new/modified on-disk data structure, does not clearly or correctly describe the INTERNAL STRUCTURE on disk and/or USAGE for it
- **−4 pts** — Does not clearly or correctly describe representing the INDEPENDENT SIZE of the ALTERNATIVE collection of data

**Space efficiency and constraints:**

- **−2 pts** — Solution is SPACE INEFFICIENT
- **−1 pt** — Solution REDUCES the MAXIMUM SIZE for the PRIMARY data
- **−2 pts** — Solution supports only a reduced MAXIMUM SIZE for the ALTERNATE data
- **−6 pts** — Solution supports only a VERY limited MAXIMUM SIZE for the ALTERNATE data (or also VERY limited PRIMARY size)
- **−5 pts** — Solution is tied to a file NAME, not to the file itself (does not support other links to the same file)
- **−10 pts** — Essentially equivalent to reserving a SECOND INODE for EVERY file, which is not allowed in the question

**Additional errors:**

- **−1 pt** — One additional error
- **−1 pt** — Another additional error

**Major design flaws:**

- **−10 pts** — Modifies the FORMAT of an INODE or SUBSTANTIALLY changes the MEANING of many fields
- **−12 pts** — Uses POINTERS in ON-DISK data structure
- **−20 pts** — Solution is not explained sufficiently to clearly communicate any part of its design
- **−24 pts** — Does not even have been an attempt to describe anything clearly or completely
- **−25 pts** — No answer

---

## Question 5: Cryptographic Message Protection (25 points)

### Reference Solution

**Setup:** Each user generates an asymmetric public/private key pair. Each process is initialized with the public and private key of its user. After generating their key pair, the user establishes identity with a trusted Certification Authority (CA), which generates a public key certificate signed by the CA. The private key is kept entirely private.

Each process maintains a cache of other users' public key certificates, obtained as needed and cached for later use.

**Sending a message** (process S_P on behalf of user S_U sends message M to receiver R_P on behalf of user R_U):

1. Generate *rand* = a random number (e.g., 64 bits)
2. Create the message to be sent, consisting of:
   - (a) the identifier of the sending user S_U
   - (b) the original message M
   - (c) the hash value H(*rand* || M || S_U), for a secure hash function H such as SHA-3
   - (d) the value of *rand*, encrypted using the sending user's private key

**Receiving a message:**

1. Use the public key of the indicated sending user to decrypt the random value
2. Compute H(decrypted rand || received message || indicated sender identifier)
3. Compare the computed hash to the hash in the received message
4. If they don't match, reject the message (sender identity or contents were forged/modified)
5. Otherwise, accept the message

**Security analysis:** An impersonator cannot encrypt the random number with the correct private key. A modified message will produce a different hash. A changed sender identifier causes the receiver to use the wrong public key, producing incorrect decryption. In all cases, the hash comparison fails.

**Efficiency:** Expensive asymmetric encryption is used only for the small random number. The message itself is covered only by the computationally cheaper hash function. Further optimization: reuse the same random number for multiple messages to the same receiver.

### Deductions

**Protection:**

- **−3 pts** — Uses ASYMMETRIC crypto over LARGE amount of data rather than over a HASH of that data
- **−2 pts** — Uses VAGUE language like "sign the message" OR "sign the hash of the message" or "verify the signature", WITHOUT defining HOW to do so
- **−3 pts** — Does NOT show how the receiver knows WHO the SENDER is and so how to know which key(s) to try using
- **−5 pts** — Does NOT actually protect against FORGED SENDER IDENTITY
- **−5 pts** — Does NOT actually protect against ALTERED or FORGED DATA
- **−8 pts** — Says to encrypt the message, but doesn't say HOW and with WHAT key
- **−10 pts** — Does NOT actually send the MESSAGE (e.g., sending the hash of the message does not allow the receiver to recover the message)

**Keys:**

- **−2 pts** — Does NOT explain HOW a process USING at least one PUBLIC key GETS that public key ("may be INITIALIZED with only a SMALL LIMITED total amount of state")
- **−1 pt** — Assumes all processes know each other's public Diffie-Hellman values, but does NOT explain HOW a process SECURELY knows that value when needed ("may be INITIALIZED with only a SMALL LIMITED total amount of state")
- **−2 pts** — Does NOT explain how a process (sender or receiver) USING at least one "shared" secret key actually SECURELY established that key as SECRET and shared by both processes ("may be INITIALIZED with only a SMALL LIMITED total amount of state")
- **−2 pts** — Uses a PUBLIC key but doesn't use a public key CERTIFICATE signed by a trusted CA in order to reliably know that public key
- **−1 pt** — Recreates the public key CERTIFICATE on EVERY message
- **−4 pts** — Does not properly keep a private key PRIVATE or secret key SECRET (e.g., generated by someone else such as the kernel or some other user, or otherwise disclosed to someone other than the user)
- **−2 pts** — Uses Diffie-Hellman but without proper safeguard against a man-in-the-middle attack
- **−2 pts** — Attempts to use Diffie-Hellman public/private values as public/private keys or otherwise to use Diffie-Hellman to generate keys for asymmetric encryption/decryption (DH can only generate a single shared key for use, e.g., with symmetric encryption)
- **−1 pt** — Says to use PUBLIC key and SECRET key, rather than PUBLIC key and PRIVATE key
- **0 pts** — Says to use a PRIVATE key, rather than a SECRET key (the two are actually VERY different)
- **−2 pts** — Unclear in at least one case exactly what key is being used

**Representation:**

- **−4 pts** — Not clear what is added to the message or what is where in the message SENT
- **−3 pts** — Sends one or more MEMORY POINTERS in the message

**Major design flaws:**

- **−24 pts** — No protection at all, since everything depends only on public information
- **−10 pts** — Everything is based ultimately only on security of the kernel
- **−15 pts** — Many details missing, giving the solution almost no meaning
- **−25 pts** — No submission
