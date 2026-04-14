package provided.pubsubsync;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import provided.logger.ILogger;
import provided.rmiUtils.IRMIUtils;

/**
 * Top-level entity that can communicate with the pubsub sync server to create or subscribe to 
 * channels through which data can be published and synced to all subscribers.   
 * A IPubSybSyncManager instance can only be obtained via a static factory method of IPubSybSyncConnection.
 * The IPubSybSyncManager will also automatically maintain "heartbeats" to notify the pubsub
 * sync server that the listeners for the various subscribed channels are still alive.  
 * @author Stephen Wong (c) 2020
 *
 */
public interface IPubSubSyncManager extends Serializable {
	/**
	 * *** FOR INTERNAL USE ONLY!  DEVELOPER CODE SHOULD NEVER CALL THIS METHOD! ***<br/>
	 * Starts the PubSub Sync manager on the current machine.
	 * The PubSub Sync manager MUST be started before it can be used! 
	 * The IPubSubSyncConnection.getPubSubSyncManager() and .getPubSubSyncManagerAt() methods 
	 * will automatically start the IPubSubSyncManager instance before returning it,
	 * so in general, no specific actions are needed by the utilizing application to start
	 * its IPubSubSyncManager instances. 
	 * @param logger The logger to use
	 * @param rmiUtils  The local instance of IRMIUtils
	 * @param stubPort the RMI stub port being used by the application.
	 */
	public void start(ILogger logger, IRMIUtils rmiUtils, int stubPort);  
	
	
	
	/**
	 * Create a new pubsub channel on the pubsub sync server. The channel is automatically subscribed to and 
	 * this creator of the channel is assumed to be able to update the channel.
	 * @param <T> The type of the data being synced
	 * @param friendlyName A friendly name for the channel.   Channels are uniquely specified by a UUID, not their friendly names.
	 * @param initialData The initial value for the data being synced
	 * @param syncFn A function that is called when new content is published to the channel.
	 * @param quitHandlerFn A function that is run when the local system is notified by the pub-sub server that that the local system is being unsubscribed from the channel.  The input is a status text message from the pub-sub server.
	 * @return An channel that can be updated
	 */
	public <T extends Serializable> IPubSubSyncChannelUpdate<T> createChannel(String friendlyName, T initialData, Consumer<IPubSubSyncData<T>> syncFn, Consumer<String> quitHandlerFn);
	
	/**
	 * Subscribe to the channel with the given ID such that the data in the channel can be updated. 
	 * Will throw an IllegalArgumentException if there is no channel with that ID.
	 * 
	 * @param <T> The type of data being synced
	 * @param channelID  The ID of the desired channel
	 * @param syncFn A function that is called when new content is published to the channel.
	 * @param quitHandlerFn A function that is run when the local system is notified by the pub-sub server that that the local system is being unsubscribed from the channel.  The input is a status text message from the pub-sub server.
	 * @return An channel that can be updated
	 */
	public <T extends Serializable> IPubSubSyncChannelUpdate<T> subscribeToUpdateChannel(UUID channelID, Consumer<IPubSubSyncData<T>> syncFn, Consumer<String> quitHandlerFn); 
	
	
	/**
	 * Subscribe to the channel with the given ID such that the data in the channel can only be watched and not updated. 
	 * Will throw an IllegalArgumentException if there is no channel with that ID.
	 * 
	 * @param <T> The type of data being synced
	 * @param channelID  The ID of the desired channel
	 * @param syncFn A function that is called when new content is published to the channel.
	 * @param quitHandlerFn A function that is run when the local system is notified by the pub-sub server that that the local system is being unsubscribed from the channel.  The input is a status text message from the pub-sub server.
	 * @return An channel that cannot be updated
	 */
	<T extends Serializable> IPubSubSyncChannelWatchOnly subscribeToWatchOnlyChannel(UUID channelID, Consumer<IPubSubSyncData<T>> syncFn, Consumer<String> quitHandlerFn);




	/**
	 * Find any channels that have the given friendly name
	 * @param friendlyName The name to search for
	 * @return A possibly empty set of IDs for channels that share the given name 
	 */
	public Set<UUID> findChannels(String friendlyName);



	/**
	 * Close and destroy the given channel.   All subscribers will be notified that the channel is closing and will be automatically unsubscribed. 
	 * @param channelId  The ID of the channel to close.
	 */
	void closeChannel(UUID channelId);

}

