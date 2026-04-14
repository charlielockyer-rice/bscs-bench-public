package hw01.shape;

import java.awt.Color;
import java.awt.Graphics;

/**
 * 
 * @author charlielockyer
 * A concrete class representing a rectangle that can be painted on a graphics object
 */
public class Rectangle extends ASimpleShape {
	/**
	 * The width of the rectangle
	 */
	private int width;

	/**
	 * The height of the rectangle
	 */
	private int height;

	/**
	 * Constructs a rectangle object
	 * @param xPos the x position (top left corner)
	 * @param yPos the y position (top left corner)
	 * @param width the width of the rectangle (horizontal)
	 * @param height the height of the rectangle (vertical)
	 * @param color the color of the rectangle (Color class)
	 */
	public Rectangle(int xPos, int yPos, int width, int height, Color color) {
		this.setXPos(xPos);
		this.setYPos(yPos);
		this.width = width;
		this.height = height;
		this.setColor(color);
	}

	/**
	 * Paints the rectangle on the Graphics object g
	 */
	public void paint(Graphics g) {
		g.setColor(this.color); // Set the color to use when drawing
		g.fillRect(this.getXPos(), this.getYPos(), this.width, this.height);
	}
}
