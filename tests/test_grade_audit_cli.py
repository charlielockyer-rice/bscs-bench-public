"""End-to-end tests for `bin/grade --audit-suite`."""

import json
import os
import subprocess
import sys
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parent.parent
GRADE_BIN = PROJECT_ROOT / "bin" / "grade"
GIT_ENV = {
    "GIT_AUTHOR_NAME": "Test",
    "GIT_AUTHOR_EMAIL": "test@test.local",
    "GIT_COMMITTER_NAME": "Test",
    "GIT_COMMITTER_EMAIL": "test@test.local",
}


def _git(cwd: Path, *args: str) -> None:
    env = {**os.environ, **GIT_ENV}
    subprocess.check_call(
        ["git"] + list(args),
        cwd=cwd,
        env=env,
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
    )


def _write_workspace(workspace: Path, *, course: str, module_number: int, module_name: str) -> None:
    workspace.mkdir(parents=True, exist_ok=True)
    workspace_yaml = (
        f"assignment_number: {module_number}\n"
        f'assignment_name: "{module_name}"\n'
        f'display_name: "{module_name.title()}"\n'
        f'course: "{course}"\n'
        'language: "python"\n'
    )
    (workspace / "workspace.yaml").write_text(workspace_yaml)


def test_new_format_audit_suite_reports_effective_inventory(tmp_path):
    root = tmp_path
    course_name = "comp999"
    module_name = "circles"
    course_dir = root / course_name / "configs"
    course_dir.mkdir(parents=True)
    (course_dir / "module1.yaml").write_text(
        "assignment_number: 1\n"
        "assignment_name: circles\n"
        "language: python\n"
        "grading:\n"
        "  requires_private_tests: false\n"
    )

    workspace = root / "workspaces" / "audit_ws"
    _write_workspace(workspace, course=course_name, module_number=1, module_name=module_name)
    public_dir = workspace / "tests" / "public"
    public_dir.mkdir(parents=True)
    (public_dir / "test_public.py").write_text("# public")
    agent_dir = workspace / "tests" / "agent"
    agent_dir.mkdir(parents=True)
    (agent_dir / "test_agent.py").write_text("# agent")

    result = subprocess.run(
        [sys.executable, str(GRADE_BIN), str(workspace), "--run-type", "test", "--audit-suite", "--json"],
        cwd=PROJECT_ROOT,
        capture_output=True,
        text=True,
    )

    assert result.returncode == 0, result.stderr
    payload = json.loads(result.stdout)
    assert payload["isValid"] is True
    assert payload["requiresPrivateTests"] is False
    assert payload["requiresPrivateTestsReason"] == "grading"
    assert payload["public"]["files"] == ["test_public.py"]
    assert payload["agent"]["files"] == ["test_agent.py"]
    assert payload["effectiveTestFiles"] == ["test_agent.py", "test_public.py"]


def test_legacy_audit_suite_fails_when_required_private_tests_are_missing(tmp_path):
    root = tmp_path
    course_dir = root / "comp999"
    configs_dir = course_dir / "configs"
    configs_dir.mkdir(parents=True)
    (configs_dir / "module1.yaml").write_text(
        "assignment_number: 1\n"
        "assignment_name: circles\n"
        "language: python\n"
        "grading:\n"
        "  requires_private_tests: true\n"
    )

    workspace = root / "workspace"
    _write_workspace(workspace, course="comp999", module_number=1, module_name="circles")
    public_dir = workspace / "tests" / "public"
    public_dir.mkdir(parents=True)
    (public_dir / "test_public.py").write_text("# public")
    _git(workspace, "init")
    _git(workspace, "add", "-A")
    _git(workspace, "commit", "-m", "Initial starter code")

    result = subprocess.run(
        [
            sys.executable,
            str(GRADE_BIN),
            "1",
            str(workspace),
            "--course",
            str(course_dir),
            "--run-type",
            "submit",
            "--audit-suite",
            "--json",
        ],
        cwd=PROJECT_ROOT,
        capture_output=True,
        text=True,
    )

    payload = json.loads(result.stdout)
    assert result.returncode == 1
    assert payload["isValid"] is False
    assert payload["requiresPrivateTests"] is True
    assert payload["requiresPrivateTestsReason"] == "grading"
    assert payload["public"]["files"] == ["test_public.py"]
    assert payload["private"]["source"] is None
    assert "requires private tests" in (payload["validationMessage"] or "")
