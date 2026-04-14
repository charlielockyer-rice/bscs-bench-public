package hw05.model.shapeFactories;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * The rectangle shape factory that makes a rectangle to be painted
 *
 */
public class RectangleShapeFactory implements IShapeFactory {

	/**
	 * Static factory
	 */
	public static RectangleShapeFactory Singleton = new RectangleShapeFactory();

	@Override
	public Shape makeShape(double x, double y, double xScale, double yScale) {
		return new Rectangle2D.Double(x - xScale / 2.0, y - yScale / 2.0, xScale, yScale);
	}

}
