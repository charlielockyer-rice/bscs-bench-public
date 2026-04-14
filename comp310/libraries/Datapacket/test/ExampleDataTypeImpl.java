package provided.datapacket.test;

/**
 * A concrete implementation of the data type IExampleDataType
 * The IExampleDataType interface defines the data type, not this implementing class!
 * @author swong
 *
 */
public class ExampleDataTypeImpl implements IExampleDataType {

	/**
	 * For Serializable
	 */
	private static final long serialVersionUID = -5435911218014275295L;
	
	/**
	 * Just some junk that this implementation is holding
	 */
	private Object junk;
	
	/**
	 * Constructor for the class
	 * @param junk some dummy junk to hold
	 */
	public ExampleDataTypeImpl(Object junk) {
		this.junk = junk;
	}
	
	/**
	 * This is just an example showing how this concrete implementation can access its own host ID value.
	 */
	@Override
	public String doStuff() {
		
		return "(ID = "+this.getID()+") junk = "+ junk;
	}

}
