"""
Matrix operations module - compatible with CodeSkulptor's numeric module.
Provides a Matrix class for linear algebra operations used in Module 6.
"""

class Matrix:
    """
    A matrix class that supports basic linear algebra operations.
    Compatible with CodeSkulptor's numeric.Matrix.
    """

    def __init__(self, data):
        """
        Create a new matrix from a 2D list of numbers.

        inputs:
            - data: a 2D list of numbers (list of rows)
        """
        if not data or not data[0]:
            raise ValueError("Matrix cannot be empty")

        self._data = [[float(val) for val in row] for row in data]
        self._rows = len(self._data)
        self._cols = len(self._data[0])

        # Validate that all rows have the same length
        for row in self._data:
            if len(row) != self._cols:
                raise ValueError("All rows must have the same length")

    def __str__(self):
        """
        Return a string representation of the matrix.
        """
        result = "["
        for i, row in enumerate(self._data):
            if i > 0:
                result += " "
            result += "["
            result += ", ".join("{:.6g}".format(val) for val in row)
            result += "]"
            if i < self._rows - 1:
                result += "\n"
        result += "]"
        return result

    def __repr__(self):
        return str(self)

    def shape(self):
        """
        Return a tuple (rows, cols) representing the shape of the matrix.
        """
        return (self._rows, self._cols)

    def getitem(self, row, col):
        """
        Get the element at (row, col).
        """
        return self._data[row][col]

    def __getitem__(self, key):
        """
        Allow indexing with matrix[row, col] or matrix[row][col].
        """
        if isinstance(key, tuple):
            return self._data[key[0]][key[1]]
        return self._data[key]

    def __setitem__(self, key, value):
        """
        Allow setting with matrix[row, col] = value.
        """
        if isinstance(key, tuple):
            self._data[key[0]][key[1]] = float(value)
        else:
            raise ValueError("Must use matrix[row, col] = value syntax")

    def __add__(self, other):
        """
        Add two matrices element-wise.
        """
        if self._rows != other._rows or self._cols != other._cols:
            raise ValueError("Matrix dimensions must match for addition")

        result = [[self._data[i][j] + other._data[i][j]
                   for j in range(self._cols)]
                  for i in range(self._rows)]
        return Matrix(result)

    def __sub__(self, other):
        """
        Subtract two matrices element-wise.
        """
        if self._rows != other._rows or self._cols != other._cols:
            raise ValueError("Matrix dimensions must match for subtraction")

        result = [[self._data[i][j] - other._data[i][j]
                   for j in range(self._cols)]
                  for i in range(self._rows)]
        return Matrix(result)

    def __mul__(self, other):
        """
        Matrix multiplication (self @ other) or scalar multiplication.
        """
        if isinstance(other, (int, float)):
            # Scalar multiplication
            result = [[self._data[i][j] * other
                       for j in range(self._cols)]
                      for i in range(self._rows)]
            return Matrix(result)

        # Matrix multiplication
        if self._cols != other._rows:
            raise ValueError("Matrix dimensions incompatible for multiplication: " +
                           str(self.shape()) + " and " + str(other.shape()))

        result = [[sum(self._data[i][k] * other._data[k][j]
                       for k in range(self._cols))
                   for j in range(other._cols)]
                  for i in range(self._rows)]
        return Matrix(result)

    def __rmul__(self, other):
        """
        Right multiplication for scalars.
        """
        if isinstance(other, (int, float)):
            return self.__mul__(other)
        raise ValueError("Cannot multiply Matrix by " + str(type(other)))

    def transpose(self):
        """
        Return the transpose of the matrix.
        """
        result = [[self._data[j][i] for j in range(self._rows)]
                  for i in range(self._cols)]
        return Matrix(result)

    def inverse(self):
        """
        Return the inverse of the matrix using Gauss-Jordan elimination.
        Only works for square matrices.
        """
        if self._rows != self._cols:
            raise ValueError("Only square matrices can be inverted")

        n = self._rows

        # Create augmented matrix [A | I]
        aug = [[self._data[i][j] for j in range(n)] +
               [1.0 if i == j else 0.0 for j in range(n)]
               for i in range(n)]

        # Forward elimination with partial pivoting
        for col in range(n):
            # Find pivot
            max_row = col
            for row in range(col + 1, n):
                if abs(aug[row][col]) > abs(aug[max_row][col]):
                    max_row = row
            aug[col], aug[max_row] = aug[max_row], aug[col]

            if abs(aug[col][col]) < 1e-10:
                raise ValueError("Matrix is singular and cannot be inverted")

            # Scale pivot row
            pivot = aug[col][col]
            for j in range(2 * n):
                aug[col][j] /= pivot

            # Eliminate column
            for row in range(n):
                if row != col:
                    factor = aug[row][col]
                    for j in range(2 * n):
                        aug[row][j] -= factor * aug[col][j]

        # Extract inverse from augmented matrix
        result = [[aug[i][j + n] for j in range(n)] for i in range(n)]
        return Matrix(result)

    def copy(self):
        """
        Return a deep copy of the matrix.
        """
        return Matrix([[val for val in row] for row in self._data])

    def summation(self):
        """
        Return the sum of all elements in the matrix.
        """
        return sum(sum(row) for row in self._data)

    def abs(self):
        """
        Return a new matrix with absolute values of all elements.
        """
        result = [[abs(val) for val in row] for row in self._data]
        return Matrix(result)

    def scale(self, scalar):
        """
        Return a new matrix with all elements scaled by scalar.
        """
        return self * scalar


def identity(n):
    """
    Create an n x n identity matrix.
    """
    return Matrix([[1.0 if i == j else 0.0 for j in range(n)] for i in range(n)])


def zeros(rows, cols):
    """
    Create a rows x cols matrix of zeros.
    """
    return Matrix([[0.0 for _ in range(cols)] for _ in range(rows)])


def ones(rows, cols):
    """
    Create a rows x cols matrix of ones.
    """
    return Matrix([[1.0 for _ in range(cols)] for _ in range(rows)])
