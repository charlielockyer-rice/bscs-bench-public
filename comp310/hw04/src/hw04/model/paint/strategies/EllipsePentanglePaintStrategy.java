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
		// TODO: Implement this constructor
		super(new AffineTransform());
	}

	public EllipsePentanglePaintStrategy(AffineTransform at) {
		// TODO: Implement this constructor
		super(at);
	}

	public EllipsePentanglePaintStrategy(AffineTransform at, APaintStrategy... paintStrategies) {
		super(at, paintStrategies);
	}

	public void paintCfg(Graphics g, IBall ball) {
		// TODO: Implement this method
	}
}
