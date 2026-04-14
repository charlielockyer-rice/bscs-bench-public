# COMP 421 Lab 1: MonTTY Terminal Device Driver

## 1 Project Overview

Implement a terminal device driver using a Mesa-style monitor for synchronization of interrupts and concurrent driver requests from different user threads that share an address space.

The project uses a software terminal emulation rather than real terminal hardware, and a simplified threads library instead of POSIX Pthreads.

## 2 Specifics

### 2.1 Environment

- Your solution must be implemented in the C programming language.

- All compiling, linking, and executing must be done on the CLEAR Linux systems at Rice.

- The terminal hardware simulation requires an X11 display (the test runner provides this via Xvfb).

- **Do not reverse-engineer the hardware emulator, library binaries, or test infrastructure.** Treat `liblab1.a`, `lab1tty`, and the test programs as black boxes. Your implementation should be based solely on the API described in this document (Sections 4–8). Do not use `nm`, `strings`, `objdump`, or similar tools on course binaries.

- Your use of threads must be limited to the package provided for the project. You may **not** directly use Pthreads or any other threads package.

### 2.2 Form

- The source code for your terminal device driver must be in a single source file named `montty.c`, and your device driver must compile into a single object file named `montty.o`.

- You may use only a total of *one* monitor in your solution (e.g., *not* one monitor per terminal).

- All of the procedures defined in Section 6.1 (`ReceiveInterrupt` and `TransmitInterrupt`) and in Section 6.2 (`WriteTerminal`, `ReadTerminal`, `InitTerminal`, `TerminalDriverStatistics`, and `InitTerminalDriver`) *must all be entry procedures of your monitor*. Your monitor must have *no* other entry procedures.

- **You may not create any threads that are simply a part of your terminal device driver.** The only threads you may create should be the users of your terminal device driver (those that call procedures such as `ReadTerminal` and `WriteTerminal`). You may *not* call the `ThreadCreate` function anywhere within your terminal driver.

- Your terminal device driver must guarantee that no deadlock is possible and must generally also ensure that no starvation is possible as well. The one case where starvation may be allowed is due to Mesa monitor semantics rather than Hoare semantics — specifically, the `while` loop around condition variable waits required for correctness may itself be a source of possible starvation.

### 2.3 Grading

- We will evaluate the correct operation of your terminal device driver through a series of test programs.

- Projects that do not correctly follow the required paradigms (particularly for structuring a Mesa monitor, as described in Section 8.2) will be severely penalized.

- Performance is also important — projects that perform badly due to synchronization issues will encounter penalties.

---

## 3 What is a Terminal Device Driver?

A device driver is a module within an operating system that manages some particular external device and encapsulates the messy details of interacting with the device's hardware.

Device drivers are generally structured into a "top half" and a "bottom half." The top half deals with reacting to the application programs that request service from the I/O device, such as "read" and "write" calls. The bottom half deals with reacting to the hardware, such as servicing interrupts from the device hardware. The top and bottom halves coordinate their work by sharing data structures within the device driver.

To work correctly, this coordination must use some form of synchronization in controlling access to those shared data structures. Also, device drivers usually must implement some sort of synchronization with user programs in order to keep different concurrent requests from different programs from interfering with each other.

---

## 4 The Terminal Hardware

In real life, a terminal is an independent piece of hardware that provides a keyboard and a screen. The terminal hardware consists mainly of two registers: an input data register and an output data register. Input and output occur one character at a time through these registers.

**Output:** When a character is ready to be sent to the screen, it is placed in the output data register; the terminal hardware then causes it to appear on the screen. When the character has been completely displayed, the terminal generates a *transmit interrupt* to the CPU, indicating that the output data register is now free to accept another character.

**Input:** When a key is pressed on the keyboard, the terminal hardware places the character in the input data register. The terminal then generates a *receive interrupt* to the CPU, indicating that a character has been placed in the input data register and is ready to be read.

The "terminals" for this project are emulated in software using the X11 Window System. Each terminal is represented by a separate X11 window.

There can be `NUM_TERMINALS` (defined in `hardware.h`) different terminal devices in this system, with terminal numbers from `0` to `NUM_TERMINALS-1`.

---

## 5 The Hardware Emulation API

