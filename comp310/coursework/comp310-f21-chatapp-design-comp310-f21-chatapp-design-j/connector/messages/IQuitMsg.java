package common.connector.messages;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * Remove the sender from the receiving ChatApp's set of peers. No methods are needed; the data type
 * (and sender contained within the data packet) is enough.
 * 
 * We assume here that a malicious ChatApp cannot forge the sender of a message.
 * 
 * @author Design Group J
 *
 */
public interface IQuitMsg extends IConnectorMsg {
	
	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IQuitMsg.class);
	}
	
	/**
	 * Get the data packet ID associated with an instance of this class.
	 * 
	 * @return The data packet ID.
	 */
	@Override
	public default IDataPacketID getID() {
		return IQuitMsg.GetID();
	}
}
