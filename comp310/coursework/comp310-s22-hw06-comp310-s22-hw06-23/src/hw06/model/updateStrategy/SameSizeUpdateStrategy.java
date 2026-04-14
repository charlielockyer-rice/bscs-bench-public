package hw06.model.updateStrategy;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Strategy that defines an interaction criteria in which in which two balls have similar sizes.
 * 
 * @author Son Nguyen and Smit Viroja
 */
public class SameSizeUpdateStrategy implements IUpdateStrategy {
	
	/**
	 * Threshold for the size differences.
	 */
	private static final int threshhold = 5;
	
	
	/**
	 * The update strategy for SameSizeUpdateStrategy. Checks that the difference between interacting
	 * ball sizes is below the threshold, and then updates the "context" and "other" ball with commands to interact
	 * (via some IInteractStrategy).
	 * 
	 * @param context -> the input ball for this strategy. 
	 * @param dispatcher -> the IDispatcher for the ball. 
	 *  
	 */
	@Override
	public void updateState(IBall context, IDispatcher<IBallCmd> dispatcher) {
		dispatcher.updateAll(new IBallCmd() {
			public void apply(IBall other, IDispatcher<IBallCmd> disp) {
				if (!other.equals(context) && Math.abs(context.getRadius()-other.getRadius()) < threshhold) {   // check size difference below threshold
			        // Save the commands that were generated.   Note that each ball is doing its own interaction behavior/calculations.
					IBallCmd contextPostInteractCmd = context.interactWith(other, disp);
					IBallCmd otherPostInteractCmd = other.interactWith(context, disp);
			        
					// Run the saved commands now that both balls' interaction behaviors are complete.
					context.update(disp, contextPostInteractCmd);
					other.update(disp, otherPostInteractCmd);
				}
			}
		});
	}
	
	/**
	 * @param host -> the host ball. 
	 * Initialize the SameSizeUpdateStrategy. This method must be run whenever the ball gets a new strategy.
	 */
	@Override
	public void init(IBall host) {
	}
}
