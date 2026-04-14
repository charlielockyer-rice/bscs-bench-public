package common.connector;

import common.connector.messageType.IConnectionMsg;
import provided.datapacket.DataPacket;

/**
 * A type narrowed DataPacket where the sender type is IConnection.
 * 
 * @author Group B
 *
 * @param <T> the message type contained in the data packet
 */
public class ConnectionDataPacket <T extends IConnectionMsg> extends DataPacket<T, INamedConnection>{

	/**
	 * For serialization.
	 */
	private static final long serialVersionUID = 6268173421128667143L;

	/**
	 * Constructor of ConnectionDataPacket.
	 * @param data the data held by this data packet 
	 * @param sender the sender of this data packet
	 */
	public ConnectionDataPacket(T data, INamedConnection sender) {
		super(data, sender);
	}

}
