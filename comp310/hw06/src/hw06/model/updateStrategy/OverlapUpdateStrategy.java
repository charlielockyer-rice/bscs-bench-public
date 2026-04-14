package hw06.model.updateStrategy;
import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Strategy that defines an interaction criteria in which the "context" ball and "other" ball have overlapping
 * radii.
 *
 * @author Son Nguyen and Smit Viroja
 *
 */
public class OverlapUpdateStrategy implements IUpdateStrategy {


	/**
	 * The update strategy for OverlapUpdateStrategy. Checks for overlapping radii, and then updates
	 * the "context" and "other" ball with commands to interact (via some IInteractStrategy).
	 *
	 * @param context -> the input ball for this strategy.
	 * @param dispatcher -> the IDispatcher for the ball.
	 *
	 */
	@Override
	public void updateState(IBall context, IDispatcher<IBallCmd> dispatcher) {
		// TODO: Implement this method
	}


	/**
	 * @param host -> the host ball.
	 * Initialize the overlap strategy. This method must be run whenever the ball gets a new strategy.
	 */
	@Override
	public void init(IBall host) {

	}
}
