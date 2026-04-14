package provided.utils.struct;

/**
 * A representation of a pair of objects
 * @author swong
 *
 * @param <F>  The type of the first element
 * @param <S>  The type of the second element
 */
public interface IDyad<F, S> {
	/**
	 * Get the first element
	 * @return The first element
	 */
	F getFirst();

	/**
	 * Get the second element
	 * @return The second element
	 */
	S getSecond();

}