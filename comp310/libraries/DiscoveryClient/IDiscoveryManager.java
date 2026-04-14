package provided.discovery;

import java.io.Serializable;
import java.rmi.RemoteException;

import provided.rmiUtils.IRMIUtils;

/**
 * Top-level entity that can obtain IDiscoveryServers to monitor the 
 * available registered end points (IEndPointData) of a given category 
 * on the remote discovery server, or 
 * to obtain IRegisteredDiscoveryServers in response to registering an end point
 * with the remote discovery server.  IRegisteredDiscoveryServers are IDiscoveryServers
 * that are associated with the registered end point and are responsible for maintaining 
 * the "heart beat" that tells the remote discovery server that the registered end point is 
 * still valid.
 * @author Stephen Wong (c) 2018
 *
 */
public interface IDiscoveryManager extends Serializable {
	/**
	 * Starts the discovery manager on the current machine.
	 * The discovery manager MUST be started before it can be used! 
	 * The IDiscoveryConnection.getDiscoveryManager() and .getDiscoveryManagerAt() methods 
	 * will automatically start the IDiscoveryManager instance before returning it,
	 * so in general, no specific actions are needed by the utilizing application to start
	 * its IDiscoveryManager instances. 
	 * @param rmiUtils  The local instance of IRMIUtils
	 */
	void start(IRMIUtils rmiUtils);  
	
	/**
	 * Registers the existence of an RMI stub bound a name in the local registry.  This information is 
	 * called an "end point".  An IRegisteredDiscoveryServer associated wtih the registered end point is returned.
	 * A friendly name is supplied for users of the discovery server to see instead of the bound name.
	 * A category name is supplied to isolate sets of end points from each other. 
	 * The Registry port on the local machine is assumed to be IRMI_Defs.REGISTRY_PORT.
	 * @param friendlyName The friendly name to be associated with this end point.
	 * @param category The grouping category for this end point (case sensitive!).
	 * @param boundName The bound name associated with this end point.
	 * @return An IRegisteredDiscoveryServer instance that is tied to the given end point, particularly its category.
	 * @throws RemoteException  In the event of a communications error with the remote discovery server machine.
	 */
	IRegisteredDiscoveryServer register(String friendlyName, String category, String boundName) throws RemoteException;  // get a customized IDiscoverServer for this client.  Needs to generate a UUID for this client.

	/**
	 * Gets an IDiscoveryServer associated with the given category name. 
	 * @param category A category name (case sensitive!)
	 * @return An IDiscoveryServer instance tied to the given category
	 * @throws RemoteException In the event of a communications error with the remote discovery server machine.
	 */
	IDiscoveryServer connectAs(String category) throws RemoteException;

}
