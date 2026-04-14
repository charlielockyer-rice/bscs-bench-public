package visitorDemoExercises.listFWVisitorExercises.model.visitors;

import provided.listFW.visitors.AAccumulator;

/**
 * Accumulates the largest values, starting with from Min_Value.
 * @author Son Nguyen and Charlie Lockyer
 */
public class LargestAcc extends AAccumulator {
	
	/**
<<<<<<< HEAD
	 * Constructor for the class
=======
	 * The constructor as Min_Value
>>>>>>> branch 'main' of https://github.com/Rice-COMP-310/comp310-s22-hw06-comp310-s22-hw06-23.git
	 */
	public LargestAcc() {
			super(Integer.MIN_VALUE);
		}

	/**
	 * Change the stored value to the given value if it's larger
	 */
	public void accumulate(Object x) {
		value = Math.max((int) value, (int) x);
	}
	
}
