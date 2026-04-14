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
		int halfSide = host.getRadius();
		g.setColor(host.getColor());
		int[] xCoords = new int[] {host.getLocation().x -halfSide, host.getLocation().x -halfSide/4,
				 host.getLocation().x, host.getLocation().x + halfSide/4, host.getLocation().x +halfSide,
				 host.getLocation().x +halfSide/4,host.getLocation().x, host.getLocation().x -halfSide/4};
		int[] yCoords = new int[] {host.getLocation().y, host.getLocation().y -halfSide/4,
				 host.getLocation().y - halfSide, host.getLocation().y -halfSide/4, host.getLocation().y,
				 host.getLocation().y +halfSide/4,host.getLocation().y + halfSide, host.getLocation().y+halfSide/4};
		g.fillPolygon(xCoords, yCoords, 8);
	}

	/**
	 * By default, do nothing for initialization.
	 */
	public void init(IBall context) {
	}
}