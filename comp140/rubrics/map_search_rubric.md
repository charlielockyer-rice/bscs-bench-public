---
total_points: 30
---

# Module 7: Map Search - Written Questions Rubric

## Recipe: BFS_DFS (5 points)

**Question:** "Modify the BFS recipe so that you can use the same algorithm to implement both BFS and DFS. Write and turn in within your writeup the complete BFS_DFS recipe."

**Expected Answer:**

A complete BFS_DFS recipe:

```
BFS_DFS(graph, RAC, start, end):
1. Create a RAC (restricted access container) and add start to the RAC
2. Initialize a dictionary parent where parent[start] = None
3. While the RAC is not empty:
   a. Remove a node current from the RAC
   b. If current equals end:
      - Return parent
   c. For each neighbor n of current in graph:
      - If n is not in parent:
        - Set parent[n] = current
        - Add n to the RAC
4. Return parent
```

Key modifications from original BFS:
- Takes RAC class as parameter instead of assuming queue
- Takes end node as parameter
- Returns immediately when end node is found
- Only returns parent (not distance)

**Rubric:**

- **Full credit (5 pts)**: Complete recipe that correctly incorporates all required modifications. Clear, logical steps that would work for both queue (BFS) and stack (DFS). Properly handles the early termination when end is found.

- **Partial credit (3-4 pts)**: Recipe is mostly correct but missing one modification or has minor errors. May handle the RAC correctly but not the early termination, or vice versa.

- **Minimal credit (1-2 pts)**: Shows understanding of the concept but recipe is incomplete or has significant errors. May miss multiple required modifications.

- **No credit (0 pts)**: No recipe or recipe doesn't correctly implement the search algorithm.

---

## Recipe: Recursive DFS Base and Recursive Cases (5 points)

**Question:** "Clearly identify the base case(s) and recursive case(s) of DFS. For each, describe the 'when' (under what conditions you are in this case) and the 'what' (what do you do when you are in this case)."

**Expected Answer:**

**Base Cases:**

1. **End node found**
   - When: current node equals the end node
   - What: Stop the search and return (the path has been found)

2. **No unvisited neighbors**
   - When: current node has no neighbors that haven't been visited (not in parent dict)
   - What: Return without further recursion (this path is a dead end)

**Recursive Case:**

- When: Current node is not the end AND has at least one unvisited neighbor
- What: For each unvisited neighbor:
  - Mark it as visited by setting parent[neighbor] = current
  - Recursively call DFS with the neighbor as the new start
  - If the recursive call found the end, stop searching

**Rubric:**

- **Full credit (5 pts)**: Correctly identifies both base cases and the recursive case with clear "when" and "what" descriptions. Shows understanding of when to stop recursion and when to continue.

- **Partial credit (3-4 pts)**: Identifies cases but description is incomplete or one case is missing. May correctly describe when to recurse but miss a base case.

- **Minimal credit (1-2 pts)**: Shows some understanding of recursion but cases are unclear or partially incorrect.

- **No credit (0 pts)**: No answer or fundamental misunderstanding of recursive structure.

---

## Recipe: Recursive DFS (5 points)

**Question:** "Clearly describe the recipe for taking a graph, a start node, an end node and a parent mapping and performing depth first search recursively."

**Expected Answer:**

```
DFS(graph, start, end, parent):
1. If start equals end:
   - Return (we have found the path)
2. For each neighbor n of start in graph:
   a. If n is not in parent:
      - Set parent[n] = start
      - Recursively call DFS(graph, n, end, parent)
      - If end is now in parent (path found):
        - Return (stop searching)
3. Return (no path found from this branch)
```

Alternative formulation that also correctly captures the algorithm:
- The parent dictionary is modified in place
- The function doesn't return a value but modifies parent as a side effect
- Early termination when end is found is important for efficiency

**Rubric:**

- **Full credit (5 pts)**: Clear, complete recipe that correctly describes recursive DFS. Properly handles the base case, recursive case, and parent tracking. Shows early termination when end is found.

- **Partial credit (3-4 pts)**: Recipe is mostly correct but missing minor details. May not clearly show early termination or have minor sequencing issues.

- **Minimal credit (1-2 pts)**: Shows attempt at recursive structure but recipe is incomplete or has errors that would prevent correct execution.

- **No credit (0 pts)**: No recipe or non-recursive algorithm presented.

---

## Recipe: A* Search (5 points)

**Question:** "Clearly describe the recipe for taking a graph, a start node, and an end node and performing A* search."

**Expected Answer:**

```
A*(graph, start, end, edge_distance, straight_line_distance):
1. Create a priority queue PQ
2. Add start to PQ with priority h(start) where h(n) = straight_line_distance(n, end, graph)
3. Initialize dictionary g where g[start] = 0
4. Initialize dictionary parent where parent[start] = None
5. While PQ is not empty:
   a. Remove the node current with lowest priority (f value) from PQ
   b. If current equals end:
      - Return parent
   c. For each neighbor n of current in graph:
      - Calculate tentative_g = g[current] + edge_distance(current, n, graph)
      - If n is not in g OR tentative_g < g[n]:
        - Set g[n] = tentative_g
        - Set parent[n] = current
        - Calculate f(n) = g[n] + h(n)
        - Add n to PQ with priority f(n)
6. Return parent (if end not found)
```

