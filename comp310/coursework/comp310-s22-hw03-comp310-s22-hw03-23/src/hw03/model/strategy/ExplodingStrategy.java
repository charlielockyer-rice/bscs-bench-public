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
		if(ball.hasBounced()[0]) {
			disp.removeObserver(ball);
			if(ball.getRadius() > 0)
				disp.addObserver(new Ball(ball.getLocation(), ball.getVelocity(), 
						ball.getColor(), ball.getRadius() / 2, ball.getDimension(), ball.getStrategy()));
				disp.addObserver(new Ball(ball.getLocation(), new Point(ball.getVelocity().x, ball.getVelocity().y * -1), 
						ball.getColor(), ball.getRadius() / 2, ball.getDimension(), ball.getStrategy()));
		}
		else if(ball.hasBounced()[1]) {
			disp.removeObserver(ball);
			if(ball.getRadius() > 0)
				disp.addObserver(new Ball(ball.getLocation(), ball.getVelocity(), 
						ball.getColor(), ball.getRadius() / 2, ball.getDimension(), ball.getStrategy()));
				disp.addObserver(new Ball(ball.getLocation(), new Point(ball.getVelocity().x * -1, ball.getVelocity().y), 
						ball.getColor(), ball.getRadius() / 2, ball.getDimension(), ball.getStrategy()));
		}
	}

}
