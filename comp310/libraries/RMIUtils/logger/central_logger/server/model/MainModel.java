package provided.rmiUtils.logger.central_logger.server.model;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.RMIPortConfig;
import provided.rmiUtils.RMIUtils;
import provided.rmiUtils.logger.IRemoteLogService;
import provided.rmiUtils.logger.impl.RemoteLogEntryProcessor;
import provided.rmiUtils.logger.impl.RemoteLogService;

/**
 * 
 * The main server model
 * @author swong
 *
 */
public class MainModel {
	
	/**
	 * The model to view adapter
	 */
	private IModel2ViewAdapter m2vAdpt;
	/**
	 * The app config in use
	 */
	private RMIPortConfig currentConfig;
	
	/**
	 * The system logger in use
	 */
	private ILogger sysLogger = ILoggerControl.getSharedLogger();
	/**
	 * The logger visible on the view
	 */
	private ILogger viewLogger; 
	
	/**
	 * The remote log service's local instance
	 */
	private IRemoteLogService remoteLogServiceServer;
	/**
	 * The stub of the remote log service to be published
	 */
	private IRemoteLogService remoteLogServiceStub;
	
	/**
	 * The RMI utils in use
	 */
	private IRMIUtils rmiUtils = new RMIUtils(sysLogger);

	/**
	 * Construct the class with a given app config and adapter to the view
	 * @param currentConfig The current app config to use
	 * @param m2vAdpt The adapter to the view
	 */
	public MainModel(RMIPortConfig currentConfig, IModel2ViewAdapter m2vAdpt) {
		this.currentConfig = currentConfig;
		this.m2vAdpt = m2vAdpt;
		sysLogger.setLogLevel(LogLevel.DEBUG);		
	}
	
	/**
	 * Start the model
	 */
	public void start() {
		
		viewLogger = ILoggerControl.makeLogger(m2vAdpt.getLogEntryProcessor());
		viewLogger.append(sysLogger);
		
		rmiUtils.startRMI(currentConfig.classServerPort);
		
		
		
		this.remoteLogServiceServer = new RemoteLogService(viewLogger);
		try {
			m2vAdpt.setIP_Ports(rmiUtils.getLocalAddress(), currentConfig.stubPort, currentConfig.classServerPort);
			remoteLogServiceStub = (IRemoteLogService)UnicastRemoteObject.exportObject(remoteLogServiceServer, currentConfig.stubPort);
			Registry localRegistry = rmiUtils.getLocalRegistry();
			
			localRegistry.rebind(IRemoteLogService.DEFAULT_BOUND_NAME, remoteLogServiceStub);
			
			
		} catch (Exception e) {
			sysLogger.log(LogLevel.ERROR, "Exception while creating remote logging service: "+e);
			e.printStackTrace();
		}
		
		
		localTest();
	}
	
	/**
	 * Do a test of the locally published logger 
	 */
	public void localTest() {
		
		try {
			
			RemoteLogEntryProcessor remoteLogEntryProc = new RemoteLogEntryProcessor(rmiUtils, sysLogger, rmiUtils.getLocalAddress());
			ILogger remoteLogger = ILoggerControl.makeLogger(remoteLogEntryProc);
			
			remoteLogger.log(LogLevel.INFO, "Test message to locally created remote logging service.");
		
		} catch (SocketException | UnknownHostException e ) {
			sysLogger.log(LogLevel.ERROR, "Exception while obtaining remote logger: "+e);
			e.printStackTrace();
		}
		
	}

	/**
	 * Set the view logger's LogLevel
	 * @param logLevel The new log level for the view's logger
	 */
	public void setLogLevel(LogLevel logLevel) {
		viewLogger.setLogLevel(logLevel);
	}

}
