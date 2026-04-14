package hw04.model.strategy;

import hw04.model.IUpdateStrategy;
import hw04.model.IBall;
import hw04.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;

/**
 * @author charlielockyer
 * Straight ball class that moves straight
 */
public class StraightStrategy implements IUpdateStrategy {

	
	/**
	 * @param disp the dispatcher to communicate
	 * @param ball the ball class
	 */
	public void updateState(IBall ball, IDispatcher<IBallCmd> disp) {
		
	}
	
	@Override
	public void init(IBall context) {
		
	}


}
