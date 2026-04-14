package hw06.model.updateStrategy;

import java.awt.Point;
import java.awt.Rectangle;

import hw06.model.IBallCmd;
import hw06.model.BallHosts.IBall;
import provided.utils.dispatcher.IDispatcher;

/**
 * Strategy for the balls to teleport to a new random location when it hits the wall.
 * @author Cole Rabson, Son Nguyen
 */
public class TeleportUpdateStrategy implements IUpdateStrategy {
	@Override
	/**
	 * @param context -> the ball undergoing the strategy
	 * @param disp -> the IDispatcher for the current ball.
	 * Updates the ball according to the Teleport update strategy. 
	 */
	public void updateState(IBall context, IDispatcher<IBallCmd> disp) {
		if (context.hitLeftWall() || context.hitTopWall() || context.hitRightWall() || context.hitBottomWall()) {
			//Randomize Velocity and Spawn at center of the canvas
			Point center = new Point(context.getContainer().getWidth() / 2, context.getContainer().getHeight() / 2);
			Point velocity = context.getRandGen().randomVel(new Rectangle(20, 20));
			context.setLocation(center);
			context.setVelocity(velocity);
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
