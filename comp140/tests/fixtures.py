"""
Shared test fixtures and utilities for COMP 140 tests.

This module provides:
- Test graph builders for Module 4 and 7
- Polynomial helpers for Module 5
- Matrix helpers for Module 6
- Common assertion utilities
"""

import sys
import os
import math
from pathlib import Path

# Add lib to path for helper modules (comp140/lib from comp140/tests/)
lib_path = Path(__file__).parent.parent / "lib"
if str(lib_path) not in sys.path:
    sys.path.insert(0, str(lib_path))


# ============================================================================
# Assertion Helpers
# ============================================================================

def approx_equal(a, b, tolerance=1e-6):
    """Check if two numbers are approximately equal."""
    if isinstance(a, (list, tuple)) and isinstance(b, (list, tuple)):
        if len(a) != len(b):
            return False
        return all(approx_equal(x, y, tolerance) for x, y in zip(a, b))
    return abs(a - b) < tolerance


def assert_approx(actual, expected, tolerance=1e-6, message=""):
    """Assert that two values are approximately equal."""
    if not approx_equal(actual, expected, tolerance):
        msg = f"Expected {expected}, got {actual}"
        if message:
            msg = f"{message}: {msg}"
        raise AssertionError(msg)


def assert_equal(actual, expected, message=""):
    """Assert that two values are equal."""
    if actual != expected:
        msg = f"Expected {expected}, got {actual}"
        if message:
            msg = f"{message}: {msg}"
        raise AssertionError(msg)


def assert_true(condition, message="Condition was False"):
    """Assert that a condition is True."""
    if not condition:
        raise AssertionError(message)


def assert_in_range(value, min_val, max_val, message=""):
    """Assert that a value is within a range."""
    if not (min_val <= value <= max_val):
        msg = f"Expected {value} to be in [{min_val}, {max_val}]"
        if message:
            msg = f"{message}: {msg}"
        raise AssertionError(msg)


# ============================================================================
# Module 1: Circles - Test Data
# ============================================================================

# Known circle test cases: (points, expected_center, expected_radius)
# All test cases ensure no two points share x or y coordinates
CIRCLE_TEST_CASES = [
    # Basic case: circle centered at (2, 1) with radius sqrt(5)
    {
        "points": [(0, 0), (4, 2), (3, 3)],
        "center": (2.0, 1.0),
        "radius": 2.23606797749979,  # sqrt(5)
        "name": "basic",
    },
    # Unit circle at origin
    {
        "points": [(1, 0), (0, 1), (0.6, -0.8)],
        "center": (0.0, 0.0),
        "radius": 1.0,
        "name": "unit",
    },
    # Circle centered at origin with radius 5
    {
        "points": [(3, 4), (4, 3), (-4, -3)],
        "center": (0.0, 0.0),
        "radius": 5.0,
        "name": "centered_at_origin",
    },
    # Offset circle centered at (2, 3) with radius sqrt(5)
    {
        "points": [(0, 2), (4, 4), (3, 5)],
        "center": (2.0, 3.0),
        "radius": 2.23606797749979,  # sqrt(5)
        "name": "offset",
    },
]

# Distance test cases: (point1, point2, expected_distance)
DISTANCE_TEST_CASES = [
    ((0, 0), (3, 4), 5.0),
    ((0, 0), (0, 0), 0.0),
    ((-1, -1), (2, 3), 5.0),
    ((1.5, 2.5), (4.5, 6.5), 5.0),
]

# Midpoint test cases: (point1, point2, expected_midpoint)
MIDPOINT_TEST_CASES = [
    ((0, 0), (4, 6), (2.0, 3.0)),
    ((0, 0), (0, 0), (0.0, 0.0)),
    ((-2, -4), (2, 4), (0.0, 0.0)),
    ((1.5, 2.5), (3.5, 4.5), (2.5, 3.5)),
]

# Slope test cases: (point1, point2, expected_slope)
SLOPE_TEST_CASES = [
    ((0, 0), (1, 1), 1.0),
    ((0, 0), (1, 2), 2.0),
    ((0, 0), (2, 1), 0.5),
    ((-1, -1), (1, 1), 1.0),
    ((0, 0), (1, -1), -1.0),
    ((0, 5), (10, 5), 0.0),  # horizontal line
]


# ============================================================================
# Module 2: Spot It! - Test Data
# ============================================================================

def projective_points_mod2():
    """Generate all projective points for mod 2."""
    return [
        (0, 0, 1), (0, 1, 0), (0, 1, 1),
        (1, 0, 0), (1, 0, 1), (1, 1, 0), (1, 1, 1),
    ]


def projective_points_mod3():
    """Generate all projective points for mod 3."""
    # There should be 3^2 + 3 + 1 = 13 unique points
    points = []
    for x in range(3):
        for y in range(3):
            for z in range(3):
                if (x, y, z) != (0, 0, 0):
                    points.append((x, y, z))
    # Remove duplicates based on equivalence
    # This is a simplified version - actual test will verify student's implementation
    return points


# ============================================================================
# Module 4 & 7: Graph Test Data
# ============================================================================

