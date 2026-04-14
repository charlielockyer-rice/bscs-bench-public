package hw05.model.paintStrategies;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;

import hw05.model.IBall;

/**
 * Abstract paint strategies for 
 * @author yihan
 *
 */
public abstract class APaintStrategy implements IPaintStrategy {

	/**
	 * The affine transform
	 */
	protected AffineTransform at;

	/**
	 * Constructor for APaintStrategy
	 */
	public APaintStrategy() {
		this.at = new AffineTransform();
	}

	/**
	 * Constructor of APaintStrategy.
	 * @param at The affine transform
	 */
	public APaintStrategy(AffineTransform at) {
		this.at = at;
	}

	@Override
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
	 *  Inject additional processing into the paint method process before the final transformations are performed.
	 * @param g the graphics to paint on
	 * @param host the ball being painted over
	 */
	protected void paintCfg(Graphics g, IBall host) {
		// Subclasses may or may not override this method
	}

	/**
	 * Use a supplied affine transform to paint.
	 * @param g the graphics object to paint on
	 * @param host the ball being painted over
	 * @param at the affine transform to apply to what is being painted
	 */
	public abstract void paintXfrm(Graphics g, IBall host, AffineTransform at);

	@Override
	public void init(IBall host) {
		// TODO Auto-generated method stub

	}

}
