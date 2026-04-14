package hw05.model.updateStrategies;

import hw05.model.IBall;
import hw05.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;

/**
 * A criteria that allows balls that have the same speed (magnitude of velocity) to interact with one another
 * @author charlielockyer
 *
 */
public class SameSpeedCriteriaStrategy implements IUpdateStrategy{

	@Override
	public void updateState(final IBall source, IDispatcher<IBallCmd> dispatcher) {
		dispatcher.updateAll(new IBallCmd() {
			
			@Override
			public void apply(IBall target, IDispatcher<IBallCmd> disp) {
				if (target == source) {
					return;
				}
				int sourceSpeed = (int) Math.sqrt(Math.pow(source.getVelocity().x, 2) + Math.pow(source.getVelocity().y, 2));
				int targetSpeed = (int) Math.sqrt(Math.pow(target.getVelocity().x, 2) + Math.pow(target.getVelocity().y, 2));
			
				if (sourceSpeed == targetSpeed) {// whatever the interaction criteria is
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
