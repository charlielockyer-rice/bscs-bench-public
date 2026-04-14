package hw04.model.strategy;

import java.awt.Color;

import hw04.model.IUpdateStrategy;
import hw04.model.IBall;
import hw04.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.valueGenerator.IRandomizer;
import provided.utils.valueGenerator.impl.Randomizer;

/**
 * A strategy to update a ball that changes the color to a random color
 * every time the ball is updated.
 */
public class ColorStrategy implements IUpdateStrategy {
	
	/**
	 * Randomizer to generate colors
	 */
	IRandomizer random = Randomizer.Singleton;

	@Override
	/**
	 * Method to update the color of the given ball
	 * @param context the ball whose state is being updated
	 * @param disp the dispatcher observing the ball
	 */
	public void updateState( IBall context, IDispatcher<IBallCmd> disp) {
		Color newColor = random.randomColor();
		context.setColor(newColor);
	}
	
	@Override
	public void init(IBall context) {
		
	}


}
