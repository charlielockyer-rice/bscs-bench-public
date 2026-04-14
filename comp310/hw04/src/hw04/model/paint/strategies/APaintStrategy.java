package hw04.model.paint.strategies;
import java.awt.geom.AffineTransform;
import hw04.model.IBall;
import java.awt.Graphics;

/**
 * An abstract paint strategy
 * @author charlielockyer
 *
 */
public abstract class APaintStrategy implements IPaintStrategy {
	protected AffineTransform at;
	
	/**
	 * Instantiates a paint strategy given an affine transform
	 * @param at the affine transform
	 */
	public APaintStrategy(AffineTransform at) {
		this.at = at;
	}
	
	@Override
	public void paint(Graphics g, IBall ball) {
		double radius = ball.getRadius();
		at.setToTranslation(ball.getLocation().x, ball.getLocation().y);
		at.scale(radius, radius);
		at.rotate(Math.atan2(ball.getVelocity().y, ball.getVelocity().x));
		g.setColor(ball.getColor());
		paintCfg(g, ball);
		paintXfrm(g, ball, at);
	}
	
	/**
	 * Initialize strategy given ball
	 */
	public void init(IBall ball) {
		
	}
	
	/**
	 * paints the object
	 * @param g the graphics object
	 * @param ball the ball to paint
	 */
	protected void paintCfg(Graphics g, IBall ball) {
		
	}
	
	public abstract void paintXfrm(Graphics g, IBall ball, AffineTransform at);
	
	protected AffineTransform getAT(AffineTransform at) {
		return this.at;
	}
	
	
}