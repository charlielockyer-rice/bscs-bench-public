package mcx1_cwl6.miniMVC.model.messageType;

import common.receiver.MessageDataPacket;
import common.receiver.messageType.exception.ICommunicationExceptionRejectionMsg;

/**
 * Implementation of ICommunicationExceptionRejectionMsg
 * @author meganxiao
 */
public class CommunicationExceptionRejectionMsg implements ICommunicationExceptionRejectionMsg{
	
	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -965201691855026272L;
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
	public CommunicationExceptionRejectionMsg(MessageDataPacket<?> packet, String errorMsg) {
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
