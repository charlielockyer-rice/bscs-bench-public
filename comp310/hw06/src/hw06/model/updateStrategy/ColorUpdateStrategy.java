package hw06.model.updateStrategy;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Strategy for the ball to change color as it moves.
 * @author Cole Rabson and Son Nguyen.
 */
public class ColorUpdateStrategy implements IUpdateStrategy {
	@Override
	/**
	 * @param context -> the input ball for this strategy.
	 * @param disp -> the IDispatcher for the ball.
	 * The update strategy for ColorUpdateStrategy. Randomly changes the color every 10 iterations.
	 */
	public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		// TODO: Implement this method
	}

	/**
	 * @param host -> the host ball.
	 * Initialize the update strategy. This method must be run whenever the ball gets a new strategy.
	 */
	@Override
	public void init(IBall host) {
	}
}