def build_simple_graph():
    """Build a simple undirected graph for Module 4 testing."""
    from comp140_module4_graphs import Graph
    g = Graph()
    g.add_node("A")
    g.add_node("B")
    g.add_node("C")
    g.add_edge("A", "B", {"movie": "Film1"})
    g.add_edge("B", "C", {"movie": "Film2"})
    return g


def build_line_graph_undirected(n=5):
    """Build an undirected line graph with n nodes."""
    from comp140_module4_graphs import Graph
    g = Graph()
    labels = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    for i in range(n - 1):
        g.add_edge(labels[i], labels[i + 1], {"movie": f"Film{i}"})
    return g


def build_complete_graph_undirected(n=4):
    """Build a complete undirected graph with n nodes."""
    from comp140_module4_graphs import Graph
    g = Graph()
    labels = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"[:n]
    for i in range(n):
        for j in range(i + 1, n):
            g.add_edge(labels[i], labels[j], {"movie": f"Film{i}{j}"})
    return g


def build_line_graph_directed(n=5):
    """Build a directed line graph with n nodes."""
    from comp140_module7_graphs import DiGraph
    g = DiGraph()
    labels = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    for i in range(n):
        g.add_node(labels[i])
        g.add_node_attr(labels[i], "x", i)
        g.add_node_attr(labels[i], "y", 0)
    for i in range(n - 1):
        g.add_edge(labels[i], labels[i + 1])
        g.add_edge_attr(labels[i], labels[i + 1], "dist", 1)
    return g


def build_grid_graph(width=3, height=3):
    """Build a directed grid graph."""
    from comp140_module7_graphs import DiGraph
    g = DiGraph()
    labels = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    # Add nodes
    for i in range(width * height):
        g.add_node(labels[i])
        g.add_node_attr(labels[i], "x", i % width)
        g.add_node_attr(labels[i], "y", i // width)

    # Add edges
    for i in range(width * height):
        x, y = i % width, i // width
        # Right neighbor
        if x < width - 1:
            g.add_edge(labels[i], labels[i + 1])
            g.add_edge_attr(labels[i], labels[i + 1], "dist", 1)
        # Down neighbor
        if y < height - 1:
            g.add_edge(labels[i], labels[i + width])
            g.add_edge_attr(labels[i], labels[i + width], "dist", 1)

    return g


def build_tree_graph():
    """Build a binary tree graph."""
    from comp140_module7_graphs import DiGraph
    g = DiGraph()
    labels = "ABCDEFGHIJKLMNO"

    # Add all nodes
    for i in range(7):  # Height 3 binary tree has 7 nodes
        g.add_node(labels[i])
        g.add_node_attr(labels[i], "x", i)
        g.add_node_attr(labels[i], "y", 0)

    # Add edges (parent to children)
    g.add_edge("A", "B")
    g.add_edge("A", "C")
    g.add_edge("B", "D")
    g.add_edge("B", "E")
    g.add_edge("C", "F")
    g.add_edge("C", "G")

    # Set distances
    for node in g.nodes():
        for nbr in g.get_neighbors(node):
            g.add_edge_attr(node, nbr, "dist", 1)

    return g


# ============================================================================
# Module 5: Polynomial / QR Code - Test Data
# ============================================================================

def get_z256_mul_table():
    """Get multiplication table entries for Z256 testing."""
    # Import z256 module
    from comp140_module5_z256 import mul, add, div, power

    # Test cases: (a, b, expected_product)
    return [
        (1, 1, 1),
        (2, 2, mul(2, 2)),
        (3, 7, mul(3, 7)),
        (255, 255, mul(255, 255)),
        (0, 100, 0),
        (100, 0, 0),
    ]


# ============================================================================
# Module 6: Sports Analytics - Test Data
# ============================================================================

def get_simple_regression_data():
    """Get simple data for linear regression testing."""
    # y = 2x + 1
    X = [[1], [2], [3], [4], [5]]
    y = [3, 5, 7, 9, 11]
    expected_coefficients = [2.0]  # slope
    expected_intercept = 1.0
    return X, y, expected_coefficients, expected_intercept


def get_multivariate_regression_data():
    """Get multivariate data for regression testing."""
    # y = 1*x1 + 2*x2 + 3
    X = [
        [1, 1],
        [2, 1],
        [1, 2],
        [2, 2],
        [3, 3],
    ]
    y = [6, 7, 8, 9, 12]
    return X, y


# ============================================================================
# Utility Functions
# ============================================================================

def make_test_result(passed, expected=None, actual=None):
    """Create a test result tuple for the runner."""
    return (passed, expected, actual)


def check_return_type(value, expected_type, type_name=None):
    """Check that a value has the expected type."""
    if type_name is None:
        type_name = expected_type.__name__
    if not isinstance(value, expected_type):
        raise AssertionError(
            f"Expected return type {type_name}, got {type(value).__name__}"
        )


def check_has_attribute(obj, attr_name):
    """Check that an object has the specified attribute."""
    if not hasattr(obj, attr_name):
        raise AssertionError(f"Object missing attribute: {attr_name}")


def check_callable(obj, method_name):
    """Check that an object has a callable method."""
    if not hasattr(obj, method_name):
        raise AssertionError(f"Object missing method: {method_name}")
    if not callable(getattr(obj, method_name)):
        raise AssertionError(f"{method_name} is not callable")
