"""
Tests for Module 1: Circles

Tests the following functions:
- distance(point0x, point0y, point1x, point1y)
- midpoint(point0x, point0y, point1x, point1y)
- slope(point0x, point0y, point1x, point1y)
- perp(lineslope)
- intersect(slope0, point0x, point0y, slope1, point1x, point1y)
- make_circle(point0x, point0y, point1x, point1y, point2x, point2y)
"""

import math
import sys
from pathlib import Path

# Add tests dir to path for fixtures import
_tests_dir = Path(__file__).parent
if str(_tests_dir) not in sys.path:
    sys.path.insert(0, str(_tests_dir))

from fixtures import approx_equal, make_test_result

# Point values for each test
TEST_POINTS = {
    "test_distance_basic": 1,
    "test_distance_same_point": 1,
    "test_distance_negative": 1,
    "test_distance_float": 1,
    "test_midpoint_basic": 1,
    "test_midpoint_same_point": 1,
    "test_midpoint_negative": 1,
    "test_midpoint_float": 1,
    "test_slope_basic": 1,
    "test_slope_negative": 1,
    "test_slope_steep": 1,
    "test_slope_float": 1,
    "test_slope_horizontal": 1,
    "test_perp_basic": 1,
    "test_perp_negative": 1,
    "test_perp_steep": 1,
    "test_perp_float": 1,
    "test_intersect_basic": 1,
    "test_intersect_perpendicular": 1,
    "test_intersect_negative": 1,
    "test_intersect_float": 1,
    "test_make_circle_basic": 1,
    "test_make_circle_unit": 1,
    "test_make_circle_centered_origin": 1,
    "test_make_circle_offset": 1,
}


# ============================================================================
# Distance Tests
# ============================================================================

def test_distance_basic(module):
    """Test distance with basic 3-4-5 triangle."""
    result = module.distance(0, 0, 3, 4)
    expected = 5.0
    return make_test_result(approx_equal(result, expected), expected, result)

test_distance_basic.input_description = "point0=(0, 0), point1=(3, 4)"


def test_distance_same_point(module):
    """Test distance between identical points."""
    result = module.distance(5, 5, 5, 5)
    expected = 0.0
    return make_test_result(approx_equal(result, expected), expected, result)

test_distance_same_point.input_description = "point0=(5, 5), point1=(5, 5)"


def test_distance_negative(module):
    """Test distance with negative coordinates."""
    result = module.distance(-1, -1, 2, 3)
    expected = 5.0
    return make_test_result(approx_equal(result, expected), expected, result)

test_distance_negative.input_description = "point0=(-1, -1), point1=(2, 3)"


def test_distance_float(module):
    """Test distance with float coordinates."""
    result = module.distance(0.5, 0.5, 3.5, 4.5)
    expected = 5.0
    return make_test_result(approx_equal(result, expected), expected, result)

test_distance_float.input_description = "point0=(0.5, 0.5), point1=(3.5, 4.5)"


# ============================================================================
# Midpoint Tests
# ============================================================================

def test_midpoint_basic(module):
    """Test midpoint with basic points."""
    result = module.midpoint(0, 0, 4, 6)
    expected = (2.0, 3.0)
    passed = approx_equal(result[0], expected[0]) and approx_equal(result[1], expected[1])
    return make_test_result(passed, expected, result)

test_midpoint_basic.input_description = "point0=(0, 0), point1=(4, 6)"


# ============================================================================
# Distance Tests
# ============================================================================

def test_midpoint_same_point(module):
    """Test midpoint of identical points."""
    result = module.midpoint(5, 5, 5, 5)
    expected = (5.0, 5.0)
    passed = approx_equal(result[0], expected[0]) and approx_equal(result[1], expected[1])
    return make_test_result(passed, expected, result)

test_midpoint_same_point.input_description = "point0=(5, 5), point1=(5, 5)"


def test_midpoint_negative(module):
    """Test midpoint with negative coordinates."""
    result = module.midpoint(-2, -4, 2, 4)
    expected = (0.0, 0.0)
    passed = approx_equal(result[0], expected[0]) and approx_equal(result[1], expected[1])
    return make_test_result(passed, expected, result)

