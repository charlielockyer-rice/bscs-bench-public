"""
QR Code Generator - Reference Solution

This module implements Reed-Solomon error correction using polynomial
arithmetic over the finite field Z256.
"""

import sys
import os

# Add lib directory to path for imports
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'lib'))

import comp140_module5_z256 as z256


def divide_terms(coefficient1, power1, coefficient2, power2):
    """
    Computes the quotient of two terms.

    The degree of the first term, power1, must be greater than or
    equal to the degree of the second term, power2.

    Inputs:
        - coefficient1: a Z_256 number representing the coefficient of the first polynomial term
        - power1: an integer representing the power of the first term.
        - coefficient2: a Z_256 number representing the coefficient of the second polynomial term
        - power2: an integer representing the power of the second term.

    Returns: an instance of a Polynomial that is the resulting
    term.
    """
    # From recipe: (a*x^b) / (c*x^d) = (a/c) * x^(b-d)
    new_coeff = z256.div(coefficient1, coefficient2)
    new_pow = power1 - power2

    # Represent our answer as a Polynomial
    divided = Polynomial()
    divided = divided.add_term(new_coeff, new_pow)
    return divided


class Polynomial:
    """
    A class used to abstract methods on a polynomial in the finite
    field Z_256 (including numbers from 0 through 255).

    Since 256 is not prime, but is rather of the form p^n = 2^8, this
    representation uses special arithmetic via the z256 module so as to
    preserve multiplicative inverses (division) inside this field.
    """

    def __init__(self, terms=None):
        """
        Creates a new Polynomial object.  If a dictionary of terms is provided,
        they will be the terms of the polynomial,
        otherwise the polynomial will be the 0 polynomial.

        inputs:
            - terms: a dictionary of terms mapping powers to coefficients or None
              (None indicates that all coefficients are 0)
        """
        if terms is not None:
            self._terms = dict(terms)
        else:
            self._terms = {}

    def __str__(self):
        """
        Returns: a string representation of the polynomial, containing the
        class name and all of the terms.
        """
        # Create a string of the form "ax^n + bx^n-1 + ... + c" by
        # creating a string representation of each term, and inserting
        # " + " in between each
        term_strings = []

        # Add the highest powers first
        powers = list(self._terms.keys())
        powers.sort(reverse=True)
        for power in powers:
            coefficient = self._terms[power]
            # Don't print out terms with a zero coefficient
            if coefficient != 0:
                # Don't print "x^0"; that just means it's a constant
                if power == 0:
                    term_strings.append("%d" % coefficient)
                else:
                    term_strings.append("%d*x^%d" % (coefficient, power))

        terms_str = " + ".join(term_strings)
        if terms_str == "":
            terms_str = "0"
        return "Polynomial: %s" % terms_str

    def __repr__(self):
        """
        Returns: a string representation suitable for debugging.
        """
        return self.__str__()

    def __eq__(self, other_polynomial):
        """
        Check if another polynomial is equivalent

        inputs:
            - other_polynomial: a Polynomial object

        Returns a boolean: True if other_polynomial contains
        the same terms as self, False otherwise.
        """
        # Make sure that other_polynomial is a Polynomial
        if not isinstance(other_polynomial, Polynomial):
            return False

        # Get the terms of the other_polynomial
        terms = other_polynomial.get_terms()

        # Check that all terms in other_polynomial appear in self
        for power, coefficient in terms.items():
            if coefficient != 0:
                if power not in self._terms:
                    return False
                if self._terms[power] != coefficient:
                    return False

        # Check that all terms in self appear in other_polynomial
        for power, coefficient in self._terms.items():
            if coefficient != 0:
                if power not in terms:
                    return False
                if terms[power] != coefficient:
                    return False

        return True

    def __ne__(self, other_polynomial):
        """
        Check if another polynomial is NOT equivalent

        inputs:
            - other_polynomial: a Polynomial object

        Return a boolean: False if other_polynomial contains the same terms
        as self, True otherwise.
        """
        return not self.__eq__(other_polynomial)

    def get_terms(self):
        """
        Returns: a dictionary of terms, mapping powers to coefficients.
        This dictionary is a completely new object and is not a reference
        to any internal structures.
        """
        terms = dict(self._terms)
        return terms

    def get_degree(self):
        """
        Returns: the maximum power over all terms in this polynomial.
        """
        # Since we don't clean zero-coefficient powers out of our dictionary,
        # we need a trickier get_degree function, to take into account that
        # some coefficients could be zero.
        highest_power = 0
        for power in self._terms:
            if (power > highest_power) and (self._terms[power] != 0):
                highest_power = power

        return highest_power

    def get_coefficient(self, power):
        """
        Determines the coefficient of x^(power) in this polynomial.
        If there is no coefficient of x^(power), this method
        returns 0.

        inputs:
            - power: an integer representing a polynomial power

        Returns: a Z_256 number that is the coefficient or 0 if there
                 is no term of the given power
        """
        if power in self._terms:
            return self._terms[power]
        else:
            return 0

    def add_term(self, coefficient, power):
        """
        Add one term to this polynomial.

        inputs:
            - coefficient: a Z_256 number representing the coefficient of the term
            - power: an integer representing the power of the term

        Returns: a new Polynomial that is the sum of adding this polynomial
        to (coefficient) * x^(power) using Z_256 arithmetic to add
        coefficients, if necessary.
        """
        # Create a copy of the current terms
        new_terms = dict(self._terms)

        # Get the existing coefficient for this power (0 if not present)
        existing_coeff = new_terms.get(power, 0)

        # Add the coefficients using Z256 arithmetic
        new_coeff = z256.add(existing_coeff, coefficient)

        # Store the new coefficient
        new_terms[power] = new_coeff

        return Polynomial(new_terms)

    def subtract_term(self, coefficient, power):
        """
        Subtract one term from this polynomial.

        inputs:
            - coefficient: a Z_256 number representing the coefficient of the term
            - power: an integer representing the power of the term

        Returns: a new Polynomial that is the difference of this polynomial
        and (coefficient) * x^(power) using Z_256 arithmetic to subtract
        coefficients, if necessary.
        """
        # In Z256, subtraction is the same as addition (XOR)
        # sub(a, b) = add(a, b) in this field
        return self.add_term(coefficient, power)

    def multiply_by_term(self, coefficient, power):
        """
        Multiply this polynomial by one term.

        inputs:
            - coefficient: a Z_256 number representing the coefficient of the term
            - power: an integer representing the power of the term

        Returns: a new Polynomial that is the product of multiplying
        this polynomial by (coefficient) * x^(power).
        """
        # Start with a zero polynomial
        result = Polynomial()

        # For each term in the current polynomial, multiply it by the input term
        for term_power, term_coeff in self._terms.items():
            # Skip zero coefficients
            if term_coeff == 0:
                continue

            # Multiply coefficients using Z256 arithmetic
            new_coeff = z256.mul(term_coeff, coefficient)

            # Add powers using regular integer arithmetic
            new_power = term_power + power

            # Add this term to the result
            result = result.add_term(new_coeff, new_power)

        return result

    def add_polynomial(self, other_polynomial):
        """
        Compute the sum of the current polynomial other_polynomial.

        inputs:
            - other_polynomial: a Polynomial object

        Returns: a new Polynomial that is the sum of both polynomials.
        """
        # Start with a copy of self
        result = Polynomial(self._terms)

        # Add each term from other_polynomial
        for power, coefficient in other_polynomial.get_terms().items():
            result = result.add_term(coefficient, power)

        return result

    def subtract_polynomial(self, other_polynomial):
        """
        Compute the difference of the current polynomial and other_polynomial.

        inputs:
            - other_polynomial: a Polynomial object

        Returns: a new Polynomial that is the difference of both polynomials.
        """
        # Start with a copy of self
        result = Polynomial(self._terms)

        # Subtract each term from other_polynomial
        for power, coefficient in other_polynomial.get_terms().items():
            result = result.subtract_term(coefficient, power)

        return result

    def multiply_by_polynomial(self, other_polynomial):
        """
        Compute the product of the current polynomial and other_polynomial.

        inputs:
            - other_polynomial: a Polynomial object

        Returns: a new Polynomial that is the product of both polynomials.
        """
        # Start with a zero polynomial
        result = Polynomial()

        # For each term in other_polynomial, multiply self by that term
        # and add to the result
        for power, coefficient in other_polynomial.get_terms().items():
            # Skip zero coefficients
            if coefficient == 0:
                continue

            # Multiply self by this term
            term_product = self.multiply_by_term(coefficient, power)

            # Add to the accumulated result
            result = result.add_polynomial(term_product)

        return result

    def remainder(self, denominator):
        """
        Compute a new Polynomial that is the remainder after dividing this
        polynomial by denominator.

        Note: does *not* return the quotient; only the remainder!

        inputs:
            - denominator: a Polynomial object

        Returns: a new polynomial that is the remainder
        """
        # Start with the numerator (self)
        remaining = Polynomial(self._terms)

        # Get the degree and leading coefficient of the denominator
        denom_degree = denominator.get_degree()
        denom_leading_coeff = denominator.get_coefficient(denom_degree)

        # Get the initial degree and coefficient of the remaining polynomial
        remaining_degree = remaining.get_degree()
        remaining_leading_coeff = remaining.get_coefficient(remaining_degree)

        # Repeatedly subtract multiples of denominator from remaining
        while remaining_degree >= denom_degree and remaining_leading_coeff != 0:
            # Compute the term to multiply denominator by
            # This is (leading_term of remaining) / (leading_term of denominator)
            factor = divide_terms(
                remaining_leading_coeff, remaining_degree,
                denom_leading_coeff, denom_degree
            )

            # Multiply denominator by this factor
            subtract_poly = denominator.multiply_by_polynomial(factor)

            # Subtract from remaining
            remaining = remaining.subtract_polynomial(subtract_poly)

            # Update degree and leading coefficient
            remaining_degree = remaining.get_degree()
            remaining_leading_coeff = remaining.get_coefficient(remaining_degree)

        return remaining


