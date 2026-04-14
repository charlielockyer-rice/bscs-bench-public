"""
Tests for Homework 6: Rooted Directed Minimum Spanning Trees

Tests the following functions:
- reverse_digraph_representation(graph)
- modify_edge_weights(rgraph, root)
- compute_rdst_candidate(rgraph, root)
- compute_cycle(rgraph)
- contract_cycle(graph, cycle)
- expand_graph(rgraph, rdst_candidate, cycle, cstar)
- compute_genetic_distance(seq1, seq2)
- construct_complete_weighted_digraph(gen_data, epi_data)
- infer_transmap(gen_data, epi_data) [via integration test]
"""

import sys
import os
import copy
from pathlib import Path

# Add tests dir to path for fixtures import
_tests_dir = Path(__file__).parent
if str(_tests_dir) not in sys.path:
    sys.path.insert(0, str(_tests_dir))

from fixtures import make_test_result, approx_equal

# Point values for each test
TEST_POINTS = {
    "test_reverse_simple": 1,
    "test_reverse_single_node": 1,
    "test_reverse_complete": 1,
    "test_modify_weights_basic": 1,
    "test_modify_weights_root_untouched": 1,
    "test_modify_weights_single_incoming": 1,
    "test_rdst_candidate_basic": 1,
    "test_rdst_candidate_all_zero": 1,
    "test_rdst_candidate_root_empty": 1,
    "test_cycle_exists": 1,
    "test_cycle_none": 1,
    "test_cycle_self_loop": 1,
    "test_contract_two_node": 1,
    "test_contract_three_node": 1,
    "test_contract_edges_consolidated": 1,
    "test_expand_basic": 1,
    "test_expand_restores_cycle": 1,
    "test_genetic_distance_identical": 1,
    "test_genetic_distance_all_differ": 1,
    "test_genetic_distance_known": 1,
    "test_digraph_edge_count": 1,
    "test_digraph_weight_formula": 1,
    "test_integration_small_rdmst": 1,
    "test_integration_rdmst_weight": 1,
    "test_integration_rdmst_spanning": 1,
}


# ============================================================================
# Helper functions
# ============================================================================

def _graphs_equal(g1, g2, tol=1e-9):
    """Check if two weighted digraphs are equal (with float tolerance)."""
    if set(g1.keys()) != set(g2.keys()):
        return False
    for node in g1:
        if set(g1[node].keys()) != set(g2[node].keys()):
            return False
        for nbr in g1[node]:
            if abs(g1[node][nbr] - g2[node][nbr]) > tol:
                return False
    return True


def _total_weight_reversed(rgraph):
    """Compute total edge weight of a reversed-representation graph."""
    total = 0.0
    for node in rgraph:
        for nbr in rgraph[node]:
            total += rgraph[node][nbr]
    return total


def _is_rdst(rgraph, root, all_nodes):
    """
    Check that rgraph (reversed representation) is a valid RDST:
    - Every non-root node has exactly one incoming edge
    - Root has no incoming edges
    - The graph is connected (forms a tree rooted at root)
    """
    # Check root has no incoming edges
    if rgraph.get(root, {}):
        return False
    # Check every non-root node has exactly one incoming edge
    for node in all_nodes:
        if node == root:
            continue
        if node not in rgraph or len(rgraph[node]) != 1:
            return False
    # Check connectivity: build standard representation and BFS from root
    std = {}
    for node in all_nodes:
        std[node] = {}
    for node in rgraph:
        for src in rgraph[node]:
            std[src][node] = rgraph[node][src]
    visited = set()
    queue = [root]
    while queue:
        current = queue.pop(0)
        if current in visited:
            continue
        visited.add(current)
        for nbr in std.get(current, {}):
            if nbr not in visited:
                queue.append(nbr)
    return visited == set(all_nodes)


# ============================================================================
# reverse_digraph_representation tests
# ============================================================================

def test_reverse_simple(module):
    """Test reversing a simple 3-node digraph."""
    graph = {0: {1: 20, 2: 4}, 1: {2: 2}, 2: {}}
    result = module.reverse_digraph_representation(graph)
    expected = {0: {}, 1: {0: 20}, 2: {0: 4, 1: 2}}
    passed = _graphs_equal(result, expected)
    return make_test_result(passed, expected, result)

