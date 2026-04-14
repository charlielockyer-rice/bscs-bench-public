package hw04.model.paint.strategies;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;

import hw04.model.IBall;
import hw04.model.paint.shape.RectangleFac;

/**
 * A square painting strategy
 * @author charlielockyer
 *
 */
public class SquarePaintStrategy implements IPaintStrategy {
	
	@Override
	public void init(IBall ball) {
		System.out.println("init for square paint strategy entered");
	}
	
	@Override
	public void paint(Graphics g, IBall ball) {
		g.setColor(ball.getColor());
		g.fillRect(ball.getLocation().x - ball.getRadius(), ball.getLocation().y - ball.getRadius(),
				ball.getRadius(), ball.getRadius());
	}
	
}
