# COMP 321: Introduction to Computer Systems

C programming course covering systems programming fundamentals. Uses custom test runner (`runner.py`) with Docker-based compilation and execution.

## Structure

```
comp321/
  Dockerfile         # Docker image (GCC 13 + make + gdb + valgrind)
  docker-compose.yml # Docker compose config
  .dockerignore      # Excludes project dirs from Docker build context
  factors/           # Project 1: Prime Factorization
  count/             # Project 2: Word Count (wc clone)
  linking/           # Project 3: Java Class File Reader
  shell/             # Project 4: Unix Shell (tsh)
  malloc/            # Project 5: Dynamic Memory Allocator
  proxy/             # Project 6: Web Proxy
  runner.py          # C test runner (compiles + runs tests)
  configs/           # Project YAML configs (factors.yaml, etc.)
  tests/             # Python test modules (test_factors.py, etc.)
  lib/               # Course library (csapp.h, csapp.c)
```

## Quick Start

```bash
# Run tests (Docker image builds automatically on first run)
python3 comp321/runner.py factors ./workspaces/agent_factors

# Verbose output
python3 comp321/runner.py factors ./workspaces/agent_factors --verbose

# JSON output (for programmatic use)
python3 comp321/runner.py factors ./workspaces/agent_factors --json

# Force rebuild Docker image
python3 comp321/runner.py factors ./workspaces/agent_factors --rebuild

# Run without Docker (host GCC, for Linux systems)
python3 comp321/runner.py factors ./workspaces/agent_factors --no-docker
```

## Docker Architecture

All COMP 321 tests run in Docker containers for consistent compilation and execution:

- **Base image**: `gcc:13` (Debian Bookworm with GCC 13)
- **Installed tools**: make, gdb, valgrind
- **csapp library**: Pre-compiled at `/opt/csapp/` with CPATH set
- **Memory limit**: 4GB (malloc needs heap space)
- **CPU limit**: 2 cores
- **Network**: Disabled by default, enabled for proxy project

### Why Docker?

1. **Consistent environment**: Same GCC version and flags across all systems
2. **Cross-platform**: Linux ELF binaries run on macOS/Windows via Docker
3. **Security**: Network isolated, memory limited
4. **csapp library**: Pre-installed and configured

### Image Details

```dockerfile
FROM gcc:13
# Install: make, gdb, valgrind
# csapp.h + csapp.c copied to /opt/csapp/
# csapp.o pre-compiled for faster builds
ENV CPATH=/opt/csapp
ENV LIBRARY_PATH=/opt/csapp
```

### Building the Image

```bash
# Auto-builds on first test run, or manually:
cd comp321 && docker build -t comp321-runner .
```

## Key Concepts

### Test Runner Architecture

- `runner.py` handles Docker-based compilation and test execution
- Each test file (`test_*.py`) exports `TEST_POINTS` dict and `test_*` functions
- Tests receive workspace path and return `TestResult` objects from fixtures
- Compilation uses Makefile inside Docker container
- Executables run inside Docker (Linux ELF format)

### Fixtures Module

`tests/fixtures.py` provides Docker-aware utilities:

```python
# Automatically uses Docker on non-Linux systems
run_program(executable, args, stdin_input, timeout)

# Force Docker mode
run_program(executable, args, use_docker=True)

# Enable network (for proxy tests)
run_program(executable, args, network=True)
```

### Project Configurations

YAML configs define:
- `assignment_number`: Assignment number for ordering
- `executable`: Binary name (or path like `readjcf/readjcf` for nested)
- `source_files`: C files required
- `functions`: Functions student must implement
- `test_points`: Per-test point allocation
- `benchmarking`: Optional performance mode for malloc

### Two-Phase Malloc Workflow

Malloc lab uses correctness + performance testing:
1. **Correctness** (`grade` tool): Runs test suite via runner.py
2. **Performance** (`malloc_perf` tool): Runs mdriver with all traces in Docker via `comp321/runner.py --perf`

