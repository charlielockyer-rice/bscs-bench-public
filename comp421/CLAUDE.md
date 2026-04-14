# COMP 421: Operating Systems

Yalnix OS course with 5 assignments: Lab 1 (MonTTY Terminal Driver), Lab 2 (Yalnix Kernel), Lab 3 (File System), Midterm Exam, and Final Exam. Labs use **SSH to Rice CLEAR servers** instead of Docker — the Yalnix simulator, kernel, and linker only exist at `/clear/courses/comp421/pub/` on CLEAR. Exams are graded via LLM.

## Structure

```
comp421/
  runner.py              # SSH-based test runner (SSHRunner class)
  grading_prompt.md      # LLM grading guidance for exams
  configs/
    lab1.yaml            # Lab 1 config (assignment_number: 1, language: c)
    lab2.yaml            # Lab 2 config (assignment_number: 2, language: c)
    lab3.yaml            # Lab 3 config (assignment_number: 3, language: c)
    midterm.yaml         # Midterm config (assignment_number: 4, language: proof)
    final.yaml           # Final config (assignment_number: 5, language: proof)
  tests/
    test_lab1.py         # Lab 1 test definitions (12 tests, 100 points)
    test_lab2.py         # Lab 2 test definitions (25 tests, 100 points)
    test_lab3.py         # Lab 3 test definitions (27 tests, 174 points)
  include/comp421/       # Course headers (filesystem.h, hardware.h, iolib.h, yalnix.h)
  lab1/                  # Lab 1: MonTTY Terminal Driver
    instructions.md      # Assignment description
    lab1.pdf             # Original PDF
    skeleton_code/       # Starter files: Makefile + stub montty.c
    samples/             # Test program C sources + utils + runner Makefile
  lab2/                  # Lab 2: Yalnix Kernel
    instructions.md      # Assignment description
    lab2.md              # Original markdown (detailed)
    lab2.pdf             # Original PDF
    skeleton_code/       # Starter files: Makefile + stub yalnix.c + load.template
    samples/             # Test program C sources (28 files: 25 tests + exec targets + idle)
    include/comp421/     # Local headers (hardware.h, yalnix.h, loadinfo.h)
  lab3/                  # Lab 3: Yalnix File System
    instructions.md      # Assignment description
    lab3.md              # Original markdown (detailed)
    lab3.pdf             # Original PDF
    skeleton_code/       # Starter files: Makefile template only
    samples/             # Test program C sources (sample1.c, writeread.c, etc.)
      mkyfs.c            # Disk formatter (compiled as Unix binary, not Yalnix)
  midterm/               # Midterm Exam (theoretical, LLM-graded)
    instructions.md      # Exam questions only (no solutions)
  final/                 # Final Exam (theoretical, LLM-graded)
    instructions.md      # Exam questions only (no solutions)
  solutions/             # Rubrics + reference solutions for LLM grading (NOT copied to workspaces)
    midterm_rubric.md    # Full solutions + deductions for midterm
    final_rubric.md      # Full solutions + deductions for final
```

## Assignment Summary

| # | Name | Type | Language | Tests | Notes |
|---|------|------|----------|-------|-------|
| 1 | Lab 1: MonTTY Terminal Driver | Lab | C | 12 | SSH to CLEAR, 100 points |
| 2 | Lab 2: Yalnix Kernel | Lab | C | 25 | SSH to CLEAR, 100 points |
| 3 | Lab 3: Yalnix FS | Lab | C | 27 | SSH to CLEAR, 174 points |
| 4 | Midterm Exam | Theoretical | proof | LLM | Sample exam with solutions |
| 5 | Final Exam | Theoretical | proof | LLM | Sample exam with solutions |

## SSH Execution Model (Labs)

All compilation and execution happens on CLEAR via SSH/SCP. No Docker or local compilation.

**Why SSH?** The Yalnix OS simulator (`yalnix`), the user-space linker (`link-user-gcc`), and all kernel/hardware libraries live only on CLEAR at `/clear/courses/comp421/pub/`. These are proprietary binaries that cannot be redistributed.

