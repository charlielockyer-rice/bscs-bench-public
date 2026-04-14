package common.connector.messages;

import java.util.UUID;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * Invite a ChatApp instance to a chat room, represented by its UUID and friendly name.
 * 
 * @author Design Group J
 *
 */
public interface IInviteMsg extends IConnectorMsg {
	
	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IInviteMsg.class);
	}
	
	/**
	 * Get the data packet ID associated with an instance of this class.
	 * 
	 * @return The data packet ID.
	 */
	@Override
	public default IDataPacketID getID() {
		return IInviteMsg.GetID();
	}
	
	/**
	 * Get the UUID of the chat room.
	 * 
	 * @return The UUID of the chat room.
	 */
	public UUID getUUID();

    /**
     * Get the friendly name of the chat room.
     * 
     * @return The friendly name of the chat room.
     */
	public String getFriendlyName();
}
