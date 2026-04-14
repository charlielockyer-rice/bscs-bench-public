"""
Tests for Module 7: Map Search

Tests the following:
- Queue class (__init__, __len__, __str__, push, pop, clear)
- Stack class (__init__, __len__, __str__, push, pop, clear)
- bfs_dfs(graph, rac_class, start_node, end_node)
- dfs(graph, start_node, end_node, parent)
- astar(graph, start_node, end_node, edge_distance, straight_line_distance)
"""

import sys
from pathlib import Path

_tests_dir = Path(__file__).parent
if str(_tests_dir) not in sys.path:
    sys.path.insert(0, str(_tests_dir))

from fixtures import (
    make_test_result, build_line_graph_directed,
    build_grid_graph, build_tree_graph
)

# Add lib to path
lib_path = Path(__file__).parent.parent.parent / "lib"
if str(lib_path) not in sys.path:
    sys.path.insert(0, str(lib_path))

import comp140_module7 as maps

# Point values for each test
TEST_POINTS = {
    # Queue tests
    "test_queue_init": 0.5,
    "test_queue_len": 0.5,
    "test_queue_fifo": 1,
    "test_queue_clear": 0.5,
    "test_queue_str": 0.5,
    "test_queue_operations": 1,
    "test_queue_multiple_types": 0.5,
    "test_queue_push_after_clear": 0.5,
    "test_queue_len_decreases": 0.5,
    # Stack tests
    "test_stack_init": 0.5,
    "test_stack_len": 0.5,
    "test_stack_lifo": 1,
    "test_stack_clear": 0.5,
    "test_stack_str": 0.5,
    "test_stack_operations": 1,
    "test_stack_multiple_types": 0.5,
    "test_stack_push_after_clear": 0.5,
    "test_stack_len_decreases": 0.5,
    # BFS/DFS tests
    "test_bfs_dfs_line_bfs": 1,
    "test_bfs_dfs_line_dfs": 1,
    "test_bfs_dfs_grid_bfs": 1,
    "test_bfs_dfs_grid_dfs": 1,
    "test_bfs_dfs_returns_parent": 1,
    "test_bfs_dfs_finds_end": 1,
    "test_bfs_dfs_unreachable": 1,
    "test_bfs_dfs_same_start_end": 1,
    "test_bfs_dfs_tree_bfs": 1,
    "test_bfs_dfs_parent_chain": 1,
    # Recursive DFS tests
    "test_dfs_recursive_line": 1,
    "test_dfs_recursive_tree": 1,
    "test_dfs_recursive_modifies_parent": 1,
    "test_dfs_recursive_stops_at_end": 1,
    "test_dfs_recursive_grid": 1,
    "test_dfs_recursive_same_start_end": 1,
    "test_dfs_recursive_unreachable": 1,
    # A* tests
    "test_astar_line": 1,
    "test_astar_grid": 1,
    "test_astar_tree": 1,
    "test_astar_returns_parent": 1,
    "test_astar_finds_path": 1,
    "test_astar_optimal": 1,
    "test_astar_uses_heuristic": 1,
    "test_astar_same_start_end": 1,
    "test_astar_single_step": 1,
    "test_astar_unreachable": 1,
}


# ============================================================================
# Queue Tests
# ============================================================================

def test_queue_init(module):
    """Test Queue initialization."""
    q = module.Queue()
    length = len(q)
    return make_test_result(length == 0, 0, length)

test_queue_init.input_description = "Queue()"


def test_queue_len(module):
    """Test Queue __len__."""
    q = module.Queue()
    q.push("a")
    q.push("b")
    result = len(q)
    expected = 2
    return make_test_result(result == expected, expected, result)

test_queue_len.input_description = "push 2 items, len()"


def test_queue_fifo(module):
    """Test Queue FIFO behavior."""
    q = module.Queue()
    q.push("first")
    q.push("second")
    q.push("third")

    results = [q.pop(), q.pop(), q.pop()]
    expected = ["first", "second", "third"]

    return make_test_result(results == expected, expected, results)

test_queue_fifo.input_description = "push first,second,third then pop 3"


