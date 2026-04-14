"""
Agent Evaluation Framework

A generic framework for evaluating AI agents on programming assignments.
Course-specific tests and configurations are kept separate from this core framework.
"""

from .config import ModuleConfig, load_module_config
from shared.results import TestResult, GradeResult, TestStatus
from .tracker import AttemptTracker
from .runner import TestRunner
from .sandbox import SafeImporter
from .yaml_utils import parse_simple_yaml

__version__ = "1.0.0"

__all__ = [
    "ModuleConfig",
    "load_module_config",
    "TestResult",
    "GradeResult",
    "TestStatus",
    "AttemptTracker",
    "TestRunner",
    "SafeImporter",
    "parse_simple_yaml",
]
