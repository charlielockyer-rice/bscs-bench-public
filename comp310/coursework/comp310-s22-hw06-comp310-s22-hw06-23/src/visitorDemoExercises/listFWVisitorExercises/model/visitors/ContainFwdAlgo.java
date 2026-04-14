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
	/**
	 * The Number to be compared to.
	 */
	int param;
	@Override
	/**
	 * The base case value is false.
	 */
	public Object emptyCase(MTList host, Object... nu) {
		return false;
	}

	@Override
	/**
	 * Pass the first value to the rest of the list, using a helper visitor, to be accumulated.
	 */
	public Object nonEmptyCase(NEList host, Object... params) {
		param = Integer.valueOf((String) params[0]);
		// The accumulated value is just the first value.
		return host.getRest().execute(helper, (boolean) ((int) host.getFirst() == param));
	}

	/**
	 * Recursive helper that uses an accumulator as its input parameter.
	 */
	private IListAlgo helper = new IListAlgo() {

		@Override
		/**
		 * The result is the current accumulated value.
		 */
		public Object emptyCase(MTList host, Object... accs) {
			return (boolean) accs[0];
		}
		 
		@Override
		/**
		 * Add first to the incoming accumulated value and pass the new value to the rest of the list, recursively.
		 */
		public Object nonEmptyCase(NEList host, Object... accs) {
			return host.getRest().execute(this, (boolean)accs[0] | (boolean)((int)host.getFirst() == param));
		}

	};
}
