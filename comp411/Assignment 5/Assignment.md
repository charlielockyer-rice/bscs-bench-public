# Assignment 5: Typed Jam

## Overview

**Testing:** Use `bin/grade` or the `grade` tool to run the test suite.

**Prerequisite:** This assignment builds on Assignment 4. You must start with a working solution to Assignment 4 (use the solution provided with Assignment 4 if needed) and revise the code to add static typing. A skeleton JUnit test file **Assign5Test.java** is provided.

## Overview

**Task** Your assignment is to convert Jam to a statically typed programming language.

**Step 1** As the first step in the project, you will strip your solution for Project 4 down to a family of two interpreters: call-by-value/eager (value/value) and call-by-value/lazy (value/need).

The public interface to the interpreter is the same as in Assignment 4 except for the fact the the **Interpreter** class will only support two public methods: `eagerEval()` and `lazyEval()` which correspond to `valueValue()` and `valueNeed()` in Assignment 4.

Save your solution to this step in a separate directory. You will use it as the starting point for Project 6.

**Step 2** As the second step of the assignment, you will modify the parser and AST representation to:

- eliminate the primitives `list?`, `number?`, `function?`, `ref?`, and `arity` from the language syntax (because they are not typable in a conventional static type system);
- require the syntax `Id : Type` for *binding occurrences* of variables, which occur on the left-hand side of definitions and in the parameter lists of `map` expressions;
- require the syntax `empty : Type` in place of `empty`, where `Type` indicates the element type of the list type to which `empty` belongs;
- force any `Factor` that is a `Prim` to be followed by an application argument list. This restriction prevents a `Prim` from being used as a general value.

Hence, the grammar looks like this:

*Expressions*

```
Exp               ::= Term { Binop Exp }
                    | if Exp then Exp else Exp
                    | let Def+ in Exp
                    | map TypedIdList to Exp
                    | "{" PropStatementList "}"
Term              ::= Unop Term
                    | Factor { ( ExpList ) }
                    | Prim ( ExpList )
                    | Empty
                    | Int
                    | Bool
Factor            ::= ( Exp ) | Id
ExpList           ::= { PropExpList }
PropExpList       ::= Exp | Exp , PropExpList
TypedIdList       ::= { PropTypedIdList }
PropTypedIdList   ::= TypedId | TypedId, PropTypedIdList
PropStatementList ::= Exp | Exp ; PropStatementList
TypedId           ::= Id : Type
```

*Definitions*

```
Def               ::= TypedId := Exp ;
```

*Primitive Constants, Operators, and Operations*

```
Empty             ::= empty : Type
Bool              ::= true | false
Unop              ::= Sign | ~ | ! | ref
Sign              ::= "+" | -
Binop             ::= Sign | "*" | / | = | != | < | > | <= | >= | & | "|" | <-
Prim              ::= empty? | cons? | cons | first | rest
```

*Identifiers*

```
Id ::= AlphaOther {AlphaOther | Digit}*
```

Please note that `Id` does *not* contain anything that matches `Prim` or the keywords `if`, `then`, `else`, `map`, `to`, `let`, `in`, `empty`, `true`, and `false`.

*Types*

```
Type              ::= unit | int | bool | list Type | ref Type | (TypeList -> Type)
TypeList          ::= { PropTypeList }
PropTypeList      ::= Type | Type, PropTypeList
```

*Numbers*

```
Int ::= Digit+
```

Note that there are three type constructors `list`, `->`, and `ref`. You will need to define an abstract syntax for types.

You are not responsible for producing slick error diagnostics for syntax errors. You should still prohibit the omitted primitive operations recognized by the lexer/parser from being used as variable names.

**Step 3** Before you can interpret a typed program, you must type check it.

Define a method in the **Interpreter** class called `checkTypes` that takes an expression (an AST) as an argument, type checks it, and returns an expression suitable for interpretation. You can simply use inheritance to define an AST representation for typed program syntax that extends the untyped AST representation. If your type checker encounters a type error, it should abort the computation by throwing a **TypeException** with an error message explaining the type error. **TypeException** looks like this:

```java
class TypeException extends RuntimeException {
    TypeException(String msg) { super(msg); }
}
```

The top-level methods `eagerEval()` and `lazyEval()` should first invoke the parser, then the context-sensitive syntax checker from Assignment 3, then the type checker, and finally the appropriate evaluation method.

The type-checking method should rely on the same tree-walking methods that you developed for checking syntax in Assignment 3. It behaves like an "abstract" interpreter that takes a type environment (mapping variables to types) as input and returns a type instead of a value as output. Note that the primitive operations and operators all have types. Moreover, the primitives `cons`, `first`, `rest`, `empty?`, and `cons?` and the operators `!`, `ref`, `<-`, `=` have schematic types that must be matched against their contexts to determine how they are instantiated:

