"""
Integration tests for the test-suite composition logic in bin/grade.

Exercises _assemble_suite_for_run_type and its helpers (_copy_tree,
_resolve_private_dir, _extract_public_from_initial_commit, etc.).

Organised into five groups:
  A  "test" run-type   (public + agent)
  B  "submit" run-type (baseline public + private)
  C  Conflict detection
  D  Private test visibility / resolution
  E  Helper functions
"""

import json
import shutil
import subprocess
from pathlib import Path

import framework.test_suites as test_suites


PROJECT_ROOT = Path(__file__).resolve().parent.parent


# ===================================================================
# Group A: "test" run-type (public + agent)
# ===================================================================

class TestRunTypeTest:
    """Tests for run_type='test', which merges public + agent tests."""

    def test_test_mode_public_only(self, grade_module, make_workspace):
        """When only public tests exist, suite_dir is a materialized temp dir
        with a manifest."""
        ws = make_workspace(
            public_tests={"test_one.py": "# public test 1"},
        )
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "test", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            assert suite_dir is not None
            assert suite_dir != ws / "tests" / "public"
            assert len(temp_dirs) == 1
            assert (suite_dir / "test_one.py").read_text() == "# public test 1"
            assert (suite_dir / ".bscs_suite_manifest.json").exists()
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)

    def test_test_mode_agent_only(self, grade_module, make_workspace):
        """When only agent tests exist, a merged temp dir is created containing
        just the agent files."""
        ws = make_workspace(
            agent_tests={"test_agent.py": "# agent test"},
        )
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "test", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            assert suite_dir is not None
            assert len(temp_dirs) == 1
            # The agent file should be present in the merged dir.
            assert (suite_dir / "test_agent.py").read_text() == "# agent test"
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)

    def test_test_mode_public_and_agent_merged(self, grade_module, make_workspace):
        """When both public and agent tests exist, suite_dir is a merged temp
        dir containing files from both sources."""
        ws = make_workspace(
            public_tests={"test_pub.py": "# public"},
            agent_tests={"test_agt.py": "# agent"},
        )
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "test", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            assert suite_dir is not None
            assert len(temp_dirs) == 1
            assert (suite_dir / "test_pub.py").read_text() == "# public"
            assert (suite_dir / "test_agt.py").read_text() == "# agent"
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)

    def test_test_mode_no_tests(self, grade_module, make_workspace):
        """When neither public nor agent tests exist, suite_dir is None."""
        ws = make_workspace()  # no public_tests, no agent_tests
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "test", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            assert suite_dir is None
            assert len(temp_dirs) == 0
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)

    def test_test_mode_agent_files_in_metadata(self, grade_module, make_workspace):
        """metadata['agentTestFiles'] should list all agent test file names."""
        ws = make_workspace(
            agent_tests={
                "test_a.py": "# a",
                "test_b.py": "# b",
            },
        )
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "test", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            assert sorted(meta["agentTestFiles"]) == ["test_a.py", "test_b.py"]
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)


# ===================================================================
# Group B: "submit" run-type (baseline public + private)
# ===================================================================

