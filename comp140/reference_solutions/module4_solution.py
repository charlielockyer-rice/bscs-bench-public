"""
Reference Solution for Module 4: Kevin Bacon Game

This module implements:
- Queue class: FIFO queue with standard operations
- bfs: Breadth-first search returning distances and parents
- distance_histogram: Counts nodes at each distance from a source
- find_path: Reconstructs path from BFS parent mapping
- play_kevin_bacon_game: Plays the game for a list of target actors
"""

import sys
from pathlib import Path

# Add lib to path for helper modules
_lib_path = Path(__file__).parent.parent / "lib"
if str(_lib_path) not in sys.path:
    sys.path.insert(0, str(_lib_path))

import simpleplot
import comp140_module4 as movies


class Queue:
    """
    A simple implementation of a FIFO queue.

    Uses a Python list internally where the front of the queue
    is at index 0 and the back is at the end of the list.
    """

    def __init__(self):
        """
        Initialize the queue with an empty list.
        """
        self._items = []

    def __len__(self):
        """
        Returns: an integer representing the number of items in the queue.
        """
        return len(self._items)

    def __str__(self):
        """
        Returns: a string representation of the queue.
        Shows items in order from front to back.
        """
        return "Queue(" + str(self._items) + ")"

    def push(self, item):
        """
        Add item to the back of the queue.

        input:
            - item: any data type that's valid in a list
        """
        self._items.append(item)

    def pop(self):
        """
        Remove and return the oldest item (front of queue).

        Assumes that there is at least one element in the queue.
        It is an error if there is not.

        Returns: the least recently added item.
        """
        return self._items.pop(0)

    def clear(self):
        """
        Remove all items from the queue.
        """
        self._items = []


def bfs(graph, start_node):
    """
    Performs a breadth-first search on graph starting at the start_node.

    BFS explores nodes in order of their distance from the start node,
    visiting all nodes at distance d before any nodes at distance d+1.

    inputs:
        - graph: a Graph object with nodes() and get_neighbors() methods
        - start_node: a node in graph representing the start node

    Returns: a two-element tuple containing:
        - dist: dictionary mapping each node to its distance from start_node
                (infinity for unreachable nodes)
        - parent: dictionary mapping each node to its parent in the BFS tree
                  (None for start_node and unreachable nodes)
    """
    # Initialize distances and parents for all nodes
    dist = {}
    parent = {}

    for node in graph.nodes():
        dist[node] = float("inf")
        parent[node] = None

    # Distance to start node is 0
    dist[start_node] = 0

    # Initialize queue with start node
    queue = Queue()
    queue.push(start_node)

    # Process nodes in BFS order
    while len(queue) > 0:
        current = queue.pop()

        # Explore all neighbors of the current node
        for neighbor in graph.get_neighbors(current):
            # If neighbor hasn't been visited (distance is still infinity)
            if dist[neighbor] == float("inf"):
                # Update distance and parent
                dist[neighbor] = dist[current] + 1
                parent[neighbor] = current
                # Add neighbor to queue for further exploration
                queue.push(neighbor)

    return dist, parent


def distance_histogram(graph, node):
    """
    Computes the distance between the given node and all other
    nodes in that graph and creates a histogram of those distances.

    inputs:
        - graph: a graph object
        - node: a node in graph

    returns: a dictionary mapping each distance to the number of
    nodes that are that distance from node.

    Note: Only includes finite distances (excludes unreachable nodes).
    """
    # Get distances from BFS
    distances, _ = bfs(graph, node)

    # Build histogram
    histogram = {}
    for n in graph.nodes():
        d = distances[n]
        # Only count reachable nodes (finite distance)
        if d != float("inf"):
            if d in histogram:
                histogram[d] += 1
            else:
                histogram[d] = 1

    return histogram


def find_path(graph, start_person, end_person, parents):
    """
    Computes the path from start_person to end_person in the graph.

    The algorithm works by backtracking from end_person to start_person
    using the parent mapping, then reversing the path.

    inputs:
        - graph: a Graph object with get_attrs(node1, node2) method
        - start_person: a node in graph representing the starting node
        - end_person: a node in graph representing the ending node
        - parents: a dictionary mapping each node to its parent from BFS

    returns: a list of tuples of the path in the form:
        [(actor1, {movie1a, ...}), (actor2, {movie2a, ...}), ..., (actorn, set())]

    Each tuple contains:
        - The node (actor) name
        - The set of edge attributes (movies) connecting this node to the next
        - The last node has an empty set since there's no next node

    Returns an empty list if end_person is not reachable from start_person.
    """
    # Build path by backtracking from end to start
    path = []

    # Start with end_person (which has no outgoing edge in the path)
    path.append((end_person, set()))

    current = end_person

    # Backtrack until we reach start_person
    while current != start_person:
        previous = parents.get(current)

        # If no parent exists, there's no path
        if previous is None:
            return []

        # Get the edge attributes (movies) from previous to current
        edge_attrs = graph.get_attrs(previous, current)

        # Add (previous, edge_attrs) to path
        path.append((previous, edge_attrs))

        current = previous

    # Reverse the path so it goes from start to end
    path.reverse()

    return path


def play_kevin_bacon_game(graph, start_person, end_people):
    """
    Play the "Kevin Bacon Game" on the actors in the given graph.

    For each person in end_people, finds and prints the shortest path
    from start_person to that person using BFS.

    inputs:
        - graph: a Graph object with edges representing movie connections
        - start_person: a node in graph representing the starting actor
        - end_people: a list of nodes in graph to find paths to

    Prints the results using movies.print_path() for each target person.
    """
    # Perform BFS once from start_person
    _, parents = bfs(graph, start_person)

    # Find and print path to each end person
    for end_person in end_people:
        path = find_path(graph, start_person, end_person, parents)
        movies.print_path(path)


def run():
    """
    Load a graph and play the Kevin Bacon Game.
    """
    graph5000 = movies.load_graph('subgraph5000')

    if len(graph5000.nodes()) > 0:
        # Play the game with Kevin Bacon as the start
        play_kevin_bacon_game(graph5000, 'Kevin Bacon',
            ['Amy Adams', 'Andrew Garfield', 'Anne Hathaway', 'Barack Obama',
             'Benedict Cumberbatch', 'Chris Pine', 'Daniel Radcliffe',
             'Jennifer Aniston', 'Joseph Gordon-Levitt', 'Morgan Freeman',
             'Sandra Bullock', 'Tina Fey'])

        # Plot distance histograms
        for person in ['Kevin Bacon', 'Stephanie Fratus']:
            hist = distance_histogram(graph5000, person)
            simpleplot.plot_bars(person, 400, 300, 'Distance',
                'Frequency', [hist], ["distance frequency"])


# Uncomment the call to run below when you have completed your code.
# run()
