package mcx1_cwl6.miniMVC.model;

import mcx1_cwl6.ChatRoom;

/**
 * The mini MVC to main MVC adapter
 */
public interface IMini2MainAdapter {
	/**
	 * Remove the room from the main view
	 * @param room The room to remove
	 */
	public void removeRoomView(ChatRoom room);
	
	/**
	 * Add the room to user
	 * @param room The room to add
	 */
	public void addRoomUser(ChatRoom room);

	
	/**
	 * Remove the room from the list of connected rooms
	 * @param room The room to remove
	 */
	public void removeRoomList(ChatRoom room);
}