class TestRunTypeSubmit:
    """Tests for run_type='submit', which merges baseline public (from git)
    with private tests.  Agent tests are excluded."""

    def test_submit_mode_baseline_from_git(
        self, grade_module, make_workspace, monkeypatch,
    ):
        """Submit mode extracts the original public tests from the initial git
        commit, not the tampered version from the working tree."""
        monkeypatch.delenv("BSCS_PRIVATE_TESTS_ROOT", raising=False)
        ws = make_workspace(
            public_tests={"test_pub.py": "ORIGINAL"},
            modify_public_after_commit=True,
        )
        # Working-tree copy should say TAMPERED.
        assert (ws / "tests" / "public" / "test_pub.py").read_text() == "TAMPERED"

        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "submit", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            assert suite_dir is not None
            # The suite should contain the ORIGINAL content from the initial commit.
            assert (suite_dir / "test_pub.py").read_text() == "ORIGINAL"
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)

    def test_submit_mode_public_and_private_merged(
        self, grade_module, make_workspace, make_private_root,
    ):
        """Submit suite merges baseline public tests with private tests."""
        ws = make_workspace(
            public_tests={"test_pub.py": "# public"},
        )
        make_private_root(
            course="comp140",
            module_name="circles",
            test_files={"test_priv.py": "# private"},
        )
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "submit", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            assert suite_dir is not None
            assert (suite_dir / "test_pub.py").read_text() == "# public"
            assert (suite_dir / "test_priv.py").read_text() == "# private"
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)

    def test_submit_mode_private_only(
        self, grade_module, make_workspace, make_private_root,
    ):
        """When there are no public tests at all (not even in git), only
        private tests appear in the suite."""
        ws = make_workspace()  # no public tests
        make_private_root(
            course="comp140",
            module_name="circles",
            test_files={"test_priv.py": "# private only"},
        )
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "submit", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            assert suite_dir is not None
            assert (suite_dir / "test_priv.py").read_text() == "# private only"
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)

    def test_submit_mode_no_tests(
        self, grade_module, make_workspace, monkeypatch,
    ):
        """When neither public baseline nor private tests exist, suite_dir is None."""
        monkeypatch.delenv("BSCS_PRIVATE_TESTS_ROOT", raising=False)
        ws = make_workspace()  # no public, no agent, no private
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "submit", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            assert suite_dir is None
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)

    def test_submit_mode_agent_excluded(
        self, grade_module, make_workspace, monkeypatch,
    ):
        """Agent test files must NOT appear in the submit suite.  This is a
        critical security property: agents could write malicious tests that
        always pass."""
        monkeypatch.delenv("BSCS_PRIVATE_TESTS_ROOT", raising=False)
        ws = make_workspace(
            public_tests={"test_pub.py": "# public"},
            agent_tests={"test_evil.py": "# should not appear"},
        )
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "submit", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            assert suite_dir is not None
            # Agent file must be absent from the suite.
            assert not (suite_dir / "test_evil.py").exists()
            # Public file should still be present.
            assert (suite_dir / "test_pub.py").exists()
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)

    def test_submit_mode_tampered_and_agent_excluded(
        self, grade_module, make_workspace, monkeypatch,
    ):
        """Submit mode uses baseline public from git (not tampered) and
        excludes agent tests -- even when both are present."""
        monkeypatch.delenv("BSCS_PRIVATE_TESTS_ROOT", raising=False)
        ws = make_workspace(
            public_tests={"test_pub.py": "ORIGINAL"},
            agent_tests={"test_evil.py": "# should not appear"},
            modify_public_after_commit=True,
        )
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "submit", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            assert suite_dir is not None
            assert (suite_dir / "test_pub.py").read_text() == "ORIGINAL"
            assert not (suite_dir / "test_evil.py").exists()
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)

    def test_submit_mode_rejects_mutable_public_without_override(
        self, grade_module, make_workspace, monkeypatch,
    ):
        """When immutable baseline public tests cannot be reconstructed,
        submit validation must reject mutable workspace public tests."""
        monkeypatch.delenv("BSCS_PRIVATE_TESTS_ROOT", raising=False)
        monkeypatch.delenv("BSCS_ALLOW_MUTATED_PUBLIC_SUBMIT", raising=False)
        ws = make_workspace(
            public_tests={"test_pub.py": "# fallback content"},
            init_git=False,
        )
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "submit", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            assert suite_dir is not None
            assert (suite_dir / "test_pub.py").read_text() == "# fallback content"
            assert meta["missingBaselinePublic"] is True
            ok, msg = grade_module._validate_submit_private_suite("submit", meta)
            assert ok is False
            assert msg is not None
            assert "could not reconstruct baseline public tests" in msg
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)

    def test_submit_mode_allows_mutable_public_with_override(
        self, grade_module, make_workspace, monkeypatch,
    ):
        """The mutable-public fallback remains available only behind an explicit override."""
        monkeypatch.delenv("BSCS_PRIVATE_TESTS_ROOT", raising=False)
        monkeypatch.setenv("BSCS_ALLOW_MUTATED_PUBLIC_SUBMIT", "1")
        ws = make_workspace(
            public_tests={"test_pub.py": "# fallback content"},
            init_git=False,
        )
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "submit", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            assert suite_dir is not None
            ok, msg = grade_module._validate_submit_private_suite("submit", meta)
            assert ok is True
            assert msg is not None
            assert "BSCS_ALLOW_MUTATED_PUBLIC_SUBMIT=1" in msg
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)


