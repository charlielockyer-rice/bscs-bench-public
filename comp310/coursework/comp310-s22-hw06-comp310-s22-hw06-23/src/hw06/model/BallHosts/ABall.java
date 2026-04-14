package hw06.model.BallHosts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;

import hw06.model.IBallCmd;
import hw06.model.BallVisitors.IBallAlgo;
import hw06.model.interactStrategy.IInteractStrategy;
import hw06.model.paint.IPaintStrategy;
import hw06.model.updateStrategy.IUpdateStrategy;
import provided.ballworld.extVisitors.IBallHostID;
import provided.ballworld.extVisitors.impl.ABallHost;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.valueGenerator.IRandomizer;
import provided.utils.valueGenerator.impl.Randomizer;

/**
 * A concrete representation of a ball
 * @author Cole Rabson and Son Nguyen
 */
public abstract class ABall extends ABallHost<IBall> implements IBall  {

	/**
	 * IBallHostID id
	 */
	private static final long serialVersionUID = 7299418823629512729L;
	/**
	 * The container of the ball
	 */
	private Component container;
	/**
	* The radius of the ball
	**/
	protected int radius;
	/**
	 * The geometric center of the ball
	 */
	protected Point location;
	/**
	 * The color of the ball
	 */
	protected Color color;
	/**
	 * The velocity of the ball
	 */
	protected Point velocity;

	/**
	 * The ball update Strategy
	 */
	protected IUpdateStrategy updateStrategy = IUpdateStrategy.NULL_OBJECT;

	/**
	 * The ball paint strategy
	 */
	protected IPaintStrategy paintStrategy = IPaintStrategy.NULL_OBJECT;
	
	/**
	 * The ball Interact strategy
	 */
	protected IInteractStrategy interactStrategy = IInteractStrategy.NULL_OBJECT;

	/**
	* A shared Randomizer object to be used by 
	* concrete implementations should they need
	* random values
	*/
	protected IRandomizer randGen = Randomizer.Singleton;
	/**
	* An internal clock of ABall independent of tick
	*/
	protected int count = 1;

	/**
	 * @return The internal clock count of this ball.
	 */
	public int getCount() {
		return this.count;
	}
	
	/**
	 * Set the internal count of the ball.
	 * @param count The internal count of the ball.
	 */
	public void setCount(int count) {
		this.count =count;
	}

	/**
	 * @return The internal random Generator for this ball.
	 */
	public IRandomizer getRandGen() {
		return this.randGen;
	}

	/**
	 * Constructor of a Ball
	 * 
	 * @param id The unique ID of a host ball.
	 * @param center Initial Spawning Location of the ball
	 * @param radius The Radius of the ball being created
	 * @param velocity An int array representing the x and y velocity
	 * @param color The color of the ball
	 * @param container The container of this ball
	 * @param installAlgo The algorithm to install
	 */
	protected ABall(IBallHostID id, Point center, int radius, Point velocity, Color color, Component container, IBallAlgo<Void, Void> installAlgo) {
		super(id);
		this.location = center;
		this.radius = radius;
		this.velocity = velocity;
		this.color = color;
		this.container = container;
		this.execute(installAlgo);
	}

	/**
	 * @return The Radius of the ball
	 */
	public int getRadius() {
		return this.radius;
	}

	/**
	 * @return The location of the center of the ball
	 */
	public Point getLocation() {
		return this.location;
	}

	/**
	 * @return The Color of the Ball
	 */
	public Color getColor() {
		return this.color;
	}

	/**
	 * @return The Velocity of the Ball
	 */
	public Point getVelocity() {
		return this.velocity;
	}

	/**
	 * @return The Update Strategy of the Ball
	 */
	public IUpdateStrategy getUpdateStrategy() {
		return this.updateStrategy;
	}

	/**
	 * @return The Strategy of the Ball
	 */
	public IPaintStrategy getPaintStrategy() {
		return this.paintStrategy;
	}
	
	/**
	 * @return The Interact Strategy of the Ball
	 */
	public IInteractStrategy getInteractStrategy() {
		return this.interactStrategy;
	}

	/**
	 * Set the radius of the ball
	 * @param radius - the new Radius of the Ball
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**
	 * Set the center of the ball
	 * @param loc - the new center of the Ball
	 */
	public void setLocation(Point loc) {
		this.location = loc;
	}

	/**
	 * Set the new velocity for the ball
	 * @param velocity - the new velocity of the Ball
	 */
	public void setVelocity(Point velocity) {
		this.velocity = velocity;
	}

