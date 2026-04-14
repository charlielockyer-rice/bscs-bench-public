# Assignment 4: Imperative Jam

## Overview

**Testing:** Use `bin/grade` or the `grade` tool to run the test suite.

**Prerequisite:** This assignment builds on Assignment 3. A complete solution to Assignment 3 is provided in the `assignment-3-solution-src/` folder. You must extend the Jam language from Assignment 3 by implementing new constructs that support mutable cells. The tests in **Assign4Test.java** exercise these extensions, but are not comprehensive.

## Overview

**Task** Your task is to extend and modify the nine interpreters from Project 3 (using either your solution or the posted class solution for Project 3) to support imperative programming by adding ML-style reference cells and blocks. In particular, you will add the unary operators `ref` and `!`, the binary assignment operator `<-`, and *blocks* consisting of non-empty sequences of expressions enclosed in braces (`{` and `}`) and separated by semicolons (the final expression in the sequence does NOT have a semicolon following it). In other words

```
Exp           ::= ...
                  | Block
Block         ::= '{' PropStmtList '}'
PropStmtList  ::= Exp { ; Exp}*
Unop          ::= ... | ref | !
Binop         ::= ... | <-
Prim          ::= ... | ref?
```

Note that a `PropStatList` is identical to a `PropExpList` except for the separator symbol `;` instead of `,`. You may want to exploit the commonality in your parser. We will often use the term "Stmt" instead of "Exp" because with the addition of mutable cells, evaluating an expression can modify program state (the contents of mutable cells). From a strictly syntactic viewpoint, there is no difference between statements and expressions. But we still try to use the term *statement* to refer to expressions where mutation is common and the term expression (more accurately *pure expression*) where no mutation is necessary in well-written code.

You must modify your lexer and parser to accept `Block`s as well as the new unary operators `ref` and `!`, the new binary operator `<-`, and the new primitive function `ref?`. The parser and lexer for Assignment 3 already include commented-out code that supports parsing Assignment 4 Jam with the exception that the lexer must recognize `!` followed by any character other than `=` is the unary prefix operator `!` rather than a `ParseException`.

**Adding ref cells and assignment** The unary operation `ref E` evaluates the expression `E` to produce an arbitrary value `V`, creates a new box `b` holding `V` and returns the box `b`.

The unary operation `! E` evaluates the expression `E` to produce a value which must be a box and returns the contents of the box. If the value of `E` is not a box, the interpreter generates a run-time error (an `EvalException`).

The binary operation `E1 <- E2` evaluates the expression `E1` to produce a value `V1` that must be a box, evaluates `E2` to produce an arbitrary value `V2`, stores `V2` in box `V1`, and returns the special value `JamUnit` (which is a *legal Jam value* that is rejected by most primitive operations as an invalid input). If `V1` is not a box, then the interpreter must generate a run-time error. `JamUnit` can conveniently be represented as singleton subclass of `JamVal`. If you follow this recommendation, you may need to modify the `JamValVisitor` interface to include a `forXXXX` method for `JamUnit`. (The class solution avoid this complication since it never uses the `JamValVisitor` interface. In your solution, you can delete this interface if it not used. We recommend that you use a different representation (from `JamUnit`) for the dummy binding value in a recursive `let`, and classify any attempt to access this value in the environment as a run-time error. Since attempting to access this dummy value is a run-time error, the representation of this dummy value does not need to be a `JamVal`. The `null` pointer in Java can be used to represent the dummy bound value in the implementation of recursive let; you can also introduce a special object (as long as it is not a `JamVal`) for this purpose.

The special value `JamUnit` can bound to a variable, returned by a `map`, and appear as an expression in a block. We recommend throwing an `EvalException` if `JamUnit` is passed as the input to any operation other than the block construct, `=`, and type recognizers like `number?`. Scheme is more liberal in its treatment of the `(void)` value (which is the Scheme analog of `JamUnit`). Scheme supports embedding `(void)` inside any data structure. You can follow this option in implementing Jam if you choose. (You can use **DrRacket** as a guide.) The behavior of `JamUnit` will be tested in our grading suites other than its return as the value of a block.

A box (reference cell) is a Jam value and can be returned as the result of an expression. When converted to a string, the output of a box should appear as `(ref <value>)`. This expression

```
let x := ref 10; in {x <- ref 17; x}
```

should evaluate to the `JamVal` that is displayed as the string

```
(ref (ref 17))
```

in call-by-value and call-by-need. In call-by-name evaluation, your interpreter should display the string

```
(ref 10)
```

since the binding of `x` is re-evaluated for every occurrence.

You will have to modify your `toString` method to support the correct display of boxes. Equality for boxes is based on the address of the box, not the contents, i.e. the expression

```
let x := ref 10; y := ref 10; in x = y
```

evaluates to `false` regardless of the semantics for let (call-by-value, call-by-name, call-by-need).

The primitive function `ref?` acts like all other type recognizers, e.g., `number?`, in that it accepts one argument, which can be of any type, but only returns `true` if that argument is a box, and `false` in all other cases.

**Adding blocks** The block `{ e1; ...; en }`, *n > 0*, evaluates `e1`, ..., `en` in left-to-right order and returns the value of `en`. The expressions `e1`, ..., `en` may evaluate to `JamUnit` (returned by the assignment operator) without aborting the computation. In the Jam grammar, a `Block` is an additional form for an expression analogous to `map`, `if`, and `let`. Hence, it cannot appear as the first argument in a binary expression unless it is enclosed in parentheses. The "empty block" is a parser error (a `ParseException`).

**Optional extra credit (10 points)** Devise a small set of programs that exhibits different behavior for eight of the nine modes of interpretation and run them (unless they diverge) from a unit test suite. For any test that diverges result include a commented out test case with an explanation. Your programs should not generate any run-time errors.

**Testing** You are expected to write unit tests for all of your non-trivial methods or functions *and submit your test code as part of your program*. For more information, refer back to Project 1.

## Testing Your Program

Make sure that your program passes the sample unit tests in **Assign1Test.java**, **Assign2Test.java**, **Assign3Test.java**, and **Assign4Test.java**. You need to thoroughly test the interpretation of each new construct, notably boxes and mutation.

Each class and method in your program should be preceded by a short **javadoc** comment stating precisely what it does.

Please make sure to remove or disable all debugging output generated by your program. Excessive use of print statements considerably slows down your interpreter.

---

## Implementation Hints

1. **Lexer**: Search for commented-out code handling `!` and `{`/`}`. The `!` token needs special handling: `!=` is not-equals, but `!` alone is dereference.

2. **Parser**: Look for commented-out `parseBlock()` or similar. Block parsing is like `parseExpList()` but uses `;` separator.

3. **AST**: Add a `Block` class (holds `AST[]` expressions). Add `JamRef` class to ValuesTokens.java for box values. Add `JamUnit` singleton.

4. **Interpreter**: Add visitor cases for `ref` (create new JamRef), `!` (return JamRef contents), `<-` (mutate JamRef, return JamUnit), and `Block` (eval all, return last).

5. **Key insight**: `JamRef` uses Java's reference equality (don't override `equals()`), so two `ref 10` expressions create different boxes.

---
*COMP 411: Principles of Programming Languages, Rice University*
