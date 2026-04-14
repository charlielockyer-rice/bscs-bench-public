# COMP 215 Homework 6: Config File Parser

## Overview

Implement a JSON configuration file parser that creates PyNodes and integrates all previous components into a complete testing system.

## Files to Implement

### Parser (`skeleton_code/src/main/rice/parse/`):

- `ConfigFile.java` - Data class for parsed configuration
- `ConfigFileParser.java` - Parses JSON config into ConfigFile
- `InvalidConfigException.java` - Exception for invalid configurations

### Main (`skeleton_code/src/main/rice/`):

- `Main.java` - Entry point that ties everything together

## Requirements

### ConfigFile

Simple data class containing:
- `getFuncName()` - Function name being tested
- `getNumRand()` - Number of random tests to generate
- `getNodes()` - List of PyNodes for arguments

### ConfigFileParser

Parses JSON configuration files with format:
```json
{
  "fname": "function_name",
  "types": ["int", "list (bool"],
  "exhaustive domain": ["1~10", "0~2 (0~1"],
  "random domain": ["1~100", "0~5 (0~1"],
  "num random": 10
}
```

**Validation requirements:**
- All required fields must be present
- Types must be valid Python types
- Domain specifications must match types
- Ranges must be valid (lower <= upper)
- No spurious parentheses or spaces

**Type syntax:**
- Primitives: `bool`, `int`, `float`
- Strings: `str`
- Collections: `list(T)`, `set(T)`, `tuple(T)`, `dict(K:V)`
- Nested types allowed

### Main

Combines all components:
1. Parse config file
2. Generate test cases using BaseSetGenerator
3. Run tests using Tester
4. Select minimal test set using ConciseSetGenerator
5. Output results

## Testing

Tests cover extensive validation:
- Missing/invalid fields
- Malformed type specifications
- Invalid domain ranges
- Complete integration tests

## Grading

Total: 85 points

---
*COMP 215: Program Design, Rice University*