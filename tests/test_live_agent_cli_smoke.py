"""Live smoke tests for Gemini and Opencode CLIs.

These are excluded from the default pytest run via pytest.ini because they make
real API calls and require local credentials. Run them explicitly with:

    python3 -m pytest -m live_cli tests/test_live_agent_cli_smoke.py -q
"""

from __future__ import annotations

import json
import os
import subprocess
from pathlib import Path

import pytest


PROJECT_ROOT = Path(__file__).resolve().parent.parent
OPENCODE_AUTH_FILE = Path.home() / ".local" / "share" / "opencode" / "auth.json"


def _require_gemini_credentials() -> None:
    if any(os.environ.get(name) for name in (
        "GEMINI_API_KEY",
        "GOOGLE_API_KEY",
        "GOOGLE_APPLICATION_CREDENTIALS",
    )):
        return
    pytest.fail(
        "Gemini smoke test requires GEMINI_API_KEY, GOOGLE_API_KEY, "
        "or GOOGLE_APPLICATION_CREDENTIALS."
    )


def _require_opencode_credentials() -> str:
    if os.environ.get("OPENCODE_CONFIG_CONTENT"):
        model = os.environ.get("OPENCODE_SMOKE_MODEL")
        if model:
            return model
        pytest.fail(
            "Opencode smoke test requires OPENCODE_SMOKE_MODEL when "
            "OPENCODE_CONFIG_CONTENT is used."
        )

    if not OPENCODE_AUTH_FILE.exists():
        pytest.fail(
            f"Opencode smoke test requires {OPENCODE_AUTH_FILE} or "
            "OPENCODE_CONFIG_CONTENT."
        )

    model = os.environ.get("OPENCODE_SMOKE_MODEL")
    if not model:
        pytest.fail(
            "Opencode smoke test requires OPENCODE_SMOKE_MODEL so the test "
            "does not assume a provider/model that may not exist locally."
        )
    return model


@pytest.mark.live_cli
def test_gemini_cli_headless_smoke():
    _require_gemini_credentials()

    result = subprocess.run(
        ["gemini", "--output-format", "text", "--model", "flash", "--yolo", "--prompt", ""],
        input="Reply with exactly OK",
        capture_output=True,
        text=True,
        cwd=str(PROJECT_ROOT),
        timeout=60,
    )

    assert result.returncode == 0, result.stderr[-2000:]
    assert result.stdout.strip() == "OK", result.stdout[-2000:]


@pytest.mark.live_cli
def test_opencode_cli_json_smoke():
    model = _require_opencode_credentials()

    env = os.environ.copy()
    env["OPENCODE_PERMISSION"] = '"allow"'

    result = subprocess.run(
        ["opencode", "run", "--format", "json", "--model", model],
        input="Reply with exactly OK",
        capture_output=True,
        text=True,
        cwd=str(PROJECT_ROOT),
        timeout=60,
        env=env,
    )

    assert result.returncode == 0, result.stderr[-2000:]

    events = []
    for line in result.stdout.splitlines():
        line = line.strip()
        if not line:
            continue
        events.append(json.loads(line))

    assert events, "Opencode produced no JSON events."
    error_events = [event for event in events if event.get("type") == "error"]
    assert not error_events, json.dumps(error_events[-1], indent=2) if error_events else ""

    text_fragments = [
        event.get("text", "")
        for event in events
        if event.get("type") == "text"
    ]
    combined_text = "".join(text_fragments).strip()
    assert combined_text == "OK", combined_text
