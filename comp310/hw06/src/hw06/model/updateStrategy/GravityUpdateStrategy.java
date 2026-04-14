package hw06.model.updateStrategy;

import java.awt.Point;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Strategy for the ball to bounce like how it would if down is the ground.
 * @author Cole Rabson, Son Nguyen
 */
public class GravityUpdateStrategy implements IUpdateStrategy {
	@Override
	/**
	 * @param context -> the ball undergoing the strategy
	 * @param disp -> the IDispatcher for the current ball.
	 * Updates the ball according to the Gravity update strategy.
	 */
	public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		// TODO: Implement this method
	}

	@Override
	/**
	 * Initialize the update strategy. This method must be run whenever the ball gets a new strategy.
	 * @param host -> the host ball.
	 */
	public void init(IBall host) {

	}
}