def test_queue_clear(module):
    """Test Queue clear."""
    q = module.Queue()
    q.push("a")
    q.push("b")
    q.clear()
    result = len(q)
    return make_test_result(result == 0, 0, result)

test_queue_clear.input_description = "push items, clear(), len()"


def test_queue_str(module):
    """Test Queue __str__."""
    q = module.Queue()
    q.push("item")
    result = str(q)
    return make_test_result(isinstance(result, str), "str", type(result).__name__)

test_queue_str.input_description = "str(Queue)"


def test_queue_operations(module):
    """Test Queue with interleaved operations."""
    q = module.Queue()
    q.push(1)
    q.push(2)
    r1 = q.pop()
    q.push(3)
    r2 = q.pop()
    r3 = q.pop()

    results = [r1, r2, r3]
    expected = [1, 2, 3]

    return make_test_result(results == expected, expected, results)

test_queue_operations.input_description = "interleaved push/pop"


def test_queue_multiple_types(module):
    """Test Queue with different types."""
    q = module.Queue()
    q.push(1)
    q.push("string")
    q.push([1, 2, 3])

    r1 = q.pop()
    r2 = q.pop()
    r3 = q.pop()

    if r1 == 1 and r2 == "string" and r3 == [1, 2, 3]:
        return make_test_result(True, "mixed types", "mixed types")
    return make_test_result(False, "[1, 'string', [1,2,3]]", [r1, r2, r3])

test_queue_multiple_types.input_description = "push int, string, list"


def test_queue_push_after_clear(module):
    """Test Queue push after clear."""
    q = module.Queue()
    q.push(1)
    q.push(2)
    q.clear()
    q.push(3)
    q.push(4)

    results = [q.pop(), q.pop()]
    expected = [3, 4]

    return make_test_result(results == expected, expected, results)

test_queue_push_after_clear.input_description = "push, clear, push, pop"


def test_queue_len_decreases(module):
    """Test Queue length decreases after pop."""
    q = module.Queue()
    q.push(1)
    q.push(2)
    q.push(3)

    len_before = len(q)
    q.pop()
    len_after = len(q)

    expected = (3, 2)
    actual = (len_before, len_after)

    return make_test_result(actual == expected, expected, actual)

test_queue_len_decreases.input_description = "len before/after pop"


# ============================================================================
# Stack Tests
# ============================================================================


# ============================================================================
# Queue Tests
# ============================================================================

def test_stack_init(module):
    """Test Stack initialization."""
    s = module.Stack()
    length = len(s)
    return make_test_result(length == 0, 0, length)

test_stack_init.input_description = "Stack()"


def test_stack_len(module):
    """Test Stack __len__."""
    s = module.Stack()
    s.push("a")
    s.push("b")
    result = len(s)
    expected = 2
    return make_test_result(result == expected, expected, result)

test_stack_len.input_description = "push 2 items, len()"


def test_stack_lifo(module):
    """Test Stack LIFO behavior."""
    s = module.Stack()
    s.push("first")
    s.push("second")
    s.push("third")

    results = [s.pop(), s.pop(), s.pop()]
    expected = ["third", "second", "first"]  # LIFO order

    return make_test_result(results == expected, expected, results)

test_stack_lifo.input_description = "push first,second,third then pop 3"


def test_stack_clear(module):
    """Test Stack clear."""
    s = module.Stack()
    s.push("a")
    s.push("b")
    s.clear()
    result = len(s)
    return make_test_result(result == 0, 0, result)

test_stack_clear.input_description = "push items, clear(), len()"


def test_stack_str(module):
    """Test Stack __str__."""
    s = module.Stack()
    s.push("item")
    result = str(s)
    return make_test_result(isinstance(result, str), "str", type(result).__name__)

test_stack_str.input_description = "str(Stack)"


def test_stack_operations(module):
    """Test Stack with interleaved operations."""
    s = module.Stack()
    s.push(1)
    s.push(2)
    r1 = s.pop()  # 2 (LIFO)
    s.push(3)
    r2 = s.pop()  # 3
    r3 = s.pop()  # 1

    results = [r1, r2, r3]
    expected = [2, 3, 1]

    return make_test_result(results == expected, expected, results)

