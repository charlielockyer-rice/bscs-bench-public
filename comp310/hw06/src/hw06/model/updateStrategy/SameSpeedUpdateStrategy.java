package hw06.model.updateStrategy;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Strategy that defines an interaction criteria in which in which two balls have similar speeds.
 *
 * @author Son Nguyen and Smit Viroja
 */
public class SameSpeedUpdateStrategy implements IUpdateStrategy {

	/**
	 * Threshold for speed difference.
	 */
	private static final int threshhold = 2;


	/**
	 * The update strategy for SameSpeedUpdateStrategy. Checks that the difference between interacting
	 * ball speeds is below the threshold, and then updates the "context" and "other" ball with commands to interact
	 * (via some IInteractStrategy).
	 *
	 * @param context -> the input ball for this strategy.
	 * @param dispatcher -> the IDispatcher for the ball.
	 *
	 */
	@Override
	public void updateState(IBall context, IDispatcher<IBallCmd> dispatcher) {
		// TODO: Implement this method
	}


	/**
	 * @param host -> the host ball.
	 * Initialize the SameSpeedUpdateStrategy. This method must be run whenever the ball gets a new strategy.
	 */
	@Override
	public void init(IBall host) {

	}
}
