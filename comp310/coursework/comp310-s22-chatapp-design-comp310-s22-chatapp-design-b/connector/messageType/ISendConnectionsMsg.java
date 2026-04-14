package common.connector.messageType;

import java.util.Set;

import common.connector.INamedConnection;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * The message type used to establish the auto-connect back feature.
 * 
 * A message type sent from a one peer to another, which contains a set of 
 * INamedConnections that the sender knows
 * 
 * When receiving this message, its command will add the additional set of peers 
 * received to its existing set of peers, and the same message type back to the sender
 * 
 * @author Group B
 *
 */
public interface ISendConnectionsMsg extends IConnectionMsg {

	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * The method that default getID() delegates to.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(ISendConnectionsMsg.class);
	}
	
	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	@Override
	public default IDataPacketID getID() {
		return ISendConnectionsMsg.GetID();
	}
	
	/**
	 * Get the set of peers contained in the message.
	 * 
	 * @return the set of peers contained in the message
	 */
	public Set<INamedConnection> getKnownConnections();
}