package provided.utils.function;

/**
 * A Consumer-type function that takes 3 inputs
 * @author swong
 *
 * @param <T>  The type of the first argument	
 * @param <U>  The type of the second argument	
 * @param <V>  The type of the third argument	
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
	/**
	 * Performs this operation on the given arguments.
	 * @param t  The first argument
	 * @param u  The second argument
	 * @param v  The third argument
	 */
	public void accept(T t, U u, V v);
}
