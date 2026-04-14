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
		this(new AffineTransform(), 0.0, 0.0, 1.0, 1.0);
	}
	
	/**
	 * @param at affine transform
	 * @param x x location
	 * @param y y location
	 * @param width width of the image
	 * @param height height of the image
	 */
	public DolphinPaintStrategy(AffineTransform at, double x, double y, double width, double height) {
		super(at, DolphinFactory.Singleton.makeShape(x, y, width, height));
	}
}
