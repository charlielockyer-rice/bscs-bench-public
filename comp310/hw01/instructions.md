# HW01 - Shapes & GUI: Union Design Pattern

## Design Pattern

This assignment introduces the **Union Design Pattern**, which models a union of
types through an abstract superclass. The key idea is that abstract classes define
the invariant behaviors shared by all concrete subclasses, while each subclass
provides its own variant implementation. Here, all shapes share a location (the
top-left corner of their bounding rectangle), but differ in how they are painted
and how they handle color.

## Architecture Overview

The shape hierarchy follows a classic Union pattern:

```
AShape (abstract)
├── ASimpleShape (abstract) - has its own Color field
│   ├── Rectangle (concrete) - draws a filled rectangle
│   ├── Ellipse (concrete) - draws a filled ellipse
│   └── Circle (concrete) - extends Ellipse with equal width/height
└── CompositeShape (concrete) - holds two AShape children, delegates to them
```

A simple Swing GUI (`simpleGUIFrame`) displays shapes in a center panel with
buttons that swap which shape is drawn. The "Pimp my GUI" button demonstrates
`CompositeShape` by building a car from rectangles and circles.

## What Is Provided

- **`AShape.java`** (`src/hw01/shape/AShape.java`): Fully implemented abstract
  base class with `xPos`/`yPos` fields, getters/setters, and abstract `setColor()`
  and `paint()` methods.
- **`Circle.java`** (`src/hw01/shape/Circle.java`): Fully implemented. Extends
  `Ellipse` with a constructor that passes `diameter` for both width and height.
- **`simpleGUIFrame.java`** (`src/hw01/view/simpleGUIFrame.java`): Fully
  implemented Swing GUI with buttons for Rectangle, Ellipse, Circle, and a
  composite "car" shape. The `paintComponent` override calls `aShape.paint(g)`.

## What You Implement

### 1. `ASimpleShape.setColor(Color color)` -- `src/hw01/shape/ASimpleShape.java`

Set the `color` field (declared as `protected Color color`) to the given color.

```java
public void setColor(Color color) {
    this.color = color;
}
```

### 2. `Rectangle.paint(Graphics g)` -- `src/hw01/shape/Rectangle.java`

Paint a filled rectangle using the `Graphics` object. Must:
- Set the graphics color to `this.color`
- Call `g.fillRect(getXPos(), getYPos(), width, height)`

### 3. `Ellipse.paint(Graphics g)` -- `src/hw01/shape/Ellipse.java`

Paint a filled ellipse using the `Graphics` object. Must:
- Set the graphics color to `this.color`
- Call `g.fillOval(getXPos(), getYPos(), width, height)`

### 4. `CompositeShape.setColor(Color color)` -- `src/hw01/shape/CompositeShape.java`

Delegate `setColor()` to both child shapes (`shape1` and `shape2`).

### 5. `CompositeShape.paint(Graphics g)` -- `src/hw01/shape/CompositeShape.java`

Delegate `paint()` to both child shapes (`shape1` and `shape2`).

## File Paths (Editable)

| File | Package | What to implement |
|------|---------|-------------------|
| `src/hw01/shape/ASimpleShape.java` | `hw01.shape` | `setColor()` |
| `src/hw01/shape/Rectangle.java` | `hw01.shape` | `paint()` |
| `src/hw01/shape/Ellipse.java` | `hw01.shape` | `paint()` |
| `src/hw01/shape/CompositeShape.java` | `hw01.shape` | `setColor()`, `paint()` |

## Expected Behavior

- **Rectangle**: When painted, a filled rectangle appears at `(getXPos(), getYPos())`
  with the specified width, height, and color.
- **Ellipse**: When painted, a filled ellipse appears bounded by the rectangle at
  `(getXPos(), getYPos())` with the specified width, height, and color.
- **Circle**: Inherits from Ellipse; appears as a circle (equal width and height).
- **CompositeShape**: When painted, both child shapes paint themselves. When
  `setColor()` is called, both children receive the new color. The composite's
  position is the minimum x and y of its children (already set in the constructor).
- **GUI**: Clicking shape buttons creates shapes and repaints. The "Pimp my GUI"
  button creates a composite car shape (rectangle body + two circle wheels).

## Testing

Run the autograder to validate your implementation:

```
Use the `grade` tool to run tests.
```

The test suite (`comp310.tests.hw01.ShapeTest`) verifies:
- Shape constructors set position, dimensions, and color correctly
- `paint()` methods invoke the correct `Graphics` calls
- `CompositeShape` properly delegates `setColor()` and `paint()` to children
- The shape hierarchy relationships are correct
