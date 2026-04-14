# Z256 API

Following is the API for the $Z_{256}$ arithmetic implementations that we will be using for module 5. This code is located in the`comp140_module5_z256`module, which you can use by including the line`import comp140_module5_z256 as z256`at the top of your file.

All of the following methods will raise a`TypeError`if any of the input numbers are not in $Z_{256}$.

  - 

`z256.add(num1, num2)`

Adds`num1`to`num2`in $Z_{256}$.

  - 

`z256.sub(num1, num2)`

Subtracts`num2`from`num1`in $Z_{256}$.

  - 

`z256.mul(num1, num2)`

Multiplies`num1`by`num2`in $Z_{256}$. Returns 0 if either`num1`or`num2`is 0.

  - 

`z256.div(numerator, denominator)`

Divides`numerator`by`denominator`in $Z_{256}$. Returns 0 if`numerator`is 0, and raises a`ZeroDivisionError`if`denominator`is 0.

  - 

`z256.power(base, exponent)`

Raises`base`to the`exponent`power in $Z_{256}$. Returns 0 if`base`is 0.