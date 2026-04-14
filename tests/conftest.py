"""
Shared fixtures for bin/grade integration tests.

Provides:
- grade_module: loads bin/grade as an importable Python module (session-scoped)
- make_workspace: factory that creates a temp workspace with git, tests/public, tests/agent
- make_private_root: factory that creates a BSCS_PRIVATE_TESTS_ROOT directory tree
"""

import json
import os
import shutil
import subprocess
from pathlib import Path

import pytest
import yaml

PROJECT_ROOT = Path(__file__).resolve().parent.parent


# ---------------------------------------------------------------------------
# grade_module -- load bin/grade as a Python module
# ---------------------------------------------------------------------------

@pytest.fixture(scope="session")
def grade_module():
    """Return the framework.test_suites module for suite-composition tests.

    Previously loaded bin/grade as a Python module, but the helpers under test
    (_assemble_suite_for_run_type, _copy_tree, _resolve_private_dir, etc.)
    live in framework.test_suites. Returning that module directly avoids
    re-exporting private names through bin/grade.
    """
    import framework.test_suites as ts
    return ts


# ---------------------------------------------------------------------------
# make_workspace -- factory fixture
# ---------------------------------------------------------------------------

# Env vars used so git does not need a global user.name / user.email.
_GIT_ENV = {
    "GIT_AUTHOR_NAME": "Test",
    "GIT_AUTHOR_EMAIL": "test@test.local",
    "GIT_COMMITTER_NAME": "Test",
    "GIT_COMMITTER_EMAIL": "test@test.local",
}


def _git(cwd, *args):
    """Run a git command inside *cwd* with deterministic author info."""
    env = {**os.environ, **_GIT_ENV}
    subprocess.check_call(["git"] + list(args), cwd=cwd, env=env,
                          stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)


@pytest.fixture()
def make_workspace(tmp_path_factory):
    """Factory that creates a temporary workspace directory.

    Parameters (passed as kwargs to the returned callable):
        course (str): course name, default "comp140"
        module_number (int): assignment number, default 1
        module_name (str): assignment name, default "circles"
        public_tests (dict[str, str]): filename -> content for tests/public/
        agent_tests (dict[str, str]): filename -> content for tests/agent/
        modify_public_after_commit (bool): if True, rewrite every public test
            file with "TAMPERED" content after the initial commit and make a
            second commit.  Simulates agent tampering.
        init_git (bool): whether to initialise a git repo (default True)

    Returns: Path to the workspace root.
    """
    def _factory(
        *,
        course: str = "comp140",
        module_number: int = 1,
        module_name: str = "circles",
        public_tests: dict[str, str] | None = None,
        agent_tests: dict[str, str] | None = None,
        modify_public_after_commit: bool = False,
        init_git: bool = True,
    ) -> Path:
        ws = tmp_path_factory.mktemp("workspace")

        # workspace.yaml
        ws_yaml = {
            "assignment_number": module_number,
            "assignment_name": module_name,
            "display_name": module_name.replace("_", " ").title(),
            "course": course,
            "language": "python",
        }
        (ws / "workspace.yaml").write_text(yaml.dump(ws_yaml))
        (ws / "workspace.json").write_text(json.dumps(ws_yaml))

        # tests/public/
        if public_tests:
            pub_dir = ws / "tests" / "public"
            pub_dir.mkdir(parents=True, exist_ok=True)
            for name, content in public_tests.items():
                (pub_dir / name).write_text(content)

        # tests/agent/
        if agent_tests:
            agent_dir = ws / "tests" / "agent"
            agent_dir.mkdir(parents=True, exist_ok=True)
            for name, content in agent_tests.items():
                (agent_dir / name).write_text(content)

        # Initialise git repo with initial commit
        if init_git:
            _git(ws, "init")
            _git(ws, "add", "-A")
            _git(ws, "commit", "-m", "Initial starter code")

            if modify_public_after_commit:
                pub_dir = ws / "tests" / "public"
                if pub_dir.exists():
                    for f in pub_dir.iterdir():
                        if f.is_file():
                            f.write_text("TAMPERED")
                    _git(ws, "add", "-A")
                    _git(ws, "commit", "-m", "Agent tampered with public tests")

        return ws

    return _factory


# ---------------------------------------------------------------------------
# make_private_root -- factory fixture
# ---------------------------------------------------------------------------

@pytest.fixture()
def make_private_root(tmp_path_factory, monkeypatch):
    """Factory that creates a BSCS_PRIVATE_TESTS_ROOT directory tree.

    Parameters (passed as kwargs to the returned callable):
        course (str): course name, e.g. "comp140"
        module_name (str): module directory name, e.g. "circles"
        test_files (dict[str, str]): filename -> content

    Sets BSCS_PRIVATE_TESTS_ROOT env var.
    Returns: the root path (the value of the env var).
    """

    def _factory(
        *,
        course: str = "comp140",
        module_name: str = "circles",
        test_files: dict[str, str] | None = None,
    ) -> Path:
        root = tmp_path_factory.mktemp("private_root")
        mod_dir = root / course / module_name
        mod_dir.mkdir(parents=True, exist_ok=True)
        if test_files:
            for name, content in test_files.items():
                (mod_dir / name).write_text(content)
        monkeypatch.setenv("BSCS_PRIVATE_TESTS_ROOT", str(root))
        return root

    return _factory
