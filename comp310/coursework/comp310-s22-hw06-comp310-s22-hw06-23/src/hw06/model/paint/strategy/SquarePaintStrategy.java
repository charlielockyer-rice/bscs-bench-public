package hw06.model.paint.strategy;

import java.awt.*;

import hw06.model.BallHosts.IBall;
import hw06.model.paint.IPaintStrategy;

/**
 * Paint strategy that paints a filled square with the Ball's radius.
 * @author Son Nguyen and Cole Rabson
 */
public class SquarePaintStrategy implements IPaintStrategy {

	/** 
	 * No parameter constructor for the class
	 */
	public SquarePaintStrategy() {
	}

	/**
	 * Paints a square on the given graphics context using the color and radius 
	 * provided by the host. 
	 * param g The Graphics context that will be paint on
	 * param host The host Ball that the required information will be pulled from.
	 */
	public void paint(Graphics g, IBall host) {
		int halfSide = host.getRadius();
		g.setColor(host.getColor());
		g.fillRect(host.getLocation().x - halfSide, host.getLocation().y - halfSide, 2 * halfSide, 2 * halfSide);
	}

	/**
	 * By default, do nothing for initialization.
	 */
	public void init(IBall context) {
	}
}