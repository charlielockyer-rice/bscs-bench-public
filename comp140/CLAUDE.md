# COMP 140: Computational Thinking

Python programming course from Rice University covering computational problem-solving through progressively complex projects. Uses CodeSkulptor (browser-based Python IDE) as the original teaching platform.

## Structure

```
comp140/
  Module_1/          # Circles (geometry)
  Module_2/          # Spot It! (projective geometry)
  Module_3/          # Stock Prediction (Markov chains)
  Module_4/          # Kevin Bacon (BFS/graphs)
  Module_5/          # QR Code (polynomial arithmetic)
  Module_6/          # Sports Analytics (linear regression)
  Module_7/          # Map Search (BFS/DFS/A*)
  lib/               # Helper modules (Graph classes, GUI stubs, Z256 arithmetic)
  tests/             # Test suites (~248 tests total)
  data/              # Synthetic test data (stocks, movie graphs)
  reference_solutions/ # Working solutions for verification
  solutions/         # Writeup solutions (markdown)
  Shared/            # Course resources (style guides, error references)
```

## Module Summary

| # | Name | Topic | Key Functions | Tests | Difficulty |
|---|------|-------|---------------|-------|------------|
| 1 | Circles | Geometry | `distance`, `midpoint`, `slope`, `perp`, `intersect`, `make_circle` | 25 | Easy |
| 2 | Spot It! | Projective geometry | `equivalent`, `incident`, `generate_all_points`, `create_cards` | 25 | Medium |
| 3 | Stock Prediction | Markov chains | `markov_chain`, `predict`, `mse`, `run_experiment` | 39 | Medium |
| 4 | Kevin Bacon | Graph search | `Queue` class, `bfs`, `distance_histogram`, `find_path` | 39 | Medium |
| 5 | QR Code | Polynomial math | `Polynomial` class (7+ methods), RS encoding functions | 50 | Hard |
| 6 | Sports Analytics | Linear regression | `LinearModel` class, `read_matrix`, `fit_least_squares`, `fit_lasso` | 25 | Medium-Hard |
| 7 | Map Search | Graph algorithms | `Queue`, `Stack`, `bfs_dfs`, `dfs` (recursive), `astar` | 45 | Medium |

## Key Concepts

### Template Structure

Each module template follows a pattern:
- Docstring with assignment description
- Import statements (often `math`, module-specific helpers)
- Function stubs returning placeholder values (`return 0.0`, `return {}`)
- Optional GUI demo code (commented out) at bottom

### Helper Libraries (`lib/`)

- **comp140_module*N*.py** - Module-specific helpers (GUI launchers, data loaders, Graph classes)
- **comp140_module*N*_graphs.py** - Graph implementations (Module 4 uses undirected `Graph`, Module 7 uses `DiGraph`)
- **comp140_module5_z256.py** - Z256 finite field arithmetic (`add`, `sub`, `mul`, `div`, `power`)
- **numeric.py** - Matrix operations for Module 6 (transpose, multiply, inverse)
- **CodeSkulptor stubs** - `simplegui`, `simpleplot`, `simplemap`, `codeskulptor` for local testing

### Test Framework

Tests use custom fixtures from `tests/fixtures.py`:
- `TEST_POINTS` dict maps test function names to point values
- Each test function receives the loaded module and returns `(passed, expected, actual)`
- `approx_equal()` for floating point comparisons (tolerance 1e-6)
- Graph builders for Modules 4 and 7

### Data Dependencies

- **Module 3**: Stock data files in `data/` (DJIA, GOOG, FSLR prices)
- **Module 4**: Movie graph JSON + actor lists in `data/`
- **Module 6**: Baseball statistics loaded via URLs (external) or local files

## Gotchas & Learnings

- **[2025-01] Multiple Return Values**: Several functions return tuples (`midpoint`, `intersect`, `make_circle`). Tests unpack with `result[0]`, `result[1]` pattern.

- **[2025-01] Z256 Arithmetic**: Module 5 requires ALL coefficient operations through `z256` module. Direct Python `*` and `+` on coefficients will fail tests.

- **[2025-01] Polynomial Immutability**: `Polynomial` methods must return NEW objects, not mutate `self.terms`. Common error source.

- **[2025-01] Graph Class Differences**: Module 4 uses undirected `Graph` (edges work both ways). Module 7 uses directed `DiGraph` (one-way edges). API is similar but semantics differ.

- **[2025-01] Randomness in Module 3**: `predict()` uses weighted random selection. Tests run multiple trials to verify statistical correctness. Avoid `random.choices()` per assignment spec.

- **[2025-01] LASSO Performance**: Module 6 `fit_lasso` can be slow. Pre-compute `X^T * X` and `X^T * y` outside the iteration loop.

- **[2025-01] A* Heuristic**: Module 7 `astar` receives distance functions as parameters. Use `straight_line_distance` for heuristic, `edge_distance` for actual costs.

## Decisions

- **CodeSkulptor Stubs**: The `lib/` stubs allow assignments to run locally without the browser-based CodeSkulptor environment. GUI functions are no-ops or simplified.
- **Docker-First Grading**: COMP 140 grading runs inside Docker by default via `comp140/docker_runner.py` for consistent environments.

- **Synthetic Data**: Test data is generated with fixed seeds for reproducibility. See `data/CLAUDE.md` for generator details.

- **Reference Solutions**: Located in `reference_solutions/` for verification. These are authoritative implementations used to validate test correctness.
