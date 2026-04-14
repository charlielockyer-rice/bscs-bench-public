package mcx1_cwl6.miniMVC.model.messageType;

import common.receiver.messageType.ITextMsg;

/**
 * TextMsg class that is an implementation of ITextMsg
 */
public class TextMsg implements ITextMsg{

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -7098411396462603600L;
	/**
	 * Message to send
	 */
	private String msg;
	
	/**
	 * Constructor for this StringMsg.
	 * @param msg The message to send
	 */
	public TextMsg(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return this.msg;
	}

}
