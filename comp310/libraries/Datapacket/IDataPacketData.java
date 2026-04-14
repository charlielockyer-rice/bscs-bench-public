package provided.datapacket;

import java.io.Serializable;

/**
 * Top-level datapacket data type.   DataPackets will only hold data of this type.  
 * This is a data type that knows its own host ID value and is Serializable.
 * 
 * Note that intermediate interface definitions below this level can be used to provide type-narrowing and
 * when used in specific API's, where the datapacket sender type is known, default methods can be defined
 * that enable a data type instance to generate its own datapacket.
 * 
 * @author Stephen Wong (c) 2018
 * * ----------------------------------------------
 * A data type that knows its own host ID value.
 */
public interface IDataPacketData extends Serializable {

	/**
	 *  public static IDataPacketID GetID() {...}
	 *  
	 *  For convenience, a corresponding static GetID() method should be defined on the specific implementing
	 *  data type interface (which must extend IDataPacketData).   GetID() should return the same value as getID().
	 *  The implementation of GetID() should call an IDataPacketFactory's makeID() method.  The default getID()
	 *  implementation should simply delegate to the static GetID() method (see the getID() docs).
	 *  
	 *  Unfortunately, Java has no syntax to define abstract static methods, so the GetID() method cannot 
	 *  be defined at this top level to force its implementation at the lower specific data type levels.  
	 *   
	 */

	/**
	 * Get the host ID value associated with the specific implementing data type.
	 * This method should be overridden with a default method by the INTERFACE 
	 * defining the data type. <br/><br/>
	 * 
	 * NEVER subsequently override the defined default method!  Unfortunately, 
	 * Java has no syntax for defining an invariant method at the interface level.    
	 * That default method represents the invariant ID value that is associated with 
	 * the data type defined by the interface it is defined on.  <br/><br/>
	 * 
	 * Since an instance method, such as a default method, can delegate to a static method but not the other
	 * way around, the implementation of getID() should simply delegate to the interface's static GetID() method.
	 * 
	 * @return A host ID value compatible with ADataPackets
	 */
	public IDataPacketID getID();
	

}