	/**
	 * Set the color of the ball
	 * @param color - the new color of the Ball
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Sets the updateStrategy of the ball
	 * @param updateStrategy - the new updateStrategy of the Ball
	 */
	public void setUpdateStrategy(IUpdateStrategy updateStrategy) {
		this.updateStrategy = updateStrategy;
		this.updateStrategy.init(this);
	}

	/**
	 * Sets the paintStrategy of the ball
	 * @param paintStrategy - the new paintStrategy of the Ball
	 */
	public void setPaintStrategy(IPaintStrategy paintStrategy) {
		this.paintStrategy = paintStrategy;
		this.paintStrategy.init(this);
	}
	
	/**
	 * Sets the interactStrategy of the ball
	 * @param interactStrategy - the new interactStrategy of the Ball
	 */
	public void setInteractStrategy(IInteractStrategy interactStrategy) {
		this.interactStrategy = interactStrategy;
		this.interactStrategy.init(this);
	}

	/**
	 * Move the ball as per its velocity
	 */
	public void move() {
		this.count++;
		int newX = this.location.x + this.velocity.x;
		int newY = this.location.y + this.velocity.y;
		this.setLocation(new Point(newX, newY));

	}

	/**
	 * Paint the ball to the GUI
	 * @param g The graphic needed to paint the ball
	 * 
	 */
	public void paint(Graphics g) {
		//FIXME: Edit this for paintStrategy
		this.paintStrategy.paint(g, this);
	}

	/**
	 * Bounce the ball off of the walk
	 */
	public void bounce() {
		if (this.hitLeftWall()) {
			this.velocity.x = Math.abs(this.velocity.x);
		}else if(this.hitRightWall()) {
			this.velocity.x = -Math.abs(this.velocity.x);
		}
		// Using if instead of else-if to account for corner cases
		// Hitting the Top/Bottom Wall
		if (this.hitTopWall()) 
			this.velocity.y = Math.abs(this.velocity.y);
		else if (this.hitBottomWall()) 
			this.velocity.y = -Math.abs(this.velocity.y);
	}

	/**
	 * Determine if the ball is hitting left wall.
	 * @return true if the ball is touching left wall.
	 */
	public boolean hitLeftWall() {
		return ( this.location.x - radius <= 0);
	}
	
	/**
	 * Determine if the ball is hitting right wall.
	 * @return true if the ball is touching right wall.
	 */
	public boolean hitRightWall() {
		return (this.location.x + radius >= getContainer().getWidth());
	}
	
	/**
	 * Determine if the ball is hitting top wall.
	 * @return true if the ball is hitting top wall.
	 */
	public boolean hitTopWall() {
		return (this.location.y - radius <= 0);

	}
	/**
	 * Determine if the ball is hitting bottom wall.
	 * @return true if the ball is hitting bottom wall.
	 */
	public boolean hitBottomWall() {
		return (this.location.y + radius >= getContainer().getHeight());

	}

	/**
	 * The update method called by the main ball Dispatcher to notify all the balls to perform the given command.
	 * The given command is executed.
	 * @param disp The Dispatcher that sent the update request.
	 * @param cmd The IBallCmd that will be run.
	 */
	@Override
	public void update(IDispatcher<IBallCmd> disp, IBallCmd cmd) {
		cmd.apply(this, disp);
	}

	/**
	 * Update the state of the ball accordingly to its current updateStrategy.
	 * @param disp the dispatcher that invokes the ball's update() method
	 */
	public void updateState(IDispatcher<IBallCmd> disp) {
		updateStrategy.updateState(this, disp);
	}
//FIXME: need a closer look.
//	/**
//	 * {@inheritDoc}
//	 * <br>
//	 * Runs the default case of the algorithm.
//	 */
//	@Override
//	public void execute(IBallAlgo algo) {
//		algo.caseDefault(this);
//
//	}

	/**
	 * @return The container of this ball
	 */
	public Component getContainer() {
		return this.container;
	}

	/**
	 * Invoke this ball's interaction strategy from the perspective of this ball.
	 * @param other  The "other" ball to interact with
	 * @param disp  A Dispatcher to use if desired.
	 * @return A command to be run by this ball after both balls' interaction behaviors are complete.
	 */
	public IBallCmd interactWith(IBall other, IDispatcher<IBallCmd> disp) {
		return this.interactStrategy.interactWithThen(this, other, disp);
	}

}
