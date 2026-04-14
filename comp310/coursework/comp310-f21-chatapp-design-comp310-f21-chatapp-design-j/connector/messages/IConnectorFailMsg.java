package common.connector.messages;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * A message representing a failure that occurred while processing another message
 * at the IConnector level.
 * 
 * @author Design Group J
 *
 */
public interface IConnectorFailMsg extends IConnectorStatusMsg {
	
	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IConnectorFailMsg.class);
	}
	
	/**
	 * Get the data packet ID associated with an instance of this class.
	 * 
	 * @return The data packet ID.
	 */
	@Override
	public default IDataPacketID getID() {
		return IConnectorFailMsg.GetID();
	}
	
}
