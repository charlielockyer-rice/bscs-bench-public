"""
Reference Solution for Module 2: Spot It!

This module implements the projective geometry functions needed to create
a valid Spot It! card deck.

In projective geometry over a finite field Z_p (where p is prime):
- A point is a triple (x, y, z) not all zero
- A line is a triple [a, b, c] not all zero
- Points (kx, ky, kz) and (x, y, z) are equivalent for any k != 0
- Point (x, y, z) lies on line [a, b, c] iff ax + by + cz = 0 (mod p)

For a prime modulus p:
- There are p^2 + p + 1 unique points (and lines)
- Each line contains p + 1 points
- Every two distinct lines intersect at exactly one point

This last property makes projective planes perfect for Spot It!:
- Each card is a line
- Each image is a point
- Any two cards share exactly one image
"""


def equivalent(point1, point2, mod):
    """
    Determines if two points are equivalent in projective space over Z_mod.

    Two points are equivalent if one is a scalar multiple of the other.
    We use the cross product to test this: if p x q = [0, 0, 0] (mod m),
    then there is no unique line through them, meaning they are the same point.

    Cross product formula:
    p x q = [p_y*q_z - p_z*q_y, p_z*q_x - p_x*q_z, p_x*q_y - p_y*q_x]

    inputs:
        - point1: a tuple of 3 integers representing the first point
        - point2: a tuple of 3 integers representing the second point
        - mod: an integer representing the modulus (should be prime)

    returns: a boolean indicating whether the points are equivalent
    """
    p_x, p_y, p_z = point1
    q_x, q_y, q_z = point2

    # Compute cross product components
    cross_a = (p_y * q_z - p_z * q_y) % mod
    cross_b = (p_z * q_x - p_x * q_z) % mod
    cross_c = (p_x * q_y - p_y * q_x) % mod

    # Points are equivalent if cross product is [0, 0, 0]
    return cross_a == 0 and cross_b == 0 and cross_c == 0


def incident(point, line, mod):
    """
    Determines if a point lies on a line in projective space over Z_mod.

    A point (x, y, z) lies on line [a, b, c] if and only if:
        a*x + b*y + c*z = 0 (mod m)

    inputs:
        - point: a tuple of 3 integers representing a point
        - line: a tuple of 3 integers representing a line
        - mod: an integer representing the modulus (should be prime)

    returns: a boolean indicating whether the point lies on the line
    """
    p_x, p_y, p_z = point
    l_a, l_b, l_c = line

    # Compute dot product and check if it's zero mod m
    dot_product = (l_a * p_x + l_b * p_y + l_c * p_z) % mod

    return dot_product == 0


def generate_all_points(mod):
    """
    Generate all unique points in the projective plane over Z_mod.

    Algorithm:
    1. Generate all possible triples (x, y, z) where x, y, z in {0, 1, ..., mod-1}
    2. Exclude (0, 0, 0) as it's not a valid point
    3. For each triple, check if it's equivalent to any already-collected point
    4. If not equivalent to any existing point, add it to our collection

    For prime p, the result should have exactly p^2 + p + 1 points.

    inputs:
        - mod: an integer representing the modulus (should be prime)

    returns: a list of unique points, each is a tuple of 3 integers
    """
    points = []

    # Iterate through all possible triples
    for x in range(mod):
        for y in range(mod):
            for z in range(mod):
                # Skip the invalid point (0, 0, 0)
                if x == 0 and y == 0 and z == 0:
                    continue

                candidate = (x, y, z)

                # Check if this candidate is equivalent to any existing point
                is_unique = True
                for existing_point in points:
                    if equivalent(candidate, existing_point, mod):
                        is_unique = False
                        break

                # If unique, add to our collection
                if is_unique:
                    points.append(candidate)

    return points


def create_cards(points, lines, mod):
    """
    Create a deck of Spot It! cards from points and lines.

    Each line becomes a card, and the images on that card are the indices
    of points that lie on that line. The index of a point in the points list
    serves as the "image number" on the card.

    inputs:
        - points: a list of unique points, each represented as a tuple of 3 integers
        - lines: a list of unique lines, each represented as a tuple of 3 integers
        - mod: an integer representing the modulus (should be prime)

    returns: a list of lists of integers, where each nested list represents a card
             and contains the indices of images (points) on that card
    """
    cards = []

    for line in lines:
        card = []

        # Find all points that lie on this line
        for index, point in enumerate(points):
            if incident(point, line, mod):
                card.append(index)

        cards.append(card)

    return cards


def make_deck(mod):
    """
    Convenience function to create a complete Spot It! deck.

    inputs:
        - mod: a prime number representing the modulus

    returns: a list of cards (each card is a list of image indices)
    """
    points = generate_all_points(mod)
    lines = points[:]  # Lines are same as points by duality
    return create_cards(points, lines, mod)


def verify_deck(mod):
    """
    Verify that the generated deck satisfies Spot It! properties.

    Properties checked:
    1. Correct number of cards (p^2 + p + 1)
    2. Correct number of images per card (p + 1)
    3. Every pair of cards shares exactly one image

    inputs:
        - mod: a prime number representing the modulus

    returns: tuple (success: bool, message: str)
    """
    deck = make_deck(mod)

    expected_cards = mod * mod + mod + 1
    expected_images_per_card = mod + 1

    # Check card count
    if len(deck) != expected_cards:
        return False, f"Expected {expected_cards} cards, got {len(deck)}"

    # Check images per card
    for i, card in enumerate(deck):
        if len(card) != expected_images_per_card:
            return False, f"Card {i} has {len(card)} images, expected {expected_images_per_card}"

    # Check that every pair shares exactly one image
    for i in range(len(deck)):
        for j in range(i + 1, len(deck)):
            common = set(deck[i]) & set(deck[j])
            if len(common) != 1:
                return False, f"Cards {i} and {j} share {len(common)} images (expected 1)"

    return True, f"Valid deck: {expected_cards} cards, {expected_images_per_card} images each"


if __name__ == "__main__":
    # Test with small moduli
    for p in [2, 3, 5, 7]:
        success, message = verify_deck(p)
        print(f"mod={p}: {message}")