```
cons      : 'a , list 'a -> list 'a
first     : list 'a      -> 'a
rest      : list 'a      -> list 'a
ref       : 'a           -> ref 'a
empty?    : list 'a      -> bool
cons?     : list 'a      -> bool
!         : ref 'a       -> 'a
<-        : ref 'a , 'a  -> unit
=         : 'a , 'a      -> bool
!=        : 'a , 'a      -> bool
```

In these type declarations, the symbol `'a` stands for any specific type (a specific type cannot contain the special symbol `'a`). Only these primitives and binary operators have polymorphic type; every program-defined function must have a specific (non-polymorphic) type. The symbol `'a` designating an undetermined type never appears in actual program text.

To simplify the type checking process, the syntax prohibits these polymorphic operations from being used as data values (stored in data structures, passed as arguments, or returned as results). Hence, to use `cons` as a data value, you must wrap it in a `map` which forces it to have a specific type. Hindley-Milner polymorphism (used in ML) is slightly more flexible; it allows you to use polymorphic operations as data values, but their reconstructed types must not be polymorphic!

From the perspective of type-checking, the `if-then-else` construct behaves like a ternary polymorphic function with type:

```
if-then-else : (bool, 'a, 'a -> 'a)
```

The remaining primitives and operators have the following specific types:

```
(unary) -  : (int -> int)
~          : (bool -> bool)
+          : (int, int -> int)
(binary) - : (int, int -> int)
*          : (int, int -> int)
/          : (int, int -> int)
<          : (int, int -> bool)
<=         : (int, int -> bool)
>          : (int, int -> bool)
>=         : (int, int -> bool)
&          : (bool, bool -> bool)
|          : (bool, bool -> bool)
```

Some type correct applications of primitives can still generate run-time errors because the collection of definable types and type-checking machinery are too weak to capture the domain of primitive functions precisely. For example, the following expressions are all type-correct:

```
1/0
first(empty : int)
rest(empty : int)
```

To type the applications of polymorphic primitives and operators, your type checker will need to match their schematic types against the types the checker produces for their inputs. Every program sub-expression other than one of the schematic primitives or operators has a specific (non-schematic) type. We augmented `empty` with type annotation to obtain this property.

Consider the following example. Assume that the type environment assigns `x` the type `int`, then the unary operator `ref` in the application `ref x` has type `(int -> ref int)`.

## Testing

You are expected to write unit tests for all of your non-trivial methods or functions *and submit your test code as part of your program*. For more information, refer back to Assignment 1.

Test that your type checker works and produces code suitable for your interpreter. As usual we are providing a sample Unit testing suite **Assign5Test.java** to help you get started on writing your unit tests and sample grading scripts so you can test that your program supports the proper interfaces before submitting it for grading.

## Testing Your Program

JUnit test files from earlier assignments will generally fail because the Assignment 5 interpreter supports different (and fewer) evaluation methods. You need to add more test cases to **Assign5Test.java**. Test that your type checker works and produces code suitable for your interpreter.

Each class and method in your program should be preceded by a short **javadoc** comment stating precisely what it does.

Please make sure to remove or disable all debugging output generated by your program. Excessive use of print statements considerably slows down your interpreter.

---

## Implementation Hints

1. **Type AST**: Create a `Type` interface with classes: `IntType`, `BoolType`, `UnitType`, `ListType(Type elem)`, `RefType(Type elem)`, `FunType(Type[] params, Type result)`. Use singletons for `IntType`, `BoolType`, `UnitType`.

2. **Step 1 simplification**: Keep only `valueValue()` and `valueNeed()` evaluation logic, rename to `eagerEval()` and `lazyEval()`. Delete the other 7 modes.

3. **Parser changes**: Modify `Def` and `Variable` to include a `Type`. Parse types after `:` in bindings. Parse `empty : Type` as a typed empty list.

4. **TypeChecker pattern**: Create a visitor that takes a type environment `Map<String, Type>` and returns a `Type`. For each AST node, check operand types match expected types, return result type.

5. **Polymorphic matching**: For `cons(x, y)` where `y : list int`, infer `'a = int`, so result is `list int`. For `first(x)` where `x : list bool`, result is `bool`. Match the concrete type against the schema to find `'a`.

6. **TypeException**: Throw when types don't match, e.g., `if 5 then ...` (condition not bool), `1 + true` (operands not int).

---
*COMP 411: Principles of Programming Languages, Rice University*
