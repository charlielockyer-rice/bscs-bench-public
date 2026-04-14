package common.connector;

import common.connector.messageType.IConnectionMsg;
import provided.datapacket.ADataPacketAlgoCmd;

/**
 * A type-narrowed ADataPacketAlgoCmd that has void return type, void parameter type, 
 * and ICmd2ModelAdapter as the adapter type. 
 * 
 * @author Group B
 *
 * @param <T> the message type that corresponds to the algo cmd
 */
public abstract class AConnectionDataPacketAlgoCmd <T extends IConnectionMsg> extends 
ADataPacketAlgoCmd<Void, T, Void, Void, ConnectionDataPacket<T>>{

	/**
	 * For serialization.
	 */
	private static final long serialVersionUID = -958364613323935065L;


}
