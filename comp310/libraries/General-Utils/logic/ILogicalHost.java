package provided.utils.logic;

/**
 * A class that models the mathematical notion of a "predicate",
 * i.e. something that evaluates to either true or false.    
 * A logical host represents a logical state of being true or false
 * AT THE TIME THAT ITS EXECUTE METHOD IS CALLED.   This state is
 * not required to be the same the next time the execute method is 
 * called.   The current state of the host is defined by the case it 
 * calls on its visitor.
 * @author swong
 *
 */
public interface ILogicalHost {

	/**
	 * Call the appropriate case on the given visitor corresponding 
	 * to the current state of the host.
	 * @param <R> The return type of the visitor
	 * @param <P> The input parameter type of the visitor 
	 * @param vis The visitor to accept which has true and false cases.
	 * @param param The input parameter to pass to the visitor's case that is called.
	 * @return The result from running the case of the visitor
	 */
	<R, P> R execute(ILogicalVisitor<R, P> vis, P param);
}
