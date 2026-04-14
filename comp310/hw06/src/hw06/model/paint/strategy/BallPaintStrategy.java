package hw06.model.paint.strategy;

import java.awt.*;

import hw06.model.BallHosts.IBall;
import hw06.model.paint.IPaintStrategy;

/**
 * Paint strategy that paints a filled Ball with the Ball's radius.
 * @author Son Nguyen and Cole Rabson
 */
public class BallPaintStrategy implements IPaintStrategy {

	/**
	 * No parameter constructor for the class
	 */
	public BallPaintStrategy() {
	}

	/**
	 * Paints a square on the given graphics context using the color and radius
	 * provided by the host.
	 * param g The Graphics context that will be paint on
	 * param host The host Ball that the required information will be pulled from.
	 */
	public void paint(Graphics g, IBall host) {
		// TODO: Implement this method
	}

	/**
	 * By default, do nothing for initialization.
	 */
	public void init(IBall context) {
	}
}