The terminal hardware emulation layer provides the following procedures, which are the *only* procedures through which your terminal driver may interact with the terminal hardware. These are defined in `hardware.h`:

```c
void WriteDataRegister(int term, char c);
```
Writes character `c` to the output data register for terminal `term`. You must *not* write another character to the same terminal until you receive a transmit interrupt for this terminal indicating that the previous character has been completely transmitted.

```c
char ReadDataRegister(int term);
```
Reads and returns the character currently in the input data register for terminal `term`. This should be called from your `ReceiveInterrupt` handler after the hardware generates a receive interrupt for this terminal.

```c
int InitHardware(int term);
```
Initializes the hardware for terminal `term`. This is called by the library's `InitTerminal()` function. Returns 0 on success, `ERROR` on failure. Calling this multiple times for the same terminal is undefined.

---

## 6 Required Terminal Device Driver Procedures

### 6.1 Interrupt Handlers (Bottom Half)

Your terminal device driver must define the following two interrupt handler procedures:

```c
void ReceiveInterrupt(int term);
```
Called by the hardware once for each character typed on the keyboard of terminal `term`, after that character has been placed in the input data register. Read the character using `ReadDataRegister()`. **You must not block the current thread** (i.e., wait on a condition variable) within this handler, since further receive interrupts cannot be delivered until this call returns.

```c
void TransmitInterrupt(int term);
```
Called by the hardware once for each character written to the output data register for terminal `term`, after the character has been completely transmitted. After calling `WriteDataRegister()`, you must assume the output data register is "busy" until you receive the corresponding `TransmitInterrupt`.

### 6.2 User-Callable Procedures (Top Half)

Your terminal device driver must also define the following user-callable procedures:

```c
int WriteTerminal(int term, char *buf, int buflen);
```
Writes the `buflen` characters from `buf` to terminal `term`. Characters are written to the terminal one at a time through the output data register, with appropriate output character processing (Section 7.3). This call blocks until all characters have been written. Returns the number of characters written on success, or `ERROR` on any error (invalid terminal number, NULL buffer, negative length, etc.).

The `user_in` count in the terminal's statistics should be increased by `buflen` when `WriteTerminal` is called.

Multiple concurrent `WriteTerminal` calls to the same terminal must be serialized — one writer completes before the next begins. Echo output from keyboard input always has priority over `WriteTerminal` output.

```c
int ReadTerminal(int term, char *buf, int buflen);
```
Reads the next line of input from terminal `term` into `buf`. Input is line-oriented (Section 7.2): the call blocks until a complete line (terminated by `\n`) is available, or until `buflen` characters have been placed in `buf`, whichever comes first. Returns the number of characters placed in `buf` on success, or `ERROR` on any error. A return of 0 for a `buflen` of 0 is not an error.

The `user_out` count in the terminal's statistics should be increased by the number of characters returned by `ReadTerminal` when it returns.

```c
int InitTerminal(int term);
```
Initializes terminal number `term` by calling `InitHardware(term)`. Returns 0 on success, `ERROR` on any error (invalid terminal number, terminal already initialized, `InitTerminalDriver` not yet called, etc.).

```c
int TerminalDriverStatistics(struct termstat *stats);
```
Returns statistics about the terminal driver in the `stats` array (which must have room for `NUM_TERMINALS` entries). Returns 0 on success, `ERROR` on any error (NULL pointer, `InitTerminalDriver` not yet called, etc.).

The `struct termstat` (defined in `terminals.h`) contains:
```c
struct termstat {
    int tty_in;     // characters received from terminal hardware
    int tty_out;    // characters sent to terminal hardware
    int user_in;    // characters received from user (WriteTerminal buflen)
    int user_out;   // characters returned to user (ReadTerminal return value)
};
```

Before `InitTerminal` is called for a terminal, all four fields for that terminal should be `-1`. After `InitTerminal`, all four fields should be `0`, and then incremented as operations occur.

```c
int InitTerminalDriver(void);
```
Initializes the terminal driver. Must be called exactly once, before any other terminal driver procedure. Returns 0 on success, `ERROR` on any error (already initialized, etc.).

---

## 7 Terminal Behavior

### 7.1 Input/Output Character Processing

The terminal driver must perform character processing on input and output:

