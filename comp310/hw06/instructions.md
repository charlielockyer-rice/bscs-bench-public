# HW06 - Extended Visitor Ballworld + List Visitors

## Design Pattern

This assignment introduces two major concepts:

1. **Extended Visitor Pattern**: Ball objects become typed "hosts" (DefaultBall,
   FollowerBall, LeaderBall, GangsterBall) that accept "visitor" algorithms
   (`IBallAlgo`). Each visitor can define different behavior for different ball
   types, enabling type-dependent processing without `instanceof` checks.

2. **List Visitor Framework**: Functional-style list processing using the Visitor
   pattern. Lists are either empty (`MTList`) or non-empty (`NEList`), and
   algorithms (`IListAlgo`) provide `emptyCase` and `nonEmptyCase` methods.

## Architecture Overview

### Ballworld with Extended Visitors

```
Controller
├── BallModel (model)
│   ├── IDispatcher<IBallCmd>
│   ├── Typed Ball Hosts (IBall extends IBallHost<IBall>)
│   │   ├── DefaultBall - standard ball
│   │   ├── DynamicBall - changeable type (new, to implement)
│   │   ├── FollowerBall - follows leaders
│   │   ├── LeaderBall - attracts followers
│   │   └── GangsterBall - chaotic behavior
│   ├── Ball Visitors (IBallAlgo<R, P>)
│   │   ├── AConfigBallAlgo - abstract config algo with model adapter
│   │   ├── OneTypeBallAlgo - same behavior for all types (to implement)
│   │   ├── SameTypeBallAlgo - type-specific config (to implement)
│   │   ├── LeadConfigAlgo - leader-follower dynamics
│   │   ├── FishWorldConfigAlgo, SolarSystemConfigAlgo, etc.
│   │   └── Each defines per-type cases for Default, Follower, Leader, Gangster
│   └── Update/Paint/Interact strategies (from HW05)
└── BallView (view)
```

### List Visitor Framework

```
IList (abstract list)
├── MTList (empty list, singleton)
└── NEList (non-empty list, has first + rest)

IListAlgo (visitor interface)
├── emptyCase(MTList host, Object... params)
└── nonEmptyCase(NEList host, Object... params)
```

## What Is Provided

### Ballworld

- **`IBall.java`** (`src/hw06/model/BallHosts/IBall.java`): Extended ball
  interface. Extends both `IObserver<IBallCmd>` and `IBallHost<IBall>`. Includes
  `getCount()`, `setCount()`, `getRandGen()`, `interactWith()`, and all
  getter/setter methods from HW05.
- **`ABall.java`** (`src/hw06/model/BallHosts/ABall.java`): Abstract ball
  extending `ABallHost<IBall>`. Provides `move()`, `bounce()`, `paint()`,
  `update()`, `updateState()`, and `interactWith()` implementations. Has an
  internal `count` clock and `IRandomizer`.
- **`DefaultBall.java`** (`src/hw06/model/BallHosts/DefaultBall.java`): Provided.
  Standard ball type with `IBallHostID ID`.
- **`FollowerBall.java`**, **`LeaderBall.java`**, **`GangsterBall.java`**:
  Provided. Each has a unique `IBallHostID ID` for visitor dispatch.
- **`IBallAlgo.java`** (`src/hw06/model/BallVisitors/IBallAlgo.java`): Visitor
  interface extending `IBallHostAlgo<R, P, IBall>`.
- **`BallAlgo.java`** (`src/hw06/model/BallVisitors/BallAlgo.java`): Concrete
  visitor with host-ID-keyed case dispatch.
- **`AConfigBallAlgo.java`** (`src/hw06/model/BallVisitors/AConfigBallAlgo.java`):
  Abstract config algo with convenience methods `installUpdateStrategy()`,
  `installPaintStrategy()`, `installInteractStrategy()`.
- **`LeadConfigAlgo.java`**: Reference implementation showing how to define
  per-type behavior in a visitor.
- **All update/paint/interact strategies** from HW05 (ported to HW06 packages).

### List Framework

- **`IList`**, **`MTList`**, **`NEList`**: Provided by `provided.listFW.*`.
  `IList.execute(IListAlgo algo, Object... params)` dispatches to the visitor.
- **`IListAlgo`**: Visitor interface with `emptyCase` and `nonEmptyCase`.
- **`ListDemoApp.java`** (`src/visitorDemoExercises/listFWVisitorExercises/controller/ListDemoApp.java`):
  Demo controller that tests list algorithms.
- **Helper visitors** `LargestAcc`, `LastAcc`, `ContainsAcc` in the visitors package.

## What You Implement

### Part A: Ballworld Extended Visitors

#### 1. `DynamicBall` -- `src/hw06/model/BallHosts/DynamicBall.java` (new file)

Create a new ball host type that can dynamically change its type ID at runtime.
It should extend `ABall` and have:
- Its own `IBallHostID ID`
- A constructor matching the pattern of `DefaultBall`, `FollowerBall`, etc.
- Optionally a method to change its host ID dynamically

#### 2. `OneTypeBallAlgo` -- `src/hw06/model/BallVisitors/OneTypeBallAlgo.java` (new file)

A configuration algorithm where all ball types (Default, Follower, Leader,
Gangster) receive the **same** configuration. The default case installs the
same update strategy, paint strategy, and interact strategy regardless of ball
type. Extend `AConfigBallAlgo`.

#### 3. `SameTypeBallAlgo` -- `src/hw06/model/BallVisitors/SameTypeBallAlgo.java` (new file)

