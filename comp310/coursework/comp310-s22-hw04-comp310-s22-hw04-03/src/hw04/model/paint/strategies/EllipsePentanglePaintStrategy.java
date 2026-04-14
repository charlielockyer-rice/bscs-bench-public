package hw04.model.paint.strategies;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;

import hw04.model.IBall;

/**
 * Paints an ellipse win pentangles that is done with multi strategies
 * @author charlielockyer
 *
 */
public class EllipsePentanglePaintStrategy extends MultiPaintStrategy {
	public EllipsePentanglePaintStrategy() {
		this(new AffineTransform());
	}
	
	public EllipsePentanglePaintStrategy(AffineTransform at) {
		this(at, new PentanglePaintStrategy(at, 0.0, 0.0, 3.0, 3.0), new EllipsePaintStrategy(at, -1.5, 2.0, 5.0, 1.0));
	}
	
	public EllipsePentanglePaintStrategy(AffineTransform at, APaintStrategy... paintStrategies) {
		super(at, paintStrategies);
	}
	
	public void paintCfg(Graphics g, IBall ball) {
		if(Math.abs(Math.atan2(ball.getVelocity().y, ball.getVelocity().x)) > Math.PI / 2.0) {
			at.scale(1, -1);
		}
	}
}
