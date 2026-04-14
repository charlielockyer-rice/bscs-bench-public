"""
Support code for generating QR codes.
"""

import simplegui
import math

# Specific version and error-correction level for this assignment
VERSION = 4
EC_LEVEL = 'M'

# Info for version 20, error-correction level M
MAX_DATA_BYTES = 64
BLOCK_SIZE = 32
EC_BYTES_PER_BLOCK = 18

# Provided functions for creating QR codes
def start(reed_solomon_fn):
    """
    Description: Initializes a QR-code generating GUI.
    Inputs:
    reed_solomon_fn -- A function that takes a list of bytestrings, and returns
                       a Polynomial representing the Reed-solomon
    """
    def encode_array_wrapper(info):
        """
        Wrapper to generate encoded array.
        """
        encoded = encode(info, MAX_DATA_BYTES)
        error_corrected = error_correct(encoded, reed_solomon_fn,
                                        BLOCK_SIZE, EC_BYTES_PER_BLOCK)
        return generate_array(error_corrected, VERSION, EC_LEVEL)
    frame = QRFrame(encode_array_wrapper)
    frame.start()

def error_correct(encoded_bytes, reed_solomon_fn,
                  block_size, num_correction_bytes):
    """
    Takes a list of bytes (length-eight strings containing '0's and
    '1's), the size of the blocks they should be split into, and the
    number of error-correction bytes per block.
    Returns a list of bytes with the same structure representing the
    error-corrected data, where the error-correction code is
    interleaved with the blocks of data.
    """
    # Split bytes into blocks
    blocks = []
    while encoded_bytes:
        blocks.append(encoded_bytes[:block_size])
        encoded_bytes = encoded_bytes[block_size:]
    # Get the error correction for each block
    error_correction_blocks = []
    for block in blocks:
        numerical_block = map(to_decimal_from_binary, block)
        reed_solomon_poly = reed_solomon_fn(list(numerical_block),
                                            num_correction_bytes)
        binary_coeffs = get_binary_coefficients(reed_solomon_poly,
                                                num_correction_bytes)
        error_correction_blocks.append(binary_coeffs)
    # Take the first byte from each block, then the second, etc. for
    # first data and then error correction blocks
    total_bytes = []
    for byte_index in range(BLOCK_SIZE):
        for block in blocks:
            total_bytes.append(block[byte_index])
    for byte_index in range(EC_BYTES_PER_BLOCK):
        for ec_block in error_correction_blocks:
            total_bytes.append(ec_block[byte_index])
    # Return a list of bytes to be turned into an array
    return total_bytes

def get_binary_coefficients(polynomial, num_coeffs_requested):
    """
    Given an instance of a Polynomial representing the polynomial
    a_n*x^n + ... + a_1*x + 0, and the number of coefficients
    k=num_coeffs_requested to return, returns a list containing all of the
    binary representations of the coefficients [a_k, a_k-1, ..., a_1, a_0].
    """
    binary_coefficients = []
    for idx in range(num_coeffs_requested - 1, -1, -1):
        coefficient = to_binary_from_decimal(polynomial.get_coefficient(idx))
        binary_coefficients.append(coefficient)
    return binary_coefficients

def to_binary_from_decimal(number):
    """
    Description: Takes a base ten number and converts it
                 to a binary string.
    Inputs:
    number -- a base 10 whole number (integer >= 0)
    Returns:
    A string representation of the binary version of the number.
    """
    # The builtin function bin() converts a number to a binary string, e.g.
    # bin(8) == '0b1000'. Since we just want the bits, we remove the first
    # two elements in the string.
    return pad(bin(number)[2:], 8)

def to_decimal_from_binary(binstr):
    """
    Description: Takes a binary number represented as a string
                 and converts it into a base 10 integer.
    Inputs:
    binstr -- a string representing a binary number
    Returns:
    A base 10 integer.
    """
    # The builtin int() function can take an optional radix;
    # if our input is in binary, that's a base 2 number.
    return int(binstr, 2)

