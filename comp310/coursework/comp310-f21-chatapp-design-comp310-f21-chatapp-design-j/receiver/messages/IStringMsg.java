package common.receiver.messages;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * A text message, offering a baseline for human-to-human communication, especially while developing a ChatApp.
 * 
 * @author Design Group J
 *
 */
public interface IStringMsg extends IReceiverMsg {
	
	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IStringMsg.class);
	}
	
	/**
	 * Get the data packet ID associated with an instance of this class.
	 * 
	 * @return The data packet ID.
	 */
	@Override
	public default IDataPacketID getID() {
		return IStringMsg.GetID();
	}
	
	/**
	 * Get the contents of the message as a String.
	 * 
	 * @return The contents of the message as a String.
	 */
	public String getString();
	
}
