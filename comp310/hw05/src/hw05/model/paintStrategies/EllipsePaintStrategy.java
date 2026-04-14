package hw05.model.paintStrategies;

import java.awt.geom.AffineTransform;

import hw05.model.shapeFactories.EllipseShapeFactory;

/**
 * Paint strategy for ellipse.
 * @author yihan
 *
 */
public class EllipsePaintStrategy extends ShapePaintStrategy {

	/**
	 * No parameter constructor that creates a prototype ellipse that 
	 * has twice the width as height but an average radius of 1.
	 * An AffineTranform for internal use is instantiated.
	 */
	public EllipsePaintStrategy() {
		// TODO: Implement this constructor
		super(new AffineTransform(), new java.awt.geom.Ellipse2D.Double());
	}

	/**
	 * Constructor that allows the specification of the location, x-radius and y-radius
	 * of the prototype ellipse.   The AffineTransform to use is given.
	 * @param at The AffineTransform to use for internal calculations
	 * @param x floating point x-coordinate of center of circle
	 * @param y floating point y-coordinate of center of circle
	 * @param width floating point x-radius of the circle (ellipse)
	 * @param height floating point y-radius of the circle (ellipse)
	 */
	public EllipsePaintStrategy(AffineTransform at, double x, double y, double width, double height) {
		// TODO: Implement this constructor
		super(at, new java.awt.geom.Ellipse2D.Double());
	}
}