# ===================================================================
# Group C: Conflict detection
# ===================================================================

class TestConflictDetection:
    """Tests that name collisions between test sources are surfaced."""

    def test_agent_conflict_with_public(self, grade_module, make_workspace):
        """In test mode, collisions are recorded and later validation rejects
        the suite."""
        ws = make_workspace(
            public_tests={"test_overlap.py": "PUBLIC VERSION"},
            agent_tests={"test_overlap.py": "AGENT VERSION"},
        )
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "test", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            assert suite_dir is not None
            assert (suite_dir / "test_overlap.py").read_text() == "PUBLIC VERSION"
            assert "test_overlap.py" in meta["agentConflicts"]
            ok, msg = grade_module._validate_submit_private_suite("test", meta)
            assert ok is False
            assert msg is not None
            assert "agent:test_overlap.py" in msg
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)

    def test_private_conflict_with_public(
        self, grade_module, make_workspace, make_private_root,
    ):
        """In submit mode, collisions are recorded and later validation rejects
        the suite."""
        ws = make_workspace(
            public_tests={"test_overlap.py": "PUBLIC BASELINE"},
        )
        make_private_root(
            course="comp140",
            module_name="circles",
            test_files={"test_overlap.py": "PRIVATE VERSION"},
        )
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "submit", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            assert suite_dir is not None
            assert (suite_dir / "test_overlap.py").read_text() == "PUBLIC BASELINE"
            assert "test_overlap.py" in meta["privateConflicts"]
            ok, msg = grade_module._validate_submit_private_suite("submit", meta)
            assert ok is False
            assert msg is not None
            assert "private:test_overlap.py" in msg
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)


# ===================================================================
# Group D: Private test visibility / resolution
# ===================================================================

class TestPrivateResolution:
    """Tests for _resolve_private_dir and related path logic."""

    def test_private_outside_workspace(
        self, grade_module, make_workspace, make_private_root,
    ):
        """The resolved private dir must be outside the workspace directory."""
        ws = make_workspace(
            public_tests={"test_pub.py": "# pub"},
        )
        priv_root = make_private_root(
            course="comp140",
            module_name="circles",
            test_files={"test_priv.py": "# priv"},
        )
        private_dir = grade_module._resolve_private_dir(
            PROJECT_ROOT, "comp140", 1, "circles",
        )
        assert private_dir is not None
        # The private dir should NOT be inside the workspace tree.
        assert not str(private_dir).startswith(str(ws))

    def test_resolve_by_module_name(
        self, grade_module, tmp_path_factory, monkeypatch,
    ):
        """_resolve_private_dir finds <root>/<course>/<module_name>."""
        root = tmp_path_factory.mktemp("priv")
        target = root / "comp140" / "circles"
        target.mkdir(parents=True)
        (target / "test.py").write_text("# resolved by name")
        monkeypatch.setenv("BSCS_PRIVATE_TESTS_ROOT", str(root))

        result = grade_module._resolve_private_dir(
            PROJECT_ROOT, "comp140", 1, "circles",
        )
        assert result == target

    def test_resolve_by_module_number(
        self, grade_module, tmp_path_factory, monkeypatch,
    ):
        """Fallback: _resolve_private_dir finds <root>/<course>/module<N>."""
        root = tmp_path_factory.mktemp("priv")
        target = root / "comp140" / "module1"
        target.mkdir(parents=True)
        (target / "test.py").write_text("# resolved by number")
        monkeypatch.setenv("BSCS_PRIVATE_TESTS_ROOT", str(root))

        # module_name=None so the name-based lookup is skipped.
        result = grade_module._resolve_private_dir(
            PROJECT_ROOT, "comp140", 1, None,
        )
        assert result == target

    def test_resolve_legacy_by_assignment_name_from_config(
        self, grade_module, tmp_path_factory, monkeypatch,
    ):
        """Legacy callers without module_name still resolve canonical dirs from course config."""
        root = tmp_path_factory.mktemp("priv")
        target = root / "comp322" / "hw1"
        target.mkdir(parents=True)
        (target / "test.py").write_text("# resolved by assignment name")
        monkeypatch.setenv("BSCS_PRIVATE_TESTS_ROOT", str(root))

        result = grade_module._resolve_private_dir(
            PROJECT_ROOT, "comp322", 1, None,
        )
        assert result == target

    def test_resolve_legacy_by_homework_name_from_config(
        self, grade_module, tmp_path_factory, monkeypatch,
    ):
        """Legacy callers resolve homework-style canonical dirs after alias cleanup."""
        root = tmp_path_factory.mktemp("priv")
        target = root / "comp215" / "homework1"
        target.mkdir(parents=True)
        (target / "test.py").write_text("# resolved by homework name")
        monkeypatch.setenv("BSCS_PRIVATE_TESTS_ROOT", str(root))

        result = grade_module._resolve_private_dir(
            PROJECT_ROOT, "comp215", 1, None,
        )
        assert result == target

    def test_resolve_no_env_var(self, grade_module, monkeypatch):
        """Returns None when BSCS_PRIVATE_TESTS_ROOT is not set."""
        monkeypatch.delenv("BSCS_PRIVATE_TESTS_ROOT", raising=False)
        result = grade_module._resolve_private_dir(
            PROJECT_ROOT, "comp140", 1, "circles",
        )
        assert result is None


