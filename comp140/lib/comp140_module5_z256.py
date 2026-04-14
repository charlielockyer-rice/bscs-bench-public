"""
Implements arithmetic operations in the finite field Z256.
"""

def _check_valid(num):
    """
    Check that num is a valid Z256 number.
    """
    if (type(num) != int) or (num < 0) or (num > 255):
        raise TypeError(str(num) + " is not a valid number in Z256")

def add(num_one, num_two):
    """
    Adds two numbers in Z256. Addition in this space is equivalent to
    subtraction, since numbers are treated as binary vectors, and addition
    is done pairwise-mod-2 per "element" (which, in this case is, a bit).
    """
    _check_valid(num_one)
    _check_valid(num_two)
    return num_one ^ num_two

def sub(num_one, num_two):
    """
    Subtracts two numbers in Z256. Subtraction in this space is equivalent
    to addition, since numbers are treated as binary vectors, and subtraction
    is done pairwise-mod-2 per "element" (which, in this case, is a bit).
    """
    _check_valid(num_one)
    _check_valid(num_two)
    return add(num_one, num_two)

def mul(num_one, num_two):
    """
    Returns the product of two numbers in Z256. This should be zero if either
    number is zero; otherwise, it should find a and b such that num_one = 2^a
    and num_two = 2^b, add the exponents a and b to get n, and return the
    number represented by 2^n in Z256.
    """
    _check_valid(num_one)
    _check_valid(num_two)
    # Zero does not have an associated power of two, so we need to
    # have a special case for it
    if num_one == 0 or num_two == 0:
        return 0
    # Compute num_one = 2^a, num_two = 2^b
    power1 = NUM_TO_POWER[num_one]
    power2 = NUM_TO_POWER[num_two]
    # Find 2^(a + b % 255)
    result_power = (power1 + power2) % 255
    return POWER_TO_NUM[result_power]

def div(numerator, denominator):
    """
    Divides two numbers in Z256. This should be zero if the numerator
    is zero, throw an error if the denominator is zero, and otherwise
    compute numerator = 2^a, denominator = 2^b, and return
    2^(a - b % 255).
    """
    _check_valid(numerator)
    _check_valid(denominator)
    # We cannot divide nor divide by zero
    if denominator == 0:
        raise ZeroDivisionError("z256.div(): Denominator is zero")
    elif numerator == 0:
        return 0
    # Division is the same as multiplication, but subtracting powers
    power1 = NUM_TO_POWER[numerator]
    power2 = NUM_TO_POWER[denominator]
    result_power = (power1 - power2) % 255
    return POWER_TO_NUM[result_power]

def power(base, exponent):
    """
    Raises base to the exponent power in Z256. This results in a number
    between 1 and 255, unless base is 0.
    """
    _check_valid(base)
    if base == 0:
        return 0
    # Find z256_n such that 2 ^ z256_n = base
    z256_n = NUM_TO_POWER[base]
    # Calculate base ^ exponent = 2 ^ (z256_n * exponent)
    result_power = (z256_n * exponent) % 255
    return POWER_TO_NUM[result_power]

def _generate_z256_mappings():
    """
    Generates two mappings of all of the numbers in Z256 of the form 2^a = x;
    one mapping is of the form {x: a}, and the other is of the form {a: x}.
    Returns both mappings so that you can convert back and forth. These mappings
    only need to be constructed once, because they don't change over time.
    """
    power_to_num = {}
    num_to_power = {}
    num_to_power[1] = 0 # 2^0 = 1
    power_to_num[0] = 1
    for pow1 in range(1, 255):
        # Generate the next number in the sequence
        result = power_to_num[pow1 - 1] * 2
        if result >= 256:
            result ^= 285
        # Map the current power to its number in the sequence
        power_to_num[pow1] = result
        num_to_power[result] = pow1
    return (power_to_num, num_to_power)

POWER_TO_NUM, NUM_TO_POWER = _generate_z256_mappings()
