# Polynomial API

Following is the API for the`Polynomial`class that we will be using for module 5. This class represents a polynomial with coefficients in $Z_{256}$. It consists of a collection of terms, where each term is $c \times x^p$, with coefficient $c$ and power $p$. Another polynomial can be added to, subtracted from, or multiplied by this polynomial. You can also find the remainder when this polynomial is divided by another polynomial. Also included are similar operations to add a term, subtract a term, or multiply by a term.
#### **General Purpose Methods**

- 

`__init__(self, terms=None)`

Constructs a new instance of a`Polynomial`. The optional`terms`argument can be a dictionary mapping powers to coefficients. If`terms`is provided, then those terms are copied to create the polynomial. Otherwise, the new polynomial is initialized to $0$.
- 

`__str__(self)`

Returns a string representation of the`Polynomial`.
- 

`__eq__(self, other_polynomial)`

Returns`True`if`other_polynomial`has the same terms as this polynomial, and`False`otherwise. This can be used to test if two polynomials are equivalent as follows:`poly1 == poly2`.
- 

`__ne__(self, other_polynomial)`

Returns`False`if`other_polynomial`has the same terms as this polynomial, and`True`otherwise. This can be used to test if two polynomials are not equivalent as follows:`poly1 != poly2`.
- 

`get_terms(self)`

Returns a dictionary mapping powers to coefficients for all of the terms of the polynomial. The returned dictionary could be passed to the`__init__`method to create an identical polynomial.
- 

`get_degree(self)`

Returns the degree of this polynomial; that is, the highest power of $x$ in a term, where that term has a nonzero coefficient.
- 

`get_coefficient(self, power)`

Returns the coefficient of $x^{power}$ in this polynomial, which is guaranteed to be an integer between 0 and 255.

#### **Methods Using Single Terms**

- 

`add_term(self, coefficient, power)`

Returns a new instance of a`Polynomial`which is the result of adding the given term, defined by the coefficient and power, to this polynomial using $Z_{256}$ arithmetic.
- 

`subtract_term(self, coefficient, power)`

Returns a new instance of a`Polynomial`which is the result of subtracting the given term, defined by the coefficient and power, from this polynomial using $Z_{256}$ arithmetic.
- 

`multiply_by_term(self, coefficient, power)`

Returns a new instance of a`Polynomial`representing the product of this polynomial and the input term, defined by the coefficient and power, using $Z_{256}$ arithmetic.

#### **Methods Using Other Polynomials**

- 

`add_polynomial(self, other_polynomial)`

Returns a new instance of a`Polynomial`representing the sum of this polynomial and`other_polynomial`.
- 

`subtract_polynomial(self, other_polynomial)`

Returns a new instance of a`Polynomial`representing the difference of this polynomial and`other_polynomial`.
- 

`multiply_by_polynomial(self, other_polynomial)`

Returns a new instance of a`Polynomial`representing the product of this polynomial and`other_polynomial`.
- 

`remainder(self, denominator)`

Returns a new instance of a`Polynomial`representing the remainder from dividing this polynomial by the`denominator`polynomial.