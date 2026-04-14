package provided.pubsubsync.impl;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import provided.logger.ILogger;
import provided.logger.LogLevel;
import provided.pubsubsync.IPubSubSyncData;

/**
 * *** FOR INTERNAL USE ONLY!! DEVELOPER CODE SHOULD NEVER USE THIS CLASS!! ***<br/>
 * Required local definition of a factory to locally instantiate IPubSubSyncListener RMI server objects and their stub.
 * RMI is not capable of remote dynamically loading the class file for a RMI stub made locally from a remotely loaded RMI server class.
 * Hence, the both the RMI server interface file and the code to instantiate it as an anonymous inner class and then instantiate the stub
 * are locally available classes and interfaces.
 * @author swong
 *
 */
public class PubSubSyncListenerFactory {

	/**
	 * Internal storage of the listeners that were made to keep them from being garbage-collected
	 */
	private Map<UUID, IPubSubSyncListener<? extends Serializable>> listenerMap = new HashMap<UUID, IPubSubSyncListener<? extends Serializable>>();
	/**
	 * Internal storage of the listener stubs that were made so they can be disposed of when their respective channel unsubscribes them.
	 */
	private Map<UUID, IPubSubSyncListener<? extends Serializable>> listenerStubMap = new HashMap<UUID, IPubSubSyncListener<? extends Serializable>>();
	/**
	 * The port to make the stub with.
	 */
	private int stubPort;
	
	/**
	 * *** FOR INTERNAL USE ONLY!! DEVELOPER CODE SHOULD NEVER USE THIS METHOD!! ***<br/>
	 * Constructor for the class.
	 * @param stubPort The port the local application is using to make stubs.
	 */
	public PubSubSyncListenerFactory(int stubPort) {
		this.stubPort = stubPort;
	}
	
	/**
	 * *** FOR INTERNAL USE ONLY!! DEVELOPER CODE SHOULD NEVER USE THIS METHOD!! ***<br/>
	 * Utility method to create an instance of channel listener.  Saves the new instance to keep it from getting garbage collected.
	 * Returns the STUB for the listener!
	 * @param <T> The type of data being synced
	 * @param logger The logger to use
	 * @param nameFn Supplier of the channel name, for lazy creation of the name.
	 * @param channelId The channel ID in use
	 * @param syncFn The Consumer to call when the data is pushed from the server.
	 * @param onQuit The Consumer to run when the listener is unsubscribed from the channel.  A status message is the supplied input parameter.
	 * @return A listener stub.
	 * @throws RemoteException when there is a problem creating the stub
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> IPubSubSyncListener<T> makeListenerStub(ILogger logger, Supplier<String> nameFn, UUID channelId, Consumer<IPubSubSyncData<T>> syncFn, Consumer<String> onQuit) throws RemoteException {
		IPubSubSyncListener<T> listener = new IPubSubSyncListener<T>() {

			@Override
			public String getName() throws RemoteException {
				return nameFn.get();
			}

			@Override
			public void sync(IPubSubSyncData<T> data) throws RemoteException {
				(new Thread(()->{
					logger.log(LogLevel.INFO, channelId+" @ "+data.getTimeStamp()+": "+data.getData());
					syncFn.accept(data);
				})).start();
			}

			@Override
			public UUID getChannelId() throws RemoteException {
				return channelId;
			}

			@Override
			public void quit(String statusMsg) {
				(new Thread(()->{
					listenerMap.remove(channelId);
					listenerStubMap.remove(channelId);
					logger.log(LogLevel.INFO, "["+nameFn.get()+": "+channelId+"] Quit order received from pubSubSync server. statusMsg = "+statusMsg);
					onQuit.accept(statusMsg);
				})).start();
			}
		};
		listenerMap.put(channelId, listener); // To keep it from being garbage collected.

		IPubSubSyncListener<T> listenerStub = (IPubSubSyncListener<T>) UnicastRemoteObject.exportObject(listener, stubPort);
		listenerStubMap.put(channelId, listenerStub); // For later reference
		
		return listenerStub;
	}
	
	
	
}
