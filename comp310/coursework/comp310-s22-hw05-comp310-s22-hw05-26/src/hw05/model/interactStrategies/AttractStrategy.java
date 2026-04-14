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
		// "source" ball is the "context" of the strategy.  The "target" ball is the ball receiving the command from the source ball's strategy.
		
		return new IBallCmd() {
			
			public void apply(IBall ball, IDispatcher<IBallCmd> disp) {
				Point attractVector = new Point((int)((target.getLocation().x - source.getLocation().x) * attractNudge), (int)((target.getLocation().y - source.getLocation().y) * attractNudge));
				ball.setVelocity(new Point(ball.getVelocity().x + attractVector.x, ball.getVelocity().y + attractVector.y));

			}
		};
	}
	
	public void init(IBall context) {
		// TODO Auto-generated method stub
		
	}
	
	// Utilities for calculating reduced mass, unit vector, impuylse and nudge vector elided.

}

