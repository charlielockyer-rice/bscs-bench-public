package hw01.shape;

import java.awt.Color;

/**
 * 
 * A concrete class representing a circle that can be painted on a graphics object
 */
public class Circle extends Ellipse {

	/**
	 * Constructs an ellipse object
	 * @param xPos the x position (top left corner of bounding rectangle)
	 * @param yPos the y position (top left corner of bounding rectangle)
	 * @param diameter the diameter of the circle
	 * @param color the color of the ellipse (Color class)
	 */
	public Circle(int xPos, int yPos, int diameter, Color color) {
		super(xPos, yPos, diameter, diameter, color);
	}
}
