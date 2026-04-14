"""
Tests for Module 4: Kevin Bacon

Tests the following:
- Queue class (__init__, __len__, __str__, push, pop, clear)
- bfs(graph, start_node)
- distance_histogram(graph, node)
- find_path(graph, start_person, end_person, parents)
- play_kevin_bacon_game(graph, start_person, end_people)
"""

import sys
import io
from pathlib import Path

_tests_dir = Path(__file__).parent
if str(_tests_dir) not in sys.path:
    sys.path.insert(0, str(_tests_dir))

from fixtures import make_test_result, build_simple_graph, build_line_graph_undirected, build_complete_graph_undirected

# Point values for each test
TEST_POINTS = {
    # Queue tests (11)
    "test_queue_init_empty": 0.5,
    "test_queue_len_empty": 0.5,
    "test_queue_len_after_push": 0.5,
    "test_queue_str_returns_string": 0.5,
    "test_queue_push_pop_fifo": 1,
    "test_queue_pop_returns_first": 1,
    "test_queue_clear_empties": 1,
    "test_queue_multiple_operations": 1,
    "test_queue_mixed_types": 1,
    "test_queue_len_after_pop": 0.5,
    "test_queue_push_after_clear": 1,
    # BFS tests (10)
    "test_bfs_single_node": 1,
    "test_bfs_two_nodes": 1,
    "test_bfs_line_graph": 1,
    "test_bfs_distances_correct": 1,
    "test_bfs_parents_correct": 1,
    "test_bfs_unreachable": 1,
    "test_bfs_cyclic_graph": 1,
    "test_bfs_star_graph": 1,
    "test_bfs_returns_tuple": 0.5,
    "test_bfs_complete_graph": 1,
    # Distance histogram tests (6)
    "test_distance_histogram_single": 1,
    "test_distance_histogram_line": 1,
    "test_distance_histogram_complete": 1,
    "test_distance_histogram_disconnected": 1,
    "test_distance_histogram_star": 1,
    "test_distance_histogram_returns_dict": 0.5,
    # Find path tests (7)
    "test_find_path_adjacent": 1,
    "test_find_path_multi_hop": 1,
    "test_find_path_format": 1,
    "test_find_path_self": 1,
    "test_find_path_unreachable": 1,
    "test_find_path_longer": 1,
    "test_find_path_has_edge_attrs": 1,
    # Play game tests (5)
    "test_play_game_prints": 1,
    "test_play_game_uses_path": 1,
    "test_play_game_multiple_targets": 1,
    "test_play_game_unreachable_target": 1,
    "test_play_game_empty_targets": 0.5,
}


# ============================================================================
# Queue Tests
# ============================================================================

def test_queue_init_empty(module):
    """Test that Queue initializes as empty."""
    q = module.Queue()
    length = len(q)
    return make_test_result(length == 0, 0, length)

test_queue_init_empty.input_description = "Queue()"


def test_queue_len_empty(module):
    """Test __len__ on empty queue."""
    q = module.Queue()
    result = len(q)
    expected = 0
    return make_test_result(result == expected, expected, result)

test_queue_len_empty.input_description = "len(Queue())"


def test_queue_len_after_push(module):
    """Test __len__ after pushing items."""
    q = module.Queue()
    q.push("a")
    q.push("b")
    q.push("c")
    result = len(q)
    expected = 3
    return make_test_result(result == expected, expected, result)

test_queue_len_after_push.input_description = "push 3 items, len()"


def test_queue_str_returns_string(module):
    """Test that __str__ returns a string."""
    q = module.Queue()
    q.push("item")
    result = str(q)
    if not isinstance(result, str):
        return make_test_result(False, "str", type(result).__name__)
    return make_test_result(True, "str", "str")

test_queue_str_returns_string.input_description = "str(Queue())"


def test_queue_push_pop_fifo(module):
    """Test FIFO behavior: push a,b,c then pop should give a,b,c."""
    q = module.Queue()
    q.push("a")
    q.push("b")
    q.push("c")

    results = [q.pop(), q.pop(), q.pop()]
    expected = ["a", "b", "c"]

    if results != expected:
        return make_test_result(False, expected, results)
    return make_test_result(True, expected, results)

test_queue_push_pop_fifo.input_description = "push a,b,c then pop 3 times"


