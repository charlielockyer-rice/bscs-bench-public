package provided.config.test;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.SwingUtilities;

import provided.config.impl.AppConfigChooser;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.IRMI_Defs;
import provided.rmiUtils.RMIPortConfigWithBoundName;
import provided.rmiUtils.RMIUtils;

/**
 * Simple demo of AppConfigChooser with RMIPortConfigWithBoundName app configs.
 * THIS DEMO MUST BE RUN USING THE ASSOCIATED .launch FILE!!   Do NOT run this file directly!
 * Select the run/launch file in the Package Explorer and click the green Run button.
 * 
 * Be sure to shut the app down from the Console.
 * 
 * Note that for simplicity, all the code is in this controller-like file but in in a real
 * application, much of the code would be located in the model part of the system.
 * 
 * It is debatable whether the app config definition and selection should take in the controller
 * or the model. If it takes place in the controller, be sure to pass the selected configuration
 * to the model upon instantiation.    
 * 
 * If using a Discover Server in a publishing mode, the selected app config's boundName MUST be 
 * supplied when starting the discovery server model.  The selected app config's name can be used for 
 * the supplied "friendly name" if desired.
 *
 * @author swong
 */
public class RMIAppConfigChooserDemo {
	
	/**
	 * The logger in use
	 */
	private ILogger logger = ILoggerControl.getSharedLogger();
	
	/**
	 * The IRMIUtils in use.   
	 */
	private IRMIUtils rmiUtils;
	
	/**
	 * The instantiation of the RMI Server object
	 */
	private IMyRMIObject myRMIserver = new IMyRMIObject() {

		@Override
		public void run() throws RemoteException {
			// Configuration-dependent output!
			logger.log(LogLevel.INFO, "myRMIserver is running under the configuration:: "+currentConfig.name);
		}
		
	};
	
	/**
	 * The RMI stub of the RMI Server object
	 */
	IMyRMIObject myRMIstub;

	/**
	 * The selected app configuration holding the configuration-dependent information
	 */
	private RMIPortConfigWithBoundName currentConfig;
	
	/**
	 * 3 possible app configs with different config names, port numbers and bound names.
	 */
	AppConfigChooser<RMIPortConfigWithBoundName> appChooser =  new AppConfigChooser<RMIPortConfigWithBoundName>( // Can add default choice index parameter here if desired
			new RMIPortConfigWithBoundName("Persona #1", IRMI_Defs.STUB_PORT_SERVER, IRMI_Defs.CLASS_SERVER_PORT_SERVER, "MyApp_1"),	
			new RMIPortConfigWithBoundName("Persona #2", IRMI_Defs.STUB_PORT_CLIENT, IRMI_Defs.CLASS_SERVER_PORT_CLIENT, "MyApp_2"),
			new RMIPortConfigWithBoundName("Persona #3", IRMI_Defs.STUB_PORT_EXTRA, IRMI_Defs.CLASS_SERVER_PORT_EXTRA, "MyApp_3")
	);	
	
	/**
	 * Constructor for the class
	 */
	public RMIAppConfigChooserDemo() {
		// Select the desired app configuration early so that any configuration-dependent
		// construction processes can use it.
		currentConfig = appChooser.choose(); // Have the user select a configuration.
		
		rmiUtils = new RMIUtils(logger); // Instantiate the IRMIUtils.		
	}
	
	/**
	 * Start the app
	 */
	public void start() {
		// Logging info about the currently selected app config:
		logger.log(LogLevel.INFO,"Current app config: "+currentConfig);
		logger.log(LogLevel.INFO,"name = "+currentConfig.name);
		logger.log(LogLevel.INFO,"stubPort = "+currentConfig.stubPort);
		logger.log(LogLevel.INFO,"classServerPort = "+currentConfig.classServerPort);
		logger.log(LogLevel.INFO,"boundName = "+currentConfig.boundName);
		
		// Start the RMI Utils with the class server port from the selected app config
		rmiUtils.startRMI(currentConfig.classServerPort);  
		
		Registry localRegistry = rmiUtils.getLocalRegistry(); // Get the local Registry
		
		try {
			// Create a stub using the stub port from the selected app config
			myRMIstub = (IMyRMIObject) UnicastRemoteObject.exportObject(myRMIserver, currentConfig.stubPort);
			
			// Bind the stub into the local Registry using the bound name from the selected app config. 
			localRegistry.rebind(currentConfig.boundName, myRMIstub);
			
			// testing the stub to show that it will log a configuration-dependent output.
			myRMIstub.run();  // This is just for demo purposes
			
		} catch (RemoteException e) {
			logger.log(LogLevel.ERROR, "Exception while creating, binding and testing the RMI stub: "+e);
			e.printStackTrace();
		} 
		
		
	}

	/**
	 * Test function to demonstrate using the chooser.  Run as a Java application. 
	 * In a real application, the code in this method would not necessarily be in main() but 
	 * rather in a location that gave the app configs any necessary access to required entities, 
	 * e.g. in the the controller's constructor and/or start() method.  
	 * @param args Not used
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(()->{
			(new RMIAppConfigChooserDemo()).start();	
		});
	}

}

/**
 * Just a test RMI entity definition for demonstration purposes
 * @author swong
 *
 */
interface IMyRMIObject extends Remote {
	
	/**
	 * Something that this class does.
	 * @throws RemoteException upon network error
	 */
	public void run() throws RemoteException;
}
