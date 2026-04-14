"""
Shared test fixtures and utilities for COMP 182 tests.

This module provides:
- make_test_result() for creating test result tuples
- Graph builder helpers for undirected dict-of-sets graphs
- Graph builder helpers for weighted directed dict-of-dicts graphs (for HW6)
"""

import sys
from pathlib import Path


# ============================================================================
# Assertion Helpers
# ============================================================================

def make_test_result(passed, expected=None, actual=None):
    """Create a test result tuple for the runner."""
    return (passed, expected, actual)


def approx_equal(a, b, tolerance=1e-6):
    """Check if two numbers are approximately equal."""
    if isinstance(a, (list, tuple)) and isinstance(b, (list, tuple)):
        if len(a) != len(b):
            return False
        return all(approx_equal(x, y, tolerance) for x, y in zip(a, b))
    return abs(a - b) < tolerance


# ============================================================================
# Undirected Graph Builders (dict[node, set[neighbors]])
# ============================================================================

def make_empty_graph():
    """Return an empty graph: {}"""
    return {}


def make_single_node(node=0):
    """Return a graph with a single node and no edges."""
    return {node: set()}


def make_single_edge(u=0, v=1):
    """Return a graph with a single undirected edge."""
    return {u: {v}, v: {u}}


def make_line_graph(n):
    """Return an undirected line graph: 0-1-2-..-(n-1)."""
    g = {i: set() for i in range(n)}
    for i in range(n - 1):
        g[i].add(i + 1)
        g[i + 1].add(i)
    return g


def make_cycle_graph(n):
    """Return an undirected cycle graph on n nodes."""
    g = make_line_graph(n)
    g[0].add(n - 1)
    g[n - 1].add(0)
    return g


def make_complete_graph(n):
    """Return a complete undirected graph K_n."""
    g = {}
    for i in range(n):
        g[i] = set(range(n)) - {i}
    return g


def make_star_graph(n):
    """Return a star graph with hub 0 and spokes 1..n-1 (n nodes total)."""
    g = {0: set(range(1, n))}
    for i in range(1, n):
        g[i] = {0}
    return g


def make_complete_bipartite(m, n):
    """Return complete bipartite graph K_{m,n}.

    Left partition: nodes 0..m-1
    Right partition: nodes m..m+n-1
    """
    g = {}
    left = set(range(m))
    right = set(range(m, m + n))
    for u in left:
        g[u] = set(right)
    for v in right:
        g[v] = set(left)
    return g


def make_disconnected_components(sizes):
    """Build an undirected graph with disconnected clique components.

    Args:
        sizes: list of component sizes, each component is a clique

    Returns:
        dict-of-sets graph
    """
    g = {}
    offset = 0
    for size in sizes:
        nodes = list(range(offset, offset + size))
        for u in nodes:
            g[u] = set(nodes) - {u}
        offset += size
    return g


def make_isolated_nodes(n):
    """Return a graph with n isolated nodes (no edges)."""
    return {i: set() for i in range(n)}


# ============================================================================
# Weighted Directed Graph Builders (dict[node, dict[neighbor, weight]])
# ============================================================================

def make_weighted_directed_edge(u=0, v=1, w=1.0):
    """Return a weighted directed graph with a single edge u->v."""
    return {u: {v: w}, v: {}}


def make_weighted_directed_line(n, weight=1.0):
    """Return a weighted directed line graph: 0->1->2->...->(n-1)."""
    g = {i: {} for i in range(n)}
    for i in range(n - 1):
        g[i][i + 1] = weight
    return g


def make_weighted_directed_cycle(n, weight=1.0):
    """Return a weighted directed cycle graph on n nodes."""
    g = make_weighted_directed_line(n, weight)
    g[n - 1][0] = weight
    return g


def make_weighted_complete_digraph(n, weight=1.0):
    """Return a weighted complete directed graph on n nodes."""
    g = {}
    for i in range(n):
        g[i] = {j: weight for j in range(n) if j != i}
    return g
