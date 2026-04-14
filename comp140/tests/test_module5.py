"""
Tests for Module 5: QR Code Generator (Public)

Smoke tests covering all major areas:
- Polynomial class basics (init, degree)
- add_term, subtract_term, multiply_by_term
- add_polynomial, subtract_polynomial, multiply_by_polynomial
- remainder
- create_message_polynomial, create_generator_polynomial
- reed_solomon_correction
"""

import sys
from pathlib import Path

_tests_dir = Path(__file__).parent
if str(_tests_dir) not in sys.path:
    sys.path.insert(0, str(_tests_dir))

from fixtures import make_test_result, approx_equal

# Add lib to path for z256 module
lib_path = Path(__file__).parent.parent.parent / "lib"
if str(lib_path) not in sys.path:
    sys.path.insert(0, str(lib_path))

import comp140_module5_z256 as z256

# Point values for each test
TEST_POINTS = {
    # Polynomial basics (2)
    "test_polynomial_init_with_terms": 0.5,
    "test_polynomial_get_degree": 0.5,
    # add_term (1)
    "test_add_term_existing_power": 1,
    # subtract_term (1)
    "test_subtract_term_existing_power": 1,
    # multiply_by_term (1)
    "test_multiply_by_term_multiple": 1,
    # add_polynomial (1)
    "test_add_polynomial_overlapping": 1,
    # subtract_polynomial (1)
    "test_subtract_polynomial_same": 1,
    # multiply_by_polynomial (1)
    "test_multiply_polynomial_multiple": 1,
    # remainder (1)
    "test_remainder_larger_degree": 1,
    # create_message_polynomial (1)
    "test_create_message_polynomial_multiple": 1,
    # create_generator_polynomial (1)
    "test_create_generator_polynomial_k2": 1,
    # reed_solomon_correction (1)
    "test_reed_solomon_degree": 1,
    # Polynomial class basics (7)
    "test_polynomial_init_empty": 0.5,
    "test_polynomial_str": 0.5,
    "test_polynomial_eq": 0.5,
    "test_polynomial_ne": 0.5,
    "test_polynomial_get_terms": 0.5,
    "test_polynomial_get_degree_zero": 0.5,
    "test_polynomial_get_coefficient_missing": 0.5,
    # add_term (4)
    "test_add_term_to_empty": 1,
    "test_add_term_new_power": 1,
    "test_add_term_zero_coefficient": 0.5,
    "test_add_term_power_zero": 0.5,
    # subtract_term (2)
    "test_subtract_term_new_power": 1,
    "test_subtract_term_same_coefficient": 1,
    # multiply_by_term (4)
    "test_multiply_by_term_zero_coeff": 1,
    "test_multiply_by_term_single": 1,
    "test_multiply_by_term_power_zero": 0.5,
    "test_multiply_by_term_one": 0.5,
    # add_polynomial (3)
    "test_add_polynomial_to_empty": 1,
    "test_add_polynomial_disjoint": 1,
    "test_add_polynomial_to_itself": 1,
    # subtract_polynomial (2)
    "test_subtract_polynomial_different": 1,
    "test_subtract_polynomial_z256": 1,
    # multiply_by_polynomial (3)
    "test_multiply_polynomial_zero": 1,
    "test_multiply_polynomial_single": 1,
    "test_multiply_polynomial_z256": 1,
    # remainder (4)
    "test_remainder_smaller_degree": 1,
    "test_remainder_same_degree": 1,
    "test_remainder_exact_division": 1,
    "test_remainder_z256": 1,
    # create_message_polynomial (3)
    "test_create_message_polynomial_single": 1,
    "test_create_message_polynomial_degree": 1,
    "test_create_message_polynomial_empty": 0.5,
    # create_generator_polynomial (3)
    "test_create_generator_polynomial_k1": 1,
    "test_create_generator_polynomial_degree": 1,
    "test_create_generator_polynomial_k3": 1,
    # reed_solomon_correction (3)
    "test_reed_solomon_returns_polynomial": 1,
    "test_reed_solomon_k1": 1,
    "test_reed_solomon_coefficients_in_range": 1,
}


# ============================================================================
# Polynomial Class Basic Tests
# ============================================================================

