"""
Configuration classes and utilities for the evaluation framework.

Supports both the legacy field names (module_number, homework_number, project_number)
and the new unified schema (assignment_number, assignment_name, display_name).
"""

import os
import json
from dataclasses import dataclass, field
from typing import List, Dict, Optional
from pathlib import Path
from datetime import datetime

from .config_compat import (
    normalize_config,
    get_assignment_number,
    get_assignment_name,
    get_display_name,
    get_timeout_seconds,
    get_total_points,
    get_total_tests,
    DEFAULT_TIMEOUT_PYTHON,
)

# Try to import yaml, but make it optional
try:
    import yaml
    HAS_YAML = True
except ImportError:
    HAS_YAML = False


@dataclass
class WorkspaceConfig:
    """Configuration for a single-task workspace.

    Supports both legacy and unified field names:
      - module_number / assignment_number
      - module_name / assignment_name
    """
    module_number: int
    module_name: str
    course: str
    created_at: str = ""
    language: str = ""  # Optional: python, java, c

    # Unified schema aliases (populated from legacy fields if not present)
    assignment_number: int = 0
    assignment_name: str = ""
    display_name: str = ""

    # Optional dependency on another workspace (for sequential assignments)
    depends_on: str = ""

    def __post_init__(self):
        if not self.created_at:
            self.created_at = datetime.now().isoformat()
        # Populate unified fields from legacy if not set
        if not self.assignment_number:
            self.assignment_number = self.module_number
        if not self.assignment_name:
            self.assignment_name = self.module_name
        if not self.display_name:
            self.display_name = self.module_name

    @classmethod
    def from_dict(cls, data: Dict) -> "WorkspaceConfig":
        """Create a WorkspaceConfig from a dictionary.

        Supports both legacy (module_number, module_name) and unified
        (assignment_number, assignment_name) field names.
        """
        # Get assignment number from any of the legacy field names
        module_number = get_assignment_number(data)
        if module_number is None:
            raise KeyError("Config must have assignment_number, module_number, homework_number, or project_number")

        # Get assignment name from any of the legacy field names
        module_name = get_assignment_name(data)
        if module_name is None:
            raise KeyError("Config must have assignment_name, module_name, homework_name, or project_name")

        return cls(
            module_number=module_number,
            module_name=module_name,
            course=data["course"],
            created_at=data.get("created_at", ""),
            language=data.get("language", ""),
            assignment_number=data.get("assignment_number", module_number),
            assignment_name=data.get("assignment_name", module_name),
            display_name=get_display_name(data) or module_name,
            depends_on=data.get("depends_on", ""),
        )

    def to_dict(self) -> Dict:
        """Convert config to dictionary.

        Includes both legacy and unified field names for compatibility.
        """
        result = {
            # Legacy field names (for backwards compatibility)
            "module_number": self.module_number,
            "module_name": self.module_name,
            "course": self.course,
            "created_at": self.created_at,
        }
        # Add optional fields if present
        if self.language:
            result["language"] = self.language
        # Add unified field names
        result["assignment_number"] = self.assignment_number
        result["assignment_name"] = self.assignment_name
        result["display_name"] = self.display_name
        # Add dependency if present
        if self.depends_on:
            result["depends_on"] = self.depends_on
        return result


def load_workspace_config(workspace_path: Path) -> WorkspaceConfig:
    """
    Load workspace configuration from workspace.yaml or workspace.json.

    Args:
        workspace_path: Path to the workspace directory

    Returns:
        WorkspaceConfig with the workspace's configuration

    Raises:
        FileNotFoundError: If neither workspace.yaml nor workspace.json exists
    """
    workspace_path = Path(workspace_path)
    yaml_file = workspace_path / "workspace.yaml"
    json_file = workspace_path / "workspace.json"

    # Try YAML first if available
    if HAS_YAML and yaml_file.exists():
        with open(yaml_file, "r") as f:
            data = yaml.safe_load(f)
        return WorkspaceConfig.from_dict(data)

    # Fall back to JSON
    if json_file.exists():
        with open(json_file, "r") as f:
            data = json.load(f)
        return WorkspaceConfig.from_dict(data)

    # Neither found
    raise FileNotFoundError(f"Workspace config not found: {workspace_path} (need workspace.yaml or workspace.json)")



