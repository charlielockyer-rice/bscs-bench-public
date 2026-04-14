package provided.ballworld.extVisitors.impl;

import provided.ballworld.extVisitors.IBallHost;
import provided.ballworld.extVisitors.IBallHostAlgo;
import provided.ballworld.extVisitors.IBallHostID;
import provided.extvisitor.AExtVisitor;

/**
 * A type-narrowed visitor for balls.  This class needs further type-narrowing to be used in student code!
 *
 * @param <R> The return type of this algorithm
 * @param <P> The input parameter type of this algorithm
 * @param <H> The specific ball sub-type being used as the visitor's host.
 * @author swong
 */
public class ABallHostAlgo<R, P, H extends IBallHost<? super H>> extends AExtVisitor<R, IBallHostID, P, H>
		implements IBallHostAlgo<R, P, H> {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = -4540689162414933970L;

	/**
	 * Constructor for the class.
	 * @param defaultCmd The default case command to use.
	 */
	public ABallHostAlgo(ABallHostAlgoCmd<R, P, H> defaultCmd) {
		super(defaultCmd);
	}

}