def test_polynomial_init_with_terms(module):
    """Test Polynomial initializes with given terms."""
    p = module.Polynomial({2: 5, 1: 3, 0: 1})  # 5x^2 + 3x + 1

    c2 = p.get_coefficient(2)
    c1 = p.get_coefficient(1)
    c0 = p.get_coefficient(0)

    if c2 == 5 and c1 == 3 and c0 == 1:
        return make_test_result(True, "5x^2 + 3x + 1", "correct")
    return make_test_result(False, "5x^2 + 3x + 1", f"{c2}x^2 + {c1}x + {c0}")

test_polynomial_init_with_terms.input_description = "Polynomial({2: 5, 1: 3, 0: 1})"


def test_polynomial_get_degree(module):
    """Test Polynomial get_degree returns highest power."""
    p = module.Polynomial({5: 1, 2: 3, 0: 1})  # x^5 + 3x^2 + 1

    degree = p.get_degree()
    expected = 5

    return make_test_result(degree == expected, expected, degree)

test_polynomial_get_degree.input_description = "x^5 + 3x^2 + 1"


# ============================================================================
# Add Term Tests
# ============================================================================

def test_add_term_existing_power(module):
    """Test add_term with existing power (Z256 addition)."""
    p = module.Polynomial({2: 5})  # 5x^2
    result = p.add_term(3, 2)  # + 3x^2

    # In Z256, addition is XOR: 5 XOR 3 = 6
    coeff = result.get_coefficient(2)
    expected = z256.add(5, 3)  # 6

    return make_test_result(coeff == expected, expected, coeff)

test_add_term_existing_power.input_description = "5x^2 + 3x^2 (Z256)"


# ============================================================================
# Subtract Term Tests
# ============================================================================

def test_subtract_term_existing_power(module):
    """Test subtract_term with existing power."""
    p = module.Polynomial({2: 10})  # 10x^2
    result = p.subtract_term(3, 2)  # - 3x^2

    coeff = result.get_coefficient(2)
    expected = z256.sub(10, 3)  # In Z256, same as add: 10 XOR 3 = 9

    return make_test_result(coeff == expected, expected, coeff)

test_subtract_term_existing_power.input_description = "10x^2 - 3x^2 (Z256)"


# ============================================================================
# Multiply by Term Tests
# ============================================================================

def test_multiply_by_term_multiple(module):
    """Test multiply_by_term on multiple terms."""
    p = module.Polynomial({2: 2, 0: 1})  # 2x^2 + 1
    result = p.multiply_by_term(3, 1)  # * 3x = 6x^3 + 3x in Z256

    c3 = result.get_coefficient(3)
    c1 = result.get_coefficient(1)

    exp_c3 = z256.mul(2, 3)
    exp_c1 = z256.mul(1, 3)

    if c3 == exp_c3 and c1 == exp_c1:
        return make_test_result(True, f"{exp_c3}x^3 + {exp_c1}x", f"{c3}x^3 + {c1}x")
    return make_test_result(False, f"{exp_c3}x^3 + {exp_c1}x", f"{c3}x^3 + {c1}x")

test_multiply_by_term_multiple.input_description = "(2x^2 + 1) * 3x"


# ============================================================================
# Add Polynomial Tests
# ============================================================================

def test_add_polynomial_overlapping(module):
    """Test add_polynomial with overlapping powers (Z256 add)."""
    p1 = module.Polynomial({2: 10, 1: 5})  # 10x^2 + 5x
    p2 = module.Polynomial({2: 3, 0: 1})   # 3x^2 + 1

    result = p1.add_polynomial(p2)

    c2 = result.get_coefficient(2)
    expected_c2 = z256.add(10, 3)  # 10 XOR 3 = 9

    return make_test_result(c2 == expected_c2, expected_c2, c2)

test_add_polynomial_overlapping.input_description = "(10x^2 + 5x) + (3x^2 + 1)"


# ============================================================================
# Subtract Polynomial Tests
# ============================================================================

def test_subtract_polynomial_same(module):
    """Test subtract_polynomial with same polynomial (gives zero)."""
    p = module.Polynomial({2: 5, 1: 3})

    result = p.subtract_polynomial(p)

    # All coefficients should be 0
    c2 = result.get_coefficient(2)
    c1 = result.get_coefficient(1)

    if c2 == 0 and c1 == 0:
        return make_test_result(True, "zero", "zero")
    return make_test_result(False, "zero", f"{c2}x^2 + {c1}x")

