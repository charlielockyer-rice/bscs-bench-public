package hw03.model.strategy;

import java.awt.Graphics;
import java.awt.Point;

import hw03.model.Ball;
import hw03.model.IUpdateStrategy;
import provided.utils.dispatcher.IDispatcher;

/**
 * 
 * @author charlielockyer
 * A strategy in which a ball splits into two when it hits a wall
 */
public class ExplodingStrategy implements IUpdateStrategy{

	/**
	 * Updates the state like in all strategies
	 */
	public void updateState(IDispatcher<Graphics> disp, Ball ball) {
		// TODO: Implement this method
	}

}
