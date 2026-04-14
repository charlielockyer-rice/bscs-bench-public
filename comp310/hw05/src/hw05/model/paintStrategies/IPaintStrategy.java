package hw05.model.paintStrategies;

import java.awt.Graphics;

import hw05.model.IBall;

/**
 * Top level interface that defines what a painting strategy can do.
 * @author yihan
 *
 */
public interface IPaintStrategy {

	/**
	 * Paint the IBall.
	 * @param g graphics
	 * @param host the host ball
	 */
	void paint(Graphics g, IBall host);

	/**
	 * Initialize the strategy and the host ball.
	 * @param host the host ball
	 */
	void init(IBall host);

	/**
	 * No-opt for null strategy
	 */
	public static final IPaintStrategy NULL = new IPaintStrategy() {
		@Override
		public void paint(Graphics g, IBall host) {
		}

		@Override
		public void init(IBall host) {
		}

	};

	/**
	 * The error strategy
	 */
	public static final IPaintStrategy ERROR = new IPaintStrategy() {

		@Override
		public void paint(Graphics g, IBall host) {
		}

		@Override
		public void init(IBall host) {
		}

	};

}
