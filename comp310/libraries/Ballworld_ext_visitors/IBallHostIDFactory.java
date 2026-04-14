package provided.ballworld.extVisitors;

/**
 * Abstract factory for generating ball-compatible host ID values.
 * The purpose of the factory is to hide the implementation of the ID values,
 * since the ID will work as long as it follows Java's rules for object equality.
 * @author Stephen Wong (c) 2018
 * * ----------------------------------------------
 * Abstract factory to generate a unique IDataPacketID given a host data type's Class object 
 */
public interface IBallHostIDFactory {

	/**
	 * Generate a unique ID value for an IBallHost to use. Multiple calls to this factory method
	 * with the same friendly name will result in DIFFERENT ID's being generated!
	 * @param idName A friendly name used when printing this ID.  This name is NOT used in determining equality!
	 * @return A unique host ID value.
	 */
	public IBallHostID makeID(String idName);

}
