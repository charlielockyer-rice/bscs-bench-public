package hw02.ball;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import provided.utils.dispatcher.IDispatcher;
import provided.utils.dispatcher.IObserver;
import provided.utils.displayModel.IDimension;

/**
 * @author rlmse
 * Class which represents abstract balls which can appear in the GUI.
 */
public abstract class ABall implements IObserver<Graphics> {
	/**
	 * Color of the ABall
	 */
	protected Color color;

	/**
	 * Position of the ABall, given as a Point
	 */
	protected Point position;

	/**
	 * Velocity of the ABall, given as a Point representing its x and y components. In pixels per
	 * Refresh cycle.
	 */
	protected Point velocity;

	/**
	 * Diameter of the ball, in pixels
	 */
	protected int diameter;

	/**
	 * Dimension of the graphics object on which the ABall is painted
	 */
	protected IDimension dimension;

	/**
	 * @param color - color of the ABall
	 * @param position - Position of the ABall
	 * @param velocity - Velocity of the ABall
	 * @param diameter - Diameter of the ABall
	 * @param dimension - Dimension of the graphic container of the ABall
	 */
	public ABall(Color color, Point position, Point velocity, int diameter, IDimension dimension) {
		this.color = color;
		this.position = position;
		this.velocity = velocity;
		this.diameter = diameter;
		this.dimension = dimension;
	}

	/**
	 * @param g - the Graphics object containing the ABall
	 */
	public void paint(Graphics g) {
		g.setColor(this.color);
		g.fillOval(this.position.x, this.position.y, diameter, diameter);
	}

	/**
	 * Update the position of the ball to reflect its movement
	 */
	public void move() {
		// TODO: Implement this method
	}

	/**
	 * Check if the ball has hit a boundary of its container. If so, updates the velocity of the 
	 * ball according to conservation of momentum (perfectly elastic collision).
	 * @return true if the ball bounced of a boundary, false otherwise.
	 */
	public boolean bounce() {
		// TODO: Implement this method
		return false;
	}

	/**
	 * Update the ball every cycle, according to its defined behavior
	 * @param disp - The dispatcher to which the ball is subscribed 
	 */
	public abstract void updateState(IDispatcher<Graphics> disp);

	public void update(IDispatcher<Graphics> disp, Graphics g) {
		// TODO: Implement this method
	}

}
