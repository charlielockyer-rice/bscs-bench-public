package hw02.ball;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import provided.utils.dispatcher.IDispatcher;
import provided.utils.displayModel.IDimension;
import provided.utils.valueGenerator.impl.Randomizer;

/**
 * @author rlmse
 * A ball which changes color when it hits a boundary, like the logo in the DVD idle screen
 */
public class DVDLogoBall extends ABall {

	/**
	 * @param color - color of the DVDLogoBall
	 * @param position - Position of the DVDLogoBall
	 * @param velocity - Velocity of the DVDLogoBall
	 * @param diameter - Diameter of the DVDLogoBall
	 * @param dimension - Dimension of the graphic container of the DVDLogoBall
	 */
	public DVDLogoBall(Color color, Point position, Point velocity, int diameter, IDimension dimension) {
		super(color, position, velocity, diameter, dimension);
	}

	@Override
	/**
	 * Changes the color of the ball if it bounces off a boundary
	 */
	public boolean bounce() {
		// TODO: Implement this method
		return false;
	}

	@Override
	public void updateState(IDispatcher<Graphics> disp) {
		// TODO: Implement this method
	}
}
