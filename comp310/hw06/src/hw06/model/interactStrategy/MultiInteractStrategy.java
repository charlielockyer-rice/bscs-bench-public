package hw06.model.interactStrategy;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Strategy that compose two IInteractStrategy strategies together properly.
 * (Not an actual interaction strategy to be initiated)
 *
 * @author Son Nguyen and Smit Viroja
 */
public class MultiInteractStrategy implements IInteractStrategy {

	/**
	 * the first of the 2 strategies.
	 */
	IInteractStrategy strat1 = IInteractStrategy.NULL_OBJECT;

	/**
	 * the second of the 2 strategies.
	 */
	IInteractStrategy strat2 = IInteractStrategy.NULL_OBJECT;

	/**
	 * Constructor for the composition strategy
	 *
	 * @param strat1 -> the first of 2 interact strategies
	 * @param strat2 -> the second of the 2 interact strategies.
	 */
	public MultiInteractStrategy(IInteractStrategy strat1, IInteractStrategy strat2) {
		this.strat1 = strat1;
		this.strat2 = strat2;
	}

	/**
	 * Return an IBallCmd for applying two interaction strategies to the context ball.
	 *
	 * @param context -> the ball undergoing the strategy
	 * @param other -> the ball interacting with the context ball
	 * @param disp -> the IDispatcher for the current ball.
	 * @return A command to be executed after both balls' interaction behaviors have completed.
	 */
	@Override
	public IBallCmd interactWithThen(IBall context, IBall other, IDispatcher<IBallCmd> disp) {
		// TODO: Implement this method
		return (ball, d) -> {};
	}

	/**
	 * Initialize the interaction strategy. This method must be run whenever the ball gets a new strategy.
	 * @param host -> the host ball.
	 */
	@Override
	public void init(IBall host) {
		// TODO: Implement this method
	}

}