test_subtract_polynomial_same.input_description = "p - p"


# ============================================================================
# Multiply by Polynomial Tests
# ============================================================================

def test_multiply_polynomial_multiple(module):
    """Test multiply_by_polynomial with multiple terms."""
    p1 = module.Polynomial({1: 1, 0: 1})  # x + 1
    p2 = module.Polynomial({1: 1, 0: 1})  # x + 1

    result = p1.multiply_by_polynomial(p2)

    # (x + 1)^2 = x^2 + 2x + 1, but in Z256: x^2 + 0x + 1
    c2 = result.get_coefficient(2)
    c1 = result.get_coefficient(1)
    c0 = result.get_coefficient(0)

    # In Z256: 1*1 = 1, 1*1 + 1*1 = 0, 1*1 = 1
    exp_c2 = z256.mul(1, 1)  # 1
    exp_c1 = z256.add(z256.mul(1, 1), z256.mul(1, 1))  # 0 (1 XOR 1)
    exp_c0 = z256.mul(1, 1)  # 1

    if c2 == exp_c2 and c1 == exp_c1 and c0 == exp_c0:
        return make_test_result(True, f"{exp_c2}x^2 + {exp_c1}x + {exp_c0}", f"{c2}x^2 + {c1}x + {c0}")
    return make_test_result(False, f"{exp_c2}x^2 + {exp_c1}x + {exp_c0}", f"{c2}x^2 + {c1}x + {c0}")

test_multiply_polynomial_multiple.input_description = "(x + 1) * (x + 1)"


# ============================================================================
# Remainder Tests
# ============================================================================

def test_remainder_larger_degree(module):
    """Test remainder when numerator has larger degree."""
    p1 = module.Polynomial({3: 1, 0: 1})  # x^3 + 1
    p2 = module.Polynomial({1: 1})        # x

    result = p1.remainder(p2)

    # x^3 + 1 = x^2 * x + 1, so remainder is 1
    degree = result.get_degree()
    c0 = result.get_coefficient(0)

    # Remainder should have degree 0
    return make_test_result(degree == 0 and c0 == 1, "1", str(result))

test_remainder_larger_degree.input_description = "(x^3 + 1) mod x"


# ============================================================================
# Create Message Polynomial Tests
# ============================================================================

def test_create_message_polynomial_multiple(module):
    """Test create_message_polynomial with multiple bytes."""
    result = module.create_message_polynomial([1, 2, 3], 2)  # k=2

    # Message [1,2,3] with k=2 should be 1*x^4 + 2*x^3 + 3*x^2
    c4 = result.get_coefficient(4)
    c3 = result.get_coefficient(3)
    c2 = result.get_coefficient(2)

    if c4 == 1 and c3 == 2 and c2 == 3:
        return make_test_result(True, "1x^4 + 2x^3 + 3x^2", "correct")
    return make_test_result(False, "1x^4 + 2x^3 + 3x^2", f"{c4}x^4 + {c3}x^3 + {c2}x^2")

test_create_message_polynomial_multiple.input_description = "[1,2,3], k=2"


# ============================================================================
# Create Generator Polynomial Tests
# ============================================================================

def test_create_generator_polynomial_k2(module):
    """Test create_generator_polynomial with k=2."""
    result = module.create_generator_polynomial(2)

    # g(x) = (x - 1)(x - 2) in Z256
    # = x^2 - 3x + 2 = x^2 + 3x + 2 (in Z256)
    degree = result.get_degree()

    if degree != 2:
        return make_test_result(False, "degree 2", f"degree {degree}")
    return make_test_result(True, "degree 2", "degree 2")

test_create_generator_polynomial_k2.input_description = "k=2"


# ============================================================================
# Reed-Solomon Correction Tests
# ============================================================================

