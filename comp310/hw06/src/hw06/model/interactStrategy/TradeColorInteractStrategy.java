package hw06.model.interactStrategy;

import java.awt.Color;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Interaction strategy for the context ball and the interacting ball to trade their colors (whenever a given
 * interaction criteria is met).
 *
 * @author Son Nguyen and Smit Viroja
 */
public class TradeColorInteractStrategy implements  IInteractStrategy {

	/**
	 * Changes the ball according to the TradeColor interaction strategy.
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

	}

}