Key elements:
- Priority queue ordered by f = g + h
- g[n] tracks actual cost from start to n
- h[n] is the heuristic (estimated cost from n to end)
- Updates g and parent when better path found
- Early termination when end is found

**Rubric:**

- **Full credit (5 pts)**: Complete, clear recipe with all components of A*. Correctly describes priority queue usage, g and h calculations, and the update logic. No Python syntax.

- **Partial credit (3-4 pts)**: Recipe captures main ideas but missing some details. May not clearly explain the g+h priority or the update condition.

- **Minimal credit (1-2 pts)**: Shows some understanding of A* but recipe is incomplete or has significant errors.

- **No credit (0 pts)**: No recipe or doesn't describe A* algorithm.

---

## Question 1 (2.5 points)

**Question:** "Compare bfs_dfs with a Stack to recursive dfs. How are they the same? How are they different? Which one is better (and why)?"

**Expected Answer:**

**Similarities:**
- Both implement depth-first search
- Both visit nodes in the same order (deepest branch first)
- Both find a path if one exists
- Both use the concept of "last in, first out" for choosing next node

**Differences:**
- Stack-based uses explicit data structure; recursive uses call stack
- Recursive version is often more elegant/readable
- Stack-based avoids risk of stack overflow for deep graphs
- Stack-based may be easier to debug (explicit state)
- Recursive version has implicit backtracking

**Which is better:**
Arguments for stack-based:
- More robust for large/deep graphs (no stack overflow)
- More explicit control over memory usage
- Easier to convert to iterative for other purposes

Arguments for recursive:
- More natural representation of DFS concept
- Often shorter code
- Easier to understand for simple cases

**Rubric:**

- **Full credit (2.5 pts)**: Identifies meaningful similarities and differences. Makes a reasoned argument for which is "better" with justification.

- **Partial credit (1-2 pts)**: Identifies some similarities and differences but analysis is incomplete or "better" choice is unjustified.

- **Minimal credit (0.5-1 pt)**: Shows some understanding but response is vague or missing key comparisons.

- **No credit (0 pts)**: No answer or incorrect comparison.

---

## Question 2 (2.5 points)

**Question:** "Which search algorithm yields the best routes? Why do you think it does so?"

**Expected Answer:**

**A* yields the best routes** because:
1. It accounts for actual distances (not just hop count)
2. It finds the shortest path in terms of distance/cost
3. The heuristic guides search toward the goal efficiently
4. It explores promising paths first, avoiding unnecessary exploration
5. BFS finds shortest hop count, not distance; DFS finds any path, not shortest

**Rubric:**

- **Full credit (2.5 pts)**: Correctly identifies A* and provides clear reasoning about why it produces better routes than BFS/DFS.

- **Partial credit (1-2 pts)**: Identifies correct algorithm but explanation is incomplete.

- **Minimal credit (0.5-1 pt)**: Shows some understanding but answer is vague or partially incorrect.

- **No credit (0 pts)**: Incorrect answer or no justification.

---

## Question 3 (2.5 points)

**Question:** "Which search algorithm yields the worst routes? Why do you think it does so?"

**Expected Answer:**

**DFS yields the worst routes** because:
1. It doesn't consider distances at all
2. It finds the first path, not the best path
3. May go far out of the way before finding the end
4. Explores deep branches regardless of direction to goal
5. The path found depends heavily on the order neighbors are explored
6. Could explore the entire graph before finding a nearby end node

**Rubric:**

- **Full credit (2.5 pts)**: Correctly identifies DFS and provides clear reasoning about why it produces poor routes.

- **Partial credit (1-2 pts)**: Identifies correct algorithm but explanation is incomplete.

- **Minimal credit (0.5-1 pt)**: Shows some understanding but answer is vague or partially incorrect.

- **No credit (0 pts)**: Incorrect answer or no justification.

---

## Question 4 (2.5 points)

**Question:** "Why might a commercial mapping service yield better routes than the best algorithm you implemented?"

**Expected Answer:**

Commercial mapping services are better because:

1. **More data**: Real-time traffic, road closures, accidents, construction
2. **Better heuristics**: Account for turn restrictions, road types, speed limits
3. **Multiple criteria**: Optimize for time, not just distance
4. **Historical data**: Know typical travel times by time of day/week
5. **Dynamic routing**: Can reroute based on changing conditions
6. **Road attributes**: Highway vs. local road preferences, avoid tolls options
7. **Preprocessing**: Pre-compute common routes for faster queries
8. **Better graph representation**: More detailed road network with accurate distances

**Rubric:**

- **Full credit (2.5 pts)**: Identifies multiple valid reasons why commercial services are better. Shows understanding of real-world routing complexity.

- **Partial credit (1-2 pts)**: Identifies one or two valid reasons but analysis is limited.

- **Minimal credit (0.5-1 pt)**: Shows some understanding but answer is vague.

- **No credit (0 pts)**: No answer or invalid reasoning.
