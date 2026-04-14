package hw04.model.strategy;

import hw04.model.IUpdateStrategy;
import hw04.model.IBall;
import hw04.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;


/**
 * @author charlielockyer
 * Switcher strategy to change 
 */
public class SwitcherStrategy implements IUpdateStrategy{
	
	/**
	 * strategy tag for the switcher
	 */
	private IUpdateStrategy strategy = new StraightStrategy();

	@Override
	public void updateState(IBall ball, IDispatcher<IBallCmd> disp) {
		// TODO: Implement this method
	}
	
	/**
	 * @param  strategy type for the switch strategy
	 */
	public void switchStrategy(IUpdateStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * @return a strategy for the getter method
	 */
	public IUpdateStrategy getStrategy() {
		return this.strategy;
	}
	
	@Override
	public void init(IBall context) {
		
	}

}
