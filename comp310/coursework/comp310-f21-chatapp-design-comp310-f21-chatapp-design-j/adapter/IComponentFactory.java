package common.adapter;

import java.awt.Component;

/**
 * A factory for a Component to be added to the chat room's view.
 * 
 * See the justification provided <a href="https://www.clear.rice.edu/comp310/JavaResources/GUI/dynamic_gui.html">here</a>.
 * 
 * @author Design Group J
 *
 */
public interface IComponentFactory {
	
	/**
	 * Make a component that will be dynamically added to the ChatApp GUI.
	 * 
	 * @return The component.
	 */
	public Component make();
	
}
