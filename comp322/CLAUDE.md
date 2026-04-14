# COMP 322: Fundamentals of Parallel Programming

## Course Overview

Java-based parallel programming course using HJlib (Habanero-Java library). Covers async/finish, futures, phasers, locks, and concurrent data structures.

**All tests run in Docker** - no local Java or Maven installation required.

## Directory Structure

```
comp322/
├── CLAUDE.md                    # This file - read this first!
├── Dockerfile                   # Docker image (Java 11 + Maven + HJlib)
├── docker-compose.yml           # Docker compose config
├── .dockerignore                # Excludes hw dirs from Docker build context
├── runner.py                    # Test runner (Docker-only)
├── lib/
│   └── hjlib.jar                # Shared HJlib JAR for all homeworks
├── configs/                     # Homework configurations (YAML)
│   ├── hw1.yaml
│   ├── hw2.yaml
│   ├── hw3.yaml
│   └── hw4.yaml
├── hw1-charlielockyer-rice/     # HW1: Functional Trees & Java Streams
├── hw2-charlielockyer-rice/     # HW2: GitHub Contributions (Parallel)
├── hw3-charlielockyer-rice/     # HW3: Pairwise Sequence Alignment
└── hw4-charlielockyer-rice/     # HW4: Minimum Spanning Tree (Boruvka)
```

## Quick Start

```bash
# Run tests (Docker image builds automatically on first run)
python3 comp322/runner.py hw1 comp322/hw1-charlielockyer-rice

# Verbose output
python3 comp322/runner.py hw1 comp322/hw1-charlielockyer-rice --verbose

# JSON output (for programmatic use)
python3 comp322/runner.py hw1 comp322/hw1-charlielockyer-rice --json

# Force rebuild Docker image
python3 comp322/runner.py hw1 comp322/hw1-charlielockyer-rice --rebuild
```

## Homework Summary

| HW | Topic | Key Concepts | Test Classes |
|----|-------|--------------|--------------|
| 1 | Trees & Streams | Functional programming, Java Streams, fold/map/filter | TreeSolutionsTest, StreamSolutionsTest |
| 2 | GitHub Contributions | Futures, data-driven tasks, concurrent UI | ContributorsTest (requires GitHub token) |
| 3 | Sequence Alignment | Smith-Waterman, phasers, abstract metrics | Checkpoint1-3, PerformanceTest |
| 4 | MST (Boruvka) | Locks, atomic variables, concurrent collections | BoruvkaCorrectnessTest, BoruvkaPerformanceTest |

## Docker Architecture

The runner uses Docker with the following security constraints:

- **Memory limit**: 10GB (HW3 PerformanceTest requires -Xmx8192m)
- **CPU limit**: 2 cores
- **Network**: Disabled (except HW2 which needs GitHub API access)
- **Maven cache**: Persisted in `comp322-maven-cache` Docker volume

### HJlib Setup

HJlib (Habanero-Java library) is pre-installed in the Docker image:
- JAR location: `comp322/lib/hjlib.jar`
- Installed to Maven repo during Docker build
- No manual setup required

## Running Tests

### Using runner.py (Recommended)

```bash
# Basic usage
python3 comp322/runner.py <hw> <workspace_path> [options]

# Options:
#   --verbose    Show detailed Maven output
#   --json       Output results as JSON
#   --rebuild    Force rebuild Docker image
#   --course     Path to comp322 directory (auto-detected)
```

### Using Docker Compose

```bash
cd comp322
WORKSPACE=./hw1-charlielockyer-rice docker compose run --rm comp322 mvn test
```

## Special Notes by Homework

### HW1 (Functional Trees & Java Streams)
- Implements functional tree operations: map, filter, fold
- Also implements Java Stream operations
- Key file: `Tree.java` contains the `fold` method to implement in `Node` class

### HW2 (GitHub Contributions)
- **Mock mode (default)**: Uses pre-recorded GitHub API responses - no credentials needed
  ```bash
  MOCK_GITHUB=true python3 comp322/runner.py hw2 comp322/hw2-charlielockyer-rice
  ```
- **Live API mode**: Requires GitHub credentials for real API access
  ```bash
  export GITHUB_USERNAME=your-username
  export GITHUB_TOKEN=your-token
  python3 comp322/runner.py hw2 comp322/hw2-charlielockyer-rice
  ```