def test_queue_pop_returns_first(module):
    """Test that pop returns the first (oldest) item."""
    q = module.Queue()
    q.push("first")
    q.push("second")

    result = q.pop()
    expected = "first"

    return make_test_result(result == expected, expected, result)

test_queue_pop_returns_first.input_description = "push first,second then pop"


def test_queue_clear_empties(module):
    """Test that clear() empties the queue."""
    q = module.Queue()
    q.push("a")
    q.push("b")
    q.clear()

    result = len(q)
    expected = 0

    return make_test_result(result == expected, expected, result)

test_queue_clear_empties.input_description = "push items, clear(), len()"


def test_queue_multiple_operations(module):
    """Test queue with interleaved push and pop."""
    q = module.Queue()
    q.push(1)
    q.push(2)
    r1 = q.pop()  # Should be 1
    q.push(3)
    r2 = q.pop()  # Should be 2
    r3 = q.pop()  # Should be 3

    results = [r1, r2, r3]
    expected = [1, 2, 3]

    return make_test_result(results == expected, expected, results)

test_queue_multiple_operations.input_description = "interleaved push/pop"


# ============================================================================
# Queue Tests
# ============================================================================

def test_queue_mixed_types(module):
    """Test queue with different data types."""
    q = module.Queue()
    q.push("string")
    q.push(42)
    q.push([1, 2, 3])
    q.push({"key": "value"})

    results = [q.pop(), q.pop(), q.pop(), q.pop()]
    expected = ["string", 42, [1, 2, 3], {"key": "value"}]

    if results != expected:
        return make_test_result(False, expected, results)
    return make_test_result(True, expected, results)

test_queue_mixed_types.input_description = "push string, int, list, dict"


def test_queue_len_after_pop(module):
    """Test that len decreases after pop."""
    q = module.Queue()
    q.push("a")
    q.push("b")
    q.push("c")

    q.pop()
    result = len(q)
    expected = 2

    if result != expected:
        return make_test_result(False, expected, result)
    return make_test_result(True, expected, result)

test_queue_len_after_pop.input_description = "push 3, pop 1, len()"


def test_queue_push_after_clear(module):
    """Test that queue works after clear."""
    q = module.Queue()
    q.push("a")
    q.push("b")
    q.clear()
    q.push("c")
    q.push("d")

    results = [q.pop(), q.pop()]
    expected = ["c", "d"]

    if results != expected:
        return make_test_result(False, expected, results)
    return make_test_result(True, expected, results)

test_queue_push_after_clear.input_description = "push, clear, push more, pop"


# ============================================================================
# BFS Tests
# ============================================================================

def test_bfs_single_node(module):
    """Test BFS on a single node graph."""
    from comp140_module4_graphs import Graph
    g = Graph()
    g.add_node("A")

    dist, parent = module.bfs(g, "A")

    if dist.get("A") != 0:
        return make_test_result(False, "dist['A']=0", f"dist['A']={dist.get('A')}")
    if parent.get("A") is not None:
        return make_test_result(False, "parent['A']=None", f"parent['A']={parent.get('A')}")

    return make_test_result(True, "correct", "correct")

test_bfs_single_node.input_description = "single node A"


def test_bfs_two_nodes(module):
    """Test BFS on two connected nodes."""
    from comp140_module4_graphs import Graph
    g = Graph()
    g.add_edge("A", "B", {})

    dist, parent = module.bfs(g, "A")

    if dist.get("A") != 0:
        return make_test_result(False, "dist['A']=0", f"dist['A']={dist.get('A')}")
    if dist.get("B") != 1:
        return make_test_result(False, "dist['B']=1", f"dist['B']={dist.get('B')}")

    return make_test_result(True, "correct distances", "correct distances")

test_bfs_two_nodes.input_description = "A--B graph"


def test_bfs_line_graph(module):
    """Test BFS on a line graph A-B-C-D-E."""
    g = build_line_graph_undirected(5)
    dist, parent = module.bfs(g, "A")

    # Check distances: A=0, B=1, C=2, D=3, E=4
    expected_dist = {"A": 0, "B": 1, "C": 2, "D": 3, "E": 4}

    for node, expected in expected_dist.items():
        actual = dist.get(node)
        if actual != expected:
            return make_test_result(False, f"dist['{node}']={expected}", f"dist['{node}']={actual}")

    return make_test_result(True, "correct distances", "correct distances")

