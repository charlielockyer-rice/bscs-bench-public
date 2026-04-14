package hw02.ball;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import provided.utils.dispatcher.IDispatcher;
import provided.utils.displayModel.IDimension;

/**
 * @author rlmse
 * Ball which is generated when the input ball type from the view is invalid. constructed so that
 * it doesn't appear in the GUI
 */
public class ErrorBall extends ABall {

	/**
	 * @param dimension - Dimension of the Graphics object containing the balls. Only used to
	 * conform to the ABall constructor, no practical purpose
	 */
	public ErrorBall(IDimension dimension) {
		super(Color.gray, new Point(0, 0), new Point(0, 0), 0, dimension);
	}

	@Override
	public void updateState(IDispatcher<Graphics> disp) {

	}

}
