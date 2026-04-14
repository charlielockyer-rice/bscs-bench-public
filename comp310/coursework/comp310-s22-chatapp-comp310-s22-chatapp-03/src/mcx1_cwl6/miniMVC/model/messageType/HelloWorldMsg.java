package mcx1_cwl6.miniMVC.model.messageType;


import common.receiver.messageType.ICommunicationMsg;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;


/**
 * Message type to send a JPanel that says hello world
 */
public class HelloWorldMsg implements ICommunicationMsg {

    /**
	 * For serialization
	 */
	private static final long serialVersionUID = -6226816285842975693L;

	/**
     * Get the data packet ID associated with this class.
     * 
     * @return The data packet ID.
     */
    public static IDataPacketID GetID() {
        return DataPacketIDFactory.Singleton.makeID(HelloWorldMsg.class);
    }

    @Override
    public IDataPacketID getID() {
        return HelloWorldMsg.GetID();
    }

}