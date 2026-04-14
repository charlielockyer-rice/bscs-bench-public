package hw06.model.interactStrategy;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import hw06.model.IBallAlgo2ModelAdapter;
import hw06.model.IBallCmd;
import hw06.model.BallHosts.DefaultBall;
import hw06.model.BallHosts.IBall;
import hw06.model.BallVisitors.ABallAlgoCmd;
import hw06.model.BallVisitors.AConfigBallAlgo;
import hw06.model.updateStrategy.IUpdateStrategy;
import hw06.model.updateStrategy.MultiUpdateStrategy;
import provided.ballworld.extVisitors.IBallHostID;
import provided.logger.ILoggerControl;
import provided.utils.dispatcher.IDispatcher;

/**
 * Interaction strategy that, upon interaction, causes the creation of a new ball with a random velocity, which averages the color
 * of the two balls ("context" and "other"), averages the radii of the balls, and combines their Update strategies.
 *
 * @author Son Nguyen and Smit Viroja
 */
public class MateInteractStrategy implements  IInteractStrategy {


	/**
	 * Changes the ball according to the Mate interaction strategy.
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
