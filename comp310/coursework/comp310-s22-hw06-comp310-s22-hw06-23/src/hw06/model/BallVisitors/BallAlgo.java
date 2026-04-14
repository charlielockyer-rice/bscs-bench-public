package hw06.model.BallVisitors;

import hw06.model.BallHosts.IBall;
import provided.ballworld.extVisitors.impl.ABallHostAlgo;

/**
 * A concrete implementation of IBallAlgo based on ABallHostAlgo
 * whose host is an IBall.
 * @author swong
 *
 * @param <R> The return type of this visitor's process
 * @param <P> The input parameter type of this visitor's process
 */
public class BallAlgo<R, P> extends ABallHostAlgo<R, P, IBall> implements IBallAlgo<R, P> {
    /**
	 * Add generated serialVersionUID
	 */
	private static final long serialVersionUID = -4620692362726923148L;
	/**
	 * A name for toString() to display.  
	 */
	private String name = super.toString();  // Default to inherited toString() value.
    
	/**
	 * Constructor for the class.
	 * @param defaultCmd  The default case command to use
	 */
	public BallAlgo(ABallAlgoCmd<R, P> defaultCmd) {
		super(defaultCmd);
	}
    
	/**
	 * Constructor for the class.
	 * @param name Friendly name for the toString() method to return 
	 * @param defaultCmd  The default case command to use
	 */
	public BallAlgo(String name, ABallAlgoCmd<R, P> defaultCmd) {
		super(defaultCmd);
		this.name = name;
	}
	
	/**
	 * {@inheritDoc}
	 * <br/>
	 * Displays given friendly name string or inherited toString() if name not given.
	 */
	@Override 
	public String toString() {
		return this.name;
	}
	
	/**
	 * Accessor for the friendly name
	 * @return The friendly name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Accessor for the friendly name
	 * @param name The friendly name to use
	 */
	public void setName(String name) {
		this.name = name;
	}    
}