"""Focused tests for the first bench-cli v2 assignment-attempt slice."""

import importlib.machinery
import importlib.util
import json
import subprocess
import sys
import tarfile
from pathlib import Path

import pytest


PROJECT_ROOT = Path(__file__).resolve().parent.parent
BIN_DIR = PROJECT_ROOT / "bin"


def _load_module(name: str, path: Path):
    if str(BIN_DIR) not in sys.path:
        sys.path.insert(0, str(BIN_DIR))
    loader = importlib.machinery.SourceFileLoader(name, str(path))
    spec = importlib.util.spec_from_loader(name, loader)
    mod = importlib.util.module_from_spec(spec)
    loader.exec_module(mod)
    return mod


@pytest.fixture()
def assignment_registry_module():
    return _load_module("assignment_registry_v2", PROJECT_ROOT / "bin" / "_assignment_registry.py")


@pytest.fixture()
def state_store_module():
    return _load_module("state_store_v2", PROJECT_ROOT / "bin" / "_state_store.py")


@pytest.fixture()
def bench_cli_module():
    return _load_module("bench_cli_v2", PROJECT_ROOT / "bin" / "bench-cli")


def test_assignment_registry_resolves_model_assignment(tmp_path, monkeypatch, assignment_registry_module):
    workspaces_dir = tmp_path / "workspaces"
    ws_dir = workspaces_dir / "gpt-5-3-codex" / "comp140_module1_circles"
    ws_dir.mkdir(parents=True)
    (ws_dir / "workspace.yaml").write_text(
        "\n".join([
            'assignment_number: 1',
            'assignment_name: "circles"',
            'display_name: "Circles"',
            'course: "comp140"',
            'language: "python"',
        ])
    )

    monkeypatch.setattr(assignment_registry_module, "WORKSPACES_DIR", workspaces_dir)

    assignment = assignment_registry_module.get_assignment("comp140_circles", "gpt-5-3-codex")

    assert assignment["workspace_id"] == "comp140_module1_circles"
    assert assignment["assignment_id"] == "comp140_circles"
    assert assignment["display_name"] == "Circles"


def test_state_store_creates_attempt_and_manifest(tmp_path, monkeypatch, state_store_module):
    monkeypatch.setattr(state_store_module, "STATE_ROOT", tmp_path / "state")

    assignment_meta = {
        "assignment_id": "comp140_circles",
        "workspace_id": "comp140_module1_circles",
        "model_key": "gpt-5-3-codex",
        "course": "comp140",
        "assignment_number": 1,
        "assignment_name": "circles",
        "display_name": "Circles",
        "assignment_mode": "code",
        "language": "python",
        "depends_on": None,
    }

    run_data = state_store_module.create_run(
        model_key="gpt-5-3-codex",
        agent="codex",
        selection={"kind": "assignment", "value": "comp140_circles"},
        requested_phases=["agent", "submit_grade"],
    )
    attempt = state_store_module.create_attempt(
        assignment_meta,
        agent="codex",
        run_id=run_data["run_id"],
        trigger_kind="manual_run",
    )
    attempt["phases"]["agent"] = {"status": "completed"}
    attempt["final_summary"] = {"code_pct": 100.0, "written_pct": None, "review_pct": None}
    state_store_module.save_attempt(attempt)
    manifest = state_store_module.promote_attempt(assignment_meta, attempt)

    assert manifest["active_attempt_id"] == attempt["attempt_id"]
    assert manifest["attempt_history"] == [attempt["attempt_id"]]
    assert (tmp_path / "state" / "models" / "gpt-5-3-codex" / "assignments" / "comp140_circles" / "manifest.json").exists()


