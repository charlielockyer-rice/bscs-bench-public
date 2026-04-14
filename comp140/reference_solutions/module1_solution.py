"""
Reference solution for COMP 140 Module 1: Circle Through Three Points.

This module implements functions to calculate the center and radius of a circle
that passes through three given points using the perpendicular bisector method.
"""

import math


def distance(point0x, point0y, point1x, point1y):
    """
    Computes the Euclidean distance between two points.

    inputs:
        -point0x: a float representing the x-coordinate of the first point
        -point0y: a float representing the y-coordinate of the first point
        -point1x: a float representing the x-coordinate of the second point
        -point1y: a float representing the y-coordinate of the second point

    returns: a float that is the distance between the two points

    Formula: d = sqrt((x1 - x0)^2 + (y1 - y0)^2)
    """
    dx = point1x - point0x
    dy = point1y - point0y
    return math.sqrt(dx * dx + dy * dy)


def midpoint(point0x, point0y, point1x, point1y):
    """
    Computes the midpoint between two points.

    inputs:
        -point0x: a float representing the x-coordinate of the first point
        -point0y: a float representing the y-coordinate of the first point
        -point1x: a float representing the x-coordinate of the second point
        -point1y: a float representing the y-coordinate of the second point

    returns: two floats that are the x- and y-coordinates of the midpoint

    Formula:
        mid_x = x0 + (x1 - x0) / 2 = (x0 + x1) / 2
        mid_y = y0 + (y1 - y0) / 2 = (y0 + y1) / 2
    """
    mid_x = point0x + (point1x - point0x) / 2.0
    mid_y = point0y + (point1y - point0y) / 2.0
    return mid_x, mid_y


def slope(point0x, point0y, point1x, point1y):
    """
    Computes the slope of the line that connects two given points.

    The x-values of the two points, point0x and point1x, must be different.

    inputs:
        -point0x: a float representing the x-coordinate of the first point.
        -point0y: a float representing the y-coordinate of the first point
        -point1x: a float representing the x-coordinate of the second point.
        -point1y: a float representing the y-coordinate of the second point

    returns: a float that is the slope between the points

    Formula: s = (y1 - y0) / (x1 - x0)
    """
    return (point1y - point0y) / (point1x - point0x)


def perp(lineslope):
    """
    Computes the slope of a line perpendicular to a given slope.

    input:
        -lineslope: a float representing the slope of a line.
                    Must be non-zero

    returns: a float that is the perpendicular slope

    Formula: p = -1 / s
    """
    return -1.0 / lineslope


def intersect(slope0, point0x, point0y, slope1, point1x, point1y):
    """
    Computes the intersection point of two lines.

    Each line is given in point-slope form: a slope and a point on the line.
    The two slopes, slope0 and slope1, must be different.

    inputs:
        -slope0: a float representing the slope of the first line.
        -point0x: a float representing the x-coordinate of a point on the first line
        -point0y: a float representing the y-coordinate of a point on the first line
        -slope1: a float representing the slope of the second line.
        -point1x: a float representing the x-coordinate of a point on the second line
        -point1y: a float representing the y-coordinate of a point on the second line

    returns: two floats that are the x- and y-coordinates of the intersection point

    Derivation:
        Line 1: y - y0 = s0 * (x - x0)  =>  y = s0 * x - s0 * x0 + y0
        Line 2: y - y1 = s1 * (x - x1)  =>  y = s1 * x - s1 * x1 + y1

        Setting equal:
        s0 * x - s0 * x0 + y0 = s1 * x - s1 * x1 + y1
        x * (s0 - s1) = s0 * x0 - s1 * x1 + y1 - y0
        x = (s0 * x0 - s1 * x1 + y1 - y0) / (s0 - s1)

        Then: y = s0 * (x - x0) + y0
    """
    intersect_x = (slope0 * point0x - slope1 * point1x + point1y - point0y) / (slope0 - slope1)
    intersect_y = slope0 * (intersect_x - point0x) + point0y
    return intersect_x, intersect_y


def make_circle(point0x, point0y, point1x, point1y, point2x, point2y):
    """
    Computes the center and radius of a circle that passes through
    three given points.

    The points must not be co-linear and no two points can have the
    same x or y values.

    inputs:
        -point0x: a float representing the x-coordinate of the first point
        -point0y: a float representing the y-coordinate of the first point
        -point1x: a float representing the x-coordinate of the second point
        -point1y: a float representing the y-coordinate of the second point
        -point2x: a float representing the x-coordinate of the third point
        -point2y: a float representing the y-coordinate of the third point

    returns: three floats that are the x- and y-coordinates of the center
    and the radius

    Algorithm:
        1. Find the slope of line between point0 and point1
        2. Find the midpoint of line between point0 and point1
        3. Find the perpendicular slope of that line
        4. Repeat steps 1-3 for point1 and point2
        5. Find intersection of the two perpendicular bisector lines
        6. Calculate distance from center to any of the three points (that's the radius)

    Note: This implementation handles vertical perpendicular bisectors (when the
    original line segment is horizontal) by computing the intersection differently
    for those cases.
    """
    # Compute midpoints for both line segments
    mid01_x, mid01_y = midpoint(point0x, point0y, point1x, point1y)
    mid12_x, mid12_y = midpoint(point1x, point1y, point2x, point2y)

    # Check if line 0-1 is horizontal (slope = 0)
    # If so, perpendicular bisector is vertical: x = mid01_x
    line01_horizontal = (point0y == point1y)

    # Check if line 1-2 is horizontal (slope = 0)
    # If so, perpendicular bisector is vertical: x = mid12_x
    line12_horizontal = (point1y == point2y)

    if line01_horizontal and line12_horizontal:
        # Both lines are horizontal - this means all three points have the same y
        # which is collinear, violating preconditions. Raise an error.
        raise ValueError("Points are collinear (all have the same y-coordinate)")

    if line01_horizontal:
        # Perpendicular bisector of line 0-1 is vertical: x = mid01_x
        # So center_x = mid01_x
        # Find center_y using perpendicular bisector of line 1-2
        center_x = mid01_x
        slope12 = slope(point1x, point1y, point2x, point2y)
        perp12 = perp(slope12)
        # Line through mid12 with slope perp12: y - mid12_y = perp12 * (x - mid12_x)
        center_y = perp12 * (center_x - mid12_x) + mid12_y

    elif line12_horizontal:
        # Perpendicular bisector of line 1-2 is vertical: x = mid12_x
        # So center_x = mid12_x
        # Find center_y using perpendicular bisector of line 0-1
        center_x = mid12_x
        slope01 = slope(point0x, point0y, point1x, point1y)
        perp01 = perp(slope01)
        # Line through mid01 with slope perp01: y - mid01_y = perp01 * (x - mid01_x)
        center_y = perp01 * (center_x - mid01_x) + mid01_y

    else:
        # Neither line is horizontal, so we can use the standard algorithm
        slope01 = slope(point0x, point0y, point1x, point1y)
        perp01 = perp(slope01)

        slope12 = slope(point1x, point1y, point2x, point2y)
        perp12 = perp(slope12)

        center_x, center_y = intersect(perp01, mid01_x, mid01_y, perp12, mid12_x, mid12_y)

    # Calculate radius as distance from center to any point
    radius = distance(point0x, point0y, center_x, center_y)

    return center_x, center_y, radius
