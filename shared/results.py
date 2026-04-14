"""
Shared result data structures for course test runners.

These dataclasses are used by all course runners to represent test results
in a consistent format. The classes support course-specific fields (like
input_description for C tests and time_seconds for Java tests) as optional
parameters.

Also includes TestStatus enum and COMP 140 formatting methods (format_summary,
format_detailed, format_full_report) previously in framework/results.py.
"""

from dataclasses import dataclass, field
from enum import Enum
from typing import Dict, List, Optional


class TestStatus(Enum):
    """Status of a test execution."""
    PASS = "pass"
    FAIL = "fail"
    ERROR = "error"
    TIMEOUT = "timeout"
    SKIP = "skip"


@dataclass
class TestResult:
    """Result of a single test.

    Attributes:
        name: Test name/identifier
        passed: Whether the test passed
        points: Points earned (0 if failed)
        max_points: Maximum points possible
        expected: Expected output/value (for display on failure)
        actual: Actual output/value (for display on failure)
        error_message: Error description if test failed
        input_description: Description of test input (COMP 321 C tests)
        time_seconds: Execution time in seconds (COMP 322 Java tests)
        status: Test status string (pass/fail/error/timeout/skip), derived from passed if not set
        traceback: Python traceback for errors (framework tests)
    """
    name: str
    passed: bool
    points: float
    max_points: float
    expected: str = ""
    actual: str = ""
    error_message: Optional[str] = None
    input_description: str = ""
    time_seconds: float = 0.0
    status: Optional[str] = None
    traceback: Optional[str] = None
    source: str = "public"  # "public" | "private" | "agent"

    def to_dict(self) -> Dict:
        """Convert to dictionary for JSON serialization."""
        return {
            "name": self.name,
            "passed": self.passed,
            "points": self.points,
            "max_points": self.max_points,
            "expected": self.expected if self.expected is not None else "",
            "actual": self.actual if self.actual is not None else "",
            "error": self.error_message,
            "input": self.input_description,
            "time": self.time_seconds,
            "status": self.status if self.status else ("pass" if self.passed else "fail"),
            "traceback": self.traceback,
            "source": self.source,
        }

    @classmethod
    def from_dict(cls, data: Dict) -> "TestResult":
        """Create from dictionary (supports both old and new field names).

        Handles field name variations from framework/results.py (test_name,
        points_earned, points_possible, execution_time_ms) and the shared
        format (name, points, max_points, time).
        """
        # Resolve passed: prefer explicit "passed" bool, fall back to status string
        if "passed" in data:
            passed = bool(data["passed"])
        elif "status" in data:
            passed = data["status"] == "pass"
        else:
            passed = False

        # Resolve time: prefer time_seconds or time (both in seconds);
        # fall back to execution_time_ms (convert ms -> seconds)
        if "time_seconds" in data:
            time_val = float(data["time_seconds"])
        elif "time" in data:
            time_val = float(data["time"])
        elif "execution_time_ms" in data:
            time_val = float(data["execution_time_ms"]) / 1000.0
        else:
            time_val = 0.0

        # Resolve status string
        status = data.get("status")
        if status is None:
            status = "pass" if passed else "fail"

        return cls(
            name=data.get("name", data.get("test_name", "")),
            passed=passed,
            points=float(data.get("points", data.get("points_earned", 0))),
            max_points=float(data.get("max_points", data.get("points_possible", 1))),
            expected=data.get("expected", "") or "",
            actual=data.get("actual", "") or "",
            error_message=data.get("error", data.get("error_message")),
            input_description=data.get("input", data.get("input_description", "")),
            time_seconds=time_val,
            status=status,
            traceback=data.get("traceback"),
            source=data.get("source", "public"),
        )


