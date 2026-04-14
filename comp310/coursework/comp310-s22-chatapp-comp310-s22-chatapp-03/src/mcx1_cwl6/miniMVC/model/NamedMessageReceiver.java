package mcx1_cwl6.miniMVC.model;

import common.connector.INamedConnection;
import common.receiver.IMessageReceiver;
import common.receiver.INamedMessageReceiver;

/**
 * Implementation of INamedMessageReceiver
 */
public class NamedMessageReceiver implements INamedMessageReceiver{
	
	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 6835373043030886907L;

	/**
	 * Name of NamedReceiver
	 */
	private String name;
	
	/**
	 * The INamedConnection
	 */
	private INamedConnection namedConnection;
	
	/**
	 * The stub
	 */
	private IMessageReceiver stub;
	
	/**
	 * Constructor for this class.
	 * @param name of this receiver.
	 * @param namedConnection the named connection
	 * @param stub to be encapsulated.
	 */
	public NamedMessageReceiver(String name, INamedConnection namedConnection, IMessageReceiver stub) {
		this.name = name;
		this.namedConnection = namedConnection;
		this.stub = stub;
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public IMessageReceiver getMessageReceiverStub() {
		// TODO Auto-generated method stub
		return this.stub;
	}

	
	/**
	 * @return the named connection
	 */
	public INamedConnection getConnection() {
		// TODO Auto-generated method stub
		return this.namedConnection;
	}
	

}
