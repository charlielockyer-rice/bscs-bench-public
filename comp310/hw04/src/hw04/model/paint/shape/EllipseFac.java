package hw04.model.paint.shape;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * A factory to create ellipse shapes
 * @author charlielockyer
 *
 */
public class EllipseFac implements IShapeFactory {
	public static EllipseFac Singleton = new EllipseFac();
	
	@Override
	public Shape makeShape(double x, double y, double width, double height) {
		return new Ellipse2D.Double(x, y, width, height);
	}
}

