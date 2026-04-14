package provided.discovery.impl.model;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

import provided.discovery.*;

/**
 * FOR INTERNAL USE ONLY! USER CODE SHOULD NEVER ACCESS THIS INTERFACE DIRECTLY!!
 * Remote interface for communicating back to the Discovery Server.   
 * @author swong
 *
 */
public interface IRegistrationManager extends Remote {
	
	/**
	 * Check if the given endpoint is currently active
	 * @param endpoint The endpoint to check
	 * @return true if currently active, false otherwise.
	 * @throws RemoteException on network error
	 */
	boolean isActiveEndPoint(IEndPointData endpoint) throws RemoteException;

	/**
	 * Set the given endpoint as active
	 * @param endpoint The endpoint to be made active.
	 * @return true if successful.
	 * @throws RemoteException on network error
	 */
	boolean setActiveEndPoint(IEndPointData endpoint) throws RemoteException;

	/**
	 * Sets the given endpoint as inactive
	 * @param endPt The endpoint to deactivate
	 * @throws RemoteException on network error
	 */
	void setInactiveEndPoint(IEndPointData endPt) throws RemoteException;

	/**
	 * Get all the endpoints currently registered under the given category.
	 * @param category The category to query
	 * @return A collection of endpoints
	 * @throws RemoteException on network error
	 */
	Collection<IEndPointData> getEndPoints(String category) throws RemoteException;
	
	/**
	 * Get all registered endpoints for all categories
	 * @return A collection of endpoints
	 * @throws RemoteException on network error
	 */
	Collection<IEndPointData> getAllEndPoints() throws RemoteException;
	
	/**
	 * Get all the registered categories
	 * @return A collection of categories
	 * @throws RemoteException on network error
	 */
	Collection<String> getCategories() throws RemoteException;

}
