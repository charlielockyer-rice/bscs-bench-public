package provided.ballworld.extVisitors;

import provided.extvisitor.IExtVisitor;

/**
 * Type-narrowed extended visitor for balls.
 * This interface needs further type-narrowing to be used in student code!
 * @author swong
 *
 * @param <R> The type of the return value
 * @param <P> The type of the input parameter values
 * @param <H> The specific sub-type of the visitor's host being used.  
 */
public interface IBallHostAlgo<R, P, H extends IBallHost<? extends H>> extends IExtVisitor<R, IBallHostID, P, H> {

}
