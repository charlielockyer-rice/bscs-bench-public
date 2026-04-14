package visitorDemoExercises.listFWVisitorExercises.model.visitors;

import provided.listFW.IListAlgo;
import provided.listFW.MTList;
import provided.listFW.NEList;


/**
 *  Returns the sum of a list of integers using forward accumulation.
 *  The input parameter is not used ("nu").
 * @author Son Nguyen and Charlie Lockyer
 *
 */
public class LastFwdAlgo implements IListAlgo {

	@Override
	/**
	 * The base case value is null,
	 */
	public Object emptyCase(MTList host, Object... nu) {
		// TODO: Implement this method
		return null;
	}

	@Override
	/**
	 * Pass the first value to the rest of the list, using a helper visitor, to be accumulated.
	 */
	public Object nonEmptyCase(NEList host, Object... nu) {
		// TODO: Implement this method
		return null;
	}

}
