# Creating COMP 341 HW Test Suites (Playbook)

Proven process for creating the full test infrastructure for a COMP 341 homework. Used successfully for HW3; repeat for HW4-7.

## Inputs Needed

1. **Colab notebook** in `comp341/notebooks/` — the original assignment
2. **Rubric** in `comp341/rubrics/hw{N}_rubric.md` — written question grading criteria
3. **Writeup template** in `comp341/templates/hw{N}/writeup.md` — already exists for all HWs
4. **Config** in `comp341/configs/hw{N}.yaml` — already exists with function list and test_points
5. **Actual data** — download via `bin/setup_comp341_data` or manually; **check column names against actual CSV**

## Steps

1. **Read the Colab notebook** thoroughly to understand the assignment flow
2. **Check the actual data** — verify column names, dtypes, missing value format. Never trust the notebook or rubric — always check the CSV headers directly.
3. **Create three files in parallel** (use Opus dev-implementer subagents):
   - `comp341/templates/hw{N}/instructions.md` — agent-readable instructions extracted from notebook. Include exact function signatures, return types, key parameters (random_state, test_size, etc.)
   - `comp341/templates/hw{N}/solution.py` — stub file with function signatures, docstrings, type hints, and `pass`. Include all necessary imports.
   - `comp341/tests/test_module{N}.py` — pytest test suite matching the `test_points` in the config yaml
4. **Cross-validate all three files** — ensure function signatures, return types, and parameter names are consistent across instructions, solution template, and tests. This is critical — mismatches between the three files are the #1 source of bugs.
5. **Update config** — set `total_tests` to actual test count after writing tests
6. **Run code review** — check for sklearn version compatibility (e.g., `penalty=None` not `'none'`), correct column names, reasonable accuracy thresholds for imbalanced data
7. **Validate with an agent** — launch an Opus subagent to attempt the HW in Docker, observe if it passes, fix any issues found

## Key Patterns

- **Test fixture chaining** with module scope for efficiency: `raw_df → features_labels → train_test_data → imputed_data → encoded_data → scaled_data`. Each fixture builds on the previous one so expensive operations (data loading, model training) only run once.
- **Accuracy thresholds**: Be careful with imbalanced datasets — a 0.5 threshold is meaningless when predicting all-negative gives 95%. Set thresholds just below the majority-class baseline.
- **One-hot encoding tests**: Check that train/test have consistent columns, not just that encoding happened.
- **The config's `test_points` dict is the source of truth** for test names and point values. Test class method names must match these names exactly.
- **test_module{N}.py naming** — must match this pattern for the framework to find them.
- **Reference existing HW tests** (hw1-3) for patterns — consistency across HWs makes the codebase easier to maintain.

## Common Pitfalls

- **Column names differ from docs**: CSV headers may not match what notebooks/rubrics say. Always `head -1` the actual CSV.
- **sklearn 1.4.0 compatibility**: Docker pins sklearn 1.4.0. Use `penalty=None` (Python None), NOT `penalty='none'` (string). Check other version-sensitive APIs.
- **Missing value encoding**: Different datasets use different missing value markers ("N/A", "", NaN, "?"). Check what the CSV actually contains and handle in `pd.read_csv` with `na_values`.
- **Config test_points counts**: The existing hw4-7 configs have `test_points` entries from initial planning. These may not match the final test suite — always reconcile and update `total_tests` after writing tests.

## Validation Checklist

Before marking a HW as complete:
- [ ] All three files (instructions.md, solution.py, test_module{N}.py) are consistent
- [ ] `total_tests` in config matches actual test count
- [ ] An Opus agent can solve it from scratch with 100% pass rate
- [ ] Docker environment works (data mounts, imports, timeouts)
- [ ] Written questions in writeup.md are answerable from the code results
- [ ] Rubric column names match actual data
