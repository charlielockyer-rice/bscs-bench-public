package hw02.ball;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import provided.utils.dispatcher.IDispatcher;
import provided.utils.displayModel.IDimension;
import provided.utils.valueGenerator.impl.Randomizer;
import provided.utils.valueGenerator.impl.SineMaker;

/**
 * @author rlmse
 * A ball whose velocity which oscillates according to randomly generated sine waves
 */
public class SineSpeedBall extends ABall {
	/**
	 * Sine wave used to change the x component of the ball's velocity
	 */
	private final SineMaker xVelChanger;

	/**
	 * Sine wave used to change the y component of the ball's velocity
	 */
	private final SineMaker yVelChanger;

	/**
	 * @param color - color of the SineSpeedBall
	 * @param position - Position of the SineSpeedBall
	 * @param velocity - Velocity of the SineSpeedBall
	 * @param diameter - Diameter of the SineSpeedBall
	 * @param dimension - Dimension of the graphic container of the SineSpeedBall
	 */
	public SineSpeedBall(Color color, Point position, Point velocity, int diameter, IDimension dimension) {
		super(color, position, velocity, diameter, dimension);
		Point velExtreme1 = Randomizer.Singleton.randomVel(new Rectangle(20, 20));
		Point velExtreme2 = Randomizer.Singleton.randomVel(new Rectangle(20, 20));
		xVelChanger = new SineMaker(Math.min(velExtreme1.x, velExtreme2.x), Math.max(velExtreme1.x, velExtreme2.x),
				0.25);
		yVelChanger = new SineMaker(Math.min(velExtreme1.y, velExtreme2.y), Math.max(velExtreme1.y, velExtreme2.y),
				0.25);
	}

	@Override
	public void updateState(IDispatcher<Graphics> disp) {
		this.velocity.setLocation(this.velocity.x + xVelChanger.getIntVal(), this.velocity.y + yVelChanger.getIntVal());

	}
}
