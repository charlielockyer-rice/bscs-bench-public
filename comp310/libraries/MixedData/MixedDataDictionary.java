package provided.mixedData;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.UUID;

/**
 * Concrete implementation of the IMixedDataDictionary interface.
 * @author swong
 *
 */
public class MixedDataDictionary implements IMixedDataDictionary {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2529509168949450604L;
	/**
	 * Internal, weakly-typed data storage
	 */
	private Hashtable<MixedDataKey<?>, Object> store  = new Hashtable<MixedDataKey<?>, Object>();
	
	
	/* (non-Javadoc)
	 * @see provided.mixedData.IMixedDataDictionary#containsKey(provided.mixedData.MixedDataKey)
	 */
	@Override
	public boolean containsKey(MixedDataKey<?> key) {
		return store.containsKey(key);
	}
	
	/* (non-Javadoc)
	 * @see provided.mixedData.IMixedDataDictionary#put(provided.mixedData.MixedDataKey, T)
	 */
	
	@Override
	public <T> T put(MixedDataKey<T> key, T value) {
		@SuppressWarnings("unchecked")
		T result =  (T) store.put(key, value);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see provided.mixedData.IMixedDataDictionary#get(provided.mixedData.MixedDataKey)
	 */
	@Override
	public <T> T get(MixedDataKey<T> key) {
		@SuppressWarnings("unchecked")
		T result = (T) store.get(key);
		return result;
	}
	
	@Override
	public <T> T remove(MixedDataKey<T> key) {
		@SuppressWarnings("unchecked")
		T result = (T) store.remove(key);
		return result;
	}
	
	
	@Override
	public Set<MixedDataKey<?>> getKeys(UUID id) {
		Set<MixedDataKey<?>> result = new HashSet<MixedDataKey<?>>();
		
		for(MixedDataKey<?> key: store.keySet()) {
			if(id.equals(key.getID())) {
				result.add(key);
			}
		}
		
		return result;
	}



}
