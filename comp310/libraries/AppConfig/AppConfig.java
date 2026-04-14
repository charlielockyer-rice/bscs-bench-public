package provided.config;

/**
 * Top-level configuration object. Defines the invariant of having a name.
 * All configuration types used in AppConfigMap must be derived from this class.
 * An application or module should define a subclass that add additional final fields for 
 * configuration values needed by that application/module.
 * @author Stephen Wong
 *
 */
public abstract class AppConfig {
	/**
	 * The name associated with a particular configuration.  The configuration will be
	 * keyed by this name in the AppConfigMap.
	 */
	public final String name;
	
	/**
	 * Constructor for the configuration
	 * @param name The name associated with this specific configuration.
	 */
	public AppConfig(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}

/**
EXAMPLE SUBCLASS IMPLEMENTATION:

// This configuration has 3 values: name, xVal and yVal. 
public class MyConfig extends AppConfig {

	public final X xVal;
	public final Y yVal;
	
	public MyConfig(String name, X xVal, Y yVal) {
		super(name);
		this.xVal = xVal;
		this.yVal = yVal;
	}
}

One would then declare the AppConfigMap instance to use MyConfig objects:

AppConfigMap<MyConfig> configMap = new AppConfigMap<MyConfig>(...);  // initialize this as desired.

*/