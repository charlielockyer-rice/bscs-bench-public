package hw03.model.strategy;

import java.awt.Graphics;
import java.awt.Point;

import hw03.model.Ball;
import hw03.model.IUpdateStrategy;
import provided.utils.dispatcher.IDispatcher;

/**
 * @author Christina
 * Strategy that reverse the ball every 10 ticks
 */
public class ReverseStrategy implements IUpdateStrategy {
	private int ticks = 0;
	
	/**
	 * @param disp the dispatcher to communicate
	 * @param ball the ball class
	 */
	public void updateState(IDispatcher<Graphics> disp, Ball ball) {
		if(ticks > 9) {
			ticks = 0;
			ball.setVelocity(new Point(ball.getVelocity().x * -1, ball.getVelocity().y * -1));
		}
		ticks++;
	}

}
