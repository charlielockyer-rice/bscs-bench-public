package hw06.model.paint;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;

import hw06.model.BallHosts.IBall;
import provided.utils.displayModel.IATImage;

/**
 * Paint Strategy that paints an image from a file, scaled to the Ball's radius.
 * @author Son Nguyen and Cole Rabson
 */
public class ImagePaintStrategy extends APaintStrategy {
	/**
	 * A scalar for the image. 
	 */
	private double fillFactor;
	/**
	 * The image to be painted. 
	 */
	private Image image;
	/**
	 * The observer for the image. 
	 */
	private ImageObserver imageObs = null;
	/**
	 * A local variable for the Affine Transform. 
	 */
	private AffineTransform localAT = new AffineTransform();
	/**
	 * A scalar factor for the image.
	 */
	private double scaleFactor;
	/**
	 * A temporary Affine Transform. 
	 */
	protected AffineTransform tempAT = new AffineTransform();

	/**
	 * @param at -> the affine transform.
	 * @param filename -> the name of the file holding the image being painted. 
	 * @param fillFactor -> a scalar to change the size of the image. 
	 */
	public ImagePaintStrategy(AffineTransform at, String filename, double fillFactor) {
		super(at);
		try {
			image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource(filename));
		} catch (Exception e) {
			System.err.println("ImagePaintStrategy: Error reading file: " + filename + "\n" + e);
		}
		this.fillFactor = fillFactor;
	}

	/**
	 * @param filename the filename fo rthe image being painted. 
	 * @param fillFactor the fill factor for the image. 
	 */
	public ImagePaintStrategy(String filename, double fillFactor) {
		super(new AffineTransform());
		try {
			image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource(filename));
		} catch (Exception e) {
			System.err.println("ImagePaintStrategy: Error reading file: " + filename + "\n" + e);
		}
		this.fillFactor = fillFactor;
	}

	@Override
	/**
	 * @param host the host ball. 
	 * Initialize the paint strategy. This method must be run whenever the ball gets a new strategy.
	 */
	public void init(IBall host) {
		imageObs = host.getContainer();
		MediaTracker mt = new MediaTracker(host.getContainer());
		mt.addImage(image, 1);
		try {
			mt.waitForAll();
		} catch (Exception e) {
			System.out.println("ImagePaintStrategy.init(): Error waiting for image.  Exception = " + e);
		}

		scaleFactor = 2.0 / (fillFactor * (image.getWidth(imageObs) + image.getHeight(imageObs)) / 2.0);
		localAT.setToScale(scaleFactor, scaleFactor);
		localAT.translate(-image.getWidth(imageObs) / 2.0, -image.getHeight(imageObs) / 2.0); // Want to center it first, then scale!
	}

	@Override
	/**
	 * @param g The graphics object to use paint. 
	 * @param host the host ball.
	 * @param at An Affine Transform.
	 * Last step of the paint method, allowing affined transform-based paint strategies to be combined based on this method but not based on the paint method.
	 */
	public void paintXfrm(Graphics g, IBall host, AffineTransform at) {
		tempAT.setTransform(localAT); // Initialize the tempAT to be the preAT, i.e. copy the preAT into the tempAT
		tempAT.preConcatenate(at); // Add the normal affine transform to the "pre"-affine transform.  The "preAT" will be applied first then the "at" when transforming an image.
		IATImage.FACTORY.apply(image, host.getContainer()).draw(g, tempAT);
	}

}
