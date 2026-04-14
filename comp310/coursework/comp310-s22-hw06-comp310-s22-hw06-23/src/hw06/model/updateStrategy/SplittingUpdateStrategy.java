package hw06.model.updateStrategy;

import java.awt.Point;

import hw06.model.IBallAlgo2ModelAdapter;
import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import hw06.model.BallVisitors.ABallAlgoCmd;
import hw06.model.BallVisitors.AConfigBallAlgo;
import provided.ballworld.extVisitors.IBallHostID;
import provided.logger.ILoggerControl;
import provided.utils.dispatcher.IDispatcher;
import provided.utils.dispatcher.IObserver;
import provided.utils.loader.IObjectLoader;
import provided.utils.loader.impl.ObjectLoader;

/**
 * Strategy for the balls to split in half periodically.
 * @author Megan Xiao, Son Nguyen
 */
public class SplittingUpdateStrategy implements IUpdateStrategy {
	@Override
	/**
	 * @param context -> the ball undergoing the strategy
	 * @param disp -> the IDispatcher for the current ball.
	 * Updates the ball according to the Splitting update strategy. 
	 */
	public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		if (context.getCount() % 60 == 0) {
			String classname = context.getClass().toString().substring(6);
			IObjectLoader<IObserver<IBallCmd>> maker = new ObjectLoader<IObserver<IBallCmd>>(null);
			IObserver<IBallCmd> child = maker.loadInstance(classname, context.getLocation(), context.getRadius() / 2,
					new Point(-context.getVelocity().x, -context.getVelocity().y), context.getRandGen().randomColor(),
					context.getContainer(), 
					new AConfigBallAlgo(ILoggerControl.getSharedLogger(), "Splitting Strategy", IBallAlgo2ModelAdapter.NULL, new ABallAlgoCmd<>() {
						/**
						 * Auto generated serial
						 */
						private static final long serialVersionUID = -7707661235581724657L;
						@Override
						public Void apply(IBallHostID index, IBall host, Void... params) {
							host.setPaintStrategy(context.getPaintStrategy());
							host.setUpdateStrategy(context.getUpdateStrategy());
							host.setInteractStrategy(context.getInteractStrategy());
							return null;
						}
					}) {
						/**
						 * Auto generated serial
						 */
						private static final long serialVersionUID = 1L;
						
					});
			context.setColor(context.getRandGen().randomColor());
			context.setRadius(context.getRadius() / 2);
			disp.addObserver(child);
		}
		if (context.getRadius() <= 5) {
			disp.removeObserver(context);
		}
	}

	@Override
	/**
	 * @param host -> the host ball.
	 * Initialize the update strategy. This method must be run whenever the ball gets a new strategy.
	 */
	public void init(IBall host) {
	}

}