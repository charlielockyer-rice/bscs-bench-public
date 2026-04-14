package provided.listFW.visitors;

/**
 * An accumulator for use in FoldR and FoldL.
 * The toString() methods of any concrete implementation should be overridden to return a String representation of the 
 * current accumulated value.  
 * @author swong
 *
 */
public interface IAccumulator {

	/**
	 * Accumulates the given value into the internally stored total
	 * @param x  The value to process for accumulation
	 */
	void accumulate(Object x);
}