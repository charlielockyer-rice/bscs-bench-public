package common.connector.messageType;

import common.connector.IRoom;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * Message type to request to join a specific room.
 */
public interface IRequestJoinMsg extends IConnectionMsg {
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * The method that default getID() delegates to.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IRequestJoinMsg.class);
	}
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	@Override
	public default IDataPacketID getID() {
		return IRequestJoinMsg.GetID();
	}
	
	/**
	 * Get the room under request.
	 * @return a room Name-ID dyad representing the room under request.
	 */
	public IRoom getRoom();
}
