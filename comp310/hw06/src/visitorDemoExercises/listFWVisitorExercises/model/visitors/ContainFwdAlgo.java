package visitorDemoExercises.listFWVisitorExercises.model.visitors;

import provided.listFW.IListAlgo;
import provided.listFW.MTList;
import provided.listFW.NEList;


/**
 *  Return a boolean indicating whether or not a given element is in the list.
 *  The input parameter is not used ("nu").
 * @author Son Nguyen and Charlie Lockyer
 *
 */
public class ContainFwdAlgo implements IListAlgo {

	@Override
	/**
	 * The base case value is false.
	 */
	public Object emptyCase(MTList host, Object... nu) {
		// TODO: Implement this method
		return null;
	}

	@Override
	/**
	 * Pass the first value to the rest of the list, using a helper visitor, to be accumulated.
	 */
	public Object nonEmptyCase(NEList host, Object... params) {
		// TODO: Implement this method
		return null;
	}

}
