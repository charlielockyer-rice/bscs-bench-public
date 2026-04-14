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
	
		if (delay < count++) {
			dispatcher.updateAll(new IBallCmd() {

				@Override
				public void apply(IBall other, IDispatcher<IBallCmd> disp) {

					if (count != 0 && context != other) {
						if ((context.getRadius() + other.getRadius()) > 
						    context.getLocation().distance(other.getLocation())) {
							disp.addObserver(new Ball(
									new Point(context.getLocation()), 
									new Point(-context.getVelocity().x+1,-context.getVelocity().y+1), 
									context.getColor(),
									context.getRadius(),
									context.getDimension(), 
									context.getCanvas(),
									new IBallAlgo() {
										public void caseDefault(IBall host) {
											host.setUpdateStrategy(new SpawnStrategy());
											host.setPaintStrategy(context.getPaintStrategy());
										}
									}));
							count = 0;
							delay *= 5;
						}
					}
					
					
				}

			});
		}
	}

	@Override
	public void init(IBall context) {
		// TODO Auto-generated method stub
		
	}

}
