package hw06.model;

import java.util.function.Supplier;

import javax.swing.JComponent;

/**
 * @author phuso
 *
 */
public interface IBallAlgo2ModelAdapter {
	
	/**
	 * Adding the configuration panel to the view.
	 * @param label the name of the configuration
	 * @param compFac The actual configuration.
	 */
	public void addConfigComponent(String label, Supplier<JComponent> compFac);
	
	/**
	 * The null object of the adapter.
	 */
	public static final IBallAlgo2ModelAdapter NULL = new IBallAlgo2ModelAdapter() {

		@Override
		public void addConfigComponent(String label, Supplier<JComponent> compFac) {
			
		}
		
	};
}
