package hw04.model.paint.strategies;

import java.awt.geom.AffineTransform;

/**
 * A painting strategy that paints an image of a tesla
 * @author charlielockyer
 *
 */
public class TeslaPaintStrategy extends ImagePaintStrategy {
	private static String imagePath = "images/tesla.png";
	private static double fillFactor = 1.0;
	
	public TeslaPaintStrategy() {
		this(new AffineTransform(), imagePath, fillFactor);
	}
	
	public TeslaPaintStrategy(AffineTransform at, String imageFile, double fillFactor) {
		super(at, imageFile, fillFactor);
	}

}
