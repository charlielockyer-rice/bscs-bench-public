"""
Support code for the Kevin Bacon Game.
"""

import urllib.request
import codeskulptor
import json
import comp140_module4_graphs as graphs

LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

def build_line():
    """
    Build a graph that is 5 nodes in a line.
    """
    graph = graphs.Graph()
    for i in range(4):
        graph.add_edge(LETTERS[i], LETTERS[i+1], set([i]))
    return graph

def build_clique():
    """
    Build a complete graph with 5 nodes.
    """
    graph = graphs.Graph()
    edge_num = 0
    for i in range(5):
        graph.add_node(LETTERS[i])
        for j in range(i + 1, 5):
            graph.add_edge(LETTERS[i], LETTERS[j], set([edge_num]))
            edge_num += 1
    return graph

def get_grid_nbrs(idx, width, height):
    """
    Get the neighbors of node idx in a width x height grid.
    """
    nbrs = []
    if (idx % width) != (width - 1):
        nbrs.append(idx + 1)
    if idx < (width * (height - 1)):
        nbrs.append(idx + width)
    return nbrs

def build_grid():
    """
    Build a graph that is a 3 x 3 grid.
    """
    graph = graphs.Graph()
    edge_num = 0
    for i in range(9):
        graph.add_node(LETTERS[i])
        for nbr in get_grid_nbrs(i, 3, 3):
            graph.add_edge(LETTERS[i], LETTERS[nbr], set([edge_num]))
            edge_num += 1
    return graph

def build_tree():
    """
    Build a binary tree of height 3.
    """
    graph = graphs.Graph()
    node = 0
    graph.add_node(LETTERS[node])
    edge_num = 0
    for i in range(3): # height = 3
        for dummy in range(2 ** i):
            graph.add_node(LETTERS[node])
            for k in range(2): # binary tree
                graph.add_edge(LETTERS[node],
                               LETTERS[node + (node + 1) + k],
                               set([edge_num]))
                edge_num += 1
            node += 1
    return graph

def build_asymmetric1():
    """
    Build an asymmetric graph with 11 nodes.
    """
    graph = graphs.Graph()
    nodes = {0:[1], 1:[2, 7], 2:[3], 3:[7, 4],
             4:[5], 6:[7, 10], 7:[8], 8:[9, 10]}
    edge_num = 0
    for node, nbrs in list(nodes.items()):
        for nbr in nbrs:
            graph.add_edge(LETTERS[node], LETTERS[nbr], set([edge_num]))
            edge_num += 1
    return graph

def build_asymmetric2():
    """
    Build an asymetric graph with 10 nodes.
    """
    graph = graphs.Graph()
    nodes = {0:[1, 2, 3, 4], 1:[4], 5:[8], 6:[7], 7:[8], 8:[9]}
    edge_num = 0
    for node, nbrs in list(nodes.items()):
        for nbr in nbrs:
            graph.add_edge(LETTERS[node], LETTERS[nbr], set([edge_num]))
            edge_num += 1
    return graph

GRAPHS = {'line': build_line,
          'clique': build_clique,
          'grid': build_grid,
          'tree': build_tree,
          'asymmetric1': build_asymmetric1,
          'asymmetric2': build_asymmetric2}

def load_test_graph(name):
    """
    Given the name of a test graph, return it as a graphs.Graph object.
    """
    if not name in GRAPHS:
        raise ValueError("test graph name must be in " + str(list(GRAPHS.keys())))
    return GRAPHS[name]()

def load_graph(name):
    """
    Given the name of a graph, loads the graph from file and returns
    it as a graphs.Graph object.
    """
    graph = graphs.Graph()
    fullname = "comp140_module4_" + name + ".json"
    filepath = codeskulptor.file2url(fullname)

    # Handle both local files and URLs
    if filepath.startswith(('http://', 'https://')):
        gfile = urllib.request.urlopen(filepath)
        gobj = json.load(gfile)
    else:
        with open(filepath, 'r') as f:
            gobj = json.load(f)

    for fields in gobj:
        actor1 = fields[0]
        actor2 = fields[1]
        movies = set(fields[2])
        ## Add new edge to the graph
        graph.add_node(actor1)
        graph.add_node(actor2)
        graph.add_edge(actor1, actor2, movies)
    return graph

def load_actors(name):
    """
    Given the name of a graph, loads the actors that appear in that
    graph, and returns them as a list of strings.
    """
    # Support both legacy "subgraph5000" format and new "small"/"medium" format
    if name[:8] == "subgraph":
        fullname = "comp140_module4_actors" + name[8:] + ".txt"
    else:
        fullname = "comp140_module4_actors_" + name + ".txt"

    filepath = codeskulptor.file2url(fullname)

    # Handle both local files and URLs
    if filepath.startswith(('http://', 'https://')):
        gfile = urllib.request.urlopen(filepath)
        content = gfile.read().decode('utf-8')
    else:
        with open(filepath, 'r') as f:
            content = f.read()

    names = content.split('\n')
    return names[:-1] if names[-1] == '' else names

def print_path(path):
    """
    Receives a path in the form:
    [(actor1, set([movie1a, ...])),
     (actor2, set([movie2a, ...])),
     ...,
     (actorn, set([]))]
    And prints in the form:
    Path from actor1 to actorn:
    actor1
        movie1a
        ...
    actor2
        movie2a
        ...
    """
    if not path:
        print("No path found.\n")
        return
    print("Path from " + str(path[0][0]) + " to " + str(path[-1][0]) + ":")
    for node in path:
        parent = node[0]
        movies = node[1]
        print(parent)
        for movie in movies:
            print("\t" + str(movie))
    print()
