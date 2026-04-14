package hw06.model.updateStrategy;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Strategy for the ball to be able to switch to a differnt strategy.
 * @author Smit Viroja, Son Nguyen
 */
public class SwitcherUpdateStrategy implements IUpdateStrategy {

	
	/**
	 * The internal strategy to keep track of updated strategy.
	 */
	private IUpdateStrategy strategy = IUpdateStrategy.NULL_OBJECT;

	/**
	 * Constructor for SwitcherStrategy
	 */
	public SwitcherUpdateStrategy() {
		strategy = new StraightUpdateStrategy();
	}

	/**
	 * Sets a new strategy for all strategy balls.
	 * @param newStrategy The new strategy to set
	 */
	public void setStrategy(IUpdateStrategy newStrategy) {
		strategy = newStrategy;
	}

	/**
	 * @param context -> the ball undergoing the strategy
	 * @param disp -> the IDispatcher for the current ball.
	 * Updates the ball according to the Switcher update strategy. 
	 */
	@Override
	public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		strategy.updateState(context, disp);
	}

	@Override
	/**
	 * @param host -> the host ball.
	 * Initialize the update strategy. This method must be run whenever the ball gets a new strategy.
	 */
	public void init(IBall host) {
	}
	

}
