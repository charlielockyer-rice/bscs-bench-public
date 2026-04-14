package common.receiver.messageType.exception;

import common.receiver.MessageDataPacket;
import common.receiver.messageType.ICommunicationMsg;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * A communication exception message super-type. Not normally instantiated.
 * 
 * @author Group B
 *
 */
public interface ICommunicationExceptionMsg extends ICommunicationMsg {

	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * The method that default getID() delegates to.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(ICommunicationExceptionMsg.class);
	}
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	@Override
	public default IDataPacketID getID() {
		return ICommunicationExceptionMsg.GetID();
	}
	
	/**
	 * Get the data packet that raises the error.
	 * 
	 * @return data packet.
	 */
	public MessageDataPacket<? extends ICommunicationMsg> getErrorDataPacket();
	
	/**
	 * Get the error message.
	 * 
	 * @return error message.
	 */
	public String getErrorMsg();
}