**Flow per test:**
1. Bundle workspace sources + test programs into a tar.gz
2. SCP upload to `~/.bscs_bench/{hash}/` on CLEAR (home-relative, NFS-shared across nodes)
3. SSH: `make clean; make all` (compiles YFS server + iolib.a; Lab 2 compiles kernel + idle instead)
4. SSH: Compile each test program with `gcc -fcommon` + `link-user-gcc` (Lab 2 links directly without iolib.a)
5. SSH: For each test — Lab 1: `xvfb-run ./<test>`; Lab 2: `timeout 30 yalnix -lk 0 -lu 0 <test>`; Lab 3: `./mkyfs && timeout 30 yalnix yfs <test>`
6. Parse output (Lab 1: stdout; Lab 2: stdout + TRACE file + TTYLOG; Lab 3: TTYLOG files + yalnix stderr), run check function
7. SSH: Cleanup remote temp directory

**Net ID configuration** (checked in order, **required**):
1. `--netid` CLI argument
2. `CLEAR_NETID` environment variable
3. No default — runner raises `ValueError` if neither is set

**SSH requirements:** Passwordless SSH must be configured (`ssh-copy-id` or SSH keys). Runner uses `BatchMode=yes` so it will fail immediately if auth is needed.

## LLM Grading (Exams)

Midterm and final exams use LLM-based grading (same system as COMP 182/382):
- `grading_prompt.md` provides OS-specific grading guidance
- `solutions/midterm_rubric.md` and `solutions/final_rubric.md` contain full reference solutions
- One attempt only — written submissions cannot be resubmitted
- Exams have `language: proof` and `type: theoretical` in their configs

## Test Definitions (Lab 3)

Tests are defined in `tests/test_lab3.py` as a `TEST_DEFINITIONS` dict. Each entry maps a test name to:

```python
{
    "program": "sample1",           # Test binary name (from lab3/samples/)
    "points": 10,                   # Point value
    "check": check_sample1,         # Function(output) -> (bool, str)
    "needs_timeout": False,         # True if test doesn't call Shutdown()
    "description": "...",           # Human-readable description
}
```

**27 tests, 174 points total.**

## Test Definitions (Lab 1)

Tests are defined in `tests/test_lab1.py`. Lab 1 has a different execution model from Lab 3:

- **Regular executables** (no Yalnix simulator, no TTYLOG files)
- Output goes to **stdout/stderr** directly
- Check functions take `(output, exit_code)` instead of just `(output)`
- Tests need **Xvfb** for the hardware emulator's X11 display

```python
{
    "program": "test1",             # Test binary name (from lab1/samples/)
    "points": 5,                    # Point value
    "check": _check_exit_ok,        # Function(output, exit_code) -> (bool, str)
    "description": "...",           # Human-readable description
}
```

**12 tests, 100 points total:** test1/smoke (5), test2/write (10), test3/loop (10), test4/concurrent (10), test8/long (10), stats (10), invalid-init (5), invalid-stats (5), invalid-read (10), invalid-write (10), use-before-init (5), test-large-write (10).

## Test Definitions (Lab 2)

Tests are defined in `tests/test_lab2.py`. Lab 2 (Yalnix Kernel) runs under the Yalnix simulator like Lab 3, but without a filesystem:

- **No DISK/mkyfs** — Lab 2 has no filesystem layer
- **No kernel name argument** — command is `yalnix [flags] <init_program>`, not `yalnix <kernel> <init>`
- **Three output sources**: printf → yalnix stdout, TracePrintf → TRACE file (`-lk 0 -lu 0`), TtyPrintf → TTYLOG files
- Runner combines all three sources before passing to check functions
- Check functions take `(output,)` like Lab 3, not `(output, exit_code)` like Lab 1

```python
{
    "program": "init_exit",         # Test binary name (from lab2/samples/)
    "points": 4,                    # Point value
    "check": check_boot,            # Function(output) -> (bool, str)
    "needs_timeout": False,         # True if test uses Delay()
    "description": "...",           # Human-readable description
}
```

**25 tests, 100 points total.** Tests cover: Boot/Init (3), Fork (4), Wait/Exit (5), Exec (2), Memory (5), Traps (3), TtyWrite (3).

