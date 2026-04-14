package hw04.model.strategy;

import hw04.model.IUpdateStrategy;
import hw04.model.IBall;
import hw04.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.valueGenerator.ISineMaker;
import provided.utils.valueGenerator.impl.SineMaker;



/**
 * An update strategy that makes the ball grow and shrink in a cyclic pattern
 */
public class BreathingStrategy implements IUpdateStrategy {
	
	/**
	 * A sine function generator to generate the radius over time
	 */
	ISineMaker sineMaker = new SineMaker(5, 20, 0.2);

	@Override
	/**
	 * Method to update the breathing strategy ball
	 * @param context the ball whose state is being updated
	 * @param disp the dispatcher that is observing the ball
	 */
	public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		int newRadius = sineMaker.getIntVal();
		context.setRadius(newRadius);
	}
	
	@Override
	public void init(IBall context) {
		
	}

}
