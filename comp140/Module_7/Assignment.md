# Module 7: Street Map Search

The goal of this assignment is to explore various alternative mechanisms for solving the same problem. This will allow us to see some of the pros and cons of using different types of solution strategies on a problem.

Be sure to read the entire assignment before beginning.

## Testing Your Solution

Use the `grade` tool to test your implementation:
```bash
bin/grade ./workspaces/<your_workspace>
```

Or if working in a workspace, simply use the grade tool provided by the agent harness.

---

## 1. The Problem

In this assignment, you will search a street map to find a route from a start point to an end point.

We will explore three different search algorithms:
1. Breadth-First Search
2. Depth-First Search
3. A* Search

These algorithms have different objectives and will yield different types of routes.

## 2. Breaking Down the Problem

You should start your implementation with the provided template (`solution.py` in your workspace).

You should write a `Queue` class and a `Stack` class that each implement all of the following methods:
- `__init__(self)` - Constructs a new empty queue/stack.
- `__len__(self)` - Returns the number of items in the queue/stack.
- `__str__(self)` - Returns a string representing the current state of the queue/stack.
- `push(self, item)` - Pushes a new item into the queue/stack.
- `pop(self)` - Removes and returns the appropriate item from the queue/stack.
- `clear(self)` - Removes all items from the queue/stack.

Note that `__len__` and `__str__` are "magic" Python methods that get called automatically for you in certain situations. In particular, if you have a `Queue` object, called `queue`, they will be called if you write `len(queue)` and `str(queue)`, respectively.

Your `__str__` method can return whatever string representation you would like. This method is primarily to help you develop and debug your class, as it will enable you to easily see the contents of the queue as you work with it.

Do **not** rewrite the `Queue` class. You have already done so! You may start with your own `Queue` class from Module 4 or implement a new one.

**Note:** In a statically typed language with more support for object-oriented programming (such as Java), you would use *inheritance* to make these two classes *subclasses* of a common base class. The base class, a restricted access container (RAC), would contain the common elements of all restricted access container types. The subclasses would then contain the unique elements of that particular type of restricted access container. While Python does support inheritance, it does not enforce that you use a particular type, so there is little additional value in this particular case.

## 3. Solving the Problem

### A. Modify BFS

First, modify the BFS algorithm to implement both BFS and DFS.

#### i. Recipe

Modify the BFS recipe so that you can use the same algorithm to implement both BFS and DFS. You will need to make the following modifications:
1. Change the name to $BFS\_DFS$.
2. Take a $RAC$ as a parameter instead of a $queue$. If the $RAC$ is a $queue$, the algorithm should implement BFS. If it is a $stack$, the algorithm should implement DFS.
3. Take as an additional argument the $end$ node. When the search reaches that node, it should stop and return immediately, as there is no need to further search the graph.
4. Return only the $parent$ mapping. Internally, you can still keep the distance mapping, but it has no real meaning for DFS, so should not be returned.

Write and turn in within your writeup the complete $BFS\_DFS$ recipe. Your final recipe should be of the same quality as the original recipe for $BFS$ that we have given you.

#### ii. Code

The graphs in this assignment are each represented as an instance of the `DiGraph` class. A `DiGraph` object is just a *directed* version of the Graph class you used in Module 4. The `DiGraph` class is defined in the `lib` module. You need a directed graph here, since there are one way streets, whereas the movie graph edges did not have a direction.

Implement the function `bfs_dfs(graph, rac_class, start_node, end_node)`. You may begin either with your implementation of `bfs` from Module 4 or write a new implementation. The `graph` parameter should be a `DiGraph` object that represents the street graph. The `rac_class` parameter should be a restricted access container (RAC) class (`Queue` or `Stack`). The `start_node` and `end_node` parameters should be nodes in `graph`. Your `bfs_dfs` function should return a dictionary mapping nodes in `graph` to their parents.

### B. Implement Recursive DFS

Next, implement a recursive version of DFS.

#### i. Base Cases and Recursive Cases

Clearly identify the base case(s) and recursive case(s) of $DFS$. For each, describe the "when" (under what conditions are you are in this case) and the "what" (what do you do when you are in this case). These need to be turned in as part of your writeup.

#### ii. Recipe

Clearly describe the recipe for taking a $graph$, a $start$ node, an $end$ node and a $parent$ mapping and performing depth first search recursively.

You should describe this recipe clearly and concisely.