def test_reed_solomon_degree(module):
    """Test that reed_solomon_correction returns correct degree."""
    k = 3
    result = module.reed_solomon_correction([1, 2, 3, 4], k)

    # Error correction polynomial should have degree k-1 or less
    degree = result.get_degree()

    if degree <= k - 1:
        return make_test_result(True, f"degree <= {k-1}", f"degree = {degree}")
    return make_test_result(False, f"degree <= {k-1}", f"degree = {degree}")

test_reed_solomon_degree.input_description = "[1,2,3,4], k=3"


# ============================================================================
# Polynomial Class Basic Tests
# ============================================================================

def test_polynomial_init_empty(module):
    """Test Polynomial initializes as zero polynomial."""
    p = module.Polynomial()

    # Should have no terms with non-zero coefficients
    c0 = p.get_coefficient(0)
    c1 = p.get_coefficient(1)

    if c0 == 0 and c1 == 0:
        return make_test_result(True, "zero polynomial", "zero polynomial")
    return make_test_result(False, "zero polynomial", f"c0={c0}, c1={c1}")

test_polynomial_init_empty.input_description = "Polynomial()"


def test_polynomial_str(module):
    """Test Polynomial __str__ returns a string."""
    p = module.Polynomial({2: 5})
    result = str(p)

    if not isinstance(result, str):
        return make_test_result(False, "str", type(result).__name__)
    return make_test_result(True, "str", "str")

test_polynomial_str.input_description = "str(Polynomial)"


def test_polynomial_eq(module):
    """Test Polynomial __eq__ for equal polynomials."""
    p1 = module.Polynomial({2: 5, 1: 3})
    p2 = module.Polynomial({2: 5, 1: 3})

    if p1 == p2:
        return make_test_result(True, "equal", "equal")
    return make_test_result(False, "equal", "not equal")

test_polynomial_eq.input_description = "p1 == p2 (same)"


def test_polynomial_ne(module):
    """Test Polynomial __ne__ for different polynomials."""
    p1 = module.Polynomial({2: 5})
    p2 = module.Polynomial({2: 3})

    if p1 != p2:
        return make_test_result(True, "not equal", "not equal")
    return make_test_result(False, "not equal", "equal")

test_polynomial_ne.input_description = "p1 != p2 (different)"


def test_polynomial_get_terms(module):
    """Test Polynomial get_terms returns dictionary."""
    p = module.Polynomial({2: 5, 0: 1})
    terms = p.get_terms()

    if not isinstance(terms, dict):
        return make_test_result(False, "dict", type(terms).__name__)
    if terms.get(2) != 5 or terms.get(0) != 1:
        return make_test_result(False, "{2: 5, 0: 1}", str(terms))
    return make_test_result(True, "correct terms", "correct terms")

test_polynomial_get_terms.input_description = "get_terms()"


def test_polynomial_get_degree_zero(module):
    """Test Polynomial get_degree on zero polynomial."""
    p = module.Polynomial()

    degree = p.get_degree()
    expected = 0

    return make_test_result(degree == expected, expected, degree)

test_polynomial_get_degree_zero.input_description = "zero polynomial"


def test_polynomial_get_coefficient_missing(module):
    """Test get_coefficient returns 0 for missing power."""
    p = module.Polynomial({2: 5})

    c3 = p.get_coefficient(3)
    expected = 0

    return make_test_result(c3 == expected, expected, c3)

test_polynomial_get_coefficient_missing.input_description = "get_coefficient(3) on 5x^2"


# ============================================================================
# Add Term Tests
# ============================================================================

def test_add_term_to_empty(module):
    """Test add_term to empty polynomial."""
    p = module.Polynomial()
    result = p.add_term(5, 3)  # 5x^3

    coeff = result.get_coefficient(3)
    expected = 5

    return make_test_result(coeff == expected, expected, coeff)

test_add_term_to_empty.input_description = "empty + 5x^3"


def test_add_term_new_power(module):
    """Test add_term with new power."""
    p = module.Polynomial({2: 3})  # 3x^2
    result = p.add_term(5, 4)  # + 5x^4

    # Should have both terms
    c2 = result.get_coefficient(2)
    c4 = result.get_coefficient(4)

    if c2 != 3 or c4 != 5:
        return make_test_result(False, "3x^2 + 5x^4", f"{c2}x^2 + {c4}x^4")
    return make_test_result(True, "3x^2 + 5x^4", "3x^2 + 5x^4")

