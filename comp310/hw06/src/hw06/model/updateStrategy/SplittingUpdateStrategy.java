package hw06.model.updateStrategy;

import java.awt.Point;

import hw06.model.IBallAlgo2ModelAdapter;
import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import hw06.model.BallVisitors.ABallAlgoCmd;
import hw06.model.BallVisitors.AConfigBallAlgo;
import provided.ballworld.extVisitors.IBallHostID;
import provided.logger.ILoggerControl;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.dispatcher.IObserver;
import provided.utils.loader.IObjectLoader;
import provided.utils.loader.impl.ObjectLoader;

/**
 * Strategy for the balls to split in half periodically.
 * @author Megan Xiao, Son Nguyen
 */
public class SplittingUpdateStrategy implements IUpdateStrategy {
	@Override
	/**
	 * @param context -> the ball undergoing the strategy
	 * @param disp -> the IDispatcher for the current ball.
	 * Updates the ball according to the Splitting update strategy.
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
