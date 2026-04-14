package hw04.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;

import hw04.model.paint.strategies.IPaintStrategy;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.displayModel.IDimension;

public interface IBall {

	/**
	 * move method to call move for balls
	 */
	void move();

	/**
	 * Method to bounce the ball
	 */
	void bounce();
	
	/**
	 * Execute the given algorithm
	 * @param algo The algorithm to execute
	 */
	public void execute(IBallAlgo algo);

	/**
	 * The update method that the dispatcher's updateAll method will call.
	 * @param disp The calling dispatcher
	 * @param g The input parameter given to the dispatcher's updateAll() method, here a Graphics object.
	 */
	void update(IDispatcher<IBallCmd> disp, IBallCmd cmd);
	
	/**
	 * update the state for the balls
	 * @param disp is the inputed dispatcher display 
	 */
	void updateState(IDispatcher<IBallCmd> disp);

	/**
	 * The paint method to fill in the ball
	 * @param g inputed graphics
	 */
	void paint(Graphics g);

	/**
	 * A set location method, mostly used for trails and teleporting
	 * @param location
	 */
	void setLocation(Point location);

	/**
	 * A getter for location
	 * @return the ball's location
	 */
	Point getLocation();

	/**
	 * @param velocity the balls velocity to set
	 */
	void setVelocity(Point velocity);

	/**
	 * @return the balls velocity to get
	 */
	Point getVelocity();

	/**
	 * @param radius the balls radius to set
	 */
	void setRadius(int radius);

	/**
	 * @return the balls radius to get
	 */
	int getRadius();

	/**
	 * @param color the balls velocity to set
	 */
	void setColor(Color color);

	/**
	 * @return the balls velocity to get
	 */
	Color getColor();

	/**
	 * A getter for dimension in order to make teleport work
	 * @return dimensions of the screen
	 */
	IDimension getDimension();

	/**
	 * Used for exploding, this is whether or not the ball bounced
	 * @return whether or not the ball has bounced
	 */
	boolean[] hasBounced();
	
	/**
	 * Returns the canvas to paint on
	 * @return the canvas
	 */
	public Component getCanvas();
	
	/**
	 * A setter for the update strategy for the ball
	 * @param updateStrategy the update strategy it needs to use
	 */
	public void setUpdateStrategy(IUpdateStrategy updateStrategy);
	
	/**
	 * A getter for the update strategy of the ball
	 * @return the ball's update strategy
	 */
	public IUpdateStrategy getUpdateStrategy();
	
	/**
	 * A setter for the paint strategy for the ball
	 * @param paintStrategy the paint strategy it needs to use
	 */
	public void setPaintStrategy(IPaintStrategy paintStrategy);
	
	/**
	 * A getter for the paint strategy of the ball
	 * @return the ball's paint strategy
	 */
	public IPaintStrategy getPaintStrategy();

}