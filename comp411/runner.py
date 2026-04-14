#!/usr/bin/env python3
"""
COMP 411 Java Test Runner (Docker-only)

Runs Jam interpreter tests inside a Docker container with Java 8.

Usage:
    python runner.py <assignment> <workspace> [options]

Examples:
    python runner.py assignment1 ./workspaces/agent_assignment1
    python runner.py assignment3 ./workspaces/agent_assignment3 --verbose
    python runner.py assignment4 ./workspaces/agent_assignment4 --json
"""

import os
import re
import shlex
import sys
from pathlib import Path

# Add parent directory to path for imports
sys.path.insert(0, str(Path(__file__).parent.parent))
from framework.docker_utils import (
    ensure_docker_image,
    is_in_container,
    run_bash_in_container,
    run_local,
)
from framework.config_compat import (
    get_assignment_number,
    get_timeout_seconds,
    get_test_classes,
    DEFAULT_TIMEOUT_JAVA,
)
from framework.runner_utils import load_course_config, make_grade_result, parse_junit_xml, runner_main
from shared.results import TestResult

# Docker image name
DOCKER_IMAGE = "comp411-runner"

# Docker build timeout (seconds)
BUILD_TIMEOUT = 300
JUNIT_REPORT_DIR = "test-reports"


class JavaRunner:
    """Runs Java/JUnit tests in Docker container."""

    def __init__(self, comp411_dir: Path):
        self.comp411_dir = comp411_dir
        self.configs_dir = comp411_dir / "configs"
        self.in_container = is_in_container()

    def load_config(self, assignment_name: str) -> dict:
        return load_course_config(self.configs_dir, assignment_name)

    def ensure_docker_image(self, force_rebuild: bool = False) -> tuple:
        return ensure_docker_image(DOCKER_IMAGE, self.comp411_dir, BUILD_TIMEOUT, force_rebuild)

    def _build_test_script(
        self,
        ws_path: str,
        suite_dir: str,
        test_classes: list[str],
        compile_test_files: list[str],
        source_files: list[str],
        report_dir: str,
    ) -> str:
        """Build the javac + JUnit bash script using the given paths.

        Args:
            ws_path: Path to workspace (e.g. "/workspace" in Docker, actual path locally)
            suite_dir: Path to suite tests directory, or empty string if no override
            test_classes: Fully qualified test class names to run
            compile_test_files: Java files to compile for tests
            source_files: Configured source files (from YAML config)
        """
        class_args = " ".join(shlex.quote(name) for name in test_classes)
        compile_targets = " ".join(shlex.quote(path) for path in compile_test_files)
        configured_sources = [
            src for src in source_files
            if src and src.endswith(".java")
        ]
        configured_sources_args = " ".join(shlex.quote(src) for src in configured_sources)

        copy_suite_cmd = ""
        if suite_dir:
            copy_suite_cmd = f"cd {shlex.quote(suite_dir)}; find . -name '*.java' -exec cp --parents {{}} /tmp/bscs_ws/ \\; ; "

        return (
            "set -e; "
            f"rm -rf /tmp/bscs_ws && mkdir -p /tmp/bscs_ws && cp -a {shlex.quote(ws_path)}/. /tmp/bscs_ws; "
            f"{copy_suite_cmd}"
            "cd /tmp/bscs_ws; "
            f"rm -rf {shlex.quote(report_dir)} && mkdir -p {shlex.quote(report_dir)}; "
            f"configured_sources=\"{configured_sources_args}\"; "
            "if [ -n \"$configured_sources\" ]; then "
            "  javac -cp .:$JUNIT_CP $configured_sources "
            f"{compile_targets}; "
            "else "
            "  source_files=$(find . -maxdepth 1 -name '*.java' "
            "    ! -name '*Test.java' ! -name '*Tests.java' ! -name '*TestPrivate.java' ! -name '*IT.java' -print); "
            "  javac -cp .:$JUNIT_CP $source_files "
            f"{compile_targets}; "
            "fi; "
            "cat > BSCSJUnitXmlRunner.java <<'EOF'\n"
            "import org.junit.runner.Description;\n"
            "import org.junit.runner.JUnitCore;\n"
            "import org.junit.runner.Request;\n"
            "import org.junit.runner.Result;\n"
            "import org.junit.runner.notification.Failure;\n"
            "import org.junit.runner.notification.RunListener;\n"
            "import java.io.File;\n"
            "import java.io.PrintWriter;\n"
            "import java.io.StringWriter;\n"
            "import java.util.LinkedHashMap;\n"
            "import java.util.Map;\n"
            "\n"
            "public class BSCSJUnitXmlRunner {\n"
            "  private static class CaseResult {\n"
            "    String className;\n"
            "    String methodName;\n"
            "    String status = \"pass\";\n"
            "    String message = \"\";\n"
            "    String details = \"\";\n"
            "    long startNanos = 0L;\n"
            "    double timeSeconds = 0.0;\n"
            "  }\n"
            "\n"
            "  private static String key(Description description) {\n"
            "    return safeClassName(description) + \"#\" + safeMethodName(description);\n"
            "  }\n"
            "\n"
            "  private static String safeClassName(Description description) {\n"
            "    String className = description.getClassName();\n"
            "    return className == null || className.isEmpty() ? \"UnknownClass\" : className;\n"
            "  }\n"
            "\n"
            "  private static String safeMethodName(Description description) {\n"
            "    String methodName = description.getMethodName();\n"
            "    return methodName == null || methodName.isEmpty() ? \"__class_init__\" : methodName;\n"
            "  }\n"
            "\n"
            "  private static CaseResult ensure(Map<String, CaseResult> results, Description description) {\n"
            "    String key = key(description);\n"
            "    CaseResult value = results.get(key);\n"
            "    if (value == null) {\n"
            "      value = new CaseResult();\n"
            "      value.className = safeClassName(description);\n"
            "      value.methodName = safeMethodName(description);\n"
            "      results.put(key, value);\n"
            "    }\n"
            "    return value;\n"
            "  }\n"
            "\n"
            "  private static String escape(String value) {\n"
            "    if (value == null) return \"\";\n"
            "    return value.replace(\"&\", \"&amp;\")\n"
            "      .replace(\"<\", \"&lt;\")\n"
            "      .replace(\">\", \"&gt;\")\n"
            "      .replace(\"\\\"\", \"&quot;\")\n"
            "      .replace(\"'\", \"&apos;\");\n"
            "  }\n"
            "\n"
            "  public static void main(String[] args) throws Exception {\n"
            "    if (args.length < 2) {\n"
            "      System.err.println(\"Usage: BSCSJUnitXmlRunner <report-dir> <test-class>...\");\n"
            "      System.exit(2);\n"
            "    }\n"
            "\n"
            "    final Map<String, CaseResult> results = new LinkedHashMap<String, CaseResult>();\n"
            "    final JUnitCore core = new JUnitCore();\n"
            "    core.addListener(new RunListener() {\n"
            "      @Override\n"
            "      public void testStarted(Description description) {\n"
            "        CaseResult result = ensure(results, description);\n"
            "        result.startNanos = System.nanoTime();\n"
            "      }\n"
            "\n"
            "      @Override\n"
            "      public void testFinished(Description description) {\n"
            "        CaseResult result = ensure(results, description);\n"
            "        if (result.startNanos != 0L) {\n"
            "          result.timeSeconds = (System.nanoTime() - result.startNanos) / 1_000_000_000.0;\n"
            "        }\n"
            "      }\n"
            "\n"
            "      @Override\n"
            "      public void testFailure(Failure failure) {\n"
            "        CaseResult result = ensure(results, failure.getDescription());\n"
            "        result.status = \"fail\";\n"
            "        Throwable error = failure.getException();\n"
            "        result.message = error == null ? \"\" : String.valueOf(error.getMessage());\n"
            "        StringWriter sw = new StringWriter();\n"
            "        PrintWriter pw = new PrintWriter(sw);\n"
            "        if (error != null) {\n"
            "          error.printStackTrace(pw);\n"
            "        } else {\n"
            "          pw.println(failure.getTrace());\n"
            "        }\n"
            "        pw.flush();\n"
            "        result.details = sw.toString();\n"
            "      }\n"
            "\n"
            "      @Override\n"
            "      public void testIgnored(Description description) {\n"
            "        CaseResult result = ensure(results, description);\n"
            "        result.status = \"skip\";\n"
            "      }\n"
            "    });\n"
            "\n"
            "    boolean hasFailures = false;\n"
            "    for (int i = 1; i < args.length; i++) {\n"
            "      String className = args[i];\n"
            "      try {\n"
            "        Result result = core.run(Request.aClass(Class.forName(className)));\n"
            "        hasFailures = hasFailures || !result.wasSuccessful();\n"
            "      } catch (Throwable error) {\n"
            "        Description synthetic = Description.createTestDescription(className, \"__class_init__\");\n"
            "        CaseResult result = ensure(results, synthetic);\n"
            "        result.status = \"fail\";\n"
            "        result.message = String.valueOf(error.getMessage());\n"
            "        StringWriter sw = new StringWriter();\n"
            "        PrintWriter pw = new PrintWriter(sw);\n"
            "        error.printStackTrace(pw);\n"
            "        pw.flush();\n"
            "        result.details = sw.toString();\n"
            "        hasFailures = true;\n"
            "      }\n"
            "    }\n"
            "\n"
            "    int failures = 0;\n"
            "    int skipped = 0;\n"
            "    for (CaseResult result : results.values()) {\n"
            "      if (\"fail\".equals(result.status)) failures++;\n"
            "      if (\"skip\".equals(result.status)) skipped++;\n"
            "    }\n"
            "\n"
            "    File outFile = new File(args[0], \"TEST-bscs-suite.xml\");\n"
            "    PrintWriter out = new PrintWriter(outFile, \"UTF-8\");\n"
            "    out.println(\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\");\n"
            "    out.println(\"<testsuite name=\\\"bscs-suite\\\" tests=\\\"\" + results.size() + \"\\\" failures=\\\"\" + failures + \"\\\" errors=\\\"0\\\" skipped=\\\"\" + skipped + \"\\\">\");\n"
            "    for (CaseResult result : results.values()) {\n"
            "      out.print(\"  <testcase classname=\\\"\" + escape(result.className) + \"\\\" name=\\\"\" + escape(result.methodName) + \"\\\" time=\\\"\" + result.timeSeconds + \"\\\"\");\n"
            "      if (\"pass\".equals(result.status)) {\n"
            "        out.println(\" />\");\n"
            "      } else if (\"skip\".equals(result.status)) {\n"
            "        out.println(\"><skipped/></testcase>\");\n"
            "      } else {\n"
            "        out.println(\"><failure message=\\\"\" + escape(result.message) + \"\\\">\" + escape(result.details) + \"</failure></testcase>\");\n"
            "      }\n"
            "    }\n"
            "    out.println(\"</testsuite>\");\n"
            "    out.close();\n"
            "    System.exit(hasFailures ? 1 : 0);\n"
            "  }\n"
            "}\n"
            "EOF\n"
            "javac -cp .:$JUNIT_CP BSCSJUnitXmlRunner.java; "
            f"java -cp .:$JUNIT_CP BSCSJUnitXmlRunner {shlex.quote(report_dir)} {class_args} || true; "
            f"rm -rf {shlex.quote(ws_path)}/{shlex.quote(report_dir)}; "
            f"mkdir -p {shlex.quote(ws_path)}/{shlex.quote(report_dir)}; "
            f"cp -a {shlex.quote(report_dir)}/. {shlex.quote(ws_path)}/{shlex.quote(report_dir)}/"
        )

    def run_tests_in_docker(
        self,
        workspace: Path,
        test_classes: list[str],
        timeout: int,
        source_files: list[str] | None = None,
    ) -> tuple:
        """Run JUnit tests inside Docker container.

        Returns (success, stdout, stderr)
        """
        if not test_classes:
            return False, "", "No test classes specified"

        suite_override = os.environ.get("BSCS_TEST_SUITE_DIR", "").strip()
        classes_to_run = test_classes
        extra_volumes = None
        compile_test_files = [f"{name}.java" for name in test_classes]

        if suite_override:
            suite_path = Path(suite_override).resolve()
            suite_entries: list[tuple[str, str]] = []
            for test_file in suite_path.rglob("*.java"):
                stem = test_file.stem
                if not re.search(r"(?:Test|Tests|IT)(?:Private)?$", stem):
                    continue
                try:
                    content = test_file.read_text(encoding="utf-8", errors="ignore")
                except Exception:
                    content = ""
                package_match = re.search(r"^\s*package\s+([A-Za-z0-9_.]+)\s*;", content, re.MULTILINE)
                fqcn = f"{package_match.group(1)}.{stem}" if package_match else stem
                rel_path = test_file.relative_to(suite_path).as_posix()
                suite_entries.append((fqcn, rel_path))

            if not suite_entries:
                return False, "", "No suite test classes found"
            suite_classes = sorted({class_name for class_name, _ in suite_entries})
            suite_compile_files = sorted({rel_path for _, rel_path in suite_entries})
            classes_to_run = sorted(set(test_classes) | set(suite_classes))
            compile_test_files = sorted(set(compile_test_files) | set(suite_compile_files))
            extra_volumes = {str(suite_path): "/suite_tests:ro"}

        safe_source_files = source_files or []
        report_dir = JUNIT_REPORT_DIR

        if self.in_container:
            # Already inside a pre-provisioned benchmark container — build the script
            # with actual local paths from the start.
            script = self._build_test_script(
                ws_path=str(workspace.resolve()),
                suite_dir=suite_override if suite_override else "",
                test_classes=classes_to_run,
                compile_test_files=compile_test_files,
                source_files=safe_source_files,
                report_dir=report_dir,
            )
            returncode, stdout, stderr = run_local(
                ["bash", "-c", script], timeout=timeout,
            )
            return returncode == 0, stdout, stderr

        # Docker path — use container-internal paths
        script = self._build_test_script(
            ws_path="/workspace",
            suite_dir="/suite_tests" if suite_override else "",
            test_classes=classes_to_run,
            compile_test_files=compile_test_files,
            source_files=safe_source_files,
            report_dir=report_dir,
        )

        returncode, stdout, stderr = run_bash_in_container(
            image=DOCKER_IMAGE,
            script=script,
            workspace=workspace,
            timeout=timeout,
            memory="1g",
            cpus="2",
            network="none",
            extra_volumes=extra_volumes,
            override_entrypoint=True,
        )

        return returncode == 0, stdout, stderr

    def parse_junit_output(self, stdout: str, stderr: str) -> tuple:
        """Parse JUnit console output to extract test results.

        Returns (test_results, compilation_error)
        """
        combined = stdout + "\n" + stderr
        results = []
        compilation_error = None

        error_patterns = [
            r"\.java:\d+: error:",
            r"cannot find symbol",
            r"class .* is public, should be declared",
            r"incompatible types",
            r"java\.lang\.NoClassDefFoundError",
            r"java\.lang\.ClassNotFoundException",
            r"Exception in thread .* Error",
        ]

        for pattern in error_patterns:
            if re.search(pattern, combined):
                compilation_error = combined
                return results, compilation_error

        ok_match = re.search(r"OK \((\d+) tests?\)", combined)
        if ok_match:
            test_count = int(ok_match.group(1))
            for i in range(test_count):
                results.append(TestResult(
                    name=f"test_{i+1}",
                    passed=True,
                    points=1.0,
                    max_points=1.0
                ))
            return results, None

        summary_match = re.search(
            r"Tests run:\s*(\d+),\s*Failures:\s*(\d+)(?:,\s*Errors:\s*(\d+))?",
            combined
        )

        if summary_match:
            total = int(summary_match.group(1))
            failures = int(summary_match.group(2))
            errors = int(summary_match.group(3)) if summary_match.group(3) else 0
            passed = total - failures - errors

            failure_details = self._extract_failure_details(combined)

            for name, error in failure_details.items():
                results.append(TestResult(
                    name=name,
                    passed=False,
                    points=0.0,
                    max_points=1.0,
                    error_message=error
                ))

            for i in range(passed):
                results.append(TestResult(
                    name=f"test_passed_{i+1}",
                    passed=True,
                    points=1.0,
                    max_points=1.0
                ))

            return results, None

        if "Exception" in combined or "Error" in combined:
            compilation_error = combined

        return results, compilation_error

    def _extract_failure_details(self, output: str) -> dict:
        """Extract failure/error details from JUnit output."""
        details = {}
        pattern = r'\d+\)\s+(\w+)\((\w+)\)\n(.*?)(?=\n\d+\)|\nFAILURES|\nTests run:|$)'
        for match in re.finditer(pattern, output, re.DOTALL):
            method, class_name, error = match.groups()
            test_name = f"{class_name}.{method}"
            details[test_name] = error.strip()[:500]
        return details

    def run_tests(self, assignment_name: str, workspace_path: Path,
                  verbose: bool = False, force_rebuild: bool = False) -> 'GradeResult':
        """Run all tests for an assignment."""
        config = self.load_config(assignment_name)
        result = make_grade_result(config, "comp411", assignment_name)

        success, error = self.ensure_docker_image(force_rebuild)
        if not success:
            result.compilation_error = error
            return result

        test_classes: list[str] = []
        source_files: list[str] = []
        tests_section = config.get("tests", {})
        if isinstance(tests_section, dict):
            if "class" in tests_section:
                test_classes.append(tests_section["class"])
            if "classes" in tests_section:
                test_classes.extend(tests_section["classes"])
            if "additional_classes" in tests_section:
                test_classes.extend(tests_section["additional_classes"])
        source_entries = config.get("source_files", [])
        if isinstance(source_entries, list):
            for entry in source_entries:
                if isinstance(entry, dict):
                    path = entry.get("path")
                    if isinstance(path, str):
                        source_files.append(path)

        if not test_classes:
            test_classes = get_test_classes(config)

        if not test_classes:
            test_classes = [f"Assign{get_assignment_number(config) or 1}Test"]
        timeout = get_timeout_seconds(config, DEFAULT_TIMEOUT_JAVA)

        success, stdout, stderr = self.run_tests_in_docker(
            workspace_path,
            test_classes,
            timeout,
            source_files,
        )

        if verbose:
            print("=" * 40, file=sys.stderr)
            print("STDOUT:", file=sys.stderr)
            print(stdout, file=sys.stderr)
            print("STDERR:", file=sys.stderr)
            print(stderr, file=sys.stderr)
            print("=" * 40, file=sys.stderr)

        test_results = parse_junit_xml(workspace_path / JUNIT_REPORT_DIR)
        compilation_error = None
        if not test_results:
            test_results, compilation_error = self.parse_junit_output(stdout, stderr)
        result.test_results = test_results
        result.compilation_error = compilation_error

        return result


def main():
    runner_main(
        description="COMP 411 Java Autograder (Docker)",
        course_code="comp411",
        runner_factory=JavaRunner,
    )


if __name__ == "__main__":
    main()
