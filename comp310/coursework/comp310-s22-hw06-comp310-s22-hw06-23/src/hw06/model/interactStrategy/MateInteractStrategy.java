package hw06.model.interactStrategy;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import hw06.model.IBallAlgo2ModelAdapter;
import hw06.model.IBallCmd;
import hw06.model.BallHosts.DefaultBall;
import hw06.model.BallHosts.IBall;
import hw06.model.BallVisitors.ABallAlgoCmd;
import hw06.model.BallVisitors.AConfigBallAlgo;
import hw06.model.updateStrategy.IUpdateStrategy;
import hw06.model.updateStrategy.MultiUpdateStrategy;
import provided.ballworld.extVisitors.IBallHostID;
import provided.logger.ILoggerControl;
import provided.utils.dispatcher.IDispatcher;

/**
 * Interaction strategy that, upon interaction, causes the creation of a new ball with a random velocity, which averages the color
 * of the two balls ("context" and "other"), averages the radii of the balls, and combines their Update strategies.
 * 
 * @author Son Nguyen and Smit Viroja
 */
public class MateInteractStrategy implements  IInteractStrategy {
	
	
	/**
	 * Changes the ball according to the Mate interaction strategy. 
	 * 
	 * @param context -> the ball undergoing the strategy
	 * @param other -> the ball interacting with the context ball
	 * @param disp -> the IDispatcher for the current ball.
	 * @return A command to be executed after both balls' interaction behaviors have completed.
	 */
	@Override
	public IBallCmd interactWithThen(IBall context, IBall other, IDispatcher<IBallCmd> disp) {
		
		IBallCmd cmd = (a, b) -> {};
		
		Point velocity = context.getRandGen().randomVel(new Rectangle(5, 5, 30, 30));
		
		int radius = (context.getRadius() + other.getRadius())/2;
		
		if (context.getCount() >600) {
			disp.removeObserver(context);
		}
		
		if (context.getCount()%100 >90 && velocity.distance(0, 0) > 5  && radius > 10) {
			cmd = new IBallCmd() {
				
				@Override
				public void apply(IBall context, IDispatcher<IBallCmd> disp) {
					Color color =  new Color((int)(context.getColor().getRed()+other.getColor().getRed())/2,
							(int)(context.getColor().getGreen()+other.getColor().getGreen())/2,
							(int)(context.getColor().getBlue()+other.getColor().getBlue())/2);
					
					IBall children = new DefaultBall(context.getLocation(), radius, 
							velocity,
							color, context.getContainer(),  //							(host) -> {
//								host.setInteractStrategy(new MultiInteractStrategy(context.getInteractStrategy(),
//										other.getInteractStrategy()));
//								host.setUpdateStrategy(new MultiUpdateStrategy(context.getUpdateStrategy(),
//																other.getUpdateStrategy()));
//								host.setPaintStrategy(context.getPaintStrategy());
//								
//							});
							new AConfigBallAlgo(ILoggerControl.getSharedLogger(), "MateStrategy", IBallAlgo2ModelAdapter.NULL, new ABallAlgoCmd<>() {
								/**
								 * Auto generated serial
								 */
								private static final long serialVersionUID = -7707661235581724657L;

								@Override
								public Void apply(IBallHostID index, IBall host, Void... params) {
									host.setInteractStrategy(new MultiInteractStrategy(context.getInteractStrategy(),
											other.getInteractStrategy()));
									host.setUpdateStrategy(new MultiUpdateStrategy(context.getUpdateStrategy(),
																	other.getUpdateStrategy()));
									host.setPaintStrategy(context.getPaintStrategy());
									return null;
								}
							}) {

								/**
								 * Auto generated serial
								 */
								private static final long serialVersionUID = 1L;
								
							});
					
					context.setCount(context.getCount() + 10);
					
					disp.addObserver(children);
				}
				
			};
		}
		
		return cmd;
	}	


	/**
	 * Initialize the interaction strategy. This method must be run whenever the ball gets a new strategy.
	 * @param host -> the host ball. 
	 */
	@Override
	public void init(IBall host) {
		host.setUpdateStrategy(new MultiUpdateStrategy(host.getUpdateStrategy(),
				new IUpdateStrategy() {
				
				@Override
				public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
					if(context.getCount() >= 600) {
						disp.removeObserver(context);
					}
				}

				@Override
				public void init(IBall host) {
					
				};

		}));
	}

}
	
