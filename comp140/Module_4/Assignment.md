# Module 4: Kevin Bacon Game

The goal of this assignment is to use the computational thinking process to solve a real world problem. We will follow the process of:
1. Reading and understanding the problem description.
2. Determining the inputs and outputs.
3. Decomposing the problem into subproblems, as appropriate.
4. Designing a computational recipe (algorithm) to solve the subproblems/problem.
5. Implementing your solution.

Be sure to read the entire assignment before beginning.

## Testing Your Solution

Use the `grade` tool to test your implementation:
```bash
bin/grade ./workspaces/<your_workspace>
```

Or if working in a workspace, simply use the grade tool provided by the agent harness.

---

## 1. The Problem

In this assignment, you will play the "Kevin Bacon Game". Also known as the "Six Degrees of Kevin Bacon", this game is based on the concept of six degrees of separation, which suggests that any two people in the world are six or fewer links apart. In six degrees of separation, links are based purely on acquaintanceship; however, six degrees of Kevin Bacon more specifically draws links between actors who have starred in movies together. An actor or actress' "Kevin Bacon" number, then, is the number of links between him or her and Kevin Bacon. You can visit [https://oracleofbacon.org/](https://oracleofbacon.org/) to play the game. Note, however, that there is no particular significance to the choice of Kevin Bacon here; the game can be played starting from any actor.

You will be provided with graphs representing a subset of the data from IMDb, where each node is an actor or actress and each edge is annotated with the set of movies that the two actors it connects appeared in together. You will implement an algorithm that will find the shortest path from a given start node to all other nodes in the graph. You will then perform this search on the provided graphs and use the output of this search to play the Kevin Bacon game between the start node and other nodes in the graph.

## 2. A Solution Strategy

First, make sure you understand the problem we are trying to solve. Once you do, we need to develop a solution strategy. We are going to use breadth-first search to solve this problem.

Given a graph and a starting node (actor), we will use breadth-first search as a means of finding the distance from that node to all other nodes in the graph. While performing the breadth-first search, we will keep track of the number of edges traversed from the start node to each other node. Additionally, we will keep a mapping of each node to the edge we traversed in order to arrive at to each node. We will then take the output of this search and backtrack along the edges to aggregate the full path from the start node to the end node. Finally, we will display the path from the starting actor to a collection of specified actors.

## 3. Breaking Down the Problem

You should start your implementation with the provided template (`solution.py` in your workspace). Your graphs should use the provided `Graph` class from the `lib` module.

### A. Queues

You should write a Queue class that implements all of the following methods:
- `__init__(self)` - Constructs a new empty queue.
- `__len__(self)` - Returns the number of items in the queue.
- `__str__(self)` - Returns a string representing the current state of the queue.
- `push(self, item)` - Pushes a new item into the queue.
- `pop(self)` - Removes and returns the oldest item in the queue.
- `clear(self)` - Removes all items from the queue.

Note that `__len__` and `__str__` are "magic" Python methods that get called automatically for you in certain situations. In particular, if you have a `Queue` object, called `queue`, they will be called if you write `len(queue)` and `str(queue)`, respectively.

Your `__str__` method can return whatever string representation you would like. This method is primarily to help you develop and debug your class, as it will enable you to easily see the contents of the queue as you work with it.

### B. Breadth First Search

#### i. Recipe

The BFS recipe is as follows:

**Breadth-First Search (BFS)**

Given a graph $G$ and a starting node $start$:

1. Initialize a queue $Q$ and add $start$ to $Q$
2. Initialize a dictionary $distance$ where $distance[start] = 0$
3. Initialize a dictionary $parent$ where $parent[start] = None$
4. While $Q$ is not empty:
   a. Remove a node $current$ from $Q$
   b. For each neighbor $n$ of $current$ in $G$:
      - If $n$ is not in $distance$:
        - Set $distance[n] = distance[current] + 1$
        - Set $parent[n] = current$
        - Add $n$ to $Q$
5. For any node $n$ not in $distance$, set $distance[n] = \infty$ and $parent[n] = None$
6. Return $distance$ and $parent$

#### ii. Code

