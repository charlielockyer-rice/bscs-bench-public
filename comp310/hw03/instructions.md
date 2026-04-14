# HW03 - Strategy Ballworld: Strategy Pattern

## Design Pattern

This assignment refactors HW02 from **inheritance-based** behavior to
**composition-based** behavior using the **Strategy Pattern**. Instead of
creating a new ball subclass for each behavior, all balls are instances of the
same `Ball` class. Each `Ball` holds an `IUpdateStrategy` reference that defines
its unique behavior. Strategies can be swapped at runtime (via `SwitcherStrategy`)
and composed together (via combining factories).

## Architecture Overview

```
Controller
├── BallModel (model)
│   ├── Timer (50ms)
│   ├── IDispatcher<Graphics> - dispatches to all Ball observers
│   ├── SwitcherStrategy - shared by all "switcher" balls
│   ├── IStrategyFac - factory pattern for creating strategies
│   ├── ObjectLoader - loads strategy by class name
│   └── Ball instances (each holds an IUpdateStrategy)
└── BallView (view)
    ├── Top dropdown - select strategy for new balls
    ├── Bottom dropdown - select strategy for combining/switching
    ├── "Make Ball" / "Make Switcher" buttons
    ├── "Combine" / "Switch" buttons
    └── "Clear" button
```

**Key refactoring from HW02**: The `ABall` abstract class is replaced with a
single concrete `Ball` class. Behavior that was in `updateState()` subclass
overrides is now in `IUpdateStrategy.updateState(IDispatcher, Ball)` implementations.

## What Is Provided

- **`Ball.java`** (`src/hw03/model/Ball.java`): Concrete ball with `location`,
  `velocity`, `color`, `radius`, `dimension`, and `strategy` fields. Methods
  `move()`, `bounce()`, `paint()`, and `update()` are fully implemented. The
  `update()` method calls `strategy.updateState(disp, this)`.
- **`BallModel.java`** (`src/hw03/model/BallModel.java`): Model with strategy
  factory loading, combining, and switcher management. `makeStrategyFac()`,
  `combineStrategyFacs()`, `loadBall()`, and `setSwitcher()` are provided.
- **`IUpdateStrategy.java`** (`src/hw03/model/IUpdateStrategy.java`): Interface
  with `updateState(IDispatcher<Graphics> disp, Ball ball)`.
- **`IStrategyFac.java`** (`src/hw03/model/IStrategyFac.java`): Strategy factory
  interface.
- **Controller, View, Adapters**: Fully implemented MVC wiring.

## What You Implement

All strategies are in `src/hw03/model/strategy/` and implement `IUpdateStrategy`.
Each strategy's `updateState(IDispatcher<Graphics> disp, Ball ball)` method
modifies the ball's properties on each tick.

### 1. `StraightStrategy.updateState()` -- `src/hw03/model/strategy/StraightStrategy.java`

A no-op strategy. The ball moves in a straight line using only the default
`move()` and `bounce()` behavior. The `updateState()` body should be empty.

### 2. `GravityStrategy.updateState()` -- `src/hw03/model/strategy/GravityStrategy.java`

Simulates gravity by adding a constant value to the ball's y-velocity each tick.
Get the ball's velocity and increase the y component by a small constant (e.g., 1):

```java
ball.getVelocity().y += 1;
```

### 3. `ColorStrategy.updateState()` -- `src/hw03/model/strategy/ColorStrategy.java`

Changes the ball's color to a random color each tick. Use `java.util.Random` or
`Randomizer.Singleton` to generate a random color and call `ball.setColor(...)`.

### 4. `ReverseStrategy.updateState()` -- `src/hw03/model/strategy/ReverseStrategy.java`

Reverses the ball's velocity every 10 ticks. Use an internal `ticks` counter.
When `ticks` reaches 10, negate both x and y velocity and reset the counter.

### 5. `ExpandingStrategy.updateState()` -- `src/hw03/model/strategy/ExpandingStrategy.java`

Oscillates the ball's radius between 5 and 60. Use the `expand` boolean field to
track direction. When expanding, increment the radius; when contracting, decrement
it. Flip direction at the bounds (5 and 60).

