# Graph Class API

Following is the API for the`Graph`class that we will be using for module 4. This class represents an undirected graph. There can be at most one edge between any pair of nodes, and each edge can have a set of attributes representing the relationship between the two nodes that it connects.

- 

`__init__(self)`

Constructs a new empty graph.

- 

`nodes(self)`

Returns a list of all nodes in the graph.

- 

`get_neighbors(self, node)`

Given a particular node, returns a list of all neighbors in the graph. Assumes that node is in the graph and causes a`KeyError`exception if it is not.

- 

`add_node(self, node)`

Adds the given node to the graph.

- 

`add_edge(self, node1, node2, attrs)`

Adds an edge between the given pair of nodes, adding the nodes themselves first if they are not already in the graph.`attrs`is a set of attributes that will be added to any existing attributes of the edge.

- 

`get_attrs(self, node1, node2)`

Given an edge in the form of a pair of nodes (`node1`,`node2`), returns the set of all attributes of that edge. Assumes that (`node1`,`node2`) is a valid edge in the graph and causes a`KeyError`exception if it is not.

- 

`copy(self)`

Returns an identical (deep) copy of the graph.

Additionally, a Graph object can be printed in order to visualize its structure. As an example, a simple graph with three nodes, "A", "B", and "C", such that an edge with a single attribute, 1, connects "A" and "B" and an edge with a single attribute, 2, connects "B" and "C", would display like this when printed:

```
[node]
======
A
                [neighbor]              [attrs]
                ==========              =======
                B                       set([1])
B
                [neighbor]              [attrs]
                ==========              =======
                A                       set([1])
                C                       set([2])
C
                [neighbor]              [attrs]
                ==========              =======
                B                       set([2])

```