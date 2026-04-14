/**
 * 
 */
package provided.rmiUtils;

/**
 * An RMIPortConfig configuration that adds the storage of the Registry bound name associated with an app configuration. 
 * This type of configuration is useful when multiple simultaneous instances of an application all need to bind application-specific 
 * stubs into the Registry.   The use of app instance-specific bound names keeps the app instances from conflicting with each other 
 * in the Registry.   Use the bound name stored here to configure the discovery service model so that the proper endpoint for the 
 * app instance can be constructed.
 * 
 * See the READ_ME.txt file in the provided.config package for example code illustrating 
 * how to use  this and other AppConfig-derived classes.
 * @author swong
 *
 */
public class RMIPortConfigWithBoundName extends RMIPortConfig {
	
	/**
	 * The bound name used for a stub bound to the local Registry
	 */
	public final String boundName;

	/**
	 * Constructor for the configuration object
	 * @param name  The name associated with this configuration.  This is required by the AppConfigMap.AppConfig superclass.
	 * @param stubPort The port to use for all RMI stubs in this configuration.
	 * @param classServerPort The port the class file server uses in this configuration.
	 * @param boundName The bound name to be used for the associated app instance's stub in the local Registry.
	 */
	public RMIPortConfigWithBoundName(String name, int stubPort, int classServerPort, String boundName) {
		super(name, stubPort, classServerPort);
		this.boundName = boundName;
	}

}
