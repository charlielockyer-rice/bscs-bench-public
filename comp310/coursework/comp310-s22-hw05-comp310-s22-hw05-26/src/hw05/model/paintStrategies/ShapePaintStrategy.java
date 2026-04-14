package hw05.model.paintStrategies;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import hw05.model.IBall;

/**
 * Paint strategy of simple shape.
 * @author yihan
 *
 */
public class ShapePaintStrategy extends APaintStrategy {

	/**
	 * The prototype shape
	 */
	private Shape shape;

	/**
	 * Constructor with at
	 * @param at the affine transform
	 * @param shape the prototype shape
	 */
	public ShapePaintStrategy(AffineTransform at, Shape shape) {
		super(at);
		this.shape = shape;
	}

	@Override
	public void init(IBall host) {

	}

	@Override
	public void paintXfrm(Graphics g, IBall host, AffineTransform at) {
		((Graphics2D) g).fill(at.createTransformedShape(shape));
	}

}