test_bfs_line_graph.input_description = "line A-B-C-D-E"


def test_bfs_distances_correct(module):
    """Test BFS returns correct distances."""
    g = build_simple_graph()  # A-B-C line
    dist, parent = module.bfs(g, "A")

    expected = {"A": 0, "B": 1, "C": 2}

    for node, exp_dist in expected.items():
        if dist.get(node) != exp_dist:
            return make_test_result(False, expected, dist)

    return make_test_result(True, expected, dist)

test_bfs_distances_correct.input_description = "simple A-B-C graph"


def test_bfs_parents_correct(module):
    """Test BFS returns correct parent mapping."""
    g = build_simple_graph()  # A-B-C line
    dist, parent = module.bfs(g, "A")

    # Parent of A should be None
    if parent.get("A") is not None:
        return make_test_result(False, "parent['A']=None", f"parent['A']={parent.get('A')}")

    # Parent of B should be A
    if parent.get("B") != "A":
        return make_test_result(False, "parent['B']='A'", f"parent['B']={parent.get('B')}")

    # Parent of C should be B
    if parent.get("C") != "B":
        return make_test_result(False, "parent['C']='B'", f"parent['C']={parent.get('C')}")

    return make_test_result(True, "correct parents", "correct parents")

test_bfs_parents_correct.input_description = "simple A-B-C graph"


def test_bfs_unreachable(module):
    """Test BFS with unreachable nodes."""
    from comp140_module4_graphs import Graph
    g = Graph()
    g.add_node("A")
    g.add_node("B")  # Not connected to A

    dist, parent = module.bfs(g, "A")

    # B should be unreachable (infinity)
    b_dist = dist.get("B")
    if b_dist != float("inf"):
        return make_test_result(False, "inf", b_dist)

    return make_test_result(True, "inf", b_dist)

test_bfs_unreachable.input_description = "disconnected A, B"


def test_bfs_cyclic_graph(module):
    """Test BFS on a graph with cycles (triangle)."""
    from comp140_module4_graphs import Graph
    g = Graph()
    g.add_edge("A", "B", {"movie": "Film1"})
    g.add_edge("B", "C", {"movie": "Film2"})
    g.add_edge("C", "A", {"movie": "Film3"})  # Creates cycle

    dist, parent = module.bfs(g, "A")

    # All nodes should be reachable
    if dist.get("A") != 0:
        return make_test_result(False, "dist['A']=0", f"dist['A']={dist.get('A')}")
    if dist.get("B") != 1:
        return make_test_result(False, "dist['B']=1", f"dist['B']={dist.get('B')}")
    if dist.get("C") != 1:
        return make_test_result(False, "dist['C']=1", f"dist['C']={dist.get('C')}")

    return make_test_result(True, "correct distances", "correct distances")

test_bfs_cyclic_graph.input_description = "triangle A-B-C-A"


def test_bfs_star_graph(module):
    """Test BFS on a star graph (center connected to all others)."""
    from comp140_module4_graphs import Graph
    g = Graph()
    for node in ["B", "C", "D", "E"]:
        g.add_edge("A", node, {"movie": f"Film{node}"})

    dist, parent = module.bfs(g, "A")

    # A is center, all others at distance 1
    if dist.get("A") != 0:
        return make_test_result(False, "dist['A']=0", f"dist['A']={dist.get('A')}")

    for node in ["B", "C", "D", "E"]:
        if dist.get(node) != 1:
            return make_test_result(False, f"dist['{node}']=1", f"dist['{node}']={dist.get(node)}")
        if parent.get(node) != "A":
            return make_test_result(False, f"parent['{node}']='A'", f"parent['{node}']={parent.get(node)}")

    return make_test_result(True, "correct star BFS", "correct star BFS")

test_bfs_star_graph.input_description = "star with A as center"