def test_v2_run_assignment_writes_attempt_state(tmp_path, monkeypatch, bench_cli_module):
    workspaces_dir = tmp_path / "workspaces"
    workspace_dir = workspaces_dir / "gpt-5-3-codex" / "comp140_module1_circles"
    workspace_dir.mkdir(parents=True)
    (workspace_dir / "workspace.yaml").write_text(
        "\n".join([
            'assignment_number: 1',
            'assignment_name: "circles"',
            'display_name: "Circles"',
            'course: "comp140"',
            'language: "python"',
        ])
    )
    (workspace_dir / "solution.py").write_text("print('hello')\n")

    subprocess.run(["git", "init"], cwd=workspace_dir, check=True, capture_output=True)
    subprocess.run(["git", "config", "user.email", "bench@example.com"], cwd=workspace_dir, check=True, capture_output=True)
    subprocess.run(["git", "config", "user.name", "Bench"], cwd=workspace_dir, check=True, capture_output=True)
    subprocess.run(["git", "add", "."], cwd=workspace_dir, check=True, capture_output=True)
    subprocess.run(["git", "commit", "-m", "Initial"], cwd=workspace_dir, check=True, capture_output=True)

    assignment_meta = {
        "assignment_id": "comp140_circles",
        "workspace_id": "comp140_module1_circles",
        "workspace_dir": str(workspace_dir),
        "course": "comp140",
        "assignment_number": 1,
        "assignment_name": "circles",
        "display_name": "Circles",
        "assignment_mode": "code",
        "language": "python",
        "depends_on": None,
        "model_key": "gpt-5-3-codex",
    }

    state_root = tmp_path / "state"
    monkeypatch.setattr(bench_cli_module, "get_assignment", lambda assignment_id, model_key: assignment_meta)
    monkeypatch.setattr(bench_cli_module, "WORKSPACES_DIR", workspaces_dir)
    monkeypatch.setattr(sys.modules["_state_store"], "STATE_ROOT", state_root)

    def _fake_run_claude(ws, result_dir, model, timeout=None, use_system_prompt=False):  # noqa: ARG001
        (result_dir / "agent_output.json").write_text(json.dumps({
            "total_cost_usd": 1.25,
            "usage": {"input_tokens": 10, "output_tokens": 20},
            "duration_ms": 3000,
            "duration_api_ms": 0,
            "num_turns": 2,
            "session_id": "sess_123",
            "modelUsage": {model: {"costUSD": 1.25}},
        }))
        (result_dir / "agent_trace.jsonl").write_text('{"type":"result"}\n')
        (workspace_dir / "solution.py").write_text("print('changed')\n")
        return 0

    def _fake_run_grade(ws, result_dir):  # noqa: ARG001
        grade_payload = {
            "passed": 3,
            "total": 3,
            "points": 3,
            "max_points": 3,
            "tests": [],
        }
        (result_dir / "grade_result.json").write_text(json.dumps(grade_payload))
        (result_dir / "grade_result.txt").write_text("PASS\n")
        (workspace_dir / "last_test_meta.json").write_text(json.dumps({"runType": "submit"}))
        return "Passed: 3/3 tests (100.0%)", {
            "tests_passed": 3,
            "tests_total": 3,
            "points_earned": 3.0,
            "points_possible": 3.0,
            "score_percentage": 100.0,
            "test_results": [],
        }

    monkeypatch.setattr(bench_cli_module, "run_claude", _fake_run_claude)
    monkeypatch.setattr(bench_cli_module, "run_grade", _fake_run_grade)

    rc = bench_cli_module.main_v2_run_assignment([
        "comp140_circles",
        "--model-key", "gpt-5-3-codex",
    ])

    assert rc == 0

    assignment_root = state_root / "models" / "gpt-5-3-codex" / "assignments" / "comp140_circles"
    manifest = json.loads((assignment_root / "manifest.json").read_text())
    attempt_id = manifest["active_attempt_id"]
    attempt_dir = assignment_root / "attempts" / attempt_id
    attempt = json.loads((attempt_dir / "attempt.json").read_text())

    assert attempt["phases"]["agent"]["status"] == "completed"
    assert attempt["phases"]["submit_grade"]["status"] == "completed"
    assert attempt["final_summary"]["code_pct"] == 100.0
    assert (attempt_dir / "agent_trace.jsonl").exists()
    assert (attempt_dir / "grade_result.json").exists()
    assert (attempt_dir / "test_meta.json").exists()
    assert (attempt_dir / "workspace.bundle").exists()
    assert manifest["attempt_history"] == [attempt_id]


