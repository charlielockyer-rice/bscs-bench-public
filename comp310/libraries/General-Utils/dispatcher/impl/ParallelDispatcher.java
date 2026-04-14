package provided.utils.dispatcher.impl;

/**
 * A dispatcher that updates its observers in parallel if possible on multiple threads.
 * @author swong
 *
 * @param <TMsg>  The type of message being sent to the observers.
 */
public class ParallelDispatcher<TMsg> extends ADispatcher<TMsg> {
	/**
	 * Create an empty parallel dispatcher
	 */
	public ParallelDispatcher() {
		super();
	}

	@Override
	public void updateAll(TMsg msg) {
		this.getObserverSet().parallelStream().forEach((obs) -> obs.update(this, msg));
	}

}
