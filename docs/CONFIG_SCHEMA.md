# Standardized Course Config Schema

This document defines the unified config schema for all courses. The `framework/config_compat.py` module provides backwards compatibility for legacy field names, so existing configs continue to work while new courses use the unified schema.

## Schema Overview

```yaml
# =============================================================================
# IDENTITY (Required)
# =============================================================================
assignment_number: 1              # Unified field (replaces module_number, homework_number, project_number)
assignment_name: "circles"        # Machine-readable identifier (lowercase, underscores)
display_name: "Circles"           # Human-readable name for UI

# =============================================================================
# LANGUAGE & RUNTIME (Required)
# =============================================================================
language: python                  # python | java | c | go | typescript | racket
runtime:
  version: "3.11"                 # Language version
  docker_image: "python:3.11-slim"  # Optional: override default Docker image

# =============================================================================
# GRADING (Required)
# =============================================================================
grading:
  total_tests: 25                 # Expected number of tests
  total_points: 100               # Maximum points
  timeout_seconds: 10             # Per-test timeout
  max_attempts: 10                # Optional, defaults to 10

# =============================================================================
# SOURCE FILES (Required)
# =============================================================================
source_files:                     # Files the student must implement
  - path: "solution.py"           # Python
  # - path: "src/main/java/Solution.java"  # Java
  # - path: "mm.c"                 # C

# =============================================================================
# TESTS (Language-specific)
# =============================================================================

# Python: test discovery from tests/ directory
tests:
  discovery: "auto"               # auto | manual
  pattern: "test_module1.py"      # Optional: specific test file

# Java: JUnit test classes
tests:
  classes:
    - "edu.rice.comp322.SolutionTest"
    - "edu.rice.comp322.PerformanceTest"

# C: executable-based testing
tests:
  executable: "factors"           # Compiled binary name
  runner: "pytest"                # pytest | custom

# =============================================================================
# BUILD (Language-specific, Optional)
# =============================================================================

# Java with Maven
build:
  tool: "maven"
  pom: "pom.xml"

# Java with HJlib
build:
  tool: "maven"
  javaagent: "lib/hjlib.jar"      # HJlib requires javaagent

# C with Make
build:
  tool: "make"
  target: "all"

# =============================================================================
# DEPENDENCIES (Optional)
# =============================================================================
dependencies:
  jars:                           # Java JARs
    - "lib/hjlib.jar"
    - "lib/json.jar"
  packages:                       # Python packages
    - "numpy"
    - "pytest"
  headers:                        # C headers
    - "lib/csapp.h"

# =============================================================================
# POINT ALLOCATION (Optional)
# =============================================================================
test_points:                      # Points per test (defaults to 1 each)
  test_basic: 1
  test_advanced: 2
  test_edge_case: 3

# =============================================================================
# DOCUMENTATION (Optional)
# =============================================================================
docs:
  instructions: "instructions.md"
  writeup_sections:               # Required writeup sections
    - "Algorithm explanation"
    - "Time complexity analysis"

# =============================================================================
# TOOLING (Optional)
# =============================================================================
tooling:
  allowed_paths:                  # Files the agent may edit/write
    - "solution.py"
    - "writeup.md"
```

## Generated Workspace Metadata

`bin/setup_workspace` writes derived metadata into each workspace's
`workspace.yaml` / `workspace.json`. In addition to the identity and language
fields copied from course config, it now stamps:

```yaml
assignment_mode: "code"          # code | hybrid | written
```

This field is intentionally runtime-facing. Prompt builders and grading flows
should prefer it over checking for `writeup.md` or other filesystem heuristics.

## Comparison: Old vs New

### Python Course (comp140)

**Old format:**
```yaml
module_number: 1
module_name: "Circles"
max_attempts: 10
timeout_per_test_seconds: 10
total_points: 20
total_tests: 25
functions:
  - distance
  - midpoint
```

**New format:**
```yaml
assignment_number: 1
assignment_name: "circles"
display_name: "Circles"
language: python
runtime:
  version: "3.11"
grading:
  total_tests: 25
  total_points: 20
  timeout_seconds: 10
  max_attempts: 10
source_files:
  - path: "solution.py"
    functions:
      - distance
      - midpoint
tests:
  discovery: "auto"
tooling:
  allowed_paths:
    - "solution.py"
    - "writeup.md"
```

### Java Course (comp215)

**Old format:**
```yaml
homework_number: 1
homework_name: "Prime Factorization"
language: java
total_points: 28
total_tests: 14
test_classes:
  - test.rice.PrimeFactorizerTest
implementation_files:
  - src/main/rice/PrimeFactorizer.java
```

**New format:**
```yaml
assignment_number: 1
assignment_name: "prime_factorization"
display_name: "Prime Factorization"
language: java
runtime:
  version: "17"
grading:
  total_tests: 14
  total_points: 28
  timeout_seconds: 10
source_files:
  - path: "src/main/rice/PrimeFactorizer.java"
tests:
  classes:
    - "test.rice.PrimeFactorizerTest"
build:
  tool: "javac"
```