def test_v2_phase_llm_grade_updates_active_attempt(tmp_path, monkeypatch, bench_cli_module):
    state_root = tmp_path / "state"
    assignment_root = state_root / "models" / "gpt-5-3-codex" / "assignments" / "comp140_circles"
    attempt_dir = assignment_root / "attempts" / "2026-03-30T18-12-44Z"
    attempt_dir.mkdir(parents=True)
    (assignment_root / "manifest.json").write_text(json.dumps({
        "assignment_id": "comp140_circles",
        "workspace_id": "comp140_module1_circles",
        "model_key": "gpt-5-3-codex",
        "agent": "claude",
        "course": "comp140",
        "assignment_number": 1,
        "assignment_name": "circles",
        "display_name": "Circles",
        "assignment_mode": "code",
        "language": "python",
        "depends_on": None,
        "active_attempt_id": "2026-03-30T18-12-44Z",
        "attempt_history": ["2026-03-30T18-12-44Z"],
        "latest_phase_status": {"agent": "completed", "submit_grade": "completed"},
        "latest_summary": {"code_pct": 100.0, "written_pct": None, "review_pct": None},
        "updated_at": "2026-03-30T18:20:00Z",
    }))
    (attempt_dir / "attempt.json").write_text(json.dumps({
        "attempt_id": "2026-03-30T18-12-44Z",
        "assignment_id": "comp140_circles",
        "workspace_id": "comp140_module1_circles",
        "model_key": "gpt-5-3-codex",
        "agent": "claude",
        "phases": {
            "agent": {"status": "completed", "metrics": {"cost_usd": 1.25}},
            "submit_grade": {"status": "completed", "summary": {
                "tests_passed": 3, "tests_total": 3,
                "points_earned": 3.0, "points_possible": 3.0,
                "score_percentage": 100.0, "test_results": []
            }},
            "llm_grade": {"status": "not_started"},
            "code_review": {"status": "not_started"},
        },
        "final_summary": {"code_pct": 100.0, "written_pct": None, "review_pct": None},
        "created_at": "2026-03-30T18:12:44Z",
        "completed_at": "2026-03-30T18:18:10Z"
    }))

    workspace_dir = tmp_path / "workspaces" / "gpt-5-3-codex" / "comp140_module1_circles"
    workspace_dir.mkdir(parents=True)
    assignment_meta = {
        "assignment_id": "comp140_circles",
        "workspace_id": "comp140_module1_circles",
        "workspace_dir": str(workspace_dir),
        "course": "comp140",
        "assignment_number": 1,
        "assignment_name": "circles",
        "display_name": "Circles",
        "assignment_mode": "code",
        "language": "python",
        "depends_on": None,
        "model_key": "gpt-5-3-codex",
    }

    monkeypatch.setattr(bench_cli_module, "get_assignment", lambda *_args, **_kwargs: assignment_meta)
    monkeypatch.setattr(sys.modules["_state_store"], "STATE_ROOT", state_root)

    def _fake_run_llm_grade(ws, result_dir, grading_model=None, agent="claude", reasoning=None, force=False):  # noqa: ARG001
        (result_dir / "llm_grade_result_claude.md").write_text("Points: 9/10\n")
        return "9/10"

    monkeypatch.setattr(bench_cli_module, "run_llm_grade", _fake_run_llm_grade)

    rc = bench_cli_module.main_v2_phase([
        "comp140_circles",
        "--model-key", "gpt-5-3-codex",
    ], "llm-grade")

    assert rc == 0
    attempt = json.loads((attempt_dir / "attempt.json").read_text())
    manifest = json.loads((assignment_root / "manifest.json").read_text())
    assert attempt["phases"]["llm_grade"]["status"] == "completed"
    assert manifest["latest_phase_status"]["llm_grade"] == "completed"
    assert manifest["latest_summary"]["written_pct"] == 90.0


def test_v2_run_assignment_promotes_failed_attempt(tmp_path, monkeypatch, bench_cli_module):
    workspaces_dir = tmp_path / "workspaces"
    workspace_dir = workspaces_dir / "gpt-5-3-codex" / "comp140_module1_circles"
    workspace_dir.mkdir(parents=True)

    assignment_meta = {
        "assignment_id": "comp140_circles",
        "workspace_id": "comp140_module1_circles",
        "workspace_dir": str(workspace_dir),
        "course": "comp140",
        "assignment_number": 1,
        "assignment_name": "circles",
        "display_name": "Circles",
        "assignment_mode": "code",
        "language": "python",
        "depends_on": None,
        "model_key": "gpt-5-3-codex",
    }

    state_root = tmp_path / "state"
    monkeypatch.setattr(bench_cli_module, "get_assignment", lambda *_args, **_kwargs: assignment_meta)
    monkeypatch.setattr(sys.modules["_state_store"], "STATE_ROOT", state_root)

    def _missing_agent(*_args, **_kwargs):
        raise FileNotFoundError("claude not installed")

    monkeypatch.setattr(bench_cli_module, "run_claude", _missing_agent)

    rc = bench_cli_module.main_v2_run_assignment([
        "comp140_circles",
        "--model-key", "gpt-5-3-codex",
    ])

    assert rc == 1
    manifest_path = state_root / "models" / "gpt-5-3-codex" / "assignments" / "comp140_circles" / "manifest.json"
    assert manifest_path.exists()
    manifest = json.loads(manifest_path.read_text())
    assert manifest["active_attempt_id"] in manifest["attempt_history"]
    assert manifest["latest_phase_status"]["agent"] == "not_started"


