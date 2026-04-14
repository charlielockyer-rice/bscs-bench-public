---
total_points: 5
---

# Project 4: Unix Shell - Written Questions Rubric

## Section: Testing Strategy (5 points)

**Question:** "Replace with a detailed description of your testing strategy."

**Expected Answer:**
A good testing strategy for the Unix shell (tsh) should cover:

1. **Basic command execution:**
   - Simple foreground commands (e.g., `/bin/ls`, `/bin/echo hello`)
   - Commands with arguments
   - Commands with paths (absolute and relative)
   - Commands not found (error handling)

2. **Built-in commands:**
   - `quit` - shell exit
   - `jobs` - job listing
   - `bg <job>` - resume job in background
   - `fg <job>` - resume job in foreground
   - Job specification formats: `%1`, `%2` (job ID), PID

3. **Job control:**
   - Background jobs (`command &`)
   - Multiple concurrent background jobs
   - Job state transitions (Running, Stopped)
   - Job listing format and content

4. **Signal handling:**
   - Ctrl-C (SIGINT) to foreground process only
   - Ctrl-Z (SIGTSTP) to stop foreground process
   - Verify signals don't affect shell itself
   - Verify signals don't affect background jobs

5. **Process lifecycle:**
   - Child process reaping (no zombies)
   - Proper waitpid usage
   - Race condition testing (rapid job creation/termination)

6. **Edge cases:**
   - Empty command line
   - Command line with only spaces
   - Very long command lines
   - Many background jobs simultaneously
   - Rapid Ctrl-C/Ctrl-Z sequences

7. **Error conditions:**
   - Invalid job ID for bg/fg
   - No current foreground job
   - Command execution failure
   - Fork failure handling (if testable)

8. **Comparison testing:**
   - Compare output with reference shell (tshref)
   - Use trace files provided
   - Verify output format matches exactly

**Rubric:**
- Full credit (5 pts): Comprehensive strategy covering:
  - Basic commands and built-ins (1 pt)
  - Job control with background jobs (1 pt)
  - Signal handling (SIGINT, SIGTSTP, SIGCHLD) (1 pt)
  - Edge cases and error conditions (1 pt)
  - Verification approach (comparison with tshref or trace files) (1 pt)
- Partial credit (4 pts): Covers 4 of the 5 areas above with specifics.
- Partial credit (3 pts): Covers basic functionality and signals but lacks edge cases or verification approach.
- Partial credit (2 pts): Mentions main areas but lacks specific test cases or systematic approach.
- Partial credit (1 pt): Very brief or vague (e.g., "tested the trace files").
- No credit (0 pts): Missing or completely inadequate.

**Note:** Testing a shell is challenging because of timing-dependent behavior with signals. The testing strategy should acknowledge this and describe how to handle non-determinism (e.g., using trace files, specific timing, or multiple runs).