test_add_term_new_power.input_description = "3x^2 + 5x^4"


def test_add_term_zero_coefficient(module):
    """Test add_term with zero coefficient (no change)."""
    p = module.Polynomial({2: 5})  # 5x^2
    result = p.add_term(0, 3)  # + 0x^3

    c3 = result.get_coefficient(3)
    c2 = result.get_coefficient(2)

    # Should still have 5x^2, and x^3 coefficient should be 0
    if c2 == 5 and c3 == 0:
        return make_test_result(True, "unchanged", "unchanged")
    return make_test_result(False, "unchanged", f"{c2}x^2 + {c3}x^3")

test_add_term_zero_coefficient.input_description = "5x^2 + 0x^3"


def test_add_term_power_zero(module):
    """Test add_term with power 0 (constant)."""
    p = module.Polynomial({1: 2})  # 2x
    result = p.add_term(7, 0)  # + 7

    c1 = result.get_coefficient(1)
    c0 = result.get_coefficient(0)

    if c1 == 2 and c0 == 7:
        return make_test_result(True, "2x + 7", f"{c1}x + {c0}")
    return make_test_result(False, "2x + 7", f"{c1}x + {c0}")

test_add_term_power_zero.input_description = "2x + 7"


# ============================================================================
# Subtract Term Tests
# ============================================================================

def test_subtract_term_new_power(module):
    """Test subtract_term with new power."""
    p = module.Polynomial({2: 3})  # 3x^2
    result = p.subtract_term(5, 4)  # - 5x^4

    c4 = result.get_coefficient(4)
    # In Z256, subtract is same as add (XOR)
    expected = z256.sub(0, 5)  # 5

    return make_test_result(c4 == expected, expected, c4)

test_subtract_term_new_power.input_description = "3x^2 - 5x^4"


def test_subtract_term_same_coefficient(module):
    """Test subtract_term with same coefficient (should give 0)."""
    p = module.Polynomial({2: 7})  # 7x^2
    result = p.subtract_term(7, 2)  # - 7x^2

    coeff = result.get_coefficient(2)
    expected = 0  # 7 XOR 7 = 0

    return make_test_result(coeff == expected, expected, coeff)

test_subtract_term_same_coefficient.input_description = "7x^2 - 7x^2"


# ============================================================================
# Multiply by Term Tests
# ============================================================================

def test_multiply_by_term_zero_coeff(module):
    """Test multiply_by_term with zero coefficient."""
    p = module.Polynomial({2: 5, 1: 3})  # 5x^2 + 3x
    result = p.multiply_by_term(0, 1)  # * 0x

    # Result should be zero polynomial
    c0 = result.get_coefficient(0)
    c1 = result.get_coefficient(1)
    c2 = result.get_coefficient(2)
    c3 = result.get_coefficient(3)

    if c0 == 0 and c1 == 0 and c2 == 0 and c3 == 0:
        return make_test_result(True, "zero polynomial", "zero polynomial")
    return make_test_result(False, "zero polynomial", f"nonzero: {c0},{c1},{c2},{c3}")

test_multiply_by_term_zero_coeff.input_description = "(5x^2 + 3x) * 0x"


def test_multiply_by_term_single(module):
    """Test multiply_by_term on single term."""
    p = module.Polynomial({2: 5})  # 5x^2
    result = p.multiply_by_term(3, 1)  # * 3x = 15x^3 in Z256

    c3 = result.get_coefficient(3)
    expected = z256.mul(5, 3)  # 5*3 in Z256

    return make_test_result(c3 == expected, expected, c3)

test_multiply_by_term_single.input_description = "5x^2 * 3x"


def test_multiply_by_term_power_zero(module):
    """Test multiply_by_term with power 0 (multiply by constant)."""
    p = module.Polynomial({2: 5, 1: 3})  # 5x^2 + 3x
    result = p.multiply_by_term(2, 0)  # * 2

    c2 = result.get_coefficient(2)
    c1 = result.get_coefficient(1)

    exp_c2 = z256.mul(5, 2)
    exp_c1 = z256.mul(3, 2)

    if c2 == exp_c2 and c1 == exp_c1:
        return make_test_result(True, f"{exp_c2}x^2 + {exp_c1}x", f"{c2}x^2 + {c1}x")
    return make_test_result(False, f"{exp_c2}x^2 + {exp_c1}x", f"{c2}x^2 + {c1}x")