**Output Character Processing** (for echo and `WriteTerminal`):

| Character | Processing |
|-----------|-----------|
| `\n` (newline) | Output as `\r\n` (carriage return + newline) |
| All other characters | Output as-is |

**Input Character Processing** (in `ReceiveInterrupt`):

| Character | Echo to Terminal | Place in Input Buffer |
|-----------|------------------|----------------------|
| `\r` (carriage return) | Echo `\r\n` | Store `\n` |
| `\n` (newline) | Echo `\r\n` | Store `\n` (completes a line) |
| `\b` or `\177` (backspace/delete) | If buffer non-empty: echo `\b` ` ` `\b` (three chars). If buffer empty: nothing. | Remove last character from buffer |
| Any other char (buffer not full) | Echo the character | Store the character |
| Any other char (buffer full) | Nothing (optionally ring bell `\a`) | Drop the character |

The input buffer should hold up to `TERMINAL_MAX_LINE` characters (defined in `terminals.h`).

The `tty_in` count should be incremented once for each character received from the hardware (each `ReceiveInterrupt` call). The `tty_out` count should be incremented once for each character sent to the hardware (each `WriteDataRegister` call).

### 7.2 Line-Oriented Input

Input is line-oriented. When a user calls `ReadTerminal`, the call blocks until a complete line is available. A "line" is a sequence of characters terminated by `\n`.

If a `ReadTerminal` request asks for fewer characters than are in the current line, the remaining characters stay in the buffer for the next `ReadTerminal` call.

**Example:** Suppose the following characters are typed (with `\b` representing backspace):

```
Hello\b\b\b\bi\nUniverse\b\b\b\b\b\b\b\bWorld\nGood bye
```

The input buffer would contain: `Hi\nWorld\nGood bye`

Now suppose the application makes these calls:

```c
char buf1[2], buf2[10], buf3[10], buf4[10];
int len1, len2, len3, len4;

len1 = ReadTerminal(0, buf1, 2);
// len1 = 2, buf1 = "Hi"

len2 = ReadTerminal(0, buf2, 10);
// len2 = 1, buf2 = "\n"

len3 = ReadTerminal(0, buf3, 10);
// len3 = 6, buf3 = "World\n"

len4 = ReadTerminal(0, buf4, 10);
// blocks until \n is typed, then returns "Good bye\n" (or portion)
```

The first `ReadTerminal` returns `"Hi"` (2 characters, filling the buffer before `\n`). The second returns `"\n"` (the remaining newline from the first line). The third returns `"World\n"` (complete second line). The fourth blocks because `"Good bye"` has no terminating `\n` yet.

### 7.3 Output Processing for WriteTerminal

When `WriteTerminal` outputs characters to the terminal, newline characters (`\n`) in the buffer should be output as `\r\n` (carriage return followed by newline). All other characters are output as-is.

### 7.4 Echo Priority

Echo output from keyboard input always has priority over `WriteTerminal` output. If both echo and `WriteTerminal` output are pending, echo should be sent first.

---

## 8 Using Threads and Concurrency

### 8.1 The Threads API

The threads package provides (defined in `threads.h`):

```c
thread_id_t ThreadCreate(void (*func)(void *), void *arg);
```
Creates a new thread that begins executing `func(arg)`. Returns an ID that can be used with `ThreadWait`.

```c
void ThreadWait(thread_id_t th);
```
Blocks the calling thread until thread `th` has exited.

```c
void ThreadWaitAll(void);
```
Blocks the calling thread until all other threads have exited.

### 8.2 Mesa Monitor Synchronization

Your terminal device driver must be implemented as a **Mesa-style monitor**. The monitor provides mutual exclusion: at most one thread can be executing inside the monitor at a time.

**Entry procedures:** Each entry procedure must call `Declare_Monitor_Entry_Procedure()` as its first statement. This acquires the monitor's mutex on entry and automatically releases it on return.

```c
void Declare_Monitor_Entry_Procedure(void);
```

**Condition variables:**

```c
cond_id_t CondCreate(void);
```
Creates and returns a new condition variable.

```c
void CondWait(cond_id_t cv);
```
Releases the monitor mutex, blocks the calling thread on `cv`, and re-acquires the mutex when woken. **Must be called from within an entry procedure.**

