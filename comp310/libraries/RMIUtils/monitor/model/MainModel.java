package provided.rmiUtils.monitor.model;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Timer;

import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.RMIUtils;
import provided.utils.struct.IDyad;
import provided.utils.struct.impl.Dyad;

/**
 * Main model for application
 * @author swong
 *
 */
public class MainModel {

	/**
	 * The adapter to the view
	 */
	private IModel2ViewAdapter viewAdpt;
	
	/**
	 * The current list of bound_name-bound_object dyads in the Registry 
	 */
	private List<IDyad<String, Object>> currentItems = new ArrayList<>();
	
	/**
	 * The reference to the RMI Registry
	 */
	private Registry registry;
	
	/**
	 * The logger in use
	 */
	ILogger logger = ILoggerControl.getSharedLogger();
	
	/**
	 * The list of boundNames from the last time the list changed.
	 * Used to reduce the number of logging events.
	 */
	List<String> oldBoundNames = null;

	/**
	 * The interval for updating the view of the Registry
	 */
	private int timerInterval = 1000; // millisec
	
	/**
	 * The timer for periodically updating the view of the Registry
	 */
	private Timer timer = new Timer(timerInterval, (evt)->{
		

		
		try {
			List<String> boundNames = Arrays.asList(registry.list()); // Get the current bound names in the Registry

			if(!boundNames.equals(oldBoundNames)) {  // Only log the bound names when the list changes.
				logger.log(LogLevel.INFO, "Registry contains bound names: "+boundNames);
				oldBoundNames = boundNames;
			}
			
			currentItems.clear();  // Clear out the current list of bound_name-bound_object dyads.
			
			// Get the associated bound objects from the Registry and add them to the current list of dyads.
			boundNames.forEach((boundName) -> {
				try {
					currentItems.add(new Dyad<>(boundName, registry.lookup(boundName)));
				} catch (RemoteException | NotBoundException e) {
					logger.log(LogLevel.ERROR, "Exception while retrieving bound object for bound name, "+boundName+": "+e);
					e.printStackTrace();
				}
			});
			viewAdpt.showItems(currentItems); // Display the list of current items on the view
		} catch (RemoteException e) {
			logger.log(LogLevel.ERROR, "Exception while retrieving bound names: "+e);
			e.printStackTrace();
		}
	});
	


	/**
	 * The RMIUtils in use
	 */
	private IRMIUtils rmiUtils = new RMIUtils(logger);
	

	
	/**
	 * The constructor for the class
	 * @param viewAdpt The adapter to the view
	 */
	public MainModel(IModel2ViewAdapter viewAdpt) {
		this.viewAdpt = viewAdpt;

	}

	/**
	 * Starts the model 
	 */
	public void start() {
		
		// Use truncated RMIUtils startup code since class file server and remote dynamic class loading not needed.
		this.configSecurityManager();
		this.configRMIProperties();
		
		// RMIUtils doesn't need to be started to get local Registry
		registry = rmiUtils.getLocalRegistry();
		logger.log(LogLevel.INFO, "Found local Registry: "+registry);
		logger.log(LogLevel.INFO, "Starting refresh timer on interval = "+timerInterval+" msec");
		timer.start(); // start the timer
	}
	
	/**
	 * Sets the java.security.policy system property to point at the location 
	 * of the security policy file, which is assumed to be at 
	 * "provided\rmiUtils\server.policy"  (file separators adjusted to 
	 * match operating system).   the security manager is then started.
	 * This method must be called before starting the
	 * class server.
	 */
	@SuppressWarnings("all")
	private void configSecurityManager() {
		// file.separator is "\" in Windows and "/" in Unix/Linux/Mac.
		String sep = System.getProperty("file.separator");
		String classpath = System.getProperty("user.dir"); // Need this because File cannot find policy if user.dir has been changed from its default when given a relative pathname.
		String policyFilePath = classpath+sep+"provided" + sep + "rmiUtils" + sep + "server.policy";

		File policyFile = new File(policyFilePath); // Better robustness if an absolute path is used here.

		logger.log(LogLevel.INFO, "policyFile = "+policyFile+", "+policyFile.getAbsolutePath());

		if (!policyFile.isFile()) {
			logger.log(LogLevel.ERROR, " <><><> !!! Security policy FILE NOT FOUND !!! <><><>\n" +
	            "Expected file at " + policyFile.getAbsolutePath() +
	            "\nJava security exceptions are likely.\n");
		}

		System.setProperty("java.security.policy", policyFilePath);
		logger.log(LogLevel.INFO, "java.security.policy: "
				+ System.getProperty("java.security.policy"));

		// Start the security manager
		
		if (System.getSecurityManager() == null) {
			logger.log(LogLevel.INFO, "Installing new Security Manager...");
			logger.log(LogLevel.INFO, "Please IGNORE the next set of warnings concerning the deprecated SecurityManager!");
			System.setSecurityManager(new SecurityManager());
			logger.log(LogLevel.INFO, "End of warnings that can be ignored.");
			logger.log(LogLevel.INFO, "Security Manager = " + System.getSecurityManager());
		}
		

	}
	
	/**
	 * Sets the java.rmi.server.hostname system property which are needed to access the local registry.
	 */	
	private void configRMIProperties() {
		// Logs all RMI activity to System.err
		System.setProperty("java.rmi.server.logCalls", "true"); 

		try {
			// Try to get figure out this server's IP address and save it as the
			// RMI server hostname.
			System.setProperty("java.rmi.server.hostname", rmiUtils.getLocalAddress());

			logger.log(LogLevel.INFO, "Configured system properties:\n"+
					"    java.rmi.server.hostname: "
					+ System.getProperty("java.rmi.server.hostname")  );

		} catch (Exception e) {
			logger.log(LogLevel.ERROR, " Error getting local host address: " + e + "\n");
		}

	}

	/**
	 * Unbind the given name from the Registry if it exists.
	 * @param name The name to unbind.
	 */
	public void unbind(String name) {
		try {
			registry.unbind(name);
		} catch (RemoteException e) {
			System.err.println("An exception occurred when attempting to unbind the name, "+name+", from the Registry: "+e);
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.err.println("The name, "+name+", was not bound in the Registry");
		}
	}

}
