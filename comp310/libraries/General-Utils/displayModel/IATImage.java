package provided.utils.displayModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.function.BiFunction;

import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

/**
 * An interface that represents an affine transform drawable image object.
 * This interface is designed to hide the UI-dependent parts of various processes
 * such as getting the width and height of the image and drawing the image onto a
 * Graphics object by wrapping an Image object with Component that is used to control it.   
 * This enables the user of an IATImage object to be more decoupled from
 * the view than would be possible if Image objects were used directly.
 * @author swong
 *
 */
public interface IATImage extends IDimension {

	/**
	 * Draw the image onto the given Graphics object using the given AffineTransform.
	 * @param g   The Graphics object to draw on.  It is assumed that this is actually a Graphics2D object.
	 * @param at The net affine transform that will transform the image (upper left hand origin) to its proper location on the drawing canvas.
	 */
	void draw(Graphics g, AffineTransform at);

	/**
	 * Null object with zero width and height and a no-op draw that logs an error to the shared logger.
	 */
	public static IATImage NULL = new IATImage() {

		@Override
		public int getWidth() {
			return 0;
		}

		@Override
		public int getHeight() {
			return 0;
		}

		@Override
		public void draw(Graphics g, AffineTransform at) {
			ILoggerControl.getSharedLogger().log(LogLevel.ERROR, "Attempted to draw IATImage.NULL!");
		}
	};

	/**
	 * Factory object that will instantiate an IATImage given an Image and a Component (the ImageObserver).   
	 * The Component should be the canvas object upon which the balls are displayed.
	 * The factory's apply() method will block until the given image has fully loaded.   If the loading
	 * is interrupted, then an IATImage.ERROR object will be returned. 
	 * Usage: Use this factory to instantiate another factory ( Function&lt;Image, IATImage&gt; ) where the imageObs
	 * value is curried into the IATImage apply(Image image) method of the new factory. This will create a 
	 * usable factory where the imageObs Component object is no longer visible.  
	 */
	public static BiFunction<Image, Component, IATImage> FACTORY = (image, imageObs) -> {

		// First, make sure that the image is fully loaded.
		MediaTracker mt = new MediaTracker(imageObs);
		mt.addImage(image, 1);
		try {
			mt.waitForAll();
		} catch (Exception e) {
			ILoggerControl.getSharedLogger().log(LogLevel.ERROR,
					"[IATImage.FACTORY] Exception while waiting for image.  Exception = " + e);
			return IATImage.ERROR;
		}

		// Return an IATImage object that knows both the original image and its associated ImageObserver. 
		return new IATImage() {

			@Override
			public int getWidth() {
				return image.getWidth(imageObs);
			}

			@Override
			public int getHeight() {
				return image.getHeight(imageObs);
			}

			@Override
			public void draw(Graphics g, AffineTransform at) {
				((Graphics2D) g).drawImage(image, at, imageObs);
			}

		};
	};

	/**
	 * An error image that is just a black square that will transform in the same way as an image.
	 */
	public static final IATImage ERROR = new IATImage() {
		int size = 100;
		Color color = Color.BLACK;
		Shape shape = new Rectangle(0, 0, size, size);

		@Override
		public int getWidth() {
			return size;
		}

		@Override
		public int getHeight() {
			return size;
		}

		@Override
		public void draw(Graphics g, AffineTransform at) {
			g.setColor(color);
			((Graphics2D) g).fill(at.createTransformedShape(shape));
		}

	};
}
