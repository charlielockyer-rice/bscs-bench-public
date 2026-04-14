# Spot It! Writeup Solutions

## 3.B.i. Equivalent Points

The points $p = (1, 0, 0)$ and $q = (2, 0, 0)$ are distinct in $\mathbb{R}$ but are equivalent in the finite projective field of $\mathbb{Z}_3$. This can be shown by the cross product of $p \times q$ as follows:

$$p \times q = [p_y q_z - p_z q_y,~~ p_z q_x - p_x q_z,~~ p_x q_y - p_y q_x]$$

$$p \times q = [0 \cdot 0 - 0 \cdot 0\mod 3,~~ 0 \cdot 2 - 1 \cdot 0\mod 3,~~ 1 \cdot 0 - 0 \cdot 2\mod 3]$$

$$p \times q = [0\mod 3,~~ 0 \mod 3,~~ 0\mod 3]$$

$$p \times q = [0, 0, 0]$$

Since the cross product is all 0s, the points $p$ and $q$ are equivalent.

---

## 3.B.ii. Different Points

In the finite projective field of $\mathbb{Z}_3$, the points $p = (1, 0, 0)$ and $q = (0, 1, 0)$ are not equivalent. This can be shown by the cross product of $p \times q$ as follows:

$$p \times q = [p_y q_z - p_z q_y,~~ p_z q_x - p_x q_z,~~ p_x q_y - p_y q_x]$$

$$p \times q = [0 \cdot 0 - 0 \cdot 1\mod 3,~~ 0 \cdot 0 - 1 \cdot 0\mod 3,~~ 1 \cdot 1 - 0 \cdot 0\mod 3]$$

$$p \times q = [0\mod 3,~~ 0\mod 3,~~ 1\mod 3]$$

$$p \times q = [0, 0, 1]$$

Since the cross product is not all 0s, the points $p$ and $q$ are not equivalent. Furthermore, we can confirm that both points, $p$ and $q$, lie on the resulting line $[0, 0, 1]$ as follows:

**For $p$:**
$$l_a p_x + l_b p_y + l_c p_z = 0\mod 3$$
$$0 \cdot 1 + 0 \cdot 0 + 1 \cdot 0 = 0\mod 3$$
$$0 + 0 + 0 = 0 \mod 3$$
$$0 = 0$$

**For $q$:**
$$l_a q_x + l_b q_y + l_c q_z = 0\mod 3$$
$$0 \cdot 0 + 0 \cdot 1 + 1 \cdot 0 = 0\mod 3$$
$$0 + 0 + 0 = 0\mod 3$$
$$0 = 0$$

---

## 3.C.i. Generating All Points

A recipe for generating all points is as follows:

**Recipe:** `generate_all_points`

**Inputs:** a prime modulus $p$ indicating that we are generating all points in the projective geometric space in $\mathbb{Z}_p$

1. Generate a sequence of all possible triples, $(x, y, z)$, where $x$, $y$, and $z$ are all integers between 0 and $p-1$; assign this sequence to `triples`.

2. Remove the triple $(0, 0, 0)$ from `triples`, as it is not a valid point.

3. Initialize `points` to be an empty sequence.

4. Repeat the following for each triple, `candidate`, in `triples`:
   - Assign the value $true$ to `unique`.
   - Repeat the following for each point, `point`, in `points`:
     - If the `equivalent` function (defined in 3B) called with `candidate`, `point`, and $p$ returns $true$, assign $false$ to `unique`
   - If `unique` is $true$ (which means `candidate` is not equivalent to any point in `points`), add `candidate` to `points`.

5. Return `points`, which is now the collection of unique points.

*Note that this is only one possible recipe. You can also remove equivalent points as you generate them, for instance. In that case, you would have to provide a more detailed description of the generation process.*

Notice that the recipe clearly explains *what* needs to be done, not *how* to do it. Think about a cooking recipe, for example, that tells you to chop celery. It leaves it up to you how to accomplish that. It doesn't specify what cutting board or knife to use. Nor does it tell you whether or not you should use a food processor. It just tells you to chop the celery. The decision of how to do it is an "implementation" detail, not part of the recipe for what you need to do to cook the dish. Be careful, though, about being too abstract, as you still must provide enough detail in order for the reader to clearly and unambiguously know what to do. It is also correct to provide additional details, so you should err on the side of providing too much detail, rather than too little.

Also notice that there is *no* Python in the recipe. A good recipe (algorithm description) is independent of any programming language.

---

## 4.A. Creating Cards

A recipe for creating a deck of Spot it! cards is as follows:

*Assume that each card is a sequence of integers, where each integer corresponds to a unique image.*

**Recipe:** `create_cards`

**Inputs:**
- a prime modulus, $p$
- a sequence of lines, $lines$, where each element is a line [a, b, c] in the projective geometric space in $\mathbb{Z}_p$
- a sequence of points, $points$, where each element is a point (x, y, z) in the projective geometric space in $\mathbb{Z}_p$

1. Assign an empty sequence to `cards`.

2. For each `line` in $lines$, do the following:
   - Assign an empty sequence to `card`.
   - For each `index` from 0 to the length of $points$ - 1, do the following:
     - Assign $points_{index}$ to `point`
     - If the `incident` function (defined in 3A) called with `point`, `line`, and `p` returns $true$ (meaning that `point` is on `line`), add `index` to the end of `card`.
   - Add `card` to `cards`.

3. Return `cards`, which is now a sequence of Spot it! cards.

*This assumes that a line is a card and the points on that line are the images on the card. You can flip this and get an equivalent result.*

---

## 5. Discussion

From the assignment description, in a valid deck of cards for the game Spot It!, every card must have the same number of images on it, and every card, with respect to each other card in the deck, must have one image, and only one image, in common with that other card. If you use a prime modulus of 7, you will generate 57 such cards. To create a valid deck with 40 cards, you could select any 40 cards from that 57-card deck. You are guaranteed that those 40 cards each have one, and only one, image in common with each other.

Note that when you played Spot It! on the first day of this module, you had a valid deck with 13 cards in it. That deck was a subset of a full deck using a prime modulus of 5. This is why not all of the images occurred an equal number of times. Furthermore, if you buy the actual game of Spot It!, you will get a deck with 56 cards in it. This deck is a subset of a full 57-card deck using a prime modulus of 7. Presumably, the makers of the game felt that 56 cards results in a better game for the typical number of players that will play the game.
