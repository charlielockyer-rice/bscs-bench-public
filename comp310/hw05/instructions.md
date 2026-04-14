# HW05 - Interaction Ballworld: Interaction Strategies

## Design Pattern

This assignment introduces the **Interaction Strategy Pattern**, which allows
balls to interact with each other in pairwise fashion. An `IInteractStrategy`
defines what happens when two balls meet a certain criteria (e.g., overlapping,
same color). Criteria detection is handled by special `IUpdateStrategy`
implementations that iterate over all balls in the dispatcher and test interaction
conditions. This creates a two-level composition: an update strategy that detects
criteria, paired with an interaction strategy that performs the response.

## Architecture Overview

```
Controller
├── BallModel (model)
│   ├── IDispatcher<IBallCmd>
│   ├── Ball instances (implement IBall)
│   │   ├── IUpdateStrategy - behavior (includes criteria strategies)
│   │   ├── IPaintStrategy - visual appearance
│   │   └── IInteractStrategy - interaction behavior (new)
│   └── Strategy/Paint/Interact factories
└── BallView (view)
    ├── Update strategy selector
    ├── Paint strategy selector
    ├── Interaction strategy selector (new)
    └── Canvas panel
```

**Key additions from HW04**:
- `IInteractStrategy` interface: `interactWith(IBall context, IBall target, IDispatcher<IBallCmd> disp)` returns an `IBallCmd`
- Criteria update strategies: `IUpdateStrategy` implementations that check conditions between ball pairs and trigger interactions
- Interaction strategies: define what happens when criteria are met

## What Is Provided

- **`IInteractStrategy.java`** (`src/hw05/model/interactStrategies/IInteractStrategy.java`):
  Interface with `interactWith(IBall context, IBall target, IDispatcher<IBallCmd> disp)`
  returning an `IBallCmd`, plus `init(IBall context)`. Includes `NULL` and `ERROR`
  static instances.
- **`IBall.java`** (`src/hw05/model/IBall.java`): Extended ball interface with
  `getInteractStrategy()`, `setInteractStrategy()`, and `interactWith()`.
- **`Ball.java`** (`src/hw05/model/Ball.java`): Concrete ball with interaction
  strategy field and delegation.
- **`IUpdateStrategy.java`** (`src/hw05/model/updateStrategies/IUpdateStrategy.java`):
  Same interface as HW04.
- **Existing update strategies**: `StraightStrategy`, `GravityStrategy`,
  `ColorStrategy`, `BreathingStrategy`, `EnergyStrategy`, `LazyStrategy`,
  `ZigZagStrategy`, `SwitcherStrategy`, `MultiStrategy` (carried over from HW04).
- **Existing paint strategies**: `APaintStrategy`, `IPaintStrategy`,
  `EllipsePaintStrategy`, `ShapePaintStrategy`, `ImagePaintStrategy`,
  `MultiPaintStrategy`, `BallPaintStrategy`, etc.
- **Controller, View, Model**: Fully implemented with interaction strategy dropdown.

## What You Implement

### Interaction Strategies (`src/hw05/model/interactStrategies/`)

All interaction strategies implement `IInteractStrategy`. The `interactWith()`
method receives the context ball (perspective), target ball (other), and
dispatcher, and returns an `IBallCmd` to execute after both balls' interactions.

#### 1. `CollideStrategy` -- `src/hw05/model/interactStrategies/CollideStrategy.java`

Implements elastic collision physics between two balls. When called:

1. Calculate the distance between the two balls' centers
2. If distance < sum of radii (collision detected):
   a. Calculate the unit vector from source to target
   b. Calculate reduced mass: `(m1 * m2) / (m1 + m2)` where mass ~ radius^2
   c. Calculate the collision impulse along the normal
   d. Update both balls' velocities based on the impulse
   e. "Nudge" balls apart so they are no longer overlapping

Implement these helper methods:
- `reducedMass(double mSource, double mTarget)`: Returns `(m1*m2)/(m1+m2)`
- `calcUnitVec(Point lSource, Point lTarget, double distance)`: Returns
  normalized direction vector from source to target
- `impulse(Point2D.Double normalVec, Point vSource, Point vTarget, double reducedMass)`:
  Returns impulse vector based on relative velocity and reduced mass
- `calcNudgeVec(Point2D.Double normalVec, double minSeparation, double distance)`:
  Returns displacement to push balls apart (uses `NudgeFactor = 1.1`)
- `updateVelocity(IBall aBall, double mass, Point2D.Double impulseVec)`:
  Adjusts ball velocity by `impulse / mass`

#### 2. `FreezeStrategy` -- `src/hw05/model/interactStrategies/FreezeStrategy.java`

When interaction criteria are met, set the target ball's velocity to zero:

