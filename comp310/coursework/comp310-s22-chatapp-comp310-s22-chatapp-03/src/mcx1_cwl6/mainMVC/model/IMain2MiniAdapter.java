package mcx1_cwl6.mainMVC.model;

import java.util.function.Supplier;

import javax.swing.JComponent;

import mcx1_cwl6.ChatRoom;
import provided.pubsubsync.IPubSubSyncManager;

/**
 * Main 2 Mini MVC adapter
 */
public interface IMain2MiniAdapter {
	/**
	 * Get the chatroom
	 * @return A chatroom
	 */
	public ChatRoom getChatRoom();

	/**
	 * Create a new channel in mini MVC.
	 * @param manager PubSubSync manager
	 * @param room info for room
	 */
	public void createChannel(IPubSubSyncManager manager, ChatRoom room);

	/**
	 * Mini-MVC join existing channel.
	 * @param manager PubSubSync manager
	 */
	public void joinChannel(IPubSubSyncManager manager);
	
	/**,
	 * Add text to the chatroom 
	 * @param str The test to display
	 */
	public void appendString(String str);
	
	/**
	 * Adds a GUI component to the chatroom
	 * @param label The JComponent label
	 * @param compFac The JComponent supplier
	 */
	public void addComponent(String label, Supplier<JComponent> compFac);
	
	/**
	 * @return the friendlyName 
	 */
	public String getFriendlyName();
	
	
	/**
	 * Leave the chatroom
	 */
	public void leaveRoom();
}
