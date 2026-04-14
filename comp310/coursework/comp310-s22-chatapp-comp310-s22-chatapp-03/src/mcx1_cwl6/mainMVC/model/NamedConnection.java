package mcx1_cwl6.mainMVC.model;

import common.connector.IConnection;
import common.connector.INamedConnection;

/**
 * Implementation of INamedConnection
 */
public class NamedConnection implements INamedConnection {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 5742410438297660730L;

	/**
	 * Name of NamedConnector
	 */
	private String name;
	
	/**
	 * Stub of NamedConnector
	 */
	private IConnection stub;
	
	/**
	 * Constructor for NamedConnector
	 * @param name The name of NamedConnector
	 * @param stub The stub of NamedConnector
	 */
	public NamedConnection(String name, IConnection stub) {
		this.name = name;
		this.stub = stub;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public IConnection getConnectionStub() {
		return this.stub;
	}
	
	
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof INamedConnection) {
			if (((INamedConnection)o).getConnectionStub().equals(this.stub)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.stub.hashCode();
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}
