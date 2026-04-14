# COMP 215: Program Design

Java programming course building a Python test generation framework. Uses custom `runner.py` for Java compilation and JUnit test execution.

**All tests run in Docker by default** - no local Java installation required. Use `--no-docker` for host execution.

## Structure

```
comp215/
  Homework_1/         # Prime Factorization (standalone)
  Homework_2/         # Python Object System (PyObj classes)
  Homework_3/         # PyNode Iterables + BaseSetGenerator
  Homework_4/         # ConciseSetGenerator + Primitive PyNodes
  Homework_5/         # Tester Framework (runs Python programs)
  Homework_6/         # Config File Parser + Main integration
  Dockerfile          # Docker image (Java 17 + JUnit 5)
  docker-compose.yml  # Docker compose config
  .dockerignore       # Excludes homework dirs from Docker build context
  runner.py           # Java test runner (Docker-enabled)
  configs/            # Homework YAML configs (homework1.yaml, etc.)
  tests/              # Custom test suites (override skeleton tests)
  lib/                # Shared JARs (JUnit standalone, JSON library)
```

## Quick Start

```bash
# Recommended: create a workspace (ensures custom tests are available)
bin/setup_workspace ./comp215 1
python3 comp215/runner.py 1 ./workspaces/comp215_homework1

# Verbose output
python3 comp215/runner.py 1 ./workspaces/comp215_homework1 --verbose

# JSON output (for programmatic use)
python3 comp215/runner.py 1 ./workspaces/comp215_homework1 --json

# Force rebuild Docker image
python3 comp215/runner.py 1 ./workspaces/comp215_homework1 --rebuild

# Run on host without Docker (legacy mode)
python3 comp215/runner.py 1 ./workspaces/comp215_homework1 --no-docker
```

## Docker Architecture

The runner uses Docker with the following security constraints:

- **Java version**: 17 (Eclipse Temurin)
- **Memory limit**: 2GB
- **CPU limit**: 2 cores
- **Network**: Disabled (no external dependencies needed)
- **Test timeout**: 30s per test (configurable in YAML)

### JARs in Container

JARs are installed to `/opt/junit/` and accessible via `JUNIT_CLASSPATH` environment variable:
- `junit-platform-console-standalone-1.9.0.jar` - JUnit 5 console runner
- `json-20210307.jar` - JSON parsing library (for HW6)

### Using Docker Compose

```bash
cd comp215
WORKSPACE=./Homework_1 docker compose run --rm comp215 bash
```

## Key Concepts

### Test Runner Architecture

- `runner.py` compiles Java sources then runs JUnit tests via standalone JAR
- By default runs in Docker container; use `--no-docker` for host execution
- Custom tests in `tests/homework{N}/` override skeleton tests in homework directories
- Each homework has `skeleton_code/` with `src/main/` and `src/test/` directories
- JARs in `lib/` at homework level (JUnit, API Guardian) plus course-level `lib/`

### Cumulative Assignment Design

HW2-6 build a complete Python test framework - each homework depends on previous:

1. **HW1 (Standalone)**: Prime factorization - no dependencies
2. **HW2**: PyObj classes represent Python values (`PyIntObj`, `PyListObj`, etc.)
3. **HW3**: PyNode classes generate test values + `BaseSetGenerator` creates test cases
4. **HW4**: `ConciseSetGenerator` selects minimal test sets + primitive nodes
5. **HW5**: `Tester` executes Python programs against test cases
6. **HW6**: `ConfigFileParser` parses JSON configs; `Main` integrates everything

### Package Structure

All Java classes use `main.rice.*` or `test.rice.*` packages:
- `main.rice.obj.*` - PyObj value classes
- `main.rice.node.*` - PyNode generator classes
- `main.rice.basegen.*` - BaseSetGenerator
- `main.rice.concisegen.*` - ConciseSetGenerator
- `main.rice.test.*` - TestCase, TestResults, Tester
- `main.rice.parse.*` - ConfigFile, ConfigFileParser

### YAML Config Format

```yaml
homework_number: 1
homework_name: "Prime Factorization"
language: java
timeout_per_test_seconds: 30
test_classes:
  - test.rice.PrimeFactorizerTest
test_points:
  testSmallMaxFactorize30: 2.0
  testLargeMaxFactorize65536: 2.0
implementation_files:
  - src/main/rice/PrimeFactorizer.java
```

## Homework Summary

| HW | Name | Key Classes | Tests | Points |
|----|------|-------------|-------|--------|
| 1 | Prime Factorization | PrimeFactorizer | 14 | 28 |
| 2 | Python Object System | PyBoolObj, PyIntObj, PyListObj, etc. | 88 | 59 |
| 3 | PyNode Iterables | PyListNode, PySetNode, BaseSetGenerator | 114 | 66 |
| 4 | ConciseSetGenerator | ConciseSetGenerator, PyBoolNode, PyIntNode | 39 | 27 |
| 5 | Tester Framework | TestCase, TestResults, Tester | 80 | 68 |
| 6 | Config Parser | ConfigFile, ConfigFileParser, Main | 102 | 85 |

**Total: ~437 tests across 6 homeworks**

## Gotchas & Learnings

- **Custom vs Skeleton Tests**: Runner checks `tests/homework{N}/` first; only uses skeleton tests if no custom tests exist. Custom tests are more complete.

- **Package Naming**: Java packages must match directory structure exactly (`main.rice.*` in `src/main/rice/`).

- **Test Points Config**: Not all tests have explicit point values in YAML - defaults to 1.0 per test if unspecified.

- **Build Directory**: Compilation outputs to `skeleton_code/build/` within each homework. This directory is created automatically.

- **JUnit Output Parsing**: Runner strips ANSI escape codes and parses JUnit verbose output for test names and pass/fail status.

- **HW5/HW6 Python Execution**: Tester class must spawn Python subprocesses to run test cases against implementations. Tests verify subprocess handling.

- **JSON Parsing (HW6)**: Uses `json-20210307.jar` from course lib. ConfigFileParser must handle extensive validation of type specifications.

## Decisions

- **Docker by Default**: Tests run in Docker for consistency and isolation. Use `--no-docker` for host execution when needed.

- **Separate Runner**: Java projects use custom `runner.py` rather than Python framework because compilation workflow differs significantly from Python modules.

- **Cumulative Dependencies**: Each homework skeleton includes all previous code (HW6 has obj/, node/, basegen/, concisegen/, test/, parse/) to allow standalone compilation.

- **Custom Test Override**: Custom tests in `tests/` directory completely replace skeleton tests when present, enabling more thorough grading without modifying student-facing skeleton.

## Troubleshooting

### Docker image won't build
```bash
# Force rebuild
python3 comp215/runner.py 1 <workspace> --rebuild

# Or manually
docker build -t comp215-runner -f comp215/Dockerfile comp215/
```

### Tests timeout
- Default timeout: 30 seconds per test + compilation buffer
- Increase `timeout_per_test_seconds` in config if needed

### Compilation errors inside container
- Check that `skeleton_code/src/main/` and `skeleton_code/src/test/` exist
- Verify package names match directory structure (`main.rice.*`)
- Check for missing dependencies in homework's `lib/` directory
