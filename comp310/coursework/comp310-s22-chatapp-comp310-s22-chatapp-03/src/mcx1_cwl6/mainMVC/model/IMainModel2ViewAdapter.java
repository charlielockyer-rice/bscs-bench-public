package mcx1_cwl6.mainMVC.model;

import java.util.Set;

import common.connector.INamedConnection;
import mcx1_cwl6.ChatRoom;

/**
 * Adapter from the model to the view in the Main MVC
 *
 */
public interface IMainModel2ViewAdapter {
	/**
	 * Shows the status of the application
	 * @param str The text to display
	 */
	public void displayStatus(String str);

	/**
	 * Add set of INamedConnectors to the view.
	 * @param userStubSet the set of user stubs
	 */
	public void addPeers(Set<INamedConnection> userStubSet);

	
	/**
	 * Add chatroom.
	 * @param room The chatroom to add
	 */
	public void addChatRoom(ChatRoom room);
	
	/**
	 * Factory Method for making a miniMVC
	 * @param room Chat room
	 * @param user The user
	 * @return a Main2MiniAdapter 
	 */
	public IMain2MiniAdapter makeMiniMVC(ChatRoom room, INamedConnection user);
	
	/**
	 * Remove chatroom 
	 * @param room The chatroom to remove
	 */
	public void removeRoom(ChatRoom room);

	/**
	 * Remove all chatrooms
	 */
	public void removeAllRooms();
	
	/**
	 * Remove peer from view.
	 * @param userStub The user to remove
	 */
	public void removePeer(INamedConnection userStub);
}
