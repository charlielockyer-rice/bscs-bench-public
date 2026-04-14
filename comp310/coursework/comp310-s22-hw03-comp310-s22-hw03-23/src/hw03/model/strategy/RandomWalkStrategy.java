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
		Random r = new Random();
		double randomX = r.nextDouble();
		double randomY = r.nextDouble();
		Point newVel =  ball.getVelocity();
		
		if(randomX>0.5)
			newVel.x += 1;
		else
			newVel.x -= 1;
		if(randomY > 0.5)
			newVel.y += 1;
		else
			newVel.y -= 1;
		ball.setVelocity(newVel);
	}
}
