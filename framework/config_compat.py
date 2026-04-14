"""
Configuration compatibility layer for unified config schema.

This module provides functions to normalize config files from the old
course-specific formats to the new unified schema. It maintains backwards
compatibility by mapping old field names to new ones.

Field Mappings:
  Old Field              -> New Field
  -----------------         -----------------
  module_number          -> assignment_number
  homework_number        -> assignment_number
  project_number         -> assignment_number
  module_name            -> assignment_name
  homework_name          -> assignment_name
  project_name           -> assignment_name
  timeout_per_test_seconds -> grading.timeout_seconds
  total_points           -> grading.total_points
  total_tests            -> grading.total_tests
  max_attempts           -> grading.max_attempts
  implementation_files   -> source_files
  solution_files         -> source_files
"""

from typing import Dict, Any, List, Optional


# =============================================================================
# Default Timeout Constants (seconds per test)
# =============================================================================
# These are the canonical timeout defaults for each language/runtime.
# Course runners should import and use these constants instead of defining
# their own magic numbers.

# Python: Simple interpreted execution, most tests complete in < 1 second.
# Higher value accounts for complex algorithms (graph search, QR encoding).
DEFAULT_TIMEOUT_PYTHON = 30

# Java (standard): JIT compilation overhead + test framework startup.
# Most JUnit tests complete in 1-5 seconds.
DEFAULT_TIMEOUT_JAVA = 30

# Java with HJlib: Bytecode-transforming javaagent adds significant overhead.
# Parallel runtime initialization (thread pools), scalability tests run
# multiple thread configurations, performance tests may run multiple iterations.
# Most tests complete in 1-10 seconds, but edge cases can approach 60+ seconds.
DEFAULT_TIMEOUT_JAVA_HJLIB = 120

# C: Native execution is fast, but some projects (shell, proxy) involve
# process management, networking, or complex I/O that need more time.
DEFAULT_TIMEOUT_C = 60


def normalize_config(config: Dict[str, Any]) -> Dict[str, Any]:
    """
    Normalize a config dictionary to the unified schema.

    This function adds the new unified field names while preserving
    the original fields for backwards compatibility. It does NOT modify
    the original config dict.

    Args:
        config: Original config dictionary

    Returns:
        New dictionary with both old and new field names
    """
    result = dict(config)

    # ==========================================================================
    # Identity fields: assignment_number, assignment_name, display_name
    # ==========================================================================

    # assignment_number (from module_number, homework_number, or project_number)
    if "assignment_number" not in result:
        if "module_number" in result:
            result["assignment_number"] = result["module_number"]
        elif "homework_number" in result:
            result["assignment_number"] = result["homework_number"]
        elif "project_number" in result:
            result["assignment_number"] = result["project_number"]

    # assignment_name (from module_name, homework_name, or project_name)
    if "assignment_name" not in result:
        if "module_name" in result:
            # module_name is typically human-readable, convert to machine name
            result["assignment_name"] = _to_machine_name(result["module_name"])
        elif "homework_name" in result:
            result["assignment_name"] = _to_machine_name(result["homework_name"])
        elif "project_name" in result:
            result["assignment_name"] = result["project_name"]  # Already machine-readable

    # display_name (keep existing or derive from assignment_name)
    if "display_name" not in result:
        if "module_name" in result:
            result["display_name"] = result["module_name"]
        elif "homework_name" in result:
            result["display_name"] = result["homework_name"]
        elif "project_name" in result:
            # project_name is machine-readable, make it human-readable
            result["display_name"] = result["project_name"].replace("_", " ").title()

    # ==========================================================================
    # Grading section
    # ==========================================================================

    # Build grading section if it doesn't exist (or is incomplete)
    if "grading" not in result or not isinstance(result.get("grading"), dict):
        grading_section = result.get("grading", {})
        if not isinstance(grading_section, dict):
            grading_section = {}
    else:
        grading_section = dict(result["grading"])

    # Map flat fields into grading section
    if "timeout_seconds" not in grading_section:
        if "timeout_per_test_seconds" in result:
            grading_section["timeout_seconds"] = result["timeout_per_test_seconds"]
        elif "timeout_seconds" in result:
            grading_section["timeout_seconds"] = result["timeout_seconds"]

    if "total_points" not in grading_section and "total_points" in result:
        grading_section["total_points"] = result["total_points"]

    if "total_tests" not in grading_section and "total_tests" in result:
        grading_section["total_tests"] = result["total_tests"]

    if "max_attempts" not in grading_section and "max_attempts" in result:
        grading_section["max_attempts"] = result["max_attempts"]

    # Only add grading section if it has content
    if grading_section:
        result["grading"] = grading_section

    # ==========================================================================
    # Source files normalization
    # ==========================================================================

    # source_files should be a list of objects with 'path' keys
    # Old formats may use: implementation_files, solution_files, or source_files as string list
    if "source_files" not in result or not result["source_files"]:
        source_files = []
        if "implementation_files" in result:
            source_files = result["implementation_files"]
        elif "solution_files" in result:
            source_files = result["solution_files"]

        # Normalize to list of objects with 'path' key
        if source_files:
            normalized = []
            for item in source_files:
                if isinstance(item, str):
                    normalized.append({"path": item})
                elif isinstance(item, dict):
                    normalized.append(item)
            if normalized:
                result["source_files"] = normalized

    # ==========================================================================
    # Tests section normalization
    # ==========================================================================

    if "tests" not in result:
        tests_section = {}

        # Java: test_classes -> tests.classes
        if "test_classes" in result:
            tests_section["classes"] = result["test_classes"]
        elif "test_class" in result:
            tests_section["classes"] = [result["test_class"]]

        # C: executable -> tests.executable
        if "executable" in result:
            tests_section["executable"] = result["executable"]

        if tests_section:
            result["tests"] = tests_section

    # ==========================================================================
    # Build section for Java
    # ==========================================================================

    if "build" not in result:
        build_section = {}

        # HJlib configuration
        if result.get("uses_hjlib"):
            build_section["tool"] = "maven"
            if "hjlib_jar" in result:
                build_section["javaagent"] = result["hjlib_jar"]

        if build_section:
            result["build"] = build_section

    # ==========================================================================
    # Runtime section
    # ==========================================================================

    if "runtime" not in result:
        runtime = {}
        if "java_version" in result:
            runtime["version"] = str(result["java_version"])
        elif result.get("language") == "java":
            # Default Java versions by course pattern
            if result.get("uses_hjlib"):
                runtime["version"] = "11"  # HJlib requires Java 11
            else:
                runtime["version"] = "17"  # Default to modern Java

        if runtime:
            result["runtime"] = runtime

    return result


