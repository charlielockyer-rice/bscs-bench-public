package hw03.model.strategy;

import java.awt.Graphics;

import hw03.model.Ball;
import hw03.model.IUpdateStrategy;
import provided.utils.dispatcher.IDispatcher;

/**
 * @author Christina
 * Switcher strategy to change 
 */
public class SwitcherStrategy implements IUpdateStrategy{
	
	/**
	 * strategy tag for the switcher
	 */
	private IUpdateStrategy strategy = new StraightStrategy();

	@Override
	public void updateState(IDispatcher<Graphics> disp, Ball ball) {
		this.strategy.updateState(disp, ball);
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
}
