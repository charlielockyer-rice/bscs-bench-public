package hw03.model.strategy;

import java.awt.Graphics;
import java.awt.Point;
import java.util.Random;

import hw03.model.Ball;
import hw03.model.IUpdateStrategy;
import provided.utils.dispatcher.IDispatcher;

/**
 * @author Christina
 * Teleporting strategy for the ball class
 */
public class TeleportingStrategy implements IUpdateStrategy {
	/**
	 * Every 20 ticks, the ball teleports to a random location on the screen
	 */
	private int ticks = 0;
	public void updateState(IDispatcher<Graphics> disp, Ball ball) {
		// TODO: Implement this method
	}

}
