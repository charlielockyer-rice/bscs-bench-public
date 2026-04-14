package mcx1_cwl6.miniMVC.model;

import java.util.HashSet;
import java.util.function.Supplier;

import javax.swing.JComponent;

import common.receiver.INamedMessageReceiver;



/**
 * The model to view adapter for the mini MVC
 */
public interface IMiniModel2ViewAdapter {
	/**
	 * Add text to view
	 * @param str Text to display
	 */
	public void displayText(String str);
	
	/**
	 * Add the GUI component
	 * @param label The label of the component
	 * @param fac The factory for the component
	 */
	public void displayComponent(String label, Supplier<JComponent> fac);
	
	/**
	 * Update users in a Chatroom
	 * @param userSet users in chatroom
	 */
	public void updateUserSet(HashSet<INamedMessageReceiver> userSet);
}
