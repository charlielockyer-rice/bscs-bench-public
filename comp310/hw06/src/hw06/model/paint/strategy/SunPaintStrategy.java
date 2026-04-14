package hw06.model.paint.strategy;

import java.awt.geom.AffineTransform;
import hw06.model.paint.ImagePaintStrategy;

/**
 * A class to paint the custom image contained by the sun.png file. 
 * @author Son Nguyen and Cole Rabson
 *
 */
public class SunPaintStrategy extends ImagePaintStrategy {

	/**
	 * Paints the image of the earth as an image. 
	 */
	public SunPaintStrategy() {
		super(new AffineTransform(), "images/sun.png", .8);
	}
}
