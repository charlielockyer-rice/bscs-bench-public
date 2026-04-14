package provided.pubsubsync.impl;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

import provided.pubsubsync.IPubSubSyncData;

/**
 * *** FOR INTERNAL USE ONLY!! DEVELOPER CODE SHOULD NEVER USE THIS CLASS!! ***<br/>
 * Required local definition of local pub-sub sync listener RMI server interface that is associated with a particular channel. 
 * RMI is not capable of remote dynamically loading the class file for a RMI stub made locally from a remotely loaded RMI server class.
 * Hence, the both the RMI server interface file and the code to instantiate it as an anonymous inner class and then instantiate the stub
 * are locally available classes and interfaces.
 * @author swong
 *
 * @param <T> The type of the published data
 */
public interface IPubSubSyncListener<T extends Serializable> extends Remote {
	
	/**
	 * *** FOR INTERNAL USE ONLY!! DEVELOPER CODE SHOULD NEVER USE THIS METHOD!! ***<br/>
	 * Get the friendly name of the associated channel
	 * @return The channel's friendly name
	 * @throws RemoteException on network error.
	 */
	public String getName() throws RemoteException;

	/**
	 * *** FOR INTERNAL USE ONLY!! DEVELOPER CODE SHOULD NEVER USE THIS METHOD!! ***<br/>
	 * Get the ID of the associated channel
	 * @return The channel's ID value
	 * @throws RemoteException on network error.
	 */
	public UUID getChannelId() throws RemoteException;
	
	/**
	 * *** FOR INTERNAL USE ONLY!! DEVELOPER CODE SHOULD NEVER USE THIS METHOD!! ***<br/>
	 * Sync the local data with the given sync data.
	 * @param data  The data to sync
	 * @throws RemoteException on network error.
	 */
	public void sync(IPubSubSyncData<T> data) throws RemoteException;


	/**
	 * *** FOR INTERNAL USE ONLY!! DEVELOPER CODE SHOULD NEVER USE THIS METHOD!! ***<br/>
	 * Called when the pub-sub server has unsubscribed this listener from the channel.  
	 * This may be due to a request to unsubscribe or by an overall closing of the channel which unsubscribes all subscribers 
	 * @param statusMsg A message from the pub-sub server relating to why the local listener was unsubscribed.
	 * @throws RemoteException on network error.
	 */
	public void quit(String statusMsg) throws RemoteException;
	

	
}
