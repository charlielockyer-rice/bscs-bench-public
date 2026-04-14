package common.connector;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The initial stub that a ChatApp retrieves from the registry. Needs to call 
 * getNamedConnection to get the serializable object that contains the connection stub. 
 * 
 * @author Group B
 *
 */
public interface IInitialConnection extends Remote {
	
	/**
	 * Getter for the named connection.
	 * @return a named connection that contains a connection stub.
	 * @throws RemoteException if something goes wrong with the remote connection
	 */
    public INamedConnection getNamedConnection() throws RemoteException;
    
}
