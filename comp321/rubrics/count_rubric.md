---
total_points: 10
---

# Project 2: Word Count - Written Questions Rubric

## Section: Black Box Test Cases (5 points)

**Question:** "Black Box Test Cases: 1. ..."

**Expected Answer:**
Black box testing treats the program as a black box - testing based on specifications without knowledge of internal implementation. Students should provide specific test cases that:

1. **Test each flag independently:**
   - `-c` (byte count) with files of various sizes
   - `-l` (line count) with files having different line structures
   - `-w` (word count) with various word patterns

2. **Test flag combinations:**
   - `-cl`, `-cw`, `-lw`, `-clw` (all combinations)
   - Default behavior (no flags = `-clw`)

3. **Test different input sources:**
   - Single file argument
   - Multiple file arguments (should show totals)
   - stdin (no file arguments, reading from pipe)

4. **Test output format:**
   - Correct column alignment
   - Correct order of counts (lines, words, chars)
   - Total line for multiple files

5. **Edge cases as inputs:**
   - Empty file
   - File with no newlines
   - File with only whitespace
   - Binary file (contains null bytes)

**Rubric:**
- Full credit (5 pts): Provides at least 5 specific, concrete test cases covering different flags, combinations, input sources, and edge cases. Each test case describes input and expected output.
- Partial credit (4 pts): Provides good coverage but missing one category (e.g., no stdin testing or no multiple file testing).
- Partial credit (3 pts): Provides several test cases but lacks specificity or misses multiple categories.
- Partial credit (2 pts): Lists test categories without concrete examples.
- Partial credit (1 pt): Very brief or vague.
- No credit (0 pts): Missing or completely inadequate.

---

## Section: White Box Test Cases and Edge Cases (5 points)

**Question:** "White Box Test Cases and Edge Cases: 1. ..."

**Expected Answer:**
White box testing uses knowledge of internal implementation to design tests. Students should consider:

1. **Code path coverage:**
   - Test each branch of flag parsing
   - Test file open success and failure paths
   - Test getopt parsing of various argument orders

2. **Internal state transitions:**
   - Word boundary detection (space to non-space, non-space to space)
   - Line counting (newline character handling)
   - Byte counting (including special characters)

3. **Buffer handling:**
   - Files larger than internal buffer size
   - Files that end mid-buffer
   - Files that are exact multiples of buffer size

4. **Error handling paths:**
   - Non-existent file
   - Permission denied
   - Read errors (if implemented)

5. **Memory and resource management:**
   - Proper file handle closure
   - No memory leaks with multiple files

**Edge cases specific to wc:**
- File ending without final newline
- Lines containing only whitespace
- Multiple consecutive spaces/tabs between words
- Unicode characters (if applicable)
- Very long lines (buffer boundary crossing)
- Files with only newlines (no content)

**Rubric:**
- Full credit (5 pts): Provides at least 5 specific test cases that demonstrate understanding of internal implementation. Tests code paths, error handling, and boundary conditions not obvious from specification alone.
- Partial credit (4 pts): Good white box tests but missing error path testing or buffer boundary considerations.
- Partial credit (3 pts): Some implementation-aware tests but mostly black box style.
- Partial credit (2 pts): Lists considerations without concrete test cases.
- Partial credit (1 pt): Very brief or essentially repeats black box tests.
- No credit (0 pts): Missing or doesn't demonstrate white box testing approach.
