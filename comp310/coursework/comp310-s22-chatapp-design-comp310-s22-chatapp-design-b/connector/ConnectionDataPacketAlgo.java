package common.connector;

import common.connector.messageType.IConnectionMsg;
import provided.datapacket.DataPacketAlgo;

/**
 * Concrete visitor for processing a connection data packet.
 * Type-narrowed DataPacketAlgo with void parameter and return type.
 * 
 * @author Group B
 *
 */
public class ConnectionDataPacketAlgo extends DataPacketAlgo<Void, Void> {

	/**
	 * For serialization.
	 */
	private static final long serialVersionUID = 5196431879940137852L;

	/**
	 * Constructor for the ConnectionDataPacketAlgo.
	 * @param defaultCmd a cmd that can process a specific IConnectionMsg type message
	 */
	public ConnectionDataPacketAlgo(AConnectionDataPacketAlgoCmd<? extends IConnectionMsg> defaultCmd) {
		super(defaultCmd);
	}

}
