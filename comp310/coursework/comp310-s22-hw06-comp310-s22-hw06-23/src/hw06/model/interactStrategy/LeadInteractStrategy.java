package hw06.model.interactStrategy;

import java.awt.Point;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import hw06.model.updateStrategy.IUpdateStrategy;
import hw06.model.updateStrategy.MultiUpdateStrategy;
import provided.utils.dispatcher.IDispatcher;


/**
 * Interaction strategy for creating a leader ball that makes other balls follow it closely after a given interaction criteria is met.
 *
 * @author Son Nguyen and Smit Viroja
 */

public class LeadInteractStrategy implements  IInteractStrategy {
	
	/**
	 * Changes the ball according to the Lead interaction strategy. 
	 * 
	 * @param context -> the ball undergoing the strategy
	 * @param other -> the ball interacting with the context ball
	 * @param disp -> the IDispatcher for the current ball.
	 * @return A command to be executed after both balls' interaction behaviors have completed.
	 */
	@Override
	public IBallCmd interactWithThen(IBall context, IBall other, IDispatcher<IBallCmd> disp) {
		
		return new IBallCmd() {
			
			@Override
			public void apply(IBall context, IDispatcher<IBallCmd> disp) {
				other.setUpdateStrategy(new MultiUpdateStrategy( other.getUpdateStrategy(),
						new IUpdateStrategy() {
							int clock = other.getCount();
							
							@Override
							public void updateState(IBall ball, IDispatcher<IBallCmd> dispball) {
								
								int scale = 15;
								if(Math.random() >.1) scale = (int)(Math.random()*21)+10;
								if (ball.getCount() < clock +50 &&  ball.getLocation().distance(context.getLocation()) > (ball.getRadius() + context.getRadius())*scale/20) {
									Point distance = new Point(context.getLocation().x- ball.getLocation().x, context.getLocation().y- ball.getLocation().y);
									double normalized_x = distance.x / distance.distance(0, 0);
									double normalized_y = distance.y / distance.distance(0, 0);
									double speedBall = context.getVelocity().distance(0, 0);
									ball.setVelocity(new Point((int)(scale*speedBall*normalized_x/20),(int)(scale*speedBall*normalized_y/20)));
								}
							}

						@Override
						public void init(IBall host) {
							// TODO Auto-generated method stub
							
						}
					
				}));
			}
			
		};
	}	

	/**
	 * Initialize the interaction strategy. This method must be run whenever the ball gets a new strategy.
	 * @param host -> the host ball. 
	 */
	@Override
	public void init(IBall host) {
		
	}

}
	
