package mcx1_cwl6.mainMVC.model;

import java.rmi.RemoteException;

import common.connector.IInitialConnection;
import common.connector.INamedConnection;


/**
 * Implementation of IInitialConnection
 */
public class InitialConnection implements IInitialConnection{

	/**
	 * A named connection
	 */
	private INamedConnection namedConnection;
	
	
	/**
	 * Constructor for InitialConnection
	 * @param namedConnection The named connection
	 */
	public InitialConnection(INamedConnection namedConnection) {
		this.namedConnection = namedConnection;
	}
	
	@Override
	public INamedConnection getNamedConnection() throws RemoteException {
		// TODO Auto-generated method stub
		return this.namedConnection;
	}

}
