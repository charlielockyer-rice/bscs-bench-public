"""
Workspace snapshot helpers for bench-cli v2.

Each attempt stores a full workspace git bundle plus convenience diff/file
artifacts for quick inspection.
"""

import shutil
import subprocess
from pathlib import Path


def current_head_commit(ws_dir: str) -> str | None:
    result = subprocess.run(
        ["git", "rev-parse", "HEAD"],
        cwd=ws_dir,
        capture_output=True,
        text=True,
    )
    if result.returncode != 0 or not result.stdout.strip():
        return None
    return result.stdout.strip()


def _initial_commit(ws_dir: str) -> str | None:
    result = subprocess.run(
        ["git", "rev-list", "--max-parents=0", "HEAD"],
        cwd=ws_dir,
        capture_output=True,
        text=True,
    )
    if result.returncode != 0 or not result.stdout.strip():
        return None
    return result.stdout.strip().splitlines()[0]


def copy_snapshot_file(ws_path: Path, attempt_dir: Path, src_name: str, dest_name: str) -> str | None:
    src = ws_path / src_name
    if not src.exists() or not src.is_file():
        return None
    shutil.copy2(src, attempt_dir / dest_name)
    return dest_name


def create_workspace_snapshot(ws_dir: str, attempt_dir: Path, base_commit: str | None = None) -> dict:
    """Create full-repo and convenience artifacts for an attempt."""
    ws_path = Path(ws_dir)
    attempt_dir.mkdir(parents=True, exist_ok=True)

    subprocess.run(["git", "add", "-A"], cwd=ws_dir, capture_output=True)
    commit_result = subprocess.run(
        ["git", "commit", "-m", "Agent solution"],
        cwd=ws_dir,
        capture_output=True,
        text=True,
    )

    bundle_result = None
    diff_base = base_commit or _initial_commit(ws_dir)
    commit_output = (commit_result.stderr or "") + "\n" + (commit_result.stdout or "")
    commit_failed = commit_result.returncode != 0 and "nothing to commit" not in commit_output.lower()

    if not commit_failed:
        bundle_path = attempt_dir / "workspace.bundle"
        bundle_result = subprocess.run(
            ["git", "bundle", "create", str(bundle_path), "--all"],
            cwd=ws_dir,
            capture_output=True,
        )
    else:
        (attempt_dir / "snapshot_error.txt").write_text(commit_output.strip() or "git commit failed")

    if diff_base and not commit_failed:
        diff = subprocess.run(
            ["git", "diff", diff_base, "HEAD"],
            cwd=ws_dir,
            capture_output=True,
            text=True,
        )
        if diff.stdout.strip():
            (attempt_dir / "solution.diff").write_text(diff.stdout)

        changed = subprocess.run(
            ["git", "diff", "--name-only", diff_base, "HEAD"],
            cwd=ws_dir,
            capture_output=True,
            text=True,
        )
        if changed.stdout.strip():
            solution_dir = attempt_dir / "solution"
            solution_dir.mkdir(exist_ok=True)
            for fname in changed.stdout.strip().splitlines():
                src = ws_path / fname
                if src.exists() and src.is_file():
                    dest = solution_dir / fname
                    dest.parent.mkdir(parents=True, exist_ok=True)
                    shutil.copy2(src, dest)

    instructions_snapshot = copy_snapshot_file(ws_path, attempt_dir, "instructions.md", "instructions_snapshot.md")
    writeup_snapshot = copy_snapshot_file(ws_path, attempt_dir, "writeup.md", "writeup_snapshot.md")

    return {
        "workspace_bundle": "workspace.bundle" if bundle_result and bundle_result.returncode == 0 else None,
        "solution_diff": "solution.diff" if (attempt_dir / "solution.diff").exists() else None,
        "solution_files": "solution" if (attempt_dir / "solution").exists() else None,
        "instructions_snapshot": instructions_snapshot,
        "writeup_snapshot": writeup_snapshot,
        "snapshot_error": "snapshot_error.txt" if (attempt_dir / "snapshot_error.txt").exists() else None,
    }
