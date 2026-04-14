package provided.utils.logic.impl;

import provided.utils.logic.ILogicalHost;
import provided.utils.logic.ILogicalVisitor;

/**
 * An ILogicalHost that represents the logical OR of two 
 * ILogicalHosts.   That is, the trueCase of the given visitor 
 * will be run if one of the trueCases of the given hosts runs.
 * Otherwise, the false case of the given visitor will run.
 * 
 * @author swong
 *
 */
public class LogicalOrHost implements ILogicalHost {

	/**
	 * The first logical host
	 */
	private ILogicalHost hostA;
	/**
	 * The second logical host
	 */
	private ILogicalHost hostB;

	/**
	 * Construct the logical OR of the two hosts.   Note that 
	 * hostA is evaluated first and if true, hostB is not evaluated.
	 * @param hostA  The first logical host
	 * @param hostB The second logical host
	 */
	public LogicalOrHost(ILogicalHost hostA, ILogicalHost hostB) {
		this.hostA = hostA;
		this.hostB = hostB;
	}

	@Override
	public <R, P> R execute(ILogicalVisitor<R, P> vis, P param) {
		// delegate to the first host
		return hostA.execute(new ILogicalVisitor<R, P>() {

			@Override
			public R trueCase(P param2) {
				// Short-circuit the calculation because the overall state is true
				return vis.trueCase(param2);
			}

			@Override
			public R falseCase(P param2) {
				// delegate to the other host
				return hostB.execute(new ILogicalVisitor<R, P>() {

					@Override
					public R trueCase(P param3) {
						// overall state is true
						return vis.trueCase(param3);
					}

					@Override
					public R falseCase(P param3) {
						// overall state is false
						return vis.falseCase(param3);
					}

				}, param2);

			}

		}, param);

	}

}
