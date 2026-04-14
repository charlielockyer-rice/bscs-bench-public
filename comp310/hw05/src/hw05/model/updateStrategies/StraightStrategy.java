package hw05.model.updateStrategies;

import hw05.model.IBall;
import hw05.model.IBallCmd;
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
