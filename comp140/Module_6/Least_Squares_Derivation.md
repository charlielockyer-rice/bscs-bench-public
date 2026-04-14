# Least-Squares Derivation

Given an $m \times 1$ matrix of weights $w$ for our model and an $n \times m$ matrix $X$ of input data, the vector of wins predicted by our model is given by $Xw$. If we also have an $n \times 1$ matrix of the*actual*number of wins, $y$, we can calculate the Mean-Squared Error between the model and the data as:

$MSE(w) = \frac{1}{n} \displaystyle \sum_{i=0}^{n-1} ((Xw)_i - y_i)^2$

We can rewrite this as:

$n \cdot MSE(w) = (Xw - y)^T(Xw - y)$

According to the properties of transpose and multiplication, we can expand this as:

$\begin{align} n \cdot MSE(w) &= (w^TX^T - y^T)(Xw - y)\\ &= w^TX^TXw - y^TXw - w^TX^Ty + y^Ty \end{align}$

Since $y^TXw = w^TX^Ty$ in this case (see Discussion Question 1), we can further simplify this to:

$n \cdot MSE(w) = w^TX^TXw - 2y^TXw + y^Ty$

To find the best set of weights for our model, we need to take the derivative of the MSE with respect to $w$. Since $w$ is a vector of weights, this is a complex computation. We have computed this derivative for you:

$\frac{dMSE(w)}{dw} = \frac{1}{n} (2w^TX^TX - 2y^TX)$