# COMP 215 Homework 2: Python Object System

## Overview

Implement Java classes that represent Python objects. This forms the foundation of a testing framework for Python programs.

## Files to Implement

All files are in `skeleton_code/src/main/rice/obj/`:

- `APyObj.java` - Abstract base class for all Python objects
- `PyBoolObj.java` - Boolean objects (True/False)
- `PyIntObj.java` - Integer objects
- `PyFloatObj.java` - Float objects
- `PyCharObj.java` - Character objects
- `AIterablePyObj.java` - Abstract base for iterable objects
- `PyListObj.java` - List objects
- `PySetObj.java` - Set objects
- `PyTupleObj.java` - Tuple objects
- `PyDictObj.java` - Dictionary objects
- `PyStringObj.java` - String objects

## Requirements

### For each PyObj class, implement:

1. **Constructor** - Initialize with the appropriate Java value
2. **getValue()** - Return the underlying Java value
3. **toString()** - Return Python-style string representation:
   - Booleans: "True" or "False"
   - Strings: surrounded by single quotes, e.g., `'hello'`
   - Lists: `[elem1, elem2]`
   - Tuples: `(elem1, elem2)`
   - Sets: `{elem1, elem2}`
   - Dicts: `{key1: val1, key2: val2}`
4. **equals(Object)** - Check equality with another object
5. **hashCode()** - Return consistent hash code

### Key Design Points

- Two `APyObj` instances are equal if they have the same type AND equal values
- Collections must properly handle nested objects
- String representation must match Python's format exactly

## Testing

Run the grade tool to execute JUnit tests covering:
- All primitive types (bool, int, float, char)
- All iterable types (list, set, tuple, dict, string)
- Mixed/nested structures

## Grading

Total: 59 points across multiple test classes

---
*COMP 215: Program Design, Rice University*