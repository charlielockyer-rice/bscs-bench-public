package provided.ballworld.extVisitors.impl;

import java.util.UUID;

import provided.ballworld.extVisitors.IBallHostID;
import provided.ballworld.extVisitors.IBallHostIDFactory;

/**
 * A concrete implementation of IBallHostIDFactory.   
 * 
 * @author swong (c) 2022
 */
public class BallHostIDFactory implements IBallHostIDFactory {

	/**
	 * Singleton instance
	 */
	public static final BallHostIDFactory Singleton = new BallHostIDFactory();

	/**
	 * Private constructor for singleton
	 */
	private BallHostIDFactory() {
	}

	/**
	 * Internal private implementation of the BallHostID interface
	 * Nothing in the system cares about this implementation!
	 * @author swong
	 */
	private static class BallHostID implements IBallHostID {

		/**
		 * For Serializable
		 */
		private static final long serialVersionUID = 4262333585908925105L;

		/**
		 * Internally held key value
		 */
		private Object key;

		/**
		 * A friendly name for this host ID.  NOT used in comparisons!
		 */
		private String idName;

		/**
		 * Constructor for the class
		 * @param idName The friendly name to use for this ID.  NOT used in comparisons!
		 */
		public BallHostID(String idName) {
			this.idName = idName;
			this.key = UUID.randomUUID();
		}

		@Override
		public int hashCode() {
			return key.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof BallHostID) { // Equality only if the other is the same type
				return key.equals(((BallHostID) other).key); // Check if the internal keys are equal.
			}
			return false; // Different classes are always unequal.
		}

		/**
		 * Just show the friendly name for convenience.
		 */
		@Override
		public String toString() {
			return idName;
		}
	}

	@Override
	public IBallHostID makeID(String idName) {
		return new BallHostID(idName);
	}

}
