package hw05.model.interactStrategies;

import hw05.model.IBall;
import hw05.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;

/**
 * If the criteria is met, the balls will be set to have the same inherent traits (radius and color)
 * Unlike the infect strategy, the ball that passes its traits is arbitrary, so you get to see an
 * evolution of sorts within the population, like if smaller balls or larger balls are more likely to
 * pass traits based on average number of interactions.
 * @author charlielockyer
 *
 */
public class CloneTraitsStrategy implements IInteractStrategy{

	public IBallCmd interactWith(IBall context, IBall target, IDispatcher<IBallCmd> disp) {
		
		return new IBallCmd() {
			public void apply(IBall ball, IDispatcher<IBallCmd> disp) {
				ball.setRadius(target.getRadius());
				ball.setColor(target.getColor());
				
			}
		};
	}

	@Override
	public void init(IBall context) {
		// TODO Auto-generated method stub
		
	}

}
