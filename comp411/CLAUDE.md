# COMP 411: Programming Languages

## Course Overview
Java-based programming languages course building the **Jam** interpreter progressively across 6 assignments. Covers parsing, interpreters (call-by-value/name/need), lazy evaluation, mutable references, type systems, and continuations.

## Directory Structure

```
comp411/
├── CLAUDE.md                    # This file
├── Dockerfile                   # Docker image for Java 8 + JUnit
├── runner.py                    # Test runner (Docker-only)
├── configs/                     # Assignment YAML configs
│   ├── assignment1.yaml         # Parser
│   ├── assignment2.yaml         # Three interpreters
│   ├── assignment3.yaml         # Lazy eval + letrec
│   ├── assignment4.yaml         # Mutable references
│   ├── assignment5.yaml         # Type system
│   └── assignment6.yaml         # Continuations
├── Assignment 1/                # Parsing and Abstract Syntax
├── Assignment 2/                # Three Interpreters (CBV/CBN/CBNeed)
├── Assignment 3/                # Lazy Evaluation and Recursive Let
├── Assignment 4/                # Imperative Jam (ref cells, blocks)
│   └── assignment-3-solution-src/  # Complete Assignment 3 solution
├── Assignment 4xc/              # Extended variant
├── Assignment 5/                # Statically Typed Jam
├── Assignment 6/                # Jam with Continuations (CPS)
└── sources/                     # PDF documentation
```

## Assignment Summary

| # | Topic | Key Concepts | Builds On |
|---|-------|--------------|-----------|
| 1 | Parser | Recursive descent, AST, EBNF | - |
| 2 | Interpreters | CBV, CBN, CBNeed, closures | Assignment 1 |
| 3 | Extensions | Letrec, lazy cons, 9 eval modes | Assignment 2 |
| 4 | Imperative | ref/!/block, mutable cells | Assignment 3 |
| 5 | Type System | Static typing, polymorphism | Assignment 4 |
| 6 | Continuations | CPS transform, letcc, SDAST | Assignment 5 |

## CRITICAL: Sequential Dependencies

Assignments in this course BUILD ON EACH OTHER. Each assignment requires a working solution from the previous assignment:

- **Assignment 2** needs working Assignment 1 Parser
- **Assignment 3** needs working Assignment 2 Interpreter
- **Assignment 4** needs working Assignment 3 (9-mode interpreter)
- **Assignment 5** needs working Assignment 4 (ref cells support)
- **Assignment 6** needs working Assignment 5 (type system)

### Solution Files Provided

| Assignment | Previous Solution Included? | Source Location |
|------------|---------------------------|-----------------|
| 1 | N/A (first assignment) | - |
| 2 | YES - Parser.java included | Starter Repository |
| 3 | YES - Full Assignment 2 solution | Starter Repository |
| 4 | YES - Full Assignment 3 solution | `assignment-3-solution-src/` |
| 5 | YES - Full Assignment 4 solution | `assignment-4-solution-src/` |
| 6 | N/A - Different architecture | Starter has fresh Syntax.java + Semantics.java |

**All assignments (1-6) can now be attempted independently.** Each has the prerequisite solution bundled, and Assignment 6 uses a fresh codebase with its own architecture.

## Dependencies

### Docker (Required)
All tests run inside a Docker container with Java 8 - no local Java needed.

```bash
# Tests automatically build the image on first run
python3 comp411/runner.py assignment3 <workspace>
```

### Java Version
**Java 8** (Amazon Corretto 8) - assignments rely on Java 8 specific behavior.

### JUnit
JUnit is bundled in `comp411/lib/` to keep Docker builds offline and deterministic.

## Running Tests

```bash
# Run tests for an assignment
python3 comp411/runner.py assignment1 ./workspaces/agent_assignment1
python3 comp411/runner.py assignment3 ./workspaces/agent_assignment3 --verbose
python3 comp411/runner.py assignment4 ./workspaces/agent_assignment4 --json

# Force rebuild the Docker image
python3 comp411/runner.py assignment3 ./workspace --rebuild

# The runner will:
# 1. Build Docker image (first run only, or if --rebuild)
# 2. Compile all .java files in workspace
# 3. Run JUnit tests
# 4. Parse and report results
```

## Workspace Setup

Each workspace needs all required Java files:

**Assignment 1:**
- `Parser.java` (student implements)
- `Lexer.java`, `AST.java`, `ValuesTokens.java` (provided)
- `Assign1Test.java` (test file)

**Assignment 2:**
- `Interp.java` (student implements)
- `Parser.java` (provided - complete solution to Assignment 1)
- `Lexer.java`, `AST.java`, `ValuesTokens.java` (provided)
- `Assign1Test.java`, `Assign2Test.java` (test files)

