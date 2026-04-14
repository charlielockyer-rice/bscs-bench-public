package hw06.model.interactStrategy;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * @author phuso
 *
 */
public abstract class AInteractStrategy implements IInteractStrategy{
	@Override
	public IBallCmd interactWithThen(IBall context, IBall other, IDispatcher<IBallCmd> disp) {
		interact(context, other, disp);
		return new IBallCmd() {
			@Override
			public void apply(IBall context, IDispatcher<IBallCmd> disp) {
			}
		};
	}
	
	/**
	 * Interaction action
	 * @param context the context ball
	 * @param target the target ball
	 * @param disp the dispatcher
	 */
	public abstract void interact(IBall context, IBall target, IDispatcher<IBallCmd> disp);
}
