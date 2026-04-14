package provided.config.impl;

import java.util.Set;
import java.util.UUID;

import provided.config.AppConfig;
import provided.mixedData.IMixedDataDictionary;
import provided.mixedData.MixedDataDictionary;
import provided.mixedData.MixedDataKey;

/**
 * An AppConfig that, as an IMixedDataDictionary, can hold arbitrary key-value pairs of data, including functionals,
 * in a type-safe manner.     This app config holds an internal UUID for use in making keys and '
 * any other desired uses.   This is a very general, very powerful configuration that can be used 
 * for many purposes including scenarios requiring complex custom configuration processes.
 * @author swong
 *
 */
public class AppConfigData extends AppConfig implements IMixedDataDictionary{
	
	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -1012219140897872337L;

	/**
	 * Internal IMixedDataDictionary that is actually holding the key-value pairs.
	 */
	private IMixedDataDictionary dataDict = new MixedDataDictionary();
	
	/**
	 * UUID for making keys or other uses.   Defaults to a random value.
	 */
	private UUID uuid = UUID.randomUUID();

	/**
	 * Construct an instance using the given friendly name and random UUID value.
	 * @param name The friendly name of this app config.
	 */
	public AppConfigData(String name) {
		super(name);
	}
	
	/**
	 * Construct an instance using the given friendly name and UUID value.
	 * @param name The friendly name of this app config.
	 * @param uuid The UUID associated with this app config.
	 */
	public AppConfigData(String name, UUID uuid) {
		super(name);
		this.uuid = uuid;
	}	
	
	/**
	 * Get the UUID associated with this app config.
	 * @return The UUID associated with this app config.
	 */
	public UUID getUUID() {
		return this.uuid;
	}
	
	@Override
	public <T> T put(MixedDataKey<T> key, T data) {
		return dataDict.put(key, data);
	}
	
	@Override
	public <T> T get(MixedDataKey<T> key) {
		return dataDict.get(key);
	}
	
	@Override
	public boolean containsKey(MixedDataKey<?> key) {
		return dataDict.containsKey(key);
	}
	
	@Override
	public <T> T remove(MixedDataKey<T> key) {
		return dataDict.remove(key);
	}
	
	@Override
	public Set<MixedDataKey<?>> getKeys(UUID id) {
		return dataDict.getKeys(id);
	}
	
	/**
	 * Convenience method to return all the keys that have this AppConfigData's UUID.
	 * @return a set of keys with this config's UUID.
	 */
	public Set<MixedDataKey<?>> getKeys() {
		return dataDict.getKeys(this.uuid);
	}
	
	/**
	 * Convenience method to make a key for an integer value with this config's UUID
	 * @param name The name of the key
	 * @return A key for an integer value 
	 */
	public MixedDataKey<Integer> makeIntKey(String name) {
		return new MixedDataKey<Integer>(this.getUUID(), name, Integer.class);
	}
	
	/**
	 * Convenience method to make a key for a String value with this config's UUID
	 * @param name The name of the key
	 * @return A key for a String value 
	 */
	public MixedDataKey<String> makeStrKey(String name) {
		return new MixedDataKey<String>(this.getUUID(), name, String.class);
	}


}
