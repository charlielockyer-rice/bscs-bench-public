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
		return null;
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
		 * Return the first value.
		 */
		public Object nonEmptyCase(NEList host, Object... accs) {
			return host.getRest().execute(this, (int) host.getFirst());
		}

	};
}
