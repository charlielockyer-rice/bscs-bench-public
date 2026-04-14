package provided.discovery;

import java.rmi.RemoteException;
import java.util.function.Consumer;

/**
 * Discovery information for a particular category.
 * An IDiscoveryServer enables one to find all end points registered on the remote discovery server machine  
 * in the same category.
 * @author Stephen Wong (c) 2018
 *
 */
public interface IDiscoveryServer {

	/**
	 * Get all the currently registered end points with the same category as associated 
	 * with this IDiscoveryServer. 
	 * @return  A collection of the available end points.  
	 * @throws RemoteException in the event of communications errors with the registration server.
	 */
	public Iterable<IEndPointData> getAvailableEndPoints() throws RemoteException; // 	

	/**
	 * Get the category associated with this discovery server
	 * @return A category name
	 */
	public String getCategory();
	
	/**
	 * Disconnects this local discovery server from the remote discovery server machine.
	 * Stops any watch threads.
	 * This method should be called when the system exits.
	 */
	public void disconnect(); 	
	
	
	/**
	 * Convenience method to automatically create a watch thread that will periodically get the 
	 * currently available registered end points,
	 * calling the given Consumer function with the current end points when it does so.
	 * Typically, the Consumer function would update the GUI with the currently available endpoints.
	 * The implementation of the supplied endPtDisplayFn is required to dispatch the call to the 
	 * GUI event thread if necessary!
	 * @param endPtDisplayFn  A Consumer function that will accept a Collection of IEndPointData
	 */
	public void watch(Consumer<Iterable<IEndPointData>> endPtDisplayFn);

}