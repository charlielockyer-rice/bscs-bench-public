"""
Tests for Homework 4: compute_largest_cc_size

Tests the function:
- compute_largest_cc_size(g) -> int

Graph representation: dict[node, set[neighbors]] (undirected)
"""

import sys
from pathlib import Path

# Add tests dir to path for fixtures import
_tests_dir = Path(__file__).parent
if str(_tests_dir) not in sys.path:
    sys.path.insert(0, str(_tests_dir))

from fixtures import (
    make_test_result,
    make_empty_graph,
    make_single_node,
    make_single_edge,
    make_line_graph,
    make_cycle_graph,
    make_complete_graph,
    make_star_graph,
    make_complete_bipartite,
    make_disconnected_components,
    make_isolated_nodes,
)

# Point values for each test (1 point each, 15 total)
TEST_POINTS = {
    "test_empty_graph": 1,
    "test_single_node_no_edges": 1,
    "test_single_edge": 1,
    "test_complete_k4": 1,
    "test_two_equal_components": 1,
    "test_two_unequal_components": 1,
    "test_three_components_different_sizes": 1,
    "test_line_graph_5": 1,
    "test_star_graph_6": 1,
    "test_large_component_plus_isolated": 1,
    "test_cycle_graph": 1,
    "test_bridge_graph": 1,
    "test_complete_bipartite_k3_3": 1,
    "test_many_isolated_nodes": 1,
    "test_one_big_several_small": 1,
}


# ============================================================================
# Tests
# ============================================================================

def test_empty_graph(module):
    """Empty graph should have largest CC size 0."""
    g = make_empty_graph()
    result = module.compute_largest_cc_size(g)
    expected = 0
    return make_test_result(result == expected, expected, result)

test_empty_graph.input_description = "g = {}"


def test_single_node_no_edges(module):
    """A single node with no edges has largest CC size 1."""
    g = make_single_node(0)
    result = module.compute_largest_cc_size(g)
    expected = 1
    return make_test_result(result == expected, expected, result)

test_single_node_no_edges.input_description = "g = {0: set()}"


def test_single_edge(module):
    """Two nodes connected by an edge have largest CC size 2."""
    g = make_single_edge(0, 1)
    result = module.compute_largest_cc_size(g)
    expected = 2
    return make_test_result(result == expected, expected, result)

test_single_edge.input_description = "g = {0: {1}, 1: {0}}"


def test_complete_k4(module):
    """Complete graph K4: all 4 nodes connected, largest CC size is 4."""
    g = make_complete_graph(4)
    result = module.compute_largest_cc_size(g)
    expected = 4
    return make_test_result(result == expected, expected, result)

test_complete_k4.input_description = "g = K4 (complete graph on 4 nodes)"


def test_two_equal_components(module):
    """Two disconnected edges: {0-1} and {2-3}, largest CC size is 2."""
    g = {0: {1}, 1: {0}, 2: {3}, 3: {2}}
    result = module.compute_largest_cc_size(g)
    expected = 2
    return make_test_result(result == expected, expected, result)

test_two_equal_components.input_description = "g = {0: {1}, 1: {0}, 2: {3}, 3: {2}}"


def test_two_unequal_components(module):
    """Triangle {0,1,2} and isolated edge {3,4}, largest CC is 3."""
    g = {0: {1, 2}, 1: {0, 2}, 2: {0, 1}, 3: {4}, 4: {3}}
    result = module.compute_largest_cc_size(g)
    expected = 3
    return make_test_result(result == expected, expected, result)

test_two_unequal_components.input_description = "g = triangle {0,1,2} + edge {3,4}"


def test_three_components_different_sizes(module):
    """Three components of sizes 3, 2, 1. Largest is 3."""
    g = make_disconnected_components([3, 2])
    # Add an isolated node
    g[10] = set()
    result = module.compute_largest_cc_size(g)
    expected = 3
    return make_test_result(result == expected, expected, result)

test_three_components_different_sizes.input_description = "g = clique(3) + clique(2) + isolated node"


def test_line_graph_5(module):
    """Line graph 0-1-2-3-4: all connected, largest CC is 5."""
    g = make_line_graph(5)
    result = module.compute_largest_cc_size(g)
    expected = 5
    return make_test_result(result == expected, expected, result)

test_line_graph_5.input_description = "g = 0-1-2-3-4 (line graph)"


def test_star_graph_6(module):
    """Star graph with hub 0 and 5 spokes: largest CC is 6."""
    g = make_star_graph(6)
    result = module.compute_largest_cc_size(g)
    expected = 6
    return make_test_result(result == expected, expected, result)

test_star_graph_6.input_description = "g = star(6) (hub + 5 spokes)"


def test_large_component_plus_isolated(module):
    """Complete graph K5 plus 3 isolated nodes. Largest CC is 5."""
    g = make_complete_graph(5)
    g[10] = set()
    g[11] = set()
    g[12] = set()
    result = module.compute_largest_cc_size(g)
    expected = 5
    return make_test_result(result == expected, expected, result)

test_large_component_plus_isolated.input_description = "g = K5 + 3 isolated nodes"


def test_cycle_graph(module):
    """Cycle graph on 6 nodes: all connected in a ring, largest CC is 6."""
    g = make_cycle_graph(6)
    result = module.compute_largest_cc_size(g)
    expected = 6
    return make_test_result(result == expected, expected, result)

test_cycle_graph.input_description = "g = cycle(6)"


def test_bridge_graph(module):
    """Graph with a bridge: two triangles connected by one edge. Largest CC is 6."""
    # Triangle {0,1,2} connected to triangle {3,4,5} via edge 2-3
    g = {
        0: {1, 2}, 1: {0, 2}, 2: {0, 1, 3},
        3: {2, 4, 5}, 4: {3, 5}, 5: {3, 4},
    }
    result = module.compute_largest_cc_size(g)
    expected = 6
    return make_test_result(result == expected, expected, result)

test_bridge_graph.input_description = "g = two triangles connected by bridge edge 2-3"


def test_complete_bipartite_k3_3(module):
    """Complete bipartite graph K_{3,3}: all 6 nodes connected, largest CC is 6."""
    g = make_complete_bipartite(3, 3)
    result = module.compute_largest_cc_size(g)
    expected = 6
    return make_test_result(result == expected, expected, result)

test_complete_bipartite_k3_3.input_description = "g = K_{3,3} (complete bipartite)"


def test_many_isolated_nodes(module):
    """10 isolated nodes: largest CC is 1."""
    g = make_isolated_nodes(10)
    result = module.compute_largest_cc_size(g)
    expected = 1
    return make_test_result(result == expected, expected, result)

test_many_isolated_nodes.input_description = "g = 10 isolated nodes"


def test_one_big_several_small(module):
    """One big clique of 5 + three isolated edges. Largest CC is 5."""
    # Big clique: nodes 0-4
    g = make_complete_graph(5)
    # Three isolated edges: {10-11}, {12-13}, {14-15}
    for u, v in [(10, 11), (12, 13), (14, 15)]:
        g[u] = {v}
        g[v] = {u}
    result = module.compute_largest_cc_size(g)
    expected = 5
    return make_test_result(result == expected, expected, result)

test_one_big_several_small.input_description = "g = K5 + three isolated edges"