def pad(string, length, char='0', side="left"):
    """
    Description: Pads a string to a desired length using
                 a given character (defaulted to '0') by
                 inserting the required number of characters
                 on a given side (defaulted to left).
    Note:        If len(s) > length, s is returned and no
                 error is thrown, although a message is printed.
    Inputs:
    string -- a string
    length -- an integer representing the desired length of the string
    char -- a string of length 0 to be used to increase the length of s
    side -- "left" or "right"
    Returns:
    The string increased to the desired length by adding
    the specified char to the specified side.
    """
    if length - len(string) < 0:
        print("\nError: invalid length\nBinaryHelper.pad: " + str(length) +
              " on " + string + ".\nOriginal string returned.\n")
    if side == "left":
        return string.rjust(length, char)
    elif side == "right":
        return string.ljust(length, char)
    else:
        print("\nError: invalid side\nBinaryHelper.pad: " + side + " on " +
              string + ".\nDefaulted to left.\n")
        return string.rjust(length, char)

def split_by_bytes(binstr):
    """
    Description: Takes a binary number represented as a string
                 and splits it into a list of strings of length
                 8 that represent bytes.
    Note:        len(s) % 8 == 0
    Inputs:
    binstr -- a string representing a binary number whose length is
              a multiple of 8
    Returns:
    A list of strings representing bytes.
    """
    if len(binstr) % 8 != 0:
        print("Error: BinaryHelper.split_by_bytes: length %d string (%s).\n"
              "The last item in the returned list was not a full byte.\n"
              % (len(binstr), binstr))
    ans = []
    index = 0
    # Iteratively strip off 8 bits (one byte) at a time
    while index < len(binstr):
        ans.append(binstr[index : index + 8])
        index += 8
    return ans

def _generate_valid_chars():
    """
    Generate the set of valid characters for a QR code in an 8-bit
    encoding scheme.
    """
    valid_chars = {}
    for val in range(130):
        if val not in set([92, 126, 128, 129]): # Invalid characters
            valid_chars[chr(val)] = val
    valid_chars[129] = 'INVALID'
    return valid_chars

VALID_CHARS = _generate_valid_chars()

def encode(info, max_bytes):
    """
    Description: Encodes a message string into a form suitable for QR codes.
    Inputs:
    info -- a string representing the message to encode
    Returns:
    A liste of bytes (length-8 strings of '0's and '1's) representing the QR
    encoding of the message, without any error correction.
    """
    # Convert from string to 8bit repr
    converted = info_to_8bit(info)
    # Prefix with the mode ('0100' for 8bit), and info length (padded to a byte,
    # for version-4 8bit info)
    converted = '0100' + to_binary_from_decimal(len(info)) + converted
    if len(converted) > max_bytes * 8:
        raise ValueError("\nError: Info '%s'\n  is too long"
              " (is %d bytes, can fit %d)"
              % (info, (len(converted) + 7) / 8, max_bytes))
    # Pad with '0000' to signal end-of-info (if there's room)
    converted += "0" * min(4, max_bytes * 8 - len(converted))
    # Pad current byte with 0s
    converted += "0" * (-len(converted) % 8)
    # Pad data size with specific bytes until necessary data len reached
    pads = ["11101100", "00010001"]
    cur_pad = 0
    while len(converted) < max_bytes * 8:
        converted += pads[cur_pad]
        cur_pad = 1 - cur_pad
    return split_by_bytes(converted)

def info_to_8bit(info):
    """
    Description: Converts a message into 8-bit binary representation.
    Inputs:
    info -- a string representing the message to encode
    Returns:
    A string of '0's and '1's representing the 8-bit encoding of the message.
    """
    # Convert to decimal representation
    decimal_values = []
    for char in info:
        if char in VALID_CHARS:
            decimal_values.append(VALID_CHARS[char])
        else:
            decimal_values.append(VALID_CHARS['INVALID'])
    # Convert decimal to byte strings
    byte_strings = []
    for num in decimal_values:
        byte_strings.append(to_binary_from_decimal(num))
    return ''.join(byte_strings)


