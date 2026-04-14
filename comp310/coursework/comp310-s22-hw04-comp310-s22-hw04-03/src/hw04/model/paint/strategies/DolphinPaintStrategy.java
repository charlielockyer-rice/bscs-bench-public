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
		this(new AffineTransform(), 0.0, 0.0, 1.0, 1.0);
	}
	
	public DolphinPaintStrategy(AffineTransform at, double x, double y, double width, double height) {
		super(at, DolphinFac.Singleton.makeShape(x, y, width, height));
	}
}
