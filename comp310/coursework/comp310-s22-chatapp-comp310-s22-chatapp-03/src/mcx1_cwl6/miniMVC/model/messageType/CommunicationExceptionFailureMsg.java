package mcx1_cwl6.miniMVC.model.messageType;

import common.receiver.MessageDataPacket;
import common.receiver.messageType.exception.ICommunicationExceptionFailureMsg;

/**
 * Implementation of ICommunicationExceptionFailureMsg
 */
public class CommunicationExceptionFailureMsg implements ICommunicationExceptionFailureMsg{

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 3250474689122607276L;
	/**
	 * Message data packet
	 */
	private MessageDataPacket<?> packet;
	/**
	 * Error message
	 */
	private String errorMsg;
	
	/**
	 * Constructor 
	 * @param packet Message data packet
	 * @param errorMsg Error message
	 */
	public CommunicationExceptionFailureMsg(MessageDataPacket<?> packet, String errorMsg) {
		this.packet = packet;
		this.errorMsg = errorMsg;
	}
	
	@Override
	public MessageDataPacket<?> getErrorDataPacket() {
		// TODO Auto-generated method stub
		return packet;
	}

	@Override
	public String getErrorMsg() {
		// TODO Auto-generated method stub
		return errorMsg;
	}

}
