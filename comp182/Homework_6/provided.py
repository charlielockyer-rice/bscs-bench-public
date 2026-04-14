"""
Provided helper functions for COMP 182 Homework 6.

This module provides:
- compute_rdmst: The main RDMST algorithm (calls student-implemented helper functions)
- read_patient_sequences: Read genetic sequence data from file
- read_patient_traces: Read epidemiological trace data from file
- compute_pairwise_gen_distances: Compute all-pairs genetic distances
"""

import copy


def compute_rdmst(graph, root):
    """
    Compute a Rooted Directed Minimum Spanning Tree (RDMST) using the
    iterative cycle-contraction algorithm.

    This function calls the student's implementations of:
    - reverse_digraph_representation
    - modify_edge_weights
    - compute_rdst_candidate
    - compute_cycle
    - contract_cycle
    - expand_graph

    Arguments:
    graph -- a weighted digraph in standard dictionary representation
    root  -- the root node

    Returns:
    An RDMST of graph rooted at root, in reversed representation.
    """
    import autograder

    # Step 1: Reverse the graph and modify edge weights
    rgraph = autograder.reverse_digraph_representation(graph)
    rgraph_copy = copy.deepcopy(rgraph)
    autograder.modify_edge_weights(rgraph_copy, root)

    # Step 2: Compute RDST candidate
    rdst_candidate = autograder.compute_rdst_candidate(rgraph_copy, root)

    # Step 3: Check for cycles
    cycle = autograder.compute_cycle(rdst_candidate)

    if cycle is None:
        # No cycle -- convert candidate back to use original weights
        rdmst = {}
        for node in rdst_candidate:
            rdmst[node] = {}
            for nbr in rdst_candidate[node]:
                rdmst[node][nbr] = rgraph[node][nbr]
        return rdmst

    # Step 4a: Contract the cycle
    graph_copy = copy.deepcopy(graph)
    contracted_graph, cstar = autograder.contract_cycle(graph_copy, cycle)

    # Step 4b: Recursively compute RDMST on contracted graph
    rdmst_contracted = compute_rdmst(contracted_graph, root)

    # Step 4c: Expand the contracted cycle
    rdmst = autograder.expand_graph(rgraph, rdmst_contracted, cycle, cstar)

    return rdmst


def read_patient_sequences(filename):
    """
    Read patient genetic sequences from a file.

    File format: one line per patient, tab-separated:
        patient_id<TAB>sequence_string

    Arguments:
    filename -- path to the patient sequences file

    Returns:
    A dictionary mapping patient_id (int) to sequence (str).
    """
    sequences = {}
    with open(filename, 'r') as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            parts = line.split('\t')
            patient_id = int(parts[0])
            sequence = parts[1]
            sequences[patient_id] = sequence
    return sequences


def read_patient_traces(filename):
    """
    Read epidemiological trace data from a file.

    File format: one line per pair, tab-separated:
        patient_i<TAB>patient_j<TAB>distance

    Arguments:
    filename -- path to the patient traces file

    Returns:
    A dictionary of dictionaries: result[i][j] = distance (float)
    representing directed epidemiological distances.
    """
    traces = {}
    with open(filename, 'r') as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            parts = line.split('\t')
            patient_i = int(parts[0])
            patient_j = int(parts[1])
            distance = float(parts[2])
            if patient_i not in traces:
                traces[patient_i] = {}
            traces[patient_i][patient_j] = distance
    return traces


def compute_pairwise_gen_distances(sequences, dist_func):
    """
    Compute all pairwise genetic distances between patient sequences.

    Arguments:
    sequences -- dictionary mapping patient_id to sequence string
    dist_func -- function that takes two sequences and returns a distance

    Returns:
    A dictionary of dictionaries: result[i][j] = genetic_distance
    for all pairs i != j.
    """
    patients = sorted(sequences.keys())
    distances = {}
    for patient_i in patients:
        distances[patient_i] = {}
        for patient_j in patients:
            if patient_i != patient_j:
                distances[patient_i][patient_j] = dist_func(
                    sequences[patient_i], sequences[patient_j]
                )
    return distances
