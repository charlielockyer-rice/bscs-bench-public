package hw05.model.paintStrategies;

import java.awt.geom.AffineTransform;

/**
 * Paints a dog
 * @author charlielockyer
 *
 */
public class DogPaintStrategy extends ImagePaintStrategy {
	/**
	 * path of the image
	 */
	private static String imagePath = "images/dog.png";
	/**
	 * fill factor of the image
	 */
	private static double fillFactor = 1.0;
	
	/**
	 * The strategy that paints a dog
	 */
	public DogPaintStrategy() {
		this(new AffineTransform(), imagePath, fillFactor);
	}
	
	/**
	 *
	 * @param at affine transform
	 * @param imageFile the image file
	 * @param fillFactor fill factor of the image
	 */
	public DogPaintStrategy(AffineTransform at, String imageFile, double fillFactor) {
		super(at, imageFile, fillFactor);
	}
}
