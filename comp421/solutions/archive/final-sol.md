# Solutions for Final Exam

## Operating Systems and Concurrent Programming

### COMP 421/ELEC 421, COMP 521/ELEC 552

**Spring 2024**

For some of the questions on the sample midterm exam, there is more than one way to solve them correctly. I give one possible correct solution for each question below.

---

## Question 1: Short Definitions

The one-sentence definitions below, I believe, essentially contain all key aspects of the definition of each term, although some of this detail may not strictly be required for an acceptable answer on this question.

Note that a definition that says something like "A system [or method or strategy, etc.] for [accomplishing something]" without saying *how* this is accomplished is not a good definition. Simply saying what is accomplished without indicating *how* it is accomplished does not really define the term.

**(a)** ***capability:*** A protected reference to some object, such that possession of the capability by some process allows that process to perform the operations indicated in the capability on that object, and such that the process cannot modify or forge the capability. (*Note that* a capability is not just this information, such as written down somewhere, but *possession* of the capability is what *gives* the process the ability to do those operations on that object. The process is *capable* of performing those operations on that object specifically if it possesses that *capability*.)

**(b)** ***cylinder group:*** A contiguous subset of the cylinders on a hard disk on which a file system is stored, containing a redundant copy of the file system's superblock, a subset of the data blocks of the file system, a subset of the inodes of the file system, and a list (e.g., a bitmap) of the free blocks and of the free inodes within that cylinder group.

**(c)** ***symbolic link:*** A form of name in a file system that, in contrast to a hard link, which represents the name as a link directly to the inode of the target file, instead represents it by the symbolic pathname of the file linked to, such that when looking up a pathname containing a symbolic link, results in traversing to the target of that symbolic link before completing the original pathname lookup.

**(d)** ***message digest:*** The output of a secure cryptographic one-way hash function, such as SHA-1 or SHA-2 or SHA-3, applied to some message, such that it is computationally infeasible to find the input or any other input that produced this output value. (*Note that* the message digest, that is, the output of the hash function, is *not* and need not be *unique*.)

**(e)** ***extent:*** A contiguous range of disk blocks (or similar for other forms of storage), representing an allocated unit or a free unit of storage, generally identified by the block number of the first block of the extent and the count of contiguous blocks in the extent.

---

## Question 2: LoadProgram and Yalnix Demand Paging

This solution requires changes in two different parts of the Yalnix kernel: in LoadProgram and in the TRAP_MEMORY handler function.

In LoadProgram, make the following changes:

- For each *text* or *data* PTE, initialize `valid = 1` in that PTE, and do *not* allocate a physical page for that virtual page.

- For each *data* PTE, initialize both the `uprot` and `kprot` fields in that PTE to `PROT_READ | PROT_WRITE`.

- For each *text* PTE, initialize the `uprot` field in that PTE to `PROT_READ | PROT_EXEC`, and initialize the `kprot` field in it to `PROT_READ | PROT_WRITE`. By initializing the kernel protection for the text pages this way, the kernel will be able later to do the `read()` into that virtual page if a page fault occurs for it; in that case, the `kprot` field in that PTE will then be changed to `PROT_READ | PROT_EXEC` after the `read()`, matching its `uprot` value.

- For each invalid PTE, initialize that entire PTE (all bits) to 0, not just the `valid` bit. Initialized this way allows the kernel to determine why the `valid` bit is 0 in any type of PTE: if the *entire* PTE is all 0, then this virtual page is truly invalid, whereas if the protection bits are *not* all 0, then this virtual page has only been marked as invalid to support a demand paging load of that page.

- Do not do the `read(fd, MEM_INVALID_SIZE, li.text_size+li.data_size)` call; in other words, do not read any of the text or data pages during LoadProgram.

- Save the value of the file descriptor number `fd` in `pcb->exec_fd`, where `pcb` is the address of the PCB of the current process.

