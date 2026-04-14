package provided.config.impl;

import java.util.HashMap;
import java.util.Map;

import provided.config.AppConfig;

/**
 * An map of configuration objects (AppConfig derivatives) keyed by the 
 * name associated with the configuration. 
 * Use putConfig(config) to store configurations and getConfig(name) to retrieve them. 
 * This map is explicitly designed to be difficult to mutate to help prevent
 * accidental mutation or corruption of configuration information.
 * @author Stephen Wong
 *
 * @param <TAppConfig> The type of AppConfig configuration being held by this mapping.
 */
public class AppConfigMap<TAppConfig extends AppConfig> {

	/**
	 * Fully encapsulated configuration object storage to prevent outside mutation
	 */
	private Map<String, TAppConfig> configMap = new HashMap<String, TAppConfig>();
	
	/**
	 * Constructor for the class that assumes that a subclass will load the configuration information.   
	 */
	public AppConfigMap() {
	}
	
	/**
	 * Alternate constructor for the class that allows an outside entity to supply
	 * the configuration objects.
	 * @param configs Vararg of configuration objects to load into the protected internal storage.
	 */
	@SafeVarargs
	public AppConfigMap(TAppConfig... configs) {
		for(TAppConfig config: configs) {
			putConfig(config);
		};
	}
	
	/**
	 * Automatically store the given configuration object keyed to its name.
	 * To protect the immutability of the configuration map, only this class 
	 * or a subclass can load the configurations.
	 * @param config  The configuration object to store.
	 */
	protected void putConfig(TAppConfig config) {
		configMap.put(config.name, config);
	}

	/**
	 * Retrieve the configuration object given its name.   
	 * @param configName The name of the desired configuration object
	 * @return The associated configuration object or null if it could not be found.
	 */
	public TAppConfig getConfig(String configName) {
		return configMap.get(configName);
	}
}
