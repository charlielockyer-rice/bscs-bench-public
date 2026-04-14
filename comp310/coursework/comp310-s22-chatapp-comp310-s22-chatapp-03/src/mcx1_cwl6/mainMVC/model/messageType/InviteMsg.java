package mcx1_cwl6.mainMVC.model.messageType;

import java.util.Set;
import common.connector.IRoom;
import common.connector.messageType.IInviteMsg;

/**
 * An invite message type that enables one ChatApp to invite another ChatApp to 
 * one of its chat rooms.
 */
public class InviteMsg implements IInviteMsg{

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 5577242785138439234L;

	/**
	 * The set of rooms in the invitemsg
	 */
	private Set<IRoom> rooms;
	
	/**
	 * Constructor
	 * @param rooms the set of rooms in the invitemsg
	 */
	public InviteMsg(Set<IRoom> rooms) {
		this.rooms = rooms;
	}
	


	@Override
	public Set<IRoom> getRooms() {
		// TODO Auto-generated method stub
		return rooms;
	}

	

}
