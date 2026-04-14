package common.connector;

import common.connector.messages.IConnectorMsg;
import provided.datapacket.DataPacket;

/**
 * A data packet used to contain a message sent to an IConnector.
 * 
 * @author Design Group J
 *
 * @param <T> The connector message type contained within the data packet.
 */
public class ConnectorDataPacket<T extends IConnectorMsg> extends DataPacket<T, INamedConnector> {
    
	/**
	 * UID for serialization purposes.
	 */
	private static final long serialVersionUID = -5580330781397921060L;

	/**
	 * Constructor for a ConnectorDataPacket.
	 * 
	 * @param data The connector message.
	 * @param sender The INamedConnector from which the message was sent.
	 */
	public ConnectorDataPacket(T data, INamedConnector sender) {
        super(data, sender);
    }
}
