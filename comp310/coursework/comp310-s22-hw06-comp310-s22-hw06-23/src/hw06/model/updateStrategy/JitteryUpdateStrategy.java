package hw06.model.updateStrategy;

import java.awt.Point;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Strategy for the ball to jitter horizontally
 * @author Megan Xiao, Son Nguyen
 *
 */
public class JitteryUpdateStrategy implements IUpdateStrategy {

	@Override
	public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		context.setVelocity(new Point(-context.getVelocity().x, context.getVelocity().y)); // constantly flips direction of velocity
	}

	/**
	 * Initialize the update strategy. This method must be run whenever the ball gets a new strategy.
	 */
	@Override
	public void init(IBall host) {

	}
}