```java
target.setVelocity(new Point(0, 0));
```

Return a no-op `IBallCmd`.

#### 3. `AttractStrategy` -- `src/hw05/model/interactStrategies/AttractStrategy.java`

Moves the context ball toward the target ball. Calculate the direction from
context to target and apply a small nudge factor (`attractNudge = 0.0025`) to
adjust the context ball's velocity toward the target.

#### 4. `ReproduceStrategy` -- `src/hw05/model/interactStrategies/ReproduceStrategy.java`

When interaction occurs, spawn a new ball with:
- Averaged radius of the two interacting balls
- Averaged color of the two balls
- Random velocity
- Combined update strategies (MultiStrategy)

Add the new ball to the dispatcher.

#### 5. `CloneTraitsStrategy` -- `src/hw05/model/interactStrategies/CloneTraitsStrategy.java`

When interaction occurs, copy the context ball's traits (radius and color) to
the target ball. The direction of trait transfer is arbitrary (context to target),
creating an evolutionary dynamic.

### Criteria Update Strategies (`src/hw05/model/updateStrategies/`)

These are `IUpdateStrategy` implementations that detect interaction criteria
between ball pairs and trigger the ball's `IInteractStrategy`.

#### 6. `CollideCriteriaStrategy` -- `src/hw05/model/updateStrategies/CollideCriteriaStrategy.java`

Each tick, dispatch a command that iterates over all balls. For each pair where
the distance between centers is less than the sum of their radii, call
`source.interactWith(other, disp)`.

The `updateState()` method dispatches a command to the dispatcher that, for each
ball it encounters, checks if the source and that ball overlap.

#### 7. `SameColorCriteriaStrategy` -- `src/hw05/model/updateStrategies/SameColorCriteriaStrategy.java`

Same structure as `CollideCriteriaStrategy`, but the criteria is color similarity.
Two balls interact if their color distance is below `colorDistanceThreshold = 45`.

Implement `colorDistance(Color color1, Color color2)`:
- Calculate Euclidean distance in RGB space:
  `sqrt((r1-r2)^2 + (g1-g2)^2 + (b1-b2)^2)`

## File Paths (Editable)

| File | Package | What to implement |
|------|---------|-------------------|
| `src/hw05/model/interactStrategies/CollideStrategy.java` | `hw05.model.interactStrategies` | `interactWith()` + helpers |
| `src/hw05/model/interactStrategies/FreezeStrategy.java` | `hw05.model.interactStrategies` | `interactWith()` |
| `src/hw05/model/interactStrategies/AttractStrategy.java` | `hw05.model.interactStrategies` | `interactWith()` |
| `src/hw05/model/interactStrategies/ReproduceStrategy.java` | `hw05.model.interactStrategies` | `interactWith()` |
| `src/hw05/model/interactStrategies/CloneTraitsStrategy.java` | `hw05.model.interactStrategies` | `interactWith()` |
| `src/hw05/model/updateStrategies/CollideCriteriaStrategy.java` | `hw05.model.updateStrategies` | `updateState()` |
| `src/hw05/model/updateStrategies/SameColorCriteriaStrategy.java` | `hw05.model.updateStrategies` | `updateState()` + `colorDistance()` |

## Expected Behavior

- **Collide**: Balls bounce off each other with elastic collision physics.
  Velocities change based on mass (radius^2) and approach angle. Balls are nudged
  apart to prevent sticking.
- **Freeze**: When criteria are met, the target ball stops moving.
- **Attract**: Balls with matching criteria slowly drift toward each other.
- **Reproduce**: Interacting balls spawn offspring with averaged traits.
- **CloneTraits**: One ball copies its radius and color to the other.
- **CollideCriteria**: Triggers interaction when two balls overlap (distance <
  sum of radii).
- **SameColorCriteria**: Triggers interaction when two balls have similar colors
  (RGB distance < 45).
- **Composition**: Criteria strategies are combined with interaction strategies.
  For example, `CollideCriteria + Collide` creates elastic bouncing, while
  `SameColor + Freeze` freezes same-colored balls on approach.

## Dependency on HW04

This assignment builds on HW04 (depends_on: `comp310_hw04`). Key additions:
- `IInteractStrategy` interface for ball-ball interactions
- Criteria update strategies that detect pairwise conditions
- Interaction strategies that respond to detected criteria

## Testing

Run the autograder to validate your implementation:

```
Use the `grade` tool to run tests.
```

The test suite (`comp310.tests.hw05.InteractTest`) verifies:
- `CollideStrategy` correctly computes elastic collision physics
- `FreezeStrategy` sets target velocity to zero
- `AttractStrategy` adjusts velocity toward target
- Criteria strategies correctly detect overlap and color similarity
- `colorDistance()` computes correct Euclidean distance in RGB space
