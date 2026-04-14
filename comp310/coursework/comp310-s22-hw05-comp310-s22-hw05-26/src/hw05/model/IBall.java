package hw05.model;

import java.awt.Color;
import java.awt.Container;
import java.awt.Point;

import hw05.model.interactStrategies.IInteractStrategy;
import hw05.model.paintStrategies.IPaintStrategy;
import hw05.model.updateStrategies.IUpdateStrategy;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.displayModel.IDimension;

/**
 * The ball interface
 * @author cindy
 *
 */
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
	 * @param cmd IBallCmd passed to the dispatcher
	 */
	void update(IDispatcher<IBallCmd> disp, IBallCmd cmd);
	

	/**
	 * A set location method, mostly used for trails and teleporting
	 * @param location location of the ball
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
	public Container getCanvas();
	
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
	
	/**
	 * Set the interact strategy
	 * @param interact strategy to be set to
	 */
	public void setInteractStrategy(IInteractStrategy interact);
	
	/**
	 * Return the Interact Strategy
	 * @return interact strategy
	 */
	public IInteractStrategy getInteractStrategy();
	
	/**
	 * 
	 * @param target target ball
	 * @param disp dispatcher
	 * @return IBallCmd to be passed to the dispatcher
	 */
	public IBallCmd interactWithThen(IBall target, IDispatcher<IBallCmd> disp);



}