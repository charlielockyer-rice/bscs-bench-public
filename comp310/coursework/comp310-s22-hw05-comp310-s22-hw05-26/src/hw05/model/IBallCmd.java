package hw05.model;

import provided.utils.dispatcher.IDispatcher;

/**
 * @author charlielockyer
 * Command-based dispatching for the ball
 */
public interface IBallCmd {
	/**
	 * The method run by a Ball's update method each time the ball is
	 * updated by the Dispatcher.
	 * @param context The ball to be updated
	 * @param disp the dispatcher
	 */
	public abstract void apply(IBall context, IDispatcher<IBallCmd> disp);

}
