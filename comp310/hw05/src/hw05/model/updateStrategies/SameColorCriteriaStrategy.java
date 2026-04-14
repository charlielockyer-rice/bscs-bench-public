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
		// TODO: Implement this method
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
        // TODO: Implement this method
        return 0;
    }

}
