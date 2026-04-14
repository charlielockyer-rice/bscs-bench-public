package visitorDemoExercises.listFWVisitorExercises.model.visitors;

import provided.listFW.IListAlgo;
import provided.listFW.MTList;
import provided.listFW.NEList;


/**
 *  Returns the largest of a list of integers using forward accumulation.
 *  The input parameter is not used ("nu").
 * @author Son Nguyen and Charlie Lockyer
 *
 */
public class LargestFwdAlgo implements IListAlgo {

	@Override
	/**
	 * The base case value is Min_Value,
	 */
	public Object emptyCase(MTList host, Object... nu) {
		return Integer.MIN_VALUE;
	}

	@Override
	/**
	 * Pass the first value to the rest of the list, using a helper visitor, to be accumulated.
	 */
	public Object nonEmptyCase(NEList host, Object... nu) {

		// The accumulated value is just the first value.
		return host.getRest().execute(helper, host.getFirst());
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
			return accs[0];
		}

		@Override
		/**
		 * Add first to the incoming accumulated value and pass the new value to the rest of the list, recursively.
		 */
		public Object nonEmptyCase(NEList host, Object... accs) {
			return host.getRest().execute(this, Math.max((int) accs[0], (int) host.getFirst()));
		}

	};
}
