# HW04 - Paint & Visitor Ballworld: Command Pattern

## Design Pattern

This assignment introduces the **Command Pattern** (via `IBallCmd`) and the
**IPaintStrategy** abstraction. The dispatcher now sends `IBallCmd` commands
rather than raw `Graphics` objects, enabling decoupled command-based updates.
Additionally, ball visual appearance is separated from behavior via
`IPaintStrategy` and its abstract base `APaintStrategy`, which uses
`AffineTransform` for resolution-independent rendering.

## Architecture Overview

```
Controller
├── BallModel (model)
│   ├── IDispatcher<IBallCmd> - dispatches commands (not Graphics)
│   ├── Ball instances (implement IObserver<IBallCmd>, IBall)
│   │   ├── IUpdateStrategy - movement/behavior (from HW03)
│   │   └── IPaintStrategy - visual appearance (new)
│   ├── Strategy loading via reflection + factories
│   └── Paint strategy loading via reflection + factories
└── BallView (view)
    ├── Update strategy selector
    ├── Paint strategy selector
    └── Canvas panel
```

**Key changes from HW03**:
- `Ball` now implements `IBall` interface (not just `IObserver<Graphics>`)
- Dispatcher type changes from `IDispatcher<Graphics>` to `IDispatcher<IBallCmd>`
- `Ball.update()` receives an `IBallCmd` and calls `cmd.apply(this, disp)`
- `IBall.execute(IBallAlgo)` supports the Visitor pattern (calls `algo.caseDefault`)
- New `IPaintStrategy` / `APaintStrategy` for visual rendering

## What Is Provided

- **`IBall.java`** (`src/hw04/model/IBall.java`): Interface with getters/setters
  for location, velocity, radius, color, dimension, updateStrategy, paintStrategy,
  canvas, plus `move()`, `bounce()`, `paint()`, `execute()`, `updateState()`.
- **`IBallCmd.java`** (`src/hw04/model/IBallCmd.java`): Command interface with
  `apply(IBall context, IDispatcher<IBallCmd> disp)`.
- **`IUpdateStrategy.java`** (`src/hw04/model/IUpdateStrategy.java`): Strategy
  interface with `updateState(IBall context, IDispatcher<IBallCmd> disp)` and
  `init(IBall context)`. Includes `NULL` and `errorStrategy` static instances.
- **`IPaintStrategy.java`** (`src/hw04/model/paint/strategies/IPaintStrategy.java`):
  Paint strategy interface with `init(IBall ball)` and `paint(Graphics g, IBall ball)`.
  Includes `NULL` and `ERROR` static instances.
- **`APaintStrategy.java`** (`src/hw04/model/paint/strategies/APaintStrategy.java`):
  Abstract paint strategy using `AffineTransform`. The `paint()` method sets up
  translate/scale/rotate transforms and calls `paintCfg()` then `paintXfrm()`.
- **`ShapePaintStrategy.java`** (`src/hw04/model/paint/strategies/ShapePaintStrategy.java`):
  Concrete strategy that paints a `java.awt.Shape` using the affine transform.
- **`Ball.java`** (`src/hw04/model/Ball.java`): Concrete ball implementing `IBall`.
  The `update()` method calls `cmd.apply(this, disp)`. The `paint()` method
  delegates to `paintStrategy.paint(g, this)`. The `execute()` method calls
  `algo.caseDefault(this)`.
- **`BallModel.java`** (`src/hw04/model/BallModel.java`): Model with update
  strategy and paint strategy factory loading.
- **Shape factories** (`src/hw04/model/paint/shape/`): `EllipseFac`, `RectangleFac`,
  `PolygonFac`, etc. for creating geometric shapes.
- **Controller, View**: Fully implemented MVC with dual strategy dropdowns.

## What You Implement

### Update Strategies (`src/hw04/model/strategy/`)

All update strategies implement `IUpdateStrategy` with
`updateState(IBall context, IDispatcher<IBallCmd> disp)` and `init(IBall context)`.

#### 1. `StraightStrategy.updateState()` -- `src/hw04/model/strategy/StraightStrategy.java`
No-op. Ball moves straight using default `move()` and `bounce()`.

#### 2. `GravityStrategy.updateState()` -- `src/hw04/model/strategy/GravityStrategy.java`
Add a constant (e.g., 1) to `context.getVelocity().y` each tick.

#### 3. `ColorStrategy.updateState()` -- `src/hw04/model/strategy/ColorStrategy.java`
Change the ball's color to a random color each tick.

#### 4. `BreathingStrategy.updateState()` -- `src/hw04/model/strategy/BreathingStrategy.java`
Oscillate the ball's radius using the `sineMaker` (SineMaker) field. Each tick:
```java
context.setRadius(sineMaker.getDVal());
```

