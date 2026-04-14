"""
Helpers for reading and writing suite manifests.
"""

from __future__ import annotations

import json
import os
from pathlib import Path
from typing import Any


SUITE_MANIFEST_NAME = ".bscs_suite_manifest.json"


def suite_manifest_path(suite_dir: Path) -> Path:
    """Return the canonical manifest path for a materialized suite dir."""
    return suite_dir / SUITE_MANIFEST_NAME


def write_suite_manifest(suite_dir: Path, manifest: dict[str, Any]) -> Path:
    """Write a suite manifest into the suite directory."""
    path = suite_manifest_path(suite_dir)
    path.write_text(json.dumps(manifest, indent=2, sort_keys=True) + "\n")
    return path


def load_suite_manifest(
    *,
    suite_dir: Path | None = None,
    manifest_path: Path | None = None,
) -> dict[str, Any] | None:
    """Load a suite manifest from an explicit path or suite directory."""
    candidate = manifest_path
    if candidate is None and suite_dir is not None:
        candidate = suite_manifest_path(suite_dir)
    if candidate is None:
        suite_override = os.environ.get("BSCS_TEST_SUITE_DIR", "").strip()
        if suite_override:
            candidate = suite_manifest_path(Path(suite_override).resolve())
    if candidate is None or not candidate.exists():
        return None
    try:
        return json.loads(candidate.read_text())
    except Exception:
        return None


def _ensure_indexes(manifest: dict[str, Any]) -> None:
    """Lazily build O(1) lookup indexes on the manifest dict."""
    if "_by_path" not in manifest:
        entries = manifest.get("entries", [])
        manifest["_by_path"] = {
            e["relative_path"]: e.get("source")
            for e in entries if e.get("relative_path")
        }
        manifest["_by_class"] = {
            e["class_name"]: e.get("source")
            for e in entries if e.get("class_name")
        }


def source_from_manifest_path(manifest: dict[str, Any] | None, rel_path: str) -> str | None:
    """Resolve test source from a relative suite path."""
    if not manifest:
        return None
    _ensure_indexes(manifest)
    normalized = rel_path.replace("\\", "/").lstrip("./")
    return manifest["_by_path"].get(normalized)


def source_from_manifest_classname(manifest: dict[str, Any] | None, classname: str) -> str | None:
    """Resolve test source from a Java classname."""
    if not manifest or not classname:
        return None
    _ensure_indexes(manifest)
    short_name = classname.split(".")[-1]
    return manifest["_by_class"].get(short_name)