def test_v2_submit_grade_skips_written_only_assignments(tmp_path, monkeypatch, bench_cli_module):
    state_root = tmp_path / "state"
    assignment_meta = {
        "assignment_id": "comp382_hw1",
        "workspace_id": "comp382_hw1",
        "workspace_dir": str(tmp_path / "workspaces" / "gpt-5-3-codex" / "comp382_hw1"),
        "course": "comp382",
        "assignment_number": 1,
        "assignment_name": "hw1",
        "display_name": "HW1",
        "assignment_mode": "written",
        "language": "proof",
        "depends_on": None,
        "model_key": "gpt-5-3-codex",
    }
    monkeypatch.setattr(sys.modules["_state_store"], "STATE_ROOT", state_root)

    run_data = sys.modules["_state_store"].create_run(
        model_key="gpt-5-3-codex",
        agent="claude",
        selection={"kind": "assignment", "value": "comp382_hw1"},
        requested_phases=["agent", "submit_grade"],
    )
    attempt = sys.modules["_state_store"].create_attempt(
        assignment_meta,
        agent="claude",
        run_id=run_data["run_id"],
        trigger_kind="manual_run",
    )

    ctx = bench_cli_module._build_attempt_context(assignment_meta, attempt)
    ctx, summary = bench_cli_module._run_submit_grade_phase_v2(ctx)
    attempt = ctx["attempt"]

    assert summary is None
    assert attempt["phases"]["submit_grade"]["status"] == "skipped"
    assert attempt["phases"]["submit_grade"]["reason"] == "written-only assignment"


def test_run_cli_grade_prefers_attempt_snapshots(tmp_path, monkeypatch, bench_cli_module):
    workspace_dir = tmp_path / "workspace"
    workspace_dir.mkdir()
    (workspace_dir / "instructions.md").write_text("LIVE instructions")
    (workspace_dir / "writeup.md").write_text("LIVE " + ("workspace text " * 20))

    attempt_dir = tmp_path / "attempt"
    attempt_dir.mkdir()
    (attempt_dir / "instructions_snapshot.md").write_text("FROZEN instructions")
    (attempt_dir / "writeup_snapshot.md").write_text("FROZEN " + ("attempt text " * 20))

    rubric_path = tmp_path / "rubric.md"
    rubric_path.write_text("Rubric")
    monkeypatch.setattr(bench_cli_module, "find_rubric_file", lambda *_args, **_kwargs: rubric_path)

    captured = {}

    def _fake_llm_task(prompt, output_path, formatter, score_parser, timeout_label, agent="claude", model=None, reasoning=None):  # noqa: ARG001
        captured["prompt"] = prompt
        output_path.write_text("ok")
        return "10/10", 1.0

    monkeypatch.setattr(bench_cli_module, "_run_cli_llm_task", _fake_llm_task)

    score, info = bench_cli_module.run_cli_grade({
        "dir": str(workspace_dir),
        "course": "comp382",
        "assignment_number": 1,
        "name": "hw1",
        "display_name": "HW1",
    }, attempt_dir)

    assert score == "10/10"
    assert info == 1.0
    assert "FROZEN instructions" in captured["prompt"]
    assert "FROZEN attempt text" in captured["prompt"]
    assert "LIVE instructions" not in captured["prompt"]