**EXTRA_PROGRAMS:** `exec_target` and `list_args` are compiled but not scored — they're Exec targets.

## Grade Dispatch

The `bin/grade` script dispatches based on the workspace's `language` field:
- Labs (`language: c`): dispatches to `comp421/runner.py`
- Exams (`language: proof`): dispatches to LLM grading via `bench-cli --with-grading`

## Mixed Course Type

comp421 is a **mixed course** — it has both C labs and theoretical exams. The `setup_workspace` script handles this via per-config override: it detects the course as `project` type (C configs), then checks the specific assignment's config. If the config has `type: theoretical` or `language: proof`, it overrides to `theoretical-hw` for that assignment only.

## Gotchas & Learnings

- **[2026-02] `-fcommon` may be needed**: If the agent declares global variables in a shared header without `extern`, GCC 10+ treats these as multiple definitions and linking fails. The instructions recommend proper `extern` usage instead. The runner passes `-fcommon` for test program compilation (runner.py) since test programs include course headers that use the old pattern.

- **[2026-02] CLEAR uses tcsh by default**: All SSH commands are wrapped in `bash -c '...'` because CLEAR's default shell is tcsh, which has incompatible syntax.

- **[2026-02] TTYLOG.0 vs TTYLOG.1 (Lab 3)**: Yalnix redirects each process's stdout/stderr to separate TTYLOG files. In Lab 3, TTYLOG.0 is the YFS server, TTYLOG.1 is the first user process (the test program). The runner extracts TTYLOG.1 for test validation. If TTYLOG.1 is empty, it falls back to combining all TTYLOG files. (Lab 2 uses TTYLOG.0 for test output — see below.)

- **[2026-02] Tests that don't call Shutdown()**: `tcreate` does NOT call `Shutdown()` and will hang until the Yalnix timeout kills it. These tests have `needs_timeout: True` which adds extra timeout buffer.

- **[2026-02] mkyfs is a Unix binary, not Yalnix**: `mkyfs` creates a fresh DISK file. It's compiled with regular `gcc`, not `link-user-gcc`. Each test gets a fresh DISK.


- **[2026-02] Lab 1 needs Xvfb**: The terminal hardware emulator opens an X11 window. On headless CLEAR, the runner uses `xvfb-run -a` which auto-allocates a virtual display per test.

- **[2026-02] Lab 1 assert-based tests**: Most Lab 1 error-handling tests use `assert()`. They pass if exit code is 0 (or 124 for timeout-killed sleeps). A non-zero exit code means an assertion fired.

- **[2026-02] Lab 1 compilation uses Makefile from samples/**: Unlike Lab 3 (which compiles each test individually), Lab 1 uploads the runner's Makefile and runs `make all` which builds montty.o + all test programs.

- **[2026-02] Lab 2 output sources**: In Lab 2, test output comes from three sources: (1) printf goes to yalnix stdout, (2) TracePrintf goes to the TRACE file (enabled with `-lk 0 -lu 0`), (3) TtyPrintf goes to TTYLOG files. The runner combines all three. TTYLOG files are often empty since most tests use printf/TracePrintf.

- **[2026-02] Lab 2 command format**: The yalnix simulator command is `yalnix [flags] <init_program>` — NO kernel binary name argument. The kernel is always loaded from `./yalnix` in the current directory. Lab 3 uses `yalnix yfs <test>` because `yfs` IS the init program (the FS server).

## Decisions

- **SSH over Docker**: Docker is not an option because the Yalnix toolchain is proprietary and lives only on CLEAR.

- **Mixed course type**: comp421 uses per-config override in `setup_workspace` to have both C labs and theoretical exams in the same course. The config's `type` and `language` fields determine workspace behavior.

- **Assignment numbering**: Lab 1=1, Lab 2=2, Lab 3=3, Midterm=4, Final=5.

- **Check functions over pytest**: Tests use hand-written check functions because TTYLOG output is non-deterministic and needs pattern-based validation.

- **Fresh DISK per test**: Each test formats a new filesystem to ensure isolation.
