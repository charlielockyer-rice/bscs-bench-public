package hw05.model.updateStrategies;

import java.awt.Point;

import provided.utils.valueGenerator.IRandomizer;
import provided.utils.valueGenerator.impl.Randomizer;
import hw05.model.IBall;
import hw05.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;

/**
 * A ZigZag strategy is a strategy that changes direction and color of the ball
 * at certain time intervals while also maintaining the bounce properties
 * of a straight ball
 * @author akashkaranam
 *
 */
public class ZigZagStrategy implements IUpdateStrategy {
	/**
	 * The number of time steps for the current ball
	 */
	private int steps = 0;
	/**
	 * Randomizer used to generate random color for ball
	 */
	private IRandomizer rand =  Randomizer.Singleton;
	
	/**
	 * 
	 * Method to change the direction and color of the ball at certain time intervals
	 * @param context the Ball whose state is being updated
	 * @param disp the dispatcher that makes calls to update the balls
	 * 
	 */
	@Override
	public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		// TODO: Implement this method
	}
	
	@Override
	public void init(IBall context) {
		
	}


}