test_stack_operations.input_description = "interleaved push/pop"


def test_stack_multiple_types(module):
    """Test Stack with different types."""
    s = module.Stack()
    s.push(1)
    s.push("string")
    s.push([1, 2, 3])

    r1 = s.pop()  # [1, 2, 3] (LIFO)
    r2 = s.pop()  # "string"
    r3 = s.pop()  # 1

    if r1 == [1, 2, 3] and r2 == "string" and r3 == 1:
        return make_test_result(True, "mixed types", "mixed types")
    return make_test_result(False, "[[1,2,3], 'string', 1]", [r1, r2, r3])

test_stack_multiple_types.input_description = "push int, string, list"


def test_stack_push_after_clear(module):
    """Test Stack push after clear."""
    s = module.Stack()
    s.push(1)
    s.push(2)
    s.clear()
    s.push(3)
    s.push(4)

    results = [s.pop(), s.pop()]  # 4, 3 (LIFO)
    expected = [4, 3]

    return make_test_result(results == expected, expected, results)

test_stack_push_after_clear.input_description = "push, clear, push, pop"


def test_stack_len_decreases(module):
    """Test Stack length decreases after pop."""
    s = module.Stack()
    s.push(1)
    s.push(2)
    s.push(3)

    len_before = len(s)
    s.pop()
    len_after = len(s)

    expected = (3, 2)
    actual = (len_before, len_after)

    return make_test_result(actual == expected, expected, actual)

test_stack_len_decreases.input_description = "len before/after pop"


# ============================================================================
# BFS/DFS Tests
# ============================================================================

def test_bfs_dfs_line_bfs(module):
    """Test bfs_dfs with BFS on line graph."""
    g = build_line_graph_directed(5)  # A-B-C-D-E

    parent = module.bfs_dfs(g, module.Queue, "A", "E")

    # Should find a path from A to E
    if "E" not in parent:
        return make_test_result(False, "E in parent", "E not found")

    return make_test_result(True, "finds E", "finds E")

test_bfs_dfs_line_bfs.input_description = "BFS on A-B-C-D-E"


def test_bfs_dfs_line_dfs(module):
    """Test bfs_dfs with DFS on line graph."""
    g = build_line_graph_directed(5)

    parent = module.bfs_dfs(g, module.Stack, "A", "E")

    if "E" not in parent:
        return make_test_result(False, "E in parent", "E not found")

    return make_test_result(True, "finds E", "finds E")

test_bfs_dfs_line_dfs.input_description = "DFS on A-B-C-D-E"


def test_bfs_dfs_grid_bfs(module):
    """Test bfs_dfs with BFS on grid graph."""
    g = build_grid_graph(3, 3)

    parent = module.bfs_dfs(g, module.Queue, "A", "I")

    if "I" not in parent:
        return make_test_result(False, "I in parent", "I not found")

    return make_test_result(True, "finds I", "finds I")

test_bfs_dfs_grid_bfs.input_description = "BFS on 3x3 grid"


def test_bfs_dfs_grid_dfs(module):
    """Test bfs_dfs with DFS on grid graph."""
    g = build_grid_graph(3, 3)

    parent = module.bfs_dfs(g, module.Stack, "A", "I")

    if "I" not in parent:
        return make_test_result(False, "I in parent", "I not found")

    return make_test_result(True, "finds I", "finds I")

test_bfs_dfs_grid_dfs.input_description = "DFS on 3x3 grid"


def test_bfs_dfs_returns_parent(module):
    """Test that bfs_dfs returns parent dictionary."""
    g = build_line_graph_directed(3)  # A-B-C

    parent = module.bfs_dfs(g, module.Queue, "A", "C")

    if not isinstance(parent, dict):
        return make_test_result(False, "dict", type(parent).__name__)

    # A should have parent None
    if parent.get("A") is not None:
        return make_test_result(False, "parent[A]=None", f"parent[A]={parent.get('A')}")

    return make_test_result(True, "correct parent dict", "correct parent dict")

