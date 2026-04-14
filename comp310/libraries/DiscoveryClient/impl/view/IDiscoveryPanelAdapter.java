package provided.discovery.impl.view;

import java.util.function.Consumer;

/**
 * Adapter for a DiscoveryPanel to communicate with the rest of the system.
 * This adapter can be used to connect the DiscoveryPanel to a larger view entity (which would then route requests to the model)
 * OR it can be used to connect the DiscoveryPanel directly to the model. 
 * One option is to install a DiscoveryPanel into the main view and have the view's constructor take both its own
 * adapter plus this adapter, which the view connects to DiscoveryPanel.
 * @author Stephen Wong (c) 2018
 *
 * @param <TEndPoint> The type of end point objects being displayed by the DiscoveryPanel
 */
public interface IDiscoveryPanelAdapter<TEndPoint> {

	/**
	 * Connect to a discovery server machine using the given category.
	 * The "Connect" button on the DiscoveryPanel will call this method.
	 * Typically, this method ends up connecting to a method in the model that will use IDiscoveryConnection 
	 * to create an IDiscoveryServer or IRegisteredDiscoveryServer.   The given endPtsUpdateFn is then given 
	 * to the discovery server's watch() method to periodically automatically update the endpoint list.
	 * @param category The category under which to connect 
	 * @param watchOnly If true, connection should be established without registering an endpoint.  If false, connection should be established by registering an endpoint. 
	 * @param endPtsUpdateFn A Consumer of an Iterable of TEndPoint objects that the rest of the system can use to update the DiscoveryPanel's displayed list of endpoints.  Typically used by the "watch" functionality of an IDiscoveryServer and its sub-types to automatically update the DiscoveryPanel's list. 
	 */
	void connectToDiscoveryServer(String category, boolean watchOnly, Consumer<Iterable<TEndPoint>> endPtsUpdateFn);

	/**
	 * Use the given TEndPoint object to retrieve a stub from a remote Registry.
	 * The "Get Selected EndPoint" button on teh DiscoveryPanel will call this method. 
	 * Typically, this method ends up connecting to a method in the model that will take the endpoint and use its information
	 * to retrieve a connection-level stub bound to a remote Registry to establish the initial connection
	 * to an application on that remote machine.   A RemoteAPIStubFactory can do this process transparently.
	 * @param selectedValue The desired TEndPoint object to use 
	 */
	void connectToEndPoint(TEndPoint selectedValue);

}
