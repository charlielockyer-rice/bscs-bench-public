"""Unit tests for framework.test_loader."""

import sys
from pathlib import Path

from framework.test_loader import load_python_module_from_file, load_python_test_modules


def test_load_python_module_from_file(tmp_path: Path):
    test_file = tmp_path / "test_sample.py"
    test_file.write_text("VALUE = 7\n")

    module = load_python_module_from_file(test_file, "sample_test_module")

    assert module.VALUE == 7


def test_load_python_test_modules_loads_canonical_file(tmp_path: Path):
    canonical = tmp_path / "test_module1.py"
    canonical.write_text("VALUE = 1\n")

    modules = load_python_test_modules(
        canonical_test_file=canonical,
        canonical_module_name="course_test_module1",
    )

    assert len(modules) == 1
    assert modules[0].VALUE == 1
    assert modules[0].__name__ == "course_test_module1"


def test_load_python_test_modules_loads_suite_override(tmp_path: Path):
    canonical = tmp_path / "test_module1.py"
    canonical.write_text("VALUE = 0\n")

    suite_dir = tmp_path / "suite"
    suite_dir.mkdir()
    (suite_dir / "test_a.py").write_text("VALUE = 'a'\n")
    (suite_dir / "test_b.py").write_text("VALUE = 'b'\n")

    modules = load_python_test_modules(
        canonical_test_file=canonical,
        canonical_module_name="course_test_module1",
        suite_dir=suite_dir,
        suite_namespace="suite_ns",
    )

    assert [module.VALUE for module in modules] == ["a", "b"]
    assert all(module.__name__.startswith("suite_ns_") for module in modules)


def test_load_python_test_modules_preserves_existing_sys_path(tmp_path: Path):
    canonical = tmp_path / "test_module1.py"
    canonical.write_text("VALUE = 0\n")

    lib_dir = tmp_path / "lib"
    lib_dir.mkdir()
    lib_path = str(lib_dir)
    if lib_path not in sys.path:
        sys.path.insert(0, lib_path)

    suite_dir = tmp_path / "suite"
    suite_dir.mkdir()
    (suite_dir / "test_smoke.py").write_text("VALUE = True\n")

    try:
        modules = load_python_test_modules(
            canonical_test_file=canonical,
            canonical_module_name="course_test_module1",
            suite_dir=suite_dir,
        )
        assert modules
        assert lib_path in sys.path
    finally:
        if lib_path in sys.path:
            sys.path.remove(lib_path)


def test_load_python_test_modules_rejects_empty_suite_dir(tmp_path: Path):
    canonical = tmp_path / "test_module1.py"
    canonical.write_text("VALUE = 0\n")

    suite_dir = tmp_path / "suite"
    suite_dir.mkdir()

    try:
        load_python_test_modules(
            canonical_test_file=canonical,
            canonical_module_name="course_test_module1",
            suite_dir=suite_dir,
        )
        assert False, "Expected FileNotFoundError"
    except FileNotFoundError as exc:
        assert "No test files found in suite dir" in str(exc)
