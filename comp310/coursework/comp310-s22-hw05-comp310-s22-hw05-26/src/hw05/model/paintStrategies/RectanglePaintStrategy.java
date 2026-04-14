package hw05.model.paintStrategies;

import java.awt.geom.AffineTransform;
import hw05.model.shapeFactories.RectangleShapeFactory;

/**
 * The paint strategy that pains a rectangle
 *
 */
public class RectanglePaintStrategy extends ShapePaintStrategy {

	/**
	 * Creates a prototype rectangle which
	 * has twice the width as height and an average radius of 1.
	 * An AffineTranform for internal use is instantiated.
	 */
	public RectanglePaintStrategy() {
		this(new AffineTransform(), 0, 0, 4.0 / 3.0, 2.0 / 3.0);
	}

	/**
	 * Constructor that allows the specification of the location, x-radius and y-radius
	 * of the prototype rectangle.   The AffineTransform to use is given.
	 * @param at The AffineTransform to use for internal calculations
	 * @param x floating point x-coordinate of center of circle
	 * @param y floating point y-coordinate of center of circle
	 * @param width floating point width of the rectangle (rectangle)
	 * @param height floating point height of the rectangle (rectangle)
	 */
	public RectanglePaintStrategy(AffineTransform at, double x, double y, double width, double height) {
		super(at, RectangleShapeFactory.Singleton.makeShape(x, y, width, height));
	}
}