############################################################
# Significance of array values
EMPTY = 2
BLACK = 1
WHITE = 0

def generate_array(data_bytes, version, ec_level):
    """
    Description: Takes a list of binary string bytes and version number
                 and creates an array representing the data as an array
                 in qr code format.
    Note:    Only guaranteed for versions less than 7
    Inputs:
    data_bytes -- a list of binary string bytes
    version -- an integer representing the version number
    ec_level -- a one-character string representing the error-correction level
    Returns:
    An array representing the data_bytes as a qr code of the given
    version number
    """
    # Fills the version-required defaults
    array_template = create_data_array(version)
    fill_defaults(array_template, version)
    # Fills all of the data and info for each of the mask patterns
    arrays = []
    for mask in range(8):
        array = array_deepcopy(array_template)
        fill_array(array, data_bytes, version, ec_level, mask)
        arrays.append(array)
    # Evaluates each mask pattern and selects the one with the lowest
    # score to be the best array
    min_score = float('inf')
    best_array = None
    for array in arrays:
        score = get_mask_score(array)
        if score < min_score:
            min_score = score
            best_array = array
    return best_array

def create_data_array(version):
    """
    Description: Generates a square array (list of lists) of the size
                 specified by the version number.
    Inputs:
    version -- an integer representing the required size / degree of
               the array
    Returns:
    An empty (-1) array of the required size.
    """
    # The specified dimensions (square) of the data array
    size = 17 + (version * 4)
    # Data array. -1 == empty
    data_array = []
    # Creates the array and fills it with the integer 2
    for dummy1 in range(size):
        temp_list = []
        for dummy2 in range(size):
            temp_list.append(EMPTY)
        data_array.append(temp_list)
    return data_array

def fill_defaults(array, version):
    """
    Description: Fills all of the necessary default patterns
                 for qr arrays of a given version.
    Inputs:
    array -- a 2x2 array represented as a list of lists
    version -- an integer representing the version number
    Returns:
    None
    * Modifies array
    """
    # Positions detection squares
    size = len(array)
    # The outermost square should be white and it should alternate for
    # each concentric square
    fills = [WHITE, BLACK]
    # The specified locations and size of the patterns
    centers = ((3, 3), (3, size - 4), (size - 4, 3))
    position_detection_size = 5
    # Creates all the squares
    for cntr in centers:
        fill_square_pattern(array, cntr, position_detection_size, fills)
        # Overrides the center bit to be black against the pattern
        array[cntr[0]][cntr[1]] = BLACK
    # Position adjustment patterns
    # The outermost square should be black and it should alternate for
    # each concentric square
    fills = [BLACK, WHITE]
    # Only versions greater than one have position adjustment patterns
    if version > 1:
        # The first and last rows/columns are specified
        first = 6
        last = size - 7
        # Keeps track of the rows/columns for the patterns
        pattern_locs = [first, last]
        # The number of additional rows/columns that must be computed,
        # based on version number
        num_locs = version // 7
        if num_locs > 0:
            # The distance that there should be between each row/column
            spacing = (last - first) / (num_locs + 1)
            # Round to the next even number
            spacing = math.ceil(spacing)
            spacing += spacing % 2
            # Add new rows/columns to the list
            for idx in range(num_locs):
                pattern_locs.append(last - (spacing * (idx + 1)))
        # Specified size of the position adjustment patterns
        position_adjustment_size = 3
        # Patterns are placed at the intersections of the rows/columns
        # that have been calculated
        for ploc_a in pattern_locs:
            for ploc_b in pattern_locs:
                # Only ones that do not overlap with the position
                # detection patterns
                if array[ploc_a][ploc_b] == EMPTY:
                    fill_square_pattern(array, (ploc_a, ploc_b),
                                        position_adjustment_size, fills)
    # Timing pattern (Goes in row/column 6)
    for idx in range(size):
        # The array is diagonally symmetrical, so only one needs to be checked
        if array[idx][6] == EMPTY:
            # Color alternates
            color = [BLACK, WHITE][idx % 2]
            array[idx][6] = color
            array[6][idx] = color
    # Required black pixel (Goes in the 7th to last row in column 8)
    array[size - 8][8] = BLACK