```c
void CondSignal(cond_id_t cv);
```
Wakes one thread waiting on `cv` (if any). The woken thread will compete for the monitor mutex. **Mesa semantics:** the signaling thread continues to hold the mutex, so the woken thread does not run until the signaler releases the mutex.

```c
void CondDestroy(cond_id_t cv);
```
Destroys condition variable `cv`.

**Important:** Your monitor must have exactly the entry procedures listed in Section 6 and no others. Internal (non-entry) procedures should be declared `static`. See the Appendix for a complete example.

### 8.3 Concurrency Considerations

Key concurrency issues to consider:

- `ReceiveInterrupt` and `TransmitInterrupt` are called on separate threads by the hardware emulation. They are entry procedures of the monitor (they acquire the monitor mutex).

- `ReceiveInterrupt` **must not block** (must not call `CondWait`). It must handle the character and return immediately.

- Multiple user threads may call `WriteTerminal` and/or `ReadTerminal` concurrently.

- Echo output has priority over `WriteTerminal` output.

- `TerminalDriverStatistics` requires a consistent snapshot of all terminals.

---

## 9 Building Your Project

A template Makefile is provided. Copy it and name it `Makefile`. The Makefile automatically compiles `montty.c` into `montty.o` and links test programs with `montty.o` and the necessary libraries.

Edit the `TEST =` line to list your test programs. For each test program name in the list, the Makefile builds it from the corresponding `.c` file.

Any `#include` files you write should use double quotes (`#include "foobar.h"`). Provided include files should use angle brackets (`#include <foobar.h>`). Do not copy provided include or library files into your directory.

---

## 10 Suggested Plan of Attack

- Read this complete handout. Read it again. Make sure you understand the top and bottom halves of device drivers. Think about how they interact. The top and bottom halves operate asynchronously, with the top half being called by user threads and the bottom half being called by a different thread for each type of interrupt. The granularity of the objects that the top and bottom half work on are also different (lines versus characters).

- Remember that you may *not* create any threads that are simply a part of your terminal device driver. The only threads you may create should be the users of your terminal device driver (those that call procedures such as `ReadTerminal` and `WriteTerminal` that are part of the terminal device driver API described in Section 6.2).

- Decide where buffers and other shared structures will be needed and which functions will interact with them. Show the buffers and other data structures, as well as the procedures that will access them.

- Try to identify the synchronization problems. Think about the behavior of each terminal, both input and output. Also think about the behavior of the `TerminalDriverStatistics` call, which requires a *consistent* picture of the state of *all* terminals at once.

- Think carefully about how you want to use pointers. Think about where it would be best to use an array, or a `struct`, or an array of `struct`s.

- Create an initial version of your terminal driver monitor that can successfully echo characters to the terminal, without having the `ReceiveInterrupt` handler block on the `TransmitInterrupt` handler (you will need to have at least *some* version of `ReceiveInterrupt` and `TransmitInterrupt` defined in your code in order to be able to link and run even this initial version of your device driver). Don't worry yet about any input or output character processing; for now, just echo each character "as is." *Test and debug* this version of your device driver before proceeding with the rest of the project. You should be able to type one character at a time and see it echo back onto the screen.

- Add support for `WriteTerminal` to your device driver. Make sure that the echo has priority over `WriteTerminal` output and that two or more concurrent `WriteTerminal` calls do not interfere with each other. Again, don't worry yet about any input or output character processing; for now, just output each character "as is." *Test and debug* this version of your driver before proceeding with the rest of the project.

- Add support for `ReadTerminal` into your driver. Make sure that echoing of characters to the screen still works even if no application calls `ReadTerminal`. Again, *test and debug* this version before proceeding with the rest of the project.

- Think about where best to introduce input and output character processing. Realize that the processing required for echo, output, and input, are different, but that they are not entirely dissimilar either.

- Add character processing.

- Thoroughly test your implementation using the provided test programs and the `grade` tool.

---

## Appendix: Monitor Example (Dining Philosophers)

This appendix shows how to use the threads package and write a monitor.

### File "philmain.c"

