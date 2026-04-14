package hw05.model.paintStrategies;

import java.awt.geom.AffineTransform;
import hw05.model.shapeFactories.TriangleShapeFactory;

/**
 * The paint strategy that paints a triangle
 *
 */
public class TrianglePaintStrategy extends ShapePaintStrategy {

	/**
	 * No parameter constructor that creates a prototype triangle
	 * An AffineTranform for internal use is instantiated.
	 */
	public TrianglePaintStrategy() {
		// TODO: Implement this constructor
		super(new AffineTransform(), new java.awt.geom.Ellipse2D.Double());
	}

	/**
	 * Constructor that allows the specification of the location, x-width and y-height
	 * @param at the affine transform
	 * @param x the x coord
	 * @param y the y coord
	 * @param width the width
	 * @param height the height
	 */
	public TrianglePaintStrategy(AffineTransform at, double x, double y, double width, double height) {
		// TODO: Implement this constructor
		super(at, new java.awt.geom.Ellipse2D.Double());
	}

}