test_bfs_dfs_returns_parent.input_description = "A-B-C returns parent dict"


def test_bfs_dfs_finds_end(module):
    """Test that bfs_dfs includes end node."""
    g = build_line_graph_directed(3)

    parent = module.bfs_dfs(g, module.Queue, "A", "C")

    if "C" in parent:
        return make_test_result(True, "C in parent", "C in parent")
    return make_test_result(False, "C in parent", "C not in parent")

test_bfs_dfs_finds_end.input_description = "A-B-C includes C"


def test_bfs_dfs_unreachable(module):
    """Test bfs_dfs with unreachable node."""
    from comp140_module7_graphs import DiGraph
    g = DiGraph()
    g.add_node("A")
    g.add_node("B")  # Not connected
    g.add_node_attr("A", "x", 0)
    g.add_node_attr("A", "y", 0)
    g.add_node_attr("B", "x", 1)
    g.add_node_attr("B", "y", 0)

    parent = module.bfs_dfs(g, module.Queue, "A", "B")

    # B should not be in parent (unreachable)
    if "B" not in parent:
        return make_test_result(True, "B not reachable", "B not reachable")
    return make_test_result(False, "B not reachable", "B was reached")

test_bfs_dfs_unreachable.input_description = "disconnected A, B"


def test_bfs_dfs_same_start_end(module):
    """Test bfs_dfs when start equals end."""
    g = build_line_graph_directed(3)  # A-B-C

    parent = module.bfs_dfs(g, module.Queue, "A", "A")

    # A should be in parent with None parent
    if "A" in parent and parent["A"] is None:
        return make_test_result(True, "A in parent", "A in parent")
    return make_test_result(False, "A in parent", f"parent: {parent}")

test_bfs_dfs_same_start_end.input_description = "BFS from A to A"


def test_bfs_dfs_tree_bfs(module):
    """Test BFS on tree graph."""
    g = build_tree_graph()

    parent = module.bfs_dfs(g, module.Queue, "A", "G")

    if "G" in parent:
        return make_test_result(True, "finds G", "finds G")
    return make_test_result(False, "finds G", "G not found")

test_bfs_dfs_tree_bfs.input_description = "BFS on tree to G"


def test_bfs_dfs_parent_chain(module):
    """Test that bfs_dfs creates valid parent chain."""
    g = build_line_graph_directed(4)  # A-B-C-D

    parent = module.bfs_dfs(g, module.Queue, "A", "D")

    # Trace back from D to A
    if "D" not in parent:
        return make_test_result(False, "D reachable", "D not found")

    path = []
    node = "D"
    while node is not None:
        path.append(node)
        node = parent.get(node)

    path.reverse()

    # Should be A-B-C-D
    if path == ["A", "B", "C", "D"]:
        return make_test_result(True, ["A", "B", "C", "D"], path)
    return make_test_result(False, ["A", "B", "C", "D"], path)

test_bfs_dfs_parent_chain.input_description = "BFS parent chain A-B-C-D"


# ============================================================================
# Recursive DFS Tests
# ============================================================================

def test_dfs_recursive_line(module):
    """Test recursive dfs on line graph."""
    g = build_line_graph_directed(4)  # A-B-C-D
    parent = {"A": None}

    module.dfs(g, "A", "D", parent)

    if "D" in parent:
        return make_test_result(True, "finds D", "finds D")
    return make_test_result(False, "finds D", "D not found")

test_dfs_recursive_line.input_description = "recursive DFS on A-B-C-D"


def test_dfs_recursive_tree(module):
    """Test recursive dfs on tree graph."""
    g = build_tree_graph()
    parent = {"A": None}

    module.dfs(g, "A", "G", parent)

    if "G" in parent:
        return make_test_result(True, "finds G", "finds G")
    return make_test_result(False, "finds G", "G not found")

test_dfs_recursive_tree.input_description = "recursive DFS on tree"


