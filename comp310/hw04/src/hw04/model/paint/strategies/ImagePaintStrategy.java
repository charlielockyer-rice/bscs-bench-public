package hw04.model.paint.strategies;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;

import hw04.model.IBall;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.utils.displayModel.IATImage;

/**
 * Paints an image that is loaded
 * @author charlielockyer
 *
 */
public class ImagePaintStrategy extends APaintStrategy {

	/**
	 * Instantite the image
	 */
	private Image image;

	private IATImage atImage;

	/**
	 * The doubles are for image scale factors and fill percentages
	 */
	private double scale = 1.0;

	private double fill = 1.0;

	protected AffineTransform preAT = new AffineTransform();

	protected AffineTransform tempAT = new AffineTransform();

	/**
	 * Constructor for an image paint strategy
	 * @param at the transform
	 * @param imageFile the path to the image file
	 * @param fillFactor the fill factor
	 */
	public ImagePaintStrategy(AffineTransform at, String imageFile, double fillFactor) {
		super(at);

		this.fill = fillFactor;

		try {
			image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource(imageFile));
		} catch (Exception e) {
			ILoggerControl.getSharedLogger().log(LogLevel.ERROR,
					"ImagePaintStrategy: Error reading file: " + imageFile + "\n");
		}
	}

	@Override
	public void init(IBall thisBall) {
		// TODO: Implement this method
	}

	@Override
	public void paintXfrm(Graphics g, IBall thisBall, AffineTransform tf) {
		// TODO: Implement this method
	}

}
