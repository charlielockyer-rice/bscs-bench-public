package visitorDemoExercises.listFWVisitorExercises.model.visitors;

import provided.listFW.IListAlgo;
import provided.listFW.MTList;
import provided.listFW.NEList;

/**
 * Returns the sum of a list of integers using reverse accumulation starting from nth.
 * The input parameter is not used ("nu").
 */
public class FromNthAlgo implements IListAlgo {
	/**
	 * Returns zero always.
	 */
	public Object emptyCase(MTList host, Object... nu) {
		// TODO: Implement this method
		return 0;
	}

	/**
	 * Returns first plus the sum of the rest of the list.
	 */
	public Object nonEmptyCase(NEList host, Object... params) {
		// TODO: Implement this method
		return 0;
	}

}
