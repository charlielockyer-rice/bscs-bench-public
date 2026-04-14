package provided.rmiUtils.examples.hello_discovery.server.model;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import provided.rmiUtils.examples.hello_common.IHello;
import provided.logger.ILogEntry;
import provided.logger.ILogEntryFormatter;
import provided.logger.ILogEntryProcessor;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.RMIPortConfigWithBoundName;
import provided.rmiUtils.RMIUtils;

/**
 * The main model for the app
 * @author swong
 *
 */
public class HelloModelServer {
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
	private IModel2ViewAdapterServer m2vAdpt;

	/**
	 * The local Registry
	 */
	private Registry registry;
	
	/**
	 * The RMI Server implementation -- the actual object that will do the processing.
	 * Using an anonymous inner class to implement the RMI Server object
	 * to show how it can close over needed resources, e.g. the logger.
	 */
	private IHello helloServer = new IHello() {

		@Override
		public String sayHello() throws RemoteException {
			viewLogger.log(LogLevel.INFO, "helloServer.sayHello() method invoked.");
			return "Howdy from the IHello RMI Server object! Server using app config: "+currentConfig;
		}
		
	};
	
	/**
	 * The stub of the IHello RMI Server object
	 */
	private IHello helloStub;

	/**
	 * The currently selected app config.
	 */
	private RMIPortConfigWithBoundName currentConfig;	

	/**
	 * Construct the model
	 * @param logger The system logger
	 * @param currentConfig The currently selected app config
	 * @param m2vAdpt The adapter to the view
	 */
	public HelloModelServer(ILogger logger, RMIPortConfigWithBoundName currentConfig, IModel2ViewAdapterServer m2vAdpt) {
		this.sysLogger = logger;
		this.currentConfig = currentConfig;
		this.m2vAdpt = m2vAdpt;
		rmiUtils = new RMIUtils(logger);
		// Make a logger that logs to the view
		viewLogger = ILoggerControl.makeLogger(new ILogEntryProcessor(){
			ILogEntryFormatter formatter = ILogEntryFormatter.MakeFormatter("[%1s] %2s");   // custom log entry formatting  "[level] msg"
			@Override
			public void accept(ILogEntry logEntry) {
				HelloModelServer.this.m2vAdpt.displayMsg(formatter.apply(logEntry));  // plain "this" refers to the ILogEntryProcessor!
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
	 * Start the model
	 */
	public void start() {
		rmiUtils.startRMI(currentConfig.classServerPort);  // Start the RMI system using an appropriate class file server port

		run();
	}
	
	/**
	 * Run the Server, which will find the RMI Registry, creating it if
	 * necessary, and bind an instance of the Server to the "Hello" name in the
	 * Registry. The server then stays in an infinite loop waiting for someone
	 * to use the bound server.
	 */
	public void run() {
		try {
			registry = rmiUtils.getLocalRegistry();
			viewLogger.log(LogLevel.INFO, "Local Registry = "+registry);
		}
		catch(Exception e) {
			viewLogger.log(LogLevel.ERROR, "Exception while intializing RMI: " + e);
			e.printStackTrace();
			quit(-1); // exit the program.
		}

		try {

			// Create a UnicastRemoteObject stub from the RMI Server implementation to be sent to the clients.
			this.helloStub = (IHello) UnicastRemoteObject.exportObject(helloServer, currentConfig.stubPort);

			// Bind the remote object's stub in the registry at the specified
			// port use rebind instead of bind so the program can be run
			// multiple times with the same registry
			registry.rebind(currentConfig.boundName, helloStub);

			viewLogger.log(LogLevel.INFO, "Server ready, using app config: "+currentConfig);

		} catch (Exception e) {
			viewLogger.log(LogLevel.ERROR, "Server exception: " + e.toString());
			e.printStackTrace();
			quit(-1); // exit the program.
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
