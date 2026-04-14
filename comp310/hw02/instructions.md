# HW02 - Basic Ballworld: Abstract Inheritance Pattern

## Design Pattern

This assignment introduces the **Abstract Inheritance Pattern** using the
Model-View-Controller (MVC) architecture. An abstract class (`ABall`) defines
the common physics behavior for all balls (move, bounce, paint), while concrete
subclasses override the abstract `updateState()` method to define unique
per-ball behaviors. The system uses a Dispatcher/Observer pattern to notify all
balls to update each tick.

## Architecture Overview

```
BallController (controller)
├── BallModel (model)
│   ├── Timer - fires every 50ms
│   ├── IDispatcher<Graphics> - notifies all balls
│   ├── ObjectLoader - creates balls by class name (reflection)
│   └── ABall instances (observers)
│       ├── BounceUpBall - diameter varies with x position
│       ├── DVDLogoBall - color changes on bounce
│       └── SineSpeedBall - oscillating velocity via sine waves
└── ViewGUI (view)
    ├── Text field for ball class name input
    ├── "Make Ball" button
    ├── "Clear" button
    └── Canvas panel (paintComponent calls model.update(g))
```

**Communication flow**: Controller wires Model and View with adapters
(`IModel2ViewAdapter`, `IView2ModelAdapter`). Each timer tick, the model tells
the view to repaint. The view's `paintComponent` calls `model.update(g)`, which
dispatches the Graphics object to all balls via `ballDispatcher.updateAll(g)`.

## What Is Provided

- **`BallController.java`** (`src/hw02/controller/BallController.java`): Fully
  implemented. Creates model and view, wires adapters, starts both.
- **`ViewGUI.java`** (`src/hw02/view/ViewGUI.java`): Fully implemented Swing GUI.
- **`IModel2ViewAdapter.java`** (`src/hw02/ball/IModel2ViewAdapter.java`): Adapter
  interface from model to view (`update()`, `getDimension()`).
- **`IView2ModelAdapter.java`** (`src/hw02/view/IView2ModelAdapter.java`): Adapter
  interface from view to model (`makeBall()`, `clearBalls()`, `paintBalls()`).
- **`ErrorBall.java`** (`src/hw02/ball/ErrorBall.java`): Fallback ball that beeps
  when an invalid class name is entered.
- **Provided library**: `provided.utils.dispatcher.*` (IDispatcher, IObserver,
  SequentialDispatcher), `provided.utils.loader.*` (ObjectLoader),
  `provided.utils.displayModel.IDimension`, `provided.utils.valueGenerator.*`
  (Randomizer, SineMaker).

## What You Implement

### 1. `ABall.move()` -- `src/hw02/ball/ABall.java`

Update the ball's position by adding velocity to position:

```java
this.position.translate(this.velocity.x, this.velocity.y);
```

### 2. `ABall.bounce()` -- `src/hw02/ball/ABall.java`

Check if the ball has hit a boundary of its container (determined by
`dimension.getWidth()` and `dimension.getHeight()`). If so, reverse the
appropriate velocity component. The ball's position is its top-left corner and
its size is `diameter`. Return `true` if a bounce occurred, `false` otherwise.

- Horizontal: if `position.x < 0` or `position.x + diameter > width`, negate
  `velocity.x`
- Vertical: if `position.y < 0` or `position.y + diameter > height`, negate
  `velocity.y`

### 3. `ABall.update(IDispatcher<Graphics> disp, Graphics g)` -- `src/hw02/ball/ABall.java`

Called by the dispatcher each tick. Must call, in order:
1. `updateState(disp)` -- abstract, defined by concrete subclass
2. `move()` -- update position
3. `bounce()` -- check boundaries
4. `paint(g)` -- draw the ball

### 4. `BounceUpBall.updateState(IDispatcher<Graphics> disp)` -- `src/hw02/ball/BounceUpBall.java`

Creates a perspective effect: the ball's diameter varies with its x position.
As the ball moves right, it appears larger (bouncing toward the viewer); as it
moves left, it appears smaller (bouncing away). Scale the diameter based on the
ball's horizontal position relative to the container width.

### 5. `DVDLogoBall.bounce()` -- `src/hw02/ball/DVDLogoBall.java`

Override `bounce()` to call `super.bounce()` and, if the ball bounced, change
its color to a random color (use `Randomizer.Singleton.randomColor()`).

### 6. `DVDLogoBall.updateState(IDispatcher<Graphics> disp)` -- `src/hw02/ball/DVDLogoBall.java`

The DVD logo ball has no special per-tick state update beyond the color change
on bounce. This can be a no-op.

### 7. `SineSpeedBall.updateState(IDispatcher<Graphics> disp)` -- `src/hw02/ball/SineSpeedBall.java`

Update the ball's velocity using the `xVelChanger` and `yVelChanger` SineMaker
objects. Each tick, set:
- `velocity.x = xVelChanger.getDVal()` (gets next sine value as int)
- `velocity.y = yVelChanger.getDVal()`

### 8. `BallModel.makeBall(String ballType)` -- `src/hw02/ball/BallModel.java`

Use the `loader` (ObjectLoader) to create an instance of the ball type specified
by `ballType` (a fully qualified class name like `hw02.ball.DVDLogoBall`). The
loader needs constructor arguments: color, position, velocity, diameter, and
dimension. Use `Randomizer.Singleton` to generate random values. Add the
resulting ball to `ballDispatcher` as an observer.

### 9. `BallModel.start()` -- `src/hw02/ball/BallModel.java`

Start the timer to begin the update cycle:

```java
timer.start();
```

## File Paths (Editable)

| File | Package | What to implement |
|------|---------|-------------------|
| `src/hw02/ball/ABall.java` | `hw02.ball` | `move()`, `bounce()`, `update()` |
| `src/hw02/ball/BounceUpBall.java` | `hw02.ball` | `updateState()` |
| `src/hw02/ball/DVDLogoBall.java` | `hw02.ball` | `bounce()`, `updateState()` |
| `src/hw02/ball/SineSpeedBall.java` | `hw02.ball` | `updateState()` |
| `src/hw02/ball/BallModel.java` | `hw02.ball` | `makeBall()`, `start()` |

## Expected Behavior

- **ABall**: Each tick, the ball updates its state, moves by its velocity, checks
  for boundary collisions, and repaints. Bounce reverses velocity on wall contact.
- **BounceUpBall**: Diameter scales with x position, creating a 3D perspective
  illusion.
- **DVDLogoBall**: Changes to a random color every time it bounces off a wall,
  mimicking the classic DVD screensaver logo.
- **SineSpeedBall**: Velocity oscillates smoothly via sine waves, creating
  swooping, curved trajectories.
- **BallModel**: Entering a fully qualified class name (e.g., `hw02.ball.DVDLogoBall`)
  in the GUI and clicking "Make Ball" creates and dispatches that ball type.

## Testing

Run the autograder to validate your implementation:

```
Use the `grade` tool to run tests.
```

The test suite (`comp310.tests.hw02.BallTest`) verifies:
- `ABall.move()` correctly translates position by velocity
- `ABall.bounce()` correctly reverses velocity at boundaries
- `ABall.update()` calls methods in the correct order
- Concrete ball `updateState()` methods produce expected behaviors
- `BallModel.makeBall()` correctly loads and dispatches ball instances
