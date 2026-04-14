"""
Module 7: Map Search - Reference Solution

This module implements graph search algorithms for pathfinding on a street map:
- Queue and Stack classes (restricted access containers)
- BFS/DFS using a generic container approach
- Recursive DFS
- A* search with heuristic

Author: Reference Implementation
"""


class Queue:
    """
    A simple implementation of a FIFO (First-In, First-Out) queue.
    """

    def __init__(self):
        """
        Construct a new empty queue.
        """
        self._items = []

    def __len__(self):
        """
        Return the number of items in the queue.
        """
        return len(self._items)

    def __str__(self):
        """
        Return a string representation of the queue.
        """
        return "Queue(" + str(self._items) + ")"

    def push(self, item):
        """
        Push a new item onto the back of the queue.
        """
        self._items.append(item)

    def pop(self):
        """
        Remove and return the item at the front of the queue.
        """
        return self._items.pop(0)

    def clear(self):
        """
        Remove all items from the queue.
        """
        self._items = []


class Stack:
    """
    A simple implementation of a LIFO (Last-In, First-Out) stack.
    """

    def __init__(self):
        """
        Construct a new empty stack.
        """
        self._items = []

    def __len__(self):
        """
        Return the number of items in the stack.
        """
        return len(self._items)

    def __str__(self):
        """
        Return a string representation of the stack.
        """
        return "Stack(" + str(self._items) + ")"

    def push(self, item):
        """
        Push a new item onto the top of the stack.
        """
        self._items.append(item)

    def pop(self):
        """
        Remove and return the item at the top of the stack.
        """
        return self._items.pop()

    def clear(self):
        """
        Remove all items from the stack.
        """
        self._items = []


def bfs_dfs(graph, rac_class, start_node, end_node):
    """
    Perform a breadth-first search or depth-first search on graph
    starting at start_node. The rac_class determines which search:
    - Queue class -> BFS (explores level by level)
    - Stack class -> DFS (explores depth first)

    Completes when end_node is found or entire graph has been searched.

    Args:
        graph: A directed Graph object representing a street map
        rac_class: A restricted access container class (Queue or Stack)
        start_node: A node in graph representing the start
        end_node: A node in graph representing the end

    Returns:
        A dictionary mapping each visited node to its parent node.
        The start_node maps to None.
    """
    # Initialize the restricted access container
    rac = rac_class()

    # Initialize parent dictionary - only start_node is known initially
    parent = {start_node: None}

    # Push the start node onto the container
    rac.push(start_node)

    # Process nodes until container is empty or goal is found
    while len(rac) > 0:
        # Remove the next node to process
        node = rac.pop()

        # Explore all neighbors of the current node
        for neighbor in graph.get_neighbors(node):
            # Only visit unvisited nodes (not in parent dictionary)
            if neighbor not in parent:
                # Record this node's parent
                parent[neighbor] = node

                # Push neighbor onto the container
                rac.push(neighbor)

                # Early termination: if we found the end, return immediately
                if neighbor == end_node:
                    return parent

    return parent


def dfs(graph, start_node, end_node, parent):
    """
    Perform a recursive depth-first search on graph starting at start_node.

    Completes when end_node is found or entire graph has been searched.

    Args:
        graph: A directed Graph object representing a street map
        start_node: A node in graph representing the current position
        end_node: A node in graph representing the goal
        parent: A dictionary that initially has one entry mapping the
                original start_node to None. This function modifies
                this dictionary to record parent relationships.

    Returns:
        True if end_node was found, False otherwise.
        Also modifies the parent dictionary in place.
    """
    # Base case: if we've reached the end node, we're done
    if start_node == end_node:
        return True

    # Recursive case: explore all unvisited neighbors
    for neighbor in graph.get_neighbors(start_node):
        # Only visit nodes we haven't seen before
        if neighbor not in parent:
            # Record the parent relationship
            parent[neighbor] = start_node

            # Recursively search from this neighbor
            if dfs(graph, neighbor, end_node, parent):
                # If we found the end, propagate success upward
                return True

    # If we explored all neighbors without finding the end, return False
    return False


def astar(graph, start_node, end_node, edge_distance, straight_line_distance):
    """
    Perform an A* search on graph starting at start_node.

    A* is a best-first search that uses:
    - g(n): actual cost from start to node n
    - h(n): heuristic estimate of cost from n to end (must be admissible)
    - f(n) = g(n) + h(n): estimated total cost through n

    Completes when end_node is found or entire graph has been searched.

    Args:
        graph: A directed Graph object representing a street map
        start_node: A node in graph representing the start
        end_node: A node in graph representing the end
        edge_distance: A function(node1, node2, graph) that returns the
                      actual distance between neighboring nodes
        straight_line_distance: A function(node1, node2, graph) that returns
                               the straight-line heuristic distance

    Returns:
        A dictionary mapping each visited node to its parent node.
        The path from start_node to end_node can be reconstructed by
        following parent pointers from end_node back to start_node.
    """
    # Initialize parent dictionary
    parent = {start_node: None}

    # Initialize cost dictionaries
    # g_cost[node] = actual cost from start to node
    g_cost = {start_node: 0}

    # h_cost[node] = heuristic estimate from node to end
    h_cost = {start_node: straight_line_distance(start_node, end_node, graph)}

    # f_cost[node] = g_cost + h_cost (estimated total cost through node)
    f_cost = {start_node: g_cost[start_node] + h_cost[start_node]}

    # openset: nodes to be explored (frontier)
    openset = {start_node}

    # closedset: nodes already fully explored
    closedset = set()

    # Main loop: process nodes until openset is empty
    while len(openset) > 0:
        # Find the node in openset with lowest f_cost
        current_node = None
        lowest_f = float('inf')
        for node in openset:
            if f_cost[node] < lowest_f:
                current_node = node
                lowest_f = f_cost[node]

        # If we've reached the end node, we're done
        if current_node == end_node:
            return parent

        # Move current node from openset to closedset
        openset.remove(current_node)
        closedset.add(current_node)

        # Explore all neighbors of current node
        for neighbor in graph.get_neighbors(current_node):
            # Skip nodes already in closedset (already fully explored)
            if neighbor in closedset:
                continue

            # Calculate the tentative g_cost through current_node
            tentative_g = g_cost[current_node] + edge_distance(current_node, neighbor, graph)

            if neighbor in openset:
                # Neighbor is already in openset - check if this path is better
                if tentative_g < g_cost[neighbor]:
                    # Found a better path - update costs and parent
                    g_cost[neighbor] = tentative_g
                    f_cost[neighbor] = g_cost[neighbor] + h_cost[neighbor]
                    parent[neighbor] = current_node
            else:
                # Neighbor is not yet discovered - add to openset
                g_cost[neighbor] = tentative_g
                h_cost[neighbor] = straight_line_distance(neighbor, end_node, graph)
                f_cost[neighbor] = g_cost[neighbor] + h_cost[neighbor]
                parent[neighbor] = current_node
                openset.add(neighbor)

    # If we get here, we searched everything without finding end_node
    return parent
