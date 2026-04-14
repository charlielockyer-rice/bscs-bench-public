package common.receiver.messageType.exception;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * A communication exception message meant to indicate a failure to process some
 * received message.
 * 
 * @author Group B
 *
 */
public interface ICommunicationExceptionFailureMsg extends ICommunicationExceptionMsg {
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * The method that default getID() delegates to.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(ICommunicationExceptionFailureMsg.class);
	}
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	@Override
	public default IDataPacketID getID() {
		return ICommunicationExceptionFailureMsg.GetID();
	}

}
