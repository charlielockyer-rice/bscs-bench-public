package provided.ballworld.extVisitors;

import provided.extvisitor.IExtVisitorCmd;

/**
 * Partially type-narrowed extended visitor command for Ballworld
 * @author swong
 *
 * @param <R> The type of the return value
 * @param <P> The type of the input parameter values
 * @param <H> The specific sub-type of the visitor's host being used. 
 */
public interface IBallHostAlgoCmd<R, P, H extends IBallHost<? super H>> extends IExtVisitorCmd<R, IBallHostID, P, H> {

}
