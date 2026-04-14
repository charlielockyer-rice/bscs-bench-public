# Writing Recipes (Resource)

This "**recipe guide**" will help you in writing "recipes" in this course. In short, a recipe is a programming-language independent description of how you will solve a problem using a computer. Throughout this course, and whenever you need to solve a problem using a computer, you will need to write "recipes". Although "recipe" is not the formal term used by computer scientists, there is significant similarity in what you will be asked to do and recipes that you would find in a cookbook. Therefore, at this level, the word*recipe*should put you in the right frame of mind.

*Writing recipes is one of the most important parts of this course.*Even more important, to a large extent, than writing Python code. To write a recipe, you must*carefully*think about and design the specific sequence of processing steps needed to solve your problem. And you must present that sequence of steps in a way that others can*clearly*understand*exactly*what you meant. This recipe guide is intended to help you do that. Based on your recipe, then anyone should be able to implement your solution using*any*programming language, whether Python or otherwise. If you don't write your recipes in a concrete, specific, and clear way, you will not have achieved your aims. So first, when thinking about writing a recipe, keep in mind the following two high-level points:
- 

**What goes in a recipe?**You need to clearly specify the input(s) to the computation, the output(s) of the computation, and a step-by-step process that will produce the output(s) given the input(s).
- 

**What doesnotgo in a recipe?**You should not use any Python syntax, constructs, or data structures in your recipes. In particular, you might ultimately decide to implement your recipe in any programming language. (Although, we will always use Python in this class.) Your recipe should be entirely independent of any particular programming language.

The recipes you write in this class can be a combination of English and mathematics. However, where what you want to say can be expressed by mathematics notation and terminology, this is preferred over expressing it in English, as mathematics is much more precise and concise than English. Note that, as stated above, your recipes should**not**include any Python. For example, a numbered list of steps and sub-steps, as in an outline, is fine for a recipe. A Python program thinly disguised with a few English words is not.

In general, you should realize that a recipe is just that: a set of instructions detailing how to implement something, not an actual implementation. You should be able to implement the recipe in a variety of ways using a variety of programming languages. Python data structures, such as lists and dictionaries, should never appear in your recipes. Instead you should describe what you mean in English or mathematics. An integer or real number, for example, are mathematical concepts that are fine to use in a recipe. A dictionary, in contrast, is a Python data structure and the word "dictionary" does not have a mathematically precise definition, and so "dictionary" should not appear in a recipe.

