# COMP 382 Homework 1 - Solutions

---

## Problem 1: Radix Trees [20 points]

### Part 1: Formal Definition [3 points]

A radix tree T is a rooted tree with the following constraints:

1. Every node x is either grey or black.
2. Every leaf is grey.
3. Every grey node x_g contains a bit string, x_g.str.
4. Every edge e contains a value e.bit which is 0 or 1.
5. For the path from T.root to any grey node x_g that follows the edges e_0, e_1, e_2, ..., e_s, the bit string stored in x_g.str is of length s + 1 where:
   - x_g.str[0] = e_0.bit
   - x_g.str[1] = e_1.bit
   - ...
   - x_g.str[s] = e_s.bit

### Part 2: Pseudo-code [10 points]

**RADIX-TREE-SEARCH(T, w):**
```
subtree <- T
for each character w_i in w do
    if w_i = 1 then
        if subtree.right != NIL then
            subtree <- subtree.right
        else
            return NIL  // No right subtree to continue search
    else
        if subtree.left != NIL then
            subtree <- subtree.left
        else
            return NIL  // No left subtree to continue search

if subtree.root is a grey node then
    return subtree.root  // traversed w fully and landed on grey node
return NIL
```

**RADIX-TREE-INSERT(T, w):**
```
subtree <- T
for each character w_i in w do
    if w_i = 1 then
        if subtree.right != NIL then
            subtree <- subtree.right
        else
            node <- CreateBlackNode()
            subtree.right <- node
            subtree <- node
    else
        if subtree.left != NIL then
            subtree <- subtree.left
        else
            node <- CreateBlackNode()
            subtree.left <- node
            subtree <- node

if subtree.root is a black node then
    ConvertToGreyNode(subtree.root)
```

**Time Complexity Justification:** Each function iterates through the characters of w exactly once, performing O(1) work per character. Thus, each function takes Θ(|w|) time.

### Part 3: Lexicographic Sorting [7 points]

**Algorithm:**
1. Insert all bit strings in set S into a radix tree T. Since inserting a bit string w takes Θ(|w|), inserting all strings takes Θ(n) where n is the sum of all string lengths.

2. Extract all strings in lexicographic order using pre-order traversal:
   - If the current node is grey, extract its string and add it to the list
   - Traverse the left subtree recursively (0 comes before 1)
   - Traverse the right subtree recursively

**Time Complexity:**
- Insertion phase: Θ(n)
- Extraction phase: Visit each node exactly once, and there are at most n nodes, so Θ(n)
- Total: Θ(n)

---

## Problem 2: Multisets [20 Points]

### Part 1 [10 points]

**Data Structure:** Use a Red-Black Tree where:
- Each node stores a unique element k as its key
- Each node has an additional field `count` storing the number of occurrences
- Each node has an augmented field `subtree_count` storing the total count of all elements in its subtree

**Operations:**

| Operation | Implementation | Time |
|-----------|----------------|------|
| CREATE(S) | Initialize empty RB-tree | O(1) |
| INSERT(S, k) | Search for k; if found, increment count; otherwise insert new node with count=1. Update subtree_count along path | O(log n) |
| GETCOUNT(S, k) | Search for k, return count (or 0 if not found) | O(log n) |
| DELETE(S, k) | Search for k; if count > 1, decrement; otherwise delete node. Update subtree_count along path | O(log n) |
| GETCOUNTS≥(S, k) | Search for k, accumulate subtree_count from right children along path | O(log n) |

### Part 2 [10 points]

**Extended Data Structure:** Use two Red-Black Trees:

**Tree E (Elements):**
- Keys are the unique elements
- Each node has a pointer to its count node in Tree C

**Tree C (Counts):**
- Keys are the unique count values
- Each node v has:
  - v.num: number of elements with this count
  - v.sub: sum of num fields in subtree rooted at v

**Operations:**

| Operation | Implementation | Time |
|-----------|----------------|------|
| WITHCOUNT(S, c) | Search for c in C, return num field | O(log n) |
| FREQUENT(S, c) | Search for c in C, use sub fields to compute sum of elements with count ≥ c | O(log n) |

---

## Problem 3: AVL Trees: Height & Size [10 Points]

**Claim:** An AVL tree with n nodes has height h < c · log₂(n + 2) + b, where c = 1/log₂φ ≈ 1.44042 and b = (log₂5)/(2·log₂φ) - 2 ≈ -0.32772, and φ = (1 + √5)/2 is the golden ratio.

**Proof:**

Let N_h be the minimum number of nodes in an AVL tree of height h. By the AVL property:
- N_0 = 0
- N_1 = 1
- N_h = N_{h-1} + N_{h-2} + 1 for h ≥ 2

**Claim:** N_h = F_{h+2} - 1, where F_h is the h-th Fibonacci number.

**Proof by induction:**
- Base cases: F_2 - 1 = 1 - 1 = 0 = N_0, and F_3 - 1 = 2 - 1 = 1 = N_1
- Inductive step: N_h = N_{h-1} + N_{h-2} + 1 = (F_{h+1} - 1) + (F_h - 1) + 1 = F_{h+1} + F_h - 1 = F_{h+2} - 1

From n ≥ N_h = F_{h+2} - 1, we have F_{h+2} ≤ n + 1.

Using the closed-form Fibonacci formula:
$$F_h = \frac{\phi^h}{\sqrt{5}} - \frac{\psi^h}{\sqrt{5}}$$

where φ = (1 + √5)/2 and ψ = (1 - √5)/2.

Since |ψ| < 1, we have F_h > φ^h/√5 - 1.

Therefore:
$$\frac{\phi^{h+2}}{\sqrt{5}} - 1 < n + 1$$
$$\phi^{h+2} < \sqrt{5}(n + 2)$$
$$h + 2 < \log_\phi\sqrt{5} + \log_\phi(n + 2)$$
$$h < \frac{\log_2(n + 2)}{\log_2\phi} + \frac{\log_2 5}{2\log_2\phi} - 2$$

Thus: **h < 1.44042 · log₂(n + 2) - 0.32772**
