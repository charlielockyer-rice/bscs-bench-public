package hw01.shape;

import java.awt.Color;
import java.awt.Graphics;

/**
 * 
 * @author charlielockyer
 * An concrete class describing a composite shape on a 2D plane, like a screen
 * A composite shape is composed of two other shapes and delegates its painting instructions to
 * those shapes.
 * Composite shapes can be composed to an arbitrary degree and do not inherently have color
 */
public class CompositeShape extends AShape {

	/**
	 * One component shape of the CompositeShape
	 */
	private AShape shape1;

	/**
	 * The other component shape of the CompositeShape
	 */
	private AShape shape2;

	/**
	 * Constructs a CompositeShape object
	 * @param shape1 the first component shape of the CompositeShape
	 * @param shape2 the other component shape of the CompositeShape
	 */
	public CompositeShape(AShape shape1, AShape shape2) {
		this.shape1 = shape1;
		this.shape2 = shape2;
		this.setXPos(Math.min(shape1.getXPos(), shape2.getXPos()));
		this.setYPos(Math.min(shape1.getYPos(), shape2.getYPos()));
	}

	/**
	 * Sets the color of the shape to an input color defined by the Color class by delegating
	 * to the component shapes
	 * @param color the color of the shape
	 */
	public void setColor(Color color) {
		shape1.setColor(color);
		shape2.setColor(color);
	}

	/**
	 * Paints the shape on the screen, abstract because each different shape is painted differently
	 * @param g the graphics object on which the shape will be painted
	 */
	public void paint(Graphics g) {
		shape1.paint(g);
		shape2.paint(g);
	}
}