test_multiply_by_term_power_zero.input_description = "(5x^2 + 3x) * 2"


def test_multiply_by_term_one(module):
    """Test multiply_by_term by 1 (identity)."""
    p = module.Polynomial({2: 5, 0: 3})  # 5x^2 + 3
    result = p.multiply_by_term(1, 0)  # * 1

    c2 = result.get_coefficient(2)
    c0 = result.get_coefficient(0)

    if c2 == 5 and c0 == 3:
        return make_test_result(True, "unchanged", "unchanged")
    return make_test_result(False, "unchanged", f"{c2}x^2 + {c0}")

test_multiply_by_term_one.input_description = "(5x^2 + 3) * 1"


# ============================================================================
# Add Polynomial Tests
# ============================================================================

def test_add_polynomial_to_empty(module):
    """Test add_polynomial to empty polynomial."""
    p1 = module.Polynomial()
    p2 = module.Polynomial({2: 5, 1: 3})

    result = p1.add_polynomial(p2)

    c2 = result.get_coefficient(2)
    c1 = result.get_coefficient(1)

    if c2 == 5 and c1 == 3:
        return make_test_result(True, "5x^2 + 3x", "5x^2 + 3x")
    return make_test_result(False, "5x^2 + 3x", f"{c2}x^2 + {c1}x")

test_add_polynomial_to_empty.input_description = "0 + (5x^2 + 3x)"


def test_add_polynomial_disjoint(module):
    """Test add_polynomial with disjoint powers."""
    p1 = module.Polynomial({3: 2})  # 2x^3
    p2 = module.Polynomial({1: 5})  # 5x

    result = p1.add_polynomial(p2)

    c3 = result.get_coefficient(3)
    c1 = result.get_coefficient(1)

    if c3 == 2 and c1 == 5:
        return make_test_result(True, "2x^3 + 5x", "2x^3 + 5x")
    return make_test_result(False, "2x^3 + 5x", f"{c3}x^3 + {c1}x")

test_add_polynomial_disjoint.input_description = "2x^3 + 5x"


def test_add_polynomial_to_itself(module):
    """Test add_polynomial to itself (doubles in Z256)."""
    p = module.Polynomial({1: 5})  # 5x
    result = p.add_polynomial(p)

    c1 = result.get_coefficient(1)
    # In Z256: 5 + 5 = 5 XOR 5 = 0
    expected = z256.add(5, 5)

    return make_test_result(c1 == expected, expected, c1)

test_add_polynomial_to_itself.input_description = "5x + 5x (Z256)"


# ============================================================================
# Subtract Polynomial Tests
# ============================================================================

def test_subtract_polynomial_different(module):
    """Test subtract_polynomial with different polynomials."""
    p1 = module.Polynomial({2: 10})
    p2 = module.Polynomial({2: 3})

    result = p1.subtract_polynomial(p2)

    c2 = result.get_coefficient(2)
    expected = z256.sub(10, 3)  # 9

    return make_test_result(c2 == expected, expected, c2)

test_subtract_polynomial_different.input_description = "10x^2 - 3x^2"


def test_subtract_polynomial_z256(module):
    """Test subtract_polynomial uses Z256 arithmetic."""
    p1 = module.Polynomial({1: 100})
    p2 = module.Polynomial({1: 50})

    result = p1.subtract_polynomial(p2)

    c1 = result.get_coefficient(1)
    expected = z256.sub(100, 50)  # 100 XOR 50 = 86

    return make_test_result(c1 == expected, expected, c1)

test_subtract_polynomial_z256.input_description = "100x - 50x (Z256)"


# ============================================================================
# Multiply by Polynomial Tests
# ============================================================================

def test_multiply_polynomial_zero(module):
    """Test multiply_by_polynomial with zero polynomial."""
    p1 = module.Polynomial({2: 5})
    p2 = module.Polynomial()  # Zero polynomial

    result = p1.multiply_by_polynomial(p2)

    # Should be zero
    c2 = result.get_coefficient(2)
    return make_test_result(c2 == 0, 0, c2)

