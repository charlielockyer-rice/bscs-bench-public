# COMP 215 Homework 4: ConciseSetGenerator and Primitive PyNodes

## Overview

Implement the ConciseSetGenerator for selecting minimal test sets and primitive PyNode classes for booleans, integers, and floats.

## Files to Implement

### Primitive Nodes (`skeleton_code/src/main/rice/node/`):

- `APyNode.java` - Abstract base class (provided, do not modify)
- `PyBoolNode.java` - Node for generating boolean values
- `PyIntNode.java` - Node for generating integer values
- `PyFloatNode.java` - Node for generating float values

### Generator (`skeleton_code/src/main/rice/concisegen/`):

- `ConciseSetGenerator.java` - Selects minimal test set that covers all failures

## Requirements

### Primitive PyNode Classes

Similar to iterable nodes from HW3, but simpler:

- `PyBoolNode`: Domain is {true, false} or subsets
- `PyIntNode`: Domain is a range of integers
- `PyFloatNode`: Domain is a range of floats

Implement:
- `getExDomain()` / `getRanDomain()`
- `getLeftChild()` / `getRightChild()` (return null for primitives)
- `genExVals()` - Generate all values in exhaustive domain
- `genRandVal()` - Generate random value from random domain

### ConciseSetGenerator

Given test results from running tests on multiple implementations:
1. Find the minimal set of test cases that still catches all buggy implementations
2. Use a greedy set cover algorithm

Key methods:
- `setCover(TestResults results)` - Return minimal test set

**Important:** Do not mutate the input TestResults object!

## Testing

Tests verify:
- Primitive nodes generate correct values
- ConciseSetGenerator finds minimal covering sets
- No mutation of input data

## Grading

Total: 27 points

---
*COMP 215: Program Design, Rice University*