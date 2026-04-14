package hw06.model.updateStrategy;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Compose two IUpdate together properly. This does not count as an update strategy by itself but to
 * compose more complex ones.
 * @author Son Nguyen and Smit Viroja
 */
public class MultiUpdateStrategy implements IUpdateStrategy {
	/**
	 * the first of the 2 strategies.
	 */
	IUpdateStrategy strat1 = IUpdateStrategy.NULL_OBJECT;
	/**
	 * the second of the 2 strategies.
	 */
	IUpdateStrategy strat2 = IUpdateStrategy.NULL_OBJECT;

	/**
	 * @param strat1 -> the first of 2 update strategiess
	 * @param strat2 -> the second of the 2 update strategies.
	 */
	public MultiUpdateStrategy(IUpdateStrategy strat1, IUpdateStrategy strat2) {
		this.strat1 = strat1;
		this.strat2 = strat2;
	}

	/**
	 * @param context -> the ball undergoing the strategy
	 * @param disp -> the IDispatcher for the current ball.
	 * Updates the ball according to the Null update strategy.
	 */
	@Override
	public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		// TODO: Implement this method
	}

	/**
	 * @param host -> the host ball.
	 * Initialize the update strategy. This method must be run whenever the ball gets a new strategy.
	 */
	@Override
	public void init(IBall host) {
		// TODO: Implement this method
	}

}