def test_bfs_returns_tuple(module):
    """Test that BFS returns a tuple of two dicts."""
    from comp140_module4_graphs import Graph
    g = Graph()
    g.add_node("A")

    result = module.bfs(g, "A")

    if not isinstance(result, tuple):
        return make_test_result(False, "tuple", type(result).__name__)
    if len(result) != 2:
        return make_test_result(False, "2-tuple", f"{len(result)}-tuple")
    if not isinstance(result[0], dict) or not isinstance(result[1], dict):
        return make_test_result(False, "(dict, dict)", f"({type(result[0]).__name__}, {type(result[1]).__name__})")

    return make_test_result(True, "(dict, dict)", "(dict, dict)")

test_bfs_returns_tuple.input_description = "return type check"


def test_bfs_complete_graph(module):
    """Test BFS on a complete graph."""
    g = build_complete_graph_undirected(4)  # A, B, C, D all connected

    dist, parent = module.bfs(g, "A")

    # All nodes should be at distance 1 from A (except A itself)
    if dist.get("A") != 0:
        return make_test_result(False, "dist['A']=0", f"dist['A']={dist.get('A')}")

    for node in ["B", "C", "D"]:
        if dist.get(node) != 1:
            return make_test_result(False, f"dist['{node}']=1", f"dist['{node}']={dist.get(node)}")

    return make_test_result(True, "complete graph BFS", "complete graph BFS")

test_bfs_complete_graph.input_description = "complete 4-node graph"


# ============================================================================
# Distance Histogram Tests
# ============================================================================

def test_distance_histogram_single(module):
    """Test distance_histogram on single node."""
    from comp140_module4_graphs import Graph
    g = Graph()
    g.add_node("A")

    result = module.distance_histogram(g, "A")

    # Should have {0: 1} (only A at distance 0)
    expected = {0: 1}

    if result != expected:
        return make_test_result(False, expected, result)
    return make_test_result(True, expected, result)

test_distance_histogram_single.input_description = "single node A"


def test_distance_histogram_line(module):
    """Test distance_histogram on line graph."""
    g = build_line_graph_undirected(4)  # A-B-C-D

    result = module.distance_histogram(g, "A")

    # From A: A=0, B=1, C=2, D=3
    # Histogram: {0: 1, 1: 1, 2: 1, 3: 1}
    expected = {0: 1, 1: 1, 2: 1, 3: 1}

    if result != expected:
        return make_test_result(False, expected, result)
    return make_test_result(True, expected, result)

test_distance_histogram_line.input_description = "line A-B-C-D"


def test_distance_histogram_complete(module):
    """Test distance_histogram on complete graph."""
    from comp140_module4_graphs import Graph
    g = Graph()
    for n in ["A", "B", "C", "D"]:
        g.add_node(n)
    for n1 in ["A", "B", "C", "D"]:
        for n2 in ["A", "B", "C", "D"]:
            if n1 < n2:
                g.add_edge(n1, n2, {})

    result = module.distance_histogram(g, "A")

    # All others at distance 1 from A
    expected = {0: 1, 1: 3}

    if result != expected:
        return make_test_result(False, expected, result)
    return make_test_result(True, expected, result)

test_distance_histogram_complete.input_description = "complete 4-node graph"


def test_distance_histogram_disconnected(module):
    """Test distance_histogram excludes unreachable nodes."""
    from comp140_module4_graphs import Graph
    g = Graph()
    g.add_edge("A", "B", {})
    g.add_node("C")  # Disconnected

    result = module.distance_histogram(g, "A")

    # Should only count A (dist 0) and B (dist 1), not C (unreachable)
    expected = {0: 1, 1: 1}

    if result != expected:
        return make_test_result(False, expected, result)
    return make_test_result(True, expected, result)

test_distance_histogram_disconnected.input_description = "A-B with disconnected C"


def test_distance_histogram_star(module):
    """Test distance_histogram on star graph."""
    from comp140_module4_graphs import Graph
    g = Graph()
    for node in ["B", "C", "D", "E"]:
        g.add_edge("A", node, {})

    result = module.distance_histogram(g, "A")

    # A at 0, B/C/D/E all at 1
    expected = {0: 1, 1: 4}

    if result != expected:
        return make_test_result(False, expected, result)
    return make_test_result(True, expected, result)

test_distance_histogram_star.input_description = "star with 5 nodes"


def test_distance_histogram_returns_dict(module):
    """Test that distance_histogram returns a dictionary."""
    from comp140_module4_graphs import Graph
    g = Graph()
    g.add_node("A")

    result = module.distance_histogram(g, "A")

    if not isinstance(result, dict):
        return make_test_result(False, "dict", type(result).__name__)
    return make_test_result(True, "dict", "dict")

