package common.receiver;

import common.receiver.messageType.ICommunicationMsg;
import provided.datapacket.DataPacket;

/**
 * A type narrowed DataPacket where the sender type is IMessageReceiver.
 * 
 * @author Group B
 *
 * @param <T> the message type contained in the data packet
 */
public class MessageDataPacket<T extends ICommunicationMsg> extends DataPacket<T, INamedMessageReceiver> {
	
	/**
	 * For serialization.
	 */
	private static final long serialVersionUID = 6445279188528551528L;

	/**
	 * Constructor of ConnectionDataPacket.
	 * @param data the data held by this data packet 
	 * @param sender the sender of this data packet
	 */
	public MessageDataPacket(T data, INamedMessageReceiver sender) {
		super(data, sender);
	}

}
