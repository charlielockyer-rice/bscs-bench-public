package hw04.model.paint.shape;

import java.awt.Point;
import java.awt.geom.AffineTransform;

/**
 * A factory to create pentagle polygons
 * @author charlielockyer
 *
 */
public class PentanglePolygonFac extends PolygonFac {
	
	public static PentanglePolygonFac Singleton = new PentanglePolygonFac();
	
	public PentanglePolygonFac() {
		super(new AffineTransform(), 1.0 / 4, new Point(-1, 2), new Point(0,5), new Point(1,2),
				new Point(4,2), new Point(2,0), new Point(3, -3), new Point(0, -1), new Point(-3, -3),
				new Point(-2, 0), new Point(-4, 2));
	}

}
