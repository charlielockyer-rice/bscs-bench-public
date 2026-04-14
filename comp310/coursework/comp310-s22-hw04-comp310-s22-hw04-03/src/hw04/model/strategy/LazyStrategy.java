package hw04.model.strategy;
import java.awt.Point;

import provided.utils.dispatcher.IDispatcher;
import hw04.model.IUpdateStrategy;
import hw04.model.IBall;
import hw04.model.IBallCmd;
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
		// Switch between lazy and not lazy at the given probability of 0.01 per tick
		boolean switchDecision = (boolean) Randomizer.Singleton.randomChoice(true, false, 0.01);
		
		// Switch the lazy flag if we decided to
		if (switchDecision) {
			this.isLazy = !this.isLazy;
		}
		
		// Handle switching to lazy
		if (switchDecision && this.isLazy) {
			// Save velocity and set ball velocity to 0
			this.nonLazyVelocity = context.getVelocity();
			context.setVelocity(new Point(0, 0));
		}
		
		// Handle switching to not lazy
		if (switchDecision && !this.isLazy) {
			context.setVelocity(this.nonLazyVelocity);
		}	
		
	}
	
	@Override
	public void init(IBall context) {
		
	}


}
