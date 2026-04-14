package common.connector.messageType;

import java.util.Set;

import common.connector.IRoom;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * An invite message type that enables one ChatApp to invite another ChatApp to 
 * one of its chat rooms.
 * 
 * @author Group B.
 *
 */
public interface ISendRoomsMsg extends IConnectionMsg {

	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * The method that default getID() delegates to.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(ISendRoomsMsg.class);
	}
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	@Override
	public default IDataPacketID getID() {
		return ISendRoomsMsg.GetID();
	}
	
	/**
	 * Get a set of room Name-ID dyads if the invite
	 * contains multiple rooms.
	 * @return A set room Name-ID dyads
	 */
	public Set<IRoom> getRooms();
}
