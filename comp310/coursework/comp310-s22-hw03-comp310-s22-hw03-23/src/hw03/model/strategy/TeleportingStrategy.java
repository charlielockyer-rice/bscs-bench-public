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
		if(ticks > 19) {
			Random r = new Random();
			int newX = (int) (ball.getDimension().getWidth() * r.nextDouble());
			int newY = (int) (ball.getDimension().getHeight() * r.nextDouble());
			ball.setLocation(new Point(newX, newY));
			int newXVel= (int) ((r.nextDouble() - 0.5) * 10);
			int newYVel = (int) ((r.nextDouble()- 0.5) * 10);
			ball.setVelocity(new Point(newXVel, newYVel));
			ticks = 0;
		}
		ticks++;
	}

}
