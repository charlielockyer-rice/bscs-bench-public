package provided.listFW.visitors;

import provided.listFW.IList;
import provided.listFW.MTList;
import provided.listFW.NEList;


/**
 * Accumulates the values into a list, starting with an empty list.
 * @author swong
 *
 */
public class CopyAcc extends AAccumulator {

	/**
	 * Constructor for the class
	 */
	public CopyAcc() {
		super(MTList.Singleton);
	}

	@Override
	/**
	 * Add the given value to the front of the stored list.
	 */
	public void accumulate(Object x) {
		value = new NEList(x, (IList) value);

	}

}