# ===================================================================
# Group E: Helper functions
# ===================================================================

class TestHelpers:
    """Tests for _copy_tree and temp dir tracking."""

    def test_copy_tree_basic(self, grade_module, tmp_path):
        """_copy_tree recursively copies files from src to dst."""
        src = tmp_path / "src"
        src.mkdir()
        (src / "a.py").write_text("alpha")
        sub = src / "sub"
        sub.mkdir()
        (sub / "b.py").write_text("beta")

        dst = tmp_path / "dst"
        dst.mkdir()
        grade_module._copy_tree(src, dst)

        assert (dst / "a.py").read_text() == "alpha"
        assert (dst / "sub" / "b.py").read_text() == "beta"

    def test_copy_tree_overwrite_default(self, grade_module, tmp_path):
        """Default overwrite=True replaces existing files."""
        src = tmp_path / "src"
        src.mkdir()
        (src / "f.py").write_text("UPDATED")

        dst = tmp_path / "dst"
        dst.mkdir()
        (dst / "f.py").write_text("STALE")

        grade_module._copy_tree(src, dst)
        assert (dst / "f.py").read_text() == "UPDATED"

    def test_copy_tree_no_overwrite(self, grade_module, tmp_path):
        """With overwrite=False, existing files are preserved and conflicts
        are recorded."""
        src = tmp_path / "src"
        src.mkdir()
        (src / "keep.py").write_text("NEW")

        dst = tmp_path / "dst"
        dst.mkdir()
        (dst / "keep.py").write_text("ORIGINAL")

        conflicts: list[str] = []
        grade_module._copy_tree(src, dst, overwrite=False, conflicts=conflicts)

        assert (dst / "keep.py").read_text() == "ORIGINAL"
        assert "keep.py" in conflicts

    def test_copy_tree_no_overwrite_subdir(self, grade_module, tmp_path):
        """Conflicts in subdirectories are recorded with POSIX relative paths."""
        src = tmp_path / "src" / "sub"
        src.mkdir(parents=True)
        (src / "deep.py").write_text("NEW")

        dst = tmp_path / "dst" / "sub"
        dst.mkdir(parents=True)
        (dst / "deep.py").write_text("ORIGINAL")

        conflicts: list[str] = []
        grade_module._copy_tree(tmp_path / "src", tmp_path / "dst", overwrite=False, conflicts=conflicts)

        assert (tmp_path / "dst" / "sub" / "deep.py").read_text() == "ORIGINAL"
        assert "sub/deep.py" in conflicts

    def test_temp_dirs_cleanup(
        self, grade_module, make_workspace, make_private_root,
    ):
        """The temp_dirs list returned by _assemble_suite_for_run_type contains
        every temporary directory that was created, so callers can clean up."""
        ws = make_workspace(
            public_tests={"test_pub.py": "# pub"},
        )
        make_private_root(
            course="comp140",
            module_name="circles",
            test_files={"test_priv.py": "# priv"},
        )
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "submit", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            # submit with both public and private creates:
            #   1. a baseline extraction temp dir (from git)
            #   2. a merged temp dir
            assert len(temp_dirs) >= 1
            for td in temp_dirs:
                assert td.exists(), f"temp dir {td} should exist before cleanup"

            # All temp dirs should be actual directories.
            for td in temp_dirs:
                assert td.is_dir()
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)

            # After cleanup they should be gone.
            for d in temp_dirs:
                assert not d.exists(), f"temp dir {d} should be gone after cleanup"

    def test_suite_manifest_contains_sources(
        self, grade_module, make_workspace, make_private_root,
    ):
        """Materialized suites write a manifest describing file sources."""
        ws = make_workspace(
            public_tests={"test_pub.py": "# pub"},
            agent_tests={"test_agent.py": "# agent"},
        )
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "test", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            manifest_path = Path(meta["manifestPath"])
            manifest = json.loads(manifest_path.read_text())
            assert manifest["run_type"] == "test"
            entries = {entry["relative_path"]: entry["source"] for entry in manifest["entries"]}
            assert entries["test_pub.py"] == "public"
            assert entries["test_agent.py"] == "agent"
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)

    def test_build_suite_audit_payload_includes_effective_inventory(
        self, grade_module, make_workspace,
    ):
        ws = make_workspace(
            public_tests={"test_pub.py": "# pub"},
            agent_tests={"test_agent.py": "# agent"},
        )
        suite_dir, temp_dirs, meta = grade_module._assemble_suite_for_run_type(
            "test", ws, PROJECT_ROOT, "comp140", 1, "circles",
        )
        try:
            payload = grade_module.build_suite_audit_payload(
                ws,
                suite_dir,
                meta,
                is_valid=True,
                validation_message=None,
            )
            assert payload["isValid"] is True
            assert payload["public"]["files"] == ["test_pub.py"]
            assert payload["agent"]["files"] == ["test_agent.py"]
            assert payload["effectiveTestFiles"] == ["test_agent.py", "test_pub.py"]
            assert payload["manifest"]["run_type"] == "test"
        finally:
            for d in temp_dirs:
                shutil.rmtree(d, ignore_errors=True)

    def test_extract_public_returns_none_when_git_show_writes_no_files(self, grade_module, tmp_path, monkeypatch):
        """Fallback per-file extraction returns None when git show fails for every file.

        Note: This exercises the fallback path (per-file git show), not the
        primary git archive | tar path, because Popen/run are not patched."""
        workspace = tmp_path / "workspace"
        workspace.mkdir()
        extracted_dir = tmp_path / "extracted"

        def fake_check_output(args, **kwargs):
            if args[:3] == ["git", "rev-list", "--max-parents=0"]:
                return "abc123\n"
            if args[:4] == ["git", "ls-tree", "-r", "--name-only"]:
                return "tests/public/test_pub.py\n"
            if args[:2] == ["git", "show"]:
                raise subprocess.CalledProcessError(returncode=1, cmd=args)
            raise AssertionError(f"Unexpected git command: {args}")

        monkeypatch.setattr(test_suites.subprocess, "check_output", fake_check_output)
        monkeypatch.setattr(test_suites.tempfile, "mkdtemp", lambda prefix: str(extracted_dir))

        result = grade_module._extract_public_from_initial_commit(workspace)

        assert result is None
        assert not extracted_dir.exists()


