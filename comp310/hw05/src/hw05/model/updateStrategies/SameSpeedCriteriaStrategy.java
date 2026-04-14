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
		// TODO: Implement this method
	}

	@Override
	public void init(IBall context) {
		// TODO Auto-generated method stub
		
	}

}
