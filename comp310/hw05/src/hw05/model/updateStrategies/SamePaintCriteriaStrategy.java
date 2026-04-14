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
		// TODO: Implement this method
	}

	@Override
	public void init(IBall context) {
		// TODO Auto-generated method stub
		
	}

}
