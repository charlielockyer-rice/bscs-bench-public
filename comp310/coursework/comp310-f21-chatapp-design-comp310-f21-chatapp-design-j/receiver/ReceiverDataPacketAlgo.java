package common.receiver;

import common.receiver.messages.IReceiverMsg;
import provided.datapacket.DataPacketAlgo;

/**
 * An extended visitor used to process a message sent to an IReceiver, provided for
 * type-narrowing purposes.
 * 
 * @author Design Group J
 *
 */
public class ReceiverDataPacketAlgo extends DataPacketAlgo<Void, Void> {

	/**
	 * UID for serialization purposes.
	 */
	private static final long serialVersionUID = -366218280184460963L;

	/**
	 * Constructor for a ReceiverDataPacketAlgo. Note that the default command here should initiate the unknown message
	 * type handling procedure.
	 * 
	 * @param defaultCmd The default command for the visitor.
	 */
	public ReceiverDataPacketAlgo(AReceiverDataPacketAlgoCmd<? extends IReceiverMsg> defaultCmd) {
		super(defaultCmd);
	}

}
