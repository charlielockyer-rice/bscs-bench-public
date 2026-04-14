package hw03.model.strategy;

import java.awt.Graphics;
import hw03.model.Ball;
import hw03.model.IUpdateStrategy;
import provided.utils.dispatcher.IDispatcher;

/**
 * @author Christina
 * Straight ball class that moves straight
 */
public class StraightStrategy implements IUpdateStrategy {

	
	/**
	 * @param disp the dispatcher to communicate
	 * @param ball the ball class
	 */
	public void updateState(IDispatcher<Graphics> disp, Ball ball) {
		// TODO: Implement this method (straight movement is a no-op strategy)
	}

}
