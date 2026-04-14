package hw06.model.interactStrategy;

import java.awt.Point;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import hw06.model.updateStrategy.IUpdateStrategy;
import hw06.model.updateStrategy.MultiUpdateStrategy;
import provided.utils.dispatcher.IDispatcher;


/**
 * Interaction strategy for creating a leader ball that makes other balls follow it closely after a given interaction criteria is met.
 *
 * @author Son Nguyen and Smit Viroja
 */

public class LeadInteractStrategy implements  IInteractStrategy {

	/**
	 * Changes the ball according to the Lead interaction strategy.
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
