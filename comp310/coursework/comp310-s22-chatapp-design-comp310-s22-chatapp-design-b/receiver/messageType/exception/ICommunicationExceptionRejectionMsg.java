package common.receiver.messageType.exception;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * A communication exception message meant to indicate that the receiver has declined
 * to process a received message.
 * 
 * @author Group B
 *
 */
public interface ICommunicationExceptionRejectionMsg extends ICommunicationExceptionMsg {
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * The method that default getID() delegates to.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(ICommunicationExceptionRejectionMsg.class);
	}
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	@Override
	public default IDataPacketID getID() {
		return ICommunicationExceptionRejectionMsg.GetID();
	}

}
