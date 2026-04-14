package provided.listFW.visitors;

import provided.listFW.IListAlgo;
import provided.listFW.MTList;
import provided.listFW.NEList;

/**
 * Computes a String representation of IList showing  a left parenthesis followed
 * by elements of the IList separated by commas, ending with with a right parenthesis.
 * Implemented as a forward accumulation algorithm.
 */
public class ToStringAlgo implements IListAlgo {
	/**
	 * Singeton instance
	 */
	public static final ToStringAlgo Singleton = new ToStringAlgo();

	/**
	 * Private constructor for the Singleton
	 */
	private ToStringAlgo() {
	}

	/**
	 * Returns "()".
	 * @param nu not used
	 * @return String
	 */
	@Override
	public Object emptyCase(MTList host, Object... nu) {
		return "()";
	}

	/**
	 * Passes "(" + first to the rest of IList and asks for help to complete the computation.
	 * @param nu not used
	 * @return String
	 */
	@Override
	public Object nonEmptyCase(NEList host, Object... nu) {
		return host.getRest().execute(ToStringHelper.Singleton, "(" + host.getFirst());
	}
}

/**
 * Helps ToStringAlgo compute the String representation of the rest of the list.
 * This is the recursive part of the forward accumulation algorithm.
 */
class ToStringHelper implements IListAlgo {
	/**
	 * Singleton instance of helper algo
	 */
	public static final ToStringHelper Singleton = new ToStringHelper();

	/**
	 * Private constructor for Singleton
	 */
	private ToStringHelper() {
	}

	/**
	 * Returns the accumulated String + ")".
	 * At end of list: done!  
	 * @param acc acc[0] is the accumulated String representation of the preceding list.
	 * @return String
	 */
	public Object emptyCase(MTList host, Object... acc) {
		return acc[0] + ")";
	}

	/**
	 * Continues accumulating the String representation by appending ", " + first to acc
	 * and recurse!
	 * @param acc acc[0] is the accumulated String representation of the preceding list.
	 * @return the String representation of the list.
	 */
	public Object nonEmptyCase(NEList host, Object... acc) {
		return host.getRest().execute(this, acc[0] + ", " + host.getFirst());
	}
}
