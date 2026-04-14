# COMP 421 Final Exam — Grading Rubric

**Spring 2024 — Total: 100 points**

---

## Question 1: Short Definitions (10 points)

Each term is worth 2 points.

Terms: **(a)** capability, **(b)** cylinder group, **(c)** symbolic link, **(d)** message digest, **(e)** extent

**Per-term rubric:**

- **0 pts** — Full credit
- **−1 pt** — Partially correct: missing some important detail, or some incorrect statement as part of the answer
- **−2 pts** — Many important errors or omissions, or no answer was given

---

## Question 2: LoadProgram and Yalnix Demand Paging (25 points)

### Changes during LoadProgram

- **−2 pts** — Doesn't save a way of accessing the file contents later during page fault (file descriptor number, inode number, or pathname)
- **−3 pts** — Doesn't initialize ALL text and data PTEs to have valid = 0
- **−3 pts** — Doesn't clearly and correctly initialize something (e.g., in PTE or in PCB) to be able to determine during a TRAP_MEMORY the reason for which valid == 0 is that this is a page fault

### Changes in TRAP_MEMORY handler

- **−3 pts** — Does not clearly and correctly determine if this TRAP_MEMORY is a page fault
- **−1 pt** — If this is NOT a page fault, does something OTHER THAN the NORMAL existing handling of that TRAP_MEMORY
- **−2 pts** — On a page fault, does not get a physical page for this virtual page AND/OR does not update the pfn field in the virtual page's PTE
- **−3 pts** — Does not turn on valid = 1 in the virtual page's PTE
- **−2 pts** — Does not clearly and correctly show the location in the file to read the page image FROM
- **−2 pts** — Does not clearly and correctly show where in memory to read the page image INTO (must be a virtual address)
- **−2 pts** — Does not clearly show how to access the correct file to read the page from
- **−6 pts** — Does not actually READ the page from the file into memory

### General use of PTEs

- **−3 pts** — Uses fields that are not defined in Lab 2 PTE format, without explaining that this uses bit(s) ignored by the hardware AND/OR that do not fit within the bits ignored by the hardware, AND/OR adds additional bits AND/OR changes the meaning of existing hardware-understood PTE bits
- **−3 pts** — Does not clearly explain how one or more PTEs in the solution are found

### Additional errors

- **−1 pt** — One additional error
- **−1 pt** — Another additional error
- **−1 pt** — Another additional error
- **−2 pts** — A larger error
- **−2 pts** — Another large error

### Major design flaws

- **−6 pts** — Requires specific HARDWARE modification to trigger a page fault
- **−7 pts** — Sets up and/or reads ALL text and data pages after a SINGLE page fault on only ONE of them
- **−10 pts** — Requires SOFTWARE to check and trigger ALL "page faults"
- **−25 pts** — No submission

---

## Question 3: Page Replacement Algorithms (15 points)

Each sub-question is worth 5 points.

### 3.1 Optimal replacement (5 points)

- **0 pts** — Correct
- **−1 pt** — Incorrect by 1 page fault
- **−2 pts** — Omitted the initial page faults due to physical memory starting empty
- **−2 pts** — Incorrect by 2 page faults
- **−1 pt** — Did NOT "clearly state the total number of page faults that will occur during the execution of this program"

### 3.2 FIFO replacement (5 points)

- **0 pts** — Correct
- **−1 pt** — Incorrect by 1 page fault
- **−2 pts** — Omitted the initial page faults due to physical memory starting empty
- **−2 pts** — Incorrect by 2 page faults
- **−3 pts** — Incorrect by 3 page faults

### 3.3 LRU replacement (5 points)

- **0 pts** — Correct
- **−1 pt** — Incorrect by 1 page fault
- **−2 pts** — Omitted the initial page faults due to physical memory starting empty
- **−2 pts** — Incorrect by 2 page faults
- **−3 pts** — Incorrect by 3 page faults
- **−4 pts** — Does not clearly state the total number of page faults

---

## Question 4: Unix File System Extension (25 points)

### On-disk data structure design

- **−4 pts** — For some new/modified on-disk data structure, does not clearly or correctly describe the LOCATION on disk of it
- **−4 pts** — For some new/modified on-disk data structure, does not clearly or correctly describe the STORAGE ALLOCATION on disk for it
- **−5 pts** — For some new/modified on-disk data structure, does not clearly or correctly describe the INTERNAL STRUCTURE on disk and/or USAGE for it
- **−4 pts** — Does not clearly or correctly describe representing the INDEPENDENT SIZE of the ALTERNATIVE collection of data

### Space efficiency and constraints

- **−2 pts** — Solution is SPACE INEFFICIENT
- **−1 pt** — Solution REDUCES the MAXIMUM SIZE for the PRIMARY data
- **−2 pts** — Solution supports only a reduced MAXIMUM SIZE for the ALTERNATE data
- **−6 pts** — Solution supports only a VERY limited MAXIMUM SIZE for the ALTERNATE data (or also VERY limited PRIMARY size)
- **−5 pts** — Solution is tied to a file NAME, not to the file itself (does not support other links to the same file)
- **−10 pts** — Essentially equivalent to reserving a SECOND INODE for EVERY file, which is not allowed in the question

### Additional errors

- **−1 pt** — One additional error
- **−1 pt** — Another additional error

### Major design flaws

- **−10 pts** — Modifies the FORMAT of an INODE or SUBSTANTIALLY changes the MEANING of many fields
- **−12 pts** — Uses POINTERS in ON-DISK data structure
- **−20 pts** — Solution is not explained sufficiently to clearly communicate any part of its design
- **−24 pts** — Does not even have been an attempt to describe anything clearly or completely
- **−25 pts** — No answer

---

## Question 5: Cryptographic Message Protection (25 points)

### Protection

- **−3 pts** — Uses ASYMMETRIC crypto over LARGE amount of data rather than over a HASH of that data
- **−2 pts** — Uses VAGUE language like "sign the message" OR "sign the hash of the message" or "verify the signature", WITHOUT defining HOW to do so
- **−3 pts** — Does NOT show how the receiver knows WHO the SENDER is and so how to know which key(s) to try using
- **−5 pts** — Does NOT actually protect against FORGED SENDER IDENTITY
- **−5 pts** — Does NOT actually protect against ALTERED or FORGED DATA
- **−8 pts** — Says to encrypt the message, but doesn't say HOW and with WHAT key
- **−10 pts** — Does NOT actually send the MESSAGE (e.g., sending the hash of the message does not allow the receiver to recover the message)

### Keys

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

### Representation

- **−4 pts** — Not clear what is added to the message or what is where in the message SENT
- **−3 pts** — Sends one or more MEMORY POINTERS in the message

### Major design flaws

- **−19 pts** — No protection at all, since everything depends only on public information
- **−10 pts** — Everything is based ultimately only on security of the kernel
- **−15 pts** — Many details missing, giving the solution almost no meaning
- **−20 pts** — No submission
