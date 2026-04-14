package hw06.model.updateStrategy;

import java.util.Random;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Strategy to shrink the ball whenever it hits the wall.
 * @author Megan Xiao, Son Nguyen
 */
public class ShrinkingUpdateStrategy implements IUpdateStrategy {

	/**
	 * Randomizer to generate random values
	 */
	private Random rand = new Random();

	/**
	 * @param context -> the ball undergoing the strategy
	 * @param disp -> the IDispatcher for the current ball.
	 * Updates the ball according to the Shrinking update strategy.
	 */
	@Override
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
