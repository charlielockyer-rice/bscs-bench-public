package hw04.model.paint.strategies;

import java.awt.geom.AffineTransform;

import hw04.model.paint.shape.DolphinFac;

/**
 * Paints a dolphin
 * @author charlielockyer
 *
 */
public class DolphinPaintStrategy extends ShapePaintStrategy {
	public DolphinPaintStrategy() {
		// TODO: Implement this constructor
		super(new AffineTransform(), new java.awt.geom.Ellipse2D.Double());
	}

	public DolphinPaintStrategy(AffineTransform at, double x, double y, double width, double height) {
		// TODO: Implement this constructor
		super(at, new java.awt.geom.Ellipse2D.Double());
	}
}