def test_v2_summarize_model_reads_manifests_only(tmp_path, monkeypatch, capsys, bench_cli_module):
    state_root = tmp_path / "state"
    assignment_root = state_root / "models" / "gpt-5-3-codex" / "assignments" / "comp140_circles"
    attempt_dir = assignment_root / "attempts" / "attempt-1"
    attempt_dir.mkdir(parents=True)
    (assignment_root / "manifest.json").write_text(json.dumps({
        "assignment_id": "comp140_circles",
        "workspace_id": "comp140_module1_circles",
        "model_key": "gpt-5-3-codex",
        "agent": "claude",
        "course": "comp140",
        "assignment_number": 1,
        "assignment_name": "circles",
        "display_name": "Circles",
        "assignment_mode": "code",
        "language": "python",
        "depends_on": None,
        "active_attempt_id": "attempt-1",
        "attempt_history": ["attempt-1"],
        "latest_phase_status": {"submit_grade": "completed"},
        "latest_summary": {"code_pct": 100.0, "written_pct": None, "review_pct": 88.0},
        "updated_at": "2026-03-30T18:20:00Z",
    }))
    (attempt_dir / "attempt.json").write_text(json.dumps({
        "attempt_id": "attempt-1",
        "assignment_id": "comp140_circles",
        "workspace_id": "comp140_module1_circles",
        "model_key": "gpt-5-3-codex",
        "agent": "claude",
        "phases": {"agent": {"status": "completed", "metrics": {"cost_usd": 1.25}}},
        "final_summary": {"code_pct": 100.0, "written_pct": None, "review_pct": 88.0},
    }))

    monkeypatch.setattr(sys.modules["_state_store"], "STATE_ROOT", state_root)
    monkeypatch.setattr(bench_cli_module, "_list_registry_assignments", lambda model_key: [{
        "assignment_id": "comp140_circles",
    }, {
        "assignment_id": "comp140_polygons",
    }])

    rc = bench_cli_module.main_v2_summarize_model(["gpt-5-3-codex"])

    captured = capsys.readouterr()
    assert rc == 0
    assert "Coverage:  1/2 assignments" in captured.out
    assert "comp140_circles" in captured.out
    assert "comp140_polygons" in captured.out


def test_v2_export_model_writes_json_and_tar(tmp_path, monkeypatch, bench_cli_module):
    state_root = tmp_path / "state"
    export_root = tmp_path / "exports"
    assignment_root = state_root / "models" / "gpt-5-3-codex" / "assignments" / "comp140_circles"
    attempt_dir = assignment_root / "attempts" / "attempt-1"
    attempt_dir.mkdir(parents=True)
    (assignment_root / "manifest.json").write_text(json.dumps({
        "assignment_id": "comp140_circles",
        "workspace_id": "comp140_module1_circles",
        "model_key": "gpt-5-3-codex",
        "agent": "claude",
        "course": "comp140",
        "assignment_number": 1,
        "assignment_name": "circles",
        "display_name": "Circles",
        "assignment_mode": "code",
        "language": "python",
        "depends_on": None,
        "active_attempt_id": "attempt-1",
        "attempt_history": ["attempt-1"],
        "latest_phase_status": {"agent": "completed", "submit_grade": "completed"},
        "latest_summary": {"code_pct": 100.0, "written_pct": None, "review_pct": 88.0},
        "updated_at": "2026-03-30T18:20:00Z",
    }))
    (attempt_dir / "attempt.json").write_text(json.dumps({
        "attempt_id": "attempt-1",
        "assignment_id": "comp140_circles",
        "workspace_id": "comp140_module1_circles",
        "model_key": "gpt-5-3-codex",
        "agent": "claude",
        "phases": {"agent": {"status": "completed", "metrics": {"cost_usd": 1.25}}},
        "final_summary": {"code_pct": 100.0, "written_pct": None, "review_pct": 88.0},
    }))
    (attempt_dir / "agent_trace.jsonl").write_text('{"type":"result"}\n')

    monkeypatch.setattr(sys.modules["_state_store"], "STATE_ROOT", state_root)
    monkeypatch.setattr(bench_cli_module, "PROJECT_ROOT", tmp_path)
    monkeypatch.setattr(bench_cli_module, "_list_registry_assignments", lambda model_key: [{
        "assignment_id": "comp140_circles",
        "course": "comp140",
        "display_name": "Circles",
    }])

    rc = bench_cli_module.main_v2_export_model(["gpt-5-3-codex"])

    assert rc == 0
    latest_json = export_root / "models" / "gpt-5-3-codex" / "latest.json"
    latest_tar = export_root / "models" / "gpt-5-3-codex" / "latest.tar.gz"
    assert latest_json.exists()
    assert latest_tar.exists()

    data = json.loads(latest_json.read_text())
    assert data["coverage"]["completed_assignments"] == 1
    assert data["assignments"][0]["artifact_paths"]["attempt_dir"].endswith("attempts/attempt-1")

    with tarfile.open(latest_tar, "r:gz") as tar:
        names = tar.getnames()
    assert "gpt-5-3-codex/latest.json" in names
    assert "gpt-5-3-codex/assignments/comp140_circles/manifest.json" in names


