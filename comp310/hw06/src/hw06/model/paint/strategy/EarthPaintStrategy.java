package hw06.model.paint.strategy;

import java.awt.geom.AffineTransform;
import hw06.model.paint.ImagePaintStrategy;

/**
 * A class to paint the custom image contained by the Earth.png file. 
 * @author Son Nguyen and Cole Rabson
 *
 */
public class EarthPaintStrategy extends ImagePaintStrategy {

	/**
	 * Paints the image of the earth as an image. 
	 */
	public EarthPaintStrategy() {
		super(new AffineTransform(), "images/Earth.png", 1);
	}
}
