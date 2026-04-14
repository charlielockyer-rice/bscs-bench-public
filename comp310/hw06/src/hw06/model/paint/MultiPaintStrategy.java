package hw06.model.paint;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;

import hw06.model.BallHosts.IBall;

/**
 * A composite design pattern extesion of APaintStrategy that paints a set of
 * paint strategies
 * @author Son Nguyen and Cole Rabson
 *
 */
public class MultiPaintStrategy extends APaintStrategy {
	/**
	 * A set of paint strategies to paint
	 */
	private APaintStrategy[] pstrats;

	/**
	 * @param at -> the affine transform.
	 * @param pstrats -> the paint strategy.
	 */
	public MultiPaintStrategy(AffineTransform at, APaintStrategy... pstrats) {
		super(at);
		this.pstrats = pstrats;
	}

	/**
	 * @param pstrats -> the paint strategy.
	 */
	public MultiPaintStrategy(APaintStrategy... pstrats) {
		super(new AffineTransform());
		this.pstrats = pstrats;
	}

	/**
	 * @param host -> the host ball.
	 * Initialize the paint strategy. This method must be run whenever the ball gets a new strategy.
	 */
	@Override
	public void init(IBall host) {
		// TODO: Implement this method
	}

	/**
	 * @param g -> the graphics to paint the ball.
	 * @param host -> the host ball.
	 * @param at -> an affine transform.
	 * Last step of the paint method, allowing affined transform-based paint strategies to be combined based on this method but not based on the paint method.
	 */
	@Override
	public void paintXfrm(Graphics g, IBall host, AffineTransform at) {
		// TODO: Implement this method
	}

}
