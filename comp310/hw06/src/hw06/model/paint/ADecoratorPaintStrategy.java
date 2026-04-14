package hw06.model.paint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;

import hw06.model.BallHosts.IBall;

/**
 * An abstract class that extends and decorate an APaint Strategy with extra features.
 * @author Son Nguyen and Cole Rabson
 */
public class ADecoratorPaintStrategy extends APaintStrategy {
	/**
	 * 
	 */
	private APaintStrategy decoree;

	/**
	 * @param decoree
	 * A constructor for a ADecoratorPaintStrategy
	 */
	public ADecoratorPaintStrategy(APaintStrategy decoree) {
		super(new AffineTransform());
		this.decoree = decoree;
	}

	/**
	 * A class to be called whenever a new ball is created. 
	 */
	@Override
	public void init(IBall host) {
		decoree.init(host);
	}

	/**
	 * @param g -> The graphics to paint.
	 * @param host -> The host ball. 
	 * Paints the ball.
	 */
	@Override
	public void paint(Graphics g, IBall host) {
		decoree.paint(g, host);

	}

	/**
	 * Last step of the paint method, allowing affined transform-based paint strategies to be combined based on this method but not based on the paint method.
	 */
	@Override
	public void paintXfrm(Graphics g, IBall host, AffineTransform at) {
		g.setColor(Color.BLACK);
		decoree.paintXfrm(g, host, at);
		g.setColor(host.getColor());
	}

}
