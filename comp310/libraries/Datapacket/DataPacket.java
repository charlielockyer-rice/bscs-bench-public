package provided.datapacket;

/**
 * Concrete data packet that holds a generic type of data.
 * Adds internal data content of type T and host ID type IDataPacketID;
 * @author Stephen Wong (c) 2018
 *
 * @param <T>  The type of the data being held.  T must be an INTERFACE extending IDataPacketData. 
 * @param <S>  The type of the sender object to which datapackets can be sent back to. * ----------------------------------------------
 * Adds the ability to hold IDataPacketData type data and have any type of sender.
 */
public class DataPacket<T extends IDataPacketData, S> extends ADataPacket {

	/**
	 * Version number for serialization
	 */
	private static final long serialVersionUID = -4384652128226661822L;

	/**
	 * The data being held
	 */
	private T data;

	/**
	 * The sender of this data packet
	 */
	private S sender;

	/**
	 * The constructor for a data packet. <br>
	 * Usage: <br>
	 * <pre>
	 * ADataPacket dp = new DataPacket&lt;IMyData&gt;(aMyData, aSender);
	 * </pre>
	 * @param data  The data to be held in the data packet.  The data's host ID value will be pulled from the data object.
	 * @param sender The sender of this data packet, an RMI stub typically.
	 */
	public DataPacket(T data, S sender) {
		super(data.getID());   // The data object knows its own host ID value
		this.data = data;
		this.sender = sender;
	}

	/**
	 * Accessor for the held data
	 * @return  The data being held
	 */
	public T getData() {
		return data;
	}

	/**
	 * Accessor for this data packet's sender
	 * @return the sender
	 */
	public S getSender() {
		return sender;
	}

}
