"""
Normalized assignment registry for bench-cli v2.

This module intentionally keeps the surface small. It resolves stable
assignment IDs from workspace metadata under a model namespace and returns
the metadata needed by the v2 assignment-attempt flow.
"""

from pathlib import Path

from _lib import WORKSPACES_DIR, read_workspace_yaml, workspace_entry


def make_assignment_id(course: str, assignment_name: str) -> str:
    """Build the stable assignment ID used by v2 state storage."""
    if not course or not assignment_name:
        raise ValueError("assignment_id requires course and assignment_name")
    return f"{course}_{assignment_name}"


def _model_workspaces_root(model_key: str) -> Path:
    root = WORKSPACES_DIR / model_key
    if not root.is_dir():
        raise FileNotFoundError(f"Model workspace directory not found: {root}")
    return root


def list_assignments(model_key: str) -> list[dict]:
    """List normalized assignments for a model namespace."""
    root = _model_workspaces_root(model_key)
    assignments = []

    for ws_dir in sorted(root.iterdir()):
        if not ws_dir.is_dir() or ws_dir.name == "runs":
            continue
        info = read_workspace_yaml(ws_dir)
        if info is None:
            continue
        ws = workspace_entry(ws_dir, info)
        assignment_id = make_assignment_id(ws["course"], ws["name"])
        assignments.append({
            "assignment_id": assignment_id,
            "workspace_id": ws["id"],
            "workspace_dir": str(ws_dir),
            "course": ws["course"],
            "assignment_number": ws["assignment_number"],
            "assignment_name": ws["name"],
            "display_name": ws["display_name"],
            "assignment_mode": ws.get("assignment_mode", ""),
            "language": ws["language"],
            "depends_on": ws.get("depends_on") or None,
            "model_key": model_key,
        })

    return assignments


def get_assignment(assignment_id: str, model_key: str) -> dict:
    """Resolve one assignment by stable assignment_id."""
    for assignment in list_assignments(model_key):
        if assignment["assignment_id"] == assignment_id:
            return assignment
    raise FileNotFoundError(
        f"Assignment not found for model '{model_key}': {assignment_id}"
    )
