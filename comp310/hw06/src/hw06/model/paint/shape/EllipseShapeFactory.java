package hw06.model.paint.shape;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

/**
 * The concrete shape factory implementation that instantiates Ellipses2D.Double shapes
 * @author Son Nguyen and Cole Rabson.
 */
public class EllipseShapeFactory implements IShapeFactory {
	/**
	 * A singleton pattern
	 */
	public static final EllipseShapeFactory Singleton = new EllipseShapeFactory();

	/**
	 * Constructor for the Ellipse Shape Factory
	 */
	private EllipseShapeFactory() {
		
	}

	/**
	 * Makes an ellipse designed by the EllipseShapeFactory.
	 */
	@Override
	public Shape makeShape(double x, double y, double xScale, double yScale) {
		Shape ellipse = new Ellipse2D.Double(-1, -1, 2, 2);
		AffineTransform at = new AffineTransform();
		at.setToTranslation(x, y);
		at.scale(xScale, yScale);
		return at.createTransformedShape(ellipse);
	}

}
