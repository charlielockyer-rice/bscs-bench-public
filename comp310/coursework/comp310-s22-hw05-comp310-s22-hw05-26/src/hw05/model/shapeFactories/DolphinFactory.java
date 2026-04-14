package hw05.model.shapeFactories;

import java.awt.Point;
import java.awt.geom.AffineTransform;

/**
 * Makes a dolphin shape
 * @author charlielockyer
 *
 */
public class DolphinFactory extends PolygonShapeFactory {
	/**
	 * Singleton of the dolphin factory
	 */
	public static DolphinFactory Singleton = new DolphinFactory();
	
	/**
	 * constructs a new dolphin and does the proper affine transforms
	 */
	public DolphinFactory() {
		super(new AffineTransform(), 1.0 / 40, new Point(0, 0), new Point(-5,10), new Point(-10,15),
				new Point(-30,13), new Point(-20,23), new Point(-10, 25), new Point(-8, 28), new Point(-2, 40),
				new Point(10, 60), new Point(20, 70), new Point(30, 78), new Point(39, 90), new Point(30, 100),
				new Point(25, 104), new Point(30, 105), new Point(40, 104), new Point(50, 103), new Point(60, 98),
				new Point(70, 99), new Point(80, 100), new Point(85, 101), new Point(90, 100), new Point(100, 100),
				new Point(110, 98), new Point(120, 95), new Point(130, 90), new Point(135, 85), new Point(140, 78),
				new Point(140, 70), new Point(150, 62), new Point(149, 60), new Point(140, 62), new Point(130, 64),
				new Point(120, 63), new Point(110, 57), new Point(100, 56), new Point(97, 58), new Point(90, 50),
				new Point(80, 48), new Point(70, 49), new Point(68, 50), new Point(75, 52), new Point(80, 56),
				new Point(70, 54), new Point(60, 55), new Point(50, 53), new Point(40, 50), new Point(30, 43),
				new Point(20, 33), new Point(0, 21), new Point(5, 10));
	}
}
