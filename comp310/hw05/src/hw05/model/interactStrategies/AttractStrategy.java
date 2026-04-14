package hw05.model.interactStrategies;

import java.awt.Point;
import hw05.model.IBall;
import hw05.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;

/**
 * A strategy in which balls that interact are attracted to one another in a way that appears magnetic
 * @author charlielockyer
 *
 */
public class AttractStrategy implements  IInteractStrategy {
	
	/**
	 * Nudge factor
	 */
	static final double attractNudge = 0.0025;

	public IBallCmd interactWith(IBall source, IBall target, IDispatcher<IBallCmd> disp) {
		// TODO: Implement this method
		return (context, d) -> {};
	}
	
	public void init(IBall context) {
		// TODO Auto-generated method stub
		
	}
	
	// Utilities for calculating reduced mass, unit vector, impuylse and nudge vector elided.

}