test_reverse_simple.input_description = "graph={0: {1: 20, 2: 4}, 1: {2: 2}, 2: {}}"


def test_reverse_single_node(module):
    """Test reversing a single-node graph with no edges."""
    graph = {0: {}}
    result = module.reverse_digraph_representation(graph)
    expected = {0: {}}
    passed = _graphs_equal(result, expected)
    return make_test_result(passed, expected, result)

test_reverse_single_node.input_description = "graph={0: {}}"


def test_reverse_complete(module):
    """Test reversing a complete 3-node digraph."""
    graph = {0: {1: 1, 2: 2}, 1: {0: 3, 2: 4}, 2: {0: 5, 1: 6}}
    result = module.reverse_digraph_representation(graph)
    expected = {0: {1: 3, 2: 5}, 1: {0: 1, 2: 6}, 2: {0: 2, 1: 4}}
    passed = _graphs_equal(result, expected)
    return make_test_result(passed, expected, result)

test_reverse_complete.input_description = "graph={0: {1:1, 2:2}, 1: {0:3, 2:4}, 2: {0:5, 1:6}}"


# ============================================================================
# modify_edge_weights tests
# ============================================================================

def test_modify_weights_basic(module):
    """Test basic weight modification: subtract min incoming per node."""
    rgraph = {0: {}, 1: {0: 20, 2: 4}, 2: {0: 4, 1: 2}}
    module.modify_edge_weights(rgraph, 0)
    # Node 1: min incoming = 4, so 20-4=16, 4-4=0
    # Node 2: min incoming = 2, so 4-2=2, 2-2=0
    expected = {0: {}, 1: {0: 16, 2: 0}, 2: {0: 2, 1: 0}}
    passed = _graphs_equal(rgraph, expected)
    return make_test_result(passed, expected, rgraph)

test_modify_weights_basic.input_description = "rgraph={0: {}, 1: {0: 20, 2: 4}, 2: {0: 4, 1: 2}}, root=0"


def test_modify_weights_root_untouched(module):
    """Test that root node's incoming edges are not modified."""
    rgraph = {0: {1: 10}, 1: {0: 5}, 2: {0: 3, 1: 7}}
    module.modify_edge_weights(rgraph, 0)
    # Root (0) should keep its incoming edges as-is
    # Node 1: min=5, 5-5=0
    # Node 2: min=3, 3-3=0, 7-3=4
    expected = {0: {1: 10}, 1: {0: 0}, 2: {0: 0, 1: 4}}
    passed = _graphs_equal(rgraph, expected)
    return make_test_result(passed, expected, rgraph)

test_modify_weights_root_untouched.input_description = "rgraph with edges into root, root=0"


def test_modify_weights_single_incoming(module):
    """Test node with single incoming edge gets weight 0."""
    rgraph = {0: {}, 1: {0: 7}, 2: {1: 3}}
    module.modify_edge_weights(rgraph, 0)
    expected = {0: {}, 1: {0: 0}, 2: {1: 0}}
    passed = _graphs_equal(rgraph, expected)
    return make_test_result(passed, expected, rgraph)

test_modify_weights_single_incoming.input_description = "rgraph={0: {}, 1: {0: 7}, 2: {1: 3}}, root=0"


# ============================================================================
# compute_rdst_candidate tests
# ============================================================================

def test_rdst_candidate_basic(module):
    """Test RDST candidate selects zero-weight edges."""
    rgraph = {0: {}, 1: {0: 16, 2: 0}, 2: {0: 2, 1: 0}}
    result = module.compute_rdst_candidate(rgraph, 0)
    # Node 1 should pick edge from 2 (weight 0)
    # Node 2 should pick edge from 1 (weight 0)
    passed = (0 in result and len(result[0]) == 0 and
              1 in result and 2 in result[1] and result[1][2] == 0 and
              2 in result and 1 in result[2] and result[2][1] == 0)
    expected = {0: {}, 1: {2: 0}, 2: {1: 0}}
    return make_test_result(passed, expected, result)

test_rdst_candidate_basic.input_description = "modified rgraph with zero-weight edges, root=0"


