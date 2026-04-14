package hw05.model.updateStrategies;
import java.awt.Point;

import provided.utils.dispatcher.IDispatcher;
import hw05.model.IBall;
import hw05.model.IBallCmd;
import provided.utils.valueGenerator.impl.Randomizer;

/**
 * An update strategy that randomly sets the ball's velocity to 0
 * and then randomly restores the ball's velocity.
 */
public class LazyStrategy implements IUpdateStrategy {
	
	/**
	 * The velocity that the ball had before becoming lazy
	 */
	Point nonLazyVelocity;

	/**
	 * True if the strategy is currently being lazy (not moving the ball), false otherwise
	 */
	boolean isLazy = false;

	@Override
	/**
	 * Method to update ball's state according to lazy strategy
	 * @param context the ball whose state is being updated
	 * @disp the dispatcher that is observing the ball
	 */
	public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		// TODO: Implement this method
	}
	
	@Override
	public void init(IBall context) {
		
	}


}