def test_dfs_recursive_modifies_parent(module):
    """Test that recursive dfs modifies the parent dictionary."""
    g = build_line_graph_directed(3)
    parent = {"A": None}

    module.dfs(g, "A", "C", parent)

    # Parent dict should now have B and C
    if "B" in parent and "C" in parent:
        return make_test_result(True, "modifies parent", "modifies parent")
    return make_test_result(False, "modifies parent", f"parent keys: {list(parent.keys())}")

test_dfs_recursive_modifies_parent.input_description = "A-B-C modifies parent"


def test_dfs_recursive_stops_at_end(module):
    """Test that recursive dfs can stop when end is found."""
    g = build_line_graph_directed(5)
    parent = {"A": None}

    module.dfs(g, "A", "C", parent)

    # Should have found C
    if "C" in parent:
        return make_test_result(True, "finds C", "finds C")
    return make_test_result(False, "finds C", "C not found")

test_dfs_recursive_stops_at_end.input_description = "A-B-C-D-E, target C"


def test_dfs_recursive_grid(module):
    """Test recursive dfs on grid graph."""
    g = build_grid_graph(3, 3)
    parent = {"A": None}

    module.dfs(g, "A", "I", parent)

    if "I" in parent:
        return make_test_result(True, "finds I", "finds I")
    return make_test_result(False, "finds I", "I not found")

test_dfs_recursive_grid.input_description = "recursive DFS on 3x3 grid"


def test_dfs_recursive_same_start_end(module):
    """Test recursive dfs when start equals end."""
    g = build_line_graph_directed(3)
    parent = {"A": None}

    module.dfs(g, "A", "A", parent)

    # A should be in parent (it was pre-initialized)
    if "A" in parent and parent["A"] is None:
        return make_test_result(True, "A handled", "A handled")
    return make_test_result(False, "A handled", f"parent: {parent}")

test_dfs_recursive_same_start_end.input_description = "DFS from A to A"


def test_dfs_recursive_unreachable(module):
    """Test recursive dfs with unreachable end node."""
    from comp140_module7_graphs import DiGraph
    g = DiGraph()
    g.add_node("A")
    g.add_node("B")  # Not connected
    g.add_node_attr("A", "x", 0)
    g.add_node_attr("A", "y", 0)
    g.add_node_attr("B", "x", 1)
    g.add_node_attr("B", "y", 0)

    parent = {"A": None}
    module.dfs(g, "A", "B", parent)

    # B should not be in parent (unreachable)
    if "B" not in parent:
        return make_test_result(True, "B not reachable", "B not reachable")
    return make_test_result(False, "B not reachable", "B was reached")

test_dfs_recursive_unreachable.input_description = "DFS disconnected graph"


# ============================================================================
# A* Tests
# ============================================================================

def test_astar_line(module):
    """Test A* on line graph."""
    g = build_line_graph_directed(5)

    parent = module.astar(g, "A", "E", maps.test_edge_distance, maps.test_straight_line_distance)

    if "E" in parent:
        return make_test_result(True, "finds E", "finds E")
    return make_test_result(False, "finds E", "E not found")

test_astar_line.input_description = "A* on A-B-C-D-E"


def test_astar_grid(module):
    """Test A* on grid graph."""
    g = build_grid_graph(3, 3)

    parent = module.astar(g, "A", "I", maps.test_edge_distance, maps.test_straight_line_distance)

    if "I" in parent:
        return make_test_result(True, "finds I", "finds I")
    return make_test_result(False, "finds I", "I not found")

test_astar_grid.input_description = "A* on 3x3 grid"


def test_astar_tree(module):
    """Test A* on tree graph."""
    g = build_tree_graph()

    parent = module.astar(g, "A", "G", maps.test_edge_distance, maps.test_straight_line_distance)

    if "G" in parent:
        return make_test_result(True, "finds G", "finds G")
    return make_test_result(False, "finds G", "G not found")

test_astar_tree.input_description = "A* on tree"


def test_astar_returns_parent(module):
    """Test that A* returns parent dictionary."""
    g = build_line_graph_directed(3)

    parent = module.astar(g, "A", "C", maps.test_edge_distance, maps.test_straight_line_distance)

    if not isinstance(parent, dict):
        return make_test_result(False, "dict", type(parent).__name__)

    if parent.get("A") is not None:
        return make_test_result(False, "parent[A]=None", f"parent[A]={parent.get('A')}")

    return make_test_result(True, "correct parent dict", "correct parent dict")

