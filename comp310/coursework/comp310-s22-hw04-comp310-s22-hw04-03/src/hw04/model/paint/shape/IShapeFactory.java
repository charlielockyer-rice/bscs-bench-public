package hw04.model.paint.shape;
import java.awt.*;

/**
 * An interface to create shapes
 * @author charlielockyer
 *
 */
public interface IShapeFactory {
	/**
	 * Makes a shape
	 * @param x coordinate
	 * @param y coordinate
	 * @param width of shape
	 * @param height of shape
	 * @return
	 */
	public Shape makeShape(double x, double y, double width, double height);
}

