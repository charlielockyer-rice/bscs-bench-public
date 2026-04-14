package provided.rmiUtils.examples.hello_discovery.client.model;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import provided.rmiUtils.examples.hello_common.IHello;
import provided.logger.ILogEntry;
import provided.logger.ILogEntryFormatter;
import provided.logger.ILogEntryProcessor;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.RMIPortConfig;
import provided.rmiUtils.RMIUtils;

/**
 * The main model for the app
 * @author swong
 *
 */
public class HelloModelClient {
	/**
	 * The system logger in use
	 */
	private ILogger sysLogger;
	
	/**
	 * A logger that logs to the view and the system logger
	 */
	private ILogger viewLogger;
	
	/**
	 * The IRMIUtils in use
	 */
	private IRMIUtils rmiUtils;

	/**
	 * The adapter to the view
	 */
	private IModel2ViewAdapterClient m2vAdpt;

	/**
	 * The stub of the remote RMI Server object
	 */
	private IHello helloStub;

	/**
	 * The currently selected app config to use
	 */
	private RMIPortConfig currentConfig;

	/**
	 * Construct the model
	 * @param logger The system logger
	 * @param currentConfig The current app config
	 * @param m2vAdpt The adapter to the view
	 */
	public HelloModelClient(ILogger logger, RMIPortConfig currentConfig, IModel2ViewAdapterClient m2vAdpt) {
		this.sysLogger = logger;
		this.currentConfig = currentConfig;
		this.m2vAdpt = m2vAdpt;
		rmiUtils = new RMIUtils(logger);
		// Make a logger that logs to the view
		viewLogger = ILoggerControl.makeLogger(new ILogEntryProcessor(){
			ILogEntryFormatter formatter = ILogEntryFormatter.MakeFormatter("[%1s] %2s");   // custom log entry formatting  "[level] msg"
			@Override
			public void accept(ILogEntry logEntry) {
				HelloModelClient.this.m2vAdpt.displayMsg(formatter.apply(logEntry));  // plain "this" refers to the ILogEntryProcessor!
			}
			
		}, LogLevel.INFO);
		viewLogger.append(sysLogger);  // Chain the system logger to the end of the view logger so that anything logged to the view also goes to the system log (default = to console).
		
	}
	/**
	 * Get the internal IRMIUtils instance being used.    The discovery model start method needs the main model's IRMIUtils.
	 * ONLY call the method AFTER the model, i.e. the internal IRMIUtils, has been started!
	 * @return The internal IRMIUtils instance
	 */
	public IRMIUtils getRMIUtils() {
		return this.rmiUtils;
	}

	/**
	 * Manual connection to a Registry to retrieve and process a stub from it.
	 * Connects to the Registry at the given address, retrieves a stub from it, 
	 * then delegates to connectToStub() to process the stub.
	 * @param remoteRegistryIPAddr  The IP address of the remote Registry
	 * @return A status message string
	 */
	public String connectTo(String remoteRegistryIPAddr) {
		try {
			sysLogger.log(LogLevel.INFO, "Locating registry at " + remoteRegistryIPAddr+ "...");
			Registry registry = rmiUtils.getRemoteRegistry(remoteRegistryIPAddr);
			sysLogger.log(LogLevel.INFO, "Found registry: " + registry );
			IHello remoteStub = (IHello) registry.lookup("Hello");   // Replace "IRemoteStubType" and "BOUND_NAME" with the appropriate values for the application  
			sysLogger.log(LogLevel.INFO, "Found remote stub: " + remoteStub);

			connectToStub(remoteStub);

		} catch (Exception e) {
			sysLogger.log(LogLevel.ERROR, "Exception connecting to " + remoteRegistryIPAddr+ ": " + e);
			e.printStackTrace();

			return "No connection established!";
		}
		return "Connection to " + remoteRegistryIPAddr+ " established!";
	}

	/**
	 * Process the newly acquired stub.  This is the method that the discovery model uses in "Client" or "Client + Server" usage modes 
	 * @param helloStub The newly acquired stub 
	 */
	public void connectToStub(IHello  helloStub) {    // Replace "IRemoteStubType" with the appropriate for the application, i.e. the Remote type of stub in Registry)  
		this.helloStub = helloStub;
		sayHello();
	}	


	/**
	 * Start the model
	 */
	public void start() {
		rmiUtils.startRMI(currentConfig.classServerPort);  // Start the RMI system using an appropriate class file server port
		
		viewLogger.log(LogLevel.INFO, "Client ready, using app config: "+currentConfig);
	}

	/**
	 * Call the sayHello() method on the remote IHello RMI server object.
	 * Log the response to the view
	 */
	public void sayHello() {
		try {
			String response = helloStub.sayHello();
			String msg = "response: " + response;
			viewLogger.log(LogLevel.INFO, msg);  

		} catch (RemoteException e) {
			viewLogger.log(LogLevel.ERROR, "Exception while invoking remote method: "+e);
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Stop the RMI system and quit the app with the given exit code.
	 * @param exitCode Exit code: 0 = normal, -1 = error 
	 */
	public void quit(int exitCode) {
		rmiUtils.stopRMI();
		System.exit(exitCode);
	}




}
