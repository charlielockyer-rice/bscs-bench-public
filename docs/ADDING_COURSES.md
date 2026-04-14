# Adding a New Course

This guide explains how to add a new course to the BSCS Bench evaluation framework.

## Overview

Each course requires:
1. **Course directory** with assignments and configs
2. **Runner** (`runner.py`) to execute tests
3. **Docker setup** (optional but recommended) for isolated execution
4. **Config files** describing each assignment

## Directory Structure

```
<course>/
├── CLAUDE.md                 # Course-specific docs for AI agents
├── runner.py                 # Test runner (Java/C courses)
├── Dockerfile                # Docker image definition
├── docker-compose.yml        # Docker compose config
├── docker_runner.py          # Docker test wrapper (Python only)
├── .dockerignore             # Files to exclude from Docker build
├── configs/                  # Assignment configurations
│   ├── module1.yaml          # Python: moduleN.yaml
│   ├── homework1.yaml        # Java: homeworkN.yaml
│   └── project_name.yaml     # C: named by project
├── lib/                      # Shared libraries/JARs
├── tests/                    # Test files (Python/C)
│   └── test_module1.py
└── <Assignment_N>/           # Assignment directories
    ├── instructions.md       # Assignment instructions
    ├── template.py           # Starter code template
    └── ...
```

## Config File Format

### Required Fields (All Languages)

Use the **unified schema** for new courses. Legacy field names (`module_number`, `homework_number`, `project_number`) are still supported via `config_compat.py`.

```yaml
# Unified schema (preferred for new courses)
assignment_number: 1              # Assignment identifier
assignment_name: "circles"        # Machine-readable name (lowercase, underscores)
display_name: "Circles"           # Human-readable name for UI

# Common required fields
language: python              # python | java | c
grading:
  total_points: 100
  total_tests: 25
  timeout_seconds: 30         # Per-test timeout (see defaults below)
  max_attempts: 10            # Optional, defaults to 10
```

Optional tool scoping:
```yaml
tooling:
  allowed_paths:              # Workspace-relative paths the agent may edit/write
    - "solution.py"
    - "writeup.md"
```

### Default Timeouts

Import from `framework/config_compat.py`:
- `DEFAULT_TIMEOUT_PYTHON = 30` - Python interpreted execution
- `DEFAULT_TIMEOUT_JAVA = 30` - Standard Java with JIT
- `DEFAULT_TIMEOUT_JAVA_HJLIB = 120` - Java with HJlib bytecode transformation
- `DEFAULT_TIMEOUT_C = 60` - C with process/network operations

### Language-Specific Fields

#### Python Courses
```yaml
language: python
source_files:
  - path: "solution.py"
    functions:                # Functions student must implement
      - distance
      - midpoint
tooling:
  allowed_paths:              # Files the agent may edit/write
    - "solution.py"
    - "writeup.md"
tests:
  discovery: "auto"           # Auto-discover from tests/test_module*.py
test_points:                  # Points per test (optional)
  test_distance_basic: 1
  test_midpoint_basic: 1
```

#### Java Courses
```yaml
language: java
source_files:
  - path: "src/main/java/Solution.java"
tests:
  classes:                    # JUnit test classes to run
    - "edu.rice.comp322.TestClass"
build:
  tool: "maven"
  javaagent: "lib/hjlib.jar"  # For HJlib courses only
```

#### C Courses
```yaml
language: c
source_files:
  - path: "main.c"
  - path: "helper.c"
    functions:                # Functions to implement
      - function_name
tests:
  executable: "program_name"  # Compiled executable name
build:
  tool: "make"
```

## Runner Implementation

### Python Courses

Python courses use the shared `framework/` module. No custom runner needed.
The framework handles:
- Test discovery from `tests/test_module*.py`
- Sandboxed execution with timeouts (uses `DEFAULT_TIMEOUT_PYTHON`)
- Result formatting via `shared/results.py`

For Docker support, create `docker_runner.py`:

Note: If `docker_runner.py` is present, `bin/grade` will run Python grading in Docker by default.

