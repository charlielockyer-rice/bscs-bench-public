package provided.discovery.impl.model;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import provided.discovery.IDiscoveryManager;
import provided.discovery.IDiscoveryServer;
import provided.discovery.IEndPointData;
import provided.discovery.IRegisteredDiscoveryServer;


/**
 * A DiscoveryManager with extra management capabilities
 * @author swong
 *
 */
public interface IDiscoveryManagerAdmin extends IDiscoveryManager {
	/**
	 * Get a collection of all the categories on the discovery server
	 * @return A collection of category names
	 * @throws RemoteException upon communications error
	 */
	public Collection<String> getCategories() throws RemoteException;
	
	/**
	 * Specialized connection to the discovery server that enables visibility of all categories 
	 * @param addFn A function that enables each category to be added.
	 * @throws RemoteException upon communications error
	 */
	public void connectAsAllCategories(BiConsumer<String, Supplier<IDiscoveryServer>> addFn) throws RemoteException;
	
	/**
	 * Same as regular register() method but allows the input of a specific IP address.  This method is used to 
	 * manually register an end point for another machine for testing purposes.   Note that the machine that 
	 * calls this method is responsible for the heartbeat of the generated endpoint.
	 * @param friendlyName The friendly name to be associated with this end point.
	 * @param category The grouping category for this end point (case sensitive!).
	 * @param boundName The bound name associated with this end point.
	 * @param address The IP address of the end point
	 * @return An IRegisteredDiscoveryServer instance that is tied to the given end point, particularly its category.
	 * @throws RemoteException  In the event of a communications error with the remote discovery server machine.
	 */
	IRegisteredDiscoveryServer register(String friendlyName, String category, String boundName, String address) throws RemoteException;  // get a customized IDiscoverServer for this client.  Needs to generate a UUID for this client.

	
	/**
	 * Deregisters the given endpoint and set the endpoint as inactive for all uses of the end point.
	 * @param endPt   The end point to de-register
	 * @throws RemoteException If an exception occurs during deregistration.
	 */
	public void deregister(IEndPointData endPt) throws RemoteException;
}
