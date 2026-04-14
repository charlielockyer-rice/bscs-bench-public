package hw05.model.updateStrategies;

import java.awt.Color;
import hw05.model.IBall;
import hw05.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;

/**
 * A criteria that allows balls of the same (or similar, depending on threshold) color to interact
 * @author charlielockyer
 *
 */
public class SameColorCriteriaStrategy implements IUpdateStrategy{
	
	/**
	 * The threshold of the difference between colors
	 */
	static final int colorDistanceThreshold = 45;
	
	public void updateState(final IBall source, IDispatcher<IBallCmd> dispatcher) {
		dispatcher.updateAll(new IBallCmd() {
			
			@Override
			public void apply(IBall target, IDispatcher<IBallCmd> disp) {
				if (target == source) {
					return;
				}
				
				if (colorDistance(source.getColor(), target.getColor()) < colorDistanceThreshold) {// whatever the interaction criteria is
				        // Save the commands that were generated.   Note that each ball is doing its own interaction behavior/calculations.
						IBallCmd contextPostInteractCmd = source.interactWithThen(target, disp);
						IBallCmd otherPostInteractCmd = target.interactWithThen(source, disp);
				        
						// Run the saved commands now that both balls' interaction behaviors are complete.
						source.update(disp, contextPostInteractCmd);
						target.update(disp, otherPostInteractCmd);
					}
			}
		}
	);

	}

	@Override
	public void init(IBall context) {
		// TODO Auto-generated method stub
		
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
