"""
Directed Graph implementation for Module 7 (Map Search).
This is a DiGraph class with support for node and edge attributes.
"""

class DiGraph:
    """
    A directed graph with support for node and edge attributes.
    """

    def __init__(self):
        """
        Create a new empty directed graph.
        """
        self._nodes = {}  # node -> {attr_name: attr_value}
        self._edges = {}  # (from_node, to_node) -> {attr_name: attr_value}
        self._neighbors = {}  # node -> set of neighbor nodes

    def __str__(self):
        """
        Return a string representation of the graph.
        """
        result = "DiGraph with " + str(len(self._nodes)) + " nodes"
        result += " and " + str(len(self._edges)) + " edges"
        return result

    def nodes(self):
        """
        Return a list of all nodes in the graph.
        """
        return list(self._nodes.keys())

    def has_node(self, node):
        """
        Return True if node is in the graph, False otherwise.
        """
        return node in self._nodes

    def add_node(self, node):
        """
        Add a node to the graph.
        """
        if node not in self._nodes:
            self._nodes[node] = {}
            self._neighbors[node] = set()

    def add_node_attr(self, node, attr_name, attr_value):
        """
        Add an attribute to a node.
        """
        if node not in self._nodes:
            self.add_node(node)
        self._nodes[node][attr_name] = attr_value

    def has_node_attr(self, node, attr_name):
        """
        Return True if the node has the given attribute.
        """
        if node not in self._nodes:
            return False
        return attr_name in self._nodes[node]

    def get_node_attr(self, node, attr_name):
        """
        Get an attribute of a node.
        """
        if node not in self._nodes:
            raise ValueError("Node " + str(node) + " not in graph")
        if attr_name not in self._nodes[node]:
            raise ValueError("Node " + str(node) + " does not have attribute " + str(attr_name))
        return self._nodes[node][attr_name]

    def has_edge(self, from_node, to_node):
        """
        Return True if there is an edge from from_node to to_node.
        """
        return (from_node, to_node) in self._edges

    def add_edge(self, from_node, to_node):
        """
        Add a directed edge from from_node to to_node.
        """
        if from_node not in self._nodes:
            self.add_node(from_node)
        if to_node not in self._nodes:
            self.add_node(to_node)
        if (from_node, to_node) not in self._edges:
            self._edges[(from_node, to_node)] = {}
            self._neighbors[from_node].add(to_node)

    def add_edge_attr(self, from_node, to_node, attr_name, attr_value):
        """
        Add an attribute to an edge.
        """
        if (from_node, to_node) not in self._edges:
            self.add_edge(from_node, to_node)
        self._edges[(from_node, to_node)][attr_name] = attr_value

    def get_edge_attr(self, from_node, to_node, attr_name):
        """
        Get an attribute of an edge.
        """
        if (from_node, to_node) not in self._edges:
            raise ValueError("No edge from " + str(from_node) + " to " + str(to_node))
        if attr_name not in self._edges[(from_node, to_node)]:
            raise ValueError("Edge does not have attribute " + str(attr_name))
        return self._edges[(from_node, to_node)][attr_name]

    def get_neighbors(self, node):
        """
        Return a list of all neighbors of node (nodes that node has edges to).
        """
        if node not in self._neighbors:
            return []
        return list(self._neighbors[node])

    def copy(self):
        """
        Return a deep copy of the graph.
        """
        new_graph = DiGraph()
        for node in self._nodes:
            new_graph._nodes[node] = dict(self._nodes[node])
            new_graph._neighbors[node] = set(self._neighbors[node])
        for edge in self._edges:
            new_graph._edges[edge] = dict(self._edges[edge])
        return new_graph
