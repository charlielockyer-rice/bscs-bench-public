# COMP 382 Homework 2 - Solutions

---

## Problem 1: Persistent Dynamic Sets [25 points]

### Part (a)
We need to make a new version of every node that is an ancestor of the node that is inserted or deleted. This is because we must preserve the original tree while creating the new version, and modifying any node requires creating a copy (to avoid changing the old version).

### Part (b)

```
PERSISTENT-TREE-INSERT(T, z)
    x = T.root
    if x == NIL then
        T.root = new node(key = z.key)
        return

    // Create new root for the new version
    new_root = COPY-NODE(x)
    current = new_root

    while x != NIL do
        y = current
        if z.key < x.key then
            x = x.left
            if x != NIL then
                y.left = COPY-NODE(x)
                current = y.left
        else
            x = x.right
            if x != NIL then
                y.right = COPY-NODE(x)
                current = y.right

    new_node = new node(key = z.key)
    if z.key < y.key then
        y.left = new_node
    else
        y.right = new_node

    return new_root
```

### Part (c)
**Time complexity:** O(h) - We traverse from root to leaf, doing O(1) work at each level.

**Space complexity:** O(h) - We create one new node for each level traversed, plus the new node being inserted.

### Part (d)
**Proof that parent pointers require Ω(n) time and space:**

When we insert an element, we need to make a new version of the root. Any node that points to the root (via parent pointer) must have a new copy made so that it points to the new root. Therefore, all nodes at depth 1 must be copied.

Similarly, all nodes that point to depth-1 nodes must have new copies made. So all nodes at depth 2 must be copied.

By induction, all nodes must be copied. Therefore, we need Ω(n) time and space.

### Part (e)
**Using Red-Black Trees for O(log n) persistent insertion:**

Since rebalancing operations can only change ancestors and children of ancestors, we only need to allocate at most O(h) = O(log n) new nodes for each insertion.

Key insight: We don't keep track of parent pointers. Instead, we:
1. Perform a search for the insertion point, storing the O(h) ancestors on a stack
2. Since these are the only nodes under consideration during insertion and rebalancing, we can know their parents from the stack
3. Since the height stays O(log n), everything can be done in O(log n) time and space

---

## Problem 2: Join Operation on Red-Black Trees [35 points]

### Part (a)
When we call RB-INSERT or RB-DELETE, we modify the black-height of the tree by at most 1. We can track this modification based on which case we're in, so no additional storage is required in the nodes.

When descending through T to determine the black-height of each node visited:
1. First determine the black-height of the root in O(log n) time by traversing any path
2. As we move down the tree, decrement the height by 1 for each black node we see
3. This can be done in O(1) time per node visited

### Part (b)
**Algorithm to find node y:**

```
y = T1.root
black_count = 0
target = T1.bh - T2.bh

while black_count < target do
    if y is black then
        black_count = black_count + 1
    if black_count < target then
        if y.right != NIL then
            y = y.right
        else
            y = y.left

return y
```

**Time complexity:** O(log n) since the height of T₁ bounds the number of iterations.

### Part (c)
Let T denote the desired tree:
- Set T.root = x
- Set x.left = T_y (subtree rooted at y)
- Set y.p = x
- Set x.right = T₂.root
- Set T₂.root.p = x

This satisfies the BST property because:
- All elements in T_y are in T₁, which contains only elements smaller than x
- All elements in T₂ are larger than x
- T_y and T₂ each already satisfy the BST property

**Time complexity:** O(1)

### Part (d)
**Color x red** to satisfy red-black properties 1, 3, and 5:
- Property 1 (nodes are red or black): x is now colored
- Property 3 (leaves are black): Unchanged, as x is internal
- Property 5 (black-height): Since y had the same black-height as T₂, coloring x red preserves the black-height on all paths

**Enforcing Properties 2 and 4:**
- If x is the root of T, color it black (satisfies property 2)
- If x.parent is red, we violate property 4. Fix using RB-INSERT-FIXUP, which performs tree rotations iterating up from x to the root

**Time complexity:** O(log n)

### Part (e)
If T₁.bh ≤ T₂.bh, we perform the symmetric operation:
- Find a black node y in T₂ with the smallest key from among nodes whose black-height is T₁.bh
- Traverse the left spine of T₂ instead of the right spine of T₁
- Form T = T₁ ∪ {x} ∪ T_y and insert into T₂

### Part (f)
**Total running time of RB-JOIN:** O(log n)
- Finding black-heights: O(log n)
- Finding node y: O(log n)
- Forming the new tree: O(1)
- RB-INSERT-FIXUP: O(log n)

---

## Problem 3: Joining and Splitting 2-3-4 Trees [40 Points]

### Part (a): Maintaining Height
**For insertion:** When we split a node x into nodes y and z, and merge the median into node w:
- w.height remains unchanged unless x was the root (then w.height = x.height + 1)
- y.height = max(y.c_i.height) + 1
- z.height = max(z.c_i.height) + 1

Each update takes O(t) = O(1). Since B-TREE-INSERT makes at most h splits, total time for height updates is O(h), preserving asymptotic running time.

**For deletion:** Height only changes when the root has a single node and is merged with its subtree nodes. Update the new root's height to be (old root height - 1).

### Part (b): Join Operation
Without loss of generality, assume h' ≥ h''.

**Algorithm:**
1. Find the node at depth h' - h'' on the right spine of T'
2. Add k as a key to this node, and T'' as the additional child
3. If the node was already full, perform a split operation

**Time complexity:** O(1 + |h' - h''|) since we traverse |h' - h''| levels and perform O(1) work per level, plus possibly one split.

### Part (c): Path Decomposition
Let x_i be the node encountered after i steps on path p. Let l_i be the index of the largest key in x_i that is less than or equal to k.

**For S':**
- k'_i = x_i.key[l_i]
- T'_{i-1} is the tree whose root consists of keys in x_i less than x_i.key[l_i], with all their children

**Relationship:** In general, T'_{i-1}.height ≥ T'_i.height (heights are non-increasing as we descend).

**For S'':** Symmetric approach:
- Keys are those in nodes on p that are immediately greater than k
- Trees are rooted at nodes consisting of larger keys with associated subtrees

### Part (d): Split Operation

**Algorithm:**
1. Let T₁ and T₂ be empty trees
2. Follow path p from root of T to k
3. At each node x_i:
   - Join tree T'_{i-1} to T₁, then insert k'_i into T₁
   - Join tree T''_{i-1} to T₂, then insert k''_i into T₂
4. At the node containing k at x_m.key[j]:
   - Join x_m.c_j with T₁
   - Join x_m.c_{j+1} with T₂

**Time complexity:** O(log n)

At each level, we perform at most 2 join operations and 1 insert. Using the runtime from part (b), when we join a tree T' to T₁, the height difference is T'.height - T₁.height.

Since heights are non-decreasing for successive trees joined, we get a **telescoping sum**: The first tree has height h, and the last has height 0. Total: O(2(h + h)) = O(h) = O(log n).