test_multiply_polynomial_zero.input_description = "5x^2 * 0"


def test_multiply_polynomial_single(module):
    """Test multiply_by_polynomial with single term."""
    p1 = module.Polynomial({1: 2})  # 2x
    p2 = module.Polynomial({1: 3})  # 3x

    result = p1.multiply_by_polynomial(p2)

    # 2x * 3x = 6x^2 in Z256
    c2 = result.get_coefficient(2)
    expected = z256.mul(2, 3)

    return make_test_result(c2 == expected, expected, c2)

test_multiply_polynomial_single.input_description = "2x * 3x"


def test_multiply_polynomial_z256(module):
    """Test multiply_by_polynomial uses Z256 multiplication."""
    p1 = module.Polynomial({0: 2})  # 2
    p2 = module.Polynomial({0: 3})  # 3

    result = p1.multiply_by_polynomial(p2)

    c0 = result.get_coefficient(0)
    expected = z256.mul(2, 3)

    return make_test_result(c0 == expected, expected, c0)

test_multiply_polynomial_z256.input_description = "2 * 3 (Z256)"


# ============================================================================
# Remainder Tests
# ============================================================================

def test_remainder_smaller_degree(module):
    """Test remainder when numerator has smaller degree."""
    p1 = module.Polynomial({1: 5})  # 5x (degree 1)
    p2 = module.Polynomial({2: 1})  # x^2 (degree 2)

    result = p1.remainder(p2)

    # Remainder should be p1 itself (5x)
    c1 = result.get_coefficient(1)
    return make_test_result(c1 == 5, 5, c1)

test_remainder_smaller_degree.input_description = "5x mod x^2"


def test_remainder_same_degree(module):
    """Test remainder when polynomials have same degree."""
    p1 = module.Polynomial({2: 6})  # 6x^2
    p2 = module.Polynomial({2: 2})  # 2x^2

    result = p1.remainder(p2)

    # 6x^2 / 2x^2 = 3 with remainder 0 (in Z256)
    c2 = result.get_coefficient(2)
    c1 = result.get_coefficient(1)
    c0 = result.get_coefficient(0)

    # After division, remainder should be zero polynomial
    return make_test_result(c2 == 0 and c1 == 0 and c0 == 0, "zero", f"{c2}x^2 + {c1}x + {c0}")

test_remainder_same_degree.input_description = "6x^2 mod 2x^2"


def test_remainder_exact_division(module):
    """Test remainder with exact division."""
    p1 = module.Polynomial({2: 1})  # x^2
    p2 = module.Polynomial({1: 1})  # x

    result = p1.remainder(p2)

    # x^2 / x = x with remainder 0
    c2 = result.get_coefficient(2)
    c1 = result.get_coefficient(1)
    c0 = result.get_coefficient(0)

    return make_test_result(c2 == 0 and c1 == 0 and c0 == 0, "zero", f"{c2}x^2 + {c1}x + {c0}")

test_remainder_exact_division.input_description = "x^2 mod x"


def test_remainder_z256(module):
    """Test remainder uses Z256 arithmetic."""
    p1 = module.Polynomial({2: 10, 1: 5, 0: 3})
    p2 = module.Polynomial({1: 2, 0: 1})

    result = p1.remainder(p2)

    # Just check that result is a polynomial with degree < 1
    if result.get_degree() < p2.get_degree():
        return make_test_result(True, "degree < divisor", "degree < divisor")
    return make_test_result(False, "degree < divisor", f"degree = {result.get_degree()}")

test_remainder_z256.input_description = "polynomial division in Z256"


# ============================================================================
# Create Message Polynomial Tests
# ============================================================================

def test_create_message_polynomial_single(module):
    """Test create_message_polynomial with single byte."""
    result = module.create_message_polynomial([65], 2)  # 'A' with 2 correction bytes

    if not isinstance(result, module.Polynomial):
        return make_test_result(False, "Polynomial", type(result).__name__)

    # Message [65] with k=2 should be 65*x^(1+2-1) = 65*x^2
    c2 = result.get_coefficient(2)
    return make_test_result(c2 == 65, 65, c2)

test_create_message_polynomial_single.input_description = "[65], k=2"


