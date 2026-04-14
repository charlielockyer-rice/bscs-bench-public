# LASSO Shooting

First define $SoftThreshold(x, t) = \begin{cases} x - t &\text{if } & x > t\\\ 0 &\text{if } & \lvert x \rvert \leq t\\ x + t &\text{if } & x < -t \end{cases}$

**Algorithm:**LASSO Shooting
**Input:**
- $X$: $n \times m$ matrix of explanatory data
- $y$: $n \times 1$ matrix of measured data
- $\lambda$: the complexity constraint parameter
- $iterations$: the maximum number of iterations to run

**Output:**$m \times 1$ matrix $w$ which minimizes $MSE(w) + \lambda \left\lVert w \right\rVert_1$
1. $w \leftarrow$ the least-squares estimate using $X$ and $y$;
2. $i \leftarrow 0$;
3. **while**$i \lt iterations$**do:**
4. $w_{old} \leftarrow w$;
5. **foreach**$j$ in $0, 1, \cdots, m-1$**do**
6. $a_j \leftarrow \displaystyle\frac{(X^Ty)_{j,0} - ((X^TX)_{j,.}w)_{0,0}}{(X^TX)_{j,j}}$;
7. $b_j \leftarrow \displaystyle\frac{\lambda}{2(X^TX)_{j,j}}$;
8. $w_j \leftarrow SoftThreshold(w_j + a_j,~ b_j)$;
9. **if**$\left\lVert w - w_{old} \right\rVert_1 < 10^{-5}$**then**
10. **break**;
11. $i \leftarrow i+1$;
12. **return**$w$;