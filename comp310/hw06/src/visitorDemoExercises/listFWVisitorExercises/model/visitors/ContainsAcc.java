package visitorDemoExercises.listFWVisitorExercises.model.visitors;

import provided.listFW.visitors.AAccumulator;

/**
 *  Return a boolean indicating whether or not a given element is in the list.
 *  Implementing FoldL or FoldR Accs.
 * @author Son Nguyen and Charlie Lockyer
 *
 */
public class ContainsAcc extends AAccumulator {

	/**
	 * Constructor for the class
	 */
	private int comparator;
	
	/**
	 * @param param The input parameter as a String
	 */
	public ContainsAcc(Object param) {
		super(false);
		this.comparator = Integer.valueOf((String)param);
	}

	/**
	 * Check if given value is stored value
	 */
	public void accumulate(Object x) {
		if((int)x == this.comparator)
			value = true;
	}

}
