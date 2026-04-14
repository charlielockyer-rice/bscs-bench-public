package provided.utils.valueGenerator.impl;

import java.awt.geom.Point2D;

import provided.utils.valueGenerator.IVectorUtil;

/**
 * Implementation of IVectorUtil
 * @author swong
 *
 */
public class VectorUtil implements IVectorUtil {

	/**
	 * Singleton instance
	 */
	public static final VectorUtil Singleton = new VectorUtil();

	/**
	 * Private constructor for singleton.
	 */
	private VectorUtil() {
	}

	@Override
	public void rotate(Point2D v, double angle) {
		double cosA = Math.cos(angle);
		double sinA = Math.sin(angle);
		v.setLocation(cosA * v.getX() - sinA * v.getY(), cosA * v.getY() + sinA * v.getX());
	}

	@Override
	public double angleBetween(Point2D v1, Point2D v2) {
		// Ref: https://stackoverflow.com/questions/5188561/signed-angle-between-two-3d-vectors-with-same-origin-within-the-same-plane

		// The cross-product length is product of each vector's length times the sine of angle between them
		double v1Xv2 = v1.getX() * v2.getY() - v1.getY() * v2.getX(); // calculate cross-product length 

		// The inner (dot) product is the product of each vector's length times the cosine of the angle between them.
		double v1Dotv2 = v1.getX() * v2.getX() + v1.getY() * v2.getY();

		// Dividing the cross product by inner product cancels out the vector lengths, leaving the tangent.  
		// Need to get the full 4-quadrant result, so must use atan2().
		return Math.atan2(v1Xv2, v1Dotv2);

	}

	@Override
	public Point2D vectorTo(Point2D source, Point2D target) {
		return new Point2D.Double(target.getX()-source.getX(), target.getY()-source.getY());
	}

}