def _to_machine_name(name: str) -> str:
    """
    Convert a human-readable name to a machine-readable identifier.

    Examples:
        "Circles" -> "circles"
        "Kevin Bacon" -> "kevin_bacon"
        "QR Code" -> "qr_code"
        "Spot It!" -> "spot_it"
    """
    # Lowercase and replace spaces with underscores
    result = name.lower().replace(" ", "_")
    # Remove special characters except underscores
    result = "".join(c for c in result if c.isalnum() or c == "_")
    # Replace multiple underscores with single
    while "__" in result:
        result = result.replace("__", "_")
    # Strip leading/trailing underscores
    return result.strip("_")


def get_assignment_number(config: Dict[str, Any]) -> Optional[int]:
    """
    Get the assignment number from a config, checking all legacy field names.

    Args:
        config: Config dictionary

    Returns:
        Assignment number or None if not found
    """
    for field in ("assignment_number", "module_number", "homework_number", "project_number"):
        if field in config:
            return config[field]
    return None


def get_assignment_name(config: Dict[str, Any]) -> Optional[str]:
    """
    Get the assignment name from a config, checking all legacy field names.

    Args:
        config: Config dictionary

    Returns:
        Assignment name or None if not found
    """
    for field in ("assignment_name", "module_name", "homework_name", "project_name"):
        if field in config:
            value = config[field]
            # module_name and homework_name are human-readable, convert
            if field in ("module_name", "homework_name"):
                return _to_machine_name(value)
            return value
    return None


def get_display_name(config: Dict[str, Any]) -> Optional[str]:
    """
    Get the display name from a config.

    Args:
        config: Config dictionary

    Returns:
        Display name or None if not found
    """
    if "display_name" in config:
        return config["display_name"]
    # Fall back to human-readable name fields
    for field in ("module_name", "homework_name"):
        if field in config:
            return config[field]
    # project_name needs to be humanized
    if "project_name" in config:
        return config["project_name"].replace("_", " ").title()
    return None


