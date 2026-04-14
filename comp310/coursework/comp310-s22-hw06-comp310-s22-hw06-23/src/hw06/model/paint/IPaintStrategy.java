package hw06.model.paint;

import java.awt.Graphics;

import hw06.model.BallHosts.IBall;

/**
 * Updates the strategy of a ball
 * @author ColeRubson, Son Nguyen
 */
public interface IPaintStrategy {

	/**
	 * Paint the ball to the screen.
	 * @param g The graphics to paint.
	 * @param host the host ball
	 */
	public void paint(Graphics g, IBall host);

	/**
	 * Initialize the paint strategy. This method must be run whenever the ball gets a new strategy.
	 * @param host The host ball
	 */
	public void init(IBall host);

	/**
	 * No-op singleton implementation of IUpdateStrategy
	 * See the web page on the Null Object Design Pattern at http://cnx.org/content/m17227/latest/
	 */
	public static final IPaintStrategy NULL_OBJECT = new IPaintStrategy() {
		@Override
		/**
		 * Paint the ball to the screen.
		 * @param g The graphics to paint.
		 * @param host the host ball
		 */
		public void paint(Graphics g, IBall host) {
		}

		@Override
		/**
		 * @param host -> the host ball.
		 * Initialize the update strategy. This method must be run whenever the ball gets a new strategy.
		 */
		public void init(IBall host) {
		}
	};

	/**
	 * An error strategy to beep.
	 */
	public static final IPaintStrategy ERROR = new IPaintStrategy() {
		@Override
		/**
		 * Paint the ball to the screen.
		 * @param g The graphics to paint.
		 * @param host the host ball
		 */
		public void paint(Graphics g, IBall host) {
		}

		@Override
		/**
		 * @param host -> the host ball.
		 * Initialize the update strategy. This method must be run whenever the ball gets a new strategy.
		 */
		public void init(IBall host) {
			java.awt.Toolkit.getDefaultToolkit().beep();
			System.err.println("IPaintStrategy.ERROR: beeping!");
		}
	};
}
