package hw06.model.updateStrategy;

import java.awt.Color;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Strategy that defines an interaction criteria in which in which two balls have similar colors.
 *
 * @author Son Nguyen and Smit Viroja
 */
public class SameColorUpdateStrategy implements IUpdateStrategy {

	/**
	 * Threshold for the color distance
	 */
	private static final int threshhold = 100;


	/**
	 * The update strategy for SameColorUpdateStrategy. Checks that the difference between interacting
	 * ball colors is below the threshold, and then updates the "context" and "other" ball with commands to interact
	 * (via some IInteractStrategy).
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
	 * Initialize the SameColorUpdateStrategy. This method must be run whenever the ball gets a new strategy.
	 */
	@Override
	public void init(IBall host) {

	}

	 /**
     * Calculate the color distance between two colors where the distance is the
     * geometric distance between the two colors as points in a 3D RGB space.
     * @param color1  The first color
     * @param color2  The second color
     * @return the distance between the two colors
     */
    private double colorDistance(Color color1, Color color2) {
        // TODO: Implement this method
        return 0;
    }
}
