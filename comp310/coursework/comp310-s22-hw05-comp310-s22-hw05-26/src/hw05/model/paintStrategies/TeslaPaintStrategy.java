package hw05.model.paintStrategies;

import java.awt.geom.AffineTransform;

/**
 * A painting strategy that paints an image of a tesla
 * @author charlielockyer
 *
 */
public class TeslaPaintStrategy extends ImagePaintStrategy {
	/**
	 * the path of the image
	 */
	private static String imagePath = "images/tesla.png";
	/**
	 * the fill factor of the image
	 */
	private static double fillFactor = 1.0;
	
	/**
	 * The paint strategies of a Tesla
	 */
	public TeslaPaintStrategy() {
		this(new AffineTransform(), imagePath, fillFactor);
	}
	
	/**
	 * @param at AffineTransform
	 * @param imageFile the file of the image
	 * @param fillFactor the fill factor of the image
	 */
	public TeslaPaintStrategy(AffineTransform at, String imageFile, double fillFactor) {
		super(at, imageFile, fillFactor);
	}

}
