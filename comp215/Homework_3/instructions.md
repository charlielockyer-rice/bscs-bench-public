# COMP 215 Homework 3: PyNode Iterables and Base Set Generator

## Overview

Implement PyNode classes for iterable types and a BaseSetGenerator that creates test cases from node specifications.

## Files to Implement

### Node Classes (`skeleton_code/src/main/rice/node/`):

- `APyNode.java` - Abstract base class for all Python nodes (provided)
- `PyIntNode.java` - Leaf node for generating int values (provided)
- `AIterablePyNode.java` - Abstract base for iterable nodes
- `PyListNode.java` - Node for generating list values
- `PySetNode.java` - Node for generating set values
- `PyTupleNode.java` - Node for generating tuple values
- `PyStringNode.java` - Node for generating string values
- `PyDictNode.java` - Node for generating dictionary values

### Generator (`skeleton_code/src/main/rice/basegen/`):

- `BaseSetGenerator.java` - Generates test cases from node specifications

## Requirements

### PyNode Classes

Each node class represents a domain of possible values and can generate:

1. **Exhaustive values** - All possible values in the domain
2. **Random values** - Random samples from the domain

Key methods to implement:
- `getExDomain()` - Get exhaustive domain specification
- `getRanDomain()` - Get random domain specification
- `getLeftChild()` / `getRightChild()` - For iterable nodes, get child nodes
- `genExVals()` - Generate all exhaustive values
- `genRandVal()` - Generate a single random value

### BaseSetGenerator

Generates test cases by combining values from multiple argument nodes:

Constructor: `BaseSetGenerator(List<APyNode<?>> nodes, int numRand)`

- `genExTests()` - Generate all exhaustive test cases (Cartesian product), returns `Set<TestCase>`
- `genRandTests(int numTests)` - Generate specified number of random test cases, returns `Set<TestCase>`
- `genBaseSet()` - Combined: all exhaustive + random tests, returns `List<TestCase>`

## Testing

Tests cover:
- Individual node types with various domain sizes
- Nested structures
- Base set generation with single and multiple arguments

## Grading

Total: 66 points

---
*COMP 215: Program Design, Rice University*