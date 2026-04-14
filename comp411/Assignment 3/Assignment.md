# Assignment 3: Lazy Evaluation and Recursive Definitions

## Overview

**Testing:** Use `bin/grade` or the `grade` tool to run the test suite.

**Prerequisite:** This assignment builds on Assignment 2. The provided files include a complete solution to Assignment 2. You are welcome to use your own solution to Assignment 2 instead, but your solution should pass the tests in **Assign3Test.java** to ensure that you are supporting the correct API.

## Overview

**Task** Your task is to extend and modify your interpreter from Assignment 2 or fill in the stub files provided in the Starter Repository to:

* perform the context-sensitive checking required to confirm that the input program is a legal program;
* enforce safety in Jam programs by detecting illegal operations before they are executed triggering a Java run-time error. (No meta errors!)
* change the semantics of the `let` construct to support recursive bindings ("recursive let"); and
* support the lazy evaluation of the `cons` constructor using both the call-by-name and the call-by-need implementation of lazy evaluation.

The details of the each of these extensions is described in the following paragraphs.

**Context-Sensitive Checking** The context sensitive constraints on Jam syntax are most easily by performing a separate "checking pass" over the abstract syntax right after it has been constructed. There are two context-sensitive conditions that Jam programs must satisfy to be well-formed:

* There are no free variables.
* The same variable name cannot appear twice in the same parameter list (of a `map`) or the same collection of let names (introduced in a `let` binding).

If you find either of these errors, throw a `SyntaxException` with a descriptive error message and abort execution.

The context-sensitive checker should be run before the expression is evaluated. Note that the context-sensitive checker does not care which unary or binary operator was used, or how many arguments were passed to a primitive function.

**Safety** Make your interpreter responsible to catching all Jam run-time errors. Do not rely on the run-time checking performed in executing Java code (meta-errors). All Jam run-time error messages should be of type `EvalException` generated directly by your interpreter.

Note: It is not enough to just wrap the entire interpreter in a `try { ... } catch(Throwable t) { ... }` block. The exception must be thrown when the Jam run-time error occurs.

Safety checking obviously includes confirming that operators are applied to arguments of the proper type. Hence, the unary operators `+` and `-` and the binary operators `+`, `-`, `/`, `*`, `<`, `<=`, `>`, and `>=`, may only be applied to integer values. Similarly, the unary operator `~` and binary operators `&` and `|` may only be applied to boolean values. In general, you must check that every language construct is applied to the correct number and type of values including checking that the test value in a Jam `if` is a boolean value. Many of these checks were already stipulated as part of the semantics of Jam constructs given in Assignment 2. In no case should your interpreters generate run-time Java errors other than insufficient machine resource errors such as **StackOverflow** or **OutOfMemory** (heap overflow) errors, which are *not* classified as meta-errors. Of course, you are welcome to catch the corresponding exceptions and report your own errors.

**Lazy cons** For all three of your interpreters from Assignment 2, you will add two lazy `cons` evaluation options that defer the evaluation of both arguments of a `cons` construction. The data object representing the deferred evaluation is called a *suspension*. Given a `cons` node containing suspensions, the `first` and `rest` operations evaluate the corresponding argument expressions in the environment where the expressions would have been evaluated without laziness.

Lazy `cons` obeys the following equations:

```
first(cons(M,N)) = M
rest(cons(M,N)) = if list?(N) then N else error
```

for all Jam expressions `M` and `N`, including expressions that diverge or generate run-time errors. Recall that `list?` is simply the disjunction of `null?` and `cons?`.

The lazy versions of `cons` postpone evaluation of argument expressions until they are demanded by a `first` or `rest` operation. You can use exactly the same suspension mechanism to support call-by-name and call-by-need lazy `cons` that you might have used to support call-by-name and call-by-need bindings in the previous assignment. In this case, the suspensions are stored in a `cons` structure rather than a binding structure the environment. An embedded suspension is not evaluated until it is "probed" by a `first` or `rest` operation. Call-by-name lazy evaluation re-evaluates suspensions every time that they are probed. Call-by-need lazy evaluation evaluates a given suspension only once and caches the resulting value in the data constructor (`cons` cell in the case of Jam).

