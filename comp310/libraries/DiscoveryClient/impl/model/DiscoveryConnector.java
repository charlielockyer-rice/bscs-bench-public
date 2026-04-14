package provided.discovery.impl.model;

import java.rmi.RemoteException;
import java.util.function.Consumer;

import provided.discovery.IDiscoveryConnection;
import provided.discovery.IDiscoveryManager;
import provided.discovery.IDiscoveryServer;
import provided.discovery.IEndPointData;
import provided.logger.ILogger;
import provided.logger.LogLevel;
import provided.rmiUtils.IRMIUtils;



/**
 * Optional utility class to create a connection to the discovery server.  
 * Instances of this class must be inside of the main model because it requires an IRMIUtils instance 
 * that has already been started.
 * Note that multiple instances of an application running on the same machine should instantiate this 
 * class with different discoveryNames and different boundNames.
 * @author Stephen Wong (c) 2019
 *
 */
public class DiscoveryConnector {
	
	
	/**
	 * The discovery manager that connects with the remote discovery service
	 */
	private IDiscoveryManager discMgr;
	
	/**
	 * The name to be associated with the local endpoint in the remote discovery service.  Null if forceWatchOnly = true.
	 */
	private String discoveryName;
	
	/**
	 * The bound name of the RMI stub in the local Registry.  The local endpoint is referring to this stub.   Null if forceWatchOnly = true.
	 */
	private String boundName;
	
	/**
	 * The IDiscoveryServer that is watching the last category specified.  This instance gets replaced if connectToDiscoveryServer() is called again.
	 */
	private IDiscoveryServer discSrv;
	
	/**
	 * If true, forces the the DiscoveryConnector to only do watch only.
	 */
	private boolean forceWatchOnly = false;

	/**
	 * The logger in use
	 */
	private ILogger logger;
	
	/**
	 * Constructor for the class when no end point is to be published to the discovery server.
	 * THIS CLASS MUST BE INSTANTIATED *AFTER* RMIUtils HAS BEEN STARTED!
	 * @param rmiUtils The IRMIUtils object, already started.
	 * @param logger The logger to use
	 * @throws RemoteException When there is a problem connecting to the remote discovery service
	 */
	public DiscoveryConnector(IRMIUtils rmiUtils, ILogger logger) throws RemoteException {
		this.logger = logger;
		forceWatchOnly = true;
		init(rmiUtils);
	}
	
	/**
	 * Constructor for the class when an end point is to be published to the discover server.  
	 * THIS CLASS MUST BE INSTANTIATED *AFTER* RMIUtils HAS BEEN STARTED!
	 * @param rmiUtils The IRMIUtils object, already started.
	 * @param logger The logger to use
	 * @param discoveryName The name to be viewed as in the discovery server by others.
	 * @param boundName The name to which the RMI stub will be bound to in the local Registry 
	 * @throws RemoteException When there is a problem connecting to the remote discovery service
	 */
	public DiscoveryConnector(IRMIUtils rmiUtils, ILogger logger, String discoveryName, String boundName) throws RemoteException {
		this.logger = logger;
		this.discoveryName = discoveryName;
		this.boundName = boundName;
		forceWatchOnly = false;
		init(rmiUtils);
	}
	
	/**
	 * Initialize the discovery connector
	 * @param rmiUtils  The IRMIUtils to use
	 * @throws RemoteException if error when connecting to discovery server
	 */
	private void init(IRMIUtils rmiUtils) throws RemoteException {
		try {
			this.discMgr = IDiscoveryConnection.getDiscoveryManager(rmiUtils);
//			this.discMgr = IDiscoveryConnection.getDiscoveryManagerAt(rmiUtils, "localhost");
		} catch (RemoteException e) {
			logger.log(LogLevel.ERROR, "[DiscoveryConnector constructor] Exception while creating IDiscoveryManager instance: "+e);
			e.printStackTrace();
			throw e;
		}		
	}

	/**
	 * Connect to the remote discovery service using the given category, registering the local endpoint when watchOnly=False
	 * This is the method that IDiscoveryPanelAdapter's connectToDiscoveryServer() method wants to call. 
	 * @param category The category on the remote discovery service to monitor
	 * @param watchOnly If True, only connect to the remote discovery service and watch it, otherwise also register the local endpoint when connecting.
	 * @param updateFn  A Consumer of an iterable of endpoints used, for instance, by the DiscoveryPanel to update the list of active endpoints in the category.
	 * @throws RemoteException When a problem occurs connect to or registering with the remote discovery service
	 */
	public void connectToDiscoveryServer(String category, boolean watchOnly, Consumer<Iterable<IEndPointData>> updateFn) throws RemoteException {
		try {
			if (null != this.discSrv) {
				logger.log(LogLevel.CRITICAL, "[DiscoveryConnector.connectToDiscoveryServer(\"+category+\")]  Disconnecting existing IDiscoveryServer instance already connected to category \""+this.discSrv.getCategory()+"\".");
				this.discSrv.disconnect();
			}
			if( watchOnly || forceWatchOnly) {
				if(!watchOnly) {
					logger.log(LogLevel.CRITICAL, "DiscoveryConnector is configured for 'Watch Only', so the watchOnly parameter is being ignored.");
				}
				this.discSrv = discMgr.connectAs(category);
			}
			else {
				this.discSrv = discMgr.register(this.discoveryName, category, this.boundName);
			}
			this.discSrv.watch(updateFn);
			
		} catch (RemoteException e) {
			logger.log(LogLevel.ERROR, "[DiscoveryConnector.connectToDiscoveryServer("+category+")] Exception: "+e);
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Disconnects the current internal IDiscoveryServer object, if it exists.
	 */
	public void disconnect() {
		if (null==this.discSrv) {
			logger.log(LogLevel.CRITICAL, "[DiscoveryConnector.disconnect()] ERROR: Attempting disconnect from remote discovery service but no IDiscoveryServer instance has been created yet.");
		}
		else {
			this.discSrv.disconnect();
		}
	}
}