def test_v2_run_course_executes_registry_assignments(tmp_path, monkeypatch, bench_cli_module):
    state_root = tmp_path / "state"
    monkeypatch.setattr(sys.modules["_state_store"], "STATE_ROOT", state_root)

    registry_items = [
        {
            "assignment_id": "comp140_circles",
            "workspace_id": "comp140_module1_circles",
            "workspace_dir": str(tmp_path / "ws1"),
            "course": "comp140",
            "assignment_number": 1,
            "assignment_name": "circles",
            "display_name": "Circles",
            "assignment_mode": "code",
            "language": "python",
            "depends_on": None,
            "model_key": "gpt-5-3-codex",
        },
        {
            "assignment_id": "comp140_polygons",
            "workspace_id": "comp140_module2_polygons",
            "workspace_dir": str(tmp_path / "ws2"),
            "course": "comp140",
            "assignment_number": 2,
            "assignment_name": "polygons",
            "display_name": "Polygons",
            "assignment_mode": "code",
            "language": "python",
            "depends_on": None,
            "model_key": "gpt-5-3-codex",
        },
        {
            "assignment_id": "comp215_hw1",
            "workspace_id": "comp215_hw1",
            "workspace_dir": str(tmp_path / "ws3"),
            "course": "comp215",
            "assignment_number": 1,
            "assignment_name": "hw1",
            "display_name": "HW1",
            "assignment_mode": "code",
            "language": "java",
            "depends_on": None,
            "model_key": "gpt-5-3-codex",
        },
    ]
    monkeypatch.setattr(bench_cli_module, "_list_registry_assignments", lambda model_key: registry_items)

    seen = []

    def _fake_run_assignment_attempt(assignment, ws, args, run_record, trigger_kind="manual_run"):  # noqa: ARG001
        seen.append((assignment["assignment_id"], run_record["run_id"]))
        return {
            "ok": True,
            "attempt": {"attempt_id": assignment["assignment_id"] + "-attempt"},
            "attempt_dir": Path(tmp_path / "state" / assignment["assignment_id"]),
            "final_summary": {"code_pct": 100.0, "written_pct": None, "review_pct": None},
        }

    monkeypatch.setattr(bench_cli_module, "_run_v2_assignment_attempt", _fake_run_assignment_attempt)

    rc = bench_cli_module.main_v2_run_course([
        "comp140",
        "--model-key", "gpt-5-3-codex",
    ])

    assert rc == 0
    assert [item[0] for item in seen] == ["comp140_circles", "comp140_polygons"]
    assert len({item[1] for item in seen}) == 1


def test_v2_run_corpus_executes_all_registry_assignments(tmp_path, monkeypatch, bench_cli_module):
    state_root = tmp_path / "state"
    monkeypatch.setattr(sys.modules["_state_store"], "STATE_ROOT", state_root)

    registry_items = [
        {
            "assignment_id": "comp140_circles",
            "workspace_id": "comp140_module1_circles",
            "workspace_dir": str(tmp_path / "ws1"),
            "course": "comp140",
            "assignment_number": 1,
            "assignment_name": "circles",
            "display_name": "Circles",
            "assignment_mode": "code",
            "language": "python",
            "depends_on": None,
            "model_key": "gpt-5-3-codex",
        },
        {
            "assignment_id": "comp215_hw1",
            "workspace_id": "comp215_hw1",
            "workspace_dir": str(tmp_path / "ws2"),
            "course": "comp215",
            "assignment_number": 1,
            "assignment_name": "hw1",
            "display_name": "HW1",
            "assignment_mode": "code",
            "language": "java",
            "depends_on": None,
            "model_key": "gpt-5-3-codex",
        },
    ]
    monkeypatch.setattr(bench_cli_module, "_list_registry_assignments", lambda model_key: registry_items)

    seen = []

    def _fake_run_assignment_attempt(assignment, ws, args, run_record, trigger_kind="manual_run"):  # noqa: ARG001
        seen.append((assignment["assignment_id"], run_record["run_id"]))
        return {
            "ok": True,
            "attempt": {"attempt_id": assignment["assignment_id"] + "-attempt"},
            "attempt_dir": Path(tmp_path / "state" / assignment["assignment_id"]),
            "final_summary": {"code_pct": 100.0, "written_pct": None, "review_pct": None},
        }

    monkeypatch.setattr(bench_cli_module, "_run_v2_assignment_attempt", _fake_run_assignment_attempt)

    rc = bench_cli_module.main_v2_run_corpus([
        "--model-key", "gpt-5-3-codex",
    ])

    assert rc == 0
    assert [item[0] for item in seen] == ["comp140_circles", "comp215_hw1"]
    assert len({item[1] for item in seen}) == 1


