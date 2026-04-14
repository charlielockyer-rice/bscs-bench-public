package hw04.model.paint.shape;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * A factory to create rectangles
 * @author charlielockyer
 *
 */
public class RectangleFac implements IShapeFactory {
	
	public static RectangleFac Singleton = new RectangleFac();
	
	@Override
	public Shape makeShape(double x, double y, double width, double height) {
		return new Rectangle2D.Double(x,y,width,height);
	}
}



