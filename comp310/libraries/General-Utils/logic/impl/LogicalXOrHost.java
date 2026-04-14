package provided.utils.logic.impl;

import provided.utils.logic.ILogicalHost;
import provided.utils.logic.ILogicalVisitor;

/**
 * An ILogicalHost that represents the logical XOR of two 
 * ILogicalHosts.   That is, the trueCase of the given visitor 
 * will be run only if the trueCase of one the given hosts runs
 * and the falseCase of the other host runs.
 * Otherwise, the false case of the given visitor will run.
 * 
 * @author swong
 *
 */
public class LogicalXOrHost implements ILogicalHost {

	/**
	 * The first logical host
	 */
	private ILogicalHost hostA;
	/**
	 * The second logical host
	 */
	private ILogicalHost hostB;

	/**
	 * Construct the logical XOR of the two hosts.   
	 * @param hostA  The first logical host
	 * @param hostB The second logical host
	 */
	public LogicalXOrHost(ILogicalHost hostA, ILogicalHost hostB) {
		this.hostA = hostA;
		this.hostB = hostB;
	}

	@Override
	public <R, P> R execute(ILogicalVisitor<R, P> vis, P param) {
		// delegate to the first host
		return hostA.execute(new ILogicalVisitor<R, P>() {

			@Override
			public R trueCase(P param2) {
				// delegate to the second host
				return hostB.execute(new ILogicalVisitor<R, P>() {

					@Override
					public R trueCase(P param3) {
						// overall state is false
						return vis.falseCase(param3);
					}

					@Override
					public R falseCase(P param3) {
						// overall state is true
						return vis.trueCase(param3);
					}

				}, param2);
			}

			@Override
			public R falseCase(P param2) {
				// delegate to the second host
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
