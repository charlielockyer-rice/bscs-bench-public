package mcx1_cwl6.miniMVC.model.messageType;

import common.receiver.AMessageDataPacketAlgoCmd;
import common.receiver.messageType.ICommunicationMsg;
import common.receiver.messageType.ISendCmdMsg;
import provided.datapacket.IDataPacketID;

/**
 * SendCmdMsg class that is an implementation of ISendCmdMsg
 */
public class SendCmdMsg implements ISendCmdMsg {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -5893751551689760156L;

	/**
	 * Message data packet algo cmd
	 */
	private AMessageDataPacketAlgoCmd<? extends ICommunicationMsg> packet;
	
	/**
	 * Data packet ID
	 */
	private IDataPacketID id;
	
	/**
	 * Constructor
	 * @param packet Message data packet algo cmd
	 * @param id Data packet ID
	 */
	public SendCmdMsg(AMessageDataPacketAlgoCmd<? extends ICommunicationMsg> packet, IDataPacketID id) {
		this.packet = packet;
		this.id = id;
	}
	
	@Override
	public AMessageDataPacketAlgoCmd<? extends ICommunicationMsg> getCmd() {
		// TODO Auto-generated method stub
		return this.packet;
	}

	@Override
	public IDataPacketID getCmdID() {
		// TODO Auto-generated method stub
		return this.id;
	}

}
