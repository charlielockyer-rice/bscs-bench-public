"""Regression tests for framework.runner suite override behavior."""

import sys

from framework.config import ModuleConfig
from framework.runner import TestRunner as FrameworkRunner


def _make_runner(workspace):
    config = ModuleConfig(module_number=1, module_name="circles")
    return FrameworkRunner(
        module_number=1,
        workspace_path=workspace,
        config=config,
        course_path=workspace,
        flat_workspace=False,
    )


def test_suite_override_runtime_import_uses_course_lib(tmp_path, monkeypatch):
    """Suite tests can import course lib modules during test execution."""
    module_dir = tmp_path / "Module_1"
    module_dir.mkdir(parents=True)
    (module_dir / "solution.py").write_text("def identity(x):\n    return x\n")

    lib_dir = tmp_path / "lib"
    lib_dir.mkdir(parents=True)
    (lib_dir / "helperlib.py").write_text("def answer():\n    return 42\n")

    suite_dir = tmp_path / "suite"
    suite_dir.mkdir()
    (suite_dir / "test_runtime_import.py").write_text(
        "def test_runtime_import(student_module):\n"
        "    import helperlib\n"
        "    return helperlib.answer() == 42\n"
    )

    monkeypatch.setenv("BSCS_TEST_SUITE_DIR", str(suite_dir))
    monkeypatch.setenv("BSCS_RUN_TYPE", "test")

    runner = _make_runner(tmp_path)
    grade_result = runner.run_tests()

    assert grade_result.tests_total == 1
    assert grade_result.tests_passed == 1


def test_load_test_modules_preserves_existing_lib_path(tmp_path, monkeypatch):
    """_load_test_modules should not remove an existing lib path entry."""
    module_dir = tmp_path / "Module_1"
    module_dir.mkdir(parents=True)
    (module_dir / "solution.py").write_text("def identity(x):\n    return x\n")

    lib_dir = tmp_path / "lib"
    lib_dir.mkdir(parents=True)
    (lib_dir / "helperlib.py").write_text("VALUE = 1\n")

    suite_dir = tmp_path / "suite"
    suite_dir.mkdir()
    (suite_dir / "test_smoke.py").write_text(
        "def test_smoke(student_module):\n"
        "    return True\n"
    )

    lib_path = str(lib_dir)
    if lib_path not in sys.path:
        sys.path.insert(0, lib_path)
    monkeypatch.setenv("BSCS_TEST_SUITE_DIR", str(suite_dir))

    runner = _make_runner(tmp_path)
    try:
        modules = runner._load_test_modules()
        assert modules
        assert lib_path in sys.path
    finally:
        if lib_path in sys.path:
            sys.path.remove(lib_path)
