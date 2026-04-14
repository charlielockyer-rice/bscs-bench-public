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
		// Only a default case is needed here because the main purpose here is to get the current ID value of the host
		// and to create an second visitor algorithm based on that ID value.
		super(new ABallAlgoCmd<>() {
			/**
			 * Add generated serialVersionUID
			 */
			private static final long serialVersionUID = 8451834955902576424L;

			@Override
			public Boolean apply(IBallHostID hostID, IBall host, IBall... otherBalls) {
            
				// Delegate to the other ball to see if its type matches the host.
				// Return whatever the result of the delegation is.
				return otherBalls[0].execute(new BallAlgo<Boolean, Void> (new ABallAlgoCmd<>() {

					/**
					 * Add generated serialVersionUID
					 */
					private static final long serialVersionUID = 5674877699468735339L;

					@Override
					public Boolean apply(IBallHostID otherID, IBall other, Void... nu) {
						// Since there is a case for the same type as the host, the answer here is unequivocally false.
						return false;
					}
				}){
					/**
					 * Add generated serialVersionUID
					 */
					private static final long serialVersionUID = 4122273318517036306L;

					// The use of an initializer block here eliminates the need for a separate line of code to add 
					// additional cases and thus this algorithm can be used without ever having to assign it to a variable.
					{
						// Add a case for the same type as the host, whatever that is.
						setCmd(hostID, new ABallAlgoCmd<>() {
							/**
							 * Add generated serialVersionUID
							 */
							private static final long serialVersionUID = -2687432036748164499L;

							@Override
							public Boolean apply(IBallHostID otherID, IBall other, Void... nu) {
								// The case for the same type as the host, thus the answer here is unequivocally true.
								return true;
							}
						});
					}
				});
			}
		});
	}
}