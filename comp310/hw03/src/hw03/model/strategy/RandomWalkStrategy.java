package hw03.model.strategy;

import java.awt.Point;
import java.util.Random;

import hw03.model.Ball;
import java.awt.Graphics;
import hw03.model.IUpdateStrategy;
import provided.utils.dispatcher.IDispatcher;

/**
 * @author Christina
 * Strategy that randomly changes the velocity of the ball
 */
public class RandomWalkStrategy implements IUpdateStrategy {

	
	/**
	 * @param disp the dispatcher to communicate
	 * @param ball the ball class
	 */
	public void updateState(IDispatcher<Graphics> disp, Ball ball) {
		// TODO: Implement this method
	}
}
