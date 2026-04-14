/**
 * 
 */
package provided.pubsubsync;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote factory for creating IPubSubSyncManagers 
 * @author swong
 *
 */
public interface IPubSubSyncManagerFactory extends Remote {

	/**
	 * Make an instance of an IPubSubSyncManager
	 * @return An IPubSubSyncManager instance
	 * @throws RemoteException on network error
	 */
	public IPubSubSyncManager make() throws RemoteException;
}
