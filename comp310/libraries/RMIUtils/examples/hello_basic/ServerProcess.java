package provided.rmiUtils.examples.hello_basic;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import provided.rmiUtils.examples.hello_common.IHello;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.IRMI_Defs;
import provided.rmiUtils.RMIUtils;


/**
 * Adapted from the <a href = "http://java.sun.com/javase/6/docs/technotes/guides/rmi/hello/hello-world.html">
 * Hello World" example from Sun</a>.  
 * 
 * This class is the process that is hosting the RMI system and serving out 
 * RMI "Server" objects.    This class is NOT an RMI Server instance!!
 * 
 * SERVER MACHINE MUST HAVE REGISTRY_PORT AND SERVER_PORT (see below and RMIUtilsBasic) OPEN
 * THROUGH FIREWALL!!
 */
public class ServerProcess  {

	/**
	 * The logger used by the app
	 */
	private ILogger logger = ILoggerControl.getSharedLogger();

	/**
	 * RMI utilities for starting and running RMI 
	 */
	private IRMIUtils rmiUtils; 

	/**
	 * Reference to the Registry
	 */
	private Registry registry;

	/**
	 * The RMI Server implementation -- the actual object that will do the processing.
	 */
	private IHello helloServer = new IHello() {

		@Override
		public String sayHello() throws RemoteException {
			logger.log(LogLevel.INFO, "helloServer.sayHello() method invoked.");
			return "Hello RMI World!";
		}
	};
	
	/**
	 * Constructor for server
	 */
	public ServerProcess() {
		rmiUtils = new RMIUtils(logger);
	}
	
	
	/**
	 * Run the Server, which will find the RMI Registry, creating it if
	 * necessary, and bind an instance of the Server to the "Hello" name in the
	 * Registry. The server then stays in an infinite loop waiting for someone
	 * to use the bound server.
	 */
	public void run() {
		try {
			// Start the RMI system and get the local Registry, making it if necessary.
			rmiUtils.startRMI(IRMI_Defs.CLASS_SERVER_PORT_SERVER);
			
			registry = rmiUtils.getLocalRegistry();
			System.out.println("Local Registry = "+registry+"\n");
		}
		catch(Exception e) {
			System.err.println("Exception while intializing RMI: \n" + e);
			e.printStackTrace();
			System.exit(-1); // exit the program.
		}

		try {

			// Create a UnicastRemoteObject stub from the RMI Server implementation to be sent to the clients.
			IHello helloStub = (IHello) UnicastRemoteObject.exportObject(helloServer, IRMI_Defs.STUB_PORT_SERVER);

			// Bind the remote object's stub in the registry at the specified
			// port use rebind instead of bind so the program can be run
			// multiple times with the same registry
			registry.rebind("Hello", helloStub);

			System.err.println("Server ready");

		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
			System.exit(-1); // exit the program.
		}

		// The following loop is just to keep the program from terminating
		// This loop will not be necessary once the server has a GUI
		while (true) {
			try {
				// Sleep this thread for 100 ms intervals to keep it from
				// consuming resources
				Thread.sleep(100);
			} 
			catch (Exception e) {
				System.err.println("Exception during sleeping: "+e);
				// Continue on.  Can kill the process from the console if needed.
			}
		}
	}

	
	
	/**
	 * Start up the server
	 * 
	 * @param args
	 *            Not used.
	 */
	public static void main(String args[]) {
		(new ServerProcess()).run();

	}
}
