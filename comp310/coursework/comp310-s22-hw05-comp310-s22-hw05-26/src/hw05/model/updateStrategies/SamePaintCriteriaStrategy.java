package hw05.model.updateStrategies;

import hw05.model.IBall;
import hw05.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;

/**
 * A criteria that allows balls with the same paint strategy to interact
 * @author charlielockyer
 *
 */
public class SamePaintCriteriaStrategy implements IUpdateStrategy{
	
	
	public void updateState(final IBall source, IDispatcher<IBallCmd> dispatcher) {
		dispatcher.updateAll(new IBallCmd() {
			
			@Override
			public void apply(IBall target, IDispatcher<IBallCmd> disp) {
				if (target == source) {
					return;
				}
				
				/**
				 * NOTE: This does not work as intended.  We would need to add a getName() to every strategy and compare the names to do this
				 * since getPaintStrategy() always returns an IPaintStrategy and getClass() will always return true.  We are instead just using
				 * this to show off the cool behaviors of some of the strategies when you want all the balls to interact.
				 */
				if (source.getPaintStrategy().getClass().equals(target.getPaintStrategy().getClass())) {// whatever the interaction criteria is
				        // Save the commands that were generated.   Note that each ball is doing its own interaction behavior/calculations.
						IBallCmd contextPostInteractCmd = source.interactWithThen(target, disp);
						IBallCmd otherPostInteractCmd = target.interactWithThen(source, disp);
				        
						// Run the saved commands now that both balls' interaction behaviors are complete.
						source.update(disp, contextPostInteractCmd);
						target.update(disp, otherPostInteractCmd);
					}
			}
		}
	);

	}

	@Override
	public void init(IBall context) {
		// TODO Auto-generated method stub
		
	}

}
