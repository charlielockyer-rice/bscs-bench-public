package hw04.model.paint.strategies;

import java.awt.geom.AffineTransform;
import hw04.model.paint.shape.PentanglePolygonFac;

/**
 * The pentangle paint strategy
 * @author charlielockyer
 *
 */
public class PentanglePaintStrategy extends ShapePaintStrategy {
	
	public PentanglePaintStrategy() {
		// TODO: Implement this constructor
		super(new AffineTransform(), new java.awt.geom.Ellipse2D.Double());
	}

	public PentanglePaintStrategy(AffineTransform at, double x, double y, double width, double height) {
		// TODO: Implement this constructor
		super(at, new java.awt.geom.Ellipse2D.Double());
	}
}
