---
total_points: 20
---

# Module 4: Kevin Bacon Game - Written Questions Rubric

## Question 1 (5 points)

**Question:** "The diameter of a graph is defined as the maximum distance between any two nodes in the graph. Describe a strategy using what you have done in this assignment for computing the diameter of a graph."

**Expected Answer:**

A complete strategy would be:

1. For each node in the graph, run BFS from that node to compute distances to all other nodes
2. For each BFS, record the maximum distance found
3. The diameter is the maximum of all these maximum distances

Alternative valid approaches:
- Run BFS from every node, collect all distances, find the overall maximum
- Recognize that you only need to consider nodes, not all pairs (since BFS from one node gives distances to all others)

Key insight: BFS computes shortest paths, so the maximum shortest path distance is the diameter.

**Rubric:**

- **Full credit (5 pts)**: Correctly describes running BFS from each node and taking the maximum of all maximum distances. Shows understanding that BFS finds shortest paths and the diameter is the longest shortest path.

- **Partial credit (3-4 pts)**: Describes using BFS but explanation is incomplete. May suggest running BFS from just one or a few nodes. May describe the general idea without specifying how to find the maximum.

- **Minimal credit (1-2 pts)**: Mentions BFS but doesn't explain how to use it for diameter. May confuse diameter with something else (like total edges).

- **No credit (0 pts)**: No answer or describes an approach that wouldn't work.

---

## Question 2 (5 points)

**Question:** "In the Kevin Bacon Game, highly connected actors are 'valuable'. The most connected nodes - in this case actors - in a graph are called 'hubs'. Describe a strategy for computing the hubs in a graph."

**Expected Answer:**

Possible strategies include:

1. **Degree-based (simplest)**: Count the number of edges (neighbors) for each node. Nodes with the highest degree are hubs.

2. **Closeness centrality**: Run BFS from each node, compute the average distance to all other nodes. Nodes with the lowest average distance (closest to everyone) are hubs.

3. **Betweenness centrality** (advanced): Count how often each node appears on shortest paths between other nodes. Nodes that appear on many shortest paths are hubs.

For this intro course, the degree-based approach or average distance approach using BFS would be most appropriate.

**Rubric:**

- **Full credit (5 pts)**: Describes a valid strategy such as counting edges/neighbors (degree) or using BFS to compute average distances. Explanation is clear and relates to what makes a node a "hub."

- **Partial credit (3-4 pts)**: Describes a reasonable approach but explanation is incomplete or slightly incorrect. May confuse hub identification with another concept.

- **Minimal credit (1-2 pts)**: Shows some understanding but strategy is vague or wouldn't correctly identify hubs. May mention "connections" without a clear method.

- **No credit (0 pts)**: No answer or completely incorrect approach.

---

## Question 3 (5 points)

**Question:** "Just as certain actors can be identified as hubs in a movie graph, and therefore be seen as important to the structure to the graph, certain movies can also be considered more important to the structure of the graph. What criteria would you use to classify movies as such?"

**Expected Answer:**

Important movies could be identified by criteria such as:

1. **Number of edges created**: Movies with large casts create many connections. A movie with 20 actors creates connections between all pairs of those actors.

2. **Bridge movies**: Movies that connect otherwise separate communities of actors (e.g., connecting foreign film actors to Hollywood actors). These are like "bridge" edges in graph theory.

3. **Unique connections**: Movies where actors appear together who don't share other movies. Without this movie, those actors would have longer paths.

4. **Impact on diameter**: If removing a movie significantly increases the graph's diameter or disconnects parts of the graph, it's structurally important.

**Rubric:**

- **Full credit (5 pts)**: Identifies one or more valid criteria with clear reasoning. Explains why those criteria relate to structural importance in the graph. Shows creative thinking about the graph structure.

- **Partial credit (3-4 pts)**: Identifies reasonable criteria but explanation is incomplete. May focus on obvious factors (cast size) without deeper analysis.

- **Minimal credit (1-2 pts)**: Provides some criteria but reasoning is weak or criteria don't relate to graph structure. May confuse movie importance with popularity.

- **No credit (0 pts)**: No answer or criteria that don't relate to graph structure.

---

## Question 4 (5 points)

**Question:** "Describe the differences between two actors with very different connectivity; what does this tell you about these two actors?"

**Expected Answer:**

A highly connected actor vs. a poorly connected actor:

**Highly Connected Actor:**
- Has appeared in many movies
- Has worked with many different actors
- Has low Kevin Bacon numbers (close to everyone)
- Likely has a long career or appears in ensemble films
- Acts as a hub in the graph, connecting different communities

**Poorly Connected Actor:**
- Has appeared in few movies
- Has worked with few other actors
- Has high Kevin Bacon numbers (far from most actors)
- May have a short career or work in niche/independent films
- May be isolated in a small cluster of the graph

**What this tells us:**
- Career longevity and breadth
- Versatility (working across different types of films/genres)
- Centrality in the film industry network
- The "small world" effect depends on highly connected hubs

**Rubric:**

- **Full credit (5 pts)**: Clearly contrasts highly vs. poorly connected actors with specific characteristics. Explains what connectivity reveals about career, industry position, or network structure.

- **Partial credit (3-4 pts)**: Makes valid contrasts but explanation is incomplete. May describe differences without explaining what they reveal about the actors.

- **Minimal credit (1-2 pts)**: Shows some understanding but description is vague or only partially correct. May focus on trivial differences.

- **No credit (0 pts)**: No answer or doesn't address the question about connectivity differences.
