package hw04.model.strategy;

import hw04.model.IUpdateStrategy;
import provided.utils.dispatcher.IDispatcher;
import hw04.model.IBall;
import hw04.model.IBallCmd;

/**
 * An update strategy that combines two strategies into a strategy with both behaviors
 */
public class MultiStrategy implements IUpdateStrategy {
	
	/**
	 * The update strategy containing the first behavior
	 */
	private IUpdateStrategy firstStrategy;
	
	/**
	 * The update strategy containing the second behavior
	 */
	private IUpdateStrategy secondStrategy;
	
	/**
	 * Instantiates a MultiStrategy given two update strategies to combine
	 * @param firstStrategy the update strategy containing the first behavior
	 * @param secondStrategy the update strategy containing the second behavior
	 */
	public MultiStrategy(IUpdateStrategy firstStrategy, IUpdateStrategy secondStrategy) {
		this.firstStrategy = firstStrategy;
		this.secondStrategy = secondStrategy;
	}

	@Override
	/**
	 * Method to update state of ball based on MultiStrategy
	 * @param context the ball whose state is being updated
	 * @param disp the dispatcher that is observing the ball
	 */
	public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		// TODO: Implement this method
	}
	
	@Override
	public void init(IBall context) {
		
	}


}
