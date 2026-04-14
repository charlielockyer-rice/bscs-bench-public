package common.receiver.messages;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * A message representing an error that occurred while processing another message
 * at the IReceiver level.
 * 
 * @author Design Group J
 *
 */
public interface IReceiverErrMsg extends IReceiverStatusMsg {
	
	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IReceiverErrMsg.class);
	}
	
	/**
	 * Get the data packet ID associated with an instance of this class.
	 * 
	 * @return The data packet ID.
	 */
	@Override
	public default IDataPacketID getID() {
		return IReceiverErrMsg.GetID();
	}
	
}