def fill_square_pattern(array, center, size, fills):
    """
    Description: Creates a square pattern in the array with the desired
                 fills and size.
    Inputs:
    array -- a 2x2 array represented as a list of lists
    center -- the location of the center of the square pattern
    size -- the number of squares that should be drawn
    fill -- a list of the items used to create the edges of the squares,
            in order from outside to inside
    Returns:
    None
    * Modifies array
    """
    loc = size
    while loc > 0:
        # Number of completed squares % size of list
        fill = fills[(size - loc) % len(fills)]
        # So that when loc = 0, [row, column] = center
        offset = loc - 1
        # Width of distance from center * 2 directions + center point
        width = 2 * offset + 1
        make_square(array, center[0] - offset, center[1] - offset, width, fill)
        loc -= 1

def make_square(array, row, column, width, fill):
    """
    Description: Creates a square in the array with the desired fill and width.
    Inputs:
    array -- a 2x2 array represented as a list of lists
    row -- the row (first index) of the top-left corner of the square
    column -- the column (second index) of the top-left corner of the square
    width -- the number of elements wide the square should be
    fill -- the item used to create the edges of the square
    Returns:
    None
    * Modifies array
    """
    # Fills in the vertical sides
    nrow = row
    ncol = column
    while nrow < row + width:
        if nrow >= 0 and nrow < len(array):
            if ncol >= 0 and ncol < len(array[0]):
                array[nrow][ncol] = fill
            if ncol + width - 1 >= 0 and ncol + width - 1 < len(array[0]):
                array[nrow][ncol + width - 1] = fill
        nrow += 1
    # Fills in the horizontal sides
    nrow = row
    ncol = column + 1
    while ncol < column + width:
        if ncol >= 0 and ncol < len(array):
            if nrow >= 0 and nrow < len(array[0]):
                array[nrow][ncol] = fill
            if nrow + width - 1 >= 0 and nrow + width - 1 < len(array[0]):
                array[nrow + width - 1][ncol] = fill
        ncol += 1

