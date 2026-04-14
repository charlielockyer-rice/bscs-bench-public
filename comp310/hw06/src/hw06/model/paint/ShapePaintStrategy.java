package hw06.model.paint;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import hw06.model.BallHosts.IBall;

/**
 * Concrete class that defines invariant painting behaviors to paint Shape objects for all its subclasses.
 * @author phuso
 *
 */
public class ShapePaintStrategy extends APaintStrategy {

	/**
	 * A prototype shape defaulting to null. 
	 */
	private Shape prototypeShape = null;

	/**
	 * @param at -> the Affine Transform to take place to the shape. 
	 * @param shape -> the shape to be painted. 
	 */
	public ShapePaintStrategy(AffineTransform at, Shape shape) {
		super(at);
		this.prototypeShape = shape;
	}

	/**
	 * @param shape -> the shape to be painted. 
	 */
	public ShapePaintStrategy(Shape shape) {
		super(new AffineTransform());
		this.prototypeShape = shape;
	}

	@Override
	/**
	 * Last step of the paint method, allowing affined transform-based paint strategies to
	 * be combined based on this method but not based on the paint method.
	 * @param g The graphics object to use paint.
	 * @param host The host ball.
	 * @param at An Affine Transform.
	 */
	public void paintXfrm(Graphics g, IBall host, AffineTransform at) {
		((Graphics2D) g).fill(at.createTransformedShape(prototypeShape));
	}

}