- Do not do the `close(fd)` call at the bottom of LoadProgram; leave the file descriptor number `fd` open upon return from LoadProgram.

In the TRAP_MEMORY handler, assume that the function includes the following initial code:

```c
addr = DOWN_TO_PAGE(info->addr);    // the address of the beginning of the page
vpn = addr >> PAGESHIFT;
pcb = address of the PCB for the current process;
pt = virtual address of current Region 0 page table;
```

In the TRAP_MEMORY handler, add the following initial code to check if this TRAP_MEMORY call is for a page fault, and if so, to handle that page fault:

```c
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

As shown above, if this TRAP_MEMORY call is for a page fault, the added code handles the page fault and returns from the TRAP_MEMORY handler. If this call is not for a page fault, continue with the existing TRAP_MEMORY handler code to handle the call as before.

In addition to the above changes, for completeness (although not expected in an answer to this question), the following three other changes are also needed:

- In the kernel handler for the Exit kernel call (and any other place in the kernel where the process is being terminated), do `close(pcb->exec_fd)`, since the process will not be experiencing any more page faults.

- At the top of LoadProgram, if this process already has a file descriptor open (e.g., this LoadProgram call is being done for an Exec kernel call, to load some new program into the same process, do `close(pcb->exec_fd)`, since the process will not be experiencing any more page faults for this old open file descriptor (the file descriptor that was opened during the LoadProgram for the now-outgoing program in this process). This can be handled, for example, by initializing `pcb->exec_fd = -1` when creating a new PCB and after closing the existing `pcb->exec_fd`. Then, at the top of LoadProgram, if `pcb->exec_fd != -1`, then do `close(pcb->exec_fd)`.

- Finally, in the kernel handler for the Fork kernel call, initialize `child_pcb->exec_fd` to the value from `dup(parent_pcb->exec_fd)`, where `parent_pcb` is the address of the parent's PCB and `child_pcb` is the address of the new child's PCB. This call to `dup()` creates a new, independent file descriptor open to the same file as the parent's `exec_fd` file descriptor is open to. By creating this new, independent open file descriptor, the two processes can then run independently, including still working correctly after either parent or child exits or does a new LoadProgram as described above.

Note, by the way, that this use of `open()`, `close()`, `lseek()`, `read()`, and `dup()` are native Unix kernel calls and are thus operating "outside" the simulated Yalnix/RCS 421 environment. You used the Unix `open()`, `read()`, and `close()` kernel calls in this way in LoadProgram in Lab 2 (provided in the LoadProgram template), and they are used in this solution here, together with the `lseek()` and `dup()` Unix kernel calls, in the same way.

---

## Question 3: Page Replacement Algorithms

In each part below, the circled virtual page numbers are the specific ones that caused a page fault.

In each part, the numbers under each Page Frame Number column are the virtual page number of the virtual page currently in that respective physical page, with the *bottom* number being the virtual page *currently* in that physical page at each point during the execution of the page reference string. The victim physical page for each page fault is indicated by the column in which that virtual page number is placed.

### 3.1 Optimal

**Total number of page faults = 4 initial faults + 3 additional faults = 7 total page faults**

Page reference string (faults marked with *):

```
3*  5*  0*  1*  0   2*  1   0   3   2   4*  2   0   5*
```

| Page Frame 0 | Page Frame 1 | Page Frame 2 | Page Frame 3 |
|:---:|:---:|:---:|:---:|
| 3 | 5 | 0 | 1 |
| 4 | | 2 | |
| | | 5 | |

### 3.2 FIFO

**Total number of page faults = 4 initial faults + 5 additional faults = 9 total page faults**

Page reference string (faults marked with *):

```
3*  5*  0*  1*  0   2*  1   0   3*  2   4*  2   0*  5*
```

| Page Frame 0 | Page Frame 1 | Page Frame 2 | Page Frame 3 |
|:---:|:---:|:---:|:---:|
| 3 | 5 | 0 | 1 |
| 4 | 3 | 4 | 0 |
| 5 | | | |

### 3.3 LRU

**Total number of page faults = 4 initial faults + 4 additional faults = 8 total page faults**

Page reference string (faults marked with *):

```
3*  5*  0*  1*  0   2*  1   0   3*  2   4*  2   0   5*
```

| Page Frame 0 | Page Frame 1 | Page Frame 2 | Page Frame 3 |
|:---:|:---:|:---:|:---:|
| 3 | 5 | 0 | 1 |
| 2 | 3 | | 4 |
| 5 | | | |

---

## Question 4: Unix File System Extension

The easiest, most straightforward way to support the second independent collection of data bytes as described in the question is to simply use a second inode for the second collection of data bytes. The "main" (i.e., first) collection of data bytes uses a standard inode exactly as in the original, unmodified file system. By using a second inode for the second collection of data bytes, each inode represents and supports its respective collection of data bytes, entirely independently of the other collection of data bytes, essentially as two entirely independent files. The second inode is allocated in exactly the same way as for the first inode, from the pool of inodes that were created and formatted when file system was formatted.

What is still needed to complete this solution is some way to "tie" the two inodes together such that opening (or otherwise looking up) a file pathname that leads to the inode for the first ("main") collection of data bytes (again, exactly as in the original, unmodified file system), it is then possible to access either the first or the second collection of data bytes. In other words, from opening or otherwise looking up the pathname, it must be possible to then find this second inode, not just the first inode.

A very simple way to do this is to create a list that maps the first inode number to the second. Since an inode number is 16-bits, this list can efficiently be represented as the equivalent of an "array of `short`" that is stored on the disk. By using the first inode number as an index into this array, the value to be stored there should be the second inode number. Since there are a fixed number of inodes in the file system (the number is determined when the file system is formatted), this list would be of constant size.

An alternative representation of this list would be an array in which each entry consists of two `short` values, with the first of those two values being the first inode number and the second of those two values being the second inode number. If fewer than half of the inodes are making use of a second inode (that is, fewer than half of the files are making use of such a second collection of data bytes), this alternative representation would take up less space on disk, but this advantage comes at the cost of needing to search the list for a given first inode number, whereas the representation described first above can be directly indexed (with no searching) by the given first inode number. And any advantage in taking up less disk space decreases (and vanishes) as the number of files using a second collection of data bytes increases.

As mentioned, this list (in whatever representation) must be stored on the disk. The simplest way to do that is to store it in a "file", using some reserved inode number to describe the disk blocks in which the representation of the list is stored. The contents of the list can then be accessed internally by the file system in exactly the same way as accessing the contents any other file using that file's inode, and the space for it can be allocated internally by the file system in exactly the same way as allocating data blocks for any file. A simple variant on using some reserved inode number for this function would be to instead use any available inode number and to store that inode number in the file system's superblock.

---

## Question 5: Cryptographic Message Protection

Each user is assumed to have generated for themselves an asymmetric public and private key pair. Each process must be initialized with the public and private key of the user on behalf of which that process is running.

After generating their own public and private key pair, the user should establish its identity in some way (e.g., offline) with a Certification Authority (CA) that is trusted by all users of the system. That CA should then generate for the user a public key certificate, signed by that CA, attesting that the public key is the real public key of this user. That public key certificate can then be shared by the process (or anyone else) to anyone (such as on request from the process itself). The private key is kept entirely private to the user and not ever shared with anyone else.

In addition, each process should maintain a cache of the public key certificates of other users, obtained as needed as described above and then cached by the process for possible later use, if needed. This cache of public key certificates can in the abstract be of any size but for best performance should be large enough to retain the public key certificate of each other user with which this process is currently communicating. If a public key certificate is discarded from this cache (e.g., due to lack of space), it can be obtained and cached again as described above. No public key is used without getting that public key from the public key certificate of that public key and first verifying the CA signature on that certificate.

There are many ways to proceed from here in obtaining the two security properties required in the question, for a process receiving a message to be able to verify the following:

- that the received message was actually sent by the sending person that the message claims to have been sent by; and
- that the contents of the received message has not been modified since that sending process sent that message on behalf of that person.

Note that these security properties do not include: confidentiality (there is no requirement in the question to prevent other processes from being able to eavesdrop on and understand the messages) or replay protection (the question does not require preventing the replay by some attacker of a saved copy of a legitimate earlier message).

I will describe below one simple solution for obtaining the two required security properties for this question (although some variations or other approaches are certainly also possible).

### Sending a Message

When some sending process S_P, executing on behalf of some sending user S_U, sends some message M to an intended receiver process R_P, executing on behalf of some receiver user R_U, the sending process should perform the following steps:

1. Generate *rand* = a random number (e.g., 64 bits)
2. Create the message to be sent over the network, consisting of the following:
   - (a) the identifier of the sending user S_U (e.g., the name or some unique number for this sending user)
   - (b) the original message M that this sender wants to send to the receiver
   - (c) the hash value H(*rand* || the bytes of the original message M || the sending user identifier S_U), for some secure one-way cryptographic hash function H such as SHA-3, where || means concatenation
   - (d) the value of *rand*, encrypted using the sending user's private key.

### Receiving a Message

When receiving such a message, the receiving process should perform the following steps:

1. Use the public key of the indicated sending user to decrypt the random value in the received message
2. Compute the hash H(the decrypted random number || the bytes of the received message || the indicated sending user identified in the received message)
3. Compare this newly computed hash value to the hash value in the received message
4. If computed hash value does not match the received hash value, then one or both of the above two properties has failed, and so the receiver should reject the received message, and the received message should be discarded
5. Otherwise, the message within the received message can safely be accepted by the receiver

### Security Analysis

If some other process is attempting to impersonate the sender, then it will not be able to properly encrypt the random number, as only the correct sender can encrypt something using its own private key. Likewise, if someone attempted to modify the encrypted random number after the legitimate sender sent the message, then they would not be able to produce a properly encrypted random number that would decrypt correctly. Likewise, if some attacker attempts to change the claimed sender identifier in the message, the receiver will use the "wrong" public key to decrypt the random number, so again the decrypted value will be wrong.

Note that it is not required that the receiver be able to detect whether or not the random number decrypted correctly. If it did not decrypt correctly, then later processing on the received message by the receiver will cause the message to be properly rejected and discarded by the receiver.

In all of the above cases, the hash value computed by the receiver will not match, as it will include the incorrect random number (and possibly also the incorrect sender identifier). In these cases, the receiver will properly reject and discard the received message.

Finally, if the message itself was modified by some attacker, then, again, the hash computed by the receiver will not match. And, again, the message will be rejected and discarded by the receiver.

### Efficiency

This solution is already reasonably efficient, as it uses expensive asymmetric public key encryption (and decryption) for each message only for the single (e.g., 64-bit) random number in the message. Everything else in the received message (including the original sent message itself) is covered only by a computationally much less expensive secure cryptographic one-way hash function.

And it can easily be made even more efficient, if needed, by a sending process making use of the same random number for more than one message it sends to the same receiver process. For example, the sender could use the same random number for some *k* messages in a row to the same receiver, thus avoiding the need to encrypt and decrypt a new random number on most message sent; the receiver could cache and reuse the same random number, if the sender did not include a new (encrypted) random number in some message. Or the receiver could cache the random number from the sender even longer term, with the sender changing the random number only if the receiver no longer has the previous random number still cached (or until there is some other reason to change the random number). Changing the random number more frequently, though, does come with some increase in security, as using the same random number on more messages makes it marginally easier for some attacker to potentially reverse engineer and/or brute force attack the value of the current random number.
