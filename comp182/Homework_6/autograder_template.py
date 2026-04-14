## Firstname Lastname
## NetID
## COMP 182 Homework 6 - Rooted Directed Minimum Spanning Trees

import provided


# =============================================================================
# Problem 2: RDMST Algorithm Functions
# =============================================================================

def reverse_digraph_representation(graph):
    """
    Convert a weighted digraph from standard to reversed representation
    (or vice versa).

    In the standard representation, graph[u][v] = w means there is an edge
    from u to v with weight w.

    In the reversed representation, graph[v][u] = w means there is an edge
    from u to v with weight w (i.e., keys map to incoming edges).

    Arguments:
    graph -- a weighted digraph in dictionary form

    Returns:
    A weighted digraph with the opposite representation.
    """
    return {}  # Your code here.


def modify_edge_weights(rgraph, root):
    """
    Modify edge weights according to Lemma 2 (Step 1 of RDMST algorithm).

    For each non-root node, subtract the minimum incoming edge weight
    from all incoming edge weights.

    Arguments:
    rgraph -- a weighted digraph in reversed representation
    root   -- the root node of the graph

    Returns:
    None (modifies rgraph in place)
    """
    pass  # Your code here.


def compute_rdst_candidate(rgraph, root):
    """
    Compute the RDST candidate by selecting one zero-weight incoming edge
    per non-root node (Step 2 of RDMST algorithm, based on Lemma 1).

    Arguments:
    rgraph -- a weighted digraph in reversed representation (after weight modification)
    root   -- the root node of the graph

    Returns:
    A weighted digraph in reversed representation containing the selected edges.
    """
    return {}  # Your code here.


def compute_cycle(rgraph):
    """
    Find a cycle in a graph where every node has in-degree at most 1.

    Arguments:
    rgraph -- a weighted digraph in reversed representation

    Returns:
    A tuple of nodes forming a cycle, or None if no cycle exists.
    """
    return None  # Your code here.


def contract_cycle(graph, cycle):
    """
    Contract a cycle into a single node (Step 4a of RDMST algorithm).

    The contracted node label is max(existing_nodes) + 1.
    Edges into/out of the cycle are consolidated: for multiple edges,
    keep the minimum weight.

    Arguments:
    graph -- a weighted digraph in standard representation
    cycle -- a tuple of nodes forming a cycle

    Returns:
    A tuple (graph, cstar) where graph is the contracted graph (modified
    in place) and cstar is the label of the new contracted node.
    """
    return ({}, 0)  # Your code here.


def expand_graph(rgraph, rdst_candidate, cycle, cstar):
    """
    Expand a contracted cycle back to the original nodes (Step 4c of RDMST algorithm).

    Arguments:
    rgraph         -- the original digraph (reversed representation) before contraction
    rdst_candidate -- the RDMST computed on the contracted graph (reversed representation)
    cycle          -- the cycle that was contracted (tuple of nodes)
    cstar          -- the node label used for the contracted cycle

    Returns:
    A weighted digraph (reversed representation) with the cycle expanded back.
    """
    return {}  # Your code here.


# =============================================================================
# Problem 3: Bacterial Infection Transmission
# =============================================================================

def compute_genetic_distance(seq1, seq2):
    """
    Compute the Hamming distance between two genetic sequences.

    The Hamming distance is the number of positions where the
    corresponding characters differ.

    Arguments:
    seq1 -- a DNA sequence encoded as a string of 0's and 1's
    seq2 -- a DNA sequence of the same length as seq1

    Returns:
    The number of positions where seq1 and seq2 differ (int).
    """
    return 0  # Your code here.


def construct_complete_weighted_digraph(gen_data, epi_data):
    """
    Build a complete weighted digraph combining genetic and epidemiological data.

    The weight of edge (A, B) is:
        D_AB = G_AB + (999 * (E_AB / max(E))) / 10^5

    where G_AB is genetic distance and E_AB is epidemiological distance.

    Arguments:
    gen_data -- filename for patient genetic sequence data
    epi_data -- filename for patient epidemiological trace data

    Returns:
    A complete weighted digraph in standard dictionary representation.
    """
    return {}  # Your code here.


def infer_transmap(gen_data, epi_data):
    """
    Infer the transmission map by computing the RDMST of the weighted digraph.

    Uses patient 1 as the root (Patient Zero).

    Arguments:
    gen_data -- filename for patient genetic sequence data
    epi_data -- filename for patient epidemiological trace data

    Returns:
    The RDMST (in standard representation) representing the most likely
    transmission map.
    """
    return {}  # Your code here.