```python
#!/usr/bin/env python3
"""Docker wrapper for Python course tests."""

import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))
from framework.docker_utils import run_python_tests_in_docker

if __name__ == "__main__":
    # Delegates to shared Docker utilities
    run_python_tests_in_docker(sys.argv[1:])
```

### Java/C Courses

Create a `runner.py` with this structure:

```python
#!/usr/bin/env python3
"""
<COURSE> Test Runner

Compiles and runs tests for <course> assignments.
"""

import argparse
import json
import subprocess
import sys
from pathlib import Path

# Add framework to path
sys.path.insert(0, str(Path(__file__).parent.parent))
from framework.docker_utils import (
    ensure_docker_image,
    run_in_container,
)
from framework.config_compat import (
    get_assignment_number,
    get_assignment_name,
    get_display_name,
    get_timeout_seconds,
    get_test_classes,
    DEFAULT_TIMEOUT_JAVA,  # or DEFAULT_TIMEOUT_C
)
from shared.results import TestResult, GradeResult

def load_config(assignment: str, course_dir: Path) -> dict:
    """Load assignment config from YAML."""
    # Use config_compat helpers to read fields:
    # num = get_assignment_number(config)
    # name = get_assignment_name(config)
    # timeout = get_timeout_seconds(config, DEFAULT_TIMEOUT_JAVA)

def run_tests(assignment: str, workspace: Path, config: dict, verbose: bool) -> GradeResult:
    """Run tests and return results."""
    # Use shared.results.GradeResult for consistent output formatting

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("assignment", help="Assignment identifier")
    parser.add_argument("workspace", help="Path to workspace")
    parser.add_argument("-v", "--verbose", action="store_true")
    parser.add_argument("--json", action="store_true")
    parser.add_argument("--no-docker", action="store_true")

    args = parser.parse_args()
    # Load config, run tests, output results
    # result = run_tests(...)
    # print(result.format_output(verbose=args.verbose))

if __name__ == "__main__":
    sys.exit(main())
```

## Docker Setup

### Dockerfile Template

```dockerfile
# <COURSE>: <Description>
# <Language> <version> environment

FROM <base-image>

WORKDIR /workspace

# Install dependencies
RUN <install commands>

# Copy shared libraries
COPY lib/ /opt/lib/

# Set environment
ENV PATH="/opt/lib:$PATH"

CMD ["bash"]
```

### Base Images by Language

| Language | Base Image | Notes |
|----------|------------|-------|
| Python | `python:3.11-slim` | Minimal Python |
| Java 17 | `eclipse-temurin:17-jdk` | Modern Java |
| Java 11 | `eclipse-temurin:11-jdk` | HJlib compatibility |
| Java 8 | `eclipse-temurin:8-jdk` | Legacy compatibility |
| C | `gcc:12` | GCC with make |
| Go | `golang:1.23` | Go modules |

### docker-compose.yml Template

```yaml
version: '3.8'

services:
  <course>:
    build:
      context: .
      dockerfile: Dockerfile
    image: <course>-runner
    volumes:
      - ${WORKSPACE:-.}:/workspace
    working_dir: /workspace
    mem_limit: 4g
    cpus: 2
```

### .dockerignore Template

```
# Ignore all assignment directories (mounted at runtime)
*/

# Keep these
!lib/
!Dockerfile
!*.py
```

## Test File Format

### Python Tests (`tests/test_module*.py`)

```python
"""Tests for Module N: <Name>"""

import pytest
from framework.sandbox import safe_import

# Import student solution
solution = safe_import("solution")

class TestFunctionName:
    """Tests for function_name()"""

    def test_basic(self):
        """Basic test case"""
        result = solution.function_name(input)
        assert result == expected

    @pytest.mark.parametrize("input,expected", [
        (1, 1),
        (2, 4),
    ])
    def test_parametrized(self, input, expected):
        """Parametrized test cases"""
        assert solution.function_name(input) == expected
```

### Java Tests (JUnit 5)

```java
package edu.rice.course;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SolutionTest {
    @Test
    void testBasic() {
        assertEquals(expected, Solution.method(input));
    }
}
```

### C Tests (Custom or Check framework)

Tests are typically shell scripts or Python that compile and run the executable:

