package visitorDemoExercises.listFWVisitorExercises.model.visitors;

import provided.listFW.visitors.AAccumulator;

/**
 * Return the last element of the list
 * Implementing FoldL or FoldR Accs.
 * @author Son Nguyen and Charlie Lockyer
 *
 */
public class LastAcc extends AAccumulator {

	
	/**
	 * Pass in 0 for the empty list
	 */
	public LastAcc() {
		super(0);
	}

	/**
	 * Store the value of x
	 */
	public void accumulate(Object x) {
		value = (int)x;
			
	}

}
