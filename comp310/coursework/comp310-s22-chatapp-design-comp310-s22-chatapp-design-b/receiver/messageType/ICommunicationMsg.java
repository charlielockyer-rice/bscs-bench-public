package common.receiver.messageType;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketData;
import provided.datapacket.IDataPacketID;

/**
 * The super type of all messages within a chat room. Defining ICommunicationMsg
 * enhances type safety because one can make IMessageReceiver only receive messages
 * of the ICommunicationMsg type. Usually not directly used. 
 * 
 * @author Group B
 *
 */
public interface ICommunicationMsg extends IDataPacketData {

	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * The method that default getID() delegates to.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(ICommunicationMsg.class);
	}
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	@Override
	public default IDataPacketID getID() {
		return ICommunicationMsg.GetID();
	}

}
