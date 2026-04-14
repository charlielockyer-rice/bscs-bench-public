package provided.utils.logic;

/**
 * The visitor to an ILogicalHost that has cases for the 
 * true and false states of the host. 
 * Note that the host is NOT passed to the cases when they are run
 * because the host contains no information other than its state
 * which is known by which case was called.  The host's state is 
 * defined by the case it calls on its visitor. 
 * Any necessary information for the case can be passed in via 
 * the input parameter or whatever the visitor is closing over.
 * 
 * @author swong
 *
 * @param <R>  The type of the return value
 * @param <P>  The type of the input parameter
 */
public interface ILogicalVisitor<R, P> {
	/**
	 * The case that a true state host calls.
	 * @param param  The input parameter given to the host's execute method.
	 * @return The return value from running the case.
	 */
	public R trueCase(P param);

	/**
	 * The case that a false state host calls.
	 * @param param  The input parameter given to the host's execute method.
	 * @return The return value from running the case.
	 */
	public R falseCase(P param);
}
