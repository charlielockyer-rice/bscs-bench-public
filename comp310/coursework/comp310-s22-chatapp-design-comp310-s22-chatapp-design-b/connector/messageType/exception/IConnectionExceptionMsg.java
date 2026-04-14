package common.connector.messageType.exception;

import common.connector.ConnectionDataPacket;
import common.connector.messageType.IConnectionMsg;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * A connection exception message super-type. Not normally instantiated.
 * 
 * @author Group B
 *
 */
public interface IConnectionExceptionMsg extends IConnectionMsg {

	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * The method that default getID() delegates to.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IConnectionExceptionMsg.class);
	}
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	@Override
	public default IDataPacketID getID() {
		return IConnectionExceptionMsg.GetID();
	}
	
	/**
	 * Get the data packet that raises the error.
	 * 
	 * @return data packet.
	 */
	public ConnectionDataPacket<? extends IConnectionMsg> getErrorDataPacket();
	
	/**
	 * Get the error message.
	 * 
	 * @return error message.
	 */
	public String getErrorMsg();
}