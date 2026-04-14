package hw04.model.paint.strategies;

import java.awt.geom.AffineTransform;
import hw04.model.paint.shape.EllipseFac;

/**
 * Paints an ellipse
 * @author charlielockyer
 *
 */
public class EllipsePaintStrategy extends ShapePaintStrategy {
	
	public EllipsePaintStrategy() {
		this(new AffineTransform(), 0, 0, 4.0 / 3.0, 2.0 / 3.0);
	}

	public EllipsePaintStrategy(AffineTransform at, double x, double y, double width, double height) {
		super(at, EllipseFac.Singleton.makeShape(x, y, width, height));
	}

}