test_distance_histogram_returns_dict.input_description = "return type check"


# ============================================================================
# Find Path Tests
# ============================================================================

def test_find_path_adjacent(module):
    """Test find_path between adjacent nodes."""
    g = build_simple_graph()  # A-B-C
    dist, parent = module.bfs(g, "A")

    path = module.find_path(g, "A", "B", parent)

    # Path should be [(A, {movie}), (B, set())]
    if not isinstance(path, list):
        return make_test_result(False, "list", type(path).__name__)
    if len(path) != 2:
        return make_test_result(False, "2 steps", f"{len(path)} steps")

    # First node should be A
    if path[0][0] != "A":
        return make_test_result(False, "starts with A", f"starts with {path[0][0]}")

    # Last node should be B
    if path[-1][0] != "B":
        return make_test_result(False, "ends with B", f"ends with {path[-1][0]}")

    return make_test_result(True, "correct path", "correct path")

test_find_path_adjacent.input_description = "A to B in A-B-C"


def test_find_path_multi_hop(module):
    """Test find_path across multiple hops."""
    g = build_simple_graph()  # A-B-C
    dist, parent = module.bfs(g, "A")

    path = module.find_path(g, "A", "C", parent)

    if len(path) != 3:
        return make_test_result(False, "3 steps", f"{len(path)} steps")

    nodes = [step[0] for step in path]
    expected_nodes = ["A", "B", "C"]

    if nodes != expected_nodes:
        return make_test_result(False, expected_nodes, nodes)
    return make_test_result(True, expected_nodes, nodes)

test_find_path_multi_hop.input_description = "A to C in A-B-C"


def test_find_path_format(module):
    """Test find_path returns correct format."""
    g = build_simple_graph()
    dist, parent = module.bfs(g, "A")

    path = module.find_path(g, "A", "C", parent)

    # Each step should be (node, edge_attrs) tuple
    for step in path:
        if not isinstance(step, tuple):
            return make_test_result(False, "tuple", type(step).__name__)
        if len(step) != 2:
            return make_test_result(False, "2-tuple", f"{len(step)}-tuple")

    # Last step should have empty edge attrs
    last_attrs = path[-1][1]
    if last_attrs and len(last_attrs) > 0 and last_attrs != set():
        return make_test_result(False, "empty attrs for last", str(last_attrs))

    return make_test_result(True, "correct format", "correct format")

test_find_path_format.input_description = "A to C path format"


def test_find_path_self(module):
    """Test find_path from a node to itself."""
    from comp140_module4_graphs import Graph
    g = Graph()
    g.add_node("A")

    dist, parent = module.bfs(g, "A")
    path = module.find_path(g, "A", "A", parent)

    # Path from A to A should be [(A, set())]
    if not isinstance(path, list):
        return make_test_result(False, "list", type(path).__name__)
    if len(path) != 1:
        return make_test_result(False, "1 step", f"{len(path)} steps")
    if path[0][0] != "A":
        return make_test_result(False, "[(A, ...)]", str(path))

    return make_test_result(True, "self path", "self path")

test_find_path_self.input_description = "A to A"


def test_find_path_unreachable(module):
    """Test find_path returns empty list for unreachable nodes."""
    from comp140_module4_graphs import Graph
    g = Graph()
    g.add_node("A")
    g.add_node("B")  # Not connected

    dist, parent = module.bfs(g, "A")
    path = module.find_path(g, "A", "B", parent)

    # Should return empty list
    if path != []:
        return make_test_result(False, "[]", str(path))
    return make_test_result(True, "[]", "[]")

test_find_path_unreachable.input_description = "A to disconnected B"


def test_find_path_longer(module):
    """Test find_path on longer path."""
    g = build_line_graph_undirected(5)  # A-B-C-D-E
    dist, parent = module.bfs(g, "A")

    path = module.find_path(g, "A", "E", parent)

    if len(path) != 5:
        return make_test_result(False, "5 steps", f"{len(path)} steps")

    nodes = [step[0] for step in path]
    expected_nodes = ["A", "B", "C", "D", "E"]

    if nodes != expected_nodes:
        return make_test_result(False, expected_nodes, nodes)
    return make_test_result(True, expected_nodes, nodes)

