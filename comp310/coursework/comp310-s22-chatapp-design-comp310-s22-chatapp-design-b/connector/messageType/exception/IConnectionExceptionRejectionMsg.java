package common.connector.messageType.exception;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * A connection exception message meant to indicate that the receiver has declined
 * to process a received message.
 * 
 * @author Group B
 *
 */
public interface IConnectionExceptionRejectionMsg extends IConnectionExceptionMsg {
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * The method that default getID() delegates to.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IConnectionExceptionRejectionMsg.class);
	}
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	@Override
	public default IDataPacketID getID() {
		return IConnectionExceptionRejectionMsg.GetID();
	}

}
