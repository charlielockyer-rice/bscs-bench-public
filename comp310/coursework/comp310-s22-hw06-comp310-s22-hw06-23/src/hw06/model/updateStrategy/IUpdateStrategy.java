package hw06.model.updateStrategy;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Updates the strategy of a ball
 * @author Cole Rabson, Son Nguyen
 */
public interface IUpdateStrategy {

	/**
	 * Updates the state of the ball
	 * @param context The Ball to update the state of.
	 * @param disp The dispatcher
	 */
	public void updateState(IBall context, IDispatcher<IBallCmd> disp);

	/**
	 * @param host -> the host ball.
	 * Initialize the update strategy. This method must be run whenever the ball gets a new strategy.
	 */
	public void init(IBall host);

	/**
	 * No-op singleton implementation of IUpdateStrategy
	 * See the web page on the Null Object Design Pattern at http://cnx.org/content/m17227/latest/
	 */
	public static final IUpdateStrategy NULL_OBJECT = new IUpdateStrategy() {
		@Override
		/**
		 * @param context -> the ball undergoing the strategy
		 * @param disp -> the IDispatcher for the current ball.
		 * Updates the ball according to the Null update strategy. 
		 */
		public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		}

		/**
		 * @param host -> the host ball.
		 * Initialize the update strategy. This method must be run whenever the ball gets a new strategy.
		 */
		@Override
		public void init(IBall host) {
		}
	};

	/**
	 * An error strategy to beep.
	 */
	public static final IUpdateStrategy ERROR = new IUpdateStrategy() {
		@Override
		/**
		 * @param context -> the ball undergoing the strategy
		 * @param disp -> the IDispatcher for the current ball.
		 * Updates the ball according to the Error update strategy. 
		 */
		public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		}

		/**
		 * @param host -> the host ball.
		 * Initialize the update strategy. This method must be run whenever the ball gets a new strategy.
		 */
		@Override
		public void init(IBall host) {
			java.awt.Toolkit.getDefaultToolkit().beep();
			System.err.println("IUpdateStrategy.ERROR: beeping!");
		}
	};
}
