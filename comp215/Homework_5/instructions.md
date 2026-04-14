# COMP 215 Homework 5: Tester Framework

## Overview

Implement the testing framework that runs Python programs against test cases and collects results.

## Files to Implement

All files are in `skeleton_code/src/main/rice/test/`:

- `TestCase.java` - Represents a single test case with arguments
- `TestResults.java` - Stores results of running tests on implementations
- `Tester.java` - Executes tests and collects results

## Requirements

### TestCase

Represents a test case with a list of arguments:

- `getArgs()` - Return the list of PyObj arguments
- `toString()` - Return string representation of arguments
- `equals(Object)` / `hashCode()` - Standard equality and hashing

### TestResults

Stores mapping from test cases to failing implementations:

- `getTestCase(int index)` - Get test case by index (throws on invalid index)
- `getWrongSet()` - Get set of test cases that caught bugs
- `getCaseToFiles()` - Get mapping from test cases to failing files

### Tester

The main testing engine:

- `getExpectedResults(List<TestCase> tests)` - Run tests against solution to get expected outputs
- `runTests(List<TestCase> tests)` - Run tests against all implementations and collect failures

**Key Implementation Details:**
- Must handle Python subprocess execution
- Must not modify solution file during testing
- Must skip non-.py files in implementation directory
- Must handle malformed/crashing implementations gracefully

## Testing

Tests cover:
- TestCase equality and string representation
- TestResults boundary conditions
- Tester with single and multiple files
- Tester with passing, failing, and mixed results
- Handling of malformed Python files

## Grading

Total: 68 points (majority in Tester tests)

---
*COMP 215: Program Design, Rice University*