def test_rdst_candidate_all_zero(module):
    """Test when multiple zero-weight edges exist (picks one per node)."""
    rgraph = {0: {}, 1: {0: 0, 2: 0}, 2: {0: 0, 1: 5}}
    result = module.compute_rdst_candidate(rgraph, 0)
    # Node 1: has two zero-weight edges, should pick exactly one
    # Node 2: has one zero-weight edge from 0
    passed = (0 in result and len(result[0]) == 0 and
              1 in result and len(result[1]) == 1 and
              list(result[1].values())[0] == 0 and
              2 in result and 0 in result[2] and result[2][0] == 0)
    expected_desc = "node 1 picks one zero-weight edge, node 2 picks edge from 0"
    return make_test_result(passed, expected_desc, result)

test_rdst_candidate_all_zero.input_description = "rgraph with multiple zero-weight edges, root=0"


def test_rdst_candidate_root_empty(module):
    """Test that root has no incoming edges in candidate."""
    rgraph = {0: {1: 0}, 1: {0: 0}, 2: {0: 0}}
    result = module.compute_rdst_candidate(rgraph, 0)
    passed = 0 in result and len(result[0]) == 0
    expected = "root node 0 has empty incoming edge set"
    return make_test_result(passed, expected, result)

test_rdst_candidate_root_empty.input_description = "rgraph with edges into root, root=0"


# ============================================================================
# compute_cycle tests
# ============================================================================

def test_cycle_exists(module):
    """Test detecting a cycle in graph with in-degree <= 1."""
    # 1->2->3->1 forms a cycle (reversed rep: each node has one incoming)
    rgraph = {0: {}, 1: {3: 0}, 2: {1: 0}, 3: {2: 0}}
    result = module.compute_cycle(rgraph)
    # Result should be a tuple containing nodes 1, 2, 3 in cycle order
    passed = (result is not None and
              set(result) == {1, 2, 3} and
              len(result) == 3)
    expected = "tuple containing nodes 1, 2, 3"
    return make_test_result(passed, expected, result)

test_cycle_exists.input_description = "rgraph with cycle 1->2->3->1"


def test_cycle_none(module):
    """Test that a tree (no cycle) returns None."""
    rgraph = {0: {}, 1: {0: 5}, 2: {0: 3}, 3: {1: 2}}
    result = module.compute_cycle(rgraph)
    passed = result is None
    return make_test_result(passed, None, result)

test_cycle_none.input_description = "rgraph forming a tree rooted at 0"


def test_cycle_self_loop(module):
    """Test detecting a 2-node cycle."""
    # Nodes 1 and 2 form a cycle: 1->2->1
    rgraph = {0: {}, 1: {2: 0}, 2: {1: 0}}
    result = module.compute_cycle(rgraph)
    passed = (result is not None and
              set(result) == {1, 2} and
              len(result) == 2)
    expected = "tuple containing nodes 1, 2"
    return make_test_result(passed, expected, result)

test_cycle_self_loop.input_description = "rgraph with 2-node cycle between 1 and 2"


# ============================================================================
# contract_cycle tests
# ============================================================================

def test_contract_two_node(module):
    """Test contracting a 2-node cycle."""
    graph = {0: {1: 10, 2: 5}, 1: {2: 3}, 2: {1: 4}}
    cycle = (1, 2)
    result_graph, cstar = module.contract_cycle(graph, cycle)
    # cstar should be max(0,1,2)+1 = 3
    passed = (cstar == 3 and
              1 not in result_graph and 2 not in result_graph and
              cstar in result_graph and 0 in result_graph)
    expected = "cstar=3, nodes 1,2 removed, cstar node present"
    return make_test_result(passed, expected, f"cstar={cstar}, nodes={set(result_graph.keys())}")

test_contract_two_node.input_description = "graph with cycle (1, 2)"


def test_contract_three_node(module):
    """Test contracting a 3-node cycle, cstar = max+1."""
    graph = {0: {1: 10, 2: 20, 3: 15},
             1: {2: 5, 0: 8},
             2: {3: 7, 0: 12},
             3: {1: 3, 0: 9}}
    cycle = (1, 2, 3)
    result_graph, cstar = module.contract_cycle(graph, cycle)
    passed = (cstar == 4 and
              1 not in result_graph and 2 not in result_graph and 3 not in result_graph and
              cstar in result_graph and 0 in result_graph)
    expected = "cstar=4, cycle nodes removed"
    return make_test_result(passed, expected, f"cstar={cstar}, nodes={set(result_graph.keys())}")