**Assignment 3:**
- `Interp.java` (student implements)
- `Syntax.java` (new - context-sensitive checking)
- All files from Assignment 2 (with complete solution)
- `Assign1Test.java`, `Assign2Test.java`, `Assign3Test.java` (test files)

**Assignment 4:**
- `Interp.java`, `Parser.java`, `AST.java`, `ValuesTokens.java` (student implements/extends)
- Complete Assignment 3 solution provided in `assignment-3-solution-src/`
- `Assign1Test.java` through `Assign4Test.java` (test files)

**Assignment 5:**
- `Interp.java` (student implements - add type system, `eagerEval()`, `lazyEval()`, `TypeException`)
- Complete Assignment 4 solution provided in `assignment-4-solution-src/`
- `Assign5Test.java` (test file - 200 lines)

**Assignment 6:**
- `Semantics.java` (student implements CPS conversion, SD evaluation)
- `Syntax.java` (complete AST/Lexer/Parser - combined architecture, different from Assignments 1-5)
- Fresh codebase - does NOT build on Assignment 5
- `Assign6Test.java`, `SyntaxTest.java`, `Assign4Test.java` (test files - 554 + 432 + 191 lines)

## Solution Files Available

All prerequisite solutions are bundled with their respective assignments:

- **Assignment 1 Solution**: Included in Assignment 2 Starter Repository
- **Assignment 2 Solution**: Included in Assignment 3 Starter Repository
- **Assignment 3 Solution**: `Assignment 4/assignment-3-solution-src/` (complete 9-mode interpreter)
- **Assignment 4 Solution**: `Assignment 5/assignment-4-solution-src/` (adds ref cells, blocks)
- **Assignment 5 Solution**: N/A - Assignment 6 uses different architecture

## Jam Language Quick Reference

```
Exp ::= Term { Binop Exp }
      | if Exp then Exp else Exp
      | let Def+ in Exp
      | map IdList to Exp

Def ::= Id := Exp ;

Binop ::= + | - | * | / | = | != | < | > | <= | >= | & | |
Unop  ::= + | - | ~
Prim  ::= number? | function? | list? | empty? | cons? | cons | first | rest | arity
```

**Assignment 4 adds:** `ref Exp`, `!Exp`, `Exp <- Exp`, `{ Exp; ... }`

**Assignment 5 adds:** Type annotations `Id : Type`

## Test Classes

| Assignment | Test Class | Tests | Lines |
|------------|------------|-------|-------|
| 1 | Assign1Test | 5 | 61 |
| 2 | Assign2Test | 14 | 191 |
| 3 | Assign3Test | 15 | 519 |
| 4 | Assign4Test | 15 | 297 |
| 5 | Assign5Test | 12 | 200 |
| 6 | Assign6Test + SyntaxTest | 39 | 554 + 432 |

## Notes

- Tests run on JUnit 4 but use JUnit 3.x naming conventions (extend TestCase, methods named `testXxx`)
- All source files use default package (no package declaration)
- Some tests are disabled with `xtest` prefix - enable as features are implemented
- Output comparison uses `toString()` on JamVal objects
- Docker container runs with resource limits (1GB RAM, 2 CPUs) and network isolation

## Gotchas & Learnings

- **[2026-03] bench-cli sandboxing is opt-in**: `bin/bench-cli` runs on the host by default. Use `--sandbox` if you want the agent inside Docker Sandbox. Direct `runner.py` calls still work unchanged.

## Known Issues

None currently - all assignments are functional.

## Expected Baseline Test Results

When workspaces are freshly created with `bin/setup_course`:

| Assignment | Baseline | Notes |
|------------|----------|-------|
| 1 | 0/5 | Parser.java needs implementation |
| 2 | 0/14 | Interp.java needs implementation |
| 3 | 0/15 | Interp.java needs lazy eval extensions |
| 4 | 4/15 | Assignment 3 solution present; ref/block features need implementation |
| 5 | 0/12 (compile error) | Needs `eagerEval()`, `lazyEval()`, `TypeException` |
| 6 | 30/39 | Basic interpreter works; CPS/SD features need implementation |

## Assignment 6 Notes

Assignment 6 has a **different architecture** from Assignments 1-5:
- Uses `Syntax.java` (combined AST/Lexer/Parser) + `Semantics.java` (interpreter)
- NOT based on Assignment 5 solution
- Fresh codebase designed for CPS conversion and static distance coordinates
- Baseline: 30/39 tests passing (basic interpreter works, CPS/SD features need implementation)