Please change the function that converts Jam values to a string representation (toString, string_of_value, etc.) so that lists are still displayed as lists in the form `(1 2 3)` as before, regardless of the cons mode used. You may want to abort processing lists longer than, say, 1000 elements and have them displayed ending in `998 999 1000 ...`). However, we will not test this behavior. We will just require that lists up to 200 elements are displayed the same way as in the last assignment.

**Recursive Let** The recursive version of the `let` construct introduces a collection of recursive bindings just as we discussed in class. We will not restrict the right hand sides of recursive `let` constructions to maps because many useful recursive definitions in Jam with lazy cons violate this restriction. Hence, the implementation of recursive `let` closely follows the implementation of the generalized version of `rec-let` construct in LC.

The only difference is that Jam recursive `let` may introduce several mutually recursive definitions where the corresponding closures all close over the new environment. Since we want invalid forward references in a Jam program to produce a meaningful error message rather than diverging, we will use an explicit list to represent the environment of bindings and destructively update it to create the self-references.

In a call-by-value application, we will initially bind each variable introduced in a `let` to a special "undefined" value (e.g., Java `null`) that is not a legal Jam value representation and will abort the computation if it is used as the input to any Jam operation. The interpreter evaluates the right-hand sides of the definitions in a recursive `let` in left-to-right order and destructively modifies each new binding in the environment as soon as the right-hand side has been determined.

In a call-by-name application, we bind each variable to a suspension (a closure of no arguments or *thunk*) for each right-hand side. Since a suspension wraps the right hand side of each `let` binding inside a lambda, the recursive environment can be constructed directly using a recursive binding construction (`letrec` or `define` in Scheme). No mutation is required in languages like Scheme and ML that support general recursive binding. In languages like Java that do not support general recursive binding, we use mutable binding cells instead.

Note that the validity of definitions in a recursive `let` depends on which semantics is used for Jam. Definitions that are valid for call-by-name or call-by-need lazy evaluation may be invalid in other evaluation modes. By this measure, lazy evaluation is better than conventional ("eager") evaluation and call-by-name/call-by-need is better than call-by-value.

**Testing** You are expected to write unit tests for all of your non-trivial methods or functions *and submit your test code as part of your program*. For more information, refer back to Assignment 1.

In Assignment 2, the Interpreter supported three evaluation methods

```
callByValue()
callByName()
callByNeed()
```

In this assignment, the Interpreter class must support nine evaluation methods:

```
valueValue()
nameValue()
needValue()
valueName()
nameName()
needName()
valueNeed()
nameNeed()
needNeed()
```

where the first word in the method name specifies the policy for evaluating program-defined procedure applications (Jam `map`s) and the second word specifies the policy for evaluating applications of the data constructor `cons`. Hence the first three methods in the preceding list correspond to the three evaluation methods in Assignment 2.

As in Assignment 2, the `Interpreter` class must support two constructors: `Interpreter(String filename)` which takes input from the specified file and `Interpreter(Reader inputStream)` which takes input from the specified Reader. A given `Interpreter` object should support the repeated invocation of any of the nine public evaluator methods. As a result, the same `Interpreter` object can be used to test all forms of evaluation (as long as none of the evaluations diverges).

In summary:

```java
/** file Interpreter.java **/
class EvalException extends RuntimeException {
    EvalException(String msg) { super(msg); }
}

class SyntaxException extends RuntimeException {
    SyntaxException(String msg) { super(msg); }
}

class Interpreter {
   Interpreter(String fileName) throws IOException;
   Interpreter(Reader reader);

   public JamVal valueValue();
   public JamVal nameValue();
   public JamVal needValue();

   public JamVal valueName();
   public JamVal nameName();
   public JamVal needName();

   public JamVal valueNeed();
   public JamVal nameNeed();
   public JamVal needNeed();
}
```