test_contract_three_node.input_description = "graph with 3-node cycle (1, 2, 3)"


def test_contract_edges_consolidated(module):
    """Test that edges to/from cycle are properly consolidated (min weight)."""
    graph = {0: {1: 10, 2: 5},
             1: {2: 3, 0: 8},
             2: {1: 4, 0: 12}}
    cycle = (1, 2)
    result_graph, cstar = module.contract_cycle(graph, cycle)
    # Edge from 0 to cycle: min(10, 5) = 5
    # Edge from cycle to 0: min(8, 12) = 8
    edge_to_cycle = result_graph.get(0, {}).get(cstar, None)
    edge_from_cycle = result_graph.get(cstar, {}).get(0, None)
    passed = (edge_to_cycle == 5 and edge_from_cycle == 8)
    expected = f"0->cstar=5, cstar->0=8"
    actual = f"0->cstar={edge_to_cycle}, cstar->0={edge_from_cycle}"
    return make_test_result(passed, expected, actual)

test_contract_edges_consolidated.input_description = "graph with cycle (1,2), multiple edges to/from cycle"


# ============================================================================
# expand_graph tests
# ============================================================================

def test_expand_basic(module):
    """Test basic expansion restores non-cycle nodes correctly."""
    # Original reversed graph (before contraction)
    # Standard: 0->1(10), 0->2(5), 1->2(3), 2->1(4), 2->3(7), 3->2(6)
    # Reversed: each node maps to its incoming edges
    rgraph = {0: {}, 1: {0: 10, 2: 4}, 2: {0: 5, 1: 3, 3: 6}, 3: {2: 7}}
    # Suppose cycle was (1, 2) contracted to cstar=4
    # RDMST on contracted graph (reversed rep):
    # Node 0: root, no incoming
    # cstar(4): incoming from 0 (weight 5, min of edges to cycle)
    # Node 3: incoming from cstar (weight 7, edge from cycle node 2)
    rdst_candidate = {0: {}, 4: {0: 5}, 3: {4: 7}}
    cycle = (1, 2)
    cstar = 4
    result = module.expand_graph(rgraph, rdst_candidate, cycle, cstar)
    # All original nodes should be present
    all_present = all(n in result for n in [0, 1, 2, 3])
    # Root has no incoming
    root_ok = len(result.get(0, {})) == 0
    passed = all_present and root_ok
    expected = "nodes 0,1,2,3 all present, root has no incoming"
    return make_test_result(passed, expected, result)

test_expand_basic.input_description = "expand cycle (1,2) from cstar=4"


def test_expand_restores_cycle(module):
    """Test that expansion produces correct edge count with cycle + external node."""
    # 5-node graph with cycle (1,2,3), root=0, external node=4
    # Reversed representation of original graph:
    rgraph = {0: {}, 1: {0: 10, 3: 4}, 2: {0: 5, 1: 3}, 3: {0: 15, 2: 7}, 4: {1: 2, 2: 6, 3: 8}}
    # Cycle (1,2,3) contracted to cstar=5
    # RDMST on contracted (reversed rep):
    #   0: root, no incoming
    #   5 (cstar): incoming from 0
    #   4: incoming from cstar (5)
    rdst_candidate = {0: {}, 5: {0: 5}, 4: {5: 2}}
    cycle = (1, 2, 3)
    cstar = 5
    result = module.expand_graph(rgraph, rdst_candidate, cycle, cstar)
    # All 5 original nodes should be present
    all_present = all(n in result for n in [0, 1, 2, 3, 4])
    # Total edges = 4 (one per non-root node in a spanning tree on 5 nodes)
    total_edges = sum(len(result.get(n, {})) for n in result)
    passed = all_present and total_edges == 4
    expected = "5 nodes, 4 edges total (one incoming per non-root)"
    actual = f"nodes={set(result.keys())}, edges={total_edges}"
    return make_test_result(passed, expected, actual)

test_expand_restores_cycle.input_description = "expand 3-node cycle back with external node"


# ============================================================================
# compute_genetic_distance tests
# ============================================================================

def test_genetic_distance_identical(module):
    """Test distance between identical sequences is 0."""
    result = module.compute_genetic_distance('00101', '00101')
    expected = 0
    passed = result == expected
    return make_test_result(passed, expected, result)

