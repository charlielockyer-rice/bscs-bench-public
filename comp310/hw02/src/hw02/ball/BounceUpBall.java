package hw02.ball;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import provided.utils.dispatcher.IDispatcher;
import provided.utils.displayModel.IDimension;

/**
 * @author rlmse
 * A ball that forces perspective, appearing as if it is bouncing out of the screen when moving
 * right and as if it is bouncing away from the screen when moving left.
 */
public class BounceUpBall extends ABall {

	/**
	 * @param color - color of the BounceUpBall
	 * @param position - Position of the BounceUpBall
	 * @param velocity - Velocity of the BounceUpBall
	 * @param diameter - Diameter of the BounceUpBall
	 * @param dimension - Dimension of the graphic container of the BounceUpBall
	 */
	public BounceUpBall(Color color, Point position, Point velocity, int diameter, IDimension dimension) {
		super(color, position, velocity, diameter, dimension);
	}

	@Override
	public void updateState(IDispatcher<Graphics> disp) {
		// TODO: Implement this method
	}
}
