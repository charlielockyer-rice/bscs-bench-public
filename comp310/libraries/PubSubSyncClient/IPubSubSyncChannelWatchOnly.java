package provided.pubsubsync;

import java.util.UUID;

import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

/**
 * Represents a Watch Only listener connection on a pubsub channel.   The channel cannot be updated through this listener.
 * Data syncs from the pubsub sync server are received however.
 * @author Stephen Wong (c) 2020
 *
 */
public interface IPubSubSyncChannelWatchOnly {

	/**
	 * Get the friendly name of the channel
	 * @return The friendly name
	 */
	public String getFriendlyName();
	 
	 /**
	 * Get the channel's unique ID value
	 * @return The channel ID
	 */
	public UUID getChannelID();
	 
	 /**
	 * Request that the pubsub sync server immediately update just this app's sync function.
	 */
	public void syncNow();
	
	/**
	 * Unsubscribe from the channel.  This will disable any sync operations from the pub-sub server.
	 * The channel will be destroyed if this was the last subscriber.
	 */
	public void unsubscribe();
	
	 /**
	 * Null object instance of the class.
	 */
	public static final IPubSubSyncChannelWatchOnly NULL = new IPubSubSyncChannelWatchOnly() {
		/**
		 * logger to use
		 */
		private ILogger logger = ILoggerControl.getSharedLogger();
		
		@Override
		public String getFriendlyName() {

			return "IPubSubSyncChannelWatchOnly.NULL";
		}

		@Override
		public UUID getChannelID() {
			logger.log(LogLevel.ERROR, "NOT IMPLEMENTED! Returning null!");
			return null;
		}

		@Override
		public void syncNow() {
			logger.log(LogLevel.ERROR, "NOT IMPLEMENTED!");
		}

		@Override
		public void unsubscribe() {
			logger.log(LogLevel.ERROR, "NOT IMPLEMENTED!");
		}
		
	};
	 
}
