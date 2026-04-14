#!/usr/bin/env python3
"""
Circles Demo - Visual verification of Module 1 solution

Plots 3 points and the circle that passes through them.
Uses matplotlib for visualization.

Usage:
    python demos/circles_demo.py
    python demos/circles_demo.py --interactive
"""

import sys
import os
import math
import argparse

# Add paths for imports
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'comp140', 'reference_solutions'))
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'comp140', 'lib'))

try:
    import matplotlib.pyplot as plt
    import matplotlib.patches as patches
except ImportError:
    print("Error: matplotlib is required for this demo")
    print("Install with: pip install matplotlib")
    sys.exit(1)

from module1_solution import distance, midpoint, slope, perp, intersect, make_circle


def plot_circle_through_points(points, ax=None, title=None):
    """
    Plot 3 points and the circle passing through them.

    Args:
        points: List of 3 tuples [(x0,y0), (x1,y1), (x2,y2)]
        ax: Optional matplotlib axes
        title: Optional title for the plot
    """
    if ax is None:
        fig, ax = plt.subplots(1, 1, figsize=(8, 8))

    p0, p1, p2 = points

    # Compute the circle
    cx, cy, radius = make_circle(p0[0], p0[1], p1[0], p1[1], p2[0], p2[1])

    # Plot the points
    xs = [p[0] for p in points]
    ys = [p[1] for p in points]
    ax.scatter(xs, ys, s=100, c='blue', zorder=5, label='Input points')

    # Label the points
    for i, (x, y) in enumerate(points):
        ax.annotate(f'P{i} ({x:.1f}, {y:.1f})', (x, y),
                   textcoords="offset points", xytext=(10, 10),
                   fontsize=10)

    # Plot the center
    ax.scatter([cx], [cy], s=100, c='red', marker='x', zorder=5, label=f'Center ({cx:.2f}, {cy:.2f})')

    # Draw the circle
    circle = patches.Circle((cx, cy), radius, fill=False, color='red', linewidth=2, label=f'Radius = {radius:.2f}')
    ax.add_patch(circle)

    # Draw lines from center to each point (showing equal radii)
    for p in points:
        ax.plot([cx, p[0]], [cy, p[1]], 'g--', alpha=0.5, linewidth=1)

    # Set equal aspect ratio and appropriate limits
    margin = radius * 0.5
    ax.set_xlim(cx - radius - margin, cx + radius + margin)
    ax.set_ylim(cy - radius - margin, cy + radius + margin)
    ax.set_aspect('equal')
    ax.grid(True, alpha=0.3)
    ax.legend(loc='upper right')

    if title:
        ax.set_title(title)
    else:
        ax.set_title(f'Circle through 3 points\nCenter: ({cx:.2f}, {cy:.2f}), Radius: {radius:.2f}')

    return cx, cy, radius


def run_test_cases():
    """Run several test cases and display them in a grid."""
    test_cases = [
        [(0, 0), (4, 0), (2, 2)],
        [(0, 0), (1, 0), (0.5, 0.5)],
        [(-3, 0), (3, 0), (0, 3)],
        [(1, 1), (5, 1), (3, 3)],
        [(0, 0), (10, 0), (5, 8)],
        [(1, 2), (4, 6), (7, 2)],
    ]

    fig, axes = plt.subplots(2, 3, figsize=(15, 10))
    axes = axes.flatten()

    for i, points in enumerate(test_cases):
        plot_circle_through_points(points, ax=axes[i], title=f'Test Case {i+1}')

    plt.tight_layout()
    plt.savefig('demos/circles_test_cases.png', dpi=150)
    print("Saved: demos/circles_test_cases.png")
    plt.show()


def interactive_mode():
    """Interactive mode: click to add points."""
    fig, ax = plt.subplots(1, 1, figsize=(10, 10))
    ax.set_xlim(0, 100)
    ax.set_ylim(0, 100)
    ax.set_aspect('equal')
    ax.grid(True, alpha=0.3)
    ax.set_title('Click to add 3 points (click again to reset)')

    points = []
    point_plots = []
    circle_patch = [None]

    def onclick(event):
        if event.inaxes != ax:
            return

        x, y = event.xdata, event.ydata

        # Reset if we already have 3 points
        if len(points) >= 3:
            points.clear()
            for p in point_plots:
                p.remove()
            point_plots.clear()
            if circle_patch[0]:
                circle_patch[0].remove()
                circle_patch[0] = None
            ax.set_title('Click to add 3 points')

        points.append((x, y))
        p = ax.scatter([x], [y], s=100, c='blue', zorder=5)
        point_plots.append(p)
        ax.annotate(f'P{len(points)-1}', (x, y), textcoords="offset points", xytext=(5, 5))

        if len(points) == 3:
            p0, p1, p2 = points
            try:
                cx, cy, radius = make_circle(p0[0], p0[1], p1[0], p1[1], p2[0], p2[1])
                circle = patches.Circle((cx, cy), radius, fill=False, color='red', linewidth=2)
                ax.add_patch(circle)
                circle_patch[0] = circle
                ax.scatter([cx], [cy], s=100, c='red', marker='x', zorder=5)
                ax.set_title(f'Center: ({cx:.2f}, {cy:.2f}), Radius: {radius:.2f}\nClick to reset')
            except Exception as e:
                ax.set_title(f'Error: {e}\nClick to reset')
        else:
            ax.set_title(f'Click to add point {len(points)+1} of 3')

        fig.canvas.draw()

    fig.canvas.mpl_connect('button_press_event', onclick)
    plt.show()


def main():
    parser = argparse.ArgumentParser(description='Circles Demo - Visual verification')
    parser.add_argument('--interactive', '-i', action='store_true',
                       help='Interactive mode: click to add points')
    args = parser.parse_args()

    if args.interactive:
        print("Interactive mode: click on the canvas to add 3 points")
        print("After 3 points, the circle will be drawn. Click again to reset.")
        interactive_mode()
    else:
        print("Running test cases...")
        run_test_cases()


if __name__ == '__main__':
    main()
