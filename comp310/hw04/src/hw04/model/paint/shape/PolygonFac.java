package hw04.model.paint.shape;

import java.awt.Shape;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;

/**
 * A polygon factory that creates polygons with affine transforms
 * @author charlielockyer
 * 
 */
public class PolygonFac implements IShapeFactory {
	
	/**
	 * Instantiates a polygon
	 */
	private Polygon poly =  new Polygon();
	
	/**
	 * Instantiates an affine transform
	 */
	private AffineTransform at;
	
	/**
	 * Instantiates the scale factor
	 */
	double scaleFactor = 1.0;
	
	/**
	 * Creates a new factory based on these parameters
	 * @param at
	 * @param scaleFactor
	 * @param points
	 */
	public PolygonFac(AffineTransform at, double scaleFactor, Point...points) {
		this.at = at;
		this.scaleFactor = scaleFactor;
		for (Point pt : points) {
			poly.addPoint(pt.x, pt.y);
		}
	}

	@Override
	public Shape makeShape(double x, double y, double width, double height) {
		at.setToTranslation(x,  y);
		at.scale(width * scaleFactor, height * scaleFactor);
		return at.createTransformedShape(poly);
	}

}