Write a function, `bfs(graph, start_node)`, which implements the above recipe. This function should take a graph, `graph`, and a starting node, `start_node`, from which to perform the search. It will return two dictionaries: one which maps each node in the graph to its distance from the start node, and one which maps each node in the graph to its "parent" in the graph. For each node *N*, the distance is the integer number of "steps" (edges traversed) from the start node to *N*. The parent is the neighbor of *N* through which the search progressed on its path to finding *N*. For all nodes that cannot be reached from `start_node`, the distance should be infinity and the parent should be `None`. Note that to get infinity in Python you can use the expression `float("inf")`.

As an example, consider the following simple graph, `graph`:

```
A -- B -- C
```

If you run `bfs(graph, "A")`, the distance dictionary should be: `{"A": 0, "B": 1, "C": 2}`

And the parent dictionary should be: `{"A": None, "B": "A", "C": "B"}`

### C. Distance Histogram

In order to better visualize the results of bfs, write a function, `distance_histogram(graph, node)`, which takes as its input a graph and a node in that graph, computes the distance from `node` to all of the nodes in `graph`, and returns these distances in the form of a dictionary associating each distance with the number of nodes that are that many hops away from `node`.

## 4. Playing the Kevin Bacon Game

### A. Path-finding

#### i. Recipe

Clearly describe the recipe for finding the path from the start node to the end node in a graph. Your recipe should take four things: the graph, the start node, the end node, and a mapping of nodes to their parent in the graph (as if from a breadth-first search in the given graph from the given start node). Your recipe should produce a sequence of "steps" in the path from start node to end node. The *i*-th step in the list is represented as a tuple of the form (node *i*, attributes of the edge (node *i*, node *i*+1)). Note, therefore, that for the final step the set of edge attributes will be empty.

The path from node A to node C in the graph from section 3B would therefore look like:

$(A, \{1\}), (B, \{2\}), (C, \{\})$

You should describe this recipe in clear, concise English. You may refer to the input graph abstractly (as a mathematical structure), or as a Graph class instance; in the latter case, you may refer to any of the methods of the provided Graph class by name. Regardless, do not use any Python syntax! You can use bulleted lists for clarity, if you desire.

#### ii. Code

Write a function, `find_path(graph, start_person, end_person, parents)` which implements your recipe. `graph` should be a `Graph` instance and `start_person` and `end_person` should both be nodes in `graph`. `parents` should be a dictionary of the form output by `bfs(graph, start_person)`.

`find_path` should return a list of tuples. Each tuple should be a node in the path and the edge to follow to get to the next node in the path. For example, again considering the graph from section 3B, the path from node A to node C would look like: `[("A", {1}), ("B", {2}), ("C", set())]`

### B. Playing the Game

Write a function, `play_kevin_bacon_game(graph, start_person, end_people)`, that takes a graph, `graph`, an actor, `start_person`, in that graph, and a list, `end_people`, of actors in that graph to which to play the Kevin Bacon game starting from `start_person`. For each end person, this function should print the path from the start person to the end person. You should make use of the provided `movies.print_path(path)` function which takes as its input a path of the form output by `find_path`.

Do not write redundant code here. Think about the code that you have written up to this point and put it together to play the game.

**Note:** you should not add any additional formatting between the print of each path, as this will cause your results to be deemed incorrect.

## 5. Discussion (Written Response)

1. The diameter of a graph is defined as the maximum distance between any two nodes in the graph. Describe a strategy using what you have done in this assignment for computing the diameter of a graph.
2. In the Kevin Bacon Game, highly connected actors are "valuable". The most connected nodes - in this case actors - in a graph are called "hubs". Describe a strategy for computing the hubs in a graph.
3. Just as certain actors can be identified as hubs in a movie graph, and therefore be seen as important to the structure to the graph, certain movies can also be considered more important to the structure of the graph. What criteria would you use to classify movies as such?
4. Describe the differences between two actors with very different connectivity; what does this tell you about these two actors?

Answer these questions in clear, precise English. Do not write more than a few sentences for each question.

Write your response in `writeup.md`.

---
*COMP 140: Computational Thinking, Rice University*
