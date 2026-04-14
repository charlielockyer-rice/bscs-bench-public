# COMP 322 Homework 2: GitHub Contributions (Parallel)

**Total: 100 points**

## Overview

Implement a concurrent version of a program that fetches and displays GitHub contributors and their total contributions across all repositories under a given organization.

## Testing Your Solution

**Important**: This homework requires GitHub API credentials.

```bash
# Set credentials before testing
export GITHUB_USERNAME=your-username
export GITHUB_TOKEN=your-github-token

# Run tests
bin/grade workspaces/agent_hw2
```

To generate a GitHub token:
1. Go to https://github.com/settings/tokens/new
2. Select the `repo` scope
3. Generate and save the token

---

## Part 1: Written Assignment (30 points)

### 1.1 Parallelizing the IsKColorable Algorithm (25 points)

Consider the pseudo-code of the **IsKColorable** algorithm:

```
Input: Graph g = (V, E) and k in N.
Output: Does there exist f : V -> [k] such that for all {u, v} in E, f(u) != f(v)?

1  foreach Assignment f of a value in [k] to each node in V do
2      colorable <- True;
3      foreach {u, v} in E do
4          if f(u) = f(v) then
5              colorable <- False;
6              break;
7      if colorable = True then
8          return True;
9  return False;
```

1. (10 points) Create a parallel version using finish and async statements to maximize parallelism while ensuring correctness.

2. (5 points) What is the big-O for total WORK?

3. (5 points) Can your parallel algorithm have **larger** WORK than sequential? Explain.

4. (5 points) Can your parallel algorithm have **smaller** WORK than sequential? Explain.

5. (5 points) Is a data race possible? If so, is it benign?

---

## Part 2: Programming Assignment (70 points)

### Goal

The sequential version (`loadContributorsSeq`) causes the UI to freeze when loading. Implement `loadContributorsPar` with these requirements:

1. UI must NOT freeze when loading data
2. All requests and processing must be performed concurrently

### Requirements

- Use **futures**, **data-driven tasks**, and **streams**
- Do NOT use `finish` or `async`
- All aggregation and post-processing via concurrent streams

### Test Organizations

- revelation
- galaxycats
- moneyspyder
- notch8
- trabian
- collectiveidea
- edgecase

### Implementation

Edit `src/main/java/edu/rice/comp322/ContributorsUI.java`:
1. Implement `loadContributorsPar` in the `LoadContributors` interface
2. Change line 65 from `loadContributorsSeq` to `loadContributorsPar`

---

## Scoring

- 50 points: Parallel solution (no UI freeze, concurrent data loading)
- 5 points: Coding style and documentation
- 15 points: Report explaining:
  - Parallel algorithm summary
  - Correctness and data-race-free justification
  - Expected work value as function of N

---

## Files to Implement

- `src/main/java/edu/rice/comp322/ContributorsUI.java` (implement `loadContributorsPar`)
- `src/main/java/edu/rice/comp322/LoadContributors.java` (parallel method)

---
*COMP 322: Fundamentals of Parallel Programming, Rice University*