class TestSubmitPrivateValidation:
    """Tests for submit-mode private suite integrity validation."""

    def test_submit_without_private_suite_succeeds_with_note(self, grade_module):
        """Submit mode should proceed when no private suite is available, with a note."""
        ok, msg = grade_module._validate_submit_private_suite("submit", {"privateSource": None})
        assert ok is True
        assert msg is not None
        assert "no private tests" in msg

    def test_submit_requires_private_suite_when_expected(self, grade_module):
        """Assignments marked as requiring private tests must fail without them."""
        ok, msg = grade_module._validate_submit_private_suite(
            "submit", {"privateSource": None, "requiresPrivateTests": True}
        )
        assert ok is False
        assert msg is not None
        assert "requires private tests" in msg

    def test_submit_rejects_mutable_public_without_override(self, grade_module, monkeypatch):
        """Submit mode should reject mutable workspace public tests by default."""
        monkeypatch.delenv("BSCS_ALLOW_MUTATED_PUBLIC_SUBMIT", raising=False)
        ok, msg = grade_module._validate_submit_private_suite(
            "submit", {"privateSource": "/tmp/private-tests", "missingBaselinePublic": True}
        )
        assert ok is False
        assert msg is not None
        assert "could not reconstruct baseline public tests" in msg

    def test_submit_allows_mutable_public_with_override(self, grade_module, monkeypatch):
        """BSCS_ALLOW_MUTATED_PUBLIC_SUBMIT=1 permits the explicit mutable-public fallback."""
        monkeypatch.setenv("BSCS_ALLOW_MUTATED_PUBLIC_SUBMIT", "1")
        ok, msg = grade_module._validate_submit_private_suite(
            "submit", {"privateSource": "/tmp/private-tests", "missingBaselinePublic": True}
        )
        assert ok is True
        assert msg is not None
        assert "BSCS_ALLOW_MUTATED_PUBLIC_SUBMIT=1" in msg

    def test_submit_passes_when_private_suite_present(self, grade_module):
        """Validation passes cleanly when privateSource is present."""
        ok, msg = grade_module._validate_submit_private_suite(
            "submit", {"privateSource": "/tmp/private-tests"}
        )
        assert ok is True
        assert msg is None