test_midpoint_negative.input_description = "point0=(-2, -4), point1=(2, 4)"


def test_midpoint_float(module):
    """Test midpoint with float coordinates."""
    result = module.midpoint(1.5, 2.5, 3.5, 4.5)
    expected = (2.5, 3.5)
    passed = approx_equal(result[0], expected[0]) and approx_equal(result[1], expected[1])
    return make_test_result(passed, expected, result)

test_midpoint_float.input_description = "point0=(1.5, 2.5), point1=(3.5, 4.5)"


# ============================================================================
# Slope Tests
# ============================================================================

def test_slope_basic(module):
    """Test slope with 45-degree line."""
    result = module.slope(0, 0, 1, 1)
    expected = 1.0
    return make_test_result(approx_equal(result, expected), expected, result)

test_slope_basic.input_description = "point0=(0, 0), point1=(1, 1)"


def test_slope_negative(module):
    """Test slope with negative slope."""
    result = module.slope(0, 0, 1, -1)
    expected = -1.0
    return make_test_result(approx_equal(result, expected), expected, result)

test_slope_negative.input_description = "point0=(0, 0), point1=(1, -1)"


def test_slope_steep(module):
    """Test slope with steep line."""
    result = module.slope(0, 0, 1, 5)
    expected = 5.0
    return make_test_result(approx_equal(result, expected), expected, result)

test_slope_steep.input_description = "point0=(0, 0), point1=(1, 5)"


def test_slope_float(module):
    """Test slope with fractional result."""
    result = module.slope(0, 0, 4, 1)
    expected = 0.25
    return make_test_result(approx_equal(result, expected), expected, result)

test_slope_float.input_description = "point0=(0, 0), point1=(4, 1)"


def test_slope_horizontal(module):
    """Test slope with horizontal line (slope=0)."""
    result = module.slope(0, 5, 10, 5)
    expected = 0.0
    return make_test_result(approx_equal(result, expected), expected, result)

test_slope_horizontal.input_description = "point0=(0, 5), point1=(10, 5)"


# ============================================================================
# Perpendicular Slope Tests
# ============================================================================

def test_perp_basic(module):
    """Test perpendicular slope of slope=1."""
    result = module.perp(1)
    expected = -1.0
    return make_test_result(approx_equal(result, expected), expected, result)

test_perp_basic.input_description = "slope=1"


def test_perp_negative(module):
    """Test perpendicular slope of slope=-1."""
    result = module.perp(-1)
    expected = 1.0
    return make_test_result(approx_equal(result, expected), expected, result)

test_perp_negative.input_description = "slope=-1"


def test_perp_steep(module):
    """Test perpendicular slope of slope=5."""
    result = module.perp(5)
    expected = -0.2
    return make_test_result(approx_equal(result, expected), expected, result)

test_perp_steep.input_description = "slope=5"


def test_perp_float(module):
    """Test perpendicular slope of slope=0.25."""
    result = module.perp(0.25)
    expected = -4.0
    return make_test_result(approx_equal(result, expected), expected, result)

test_perp_float.input_description = "slope=0.25"


# ============================================================================
# Intersection Tests
# ============================================================================

def test_intersect_basic(module):
    """Test intersection of two simple lines."""
    # Line 1: y = x (slope=1 through origin)
    # Line 2: y = -x + 2 (slope=-1 through (1,1))
    result = module.intersect(1, 0, 0, -1, 1, 1)
    expected = (1.0, 1.0)
    passed = approx_equal(result[0], expected[0]) and approx_equal(result[1], expected[1])
    return make_test_result(passed, expected, result)

test_intersect_basic.input_description = "slope0=1, point0=(0,0), slope1=-1, point1=(1,1)"


def test_intersect_perpendicular(module):
    """Test intersection of perpendicular lines."""
    # Line 1: y = x (slope=1 through origin)
    # Line 2: y = -x + 4 (slope=-1 through (2,2))
    result = module.intersect(1, 0, 0, -1, 2, 2)
    expected = (2.0, 2.0)
    passed = approx_equal(result[0], expected[0]) and approx_equal(result[1], expected[1])
    return make_test_result(passed, expected, result)

