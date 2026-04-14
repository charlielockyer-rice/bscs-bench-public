package hw03.model.strategy;

import java.awt.Graphics;

import hw03.model.Ball;
import hw03.model.IUpdateStrategy;
import provided.utils.dispatcher.IDispatcher;

/**
 * @author Christina
 * Ball expands and contracts
 */
public class ExpandingStrategy implements IUpdateStrategy {

	/**
	 * Boolean for whether should expand or not
	 */
	boolean expand = true;
	/**
	 * @param disp the dispatcher to communicate
	 * @param ball the ball class
	 */
	public void updateState(IDispatcher<Graphics> disp, Ball ball) {
		// TODO: Implement this method
	}
}