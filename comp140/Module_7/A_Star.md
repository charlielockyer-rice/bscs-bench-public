# A 

The A* search algorithm is a "best first" search algorithm in which you try to explore the most promising nodes first. The "best" node is found by combining the actual cost to arrive at a node from the start node plus a heuristic estimate of the remaining distance to the end node.

Each node, $n$, therefore, has three costs associated with it:
1. The actual cost to get to node $n$ from the start node. This cost is often called $g$.
2. The heuristic estimate of the cost to get from node $n$ to the end node. This estimate must be a lower bound on the actual cost. This cost is often called $h$.
3. The sum of the actual cost to get from the start node to node $n$ plus the estimate to get from node $n$ to the end node. This cost is $g + h$ and is often called $f$.

A* starts by initializing the costs of the start node. For the start node, $g = 0$, obviously. The algorithm then determines $h$ for the start node and computes $f$ for it. Further, the algorithm initializes the parent of the start node to be $null$.

The algorithm then proceeds using the notion of an $openset$ and a $closedset$. The $openset$ is the set of nodes that are currently under consideration. It serves a similar role to the $queue$ or $stack$ in BFS and DFS. Nodes are removed from the $openset$, one by one, in order of increasing $f$ cost. This can be done efficiently if the $openset$ is stored as a priority queue, but this is beyond the scope of this class. You may use whatever technique you want to find the node in the $openset$ with the lowest $f$ cost. The $closedset$ is the set of nodes for which the shortest path to that node has already been found, so those nodes should not be searched again. The $closedset$ is initially empty. As nodes are removed from the $openset$, they should be placed into the $closedset$. You should convince yourself that you understand why.

Just as in BFS or DFS, once a node has been removed from the $openset$, you need to explore its neighbors. Of course, if the node is the end node, then you are done. If not the end node, then each of its neighbors should be considered in turn. There are three cases:
1. The neighbor is in the $closedset$. What should you do then?
2. The neighbor is in the $openset$. You should check the current $g$ cost of the neighbor and see if you have found a path with a lower $g$ cost. If so, you have found a shorter path to that node and you should update its $g$ cost, its $f$ cost (if you are storing it), and its parent.
3. The neighbor is in neither the $closedset$ nor the $openset$. This means that it has not yet been explored. What should you do then?

The algorithm continues until you find the end node or you search the entire graph without finding it.