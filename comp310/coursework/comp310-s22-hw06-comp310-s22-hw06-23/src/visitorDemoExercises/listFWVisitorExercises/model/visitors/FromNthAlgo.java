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
		return 0;
	}

	/**
	 * Returns first plus the sum of the rest of the list.
	 */
	public Object nonEmptyCase(NEList host, Object... params) {
		int a = 0;
		int param = Integer.valueOf((String)params[0])- 1;
		if (param < 0)
			a = (int) host.getFirst();
		return a + (int) host.getRest().execute(this,""+param);
	}

}