### C Course (comp321)

**Old format:**
```yaml
project_name: "factors"
project_number: 1
display_name: "Prime Factors"
language: c
total_points: 60
total_tests: 48
executable: "factors"
source_files:
  - factors.c
functions:
  - count_factors
```

**New format:**
```yaml
assignment_number: 1
assignment_name: "factors"
display_name: "Prime Factors"
language: c
runtime:
  version: "gcc12"
grading:
  total_tests: 48
  total_points: 60
  timeout_seconds: 60
source_files:
  - path: "factors.c"
    functions:
      - count_factors
tests:
  executable: "factors"
build:
  tool: "make"
```

### Java/HJlib Course (comp322)

**Old format:**
```yaml
project_name: "hw1"
project_number: 1
display_name: "Functional Trees & Java Streams"
language: java
uses_hjlib: true
hjlib_jar: "lib/hjlib.jar"
test_classes:
  - "edu.rice.comp322.TreeSolutionsTest"
solution_files:
  - "src/main/java/edu/rice/comp322/solutions/TreeSolutions.java"
```

**New format:**
```yaml
assignment_number: 1
assignment_name: "hw1"
display_name: "Functional Trees & Java Streams"
language: java
runtime:
  version: "11"                   # HJlib requires Java 11
grading:
  total_tests: 13
  total_points: 100
  timeout_seconds: 120
source_files:
  - path: "src/main/java/edu/rice/comp322/solutions/TreeSolutions.java"
tests:
  classes:
    - "edu.rice.comp322.TreeSolutionsTest"
build:
  tool: "maven"
  javaagent: "lib/hjlib.jar"
dependencies:
  jars:
    - "lib/hjlib.jar"
```

## Implementation: config_compat.py

The `framework/config_compat.py` module provides the compatibility layer:

### Timeout Constants

```python
from framework.config_compat import (
    DEFAULT_TIMEOUT_PYTHON,     # 30s - interpreted execution
    DEFAULT_TIMEOUT_JAVA,       # 30s - JIT + test framework
    DEFAULT_TIMEOUT_JAVA_HJLIB, # 120s - bytecode transformation
    DEFAULT_TIMEOUT_C,          # 60s - shell/proxy need more time
)
```

### Helper Functions

```python
from framework.config_compat import (
    normalize_config,         # Add unified fields to legacy config
    get_assignment_number,    # From any legacy field name
    get_assignment_name,      # From any legacy field name
    get_display_name,         # Human-readable name
    get_timeout_seconds,      # From grading section or flat field
    get_total_tests,          # From grading section or flat field
    get_total_points,         # From grading section or flat field
    get_source_files,         # List of file paths (strings)
    get_source_file_details,  # List of dicts with path + metadata
    get_test_classes,         # Java test class names
)
```

### Field Mappings

| Old Field | New Field |
|-----------|-----------|
| `module_number` | `assignment_number` |
| `homework_number` | `assignment_number` |
| `project_number` | `assignment_number` |
| `module_name` | `assignment_name` |
| `homework_name` | `assignment_name` |
| `project_name` | `assignment_name` |
| `timeout_per_test_seconds` | `grading.timeout_seconds` |
| `implementation_files` | `source_files` |
| `solution_files` | `source_files` |

## Migration Status

- **Phase 1 (Complete):** `config_compat.py` supports both old and new formats
- **Phase 2 (Complete):** All runners use helper functions for field access
- **Phase 3 (In Progress):** Existing configs work as-is, new courses use unified schema
- **Phase 4 (Future):** Remove legacy support after all configs migrated

## Benefits of Unified Schema

1. **Consistency** - Same field names across all courses
2. **Discoverability** - Easier to understand what's required
3. **Tooling** - Can build validators, generators, UI forms
4. **Documentation** - One schema to document instead of four
5. **Extensibility** - Clear structure for adding new languages/features

## Default Docker Images

| Language | Version | Default Image |
|----------|---------|---------------|
| python | 3.11 | `python:3.11-slim` |
| python | 3.10 | `python:3.10-slim` |
| java | 17 | `eclipse-temurin:17-jdk` |
| java | 11 | `eclipse-temurin:11-jdk` |
| java | 8 | `eclipse-temurin:8-jdk` |
| c | gcc12 | `gcc:12` |
| c | gcc11 | `gcc:11` |
| go | 1.23 | `golang:1.23` |
| racket | 8.x | `racket/racket:8.11` |

## Validation

A config is valid if:
- [ ] `assignment_number` is a positive integer
- [ ] `assignment_name` matches `[a-z0-9_]+`
- [ ] `display_name` is non-empty string
- [ ] `language` is one of: python, java, c, go, typescript, racket
- [ ] `grading.total_tests` is a positive integer
- [ ] `grading.total_points` is a positive number
- [ ] `source_files` has at least one entry
- [ ] For Java: `tests.classes` is non-empty
- [ ] For C: `tests.executable` is defined
