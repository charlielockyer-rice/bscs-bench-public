package provided.datapacket;

/**
 * Abstract factory for generating datapacket-compatible host ID values.
 * The purpose of the factory is to hide the implementation of the ID values,
 * since the ID will work as long as it follows Java's rules for object equality.
 * @author Stephen Wong (c) 2018
 * * ----------------------------------------------
 * Abstract factory to generate a unique IDataPacketID given a host data type's Class object 
 */
public interface IDataPacketIDFactory {

	/**
	 * Generate datapacket host ID value given the Class object corresponding to the INTERFACE
	 * that defines a data type.  Since the interface defines the data type, the Class object 
	 * representing that interface has all the necessary information to generate the unique ID 
	 * for that data type. 
	 * @param dataInterface   The Class object for a data type's defining interface.
	 * @return The datapacket host ID value that corresponds to the given data type.
	 */
	public IDataPacketID makeID(Class<? extends IDataPacketData> dataInterface);

}
