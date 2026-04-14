package common.connector.messages;

import common.connector.ConnectorDataPacket;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * A message representing a status that occurred while processing another message
 * at the IConnector level.
 * 
 * @author Design Group J
 *
 */
public interface IConnectorStatusMsg extends IConnectorMsg {
	
	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IConnectorStatusMsg.class);
	}
	
	/**
	 * Get the data packet ID associated with an instance of this class.
	 * 
	 * @return The data packet ID.
	 */
	@Override
	public default IDataPacketID getID() {
		return IConnectorStatusMsg.GetID();
	}
	
	/**
	 * Get the data packet that caused this status to occur.
	 * 
	 * @return The data packet.
	 */
	public ConnectorDataPacket<?> getPacket();
	
	/**
	 * Get the status message.
	 * 
	 * @return The status message.
	 */
	public String getStatus();

}
