# Assignment 4xc: Imperative Jam (Algol-style)

## Overview

**Testing:** Use `bin/grade` or the `grade` tool to run the test suite.

**Prerequisite:** This assignment builds on Assignment 3. You must extend and revise a solution to Assignment 3 to support a dialect of Jam with assignable variables like Algol 60. For simplicity, all procedure parameters are passed by value and the `cons` operation is eager. The behavior of purely functional call-by-value/eager code is unaffected by these revisions, but the implementation of evaluation must be subtly revised to support the semantics of assignable variables. The tests in **Assign4xcTest.java** exercise the imperative behavior.

## Overview

**Task** Your task is to extend and modify the call-by-value/eager interpreter from Project 3 (using either your solution or the posted class solution for Project 3) to support an "assignable variable" model of mutability that is more general than the standard Algol model. Algol-like Jam is call-by-value/eager Jam from Assignment 3 augmented by (i) a binary assignment operator `<-`, and optional `ref` modifiers in front of `map` parameters and `let` variables and (ii) compound statements consisting of sequences of expressions called *blocks*. (Blocks have the same syntax and semantics as in Assignment 4.) The only assignable variables in Algol-like Jam are those let variables and procedure parameters that are explicitly declared as `ref` variables. As in Algol, no dereference operator is required because assignable variables (boxes) are coerced to their contents in "right-hand" contexts.

We suggest that you use your lexer and parser from Assignment 4 as the basis for building the lexer and parser for this assignment. In contrast to the Imperative Jam language introduced in Assignment 4, the unary operators `ref`, `ref?`, and `!` *do not* exist in Algol-like Jam. There are no `ref` values other than the values of `ref` variables appearing in left-hand contexts.

## Adding Assignable Variables, Assignment, and Blocks

The binary operation `e <- E` "left-hand" evaluates the expression `e` to produce a value `v` that must be a box, "right-hand" evaluates `E` to produce an unboxed value `V`, stores the value `V` in the box `v`, and returns the special "unit" value `JamUnit` (akin to `JamUnit` in Assignment 4) which is distinct from the "undefined" value used in recursive `let`. This value cannot be stored as the contents of a box, but may otherwise appear anywhere in a computation.

The block `{e1; ...; en}`, *n* > 0, evaluates `e1`, ..., `en` in left-to-right order and returns the value of `en`. Note that any of the expressions `e1`, ..., `en` may evaluate to the "unit" value without aborting the computation. In the Jam grammar, a `Block` is an additional form for an expression analogous to `map`, `if`, and `let`. Hence, it cannot appear as the first argument in a binary operation unless it is enclosed in parentheses. The "empty" block is a syntax error.

**Testing** You are expected to write unit tests for all of your non-trivial methods or functions *and submit your test code as part of your program*. For more information, refer to the instructions for testing Projects 1, 2, and 3. If you wrote comprehensive tests for Project 3, you should not have to write too many additional tests to cover this project.

**Testing Interface** The interface used by our test code is identical to that in Project 3 except for the fact that our test code will only invoke one evaluation method, named `eval()`, on `Interpreter` objects instead of `valueValue()`, `nameValue()`, *etc*.

## Left and Right Hand Evaluation

The Algol-like dialect of Jam avoids the need for an explicit dereferencing operator (the unary `!` operator used in Project 4 to get the contents of a `ref`) by coercing boxed values to their contents when they occur in right-hand contexts.

Right-hand contexts include the following expressions:

1. Heads of function applications (i.e., the "rator" in a function-call expression).
2. Arguments to primitive operations and operators, other than the first argument to `<-` as described above.
3. Argument expressions bound to non-`ref` parameters in `map` applications.
4. Right-hand sides of definitions of non-`ref` variables in `let` constructions.
5. Test expressions in conditionals.
6. Top-level expressions (i.e., the expression corresponding to the AST root in the program passed to the interpreter).

A left-hand context is any context that is not a right-hand context. Left-hand contexts include the following expressions:

1. Right-hand sides of definitions of `ref` variables in `let` constructions.
2. Argument expressions bound to `ref` parameters.
3. Left-hand sides of assignments.
4. Bodies of `let` constructions and procedures.
5. The *consequent* and *alternative* expressions in conditionals.
6. Each of the expressions forming a block.

Note that the bodies of compound expressions—like let construction and procedure (`map`) applications—are effectively right-hand evaluated when the containing compound expressions appear in right-hand contexts.

This set of left-hand contexts is more extensive than in Pascal. In particular, the last three contexts given above (iv–vi) are not treated as *left-hand* contexts in Pascal. In Algol-like Jam, a procedure call can return an assignable variable to a left-hand context—which is impossible in Pascal.

In this Jam dialect, a `ref` variable definition

```
ref x := y
```

creates a new variable `x` that is synonymous with `y` if `y` is a `ref` (box) (synonymy means both variables are bound to the same box); otherwise, it boxes the value of `y` and binds that box to `x`. So variables declared as `ref`s are always bound to boxes.

An assignment expression

```
x <- y
```

"left-hand" evaluates `x`, confirms that `x` is bound to a `ref` (box), "right-hand" evaluates `y` (i.e., "left-hand" evaluates `y`, then coerces it to a non-reference if necessary), and stores this result in the box `x`. Note that it is impossible for a `ref` (box) to have a `ref` (box) as its value because the value stored into a ref is always dereferenced!

The following program illustrates the intended syntax for this language.

```
let
  ref x := 20;
  ref y := 5;
  z := 10;
  swap := map ref x, ref y to
            let z := x;
            in {x <- y; y <- z};
in
  {x <- (x + y);
   swap(x,y);
   swap(x,z);
   x * y * z}
```

In the body of the `let`:

- `x` is assigned the value 25,
- the values of `x` and `y` are swapped leaving `x` with the value 5 and `y` with the value 25,
- the values of `x` and the box `ref z` (since `z` is not a box, it is coerced to one by wrapping it in a `ref`) are swapped leaving `x` with the value 10, and
- the value 10*25*10 = 2500 is returned.

Note that the binding of a variable *never changes*; only the contents of boxes change. The "values" of `x` and `y` change because they are both bound to boxes and the contents of those boxes are changed by assignments. In contrast, the value of `z` cannot change because `z` is not bound to a box. To perform a swap involving `z`, the program places `z` inside a box (which becomes inaccessible once the call on `swap` returns).

**Hint** Left-hand evaluation is the fundamental mode of evaluation. This form of evaluation does not necessarily produce a `ref` as its result. Given an implementation of left-hand evaluation, it is trivial to generate a corresponding right-hand evaluator by coercing the result, if it is a `ref`, to its contents (which cannot be a `ref`).

**Extending Algol-like Jam to a more realistic language** Algol-like Jam does not include any mutable data structures like arrays or mutable records. When these are added, Jam must be able to distinguish the components that are assignable fields and those that are not. Assignable fields should behave exactly like assignable variables. Such an extension is beyond the scope of this assignment.

## Testing Your Program

The provided test files include some very simple unit tests. These tests are *far* from comprehensive. Make sure that your program passes the sample unit tests.

Each procedure or method in your program should include a short comment stating precisely what it does. For routines that parse a particular form of program phrase, give grammar rules describing that form.

Please make sure to remove or disable all debug output. Excessive use of print statements considerably slows your interpreter.

---
*COMP 411: Principles of Programming Languages, Rice University*
