package provided.rmiUtils.examples.hello_basic;

import java.rmi.registry.Registry;

import provided.rmiUtils.examples.hello_common.IHello;
import provided.logger.ILoggerControl;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.IRMI_Defs;
import provided.rmiUtils.RMIUtils;


/**
 * Client defaults to the local IP address.   If other IP address is desired, enter as command line
 * parameter in launch/run configuration. 
 * 
 * Adapted from the <a href ="http://java.sun.com/javase/6/docs/technotes/guides/rmi/hello/hello-world.html">
 * Hello World example from Sun</a>.   
 * 
 *
 * This is the client program that accesses the remote IHello object  (the RMI "Server" instance) being presented via RMI on the server process.
 */
public class Client {
	
	/**
	 * RMI utilities for starting RMI and for getting the Registry
	 */
	private IRMIUtils rmiUtils =   new RMIUtils(ILoggerControl.getSharedLogger()); 

	/**
	 * constructor for the client
	 */
	public Client() {}

	/**
	 * Run the client
	 * @param host The the network name (e.g. "myserver.rice.edu")  or IP address of the RMI server. null mean use localhost
	 */
	public void run(String host){
		try {
			
			rmiUtils.startRMI(IRMI_Defs.CLASS_SERVER_PORT_CLIENT);
			if(null == host) {
				host =  System.getProperty("java.rmi.server.hostname");   // Safety check to make sure that the selected local host address is being used when multiple local addresses are available.
			}
			
			Registry registry = rmiUtils.getRemoteRegistry(host);
			System.out.println("Remote Registry = "+registry+"\n");
			
			IHello helloStub = (IHello) registry.lookup("Hello");   // get the stub for the Hello object
			System.out.println("stub: " + helloStub+"\n"); // The toString of a stub object will tell you the IP address of the RMI Server
			String response = helloStub.sayHello(); // Run a method on the Hello object
			System.out.println("response: " + response);  
			
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}		
	}

	/**
	 * Start a client up, which will get the Registry from the host specified by 
	 * args[0], get a stub to the object bound to "Hello" and call its sayHello() method.
	 * 
	 * @param args  The address of the server
	 */
	public static void main(String[] args) {
		String host = (args.length < 1) ? null : args[0];  // see if any command line arguments are supplied
		(new Client()).run(host);
	}
}
