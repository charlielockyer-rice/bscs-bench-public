package hw06.model.paint.strategy;

import java.awt.geom.AffineTransform;

import hw06.model.paint.ImagePaintStrategy;

/**
 * A class to paint the custom image contained by the FIFA_Soccer_Ball.png file.
 * @author Son Nguyen and Cole Rabson
 */
public class SoccerImagePaintStrategy extends ImagePaintStrategy {
	/**
	 * A constructor for the SoccerImagePaintStrategy Class. 
	 */
	public SoccerImagePaintStrategy() {
		super(new AffineTransform(), "images/FIFA_Soccer_Ball.png", .4);
	}

}