def test_create_message_polynomial_degree(module):
    """Test create_message_polynomial has correct degree."""
    message = [10, 20, 30, 40]  # 4 bytes
    k = 3  # 3 correction bytes
    result = module.create_message_polynomial(message, k)

    # Degree should be n + k - 1 = 4 + 3 - 1 = 6
    expected_degree = 6
    actual_degree = result.get_degree()

    return make_test_result(actual_degree == expected_degree, expected_degree, actual_degree)

test_create_message_polynomial_degree.input_description = "[10,20,30,40], k=3"


def test_create_message_polynomial_empty(module):
    """Test create_message_polynomial with empty message."""
    result = module.create_message_polynomial([], 2)

    if not isinstance(result, module.Polynomial):
        return make_test_result(False, "Polynomial", type(result).__name__)

    # Empty message should give zero polynomial
    degree = result.get_degree()
    return make_test_result(degree == 0, "degree 0", f"degree {degree}")

test_create_message_polynomial_empty.input_description = "[], k=2"


# ============================================================================
# Create Generator Polynomial Tests
# ============================================================================

def test_create_generator_polynomial_k1(module):
    """Test create_generator_polynomial with k=1."""
    result = module.create_generator_polynomial(1)

    if not isinstance(result, module.Polynomial):
        return make_test_result(False, "Polynomial", type(result).__name__)

    # g(x) = (x - 2^0) = (x - 1) = x + 1 (in Z256, subtraction is addition)
    c1 = result.get_coefficient(1)
    c0 = result.get_coefficient(0)

    if c1 == 1 and c0 == 1:
        return make_test_result(True, "x + 1", f"{c1}x + {c0}")
    return make_test_result(False, "x + 1", f"{c1}x + {c0}")

test_create_generator_polynomial_k1.input_description = "k=1"


def test_create_generator_polynomial_degree(module):
    """Test create_generator_polynomial has correct degree."""
    k = 4
    result = module.create_generator_polynomial(k)

    # Generator polynomial for k correction bytes has degree k
    expected_degree = k
    actual_degree = result.get_degree()

    return make_test_result(actual_degree == expected_degree, expected_degree, actual_degree)

test_create_generator_polynomial_degree.input_description = "k=4"


def test_create_generator_polynomial_k3(module):
    """Test create_generator_polynomial with k=3."""
    result = module.create_generator_polynomial(3)

    degree = result.get_degree()
    if degree != 3:
        return make_test_result(False, "degree 3", f"degree {degree}")
    return make_test_result(True, "degree 3", "degree 3")

test_create_generator_polynomial_k3.input_description = "k=3"


# ============================================================================
# Reed-Solomon Correction Tests
# ============================================================================

def test_reed_solomon_returns_polynomial(module):
    """Test that reed_solomon_correction returns a Polynomial."""
    result = module.reed_solomon_correction([1, 2, 3], 2)

    if not isinstance(result, module.Polynomial):
        return make_test_result(False, "Polynomial", type(result).__name__)
    return make_test_result(True, "Polynomial", "Polynomial")

test_reed_solomon_returns_polynomial.input_description = "[1,2,3], k=2"


def test_reed_solomon_k1(module):
    """Test reed_solomon_correction with k=1."""
    result = module.reed_solomon_correction([1, 2, 3], 1)

    if not isinstance(result, module.Polynomial):
        return make_test_result(False, "Polynomial", type(result).__name__)

    # With k=1, result should have degree 0 (single byte)
    degree = result.get_degree()
    return make_test_result(degree == 0, "degree 0", f"degree {degree}")

test_reed_solomon_k1.input_description = "[1,2,3], k=1"


def test_reed_solomon_coefficients_in_range(module):
    """Test reed_solomon_correction coefficients are in Z256 range."""
    result = module.reed_solomon_correction([65, 66, 67, 68], 4)  # "ABCD"

    terms = result.get_terms()
    for power, coeff in terms.items():
        if not (0 <= coeff <= 255):
            return make_test_result(False, "0-255", f"coeff {coeff} at power {power}")

    return make_test_result(True, "all in 0-255", "all in 0-255")

test_reed_solomon_coefficients_in_range.input_description = "[65,66,67,68], k=4"
