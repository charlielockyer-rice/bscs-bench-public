package hw06.model.interactStrategy;

import java.awt.Point;
import java.awt.geom.Point2D;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Interaction strategy for the ball to elastically collide with another ball (once a given interaction criteria is met between the balls).
 * A "nudge" factor is added to allow a realistic collision in a discrete-time environment.
 *
 * @author Son Nguyen and Smit Viroja
 */
public class CollideInteractStrategy implements  IInteractStrategy {

	/**
	 * Changes the ball according to the Collide interaction strategy.
	 *
	 * @param context -> the ball undergoing the strategy
	 * @param other -> the ball interacting with the context ball
	 * @param disp -> the IDispatcher for the current ball.
	 * @return A command to be executed after both balls' interaction behaviors have completed.
	 */
	@Override
	public IBallCmd interactWithThen(IBall context, IBall other, IDispatcher<IBallCmd> disp) {
		// TODO: Implement this method
		// Calculate nudge factor, reduced mass, impulse, and update velocities for elastic collision
		return (ball, d) -> {};
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
	 * Operational note: Even though theoretically, the difference in velocities
	 * of two balls should be co-linear with the normal line between them, the
	 * discrete nature of animations means that the point where collision is
	 * detected may not be at the same point as the theoretical contact point.
	 * This method calculates the rebound directions as if the two balls were
	 * the appropriate radii such that they had just contacted
	 * _at_the_point_of_collision_detection_. This may give slightly different
	 * rebound direction than one would calculate if they contacted at the
	 * theoretical point given by their actual radii.
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
	private static final double NudgeFactor = 1.4;

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


	/**
	 * Initialize the interaction strategy. This method must be run whenever the ball gets a new strategy.
	 * @param host -> the host ball.
	 */
	@Override
	public void init(IBall host) {

	}

}
