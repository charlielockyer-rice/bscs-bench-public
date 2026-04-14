package hw05.model.updateStrategies;


import hw05.model.IBall;
import hw05.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;

/**
 * An update strategy that makes the ball "fall" towards the ground.
 */
public class GravityStrategy implements IUpdateStrategy {

	/**
	 * Applies gravitational effects to the velocity
	 * @param context the ball being updated
	 * @param disp the dispatcher observing the ball
	 */
	@Override
	public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		// TODO: Implement this method
	}
	
	@Override
	public void init(IBall context) {
		
	}


}
