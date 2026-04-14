"""
Helpers for composing public, private, and agent-authored test suites.
"""

from __future__ import annotations

import json
import os
import shutil
import subprocess
import tempfile
from pathlib import Path

from .config_compat import get_assignment_name, get_assignment_number
from .suite_manifest import write_suite_manifest

# Optional YAML support, mirroring other framework modules.
try:
    import yaml

    HAS_YAML = True
except ImportError:
    HAS_YAML = False


def _copy_tree(
    src: Path,
    dst: Path,
    *,
    overwrite: bool = True,
    conflicts: list[str] | None = None,
) -> None:
    """Recursively copy src into dst.

    When overwrite=False, existing files are preserved and recorded in conflicts.
    """
    if not src.exists():
        return
    for root, dirs, files in os.walk(src):
        rel = Path(root).relative_to(src)
        target_dir = dst / rel
        target_dir.mkdir(parents=True, exist_ok=True)
        for file_name in files:
            src_file = Path(root) / file_name
            dst_file = target_dir / file_name
            if dst_file.exists() and not overwrite:
                if conflicts is not None:
                    conflicts.append(str((rel / file_name).as_posix() if str(rel) != "." else file_name))
                continue
            shutil.copy2(src_file, dst_file)


def _list_files_rel(root: Path) -> list[str]:
    """List all files under root as relative POSIX paths."""
    if not root.exists():
        return []
    files: list[str] = []
    for dirpath, _, filenames in os.walk(root):
        for name in filenames:
            rel = Path(dirpath, name).relative_to(root)
            files.append(str(rel.as_posix()))
    files.sort()
    return files


def _collect_suite_entries(root: Path, source: str) -> list[dict]:
    """Describe all files in a suite subtree for manifest consumers."""
    entries: list[dict] = []
    if not root.exists():
        return entries
    for rel_path in _list_files_rel(root):
        rel_obj = Path(rel_path)
        entries.append({
            "source": source,
            "relative_path": rel_path,
            "basename": rel_obj.name,
            "stem": rel_obj.stem,
            "class_name": rel_obj.stem if rel_obj.suffix == ".java" else None,
            "original_path": str((root / rel_obj).resolve()),
        })
    return entries


def _load_assignment_config_entry_for_number(
    project_root: Path,
    course_name: str,
    module_number: int,
) -> tuple[Path | None, dict | None]:
    """Load the config path/data pair for the requested assignment number."""
    course_path = project_root / course_name
    configs_dir = course_path / "configs"
    if not configs_dir.exists():
        return None, None
    for config_file in configs_dir.glob("*.yaml"):
        data = _load_yaml_config(config_file)
        if data and get_assignment_number(data) == module_number:
            return config_file, data
    return None, None


def _load_assignment_config_for_number(
    project_root: Path,
    course_name: str,
    module_number: int,
) -> dict | None:
    """Load the assignment config matching the requested assignment number."""
    _, config = _load_assignment_config_entry_for_number(
        project_root, course_name, module_number,
    )
    return config


def _parse_bool_setting(value) -> bool | None:
    """Parse a config boolean, returning None when unspecified/invalid."""
    if isinstance(value, bool):
        return value
    if isinstance(value, str):
        normalized = value.strip().lower()
        if normalized in {"1", "true", "yes", "on"}:
            return True
        if normalized in {"0", "false", "no", "off"}:
            return False
    return None


def _private_test_requirement(
    project_root: Path,
    course_name: str,
    module_number: int,
    module_name: str | None,
) -> tuple[bool, str]:
    """Return whether private tests are required and how that was decided."""
    config_path, config = _load_assignment_config_entry_for_number(
        project_root, course_name, module_number,
    )
    if config:
        explicit = _parse_bool_setting(config.get("requires_private_tests"))
        if explicit is not None:
            return explicit, "config"
        grading = config.get("grading")
        if isinstance(grading, dict):
            explicit = _parse_bool_setting(grading.get("requires_private_tests"))
            if explicit is not None:
                return explicit, "grading"

    discovered = any(
        candidate.exists() and candidate.is_dir()
        for candidate in _expected_private_test_candidates(
            project_root,
            course_name,
            module_number,
            module_name,
            _config=config,
            _config_path=config_path,
        )
    )
    return discovered, "repo_scan" if discovered else "default"


