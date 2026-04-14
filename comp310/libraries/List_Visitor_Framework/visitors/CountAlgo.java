package provided.listFW.visitors;

import provided.listFW.IListAlgo;
import provided.listFW.MTList;
import provided.listFW.NEList;

/**
 * Returns the count of the number of elements in the list
 * 
 */
public class CountAlgo implements IListAlgo {
	/**
	 * Returns zero always
	 * @param nu Not used
	 */
	public Object emptyCase(MTList host, Object... nu) {

		return 0;
	}

	/**
	 * Returns 1 plus the count of the rest of the list.
	 * @param nu Not used
	 */
	public Object nonEmptyCase(NEList host, Object... nu) {

		return 1 + (int) host.getRest().execute(this);
	}

}