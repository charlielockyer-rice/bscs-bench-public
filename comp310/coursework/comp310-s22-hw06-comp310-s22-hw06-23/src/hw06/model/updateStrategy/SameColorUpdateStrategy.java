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
		dispatcher.updateAll(new IBallCmd() {
			public void apply(IBall other, IDispatcher<IBallCmd> disp) {
				if (!other.equals(context) && colorDistance(context.getColor(),other.getColor()) < threshhold) {   // whatever the interaction criteria is
			        
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
        double cDist = Math.sqrt(Math.pow(color1.getRed()-color2.getRed(), 2)+
                Math.pow(color1.getGreen()-color2.getGreen(), 2)+
                Math.pow(color1.getBlue()-color2.getBlue(), 2));
        return cDist;
    }
}