#### 5. `SwitcherStrategy.updateState()` -- `src/hw04/model/strategy/SwitcherStrategy.java`
Delegate to the internal strategy (same pattern as HW03, adapted for the new
`IBall`/`IBallCmd` signatures).

#### 6. `MultiStrategy.updateState()` -- `src/hw04/model/strategy/MultiStrategy.java`
Execute both `firstStrategy` and `secondStrategy` on the context ball:
```java
firstStrategy.updateState(context, disp);
secondStrategy.updateState(context, disp);
```

### Paint Strategies (`src/hw04/model/paint/strategies/`)

#### 7. `EllipsePaintStrategy` -- `src/hw04/model/paint/strategies/EllipsePaintStrategy.java`
Extends `ShapePaintStrategy`. Constructor creates an `Ellipse2D.Double` shape
at unit coordinates (e.g., `(-1, -1, 2, 2)`) that gets scaled by the affine
transform in `APaintStrategy.paint()`.

#### 8. `SquarePaintStrategy` -- `src/hw04/model/paint/strategies/SquarePaintStrategy.java`
Implements `IPaintStrategy` directly (non-affine-transform-based). Paint a filled
square centered at the ball's location using `g.fillRect()` with the ball's
radius as half-width.

#### 9. `MultiPaintStrategy` -- `src/hw04/model/paint/strategies/MultiPaintStrategy.java`
Extends `APaintStrategy`. Holds an array of `APaintStrategy` sub-strategies.
- `init()`: Call `init()` on each sub-strategy
- `paintCfg()`: Call `paintCfg()` on each sub-strategy
- `paintXfrm()`: Call `paintXfrm()` on each sub-strategy with the same transform

## File Paths (Editable)

| File | Package | What to implement |
|------|---------|-------------------|
| `src/hw04/model/strategy/StraightStrategy.java` | `hw04.model.strategy` | `updateState()` |
| `src/hw04/model/strategy/GravityStrategy.java` | `hw04.model.strategy` | `updateState()` |
| `src/hw04/model/strategy/ColorStrategy.java` | `hw04.model.strategy` | `updateState()` |
| `src/hw04/model/strategy/BreathingStrategy.java` | `hw04.model.strategy` | `updateState()` |
| `src/hw04/model/strategy/SwitcherStrategy.java` | `hw04.model.strategy` | `updateState()` |
| `src/hw04/model/strategy/MultiStrategy.java` | `hw04.model.strategy` | `updateState()` |
| `src/hw04/model/paint/strategies/EllipsePaintStrategy.java` | `hw04.model.paint.strategies` | constructors |
| `src/hw04/model/paint/strategies/SquarePaintStrategy.java` | `hw04.model.paint.strategies` | `init()`, `paint()` |
| `src/hw04/model/paint/strategies/MultiPaintStrategy.java` | `hw04.model.paint.strategies` | `init()`, `paintCfg()`, `paintXfrm()` |

## Expected Behavior

- **Command dispatch**: The model creates an `IBallCmd` each tick that calls
  `updateState()`, `move()`, `bounce()`, and `paint()` on each ball. This is
  dispatched to all balls via `dispatcher.updateAll(cmd)`.
- **Update strategies**: Same behavioral effects as HW03 (gravity, color change,
  breathing, etc.) but now through the `IBall`/`IBallCmd` interface.
- **Paint strategies**: Determine the visual shape of each ball:
  - `EllipsePaintStrategy`: Draws an ellipse scaled/rotated by the affine transform
  - `SquarePaintStrategy`: Draws a filled square at the ball's position
  - `MultiPaintStrategy`: Combines multiple paint strategies (e.g., ellipse + pentangle)
- **Dual strategy selection**: GUI allows selecting both an update strategy and a
  paint strategy for each new ball.

## Dependency on HW03

This assignment builds on HW03 (depends_on: `comp310_hw03`). Key differences:
- Dispatcher type changes from `Graphics` to `IBallCmd`
- `IBall` interface replaces concrete `Ball` reference in strategies
- New `IPaintStrategy`/`APaintStrategy` layer for rendering
- `Ball.update()` now receives commands, not graphics directly

## Testing

Run the autograder to validate your implementation:

```
Use the `grade` tool to run tests.
```

The test suite (`comp310.tests.hw04.PaintStrategyTest`) verifies:
- Update strategies correctly modify ball state via the `IBall` interface
- Paint strategies render correctly
- `MultiStrategy` composes two update strategies
- `MultiPaintStrategy` composes multiple paint strategies
- Affine-transform-based paint strategies scale/rotate properly
