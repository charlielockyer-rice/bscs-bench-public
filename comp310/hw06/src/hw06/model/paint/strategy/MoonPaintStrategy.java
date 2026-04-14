package hw06.model.paint.strategy;

import java.awt.geom.AffineTransform;
import hw06.model.paint.ImagePaintStrategy;

/**
 * A class to paint the custom image contained by the moon.png file. 
 * @author Son Nguyen and Cole Rabson
 *
 */
public class MoonPaintStrategy extends ImagePaintStrategy {

	/**
	 * Paints the image of the earth as an image. 
	 */
	public MoonPaintStrategy() {
		super(new AffineTransform(), "images/moon.png", 1);
	}
}
