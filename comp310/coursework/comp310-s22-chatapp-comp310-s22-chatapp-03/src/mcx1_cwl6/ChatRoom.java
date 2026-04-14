package mcx1_cwl6;

import java.util.HashSet;
import java.util.UUID;

import common.connector.IRoom;
import common.receiver.INamedMessageReceiver;

/**
 * ChatRoom class
 */
public class ChatRoom implements IRoom{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7762107828417353699L;

	/**
	 * Name of chat room
	 */
	private String name;
	
	/**
	 * UUID of chat room
	 */
	private UUID ID;
	
	/**
	 * List of members in the chat room
	 */
	private HashSet<INamedMessageReceiver> members = new HashSet<INamedMessageReceiver>();
	
	/**
	 * Constructor for the chat room
	 * @param name The name of the chat room
	 */
	public ChatRoom(String name) {
		this.name = name;
	}

	/**
	 * Set UUID of chat room
	 * @param id The UUID of the chatroom
	 */
	public void setUUID(UUID id) {
		this.ID = id;
	}
	
	/**
	 * Get member of the chat room
	 * @return The members of the chat room
	 */
	public HashSet<INamedMessageReceiver> getMembers() {
		return members;
	}

	/**
	 * Add INamedReceiver to ChatRoom.
	 * @param r The named message receiver
	 */
	public void addReceiver(INamedMessageReceiver r) {
		this.members.add(r);
	} 
	
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Set set of members.
	 * @param members set of members of this ChatRoom
	 */
	public void setMembers(HashSet<INamedMessageReceiver> members) {
		this.members = members;
		
	}

	@Override
	public String getRoomName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public UUID getRoomID() {
		// TODO Auto-generated method stub
		return this.ID;
	}
}
