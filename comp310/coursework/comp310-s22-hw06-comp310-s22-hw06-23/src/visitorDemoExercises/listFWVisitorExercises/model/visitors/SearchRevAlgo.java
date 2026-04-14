package visitorDemoExercises.listFWVisitorExercises.model.visitors;

import provided.listFW.IListAlgo;
import provided.listFW.MTList;
import provided.listFW.NEList;

/**
 * Returns the boolean value if a param is in a list of integers using reverse accumulation.
 * The input parameter is not used ("nu").
 */
public class SearchRevAlgo implements IListAlgo {
	/**
	 * Returns zero always. 
	 */
	public Object emptyCase(MTList host, Object... nu) {
		return false;
	}

	/**
	 * Returns first plus the result of the rest of the list.
	 */
	public Object nonEmptyCase(NEList host, Object... param) {
		return (boolean) ((int) host.getFirst() == Integer.valueOf((String)param[0]))  |
				(boolean) (host.getRest().execute(this,param));
	}

}