test_find_path_longer.input_description = "A to E in 5-node line"


def test_find_path_has_edge_attrs(module):
    """Test find_path includes edge attributes (movies)."""
    g = build_simple_graph()  # A-B-C with movie attrs
    dist, parent = module.bfs(g, "A")

    path = module.find_path(g, "A", "B", parent)

    # First step should have edge attrs to next node
    if len(path) < 1:
        return make_test_result(False, "non-empty path", "empty path")

    first_attrs = path[0][1]
    # Should have some edge attributes (movies)
    if not isinstance(first_attrs, (set, dict)):
        return make_test_result(False, "set or dict attrs", type(first_attrs).__name__)

    return make_test_result(True, "has edge attrs", "has edge attrs")

test_find_path_has_edge_attrs.input_description = "check edge attributes included"


# ============================================================================
# Play Kevin Bacon Game Tests
# ============================================================================

def test_play_game_prints(module):
    """Test that play_kevin_bacon_game produces output."""
    g = build_simple_graph()

    # Capture stdout
    old_stdout = sys.stdout
    sys.stdout = captured = io.StringIO()

    try:
        module.play_kevin_bacon_game(g, "A", ["C"])
    finally:
        sys.stdout = old_stdout

    output = captured.getvalue()

    if len(output) == 0:
        return make_test_result(False, "some output", "no output")
    return make_test_result(True, "prints path", "prints path")

test_play_game_prints.input_description = "A to [C] in simple graph"


def test_play_game_uses_path(module):
    """Test that play_kevin_bacon_game shows the path."""
    g = build_simple_graph()

    old_stdout = sys.stdout
    sys.stdout = captured = io.StringIO()

    try:
        module.play_kevin_bacon_game(g, "A", ["B"])
    finally:
        sys.stdout = old_stdout

    output = captured.getvalue()

    # Output should mention A and B (the path nodes)
    if "A" not in output and "B" not in output:
        return make_test_result(False, "contains A, B", output[:50])
    return make_test_result(True, "contains path", "contains path")

test_play_game_uses_path.input_description = "A to [B] in simple graph"


def test_play_game_multiple_targets(module):
    """Test play_kevin_bacon_game with multiple target people."""
    g = build_simple_graph()  # A-B-C

    old_stdout = sys.stdout
    sys.stdout = captured = io.StringIO()

    try:
        module.play_kevin_bacon_game(g, "A", ["B", "C"])
    finally:
        sys.stdout = old_stdout

    output = captured.getvalue()

    # Should print paths to both B and C
    if "B" not in output:
        return make_test_result(False, "mentions B", "B not in output")
    if "C" not in output:
        return make_test_result(False, "mentions C", "C not in output")

    return make_test_result(True, "paths to B and C", "paths to B and C")

test_play_game_multiple_targets.input_description = "A to [B, C]"


def test_play_game_unreachable_target(module):
    """Test play_kevin_bacon_game handles unreachable target."""
    from comp140_module4_graphs import Graph
    g = Graph()
    g.add_node("A")
    g.add_node("B")  # Disconnected

    old_stdout = sys.stdout
    sys.stdout = captured = io.StringIO()

    try:
        module.play_kevin_bacon_game(g, "A", ["B"])
    except Exception as e:
        sys.stdout = old_stdout
        return make_test_result(False, "no exception", str(e))
    finally:
        sys.stdout = old_stdout

    # Should complete without error (may print "no path" message)
    return make_test_result(True, "handles unreachable", "handles unreachable")

test_play_game_unreachable_target.input_description = "A to [disconnected B]"


def test_play_game_empty_targets(module):
    """Test play_kevin_bacon_game with empty target list."""
    g = build_simple_graph()

    old_stdout = sys.stdout
    sys.stdout = captured = io.StringIO()

    try:
        module.play_kevin_bacon_game(g, "A", [])
    except Exception as e:
        sys.stdout = old_stdout
        return make_test_result(False, "no exception", str(e))
    finally:
        sys.stdout = old_stdout

    # Should complete without error
    return make_test_result(True, "handles empty list", "handles empty list")

test_play_game_empty_targets.input_description = "A to []"
