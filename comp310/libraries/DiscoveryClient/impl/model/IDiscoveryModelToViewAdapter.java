package provided.discovery.impl.model;

import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

/**
 * Adapter used by some ADiscoveryModel implementations to connect to their "view".
 * @author swong
 *
 * @param <TRemoteStub> The type of stub to which the discovery server's endpoints are referencing
 */
public interface IDiscoveryModelToViewAdapter<TRemoteStub> {
	
	/**
	 * Add the given stub to the rest of the world (i.e the "view")
	 * Called when a discovery model has retrieved a stub using an endpoint pulled from the discovery server.
	 * @param stub The stub to add.
	 */
	public void addStub(TRemoteStub stub);
	
	
	/**
	 * Factory to create a null adapter.  A factory is needed because the adapter requires generic types,
	 * which the compiler will deduce from the variable to which the resultant adapter is assigned.
	 * @param <T> The TRemoteStub generic type of the IDiscoveryModelToViewAdapter variable to which this adapter will be assigned. 
	 * @return A no-op adapter
	 */
	public static <T> IDiscoveryModelToViewAdapter<T> makeNullAdapter() {
		return new IDiscoveryModelToViewAdapter<T>() {

			@Override
			public void addStub(T stub) {
				ILoggerControl.getSharedLogger().log(LogLevel.INFO, "NULL adapter, no-op.");
			}
		};
		
	}

}
