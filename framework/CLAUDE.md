# Evaluation Framework

Generic Python test runner for evaluating agent submissions on programming assignments. Course-agnostic - tests and configs live in course directories (e.g., `comp140/`).

## Structure

```
framework/
  __init__.py       # Public API exports (re-exports from shared.results)
  config.py         # WorkspaceConfig, ModuleConfig, YAML/JSON loading
  config_compat.py  # Unified schema helpers + timeout constants
  runner.py         # TestRunner - orchestrates test execution
  tracker.py        # AttemptTracker - persists attempt history to JSON
  sandbox.py        # SafeImporter - isolated module loading with timeout
  cli.py            # `bin/grade` CLI implementation
  yaml_utils.py     # Lightweight YAML parser (no PyYAML dependency)
```

**Note:** `results.py` was consolidated into `shared/results.py`. All result
classes (`TestResult`, `GradeResult`, `TestStatus`) now live there.

## Key Concepts

### Workspace Type

Single-task workspace with `workspace.yaml` at root:
```
workspace_circles/
  workspace.yaml    # {assignment_number, assignment_name, course, language}
  solution.py       # Agent edits this
  template.py       # Clean reference
  attempts.json     # Attempt history
```

### Test Execution Flow

```
TestRunner.run_tests()
  -> AttemptTracker.can_attempt()      # Check attempt limit
  -> _load_test_module()               # Import test_module{N}.py from course/tests/
  -> SafeImporter.load_student_module() # Load solution.py with timeout + blocking
  -> _run_single_test() for each test  # Run with timeout_context()
  -> AttemptTracker.record_attempt()   # Persist result
```

### Test Function Contract

Test functions in `course/tests/test_module{N}.py` receive the student module:

```python
def test_distance_basic(student_module):
    """Return (passed, expected, actual) or just bool"""
    result = student_module.distance(0, 0, 3, 4)
    return (result == 5.0, 5.0, result)
```

### Result Statuses

`TestStatus` enum (in `shared/results.py`): `PASS`, `FAIL`, `ERROR`, `TIMEOUT`, `SKIP`

The framework uses `TestStatus.X.value` (string) when constructing `TestResult`
objects, since the shared `TestResult.status` field is a plain string.

- `"fail"`: Test returned False or assertion failed
- `"error"`: Exception during test (not timeout)
- `"timeout"`: Exceeded `timeout_per_test_seconds`

### Timeout Constants (config_compat.py)

Centralized timeout defaults for all languages:
- `DEFAULT_TIMEOUT_PYTHON = 30` - Simple interpreted execution
- `DEFAULT_TIMEOUT_JAVA = 30` - JIT compilation + test framework startup
- `DEFAULT_TIMEOUT_JAVA_HJLIB = 120` - HJlib bytecode transformation overhead
- `DEFAULT_TIMEOUT_C = 60` - Native execution, but shell/proxy tests need more time

### Config Compatibility (config_compat.py)

Helper functions for the unified config schema:
- `normalize_config(dict)` - Add unified fields while preserving legacy
- `get_assignment_number(dict)` - Get from assignment_number/module_number/homework_number/project_number
- `get_assignment_name(dict)` - Get from assignment_name/module_name/homework_name/project_name
- `get_display_name(dict)` - Human-readable name
- `get_timeout_seconds(dict, default)` - From grading.timeout_seconds or legacy fields
- `get_source_files(dict)` - List of file paths
- `get_test_classes(dict)` - Java test class names

### Configuration Loading

Priority: YAML file -> JSON file -> hardcoded defaults

```python
load_workspace_config(path)  # workspace.yaml -> workspace.json
load_module_config(num, course_path)  # course/configs/moduleN.yaml -> defaults
```

### Result Classes (shared/results.py)

All result classes live in `shared/results.py` and are used by every course runner:

- `TestStatus` enum: `PASS`, `FAIL`, `ERROR`, `TIMEOUT`, `SKIP`
- `TestResult`: Single test outcome with `name`, `passed`, `points`, `max_points`, `status` (string), `time_seconds`, `error_message`, `traceback`, etc.
- `GradeResult`: Aggregate results with `project_name`, `display_name`, `course_code`, `project_number`, `test_results`, plus optional `attempt_number`/`max_attempts` for COMP 140

Key methods on `GradeResult`:
- `format_output(verbose)` - Course-aware formatting (comp215/comp310/comp321/comp322/comp411/comp421)
- `format_summary()` - COMP 140-style header with attempt info
- `format_detailed()` - Test-by-test results with status symbols (PASS/FAIL/ERROR/TIMEOUT/SKIP)
- `format_full_report()` - `format_summary() + format_detailed()`
- `to_dict()` / `from_dict()` - JSON serialization/deserialization
- `add_result(test_result)` - Append a TestResult

The framework's `runner.py` and `tracker.py` import directly from `shared.results`.

## Gotchas & Learnings

- **Import Blocking**: `SafeImporter` blocks imports from `eval/tests`, `solutions` paths. Tests themselves bypass this.

- **sys.path Pollution**: `SafeImporter` adds lib and module directories to `sys.path`. Always use context manager for cleanup.

- **Timeout on Unix Only**: `timeout_context()` uses SIGALRM - no timeout protection on Windows. Tests run unbounded there.

- **Code Hash Truncation**: `AttemptTracker.compute_code_hash()` returns first 16 chars of SHA256 - enough for change detection, not security.

- **Module Name Collisions**: Each loaded module gets unique name like `student_module1`. Without cleanup, subsequent loads may get stale cached modules.

- **Flat Workspace Detection**: CLI checks for `workspace.yaml` OR `workspace.json` to determine workspace type. Missing both triggers legacy mode hint.

- **Test Point Values**: Default 1.0 per test. Override via `TEST_POINTS` dict in test module. Grade parser uses pass/fail counts.

## Decisions

- **PyYAML Optional**: `yaml_utils.py` provides basic parsing for systems without PyYAML. Full YAML used when available.

- **Attempt Limits**: `max_attempts` enforced at runner level. After limit, `run_tests()` raises `RuntimeError`. Agents must handle gracefully.

- **No Test Discovery**: Tests explicitly loaded via `test_module{N}` naming convention, not pytest autodiscovery. Keeps framework minimal.