```python
def test_factors_basic():
    result = subprocess.run(["./factors", "12"], capture_output=True)
    assert result.stdout.strip() == "2"
```

## Workspace Structure

When `bin/setup_workspace` creates a workspace, it generates:

```
workspaces/<agent>_<assignment>/
├── workspace.yaml            # Metadata (REQUIRED)
├── workspace.json            # JSON fallback
├── instructions.md           # Assignment instructions
├── solution.py               # Python: editable solution
├── src/                      # Java: Maven structure
├── lib/ -> symlink           # Shared libraries
├── pom.xml                   # Java: Maven config
├── Makefile                  # C: build config
└── .git/                     # Git repo for reset
```

### workspace.yaml Format

```yaml
assignment_number: 1
assignment_name: "circles"
display_name: "Circles"
course: "comp140"
language: "python"            # python | java | c
assignment_mode: "hybrid"     # generated: code | hybrid | written
created_at: "2024-01-20T10:30:00"
```

`assignment_mode` is generated by `bin/setup_workspace`, not authored by hand in
course configs. It is the runtime metadata field that downstream prompt/grading
code should use when deciding whether an assignment requires only code,
code plus a writeup, or written work only.

## Integration Checklist

When adding a new course, verify:

- [ ] `configs/` has YAML for each assignment using unified schema
- [ ] Config uses `assignment_number`, `assignment_name`, `display_name`
- [ ] Config uses `grading.timeout_seconds` (or relies on language default)
- [ ] `workspace.yaml` includes `language` field
- [ ] `workspace.yaml` includes generated `assignment_mode`
- [ ] `runner.py` uses `config_compat` helpers for field access
- [ ] `runner.py` uses `shared.results.GradeResult` for output formatting
- [ ] `runner.py` handles `--verbose`, `--json`, `--no-docker` flags
- [ ] Dockerfile builds successfully
- [ ] `bin/grade <workspace>` works without PyYAML
- [ ] `bin/grade <workspace> --docker` works
- [ ] Tests parse into the expected JSON format
- [ ] Grade results display correctly in bench-cli output

## Output Format

All runners must output results in this format for the grade parser:

```
============================================================
<COURSE> <Type> N: <Name> - Results
============================================================

RESULTS SUMMARY
---------------
Passed: X/Y tests (Z%)
Points: A/B (C%)

DETAILED RESULTS
----------------
[PASS] test_name (points/max pts)
[FAIL] test_name (points/max pts)
       Input: <input>
       Expected: <expected>
       Actual: <actual>
============================================================
```

For `--json` output:

```json
{
  "project_name": "assignment",
  "display_name": "Assignment Name",
  "passed": 10,
  "total": 12,
  "points": 85.0,
  "max_points": 100.0,
  "compilation_error": null,
  "tests": [
    {
      "name": "test_name",
      "passed": true,
      "points": 1.0,
      "max_points": 1.0,
      "error": null
    }
  ]
}
```

## Existing Course Reference

| Course | Language | Docker Image | Runner | Config Style |
|--------|----------|--------------|--------|--------------|
| comp140 | Python | python:3.11-slim | framework/ | `module*.yaml` |
| comp182 | Proof + Python | python:3.11-slim (hybrid HW4/HW6) | LLM grader + framework/ | `homework*.yaml` |
| comp215 | Java 17 | eclipse-temurin:17-jdk | runner.py | `homework*.yaml` |
| comp310 | Java 17 | eclipse-temurin:17-jdk-jammy | runner.py (Docker) | `hw*.yaml` |
| comp321 | C | gcc:12 | runner.py | `<project>.yaml` |
| comp322 | Java 11 | eclipse-temurin:11-jdk | runner.py | `hw*.yaml` |
| comp341 | Python | python:3.11-slim | framework/ | `hw*.yaml` |
| comp382 | Proof | — (LLM-graded) | LLM grader | `hw*.yaml` |
| comp411 | Java 8 | eclipse-temurin:8-jdk | runner.py | `assign*.yaml` |
| comp421 | C + Proof | — (SSH to CLEAR) | runner.py (SSH) | `lab*.yaml`, `midterm.yaml`, `final.yaml` |
