package provided.discovery;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote factory for creating IDiscoveryManagers
 * @author swong
 *
 */
public interface IDiscoveryManagerFactory extends Remote {
	
	/**
	 * Make an IDiscoveryManager.   The returned manager is NOT started as this 
	 * requires an started instance of the LOCAL IRMIUtils.
	 * @return An IDiscoveryManager instance
	 * @throws RemoteException on network error
	 */
	public IDiscoveryManager makeManager() throws RemoteException;
}