def fill_array(array, data_bytes, version, ec_level, mask):
    """
    Description: Fills all of the data_bytes and the information bits
                 into the array using the given mask pattern.
    Inputs:
    array -- a 2x2 array represented as a list of lists
    data_bytes -- a list of binary string bytes
    mask -- an integer (0 - 7) representing the mask to be used
    ec_level -- a one-character string representing the error-correction level
    Returns:
    None
    * Modifies array
    """
    size = len(array)
    # The coded information for each error-correction level and mask
    mask_and_ec_codes = {'L': ['111011111000100', '111001011110011',
                               '111110110101010', '111100010011101',
                               '110011000101111', '110001100011000',
                               '110110001000001', '110100101110110'],
                         'M': ['101010000010010', '101000100100101',
                               '101111001111100', '101101101001011',
                               '100010111111001', '100000011001110',
                               '100111110010111', '100101010100000'],
                         'Q': ['011010101011111', '011000001101000',
                               '011111100110001', '011101000000110',
                               '010010010110100', '010000110000011',
                               '010111011011010', '010101111101101'],
                         'H': ['001011010001001', '001001110111110',
                               '001110011100111', '001100111010000',
                               '000011101100010', '000001001010101',
                               '000110100001100', '000100000111011']}
    cur_code = mask_and_ec_codes[ec_level][mask]
    # Inserts the ec-level and mask data into row and column 8
    idx = 0
    while idx < len(cur_code):
        # Offsets the index to avoid the presets and the middle section
        #   (the first and last bit are each at an edge of the array)
        #   (15 = length of ec-level and mask data bits)
        if idx <= 5:
            v_offset = 0
            h_offset = 0
        elif idx == 6:
            v_offset = 0
            h_offset = 1
        elif idx <= 8:
            v_offset = size - len(cur_code) - 1
            h_offset = size - len(cur_code)
        else:
            v_offset = size - len(cur_code)
            h_offset = size - len(cur_code)
        # Info in row starts from left
        array[8][idx + h_offset] = int(cur_code[idx])
        # Info in column starts from bottom
        array[size - 1 - (idx + v_offset)][8] = int(cur_code[idx])
        idx += 1
    # Inserts the required version information bits if the version is
    # higher than 6
    if version > 6:
        # The required version info bits for each version
        version_info_bits = {
             7: '001010010011111000',  8: '001111011010000100',
             9: '100110010101100100', 10: '110010110010010100',
            11: '011011111101110100', 12: '010001101110001100',
            13: '111000100001101100', 14: '101100000110011100',
            15: '000101001001111100', 16: '000111101101000010',
            17: '101110100010100010', 18: '111010000101010010',
            19: '010011001010110010', 20: '011001011001001010',
            21: '110000010110101010', 22: '100100110001011010',
            23: '001101111110111010', 24: '001000110111000110',
            25: '100001111000100110', 26: '110101011111010110',
            27: '011100010000110110', 28: '010110000011001110',
            29: '111111001100101110', 30: '101011101011011110',
            31: '000010100100111110', 32: '101010111001000001',
            33: '000011110110100001', 34: '010111010001010001',
            35: '111110011110110001', 36: '110100001101001001',
            37: '011101000010101001', 38: '001001100101011001',
            39: '100000101010111001', 40: '100101100011000101'
        }
        if version > 40:
            print("\nError: Unsupported Version: " + str(version) +
                  "\n in fill_array on data_bytes " + str(data_bytes) + "\n")
        else:
            v_info_bits = version_info_bits[version]
            # The bits are positioned along the right and bottom sides
            # of the array, with the first bit at the index 11 less
            # than the size
            offset = size - 11
            idx = 0
            while idx < len(v_info_bits):
                # The data is placed in a 3x8 pattern, with each
                # row/column of 3 being filled before moving on to the
                # next row/column
                array[idx % 3 + offset][idx // 3] = int(v_info_bits[idx])
                array[idx // 3][idx % 3 + offset] = int(v_info_bits[idx])
                idx += 1
    # Inserts the actual data
    insert_data_with_mask(array, data_bytes, mask)

def int_floor(num):
    """
    Returns floor of num as an integer.
    """
    return int(math.floor(num))

def insert_data_with_mask(array, data_bytes, mask):
    """
    Description: Fills all of the data_bytes into the array using
                 the given mask pattern.
    Inputs:
    array -- a 2x2 array represented as a list of lists
    data_bytes -- a list of binary string bytes
    mask -- an integer (0 - 7) representing the mask to be used
    ec_level -- a one-character string representing the error-correction level
    Returns:
    None
    * Modifies array
    """
    # Starts in the bottom-right corner
    row = len(array) - 1
    col = len(array) - 1
    # Keeps track of weather the program should currently be going up
    # or down in rows
    row_inc = -1
    # Keeps track of which of the two adjacent rows the program is in
    side = 0
    # Formula to determine weather or not bits should be flipped
    # according to the mask pattern
    mask_formula = [
        lambda n, r, c: [1-n, n][(r+c)%2],
        lambda n, r, c: [1-n, n][r%2],
        lambda n, r, c: [1-n, n, n][(c)%3],
        lambda n, r, c: [1-n, n, n][(r+c)%3],
        lambda n, r, c: [1-n, n][(int_floor(r/2)+int_floor(c/3))%2],
        lambda n, r, c: [1-n, n, n, n][((r*c)%2 + ((r*c)%3))],
        lambda n, r, c: [1-n, n][((r*c)%2 + ((r*c)%3))%2],
        lambda n, r, c: [1-n, n][((r+c)%2 + ((r*c)%3))%2]
        ][mask]
    data_string = "".join(data_bytes)
    # The current location in data_string
    index = 0
    while col > 0:
        # If there are no presets there already
        if array[row][col + side] == EMPTY:
            # Extra bits are assigned to be white
            bit = WHITE
            if index < len(data_string):
                bit = int(data_string[index])
            array[row][col + side] = mask_formula(bit, row, col + side)
            index += 1
        # If bits have been placed on both sides of the current row
        if side == -1:
            row += row_inc
        # If the end of a column has been reached
        if row < 0 or row == len(array):
            col -= 2
            # Skips the vertical timing pattern in column 6
            if col == 6:
                col -= 1
            # Goes back to a valid row in the grid
            row -= row_inc
            # Switches directions
            row_inc = row_inc * -1
        side = -1 - side

def get_mask_score(array):
    """
    Description: Finds the penalty score for a given qr array
                 as determined by the 4 established rules.
    Inputs:
    array -- a 2x2 array represented as a list of lists
    Returns:
    An integer with the same value as the penalty score for the given array.
    """
    # Keep track of the penatlties for each rule
    rule_1 = 0
    rule_2 = 0
    rule_3 = 0
    rule_4 = 0
    num_black = 0
    # Stores each line (vertical and horizontal) of the array as a string
    line_strings = []
    # Going through each column of the array
    for ncol in range(len(array)):
        # These keep track of the elements in the array as strings
        column = []
        row = []
        # Used to find squares (cur and last form one side of the square)
        last = None
        cur = None
        for nrow in range(len(array)):
            val = array[nrow][ncol]
            column.append(str(val))
            row.append(str(array[ncol][nrow]))
            # Used for calculating Rule 4
            if val == BLACK:
                num_black += 1
            if nrow == 0:
                last = val
            else:
                cur = val
                # Rule 2 - 2x2 squares
                if last == cur and ncol > 0:
                    if (array[nrow][ncol-1] == cur
                        and array[nrow-1][ncol-1] == cur):
                        # 3 point penalty
                        rule_2 += 3
                last = val
        # Adds a string representation of each row and column to
        # line_strings which is used when calculating Rule 1 and
        # Rule 3
        line_strings.append("".join(column))
        line_strings.append("".join(row))
    # Totals the Rule 1 and Rule 3 penalties for each row and column
    for string in line_strings:
        rule_1 += get_rule_1_score(string)
        rule_3 += get_rule_3_score(string)
    # Calculations for rule 4 according to formula
    total_squares = len(array) ** 2
    extra = (num_black / total_squares) * 100 - 50
    rule_4 = int(math.fabs(extra)) / 5 * 10
    return rule_1 + rule_2 + rule_3 + rule_4

def get_rule_1_score(string):
    """
    Description: Finds the rule 1 penalty score for a given string.
                 (based on the number of chains of 5 or more
                  squares of the same color)
    Inputs:
    string -- a string representing an entire row or column of a qr code array
    Returns:
    An integer with the same value as the penalty score for the
    given row/column.
    """
    # Index (the first one is already counted)
    idx = 1
    # How many in a row there are now (counts the first one)
    count = 1
    # Total penalty score
    total = 0
    while idx < len(string):
        # If the chain continues
        if string[idx] == string[idx-1]:
            count += 1
            # If that was the last element in the string, evaluate the
            # chain so far
            if idx == len(string) - 1 and count >= 5:
                total += (count - 5) + 3
        # If the chain is broken
        else:
            # 3 points for a chain of 5 + 1 point for each additional
            # square
            if count >= 5:
                total += (count - 5) + 3
            count = 1
        idx += 1
    return total

def get_rule_3_score(string):
    """
    Description: Finds the rule 3 penalty score for a given string.
                 (based on the number of subsets of the string that match
                  the specified patterns)
    Inputs:
    string -- a string representing an entire row or column of a qr code array
    Returns:
    An integer with the same value as the penalty score for the
    given row/column.
    """
    idx = 0
    total = 0
    while idx < len(string):
        # The first item is the string to test for, the second is how
        # many spaces to skip to arrive at the next possible location
        # of a match
        strings = [("000010111010000", 11),
                   ("10111010000", 7),
                   ("00001011101", 11)]
        for test in strings:
            if string[idx:idx+len(test[0])] == test[0]:
                # Each pattern is worth 40 points
                total += 40
                # Adds one less since one is added everytime at the
                # end of the loop
                idx += test[1] - 1
                break
        idx += 1
    return total

def array_deepcopy(array):
    """
    Description: Copies a 2x2 array so that there are no
                 shared objects/references.
    Inputs:
    array -- a 2x2 array represented as a list of lists
    Returns:
    A copy of the array
    """
    new_array = []
    for elem in array:
        new_array.append(list(elem))
    return new_array


#################################################################
CANVAS_SIZE = 600

class QRFrame:
    """
    A GUI for generating QR codes.
    """
    def __init__(self, array_gen_function):
        """
        Constructs a GUI for generating QR codes based on a function to
        turn plaintext into an array representing a QR code that can be
        drawn.
        """
        self._array_gen_function = array_gen_function
        self._info = "Hello, World!"
        self._input = None
        self._frame = None
        self._array = None
        self._setup_frame()

    def _setup_frame(self):
        """
        Prepares the frame to be drawn.
        """
        self._frame = simplegui.create_frame("QR Code Generator",
                                             CANVAS_SIZE, CANVAS_SIZE)
        self._frame.set_draw_handler(self._draw)
        self._frame.set_canvas_background('White')
        self._input = self._frame.add_input("Info:", self._set_info, 100)
        self._frame.add_label(" ")
        self._frame.add_button("Generate!", self._generate_array, 100)
        self._frame.add_label(" ")
        self._frame.add_button("Image in New Window", self._get_image, 100)

    def _set_info(self, new_info):
        """
        Sets the info to be QR encoded.
        """
        self._info = new_info
        self._array = self._array_gen_function(new_info)

    def _get_image(self):
        """
        Opens the image of the QR code in its own window.
        """
        self._frame.get_canvas_image()

    def _generate_array(self):
        """
        Regenerates the QR array based on the current info.
        """
        if self._input:
            self._info = self._input.get_text()
        self._array = self._array_gen_function(self._info)

    def _draw(self, canvas):
        """
        Description: Draws the global array on the given canvas,
                     where '0' = White, '1' = Black, and
                     everything else = Red.
        Inputs:
        canvas -- the canvas used by the frame
        Returns:
        None
        """
        if self._array != None:
            # Size include a 3-square wide border around the array
            size = len(self._array) + 6
            # Rounding the width up and the space down ensures that
            # there are no gaps between squares
            width = math.ceil(CANVAS_SIZE / size) -1
            space = math.floor(CANVAS_SIZE / size)
            #space = width+1
            row = 0
            while row < len(self._array):
                col = 0
                while col < len(self._array):
                    # Forms a square of the color signified in the array
                    color = ['White', 'Black', 'Red'][self._array[row][col]]
                    xval = (col + 3) * space
                    yval = (row + 3) * space
                    points = [(xval, yval),
                              (xval + width, yval),
                              (xval + width, yval + width),
                              (xval, yval + width)]
                    canvas.draw_polygon(points, 2, color, color)
                    col += 1
                row += 1

    def start(self):
        """
        Begins drawing the frame to the screen.
        """
        self._frame.start()