class TestPrivateExpectations:
    """Tests for determining whether private tests are expected."""

    def test_local_repo_private_tests_mark_assignment_as_required(self, grade_module):
        assert grade_module._requires_private_tests(PROJECT_ROOT, "comp341", 1, "baby_names") is True

    def test_assignment_without_private_tests_is_not_marked_required(self, grade_module):
        assert grade_module._requires_private_tests(PROJECT_ROOT, "comp321", 5, "malloc") is False

    def test_repo_local_private_tests_are_resolved_for_submit(self, grade_module):
        private_dir = grade_module._resolve_private_dir(PROJECT_ROOT, "comp341", 1, "baby_names")
        assert private_dir == PROJECT_ROOT / "private_tests" / "comp341" / "baby_names"

    def test_explicit_config_true_marks_assignment_required(self, grade_module, tmp_path):
        project_root = tmp_path
        config_dir = project_root / "comp999" / "configs"
        config_dir.mkdir(parents=True)
        (config_dir / "module1.yaml").write_text(
            "assignment_number: 1\n"
            "assignment_name: circles\n"
            "language: python\n"
            "grading:\n"
            "  requires_private_tests: true\n"
        )

        assert grade_module._requires_private_tests(project_root, "comp999", 1, "circles") is True

    def test_explicit_config_false_overrides_repo_scan(self, grade_module, tmp_path):
        project_root = tmp_path
        config_dir = project_root / "comp999" / "configs"
        config_dir.mkdir(parents=True)
        (config_dir / "module1.yaml").write_text(
            "assignment_number: 1\n"
            "assignment_name: circles\n"
            "language: python\n"
            "grading:\n"
            "  requires_private_tests: false\n"
        )
        private_dir = project_root / "private_tests" / "comp999" / "circles"
        private_dir.mkdir(parents=True)
        (private_dir / "test_priv.py").write_text("# hidden")

        required, reason = grade_module._private_test_requirement(project_root, "comp999", 1, "circles")

        assert required is False
        assert reason == "grading"
