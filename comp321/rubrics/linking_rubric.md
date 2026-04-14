---
total_points: 25
---

# Project 3: Linking - Written Questions Rubric

## Section: Problem 7.6 (5 points)

**Question:** "Replace with the answer to problem 7.6."

**Problem Context (CS:APP 3rd Edition, Problem 7.6):**
This problem asks about symbol resolution with multiple object files containing symbols with the same name. Given modules that define symbols, determine which symbol definitions are used and what values result.

**Expected Answer:**
The answer depends on the specific version of Problem 7.6 being used, but the key concepts that must be demonstrated:

1. **Strong vs. Weak Symbols:**
   - Strong symbols: Functions and initialized global variables
   - Weak symbols: Uninitialized global variables

2. **Symbol Resolution Rules:**
   - Rule 1: Multiple strong symbols with same name are not allowed (linker error)
   - Rule 2: Given a strong symbol and weak symbols, choose the strong symbol
   - Rule 3: Given multiple weak symbols, choose any one (typically the first encountered)

3. **Type Mismatch Issues:**
   - When strong/weak symbols have different types, the strong symbol's type is used
   - This can cause subtle bugs when weak symbol's allocation differs from strong symbol's type

**Example answer structure:**
- Identify which symbols are strong vs. weak in each module
- Apply resolution rules to determine which definition is used
- Calculate resulting values based on chosen definitions
- Note any potential issues (e.g., type mismatches)

**Rubric:**
- Full credit (5 pts): Correctly identifies strong/weak symbols, applies resolution rules correctly, and arrives at correct answer with clear reasoning.
- Partial credit (4 pts): Correct answer with minor errors in explanation or incomplete reasoning.
- Partial credit (3 pts): Understands strong/weak distinction but makes errors in applying rules.
- Partial credit (2 pts): Shows understanding of symbol resolution but significant errors.
- Partial credit (1 pt): Attempts the problem with some relevant content.
- No credit (0 pts): Missing, wrong, or demonstrates no understanding of symbol resolution.

---

## Section: Problem 7.9 (5 points)

**Question:** "Replace with the answer to problem 7.9."

**Problem Context (CS:APP 3rd Edition, Problem 7.9):**
This problem asks about relocation - the process of adjusting addresses in code and data after the linker assigns final memory addresses to sections.

**Expected Answer:**
Key concepts that must be demonstrated:

1. **Relocation Entries:**
   - R_X86_64_PC32: PC-relative reference (32-bit)
   - R_X86_64_32: Absolute reference (32-bit)
   - R_X86_64_PLT32: Procedure Linkage Table reference

2. **Relocation Calculation:**
   - For PC-relative: `*refptr = ADDR(symbol) - ADDR(refptr)`
   - For absolute: `*refptr = ADDR(symbol)`

3. **Address Calculation:**
   - refaddr = ADDR(section) + offset
   - Final address = base address + section offset + relocation addend

**Example answer structure:**
- Show the relocation entry being processed
- Identify the type of relocation
- Calculate the runtime address of the referenced symbol
- Calculate the address of the reference location
- Apply the appropriate formula to compute the relocated value
- Express the result in hexadecimal

**Rubric:**
- Full credit (5 pts): Correctly performs relocation calculation with proper formula and arrives at correct hexadecimal result.
- Partial credit (4 pts): Correct approach with minor arithmetic error.
- Partial credit (3 pts): Understands relocation concept but uses wrong formula or makes significant calculation errors.
- Partial credit (2 pts): Shows understanding of address calculation but fundamental errors in relocation.
- Partial credit (1 pt): Attempts the problem with some relevant content.
- No credit (0 pts): Missing, wrong, or demonstrates no understanding of relocation.

---

## Section: Problem 7.12 (5 points)

**Question:** "Replace with the answer to problem 7.12."

**Problem Context (CS:APP 3rd Edition, Problem 7.12):**
This problem asks about library linking order and how the order of libraries on the command line affects which symbols are resolved.

**Expected Answer:**
Key concepts that must be demonstrated:

1. **Library Scanning Algorithm:**
   - The linker scans files left to right
   - Maintains: E (relocatable object files), U (unresolved symbols), D (defined symbols)
   - For each input file:
     - If object file: add to E, update U and D
     - If archive: scan members to resolve symbols in U

