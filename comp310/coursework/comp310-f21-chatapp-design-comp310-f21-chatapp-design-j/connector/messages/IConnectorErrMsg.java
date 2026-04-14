package common.connector.messages;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * A message representing an error that occurred while processing another message
 * at the IConnector level.
 * 
 * @author Design Group J
 *
 */
public interface IConnectorErrMsg extends IConnectorStatusMsg {

	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IConnectorErrMsg.class);
	}
	
	/**
	 * Get the data packet ID associated with an instance of this class.
	 * 
	 * @return The data packet ID.
	 */
	@Override
	public default IDataPacketID getID() {
		return IConnectorErrMsg.GetID();
	}

}