def _expected_private_test_candidates(
    project_root: Path,
    course_name: str,
    module_number: int,
    module_name: str | None,
    *,
    root: Path | None = None,
    _config: dict | None = None,
    _config_path: Path | None = None,
) -> list[Path]:
    """Return all plausible private-suite locations for this assignment."""
    candidates: list[Path] = []
    local_root = root if root is not None else (project_root / "private_tests")
    if module_name:
        candidates.append(local_root / course_name / module_name)

    config_path = _config_path
    config = _config
    if config is None or config_path is None:
        entry_path, entry_config = _load_assignment_config_entry_for_number(
            project_root, course_name, module_number,
        )
        if config is None:
            config = entry_config
        if config_path is None:
            config_path = entry_path
    if config:
        assignment_name = get_assignment_name(config)
        if assignment_name:
            candidates.append(local_root / course_name / assignment_name)
        if config_path is not None:
            candidates.append(local_root / course_name / config_path.stem)

    candidates.append(local_root / course_name / f"module{module_number}")
    candidates.append(local_root / course_name / str(module_number))

    if config:
        for key in ("assignment_name", "display_name"):
            value = Path(str(config.get(key, ""))).name
            if value:
                candidates.append(local_root / course_name / value)

    seen: set[str] = set()
    unique: list[Path] = []
    for candidate in candidates:
        key = str(candidate)
        if key and key not in seen:
            seen.add(key)
            unique.append(candidate)
    return unique


def _requires_private_tests(
    project_root: Path,
    course_name: str,
    module_number: int,
    module_name: str | None,
) -> bool:
    """Return whether submit runs are expected to include private tests."""
    required, _ = _private_test_requirement(project_root, course_name, module_number, module_name)
    return required


def _resolve_workspace_public_dir(workspace_path: Path) -> Path | None:
    """Return workspace public test dir if present."""
    public_dir = workspace_path / "tests" / "public"
    if public_dir.exists() and public_dir.is_dir():
        return public_dir
    return None


def _resolve_workspace_agent_dir(workspace_path: Path) -> Path | None:
    """Return workspace agent test dir if present."""
    agent_dir = workspace_path / "tests" / "agent"
    if agent_dir.exists() and agent_dir.is_dir():
        return agent_dir
    return None


def _extract_public_from_initial_commit(workspace_path: Path) -> Path | None:
    """
    Rehydrate tests/public from the workspace's initial git commit.
    Returns a temp dir containing the extracted suite, or None if unavailable.

    Uses `git archive | tar` to extract all files in a single subprocess pair
    instead of per-file `git show` calls.
    """
    try:
        initial = subprocess.check_output(
            ["git", "rev-list", "--max-parents=0", "HEAD"],
            cwd=workspace_path,
            text=True,
            stderr=subprocess.DEVNULL,
        ).strip().splitlines()[0]
        # Check that tests/public exists in the initial commit
        files_raw = subprocess.check_output(
            ["git", "ls-tree", "-r", "--name-only", initial, "tests/public"],
            cwd=workspace_path,
            text=True,
            stderr=subprocess.DEVNULL,
        ).strip()
    except Exception:
        return None

    if not files_raw:
        return None

    temp_dir = Path(tempfile.mkdtemp(prefix="bscs_public_baseline_"))
    try:
        # Extract tests/public/ in one shot via git archive piped to tar.
        # tar --strip-components=2 removes the "tests/public/" path prefix
        # so files land directly at temp_dir root.
        archive = subprocess.Popen(
            ["git", "archive", initial, "tests/public"],
            cwd=workspace_path,
            stdout=subprocess.PIPE,
            stderr=subprocess.DEVNULL,
        )
        tar = subprocess.run(
            ["tar", "xf", "-", "--strip-components=2"],
            cwd=temp_dir,
            stdin=archive.stdout,
            stderr=subprocess.DEVNULL,
        )
        archive.stdout.close()
        archive.wait()
        if tar.returncode != 0 or archive.returncode != 0:
            raise RuntimeError("git archive | tar failed")
    except Exception:
        # Fallback: per-file extraction for platforms where tar flags differ
        for rel in files_raw.splitlines():
            rel = rel.strip()
            if not rel or not rel.startswith("tests/public/"):
                continue
            out_rel = Path(rel).relative_to("tests/public")
            out_file = temp_dir / out_rel
            out_file.parent.mkdir(parents=True, exist_ok=True)
            try:
                content = subprocess.check_output(
                    ["git", "show", f"{initial}:{rel}"],
                    cwd=workspace_path,
                    stderr=subprocess.DEVNULL,
                )
                out_file.write_bytes(content)
            except Exception:
                continue

    if any(path.is_file() for path in temp_dir.rglob("*")):
        return temp_dir
    shutil.rmtree(temp_dir, ignore_errors=True)
    return None


