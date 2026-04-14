package provided.listFW.visitors;

import provided.listFW.IList;
import provided.listFW.IListAlgo;
import provided.listFW.MTList;
import provided.listFW.NEList;


/**
 * Returns a copy of the list where the single integer parameter is added to all elements
 */
public class AddToAlgo implements IListAlgo {
	/**
	 * Returns an empty list always
	 * @param inp Not used in the base case
	 */
	public Object emptyCase(MTList host, Object... inp) {
		return MTList.Singleton;
	}

	/**
	 * Returns a new NEList with the original first added to the given integer parameter, where the rest of the 
	 * list is the AddToAlgo applied to the rest of the original list.
	 * @param inp inp[0] is the integer value to add to each element.
	 */
	public Object nonEmptyCase(NEList host, Object... inp) {

		return new NEList(Integer.parseInt((String) inp[0])
				+ (int) host.getFirst(), (IList) host.getRest().execute(this,
				inp));
	}

}