package hw05.model.shapeFactories;

import java.awt.Point;
import java.awt.geom.AffineTransform;

/**
 * The Triangle Shape Factory that makes the triangle to be painted
 *
 */
public class TriangleShapeFactory extends PolygonShapeFactory {
	/**
	 * static generator
	 */
	public static TriangleShapeFactory Singleton = new TriangleShapeFactory();

	/**
	 * @param at the affine transform
	 * @param scaleFactor the scale factor
	 * @param pts the points
	 */
	public TriangleShapeFactory(AffineTransform at, double scaleFactor, Point... pts) {
		super(at, scaleFactor, pts);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Triangle prototype
	 */
	public TriangleShapeFactory() {
		this(new AffineTransform(), 0.25d, new Point(0, 1), new Point(-2, -1), new Point(2, -1));
	}

}
