package provided.pubsubsync;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import provided.logger.ILogger;
import provided.logger.LogLevel;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.IRMI_Defs;


/**
 * Interface that provides static methods that enable an IPubSubSyncManager to 
 * pulled down from the pubsub sync server machine. 
 * Note that RMI operations require the use of the *local* IRMIUtils instance.
 * @author Stephen Wong (c) 2020
 *
 */
public interface IPubSubSyncConnection {
	
	/**
	 * The bound name of the IDiscoveryManager object
	 */
	public static final String PUBSUBSYNC_REGISTRY_BOUND_NAME = "PubSubSync";
	
	/**
	 * The default IP address of the discovery server machine.
	 */
	public static final String PUBSUBSYNC_REGISTRY_IP_ADDR = "discovery1.ad.rice.edu";

	
	
	/**
	 * Get a pubsub sync manager from the default IP address
	 * The supplied IRMIUtils instance MUST be ALREADY STARTED!
	 * @param logger The logger for the pubsub sync manager to use
	 * @param rmiUtils   A *STARTED* IRMIUtils instance
	 * @param stubPort the RMI stub port being used by the application.
	 * @return An IPubSubSyncManager instance, already started.
	 * @throws RemoteException  In the event of communications errors with the pubsub sync server
	 */
	public static IPubSubSyncManager getPubSubSyncManager(ILogger logger, IRMIUtils rmiUtils, int stubPort) throws RemoteException {
		return  getPubSubSyncManagerAt(logger, rmiUtils, stubPort, PUBSUBSYNC_REGISTRY_IP_ADDR);
	}
		
	/**
	 * Get a pubsub sync manager from a specific IP address.  
	 * The supplied IRMIUtils instance MUST be ALREADY STARTED!
	 * @param logger The logger for the pubsub sync manager to use
	 * @param rmiUtils A *STARTED* IRMIUtils instance
	 * @param stubPort the RMI stub port being used by the application.
	 * @param ipAddr   The IP address of the discovery server's Registry
	 * @return An IPubSubSyncManager instance, already started.
	 * @throws RemoteException In the event of communications errors with the pubsub sync server
	 */ 
	public static IPubSubSyncManager getPubSubSyncManagerAt(ILogger logger, IRMIUtils rmiUtils, int stubPort, String ipAddr) throws RemoteException {
		
		try {
			Registry registry = rmiUtils.getRemoteRegistry(ipAddr);

			IPubSubSyncManagerFactory discMgrFac = (IPubSubSyncManagerFactory) registry.lookup(PUBSUBSYNC_REGISTRY_BOUND_NAME);
			if( null == discMgrFac) {
				throw new RemoteException("No IPubSubSyncManagerFactory bound to \""+PUBSUBSYNC_REGISTRY_BOUND_NAME+"\" at IP address = "+ipAddr+":"+IRMI_Defs.REGISTRY_PORT);
			}
			else {
				IPubSubSyncManager discMgr = discMgrFac.make();
				discMgr.start(logger, rmiUtils, stubPort);
				
				return discMgr;
			}
		}
		catch (Exception e) {
			String errorMsg =  "(Bound name = \""+PUBSUBSYNC_REGISTRY_BOUND_NAME+"\" at IP address = "+ipAddr+":"+IRMI_Defs.REGISTRY_PORT+") Exception: "+e;
			logger.log(LogLevel.ERROR, errorMsg);
			e.printStackTrace();
			throw new RemoteException("[IPubSubSyncConnection.getPubSubSyncManagerAt()]  "+errorMsg);
		}
	}
}