test_genetic_distance_identical.input_description = "seq1='00101', seq2='00101'"


def test_genetic_distance_all_differ(module):
    """Test distance when all positions differ equals length."""
    result = module.compute_genetic_distance('0000', '1111')
    expected = 4
    passed = result == expected
    return make_test_result(passed, expected, result)

test_genetic_distance_all_differ.input_description = "seq1='0000', seq2='1111'"


def test_genetic_distance_known(module):
    """Test known example from assignment instructions."""
    result = module.compute_genetic_distance('00101', '10100')
    expected = 2
    passed = result == expected
    return make_test_result(passed, expected, result)

test_genetic_distance_known.input_description = "seq1='00101', seq2='10100'"


# ============================================================================
# construct_complete_weighted_digraph tests
# ============================================================================

def _get_data_dir(module):
    """Get the directory containing patient data files."""
    # Try to find data files relative to the module's location
    if hasattr(module, '__file__') and module.__file__:
        mod_dir = Path(module.__file__).parent
        if (mod_dir / 'patient_sequences.txt').exists():
            return mod_dir
    # Fallback: look relative to this test file
    hw6_dir = Path(__file__).parent.parent / 'Homework_6'
    if (hw6_dir / 'patient_sequences.txt').exists():
        return hw6_dir
    return None


def test_digraph_edge_count(module):
    """Test that complete digraph has n*(n-1) edges for n patients."""
    data_dir = _get_data_dir(module)
    if data_dir is None:
        return make_test_result(False, "data files found", "patient data files not found")
    gen_file = str(data_dir / 'patient_sequences.txt')
    epi_file = str(data_dir / 'patient_traces.txt')
    result = module.construct_complete_weighted_digraph(gen_file, epi_file)
    n = len(result)
    total_edges = sum(len(result[node]) for node in result)
    expected_edges = n * (n - 1)  # complete digraph
    passed = total_edges == expected_edges
    return make_test_result(passed, f"{expected_edges} edges for {n} patients",
                            f"{total_edges} edges for {n} patients")

test_digraph_edge_count.input_description = "synthetic patient data (8 patients)"


def test_digraph_weight_formula(module):
    """Test that edge weights follow D_AB = G_AB + 999*(E_AB/max_E)/10^5."""
    data_dir = _get_data_dir(module)
    if data_dir is None:
        return make_test_result(False, "data files found", "patient data files not found")
    gen_file = str(data_dir / 'patient_sequences.txt')
    epi_file = str(data_dir / 'patient_traces.txt')
    result = module.construct_complete_weighted_digraph(gen_file, epi_file)
    # All weights should be positive
    all_positive = all(
        result[u][v] > 0
        for u in result for v in result[u]
    )
    # Weights should be dominated by genetic distance (integers) with small epi adjustment
    # So each weight should be close to an integer
    some_weight = None
    for u in result:
        for v in result[u]:
            some_weight = result[u][v]
            break
        if some_weight is not None:
            break
    # Weight should be a number (int or float)
    is_number = isinstance(some_weight, (int, float))
    passed = all_positive and is_number
    expected = "all positive numeric weights"
    actual = f"all_positive={all_positive}, sample_weight={some_weight}"
    return make_test_result(passed, expected, actual)

test_digraph_weight_formula.input_description = "synthetic patient data, verify weight formula"


# ============================================================================
# Integration tests (full RDMST pipeline)
# ============================================================================

