"""
Test runner for the evaluation framework.
"""

import sys
import os
import traceback
import time
from pathlib import Path
from typing import Dict, List, Optional, Callable, Any, Tuple
from dataclasses import dataclass
from contextlib import contextmanager

from .config import ModuleConfig, load_module_config, WorkspaceConfig, load_workspace_config
from shared.results import TestResult, GradeResult, TestStatus
from .sandbox import SafeImporter, timeout_context, ExecutionTimeoutError
from .tracker import AttemptTracker
from .test_loader import load_python_test_modules


@dataclass
class TestCase:
    """Definition of a single test case."""
    name: str
    function_name: str
    points: float
    test_func: Callable
    input_description: str = ""
    source: str = "public"  # "public" | "private" | "agent"


class TestRunner:
    """
    Runs tests against student code and produces grading results.

    This class:
    1. Loads student code safely using SafeImporter
    2. Runs test cases against the student's functions
    3. Captures results, errors, and timing
    4. Produces a GradeResult with all test outcomes
    """

    def __init__(
        self,
        module_number: int,
        workspace_path: Path,
        config: Optional[ModuleConfig] = None,
        course_path: Optional[Path] = None,
        flat_workspace: bool = False,
    ):
        """
        Initialize the test runner.

        Args:
            module_number: The module to test (1-7)
            workspace_path: Path to the agent's workspace
            config: Optional module config (loads from YAML if not provided)
            course_path: Path to the course directory (contains tests/, configs/, lib/)
            flat_workspace: If True, solution.py is at workspace root (new single-task format)
        """
        self.module_number = module_number
        self.workspace_path = Path(workspace_path)
        self.course_path = Path(course_path) if course_path else None
        self.config = config or load_module_config(module_number, self.course_path)
        self.flat_workspace = flat_workspace

        # Create appropriate tracker based on workspace type
        if flat_workspace:
            self.tracker = AttemptTracker.from_workspace(workspace_path)
        else:
            self.tracker = AttemptTracker(workspace_path, module_number)

        # Determine paths based on workspace type
        if flat_workspace:
            # New flat workspace: solution.py at workspace root
            self.module_dir = self.workspace_path
            self.template_path = self.workspace_path / self.config.template_filename
        else:
            # Legacy: solution.py in Module_N/ subdirectory
            self.module_dir = self.workspace_path / f"Module_{module_number}"
            self.template_path = self.module_dir / self.config.template_filename

        # Find lib path - could be symlink in workspace or in course directory
        if (self.workspace_path / "lib").exists():
            self.lib_path = self.workspace_path / "lib"
        elif self.course_path and (self.course_path / "lib").exists():
            self.lib_path = self.course_path / "lib"
        else:
            self.lib_path = Path(__file__).parent.parent / "lib"

    @classmethod
    def from_workspace(cls, workspace_path: Path) -> "TestRunner":
        """
        Create a TestRunner from a flat single-task workspace.

        Reads workspace.yaml to determine module number and course path.

        Args:
            workspace_path: Path to the workspace directory

        Returns:
            TestRunner configured for the workspace
        """
        workspace_path = Path(workspace_path)
        ws_config = load_workspace_config(workspace_path)

        # Determine course path from workspace config
        # Course is stored as name (e.g., "comp140"), resolve relative to project root
        project_root = Path(__file__).parent.parent
        course_path = project_root / ws_config.course

        return cls(
            module_number=ws_config.module_number,
            workspace_path=workspace_path,
            course_path=course_path,
            flat_workspace=True,
        )

    def _load_test_modules(self) -> List[Any]:
        """Load test module(s) for this assignment.

        Default behavior (no suite override): load the canonical public module only.
        Suite override behavior: load all `test_*.py` modules so agent-added tests run.
        """
        suite_override = os.environ.get("BSCS_TEST_SUITE_DIR", "").strip()

        # Import canonical module from course tests/ directory
        if self.course_path:
            tests_dir = self.course_path / "tests"
        else:
            tests_dir = Path(__file__).parent / "tests"

        test_module_name = f"test_module{self.module_number}"
        test_file = tests_dir / f"{test_module_name}.py"
        suite_dir = Path(suite_override).resolve() if suite_override else None
        return load_python_test_modules(
            canonical_test_file=test_file,
            canonical_module_name=f"bscs_course_{self.module_number}_{test_module_name}",
            suite_dir=suite_dir,
            suite_namespace=f"bscs_suite_{self.module_number}",
        )

    def _get_test_cases(self, test_modules: List[Any]) -> List[TestCase]:
        """Extract test cases from loaded test modules."""
        test_cases = []

        # Look for functions starting with "test_" in each module.
        for test_module in test_modules:
            module_label = getattr(test_module, "__name__", "test_module")
            module_short = module_label.split(".")[-1]

            # Detect source from module name
            if "_private" in module_label:
                source = "private"
            elif "_agent" in module_label or "test_my_" in module_label:
                source = "agent"
            else:
                source = "public"

            for name in dir(test_module):
                if name.startswith("test_"):
                    func = getattr(test_module, name)
                    if callable(func):
                        points = 1.0
                        if hasattr(test_module, "TEST_POINTS"):
                            points = test_module.TEST_POINTS.get(name, 1.0)

                        input_desc = ""
                        if hasattr(func, "input_description"):
                            input_desc = func.input_description

                        function_name = name.replace("test_", "").split("_")[0]
                        case_name = f"{module_short}.{name}"
                        test_cases.append(TestCase(
                            name=case_name,
                            function_name=function_name,
                            points=points,
                            test_func=func,
                            input_description=input_desc,
                            source=source,
                        ))

        return test_cases

    def _run_single_test(
        self,
        test_case: TestCase,
        student_module: Any,
        timeout_seconds: float,
    ) -> TestResult:
        """
        Run a single test case.

        Args:
            test_case: The test case to run
            student_module: The loaded student module
            timeout_seconds: Timeout for test execution

        Returns:
            TestResult with the outcome
        """
        start_time = time.time()

        try:
            with timeout_context(timeout_seconds):
                # Run the test function, passing the student module
                result = test_case.test_func(student_module)

            execution_time = (time.time() - start_time) * 1000

            # Test function should return (passed, expected, actual) or just bool
            if isinstance(result, tuple):
                passed, expected, actual = result
            else:
                passed = bool(result)
                expected = None
                actual = None

            if passed:
                return TestResult(
                    name=test_case.name,
                    passed=True,
                    points=test_case.points,
                    max_points=test_case.points,
                    input_description=test_case.input_description,
                    time_seconds=execution_time / 1000.0,
                    status=TestStatus.PASS.value,
                    source=test_case.source,
                )
            else:
                return TestResult(
                    name=test_case.name,
                    passed=False,
                    points=0.0,
                    max_points=test_case.points,
                    input_description=test_case.input_description,
                    expected=str(expected) if expected is not None else "",
                    actual=str(actual) if actual is not None else "",
                    time_seconds=execution_time / 1000.0,
                    status=TestStatus.FAIL.value,
                    source=test_case.source,
                )

        except ExecutionTimeoutError as e:
            return TestResult(
                name=test_case.name,
                passed=False,
                points=0.0,
                max_points=test_case.points,
                input_description=test_case.input_description,
                error_message=str(e),
                time_seconds=timeout_seconds,
                status=TestStatus.TIMEOUT.value,
                source=test_case.source,
            )
        except AssertionError as e:
            # Assertion errors from tests contain expected/actual info
            execution_time = (time.time() - start_time) * 1000
            return TestResult(
                name=test_case.name,
                passed=False,
                points=0.0,
                max_points=test_case.points,
                input_description=test_case.input_description,
                error_message=str(e),
                traceback=traceback.format_exc(),
                time_seconds=execution_time / 1000.0,
                status=TestStatus.FAIL.value,
                source=test_case.source,
            )
        except Exception as e:
            execution_time = (time.time() - start_time) * 1000
            return TestResult(
                name=test_case.name,
                passed=False,
                points=0.0,
                max_points=test_case.points,
                input_description=test_case.input_description,
                error_message=f"{type(e).__name__}: {e}",
                traceback=traceback.format_exc(),
                time_seconds=execution_time / 1000.0,
                status=TestStatus.ERROR.value,
                source=test_case.source,
            )

    def run_tests(self) -> GradeResult:
        """
        Run all tests against the student's code.

        Returns:
            GradeResult with all test outcomes

        Raises:
            FileNotFoundError: If the template file doesn't exist
            RuntimeError: If max attempts exceeded
        """
        # For suite-aware test/submit flow, test runs are unlimited.
        # Keep legacy attempt limits only when run type is unspecified.
        run_type = os.environ.get("BSCS_RUN_TYPE", "").strip().lower()
        enforce_attempt_limit = run_type not in {"test", "submit"}
        if enforce_attempt_limit and not self.tracker.can_attempt(self.config.max_attempts):
            raise RuntimeError(
                f"Maximum attempts ({self.config.max_attempts}) exceeded. "
                f"Best score: {self.tracker.best_attempt.score_percentage:.1f}%"
            )

        # Check that template exists
        if not self.template_path.exists():
            raise FileNotFoundError(f"Template not found: {self.template_path}")

        # Create the grade result
        grade_result = GradeResult(
            project_name=self.config.module_name.lower().replace(" ", "_"),
            display_name=self.config.module_name,
            course_code="comp140",
            project_number=self.module_number,
            attempt_number=self.tracker.attempt_count + 1,
            max_attempts=self.config.max_attempts,
        )

        with self._ensure_lib_path():
            # Load test module
            try:
                test_modules = self._load_test_modules()
                test_cases = self._get_test_cases(test_modules)
            except Exception as e:
                # If tests can't be loaded, that's a framework error
                raise RuntimeError(f"Failed to load tests: {e}")

            # Load student module
            with SafeImporter(self.workspace_path, self.lib_path) as importer:
                try:
                    student_module = importer.load_student_module(
                        self.template_path,
                        f"student_module{self.module_number}",
                        timeout_seconds=30.0,
                    )
                except Exception as e:
                    # Import error - all tests fail
                    for test_case in test_cases:
                        grade_result.add_result(TestResult(
                            name=test_case.name,
                            passed=False,
                            points=0.0,
                            max_points=test_case.points,
                            error_message=f"Module import failed: {e}",
                            traceback=traceback.format_exc(),
                            status=TestStatus.ERROR.value,
                            source=test_case.source,
                        ))

                    # Record the attempt
                    self.tracker.record_attempt(grade_result, self.template_path)
                    return grade_result

                # Run each test
                for test_case in test_cases:
                    result = self._run_single_test(
                        test_case,
                        student_module,
                        self.config.timeout_per_test_seconds,
                    )
                    grade_result.add_result(result)

        # Record the attempt
        self.tracker.record_attempt(grade_result, self.template_path)

        return grade_result

    @contextmanager
    def _ensure_lib_path(self):
        """Ensure lib path stays importable for module load and test execution."""
        lib_str = str(self.lib_path) if self.lib_path and self.lib_path.exists() else None
        inserted = False
        if lib_str and lib_str not in sys.path:
            sys.path.insert(0, lib_str)
            inserted = True
        try:
            yield
        finally:
            if inserted and lib_str in sys.path:
                sys.path.remove(lib_str)

    def check_submission(self) -> Tuple[bool, str]:
        """
        Quick check if the submission is valid for grading.

        Returns:
            (is_valid, message) tuple
        """
        if not self.template_path.exists():
            return False, f"Template file not found: {self.template_path}"

        if not self.tracker.can_attempt(self.config.max_attempts):
            best = self.tracker.best_attempt
            return False, (
                f"Maximum attempts ({self.config.max_attempts}) reached. "
                f"Best score: {best.score_percentage:.1f}%"
            )

        return True, "Ready for grading"


def run_grading(
    module_number: int,
    workspace_path: Path,
    course_path: Optional[Path] = None,
) -> GradeResult:
    """
    Convenience function to run grading for a module (legacy format).

    Args:
        module_number: The module to grade (1-7)
        workspace_path: Path to the agent's workspace
        course_path: Path to the course directory

    Returns:
        GradeResult with all test outcomes
    """
    runner = TestRunner(module_number, workspace_path, course_path=course_path)
    return runner.run_tests()


def run_grading_from_workspace(workspace_path: Path) -> GradeResult:
    """
    Convenience function to run grading for a flat single-task workspace.

    Reads workspace.yaml to determine module and course.

    Args:
        workspace_path: Path to the workspace directory

    Returns:
        GradeResult with all test outcomes
    """
    runner = TestRunner.from_workspace(workspace_path)
    return runner.run_tests()
