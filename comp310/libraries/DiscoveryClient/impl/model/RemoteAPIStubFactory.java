package provided.discovery.impl.model;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import provided.discovery.IEndPointData;
import provided.logger.ILogger;
import provided.logger.LogLevel;
import provided.rmiUtils.IRMIUtils;

/**
 * Optional convenience factory for a specific API that can take an IEndPointData object 
 * and return the Registry-bound API-specific stub associated with the endpoint.  
 * This also supplies a method that can be used for a direct, non-endpoint-based connection
 * to another machine.
 * @author Stephen Wong (c) 2019
 *
 * @param <TRemoteStub>  The API-defined Remote type that is bound into the Registry
 */
public class RemoteAPIStubFactory<TRemoteStub extends Remote> {
	
	/**
	 * The IRMIUtils object the factory will use for RMI operations.
	 */
	private IRMIUtils rmiUtils;
	
	/**
	 * The logger in use
	 */
	private ILogger logger;

	/**
	 * Constructor for the class.   Do NOT construct this factory until 
	 * AFTER the IRMIUtils have been started! 
	 * @param rmiUtils  The IRMIUtils object the factory will use for RMI operations.  
	 * The IRMIUtils object must be ALREADY STARTED!!
	 * @param logger The logger to use
	 */
	public RemoteAPIStubFactory(IRMIUtils rmiUtils, ILogger logger) {
		this.rmiUtils = rmiUtils;
		this.logger = logger;
	}
	

	/**
	 * Get the bound stub in the Registry as defined by the given IEndPointData object.
	 * This is the method that IDiscoveryPanelAdapter's connectToEndPoint() method wants to call.
	 * Warning: Due to updating lags, there is always a possibility that the given endpoint is stale, 
	 * that is, that the associated stub is no longer bound in the Registry associated with the given endpoint.
	 * Be sure to properly catch and process any exceptions that this method may throw!
	 * This method merely checks if the endpoint is active and if so, delegates to the getFromAddr() method with the 
	 * information in the endpoint.
	 * @param endPt  The endpoint object with the retrieval information about the bound stub.
	 * @return The bound stub that was found.
	 * @throws RemoteException If there was an error communicating with remote Registry or
	 * an exception while communicating with the Registry.
	 * @throws NotBoundException  If the associated stub was not found in the Registry associated with endpoint or the endpoint is not active.
	 */
	public TRemoteStub get(IEndPointData endPt) throws RemoteException, NotBoundException  {
		if(endPt.isActive()) {  // Make sure the endpoint is still active before using it.
			return getFromAddr(endPt.getAddress(), endPt.getBoundName());  // delegate to the non-endpoint-based stub retrieval process.
			
		} else {
			logger.log(LogLevel.ERROR, "[RemoteAPIStubFactory.get("+endPt+")] Endpoint is not active!");
			throw new NotBoundException("Endpoint, "+endPt+", is not active!");
		}			
	}
	
	/**
	 * Connect to a remote Registry at the given IP address and retrieve the RMI stub bound to the given name. 
	 * This is the method that one would use when directly connecting to another machine.
	 * @param registryAddr  The IP address of the remote Registry
	 * @param boundName The name to which the desired RMI stub is bound
	 * @return An RMI stub instance retrieved from the remote Registry.
	 * @throws RemoteException If there was an error communicating with remote Registry or
	 * an exception while communicating with the Registry.
	 * @throws NotBoundException  If the associated stub was not found in the Registry associated with endpoint.
	 */
	public TRemoteStub getFromAddr(String registryAddr, String boundName) throws RemoteException, NotBoundException  {
		try {
			Registry remoteRegistry = rmiUtils.getRemoteRegistry(registryAddr);   
			@SuppressWarnings("unchecked")
			TRemoteStub stub =  (TRemoteStub) remoteRegistry.lookup(boundName);  // This line is not combined with the return so that the SuppressWarning can be applied to just this line and not the whole method.
			logger.log(LogLevel.INFO, "From Registry at "+registryAddr+", retrieved stub with bound name, '"+boundName+"': " +stub);
			return stub;
			
		} catch (RemoteException | NotBoundException e) {
			logger.log(LogLevel.ERROR, "[RemoteAPIStubFactory.getFromAddr("+registryAddr+", "+boundName+")] Exception while retrieving stub from remote Registry: "+e);
			e.printStackTrace();
			throw e;
		} 
	}

}
