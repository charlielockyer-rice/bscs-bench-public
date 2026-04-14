package common.receiver;

import common.adapter.ICmd2ModelAdapter;
import common.receiver.messages.IReceiverMsg;
import provided.datapacket.ADataPacketAlgoCmd;

/**
 * A type-narrowed extended visitor command, used to process a message sent to an IReceiver.
 * 
 * @author Design Group J
 *
 * @param <T> The receiver message type processed by the command.
 */
public abstract class AReceiverDataPacketAlgoCmd<T extends IReceiverMsg> extends ADataPacketAlgoCmd<Void, T, Void, ICmd2ModelAdapter, ReceiverDataPacket<T>> {

	/**
	 * UID for serialization purposes.
	 */
	private static final long serialVersionUID = 2869868189868592192L;

}
