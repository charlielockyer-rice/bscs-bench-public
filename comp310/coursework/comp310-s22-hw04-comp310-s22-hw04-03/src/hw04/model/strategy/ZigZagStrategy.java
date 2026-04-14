package hw04.model.strategy;

import java.awt.Point;

import provided.utils.valueGenerator.IRandomizer;
import provided.utils.valueGenerator.impl.Randomizer;
import hw04.model.IUpdateStrategy;
import hw04.model.IBall;
import hw04.model.IBallCmd;
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
		int modSteps = this.steps % 30;
		
		if(modSteps == 10)
		{
			context.setVelocity(new Point(-context.getVelocity().x, context.getVelocity().y));
			context.setColor(rand.randomColor());
			
		}
		
		this.steps++;
	}
	
	@Override
	public void init(IBall context) {
		
	}


}
