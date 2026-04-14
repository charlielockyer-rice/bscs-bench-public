package common.receiver.messageType;

import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * The message type used for requesting a command from sender. Usually sent because 
 * an unknown type of message is received. 
 * 
 * @author Group B
 *
 */
public interface IRequestCmdMsg extends ICommunicationMsg{

	/**
	 * This method allows one to get the ID value directly from the interface.
	 * 
	 * The only difference between this code and any other data type's getID() code is the value of the 
	 * Class object being passed to the DataPacketIDFactory's makeID() method.    This has to be 
	 * specified here because this is the only place where the proper Class object is unequivocally known.
	 * 
	 *	@return The ID value associated with this data type.
	 */
	
	public static IDataPacketID GetID() {
		// DataPacketIDFactory.Singleton is an instance of an IDataPacketIDFactory
		return DataPacketIDFactory.Singleton.makeID(IRequestCmdMsg.class);   
    }

	/**
	 * This method MUST be defined at this INTERFACE level so that any concrete implementation 
	 * will automatically have the ability to generate its proper host ID value.
	 * Since an instance method can call a static method but not the other way around, simply delegate to 
	 * the static method from here. 
	 * 
	 * NEVER override this method, as it defines an invariant for the data type.   Unfortunately, Java does not allow 
	 * one to define an invariant instance method at the interface level, i.e. this method cannot be made final.
	 */
	@Override
	public default IDataPacketID getID() {
		return IRequestCmdMsg.GetID();
	}
	
	/**
	 * Get the ID of the data packet that contains the unknown message.
	 * 
	 * @return ID
	 */
	public IDataPacketID getUnknownMsgPacketID();
}
