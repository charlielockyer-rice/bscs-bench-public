package common.receiver.messages;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * A message indicating that an earlier message was rejected during processing
 * at the IReceiver level.
 * 
 * @author Design Group J
 *
 */
public interface IReceiverRejectMsg extends IReceiverStatusMsg {
	
	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IReceiverRejectMsg.class);
	}
	
	/**
	 * Get the data packet ID associated with an instance of this class.
	 * 
	 * @return The data packet ID.
	 */
	@Override
	public default IDataPacketID getID() {
		return IReceiverRejectMsg.GetID();
	}
	
}
