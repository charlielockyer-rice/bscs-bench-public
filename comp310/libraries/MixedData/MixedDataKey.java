package provided.mixedData;

import java.io.Serializable;
import java.util.UUID;
/**
 * A key for an IMixedDataDictionary
 * @author swong
 *
 * @param <T>   The type of value to which this key will refer.
 */
public class MixedDataKey<T> implements Serializable {
	
	/**
	 * For Serializable
	 */
	private static final long serialVersionUID = -954450371358260921L;
	
	/**
	 * Unique identifier for key
	 */
	private UUID id;
	
	/**
	 * Description of key
	 */
	private String desc;
	
	
	/**
	 * Type of associated value for this key
	 * The Class object itself cannot not be used because the same class loaded on different machines 
	 * may not calculate as equal due to machine-specific info injected by the machine-specific class loader. 
	 */
	private String type;

	/**
	 * Only the combination of id+desc+type must be unique to uniquely reference any value.
	 * @param id   Unique id common to a *set* of keys -- all keys belonging to an isolated sub-system using the IMixedDataDictionary would generally use the same id value.   This prevents name (description) clashes between sub-systems sharing a common dictionary.  
	 * @param desc A String description/name of the key. 
	 * @param type  A Class object representing the type of the value that this key references.
	 */
	public MixedDataKey(UUID id, String desc, Class<T> type) {
		this.id = id;
		this.desc = desc;
		this.type = type.getName(); // Use the fully-qualified class name as a unique identifier to avoid class loader problems across different machines.
	}

	
	/**
	 * Overridden equals() method.  Requires that the 
	 * @return true if id, desc, and type are ALL be equal.
	 */
	public boolean equals(Object other) {
		if(other instanceof MixedDataKey<?>) {
			MixedDataKey<?> o = (MixedDataKey<?>) other;
//			System.out.println("[MixedDataKey.equals()]"
//			+"\n  id: "+this.id+" vs. "+o.id+" ("+this.id.equals(o.id)+")"
//			+"\n  desc: "+this.desc+" vs. "+o.desc + " ("+this.desc.equals(o.desc)+")"
//			+"\n  type: "+this.type +" vs. "+o.type+ " ("+this.type.equals(o.type)+")");
//			return this.id.equals(o.id) &&  this.desc.equals(o.desc) && this.type.toString().equals(o.type.toString());
			return this.id.equals(o.id) &&  this.desc.equals(o.desc) && this.type.equals(o.type);

		}
		return false;
	}
	
	/**
	 * Overridden hashCode() method.   JAva requires that if two objects are equal, 
	 * their hashCodes must also be equal.
	 * @return The sum of the hashCodes of id+desc+type.
	 */
	public int hashCode() {
//		System.out.println("[MixedDataKey.hashCode()] hashCode = "+id.hashCode()+" + " + desc.hashCode() +" + "+ type.hashCode());
//		return id.hashCode() + desc.hashCode() + type.toString().hashCode();
		return id.hashCode() + desc.hashCode() + type.hashCode();

	}
	
	/**
	 * Accessor for the key's description 
	 * @return The key's description
	 */
	public String getDesc() {
		return this.desc;
	}
	
	/**
	 * Accessor for the key's ID value 
	 * @return The key's ID value
	 */	
	public UUID getID() {
		return this.id;
	}
	
	/**
	 * Accessor for the key's class typing
	 * @return The Class object representing the value type keyed by this key.
	 * @throws ClassNotFoundException If the stored class type information does not correspond to a known data type in the current system.
	 */
	public Class<?> getType() throws ClassNotFoundException {
		return Class.forName(this.type);
	}
}