def create_message_polynomial(message, num_correction_bytes):
    """
    Creates the appropriate Polynomial to represent the
    given message. Relies on the number of error correction
    bytes (k). The message polynomial is of the form
    message[i]*x^(n+k-i-1) for each number/byte in the message.

    Inputs:
        - message: a list of integers (each between 0-255) representing data
        - num_correction_bytes: an integer representing the number of
          error correction bytes to use

    Returns: a Polynomial with the appropriate terms to represent the
    message with the specified level of error correction.
    """
    # message = sum over i from 0 to n-1 of: m_i * x^(k + n - i - 1)
    # where n is the length of the message and k is num_correction_bytes
    n = len(message)
    k = num_correction_bytes

    result = Polynomial()

    for i in range(n):
        coefficient = message[i]
        power = k + n - i - 1
        result = result.add_term(coefficient, power)

    return result


def create_generator_polynomial(num_correction_bytes):
    """
    Generates a static generator Polynomial for error
    correction, which is the product of (x-2^i) for all i in the
    set {0, 1, ..., num_correction_bytes - 1}.

    Inputs:
        - num_correction_bytes: desired number of error correction bytes.
                                In the formula, this is represented as k.

    Returns: generator Polynomial for generating Reed-Solomon encoding data.
    """
    # generator = product over i from 0 to k-1 of: (x - 2^i)
    # Note: In Z256, -2^i is the same as 2^i (since subtraction is XOR)
    # So (x - 2^i) in Z256 is represented as x + 2^i which is: {1: 1, 0: 2^i}
    k = num_correction_bytes

    # Start with the polynomial 1 (multiplicative identity)
    result = Polynomial({0: 1})

    for i in range(k):
        # Compute 2^i in Z256
        power_of_two = z256.power(2, i)

        # Create the factor polynomial (x - 2^i)
        # In Z256, this is x + 2^i since subtraction equals addition
        # Represented as: 1*x^1 + (2^i)*x^0
        factor = Polynomial({1: 1, 0: power_of_two})

        # Multiply the result by this factor
        result = result.multiply_by_polynomial(factor)

    return result


