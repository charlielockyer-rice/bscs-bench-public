"""
State storage helpers for bench-cli v2.

Canonical durable state lives under state/models/<model_key>/...
"""

import json
from datetime import datetime, timezone
from pathlib import Path

from _lib import PROJECT_ROOT


STATE_ROOT = PROJECT_ROOT / "state"


def utc_now_iso() -> str:
    """ISO 8601 timestamp with millisecond precision to avoid ID collisions."""
    now = datetime.now(timezone.utc)
    return now.strftime("%Y-%m-%dT%H:%M:%S.") + f"{now.microsecond // 1000:03d}Z"


def state_model_root(model_key: str) -> Path:
    return STATE_ROOT / "models" / model_key


def assignment_root(model_key: str, assignment_id: str) -> Path:
    return state_model_root(model_key) / "assignments" / assignment_id


def attempts_root(model_key: str, assignment_id: str) -> Path:
    return assignment_root(model_key, assignment_id) / "attempts"


def attempt_root(model_key: str, assignment_id: str, attempt_id: str) -> Path:
    return attempts_root(model_key, assignment_id) / attempt_id


def manifest_path(model_key: str, assignment_id: str) -> Path:
    return assignment_root(model_key, assignment_id) / "manifest.json"


def run_path(model_key: str, run_id: str) -> Path:
    return state_model_root(model_key) / "runs" / f"{run_id}.json"


def _write_json_atomic(path: Path, data: dict) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    tmp_path = path.with_suffix(path.suffix + ".tmp")
    tmp_path.write_text(json.dumps(data, indent=2))
    tmp_path.replace(path)


def _read_json(path: Path) -> dict | None:
    if not path.exists():
        return None
    return json.loads(path.read_text())


def create_run(model_key: str, agent: str, selection: dict, requested_phases: list[str]) -> dict:
    run_id = "run_" + utc_now_iso().replace(":", "-")
    data = {
        "run_id": run_id,
        "model_key": model_key,
        "agent": agent,
        "selection": selection,
        "requested_phases": requested_phases,
        "attempts_created": [],
        "started_at": utc_now_iso(),
        "completed_at": None,
    }
    _write_json_atomic(run_path(model_key, run_id), data)
    return data


def append_run_attempt(model_key: str, run_id: str, assignment_id: str, attempt_id: str) -> None:
    path = run_path(model_key, run_id)
    data = _read_json(path) or {}
    data.setdefault("attempts_created", []).append({
        "assignment_id": assignment_id,
        "attempt_id": attempt_id,
    })
    _write_json_atomic(path, data)


def finalize_run(model_key: str, run_id: str) -> None:
    path = run_path(model_key, run_id)
    data = _read_json(path) or {}
    data["completed_at"] = utc_now_iso()
    _write_json_atomic(path, data)


def create_attempt(assignment_meta: dict, agent: str, run_id: str | None, trigger_kind: str) -> dict:
    attempt_id = utc_now_iso().replace(":", "-")
    attempt_dir = attempt_root(
        assignment_meta["model_key"],
        assignment_meta["assignment_id"],
        attempt_id,
    )
    attempt_dir.mkdir(parents=True, exist_ok=True)
    data = {
        "attempt_id": attempt_id,
        "assignment_id": assignment_meta["assignment_id"],
        "workspace_id": assignment_meta["workspace_id"],
        "model_key": assignment_meta["model_key"],
        "agent": agent,
        "trigger": {
            "kind": trigger_kind,
            "run_id": run_id,
        },
        "phases": {
            "agent": {"status": "not_started"},
            "submit_grade": {"status": "not_started"},
            "llm_grade": {"status": "not_started"},
            "code_review": {"status": "not_started"},
        },
        "final_summary": {
            "code_pct": None,
            "written_pct": None,
            "review_pct": None,
        },
        "created_at": utc_now_iso(),
        "completed_at": None,
    }
    _write_json_atomic(attempt_dir / "attempt.json", data)
    return data


def load_attempt(model_key: str, assignment_id: str, attempt_id: str) -> dict:
    path = attempt_root(model_key, assignment_id, attempt_id) / "attempt.json"
    data = _read_json(path)
    if data is None:
        raise FileNotFoundError(f"Attempt not found: {path}")
    return data


def save_attempt(data: dict) -> None:
    path = attempt_root(
        data["model_key"], data["assignment_id"], data["attempt_id"]
    ) / "attempt.json"
    _write_json_atomic(path, data)


def update_phase(data: dict, phase_name: str, phase_data: dict) -> dict:
    data.setdefault("phases", {})[phase_name] = phase_data
    save_attempt(data)
    return data


def finalize_attempt(data: dict, final_summary: dict) -> dict:
    data["final_summary"] = final_summary
    data["completed_at"] = utc_now_iso()
    save_attempt(data)
    return data


def load_manifest(model_key: str, assignment_id: str) -> dict | None:
    return _read_json(manifest_path(model_key, assignment_id))


def load_active_attempt(model_key: str, assignment_id: str) -> dict:
    manifest = load_manifest(model_key, assignment_id)
    if not manifest or not manifest.get("active_attempt_id"):
        raise FileNotFoundError(
            f"No active attempt for {assignment_id} in model {model_key}"
        )
    return load_attempt(model_key, assignment_id, manifest["active_attempt_id"])


def list_manifests(model_key: str) -> list[dict]:
    root = state_model_root(model_key) / "assignments"
    if not root.is_dir():
        return []
    manifests = []
    for assignment_dir in sorted(root.iterdir()):
        if not assignment_dir.is_dir():
            continue
        data = _read_json(assignment_dir / "manifest.json")
        if data:
            manifests.append(data)
    return manifests


def promote_attempt(assignment_meta: dict, attempt_data: dict) -> dict:
    path = manifest_path(assignment_meta["model_key"], assignment_meta["assignment_id"])
    existing = _read_json(path) or {
        "assignment_id": assignment_meta["assignment_id"],
        "workspace_id": assignment_meta["workspace_id"],
        "model_key": assignment_meta["model_key"],
        "agent": attempt_data["agent"],
        "course": assignment_meta["course"],
        "assignment_number": assignment_meta["assignment_number"],
        "assignment_name": assignment_meta["assignment_name"],
        "display_name": assignment_meta["display_name"],
        "assignment_mode": assignment_meta["assignment_mode"],
        "language": assignment_meta["language"],
        "depends_on": assignment_meta["depends_on"],
        "attempt_history": [],
    }
    existing["active_attempt_id"] = attempt_data["attempt_id"]
    if attempt_data["attempt_id"] not in existing["attempt_history"]:
        existing["attempt_history"].append(attempt_data["attempt_id"])
    existing["latest_phase_status"] = {
        phase: payload.get("status", "not_started")
        for phase, payload in attempt_data.get("phases", {}).items()
    }
    agent_metrics = attempt_data.get("phases", {}).get("agent", {}).get("metrics", {})
    existing["latest_summary"] = {
        **attempt_data.get("final_summary", {}),
        "cost_usd": agent_metrics.get("cost_usd"),
        "coverage": {
            phase: payload.get("status") == "completed"
            for phase, payload in attempt_data.get("phases", {}).items()
        },
    }
    existing["updated_at"] = utc_now_iso()
    _write_json_atomic(path, existing)
    return existing
