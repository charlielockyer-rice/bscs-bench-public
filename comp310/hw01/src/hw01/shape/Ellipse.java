package hw01.shape;

import java.awt.Color;
import java.awt.Graphics;

/**
 * 
 * @author charlielockyer
 * A concrete class representing an ellipse that can be painted on a graphics object
 */
public class Ellipse extends ASimpleShape {

	/**
	 * The width of the ellipse
	 */
	private int width;

	/**
	 * The height of the ellipse
	 */
	private int height;

	/**
	 * Constructs an ellipse object
	 * @param xPos the x position (top left corner of bounding rectangle)
	 * @param yPos the y position (top left corner of bounding rectangle)
	 * @param width the width of the ellipse (horizontal)
	 * @param height the height of the ellipse (vertical)
	 * @param color the color of the ellipse (Color class)
	 */
	public Ellipse(int xPos, int yPos, int width, int height, Color color) {
		this.setXPos(xPos);
		this.setYPos(yPos);
		this.width = width;
		this.height = height;
		this.setColor(color);
	}

	/**
	 * Paints the ellipse on the Graphics object g
	 */
	public void paint(Graphics g) {
		// TODO: Implement this method
	}
}
