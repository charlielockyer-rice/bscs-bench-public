package provided.pubsubsync;

import java.io.Serializable;
import java.time.Instant;



/**
 * Published data received from the pubsub sync server during a sync operation.
 * @author Stephen Wong (c) 2020
 *
 * @param <T> The type of data being published.
 */
public interface IPubSubSyncData<T extends Serializable> extends Serializable {
	
	/**
	 * Get the data corresponding to the supplied timestamp.
	 * @return The published data
	 */
	public T getData();
	
	/**
	 * The timestamp of when the supplied data was updated on the pubsub sync server.
	 * This value can be used to check whether or not the local data is properly synced.
	 * @return The timestamp of the supplied data.
	 */
	public Instant getTimeStamp();
}
