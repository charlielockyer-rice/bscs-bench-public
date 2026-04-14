"""
Helpers for loading Python test modules from canonical test files or suite overrides.
"""

from __future__ import annotations

import importlib.util
import os
import sys
from pathlib import Path
from typing import Any


def load_python_module_from_file(test_file: Path, module_name: str) -> Any:
    """Load a Python module from a file path."""
    spec = importlib.util.spec_from_file_location(module_name, test_file)
    if spec is None or spec.loader is None:
        raise RuntimeError(f"Failed to load test module: {test_file}")
    module = importlib.util.module_from_spec(spec)
    sys.modules[module_name] = module
    spec.loader.exec_module(module)
    return module


def load_python_test_modules(
    *,
    canonical_test_file: Path,
    canonical_module_name: str,
    suite_dir: Path | None = None,
    suite_namespace: str = "bscs_suite",
) -> list[Any]:
    """Load Python test modules from a canonical file or an override suite dir."""
    if suite_dir is None:
        if not canonical_test_file.exists():
            raise FileNotFoundError(f"Test module not found: {canonical_test_file}")
        return [load_python_module_from_file(canonical_test_file, canonical_module_name)]

    suite_dir = Path(suite_dir).resolve()
    test_files = sorted(suite_dir.glob("test_*.py"))
    if not test_files:
        raise FileNotFoundError(f"No test files found in suite dir: {suite_dir}")

    modules: list[Any] = []
    sys.path.insert(0, str(suite_dir))
    try:
        for test_file in test_files:
            module_suffix = str(test_file.relative_to(suite_dir)).replace(os.sep, "_").replace(".", "_")
            module_name = f"{suite_namespace}_{module_suffix}"
            modules.append(load_python_module_from_file(test_file, module_name))
    finally:
        if str(suite_dir) in sys.path:
            sys.path.remove(str(suite_dir))
    return modules
