package provided.rmiUtils.logger.impl;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import provided.logger.ILogEntry;
import provided.logger.ILogEntryProcessor;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.logger.IRemoteLogService;

import java.util.Objects;

/**
 * An ILogEntryProcessor implementation that utilizes a remote IRemoteLogService logging service.
 * This is a local encapsulation of an IRemoteLogService stub from a remote system.
 * An RMI CLIENT would use this class to connect to and utilize a remote logging service.
 * This class can be used anywhere an ILogEntryProcessor can be used, e.g. in any ILogger.
 * This log entry processor will always add the local IP address to the code location value 
 * of any given ILogEntry. 
 * @author swong
 *
 */
public class RemoteLogEntryProcessor implements ILogEntryProcessor {

	/**
	 * The local RMI Utils which must already be started.
	 */
	private IRMIUtils rmiUtils;
	
	/**
	 * A logger from the LOCAL system
	 */
	private ILogger localLogger;
	
	/**
	 * The RMI stub of the remote logging service.   Defaults to logging locally with an accompanying error message.
	 */
	private IRemoteLogService loggerStub = new IRemoteLogService() {

		@Override
		public void accept(ILogEntry logEntry) throws RemoteException {
			localLogger.log(LogLevel.ERROR, "No remote connection exists, local logging only!");
			localLogger.log(logEntry);
		}
		
	};
	
	/**
	 * The IP address of the local system.
	 */
	private String localAddr;
	
	/**
	 * Constructor for the class
	 * @param rmiUtils An ALREADY started rmiUtils instance.    The bound name of the desired stub defaults to 
	 * IRemoteLogService.DEFAULT_BOUND_NAME.
	 * @param localLogger A logger from the local system.
	 * @param remoteRegistryAddr The IP address of the Registry holding the RMI stub of the IRemoteLogService 
	 */
	public RemoteLogEntryProcessor(IRMIUtils rmiUtils, ILogger localLogger, String remoteRegistryAddr) {
		this(rmiUtils, localLogger, remoteRegistryAddr,IRemoteLogService.DEFAULT_BOUND_NAME);
	}
	
	/**
	 * Constructor for the class
	 * @param rmiUtils An ALREADY started rmiUtils instance.    The bound name of the desired stub defaults to 
	 * IRemoteLogService.DEFAULT_BOUND_NAME.
	 * @param localLogger A logger from the local system.
	 * @param remoteRegistryAddr The IP address of the Registry holding the RMI stub of the IRemoteLogService 
	 * @param boundName  The RMI Registry bound name for the IRemoteLogService stub.
	 */
	public RemoteLogEntryProcessor(IRMIUtils rmiUtils, ILogger localLogger, String remoteRegistryAddr, String boundName) {
		Objects.requireNonNull(rmiUtils, "[RemoteLogEntryProcessor constructor] rmiUtils must be non-null.");
		Objects.requireNonNull(localLogger, "[RemoteLogEntryProcessor constructor] The localLogger must be non-null.");	
		if(null == remoteRegistryAddr || "".equals(remoteRegistryAddr)) {
			throw new IllegalArgumentException("[RemoteLogEntryProcessor constructor] remoteRegistryAddr must be non-null and non-empty.");
		}
		if(null == boundName || "".equals(boundName)) {
			throw new IllegalArgumentException("[RemoteLogEntryProcessor constructor] boundName must be non-null and non-empty.");
		}
		if(!rmiUtils.isStarted()) {
			String errMsg = "[RemoteLogEntryProcessor constructor] The IRMIUtils instance must be started before instantiating this class!";
			localLogger.log(LogLevel.ERROR, errMsg);
			throw new IllegalArgumentException(errMsg);
		}

		
		this.rmiUtils = rmiUtils;
		this.localLogger = localLogger;		
		try {
			localAddr = rmiUtils.getLocalAddress();
			Registry remoteRegistry = this.rmiUtils.getRemoteRegistry(remoteRegistryAddr);
			loggerStub = (IRemoteLogService) remoteRegistry.lookup(boundName);
		} catch (RemoteException | NotBoundException | SocketException | UnknownHostException e) {
			localLogger.log(LogLevel.ERROR,"Local logging ONLY: Exception while obtaining remote logger: "+e );
			e.printStackTrace();
		}
		
	}
	
	
	
	@Override
	public void accept(ILogEntry logEntry) {
		try {
			loggerStub.accept(augmentLogEntry(logEntry));
		} catch (RemoteException e) {
			localLogger.log(LogLevel.ERROR,"Exception while communicating with remote logger: "+e );
			e.printStackTrace();
		}

	}
	
	/**
	 * Convenience method to construct an ILogger from this ILogEntryProcessor
	 * @return A new ILogger instance with this log entry processor as its only processor.
	 */
	public ILogger makeLogger() {
		return ILoggerControl.makeLogger(this);
	}
	
	/**
	 * Create a new ILogEntry from the given log entry by adding the local address to the log entry's code location value.
	 * @param logEntry The log entry to augment.
	 * @return A new ILogEntry instance
	 */
	private ILogEntry augmentLogEntry(ILogEntry logEntry) {
		return ILogEntry.make(logEntry.getLevel(), "("+localAddr+") "+logEntry.getLoc(), logEntry.getMsg());
	}

}