def _load_yaml_config(config_path: Path) -> dict | None:
    """Load a YAML config file, returning None on failure."""
    if not HAS_YAML or not config_path.exists():
        return None
    try:
        with open(config_path, "r") as f:
            return yaml.safe_load(f)
    except Exception:
        return None


def _resolve_private_dir(
    project_root: Path,
    course_name: str,
    module_number: int,
    module_name: str | None,
) -> Path | None:
    """
    Resolve private test directory from the configured override root or the
    repo-local private_tests tree.
    """
    config_path, config = _load_assignment_config_entry_for_number(
        project_root, course_name, module_number,
    )
    root_raw = os.environ.get("BSCS_PRIVATE_TESTS_ROOT", "").strip()
    roots: list[Path] = []
    if root_raw:
        roots.append(Path(root_raw).expanduser().resolve())
    roots.append((project_root / "private_tests").resolve())

    seen_roots: set[str] = set()
    for root in roots:
        key = str(root)
        if key in seen_roots:
            continue
        seen_roots.add(key)
        for candidate in _expected_private_test_candidates(
            project_root,
            course_name,
            module_number,
            module_name,
            root=root,
            _config=config,
            _config_path=config_path,
        ):
            if candidate.exists() and candidate.is_dir():
                return candidate
    return None


def _is_env_truthy(name: str) -> bool:
    value = os.environ.get(name, "").strip().lower()
    return value in {"1", "true", "yes", "on"}


def _validate_submit_private_suite(run_type: str, suite_meta: dict) -> tuple[bool, str | None]:
    """
    Validate submit-mode test suite configuration.

    Returns a note when no private tests are configured.
    Blocks only when baseline public tests cannot be reconstructed from git.
    """
    if suite_meta.get("suiteConflicts"):
        details = ", ".join(
            sorted(
                f"{item.get('source')}:{item.get('relative_path')}"
                for item in suite_meta["suiteConflicts"]
            )
        )
        return False, (
            "Error: test-suite merge conflicts detected; refusing to run with "
            f"shadowed tests: {details}"
        )
    if run_type != "submit":
        return True, None
    if suite_meta.get("missingBaselinePublic"):
        message = (
            "submit run-type could not reconstruct baseline public tests from the initial "
            "commit; refusing to use mutable workspace tests/public."
        )
        if _is_env_truthy("BSCS_ALLOW_MUTATED_PUBLIC_SUBMIT"):
            return True, f"Warning: {message} Continuing because BSCS_ALLOW_MUTATED_PUBLIC_SUBMIT=1."
        return False, f"Error: {message} If intentional, set BSCS_ALLOW_MUTATED_PUBLIC_SUBMIT=1."
    if suite_meta.get("requiresPrivateTests") and not suite_meta.get("privateSource"):
        return False, (
            "Error: submit run-type requires private tests for this assignment, but none were "
            "resolved. Check BSCS_PRIVATE_TESTS_ROOT or assignment config."
        )
    if not suite_meta.get("privateSource"):
        return True, "Note: no private tests configured; submit will run public tests only."
    return True, None