Performance index formula:
- 40 points: Memory utilization (heap efficiency)
- 60 points: Throughput (ops/sec relative to libc)
- Configurable threshold in `malloc.yaml`: `performance_threshold: 90`

### Dockerized Performance Runs

`malloc_perf` compiles and runs `mdriver -v` inside Docker for more consistent results across machines.

## Projects Summary

| # | Name | Key Files | Tests | Description |
|---|------|-----------|-------|-------------|
| 1 | factors | factors.c | 48 | Prime factorization (iterative + recursive) |
| 2 | count | count.c | 34 | Word count with flags (-c, -l, -w) |
| 3 | linking | readjcf/readjcf.c | 27 | Parse Java .class files, extract dependencies |
| 4 | shell | tsh.c | 24 | Unix shell with job control, signals |
| 5 | malloc | mm.c | 14 + perf | Dynamic allocator with performance scoring |
| 6 | proxy | proxy.c | 13 | Concurrent web proxy with logging |

## Special Cases

### Proxy Project (Network Access)

The proxy project requires network access for testing:
- Docker runs with `--network=bridge` instead of `--network=none`
- Tests make HTTP requests to external sites
- 60-second timeout per test

### Linking Project (Nested Directory)

The linking project has executable at `readjcf/readjcf`:
- Config specifies `executable: readjcf/readjcf`
- Runner detects nested path and does `cd readjcf && make`

### Shell Project (Trace Files)

Shell tests use trace files for input:
- Tests copy trace files to workspace
- Reference output from `tshref` binary
- PIDs are normalized in output comparison

## Using Docker Compose

For interactive development:

```bash
cd comp321

# Basic compilation
WORKSPACE=./factors/skeleton_code docker compose run --rm comp321 make

# Run with network (for proxy)
WORKSPACE=./proxy docker compose run --rm comp321-network make
```

## Troubleshooting

### Docker image won't build

```bash
# Force rebuild
python3 comp321/runner.py factors <workspace> --rebuild

# Or manually
docker build -t comp321-runner -f comp321/Dockerfile comp321/
```

### "exec format error" on macOS

This happens when trying to run a Docker-compiled Linux binary directly:
- Solution: Use Docker mode (default on non-Linux systems)
- The runner handles this automatically

### Tests timeout

- Default timeout: 60 seconds per test + compilation buffer
- Proxy tests may need longer timeouts (network latency)
- Increase `timeout_per_test_seconds` in config if needed

## Gotchas & Learnings

- **[2026-03] bench-cli sandboxing is opt-in**: `bin/bench-cli` runs on the host by default. Use `--sandbox` if you want the agent inside Docker Sandbox. Direct `runner.py` calls still work unchanged.

- **[2025-01] Nested Executables**: Linking project has executable at `readjcf/readjcf`, not root. Build must happen in subdirectory.

- **[2025-01] Shell Trace Files**: Shell tests use trace files (trace01.txt-trace24.txt) copied to workspace. Reference output from `tshref` binary.

- **[2025-01] Malloc Performance Regex**: Output format is `Perf index = X/40 (util) + Y/60 (thru) = Z/100`. Parser uses regex to extract scores.

- **[2025-01] Proxy Test Timeout**: Proxy tests hit external sites (neverssl.com, etc). Long timeout (60s) required.

- **[2025-01] Docker Cross-Platform**: Docker-compiled binaries are Linux ELF files. Tests must run inside Docker on macOS/Windows.

## Decisions

- **Docker-First**: All C projects compile and run in Docker for consistency. Host execution available via `--no-docker` for Linux systems.

- **Separate Runner**: C projects use `runner.py` instead of Python framework because compilation and test execution patterns differ significantly from Python modules.

- **YAML Configs**: Each project has its own `<name>.yaml` config rather than numbered `module*.yaml` files. Allows descriptive naming and per-project settings.

- **Performance as Separate Phase**: Malloc performance testing is intentionally separate from correctness testing. Agents should focus on passing tests first, then optimize.
