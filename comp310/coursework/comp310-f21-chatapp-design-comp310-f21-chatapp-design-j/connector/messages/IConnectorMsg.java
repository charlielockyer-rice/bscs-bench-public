package common.connector.messages;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketData;
import provided.datapacket.IDataPacketID;

/**
 * A type-narrowed IDataPacketData, denoting a message that can be sent to an IConnector. 
 * 
 * @author Design Group J
 *
 */
public interface IConnectorMsg extends IDataPacketData {
	
	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IConnectorMsg.class);
	}
	
	/**
	 * Get the data packet ID associated with an instance of this class.
	 * 
	 * @return The data packet ID.
	 */
	@Override
	public default IDataPacketID getID() {
		return IConnectorMsg.GetID();
	}
}
