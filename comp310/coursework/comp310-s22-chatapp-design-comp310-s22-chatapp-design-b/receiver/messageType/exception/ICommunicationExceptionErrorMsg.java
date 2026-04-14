package common.receiver.messageType.exception;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * A communication exception message meant to indicate damage to a received message
 * and/or failure to de-serialize.
 * 
 * @author Group B
 *
 */
public interface ICommunicationExceptionErrorMsg extends ICommunicationExceptionMsg {
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * The method that default getID() delegates to.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(ICommunicationExceptionErrorMsg.class);
	}
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	@Override
	public default IDataPacketID getID() {
		return ICommunicationExceptionErrorMsg.GetID();
	}

}
