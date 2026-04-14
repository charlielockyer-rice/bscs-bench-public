package provided.rmiUtils.logger.central_logger.client.model;

import java.net.SocketException;
import java.net.UnknownHostException;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.RMIPortConfig;
import provided.rmiUtils.RMIUtils;
import provided.rmiUtils.logger.impl.RemoteLogEntryProcessor;

/**
 * The main model of the app
 * @author swong
 *
 */
public class MainModel {
	
	/**
	 * The model to view adapter
	 */
	private IModel2ViewAdapter m2vAdpt;
	/**
	 * The current app config
	 */
	private RMIPortConfig currentConfig;
	
	/**
	 * The system logger to use
	 */
	private ILogger sysLogger = ILoggerControl.getSharedLogger();
	/**
	 * The logger visible on the view
	 */
	private ILogger viewLogger;

	/**
	 * A logger connected to a remote log service
	 */
	private ILogger remoteLogger;
	
	/**
	 * The IRMIUtils to use
	 */
	private IRMIUtils rmiUtils = new RMIUtils(sysLogger);
	/**
	 * The local IP address being used
	 */
	private String localAddr;
	/**
	 * The bound name for the IRemoteLogService stub in a remote Registry
	 */
	private String boundName;

	/**
	 * Constructor for the class
	 * @param boundName The bound name for the IRemoteLogService stub in a remote Registry
	 * @param currentConfig The app config to use
	 * @param m2vAdpt The model to view adapter.
	 */
	public MainModel(String boundName, RMIPortConfig currentConfig, IModel2ViewAdapter m2vAdpt) {
		this.boundName = boundName;
		this.currentConfig = currentConfig;
		this.m2vAdpt = m2vAdpt;
		
		viewLogger = ILoggerControl.makeLogger(m2vAdpt.getLogEntryProcessor());
		viewLogger.append(sysLogger);
		
	}
	
	/**
	 * Start the model
	 */
	public void start() {

		try {
			rmiUtils.startRMI(currentConfig.classServerPort);
			localAddr = rmiUtils.getLocalAddress();
			m2vAdpt.setAddr(localAddr);
		} catch (SocketException | UnknownHostException e) {
			viewLogger.log(LogLevel.ERROR, "Exception while obtaining local address: "+e);
			e.printStackTrace();
		}

	}
	

	/**
	 * Connect to and retrieve the IRemoteLogService stub bound in the remote Registry at the given IP address.
	 * @param ipAddr  The IP address of the remote Registry.
	 */
	public void connectTo(String ipAddr) {

		remoteLogger = (new RemoteLogEntryProcessor(rmiUtils, sysLogger, ipAddr, boundName)).makeLogger();
		remoteLogger.setLogLevel(LogLevel.DEBUG);
		String msg = "Initial connection established.";
		logMsg(LogLevel.INFO, msg);
		
		m2vAdpt.enableSendMsg();
				
	}

	/**
	 * Create a log entry with the given log level and message and log it both remotely and locally.
	 * @param level The LogLevel to use
	 * @param msg The log message to use.
	 */
	public void logMsg(LogLevel level, String msg) {
		remoteLogger.log(level, msg);
		viewLogger.log(LogLevel.INFO, "Sent to remote logging service: ["+level+"] "+msg);
	}

}
