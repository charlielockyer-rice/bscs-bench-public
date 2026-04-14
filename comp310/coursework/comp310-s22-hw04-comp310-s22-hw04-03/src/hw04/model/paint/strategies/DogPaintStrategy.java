package hw04.model.paint.strategies;

import java.awt.geom.AffineTransform;

/**
 * Paints a dog
 * @author charlielockyer
 *
 */
public class DogPaintStrategy extends ImagePaintStrategy {
	private static String imagePath = "images/dog.png";
	private static double fillFactor = 1.0;
	
	public DogPaintStrategy() {
		this(new AffineTransform(), imagePath, fillFactor);
	}
	
	public DogPaintStrategy(AffineTransform at, String imageFile, double fillFactor) {
		super(at, imageFile, fillFactor);
	}
}