- The runner passes MOCK_GITHUB env var and credentials to Docker container
- Network access is enabled for this homework (needed for live API mode)
- Mock data covers 7 organizations: edgecase, trabian, collectiveidea, galaxycats, revelation, moneyspyder, notch8

### HW3 (Sequence Alignment)
- Checkpoint 1: Abstract metrics only (no phasers)
- Checkpoint 2: Real performance on NOTS cluster
- Final: Sparse memory version for O(10^5) strings

### HW4 (Boruvka MST)
- Performance tested with USA road network graphs
- Scalability from 1 to 16 threads

## Solution Files to Implement

### HW1
- `src/main/java/edu/rice/comp322/solutions/TreeSolutions.java`
- `src/main/java/edu/rice/comp322/solutions/StreamSolutions.java`
- `src/main/java/edu/rice/comp322/provided/trees/Tree.java` (fold method in Node class)

### HW2
- `src/main/java/edu/rice/comp322/ContributorsUI.java` (implement `loadContributorsPar`)

### HW3
- `src/main/java/edu/rice/comp322/IdealParScoring.java`
- `src/main/java/edu/rice/comp322/UsefulParScoring.java`
- `src/main/java/edu/rice/comp322/SparseParScoring.java`

### HW4
- `src/main/java/edu/rice/comp322/boruvka/parallel/ParBoruvka.java`
- `src/main/java/edu/rice/comp322/boruvka/parallel/ParComponent.java`
- `src/main/java/edu/rice/comp322/boruvka/parallel/ParEdge.java`

## Workspace Integration

COMP 322 is integrated with the bscs-bench workspace system:

```bash
# Create all COMP 322 workspaces
bin/setup_course ./comp322 agent

# Create single workspace
bin/setup_workspace ./comp322 agent 1

# Creates: workspaces/agent_hw1/, agent_hw2/, etc.
```

Each workspace contains:
- Full Maven project structure
- `workspace.yaml` with metadata (assignment_number, assignment_name, course, language)
- All source and test files

## Config File Format

Each homework has a YAML config in `configs/`. Both legacy and unified schemas are supported via `config_compat.py`:

```yaml
# Unified schema (preferred)
assignment_number: 1
assignment_name: "hw1"
display_name: "Functional Trees & Java Streams"
language: java
grading:
  timeout_seconds: 120        # Or omit to use DEFAULT_TIMEOUT_JAVA_HJLIB (120s)
  total_points: 100
source_dir: "hw1-charlielockyer-rice"
build:
  tool: "maven"
  javaagent: "lib/hjlib.jar"
tests:
  classes:
    - "edu.rice.comp322.trees.TreeSolutionsTest"
    - "edu.rice.comp322.streams.StreamSolutionsTest"
source_files:
  - path: "src/main/java/edu/rice/comp322/solutions/TreeSolutions.java"
requires_credentials: false  # true for hw2
```

Legacy fields (`project_name`, `project_number`, `timeout_per_test_seconds`, `test_classes`, `solution_files`) are automatically mapped to the unified schema.

## Troubleshooting

### Docker image won't build
```bash
# Force rebuild
python3 comp322/runner.py hw1 <workspace> --rebuild

# Or manually
docker build -t comp322-runner -f comp322/Dockerfile comp322/
```

### Tests timeout
- Default timeout: 120 seconds per test (from `DEFAULT_TIMEOUT_JAVA_HJLIB` in `framework/config_compat.py`) + compilation buffer
- Increase `grading.timeout_seconds` in config if needed (or legacy `timeout_per_test_seconds`)

### HW2 fails with authentication error
- Ensure `GITHUB_USERNAME` and `GITHUB_TOKEN` are set
- Token needs `repo` scope for API access

### Maven cache issues
```bash
# Clear the Maven cache volume
docker volume rm comp322-maven-cache
```

## Agent Integration Notes

When an agent works on COMP 322 workspaces:

1. **Testing:** Always use the `grade` tool, never run `mvn` directly
   - Docker handles HJlib javaagent configuration
   - Tests parse JUnit XML output automatically

2. **File editing:** Solution files are at:
   - `src/main/java/edu/rice/comp322/.../`
   - Look for TODO comments or method stubs

3. **Key HJlib imports:**
   ```java
   import static edu.rice.hj.Module1.*;  // async, finish, forasync
   import edu.rice.hj.api.*;              // HjFuture, HjPhaser
   ```

4. **Common patterns:**
   - Parallel divide-and-conquer with async/finish
   - Parallel loops with forasync
   - Point-to-point sync with phasers

