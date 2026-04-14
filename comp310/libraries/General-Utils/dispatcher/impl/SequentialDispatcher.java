package provided.utils.dispatcher.impl;

/**
 * A simple dispatcher that updates its observers sequentially on a single thread.
 * @author swong
 *
 * @param <TMsg>  The type of message being sent to the observers.
 */
public class SequentialDispatcher<TMsg> extends ADispatcher<TMsg> {
	/**
	 * Create an empty sequential dispatcher
	 */
	public SequentialDispatcher() {
		super();
	}
	
	@Override
	public void updateAll(TMsg msg) {
		this.getObserverSet().forEach((obs) -> obs.update(this, msg));
	}

}
