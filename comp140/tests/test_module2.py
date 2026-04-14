"""
Tests for Module 2: Spot It!

Tests the following functions:
- equivalent(point1, point2, mod)
- incident(point, line, mod)
- generate_all_points(mod)
- create_cards(points, lines, mod)
"""

import sys
from pathlib import Path

_tests_dir = Path(__file__).parent
if str(_tests_dir) not in sys.path:
    sys.path.insert(0, str(_tests_dir))

from fixtures import make_test_result

# Point values for each test
TEST_POINTS = {
    "test_equivalent_same_point": 1,
    "test_equivalent_scaled_point": 1,
    "test_equivalent_different_points": 1,
    "test_equivalent_mod2": 1,
    "test_equivalent_mod3": 1,
    "test_equivalent_mod_wraparound": 1,
    "test_equivalent_not_scaled": 1,
    "test_incident_on_line": 1,
    "test_incident_not_on_line": 1,
    "test_incident_origin_line": 1,
    "test_incident_mod2": 1,
    "test_incident_mod3": 1,
    "test_generate_all_points_mod2_count": 1,
    "test_generate_all_points_mod2_unique": 1,
    "test_generate_all_points_mod3_unique": 1,
    "test_generate_all_points_mod3_count": 1,
    "test_generate_all_points_mod5_count": 1,
    "test_generate_all_points_tuples": 1,
    "test_create_cards_mod2_count": 1,
    "test_create_cards_mod2_images_per_card": 1,
    "test_create_cards_mod3_one_common": 2,
    "test_create_cards_mod3_valid_deck": 2,
    "test_create_cards_all_images_used": 1,
    "test_create_cards_image_frequency": 1,
    "test_create_cards_no_duplicates_on_card": 1,
}


# ============================================================================
# Equivalent Tests
# ============================================================================

def test_equivalent_same_point(module):
    """Test equivalent with identical points."""
    result = module.equivalent((1, 0, 0), (1, 0, 0), 5)
    expected = True
    return make_test_result(result == expected, expected, result)

test_equivalent_same_point.input_description = "point1=(1,0,0), point2=(1,0,0), mod=5"


def test_equivalent_scaled_point(module):
    """Test equivalent with scaled points (same in projective space)."""
    # (1,2,3) and (2,4,6) should be equivalent in mod 7 (since 2*1=2, 2*2=4, 2*3=6)
    result = module.equivalent((1, 2, 3), (2, 4, 6), 7)
    expected = True
    return make_test_result(result == expected, expected, result)

test_equivalent_scaled_point.input_description = "point1=(1,2,3), point2=(2,4,6), mod=7"


def test_equivalent_different_points(module):
    """Test equivalent with truly different points."""
    result = module.equivalent((1, 0, 0), (0, 1, 0), 5)
    expected = False
    return make_test_result(result == expected, expected, result)

test_equivalent_different_points.input_description = "point1=(1,0,0), point2=(0,1,0), mod=5"


def test_equivalent_mod2(module):
    """Test equivalent in mod 2."""
    # (1,1,1) and (1,1,1) should be equivalent
    result = module.equivalent((1, 1, 1), (1, 1, 1), 2)
    expected = True
    return make_test_result(result == expected, expected, result)

test_equivalent_mod2.input_description = "point1=(1,1,1), point2=(1,1,1), mod=2"


def test_equivalent_mod3(module):
    """Test equivalent in mod 3 with scaling."""
    # (1,1,0) and (2,2,0) are equivalent in mod 3 (2*1=2 mod 3)
    result = module.equivalent((1, 1, 0), (2, 2, 0), 3)
    expected = True
    return make_test_result(result == expected, expected, result)

test_equivalent_mod3.input_description = "point1=(1,1,0), point2=(2,2,0), mod=3"


# ============================================================================
# Equivalent Tests
# ============================================================================

def test_equivalent_mod_wraparound(module):
    """Test equivalent with scaling that wraps around the modulus."""
    # (1,2,3) scaled by 3 = (3,6,9) = (3,1,4) mod 5
    # Cross product should be [0,0,0] mod 5
    result = module.equivalent((1, 2, 3), (3, 1, 4), 5)
    expected = True
    return make_test_result(result == expected, expected, result)

test_equivalent_mod_wraparound.input_description = "point1=(1,2,3), point2=(3,1,4), mod=5"


def test_equivalent_not_scaled(module):
    """Test equivalent with points that look similar but aren't equivalent."""
    # (1,2,3) and (1,2,4) are NOT equivalent - no scalar k makes them equal
    result = module.equivalent((1, 2, 3), (1, 2, 4), 5)
    expected = False
    return make_test_result(result == expected, expected, result)

test_equivalent_not_scaled.input_description = "point1=(1,2,3), point2=(1,2,4), mod=5"


# ============================================================================
# Incident Tests
# ============================================================================

def test_incident_on_line(module):
    """Test incident when point is on line."""
    # Point (1,1,1) on line [1,1,1] if 1*1 + 1*1 + 1*1 = 3 = 0 mod 3
    result = module.incident((1, 1, 1), (1, 1, 1), 3)
    expected = True
    return make_test_result(result == expected, expected, result)

