package hw04.model.paint.strategies;

import java.awt.Graphics;
import hw04.model.IBall;

/**
 * Ball painting strategy
 * @author charlielockyer
 *
 */
public class BallPaintStrategy implements IPaintStrategy {
	@Override
	public void init(IBall ball) {
		System.out.println("Init for ball paint strategy entered");
	}
	
	@Override
	public void paint(Graphics g, IBall ball) {
		g.setColor(ball.getColor());
		g.fillOval(ball.getLocation().x - ball.getRadius(), ball.getLocation().y - ball.getRadius(),
				2 * ball.getRadius(), 2 * ball.getRadius());
	}
}