package hw06.model;

import java.awt.Container;
import java.util.function.Supplier;

import javax.swing.JComponent;

/**
 * Interface that goes from the model to the view that enables the model to talk to the view 
 * @author Cole Rabson and Son Nguyen.
 */
public interface IViewCtrlAdapter {

	/**
	 * @return The container of the ball.
	 */
	public Container getCanvas();

	/**
	 * No-op "null" adapter
	 * See the web page on the Null Object Design Pattern at http://cnx.org/content/m17227/latest/
	 */
	public static final IViewCtrlAdapter NULL_OBJECT = new IViewCtrlAdapter() {
		@Override
		/**
		 * Gets the dimensions of the canvas of the view. 
		 */
		public Container getCanvas() {
			return null;
		}

		@Override
		public void addConfigComponent(String label, Supplier<JComponent> compFac) {
		}
	};

	/**
	 * Add the configuration for the configuration algorithm.
	 * @param label The string name of the configuration tab.
	 * @param compFac The actual configuration.
	 */
	public void addConfigComponent(String label, Supplier<JComponent> compFac);
}
