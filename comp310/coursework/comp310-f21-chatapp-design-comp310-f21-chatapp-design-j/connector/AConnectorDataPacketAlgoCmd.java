package common.connector;

import common.connector.messages.IConnectorMsg;
import provided.datapacket.ADataPacketAlgoCmd;

/**
 * A type-narrowed extended visitor command, used to process a message sent to an IConnector.
 * 
 * @author Design Group J
 *
 * @param <T> The connector message type processed by the command.
 */
public abstract class AConnectorDataPacketAlgoCmd<T extends IConnectorMsg> extends ADataPacketAlgoCmd<Void, T, Void, Void, ConnectorDataPacket<T>> {

	/**
	 * UID for serialization purposes.
	 */
	private static final long serialVersionUID = 5118552638977046260L;
	
	/**
	 * No-op implementation of setCmd2ModelAdpt().
	 */
	@Override
	public void setCmd2ModelAdpt(Void cmd2ModelAdpt) {
		
	}

}
