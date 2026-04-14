/**
 * 
 */
package provided.rmiUtils;

import provided.config.AppConfig;

/**
 * An AppConfig used for holding the stub and class file server port numbers 
 * for an application instance.   This class should be subclassed to create more
 * specialized configurations that hold additional data as well as perform customizable
 * configuration processes through invariant methods that delegate to variant strategies.   
 * Use this configuration class in conjunction with AppConfigMap&lt;RMIPortConfig&gt; 
 * (substitute the appropriate subclass if subclassing this class). 
 * 
 * See the READ_ME.txt file in the provided.config package for example code illustrating 
 * how to use this and other AppConfig-derived classes.
 * 
 * @author Stephen Wong
 *
 */
public class RMIPortConfig extends AppConfig {
	
	/**
	 * The port to use for all RMI stubs in this configuration.
	 */
	public final int stubPort;
	
	/**
	 * The port the class file server uses in this configuration.
	 */
	public final int classServerPort;
	
	/**
	 * Constructor for the configuration object
	 * @param name  The name associated with this configuration.  This is required by the AppConfigMap.AppConfig superclass.
	 * @param stubPort The port to use for all RMI stubs in this configuration.
	 * @param classServerPort The port the class file server uses in this configuration.
	 */
	public RMIPortConfig(String name, int stubPort, int classServerPort) {
		super(name);
		this.stubPort = stubPort;
		this.classServerPort = classServerPort;
	}

}
