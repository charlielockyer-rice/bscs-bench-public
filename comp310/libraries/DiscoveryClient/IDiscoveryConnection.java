package provided.discovery;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.IRMI_Defs;


/**
 * Interface that provides static methods that enable an IDiscoveryManager to 
 * pulled down from the discovery server machine. 
 * Note that RMI operations require the use of the *local* IRMIUtils instance.
 * @author Stephen Wong (c) 2018
 *
 */
public interface IDiscoveryConnection {
	
	/**
	 * The bound name of the IDiscoveryManager object
	 */
	public static final String DISCOVERY_REGISTRY_BOUND_NAME = "Discover";
	
	/**
	 * The default IP address of the discovery server machine.
	 */
	public static final String DISCOVERY_REGISTRY_IP_ADDR = "discovery1.ad.rice.edu";

	
	
	/**
	 * Get a discovery manager from the default IP address
	 * The supplied IRMIUtils instance MUST be ALREADY STARTED!
	 * @param rmiUtils   A *STARTED* IRMIUtils instance
	 * @return An IDiscoveryManager instance, already started.
	 * @throws RemoteException  In the event of communications errors with the discover server
	 */
	public static IDiscoveryManager getDiscoveryManager(IRMIUtils rmiUtils) throws RemoteException {
		return  getDiscoveryManagerAt(rmiUtils, DISCOVERY_REGISTRY_IP_ADDR);
	}
		
	/**
	 * Get a discovery manager from a specific IP address.  
	 * The supplied IRMIUtils instance MUST be ALREADY STARTED!
	 * @param rmiUtils A *STARTED* IRMIUtils instance
	 * @param ipAddr   The IP address of the discovery server's Registry
	 * @return An IDiscoveryManager instance, already started.
	 * @throws RemoteException In the event of communications errors with the discover server
	 */ 
	public static IDiscoveryManager getDiscoveryManagerAt(IRMIUtils rmiUtils, String ipAddr) throws RemoteException {
		
		try {
			Registry registry = rmiUtils.getRemoteRegistry(ipAddr);

			
			IDiscoveryManagerFactory discMgrFacStub = (IDiscoveryManagerFactory) registry.lookup(DISCOVERY_REGISTRY_BOUND_NAME);

			if( null == discMgrFacStub) {
				throw  new RemoteException("No IDiscoveryManagerFactory bound to \""+DISCOVERY_REGISTRY_BOUND_NAME+"\" at IP address = "+DISCOVERY_REGISTRY_IP_ADDR+":"+IRMI_Defs.REGISTRY_PORT);
			}
			else {
				IDiscoveryManager discMgr = discMgrFacStub.makeManager();
				discMgr.start(rmiUtils);
				
				return discMgr;
			}
		}
		catch (Exception e) {
			String errorMsg =  "[IDiscoveryConnection.getDiscoveryManager()]  (Bound name = \""+DISCOVERY_REGISTRY_BOUND_NAME+"\" at IP address = "+DISCOVERY_REGISTRY_IP_ADDR+":"+IRMI_Defs.REGISTRY_PORT+") "+e;
			ILoggerControl.getSharedLogger().log(LogLevel.ERROR, errorMsg);
			e.printStackTrace();
			throw new RemoteException(errorMsg);
		}
	}
}
