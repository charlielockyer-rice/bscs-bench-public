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
		// TODO: Implement this constructor
		super(new AffineTransform(), "", 1.0);
	}

	public DogPaintStrategy(AffineTransform at, String imageFile, double fillFactor) {
		// TODO: Implement this constructor
		super(at, imageFile, fillFactor);
	}
}
