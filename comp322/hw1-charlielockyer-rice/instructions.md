# COMP 322 Homework 1: Functional Programming & Java Streams

**Total: 100 points**

## Overview

This assignment covers functional programming with trees and Java Streams for data processing.

## Testing Your Solution

Use the `grade` tool to run tests:

```bash
# From the workspace directory
bin/grade .

# Or from the project root
bin/grade workspaces/agent_hw1
```

Tests are run in a Docker container with Maven and HJlib pre-configured.

---

## Part 1: Written Assignment (15 points)

Write a PDF report analyzing recursive Fibonacci complexity.

### 1.1 Recursive Fibonacci (15 points)

Consider the Java code shown below to compute the Fibonacci function using recursion:

```java
public class RecursiveFib {
    public static int fib(int n) {
        if (n <= 0) return 0;
        else if (n == 1) return 1;
        else return fib(n - 1) + fib(n - 2);
    }
}
```

#### 1.1.1 Recursive Fibonacci Complexity (5 points)

What is the formula (exact answer) for the *total work* performed by a call to fib(n)? Assume that a single call to fib() (without any recursive calls inside) has a total WORK of 1. Include an explanation of the analysis, and state what expression you get for WORK(n) as a function of n. The exact answer should not be recursive.

#### 1.1.2 Memoized Fibonacci Complexity (10 points)

Consider the memoized version:

```java
public class MemoizationFib {
    private static final int MaxMemo = 1000000;
    private static final Lazy<Integer>[] memoized =
        IntStream.range(0, MaxMemo)
        .mapToObj(e->Lazy.of(()->fib(e)))
        .toArray(Lazy[]::new);

    public static int fib(int n) {
        if (n <= 0) return 0;
        else if (n == 1) return 1;
        else if (n >= MaxMemo) return fib(n - 1) + fib(n - 2);
        else return memoized[n-1].get() + memoized[n-2].get();
    }
}
```

- (5 points) What is the big-O for WORK performed by fib(n) when called for the very first time?
- (5 points) After many random calls to fib(k), what is the big-O for expected WORK of a subsequent fib(n) call?

---

## Part 2: Programming Assignment (85 points)

### 2.1 Functional Trees

Write solutions in `src/main/java/edu/rice/comp322/solutions/TreeSolutions.java`.

1. **Problem 1**: Write a recursive `sum` function to calculate the sum of all node values in a `Tree<Integer>`. No higher-order functions, mutation, or loops allowed.

2. **Problem 2**: Calculate the same sum using higher-order `GList` functions: `map`, `fold`, and `filter`.

3. **Problem 3**: Complete the higher-order `fold` function for trees. (Already implemented in `Tree.java`)

4. **Problem 4**: Use `fold` to calculate the sum of tree nodes.

### 2.2 Java Streams: Sales Data

Write solutions in `src/main/java/edu/rice/comp322/solutions/StreamSolutions.java`.

For each problem, implement both sequential and parallel versions using Java Streams API.

**Stream Operations:**

1. Calculate maximum possible revenue from February online sales (full price)
2. Get order IDs of the 5 most recently placed orders
3. Count the number of distinct customers making purchases
4. Calculate total discount for March 2021 orders

**Data Map Creation:**

5. Create mapping: customer IDs -> total amount spent (full price)
6. Create mapping: product categories -> average item cost
7. Create mapping: Tech product IDs -> customer IDs who ordered them
8. Create mapping: tier-0 customer IDs -> sales utilization rate

### Data Model

The database contains `Products`, `Orders`, and `Customers`. Access data via repositories:

```java
productRepo.findAll().stream()   // Sequential stream of products
orderRepo.findAll().parallelStream()  // Parallel stream of orders
customerRepo.findAll().stream()  // Stream of customers
```

---

## Scoring

- 60 points: Programming solutions (12 problems)
- 10 points: Coding style and documentation
- 15 points: Report comparing sequential vs parallel Java Streams performance

---

## Files to Implement

- `src/main/java/edu/rice/comp322/solutions/TreeSolutions.java`
- `src/main/java/edu/rice/comp322/solutions/StreamSolutions.java`
- `src/main/java/edu/rice/comp322/provided/trees/Tree.java` (fold method in Node class)

---
*COMP 322: Fundamentals of Parallel Programming, Rice University*
