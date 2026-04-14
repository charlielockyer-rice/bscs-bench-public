package mcx1_cwl6.miniMVC.model.messageType;

import common.receiver.messageType.IRequestCmdMsg;
import provided.datapacket.IDataPacketID;

/**
 * Implementation of IRequestCmdMsg
 */
public class RequestCmdMsg implements IRequestCmdMsg{

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -6412876741745377082L;
	/**
	 * Data packet ID
	 */
	private IDataPacketID id;
	
	/**
	 * Constructor for RequestCmdMsg
	 * @param id Data packet ID
	 */
	public RequestCmdMsg(IDataPacketID id) {
		this.id = id;
	}
	
	
	@Override
	public IDataPacketID getUnknownMsgPacketID() {
		// TODO Auto-generated method stub
		return this.id;
	}

}
