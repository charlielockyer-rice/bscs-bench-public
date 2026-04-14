package hw05.model.shapeFactories;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

/**
 * The polygon shape factory that makes the polygon to be painted
 *
 */
public class PolygonShapeFactory implements IShapeFactory {

	/**
	 * The local at
	 */
	AffineTransform at = new AffineTransform();
	/** 
	 * the scale factor
	 */
	double scaleFactor;
	/**
	 * the local polygon
	 */
	Polygon poly;

	/**
	 * The constructor of the factory
	 * @param at the affine transform
	 * @param scaleFactor the scale factor
	 * @param pts the points
	 */
	public PolygonShapeFactory(AffineTransform at, double scaleFactor, Point... pts) {
		this.at = at;
		this.scaleFactor = scaleFactor;
		this.poly = new Polygon();
		for (Point pt : pts) {
			this.poly.addPoint(pt.x, pt.y);
		}
	}

	@Override
	public Shape makeShape(double x, double y, double xScale, double yScale) {
		this.at.setToTranslation(x, y);
		this.at.scale(xScale * this.scaleFactor, yScale * this.scaleFactor);
		return at.createTransformedShape(poly);
	}

}
