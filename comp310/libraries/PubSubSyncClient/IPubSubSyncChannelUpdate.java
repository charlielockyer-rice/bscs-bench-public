package provided.pubsubsync;

import java.io.Serializable;
import java.util.UUID;

import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

/**
 * Represents a subscribed channel where the user can update the published data and thus sync all subscribers to the channel. 
 * @author Stephen Wong (c) 2020
 *
 * @param <T>  The type of data that is published.
 */
public interface IPubSubSyncChannelUpdate<T extends Serializable> extends IPubSubSyncChannelWatchOnly  {

	 /**
	 * Update the published data using the given updater function.   
	 * Note that any new data elements would be encapsulated inside of the updater function.
	 * This will cause all subscribers to be synced with the new data. 
	 * @param updateFn The updater function to use to update the published data.
	 */
	public void update(IPubSubSyncUpdater<T> updateFn);
	 
	/**
	 * Unsubscribe from the channel.  This will disable both sync operations from the pub-sub server and update capability.
	 * The channel will be destroyed if this was the last subscriber.
	 */
	@Override
	public void unsubscribe();	 
	 /**
	 * Null object instance of the class.
	 */
	public static final IPubSubSyncChannelUpdate<Serializable> NULL = new IPubSubSyncChannelUpdate<Serializable>() {
		/**
		 * logger to use
		 */
		private ILogger logger = ILoggerControl.getSharedLogger();

		@Override
		public String getFriendlyName() {
			return "IPubSubSyncChannelUpdate.NULL";
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
		public void update(IPubSubSyncUpdater<Serializable> updateFn) {
			logger.log(LogLevel.ERROR, "NOT IMPLEMENTED!");
		}

		@Override
		public void unsubscribe() {
			logger.log(LogLevel.ERROR, "NOT IMPLEMENTED!");
		}
		 
	 };
}
