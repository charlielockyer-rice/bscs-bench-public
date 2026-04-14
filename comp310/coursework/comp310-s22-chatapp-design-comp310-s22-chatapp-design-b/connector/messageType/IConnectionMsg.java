package common.connector.messageType;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketData;
import provided.datapacket.IDataPacketID;


/**
 * The super type of all other connection level messages. Defining IConnectionMsg
 * enhances type safety because one can specify IConnection to only receive messages
 * of the IConnectionMsg type. Usually not directly used.
 * 
 * @author Group B
 *
 */
public interface IConnectionMsg extends IDataPacketData {

	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * The method that default getID() delegates to.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IConnectionMsg.class);
	}
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	@Override
	public default IDataPacketID getID() {
		return IConnectionMsg.GetID();
	}
}
