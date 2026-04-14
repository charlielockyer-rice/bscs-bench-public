package provided.utils.valueGenerator;

/**
 * Defines an object that is capable of accumulating the weighted average of the values 
 * added to it.  The weights given to each value do NOT need to be normalized.
 *  
 * @author swong
 *
 * @param <TInput> The type of the input value
 * @param <TOutput> The type of the averaged value
 */
public interface IAverager<TInput, TOutput> {
	/**
	 * Resets the averager, clearing out any accumulated values and weights.
	 * hasValidAvg() will return false immediately after reset();
	 */
	public void reset();
	
	/**
	 * Returns true if a valid average can be obtained.
	 * Always returns false immediately after a reset().
	 * @return boolean if getAvg() will return a valid average.
	 */
	public boolean hasValidAvg();
	
	/**
	 * Calculate the average from the accumulated values and weights so far.
	 * @return The current average value from the accumulated values and weights so far.
	 * @throws IllegalStateException If a no valid average has been accumulated so far.
	 */
	public TOutput getAvg();
	
	/**
	 * Adds the given value with the given weight to the accumulated value so far and 
	 * adds the given weight to accumulated weights so far.
	 * @param weight The weight of the value.  For equally weighted values, use 1 for the weight.
	 * @param value The value to accumulate.
	 * @throws IllegalArgumentException If given weight is less than zero.
	 */
	public void addTo(double weight, TInput value);
	
}

