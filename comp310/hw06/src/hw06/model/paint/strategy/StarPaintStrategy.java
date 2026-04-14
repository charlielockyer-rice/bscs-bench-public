package hw06.model.paint.strategy;

import java.awt.*;

import hw06.model.BallHosts.IBall;
import hw06.model.paint.IPaintStrategy;

/**
 * Paint strategy that paints a filled StarPaintStrategy.
 * @author Son Nguyen and Cole Rabson
 */
public class StarPaintStrategy implements IPaintStrategy {

	/**
	 * No parameter constructor for the class
	 */
	public StarPaintStrategy() {
	}

	/**
	 * Paints a StarPaintStrategy of a given radius
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