**Lazy List Equality** There are two credible to checking the equality of lazy lists. Either all comparisons involving lazy lists force evaluation of the entire list (causing the evaluation to diverge if a list is infinite) which is the same semantics as for eager lists, or equality checks can short-circuit and return meaningful values on some comparisons involving infinite lazy lists. In this assignment, we will use the latter, more general, semantics.

Short-circuit equality checks are implemented by doing an element-wise comparison of the two operand lists, and returning `true` or `false` as soon as the lists are demonstrably equal or non-equal. For example:

```
let xs := cons(0, xs);
    ys := cons(0, cons(0, null));
in xs = ys
```

In the above code, we can return `false` when we are comparing the 3rd cons-cells of `xs` and `ys` above, since the `rest` of `xs` is non-null, but the `rest` of `ys` is `null`.

Equality checks can similarly short-circuit on non-equal head values, or on identical tails. For example:

```
let ones := cons(1, ones);
in ones = cons(1, ones)
```

In the above code, we can return `true` when we are comparing `rest`s of the 1st cons-cells since `ones` and `ones` are identical (i.e., they are the same object).

```
let xs := cons(1, 2);
    ys := cons(3, null);
in xs != ys
```

The above code would return `true` with short-circuiting equality checks on lazy lists (since 1≠3), whereas if `xs` is fully evaluated (either because the lists are eager, or because the equality checks are eager) then a runtime exception will be thrown due to the illegal value (2) passed as the second argument to `cons`.

Modify your implementation of Jam's `=` and `!=` operators to support short-circuit list equality, as described above, when evaluating in the by-name and by-need lazy `cons` modes.

Note that you can actually use the same evaluation strategy for eager (by-value) `cons`; however, it would only be a performance optimization in that case, whereas short-circuiting equality changes the semantics for some expressions involving lazy `cons`.

We will not test this functionality after this assignment, so we recommend deferring the more general semantics for the equality testing of lazy lists until everything else in your solution is working.

## General Implementation Issues

To produce intelligible output for computations involving lazy `cons`, your interpreter must force the complete evaluation of the answer up to a large depth bound (which is a constant in your program) but abandon evaluation after reaching this depth to avoid non-termination; if you evaluate list elements only when they are demanded (including the generation of printed output), this process unfolds naturally. The provided code defines the `toString()` operation for all lists in `JamCons` to generate depth-bounded output. MAX_DEPTH is set to 1000 but you can change it to any value you want greater than 200 for which all of your tests run in a few seconds. As a result, the output of finite lists created by call-by-value interpretation is bounded by this same depth limit. None of our test cases involves printing lists longer than this depth bound.

If a computation produces an infinite answer, the bound on printing depth prevents the forced computation from running "forever" (until the provided computational resources are exhausted). An interactive user interface that allowed the user to request the expansion of an unevaluated node (to some depth) in an answer would be more satisfactory, but building such an interface is beyond the scope of a routine programming assignment.

## Testing Your Program

Make sure that your program passes the sample unit tests in **Assign1Test.java**, **Assign2Test.java**, and **Assign3Test.java** (including those that have been "commented out" by inserting an `x` in front of the test method name). You need to add more test cases that:

* test the interpretation of each basic form of program **AST**;
* test every inductive **AST** construction; and
* test some more complex composite programs involving lists, integers, and Booleans.

Each class and method in your program should be preceded by a short **javadoc** comment stating precisely what it does.

Your test suite should ideally test every feasible control path in your code. Actual code often contains unreachable error reports. Such control paths obviously cannot be traversed by any test case.

Please make sure to remove or disable all debugging output generated by your program. Excessive use of print statements considerably slows your interpreter.

---
*COMP 411: Principles of Programming Languages, Rice University*
