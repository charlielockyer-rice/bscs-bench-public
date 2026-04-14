package hw05.model.paintStrategies;

import java.awt.geom.AffineTransform;

import hw05.model.shapeFactories.DolphinFactory;

/**
 * Paints a dolphin
 * @author charlielockyer
 *
 */
public class DolphinPaintStrategy extends ShapePaintStrategy {
	/**
	 * paint strategy of the dolphin
	 */
	public DolphinPaintStrategy() {
		// TODO: Implement this constructor
		super(new AffineTransform(), new java.awt.geom.Ellipse2D.Double());
	}

	/**
	 * @param at affine transform
	 * @param x x location
	 * @param y y location
	 * @param width width of the image
	 * @param height height of the image
	 */
	public DolphinPaintStrategy(AffineTransform at, double x, double y, double width, double height) {
		// TODO: Implement this constructor
		super(at, new java.awt.geom.Ellipse2D.Double());
	}
}
