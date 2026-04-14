package hw06.model.BallVisitors;

import hw06.model.IBallAlgo2ModelAdapter;
import provided.logger.ILoggerControl;

/**
 * Interface for configuration algorithms
 * @author swong
 *
 */
public interface IConfigBallAlgo extends IBallAlgo<Void, Void> {

	/**
	 * Instantiate a configuration algorithm that defaults to a no-op and where additional commands can be added later.
	 * @return A configuration algo defaulting to no-op
	 */
	public static IConfigBallAlgo MakeDefaultNULL() {
		return new AConfigBallAlgo(ILoggerControl.getSharedLogger(), IBallAlgo2ModelAdapter.NULL) {
			/**
			 * Add generated serialVersionUID
			 */
			private static final long serialVersionUID = -3135799595550873700L;

			@Override
			public String toString() {
				return "IConfigBallAlgo.NULL";
			}
		
		};
		
	}; 

	/**
	 * Return a configuration algorithm that defaults to an error command and where additional commands can be added later/
	 * @return A configuration algo defaulting to error.
	 */
	public static IConfigBallAlgo MakeDefaultERROR() { 
		return new AConfigBallAlgo(ILoggerControl.getSharedLogger(), IBallAlgo2ModelAdapter.NULL) {
			/**
			 * Add generated serialVersionUID
			 */
			private static final long serialVersionUID = 7333810291609390346L;

			{
				setDefaultCmd(ABallAlgoCmd.MakeError());
			}
			
			@Override
			public String toString() {
				return "IConfigBallAlgo.ERROR";
			}
		}; 
	}
}