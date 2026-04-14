package provided.ballworld.extVisitors.impl;

import provided.ballworld.extVisitors.IBallHost;
import provided.ballworld.extVisitors.IBallHostAlgoCmd;
import provided.ballworld.extVisitors.IBallHostID;
import provided.extvisitor.IExtVisitorHost;

/**
 * Type-narrowed command for ABallHostAlgo
 * @param <R>  Return type for visitor algorithm
 * @param <P> Input parameter type for visitor algorithm.
 * @param <H> Specific ball sub-type for host
 * @author swong
 */
public abstract class ABallHostAlgoCmd<R, P, H extends IBallHost<? super H>> implements IBallHostAlgoCmd<R, P, H> { //		implements IExtVisitorCmd<R, IBallHostID, P, H> {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 1334888873954045544L;

	@SuppressWarnings("unchecked")
	@Override
	final public <T extends IExtVisitorHost<IBallHostID, ? super H>> R apply(IBallHostID index, T host, P... params) {
		return apply(index, (H) host, params);
	}

	/**
	 * The method that performs the command's processing of the host
	 * @param index The host's identifying index that was used to invoke this command
	 * @param host The host that invoked the visitor with this command installed.
	 * @param params The input parameters to the execution of the visitor
	 * @return The return value of this command's processing of the host 
	 */
	abstract public R apply(IBallHostID index, H host, @SuppressWarnings("unchecked") P... params);

}