A configuration algorithm where ball behavior depends on the ball type. Define
specific cases for each ball host ID:
- **DefaultBall**: Basic configuration (e.g., straight movement, ball paint)
- **FollowerBall**: Follows leaders (e.g., attracted to LeaderBalls)
- **LeaderBall**: Leads followers (e.g., distinct paint, movement pattern)
- **GangsterBall**: Aggressive behavior (e.g., different interaction)

Use `addCmd()` on the `BallAlgo` to register cases for each `IBallHostID`.

### Part B: List Visitor Exercises

All list algorithms are in `src/visitorDemoExercises/listFWVisitorExercises/model/visitors/`
and implement `IListAlgo`.

#### 4. `LargestFwdAlgo` -- Forward Accumulation: Find Largest

Find the largest integer in a list using forward accumulation.
- `emptyCase`: Return `Integer.MIN_VALUE`
- `nonEmptyCase`: Pass `host.getFirst()` as the initial accumulator to a helper
  visitor. The helper compares each element to the accumulator and keeps the max.

#### 5. `LastFwdAlgo` -- Forward Accumulation: Find Last Element

Find the last element using forward accumulation.
- `emptyCase`: Return `null`
- `nonEmptyCase`: Pass `host.getFirst()` to a helper that replaces the
  accumulator with each new element. At the empty list base case, return the
  accumulator (which is the last element seen).

#### 6. `ContainFwdAlgo` -- Forward Accumulation: Check Membership

Check if a given value exists in the list.
- `emptyCase`: Return `false`
- `nonEmptyCase`: The search value comes via `params[0]` (as a String to parse).
  Use a boolean accumulator that becomes `true` once the element is found.
  OR the current result with each comparison.

#### 7. `FromNthAlgo` -- Sum from Nth Position

Sum all elements starting from the Nth position (0-indexed or 1-indexed based
on implementation).
- `emptyCase`: Return `0`
- `nonEmptyCase`: Decrement a counter (passed as `params[0]`). Only add
  `host.getFirst()` to the sum once the counter drops below 0 (past the Nth
  position). Recurse on `host.getRest()`.

#### 8. `RemoveSmallestRevAlgo` -- Reverse Accumulation: Remove Smallest

Return a new list with the smallest element removed (single pass).
- `emptyCase`: Return `MTList.Singleton`
- `nonEmptyCase`: Compare `host.getFirst()` against the minimum of the rest.
  If current element is the minimum, skip it; otherwise, prepend it and recurse
  on the rest.

## File Paths (Editable)

| File | Package | What to implement |
|------|---------|-------------------|
| `src/hw06/model/BallHosts/DynamicBall.java` | `hw06.model.BallHosts` | New ball host type |
| `src/hw06/model/BallVisitors/OneTypeBallAlgo.java` | `hw06.model.BallVisitors` | Uniform config algo |
| `src/hw06/model/BallVisitors/SameTypeBallAlgo.java` | `hw06.model.BallVisitors` | Type-specific config algo |
| `src/visitorDemoExercises/listFWVisitorExercises/model/visitors/LargestFwdAlgo.java` | `...visitors` | Find largest (forward) |
| `src/visitorDemoExercises/listFWVisitorExercises/model/visitors/LastFwdAlgo.java` | `...visitors` | Find last (forward) |
| `src/visitorDemoExercises/listFWVisitorExercises/model/visitors/ContainFwdAlgo.java` | `...visitors` | Check membership (forward) |
| `src/visitorDemoExercises/listFWVisitorExercises/model/visitors/FromNthAlgo.java` | `...visitors` | Sum from Nth (mixed) |
| `src/visitorDemoExercises/listFWVisitorExercises/model/visitors/RemoveSmallestRevAlgo.java` | `...visitors` | Remove smallest (reverse) |

## Expected Behavior

### Ballworld

- **DynamicBall**: A ball that can change its host type dynamically, allowing
  visitors to process it differently over time.
- **OneTypeBallAlgo**: Creates balls where all types look and behave identically,
  demonstrating that the visitor can apply uniform configuration.
- **SameTypeBallAlgo**: Creates balls where Followers, Leaders, and Gangsters
  each have distinct appearances and behaviors, demonstrating type-dependent
  visitor dispatch.

### List Visitors

- **LargestFwdAlgo**: `[3, 7, 1, 9, 2]` returns `9`
- **LastFwdAlgo**: `[3, 7, 1, 9, 2]` returns `2`
- **ContainFwdAlgo**: `[3, 7, 1].execute(algo, "7")` returns `true`
- **FromNthAlgo**: `[10, 20, 30, 40].execute(algo, "2")` returns `30 + 40 = 70`
- **RemoveSmallestRevAlgo**: `[3, 1, 4, 1, 5]` returns `[3, 4, 1, 5]` (first
  occurrence of smallest removed)

## Dependency on HW05

This assignment builds on HW05 (depends_on: `comp310_hw05`). Key additions:
- Typed ball hosts with `IBallHostID` for visitor dispatch
- Extended visitor pattern via `IBallAlgo<R, P>`
- Configuration algorithms that install strategies per ball type
- Separate list visitor exercises using `provided.listFW` framework

## Testing

Run the autograder to validate your implementation:

```
Use the `grade` tool to run tests.
```

Two test suites run for this assignment:
- **`comp310.tests.hw06.ListVisitorTest`**: Tests the list visitor algorithms
  (LargestFwdAlgo, LastFwdAlgo, ContainFwdAlgo, FromNthAlgo, RemoveSmallestRevAlgo)
- **`comp310.tests.hw06.ExtVisitorTest`**: Tests the extended visitor ball
  algorithms (DynamicBall, OneTypeBallAlgo, SameTypeBallAlgo)