def test_v2_phase_llm_grade_corpus_executes_active_attempts(tmp_path, monkeypatch, bench_cli_module):
    state_root = tmp_path / "state"
    monkeypatch.setattr(sys.modules["_state_store"], "STATE_ROOT", state_root)

    registry_items = [
        {
            "assignment_id": "comp140_circles",
            "workspace_id": "comp140_module1_circles",
            "workspace_dir": str(tmp_path / "ws1"),
            "course": "comp140",
            "assignment_number": 1,
            "assignment_name": "circles",
            "display_name": "Circles",
            "assignment_mode": "code",
            "language": "python",
            "depends_on": None,
            "model_key": "gpt-5-3-codex",
        },
        {
            "assignment_id": "comp215_hw1",
            "workspace_id": "comp215_hw1",
            "workspace_dir": str(tmp_path / "ws2"),
            "course": "comp215",
            "assignment_number": 1,
            "assignment_name": "hw1",
            "display_name": "HW1",
            "assignment_mode": "code",
            "language": "java",
            "depends_on": None,
            "model_key": "gpt-5-3-codex",
        },
    ]
    monkeypatch.setattr(bench_cli_module, "_list_registry_assignments", lambda model_key: registry_items)

    for item in registry_items:
        assignment_root = state_root / "models" / "gpt-5-3-codex" / "assignments" / item["assignment_id"]
        attempt_dir = assignment_root / "attempts" / "attempt-1"
        attempt_dir.mkdir(parents=True)
        (assignment_root / "manifest.json").write_text(json.dumps({
            "assignment_id": item["assignment_id"],
            "workspace_id": item["workspace_id"],
            "model_key": "gpt-5-3-codex",
            "agent": "claude",
            "course": item["course"],
            "assignment_number": item["assignment_number"],
            "assignment_name": item["assignment_name"],
            "display_name": item["display_name"],
            "assignment_mode": item["assignment_mode"],
            "language": item["language"],
            "depends_on": None,
            "active_attempt_id": "attempt-1",
            "attempt_history": ["attempt-1"],
            "latest_phase_status": {"agent": "completed", "submit_grade": "completed"},
            "latest_summary": {"code_pct": 100.0, "written_pct": None, "review_pct": None},
            "updated_at": "2026-03-30T18:20:00Z",
        }))
        (attempt_dir / "attempt.json").write_text(json.dumps({
            "attempt_id": "attempt-1",
            "assignment_id": item["assignment_id"],
            "workspace_id": item["workspace_id"],
            "model_key": "gpt-5-3-codex",
            "agent": "claude",
            "phases": {
                "agent": {"status": "completed", "metrics": {"cost_usd": 1.0}},
                "submit_grade": {"status": "completed", "summary": {
                    "tests_passed": 1, "tests_total": 1,
                    "points_earned": 1.0, "points_possible": 1.0,
                }},
                "llm_grade": {"status": "not_started"},
                "code_review": {"status": "not_started"},
            },
            "final_summary": {"code_pct": 100.0, "written_pct": None, "review_pct": None},
        }))

    seen = []

    def _fake_run_llm_grade_phase(ctx, args):  # noqa: ARG001
        seen.append(ctx["assignment"]["assignment_id"])
        (ctx["attempt_dir"] / "llm_grade_result_claude.md").write_text("Points: 9/10\n")
        ctx = bench_cli_module._set_phase_status(ctx, "llm_grade", {
            "status": "completed",
            "started_at": "2026-03-30T18:21:00Z",
            "completed_at": "2026-03-30T18:21:01Z",
            "artifacts": {"reviews": ["llm_grade_result_claude.md"]},
            "summary": {"result": "9/10"},
        })
        return ctx, "9/10"

    monkeypatch.setattr(bench_cli_module, "_run_llm_grade_phase_v2", _fake_run_llm_grade_phase)

    rc = bench_cli_module.main_v2_phase([
        "corpus",
        "--model-key", "gpt-5-3-codex",
    ], "llm-grade")

    assert rc == 0
    assert seen == ["comp140_circles", "comp215_hw1"]


