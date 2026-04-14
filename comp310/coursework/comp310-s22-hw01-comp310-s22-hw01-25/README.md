# HW01 - Shapes & GUI

**Please see the assignment instructions in Canvas.**

## List ALL Partner Names and NetIDs:
1. Charlie Lockyer, cwl6
1. Ryan Mbuashu-Ndip, rlm14

## Application Notes:
To run (assuming you're using eclipse), right click the 'view' package, hover 'Run As', and select
"Java Application". Hope that works.

## Written Discussion Questions:
1. Yes, abstract shape has a location, which is the center of the rectangle that bounds the shape.
Every shape must eventually be drawn on the screen. This an invariant aspect of the shapes in the
GUI. The drawing of a shape on the screen requires a location for where to draw the shape.
Therefore, putting a location in the abstract shape class abstracts this invariant property,
conforming to the Union Design Pattern.

2. The location of a shape is the coordinates of the top-left corner of the rectangle which bounds the
shape. In this way, the concept of location is consistent across simple and composite shapes,
because a bounding rectangle can be drawn around all kinds of shapes.

3. Yes, shapes must have a color to appear on the screen. Every AShape has a setColor() method.
simple shapes have a color field which the setColor() method modifies. The setColor() method of composite shapes calls the setColor() method of each of its component shapes.

4. The color of a composite shape is dependent on the colors of its component shapes. The component
shapes can each have colors independent of the colors of the other components. An abstract shape
defines the behavior of setting color, not the property of color. So composite and simple
shapes can use color in different ways.

5. No, abstract shapes cannot paint themselves on the screen. Abstract shapes cannot be
instantiated. The behavior exists across composite and simple shapes, but the implementation
is left to the concrete shapes because only they can exist on the screen.
