package provided.listFW.visitors;

import provided.listFW.IList;
import provided.listFW.IListAlgo;
import provided.listFW.MTList;
import provided.listFW.NEList;

/**
 * Returns a copy of the list
 * 
 */
public class CopyAlgo implements IListAlgo {
	/**
	 * Returns an MTList always.
	 * @param nu Not used
	 */
	public Object emptyCase(MTList host, Object... nu) {
		return MTList.Singleton;
	}

	/**
	 * Returns a new NEList where first is the original first and 
	 * rest is a copy of the original rest.
	 * @param nu Not used
	 */
	public Object nonEmptyCase(NEList host, Object... nu) {

		return new NEList(host.getFirst(), (IList) host.getRest().execute(this));
	}

}
