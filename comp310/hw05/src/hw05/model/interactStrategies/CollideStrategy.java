package hw05.model.interactStrategies;

import java.awt.Point;
import java.awt.geom.Point2D;

import hw05.model.IBall;
import hw05.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;

/**
 * @author cindy
 * Elastic Collision without decomposition
 *
 */
public class CollideStrategy implements  IInteractStrategy {

	@Override
	public IBallCmd interactWith(IBall source, IBall target, IDispatcher<IBallCmd> disp) {
		// TODO: Implement this method
		return (context, d) -> {};
	}

	/**
	 * Returns the reduced mass of the two balls (m1*m2)/(m1+m2) Gives correct
	 * result if one of the balls has infinite mass.
	 * 
	 * @param mSource
	 *            Mass of the source ball
	 * @param mTarget
	 *            Mass of the target ball
	 * @return The reduced mass of the two balls
	 */
	protected double reducedMass(double mSource, double mTarget) {
		// TODO: Implement this method
		return 0;
	}
	
	/**
	 * Calculate the unit vector (normalized vector) from the location of the source ball to the location of the target ball.
	 * @param lSource Location of the source ball
	 * @param lTarget Location of the target ball
	 * @param distance Distance from the source ball to the target ball
	 * @return A double-precision vector (point)
	 */
	Point2D.Double calcUnitVec(Point lSource, Point lTarget, double distance) {
		// TODO: Implement this method
		return null;
	}		

	/**
	 * Calculates the impulse (change in momentum) of the collision in the
	 * direction from the source to the target This method calculates the
	 * impulse on the source ball. The impulse on the target ball is the
	 * negative of the result. The change in velocity of the source ball is the
	 * impulse divided by the source's mass The change in velocity of the target
	 * ball is the negative of the impulse divided by the target's mass
	 * 
	 * @param normalVec 
	 *            The unit vector (normalized vector) from the location of the source ball to the location of the target ball.
	 * @param vSource
	 *            Velocity of the source ball
	 * @param vTarget
	 *            Velocity of the target ball
	 * @param reducedMass
	 *            Reduced mass of the two balls
	 * @return The value of the collision's impulse
	 */
	protected Point2D.Double impulse(Point2D.Double normalVec, Point vSource, Point vTarget, double reducedMass) {
		// TODO: Implement this method
		return null;
	}

	
	/**
	 * The multiplicative factor to increase the separation distance to insure that the two balls
	 * are beyond collision distance
	 */
	private static final double NudgeFactor = 1.1;
	
	/**
	 * Calculate the vector to add to the source ball's location to "nudge" it out of the way of the target ball.
	 * @param normalVec  The unit vector (normalized vector) from the location of the source ball to the location of the target ball.
	 * @param minSeparation The minimum allowed non-colliding separation between the centers of the balls = maximum allowed colliding separation.
	 * @param distance The actual distance between the centers of the balls.
	 * @return A Point object which is the amount to "nudge" the source ball away from the target ball.
	 */
	Point calcNudgeVec(Point2D.Double normalVec, double minSeparation, double distance) {
		// TODO: Implement this method
		return null;
	}

	/**
	 * Updates the velocity of a ball, given an impulse vector. The change in velocity is the 
	 * impulse divided by the ball's mass. 
	 * 
	 * @param aBall
	 *            The ball whose velocity needs to be modified by the impulse
	 * @param mass 
	 *            The "mass" of the ball
	 * @param impulseVec 
	 *            The impulse vector for the ball
	 */
	protected void updateVelocity(IBall aBall, double mass, Point2D.Double impulseVec) {
		// TODO: Implement this method
	}
	
	@Override
	public void init(IBall context) {
		// TODO Auto-generated method stub
		
	}
	
	// Utilities for calculating reduced mass, unit vector, impuylse and nudge vector elided.

}

