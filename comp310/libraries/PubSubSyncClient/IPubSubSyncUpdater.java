package provided.pubsubsync;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.HashSet;
import java.util.function.Function;

import common.room.messageReceiver.INamedRoomMessageReceiver;

/**
 * Represents a generic function to update the value of the published data.  
 * This function takes the current data value and returns the new data value.   
 * The new data value may be the old data value mutated in some manner or a new instance 
 * to replace the current data value.
 * Any data elements that are needed to update the current published data value should be encapsulated 
 * inside of the IPubSubSyncUpdater instance.    
 * Static factory methods are provided to instantiate IPubSubSyncUpdater instances for some common updating scenarios.
 * @author Stephen Wong (c) 2020
 *
 * @param <T>  The type of the data being published.
 */
public interface IPubSubSyncUpdater<T extends Serializable> extends Function<T, T>, Serializable {

	/**
	 * Takes the current data value and mutates or replaces it, returning the new data value to be published.
	 * @param currentData The current published data value
	 * @return The new data value to be published
	 */
	@Override
	public T apply(T currentData);
	
	
	/**
	 * Factory method to create an IPubSubSyncUpdater instance that will replace the currently published data 
	 * with an entirely new value.
	 * @param <E> The type of data being published.
	 * @param data The new data value to be published.
	 * @return An IPubSubSyncUpdater instance
	 */
	public static <E extends Serializable> IPubSubSyncUpdater<E> makeReplaceFn(final E data) {
		return new IPubSubSyncUpdater<E>() {

			/**
			 * For serialization
			 */
			private static final long serialVersionUID = -5091700664203956072L;

			@Override
			public E apply(E t) {
				return data;
			}
		};
	}
	
	/**
	 * Factory method to create an IPubSubSyncUpdater instance that will add an element to a published 
	 * HashSet of elements.  Note that the channel must be created with a HashSet&lt;E&gt; object 
	 * as the initial data value.
	 * @param <E> The type of elements in the published HashSet 
	 * @param element  The element to be added to the published HashSet.
	 * @return An IPubSubSyncUpdater instance
	 */
	public static <E extends Serializable> IPubSubSyncUpdater<HashSet<E>> makeSetAddFn(final E element) {
		return new IPubSubSyncUpdater<HashSet<E>>() {

			/**
			 * For serialization
			 */
			private static final long serialVersionUID = 5624873223898712648L;

			@Override
			public HashSet<E> apply(HashSet<E> data) {
				data.add(element);
				return data;
			}
		};
	}

	/**
	 * Factory method to create an IPubSubSyncUpdater instance that will remove an element from a published 
	 * HashSet of elements.  Note that the channel must be created with a HashSet&lt;E&gt; object 
	 * as the initial data value.    Note that the data will be considered to be updated even if current 
	 * published HashSet data value doesn't contain the given element. 
	 * @param <E> The type of elements in the published HashSet 
	 * @param element  The element to be removed from the published HashSet.
	 * @return An IPubSubSyncUpdater instance
	 */
	public static <E extends Serializable> IPubSubSyncUpdater<HashSet<E>> makeSetRemoveFn(final E element) {
		return new IPubSubSyncUpdater<HashSet<E>>() {
			
			/**
			 * For serialization
			 */
			private static final long serialVersionUID = -265427104451304133L;

			@Override
			public HashSet<E> apply(HashSet<E> data) {
				INamedRoomMessageReceiver elt = (INamedRoomMessageReceiver) element;
				data.forEach((item)->{
					INamedRoomMessageReceiver dyad = (INamedRoomMessageReceiver) item;
					System.out.println("[IPubSubSyncUpdater.makeSetRemoveFn()] "+dyad.getName()+ ": "
							+dyad.equals(elt)+", "
							+dyad.getName().equals(elt.getName())+", "
							+dyad.getRoomConnectionStub().equals(elt.getRoomConnectionStub())+", "
							+dyad.getNetworkDyad().equals(elt.getNetworkDyad())+", "
							+dyad.getNetworkDyad().getName().equals(elt.getNetworkDyad().getName())+", "
							+dyad.getNetworkDyad().getNetworkStub().equals(elt.getNetworkDyad().getNetworkStub())
							);

				});
				
				
				if(data.remove(element)) {
					System.out.println("[IPubSubSyncUpdater.makeSetRemoveFn()] Successfully removed element: "+element);
				}
				else {
					System.err.println("ERROR! [IPubSubSyncUpdater.makeSetRemoveFn()] No element removed! Element not found: "+element);
				}
				return data;
			}
		};
	}
	
	/**
	 * Factory method to create an IPubSubSyncUpdater instance that will add a <b>stub</b> of an RMI Remote element to a published 
	 * HashSet of stub  Note that the channel must be created with a HashSet&lt;E&gt; object 
	 * as the initial data value.  
	 * <b>WARNING</b>: Be sure that you are indeed adding an RMI <em>stub</em> object rather than the actual RMI server object!  
	 * RMI server objects are NOT Serializable but their stubs are.  Unfortunately, the compiler cannot tell the difference between
	 * an RMI stub and its RMI server instance, so it is up to the developer to make sure that the correct entities are placed 
	 * in the published data.  
	 * @param <E> The type of Remote stub elements in the published HashSet 
	 * @param element  The element to be added to the published HashSet.
	 * @return An IPubSubSyncUpdater instance
	 */
	public static <E extends Remote> IPubSubSyncUpdater<HashSet<E>> makeRemoteSetAddFn(final E element) {
		return new IPubSubSyncUpdater<HashSet<E>>() {

			/**
			 * For serialization
			 */
			private static final long serialVersionUID = 4642923222715111913L;

			@Override
			public HashSet<E> apply(HashSet<E> data) {
				data.add(element);
				return data;
			}
		};
	}

	/**
	 * Factory method to create an IPubSubSyncUpdater instance that will remove a <b>stub</b> of an RMI Remote element from a published 
	 * HashSet of stub  Note that the channel must be created with a HashSet&lt;E&gt; object 
	 * as the initial data value.  
	 * <b>WARNING</b>: Be sure that you are indeed adding an RMI <em>stub</em> object rather than the actual RMI server object!  
	 * RMI server objects are NOT Serializable but their stubs are.  Unfortunately, the compiler cannot tell the difference between
	 * an RMI stub and its RMI server instance, so it is up to the developer to make sure that the correct entities are placed 
	 * in the published data.  
	 * @param <E> The type of Remote stub elements in the published HashSet 
	 * @param element  The element to be added to the published HashSet.
	 * @return An IPubSubSyncUpdater instance
	 */
	public static <E extends Remote> IPubSubSyncUpdater<HashSet<E>> makeRemoteSetRemoveFn(final E element) {
		return new IPubSubSyncUpdater<HashSet<E>>() {

			/**
			 * For serialization
			 */
			private static final long serialVersionUID = 6997715469742523398L;

			@Override
			public HashSet<E> apply(HashSet<E> data) {
				data.remove(element);
				return data;
			}
		};
	}
	
}
