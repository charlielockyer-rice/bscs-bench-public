package hw06.model.BallHosts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;

import hw06.model.IBallCmd;
import hw06.model.interactStrategy.IInteractStrategy;
import hw06.model.paint.IPaintStrategy;
import hw06.model.updateStrategy.IUpdateStrategy;
import provided.ballworld.extVisitors.IBallHost;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.dispatcher.IObserver;
import provided.utils.valueGenerator.IRandomizer;

/**
 * A Ball Interface extracted from a concrete ball for extensibility.
 * @author Cole Rabson and Son Nguyen
 */
public interface IBall extends IObserver<IBallCmd>, IBallHost<IBall>{

	/**
	 * @return The internal clock count of this ball.
	 */
	int getCount();

	/**
	 * @return The internal random Generator for this ball.
	 */
	IRandomizer getRandGen();

	/**
	 * @return The Radius of the ball
	 */
	int getRadius();

	/**
	 * @return The location of the center of the ball
	 */
	Point getLocation();

	/**
	 * @return The Color of the Ball
	 */
	Color getColor();

	/**
	 * @return The Velocity of the Ball
	 */
	Point getVelocity();

	/**
	 * @return The Update Strategy of the Ball
	 */
	IUpdateStrategy getUpdateStrategy();

	/**
	 * @return The paint Strategy of the Ball
	 */
	IPaintStrategy getPaintStrategy();
	
	/**
	 * @return The Interact Strategy of the Ball
	 */
	IInteractStrategy getInteractStrategy();

	/**
	 * Set the radius of the ball
	 * @param radius - the new Radius of the Ball
	 */
	void setRadius(int radius);
	
	/**
	 * Set the count of the ball
	 * @param count - the new Radius of the Ball
	 */
	void setCount(int count);

	/**
	 * Set the center of the ball
	 * @param loc - the new center of the Ball
	 */
	void setLocation(Point loc);

	/**
	 * Set the new velocity for the ball
	 * @param velocity - the new velocity of the Ball
	 */
	void setVelocity(Point velocity);

	/**
	 * Set the color of the ball
	 * @param color - the new color of the Ball
	 */
	void setColor(Color color);

	/**
	 * Sets the updateStrategy of the ball
	 * @param updateStrategy - the new updateStrategy of the Ball
	 */
	void setUpdateStrategy(IUpdateStrategy updateStrategy);

	/**
	 * Sets the paintStrategy of the ball
	 * @param paintStrategy - the new updateStrategy of the Ball
	 */
	void setPaintStrategy(IPaintStrategy paintStrategy);
	
	/**
	 * Sets the paintStrategy of the ball
	 * @param paintStrategy - the new updateStrategy of the Ball
	 */
	void setInteractStrategy(IInteractStrategy paintStrategy);

	/**
	 * Move the ball as per its velocity
	 */
	void move();

	/**
	 * Paint the ball to the GUI
	 * @param g The graphic needed to paint the ball
	 * 
	 */
	void paint(Graphics g);

	/**
	 * Bounce the ball off of the walk
	 */
	void bounce();

	/**
	 * Determine if the ball is hitting left wall.
	 * @return true if the ball is touching left wall.
	 */
	boolean hitLeftWall();
	
	/**
	 * Determine if the ball is hitting right wall.
	 * @return true if the ball is touching right wall.
	 */
	public boolean hitRightWall();
	
	/**
	 * Determine if the ball is hitting top wall.
	 * @return true if the ball is hitting top wall.
	 */
	public boolean hitTopWall();
	/**
	 * Determine if the ball is hitting bottom wall.
	 * @return true if the ball is hitting bottom wall.
	 */
	public boolean hitBottomWall();

	/**
	 * The update method called by the main ball Dispatcher to notify all the balls to perform the given command.
	 * The given command is executed.
	 * @param disp The Dispatcher that sent the update request.
	 * @param cmd The IBallCmd that will be run.
	 */
	void update(IDispatcher<IBallCmd> disp, IBallCmd cmd);

	/**
	 * Update the state of the ball accordingly to its current updateStrategy.
	 * @param disp the dispatcher that invokes the ball's update() method
	 */
	void updateState(IDispatcher<IBallCmd> disp);

	/**
	 * @return The container of this ball
	 */
	public Component getContainer();

	/**
	 * Interact with another ball in the canvas.
	 * @param other The other ball that this ball is interacting with.
	 * @param disp The dispatcher to controll the ball.
	 * @return The IBallCmd for the interaction.
	 */
	IBallCmd interactWith(IBall other, IDispatcher<IBallCmd> disp);

}