package hw05.model.updateStrategies;

import java.awt.Point;

import hw05.model.Ball;
import hw05.model.IBall;
import hw05.model.IBallAlgo;
import hw05.model.IBallCmd;
import provided.utils.dispatcher.IDispatcher;

/**
 * The spawn strategy that spawns a new ball when the two balls collide
 * @author cindy
 *
 */
public class SpawnStrategy implements IUpdateStrategy {

	/**
	 * tick counter that counts out the delay before another ball can be spawned.
	 */
	private int count = 0; 
	/**
	 * tick delay which increases at each spawn to keep total spawn rate from exponentially exploding.
	 */
	private int delay = 100; 

	@Override
	public void updateState(final IBall context, IDispatcher<IBallCmd> dispatcher) {
		// TODO: Implement this method
	}

	@Override
	public void init(IBall context) {
		// TODO Auto-generated method stub
		
	}

}
