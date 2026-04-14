package hw05.model.updateStrategies;

import hw05.model.IBall;
import hw05.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;

/**
 * A criteria that allows balls that have the same radius to interact with one another
 * @author charlielockyer
 *
 */
public class SameRadiusCriteriaStrategy implements IUpdateStrategy{
	
	public void updateState(final IBall source, IDispatcher<IBallCmd> dispatcher) {
		dispatcher.updateAll(new IBallCmd() {
			
			public void apply(IBall target, IDispatcher<IBallCmd> disp) {
				if (target == source) {
					return;
				}
			
				if (source.getRadius() == target.getRadius()) {// whatever the interaction criteria is
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
	
	public void init(IBall context) {
	}

}
