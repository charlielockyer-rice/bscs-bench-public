package common.connector.messages;

import java.util.Set;

import common.connector.INamedConnector;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * Add a set of INamedConnectors to a ChatApp instance's set of peers.
 * 
 * @author Design Group J
 *
 */
public interface IAddPeersMsg extends IConnectorMsg {
	
	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IAddPeersMsg.class);
	}
	
	/**
	 * Get the data packet ID associated with an instance of this class.
	 * 
	 * @return The data packet ID.
	 */
	@Override
	public default IDataPacketID getID() {
		return IAddPeersMsg.GetID();
	}
	
	/**
	 * Get the peers to add to the receiver's set of peers.
	 * 
	 * @return The peers to add to the receiver's set of peers. 
	 */
	public Set<INamedConnector> getNewPeers();
	
}
