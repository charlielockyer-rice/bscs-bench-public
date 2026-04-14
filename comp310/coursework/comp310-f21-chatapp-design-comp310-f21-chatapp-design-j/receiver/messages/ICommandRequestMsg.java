package common.receiver.messages;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * Used to request a command from a sender when the receiver does not know how to process an initial message's type.
 * 
 * @author Design Group J
 *
 */
public interface ICommandRequestMsg extends IReceiverMsg {
	
	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(ICommandRequestMsg.class);
	}
	
	/**
	 * Get the data packet ID associated with an instance of this class.
	 * 
	 * @return The data packet ID.
	 */
	@Override
	public default IDataPacketID getID() {
		return ICommandRequestMsg.GetID();
	}
	
	/**
	 * Get the ID which the sender does not know how to process.
	 * 
	 * @return The ID which the sender does not know how to process.
	 */
	public IDataPacketID getCmdID();
	
}