@dataclass
class FunctionConfig:
    """Configuration for a single function to test."""
    name: str
    points: int = 1
    timeout_seconds: float = 10.0


@dataclass
class ModuleConfig:
    """Configuration for a COMP 140 module evaluation.

    Supports both legacy and unified field names:
      - module_number / assignment_number
      - module_name / assignment_name
      - timeout_per_test_seconds / grading.timeout_seconds
    """
    module_number: int
    module_name: str
    max_attempts: int = 10
    timeout_per_test_seconds: float = float(DEFAULT_TIMEOUT_PYTHON)
    total_points: int = 20
    functions: List[str] = field(default_factory=list)
    writeup_sections: List[str] = field(default_factory=list)

    # Derived paths
    template_filename: str = ""
    test_module_name: str = ""

    # Unified schema fields
    assignment_number: int = 0
    assignment_name: str = ""
    display_name: str = ""
    total_tests: int = 0

    def __post_init__(self):
        """Set derived attributes after initialization."""
        if not self.template_filename:
            # Use "solution.py" for agent workspaces (the file agents edit)
            # template.py is kept as a clean reference
            self.template_filename = "solution.py"
        if not self.test_module_name:
            self.test_module_name = f"test_module{self.module_number}"
        # Populate unified fields from legacy if not set
        if not self.assignment_number:
            self.assignment_number = self.module_number
        if not self.assignment_name:
            self.assignment_name = self.module_name
        if not self.display_name:
            self.display_name = self.module_name

    @classmethod
    def from_dict(cls, data: Dict) -> "ModuleConfig":
        """Create a ModuleConfig from a dictionary.

        Supports both legacy (module_number, module_name) and unified
        (assignment_number, assignment_name, grading section) field names.
        """
        # Get assignment number from any of the legacy field names
        module_number = get_assignment_number(data)
        if module_number is None:
            raise KeyError("Config must have assignment_number, module_number, homework_number, or project_number")

        # Get assignment name from any of the legacy field names
        module_name = get_assignment_name(data)
        if module_name is None:
            raise KeyError("Config must have assignment_name, module_name, homework_name, or project_name")

        return cls(
            module_number=module_number,
            module_name=module_name,
            max_attempts=data.get("max_attempts", 10),
            timeout_per_test_seconds=get_timeout_seconds(data, DEFAULT_TIMEOUT_PYTHON),
            total_points=get_total_points(data, 20),
            functions=data.get("functions", []),
            writeup_sections=data.get("writeup_sections", []),
            template_filename=data.get("template_filename", ""),
            test_module_name=data.get("test_module_name", ""),
            assignment_number=data.get("assignment_number", module_number),
            assignment_name=data.get("assignment_name", module_name),
            display_name=get_display_name(data) or module_name,
            total_tests=get_total_tests(data, 0),
        )

    def to_dict(self) -> Dict:
        """Convert config to dictionary.

        Includes both legacy and unified field names for compatibility.
        """
        return {
            # Legacy field names (for backwards compatibility)
            "module_number": self.module_number,
            "module_name": self.module_name,
            "max_attempts": self.max_attempts,
            "timeout_per_test_seconds": self.timeout_per_test_seconds,
            "total_points": self.total_points,
            "functions": self.functions,
            "writeup_sections": self.writeup_sections,
            "template_filename": self.template_filename,
            "test_module_name": self.test_module_name,
            # Unified field names
            "assignment_number": self.assignment_number,
            "assignment_name": self.assignment_name,
            "display_name": self.display_name,
            "total_tests": self.total_tests,
        }


