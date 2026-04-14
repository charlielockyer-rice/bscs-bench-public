"""
Attempt tracking for the COMP 140 evaluation framework.
"""

import json
import hashlib
import os
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Optional
from dataclasses import dataclass, field

from shared.results import GradeResult


@dataclass
class AttemptRecord:
    """Record of a single grading attempt."""
    attempt_number: int
    timestamp: str
    code_hash: str
    tests_passed: int
    tests_total: int
    points_earned: float
    points_possible: float
    pass_rate: float
    score_percentage: float
    errors: List[str] = field(default_factory=list)

    def to_dict(self) -> Dict:
        """Convert to dictionary."""
        return {
            "attempt_number": self.attempt_number,
            "timestamp": self.timestamp,
            "code_hash": self.code_hash,
            "tests_passed": self.tests_passed,
            "tests_total": self.tests_total,
            "points_earned": self.points_earned,
            "points_possible": self.points_possible,
            "pass_rate": self.pass_rate,
            "score_percentage": self.score_percentage,
            "errors": self.errors,
        }

    @classmethod
    def from_dict(cls, data: Dict) -> "AttemptRecord":
        """Create from dictionary."""
        return cls(
            attempt_number=data["attempt_number"],
            timestamp=data["timestamp"],
            code_hash=data["code_hash"],
            tests_passed=data["tests_passed"],
            tests_total=data["tests_total"],
            points_earned=data["points_earned"],
            points_possible=data["points_possible"],
            pass_rate=data["pass_rate"],
            score_percentage=data["score_percentage"],
            errors=data.get("errors", []),
        )


class AttemptTracker:
    """
    Tracks grading attempts for a module in a workspace.

    Stores attempt history in a JSON file, including:
    - Attempt number
    - Timestamp
    - Code hash (to detect if code changed)
    - Score summary
    - Error messages
    """

    def __init__(self, workspace_path: Path, module_number: int, flat_workspace: bool = False):
        """
        Initialize the tracker.

        Args:
            workspace_path: Path to the agent's workspace
            module_number: The module being tracked (1-7)
            flat_workspace: If True, uses attempts.json at workspace root
        """
        self.workspace_path = Path(workspace_path)
        self.module_number = module_number
        self.flat_workspace = flat_workspace

        if flat_workspace:
            # New flat workspace: attempts.json at workspace root
            self.logs_dir = self.workspace_path
            self.attempts_file = self.workspace_path / "attempts.json"
        else:
            # Legacy: logs/module{n}_attempts.json
            self.logs_dir = self.workspace_path / "logs"
            self.attempts_file = self.logs_dir / f"module{module_number}_attempts.json"

        self._attempts: List[AttemptRecord] = []
        self._load()

    @classmethod
    def from_workspace(cls, workspace_path: Path) -> "AttemptTracker":
        """
        Create an AttemptTracker from a flat single-task workspace.

        Reads workspace.yaml to determine module number.

        Args:
            workspace_path: Path to the workspace directory

        Returns:
            AttemptTracker configured for the workspace
        """
        from .config import load_workspace_config

        workspace_path = Path(workspace_path)
        ws_config = load_workspace_config(workspace_path)

        return cls(
            workspace_path=workspace_path,
            module_number=ws_config.module_number,
            flat_workspace=True,
        )

    def _load(self):
        """Load existing attempts from file."""
        if self.attempts_file.exists():
            with open(self.attempts_file, "r") as f:
                data = json.load(f)
                self._attempts = [
                    AttemptRecord.from_dict(a) for a in data.get("attempts", [])
                ]

    def _save(self):
        """Save attempts to file.

        Flat workspaces write to: {workspace}/attempts.json
        Legacy workspaces write to: {workspace}/logs/module{n}_attempts.json
        """
        if not self.flat_workspace:
            # Legacy format stores attempts in logs/ subdirectory
            self.logs_dir.mkdir(parents=True, exist_ok=True)
        with open(self.attempts_file, "w") as f:
            json.dump({
                "module_number": self.module_number,
                "attempts": [a.to_dict() for a in self._attempts],
            }, f, indent=2)

    @property
    def attempt_count(self) -> int:
        """Number of attempts made."""
        return len(self._attempts)

    @property
    def attempts(self) -> List[AttemptRecord]:
        """List of all attempt records."""
        return self._attempts.copy()

    @property
    def latest_attempt(self) -> Optional[AttemptRecord]:
        """Most recent attempt, or None if no attempts."""
        return self._attempts[-1] if self._attempts else None

    @property
    def best_attempt(self) -> Optional[AttemptRecord]:
        """Attempt with highest score, or None if no attempts."""
        if not self._attempts:
            return None
        return max(self._attempts, key=lambda a: a.score_percentage)

    def compute_code_hash(self, code_path: Path) -> str:
        """
        Compute a hash of the code file.

        Args:
            code_path: Path to the code file

        Returns:
            SHA256 hash of the file contents
        """
        with open(code_path, "rb") as f:
            return hashlib.sha256(f.read()).hexdigest()[:16]

    def record_attempt(self, grade_result: GradeResult, code_path: Path) -> AttemptRecord:
        """
        Record a new grading attempt.

        Args:
            grade_result: The result of grading
            code_path: Path to the submitted code

        Returns:
            The created AttemptRecord
        """
        # Collect error messages from failed tests
        errors = []
        for result in grade_result.test_results:
            if not result.passed and result.error_message:
                errors.append(f"{result.name}: {result.error_message}")

        record = AttemptRecord(
            attempt_number=self.attempt_count + 1,
            timestamp=datetime.now().isoformat(),
            code_hash=self.compute_code_hash(code_path),
            tests_passed=grade_result.passed_count,
            tests_total=grade_result.total_count,
            points_earned=grade_result.total_points,
            points_possible=grade_result.max_points,
            pass_rate=grade_result.pass_rate,
            score_percentage=grade_result.score_percentage,
            errors=errors[:10],  # Keep at most 10 error messages
        )

        self._attempts.append(record)
        self._save()
        return record

    def can_attempt(self, max_attempts: int) -> bool:
        """
        Check if more attempts are allowed.

        Args:
            max_attempts: Maximum allowed attempts

        Returns:
            True if another attempt is allowed
        """
        return self.attempt_count < max_attempts

    def get_summary(self) -> str:
        """Get a summary of all attempts."""
        if not self._attempts:
            return "No attempts recorded yet."

        header = "Attempt History" if self.flat_workspace else f"Module {self.module_number} Attempt History"
        lines = [
            header,
            "-" * 40,
        ]

        for attempt in self._attempts:
            lines.append(
                f"  #{attempt.attempt_number}: {attempt.score_percentage:.1f}% "
                f"({attempt.tests_passed}/{attempt.tests_total} tests) - "
                f"{attempt.timestamp[:19]}"
            )

        best = self.best_attempt
        if best:
            lines.append("")
            lines.append(f"Best score: {best.score_percentage:.1f}% (Attempt #{best.attempt_number})")

        return "\n".join(lines)