test_incident_on_line.input_description = "point=(1,1,1), line=[1,1,1], mod=3"


def test_incident_not_on_line(module):
    """Test incident when point is not on line."""
    # Point (1,0,0) on line [0,1,0]: 1*0 + 0*1 + 0*0 = 0, so it IS on the line
    # Let's use: point (1,1,0) on line [1,0,0]: 1*1 + 1*0 + 0*0 = 1 != 0 mod 5
    result = module.incident((1, 1, 0), (1, 0, 0), 5)
    expected = False
    return make_test_result(result == expected, expected, result)

test_incident_not_on_line.input_description = "point=(1,1,0), line=[1,0,0], mod=5"


def test_incident_origin_line(module):
    """Test incident with line through origin pattern."""
    # Point (0,0,1) on line [1,1,0]: 0*1 + 0*1 + 1*0 = 0, so True
    result = module.incident((0, 0, 1), (1, 1, 0), 5)
    expected = True
    return make_test_result(result == expected, expected, result)

test_incident_origin_line.input_description = "point=(0,0,1), line=[1,1,0], mod=5"


def test_incident_mod2(module):
    """Test incident in mod 2."""
    # Point (1,0,1) on line [1,0,1]: 1*1 + 0*0 + 1*1 = 2 = 0 mod 2
    result = module.incident((1, 0, 1), (1, 0, 1), 2)
    expected = True
    return make_test_result(result == expected, expected, result)

test_incident_mod2.input_description = "point=(1,0,1), line=[1,0,1], mod=2"


def test_incident_mod3(module):
    """Test incident in mod 3."""
    # Point (1,2,0) on line [2,1,0]: 1*2 + 2*1 + 0*0 = 4 = 1 mod 3 != 0
    result = module.incident((1, 2, 0), (2, 1, 0), 3)
    expected = False
    return make_test_result(result == expected, expected, result)

test_incident_mod3.input_description = "point=(1,2,0), line=[2,1,0], mod=3"


# ============================================================================
# Generate All Points Tests
# ============================================================================

def test_generate_all_points_mod2_count(module):
    """Test generate_all_points returns correct count for mod 2."""
    # For mod p, there should be p^2 + p + 1 unique points
    # For p=2: 4 + 2 + 1 = 7 points
    result = module.generate_all_points(2)
    expected_count = 7
    actual_count = len(result)
    return make_test_result(actual_count == expected_count, expected_count, actual_count)

test_generate_all_points_mod2_count.input_description = "mod=2"


def test_generate_all_points_mod2_unique(module):
    """Test generate_all_points returns unique points for mod 2."""
    points = module.generate_all_points(2)
    # Check that no two points are equivalent
    for i, p1 in enumerate(points):
        for j, p2 in enumerate(points):
            if i < j:
                if module.equivalent(p1, p2, 2):
                    return make_test_result(False, "all unique", f"{p1} equivalent to {p2}")
    return make_test_result(True, "all unique", "all unique")

test_generate_all_points_mod2_unique.input_description = "mod=2"


def test_generate_all_points_mod3_unique(module):
    """Test generate_all_points returns unique points for mod 3."""
    points = module.generate_all_points(3)
    # Check that no two points are equivalent
    for i, p1 in enumerate(points):
        for j, p2 in enumerate(points):
            if i < j:
                if module.equivalent(p1, p2, 3):
                    return make_test_result(False, "all unique", f"{p1} equivalent to {p2}")
    return make_test_result(True, "all unique", "all unique")

test_generate_all_points_mod3_unique.input_description = "mod=3"


def test_generate_all_points_mod3_count(module):
    """Test generate_all_points returns correct count for mod 3."""
    # For p=3: 9 + 3 + 1 = 13 points
    result = module.generate_all_points(3)
    expected_count = 13
    actual_count = len(result)
    return make_test_result(actual_count == expected_count, expected_count, actual_count)

test_generate_all_points_mod3_count.input_description = "mod=3"


def test_generate_all_points_mod5_count(module):
    """Test generate_all_points returns correct count for mod 5."""
    # For p=5: 25 + 5 + 1 = 31 points
    result = module.generate_all_points(5)
    expected_count = 31
    actual_count = len(result)
    return make_test_result(actual_count == expected_count, expected_count, actual_count)

test_generate_all_points_mod5_count.input_description = "mod=5"


def test_generate_all_points_tuples(module):
    """Test generate_all_points returns list of tuples."""
    points = module.generate_all_points(2)
    if not isinstance(points, list):
        return make_test_result(False, "list", type(points).__name__)
    if len(points) == 0:
        return make_test_result(False, "non-empty list", "empty list")
    if not isinstance(points[0], tuple):
        return make_test_result(False, "tuple", type(points[0]).__name__)
    if len(points[0]) != 3:
        return make_test_result(False, "3-tuple", f"{len(points[0])}-tuple")
    return make_test_result(True, "list of 3-tuples", "list of 3-tuples")

test_generate_all_points_tuples.input_description = "mod=2"