test_intersect_perpendicular.input_description = "slope0=1, point0=(0,0), slope1=-1, point1=(2,2)"


def test_intersect_negative(module):
    """Test intersection with negative coordinates."""
    # Line 1: y = 2x - 2 (slope=2 through (1,0))
    # Line 2: y = -x + 1 (slope=-1 through (0,1))
    result = module.intersect(2, 1, 0, -1, 0, 1)
    expected = (1.0, 0.0)
    passed = approx_equal(result[0], expected[0]) and approx_equal(result[1], expected[1])
    return make_test_result(passed, expected, result)

test_intersect_negative.input_description = "slope0=2, point0=(1,0), slope1=-1, point1=(0,1)"


def test_intersect_float(module):
    """Test intersection resulting in float coordinates."""
    # Line 1: y = 0.5x (slope=0.5 through origin)
    # Line 2: y = -2x + 5 (slope=-2 through (2,1))
    result = module.intersect(0.5, 0, 0, -2, 2, 1)
    expected = (2.0, 1.0)
    passed = approx_equal(result[0], expected[0]) and approx_equal(result[1], expected[1])
    return make_test_result(passed, expected, result)

test_intersect_float.input_description = "slope0=0.5, point0=(0,0), slope1=-2, point1=(2,1)"


# ============================================================================
# Make Circle Tests
# ============================================================================

def test_make_circle_basic(module):
    """Test make_circle with basic points."""
    # Points on a circle centered at (2, 1) with radius sqrt(5)
    # No two points share x or y coordinates
    cx, cy, r = module.make_circle(0, 0, 4, 2, 3, 3)

    # Verify center
    center_ok = approx_equal(cx, 2.0) and approx_equal(cy, 1.0)
    # Verify radius
    expected_r = math.sqrt(5)
    radius_ok = approx_equal(r, expected_r)

    expected = f"(2.0, 1.0, {expected_r:.6f})"
    actual = f"({cx}, {cy}, {r})"
    return make_test_result(center_ok and radius_ok, expected, actual)

test_make_circle_basic.input_description = "points: (0,0), (4,2), (3,3)"


def test_make_circle_unit(module):
    """Test make_circle with unit circle points."""
    # Points on unit circle centered at origin
    # No two points share x or y coordinates
    cx, cy, r = module.make_circle(1, 0, 0, 1, 0.6, -0.8)

    center_ok = approx_equal(cx, 0.0) and approx_equal(cy, 0.0)
    radius_ok = approx_equal(r, 1.0)

    expected = "(0.0, 0.0, 1.0)"
    actual = f"({cx}, {cy}, {r})"
    return make_test_result(center_ok and radius_ok, expected, actual)

test_make_circle_unit.input_description = "points: (1,0), (0,1), (0.6,-0.8)"


def test_make_circle_centered_origin(module):
    """Test make_circle with circle centered at origin."""
    # Points on circle centered at origin with radius 5
    # No two points share x or y coordinates
    cx, cy, r = module.make_circle(3, 4, 4, 3, -4, -3)

    center_ok = approx_equal(cx, 0.0) and approx_equal(cy, 0.0)
    radius_ok = approx_equal(r, 5.0)

    expected = "(0.0, 0.0, 5.0)"
    actual = f"({cx}, {cy}, {r})"
    return make_test_result(center_ok and radius_ok, expected, actual)

test_make_circle_centered_origin.input_description = "points: (3,4), (4,3), (-4,-3)"


def test_make_circle_offset(module):
    """Test make_circle with offset center."""
    # Points on circle centered at (2, 3) with radius sqrt(5)
    # No two points share x or y coordinates
    cx, cy, r = module.make_circle(0, 2, 4, 4, 3, 5)

    center_ok = approx_equal(cx, 2.0) and approx_equal(cy, 3.0)
    expected_r = math.sqrt(5)
    radius_ok = approx_equal(r, expected_r)

    expected = f"(2.0, 3.0, {expected_r:.6f})"
    actual = f"({cx}, {cy}, {r})"
    return make_test_result(center_ok and radius_ok, expected, actual)

test_make_circle_offset.input_description = "points: (0,2), (4,4), (3,5)"
