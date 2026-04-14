package mcx1_cwl6.miniMVC.model.messageType;

import common.receiver.MessageDataPacket;
import common.receiver.messageType.exception.ICommunicationExceptionErrorMsg;

/**
 * Implementation of ICommunicationErrorMsg
 */
public class CommunicationExceptionErrorMsg implements ICommunicationExceptionErrorMsg{

	
	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -1771295366444281230L;
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
	public CommunicationExceptionErrorMsg(MessageDataPacket<?> packet, String errorMsg) {
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
