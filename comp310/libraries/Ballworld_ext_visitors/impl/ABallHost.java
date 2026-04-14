package provided.ballworld.extVisitors.impl;

import provided.ballworld.extVisitors.IBallHost;
import provided.ballworld.extVisitors.IBallHostID;
import provided.extvisitor.AExtVisitorHost;

/**
 * Type-narrowed extended visitor host for balls. 
 * This class needs further type-narrowing to be used in student code!
 * @author swong
 *
 * @param <H> The specific ball sub-type in use
 */
public class ABallHost<H extends IBallHost<? extends H>> extends AExtVisitorHost<IBallHostID, H>
		implements IBallHost<H> {

	/**
	 * For serialization
	 */
	private static final long serialVersionUID = 2037052762990852134L;

	/**
	 * Type-narrowed constructor.
	 * @param id The extended visitor host ID being used 
	 */
	protected ABallHost(IBallHostID id) {
		super(id);
	}

}