# ============================================================================
# Create Cards Tests
# ============================================================================

def test_create_cards_mod2_count(module):
    """Test create_cards returns correct number of cards for mod 2."""
    # For p=2: 7 points = 7 lines = 7 cards
    points = module.generate_all_points(2)
    lines = points[:]  # Lines are same as points by duality
    cards = module.create_cards(points, lines, 2)
    expected_count = 7
    actual_count = len(cards)
    return make_test_result(actual_count == expected_count, expected_count, actual_count)

test_create_cards_mod2_count.input_description = "mod=2"


def test_create_cards_mod2_images_per_card(module):
    """Test create_cards gives correct images per card for mod 2."""
    # For p=2: each card should have p+1 = 3 images
    points = module.generate_all_points(2)
    lines = points[:]
    cards = module.create_cards(points, lines, 2)
    if len(cards) == 0:
        return make_test_result(False, "3 images per card", "no cards")
    expected_images = 3
    actual_images = len(cards[0])
    # Check all cards have same number of images
    all_same = all(len(card) == expected_images for card in cards)
    if not all_same:
        return make_test_result(False, f"all cards have {expected_images}", "varying counts")
    return make_test_result(actual_images == expected_images, expected_images, actual_images)

test_create_cards_mod2_images_per_card.input_description = "mod=2"


def test_create_cards_mod3_one_common(module):
    """Test create_cards: every pair of cards shares exactly one image (mod 3)."""
    points = module.generate_all_points(3)
    lines = points[:]
    cards = module.create_cards(points, lines, 3)

    if len(cards) < 2:
        return make_test_result(False, "at least 2 cards", len(cards))

    # Check that every pair shares exactly one image
    for i in range(len(cards)):
        for j in range(i + 1, len(cards)):
            card1_set = set(cards[i])
            card2_set = set(cards[j])
            common = card1_set & card2_set
            if len(common) != 1:
                return make_test_result(
                    False,
                    "exactly 1 common",
                    f"cards {i},{j} share {len(common)}"
                )

    return make_test_result(True, "all pairs share 1", "all pairs share 1")

test_create_cards_mod3_one_common.input_description = "mod=3"


def test_create_cards_mod3_valid_deck(module):
    """Test create_cards produces valid Spot It deck for mod 3."""
    points = module.generate_all_points(3)
    lines = points[:]
    cards = module.create_cards(points, lines, 3)

    # For p=3: 13 cards, 4 images per card
    expected_cards = 13
    expected_images = 4

    if len(cards) != expected_cards:
        return make_test_result(False, f"{expected_cards} cards", f"{len(cards)} cards")

    for i, card in enumerate(cards):
        if len(card) != expected_images:
            return make_test_result(
                False,
                f"{expected_images} images",
                f"card {i} has {len(card)}"
            )

    return make_test_result(True, "valid deck", "valid deck")

test_create_cards_mod3_valid_deck.input_description = "mod=3"


def test_create_cards_all_images_used(module):
    """Test create_cards uses all images (point indices) at least once."""
    points = module.generate_all_points(3)
    lines = points[:]
    cards = module.create_cards(points, lines, 3)

    # For p=3: 13 unique images (0 through 12)
    expected_images = set(range(13))

    # Collect all images used across all cards
    used_images = set()
    for card in cards:
        used_images.update(card)

    if used_images != expected_images:
        missing = expected_images - used_images
        extra = used_images - expected_images
        if missing:
            return make_test_result(False, "all 13 images used", f"missing: {missing}")
        if extra:
            return make_test_result(False, "images 0-12 only", f"extra: {extra}")

    return make_test_result(True, "all images used", "all images used")

test_create_cards_all_images_used.input_description = "mod=3"


def test_create_cards_image_frequency(module):
    """Test each image appears on exactly p+1 cards (mod 3 -> 4 cards each)."""
    points = module.generate_all_points(3)
    lines = points[:]
    cards = module.create_cards(points, lines, 3)

    # For p=3: each image should appear on exactly p+1 = 4 cards
    # (By duality: each point lies on exactly p+1 lines)
    expected_frequency = 4

    # Count how many times each image appears
    image_counts = {}
    for card in cards:
        for img in card:
            image_counts[img] = image_counts.get(img, 0) + 1

    for img, count in image_counts.items():
        if count != expected_frequency:
            return make_test_result(
                False,
                f"each image on {expected_frequency} cards",
                f"image {img} on {count} cards"
            )

    return make_test_result(True, "correct frequency", "correct frequency")

test_create_cards_image_frequency.input_description = "mod=3"


def test_create_cards_no_duplicates_on_card(module):
    """Test no card has duplicate images."""
    points = module.generate_all_points(2)
    lines = points[:]
    cards = module.create_cards(points, lines, 2)

    for i, card in enumerate(cards):
        if len(card) != len(set(card)):
            return make_test_result(
                False,
                "no duplicates",
                f"card {i} has duplicates: {card}"
            )

    return make_test_result(True, "no duplicates", "no duplicates")

test_create_cards_no_duplicates_on_card.input_description = "mod=2"