## Gotchas and Known Issues

- **[2026-03] bench-cli sandboxing is opt-in**: `bin/bench-cli` runs on the host by default. Use `--sandbox` if you want the agent inside Docker Sandbox. Direct `runner.py` calls still work unchanged.

### HW2 Mock Mode and Credentials

HW2 supports two modes for GitHub API access:

**Mock mode (default)**:
- Uses pre-recorded API responses from `src/main/resources/mock-responses/`
- No GitHub credentials required
- Network disabled (no real API calls)
- 7 organizations with recorded data (204 contributor files total)

**Live API mode** - For testing with real data:
- Requires `GITHUB_USERNAME` and `GITHUB_TOKEN` environment variables
- Runner validates credentials before launching Docker container
- Set `MOCK_GITHUB=false` to enable live API mode

```python
# runner.py credential handling:
if mock_github:
    # No credentials needed - use mock responses
elif not github_user or not github_token:
    return False, "", "HW2 requires GITHUB_USERNAME and GITHUB_TOKEN (set MOCK_GITHUB=false to use live API)"
```

### docker-compose.yml vs runner.py Security

Both `docker-compose.yml` and `runner.py` define Docker security constraints, but they serve different purposes:

| Constraint | runner.py | docker-compose.yml | Notes |
|------------|-----------|-------------------|-------|
| Memory limit | `--memory 10g` | `mem_limit: 10g` | Must match |
| CPU limit | `--cpus 2` | `cpus: 2` | Must match |
| Network | `--network none` (except HW2) | Not enforced | docker-compose always has network |

**Key differences:**
- **runner.py**: Used for automated testing (grade tool, benchmarks). Enforces all security constraints including network isolation.
- **docker-compose.yml**: Used for interactive development. Does not disable network (convenient for debugging). Security constraints are set for consistency but network is not restricted.

When modifying security settings, update both files to keep them synchronized.

### Dockerfile Error Visibility

The Dockerfile uses `|| true` to allow Maven dependency caching to complete even if some tests fail during the initial build. This is intentional:

```dockerfile
RUN cd /tmp/hw1 && \
    mvn clean test -q || true && \
    mvn dependency:go-offline -q || true && \
    rm -rf /tmp/hw1
```

Error output is **not suppressed** (no `2>/dev/null`) so build issues remain visible for debugging.

### Test Timeout & Deadlock Detection

The `timeout_seconds` config value is the total test budget for the assignment. Maven's `surefire.timeout` is set to this value, which kills the forked JVM if tests exceed the budget (catching deadlocks/infinite loops). The Docker container timeout is `test_timeout + 120s` (compilation buffer).

### HW2 Parallel Implementation Challenge

HW2 requires implementing `loadContributorsPar` using HJlib futures. This is a challenging assignment that requires understanding HJlib's parallel constructs.

**Key requirements from the assignment:**
- Use futures, data-driven tasks, and streams
- Do NOT use finish or async directly
- Must not freeze the UI when loading data
- Work metric should be bounded by O(repos)

**HJlib Lab 1 Reference:**
Each COMP 322 workspace includes `hjlib-lab1-reference.pdf` which documents the basic HJlib APIs:
- `launchHabaneroApp()` - launches code in Habanero runtime (implicit finish)
- `async()` - spawns asynchronous task
- `finish()` - waits for all spawned asyncs
- `future()` - creates a future that returns a value

**Implementation pattern for futures:**
```java
import static edu.rice.hj.Module1.future;
import edu.rice.hj.api.HjFuture;
import edu.rice.hj.api.SuspendableException;

// Create futures for parallel work
List<HjFuture<ResultType>> futures = new ArrayList<>();
for (Item item : items) {
    HjFuture<ResultType> f = future(() -> {
        // Compute and return result
        return computeResult(item);
    });
    futures.add(f);
}

// Collect results (blocks until each future completes)
for (HjFuture<ResultType> f : futures) {
    try {
        ResultType result = f.get();
        // Process result
    } catch (SuspendableException e) {
        // Handle error
    }
}
```

**Known complexity:**
The tests verify both correctness AND parallelism metrics. The implementation must:
1. Return the same results as the sequential version
2. Have `work <= repos + 1` (bounded parallel overhead)
3. Handle the HJlib runtime context properly (tests run inside `launchHabaneroApp`)

This assignment is genuinely difficult and may require iterative debugging to get the HJlib patterns correct.
