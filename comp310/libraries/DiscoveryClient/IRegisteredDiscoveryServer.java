package provided.discovery;

/**
 * Discovery information for the category associated with a particular local end point data, particularly
 * with the category of the local endpoint data.
 * This local discovery server object will regularly send a "heartbeat" to the remote discovery server machine  
 * to tell it that the local end point is still operational. 
 * An IRegisteredDiscoveryServer enables one to find all other end points registered on the discovery server in the 
 * same category as the associated local end point.
 * @author Stephen Wong (c) 2018
 *
 */
public interface IRegisteredDiscoveryServer extends IDiscoveryServer {
	
 
	
	/**
	 * Get the local end point associated with this IRegisteredDiscoveryServer
	 * @return an IEndPointData instance made from the registration information used to generate this IRegisteredDiscoveryServer.
	 */
	public IEndPointData getLocalEndPoint();
	
	/**
	 * In addition to disconnecting from the remote discovery server machine, 
	 * de-registers the associated end point (name/category/boundName) and
	 * terminates the associated heart beat.	 
	 * This method should be called when the system exists to properly de-register the end point.
	 */
	@Override
	public void disconnect(); 	
}
