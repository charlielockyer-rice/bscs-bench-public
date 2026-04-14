package hw04.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Container;

import provided.utils.dispatcher.IDispatcher;
import provided.utils.dispatcher.IObserver;
import provided.utils.displayModel.IDimension;

import hw04.model.paint.strategies.IPaintStrategy;
import hw04.model.strategy.StraightStrategy;

/**
 * @author charlielockyer
 * Ball class represented to create a ball
 */
public class Ball implements IObserver<IBallCmd>, IBall{


	/**
	 * The location of the center of the ball
	 */
	protected Point location;
	
	/**
	 * The velocity of the ball
	 */
	protected Point velocity;
	
	/**
	 * The color of the ball
	 */
	protected Color color;
	
	/**
	 * Radius of the circle
	 */
	protected int radius;
	
	/**
	 * Dimension of the interface
	 */
	protected IDimension dimension; 
	
	/**
	 * Movement strategy of the ball
	 */
	
	protected IUpdateStrategy strategy = new StraightStrategy();
	
	/**
	 * Paint strategy of the ball
	 */
	protected IPaintStrategy paintStrategy = IPaintStrategy.NULL;
	
	/**
	 * Canvas to paint on
	 */
	protected Container canvas;

	/**
	 * A boolean saying whether or not the ball has ever bounced against a wall
	 */
	private boolean[] bounced = {false,false};
	
	/**
	 * @param location is the location of the ball
	 * @param velocity is how fast the ball is going in x and y location
	 * @param color is the set color for the ball
	 * @param radius is the size of the ball
	 * @param dimension the size of the canvas
	 * @param algo the ball algorithm 
	 */
	public Ball(Point location, Point velocity, Color color, int radius, IDimension dimension, Container canvas,
			IBallAlgo algo) {
		this.location = location;
		this.velocity = velocity;
		this.color = color;
		this.radius = radius;
		this.dimension = dimension;
		this.canvas = canvas;
		this.execute(algo);
		
		this.paintStrategy.init(this);
	};
	
	/**
	 * move method to call move for balls
	 */
	@Override
	public void move(){
		this.location.translate(velocity.x, velocity.y);
	};
	
	/**
	 * {@inheritDoc}
	 * <br>
	 * Runs the default case of the algorithm
	 */
	@Override
	public void execute(IBallAlgo algo) {
		algo.caseDefault(this);
	}
	
	/**
	 * Method to bounce the ball
	 */
	@Override
	public void bounce() {
		// make concrete if most balls share the bounce behavior 
		if (this.location.x+this.radius >= this.dimension.getWidth() || this.location.x-this.radius <= 0) {
			this.velocity.x = -this.velocity.x;
			bounced[0] = true;
		}
		
		if (this.location.y+this.radius >= this.dimension.getHeight() || this.location.y-this.radius <= 0) {
			this.velocity.y = -this.velocity.y;
			bounced[1] = true;
		}
		// set the new location
		location.setLocation(this.location);
	};
	
	/**
	 * The update method that the dispatcher's updateAll method will call.
	 * @param disp The calling dispatcher
	 * @param g The input parameter given to the dispatcher's updateAll() method, here a Graphics object.
	 */
    @Override
	public void update(IDispatcher<IBallCmd> disp, IBallCmd cmd) { 
    	cmd.apply(this, disp);
    };
		
	/**
	 * update the state for the balls
	 * @param disp is the inputed dispatcher display 
	 */
	public void updateState(IDispatcher<IBallCmd> disp) {
		this.strategy.updateState(this, disp);
	}

	/**
	 * The paint method to fill in the ball
	 * @param g inputed graphics
	 */
	@Override
	public void paint(Graphics g) {
		this.paintStrategy.paint(g, this);
	}
	
	/**
	 * A set location method, mostly used for trails and teleporting
	 * @param location
	 */
	@Override
	public void setLocation(Point location) {
		this.location = location;
	}
	
	/**
	 * A getter for location
	 * @return the ball's location
	 */
	@Override
	public Point getLocation() {
		return this.location;
	}
	
	/**
	 * @param velocity the balls velocity to set
	 */
	@Override
	public void setVelocity(Point velocity) {
		this.velocity = velocity;
	}
	
	/**
	 * @return the balls velocity to get
	 */
	@Override
	public Point getVelocity() {
		return this.velocity;
	}
	
	/**
	 * @param radius the balls radius to set
	 */
	@Override
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	/**
	 * @return the balls radius to get
	 */
	@Override
	public int getRadius() {
		return this.radius;
	}
	
	/**
	 * @param color the balls velocity to set
	 */
	@Override
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * @return the balls velocity to get
	 */
	@Override
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * A getter for dimension in order to make teleport work
	 * @return dimensions of the screen
	 */
	@Override
	public IDimension getDimension() {
		return this.dimension;
	}
	
	/**
	 * A setter for the ball's update strategy
	 */
	public void setUpdateStrategy(IUpdateStrategy updateStrategy) {
		this.strategy = updateStrategy;
	}
	
	/**
	 * A getter for the ball's update strategy
	 * @return the ball's update strategy
	 */
	public IUpdateStrategy getUpdateStrategy() {
		return this.strategy;
	}
	
	/**
	 * A setter for the ball's paint strategy
	 */
	public void setPaintStrategy(IPaintStrategy paintStrategy) {
		this.paintStrategy = paintStrategy;
	}
	
	/**
	 * A getter for the ball's paint strategy
	 * @return the ball's paint strategy
	 */
	public IPaintStrategy getPaintStrategy() {
		return this.paintStrategy;
	}
	
	/**
	 * Used for exploding strategy, this is whether or not the ball bounced
	 * @return whether or not the ball has bounced
	 */
	public boolean[] hasBounced() {
		return this.bounced;
	}
	
	/**
	 * A getter for the canvas
	 * @return the canvas
	 */
	public Container getCanvas() {
		return this.canvas;
	}
}