test_astar_returns_parent.input_description = "A* returns parent dict"


def test_astar_finds_path(module):
    """Test that A* finds path to end node."""
    g = build_line_graph_directed(4)

    parent = module.astar(g, "A", "D", maps.test_edge_distance, maps.test_straight_line_distance)

    # Verify we can trace back from D to A
    if "D" not in parent:
        return make_test_result(False, "D in parent", "D not found")

    path = []
    node = "D"
    while node is not None:
        path.append(node)
        node = parent.get(node)

    path.reverse()

    if path[0] == "A" and path[-1] == "D":
        return make_test_result(True, "path A to D", path)
    return make_test_result(False, "path A to D", path)

test_astar_finds_path.input_description = "A* path from A to D"


def test_astar_optimal(module):
    """Test that A* finds optimal path on grid."""
    g = build_grid_graph(3, 3)

    parent = module.astar(g, "A", "I", maps.test_edge_distance, maps.test_straight_line_distance)

    # Count path length
    if "I" not in parent:
        return make_test_result(False, "finds I", "I not found")

    path_len = 0
    node = "I"
    while parent.get(node) is not None:
        path_len += 1
        node = parent.get(node)

    # Optimal path on 3x3 grid from A (top-left) to I (bottom-right) is 4 edges
    expected_len = 4

    if path_len == expected_len:
        return make_test_result(True, expected_len, path_len)
    return make_test_result(False, expected_len, path_len)

test_astar_optimal.input_description = "A* optimal path length"


def test_astar_uses_heuristic(module):
    """Test that A* uses heuristic function."""
    g = build_grid_graph(3, 3)

    # Count calls to heuristic
    call_count = [0]
    original_heuristic = maps.test_straight_line_distance

    def counting_heuristic(n1, n2, graph):
        call_count[0] += 1
        return original_heuristic(n1, n2, graph)

    parent = module.astar(g, "A", "I", maps.test_edge_distance, counting_heuristic)

    if call_count[0] > 0:
        return make_test_result(True, "uses heuristic", f"called {call_count[0]} times")
    return make_test_result(False, "uses heuristic", "heuristic not called")

test_astar_uses_heuristic.input_description = "A* calls heuristic"


def test_astar_same_start_end(module):
    """Test A* when start equals end."""
    g = build_line_graph_directed(3)

    parent = module.astar(g, "A", "A", maps.test_edge_distance, maps.test_straight_line_distance)

    # A should be in parent with None parent
    if "A" in parent and parent["A"] is None:
        return make_test_result(True, "A in parent", "A in parent")
    return make_test_result(False, "A in parent", f"parent: {parent}")

test_astar_same_start_end.input_description = "A* from A to A"


def test_astar_single_step(module):
    """Test A* with single step path."""
    g = build_line_graph_directed(2)  # A-B

    parent = module.astar(g, "A", "B", maps.test_edge_distance, maps.test_straight_line_distance)

    if "B" in parent and parent["B"] == "A":
        return make_test_result(True, "B->A", f"B->{parent.get('B')}")
    return make_test_result(False, "B->A", f"parent: {parent}")

test_astar_single_step.input_description = "A* from A to B (direct)"


def test_astar_unreachable(module):
    """Test A* with unreachable end node."""
    from comp140_module7_graphs import DiGraph
    g = DiGraph()
    g.add_node("A")
    g.add_node("B")  # Not connected
    g.add_node_attr("A", "x", 0)
    g.add_node_attr("A", "y", 0)
    g.add_node_attr("B", "x", 1)
    g.add_node_attr("B", "y", 0)

    parent = module.astar(g, "A", "B", maps.test_edge_distance, maps.test_straight_line_distance)

    # B should not be in parent (unreachable)
    if "B" not in parent:
        return make_test_result(True, "B not reachable", "B not reachable")
    return make_test_result(False, "B not reachable", "B was reached")

test_astar_unreachable.input_description = "A* disconnected graph"
