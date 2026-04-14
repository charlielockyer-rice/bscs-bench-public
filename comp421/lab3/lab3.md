# COMP 421 Lab 3: The Yalnix File System

**Due Date:** 11:59 PM, Friday, April 19, 2024

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [The Yalnix File System on Disk](#2-the-yalnix-file-system-on-disk)
   - [Disk Organization](#21-disk-organization)
   - [Inodes](#22-inodes)
   - [Directory Entries](#23-directory-entries)
   - [Pathnames](#24-pathnames)
   - [Symbolic Links](#25-symbolic-links)
   - [Formatting a File System on the Disk](#26-formatting-a-file-system-on-the-disk)
3. [New Yalnix Kernel Calls](#3-new-yalnix-kernel-calls)
4. [The Yalnix File System Server](#4-the-yalnix-file-system-server)
   - [Overview](#41-overview)
   - [User Process File System Operations](#42-user-process-file-system-operations)
   - [Running Yalnix, the Server, and User Processes](#43-running-yalnix-the-server-and-user-processes)
5. [Your Assignment](#5-your-assignment)
   - [General Specification](#51-general-specification)
   - [For Students Enrolled in COMP 521 or ELEC 552](#52-for-students-enrolled-in-comp-521-or-elec-552)
   - [Submitting your Project for Grading](#53-submitting-your-project-for-grading)
   - [Honor Code Policy](#54-honor-code-policy)

---

## 1 Project Overview

This project asks you to implement a file system for the Yalnix kernel. Your implementation will consist of two components: a file system library and a file system server process. The file system server responds to requests from client processes through interprocess communication (IPC), while the file system library provides user-level procedures for issuing these requests.

The file system is designed to be simple but realistic. It includes several standard Unix-style features: regular files, directories, symbolic links (optional), file permissions and link counts, and a cache of recently accessed disk blocks and inodes for performance.

---

## 2 The Yalnix File System on Disk

### 2.1 Disk Organization

The Yalnix file system is stored on a single disk. The disk is partitioned into the following regions:

- **Boot block (sector 0):** Reserved for boot code; not used by the file system.
- **Super block (sector 1):** Contains information about the file system layout, including the number of inodes, number of disk blocks, and locations of the inode and block regions.
- **Inode region:** A contiguous region containing all inodes for the file system.
- **Data block region:** A contiguous region containing all data blocks for the file system.

The file system is stored on disk as a sequence of sectors. Each sector is `SECTORSIZE` bytes. Constants defining the file system structure are given in the header file `comp421/filesystem.h`.

### 2.2 Inodes

Each file (regular file or directory) has an associated inode. An inode is a data structure that contains metadata about the file, including its size, owner, permissions, and the location of its data on disk.

The format of an inode is:

```c
struct inode {
    int nlink;                    /* number of hard links to this inode */
    int size;                     /* size of file in bytes */
    int type;                     /* type of file */
    int owner;                    /* owner of the file */
    int created;                  /* time file was created */
    int modified;                 /* time file was last modified */
    int accessed;                 /* time file was last accessed */
    int acmode;                   /* access mode (permissions) */
    int direct[NUMDIRECT];        /* disk block numbers for direct blocks */
    int indirect;                 /* disk block number for indirect block */
    int reuse;                    /* how many times the inode has been reused */
};
```

The constants `NUMDIRECT` and `INODE_SIZE` are defined in `comp421/filesystem.h`.

The inode `type` field can take one of the following values (defined in `comp421/filesystem.h`):

- `INODE_REGULAR`: A regular file.
- `INODE_DIRECTORY`: A directory file.
- `INODE_SYMLINK`: A symbolic link (optional).

The `direct` array contains disk block numbers for the first several disk blocks of the file. If the file is larger than can be addressed by the direct blocks, the `indirect` field contains the disk block number of an indirect block, which itself contains disk block numbers for additional data blocks.

An inode is allocated from the inode region on disk. The file system manages free inodes using a free inode list. When a new file is created, the file system allocates a free inode from this list. When a file is deleted (its link count drops to zero), the inode is returned to the free inode list.

The `reuse` field in an inode is used to distinguish between different versions of the same inode number. When the file system is first formatted, the `reuse` field in each inode should be initialized to 0. Each time an inode is allocated (from being free), the `reuse` count in the inode must be incremented. The `reuse` count allows your file system to detect when a file descriptor refers to an inode that has been reused since the file was originally opened.

### 2.3 Directory Entries

A directory is a special file whose contents consist of a sequence of directory entries. Each directory entry contains:

- A file name (up to `DIRNAMELEN` characters, as defined in `comp421/filesystem.h`)
- The inode number of the file

The file name in a directory entry may contain any characters other than a slash (`'/'`) or the null character (`'\0'`). For example, even "unusual" characters such as a space, a backspace, or even a newline character are all legal in a file name; each of these characters should be treated exactly the same as any other character in a file name.

If the actual file name is shorter than `DIRNAMELEN` characters, the remainder of the characters in this field should be filled with the null character (`'\0'`). If the file name is exactly `DIRNAMELEN` characters long, there will be no null character at the end of the file name in the directory entry (it would waste a lot of disk space to leave room store the null character for every directory entry, and it is not needed to understand the disk format). You thus cannot correctly use the functions `strlen()` or `strcpy()` to access the file name, since the null terminating character may not be present. When looking at the file name in a directory entry, the name ends at the first null character or after `DIRNAMELEN` characters, whichever occurs first. Any attempt to create a file name longer than `DIRNAMELEN` characters should be considered as an error by your file system.

The `name` field of a free directory entry (with `inum` set to 0) need have no special value and should be ignored by your file system. However, it is best that, if the `inum` field is 0, then the `name` field is also all 0's, but this is not required.

When creating a new entry in a directory, you should "reuse" the first "free" entry in the directory (directory entry in which the `inum` field is set to 0). If there are no "free" entries in the directory, you should append a single new entry to the end of the directory.

### 2.4 Pathnames

As in Unix, each file is identified by a pathname, such as `/dirname/subdirname/file`. The slash character (`'/'`) at the beginning of the pathname indicates that this is an absolute pathname, meaning that processing of that pathname begins at the inode of the root directory. A pathname such as `otherdir/file` (which does not have a slash character at the beginning) is a relative pathname; processing of a relative pathname begins at the inode of the current directory of the requesting user process, set by the process calling the `ChDir` operation (the current directory of each process when the process begins execution of a new program should be `"/"`).

The slash character is also used to separate individual directory names along the path to the file, and to separate the last directory name from the name of the particular file in that directory. More than one slash character in a row within a pathname should be processed the same as if a single slash character had occurred there (except that those additional slash characters still do count in the total length of the pathname string). For example, the pathname such as `/////dirname//subdirname///file` refers to exactly the same file as does the pathname `/dirname/subdirname/file`. A pathname with one or more trailing slash characters at the end of the pathname (with no further characters after the last slash character) should be treated as if the pathname ended in a final component `"."` (described below) following that last slash character; so, for example, the pathname `"/a/b/c/"` should be treated as if the pathname was actually `"/a/b/c/."` instead. An empty pathname (a null string) should result in an error being returned for any Yalnix file system operation attempting to use such an empty pathname.

When processing a pathname, e.g., as part of an `Open` operation, you should process the pathname one component at a time. A component of a pathname is the name that occurs between two slash characters (or before the first slash character, or after the last slash character). As noted above, processing begins either with the root inode (for an absolute pathname) or at the inode of the current directory of the requesting process's current directory (for a relative pathname). This starting inode defines the initial "current lookup inode" for this pathname. For each next component of the pathname, beginning with the leftmost component and working component by component across the pathname, processing of that component is performed relative to the then-current lookup inode in the processing of that pathname. If that component is a directory name, for example, the current lookup inode becomes the inode for that directory (if that directory itself was found), and processing of the following component of the pathname proceeds with the inode for that directory as the current lookup inode.

A pathname, when presented as an argument to a Yalnix file system call is represented as a normal C-style string, terminated by a null character. The maximum length of the entire pathname, including the null character, is limited to `MAXPATHNAMELEN` characters. This limit of `MAXPATHNAMELEN` characters applies only to the length of the pathname string when presented as an argument to a Yalnix file system call. The fact of whether this pathname is an absolute pathname or a relative pathname, or the possible presence of symbolic links encountered while processing this pathname, do not count against that limit of `MAXPATHNAMELEN` characters. The limit of `MAXPATHNAMELEN` characters literally applies only to the argument of the call itself.

Within each directory, two special directory entries must exist (created by the `MkDir` call):

- **`"."`** (dot): This directory entry has the name `"."` and the inode number of the directory within which it is contained.
- **`".."`** (dot dot): This directory entry has the name `".."` and the inode number of the parent directory of the directory within which it is contained. In the root directory, the `".."` entry instead has the same value as `"."` (the inode number of the root directory, which is defined as `ROOTINO DE` in `comp421/filesystem.h`).

The `"."` and `".."` entries are created in a directory when it is created (by the `MkDir` request) as the **first two entries** in the new directory. These two directory entries subsequently cannot be explicitly deleted, but are automatically cleaned up along with the rest of the directory on a successful `RmDir` request. The `"."` and `".."` entries **must be included** in the `nlink` count in the inode of the directory to which each points.

### 2.5 Symbolic Links

**NOTE:** Support for symbolic links in this project is **optional** and will not affect your grade. Symbolic links are defined here for those who want to experiment with them in their implementation, and to give a clear example of how symbolic links fit into a "real" file system. You are strongly encouraged — but not required — to implement support for symbolic links in your file system.

The Yalnix file system can support symbolic links, as in the Unix file system. A symbolic link to some other file is represented in the Yalnix file system by an inode of type `INODE_SYMLINK`; the format of this file is otherwise the same as an `INODE_REGULAR` file. However, the contents of this file (the data stored in the data blocks hanging off of this inode) is interpreted by the file system as the name of the file to which this symbolic link is linked. Note that the length of this name is the **entire length of the data in the file**, as given by the `size` field in the inode, and the name as recorded here is **not terminated by a null (`'\0'`)** character.

The file name to which a symbolic link points may be either an absolute pathname or a relative pathname. If a relative pathname, it is interpreted relative to the directory in which the symbolic link file itself occurs; that is, the processing of the symbolic link target begins with the current lookup inode (see Section 2.4) being the inode of the directory in which the symbolic link file itself was found. For example, consider the pathname `"/a/b/c"`, where `"b"` within this pathname is a symbolic link to `"d/e"`. Since the target of the symbolic link `"b"` in this example is a relative pathname, `"d/y"`, the search for `"x"` when, for example, attempting to `Open` the name `"/a/b/c"`, begins in the same directory in which the name `"b"` itself was found. Thus, attempting to `Open` the file `"/a/b/c"` is ultimately "equivalent" to attempting to `Open` the name `"/a/b/e/c"`. If, instead, `"b"` within the pathname `"/a/b/c"` is a symbolic link to the absolute pathname `"/p/q"`, then attempting to open the file `"/a/b/c"` is then "equivalent" to attempting to open the name `"/p/q/c"` (but see the **note** below).

As another example, suppose that:

- You are attempting to `Open` the pathname `"/a/b/c"`.
- In doing this `Open`, you find that the name `"b"` within this pathname is a symbolic link to `"d/e"`.
- You further find that the name `"e"` is a symbolic link to `"f/g/h"`.
- And you finally find that the name `"c"` is a symbolic link to `"j"`.

The combined effect of attempting to `Open` this original pathname and encountering these symbolic links during processing that `Open` attempt is "equivalent" to attempting to `Open` the pathname `"/f/g/h/j"` (but see the **note** below).

**Note that to process a pathname such as in these examples, in which you might encounter one or more symbolic links during processing that pathname, you should not attempt to build up the complete pathname that the original name is "equivalent" to.** Rather, as with any pathname, you should process each component of the pathname one at a time. If you encounter a symbolic link, you should make a recursive call to your pathname lookup procedure, attempting to lookup the target of that symbolic link, before resuming processing what remains of the name you were processing when you encountered that symbolic link. This is why we say I an Open pathname called when is "equivalent" to attempting to `Open` some other pathname. Attempting to `Open` the first pathname is **not literally** the same as attempting to `Open` the second, but the final effect in terms of which file ends up being `Opened` is the same.

When creating a symbolic link, it is **not** an error if the target of the new symbolic link does not exist. Indeed, when creating a symbolic link, the target is simply recorded as-is, without any processing, except that it is an error to attempt to create a symbolic link to an empty pathname (a null string). Furthermore, if the target of an existing symbolic link is later deleted, this is **not** an error. In both cases, such a "dangling" symbolic link is not a problem; however, if such a dangling symbolic link is encountered while processing some other pathname (such as during an `Open` operation), an error would be returned upon encountering the dangling symbolic link.

Also, note that, whereas it is an error to attempt to create a hard (regular) link to a directory, creating a symbolic link to a directory is allowed; this is **not** an error.

Within the complete processing of a single pathname passed as an argument to any Yalnix file system operation, the maximum number of symbolic link traversals that may be performed is limited to `MAXSYMLINKS` symbolic link traversals (defined in `comp421/filesystem.h`). If in processing any single pathname that was an argument to any Yalnix file system operation, you would need to traverse more than `MAXSYMLINKS` symbolic links, you should terminate processing of that pathname and instead return an error as the result of that Yalnix file system operation.

Lastly, note the following special exception to handling a symbolic link when looking up a pathname, with respect to the last component of that pathname: **If the last component of the pathname is the name of a symbolic link, that symbolic link must not be traversed unless the pathname is being looked up for an `Open`, `Create`, or `ChDir` file system operation** (see the definition of the file system operations in Section 4.2). For example, if the last component of the pathname passed to an `Unlink` operation is the name of a symbolic link, then the symbolic link itself should be removed, but if the last component of the pathname passed to an `Open` operation is the name of a symbolic link, then the file to which the symbolic link refers should be opened, **not** the symbolic link itself.

### 2.6 Formatting a File System on the Disk

The hardware disk is initially empty, just as it would arrive if purchased new from the manufacturer. Before Yalnix can access a file system on this disk, the file system must first be **formatted** on the disk. Formatting a file system means to write the necessary disk data structures onto the disk to initially create a valid file system layout on the disk.

**You must format a file system on your disk before your file system server process can read (and see) a valid file system on the disk.**

We provide a Unix (rather than a Yalnix) program to format an empty, validly-formatted Yalnix file system on the disk. To use this program, execute the Unix program

```
/clear/courses/comp421/pub/bin/mkfs
```

(Run this program just as shown above from a Unix shell; do not run it under Yalnix.) This will create a YFS file system with 47 inodes (6 blocks worth of inodes). If you want a different number of inodes, put the number of inodes as the command line argument. The file system will contain only a root directory with `"."` and `".."` in it. Run `mkfs` as a Unix command, not under Yalnix, from the same directory where you will run Yalnix.

If you want to, you can modify this `mkfs.c` program to set up test cases for yourself; the source code to this program is in the file `/clear/courses/comp421/pub/yfssamples-1.lab3/mkfs.c`. For example, before you get the `MkDir` file system request working correctly in your server, you can modify `mkfs.c` to make test directories for yourself. Run that version of `mkfs` before you then run your YFS server under Yalnix. This is just one example of what you could do to test some things in your server before other things in your server are working yet. Since `mkfs.c` runs as a Unix program, you can create anything in the Yalnix disk that you want to, and then run your server and see what your server can do with the contents that it finds on the disk.

---

## 3 New Yalnix Kernel Calls

For this project, a Yalnix kernel executable will be provided for your use. This Yalnix kernel has been enhanced with the addition of support for reading and writing individual sectors of the disk. The disk is accessed by identifying the sector number to be read or written, and supplying the address of a sector-sized buffer. Function prototypes for these additional Yalnix kernel calls are provided in the header file `comp421/yalnix.h`. In particular, the following two kernel calls are available for accessing the disk:

- **`int ReadSector(int sectornum, void *buf)`**

  Initiate a read of disk sector number `sectornum` into the buffer at address `buf`. The buffer `buf` must be of size `SECTORSIZE` bytes. The calling process is blocked until the disk read completes. `ReadSector` returns the value 0 if the operation was completed successfully; however, if the indicated sector number is invalid or the indicated buffer in memory cannot be written, `ReadSector` returns `ERROR`.

- **`int WriteSector(int sectornum, void *buf)`**

  Initiate a write of disk sector number `sectornum` from the buffer at address `buf`. The buffer `buf` must be of length `SECTORSIZE` bytes. The calling process is blocked until the disk write completes. `WriteSector` returns the value 0 if the operation was completed successfully; however, if the indicated sector number is invalid or the indicated buffer in memory cannot be read, `WriteSector` returns `ERROR`.

These kernel calls wait for the read or write to be completed before returning. For this project, you may assume that the disk hardware is perfectly reliable, and that all read and write operations with valid arguments will eventually complete and return, unblocking the calling process; you may assume that no disk hardware errors are possible during the read or write.

The provided Yalnix kernel executable has also been enhanced with additional Yalnix kernel calls for interprocess communication. You may find some or all of these new IPC calls useful in the project:

- **`int Register(unsigned int service_id)`**

  Registers the calling process as providing the service `service_id`. The Yalnix kernel does not enforce that the process actually provides the service and does not know to what specific service a particular `service_id` value refers. The `service_id` values are abstract and are chosen by convention. After a process is registered with `Register`, it is referred to as a "server" for this service. Other processes can then use the `Send` kernel call to send messages to it by specifying the **negative** of this `service_id` value instead of the actual process ID as the destination of the message sent (the process ID argument will thus be a negative number in this case). Returns 0 on success; returns `ERROR` on any error, including if another current process is already registered for that `service_id`. (As defined below in Section 4.3, your YFS server should register itself for the `FILE_SERVER` service id.)

- **`int Send(void *msg, int pid)`**

  The argument `msg` points to a fixed-sized 32-byte buffer holding a message to be sent. The `Send` kernel call sends this message to the process indicated by the argument `pid`. The calling process is blocked until a reply message sent with `Reply` is received. **Note that the `Send` kernel call always sends exactly 32 bytes, beginning at address `msg` as the message.** Likewise, the reply message sent with `Reply` always overwrites exactly 32 bytes, beginning at address `msg`. Also note that all values in the 32-byte `msg`, including any pointer values, are simply sent uninterpreted, as the numerical values that they are; only those 32 bytes are sent, not anything that some pointer in the message points to.

  If the value `pid` is positive, it is interpreted as the process ID of the process to which to send the message. If the value `pid` is negative, it is interpreted instead as an indication of the service to which to send the message, and the message is sent to the process currently registered with `Register` as providing service `-pid`. On success, the call returns 0; on any error, the value `ERROR` is returned.

  Note that any values in `msg`, including pointers, are sent to the receiver uninterpreted, simply as the numerical values that they are.

  If, at the time that the `Send` begins, the destination process identified by `pid` exists, but if that process later exits or is terminated before doing a `Reply` back to this process, then at the time that the destination process does exit or is terminated, the `Send` call is completed with a return value of `ERROR`, as if the destination process had not existed.

- **`int Receive(void *msg)`**

  The argument `msg` points to a fixed-sized 32-byte buffer in the calling process's address space. The `Receive` kernel call receives the next message sent to this process with `Send`, copying that message into the buffer at `msg`. If no unreceived such message has been sent yet, the calling process is blocked until a suitable message is sent. **Note that the `Receive` kernel call always receives (and thus overwrites) exactly 32 bytes, beginning at address `msg`. On success, the call returns the process ID of the sending process; on any error, the value `ERROR` is returned.**

  However, if a process is to be blocked, as described above, due to its call to `Receive`, or if at any time while a process is already blocked due to a `Receive` call, it becomes the case that all other processes (other than the idle process) are also each blocked on some `Receive` call, the result would normally be a **deadlock**, since there would in that case be no other process available that could possibly (then or in the future) `Send` a message to this process (or any of these other processes) to satisfy its `Receive` call. Instead, to break this deadlock, the Yalnix kernel in this case returns 0 to the calling process (and to each other process also then blocked on a `Receive` call). A process that has called `Receive` can determine that its `Receive` call was unblocked for this reason, since `Receive` always otherwise returns the process ID of the sending process (which will always be non-zero) or returns `ERROR` in case of any error on the `Receive` call.

- **`int Reply(void *msg, int pid)`**

  The argument `msg` points to a fixed-sized 32-byte buffer holding a reply message to be sent by the calling process. The `Reply` kernel call sends this reply message to the process with process ID `pid`, which must be currently blocked awaiting a reply from an earlier `Send` to this process. The reply message from the calling process overwrites the original message sent in the address space of the process with process ID `pid` (indicated by the `msg` pointer passed by that process on its earlier call to `Send` that is currently blocked awaiting this reply). **Note that the `Reply` kernel call always replies with exactly 32 bytes, beginning at address `msg`. On success, the call returns 0; on any error, the value `ERROR` is returned.**

- **`int CopyFrom(int srcpid, void *dest, void *src, int len)`**

  This call copies `len` bytes beginning at address `src` in the address space of process `srcpid`, to the calling process's address space beginning at address `dest`. The process `srcpid` must be currently blocked awaiting a reply from an earlier `Send` to the calling process. On success, the call returns 0; on any error, the value `ERROR` is returned.

- **`int CopyTo(int destpid, void *dest, void *src, int len)`**

  This call copies `len` bytes beginning at address `src` in the address space of the calling process, to the address space of process `destpid`. The process `destpid` must be currently blocked awaiting a reply from an earlier `Send` to the calling process. On success, the call returns 0; on any error, the value `ERROR` is returned.

The relationship between the `Send`, `Receive`, `Reply`, `CopyFrom`, and `CopyTo` Yalnix interprocess communication kernel calls is depicted in Figure 1. The `CopyFrom` and `CopyTo` kernel calls may be used by the receiver process with respect to a given sender process only between the receiver's `Receive` from and its `Reply` to that sender process. The `src` address on the `CopyFrom` call is interpreted by the Yalnix kernel as being a virtual address in the **sending process's address space** and is the address from which `len` bytes are to be copied from the sender process (identified by `srcpid`) to this receiver. Similarly, the `dest` address on the `CopyTo` call is interpreted by the Yalnix kernel as being a virtual address in the **sending process's address space** and is the address to which `len` bytes are to be copied from this receiver to the sender process (identified by `destpid`).

### Figure 1: Relationship between Yalnix Interprocess Communication Kernel Calls

```
                 Sender process is blocked between
                 its Send and the receiver process's Reply

    Sender ─────────────────────────────────────────────────→ Time
             Send                                        ↑
                    \                                    |
                     \                                   |
                      ↘                                  |
      Receiver ────────────────────────────────────────────→
               Receive                              Reply

             Receiver process may use CopyFrom and CopyTo in order to
             read/write areas within the sender process's virtual address space
```

This incorrect code sends 32 bytes, but only 4 bytes (the size of an integer) are supplied. The remaining (32 – 4) = 28 bytes that are sent are whatever unknown data is in memory immediately following the `msg` integer variable. In addition, when a `Reply` is sent, these unknown 28 bytes will be overwritten, since the reply message is also always 32 bytes in length.

Similarly, the following code is **incorrect:**

```c
Send((void *) "hello world", pid);
```

This incorrect code sends 32 bytes, but only 12 bytes (the length of the string "hello world", including the null character at the end of the string) are supplied. The remaining (32 – 12) = 20 bytes that are sent are whatever unknown data is in memory immediately following this character string. In addition, when a `Reply` is sent, these unknown 20 bytes will be overwritten, since the `Reply` will overwrite the entire original 32 bytes: depending on the compiler and options used in compiling the program, character string "hello world" may be in read-only memory, in which case the `Reply` will fail; in other cases, in which the compiler places this string in writable memory, the "hello world" character string (which is supposed to be a **constant**) will actually get overwritten by the reply message.

Instead, the best (**correct**) way to use the `Send`, `Receive`, and `Reply` kernel calls is to define a **struct** of length 32 bytes as your message. For example, defining the following `struct` for a message would be a correct message:

```c
struct my_msg {
    int data1;
    int data2;
    char data3[16];
    void *ptr;
};
```

The size of each `int` is 4 bytes (32 bits), and the size of the `char` array here is 16 bytes. The size of the `ptr` pointer is 8 bytes (64 bits). The total size of this `struct` is 32 bytes. You could, for example, define a single generic `struct` such as this to suit your needs for all messages, or you could define a different

`struct` for each type of message you need to send. It is recommended that you put a "type" value (such as an `int` or `short` or `char` value) as the first thing in every message, so that the receiver can look at this value to determine the format of the rest of the message.

Also, be aware that the C compiler will insert "padding" into your structure if needed in order to keep each member of the structure aligned on a natural boundary for its size. For example, the compiler will ensure that a `char` is aligned on a multiple of 2 boundary from the beginning of the structure, an `int` is aligned on a multiple of 4 boundary from the beginning of the structure, and any pointer is aligned on a multiple of 8 boundary from the beginning of the structure. Also, the compiler will insert padding at the end of the structure to ensure that the total size of the structure is a multiple of 8 in size. Thus, if the members of the structure above were simply reordered as follows

```c
struct my_msg {
    int data1;
    void *ptr;
    int data2;
    char data3[16];
};
```

then the total size of this structure will no longer be 32 bytes. Instead, the compiler will insert 4 bytes of padding before the pointer `ptr` member, and will insert another 4 bytes of padding at the end of the structure; thus, in this case, the total size of the message would be **40 bytes**, not 32 bytes. This message definition **will not work** as you might intend with Yalnix message passing operations, since only the first 32 bytes of it will end up actually being sent.

**You are strongly advised to confirm the actual size of any of your message structure definitions by using the C compiler's `sizeof` operator to determine the size that the compiler actually thinks your structure is.**

---

## 4 The Yalnix File System Server

### 4.1 Overview

The Yalnix File System operates as a server executing as a regular user process outside the Yalnix kernel. Other processes using the file system send **requests** to the server and receive **replies** from the server, using the message-passing interprocess communication calls provided by the Yalnix kernel.

When making a file system request such as `Create` or `Read`, a user process calls a library procedure known as a stub function, which in turn packages the appropriate parameters into a request message and sends this message to the file server. Sending this message (with the Yalnix `Send` kernel call) also blocks the requesting process until a reply (from `Reply`) from the server is received. The server executes the request and sends a reply message back to the requesting process when the request has completed. There is a **separate copy of the library linked into each Yalnix user program that uses the file server.**

The YFS server process retains **no state** on behalf of individual client processes or open files. Thus, the server process has no knowledge of specific file descriptor numbers that represent specific open files in specific processes. Instead, all state about a particular open file is retained within the copy of the file system library that is linked in to the client process itself; any necessary state is passed in the message to the server on each file system request. This allows the server to operate without worrying whether the client process that opened any individual file still exists, greatly simplifying the design of the file server. In the Yalnix file system, there are **no reserved or otherwise special file descriptor number values**, such as standard input, output, or error.

The file system server process maintains a **cache** in memory of recently accessed inodes and recently accessed data blocks. These caches are each of a **constant size**, defined by the following two constants from `comp421/filesystem.h`:

- **`BLOCK_CACHESIZE`:** The cache of recently accessed disk blocks in the file server must be of **constant** size `BLOCK_CACHESIZE` blocks. Initially, all blocks in the cache are unused, but as new blocks are accessed, your server must manage the constant number of blocks in the cache using a **write-back LRU policy**.

- **`INODE_CACHESIZE`:** The cache of recently accessed inodes in the file server must be of **constant** size `INODE_CACHESIZE` inodes. Initially, all inodes in the cache are unused, but as new inodes are accessed, your server must manage the constant number of inodes in the cache in a **write-back LRU policy**.

Again, the block cache and the inode cache **must each be a constant size**, as described above. If a new item must be brought into the cache, you must decide which existing entry in the cache to replace to make room for it in the cache.

While executing a file system request from some client process, the file system server will generally need to read or write a number of disk blocks or inodes. You will need to maintain a hash table to allow a block in the cache (if present) to be found quickly given the block's disk block number. Similarly, you will need to maintain a (separate) hash table to allow an inode in the cache (if present) to be found quickly given the inode's inode number. The cache management policy is **write-back** in that dirty blocks (or inodes) are left in the cache (marked "dirty") until that cache block (or cache inode) is needed for holding a different disk block (or inode). At that time, the cached value is written back to disk and the new value is read into that space in the cache. When writing a cached disk block back to disk, you will need to use the `WriteSector` kernel call, but when writing an inode back to disk, you should simply write it back to the disk block cache for the disk block in which that inode lives; you leave this disk block in the cache marked "dirty;" and actually write it out to the disk later, when you need that space in the block cache for a different disk block.

### 4.2 User Process File System Operations

A user process using the YFS file system makes file system requests by calling one of the procedures defined within your file system library. The library remembers the current directory of the process and maintains information about each file open by the process. The file system library interacts with the file server process through the Yalnix IPC facilities. The interface to the file system library is defined in the C header file `comp421/iolib.h`. This header file is located in

```
/clear/courses/comp421/pub/include/comp421/iolib.h
```

**Do not copy this file into your own directory.** This file can be included in your source file using

```c
#include <comp421/iolib.h>
```

When opening a file with either the `Open` or `Create` request, your **library** must allocate a data structure to remember information about the open file. Specifically, in your library, you will need to remember

- the file's inode number, and
- the current position within the file.

The library must support up to a maximum of `MAX_OPEN_FILES` open files at a time; if, in your file system library, you run out of unused file descriptor numbers within this limit, you should return `ERROR` in response to any operation (`Open` or `Create`) that attempts to open a new file. For an `Open` or `Create` request, your file system library learns the file's inode number from the file server process (the current position within the file is initialized to 0) and stores this within the data structure in the library representing that open file. The file descriptor number assigned for this newly opened file **must be the lowest available (unused) file descriptor number that could be assigned for this newly opened file. The user process then uses this file descriptor number on future requests (such as `Read` and `Write`) to refer to the open file.

The file descriptor numbers used by a process are assigned by and managed by the copy of your library linked into the user program running in that process. The file descriptor number is part of the procedure call interface between a user program and the copy of your library linked into that program; the file descriptor number is **not** part of the message interface between your library and your file server process.

Your library, together with your file server process, must support the following procedure call requests, with the indicated procedure names, arguments, and return values:

- **`int Open(char *pathname)`**

  This request opens the file named by `pathname`. If the file exists, this request returns a file descriptor number that can be used for future requests on this opened file; the file descriptor number assigned for this newly open file **must be the lowest available (unused) file descriptor number that could be assigned for this newly opened file. If the file does not exist, or if any other error occurs, this request returns `ERROR`. It is **not** an error to `Open()` a directory; the contents of the open directory (the bytes in the format of a directory) can then be read using `Read()`, although it is an error to attempt to `Write()` to an open directory. If the `Open` is successful, the current position for `Read` or `Write` operations on this file descriptor begins at position 0.

  Within a process, each successful call to `Open` or `Create` must return a new, unique file descriptor number for the open file. Each such instance of a file descriptor open to this file thus has its own, separate current position within the file.

- **`int Close(int fd)`**

  This request closes the open file specified by the file descriptor number `fd`. If `fd` is not the descriptor number of a file currently open in this process, this request returns `ERROR`; otherwise, it returns 0.

- **`int Create(char *pathname)`**

  This request creates and opens the new file named `pathname`. All directories in the named pathname must already exist; the `Create` request creates only the last file name component in the indicated pathname. If the named file already exists, this request instead truncates the size of the existing file to 0 and opens the now empty file (this truncation of the file does not increase the `reuse` count in the file's inode, as it is still the same file, just truncated then to size 0). However, note that it is an error to attempt to `Create` a file with the same name as an existing directory, since truncating a directory to size 0 would be equivalent to removing all of the names in that directory including the `.` and `..` entries, which is not allowed. On success, this request returns a file descriptor number that can be used for future requests on this open file; the file descriptor number assigned for this newly created and opened file **must be the lowest available (unused) file descriptor number that could be assigned for this newly opened file. Otherwise, this request returns `ERROR`. If the `Create` is successful, the current position for `Read` or `Write` operations on this file descriptor begins at position 0.

  Within a process, each successful call to `Open` or `Create` must return a new, unique file descriptor number for the open file. Each such instance of a file descriptor open to this file thus has its own, separate current position within the file.

- **`int Read(int fd, void *buf, int size)`**

  This request reads data from an open file, beginning at the current position in the file as represented by the given file descriptor `fd`. The argument `fd` specifies the file descriptor number of the file to be read. `buf` specifies the address of the buffer in the requesting process from which to perform the read, and `size` is the number of bytes to read from the file. This request returns the number of bytes read, which will be 0 if reading at the end-of-file; the number of bytes read will be the minimum of the number of bytes requested and the number of bytes remaining in the file until end-of-file. Upon successful completion, the current position in the file as represented by the given file descriptor `fd` should be advanced by the number of bytes read, and only this number of bytes within the caller's `buf` should be modified (bytes beyond this count must remain unchanged). On any error, this call should return `ERROR`. It is **not** an error to attempt to `Read()` from a file descriptor that is open on a directory. Unless this `Read` operation returns `ERROR`, the current position for subsequent `Read` or `Write` operations on this file descriptor advances by the number of bytes read (the value returned by the `Read` request).

- **`int Write(int fd, void *buf, int size)`**

  This request writes data to an open file, beginning at the current position in the file as represented by the given file descriptor `fd`. The argument `fd` specifies the file descriptor number of the file to be written. `buf` specifies the address of the buffer in the requesting process from which to perform the write, and `size` is the number of bytes to be written to the file. This request returns the number of bytes written, which may be less than the number of bytes requested to be written (a return value of 0 indicates that nothing was written). Upon successful completion, the current position in the file as represented by the given file descriptor `fd` should be advanced by the number of bytes written. Any error should return `ERROR`. It is an error to attempt to `Write()` to a file descriptor that is open on a directory. Unless this `Write` operation returns `ERROR`, the current position for subsequent `Read` or `Write` operations on this file descriptor advances by the number of bytes written (the value returned by the `Write` request).

- **`int Seek(int fd, int offset, int whence)`**

  This request changes the current file position of the open file specified by file descriptor number `fd`. The argument `offset` specifies a byte offset in the file relative to the position indicated by `whence`. The value of `offset` may be positive, negative, or zero. The value of `whence` must be one of the following three codes defined in `comp421/iolib.h`:

  - `SEEK_SET`: Set the current position of the file to be `offset` bytes **after** the beginning of the file.
  - `SEEK_CUR`: Set the current position of the file to be `offset` bytes **after** the current position it in the open file indicated by file descriptor `fd`.
  - `SEEK_END`: Set the current position of the file to be `offset` bytes **after** the current end of the file.

  It is an error if the `Seek` attempts to go before the beginning of the file. This request returns as its result the new position (offset) in the open file, unless an error is encountered, in which case the value `ERROR` is returned instead. As an example, `Seek(fd, 0, SEEK_END)` seeks to the end of the file and returns the file size.

  The `Seek` file system operation **does allow** the file offset to be set **beyond** the end of the existing size of the file; this is **not** an error. Note, though, that **this, itself, does not change the size of the file.** However, if data is later written at this new offset in the file, the size of the file is then set to represent the total size of the file **including the gap** (a "hole" in the file) that had been skipped over by the earlier `Seek` operation. Also, in this case, subsequent `Reads` of the data in the gap (the "hole" in the file) return bytes of zeros (`'\0'`) for the bytes of the hole, until any data is actually written into the hole.

- **`int Link(char *oldname, char *newname)`**

  This request creates a (hard) link from the new file name `newname` to the existing file `oldname`. The files `oldname` and `newname` need not be in the same directory. The file `oldname` must not be a directory. It is an error if the file `newname` already exists. On success, this request returns 0; on any error, the value `ERROR` is returned.

- **`int Unlink(char *pathname)`**

  This request removes the directory entry for `pathname`, and if this is the last link to a file, the file itself should be freed and reuse its inode. The file `pathname` must not be a directory. On success, this request returns 0; on any error, the value `ERROR` is returned.

- **`int SymLink(char *oldname, char *newname)`**

  This request creates a symbolic link from the new file name `newname` to the file name `oldname`. The files `oldname` and `newname` need not be in the same directory. It is an error if the file `newname` already exists. The file `oldname` need not currently exist in order to create a symbolic link to this name. Indeed, when creating a symbolic link, the target is simply recorded as-is, without any processing, except that it is an error to attempt to create a symbolic link to an empty pathname (a null string). On success, this request returns 0; on any error, the value `ERROR` is returned.

- **`int ReadLink(char *pathname, char *buf, int len)`**

  This request reads the name of the file that the symbolic link `pathname` is linked to; the named pathname must be that of a symbolic link. On success, this request returns the length (number of characters) of the name that the symbolic link `pathname` points to (or the value `len`, whichever is smaller), and places in the buffer beginning with address `buf` the name that the symbolic link points to, up to a maximum number of characters of `len` characters; if the name that the symbolic link points to is longer than `len` bytes, the name is truncated as returned in the buffer `buf` (this is not an error). The characters placed into `buf` are **not terminated by a `'\0'` character.** On any error, the value `ERROR` is returned.

- **`int MkDir(char *pathname)`**

  This request creates a new directory named `pathname`, including the `"."` and `".."` entries within the new directory. It is an error if the file `pathname` exists. On success, this request returns 0; on any error, the value `ERROR` is returned.

- **`int RmDir(char *pathname)`**

  This request deletes the existing directory named `pathname`. The directory must contain no directory entries other than the `"."` and `".."` entries and possibly some free entries. On success, this request returns 0; on any error, the value `ERROR` is returned. Note that it is an error to attempt to remove the root directory; it is also an error to attempt to remove individually the `"."` or `".."` entry from a directory.

- **`int ChDir(char *pathname)`**

  This request changes the current directory within the requesting process to be the directory indicated by `pathname`. The current directory of a process should be remembered by the **inode number** of that directory, within the file system library in that process. That current directory inode number should then be passed to the file server on each request that takes any file name arguments. The file `pathname` on this request must be a directory. On success, this request returns 0; on any error, the value `ERROR` is returned.

- **`int Stat(char *pathname, struct Stat *statbuf)`**

  This request returns information about the file indicated by `pathname` in the information structure at address `statbuf`. The information structure is defined within `comp421/iolib.h` as follows:

  ```c
  struct Stat {
      int inum;       /* inode number of file */
      int type;       /* type of file (e.g., INODE_REGULAR) */
      int size;       /* size of file in bytes */
      int nlink;      /* link count of file's inode */
  };
  ```

  The fields in the information structure are copied from the information in the file's inode. On success, this request returns 0; on any error, the value `ERROR` is returned.

- **`int Sync(void)`**

  This request writes all dirty cached inodes back to their corresponding disk blocks (in the cache) and then writes all dirty cached disk blocks to the disk. The request does not complete until all dirty inodes and disk blocks have been written to the disk; this request always then returns the value 0.

- **`int Shutdown(void)`**

  This request performs an orderly shutdown of the file server process. All dirty cached inodes and disk blocks should be written back to the disk (as in a `Sync` request), and the file server should then call `Exit` to complete its shutdown. As part of a `Shutdown` request, the server should print an informative message indicating that it is shutting down. This request always then returns the value 0.

As noted above in Section 2.2.2, the `reuse` field in each inode indicates the number of times that this particular inode has been "reused" since the file system was originally formatted. When the file system is formatted, the `reuse` field in each inode should be initialized to 0. Each time an inode is allocated (from being free), the `reuse` count in the inode must be incremented. The `reuse` count in each inode allows your file system to ensure, for example after doing an `Open` on some file, that on later attempts to `Read` or `Write` that open file, the **same file still exists and that the inode has not instead been reused since that `Open` for some different file.** If the inode's current `reuse` count for some request does not match the `reuse` count that the file had when it was opened, your server should return `ERROR` for that request.

### 4.3 Running Yalnix, the Server, and User Processes

For your use in this project, we have provided an enhanced Yalnix kernel executable that supports access to the disk and IPC calls, as described in Section 3. You should implement your server process to run on top of this Yalnix kernel. The provided Yalnix kernel is located on CLEAR at

```
/clear/courses/comp421/pub/bin/yalnix
```

**Do not copy this program to your own directory.** To use this Yalnix kernel, you may either run it using the full pathname above, or you may put the `/clear/courses/comp421/pub/bin` directory on your shell's executable search path and can then run this kernel as just `yalnix`.

As in Lab 2, the Yalnix kernel automatically starts only one process (other than the idle process) at boot time. This process, commonly known as the "init" process, must then use `Fork` and `Exec` to create any other processes or run any other programs, as needed.

For this project, you should execute your YFS server process as the Yalnix "init" process. As part of its initialization, the YFS server should use the `Register` kernel call to register itself as the `FILE_SERVER` server. It should then `Fork` and `Exec` a first client user process. In particular, like any C `main()` program, your file server process is passed an `argc` and `argv`. If this `argc` is greater than 1 (the first argument, `argv[0]`, is the name of your file server program itself), then your server should `Fork` and `Exec` the first client user program, as:

```c
Exec(argv[1], argv + 1);
```

This uses the second argument as the name of the process to execute, and passes this and the remaining arguments to that process as its command line arguments.

For example, to execute the Yalnix kernel, with your file server as the kernel "init" process, and a program `testprog` as the first client user process, you should use the shell command:

```
yalnix yfs testprog testarg1 testarg2 testarg3 ...
```

where `testarg1`, `testarg2`, `testarg3`, etc., are command line arguments to give in `argv` to the program `testprog`. The program `testprog` can then create any additional processes itself as needed using `Fork` and `Exec`. In essence, this `testprog` program serves the role played by the init process in Lab 2.

Within your file system library, a client process can send a message to your file server process using the `Send` kernel call by sending a message to the `FILE_SERVER` service id:

```c
Send(msg, -FILE_SERVER);
```

Since the file server process registers itself as the `FILE_SERVER` service id, this message will be sent to your file server process without client process needing to know the real process id of the file server.

As in Lab 2, you can use `TracePrintf` to help with debugging your YFS server process. The `TracePrintf` call is used the same as it was in Lab 2:

```c
TracePrintf(int level, char *fmt, args...)
```

where `level` is an integer tracing level, `fmt` is a format specification in the style of `printf`, and `args` are the arguments needed by `fmt`. The tracing level for the YFS server process is independent of the tracing level for the hardware, kernel, or regular user processes. To set the tracing level for the YFS server process, add `"-ly level"` to the `yalnix` command line. For example, using

```
yalnix -ly 5 yfs testprog testarg1 testarg2 testarg3 ...
```

sets the file server `TracePrintf` level to 5. You may also include any of `"-lh level"`, `"-lk level"`, and/or `"-lu level"` on the `yalnix` command line to set, respectively, the hardware, kernel, and/or user `TracePrintf` tracing level, as well. The `"-ly level"` setting affects `TracePrintf` calls made by your Yalnix server process.

---

## 5 Your Assignment

### 5.1 General Specification

You are to implement the Yalnix file system, consisting of **both** of the two parts described below:

- **A YFS file server process.** You must implement a Yalnix user program named `yfs`. This process receives messages from client processes, executes the requested file system operations, and returns reply messages to the requesting processes.

- **A YFS file system library.** You must implement a Unix library archive file named `iolib.a`. This library defines a procedure for each of the file system requests defined in Section 4.2. Each of these procedures communicates with the server process using the Yalnix IPC kernel calls in order to request the server process to perform the necessary file system operation, and finally returns a status return value as the result of the user call.

In other words, your **file server process** implements most of the functionality of the Yalnix file system, managing a cache of inodes and disk blocks and performing all input and output with the disk drive. Your **file system library** consists of a collection of procedures called by Yalnix user programs using the file server; the file system library maintains information on the current directory of the process and on each file open by the process, and uses the Yalnix IPC facility to communicate with the server. Mostly, each file system library procedure is just a stub procedure that formats and sends a request message to the Yalnix file system server process, which then completes that request and returns a reply message. Your project must execute on CLEAR using the provided enhanced Yalnix kernel.

The interface to your file server **library** consists of the procedures defined in Section 4.2. The format of the data that you must send on disk for the file system is defined in Section 2. You must design your own messages for communication between your file system library and file system server.

A template `Makefile` for this project is available as `Makefile.lab3.template` in the directory `/clear/courses/comp421/pub/templates`. You should copy this file to your project directory and edit it as described in the comments in the file. **In particular, this template `Makefile` contains special rules for compiling and linking programs, including your Yalnix file server `yfs`, which must be used for the project to work.**

The project should be done in groups of two students. This project requires a reasonable amount of work, so you will need to divide the load between the two group members. If you **really** want to, you may work on this project by yourself, without a partner, but I **strongly recommend against** this. All projects will be graded in the same way, regardless of whether you worked with a partner or not, so you are only making more work for yourself.

As with other parts of this course, we will be using Piazza for class discussion (please use the "lab3" folder). Please check Piazza regularly; you will likely find it very helpful in the project. As before, when posting questions or answers about the project on Piazza, please be careful about what you post, to avoid inadvertently violating the COMP 421 course Honor Code policy described in the course syllabus and below in Section 5.4. **Specifically, please do not post details about your own project solution, such as portions of your source code or details of how your code works, in public questions or answers on Piazza.** If you need to ask a question that includes such details, please make your question private on Piazza by selecting "Instructor(s)" (rather than "Entire Class") for "Post to" at the top, so that only the course instructor and TAs can see your posting.

Please try to regularly check Piazza for new questions and answers. These Piazza posts can be a valuable resource to you as you work on the project, for example clarifying issues even before you have realized that you have a problem and need those issues clarified; checking this can potentially save you a lot of of time and possible frustration during the project, and can save you from posting a redundant question on Piazza that has been asked and answered already. To easily check for new messages on Piazza, you can, for example, click on the "Unread" or "Updated" buttons near the top-left corner of the Piazza web view, to show my messages that, respectively, you have not read yet or have been updated since you last read them. You can also use the Piazza Search functionality to look for existing messages related to a given question you have or problem you may be having.

### 5.2 For Students Enrolled in COMP 521 or ELEC 552

For students taking COMP 521 or ELEC 552, the **graduate student** version of this course, in addition to implementing the Yalnix file system server and library as described above, you must also write and submit a report addressing issues including the files affecting the performance of the design in your solution to the project and the issues involved in scaling the project to larger sizes in larger systems. Students taking COMP 521 or ELEC 552, the **undergraduate student** version of this course, may ignore this section, as this project requirement does not apply to you.

For students taking the graduate version of this course, your report must be written **individually** and not in collaboration with your partner in your group for this project; each student must write their own individual report. Your report should address issues such as, but not necessarily limited to, the general points below:

- What design choices did you make in your solution to the project that you intended to improve the performance of your solution? Explain these design choices and **why they should improve performance.**

- What design choices **would** you have made in your solution, if you'd had more time to implement them, that could have improved the performance of your solution? Explain these design choices and **why they should have improved performance.**

- What are the biggest issues affecting the ability of your solution, and what your solution **could** have been if you'd had more time, to scale to larger sizes and larger systems? Think about things such as systems with larger disks and larger file systems; more total files, more directories, and more files per directory; larger files, and more client processes using the file system, etc. Think about factors including execution time, performance, memory consumption, and disk usage.

In thinking about and discussing these issues in your report, you should consider them from the point of view of how they would affect an entirely real system. For example, do not discuss issues of performance or how to scale the system in light of the simulated hardware used in the project; rather, discuss performance and scale as would be appropriate for your server and library on entirely real hardware. Also, try to be as specific as you can in discussing each issue, rather than just making generalized, vague statements.

Your report **must be limited to a maximum of 6 pages**, although there is no **minimum** required length for your report; the report may be formatted in either single-column or double-column format and should use a font size no smaller than 10 points. Your report will be graded based on what you say in it, not on how many pages you fill. In writing your report, please try to think critically about what you want to say and to say it clearly.

Note that your report should **not simply be a description of your implementation.** Rather, your report must **specifically address the issues mentioned above.** That is, it is not correct to just give generalized vague statements; and your report should just be a description of your design. You **must address the issues above.**

In addition to the limit of a **maximum report length of 6 pages**, your report **must be submitted in PDF format** (e.g., **not in other strange formats such as Microsoft Word**).

As with the second project in this course, and **unlike for the first project**, to submit your report for this project, please email it to me at

```
dblj@rice.edu
```

**Both the email "Subject:" line and the file name for your report must be**

```
report-lab3-netid.pdf
```

where `netid` is your Rice NetID name. Only reports named as described above that are emailed to me as described above will be graded. When I receive your report email, I will send you an acknowledgment email letting you know that I received your report; if you do not receive this acknowledgment email, please let me know right away. Do not include your report with the files that you submit using the `lab3submit` program (defined in Section 5.3 below).

The deadline for emailing your report to me is the same as the submission deadline for the project as a whole.

### 5.3 Submitting your Project for Grading

Your project is due by **11:59 PM, Friday, April 19, 2024.** The project should be done in groups of 2 students, and you should only submit one project for your group (only one group member should submit the project for the group).

Similar to turning in your Lab 1 and Lab 2, everything you want to turn in for your group should be in a single directory (or its subdirectories). Before turning in your project, please create a plain text file named "README" in the directory where your files for the project are located. Your "README" file **must be in plain text format** (i.e., **not in PDF format or in other strange formats such as Microsoft Word**).

Please describe in your "README" file anything you think it would be helpful for the TAs to know in grading your project. This might, for example, mean describing unusual details of your algorithms or data structures, and/or describing the testing you have done and what parts of the project you think work (or don't work). Also, in your "README" file, please list the names and NetIDs of both members in your project group.

Actually submitting your project for grading will be done similarly to what was done for Lab 1 and Lab 2. Specifically, to submit your project for grading when you are ready, one of the group members should perform the following two steps:

- First, on CLEAR, change your current directory to the directory where your files for the project for grading are located. For example, use the `cd` command to change your current directory. When you run the submission program, it will submit everything in (and below) your current directory, including all files and all subdirectories (and everything in them, too). Please make sure all files you want us to see for grading are located in this single directory and/or in subdirectories of this single directory.

- Second, on CLEAR, run the submission program

  ```
  /clear/courses/comp421/pub/bin/lab3submit
  ```

  This program will check with you that you are in the correct directory that you want to submit for grading, and will confirm with you your name and the name of the partner you have been working with on the project. Finally, the `lab3submit` program will normally just print

  ```
  SUCCESS! Your Lab 3 project submission is complete.
  ```

  If you get any error messages in running `lab3submit`, please let me know.

- After your submission, you should also receive an email confirmation of your submission, including a listing of all of the individual files that you submitted.

**Only one of the two group members need to do the above steps.** If both members of a project group run the `lab3submit` program, the one who runs it second will simply overwrite the submission of the one who ran it first. Only one submission per **group** will be recorded.

Note, however, that **both students in a group are jointly responsible for what the group submits for their project.** Only one of the two students in a project group actually does the submission, but both partners should agree on what the group submits for their project. Each student in the group must agree on what is to be submitted. Also, after a project submission, both partners should receive email (also sent automatically to the instructor) detailing the submission and the files that were included in it. Please carefully check this email when you receive it to make sure that the right files were submitted. In any case, if you have a concern about what your partner has submitted for your group, try to work it out with your partner (and if needed, you can redo the submission with a different version of the project files); if you still have a concern about a submission by your group, contact the instructor.

And again, for students taking the **graduate student** version of this course, COMP 521 or ELEC 552, **do not include your project report in the files that your group submits with the `lab3submit` program.** Rather, please email your report to me as described above in Section 5.2. Also remember, **each student must write their own individual report**, not jointly together with your group partner for the project (together with anyone else).

### 5.4 Honor Code Policy

**The Honor Code is a special privilege and responsibility at Rice University.** As stated in a student editorial published in the January 20, 2016 edition of **The Rice Thresher:** "As incoming students enter Rice, many are surprised by the degree to which the university's Honor Code extends into the student body. . . . The privileges of the Honor Code stem from the idea that Rice's aim is not just to instill knowledge in its students, but [to] also help them develop moral character. This idea is fundamental to Rice's identity: Students can and should be held to a high moral character standard."

As a reminder, as previously stated, all assignments in this course are conducted under the Rice Honor Code, a code that you pledged to honor when you matriculated at Rice. You are expected to behave in all aspects of your work in this course according to the Rice Honor Code. When in doubt as to whether a specific behavior is acceptable, ask the instructor for a written clarification. **Suspected Honor Code violations on the projects and/or exams in this course will be researched, documented, and reported in extensive detail to the Rice Honor Council or Rice Graduate Honor Council.** For more information on the Rice Honor Code

**System**, see http://honor.rice.edu/ and http://gradhonor.rice.edu/ . In particular, you should consult the Honor System Handbook at

```
http://honor.rice.edu/honor-system-handbook/
```

This handbook outlines the University's executions for the integrity of your academic work, the procedures for resolving alleged violations of those expectations, and the rights and responsibilities of students and faculty members throughout the process.

For the programming assignments in this course, students are encouraged to talk to each other, to the TAs, to the instructor, about the assignment. This assistance, however, must be limited to a general discussion of the problem; **each student or project group must produce their own solution to each programming project.** Consulting or copying, in any manner, another student's or project group's solution is prohibited, and submitted solutions may not be copied from any source. Also, as noted above in Section 5.3, although only one of the two students in a project group should actually do the submission using the `lab3submit` program, both students in the group are responsible for what the group submits for their project.

Also, for students taking COMP 521 or ELEC 552, **for all projects, each student must write their own report on the project; for the last two projects, for which the project itself will be done in groups of two students, each student in the group must still write their own report, not working together on your report with your group partner for the project (together with anyone else).**

In addition, for all programming assignments, you may not place source code for your project on any **publicly accessible repository**, such as GitHub, including even after the end of the semester; to do so would be a violation of the Honor Code, as it would give aid to other students on the project. Also, if any such public repositories do exist, you may not refer to them (or other such sources) in working on or producing your solution for such project.

I want to treat you all in this class as responsible adults. But please be aware that **cheating on any of the programming projects in this class constitutes a Rice Honor Code violation.** Submitting a case to the Rice Honor Council requires a lot of work on my part, your future status as a student at Rice University, and your prospects for a degree from Rice. Please do not make me have to submit any Honor Council cases in this class. This will help both you and me. As I said, I want to treat you all in this class as responsible adults.

