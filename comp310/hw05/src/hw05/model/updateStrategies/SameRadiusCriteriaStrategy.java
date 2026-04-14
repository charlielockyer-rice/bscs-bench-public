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
		// TODO: Implement this method
	}
	
	public void init(IBall context) {
	}

}
