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
		// TODO: Implement this constructor
		super(new AffineTransform(), new java.awt.geom.Ellipse2D.Double());
	}

	public EllipsePaintStrategy(AffineTransform at, double x, double y, double width, double height) {
		// TODO: Implement this constructor
		super(at, new java.awt.geom.Ellipse2D.Double());
	}

}