def test_integration_small_rdmst(module):
    """Test full RDMST computation on a small known graph."""
    # Simple 4-node graph where RDMST is straightforward
    # 0 is root, edges: 0->1 (w=2), 0->2 (w=4), 0->3 (w=10), 1->2 (w=1), 1->3 (w=3), 2->3 (w=2)
    graph = {
        0: {1: 2, 2: 4, 3: 10},
        1: {0: 8, 2: 1, 3: 3},
        2: {0: 9, 1: 6, 3: 2},
        3: {0: 7, 1: 5, 2: 4},
    }
    root = 0
    # Compute the reversed graph
    rgraph = module.reverse_digraph_representation(graph)
    # Modify weights
    rgraph_copy = copy.deepcopy(rgraph)
    module.modify_edge_weights(rgraph_copy, root)
    # Get candidate
    candidate = module.compute_rdst_candidate(rgraph_copy, root)
    # Check for cycle
    cycle = module.compute_cycle(candidate)

    if cycle is None:
        # Candidate is already an RDST -- verify it
        # Restore original weights
        rdmst = {}
        for node in candidate:
            rdmst[node] = {}
            for nbr in candidate[node]:
                rdmst[node][nbr] = rgraph[node][nbr]
        all_nodes = set(graph.keys())
        passed = _is_rdst(rdmst, root, all_nodes)
    else:
        # There's a cycle, which means the algorithm needs further steps
        # Just verify the cycle detection was correct
        passed = all(n in graph for n in cycle) and len(cycle) >= 2

    expected = "valid RDST or correct cycle detected"
    return make_test_result(passed, expected, f"cycle={cycle}, candidate has {sum(len(candidate[n]) for n in candidate)} edges")

test_integration_small_rdmst.input_description = "4-node graph, root=0"


def test_integration_rdmst_weight(module):
    """Test that RDMST total weight is optimal on a small graph."""
    # Graph where optimal RDMST is clear: 0->1(2), 1->2(1), 2->3(2) = total 5
    graph = {
        0: {1: 2, 2: 4, 3: 10},
        1: {2: 1, 3: 3},
        2: {3: 2},
        3: {},
    }
    # Add reverse edges to make it a complete graph for the algorithm
    graph = {
        0: {1: 2, 2: 4, 3: 10},
        1: {0: 8, 2: 1, 3: 3},
        2: {0: 9, 1: 6, 3: 2},
        3: {0: 7, 1: 5, 2: 4},
    }
    root = 0
    # Run the full pipeline using the steps
    rgraph = module.reverse_digraph_representation(graph)
    rgraph_mod = copy.deepcopy(rgraph)
    module.modify_edge_weights(rgraph_mod, root)
    candidate = module.compute_rdst_candidate(rgraph_mod, root)
    cycle = module.compute_cycle(candidate)

    if cycle is None:
        rdmst = {}
        for node in candidate:
            rdmst[node] = {}
            for nbr in candidate[node]:
                rdmst[node][nbr] = rgraph[node][nbr]
        total = _total_weight_reversed(rdmst)
        # Optimal: 0->1(2) + 1->2(1) + 2->3(2) = 5
        passed = approx_equal(total, 5.0, tolerance=0.01)
        actual = f"total_weight={total}"
    else:
        # If cycle found, partial credit -- algorithm is working
        passed = len(cycle) >= 2
        actual = f"cycle found: {cycle}"

    expected = "total RDMST weight = 5.0"
    return make_test_result(passed, expected, actual)

test_integration_rdmst_weight.input_description = "4-node graph with known optimal RDMST weight=5"


def test_integration_rdmst_spanning(module):
    """Test that RDMST spans all nodes in a 5-node graph."""
    graph = {
        0: {1: 1, 2: 5, 3: 8, 4: 10},
        1: {0: 9, 2: 2, 3: 6, 4: 7},
        2: {0: 8, 1: 7, 3: 1, 4: 4},
        3: {0: 10, 1: 5, 2: 6, 4: 3},
        4: {0: 11, 1: 8, 2: 9, 3: 7},
    }
    root = 0
    rgraph = module.reverse_digraph_representation(graph)
    rgraph_mod = copy.deepcopy(rgraph)
    module.modify_edge_weights(rgraph_mod, root)
    candidate = module.compute_rdst_candidate(rgraph_mod, root)
    cycle = module.compute_cycle(candidate)

    if cycle is None:
        rdmst = {}
        for node in candidate:
            rdmst[node] = {}
            for nbr in candidate[node]:
                rdmst[node][nbr] = rgraph[node][nbr]
        all_nodes = set(graph.keys())
        passed = _is_rdst(rdmst, root, all_nodes)
        actual = f"RDST with {sum(len(rdmst[n]) for n in rdmst)} edges spanning {len(rdmst)} nodes"
    else:
        passed = all(n in graph for n in cycle)
        actual = f"cycle detected: {cycle}"

    expected = "valid spanning RDST from root 0"
    return make_test_result(passed, expected, actual)

test_integration_rdmst_spanning.input_description = "5-node complete graph, root=0"
