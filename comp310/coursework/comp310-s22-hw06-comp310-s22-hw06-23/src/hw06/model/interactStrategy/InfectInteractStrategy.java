package hw06.model.interactStrategy;

import java.awt.Color;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import hw06.model.updateStrategy.IUpdateStrategy;
import hw06.model.updateStrategy.StraightUpdateStrategy;
import provided.utils.dispatcher.IDispatcher;


/**
 * Interaction strategy for the context ball to "infect" other balls (once a given interaction criteria is met between the balls).
 * Infection means changing the other balls' update strategies to the current ball's update strategy.
 * @author Son Nguyen and Smit Viroja
 */
public class InfectInteractStrategy implements  IInteractStrategy{

	/**
	 * Changes the ball according to the Infect interaction strategy. 
	 * 
	 * @param context -> the ball undergoing the strategy
	 * @param other -> the ball interacting with the context ball
	 * @param disp -> the IDispatcher for the current ball.
	 * @return A command to be executed after both balls' interaction behaviors have completed.
	 */
	@Override
	public IBallCmd interactWithThen(IBall context, IBall other, IDispatcher<IBallCmd> disp) {
		return new IBallCmd() {
			@Override
			public void apply(IBall ball, IDispatcher<IBallCmd> disp) {
				if (ball.getCount() < 20 && !ball.equals(other)) {
					other.setInteractStrategy(new InfectInteractStrategy());
					other.setUpdateStrategy(context.getUpdateStrategy());
					other.setCount(0);
				}
			}
			
		};
	}

	
	/**
	 * Initialize the interaction strategy. This method must be run whenever the ball gets a new strategy.
	 * @param host -> the host ball. 
	 */
	@Override
	public void init(IBall host) {
		host.setColor(Color.BLACK);
		host.setCount(0);
		host.setUpdateStrategy(new IUpdateStrategy() {
			IUpdateStrategy oldstrat = host.getUpdateStrategy();
			@Override
			public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
				oldstrat.updateState(context, disp);
				if(context.getCount() >= 20) {
					context.setColor(Color.BLUE);
					context.setUpdateStrategy(new StraightUpdateStrategy());
					context.setInteractStrategy(IInteractStrategy.NULL_OBJECT);
				};
			}

			@Override
			public void init(IBall host) {				
			}
		});
	}	
	
	

}
	
