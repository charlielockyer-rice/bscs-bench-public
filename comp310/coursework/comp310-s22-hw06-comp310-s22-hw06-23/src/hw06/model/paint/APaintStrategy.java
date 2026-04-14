package hw06.model.paint;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;

import hw06.model.BallHosts.IBall;

/**
 * This class provides the basic affine transform services that its subclasses will use to resize,
 *  translate and rotate their prototype images into their proper current locations and orientations on the screen.
 *  Tis class is designed to be the root class for all strategies that use affine transforms to create their 
 *  visual representations.
 * @author Son Nguyen, Cole Rabson
 */
public abstract class APaintStrategy implements IPaintStrategy {
	/**
	 * The affine transform object.
	 */
	protected AffineTransform at = new AffineTransform();

	/**
	 * Constructor for the APaintStrategy.
	 * @param at The affine transform objected that is used to do all the affine transform
	 * needed.
	 */
	public APaintStrategy(AffineTransform at) {

	}

	@Override
	/**
	 * Initialize the update strategy. This method must be run whenever the ball gets a new strategy.
	 * @param host -> the host ball.
	 */
	public void init(IBall host) {

	}

	@Override
	/**
	 * Paint the ball to the screen.
	 * @param g The graphics to paint.
	 * @param host the host ball
	 */
	public void paint(Graphics g, IBall host) {
		double scale = host.getRadius();
		at.setToTranslation(host.getLocation().x, host.getLocation().y);
		at.scale(scale, scale);
		at.rotate(host.getVelocity().x, host.getVelocity().y);
		g.setColor(host.getColor());
		paintCfg(g, host);
		paintXfrm(g, host, at);
	}

	/**
	 * A concrete no-op method that the subclasses may or may not override.
	 * Allowing for additional processing of the ball into the paint method process
	 * beore the final transformations are performed.
	 * @param g The graphics objected to be painted on.
	 * @param host The host ball.
	 */
	protected void paintCfg(Graphics g, IBall host) {
	}

	/**
	 * Last step of the paint method, allowing affined transform-based paint strategies to
	 * be combined based on this method but not based on the paint method.
	 * @param g The graphics object to use paint.
	 * @param host The host ball.
	 * @param at An Affine Transform.
	 */
	public abstract void paintXfrm(Graphics g, IBall host, AffineTransform at);

	/**
	 * @return The affine transform that the Strategy currently has
	 */
	protected AffineTransform getAT() {
		return at;
	}
}
