package hw06.model.paint;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;

import hw06.model.BallHosts.IBall;

/**
 * @author phuso
 * A class for a ball that contains multiple paint strategies and will always orient itself upright.
 */
public class UprightMultiPaintStrategy extends MultiPaintStrategy {
	/**
	 * @param pstrats -> the paint strategy.
	 * Constructor for the UprightMultiPaintStrategy class.
	 */
	public UprightMultiPaintStrategy(APaintStrategy... pstrats) {
		super(pstrats);
	}

	/**
	 * @param at -> the Affine Transform.
	 * @param pstrats -> the paint strategy.
	 * Constructor for the uprightMultiPaintStrategy class that takes the Affine transform.
	 */
	public UprightMultiPaintStrategy(AffineTransform at, APaintStrategy... pstrats) {
		super(at, pstrats);
	}

	/**
	 * @param g -> the graphics to paint the ball.
	 * @param host -> the host ball.
	 * A concrete no-op method that the subclasses may or may not override. Allowing for additional processing of the ball into the paint method process beore the final transformations are performed.
	 */
	@Override
	protected void paintCfg(Graphics g, IBall host) {
		// TODO: Implement this method
	}
}
