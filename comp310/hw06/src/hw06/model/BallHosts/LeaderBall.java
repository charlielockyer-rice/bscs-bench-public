package hw06.model.BallHosts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;

import hw06.model.BallVisitors.IBallAlgo;
import provided.ballworld.extVisitors.IBallHostID;
import provided.ballworld.extVisitors.impl.BallHostIDFactory;

/**
 * Ball type that represents a leader entity.
 * @author Charlie Lockyer and Son Nguyen
 *
 */
public class LeaderBall extends ABall {
	
	/**
	 * Add generated serial UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 *  The identifying host ID for this class.
	 */
	public static final IBallHostID ID = BallHostIDFactory.Singleton.makeID(LeaderBall.class.getName());

	/**
	 * Initializes this Ball to a given center, radius, color,
	 * adapter to access system information such as canvas dimensions 
	 * and image loading services and an algorithm to run that will perform 
	 * any desired configuration operations such as installing strategies.
	 * 
	 * @param radius the initial radius of this Ball.
	 * @param velocity the initial velocity of this Ball.
	 * @param color The initial color of the ball.
	 * @param location the initial center of this Ball.
	 * @param container the container of the balls
	 * @param installAlgo The algo to complete the installation of strategies and any other desired operations
	 */
	public LeaderBall(Point location, int radius, Point velocity, Color color, Component container,
			IBallAlgo<Void, Void> installAlgo) {
		super(ID, location, radius, velocity, color, container, installAlgo);
	}
} 