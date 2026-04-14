#### **Background**

Recall that for Reed-Solomon codes, we need to create a polynomial from the message we're encoding, create a polynomial to divide it by, and then return the remainder for the division.

The*message*polynomial, which is our*numerator*, is constructed as follows:

$message = \sum\limits_{i = 0}^{n-1} M_ix^{k+n-i-1}$ where $M = m_0m_1\cdots m_{n-2}m_{n-1}$ are the $n$ bytes in our message, and $k$ is the number of error correction bytes required.

The*generator*polynomial, which is our*denominator*, is constructed as follows:

$generator = \prod\limits_{i = 0}^{k-1} (x - 2^i)$ where $k$ is the number of error correction bytes required.
#### **The Example**

You can follow along with the example if you utilize our[z256 Module](https://canvas.rice.edu/courses/33582/pages/z256-api)to do the necessary arithmetic operations. Usage of this module looks something like the following:
```
import comp140_module5_z256 as z256
print(z256.mul(3, 104))
print(z256.power(2, 1))

```

Let's do a small example of Reed-Solomon encoding by hand, using the definition above. In particular, let's use as our message $M = [01101000, 01101001, 00100001, 00100001] = [104, 105, 33, 33]$, and pretend we need $k=2$ bytes of error-correction.

First, let's create our message polynomial: $message = 104x^{2+4-0-1} + 105x^{2+4-1-1} + 33x^{2+4-2-1} + 33x^{2+4-3-1} = 104x^5 + 105x^4 + 33x^3 + 33x^2$.

Similarly, let's create our generator from the formula, remembering to do the coefficient arithmetic in $Z_{256}$: $generator = (x-2^0)(x-2^1) = (x-1)(x-2) = x^2 + 3x + 2$.

Now, let's use polynomial long division to compute the remainder from dividing $message/generator$:
- 

If we multiply $generator$ by $104x^3$, we can subtract out a factor of $(x^2 + 3x + 2)(104x^3) = 104x^5 + 184x^4 + 208x^3$ (remember, the coefficients are in $Z_{256}$!), leaving our numerator at $104x^5 + 105x^4 + 33x^3 + 33x^2 - (104x^5 + 184x^4 + 208x^3) = 209x^4 + 241x^3 + 33x^2$.
- 

If we multiply $generator$ by $209x^2$, we can subtract out a factor of $(x^2 + 3x + 2)(209x^2) = 209x^4 + 110x^3 + 191x^2$ (remember, the coefficients are in $Z_{256}$!), leaving our numerator at $209x^4 + 241x^3 + 33x^2 - (209x^4 + 110x^3 + 191x^2) = 159x^3 + 158x^2$.
- 

If we multiply $generator$ by $159x$, we can subtract out a factor of $(x^2 + 3x + 2)(159x) = 159x^3 + 188x^2 + 35x$ (remember, the coefficients are in $Z_{256}$!), leaving our numerator at $159x^3 + 158x^2 - (159x^3 + 188x^2 + 35x) = 34x^2 + 35x$.
- 

If we multiply $generator$ by $34$, we can subtract out a factor of $(x^2 + 3x + 2)(34) = 34x^2 + 102x + 68$ (remember, the coefficients are in $Z_{256}$!), leaving our numerator at $34x^2 + 35x - (34x^2 + 102x + 68) = 69x + 68$.

There are no longer any multiples of $generator$ in our numerator, so we're done -- the remainder polynomial is $69x + 68$.*This remainder polynomial is what your recipe and code should return*.

When the QR code is created, the error-correction bytes taken from this polynomial will be $[69, 68] = [01000101, 01000100]$.