def get_config_path(module_number: int, ext: str = "yaml", course_path: Optional[Path] = None) -> Path:
    """
    Get the path to a module's configuration file.

    Args:
        module_number: The module number
        ext: File extension (yaml or json)
        course_path: Path to the course directory containing configs/
    """
    if course_path is not None:
        return course_path / "configs" / f"module{module_number}.{ext}"
    # Fall back to framework directory (for backwards compatibility)
    eval_dir = Path(__file__).parent
    return eval_dir / "configs" / f"module{module_number}.{ext}"


def get_default_configs() -> Dict[int, ModuleConfig]:
    """
    Get default configurations for all modules.

    These are built-in defaults used when no config file exists.
    """
    return {
        1: ModuleConfig(
            module_number=1,
            module_name="Circles",
            total_points=20,
            functions=["distance", "midpoint", "slope", "perp", "intersect", "make_circle"],
            writeup_sections=["Recipe for make_circle", "Discussion: Alternative approaches"],
        ),
        2: ModuleConfig(
            module_number=2,
            module_name="Spot It!",
            total_points=22,
            functions=["equivalent", "incident", "generate_all_points", "create_cards"],
            writeup_sections=[
                "Cross product calculations",
                "Recipe for generate_all_points",
                "Recipe for create_cards",
                "Discussion: 40-card deck possibility",
            ],
        ),
        3: ModuleConfig(
            module_number=3,
            module_name="Stock Prediction",
            total_points=20,
            functions=["markov_chain", "predict", "mse", "run_experiments"],
            writeup_sections=["Recipe for markov_chain", "Analysis discussion"],
        ),
        4: ModuleConfig(
            module_number=4,
            module_name="Kevin Bacon",
            total_points=20,
            functions=["Queue", "bfs", "distance_histogram", "find_path", "play_kevin_bacon_game"],
            writeup_sections=["Recipe for find_path", "Discussion questions"],
        ),
        5: ModuleConfig(
            module_number=5,
            module_name="QR Code",
            total_points=33,
            functions=[
                "Polynomial.add_term", "Polynomial.subtract_term", "Polynomial.multiply_by_term",
                "Polynomial.add_polynomial", "Polynomial.subtract_polynomial",
                "Polynomial.multiply_by_polynomial", "Polynomial.remainder",
                "create_message_polynomial", "create_generator_polynomial", "reed_solomon_correction",
            ],
            writeup_sections=["Polynomial operation recipes", "Reed-Solomon discussion"],
        ),
        6: ModuleConfig(
            module_number=6,
            module_name="Sports Analytics",
            total_points=22,
            functions=["LinearModel", "read_matrix", "fit_least_squares", "fit_lasso"],
            writeup_sections=["Linear regression discussion", "LASSO analysis"],
        ),
        7: ModuleConfig(
            module_number=7,
            module_name="Map Search",
            total_points=27,
            functions=["Queue", "Stack", "bfs_dfs", "dfs", "astar"],
            writeup_sections=["BFS/DFS recipes", "A* analysis", "Algorithm comparison"],
        ),
    }


def load_module_config(module_number: int, course_path: Optional[Path] = None) -> ModuleConfig:
    """
    Load module configuration.

    First tries to load from YAML/JSON config file, then falls back to defaults.

    Args:
        module_number: The module number (e.g., 1-7 for COMP 140, 1-6 for COMP 321)
        course_path: Path to the course directory containing configs/

    Returns:
        ModuleConfig object with the module's configuration

    Raises:
        FileNotFoundError: If no config exists for the given module number
    """

    # Try YAML first if available
    if HAS_YAML:
        yaml_path = get_config_path(module_number, "yaml", course_path)
        if yaml_path.exists():
            with open(yaml_path, "r") as f:
                data = yaml.safe_load(f)
            return ModuleConfig.from_dict(data)

    # Try JSON
    json_path = get_config_path(module_number, "json", course_path)
    if json_path.exists():
        with open(json_path, "r") as f:
            data = json.load(f)
        return ModuleConfig.from_dict(data)

    # Fall back to defaults
    defaults = get_default_configs()
    if module_number in defaults:
        return defaults[module_number]

    raise FileNotFoundError(f"No config found for module {module_number}")
