package hw06.model.BallVisitors;

import hw06.model.BallHosts.IBall;
import provided.ballworld.extVisitors.IBallHostID;

/**
 * Algorithm that will return true if the host is the same type as the given input parameter.
 * This algorithm will always work, even for dynamically typed balls.
 * Parameter: An IBall instance.
 * Returns:  True if the host and the parameter are the same type.  False otherwise.
 * Usage:  boolean result = host.execute(SameTypeBallAlgo.Singleton, other) 
 * @author swong
 *
 */
public class SameTypeBallAlgo extends BallAlgo<Boolean, IBall> {
	/**
	 *  Add generated serialVersionUID
	 */
	private static final long serialVersionUID = 8380154111346265229L;
	/**
	 * Singleton instance
	 */
	public static final SameTypeBallAlgo Singleton = new SameTypeBallAlgo();

	/**
	 * Private constructor for the class.  
	 */
	private SameTypeBallAlgo() {
		// TODO: Implement this constructor
		// Only a default case is needed here because the main purpose here is to get the current ID value of the host
		// and to create a second visitor algorithm based on that ID value.
		super(new ABallAlgoCmd<>() {
			/**
			 * Add generated serialVersionUID
			 */
			private static final long serialVersionUID = 8451834955902576424L;

			@Override
			public Boolean apply(IBallHostID hostID, IBall host, IBall... otherBalls) {
				// TODO: Implement this method
				// Delegate to the other ball to see if its type matches the host.
				// Return whatever the result of the delegation is.
				return false;
			}
		});
	}
}