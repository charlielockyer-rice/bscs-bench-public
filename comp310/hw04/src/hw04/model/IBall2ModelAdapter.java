package hw04.model;

import java.awt.Container;

/**
 * An adapter from the ball (canvas specific) to the model
 * @author charlielockyer
 *
 */
public interface IBall2ModelAdapter {
	
	/**
	 * Returns the canvas to paint on
	 * @return the canvas
	 */
	public Container getCanvas();
	
	/**
	 * The null object version of the adapter
	 */
	public static final IBall2ModelAdapter NULL = new IBall2ModelAdapter() {
		public Container getCanvas() {
			return null;
		}
	};
}
