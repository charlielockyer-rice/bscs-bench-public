package hw05.model.updateStrategies;

import java.awt.Point;

import hw05.model.IBall;
import hw05.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;

/**
 * The Criteria strategy that is satisfied when the two balls collide
 * @author cindy
 *
 */
public class CollideCriteriaStrategy implements IUpdateStrategy{

	@Override
	public void updateState(final IBall source, IDispatcher<IBallCmd> dispatcher) {
		dispatcher.updateAll(new IBallCmd() {
			
			@Override
			public void apply(IBall target, IDispatcher<IBallCmd> disp) {
				if (target == source) {
					return;
				}
				int rSource = source.getRadius();
				int rTarget = target.getRadius();
				Point locSource = source.getLocation();
				Point locTarget = target.getLocation();
				double distance = locSource.distance(locTarget);
				double minDistance = rSource+rTarget;
			
				if (minDistance > distance) {// whatever the interaction criteria is
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
