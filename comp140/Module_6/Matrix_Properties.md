# Matrix Properties

Matrices have many useful properties. Some of these are identical to properties on the real numbers, and some are very different. In the discussion below, we will need many matrices of many different sizes. Let $m$, $n$, $p$, and $q$ be any positive integers and define the following matrices:
- $A$, $B$ and $C$ are all $m \times n$ matrices.
- $D$ is any $n \times p$ matrix.
- $E$ is any $p \times q$ matrix.
- $I_k$ is the $k \times k$ identity matrix.
- $S$, $T$ and $U$ are $n \times n$ square matrices which have inverses.

### **Properties of Addition**

- $A + B = B + A$
- $(A+B)+C = A+(B+C)$

### **Properties of Multiplication**

- $(A+B)D = AD + BD$
- $(AD)E = A(DE)$
- $I_mA = AI_n = A$

### **Properties of Transpose**

- $(A+B)^T = A^T + B^T$
- $(AD)^T = D^TA^T$
- $(A^T)^T = A$

### **Properties of Inverse**

Only square matrices have inverses. In fact, square matrices might*not*have an inverse. Determining whether or not a matrix has an inverse involves a property called the determinant of a matrix, and is not within the scope of this course. All square matrices we give you in this class will have inverses.
- $SS^{-1} = S^{-1}S = I_n$
- $(ST)^{-1} = T^{-1}S^{-1}$