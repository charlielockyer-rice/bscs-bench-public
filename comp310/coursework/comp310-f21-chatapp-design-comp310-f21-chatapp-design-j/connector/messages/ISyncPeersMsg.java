package common.connector.messages;

import java.util.Set;

import common.connector.INamedConnector;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * Synchronize the peers of two fully-connected networks of ChatApps.
 * 
 * Upon receipt of this message, a ChatApp must first iterate over the peers to which it is connected (including itself), sending each
 * an IAddPeersMsg containing the new peers contained in this ISyncPeers message, and then must iterate over the peers within this ISyncPeers
 * message, sending each an IAddPeersMsg containing the INamedConnectors to which this ChatApp is connected (again including itself).
 * 
 * This provides an elegant way to merge the fully connected ChatApp networks corresponding two instances connecting through
 * the discovery server.
 * 
 * Note that each resultant IAddPeersMsg sent to a remote node includes only nodes that the remote node has not seen before, and each
 * node in the network receives only one IAddPeersMsg as a result of an ISyncPeersMsg operation.
 * 
 * @author Design Group J
 *
 */
public interface ISyncPeersMsg extends IConnectorMsg {
	
	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(ISyncPeersMsg.class);
	}
	
	/**
	 * Get the data packet ID associated with an instance of this class.
	 * 
	 * @return The data packet ID.
	 */
	@Override
	public default IDataPacketID getID() {
		return ISyncPeersMsg.GetID();
	}
	
	/**
	 * Get the set of all the INamedConnectors to which the sender ChatApp is connected, including itself.
	 * 
	 * @return The set of all the INamedConnectors to which the sender ChatApp is connected, including itself.
	 */
	public Set<INamedConnector> getNewPeers();
	
}
