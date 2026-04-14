package hw01.shape;

import java.awt.Color;
import java.awt.Graphics;

/**
 * 
 * @author charlielockyer
 * An abstract class describing an abstract shape on a 2D plane, like a screen
 */
public abstract class AShape {
	/**
	 * xPos: the x (horizontal) position of the shape on the screen, defined by the shape's top 
	 * left corner
	 */
	private int xPos;

	/**
	 * yPos: the y (vertical) position of the shape on the screen, defined by the shape's top left
	 * corner
	 */
	private int yPos;

	/**
	 * Sets the x position to an input integer
	 * @param xPos the shape's x position
	 */
	public void setXPos(int xPos) {
		this.xPos = xPos;
	}

	/**
	 * Sets the y position to an input integer
	 * @param yPos the shape's y position
	 */
	public void setYPos(int yPos) {
		this.yPos = yPos;
	}

	/**
	 * 
	 * @return the shape's x position
	 */
	public int getXPos() {
		return xPos;
	}

	/**
	 * 
	 * @return the shape's y position
	 */
	public int getYPos() {
		return yPos;
	}

	/**
	 * Sets the color of the shape to an input color defined by the Color class
	 * @param color the color of the shape
	 */
	public abstract void setColor(Color color);

	/**
	 * Paints the shape on the screen, abstract because each different shape is painted differently
	 * @param g the graphics object on which the shape will be painted
	 */
	public abstract void paint(Graphics g);
}