def reed_solomon_correction(encoded_data, num_correction_bytes):
    """
    Corrects the encoded data using Reed-Solomon error correction

    Inputs:
        - encoded_data: a list of integers (each between 0-255)
                        representing an encoded QR message.
        - num_correction_bytes: desired number of error correction bytes.

    Returns: a polynomial that represents the Reed-Solomon error
    correction code for the input data.
    """
    # Create the message polynomial from the encoded data
    message_poly = create_message_polynomial(encoded_data, num_correction_bytes)

    # Create the generator polynomial
    generator_poly = create_generator_polynomial(num_correction_bytes)

    # Compute and return the remainder
    return message_poly.remainder(generator_poly)


# Test with the example from the Reed-Solomon example document
if __name__ == "__main__":
    # Test case from the documentation:
    # Message: [104, 105, 33, 33] (which is "hi!!" in ASCII)
    # k = 2 correction bytes
    # Expected remainder: 69x + 68

    print("Testing Reed-Solomon implementation with example from documentation...")
    print()

    message = [104, 105, 33, 33]
    k = 2

    # Create message polynomial
    msg_poly = create_message_polynomial(message, k)
    print(f"Message polynomial: {msg_poly}")
    # Expected: 104x^5 + 105x^4 + 33x^3 + 33x^2

    # Create generator polynomial
    gen_poly = create_generator_polynomial(k)
    print(f"Generator polynomial: {gen_poly}")
    # Expected: x^2 + 3x + 2

    # Compute remainder
    remainder = reed_solomon_correction(message, k)
    print(f"Remainder polynomial: {remainder}")
    # Expected: 69x + 68

    # Verify the result
    expected_remainder = Polynomial({1: 69, 0: 68})
    if remainder == expected_remainder:
        print()
        print("SUCCESS: Remainder matches expected value!")
    else:
        print()
        print("FAILURE: Remainder does not match expected value!")
        print(f"Expected: {expected_remainder}")
        print(f"Got: {remainder}")

    print()
    print("Additional verification of polynomial operations...")

    # Test add_term
    p = Polynomial()
    p = p.add_term(5, 2)  # 5x^2
    p = p.add_term(3, 1)  # 5x^2 + 3x
    print(f"5x^2 + 3x = {p}")

    # Test multiply_by_term
    p2 = p.multiply_by_term(2, 1)  # (5x^2 + 3x) * 2x
    print(f"(5x^2 + 3x) * 2x = {p2}")

    # Test multiply_by_polynomial
    # (x + 1) * (x + 2) in Z256
    factor1 = Polynomial({1: 1, 0: 1})  # x + 1
    factor2 = Polynomial({1: 1, 0: 2})  # x + 2
    product = factor1.multiply_by_polynomial(factor2)
    print(f"(x + 1) * (x + 2) = {product}")
    # In Z256: x^2 + 2x + x + 2 = x^2 + 3x + 2 (since 2 XOR 1 = 3)
