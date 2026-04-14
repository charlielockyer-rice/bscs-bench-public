package provided.datapacket;

/**
 * A concrete implementation of IDataPacketIDFactory.   
 * This factory is NOT a unique implementation of the factory nor does it produce
 * unique implementations of the IDataPacketIDs.
 * 
 * @author Stephen Wong (c) 2018
 * * ----------------------------------------------
 * Concrete implementation of IDataPacketIDFactory
 */
public class DataPacketIDFactory implements IDataPacketIDFactory {

	/**
	 * Singleton instance
	 */
	public static final DataPacketIDFactory Singleton = new DataPacketIDFactory();

	/**
	 * Private constructor for singleton
	 */
	private DataPacketIDFactory() {
	}

	/**
	 * Internal private implementation of the IDataPacketID interface
	 * Nothing in the system cares about this implementation!
	 * @author swong
	 * * ----------------------------------------------
	* A concrete IDataPacketID implementation used by DataPacketIDFactory 
	* 
	 */
	private static class DataPacketID implements IDataPacketID {

		/**
		 * For Serializable
		 */
		private static final long serialVersionUID = 4262333585908925105L;

		/**
		 * Internally held key value
		 */
		private Object key;

		/**
		 * @param dataClass - class of the datapacket
		 */
		public DataPacketID(Class<? extends IDataPacketData> dataClass) {
			key = dataClass.getName();
		}

		@Override
		public int hashCode() {
			return key.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof DataPacketID) { // Equality only if the other is the same type
				return key.equals(((DataPacketID) other).key); // Check if the internal keys are equal.
			}
			return false; // Different classes are always unequal.
		}

		/**
		 * Just show the key's toString() for convenience.
		 */
		@Override
		public String toString() {
			return key.toString();
		}
	}

	@Override
	public IDataPacketID makeID(Class<? extends IDataPacketData> dataInterface) {
		return new DataPacketID(dataInterface);
	}

}
