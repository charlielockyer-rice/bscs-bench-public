package hw05.model.interactStrategies;

import java.awt.Point;

import hw05.model.IBall;
import hw05.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;

/**
 * A strategy in which interacted balls "freeze" in place
 * @author charlielockyer
 *
 */
public class FreezeStrategy implements IInteractStrategy{

	@Override
	public IBallCmd interactWith(IBall context, IBall target, IDispatcher<IBallCmd> disp) {
		return new IBallCmd() {
			@Override
			public void apply(IBall ball, IDispatcher<IBallCmd> disp) {
				ball.setVelocity(new Point(0, 0));			

			}
		};
	}

	@Override
	public void init(IBall context) {
		// TODO Auto-generated method stub
		
	}

}
