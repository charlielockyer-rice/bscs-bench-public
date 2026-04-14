package hw03.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import provided.utils.dispatcher.IDispatcher;
import provided.utils.dispatcher.IObserver;
import provided.utils.displayModel.IDimension;

/**
 * @author Christina
 * Ball class represented to create a ball
 */
public class Ball implements IObserver<Graphics>{


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
	 * Strategy of the ball
	 */
	protected IUpdateStrategy strategy;

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
	 * @param strategy the ball strategy 
	 */
	public Ball(Point location, Point velocity, Color color, int radius, IDimension dimension, IUpdateStrategy strategy) {
		this.location = location;
		this.velocity = velocity;
		this.color = color;
		this.radius = radius;
		this.dimension = dimension;
		this.strategy = strategy;
	};
	
	/**
	 * move method to call move for balls
	 */
	public void move(){
		this.location.translate(velocity.x, velocity.y);
	};
	
	/**
	 * Method to bounce the ball
	 */
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
    public void update(IDispatcher<Graphics> disp, Graphics g) { 
    	updateState(disp);
    	strategy.updateState(disp, this);
    	move();
    	bounce();
    	paint(g);
    };
		
	/**
	 * update the state for the balls
	 * @param disp is the inputed dispatcher display 
	 */
	void updateState(IDispatcher<Graphics> disp) {
	}

	/**
	 * The paint method to fill in the ball
	 * @param g inputed graphics
	 */
	public void paint(Graphics g) {
		g.setColor(this.color);
		g.fillOval(this.location.x-this.radius, this.location.y-this.radius, radius, radius);
	}
	
	/**
	 * A set location method, mostly used for trails and teleporting
	 * @param location
	 */
	public void setLocation(Point location) {
		this.location = location;
	}
	
	/**
	 * A getter for location
	 * @return the ball's location
	 */
	public Point getLocation() {
		return this.location;
	}
	
	/**
	 * @param velocity the balls velocity to set
	 */
	public void setVelocity(Point velocity) {
		this.velocity = velocity;
	}
	
	/**
	 * @return the balls velocity to get
	 */
	public Point getVelocity() {
		return this.velocity;
	}
	
	/**
	 * @param radius the balls radius to set
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	/**
	 * @return the balls radius to get
	 */
	public int getRadius() {
		return this.radius;
	}
	
	/**
	 * @param color the balls velocity to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * @return the balls velocity to get
	 */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * A getter for dimension in order to make teleport work
	 * @return dimensions of the screen
	 */
	public IDimension getDimension() {
		return this.dimension;
	}
	
	
	/**
	 * A getter for the strategy
	 * @return strategy of htis ball
	 */
	public IUpdateStrategy getStrategy() {
		return this.strategy;
	}
	
	/**
	 * Used for exploding, this is whether or not the ball bounced
	 * @return whether or not the ball has bounced
	 */
	public boolean[] hasBounced() {
		return this.bounced;
	}
}