#### iii. Code

Implement the function `dfs(graph, start_node, end_node, parent)`. The `graph` parameter should be a `DiGraph` object that represents the street graph. The `start_node` and `end_node` parameters should be nodes in `graph`. The `parent` parameter should be a dictionary mapping each node in `graph` that has already been explored to its parent. When called initially, `parent` will map `start_node` to `None`. The `dfs` function should modify `parent` appropriately as it runs. Further, `dfs` must be recursive. Each function invocation can look only at the neighbors of a single node in `graph`.

### C. Implement A*

BFS and DFS do not account for actual distances. A* not only accounts for distances, but also targets the search by exploring promising paths first.

#### A* Search Algorithm

A* is an informed search algorithm that finds the shortest path between a start node and an end node. It uses a heuristic function to guide the search towards the goal.

The algorithm maintains:
- A priority queue of nodes to explore, ordered by $f(n) = g(n) + h(n)$
- $g(n)$ = the actual cost from start to node $n$
- $h(n)$ = the heuristic estimate from $n$ to the goal (in our case, straight-line distance)

The algorithm:
1. Initialize the priority queue with the start node (priority = 0 + h(start))
2. Initialize $g[start] = 0$ and $parent[start] = None$
3. While the priority queue is not empty:
   a. Remove the node $current$ with the lowest $f$ value
   b. If $current$ is the end node, return the parent mapping
   c. For each neighbor $n$ of $current$:
      - Calculate $tentative\_g = g[current] + edge\_distance(current, n)$
      - If $n$ not in $g$ or $tentative\_g < g[n]$:
        - Update $g[n] = tentative\_g$
        - Update $parent[n] = current$
        - Add $n$ to priority queue with priority $f(n) = g[n] + h(n)$
4. Return the parent mapping

#### i. Recipe

Clearly describe the recipe for taking a $graph$, a $start$ node, and an $end$ node and performing A* search. A* should return a mapping of nodes to their parents. You may assume that $start$ and $end$ are nodes in $graph$.

This is a complex recipe. You are expected to give a clear and precise recipe that will be held to higher quality standards than in past assignments. You should not simply be repeating the English that was given to you above. That gives a conceptual explanation of the algorithm, but it does not give a clear sequence of steps. Further, there should be **no** Python syntax in your recipe.

#### ii. Code

Implement the function `astar(graph, start_node, end_node, edge_distance, straight_line_distance)`. The `graph` parameter should be a `DiGraph` object that represents the street graph. The `start_node` and `end_node` parameters should be nodes in `graph`. The `edge_distance` and `straight_line_distance` parameters are the distance functions you should use. Each of these distance functions takes three parameters: `node1`, `node2`, and `graph`. The `edge_distance` function returns the actual distance required to travel from `node1` to `node2` in `graph`, if the two nodes are neighbors. The `straight_line_distance` function returns the heuristic distance from `node1` to `node2`, where `node2` does not need to be a neighbor of `node1` in `graph`; if it is, this function returns the straight line distance between them. The `astar` function should return a dictionary mapping nodes in `graph` to their parents. Only the parents along the path from `start_node` to `end_node` need to reflect the best path, but all nodes that have been explored in the search should have a parent, and nodes that have not been explored either should not be in the dictionary or should have `None` as a parent.

In the provided code (from `lib`), there are two sets of distance functions: `map_edge_distance` and `map_straight_line_distance`, which will be used when searching the map, and `test_edge_distance` and `test_straight_line_distance`, which will be used when testing your code. The two map searching distance functions return actual distances in meters. The two testing distance functions return unitless distances that are relevant to the test graphs.

## 4. Discussion (Written Response)

When you run your search algorithms on the map, you should see which edges were explored (colored in orange) and which edges comprise the final path (colored in green). Be sure to run multiple searches with different start and end nodes to get an understanding of how each algorithm behaves.

1. Compare `bfs_dfs` with a `Stack` to recursive `dfs`. How are they the same? How are they different? Which one is better (and why)?
2. Which search algorithm yields the best routes? Why do you think it does so?
3. Which search algorithm yields the worst routes? Why do you think it does so?
4. Why might a commercial mapping service yield better routes than the best algorithm you implemented?

Answer these questions in clear, precise English. Do not write more than a few sentences for each question.

Write your response in `writeup.md`.

---
*COMP 140: Computational Thinking, Rice University*
