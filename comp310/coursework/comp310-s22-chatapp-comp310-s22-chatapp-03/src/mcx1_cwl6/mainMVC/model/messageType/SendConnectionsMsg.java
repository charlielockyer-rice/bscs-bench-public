package mcx1_cwl6.mainMVC.model.messageType;

import java.util.Set;

import common.connector.INamedConnection;
import common.connector.messageType.ISendConnectionsMsg;

/**
 * One of the two message types used to achieve the auto-connect back feature.
 * 
 * A message type sent from a "host". This message contains a set of INamedConnections
 * that the host knows. The "host" is defined as the entity that receives IClientSendKnownConnectionsMsg.
 * 
 * When receiving this message, its command will update its set of peers.
 */
public class SendConnectionsMsg implements ISendConnectionsMsg{
	
	
	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 1901049280603831634L;
	/**
	 * The Set of INamedConnectos to send
	 */
	private Set<INamedConnection> peers;
	
	/**
	 * Constructor for HostSendKnownConnectionMessage
	 * @param peers The peers to send
	 */
	public SendConnectionsMsg(Set<INamedConnection> peers) {
		this.peers = peers;
	}
	
	@Override
	public Set<INamedConnection> getKnownConnections() {
		return this.peers;
	}
}
