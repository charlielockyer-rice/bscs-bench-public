package common.connector;

import common.connector.messages.IConnectorMsg;
import provided.datapacket.DataPacketAlgo;

/**
 * An extended visitor used to process a message sent to an IConnector, provided for
 * type-narrowing purposes.
 * 
 * @author Design Group J
 *
 */
public class ConnectorDataPacketAlgo extends DataPacketAlgo<Void, Void> {

	/**
	 * UID for serialization purposes.
	 */
	private static final long serialVersionUID = 3889858704157848716L;

	/**
	 * Constructor for a ConnectorDataPacketAlgo. Note that the default command here should NOT request a command for an unknown message type.
	 * 
	 * @param defaultCmd The default command for the visitor.
	 */
	public ConnectorDataPacketAlgo(AConnectorDataPacketAlgoCmd<? extends IConnectorMsg> defaultCmd) {
		super(defaultCmd);
	}
    
}
