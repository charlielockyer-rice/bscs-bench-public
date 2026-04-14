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
		// TODO: Implement this constructor
		super(new AffineTransform(), "", 1.0);
	}

	/**
	 *
	 * @param at affine transform
	 * @param imageFile the image file
	 * @param fillFactor fill factor of the image
	 */
	public DogPaintStrategy(AffineTransform at, String imageFile, double fillFactor) {
		// TODO: Implement this constructor
		super(at, imageFile, fillFactor);
	}
}