### 6. `TeleportingStrategy.updateState()` -- `src/hw03/model/strategy/TeleportingStrategy.java`

Every 20 ticks, teleport the ball to a random location on the canvas. Use the
`ticks` counter. When it reaches 20, set the ball's location to a random point
within `ball.getDimension()` bounds and reset the counter.

### 7. `RandomWalkStrategy.updateState()` -- `src/hw03/model/strategy/RandomWalkStrategy.java`

Each tick, randomly adjust the ball's velocity by a small amount (e.g., +/-1
for both x and y). This creates a "drunk walk" effect.

### 8. `ExplodingStrategy.updateState()` -- `src/hw03/model/strategy/ExplodingStrategy.java`

When the ball bounces off a wall, it "explodes" by spawning a new ball (added to
the dispatcher) and optionally changing direction. Check `ball.hasBounced()` to
detect wall collisions. Use `disp.addObserver()` to add a new ball.

### 9. `SwitcherStrategy.updateState()` -- `src/hw03/model/strategy/SwitcherStrategy.java`

Delegates to an internal strategy. The `switchStrategy()` method (provided)
swaps the internal strategy, and `updateState()` should simply delegate:

```java
strategy.updateState(disp, ball);
```

All switcher balls share the same `SwitcherStrategy` instance in the model, so
when the internal strategy is switched, all switcher balls change behavior at once.

## File Paths (Editable)

| File | Package | What to implement |
|------|---------|-------------------|
| `src/hw03/model/strategy/StraightStrategy.java` | `hw03.model.strategy` | `updateState()` (no-op) |
| `src/hw03/model/strategy/GravityStrategy.java` | `hw03.model.strategy` | `updateState()` |
| `src/hw03/model/strategy/ColorStrategy.java` | `hw03.model.strategy` | `updateState()` |
| `src/hw03/model/strategy/ReverseStrategy.java` | `hw03.model.strategy` | `updateState()` |
| `src/hw03/model/strategy/ExpandingStrategy.java` | `hw03.model.strategy` | `updateState()` |
| `src/hw03/model/strategy/TeleportingStrategy.java` | `hw03.model.strategy` | `updateState()` |
| `src/hw03/model/strategy/RandomWalkStrategy.java` | `hw03.model.strategy` | `updateState()` |
| `src/hw03/model/strategy/ExplodingStrategy.java` | `hw03.model.strategy` | `updateState()` |
| `src/hw03/model/strategy/SwitcherStrategy.java` | `hw03.model.strategy` | `updateState()` |

## Expected Behavior

- **Straight**: Ball moves in a straight line, bouncing off walls.
- **Gravity**: Ball accelerates downward, bouncing with increasing speed.
- **Color**: Ball continuously changes color randomly.
- **Reverse**: Ball reverses direction every 10 ticks.
- **Expanding**: Ball grows and shrinks cyclically between radius 5 and 60.
- **Teleporting**: Ball jumps to a random location every 20 ticks.
- **RandomWalk**: Ball moves erratically, changing velocity slightly each tick.
- **Exploding**: Ball spawns a new ball each time it hits a wall.
- **Switcher**: All switcher balls delegate to a shared strategy that can be
  switched at runtime via the GUI dropdown.
- **Combining**: Two strategies can be composed so a ball exhibits both behaviors.

## Dependency on HW02

This assignment builds on HW02 (depends_on: `comp310_hw02`). If available,
`previous_solution/` contains your HW02 work for reference. Key differences:
- HW02's `ABall` subclasses are replaced by `Ball` + `IUpdateStrategy`
- Behavior is now swappable at runtime rather than fixed at compile time

## Testing

Run the autograder to validate your implementation:

```
Use the `grade` tool to run tests.
```

The test suite (`comp310.tests.hw03.StrategyTest`) verifies:
- Each strategy modifies ball state correctly
- `GravityStrategy` increases y-velocity
- `ColorStrategy` changes the ball's color
- `ReverseStrategy` flips velocity periodically
- `ExpandingStrategy` oscillates radius within bounds
- `TeleportingStrategy` relocates the ball periodically
- `SwitcherStrategy` properly delegates to its internal strategy