def get_timeout_seconds(config: Dict[str, Any], default: int = DEFAULT_TIMEOUT_C) -> int:
    """
    Get the timeout in seconds from a config.

    Callers should explicitly pass a language-appropriate default from this module:
    - DEFAULT_TIMEOUT_PYTHON (30s) for Python courses
    - DEFAULT_TIMEOUT_JAVA (30s) for standard Java courses
    - DEFAULT_TIMEOUT_JAVA_HJLIB (120s) for HJlib/interpreter courses
    - DEFAULT_TIMEOUT_C (60s) for C courses

    Args:
        config: Config dictionary
        default: Default value if not found (defaults to DEFAULT_TIMEOUT_C)

    Returns:
        Timeout in seconds
    """
    # Check nested grading section first
    grading = config.get("grading", {})
    if isinstance(grading, dict) and "timeout_seconds" in grading:
        return grading["timeout_seconds"]

    # Check flat fields
    for field in ("timeout_seconds", "timeout_per_test_seconds"):
        if field in config:
            return config[field]

    return default


def get_total_tests(config: Dict[str, Any], default: int = 0) -> int:
    """
    Get the total number of tests from a config.

    Args:
        config: Config dictionary
        default: Default value if not found

    Returns:
        Total number of tests
    """
    # Check nested grading section first
    grading = config.get("grading", {})
    if isinstance(grading, dict) and "total_tests" in grading:
        return grading["total_tests"]

    # Check flat field
    if "total_tests" in config:
        return config["total_tests"]

    return default


def get_total_points(config: Dict[str, Any], default: int = 100) -> int:
    """
    Get the total points from a config.

    Args:
        config: Config dictionary
        default: Default value if not found

    Returns:
        Total points
    """
    # Check nested grading section first
    grading = config.get("grading", {})
    if isinstance(grading, dict) and "total_points" in grading:
        return grading["total_points"]

    # Check flat field
    if "total_points" in config:
        return config["total_points"]

    return default


def get_source_files(config: Dict[str, Any]) -> List[str]:
    """
    Get the list of source file paths from a config.

    This function returns only the paths as strings, discarding any additional
    metadata (like 'functions'). Use get_source_file_details() to preserve
    the full metadata.

    Args:
        config: Config dictionary

    Returns:
        List of source file paths (as strings)
    """
    # Check source_files first (may be normalized or raw)
    if "source_files" in config:
        files = config["source_files"]
        if isinstance(files, list):
            result = []
            for item in files:
                if isinstance(item, str):
                    result.append(item)
                elif isinstance(item, dict) and "path" in item:
                    result.append(item["path"])
            return result

    # Check legacy fields
    for field in ("implementation_files", "solution_files"):
        if field in config:
            return config[field]

    return []


def get_source_file_details(config: Dict[str, Any]) -> List[Dict[str, Any]]:
    """
    Get the list of source files with full metadata from a config.

    Each item in the returned list is a dict with at least a 'path' key,
    and optionally other metadata like 'functions'.

    Example return value:
        [
            {"path": "solution.py", "functions": ["distance", "midpoint", "slope"]},
            {"path": "helpers.py"}
        ]

    Args:
        config: Config dictionary

    Returns:
        List of source file dicts with 'path' and optional metadata
    """
    # Check source_files first (may be normalized or raw)
    if "source_files" in config:
        files = config["source_files"]
        if isinstance(files, list):
            result = []
            for item in files:
                if isinstance(item, str):
                    result.append({"path": item})
                elif isinstance(item, dict) and "path" in item:
                    result.append(item)
            return result

    # Check legacy fields - these are always string lists
    for field in ("implementation_files", "solution_files"):
        if field in config:
            return [{"path": f} for f in config[field]]

    return []


def get_source_file_functions(config: Dict[str, Any], path: str) -> List[str]:
    """
    Get the list of functions for a specific source file.

    Args:
        config: Config dictionary
        path: The source file path to look up

    Returns:
        List of function names, or empty list if not found or no functions defined
    """
    details = get_source_file_details(config)
    for item in details:
        if item.get("path") == path:
            return item.get("functions", [])
    return []


def get_test_classes(config: Dict[str, Any]) -> list:
    """
    Get the list of test classes for Java projects.

    Args:
        config: Config dictionary

    Returns:
        List of test class names
    """
    # Check nested tests section
    tests = config.get("tests", {})
    if isinstance(tests, dict) and "classes" in tests:
        return tests["classes"]

    # Check flat fields
    if "test_classes" in config:
        return config["test_classes"]
    if "test_class" in config:
        return [config["test_class"]]

    return []