def build_suite_audit_payload(
    workspace_path: Path,
    suite_dir: Path | None,
    suite_meta: dict,
    *,
    is_valid: bool,
    validation_message: str | None,
) -> dict:
    """Build a stable audit payload for CLI/debugging consumers."""
    effective_test_files = list(suite_meta.get("effectiveTestFiles") or [])
    manifest_path = suite_meta.get("manifestPath")
    manifest = None
    if manifest_path:
        try:
            manifest = json.loads(Path(manifest_path).read_text())
        except Exception:
            manifest = None

    return {
        "workspace": str(workspace_path.resolve()),
        "runType": suite_meta.get("runType"),
        "isValid": is_valid,
        "validationMessage": validation_message,
        "suiteDir": str(suite_dir) if suite_dir else None,
        "manifestPath": manifest_path,
        "manifest": manifest,
        "requiresPrivateTests": suite_meta.get("requiresPrivateTests", False),
        "requiresPrivateTestsReason": suite_meta.get("requiresPrivateTestsReason"),
        "public": {
            "source": suite_meta.get("publicSource"),
            "files": list(suite_meta.get("publicTestFiles") or []),
            "missingBaseline": bool(suite_meta.get("missingBaselinePublic")),
        },
        "private": {
            "source": suite_meta.get("privateSource"),
            "files": list(suite_meta.get("privateTestFiles") or []),
        },
        "agent": {
            "source": suite_meta.get("agentSource"),
            "files": list(suite_meta.get("agentTestFiles") or []),
        },
        "conflicts": list(suite_meta.get("suiteConflicts") or []),
        "effectiveTestFiles": effective_test_files,
    }


def format_suite_audit_text(payload: dict) -> str:
    """Format a human-readable test-suite audit summary."""
    lines = [
        f"workspace: {payload.get('workspace')}",
        f"run_type: {payload.get('runType')}",
        f"valid: {'yes' if payload.get('isValid') else 'no'}",
        f"suite_dir: {payload.get('suiteDir') or '(none)'}",
        f"requires_private_tests: {payload.get('requiresPrivateTests')} ({payload.get('requiresPrivateTestsReason')})",
    ]
    if payload.get("validationMessage"):
        lines.append(f"validation: {payload['validationMessage']}")

    for label in ("public", "private", "agent"):
        section = payload.get(label, {})
        lines.append(f"{label}_source: {section.get('source') or '(none)'}")
        files = section.get("files") or []
        lines.append(f"{label}_files[{len(files)}]: {', '.join(files) if files else '(none)'}")

    conflicts = payload.get("conflicts") or []
    if conflicts:
        rendered = ", ".join(
            f"{item.get('source')}:{item.get('relative_path')}" for item in conflicts
        )
    else:
        rendered = "(none)"
    lines.append(f"conflicts: {rendered}")

    effective = payload.get("effectiveTestFiles") or []
    lines.append(f"effective_files[{len(effective)}]: {', '.join(effective) if effective else '(none)'}")
    return "\n".join(lines)


