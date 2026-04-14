package hw06.model.paint.shape;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

/**
 * Concrete implementation that instantiataes Rectangle2D.Double shapes
 * @author Son Nguyen and Cole Rabson.
 */
public class PolygonFactory implements IShapeFactory {

	/**
	 * The Affine Transform of the shape. 
	 */
	private AffineTransform at;
	/**
	 * The polygon being designed. 
	 */
	private Polygon poly;
	/**
	 * The scale factor of the polygon. 
	 */
	private double scaleFactor;

	/**
	 * Constructor for the PolygonFactory
	 * @param at -> the Affine Transform result for the shape. 
	 * @param scaleFactor -> the scale factor for the Polygon.
	 * @param pts -> an array of the points for the shape
	 */
	private PolygonFactory(AffineTransform at, double scaleFactor, Point... pts) {
		this.at = at;
		this.scaleFactor = scaleFactor;
		this.poly = new Polygon();
		for (Point point : pts) {
			this.poly.addPoint(point.x, point.y);
		}
	}

	/**
	 * Makes the polygon designed based on the inputs to the function.
	 * @param x -> the x location for the shape.
	 * @param y -> the y location for the shape.
	 * @param xScale -> the scale of the polygon in the x direction.
	 * @param yScale -> the scale of the polygon in the y direction. 
	 */
	@Override
	public Shape makeShape(double x, double y, double xScale, double yScale) {
		at.setToTranslation(x, y);
		at.scale(xScale * scaleFactor, yScale * scaleFactor);
		return at.createTransformedShape(poly);
	}

}
