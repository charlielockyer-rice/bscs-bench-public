package common.receiver;

import common.adapter.ICmd2ModelAdapter;
import common.receiver.messageType.ICommunicationMsg;
import provided.datapacket.ADataPacketAlgoCmd;

/**
 * A type-narrowed ADataPacketAlgoCmd that has void return type, void parameter type, 
 * and ICmd2ModelAdapter as the adapter type. 
 * 
 * @author Group B
 *
 * @param <T> the message type that corresponds to the algo cmd
 */
public abstract class AMessageDataPacketAlgoCmd<T extends ICommunicationMsg> extends 
ADataPacketAlgoCmd<Void, T, Void, ICmd2ModelAdapter, MessageDataPacket<T>>{

	/**
	 * For serialization.
	 */
	private static final long serialVersionUID = 5901295187048286196L;
	
}
