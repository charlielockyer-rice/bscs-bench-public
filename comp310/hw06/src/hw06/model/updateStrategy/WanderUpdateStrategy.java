package hw06.model.updateStrategy;

import java.awt.Rectangle;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Strategy for the balls to changes it velocity randomly.
 * @author Megan Xiao, Son Nguyen
 */
public class WanderUpdateStrategy implements IUpdateStrategy {
	@Override
	/**
	 * @param context -> the ball undergoing the strategy
	 * @param disp -> the IDispatcher for the current ball.
	 * Updates the ball according to the Wander update strategy.
	 */
	public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		// TODO: Implement this method
	}

	@Override
	/**
	 * @param host -> the host ball.
	 * Initialize the update strategy. This method must be run whenever the ball gets a new strategy.
	 */
	public void init(IBall host) {
	}
}