def _assemble_suite_for_run_type(
    run_type: str,
    workspace_path: Path,
    project_root: Path,
    course_name: str,
    module_number: int,
    module_name: str | None,
) -> tuple[Path | None, list[Path], dict]:
    """
    Build the effective test suite directory for this run type.
    Returns (suite_dir, temp_dirs_to_cleanup, metadata).
    """
    temp_dirs: list[Path] = []
    metadata: dict = {
        "runType": run_type,
        "publicSource": None,
        "publicTestFiles": [],
        "privateSource": None,
        "privateTestFiles": [],
        "missingBaselinePublic": False,
        "requiresPrivateTests": False,
        "requiresPrivateTestsReason": "default",
        "agentSource": None,
        "agentTestFiles": [],
        "agentConflicts": [],
        "privateConflicts": [],
        "suiteConflicts": [],
        "effectiveTestFiles": [],
        "manifestPath": None,
    }

    workspace_public = _resolve_workspace_public_dir(workspace_path)
    workspace_agent = _resolve_workspace_agent_dir(workspace_path)
    required_private, requirement_reason = _private_test_requirement(
        project_root, course_name, module_number, module_name,
    )
    metadata["requiresPrivateTests"] = required_private
    metadata["requiresPrivateTestsReason"] = requirement_reason
    if workspace_agent:
        metadata["agentSource"] = str(workspace_agent)
        metadata["agentTestFiles"] = _list_files_rel(workspace_agent)
    if workspace_public:
        metadata["publicTestFiles"] = _list_files_rel(workspace_public)

    if run_type == "test":
        if not workspace_public and not workspace_agent:
            return None, temp_dirs, metadata

        metadata["publicSource"] = str(workspace_public) if workspace_public else None
        merged = Path(tempfile.mkdtemp(prefix="bscs_test_suite_test_"))
        temp_dirs.append(merged)
        manifest_entries: list[dict] = []
        if workspace_public:
            _copy_tree(workspace_public, merged)
            manifest_entries.extend(_collect_suite_entries(workspace_public, "public"))
        agent_conflicts: list[str] = []
        if workspace_agent:
            _copy_tree(workspace_agent, merged, overwrite=False, conflicts=agent_conflicts)
            metadata["agentConflicts"] = agent_conflicts
            manifest_entries.extend(
                entry for entry in _collect_suite_entries(workspace_agent, "agent")
                if entry["relative_path"] not in set(agent_conflicts)
            )
        if agent_conflicts:
            metadata["suiteConflicts"] = [
                {"source": "agent", "relative_path": rel_path} for rel_path in agent_conflicts
            ]
        metadata["effectiveTestFiles"] = sorted(entry["relative_path"] for entry in manifest_entries)
        manifest_path = write_suite_manifest(merged, {
            "version": 1,
            "run_type": run_type,
            "entries": sorted(manifest_entries, key=lambda item: item["relative_path"]),
        })
        metadata["manifestPath"] = str(manifest_path)
        return merged, temp_dirs, metadata

    baseline_public = _extract_public_from_initial_commit(workspace_path)
    if baseline_public:
        temp_dirs.append(baseline_public)
        metadata["publicSource"] = str(baseline_public)
    elif workspace_public:
        baseline_public = workspace_public
        metadata["missingBaselinePublic"] = True
        metadata["publicSource"] = str(workspace_public)

    private_dir = _resolve_private_dir(project_root, course_name, module_number, module_name)
    if private_dir:
        metadata["privateSource"] = str(private_dir)
        metadata["privateTestFiles"] = _list_files_rel(private_dir)
    if baseline_public:
        metadata["publicTestFiles"] = _list_files_rel(baseline_public)

    if not baseline_public and not private_dir:
        return None, temp_dirs, metadata

    merged = Path(tempfile.mkdtemp(prefix="bscs_test_suite_submit_"))
    temp_dirs.append(merged)
    manifest_entries: list[dict] = []
    if baseline_public:
        _copy_tree(baseline_public, merged)
        manifest_entries.extend(_collect_suite_entries(baseline_public, "public"))
    if private_dir:
        private_conflicts: list[str] = []
        _copy_tree(private_dir, merged, overwrite=False, conflicts=private_conflicts)
        metadata["privateConflicts"] = private_conflicts
        metadata["suiteConflicts"] = [
            {"source": "private", "relative_path": rel_path} for rel_path in private_conflicts
        ]
        manifest_entries.extend(
            entry for entry in _collect_suite_entries(private_dir, "private")
            if entry["relative_path"] not in set(private_conflicts)
        )
    metadata["effectiveTestFiles"] = sorted(entry["relative_path"] for entry in manifest_entries)
    manifest_path = write_suite_manifest(merged, {
        "version": 1,
        "run_type": run_type,
        "entries": sorted(manifest_entries, key=lambda item: item["relative_path"]),
    })
    metadata["manifestPath"] = str(manifest_path)
    return merged, temp_dirs, metadata
