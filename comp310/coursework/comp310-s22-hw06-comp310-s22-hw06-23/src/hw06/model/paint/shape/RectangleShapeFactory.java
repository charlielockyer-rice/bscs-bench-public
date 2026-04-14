package hw06.model.paint.shape;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Concrete implementation that instantiataes Rectangle2D.Double shapes
 * @author Son Nguyen and Cole Rabson.
 */
public class RectangleShapeFactory implements IShapeFactory {

	/**
	 * A singleton pattern
	 */
	public static final RectangleShapeFactory Singleton = new RectangleShapeFactory();

	/**
	 * Constructor for the Ellipse Shape Factory
	 */
	private RectangleShapeFactory() {

	}

	@Override
	public Shape makeShape(double x, double y, double xScale, double yScale) {
		Shape rectangle = new Rectangle2D.Double(-Math.sqrt(2) / 2, -Math.sqrt(2) / 2, Math.sqrt(2), Math.sqrt(2));
		AffineTransform at = new AffineTransform();
		at.setToTranslation(x, y);
		at.scale(xScale, yScale);
		return at.createTransformedShape(rectangle);
	}

}
