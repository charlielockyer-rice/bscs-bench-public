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
		// TODO: Implement this method
	}

	@Override
	public void init(IBall context) {
		// TODO Auto-generated method stub
		
	}

}
