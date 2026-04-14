package provided.utils.logic.impl;

import provided.utils.logic.ILogicalHost;
import provided.utils.logic.ILogicalVisitor;

/**
 * An ILogicalHost that represents the logical NOT of the given 
 * ILogicalHost.   That is, the trueCase of the given visitor 
 * will be run only if the falseCase of the given hosts runs.  
 * The falseCase of the given visitor will be run only if the
 * trueCase of the given visitor runs.
 * 
 * @author swong
 *
 */
public class LogicalNotHost implements ILogicalHost {

	/**
	 * The given logical host
	 */
	private ILogicalHost host;

	/**
	 * Construct the logical NOT of the given hosts.   
	 * @param host  The given logical host
	 */
	public LogicalNotHost(ILogicalHost host) {
		this.host = host;
	}

	@Override
	public <R, P> R execute(ILogicalVisitor<R, P> vis, P param) {
		// delegate to the given host
		return host.execute(new ILogicalVisitor<R, P>() {

			@Override
			public R trueCase(P param2) {
				// overall state is false
				return vis.falseCase(param2);
			}

			@Override
			public R falseCase(P param2) {
				// overall state is true
				return vis.trueCase(param2);
			}

		}, param);

	}

}