```c
#include <stdio.h>
#include <stdlib.h>

#include <threads.h>    /* COMP 421 threads package definitions */

void pickup_forks(int);
void putdown_forks(int);
void init_philosophers(void);

void
do_phil(void *arg)
{
    int i = (int)(long)arg;

    printf("do_phil %d\n", i);

    while (1) {
        pickup_forks(i);
        printf("eating %d\n", i);
        putdown_forks(i);
        printf("thinking %d\n", i);
    }
}

int
main(int argc, char **argv)
{
    int i;

    init_philosophers();

    for (i = 0; i < 5; i++) {
        ThreadCreate(do_phil, (void *)(long)i);
    }

    ThreadWaitAll();

    exit(0);
}
```

### File "philosopher.c"

```c
#include <stdio.h>

#include <threads.h>    /* COMP 421 threads package definitions */

/*
 * The state of each of the 5 philosophers: either THINKING, HUNGRY,
 * or EATING.
 *
 * This is declared 'static' so it can't be seen outside this .c file.
 */
static int state[5];
#define THINKING    0
#define HUNGRY      1
#define EATING      2

/*
 * A condition variable for each philosopher to wait on.  Declared
 * 'static' as with all variables that should only be seen inside
 * this monitor.
 */
static cond_id_t philcond[5];

#define LEFTPHIL    ((i+1) % 5)    /* the philosopher to i's left */
#define RIGHTPHIL   ((i+4) % 5)    /* the philosopher to i's right */

static void test_forks(int);

/*
 * Pick up both of the forks for philosopher i.  Waits until
 * both forks are available.
 *
 * This is an entry procedure for the monitor, so:
 *   - it is an 'extern' function (any function not defined 'static'
 *     is automatically extern, according to the C language; and
 *   - it acquires the mutual exclusion of the monitor at the top
 *     and releases it at the bottom.
 */
extern void
pickup_forks(int i)
{
    /*
     * You MUST use Declare_Monitor_Entry_Procedure() at the
     * beginning of EACH and EVERY entry procedure of your monitor.
     * You MUST NOT use Declare_Monitor_Entry_Procedure() anywhere
     * else.  This call acquires the mutual exclusion of the monitor
     * and arranges for the mutual exclusion to be automatically
     * released when this entry procedure returns.
     */
    Declare_Monitor_Entry_Procedure();

    state[i] = HUNGRY;
    test_forks(i);
    while (state[i] != EATING)
        CondWait(philcond[i]);
}

/*
 * Put down both of the forks for philosopher i.  If this allows either
 * the philosopher to our left or to our right to begin eating, let them
 * eat.
 *
 * As with pickup_forks, this is also an entry procedure of the monitor.
 */
extern void
putdown_forks(int i)
{
    /*
     * You MUST use Declare_Monitor_Entry_Procedure() at the
     * beginning of EACH and EVERY entry procedure of your monitor.
     * You MUST NOT use Declare_Monitor_Entry_Procedure() anywhere
     * else.  This call acquires the mutual exclusion of the monitor
     * and arranges for the mutual exclusion to be automatically
     * released when this entry procedure returns.
     */
    Declare_Monitor_Entry_Procedure();

    state[i] = THINKING;
    test_forks(LEFTPHIL);
    test_forks(RIGHTPHIL);
}

/*
 * Test whether philosopher i can begin eating.  If so, move him
 * to EATING state and signal him (in case he is waiting).
 *
 * This is an *internal* (non-entry) procedure of the monitor.  Thus,
 * it is a 'static' function, making this function name not known
 * outside this .c file, so it can't be called from outside the
 * monitor.  An internal procedure should be called only from a
 * monitor entry procedure (or from another internal procedure of
 * the monitor).
 */
static void
test_forks(int i)
{
    if (state[LEFTPHIL] != EATING &&
        state[i] == HUNGRY &&
        state[RIGHTPHIL] != EATING) {
        state[i] = EATING;
        CondSignal(philcond[i]);
    }
}

/*
 * Initialize the Dining Philosophers monitor.
 *
 * This procedure should be called *once* when the whole program starts
 * running.  It creates the condition variables and initializes the
 * shared variables used inside the monitor.
 */
void
init_philosophers()
{
    int i;

    for (i = 0; i < 5; i++) {
        state[i] = THINKING;
        philcond[i] = CondCreate();
    }
}
```
