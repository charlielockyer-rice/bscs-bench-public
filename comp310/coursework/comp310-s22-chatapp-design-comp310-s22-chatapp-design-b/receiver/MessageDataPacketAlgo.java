package common.receiver;

import common.receiver.messageType.ICommunicationMsg;
import provided.datapacket.DataPacketAlgo;

/**
 * Concrete visitor for processing a message data packet.
 * Type-narrowed DataPacketAlgo with void parameter and return type.
 * 
 * @author Group B
 */
public class MessageDataPacketAlgo extends DataPacketAlgo<Void, Void> {

	/**
	 * For serialization.
	 */
	private static final long serialVersionUID = 7652065228605010382L;
	
	/**
	 * Constructor for the MessageDataPacketAlgo.
	 * @param defaultCmd a cmd that can process a specific ICommunicationMsg type message
	 */
	public MessageDataPacketAlgo(AMessageDataPacketAlgoCmd<? extends ICommunicationMsg> defaultCmd) {
		super(defaultCmd);
	}

}
