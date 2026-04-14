package visitorDemoExercises.listFWVisitorExercises.model.visitors;

import provided.listFW.IListAlgo;
import provided.listFW.MTList;
import provided.listFW.NEList;

/**
 * Returns the sum of a list of integers using reverse accumulation.
 * The input parameter is not used ("nu").
 * @author Son Nguyen and Charlie Lockyer
 */
public class LargestRevAlgo implements IListAlgo {
	/**
	 * Returns Min_value always. 
	 */
	public Object emptyCase(MTList host, Object... nu) {
		return Integer.MIN_VALUE;
	}

	/**
	 * Returns max between first and rest
	 */
	public Object nonEmptyCase(NEList host, Object... nu) {

		return Math.max((int) host.getFirst(), (int) host.getRest().execute(this));
	}

}