def test_v2_phase_code_review_course_executes_active_attempts(tmp_path, monkeypatch, bench_cli_module):
    state_root = tmp_path / "state"
    monkeypatch.setattr(sys.modules["_state_store"], "STATE_ROOT", state_root)

    registry_items = [
        {
            "assignment_id": "comp140_circles",
            "workspace_id": "comp140_module1_circles",
            "workspace_dir": str(tmp_path / "ws1"),
            "course": "comp140",
            "assignment_number": 1,
            "assignment_name": "circles",
            "display_name": "Circles",
            "assignment_mode": "code",
            "language": "python",
            "depends_on": None,
            "model_key": "gpt-5-3-codex",
        },
        {
            "assignment_id": "comp140_polygons",
            "workspace_id": "comp140_module2_polygons",
            "workspace_dir": str(tmp_path / "ws2"),
            "course": "comp140",
            "assignment_number": 2,
            "assignment_name": "polygons",
            "display_name": "Polygons",
            "assignment_mode": "code",
            "language": "python",
            "depends_on": None,
            "model_key": "gpt-5-3-codex",
        },
        {
            "assignment_id": "comp215_hw1",
            "workspace_id": "comp215_hw1",
            "workspace_dir": str(tmp_path / "ws3"),
            "course": "comp215",
            "assignment_number": 1,
            "assignment_name": "hw1",
            "display_name": "HW1",
            "assignment_mode": "code",
            "language": "java",
            "depends_on": None,
            "model_key": "gpt-5-3-codex",
        },
    ]
    monkeypatch.setattr(bench_cli_module, "_list_registry_assignments", lambda model_key: registry_items)

    for item in registry_items:
        assignment_root = state_root / "models" / "gpt-5-3-codex" / "assignments" / item["assignment_id"]
        attempt_dir = assignment_root / "attempts" / "attempt-1"
        attempt_dir.mkdir(parents=True)
        (assignment_root / "manifest.json").write_text(json.dumps({
            "assignment_id": item["assignment_id"],
            "workspace_id": item["workspace_id"],
            "model_key": "gpt-5-3-codex",
            "agent": "claude",
            "course": item["course"],
            "assignment_number": item["assignment_number"],
            "assignment_name": item["assignment_name"],
            "display_name": item["display_name"],
            "assignment_mode": item["assignment_mode"],
            "language": item["language"],
            "depends_on": None,
            "active_attempt_id": "attempt-1",
            "attempt_history": ["attempt-1"],
            "latest_phase_status": {"agent": "completed", "submit_grade": "completed"},
            "latest_summary": {"code_pct": 100.0, "written_pct": None, "review_pct": None},
            "updated_at": "2026-03-30T18:20:00Z",
        }))
        (attempt_dir / "attempt.json").write_text(json.dumps({
            "attempt_id": "attempt-1",
            "assignment_id": item["assignment_id"],
            "workspace_id": item["workspace_id"],
            "model_key": "gpt-5-3-codex",
            "agent": "claude",
            "phases": {
                "agent": {"status": "completed", "metrics": {"cost_usd": 1.0}},
                "submit_grade": {"status": "completed", "summary": {
                    "tests_passed": 1, "tests_total": 1,
                    "points_earned": 1.0, "points_possible": 1.0,
                }},
                "llm_grade": {"status": "not_started"},
                "code_review": {"status": "not_started"},
            },
            "final_summary": {"code_pct": 100.0, "written_pct": None, "review_pct": None},
        }))

    seen = []

    def _fake_run_code_review_phase(ctx, args):  # noqa: ARG001
        seen.append(ctx["assignment"]["assignment_id"])
        (ctx["attempt_dir"] / "code_review_result_claude.md").write_text("Overall Score: 80/100\n")
        ctx = bench_cli_module._set_phase_status(ctx, "code_review", {
            "status": "completed",
            "started_at": "2026-03-30T18:22:00Z",
            "completed_at": "2026-03-30T18:22:01Z",
            "artifacts": {"reviews": ["code_review_result_claude.md"]},
            "summary": {"result": "80/100"},
        })
        return ctx, "80/100"

    monkeypatch.setattr(bench_cli_module, "_run_code_review_phase_v2", _fake_run_code_review_phase)

    rc = bench_cli_module.main_v2_phase([
        "course",
        "comp140",
        "--model-key", "gpt-5-3-codex",
    ], "code-review")

    assert rc == 0
    assert seen == ["comp140_circles", "comp140_polygons"]
