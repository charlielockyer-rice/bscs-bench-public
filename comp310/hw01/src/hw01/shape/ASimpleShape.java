package hw01.shape;

import java.awt.Color;
import java.awt.Graphics;

/**
 * 
 * @author charlielockyer
 * An abstract class describing an abstract simple shape on a 2D plane, like a screen
 * A simple shape has its own custom painting instructions, and is not composed of other shapes
 * Simple shapes are the most basic shapes and inherently have a color
 */
public abstract class ASimpleShape extends AShape {
	/**
	 * The color of the simple shape in the RGB color space; index 0 is R, 1 is G, and 2 is B
	 */
	protected Color color;

	/**
	 * Sets the color of the shape as written in AShape
	 */
	public void setColor(Color color) {
		// TODO: Implement this method
	}

	/**
	 * Paints the shape on the screen, abstract because each different shape is painted differently
	 */
	public abstract void paint(Graphics g);
}
