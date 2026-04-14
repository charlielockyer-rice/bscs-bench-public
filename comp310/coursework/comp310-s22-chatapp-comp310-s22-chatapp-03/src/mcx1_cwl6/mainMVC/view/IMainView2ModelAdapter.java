package mcx1_cwl6.mainMVC.view;

import java.util.List;

/**
 * Adapter from the view to the model
 * @param <TUserDropListItem> TUserDropListItem parameter
 * @param <TRoomDropListItem> TRoomDropListItem parameter
 */
public interface IMainView2ModelAdapter<TUserDropListItem, TRoomDropListItem> {

	/**
	 * Shutdown the RMI system and quit the app
	 */
	void quit();

	/**
	 * Send message to the client 
	 * @param text The message to send
	 */
	void send(String text);

	/**
	 * Connect user to remote user
	 * @param IP The IP address of the remote user
	 * @return Connected user
	 */
	public String connectUser(String IP);
	
	/**
	 * Gets the room that connected user is in
	 * @param stub The connected user
	 * @return The list of room of the connected user
	 */
	public List<TRoomDropListItem> getSelectedUserRooms(TUserDropListItem stub);
	
	/**
	 * Makes a new room
	 * @param name The name for new room
	 * @return The list of room of the connected user
	 */
	public TRoomDropListItem createRoom(String name);
	
	/**
	 * Leave a chat room you are in
	 * @param room The room you want to leave
	 */
	public void leaveRoom(TRoomDropListItem room);
	
	/**
	 * Invite a user to a room you are in
	 * @param connectedUser The user to invite
	 * @param yourRoom The room you are inviting them to
	 */
	public void inviteUser(TUserDropListItem connectedUser, TRoomDropListItem yourRoom);

}
