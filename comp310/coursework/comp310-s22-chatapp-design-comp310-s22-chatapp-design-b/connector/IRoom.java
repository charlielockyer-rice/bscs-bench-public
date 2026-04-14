package common.connector;

import java.io.Serializable;
import java.util.UUID;

/**
 * A dyad that contains the friendly name of a room
 * and its UUID
 * 
 * @author GroupB
 */
public interface IRoom extends Serializable {

	/**
	 * @return The friendly name of the room
	 */
	public String getRoomName();
	
	/**
	 * @return The room's UUID
	 */
	public UUID getRoomID();
}
