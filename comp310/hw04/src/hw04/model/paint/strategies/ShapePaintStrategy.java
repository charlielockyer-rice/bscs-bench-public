package hw04.model.paint.strategies;

import java.awt.Graphics;
import hw04.model.IBall;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

/**
 * Shape painting strategy
 * @author charlielockyer
 *
 */
public class ShapePaintStrategy extends APaintStrategy {
	private Shape shape;
	
	/**
	 * Constructs a shape painting strategy
	 * @param shape the shape to paint
	 */
	public ShapePaintStrategy(Shape shape) {
		super(new AffineTransform());
		this.shape = at.createTransformedShape(shape);
	}
	
	/**
	 * Constructs a shape painting strategy given an affine transform as well
	 * @param at the affine transform
	 * @param shape the shape to paint
	 */
	public ShapePaintStrategy(AffineTransform at, Shape shape) {
		super(at);
		this.shape = shape;
	}
	
	@Override
	public void paintXfrm(Graphics g, IBall thisBall, AffineTransform at) {
		// TODO: Implement this method
	}
	
}
