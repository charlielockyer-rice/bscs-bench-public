package common.receiver;

import common.receiver.messages.IReceiverMsg;
import provided.datapacket.DataPacket;

/**
 * A data packet used to contain a message sent to an IReceiver.
 * 
 * @author Design Group J
 *
 * @param <T> The receiver message type contained within the data packet.
 */
public class ReceiverDataPacket<T extends IReceiverMsg> extends DataPacket<T, INamedReceiver> {
    
	/**
	 * UID for serialization purposes.
	 */
	private static final long serialVersionUID = 4795070347013160715L;

	/**
	 * Constructor for a ReceiverDataPacket.
	 * 
	 * @param data The connector message.
	 * @param sender The INamedReceiver from which the message was sent.
	 */
	public ReceiverDataPacket(T data, INamedReceiver sender) {
        super(data, sender);
    }
	
}
