package common.receiver.messages;

import common.receiver.AReceiverDataPacketAlgoCmd;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * A message containing a command used to process a message of an unknown type.
 * 
 * @author Design Group J
 *
 */
public interface ICommandMsg extends IReceiverMsg {
	
	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(ICommandMsg.class);
	}
	
	/**
	 * Get the data packet ID associated with an instance of this class.
	 * 
	 * @return The data packet ID.
	 */
	@Override
	public default IDataPacketID getID() {
		return ICommandMsg.GetID();
	}
	
	/**
	 * Get the command contained within the message.
	 * 
	 * @return The command contained within the message.
	 */
	public AReceiverDataPacketAlgoCmd<?> getCmd();

    /**
     * Get the ID to which the command contained within the message corresponds.
     * 
     * @return The ID to which the command contained within the message corresponds.
     */
	public IDataPacketID getCmdID();
	
}
