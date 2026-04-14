package provided.datapacket;

import provided.extvisitor.*;

/**
 * Abstract data packet that defines the use of a IDataPacketID object as the host ID type.  
 * The type of data held by the data packet defines its type and thus what case it
 * calls on its processing visitors.
 * Specifies use of IDataPacketID type for host ID
 * @author Stephen Wong (c) 2018 * ----------------------------------------------
 * Restricts the host ID type to be IDataPacketID
 */
public abstract class ADataPacket extends AExtVisitorHost<IDataPacketID, ADataPacket> {

	/**
	 * Version number for serialization
	 */
	private static final long serialVersionUID = 5990493490087888131L;


	/**
	 * Constructor for this abstract superclass
	 * @param id An IDataPacketID value to be used as the host ID value defining this type of data packet.
	 */
	public ADataPacket(IDataPacketID id) {
		super(id);
	}

}