@dataclass
class GradeResult:
    """Overall grading result for a project/homework.

    This class handles result formatting and serialization for all course
    runners. The course_code determines course-specific formatting in
    format_output().

    Attributes:
        project_name: Internal project identifier (e.g., "factors", "hw1", "assignment1")
        display_name: Human-readable name (e.g., "Prime Factors", "Functional Trees")
        course_code: Course identifier for formatting (comp140, comp215, comp321, comp322, comp411)
        project_number: Project/homework/assignment number for ordering
        test_results: List of individual test results
        compilation_error: Error message if compilation failed
        performance_index: Overall performance score (malloc lab only)
        utilization_score: Memory utilization score out of 40 (malloc lab only)
        throughput_score: Throughput score out of 60 (malloc lab only)
        attempt_number: Current attempt number (COMP 140 framework)
        max_attempts: Maximum allowed attempts (COMP 140 framework)
    """
    project_name: str
    display_name: str
    course_code: str = "comp321"
    project_number: int = 0
    test_results: List[TestResult] = field(default_factory=list)
    compilation_error: Optional[str] = None
    # Performance metrics for malloc lab (comp321 only)
    performance_index: Optional[int] = None
    utilization_score: Optional[int] = None
    throughput_score: Optional[int] = None
    # Attempt tracking (COMP 140 framework)
    attempt_number: int = 0
    max_attempts: int = 0

    @property
    def total_points(self) -> float:
        return sum(r.points for r in self.test_results)

    @property
    def max_points(self) -> float:
        return sum(r.max_points for r in self.test_results)

    @property
    def passed_count(self) -> int:
        return sum(1 for r in self.test_results if r.passed)

    @property
    def total_count(self) -> int:
        return len(self.test_results)

    @property
    def tests_passed(self) -> int:
        """Compatibility alias for older framework callers."""
        return self.passed_count

    @property
    def tests_total(self) -> int:
        """Compatibility alias for older framework callers."""
        return self.total_count

    @property
    def pass_rate(self) -> float:
        """Percentage of tests passed."""
        if self.total_count == 0:
            return 0.0
        return (self.passed_count / self.total_count) * 100.0

    @property
    def score_percentage(self) -> float:
        """Percentage of points earned."""
        if self.max_points == 0:
            return 0.0
        return (self.total_points / self.max_points) * 100.0

    @property
    def all_passed(self) -> bool:
        """Whether all tests passed."""
        return self.passed_count == self.total_count

    def add_result(self, result: TestResult):
        """Append a TestResult to test_results."""
        self.test_results.append(result)

    # Status string -> display symbol for format_detailed()
    _STATUS_SYMBOLS = {s.value: s.value.upper() for s in TestStatus}

    # Course code -> (display prefix, assignment unit name)
    _COURSE_LABELS = {
        "comp140": ("COMP 140", "Module"),
        "comp215": ("COMP 215", "Homework"),
        "comp310": ("COMP 310", "Homework"),
        "comp322": ("COMP 322", "Homework"),
        "comp341": ("COMP 341", "Homework"),
        "comp411": ("COMP 411", "Assignment"),
        "comp421": ("COMP 421", "Assignment"),
        "comp321": ("COMP 321", "Project"),
    }

    def _format_header(self) -> str:
        """Format the header based on course type."""
        label, unit = self._COURSE_LABELS.get(self.course_code, ("COMP 321", "Project"))
        return f"{label} {unit} {self.project_number}: {self.display_name} - Results"

    def _format_compilation_error(self) -> list:
        """Format compilation error section."""
        if self.course_code in ("comp310", "comp322"):
            return [
                "COMPILATION/BUILD FAILED",
                "-" * 25,
                self.compilation_error[:2000] if self.compilation_error else "",
                ""
            ]
        # comp215, comp321, comp411 use similar compilation error format
        return [
            "COMPILATION FAILED",
            "-" * 18,
            self.compilation_error[:2000] if self.compilation_error else "",
            ""
        ]

    def format_output(self, verbose: bool = False) -> str:
        """Format the grade result for display.

        Used by all non-COMP-140 course runners. COMP 140 uses
        format_summary() / format_detailed() / format_full_report() instead.
        """
        lines = [
            "=" * 60,
            self._format_header(),
            "=" * 60,
            ""
        ]

        if self.compilation_error:
            lines.extend(self._format_compilation_error())
            return "\n".join(lines)

        # Summary
        lines.extend([
            "RESULTS SUMMARY",
            "-" * 15,
            f"Passed: {self.passed_count}/{self.total_count} tests ({self.pass_rate:.1f}%)",
            f"Points: {self.total_points:.1f}/{self.max_points:.1f} ({self.score_percentage:.1f}%)",
        ])

        # Add performance index for malloc lab (comp321 only)
        if self.performance_index is not None:
            lines.append(
                f"Perf index = {self.utilization_score}/40 (util) + "
                f"{self.throughput_score}/60 (thru) = {self.performance_index}/100"
            )

        lines.append("")

        if verbose or not all(r.passed for r in self.test_results):
            lines.extend([
                "DETAILED RESULTS",
                "-" * 16,
            ])

            for result in sorted(self.test_results, key=lambda r: (r.passed, r.name)):
                status_value = result.status if result.status else ("pass" if result.passed else "fail")
                status_map = {
                    "pass": "[PASS]",
                    "fail": "[FAIL]",
                    "error": "[ERROR]",
                    "timeout": "[TIMEOUT]",
                    "skip": "[SKIP]",
                }
                status = status_map.get(status_value, "[FAIL]")

                # Include time for Java tests (comp215, comp322)
                if self.course_code in ("comp215", "comp310", "comp322") and result.time_seconds > 0:
                    lines.append(f"{status} {result.name} ({result.time_seconds:.2f}s)")
                else:
                    lines.append(f"{status} {result.name}")

                # Show input description for comp321 tests
                if result.input_description:
                    lines.append(f"       Input: {result.input_description}")

                if not result.passed:
                    if self.course_code in ("comp310", "comp322"):
                        # comp310/comp322: show error details with line limits (10 lines, 500 chars)
                        if result.error_message:
                            error_lines = result.error_message.split('\n')[:10]
                            for line in error_lines:
                                lines.append(f"       {line[:500]}")
                    elif self.course_code == "comp215":
                        # comp215: show error details (5 lines, 200 chars)
                        if result.error_message:
                            error_lines = result.error_message.split('\n')[:5]
                            for line in error_lines:
                                lines.append(f"       {line.strip()[:200]}")
                    elif self.course_code == "comp411":
                        # comp411: show error details (5 lines, 100 chars)
                        if result.error_message:
                            error_lines = result.error_message.split('\n')[:5]
                            for line in error_lines:
                                lines.append(f"       {line[:100]}")
                    else:
                        # comp321/comp421/comp140: show expected/actual/error
                        if result.expected:
                            lines.append(f"       Expected: {result.expected[:100]}")
                        if result.actual:
                            lines.append(f"       Actual: {result.actual[:100]}")
                        if result.error_message:
                            lines.append(f"       Error: {result.error_message[:100]}")

            lines.append("")

        lines.append("=" * 60)
        return "\n".join(lines)

    # ------------------------------------------------------------------
    # COMP 140 formatting methods (previously in framework/results.py)
    # ------------------------------------------------------------------

    def format_summary(self) -> str:
        """Format a COMP 140-style summary header with attempt info."""
        label, unit = self._COURSE_LABELS.get(self.course_code, ("COMP 140", "Module"))
        lines = [
            "=" * 60,
            f"{label} {unit} {self.project_number}: {self.display_name} - Autograder Results",
            "=" * 60,
        ]
        if self.attempt_number and self.max_attempts:
            lines.append(f"Attempt: {self.attempt_number} of {self.max_attempts}")
        lines.extend([
            "",
            "RESULTS SUMMARY",
            "-" * 15,
            f"Passed: {self.passed_count}/{self.total_count} tests ({self.pass_rate:.1f}%)",
            f"Points: {self.total_points:.0f}/{self.max_points:.0f} ({self.score_percentage:.1f}%)",
            "",
        ])
        return "\n".join(lines)

    def format_detailed(self) -> str:
        """Format detailed test-by-test results with status symbols.

        Uses the status string to pick the display symbol (PASS/FAIL/ERROR/
        TIMEOUT/SKIP). Falls back to PASS/FAIL based on the passed bool.
        """
        lines = [
            "DETAILED RESULTS",
            "-" * 16,
        ]

        for result in self.test_results:
            effective_status = result.status if result.status else ("pass" if result.passed else "fail")
            status_symbol = self._STATUS_SYMBOLS.get(effective_status, "FAIL")

            lines.append(f"[{status_symbol}] {result.name}")

            if not result.passed:
                if result.input_description:
                    lines.append(f"       Input: {result.input_description}")
                if result.expected:
                    lines.append(f"       Expected: {result.expected}")
                if result.actual:
                    lines.append(f"       Actual: {result.actual}")
                if result.error_message:
                    lines.append(f"       Error: {result.error_message}")
                if result.traceback:
                    # Indent traceback
                    tb_lines = result.traceback.strip().split("\n")
                    lines.append("       Traceback:")
                    for tb_line in tb_lines[-5:]:  # Last 5 lines of traceback
                        lines.append(f"         {tb_line}")
                lines.append("")

        lines.append("=" * 60)
        return "\n".join(lines)

    def format_full_report(self) -> str:
        """Format the complete grading report (summary + detailed)."""
        return self.format_summary() + self.format_detailed()

    def to_dict(self) -> dict:
        """Convert to dictionary for JSON output.

        Includes course-specific field aliases for backwards compatibility:
        - comp215: homework_number, homework_name
        """
        result = {
            "project_name": self.project_name,
            "display_name": self.display_name,
            "passed": self.passed_count,
            "total": self.total_count,
            "points": self.total_points,
            "max_points": self.max_points,
            "compilation_error": self.compilation_error,
            "tests": [t.to_dict() for t in self.test_results],
        }

        # Include course-specific field aliases for backwards compatibility
        if self.course_code == "comp215":
            result["homework_number"] = self.project_number
            result["homework_name"] = self.display_name

        # Include performance metrics for malloc lab
        if self.performance_index is not None:
            result["performance_index"] = self.performance_index
            result["utilization_score"] = self.utilization_score
            result["throughput_score"] = self.throughput_score

        return result

    @classmethod
    def from_dict(cls, data: Dict) -> "GradeResult":
        """Create from dictionary (supports both old and new field names).

        Handles field name variations from framework/results.py (module_number,
        module_name) and the shared format (project_name, display_name).
        """
        # Resolve project_name: prefer project_name, derive from module_name
        project_name = data.get("project_name", "")
        if not project_name:
            module_name = data.get("module_name", data.get("display_name", ""))
            project_name = module_name.lower().replace(" ", "_") if module_name else ""

        # Resolve display_name
        display_name = data.get("display_name", data.get("module_name", project_name))

        # Resolve project_number
        project_number = data.get("project_number", data.get("module_number", 0))

        result = cls(
            project_name=project_name,
            display_name=display_name,
            course_code=data.get("course_code", "comp321"),
            project_number=project_number,
            compilation_error=data.get("compilation_error"),
            performance_index=data.get("performance_index"),
            utilization_score=data.get("utilization_score"),
            throughput_score=data.get("throughput_score"),
            attempt_number=data.get("attempt_number", 0),
            max_attempts=data.get("max_attempts", 0),
        )

        # Support both "test_results" and "tests" keys
        tests_data = data.get("tests", data.get("test_results", []))
        for test_data in tests_data:
            result.add_result(TestResult.from_dict(test_data))

        return result
