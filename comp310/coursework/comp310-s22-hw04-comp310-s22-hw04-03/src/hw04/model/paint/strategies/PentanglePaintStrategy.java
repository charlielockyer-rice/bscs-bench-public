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
		this(new AffineTransform(), 0.0, 0.0, 1.0, 1.0);
	}
	
	public PentanglePaintStrategy(AffineTransform at, double x, double y, double width, double height) {
		super(at, PentanglePolygonFac.Singleton.makeShape(x, y, width, height));
	}
}