You may write your recipes using any editing and formatting tools that you prefer. We have provided some brief information for you if you would like to use[Google Docs](https://canvas.rice.edu/courses/33582/pages/writing-recipes-in-google-docs)or[LaTeX](https://canvas.rice.edu/courses/33582/pages/writing-recipes-in-latex).

We give a long glossary of mathematical and general computer science terms below. None of these terms are specific to Python and so are valid to include in any recipe. As with any glossary, it is designed to be referenced; it is*not*required (or even recommended) that you memorize all the terms and properties as listed. However,*you should read through the glossary below, to be familiar with it*, and you should be aware of the tools available to you when writing recipes, along with their properties and limitations.
### General Computer Science Terms
Assignment

You can**assign**a label to any mathematical object (number, string, set, vertex, etc.). This label is called a**variable**. Assignment is generally denoted by $v ← x$, where $v$ is the variable (i.e., the label) and $x$ is the mathematical object being given that label. Note the direction of the arrow, pointing from the mathematical object*to*the label (i.e., an arrow from right to left,*not*from left to right). You can read the $←$ symbol, in simple terms, as "gets". For example, this recipe assigns 5 to "a", then assigns 3 to "b", and finally assigns the sum of (the current value of) "a" and "b" to "a"; after these three assignments, the label "a" now refers to the integer 8:
```
a ← 5;
b ← 3;
a ← a + b; 
```
Class

A**class**is a definition of a type of object. It defines the object's data (i.e.,**member variables**, or**fields**) and operations that can be performed on that data (i.e.,**methods**).

An**instance**of a class is an individual**object**of the type defined by that class.Function

A**function**is a self-contained sequence of operations that performs a task.

A function may have**parameters**, which are the names of its inputs. When you call a function, you must specify the**arguments**that will be used as the values for the function's parameters.Iteration

You can**iterate**over the elements in a sequence or in a set by specifying a series of steps to follow for each element.

When iterating over a*sequence*, these steps will occur once for every element of the sequence, in the order that the elements occur in the sequence, first using the first element of the sequence and moving in order towards the last element of the sequence. For example, if the variable "times" is a sequence of time specifications, you could iterate over each of these, in order, by saying something like
```
for each time in times do
```

followed by the (indented) sequence of steps to do for each time specification, using the variable "time" (defined by the loop) to refer to the current element of the sequence "times" that is being processed during that particular iteration of the loop. If you simply want to iterate over a range of values, such as the integers from $0$ to $n-1$, you could, for example, write this as
```
for each idx in 0, 1, 2, ..., n-1 do
```

again, followed by the (indented) sequence of steps to do for each value in that sequence, in order. This example is the same as the previous one, except that it explicitly lists the sequence over which you are iterating, rather than referring to some existing sequence using a variable name.

When iterating over a*set*, the steps will occur once for every element of the set,*but in no particular order*. For example, if, "times" is instead a set of time specifications (rather than a sequence, as in the example above), you could iterate over the times in this set by saying something like
```
for each time in times do
```

Note that the only thing different in this example from the "times" example above is that "times", within this recipe, is a set rather than a sequence, so the elements are iterated over in no particular order.Queue

A**queue**is a sequence of elements with a restricted set of operations that can be performed on it. Specifically, a queue is a FIFO (i.e., First-In-First-Out) data structure.
- 

A queue is**empty**if the corresponding sequence is empty.
- 

You can**push**an element $b$ onto the queue. This adds $b$ onto the beginning of the corresponding sequence.
- 

You can**pop**an element off of a nonempty queue. This removes and returns the last element of the corresponding sequence.

For example, if a queue currently consists of the sequence of 6 elements (reading from beginning to end of the sequence) $s_0, s_1, s_2, s_3, s_4, s_5$ and the element $b$ is pushed onto this queue, then the resulting queue consists of the sequence of 7 elements $b, s_0, s_1, s_2, s_3, s_4, s_5$ . If a pop on this queue is then performed, the value $s_5$ is returned and the queue now consists of the sequence of 6 elements $b, s_0, s_1, s_2, s_3, s_4$.Return

A**return**statement in a recipe indicates that the recipe is complete. The return statement should specifically indicate what to output or which variables contain the expected output (i.e., the value or values to be "returned" from the recipe).Stack

A**stack**is a sequence of elements with a restricted set of operations that can be performed on it. Specifically, a stack is a LIFO (i.e., Last-In-First-Out) data structure.
- 

A stack is**empty**if the corresponding sequence is empty.
- 

You can**push**an element $b$ onto the stack. This adds $b$ onto the end of the corresponding sequence.
- 

You can**pop**an element off of a nonempty stack. This removes and returns the last element of the corresponding sequence.

For example, if a stack currently consists of the sequence of 6 elements (reading from beginning to end of the sequence) $s_0, s_1, s_2, s_3, s_4, s_5$ and the element $b$ is pushed onto this stack, then the resulting stack consists of the sequence of 7 elements $s_0, s_1, s_2, s_3, s_4, s_5, b$ . If a pop on this stack is then performed, the value $b$ is returned and the stack again consists of the sequence of 6 elements $s_0, s_1, s_2, s_3, s_4, s_5$.String

A**string**is a sequence of characters drawn from some specified character set. Strings are often written "$a_0a_1...a_{n-1}$", where $a_0, a_1, \cdots, a_{n-1}$ is the corresponding sequence of characters. For example, "HOUSTON" and "RICE" are both strings over the character set $\{A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z\}$.Variable

A**variable**is a reusable name given to a mathematical object. Using*meaningful*variable names in your recipe often immensely improves the clarity of the recipe, just as it will in your Python code! You should name your variables something meaningful in terms of what the variable is*used for*in your recipe, not, for example, simply naming it after the representation of the data. Variable names such as "start_month", "end_month", and "interest_rate" are good variable names (in a recipe dealing with these concepts), whereas variable names such as "start", "end", and "percent" are not as good; names such as "m1", "m2", and "p" are clearly worse.
### Mathematical Constructs
Finite Field

A**finite field**$F_n$ is a set of $n$ elements together with a context for computing the mathematical operations of addition, subtraction, multiplication, and division on those elements. In a finite field, every nonzero element $b$ has a multiplicative inverse $b^{-1}$. Division by $b$ is accomplished instead by multiplying by $b^{-1}$.
- 

The**order**of a finite field $F_n$ is the corresponding integer $n$. This is the number of elements in the finite field. There is only a single finite field of any given order.
- 

If the order of the finite field is some prime $p$, we can write the finite field $F_p$ as $\mathbb{Z}_p$. This is the set of numbers from $0$ to $p-1$, inclusive, where all operations are done "mod $p$", meaning you take the remainder after division by $p$ after computing the result of any operation. For example, when working in $\mathbb{Z}_5$ we have that $4 + 3 = 2~(\text{mod}~5)$.
- 

The structure of a finite field is more complex if the order is some integer power of a prime $p^m$ for $m > 1$.
Graph

A**graph**$G$ can be considered as a nonempty set of vertices $V$ together with a set of edges $E$, where each edge is an ordered pair of elements of $V$.
- 

A graph is**undirected**if all edges are bidirectional, meaning that all edges go in both directions. In an undirected graph, we consider the edge $(v, w)$ to be identical to the edge $(w, v)$.
- 

A graph is**directed**if all edges go in a single direction: the edge $(v,w)$ goes from vertex $v$ to vertex $w$. In a directed graph the edge $(v, w)$ is different from the edge $(w,v)$. Note that edges in both directions for some pair of vertices might (or might not) exist in a single graph.
- 

The**degree**of a vertex $v$ is the number of edges connected to $v$ in the graph. This can be written $deg(v)$.
- 

The**in degree**of a vertex $v$ is the number of edges leading*to*vertex $vv$, that is, the number of edges of the form $(x,v)$ for any $x$.
- 

The**out degree**of a vertex $v$ is the number of edges originating from vertex $vv$, that is, the number of edges of the form $(v,x)$ for any $x$.
- 

A**weighted graph**is a graph in which each edge is given a numeric**weight**. Specifically, a weighted graph is a nonempty set of vertices $V$, a set of edges on those vertices $E$, and a map $f : E \rightarrow \mathbb{R}$. It is often useful to consider only positive weights (for example, where the weight represents a distance or cost).
- 

A graph $G' = V', E'$ is a**subgraph**of a graph $G = V, E$ if both:
  1. 

$V'$ is a nonempty subset of $V$
  2. 

$E'$ is a subset of $E$ that contains only edges connecting vertices in $V'$

Map

A**map**is a correspondence between a set of keys and a set of values. For every key, the map will produce exactly one corresponding value. Mathematically, this can be written $f:A \rightarrow B$ where $f$ is the map, $A$ is the set of keys, and $B$ is the set of values. If $a$ is an element of $A$, the corresponding element of $B$ in the map can be written $f_a$ or $f(a)$.
- 

A correspondence between elements $a$ and $b$ can be written $a \mapsto b$, meaning that the key $a$ maps to the value $b$. Note the direction of the arrow from $a$ to $b$. You can then define a map as a set of such correspondences, as long as each key is the start of exactly one correspondence (i.e., the keys in the mapping must be unique, with each key mapping to exactly one value). For example, $\{dog \mapsto 3, fish \mapsto 4, cat \mapsto 3\}$ is a mapping from $\{dog, cat, fish\}$ to $\{3, 4\}$ that maps strings to their lengths.
- 

The**empty mapping**is the map with no correspondences. The set of keys of this map is the empty set, $\emptyset$.
- 

If you have an existing map $f:A \rightarrow B$ and a new correspondence $a \mapsto b$, you can create a larger map containing the new correspondence. Be careful to make sure every key maps to only one value (and thus that the key $a$ is not already present in $f$)!
- Note that there is no such thing as a "default map". Python defines the concept of a defaultdict, but that concept is Python-specific. In math, only the keys that are explicitly defined within the map correspond to a value. There is no default value in a map for keys not explicitly defined as corresponding to a value in the map. Do not ignore this and attempt to use a "default map" in a recipe or attempt to define a default value for some map.
MatrixA**matrix**is a 2D (i.e., 2-dimensional, or rectangular) grid of numbers. If the matrix has $m$ rows and $n$ columns, we say that it is an $m \times n$ matrix (i.e., an "m by n matrix"). A matrix can be written by surrounding the 2D grid of numbers either in a pair of square brackets or a pair of parenthesis. One example of a $2 \times 3$ matrix is:
$\left( \begin{array}{ccc} 1 & 2 & 0 \\ -5 & 1 & 0 \end{array} \right)$
- 

Given an $m \times n$ matrix $A$, the entry in row $i$ and column $j$ is denoted $A_{i, j}$. The entire row $i$ of $A$ can be denoted $A_{i,.}$. The entire column $j$ of $A$ can be denoted $A_{.,j}$.
- 

A matrix is**square**if the number of rows equals the number of columns. For example, all $5 \times 5$ matrices are square, but no $3 \times 2$ matrices are square.
- 

The $n \times n$**identity matrix**$I_n$ is the square matrix with $1$ entries along the diagonal and $0$ entries elsewhere. For example, the identity $5 \times 5$ matrix is:$I_5 = \left( \begin{array}{ccccc}1&0&0&0&0\\0&1&0&0&0\\0&0&1&0&0\\0&0&0&1&0\\0&0&0&0&1 \end{array}\right)$
- 

The**sum**of two $m \times n$ matrices $A$ and $B$ is the $m \times n$ matrix where each entry is the sum of the corresponding entries in $A$ and $B$. This is denoted $A + B$. Mathematically, $(A+B)_{i, j} = A_{i,j} + B_{i,j}$. For example, $\left( \begin{array}{c} 2 \\ -3 \end{array} \right) + \left( \begin{array}{c} 6 \\ 2 \end{array} \right) = \left( \begin{array}{c} 8 \\ -1 \end{array} \right)$
- 

The**difference**of two $m \times n$ matrices $A$ and $B$ is the $m \times n$ matrix where each entry is the difference of the corresponding entries in $A$ and $B$. This is denoted $A - B$. Mathematically, $(A-B)_{i, j} = A_{i,j} - B_{i,j}$. For example, $\left( \begin{array}{c} 2 \\ -3 \end{array} \right) - \left( \begin{array}{c} 6 \\ 2 \end{array} \right) = \left( \begin{array}{c} -4 \\ -5 \end{array} \right)$
- 

The**transpose**of an $m \times n$ matrix $A$ is the $n \times m$ matrix $A^T$. The entry in $A^T$ at row $j$ and column $i$ is the entry in $A$ at row $i$ and column $j$. For example, $\left( \begin{array}{c} 2 \\ -3 \end{array} \right)^T = \left( \begin{array}{cc} 2 & -3\end{array} \right)$.
- 

The**product**of two matrices $A$ and $B$ is denoted $AB$. The number of*columns*of $A$ must match the number of*rows*of $B$; if $A$ is $m \times q$ and $B$ is $q \times n$, then $AB$ is $m \times n$. In that case, the product is defined mathematically as $(AB)_{i,j} = A_{i,.} \cdot B_{.,j} = \displaystyle\sum^{q-1}_{k=0} A_{i,k}B_{k,j}$. In other words, the entry in $AB$ at row $i$ and column $j$ is the sum of the corresponding products of the entries of row $i$ in $A$ and column $j$ in $B$.
- 

The**inverse**of a square $n \times n$ matrix $A$ is the $n \times n$ matrix $B$ such that $AB = I_n$. Not all square matrices have inverses.
Sequence

A**sequence**is an*ordered*collection of elements. A sequence can be written $a_0, a_1, \cdots, a_{n-1}$, where each $a_i$ is the element of the sequence at position $i$. Elements in a sequence can be integers, tuples, sets, vertices, or any other mathematical constructs. For example, $3, 1, 4, 1, 5, 9$ is a sequence of integers.
- 

The**length**of a sequence is the number of elements in the sequence. For example, $a_0, a_1, \cdots, a_{n-1}$ is a length $n$ sequence. $5, 2, 7$ is a length 3 sequence.
- 

A sequence is**empty**if its length is $0$.
- 

The**index**of an element in a sequence is the position of that element in the sequence.Given any length $n$ sequence, $a_0, a_1, \cdots, a_{n-1}$, the**first**element is $a_0$ and the**last**element is $a_{n-1}$.
- 

Note that sequences might contain more than one element of the same value; for example, $a_1$ might equal $a_{n-1}$. If an element appears multiple times in a sequence then its index in that sequence is not well defined. Instead, depending on context, consider using "first index" or "last index" of the element in the sequence.
- 

Given any sequence of length $n$ and an additional element $b$, you can insert $b$ at any position (between $0$ and $n$) in the sequence to create a new sequence of length $n+1$.
- 

Given any sequence of length $n$ and a position $i$ between $0$ and $n-1$, you can delete the element at position $i$ from the sequence (without changing the order of the remaining elements) to create a new length $n-1$ sequence.
- 

A length $m$ sequence is a**subsequence**of a length $n$ sequence if $m \leq n$ and the smaller sequence can be obtained from the larger sequence by deleting elements without changing the order of the remaining terms. For example, the sequence $3, 4, 1, 9$ is a subsequence of the sequence $3, 1, 4, 1, 5, 9$
Set

A**set**is an*unordered*collection of*distinct*elements. Sets can be written by surrounding their elements in $\{$ and $\}$. For example, the set containing the elements 1, 3, and 0 can be written $\{1, 3, 0\}$. Note that this is*exactly*the same thing as saying $\{3, 1, 0\}$ or $\{0, 1, 3\}$, as order does not matter in defining a set. Elements in a set can be integers, tuples, sets, vertices, or any other mathematical constructs. Again, sets are not ordered; there is no first or last element of a set. Sets contain only distinct elements; the set $\{2, 2\}$ is identical to the set $\{2\}$; the element 2 still only occurs in the set once.
- 

The**size**of a set is the number of elements in the set (and since all elements in a set must be distinct, this is the same as saying the number of distinct elements in the set). The size of the set $\{3, 4, 0\}$ is 3. This can be denoted by surrounding the set in vertical bars; for example, $|\{3, 4, 0\}| = 3$.
- 

A set $A$ is**empty**if $|A| = 0$. The empty set is generally denoted $\emptyset$
- 

Two sets $A$ and $B$ are equal if they contain exactly the same elements (again, order does not matter). This can be written $A = B$.
- 

You can check if an element $v$ is in the set $A$. This can be written $v \in A$. For example, $4 \in \{3, 4, 0\}$, but $2 \not\in \{3, 4, 0\}$.
- 

A set $A$ is a**subset**of a set $B$ if every element of $A$ is also an element of $B$. This can be written $A \subseteq B$. For example, $\{1, 2\} \subseteq \{1, 2, 3\}$. Note that $\{1, 2, 3\} \subseteq \{1, 2, 3\}$.
- 

Similarly, we say a set $B$ is a**superset**of a set $A$ if $A$ is a subset of $B$; this can be written $B \supseteq A$. For example, $\{b, a, 3\} \supseteq \{a, 3\}$.
- 

The**union**of two sets $A$ and $B$ is the set of elements that are in either $A$ or $B$ (or both). This is denoted by $A \cup B$. For example, $\{1, 2, 3, 4\} \cup \{2, 4, 6, 8\} = \{1, 2, 3, 4, 6, 8\}$.
- 

The**intersection**of two sets $A$ and $B$ is the set of elements that are in both $A$ and also $B$. This is denoted by $A \cap B$. For example, $\{1, 2, 3, 4\} \cap\{2, 4, 6, 8\} = \{2, 4\}$.
- 

The**difference**of two sets $A$ and $B$ is the set of elements that are in $A$ but not in $B$. This is denoted by $A - B$ or $A \setminus B$. For example, $\{1, 2, 3, 4\} - \{2, 4, 6, 8\} = \{1, 3\}$.
- 

The**product**of two sets $A$ and $B$ is the set of all ordered pairs where the first element of each pair is an element in $A$ and the second element is an element in $B$. This is denoted by $A \times B$. For example, $\{1, 2\} \times \{a, b\} = \{(1,a), (1, b), (2, a), (2, b)\}$. $A \times A$ is often denoted $A^2$. For example, $\mathbb{R}^2$ is the set of all ordered pairs of real numbers, where $\mathbb{R}$ is the set of all real numbers.
Tuple

A**tuple**or**n-tuple**is an ordered collection of $n$ elements. A tuple is written $(a_0, a_1, \cdots, a_{n-1})$, where each $a_i$ is the element of the tuple in the $i$-th coordinate. Although tuples can be constructed using any mathematical constructs, they are typically constructed only with simpler items such as integers or vertices. For example, the tuple $(1, 2)$ can be used to represent a point on the 2D plane.
- 

A 2-tuple is often called an**ordered pair**. A 3-tuple is often called an**ordered triple**.

### Other Components of a Recipe
Recipe Name

Every recipe must have a name. The name of a recipe gives you a way to refer to that recipe. The name of a recipe should be some short word or phrase that describes what the recipe*does*. For example, a recipe that sorts a sequence of integers could reasonably be named "SortNumbers".Recipe Inputs and Outputs

A recipe must clearly specify its input(s) and its output(s). The input(s) should specified at the top of the recipe, together with the recipe name; the output(s) may be specified there as well, but must (also) be specified on any**return**from the recipe, as described above. The specification of each input must give the name of the input, the type of that input, and an explanation of what that input actually is (e.g., what it means).Other Names

All variable, class, and method names in a recipe must be at least 3 characters in length. The first character of each name should follow these conventions:
- 

Variable names should always start with a lowercase letter (except for names that represent constants, which should use all uppercase letters).
- 

Method names should always start with a lowercase letter.
- 

Class names should always start with an uppercase letter.

Such names should also be*meaningful*in the context of the recipe. For example, variable names should be chosen to indicate what that variable*represents*within the recipe, not for example simply based on the type of the variable; and method names should be chosen to represent what that method*does*within the recipe. The names of Python types (e.g., "list" and "dict") must also be avoided in choosing names (this restriction will be easy to follow if you follow the other rules given in this recipe guide).Initializing a Variable

It is possible to express initializing a variable in your recipe in a number of different ways. For example, suppose you have a variable "num_students" that represents the number of students enrolled in a class. To initialize "num_students" to 0, you might write something such as
```
initialize the value of the variable num_students to 0.
```

However, that is quite verbose and takes some degree of mental effort to read it, parse it, and understand what it is trying to say. That same initialization could be much more concisely and clearly expressed as
```
num_students ← 0
```

The latter is highly preferred, as it is far easier to read and to understand.Modifying the Value of a Variable

It is possible to express modifying the value of a variable in your recipe in a number of different ways. For example, to increment the value of the variable "num_students", you might write something such as
```
set the value of the variable num_students to be the current value of the variable num_students plus 1.
```

However, that is quite verbose and takes some degree of mental effort to read it, parse it, and understand what it is trying to say. That same modification could be much more concisely and clearly expressed as
```
num_students ← num_students + 1
```

The latter is highly preferred, as it is far easier to read and to understand.Calling a Recipe

From within one recipe, you can call another (or the same) recipe, for example because you are calling a helper recipe or because you are doing recursion; this call can be expressed in a number of different ways. For example, suppose you have a helper recipe named "CountStudentsWithName" that counts the number of students in class that have a specified first name and last name. To call this recipe, you might write something such as
```
set name_count to the value returned by calling CountStudentsWithName with the inputs first_name and last_name.
```

However, that is quite verbose and takes some degree of mental effort to read it, parse it, and understand what it is trying to say. That same call could be much more concisely and clearly expressed as
```
name_count ← CountStudentsWithName(first_name, last_name)
```

The latter is highly preferred, as it is far easier to read and to understand.