2. **Archive Member Selection:**
   - Only archive members that resolve undefined symbols are included
   - Members are scanned in order, left to right
   - Once an archive is passed, its members are not reconsidered

3. **Command Line Order Matters:**
   - Libraries should come after object files that reference them
   - If library A depends on library B, A should come before B
   - Circular dependencies may require repeating libraries

**Example answer structure:**
- Trace through the linking process showing U and D at each step
- Show which archive members are selected and why
- Explain why certain orderings work or fail
- Identify if any symbols remain unresolved

**Rubric:**
- Full credit (5 pts): Correctly traces the linking algorithm, explains library scanning order, and arrives at correct answer.
- Partial credit (4 pts): Correct answer with minor gaps in explanation.
- Partial credit (3 pts): Understands ordering matters but makes errors in tracing algorithm.
- Partial credit (2 pts): Shows basic understanding of library linking but significant errors.
- Partial credit (1 pt): Attempts the problem with some relevant content.
- No credit (0 pts): Missing, wrong, or demonstrates no understanding of library linking.

---

## Section: READJCF Testing Strategy (5 points)

**Question:** "Replace with a detailed description of your testing strategy."

**Expected Answer:**
A good testing strategy for the Java class file reader should cover:

1. **Valid class files:**
   - Simple classes with few methods/fields
   - Classes with various constant pool entry types
   - Classes with interfaces, superclasses
   - Classes with static and instance fields
   - Classes with different access modifiers

2. **Constant pool coverage:**
   - Utf8, Integer, Float, Long, Double entries
   - Class, String, FieldRef, MethodRef entries
   - NameAndType, InterfaceMethodRef entries
   - Test entries that reference other entries

3. **Edge cases:**
   - Minimal valid class file
   - Class with empty constant pool (just required entries)
   - Class with maximum length strings
   - Class files generated by different compilers

4. **Error cases:**
   - Invalid magic number
   - Truncated file
   - Invalid constant pool indices
   - Unsupported class file version (if applicable)

5. **Output verification:**
   - Compare against javap output
   - Verify correct hex formatting
   - Check tag names match specification

**Rubric:**
- Full credit (5 pts): Comprehensive strategy covering valid files, constant pool types, edge cases, and error cases. Mentions verification against reference implementation (javap).
- Partial credit (4 pts): Good coverage but missing one major category.
- Partial credit (3 pts): Covers basic testing but lacks depth in constant pool or error testing.
- Partial credit (2 pts): Lists general categories without specifics.
- Partial credit (1 pt): Very brief or vague.
- No credit (0 pts): Missing or inadequate.

---

## Section: Two Things I Learned (5 points)

**Question:** "Replace with a description of two features of C that are used in the provided code for this assignment but weren't taught in lecture or lab that you learned about while completing this assignment."

**Expected Answer:**
Students should describe two specific C features from the provided code. Acceptable answers include (but are not limited to):

1. **Binary file I/O:**
   - fread/fwrite for binary data
   - Reading multi-byte values (endianness)
   - File positioning with fseek

2. **Bit manipulation:**
   - Bitwise operators for flag checking
   - Extracting fields from packed data
   - Byte order swapping (big-endian to host)

3. **Structs and memory layout:**
   - Packed structs for file format parsing
   - Structure padding and alignment
   - Union types for type punning

4. **Pointer arithmetic:**
   - Casting between pointer types
   - Pointer to different sized types
   - Array-pointer equivalence

5. **Preprocessor features:**
   - Macros for byte swapping
   - Conditional compilation
   - Header guards

6. **Standard library functions:**
   - memcpy, memset usage
   - String formatting with sprintf
   - errno and error handling

**Rubric:**
- Full credit (5 pts): Clearly identifies two distinct C features from the provided code and demonstrates understanding of each. Explanations show genuine learning.
  - 2.5 pts per feature
- Partial credit (4 pts): Two features identified but one explanation is superficial.
- Partial credit (3 pts): One well-explained feature, second is weak.
- Partial credit (2 pts): Two features mentioned but both explanations are superficial.
- Partial credit (1 pt): Only one feature mentioned or explanations very weak.
- No credit (0 pts): Missing, or describes things taught in lecture/lab, or not C features.
