"""
Shared modules for all course runners.

This package contains common data structures and utilities used by
all course test runners (comp140 framework, comp310, comp215, comp321,
comp322, comp341, comp411, comp421).
"""

from .results import TestResult, GradeResult, TestStatus

__all__ = ["TestResult", "GradeResult", "TestStatus"]
